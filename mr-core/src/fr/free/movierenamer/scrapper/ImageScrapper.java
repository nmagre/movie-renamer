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
package fr.free.movierenamer.scrapper;

import java.util.List;
import java.util.Locale;

import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.searchinfo.Image;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.utils.CacheObject;

/**
 * Class ImageScrapper
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class ImageScrapper extends Scrapper {

  protected ImageScrapper(Locale defaultLocale) {
    super(defaultLocale);
  }

  public final List<ImageInfo> getImages(Media media) throws Exception {
    return getImages(media, getLocale());
  }

  protected final List<ImageInfo> getImages(Media media, Locale locale) throws Exception {
    CacheObject cache = getCache();
    List<ImageInfo> imageList = (cache != null) ? cache.getList(media, locale, ImageInfo.class) : null;
    if (imageList != null) {
      return imageList;
    }

    // perform actual search
    imageList = fetchImagesInfo(media, locale);

    // cache results and return
    return (cache != null) ? cache.putList(media, locale, ImageInfo.class, imageList) : imageList;
  }

  protected abstract List<ImageInfo> fetchImagesInfo(Media media, Locale locale) throws Exception;

}