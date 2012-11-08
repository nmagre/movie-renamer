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

import fr.free.movierenamer.info.SubtitleInfo;
import fr.free.movierenamer.searchinfo.Subtitle;
import fr.free.movierenamer.utils.CacheObject;

/**
 * Class SubtitleScrapper
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class SubtitleScrapper extends SearchScrapper<Subtitle> {
  
  protected SubtitleScrapper(Locale defaultLocale) {
    super(defaultLocale);
  }

  public final List<SubtitleInfo> getSubtitles(Subtitle subtitle) throws Exception {
    return getSubtitles(subtitle, getLocale());
  }

  protected final List<SubtitleInfo> getSubtitles(Subtitle subtitle, Locale locale) throws Exception {
    CacheObject cache = getCache();
    List<SubtitleInfo> subtitleList = (cache != null) ? cache.getList(subtitle, locale, SubtitleInfo.class) : null;
    if (subtitleList != null) {
      return subtitleList;
    }

    // perform actual search
    subtitleList = fetchSubtitlesInfo(subtitle, locale);

    // cache results and return
    return (cache != null) ? cache.putList(subtitle, locale, SubtitleInfo.class, subtitleList) : subtitleList;
  }

  protected abstract List<SubtitleInfo> fetchSubtitlesInfo(Subtitle subtitle, Locale locale) throws Exception;

  @Override
  public final List<Subtitle> search(String query, Locale locale) throws Exception {
    CacheObject cache = getCache();
    List<Subtitle> results = (cache != null) ? cache.getList(query, locale, Subtitle.class) : null;
    if (results != null) {
      return results;
    }

    // perform actual search
    results = searchSubtitles(query, locale);

    // cache results and return
    return (cache != null) ? cache.putList(query, locale, Subtitle.class, results) : results;
  }

  protected abstract List<Subtitle> searchSubtitles(String query, Locale locale) throws Exception;
  
}
