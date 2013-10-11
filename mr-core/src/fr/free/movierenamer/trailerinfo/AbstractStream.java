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
package fr.free.movierenamer.trailerinfo;

import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.CacheObject;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

/**
 * Class AbstractStream
 *
 * @author Nicolas Magré
 */
public abstract class AbstractStream {

  public static enum Quality {

    LD,
    SD,
    HD
  }

  public URL getLink(URL url) {
    return getLink(url, Quality.HD);
  }

  public URL getLink(URL url, Quality quality) {
    Map<Quality, URL> links = null;

    Settings.LOGGER.log(Level.INFO, String.format("Use '%s' to get stream for '%s'", getName(), url));
    CacheObject cache = getCache();

    //links = (cache != null) ? cache.getData(url, Locale.ENGLISH, EnumMap.class) : null;
    if (links != null) {
      return getLink(links, quality);
    }

    try {
      links = getLinks(url);
    } catch (Exception ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
      return null;
    }

    if (cache != null) {
      cache.putData(url, Locale.ENGLISH, links);
    }

    return getLink(links, quality);
  }

  private URL getLink(Map<Quality, URL> links, Quality quality) {
    if (links != null && links.size() > 0) {
      if (links.containsKey(quality)) {
        return links.get(quality);
      }

      int ordinal = quality.ordinal() - 1;
      while (ordinal > 0) {
        if (links.containsKey(Quality.values()[ordinal])) {
          return links.get(Quality.values()[ordinal]);
        }
        ordinal -= 1;
      }
    }

    return null;
  }

  protected abstract Map<Quality, URL> getLinks(URL url) throws Exception;

  protected abstract boolean isUrlSupported(URL url);

  public abstract String getName();

  protected abstract String getHost();

  protected String getCacheName() {
    return "short";
  }

  protected final CacheObject getCache() {
    String host = getHost();
    String cacheName = getCacheName();
    if (host != null && cacheName != null) {
      return new CacheObject(host, Cache.getCache(cacheName));
    } else {
      return null;
    }
  }

  @Override
  public final String toString() {
    return String.format("%s", getName());
  }
}
