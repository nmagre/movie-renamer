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
package fr.free.movierenamer.scraper.impl.image;

import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.scraper.ImageScraper;
import fr.free.movierenamer.scraper.impl.movie.TMDbScraper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.JSONUtils;
import fr.free.movierenamer.utils.ScraperUtils.TmdbImageSize;
import fr.free.movierenamer.utils.URIRequest;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

/**
 * Class TMDbImagesScraper : search images on TMDb
 *
 * @see http://help.themoviedb.org/kb/api/
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class TMDbImagesScraper extends ImageScraper<Movie> {

  private static final String host = "api.themoviedb.org";
  private static final String name = "TheMovieDb";
  private static final String version = "3";
  private final String apikey;

  public TMDbImagesScraper() {
    String key = Settings.decodeApkKey(Settings.getApplicationProperty("themoviedb.apkapikey"));
    if (key == null || key.trim().length() == 0) {
      throw new NullPointerException("apikey must not be null");
    }
    this.apikey = key;
  }

  private String tmdbIDLookUp(String imdbId) throws Exception {
    URL searchUrl = new URL("http", host, "/" + version + "/movie/" + imdbId + "?api_key=" + apikey);
    JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());
    return JSONUtils.selectString("id", json);
  }

  @Override
  protected List<ImageInfo> fetchImagesInfo(Movie movie) throws Exception {
    IdInfo mid = movie.getMediaId();
    if (movie.getImdbId() != null) {
      mid = movie.getImdbId();
    }

    String id = mid.toString();
    switch (mid.getIdType()) {
      case IMDB:
        id = tmdbIDLookUp(id);
        break;
      case THEMOVIEDB:
        break;
      default:
        throw new UnsupportedOperationException(movie.getMediaId().getIdType() + " is not supported by " + getName() + " image scraper");
    }

    URL searchUrl = new URL("http", host, "/" + version + "/movie/" + id + "/images?api_key=" + apikey);
    JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());

    List<ImageInfo> images = new ArrayList<>();
    for (String section : new String[]{
      "backdrops", "posters"
    }) {
      List<JSONObject> jsonObjs = JSONUtils.selectList(section, json);
      TmdbImageSize imageSize = section.equals("backdrops") ? TmdbImageSize.backdrop : TmdbImageSize.poster;
      int count = 0;
      for (JSONObject jsonObj : jsonObjs) {
        Map<ImageInfo.ImageProperty, String> imageFields = new EnumMap<>(ImageInfo.ImageProperty.class);
        String file_path = JSONUtils.selectString("file_path", jsonObj);

        imageFields.put(ImageInfo.ImageProperty.url, TMDbScraper.imageUrl + imageSize.getBig() + file_path);
        imageFields.put(ImageInfo.ImageProperty.urlMid, TMDbScraper.imageUrl + imageSize.getMedium() + file_path);
        imageFields.put(ImageInfo.ImageProperty.urlTumb, TMDbScraper.imageUrl + imageSize.getSmall() + file_path);

        String lang = JSONUtils.selectString("iso_639_1", jsonObj);
        if (lang != null && !lang.equals("null")) {
          imageFields.put(ImageInfo.ImageProperty.language, lang);
        }

        imageFields.put(ImageInfo.ImageProperty.width, JSONUtils.selectString("width", jsonObj));
        imageFields.put(ImageInfo.ImageProperty.height, JSONUtils.selectString("height", jsonObj));

        images.add(new ImageInfo(count++, imageFields, section.equals("posters") ? ImageInfo.ImageCategoryProperty.thumb : ImageInfo.ImageCategoryProperty.fanart));
      }
    }

    return images;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  protected String getCacheKey() {
    return getClass().getName();
  }

  @Override
  protected String getHost() {
    return host;
  }

  @Override
  public InfoQuality getQuality() {
    return InfoQuality.GREAT;
  }

}
