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
package fr.free.movierenamer.scraper;

import fr.free.movierenamer.info.IdInfo;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.CacheObject;

/**
 * Class ImageScraper
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class ImageScraper<M extends Media> extends Scraper {

  public final List<ImageInfo> getImages(M media) throws Exception {
    Settings.LOGGER.log(Level.INFO, String.format("Use '%s' to get images for '%s", getName(), media));
    CacheObject cache = getCache();

    IdInfo id = media.getMediaId();
    String cacheKey = id.toString() + "_" + getCacheKey();

    List<ImageInfo> imageList = (cache != null) ? cache.getList(cacheKey, Locale.ROOT, ImageInfo.class) : null;
    if (imageList != null) {
      return imageList;
    }

    // perform actual search
    imageList = fetchImagesInfo(media);
    Settings.LOGGER.log(Level.INFO, String.format("'%s' returns %d images for '%s' in", getName(), imageList.size(), media));

    // cache results and return
    return (cache != null) ? cache.putList(media, Locale.ROOT, ImageInfo.class, imageList) : imageList;
  }

  protected abstract List<ImageInfo> fetchImagesInfo(M media) throws Exception;

  protected abstract String getCacheKey();

  @Override
  protected final String getCacheName() {
    return "medium";
  }
}
