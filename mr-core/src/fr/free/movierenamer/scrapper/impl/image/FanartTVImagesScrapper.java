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
package fr.free.movierenamer.scrapper.impl.image;

import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.scrapper.impl.FanartTvScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import java.util.ArrayList;
import java.util.List;

/**
 * Class FanartTVImagesScrapper
 *
 * @author Simon QUÉMÉNEUR
 * @author Nicolas Magré
 */
public class FanartTVImagesScrapper extends FanartTvScrapper<Movie> {

  private enum ImageType {

    hdmovielogo, // logo HD
    movielogo, // logo
    movieart, // clearart
    hdmovieart, // clearart
    moviebackground,// fanart
    moviebanner, // banner (not useful)
    moviedisc,; // cdart
    //moviethumb; // thumb but not really no
  }

  @Override
  public List<String> getTags() {
    List<String> tags = new ArrayList<String>();
    for (ImageType imgtype : ImageType.values()) {
      tags.add(imgtype.name());
    }

    return tags;
  }

  @Override
  protected String getCacheKey() {
    return getClass().getName();
  }

  @Override
  protected String getTypeName() {
    return "movie";
  }

  @Override
  protected ImageInfo.ImageCategoryProperty getCategory(String key) {
    ImageType type = Enum.valueOf(ImageType.class, key);
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

    return category;
  }
}
