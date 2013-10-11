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
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Class Stream
 *
 * @author Nicolas Magré
 */
public final class Stream {

  private static final Map<Class<? extends AbstractStream>, AbstractStream> map = new LinkedHashMap<Class<? extends AbstractStream>, AbstractStream>(0);

  static {
    getStreamer(Dailymotion.class);
    getStreamer(Youtube.class);
  }

  public static URL getStream(URL url) {
    for(AbstractStream absStream : map.values()) {
      if(absStream.isUrlSupported(url)) {
        return absStream.getLink(url);
      }
    }

    return null;
  }
  
  public static URL getStream(URL url, AbstractStream.Quality quality) {
    for(AbstractStream absStream : map.values()) {
      if(absStream.isUrlSupported(url)) {
        return absStream.getLink(url, quality);
      }
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  public static <T extends AbstractStream> T getStreamer(Class<T> streamClass) {
    T streamer = null;
    synchronized (map) {
      if (map.containsKey(streamClass)) {
        streamer = (T) map.get(streamClass);
      } else {
        int modifier = streamClass.getModifiers();
        if (!Modifier.isAbstract(modifier) && !Modifier.isInterface(modifier)) {
          try {
            streamer = streamClass.newInstance();
          } catch (InstantiationException e) {
            Settings.LOGGER.log(Level.SEVERE, null, e);
          } catch (IllegalAccessException e) {
            Settings.LOGGER.log(Level.SEVERE, null, e);
          }
        }
        map.put(streamClass, streamer);
      }
    }
    return streamer;
  }
}
