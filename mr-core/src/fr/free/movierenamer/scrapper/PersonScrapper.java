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

import fr.free.movierenamer.info.PersonInfo;
import fr.free.movierenamer.searchinfo.SearchResult;
import fr.free.movierenamer.utils.CacheObject;

/**
 * Class PersonScrapper
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class PersonScrapper<T extends SearchResult> extends Scrapper {
  
  protected PersonScrapper(Locale defaultLocale) {
    super(defaultLocale);
  }

  public final List<PersonInfo> getPersons(T search) throws Exception {
    return getPersons(search, getLocale());
  }

  protected final List<PersonInfo> getPersons(T search, Locale locale) throws Exception {
    Logger.getLogger(SearchScrapper.class.getName()).log(Level.INFO, String.format("Use '%s' to get person info list for '%s' in '%s'", getName() , search, locale.getDisplayLanguage(Locale.ENGLISH)));
    CacheObject cache = getCache();
    List<PersonInfo> personList = (cache != null) ? cache.getList(search, locale, PersonInfo.class) : null;
    if (personList != null) {
      return personList;
    }

    // perform actual search
    personList = fetchPersonsInfo(search, locale);
    Logger.getLogger(SearchScrapper.class.getName()).log(Level.INFO, String.format("'%s' returns %d person(s) info for '%s' in '%s'", getName(), personList.size(), search, locale.getDisplayLanguage(Locale.ENGLISH)));

    // cache results and return
    return (cache != null) ? cache.putList(search, locale, PersonInfo.class, personList) : personList;
  }

  protected abstract List<PersonInfo> fetchPersonsInfo(T search, Locale locale) throws Exception;

  @Override
  protected final String getCacheName() {
    return "medium";
  }
}
