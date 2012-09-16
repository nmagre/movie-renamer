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

import fr.free.movierenamer.parser.MrParser;
import fr.free.movierenamer.parser.XMLParser;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.HttpGet;
import fr.free.movierenamer.utils.Settings;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

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
    
    T processedFile = processFile(file);
    return processedFile;
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

  protected abstract String getUri() throws Exception;/* {
    return uri;
  }*/

  protected abstract MrParser<T> getParser() throws Exception;/* {
    return parser;
  }*/
  
  protected T processFile(File xmlFile) throws Exception {
    T object = null;

    if (xmlFile == null) {
      firePropertyChange("closeLoadingDial", "scrapperInfoFailed");
      return null;
    }
    
    try {
      // Parse XML
      MrParser<T> parser = getParser();
      XMLParser<T> xmp = new XMLParser<T>(xmlFile.getAbsolutePath(), getInnerFileName());
      parser.setOriginalFile(xmlFile);
      xmp.setParser(parser);
      object = xmp.parseXml();

    } catch (IOException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (ParserConfigurationException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (SAXException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }

    if (object == null) {
      firePropertyChange("closeLoadingDial", "scrapperInfoFailed");
      return null;
    }

//    if (!((MI<U>) object).getTrailer().equals("")) {
//      String trailer = YTdecodeUrl.getRealUrl(((MI<U>) object).getTrailer(), YTdecodeUrl.HD);// FIXME Il n'y a pas que YT dans la vie ;)
//      if (trailer != null) {
//        ((MI<U>) object).setTrailer(trailer);
//      }
//    }

    setProgress(100);
    return object;
  }
  
  protected String getInnerFileName() {
    return null;
  }
  
}
