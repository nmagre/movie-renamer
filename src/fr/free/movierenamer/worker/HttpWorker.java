 /*
 * movie-renamer
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
package fr.free.movierenamer.worker;

import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.HttpGet;
import fr.free.movierenamer.utils.Settings;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.net.URL;
import java.util.logging.Level;

/**
 * Class HttpWorker
 *
 * @param <T>
 * @author QUÉMÉNEUR Simon
 * @author Nicolas Magré
 */
public abstract class HttpWorker<T> extends Worker<T> {

  private static final int RETRY = 3;
  private URL realUrl;

  public HttpWorker(PropertyChangeSupport errorSupport) {
    super(errorSupport);
  }

  @Override
  public final T executeInBackground() throws Exception {
    Cache.CacheType cacheType = getCacheType();
    realUrl = new URL(getUri());
    File file = Cache.getInstance().get(realUrl, cacheType);
    if (file != null) {
      Settings.LOGGER.log(Level.FINE, "Use of cache file for {0}", realUrl);
    } else {
      for (int i = 0; i < RETRY; i++) {
        HttpGet http;
        try {
          http = new HttpGet(getUri());
          file = Cache.getInstance().add(http, cacheType);
          realUrl = http.getURL();
          break;
        } catch (Exception e) {// Don't care about exception, "file" will be null
          Settings.LOGGER.log(Level.SEVERE, null, e);
          try {
            Thread.sleep(300);
          } catch (InterruptedException ex) {
            Settings.LOGGER.log(Level.SEVERE, null, ex);
          }
        }
      }
    }
    
    return fileAnalysis(file);
  }

  protected Cache.CacheType getCacheType() {
    return Cache.CacheType.XML;
  }

  /**
   * @return the url
   */
  protected final URL getUrl() {
    return realUrl;
  }

  protected abstract String getUri() throws Exception;
  
  protected abstract T fileAnalysis(final File file) throws Exception;

  
  
}
