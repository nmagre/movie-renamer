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
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.free.movierenamer.info.EpisodeInfo;
import fr.free.movierenamer.info.TvShowInfo;
import fr.free.movierenamer.searchinfo.TvShow;
import fr.free.movierenamer.utils.CacheObject;

/**
 * Class TvShowScrapper
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class TvShowScrapper extends MediaScrapper<TvShow, TvShowInfo> {

  protected TvShowScrapper(Locale defaultLocale) {
    super(defaultLocale);
  }

  protected abstract List<EpisodeInfo> fetchEpisodesInfoList(TvShow tvShow, Locale locale) throws Exception;

  public final List<EpisodeInfo> getEpisodesInfoList(TvShow tvShow) throws Exception {
    return getEpisodesInfoList(tvShow, getLocale());
  }

  protected final List<EpisodeInfo> getEpisodesInfoList(TvShow tvShow, Locale locale) throws Exception {
    Logger.getLogger(SearchScrapper.class.getName()).log(Level.INFO, String.format("Use '%s' to get episode info list for '%s' in '%s'", getName(), tvShow, locale.getDisplayLanguage(Locale.ENGLISH)));
    CacheObject cache = getCache();
    List<EpisodeInfo> episodes = (cache != null) ? cache.getList(tvShow, locale, EpisodeInfo.class) : null;
    if (episodes != null) {
      return episodes;
    }

    // perform actual search
    episodes = fetchEpisodesInfoList(tvShow, locale);
    Logger.getLogger(SearchScrapper.class.getName()).log(Level.INFO, String.format("'%s' returns %d episode info for '%s' in '%s'", getName(), episodes.size(), tvShow, locale.getDisplayLanguage(Locale.ENGLISH)));

    // cache results and return
    return (cache != null) ? cache.putList(tvShow, locale, EpisodeInfo.class, episodes) : episodes;
  }

  // public TvShowInfo getTvShowInfoByID(int id, Locale locale) throws Exception;

  // public TvShowInfo getSeriesInfoByIMDBID(int imdbid, Locale locale) throws Exception;

}
