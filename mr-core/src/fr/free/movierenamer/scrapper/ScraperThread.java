/*
 * movie-renamer-core
 * Copyright (C) 2014 Nicolas Magré
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

import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import java.util.concurrent.Callable;

/**
 * Class UniversalScrapperThread
 *
 * @author Nicolas Magré
 */
public class ScraperThread<M extends Media, MI extends MediaInfo> implements Callable<MI> {

  private final MediaScrapper<M, MI> scraper;
  private final M result;
  private final AvailableLanguages lang;

  public ScraperThread(MediaScrapper<M, MI> scraper, M result, AvailableLanguages lang) {
    this.scraper = scraper;
    this.result = result;
    this.lang = lang;
  }

  @Override
  public MI call() throws Exception {
    MI info = scraper.getInfo(result);

    if (!scraper.hasSupportedLanguage(lang) /*&& settings.isGetOnlyLangDepInfo()*/) {
      info.unsetUnsupportedLangInfo();
    }
    return info;
  }

}
