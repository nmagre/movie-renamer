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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.free.movierenamer.scrapper;

import java.util.List;

import fr.free.movierenamer.searchinfo.SearchResult;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import java.util.Arrays;
import java.util.Collections;

/**
 * Class SearchScrapper
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class SearchScrapper<SR extends SearchResult> extends Scrapper {

  private final List<AvailableLanguages> supportedLanguages;
  private AvailableLanguages language;

  protected SearchScrapper(AvailableLanguages... supportedLanguages) {
    if (supportedLanguages == null || supportedLanguages.length == 0 || supportedLanguages[0] == null) {
      throw new NullPointerException("defaultLanguage must not be null");
    } else {
      this.supportedLanguages = Arrays.asList(supportedLanguages);
    }
  }

  protected abstract AvailableLanguages getDefaultLanguage();

  public final List<AvailableLanguages> getSupportedLanguages() {
    return Collections.unmodifiableList(supportedLanguages);
  }

  public final boolean hasSupportedLanguage(AvailableLanguages language) {
    return supportedLanguages.contains(language);
  }

  protected final AvailableLanguages getLanguage() {
    if (language != null) {
      return language;
    }

    return Settings.getInstance().getSearchScrapperLang();
  }

  public final void setLanguage(AvailableLanguages language) {

    this.language = language;

    if (this.language == null) {
      this.language = getDefaultLanguage();
    }
  }

  public final List<SR> search(String query) throws Exception {
    return search(query, getLanguage());
  }

  protected abstract List<SR> search(String query, AvailableLanguages language) throws Exception;

  @Override
  protected final String getCacheName() {
    return "short";
  }
}
