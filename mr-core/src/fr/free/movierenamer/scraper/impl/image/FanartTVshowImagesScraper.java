/*
 * Movie Renamer
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

import fr.free.movierenamer.scraper.impl.FanartTvScraper;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.searchinfo.TvShow;
import java.util.ArrayList;
import java.util.List;

/**
 * Class FanartTVshowImagesScraper
 *
 * @author Nicolas Magré
 */
public class FanartTVshowImagesScraper extends FanartTvScraper<TvShow> {

  private enum TvImagesType {

    hdtvlogo,
    clearlogo,
    clearart,
    seasonthumb,
    tvthumb,
    showbackground
  }

  @Override
  public List<String> getTags() {
    List<String> tags = new ArrayList<String>();
    for (TvImagesType imgtype : TvImagesType.values()) {
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
    return "series";
  }

  @Override
  protected ImageInfo.ImageCategoryProperty getCategory(String key) {
    TvImagesType type = Enum.valueOf(TvImagesType.class, key);
    ImageInfo.ImageCategoryProperty category;
    switch (type) {
      case hdtvlogo:
      case clearlogo:
        category = ImageInfo.ImageCategoryProperty.logo;
        break;
      case clearart:
        category = ImageInfo.ImageCategoryProperty.clearart;
        break;
      case tvthumb:
      case seasonthumb:
        category = ImageInfo.ImageCategoryProperty.thumb;
        break;
      case showbackground:
        category = ImageInfo.ImageCategoryProperty.fanart;
        break;
      default:
        category = ImageInfo.ImageCategoryProperty.unknown;
    }

    return category;
  }

  @Override
  public InfoQuality getQuality() {
    return InfoQuality.AWESOME;
  }

}
