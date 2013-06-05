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

import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.scrapper.impl.image.FanartTVImagesScrapper;
import fr.free.movierenamer.scrapper.impl.image.TMDbImagesScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Class MovieScrapper
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class MovieScrapper extends MediaScrapper<Movie, MovieInfo> {

  protected MovieScrapper(AvailableLanguages... supportedLanguages) {
    super(supportedLanguages);
  }

  @Override
  protected final List<ImageInfo> fetchImagesInfo(Movie movie) throws Exception {

    List<ImageInfo> imagesInfo = new ArrayList<ImageInfo>();
    List<ImageInfo> tmpImagesInfo;

    try {
      // Try to get images from tmdb
      TMDbImagesScrapper tmiscc = new TMDbImagesScrapper();
      tmpImagesInfo = tmiscc.getImages(movie);
      if (tmpImagesInfo != null) {
        imagesInfo.addAll(tmpImagesInfo);
      }
    } catch (UnsupportedOperationException ex) {
      // Images scrapper do not support this id type
    } catch (FileNotFoundException ex) {
      // No images for this movie
    }

    try {
      // Try to get images from fanart.tv
      FanartTVImagesScrapper fanartImagesSc = new FanartTVImagesScrapper();
      tmpImagesInfo = fanartImagesSc.getImages(movie);
      if (tmpImagesInfo != null) {
        imagesInfo.addAll(tmpImagesInfo);
      }
    } catch (UnsupportedOperationException ex) {
      // Images scrapper do not support this id type
    } catch (FileNotFoundException ex) {
      // No images for this movie
    }

    // use scrapper default get image
    if (imagesInfo.isEmpty()) {
      tmpImagesInfo = getScrapperImages(movie);
      if (tmpImagesInfo != null) {
        imagesInfo.addAll(tmpImagesInfo);
      }
    }

    return imagesInfo;
  }
}
