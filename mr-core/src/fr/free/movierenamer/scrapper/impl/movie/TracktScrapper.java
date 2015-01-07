/*
 * movie-renamer-core
 * Copyright (C) 2014 Nicolas Magré
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
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.ImageInfo.ImageCategoryProperty;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.JSONUtils;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.ScrapperUtils;
import fr.free.movierenamer.utils.ScrapperUtils.AvailableApiIds;
import fr.free.movierenamer.utils.StringUtils;
import fr.free.movierenamer.utils.URIRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Class TrackTvScrapper
 *
 * @author Nicolas Magré
 */
public class TracktScrapper extends MovieScrapper {

  private static final String host = "trakt.tv";
  private static final String apiHost = "api." + host;
  private static final String name = "Trackt";
  private static final String NOIMAGE = "http://slurm.trakt.us/images/avatar-large.jpg";
  private static String apikey;
  private static final AvailableApiIds supportedId = AvailableApiIds.IMDB;// Track.tv support both imdb and tmdb ids

  public TracktScrapper() {
    super(AvailableLanguages.en);
    String key = Settings.decodeApkKey(Settings.getApplicationProperty("trackt.apkapikey"));
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
  public ScrapperUtils.InfoQuality getInfoQuality() {
    return ScrapperUtils.InfoQuality.GREAT;
  }

  @Override
  protected AvailableLanguages getDefaultLanguage() {
    return AvailableLanguages.en;
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
  public IdInfo getIdfromURL(URL url) {
    return null;
  }

  @Override
  public URL getURL(IdInfo id) {
    return null;
  }

  @Override
  protected List<Movie> searchMedia(String query, AvailableLanguages language) throws Exception {
    URL searchUrl = new URL("http", apiHost, "/search/movies.json/" + apikey + "?&query=" + URIRequest.encode(query));
    return searchMedia(searchUrl, language);
  }

  @Override
  protected List<Movie> searchMedia(URL searchUrl, AvailableLanguages language) throws Exception {
    Map<Integer, Movie> resultSet = new LinkedHashMap<Integer, Movie>();
    JSONArray json = URIRequest.getJsonArrayDocument(searchUrl.toURI());
    Iterator<JSONObject> iterator = json.iterator();

    JSONObject node;
    while (iterator.hasNext()) {
      node = iterator.next();
      int id = JSONUtils.selectInteger("tmdb_id", node);
      String imId = JSONUtils.selectString("imdb_id", node);
      IdInfo imdbId = null;
      if (imId != null && !imId.equals("")) {
        imdbId = new IdInfo(Integer.parseInt(imId.replace("tt", "")), ScrapperUtils.AvailableApiIds.IMDB);
      }
      String title = JSONUtils.selectString("title", node);

      Integer year = -1;
      String syear = JSONUtils.selectString("year", node);
      if (syear != null && !syear.equals("")) {
        year = Integer.parseInt(syear);
      }

      JSONObject posterNode = JSONUtils.selectObject("images", node);

      String imageNode = null;
      if (posterNode != null) {
        imageNode = JSONUtils.selectString("poster", posterNode);
      }

      URL thumb = null;
      try {
        if (imageNode != null) {
          thumb = new URL(imageNode);
        }
      } catch (Exception e) {
        Settings.LOGGER.log(Level.WARNING, "Invalid image: " + imageNode, e);
      }

      if (!resultSet.containsKey(id)) {
        resultSet.put(id, new Movie(imdbId, new IdInfo(id, ScrapperUtils.AvailableApiIds.THEMOVIEDB), title, title, thumb, year));
      }
    }

    return new ArrayList<Movie>(resultSet.values());
  }

  @Override
  protected MovieInfo fetchMediaInfo(Movie searchResult, IdInfo id, AvailableLanguages language) throws Exception {

    URL searchUrl = new URL("http", apiHost, "/movie/summary.json/" + apikey + "/" + id);
    JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());

    final Map<MediaInfo.MediaProperty, String> mediaFields = new EnumMap<MediaInfo.MediaProperty, String>(MediaInfo.MediaProperty.class);
    Map<MovieInfo.MovieProperty, String> fields = new EnumMap<MovieInfo.MovieProperty, String>(MovieInfo.MovieProperty.class);
    Map<MovieInfo.MovieMultipleProperty, List<String>> multipleFields = new EnumMap<MovieInfo.MovieMultipleProperty, List<String>>(MovieInfo.MovieMultipleProperty.class);

    mediaFields.put(MediaInfo.MediaProperty.title, JSONUtils.selectString("title", json));
    String syear = JSONUtils.selectString("year", json);
    if (syear != null && !syear.isEmpty()) {
      fields.put(MovieInfo.MovieProperty.releasedDate, syear);
      mediaFields.put(MediaInfo.MediaProperty.year, syear);
    }

    fields.put(MovieInfo.MovieProperty.overview, JSONUtils.selectString("overview", json));
    fields.put(MovieInfo.MovieProperty.runtime, JSONUtils.selectString("runtime", json));
    fields.put(MovieInfo.MovieProperty.tagline, JSONUtils.selectString("tagline", json));
    fields.put(MovieInfo.MovieProperty.certificationCode, JSONUtils.selectString("certification", json));

    JSONObject jsrate = JSONUtils.selectObject("ratings", json);
    if (jsrate != null) {
      String rate = JSONUtils.selectString("percentage", jsrate);
      if (rate != null && !rate.equals("")) {
        Double rating = Double.parseDouble(rate) / 10;
        mediaFields.put(MediaInfo.MediaProperty.rating, "" + rating);
      }

      Integer votes = JSONUtils.selectInteger("votes", jsrate);
      if (votes != null) {
        fields.put(MovieInfo.MovieProperty.votes, "" + votes);
      }
    }

    List<String> genres = new ArrayList<String>();
    String jgenres = JSONUtils.selectString("genres", json);
    if (jgenres != null && !jgenres.isEmpty()) {
      genres = Arrays.asList(StringUtils.fromString(jgenres));
    }

    multipleFields.put(MovieInfo.MovieMultipleProperty.genres, genres);

    List<IdInfo> ids = new ArrayList<IdInfo>();
    addId(ids, json, "imdb_id", ScrapperUtils.AvailableApiIds.IMDB);
    addId(ids, json, "tmdb_id", ScrapperUtils.AvailableApiIds.THEMOVIEDB);
    addId(ids, json, "rt_id", ScrapperUtils.AvailableApiIds.ROTTENTOMATOES);

    MovieInfo movieInfo = new MovieInfo(mediaFields, ids, fields, multipleFields);
    return movieInfo;
  }

  @Override
  protected List<CastingInfo> fetchCastingInfo(Movie search, IdInfo id, AvailableLanguages language) throws Exception {// TODO

    URL searchUrl = new URL("http", apiHost, "/movie/summary.json/" + apikey + "/" + id);
    JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());
    JSONObject peoples = JSONUtils.selectObject("people", json);

    List<CastingInfo> casting = new ArrayList<CastingInfo>();

    addCast(casting, "directors", peoples, CastingInfo.DIRECTOR);
    addCast(casting, "writers", peoples, CastingInfo.WRITER);
    addCast(casting, "actors", peoples, CastingInfo.ACTOR);

    return casting;
  }

  private void addCast(List<CastingInfo> casting, String path, JSONObject json, String job) {
    for (JSONObject cast : JSONUtils.selectList(path, json)) {

      Map<CastingInfo.PersonProperty, String> personFields = new EnumMap<CastingInfo.PersonProperty, String>(CastingInfo.PersonProperty.class);
      personFields.put(CastingInfo.PersonProperty.name, JSONUtils.selectString("name", cast));
      if (job.equals(CastingInfo.ACTOR)) {
        personFields.put(CastingInfo.PersonProperty.character, JSONUtils.selectString("character", cast));
      }

      JSONObject image = JSONUtils.selectObject("images", cast);
      ImageInfo imgInfo = null;
      if (image != null) {
        String img = JSONUtils.selectString("headshot", image);
        if (img != null && !img.equals(NOIMAGE)) {
          Map<ImageInfo.ImageProperty, String> fields = new HashMap<ImageInfo.ImageProperty, String>();
          fields.put(ImageInfo.ImageProperty.url, JSONUtils.selectString("headshot", image));
          int id = fields.get(ImageInfo.ImageProperty.url).hashCode();
          imgInfo = new ImageInfo(id, fields, ImageCategoryProperty.actor);
        }
      }
      personFields.put(CastingInfo.PersonProperty.job, job);

      casting.add(new CastingInfo(personFields, imgInfo));
    }
  }

  private void addId(List<IdInfo> ids, JSONObject json, String name, ScrapperUtils.AvailableApiIds type) {
    String id = JSONUtils.selectString(name, json);
    if (id != null && !id.equals("")) {
      ids.add(new IdInfo(Integer.parseInt(id.replaceAll("[^0-9]", "")), type));
    }
  }

}
