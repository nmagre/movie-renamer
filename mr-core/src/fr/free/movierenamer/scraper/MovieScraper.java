/*
 * movie-renamer-core
 * Copyright (C) 2012-2014 Nicolas Magré
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.free.movierenamer.scraper;

import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.scraper.impl.image.FanartTVImagesScraper;
import fr.free.movierenamer.scraper.impl.image.TMDbImagesScraper;
import fr.free.movierenamer.searchinfo.Media.MediaType;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Class MovieScraper
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class MovieScraper extends VideoScraper<Movie, MovieInfo> {

  protected MovieScraper(AvailableLanguages... supportedLanguages) {
    super(supportedLanguages);
  }

  @Override
  protected List<ImageInfo> fetchImagesInfo(Movie movie) throws Exception {

    List<ImageInfo> imagesInfo = new ArrayList<ImageInfo>();
    List<ImageInfo> tmpImagesInfo;

    try {
      // Try to get images from tmdb
      TMDbImagesScraper tmiscc = new TMDbImagesScraper();
      tmpImagesInfo = tmiscc.getImages(movie);
      if (tmpImagesInfo != null) {
        imagesInfo.addAll(tmpImagesInfo);
      }
    } catch (UnsupportedOperationException ex) {
      // Images scraper do not support this id type
      Settings.LOGGER.log(Level.SEVERE, ex.getMessage());
    } catch (FileNotFoundException ex) {
      // No images for this movie
    }

    // If there is no thumb and fanart we use "current" scraper to get image
    if (imagesInfo.isEmpty()) {
      tmpImagesInfo = getScraperImages(movie);
      if (tmpImagesInfo != null) {
        imagesInfo.addAll(tmpImagesInfo);
      }
    }

    // If there is no images, we try to add image from search
    if (imagesInfo.isEmpty()) {
      URI thumb = movie.getURL();
      if (thumb != null) {
        Map<ImageInfo.ImageProperty, String> imageFields = new EnumMap<ImageInfo.ImageProperty, String>(ImageInfo.ImageProperty.class);
        imageFields.put(ImageInfo.ImageProperty.url, thumb.toString());
        imagesInfo.add(new ImageInfo(0, imageFields, ImageInfo.ImageCategoryProperty.thumb));
      }
    }

    try {
      // Try to get images from fanart.tv
      FanartTVImagesScraper fanartImagesSc = new FanartTVImagesScraper();
      tmpImagesInfo = fanartImagesSc.getImages(movie);
      if (tmpImagesInfo != null) {
        imagesInfo.addAll(tmpImagesInfo);
      }
    } catch (UnsupportedOperationException ex) {
      // Images scraper do not support this id type
      Settings.LOGGER.log(Level.SEVERE, ex.getMessage());
    } catch (FileNotFoundException ex) {
      // No images for this movie
    }

    return imagesInfo;
  }

  @Override
  public MediaType getSupportedMediaType() {
    return MediaType.MOVIE;
  }
  
}
