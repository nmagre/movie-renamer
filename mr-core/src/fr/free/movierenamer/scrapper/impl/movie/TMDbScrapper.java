/*
 * movie-renamer-core
 * Copyright (C) 2012 Nicolas Magré
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.free.movierenamer.scrapper.impl.movie;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.CastingInfo.PersonProperty;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.MovieInfo.MovieProperty;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.JSONUtils;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.NumberUtils;
import fr.free.movierenamer.utils.ScrapperUtils;
import fr.free.movierenamer.utils.ScrapperUtils.TmdbImageSize;
import fr.free.movierenamer.utils.URIRequest;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONObject;

/**
 * Class TMDbScrapper : search movie on TMDb
 *
 * @see http://help.themoviedb.org/kb/api/
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class TMDbScrapper extends MovieScrapper {

  private static final String host = "themoviedb.org";
  private static final String apiHost = "api." + host;
  private static final String name = "TheMovieDb";
  private static final String version = "3";
  private final String apikey;
  public static final String imageUrl = "http://cf2.imgobject.com/t/p/";

  public TMDbScrapper() {
    super(AvailableLanguages.values());
    String key = Settings.decodeApkKey(Settings.getApplicationProperty("themoviedb.apkapikey"));
    if (key == null || key.trim().length() == 0) {
      throw new NullPointerException("apikey must not be null");
    }
    this.apikey = key;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  protected String getHost() {
    return host;
  }

  @Override
  protected Locale getDefaultLanguage() {
    return Locale.ENGLISH;
  }

  @Override
  protected List<Movie> searchMedia(String query, Locale language) throws Exception {
    URL searchUrl = new URL("http", apiHost, "/" + version + "/search/movie"
            + "?api_key=" + apikey + "&language=" + language.getLanguage() + "&query="
            + URIRequest.encode(query));
    return searchMedia(searchUrl, language);
  }

  @Override
  protected List<Movie> searchMedia(URL searchUrl, Locale language) throws Exception {
    JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());
    Map<Integer, Movie> resultSet = new LinkedHashMap<Integer, Movie>();

    try {
      List<JSONObject> jsonObj = JSONUtils.selectList("results", json);

      for (JSONObject node : jsonObj) {
        int id = JSONUtils.selectInteger("id", node);
        String title = JSONUtils.selectString("title", node);
        String originalTitle = JSONUtils.selectString("original_title", node);
        String imageNode = JSONUtils.selectString("poster_path", node);
        URL thumb = null;
        try {
          if (imageNode != null) {
            thumb = new URL(imageUrl + TmdbImageSize.poster.getSmall() + imageNode);
          }
        } catch (Exception e) {
          Settings.LOGGER.log(Level.WARNING, "Invalid image: " + imageNode, e);
        }

        Integer year = null;
        String syear = JSONUtils.selectString("release_date", node);
        if (syear != null && !syear.equals("")) {
          if (syear.contains("-")) {
            syear = syear.substring(0, syear.indexOf("-"));
          }
          if (NumberUtils.isNumeric(syear)) {
            year = Integer.parseInt(syear);
          }
        }

        if (year == null) {
          year = -1;
        }

        if (!resultSet.containsKey(id)) {
          resultSet.put(id, new Movie(new IdInfo(id, ScrapperUtils.AvailableApiIds.TMDB), title, originalTitle, thumb, year));
        }
      }
    } catch (Exception ex) {
      if (!searchUrl.toString().startsWith("www." + host)) {// It's not a movie url therefore this is a bug
        throw ex;
      }
    }

    // movie page ?
    if (resultSet.isEmpty()) {
      try {
        int tmdbid = findTmdbId(searchUrl.toString());
        IdInfo id = new IdInfo(tmdbid, ScrapperUtils.AvailableApiIds.TMDB);
        MovieInfo info = fetchMediaInfo(new Movie(id, null, null, null, -1), language);
        URL thumb;
        try {
          thumb = new URL(info.getPosterPath().toURL().toExternalForm());
        } catch (Exception ex) {
          thumb = null;
        }
        Movie movie = new Movie(id, info.getTitle(), info.getOriginalTitle(),
                thumb, info.getYear());
        if (movie != null) {
          resultSet.put(tmdbid, movie);
        }
      } catch (Exception e) {
        // ignore, can't find movie
      }
    }

    return new ArrayList<Movie>(resultSet.values());
  }

  protected int findTmdbId(String source) {
    Matcher matcher = Pattern.compile("/movie/(\\d+)-").matcher(source);

    if (matcher.find()) {
      return Integer.parseInt(matcher.group(1));
    }

    throw new IllegalArgumentException(String.format("Cannot find tmdb id: %s", source));
  }

  @Override
  protected MovieInfo fetchMediaInfo(Movie movie, Locale language) throws Exception {
    URL searchUrl = new URL("http", apiHost, "/" + version + "/movie/" + movie.getId() + "?api_key=" + apikey + "&language=" + language.getLanguage() + "&append_to_response=releases,keywords");
    JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());

    Map<MovieProperty, String> fields = new EnumMap<MovieProperty, String>(MovieProperty.class);
    Map<MovieInfo.MovieMultipleProperty, List<?>> multipleFields = new EnumMap<MovieInfo.MovieMultipleProperty, List<?>>(MovieInfo.MovieMultipleProperty.class);
    fields.put(MovieProperty.title, JSONUtils.selectString("title", json));
    fields.put(MovieProperty.rating, JSONUtils.selectString("vote_average", json));
    fields.put(MovieProperty.votes, JSONUtils.selectString("vote_count", json));
    fields.put(MovieProperty.originalTitle, JSONUtils.selectString("original_title", json));
    fields.put(MovieProperty.releasedDate, JSONUtils.selectString("release_date", json));
    fields.put(MovieProperty.overview, JSONUtils.selectString("overview", json));
    fields.put(MovieProperty.runtime, JSONUtils.selectString("runtime", json));
    fields.put(MovieProperty.budget, JSONUtils.selectString("budget", json));
    fields.put(MovieProperty.tagline, JSONUtils.selectString("tagline", json));
    JSONObject collection = JSONUtils.selectObject("belongs_to_collection", json);
    fields.put(MovieProperty.collection, collection != null ? JSONUtils.selectString("name", collection) : "");

    List<IdInfo> ids = new ArrayList<IdInfo>();
    ids.add(new IdInfo(JSONUtils.selectInteger("id", json), ScrapperUtils.AvailableApiIds.TMDB));
    String imdbId = JSONUtils.selectString("imdb_id", json);
    if (imdbId != null) {
      ids.add(new IdInfo(Integer.parseInt(imdbId.substring(2)), ScrapperUtils.AvailableApiIds.IMDB));
    }

    for (JSONObject jsonObj : JSONUtils.selectList("countries", json)) {
      if (JSONUtils.selectString("iso_3166_1", jsonObj).equals("US")) {
        fields.put(MovieProperty.certificationCode, JSONUtils.selectString("certification", jsonObj));
        break;
      }
    }

    List<String> genres = new ArrayList<String>();
    for (JSONObject jsonObj : JSONUtils.selectList("genres", json)) {
      genres.add(JSONUtils.selectString("name", jsonObj));
    }

    List<Locale> countries = new ArrayList<Locale>();
    for (JSONObject jsonObj : JSONUtils.selectList("production_countries", json)) {
      countries.add(new Locale("", JSONUtils.selectString("iso_3166_1", jsonObj)));
    }

    List<String> studios = new ArrayList<String>();
    for (JSONObject jsonObj : JSONUtils.selectList("production_companies", json)) {
      studios.add(JSONUtils.selectString("name", jsonObj));
    }

    List<String> tags = new ArrayList<String>();
    JSONObject keywords = JSONUtils.selectObject("keywords", json);
    if (keywords != null) {
      for (JSONObject jsonObj : JSONUtils.selectList("keywords", keywords)) {
        tags.add(JSONUtils.selectString("name", jsonObj));
      }
    }

    multipleFields.put(MovieInfo.MovieMultipleProperty.ids, ids);
    multipleFields.put(MovieInfo.MovieMultipleProperty.studios, studios);
    multipleFields.put(MovieInfo.MovieMultipleProperty.tags, tags);
    multipleFields.put(MovieInfo.MovieMultipleProperty.countries, countries);
    multipleFields.put(MovieInfo.MovieMultipleProperty.genres, genres);

    MovieInfo movieInfo = new MovieInfo(fields, multipleFields);
    return movieInfo;
  }

  @Override
  protected List<CastingInfo> fetchCastingInfo(Movie movie, Locale language) throws Exception {
    URL searchUrl = new URL("http", apiHost, "/" + version + "/movie/" + movie.getId() + "/casts?api_key=" + apikey);
    JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());

    List<CastingInfo> casting = new ArrayList<CastingInfo>();

    for (String section : new String[]{
              "cast", "crew"
            }) {
      List<JSONObject> jsonObjs = JSONUtils.selectList(section, json);
      for (JSONObject jsonObj : jsonObjs) {
        Map<PersonProperty, String> personFields = new EnumMap<PersonProperty, String>(PersonProperty.class);
        personFields.put(PersonProperty.name, JSONUtils.selectString("name", jsonObj));
        personFields.put(PersonProperty.character, JSONUtils.selectString("character", jsonObj));
        String image = JSONUtils.selectString("profile_path", jsonObj);
        if (image != null && image.length() > 0) {
          personFields.put(PersonProperty.picturePath, imageUrl + TmdbImageSize.cast.getMedium() + image);
        }

        if (section.equals("crew")) {
          personFields.put(PersonProperty.job, JSONUtils.selectString("job", jsonObj));
        } else {
          personFields.put(PersonProperty.job, CastingInfo.ACTOR);
        }
        casting.add(new CastingInfo(personFields));
      }
    }

    return casting;
  }
}
