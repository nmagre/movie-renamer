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
package fr.free.movierenamer.scrapper.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.json.simple.JSONObject;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.CastingInfo.PersonProperty;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.ImageInfo.ImageCategoryProperty;
import fr.free.movierenamer.info.ImageInfo.ImageProperty;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.MovieInfo.MovieProperty;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.ImageUtils;
import fr.free.movierenamer.utils.JSONUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.WebRequest;

/**
 * Class AllocineScrapper : search movie on allocine
 * 
 * @see http://wiki.gromez.fr/dev/api/allocine_v3
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class AllocineScrapper extends MovieScrapper {

  private static final String host = "api.allocine.fr";
  private static final String name = "Allocine";
  private static final String version = "3";

  private final String apikey;

  public AllocineScrapper() {
    super(Locale.FRENCH);
    String key = Settings.getApplicationProperty("allocine.apikey");
    if (key == null) {
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
  public Icon getIcon() {
    return new ImageIcon(ImageUtils.getImageFromJAR("scrapper/allocine.png"));
  }
  
  @Override
  public boolean hasLocaleSupport() {
    return false;
  }

  @Override
  protected List<Movie> searchMedia(String query, Locale locale) throws Exception {
    URL searchUrl = new URL("http", host, "/rest/v" + version + "/search?partner=" + apikey + "&filter=movie&striptags=synopsis,synopsisshort&format=json&q=" + WebRequest.encode(query));
    JSONObject json = WebRequest.getJsonDocument(searchUrl.toURI());
    Map<Integer, Movie> resultSet = new LinkedHashMap<Integer, Movie>();

    for (JSONObject movie : JSONUtils.selectList("feed/movie", json)) {
      String name = JSONUtils.selectString("title", movie);
      if (name == null || name.isEmpty()) {
        name = JSONUtils.selectString("originalTitle", movie);
      }
      Integer year = JSONUtils.selectInteger("productionYear", movie);
      Integer imdbId = -1;
      Integer movieId = JSONUtils.selectInteger("code", movie);
      JSONObject poster = JSONUtils.selectObject("poster", movie);
      URL posterURL;
      if (poster != null) {
        posterURL = new URL(JSONUtils.selectString("href", poster));
      } else {
        posterURL = null;
      }

      resultSet.put(movieId, new Movie(movieId, name, posterURL, year, imdbId));
    }

    return new ArrayList<Movie>(resultSet.values());
  }

  @Override
  protected MovieInfo fetchMediaInfo(Movie movie, Locale locale) throws Exception {
    URL searchUrl = new URL("http", host, "/rest/v" + version + "/movie?partner=" + apikey + "&profile=large&filter=movie&striptags=synopsis,synopsisshort&format=json&code=" + movie.getMediaId());
    JSONObject json = WebRequest.getJsonDocument(searchUrl.toURI());

    JSONObject movieObject = JSONUtils.selectObject("movie", json);
    JSONObject statistics = JSONUtils.selectObject("statistics", movieObject);
    JSONObject release = JSONUtils.selectObject("release", movieObject);

    Map<MovieProperty, String> fields = new EnumMap<MovieProperty, String>(MovieProperty.class);
    fields.put(MovieProperty.title, JSONUtils.selectString("title", movieObject));
    fields.put(MovieProperty.rating, String.valueOf(JSONUtils.selectDouble("userRating", statistics) * 2));// allocine return rating out of 5
    fields.put(MovieProperty.votes, JSONUtils.selectString("userRatingCount", statistics));
    fields.put(MovieProperty.id, JSONUtils.selectString("code", movieObject));
    fields.put(MovieProperty.originalTitle, JSONUtils.selectString("originalTitle", movieObject));
    fields.put(MovieProperty.releasedDate, JSONUtils.selectString("releaseDate", release));
    fields.put(MovieProperty.overview, JSONUtils.selectString("synopsis", movieObject));
    fields.put(MovieProperty.runtime, String.valueOf(JSONUtils.selectInteger("runtime", movieObject) / 60));// allocine return time in sec
    fields.put(MovieProperty.budget, JSONUtils.selectString("budget", movieObject));
    fields.put(MovieProperty.posterPath, JSONUtils.selectString("href", JSONUtils.selectObject("posterPath", movieObject)));

    List<String> genres = new ArrayList<String>();
    for (JSONObject genre : JSONUtils.selectList("genre", movieObject)) {
      genres.add(JSONUtils.selectString("$", genre));
    }

    List<Locale> countries = new ArrayList<Locale>();
    for (JSONObject country : JSONUtils.selectList("nationality", movieObject)) {
      countries.add(LocaleUtils.getLocale(JSONUtils.selectString("$", country), locale));
    }

    MovieInfo movieInfo = new MovieInfo(fields, genres, countries);
    return movieInfo;
  }

  @Override
  protected List<ImageInfo> fetchImagesInfo(Movie movie, Locale locale) throws Exception {
	  URL searchUrl = new URL("http", host, "/rest/v" + version + "/movie?partner=" + apikey + "&profile=large&filter=movie&striptags=synopsis,synopsisshort&format=json&code=" + movie.getMediaId());
    JSONObject json = WebRequest.getJsonDocument(searchUrl.toURI());

    JSONObject movieObject = JSONUtils.selectObject("movie", json);
    List<JSONObject> medias = JSONUtils.selectList("media", movieObject);

    List<ImageInfo> images = new ArrayList<ImageInfo>();
    if (medias != null) {
      for (JSONObject media : medias) {
        if ("picture".equals(JSONUtils.selectString("class", media))) {
          Integer code = JSONUtils.selectInteger("code", JSONUtils.selectObject("type", media));
          Map<ImageProperty, String> imageFields = new EnumMap<ImageProperty, String>(ImageProperty.class);
          ImageCategoryProperty category;
          if (code == 31001) {
            // affiche
            category = ImageCategoryProperty.thumb;
          } else if (code == 31006) {
            // photo
            category = ImageCategoryProperty.fanart;
          } else {
            category = ImageCategoryProperty.unknown;
          }
          imageFields.put(ImageProperty.url, JSONUtils.selectString("href", JSONUtils.selectObject("thumbnail", media)));
          imageFields.put(ImageProperty.desc, JSONUtils.selectString("title", media));
          images.add(new ImageInfo(imageFields, category));
        }
      }
    }

    return images;
  }

  @Override
  protected List<CastingInfo> fetchCastingInfo(Movie movie, Locale locale) throws Exception {
	  URL searchUrl = new URL("http", host, "/rest/v" + version + "/movie?partner=" + apikey + "&profile=large&filter=movie&striptags=synopsis,synopsisshort&format=json&code=" + movie.getMediaId());
    JSONObject json = WebRequest.getJsonDocument(searchUrl.toURI());

    JSONObject movieObject = JSONUtils.selectObject("movie", json);
    List<JSONObject> castMembers = JSONUtils.selectList("castMember", movieObject);

    List<CastingInfo> casting = new ArrayList<CastingInfo>();
    if (castMembers != null) {
      for (JSONObject castMemberObj : JSONUtils.selectList("castMember", movieObject)) {
        Map<PersonProperty, String> personFields = new EnumMap<PersonProperty, String>(PersonProperty.class);
        JSONObject personObj = JSONUtils.selectObject("person", castMemberObj);
        JSONObject activityObj = JSONUtils.selectObject("activity", castMemberObj);
        JSONObject pictureObj = JSONUtils.selectObject("picture", castMemberObj);
        personFields.put(PersonProperty.id, JSONUtils.selectString("code", personObj));
        personFields.put(PersonProperty.name, JSONUtils.selectString("name", personObj));
        Integer jobCode = JSONUtils.selectInteger("code", activityObj);
        if (jobCode == 8001) {
          // Actor
          personFields.put(PersonProperty.job, CastingInfo.ACTOR);
        } else if (jobCode == 8002) {
          // Director
          personFields.put(PersonProperty.job, CastingInfo.DIRECTOR);
        } else if (jobCode == 8004) {
          // Director
          personFields.put(PersonProperty.job, CastingInfo.WRITER);
        } else {
          personFields.put(PersonProperty.job, JSONUtils.selectString("$", activityObj));
        }
        personFields.put(PersonProperty.picturePath, JSONUtils.selectString("href", pictureObj));
        casting.add(new CastingInfo(personFields));
      }
    }

    return casting;
  }
}
