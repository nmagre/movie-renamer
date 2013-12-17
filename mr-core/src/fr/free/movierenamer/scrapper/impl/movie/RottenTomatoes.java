/*
 * movie-renamer-core
 * Copyright (C) 2012-2013 Nicolas Magré
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
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.JSONUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.ScrapperUtils;
import fr.free.movierenamer.utils.StringUtils;
import fr.free.movierenamer.utils.URIRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONObject;

/**
 * Class RottenTomatoes, search movie on RottenTomatoes
 *
 * @author Nicolas Magré
 */
public class RottenTomatoes extends MovieScrapper {

  private static final String host = "rottentomatoes.com";
  private static final String apiHost = "api." + host;
  private static final String name = "RottenTomatoes";
  private static final String version = "1.0";
  private static final Pattern origTitle = Pattern.compile("[(]([^)]*)[)]$");
  private static String apikey;

  public RottenTomatoes() {
    super(LocaleUtils.AvailableLanguages.en);
    String key = Settings.decodeApkKey(Settings.getApplicationProperty("rottentomatoes.apkapikey"));
    if (key == null || key.trim().length() == 0) {
      throw new NullPointerException("apikey must not be null");
    }
    apikey = key;
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
    URL searchUrl = new URL("http", apiHost, "/api/public/v" + version + "/movies.json"
            + "?apikey=" + apikey + "&q=" + URIRequest.encode(query));
    return searchMedia(searchUrl, language);
  }

  @Override
  protected List<Movie> searchMedia(URL searchUrl, Locale language) throws Exception {
    JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());
    Map<Integer, Movie> resultSet = new LinkedHashMap<Integer, Movie>();
    List<JSONObject> jsonObj = JSONUtils.selectList("movies", json);
    for (JSONObject node : jsonObj) {
      if (node == null) {
        continue;
      }

      int id = JSONUtils.selectInteger("id", node);
      String title = JSONUtils.selectString("title", node);
      String originaleTitle = null;
      if (title.contains("(")) {
        Matcher m = origTitle.matcher(title);
        if (m.find()) {
          title = m.group(1);
          originaleTitle = StringUtils.removeBrackets(title);
        }
      }
      JSONObject posterNode = JSONUtils.selectObject("posters", json);

      String imageNode = null;
      if (posterNode != null) {
        imageNode = JSONUtils.selectString("profile", posterNode);
      }

      URL thumb = null;
      try {
        if (imageNode != null) {
          thumb = new URL(imageNode);
        }
      } catch (Exception e) {
        Settings.LOGGER.log(Level.WARNING, "Invalid image: " + imageNode, e);
      }

      Integer year = -1;
      String syear = JSONUtils.selectString("year", node);
      if (syear != null && !syear.equals("")) {
        year = Integer.parseInt(syear);
      }

      if (!resultSet.containsKey(id)) {
        resultSet.put(id, new Movie(null, new IdInfo(id, ScrapperUtils.AvailableApiIds.ROTTEN), title, originaleTitle, thumb, year));
      }
    }

    return new ArrayList<Movie>(resultSet.values());
  }

  private String rottenTomatoesIdLookUp(String imdbId) throws Exception {
    URL searchUrl = new URL("http", apiHost, "/api/public/v" + version + "/movie_alias.json?apikey=" + apikey + "&type=imdb&id=" + imdbId);
    JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());
    return JSONUtils.selectString("id", json);
  }

  public static IdInfo imdbIdLookup(IdInfo rottenTomatoesId) {
    if (rottenTomatoesId.getIdType() != ScrapperUtils.AvailableApiIds.ROTTEN) {
      return null;
    }

    try {
      URL searchUrl = new URL("http", apiHost, "/api/public/v" + version + "/movies/" + rottenTomatoesId.toString() + ".json?apikey=" + apikey);
      JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());
      JSONObject jobject = JSONUtils.selectObject("alternate_ids", json);
      if (jobject != null) {
        Integer imdbId = JSONUtils.selectInteger("imdb", jobject);
        if (imdbId != null) {
          return new IdInfo(imdbId, ScrapperUtils.AvailableApiIds.IMDB);
        }
      }
    } catch (MalformedURLException ex) {
      Logger.getLogger(RottenTomatoes.class.getName()).log(Level.SEVERE, null, ex);
    } catch (URISyntaxException ex) {
      Logger.getLogger(RottenTomatoes.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(RottenTomatoes.class.getName()).log(Level.SEVERE, null, ex);
    }

    return null;
  }

  @Override
  protected MovieInfo fetchMediaInfo(Movie movie, Locale language) throws Exception {
    String id = movie.getMediaId().toString();
    if (movie.getMediaId().getIdType() == ScrapperUtils.AvailableApiIds.IMDB) {
      id = rottenTomatoesIdLookUp(id);
    }

    URL searchUrl = new URL("http", apiHost, "/api/public/v" + version + "/movies/" + id + ".json?apikey=" + apikey);
    JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());

    Map<MovieInfo.MovieProperty, String> fields = new EnumMap<MovieInfo.MovieProperty, String>(MovieInfo.MovieProperty.class);
    Map<MovieInfo.MovieMultipleProperty, List<String>> multipleFields = new EnumMap<MovieInfo.MovieMultipleProperty, List<String>>(MovieInfo.MovieMultipleProperty.class);

    String title = JSONUtils.selectString("title", json);
    String originaleTitle = null;
    if (title.contains("(")) {
      Matcher m = origTitle.matcher(title);
      if (m.find()) {
        title = m.group(1);
        originaleTitle = StringUtils.removeBrackets(title);
      }
    }

    fields.put(MovieInfo.MovieProperty.title, title);
    if (originaleTitle != null) {
      fields.put(MovieInfo.MovieProperty.originalTitle, originaleTitle);
    }

    fields.put(MovieInfo.MovieProperty.overview, JSONUtils.selectString("synopsis", json));
    fields.put(MovieInfo.MovieProperty.runtime, JSONUtils.selectString("runtime", json));
    fields.put(MovieInfo.MovieProperty.tagline, JSONUtils.selectString("critics_consensus", json));

    JSONObject jobject = JSONUtils.selectObject("release_dates", json);
    if (jobject != null) {
      String releaseDate = JSONUtils.selectString("theater", jobject);
      if (releaseDate == null) {
        releaseDate = JSONUtils.selectString("dvd", jobject);
      }
      fields.put(MovieInfo.MovieProperty.releasedDate, releaseDate != null ? releaseDate : "");
    }

    jobject = JSONUtils.selectObject("ratings", json);
    fields.put(MovieInfo.MovieProperty.rating, jobject != null ? "" + (JSONUtils.selectInteger("audience_score", jobject) / 20) : "");

    String mpaa = JSONUtils.selectString("mpaa_rating", json);
    if (mpaa != null) {
      if (mpaa.equalsIgnoreCase("UNRATED")) {
        mpaa = "NC-17";
      }
      fields.put(MovieInfo.MovieProperty.certificationCode, mpaa);
    }

    List<IdInfo> ids = new ArrayList<IdInfo>();
    ids.add(new IdInfo(JSONUtils.selectInteger("id", json), ScrapperUtils.AvailableApiIds.ROTTEN));
    jobject = JSONUtils.selectObject("alternate_ids", json);
    if (jobject != null) {
      Integer imdbId = JSONUtils.selectInteger("imdb", jobject);
      if (imdbId != null) {
        ids.add(new IdInfo(imdbId, ScrapperUtils.AvailableApiIds.IMDB));
      }
    }

    List<String> genres = new ArrayList<String>();
    String sgenres = JSONUtils.selectString("genres", json);
    if (sgenres != null) {
      for (String genre : sgenres.split(",")) {
        genres.add(genre.replace("\"", ""));
      }
    }

    List<String> studios = new ArrayList<String>();
    studios.add(JSONUtils.selectString("studio", json));

    multipleFields.put(MovieInfo.MovieMultipleProperty.studios, studios);
    multipleFields.put(MovieInfo.MovieMultipleProperty.genres, genres);

    MovieInfo movieInfo = new MovieInfo(ids, fields, multipleFields);
    return movieInfo;
  }

  @Override
  protected List<CastingInfo> fetchCastingInfo(Movie movie, Locale language) throws Exception {

    URL searchUrl = new URL("http", apiHost, "/api/public/v" + version + "/movies/" + movie.getMediaId() + "/cast.json?apikey=" + apikey);
    JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());

    List<CastingInfo> casting = new ArrayList<CastingInfo>();

    String section = "cast";

    List<JSONObject> jsonObjs = JSONUtils.selectList(section, json);
    for (JSONObject jsonObj : jsonObjs) {
      Map<CastingInfo.PersonProperty, String> personFields = new EnumMap<CastingInfo.PersonProperty, String>(CastingInfo.PersonProperty.class);
      personFields.put(CastingInfo.PersonProperty.name, JSONUtils.selectString("name", jsonObj));
      personFields.put(CastingInfo.PersonProperty.character, JSONUtils.selectString("character", jsonObj));
      personFields.put(CastingInfo.PersonProperty.job, CastingInfo.ACTOR);

      casting.add(new CastingInfo(personFields));
    }

    searchUrl = new URL("http", apiHost, "/api/public/v" + version + "/movies/" + movie.getMediaId() + ".json?apikey=" + apikey);
    json = URIRequest.getJsonDocument(searchUrl.toURI());

    for (JSONObject jsonObj : JSONUtils.selectList("abridged_director", json)) {
      Map<CastingInfo.PersonProperty, String> personFields = new EnumMap<CastingInfo.PersonProperty, String>(CastingInfo.PersonProperty.class);
      personFields.put(CastingInfo.PersonProperty.name, JSONUtils.selectString("name", jsonObj));
      personFields.put(CastingInfo.PersonProperty.job, CastingInfo.DIRECTOR);

      casting.add(new CastingInfo(personFields));
    }

    return casting;
  }

  @Override
  public ScrapperUtils.InfoQuality getInfoQuality() {
    return ScrapperUtils.InfoQuality.AVERAGE;
  }

}
