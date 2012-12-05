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
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.JSONUtils;
import fr.free.movierenamer.utils.WebRequest;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.simple.JSONObject;

/**
 * Class FanartTV
 *
 * @author Simon QUÉMÉNEUR
 * @author Nicolas Magré
 */
public class FanartTV extends ImageScrapper { // TODO Get images for Tv show

  private static final String host = "api.fanart.tv/webservice";
  private static final String name = "FanartTV";
  private final String apikey;

  private enum ImageType {

    hdmovielogo, // logo HD
    movielogo, // logo
    movieart, // clearart
    hdmovieart, // clearart
    moviebackground,// fanart
    moviebanner, // banner (not useful)
    moviedisc, // cdart
    //moviethumb; // thumb but not really no
  }

  public FanartTV() {
    super(Locale.ENGLISH);
    String key = Settings.decodeApkKey(Settings.getApplicationProperty("fanarttv.apkapikey"));
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
  protected List<ImageInfo> fetchImagesInfo(Media media, Locale locale) throws Exception {
    URL searchUrl = new URL("http", host, "/movie/" + apikey + "/" + media.getMediaId() + "/");// Last slash is required
    JSONObject json = WebRequest.getJsonDocument(searchUrl.toURI());
    JSONObject movie = JSONUtils.selectFirstObject(json);

    List<ImageInfo> imagesInfos = new ArrayList<ImageInfo>();

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
