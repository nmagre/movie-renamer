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
 */
public abstract class HttpWorker<T> extends Worker<T> {
  private static final int RETRY = 3;
  private URL realUrl;

  public HttpWorker(PropertyChangeSupport errorSupport) {
    super(errorSupport);
  }

  // FIXME Les infos change avec le temps, exemple une série peut evolué très vite sur une API (certaines gère les update),
  // Donc un "refresh" (force) et une vérification de l'ancièneté(par exemple une semaine => plus valide) du cache me parraisse indispensable
  // Ne concerne que les XML/ZIP
  @Override
  protected final T executeInBackground() throws Exception {
    Cache.CacheType cacheType = getCacheType();
    String uri = getSearchUri();
    realUrl = new URL(uri);
    File file = Cache.getInstance().get(realUrl, cacheType); 
    if (file != null) {
      Settings.LOGGER.log(Level.FINE, "Use of cache file for {0}", realUrl);
    } else {
      for (int i = 0; i < RETRY; i++) {
        HttpGet http;
        try {
          http = new HttpGet(uri);
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

    return proccessFile(file);
  }

  protected Cache.CacheType getCacheType() {
    return Cache.CacheType.XML;
  }
  
  /**
   * @return the url
   */
  protected URL getUrl() {
    return realUrl;
  }

  /*
   * 
   * MovieInfo movieInfo = null; MovieImage movieImage = null; try { // String uri = config.tmdbAPIMovieInf + new String(DatatypeConverter.parseBase64Binary(config.xurlMdb)) + "/" + id.getID(); // if
   * (config.movieScrapperFR) { // uri = uri.replace("/en/", "/fr/"); // } // // URL url = new URL(uri); // File xmlFile = Cache.getInstance().get(url, Cache.CacheType.XML); // if (xmlFile == null) {
   * // for (int i = 0; i < RETRY; i++) { // InputStream in; // try { // in = url.openStream(); // Cache.getInstance().add(in, url.toString(), Cache.CacheType.XML); // xmlFile =
   * Cache.getInstance().get(url, Cache.CacheType.XML); // break; // } catch (Exception e) {// Don't care about exception, "xmlFile" will be null // Settings.LOGGER.log(Level.SEVERE, null, e); // try
   * { // Thread.sleep(300); // } catch (InterruptedException ex) { // Settings.LOGGER.log(Level.SEVERE, null, ex); // } // } // } // } File xmlFile = loadXmlFile();
   * 
   * if (xmlFile == null) { firePropertyChange("closeLoadingDial", "httpFailed"); return null; }
   * 
   * // Parse TMDB API XML XMLParser<MovieInfo> xmp = new XMLParser<MovieInfo>(xmlFile.getAbsolutePath()); xmp.setParser(getInfoParser()); movieInfo = xmp.parseXml();
   * 
   * XMLParser<MovieImage> xmmp = new XMLParser<MovieImage>(xmlFile.getAbsolutePath()); xmmp.setParser((getImageParser()); movieImage = xmmp.parseXml();
   * 
   * } catch (IOException ex) { Settings.LOGGER.log(Level.SEVERE, null, ex); } catch (InterruptedException ex) { Settings.LOGGER.log(Level.SEVERE, null, ex); } catch (ParserConfigurationException ex)
   * { Settings.LOGGER.log(Level.SEVERE, null, ex); } catch (SAXException ex) { Settings.LOGGER.log(Level.SEVERE, null, ex); }
   * 
   * if (movieInfo == null) { firePropertyChange("closeLoadingDial", "scrapperInfoFailed"); return null; }
   * 
   * if (movieImage == null) { firePropertyChange("closeLoadingDial", "scrapperInfoFailed"); return null; }
   * 
   * movieInfo.setImages(movieImage); if (!movieInfo.getTrailer().equals("")) { String trailer = YTdecodeUrl.getRealUrl(movieInfo.getTrailer(), YTdecodeUrl.HD); if (trailer != null) {
   * movieInfo.setTrailer(trailer); } }
   * 
   * setProgress(100); return movieInfo;
   * 
   * 
   * 
   * 
   * T workerResult = null; try { File xmlFile = loadXmlFile();
   * 
   * if (xmlFile == null) { firePropertyChange("closeLoadingDial", "httpFailed"); return null; }
   * 
   * setProgress(30);
   * 
   * workerResult = parseXml(xmlFile);
   * 
   * } catch (IOException ex) { Settings.LOGGER.log(Level.SEVERE, null, ex); } catch (InterruptedException ex) { Settings.LOGGER.log(Level.SEVERE, null, ex); } catch (ParserConfigurationException ex)
   * { Settings.LOGGER.log(Level.SEVERE, null, ex); } catch (SAXException ex) { Settings.LOGGER.log(Level.SEVERE, null, ex); }
   * 
   * if (workerResult == null) { firePropertyChange("closeLoadingDial", "scrapperSearchFailed"); return null; }
   * 
   * setProgress(50);
   * 
   * workerResult = loadImages(workerResult); setProgress(100);
   * 
   * // FIXME Settings.LOGGER.log(Level.INFO, "found : {0} Media", searchResult.size());
   * 
   * return workerResult; }
   */
  protected abstract T proccessFile(File xmlFile) throws Exception;

  // /**
  // * Load corresponding images
  // *
  // * @param workerResult
  // * @return
  // * @throws Exception
  // */
  // protected abstract T loadImages(T workerResult) throws Exception;

  // private File loadXmlFile() throws Exception {
  // String uri = getSearchUri();
  // URL url = new URL(uri);
  // File xmlFile = Cache.getInstance().get(url, Cache.CacheType.XML);
  // if (xmlFile != null) {
  // Settings.LOGGER.log(Level.FINE, "Use of cache file for {0}", url);
  // } else {
  // for (int i = 0; i < RETRY; i++) {
  // InputStream in;
  // HttpGet http;
  // try {
  // http = new HttpGet(uri);
  // in = http.getInputStream(true, "ISO-8859-15");// url.openStream();
  // Cache.getInstance().add(in, url.toString(), Cache.CacheType.XML);
  // xmlFile = Cache.getInstance().get(url, Cache.CacheType.XML);
  // break;
  // } catch (Exception e) {// Don't care about exception, "xmlFile" will be null
  // Settings.LOGGER.log(Level.SEVERE, null, e);
  // try {
  // Thread.sleep(300);
  // } catch (InterruptedException ex) {
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // }
  // }
  // }
  // }
  //
  // return xmlFile;
  // }

  // private T parseXml(File xmlFile) throws Exception {
  // // Parse XML
  // XMLParser<T> xmp = new XMLParser<T>(xmlFile.getAbsolutePath());
  // MrParser<T> parser = getParser();
  // parser.setOriginalFile(xmlFile);
  // xmp.setParser(parser);
  // return xmp.parseXml();
  // }

  /**
   * @return The complete String URI where to look for data
   * @throws Exception
   */
  protected abstract String getSearchUri() throws Exception;

}
