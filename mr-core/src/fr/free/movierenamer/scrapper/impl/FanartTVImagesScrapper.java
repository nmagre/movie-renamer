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
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.utils.JSONUtils;
import fr.free.movierenamer.utils.URIRequest;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.simple.JSONObject;


/**
 * Class FanartTVImagesScrapper
 *
 * @author Simon QUÉMÉNEUR
 * @author Nicolas Magré
 */
public class FanartTVImagesScrapper extends FanartTvScrapper<Movie> {

  @Override
  protected List<ImageInfo> fetchImagesInfo(Movie media, Locale language) throws Exception {
    URL searchUrl = new URL("http", host, "/movie/" + apikey + "/" + media.getMediaId() + "/");// Last slash is required
    JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());
    JSONObject movie = JSONUtils.selectFirstObject(json);

    List<ImageInfo> imagesInfos = new ArrayList<ImageInfo>();

    if(movie == null) {// No images for this movie
      return imagesInfos;
    }

    for (ImageType type : ImageType.values()) {
      List<JSONObject> images = JSONUtils.selectList(type.name(), movie);
      if(images == null) continue;

      for (JSONObject image : images) {
        Map<ImageInfo.ImageProperty, String> imageFields = new EnumMap<ImageInfo.ImageProperty, String>(ImageInfo.ImageProperty.class);
        imageFields.put(ImageInfo.ImageProperty.url, JSONUtils.selectString("url", image));
        imageFields.put(ImageInfo.ImageProperty.language, JSONUtils.selectString("lang", image));
        ImageInfo.ImageCategoryProperty category;
        switch (type) {
          case hdmovielogo:
          case movielogo:
            category = ImageInfo.ImageCategoryProperty.logo;
            break;
          case hdmovieart:
          case movieart:
            category = ImageInfo.ImageCategoryProperty.clearart;
            break;
          case moviedisc:
            category = ImageInfo.ImageCategoryProperty.cdart;
            break;
          case moviebackground:
            category = ImageInfo.ImageCategoryProperty.fanart;
            break;
          case moviebanner:
            category = ImageInfo.ImageCategoryProperty.banner;
            break;
          default:
            category = ImageInfo.ImageCategoryProperty.unknown;
        }
        imagesInfos.add(new ImageInfo(imageFields, category));
      }
    }

    return imagesInfos;
  }
}
