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
package fr.free.movierenamer.scraper.impl.movie;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.CastingInfo.PersonProperty;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.info.MediaInfo.MediaProperty;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.MovieInfo.MovieMultipleProperty;
import fr.free.movierenamer.info.MovieInfo.MovieProperty;
import fr.free.movierenamer.scraper.MovieScraper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.CacheObject;
import fr.free.movierenamer.utils.JSONUtils;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.NumberUtils;
import fr.free.movierenamer.utils.ScraperUtils;
import fr.free.movierenamer.utils.ScraperUtils.AvailableApiIds;
import fr.free.movierenamer.utils.ScraperUtils.TmdbImageSize;
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
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONObject;

/**
 * Class TMDbScraper : search movie on TMDb
 *
 * @see http://help.themoviedb.org/kb/api/
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class TMDbScraper extends MovieScraper {

  private static final String host = "themoviedb.org";
  private static final String apiHost = "api." + host;
  private static final String name = "TheMovieDb";
  private static final String version = "3";
  private static final String cacheImageBaseUrl = "tmdbimagebaseurl";
  private static String apikey;
  public static final String imageUrl = "http://image.tmdb.org/t/p/";
  private static final AvailableApiIds supportedId = AvailableApiIds.THEMOVIEDB;

  public TMDbScraper() {
    super(AvailableLanguages.values());
    String key = Settings.decodeApkKey(Settings.getApplicationProperty("themoviedb.apkapikey"));
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
  public IdInfo getIdfromURL(URL url) {
    try {
      return new IdInfo(findTmdbId(url.toExternalForm()), supportedId);
    } catch (Exception ex) {
    }

    return null;
  }

  @Override
  public URL getURL(IdInfo id) {
    try {
      return new URL("http", apiHost, "/" + version + "/movie/" + id + "?api_key=" + apikey);
    } catch (MalformedURLException ex) {
    }

    return null;
  }

  public String getTmdbImageBaseUrl() {

    CacheObject cache = new CacheObject(host, Cache.getCache("medium"));
    String basurl = cache.getData(cacheImageBaseUrl, Locale.ROOT, String.class);
    if (basurl != null) {
      return basurl;
    }

    basurl = imageUrl;

    try {
      URL confUrl = new URL("http", apiHost, "/" + version + "/configuration?api_key=" + apikey);
      JSONObject json = URIRequest.getJsonDocument(confUrl.toURI());
      String burl = JSONUtils.selectString("base_url", JSONUtils.selectObject("images", json));
      if (burl != null) {
        basurl = burl;
        cache.putData(cacheImageBaseUrl, Locale.ROOT, basurl);
      }
    } catch (URISyntaxException ex) {
    } catch (IOException ex) {
    }

    return basurl;
  }

  @Override
  protected List<Movie> searchMedia(String query, AvailableLanguages language) throws Exception {
    URL searchUrl = new URL("http", apiHost, "/" + version + "/search/movie"
            + "?api_key=" + apikey + "&language=" + language.name() + "&query="
            + URIRequest.encode(query));
    return searchMedia(searchUrl, language);
  }

  @Override
  protected List<Movie> searchMedia(URL searchUrl, AvailableLanguages language) throws Exception {
    JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());
    Map<Integer, Movie> resultSet = new LinkedHashMap<Integer, Movie>();

    try {
      List<JSONObject> jsonObj = JSONUtils.selectList("results", json);

      for (JSONObject node : jsonObj) {
        if (node == null) {
          continue;
        }

        int id = JSONUtils.selectInteger("id", node);
        String title = JSONUtils.selectString("title", node);
        String originalTitle = JSONUtils.selectString("original_title", node);
        String imageNode = JSONUtils.selectString("poster_path", node);
        URL thumb = null;
        try {
          if (imageNode != null) {
            thumb = new URL(getTmdbImageBaseUrl() + TmdbImageSize.poster.getSmall() + imageNode);
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
          resultSet.put(id, new Movie(null, new IdInfo(id, ScraperUtils.AvailableApiIds.THEMOVIEDB), title, originalTitle, thumb, year));
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
        IdInfo id = new IdInfo(tmdbid, ScraperUtils.AvailableApiIds.THEMOVIEDB);
        MovieInfo info = fetchMediaInfo(new Movie(null, id, null, null, null, -1), id, language);
        URL thumb;
        try {
          thumb = new URL(info.getPosterPath().toURL().toExternalForm());
        } catch (MalformedURLException ex) {
          thumb = null;
        }

        Movie movie = new Movie(null, id, info.getTitle(), info.getOriginalTitle(),
                thumb, info.getYear());

        resultSet.put(tmdbid, movie);

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

  public static IdInfo tmdbIDLookUp(IdInfo imdbId) {

    if (imdbId.getIdType() != ScraperUtils.AvailableApiIds.IMDB) {
      return null;
    }

    try {
      URL searchUrl = new URL("http", apiHost, "/" + version + "/movie/" + imdbId + "?api_key=" + apikey);
      JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());
      String id = JSONUtils.selectString("id", json);
      if (id != null && !id.isEmpty()) {
        return new IdInfo(Integer.parseInt(id), ScraperUtils.AvailableApiIds.THEMOVIEDB);
      }
    } catch (Exception ex) {
      // No id found
    }

    return null;
  }

  public static IdInfo imdbIdLookup(IdInfo tmdbId) {

    if (tmdbId.getIdType() != ScraperUtils.AvailableApiIds.THEMOVIEDB) {
      return null;
    }

    try {
      URL searchUrl = new URL("http", apiHost, "/" + version + "/movie/" + tmdbId + "?api_key=" + apikey);
      JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());
      String simdbId = JSONUtils.selectString("imdb_id", json);
      if (simdbId != null) {
        return new IdInfo(Integer.parseInt(simdbId.substring(2)), ScraperUtils.AvailableApiIds.IMDB);
      }
    } catch (MalformedURLException ex) {
      Logger.getLogger(TMDbScraper.class.getName()).log(Level.SEVERE, null, ex);
    } catch (URISyntaxException ex) {
      Logger.getLogger(TMDbScraper.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(TMDbScraper.class.getName()).log(Level.SEVERE, null, ex);
    }

    return null;
  }

  @Override
  protected MovieInfo fetchMediaInfo(Movie movie, IdInfo id, AvailableLanguages language) throws Exception {

    URL searchUrl = new URL("http", apiHost, "/" + version + "/movie/" + id + "?api_key=" + apikey + "&language=" + language.name() + "&append_to_response=releases,keywords");
    JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());

    final Map<MediaInfo.MediaProperty, String> mediaFields = new EnumMap<MediaInfo.MediaProperty, String>(MediaInfo.MediaProperty.class);
    Map<MovieProperty, String> fields = new EnumMap<MovieProperty, String>(MovieProperty.class);
    Map<MovieMultipleProperty, List<String>> multipleFields = new EnumMap<MovieMultipleProperty, List<String>>(MovieMultipleProperty.class);

    mediaFields.put(MediaInfo.MediaProperty.title, JSONUtils.selectString("title", json));

    String rating = JSONUtils.selectString("vote_average", json);
    if (rating != null && !rating.isEmpty()) {
      mediaFields.put(MediaInfo.MediaProperty.rating, rating);
    }
    fields.put(MovieProperty.votes, JSONUtils.selectString("vote_count", json));
    mediaFields.put(MediaProperty.originalTitle, JSONUtils.selectString("original_title", json));

    String syear = JSONUtils.selectString("release_date", json);
    if (syear != null && !syear.isEmpty()) {
      Pattern pattern = Pattern.compile("(\\d{4})-\\d{2}-\\d{2}");
      Matcher matcher = pattern.matcher(syear);
      if (matcher.find()) {
        mediaFields.put(MediaInfo.MediaProperty.year, matcher.group(1));
      }
      fields.put(MovieProperty.releasedDate, syear);
    }

    fields.put(MovieProperty.overview, JSONUtils.selectString("overview", json));
    fields.put(MovieProperty.runtime, JSONUtils.selectString("runtime", json));
    fields.put(MovieProperty.budget, JSONUtils.selectString("budget", json));
    fields.put(MovieProperty.tagline, JSONUtils.selectString("tagline", json));
    JSONObject collection = JSONUtils.selectObject("belongs_to_collection", json);
    fields.put(MovieProperty.collection, collection != null ? JSONUtils.selectString("name", collection).replace("(Collection)", "").trim() : "");

    List<IdInfo> ids = new ArrayList<IdInfo>();
    ids.add(new IdInfo(JSONUtils.selectInteger("id", json), ScraperUtils.AvailableApiIds.THEMOVIEDB));
    String simdbId = JSONUtils.selectString("imdb_id", json);
    if (simdbId != null) {
      IdInfo imdbId = new IdInfo(Integer.parseInt(simdbId.substring(2)), ScraperUtils.AvailableApiIds.IMDB);
      ids.add(imdbId);
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

    List<String> countries = new ArrayList<String>();
    for (JSONObject jsonObj : JSONUtils.selectList("production_countries", json)) {
      String iso_3166_1 = JSONUtils.selectString("iso_3166_1", jsonObj);
      Locale locale = new Locale("", iso_3166_1);
      countries.add(locale.getDisplayCountry(language.getLocale()));
    }

    List<String> studios = new ArrayList<String>();
    for (JSONObject jsonObj : JSONUtils.selectList("production_companies", json)) {
      studios.add(JSONUtils.selectString("name", jsonObj));
    }

    List<String> tags = new ArrayList<String>();
    if (Settings.getInstance().isGetTmdbTagg()) {
      JSONObject keywords = JSONUtils.selectObject("keywords", json);
      if (keywords != null) {
        for (JSONObject jsonObj : JSONUtils.selectList("keywords", keywords)) {
          tags.add(JSONUtils.selectString("name", jsonObj));
        }
      }
    }

    multipleFields.put(MovieMultipleProperty.studios, studios);
    multipleFields.put(MovieMultipleProperty.tags, tags);
    multipleFields.put(MovieMultipleProperty.countries, countries);
    multipleFields.put(MovieMultipleProperty.genres, genres);

    MovieInfo movieInfo = new MovieInfo(mediaFields, ids, fields, multipleFields);
    return movieInfo;
  }

  @Override
  protected List<CastingInfo> fetchCastingInfo(Movie movie, IdInfo id, AvailableLanguages language) throws Exception {
    URL searchUrl = new URL("http", apiHost, "/" + version + "/movie/" + id + "/casts?api_key=" + apikey);
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
        ImageInfo imgInfo = null;
        if (image != null && image.length() > 0) {
          int cid = JSONUtils.selectInteger("id", json);

          Map<ImageInfo.ImageProperty, String> fields = new HashMap<ImageInfo.ImageProperty, String>();
          fields.put(ImageInfo.ImageProperty.urlTumb, getTmdbImageBaseUrl() + TmdbImageSize.cast.getSmall() + image);
          fields.put(ImageInfo.ImageProperty.urlMid, getTmdbImageBaseUrl() + TmdbImageSize.cast.getMedium() + image);
          fields.put(ImageInfo.ImageProperty.url, getTmdbImageBaseUrl() + TmdbImageSize.cast.getBig() + image);
          imgInfo = new ImageInfo(cid, fields, ImageInfo.ImageCategoryProperty.actor);
        }

        if (section.equals("crew")) {
          personFields.put(PersonProperty.job, JSONUtils.selectString("job", jsonObj));
        } else {
          personFields.put(PersonProperty.job, CastingInfo.ACTOR);
        }

        casting.add(new CastingInfo(personFields, imgInfo));
      }
    }

    return casting;
  }

  @Override
  public ScraperUtils.InfoQuality getInfoQuality() {
    return ScraperUtils.InfoQuality.GREAT;
  }
}
