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
package fr.free.movierenamer.scraper.impl.movie;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.info.MediaInfo.MediaProperty;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.MovieInfo.MovieMultipleProperty;
import fr.free.movierenamer.info.VideoInfo;
import fr.free.movierenamer.scraper.MovieScraper;
import fr.free.movierenamer.scraper.SearchParam;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.JSONUtils;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.ScraperUtils;
import fr.free.movierenamer.utils.ScraperUtils.AvailableApiIds;
import fr.free.movierenamer.utils.StringUtils;
import fr.free.movierenamer.utils.URIRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class RottenTomatoes, search movie on RottenTomatoes
 *
 * @author Nicolas Magré
 */
public class RottenTomatoesScraper extends MovieScraper {

  private static final String host = "rottentomatoes.com";
  private static final String apiHost = "api." + host;
  private static final String name = "RottenTomatoes";
  private static final String version = "1.0";
  private static final Pattern origTitle = Pattern.compile("[(]([^)]*)[)]$");
  private static final AvailableApiIds supportedId = AvailableApiIds.ROTTENTOMATOES;
  private static String apikey;

  public RottenTomatoesScraper() {
    super(AvailableLanguages.en);
    String key = Settings.decodeApkKey(Settings.getApplicationProperty("rottentomatoes.apkapikey"));
    if (key == null || key.trim().length() == 0) {
      throw new NullPointerException("apikey must not be null");
    }
    apikey = key;
  }

  @Override
  public AvailableApiIds getSupportedId() {
    return supportedId;
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
  protected AvailableLanguages getDefaultLanguage() {
    return AvailableLanguages.en;
  }

  @Override
  public IdInfo getIdfromURL(URL url) {// Id is not in URL for Rotten
    return null;
  }

  @Override
  public URL getURL(IdInfo id) {// Id is not in URL for Rotten
    return null;
  }

  @Override
  protected List<Movie> searchMedia(String query, SearchParam sep, AvailableLanguages language) throws Exception {
    URL searchUrl = new URL("http", apiHost, "/api/public/v" + version + "/movies.json"
      + "?apikey=" + apikey + "&q=" + URIRequest.encode(query));
    return searchMedia(searchUrl, sep, language);
  }

  @Override
  protected List<Movie> searchMedia(URL searchUrl, SearchParam sep, AvailableLanguages language) throws Exception {
    JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());
    Map<Integer, Movie> resultSet = new LinkedHashMap<>();
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
        resultSet.put(id, new Movie(null, new IdInfo(id, AvailableApiIds.ROTTENTOMATOES), title, originaleTitle, thumb, year));
      }
    }

    return new ArrayList<>(resultSet.values());
  }

  public static IdInfo rottenTomatoesIdLookUp(IdInfo imdbId) {
    if (imdbId.getIdType() != AvailableApiIds.IMDB) {
      return null;
    }

    try {
      URL searchUrl = new URL("http", apiHost, "/api/public/v" + version + "/movie_alias.json?apikey=" + apikey + "&type=imdb&id=" + imdbId);
      JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());
      String id = JSONUtils.selectString("id", json);
      if (id != null && !id.isEmpty()) {
        return new IdInfo(Integer.parseInt(id), ScraperUtils.AvailableApiIds.ROTTENTOMATOES);
      }
    } catch (Exception ex) {
      // No id found
    }

    return null;
  }

  public static IdInfo imdbIdLookup(IdInfo rottenTomatoesId) {
    if (rottenTomatoesId.getIdType() != ScraperUtils.AvailableApiIds.ROTTENTOMATOES) {
      return null;
    }

    try {
      URL searchUrl = new URL("http", apiHost, "/api/public/v" + version + "/movies/" + rottenTomatoesId.toString() + ".json?apikey=" + apikey);
      JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());
      JSONObject jobject = JSONUtils.selectObject("alternate_ids", json);
      if (jobject != null) {
        Integer imdbId = JSONUtils.selectInteger("imdb", jobject);
        if (imdbId != null) {
          return new IdInfo(imdbId, ScraperUtils.AvailableApiIds.IMDB);
        }
      }
    } catch (MalformedURLException ex) {
      Logger.getLogger(RottenTomatoesScraper.class.getName()).log(Level.SEVERE, null, ex);
    } catch (URISyntaxException | JSONException | IOException ex) {
      Logger.getLogger(RottenTomatoesScraper.class.getName()).log(Level.SEVERE, null, ex);
    }

    return null;
  }

  @Override
  protected MovieInfo fetchMediaInfo(Movie movie, IdInfo id, AvailableLanguages language) throws Exception {

    URL searchUrl = new URL("http", apiHost, "/api/public/v" + version + "/movies/" + id + ".json?apikey=" + apikey);
    JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());

    Map<MediaInfo.MediaInfoProperty, String> info = new HashMap<>();
    Map<MovieMultipleProperty, List<String>> multipleInfo = new EnumMap<>(MovieMultipleProperty.class);

    String title = JSONUtils.selectString("title", json);
    String originaleTitle = null;
    if (title.contains("(")) {
      Matcher m = origTitle.matcher(title);
      if (m.find()) {
        title = m.group(1);
        originaleTitle = StringUtils.removeBrackets(title);
      }
    }
    info.put(MediaInfo.MediaProperty.title, title);

    if (originaleTitle != null) {
      info.put(MediaProperty.originalTitle, originaleTitle);
    }

    info.put(MovieInfo.MovieProperty.overview, JSONUtils.selectString("synopsis", json));
    info.put(VideoInfo.VideoProperty.runtime, JSONUtils.selectString("runtime", json));
    info.put(MovieInfo.MovieProperty.tagline, JSONUtils.selectString("critics_consensus", json));

    JSONObject jobject = JSONUtils.selectObject("release_dates", json);
    if (jobject != null) {
      String releaseDate = JSONUtils.selectString("theater", jobject);
      releaseDate = releaseDate == null ? JSONUtils.selectString("dvd", jobject) : releaseDate;

      if (releaseDate != null) {
        info.put(VideoInfo.VideoProperty.releasedDate, releaseDate);
        Pattern pattern = Pattern.compile("(\\d{4})-\\d{2}-\\d{2}");
        Matcher matcher = pattern.matcher(releaseDate);
        if (matcher.find()) {
          info.put(MediaInfo.MediaProperty.year, matcher.group(1));
        }
        info.put(VideoInfo.VideoProperty.releasedDate, releaseDate);
      }

    }

    jobject = JSONUtils.selectObject("ratings", json);
    if (jobject != null) {
      Double rating = JSONUtils.selectInteger("audience_score", jobject).doubleValue() / 10;
      info.put(MediaInfo.MediaProperty.rating, "" + rating);
    }

    String mpaa = JSONUtils.selectString("mpaa_rating", json);
    if (mpaa != null) {
      if (mpaa.equalsIgnoreCase("UNRATED")) {
        mpaa = "NC-17";
      }
      info.put(MovieInfo.MovieProperty.certificationCode, mpaa);
    }

    List<IdInfo> ids = new ArrayList<>();
    ids.add(new IdInfo(JSONUtils.selectInteger("id", json), ScraperUtils.AvailableApiIds.ROTTENTOMATOES));
    jobject = JSONUtils.selectObject("alternate_ids", json);
    if (jobject != null) {
      Integer imdbId = JSONUtils.selectInteger("imdb", jobject);
      if (imdbId != null) {
        ids.add(new IdInfo(imdbId, ScraperUtils.AvailableApiIds.IMDB));
      }
    }

    List<String> genres = new ArrayList<>();
    String sgenres = JSONUtils.selectString("genres", json);
    if (sgenres != null) {
      for (String genre : sgenres.split(",")) {
        genres.add(genre.replace("\"", ""));
      }
    }

    List<String> studios = new ArrayList<>();
    studios.add(JSONUtils.selectString("studio", json));

    multipleInfo.put(MovieMultipleProperty.studios, studios);
    multipleInfo.put(MovieMultipleProperty.genres, genres);
    //FIXME country ??????

    return new MovieInfo(info, multipleInfo, ids);
  }

  @Override
  protected List<CastingInfo> fetchCastingInfo(Movie movie, IdInfo id, AvailableLanguages language) throws Exception {

    URL searchUrl = new URL("http", apiHost, "/api/public/v" + version + "/movies/" + id + "/cast.json?apikey=" + apikey);
    JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());

    List<CastingInfo> casting = new ArrayList<>();

    String section = "cast";

    List<JSONObject> jsonObjs = JSONUtils.selectList(section, json);
    for (JSONObject jsonObj : jsonObjs) {
      Map<CastingInfo.PersonProperty, String> personFields = new EnumMap<>(CastingInfo.PersonProperty.class);
      personFields.put(CastingInfo.PersonProperty.name, JSONUtils.selectString("name", jsonObj));
      personFields.put(CastingInfo.PersonProperty.character, JSONUtils.selectString("character", jsonObj));
      personFields.put(CastingInfo.PersonProperty.job, CastingInfo.ACTOR);

      casting.add(new CastingInfo(personFields, null));
    }

    searchUrl = new URL("http", apiHost, "/api/public/v" + version + "/movies/" + movie.getMediaId() + ".json?apikey=" + apikey);
    json = URIRequest.getJsonDocument(searchUrl.toURI());

    for (JSONObject jsonObj : JSONUtils.selectList("abridged_director", json)) {
      Map<CastingInfo.PersonProperty, String> personFields = new EnumMap<>(CastingInfo.PersonProperty.class);
      personFields.put(CastingInfo.PersonProperty.name, JSONUtils.selectString("name", jsonObj));
      personFields.put(CastingInfo.PersonProperty.job, CastingInfo.DIRECTOR);

      casting.add(new CastingInfo(personFields, null));
    }

    return casting;
  }

  @Override
  public InfoQuality getQuality() {
    return InfoQuality.AVERAGE;
  }

}
