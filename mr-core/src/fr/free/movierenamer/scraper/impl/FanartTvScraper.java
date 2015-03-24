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
package fr.free.movierenamer.scraper.impl;

import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.ImageInfo.ImageCategoryProperty;
import fr.free.movierenamer.scraper.ImageScraper;
import fr.free.movierenamer.searchinfo.Video;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.JSONUtils;
import fr.free.movierenamer.utils.URIRequest;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;


/**
 * Class FanartTvScraper
 *
 * @author Simon QUÉMÉNEUR
 * @author Nicolas Magré
 */
public abstract class FanartTvScraper<M extends Video> extends ImageScraper<M> {

  protected final String host = "api.fanart.tv/webservice";
  protected final String name = "FanartTV";
  protected final String apikey;

  protected FanartTvScraper() {
    String key = Settings.decodeApkKey(Settings.getApplicationProperty("fanarttv.apkapikey"));
    if (key == null || key.trim().length() == 0) {
      throw new NullPointerException("apikey must not be null");
    }
    this.apikey = key;
  }

  @Override
  protected final List<ImageInfo> fetchImagesInfo(M media) throws Exception {
    IdInfo mid = media.getMediaId();
    if (media.getImdbId() != null) {
      mid = media.getImdbId();
    }

    switch (mid.getIdType()) {
      case IMDB:
        break;
      case THEMOVIEDB:
        break;
      default:
        throw new UnsupportedOperationException(mid.getIdType() + " is not supported by " + getName() + " image scraper");
    }

    URL searchUrl = new URL("http", host, "/" + getTypeName() + "/" + apikey + "/" + mid.toString() + "/");// Last slash is required
    JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());
    JSONObject jmedia = JSONUtils.selectFirstObject(json);

    List<ImageInfo> imagesInfos = new ArrayList<ImageInfo>();
    if (jmedia == null) {
      return imagesInfos;
    }

    for (String tag : getTags()) {

      List<JSONObject> images = JSONUtils.selectList(tag, jmedia);
      if (images == null) {
        continue;
      }

      for (JSONObject image : images) {
        Map<ImageInfo.ImageProperty, String> imageFields = new EnumMap<ImageInfo.ImageProperty, String>(ImageInfo.ImageProperty.class);
        int id = JSONUtils.selectInteger("id", image);
        imageFields.put(ImageInfo.ImageProperty.url, JSONUtils.selectString("url", image));
        imageFields.put(ImageInfo.ImageProperty.urlTumb, JSONUtils.selectString("url", image).replace("/fanart/", "/preview/"));
        imageFields.put(ImageInfo.ImageProperty.language, JSONUtils.selectString("lang", image));
        ImageInfo.ImageCategoryProperty category = getCategory(tag);
        imagesInfos.add(new ImageInfo(id, imageFields, category));
      }
    }

    return imagesInfos;
  }

  protected abstract String getTypeName();

  protected abstract List<String> getTags();

  protected abstract ImageCategoryProperty getCategory(String tag);

  @Override
  public String getName() {
    return name;
  }

  @Override
  protected String getHost() {
    return host;
  }
}
