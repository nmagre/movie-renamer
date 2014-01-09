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
package fr.free.movierenamer.scrapper;

import fr.free.movierenamer.info.IdInfo;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import fr.free.movierenamer.info.SubtitleInfo;
import fr.free.movierenamer.searchinfo.Subtitle;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.CacheObject;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;

/**
 * Class SubtitleScrapper
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class SubtitleScrapper extends SearchScrapper<Subtitle> {

  protected SubtitleScrapper(AvailableLanguages... supportedLanguages) {
    super(supportedLanguages);
  }

  public final List<SubtitleInfo> getSubtitles(Subtitle subtitle) throws Exception {
    return getSubtitles(subtitle, getLanguage());
  }

  protected final List<SubtitleInfo> getSubtitles(Subtitle subtitle, AvailableLanguages language) throws Exception {
    Locale lang = language.getLocale();
    Settings.LOGGER.log(Level.INFO, String.format("Use '%s' to get subtitle info list for '%s' in '%s'", getName(), subtitle, lang.getDisplayLanguage(Locale.ENGLISH)));
    CacheObject cache = getCache();
    List<SubtitleInfo> subtitleList = (cache != null) ? cache.getList(subtitle, lang, SubtitleInfo.class) : null;
    if (subtitleList != null) {
      return subtitleList;
    }

    // perform actual search
    subtitleList = fetchSubtitlesInfo(subtitle, language);
    Settings.LOGGER.log(Level.INFO, String.format("'%s' returns %d subtitle(s) info for '%s' in '%s'", getName(), subtitleList.size(), subtitle, lang.getDisplayLanguage(Locale.ENGLISH)));

    // cache results and return
    return (cache != null) ? cache.putList(subtitle, lang, SubtitleInfo.class, subtitleList) : subtitleList;
  }

  protected abstract List<SubtitleInfo> fetchSubtitlesInfo(Subtitle subtitle, AvailableLanguages language) throws Exception;

  @Override
  public final List<Subtitle> search(String query, AvailableLanguages language) throws Exception {
    Locale lang = language.getLocale();
    Settings.LOGGER.log(Level.INFO, String.format("Use '%s' to search subtitles for '%s' in '%s'", getName(), query, lang.getDisplayLanguage(Locale.ENGLISH)));
    CacheObject cache = getCache();
    List<Subtitle> results = (cache != null) ? cache.getList(query, lang, Subtitle.class) : null;
    if (results != null) {
      return results;
    }

    // perform actual search
    results = searchSubtitles(query, language);
    Settings.LOGGER.log(Level.INFO, String.format("'%s' returns %d subtitle(s) for '%s' in '%s'", getName(), results.size(), query, lang.getDisplayLanguage(Locale.ENGLISH)));

    // cache results and return
    return (cache != null) ? cache.putList(query, lang, Subtitle.class, results) : results;
  }

  public final List<Subtitle> searchById(IdInfo id) {
    Locale lang = getLanguage().getLocale();
    Settings.LOGGER.log(Level.INFO, String.format("Use '%s' to search subtitles for '%s' in '%s'", getName(), id, lang.getDisplayLanguage(Locale.ENGLISH)));
    CacheObject cache = getCache();
    List<Subtitle> results = (cache != null) ? cache.getList(id, lang, Subtitle.class) : null;
    if (results != null) {
      return results;
    }

    // perform actual search
    results = searchSubtitlesById(id, getLanguage());
    Settings.LOGGER.log(Level.INFO, String.format("'%s' returns %d subtitle(s) for '%s' in '%s'", getName(), results.size(), id, lang.getDisplayLanguage(Locale.ENGLISH)));

    // cache results and return
    return (cache != null) ? cache.putList(id, lang, Subtitle.class, results) : results;
  }

  protected abstract List<Subtitle> searchSubtitles(String query, AvailableLanguages language) throws Exception;

  protected abstract List<Subtitle> searchSubtitlesById(IdInfo id, AvailableLanguages language);
}
