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

import java.util.List;
import java.util.Locale;

import fr.free.movierenamer.searchinfo.SearchResult;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;

/**
 * Class SearchScrapper
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class SearchScrapper<SR extends SearchResult> extends Scrapper {

  private final List<AvailableLanguages> supportedLanguages;
  private Locale language;

  protected SearchScrapper(AvailableLanguages... supportedLanguages) {
    if (supportedLanguages == null || supportedLanguages.length == 0 || supportedLanguages[0] == null) {
      throw new NullPointerException("defaultLanguage must not be null");
    } else {
      this.supportedLanguages = Arrays.asList(supportedLanguages);
    }
  }

  protected abstract Locale getDefaultLanguage();

  public final List<AvailableLanguages> getSupportedLanguages() {
    return Collections.unmodifiableList(supportedLanguages);
  }

  public final boolean hasSupportedLanguage(AvailableLanguages language) {
    return supportedLanguages.contains(language);
  }

  protected final Locale getLanguage() {
    if (language != null) {
      return language;
    } else {
      return getDefaultLanguage();
    }
  }

  public final void setLanguage(Locale language) {// FIXME

    this.language = language;
//    for (LocaleUtils.Language lang : supportedLanguages) {
//      //if (lang.getLocale().getLanguage().equals(language.getLanguage())) {
//      this.language = lang.getLocale();
//      break;
//      //}
//    }

    if (this.language == null) {
      this.language = getDefaultLanguage();
      Settings.LOGGER.log(Level.WARNING, String.format("Try to set Language (%s) to scrapper (%s) which has no support for it ! : ", language, getName()));
    }
  }

  public final List<SR> search(String query) throws Exception {
    return search(query, getLanguage());
  }

  protected abstract List<SR> search(String query, Locale language) throws Exception;

  @Override
  protected final String getCacheName() {
    return "short";
  }
}
