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

import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.scrapper.ImageScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.JSONUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.ScrapperUtils;
import fr.free.movierenamer.utils.URIRequest;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.simple.JSONObject;

/**
 * Class TMDbImagesScrapper : search images on TMDb
 *
 * @see http://help.themoviedb.org/kb/api/
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class TMDbImagesScrapper extends ImageScrapper<Movie> {

  private static final String host = "api.themoviedb.org";
  private static final String name = "TheMovieDb";
  private static final String version = "3";
  private final String apikey;

  public TMDbImagesScrapper() {
    super(LocaleUtils.AvailableLanguages.en, LocaleUtils.AvailableLanguages.fr, LocaleUtils.AvailableLanguages.es, LocaleUtils.AvailableLanguages.it, LocaleUtils.AvailableLanguages.de);
    String key = Settings.decodeApkKey(Settings.getApplicationProperty("themoviedb.apkapikey"));
    if (key == null || key.trim().length() == 0) {
      throw new NullPointerException("apikey must not be null");
    }
    this.apikey = key;
  }

  private String imdbIDLookUp(String imdbId) throws Exception {
    URL searchUrl = new URL("http", host, "/" + version + "/movie/" + imdbId + "?api_key=" + apikey);
    JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());
    return JSONUtils.selectString("id", json);
  }

  @Override
  protected List<ImageInfo> fetchImagesInfo(Movie movie, Locale language) throws Exception {
    String id = movie.getId().toString();
    switch(movie.getId().getIdType()) {
      case IMDB:
        id = imdbIDLookUp(id);
        break;
      case TMDB:
        break;
      default:
        throw new UnsupportedOperationException(movie.getId().getIdType().name() + " is not supported by tmsb image scrapper");
    }

    URL searchUrl = new URL("http", host, "/" + version + "/movie/" + id + "/images?api_key=" + apikey);
    JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());

    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (String section : new String[]{
              "backdrops", "posters"
            }) {
      List<JSONObject> jsonObjs = JSONUtils.selectList(section, json);
      TMDbScrapper.TmdbImageSize imageSize = section.equals("backdrops") ? TMDbScrapper.TmdbImageSize.backdrop : TMDbScrapper.TmdbImageSize.poster;
      for (JSONObject jsonObj : jsonObjs) {
        Map<ImageInfo.ImageProperty, String> imageFields = new EnumMap<ImageInfo.ImageProperty, String>(ImageInfo.ImageProperty.class);
        String file_path = JSONUtils.selectString("file_path", jsonObj);

        imageFields.put(ImageInfo.ImageProperty.url, TMDbScrapper.imageUrl + imageSize.getBig() + file_path);
        imageFields.put(ImageInfo.ImageProperty.urlMid, TMDbScrapper.imageUrl + imageSize.getMedium() + file_path);
        imageFields.put(ImageInfo.ImageProperty.urlTumb, TMDbScrapper.imageUrl + imageSize.getSmall() + file_path);

        String lang = JSONUtils.selectString("iso_639_1", jsonObj);
        if(lang != null && !lang.equals("null")) {
          imageFields.put(ImageInfo.ImageProperty.language, lang);
        }
        imageFields.put(ImageInfo.ImageProperty.width, JSONUtils.selectString("width", jsonObj));
        imageFields.put(ImageInfo.ImageProperty.height, JSONUtils.selectString("height", jsonObj));

        images.add(new ImageInfo(imageFields, section.equals("posters") ? ImageInfo.ImageCategoryProperty.thumb : ImageInfo.ImageCategoryProperty.fanart));
      }
    }

    return images;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  protected String getHost() {
    return host;
  }
}
