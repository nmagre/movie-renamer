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
import java.util.logging.Level;

import fr.free.movierenamer.info.PersonInfo;
import fr.free.movierenamer.searchinfo.SearchResult;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.CacheObject;

/**
 * Class PersonScrapper
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class PersonScrapper<SR extends SearchResult> extends Scrapper {

  public final List<PersonInfo> getPersons(SR search) throws Exception {
    Settings.LOGGER.log(Level.INFO, String.format("Use '%s' to get person info list for '%s'", getName(), search));
    CacheObject cache = getCache();
    List<PersonInfo> personList = (cache != null) ? cache.getList(search, Locale.ROOT, PersonInfo.class) : null;
    if (personList != null) {
      return personList;
    }

    // perform actual search
    personList = fetchPersonsInfo(search);
    Settings.LOGGER.log(Level.INFO, String.format("'%s' returns %d person(s) info for '%s'", getName(), personList.size(), search));

    // cache results and return
    return (cache != null) ? cache.putList(search, Locale.ROOT, PersonInfo.class, personList) : personList;
  }

  protected abstract List<PersonInfo> fetchPersonsInfo(SR search) throws Exception;

  @Override
  protected final String getCacheName() {
    return "medium";
  }
}
