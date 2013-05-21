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
package fr.free.movierenamer.utils;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.free.movierenamer.scrapper.SearchScrapper;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.Cache.CacheKey;

/**
 * Class CacheObject
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class CacheObject {
  private final String id;
  private final Cache cache;

  public CacheObject(String id, Cache cache) {
    this.id = id;
    this.cache = cache;
  }

  protected String normalize(String query) {
    return query == null ? null : query.trim().toLowerCase();
  }

  public synchronized <T> List<T> putList(Object key, Locale language, Class<T> type, List<T> object) {
    try {
      cache.put(new CacheKey(id, key, type, language), object);
    } catch (Exception e) {
      Settings.LOGGER.log(Level.WARNING, e.getMessage());
    }
    return object;
  }

  public synchronized <T> T putData(Object key, Locale language, T object) {
    try {
      cache.put(new CacheKey(id, key, language), object);
    } catch (Exception e) {
      Settings.LOGGER.log(Level.WARNING, e.getMessage());
    }
    return object;
  }

  @SuppressWarnings("unchecked")
  public <T> List<T> getList(Object key, Locale language, Class<T> type) {
    // return getData(key, locale, (Class<List<T>>) (Class<?>) List.class);
    try {
      List<T> value = cache.get(new CacheKey(id, key, type, language), List.class);
      if (value != null) {
        return value;
      }
    } catch (Exception e) {
      Settings.LOGGER.log(Level.WARNING, e.getMessage(), e);
    }

    return null;
  }

  public <T> T getData(Object key, Locale language, Class<T> type) {
    try {
      T value = cache.get(new CacheKey(id, key, language), type);
      if (value != null) {
        return value;
      }
    } catch (Exception e) {
      Settings.LOGGER.log(Level.WARNING, e.getMessage(), e);
    }

    return null;
  }
}
