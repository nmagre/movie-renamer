/*
 * movie-renamer-core
 * Copyright (C) 2013 Nicolas Magré
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

import fr.free.movierenamer.info.TrailerInfo;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.searchinfo.Trailer;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.CacheObject;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

/**
 * Class TrailerScrapper
 *
 * @author Nicolas Magré
 */
public abstract class TrailerScrapper extends Scrapper {

  public final List<Trailer> getTrailer(Movie media) throws Exception {// TODO
    Settings.LOGGER.log(Level.INFO, String.format("Use '%s' to get image info list for '%s", getName(), media));
    CacheObject cache = getCache();
    List<Trailer> trailerList = (cache != null) ? cache.getList(media, Locale.ROOT, Trailer.class) : null;
    if (trailerList != null) {
      return trailerList;
    }

    // perform actual search
    trailerList = searchTrailer(media);
    Settings.LOGGER.log(Level.INFO, String.format("'%s' returns %d images for '%s' in", getName(), trailerList.size(), media));

    // cache results and return
    return (cache != null) ? cache.putList(media, Locale.ROOT, Trailer.class, trailerList) : trailerList;
  }

  protected abstract List<Trailer> searchTrailer(Movie movie) throws Exception;

  public abstract TrailerInfo fetchTrailerInfo(Trailer trailercc) throws Exception;

}
