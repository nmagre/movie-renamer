/*
 * movie-renamer-core
 * Copyright (C) 2013-2014 Nicolas Magré
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
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.searchinfo.Media.MediaType;
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

  public final List<Trailer> getTrailer(Media media) throws Exception {// TODO
    Settings.LOGGER.log(Level.INFO, String.format("Use '%s' to get trailer for '%s", getName(), media));
    CacheObject cache = getCache();
    List<Trailer> trailerList = (cache != null) ? cache.getList(media, Locale.ROOT, Trailer.class) : null;
    if (trailerList != null) {
      return trailerList;
    }

    // perform actual search
    trailerList = searchTrailer(media);
    Settings.LOGGER.log(Level.INFO, String.format("'%s' returns %d trailer for '%s'", getName(), trailerList.size(), media));

    // cache results and return
    return (cache != null) ? cache.putList(media, Locale.ROOT, Trailer.class, trailerList) : trailerList;
  }

  public final TrailerInfo getInfo(Trailer search) throws Exception {
    Settings.LOGGER.log(Level.INFO, String.format("Use '%s' to get trailer info for '%s'", getName(), search));
    CacheObject cache = getCache();

    TrailerInfo info = (cache != null) ? cache.getData(search, Locale.ENGLISH, TrailerInfo.class) : null;
    if (info != null) {
      return info;
    }

    // perform actual search
    info = fetchTrailerInfo(search);
    Settings.LOGGER.log(Level.INFO, String.format("'%s' returns '%s' as info for '%s'", getName(), info, search));

    if (info == null) {
      return info;
    }

    // cache results
    if (cache != null) {
      cache.putData(search, Locale.ENGLISH, info);
    }

    return info;
  }

  protected abstract List<Trailer> searchTrailer(Media media) throws Exception;

  protected abstract TrailerInfo fetchTrailerInfo(Trailer searchResult) throws Exception;
  
  public abstract List<MediaType> getSupportedMediaType();

}
