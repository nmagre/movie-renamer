/*
 * Movie Renamer
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

import java.awt.Image;
import java.io.*;
import java.net.URL;
import java.util.Date;
import javax.imageio.ImageIO;

/**
 * Class Cache , Really simple cache
 *
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
public class Cache {

  private static final int ONEWEEK = 604800;
  private static final long TIMESTAMP = new Date().getTime() / 1000 - ONEWEEK;

  // Type
  public enum CacheType {

    THUMB,
    FANART,
    ACTOR,
    XML,
    TVSHOWZIP;
  }
  private static Cache instance;// The only instance of Cache

  /**
   * Private build for singleton fix
   *
   * @return
   */
  private static synchronized Cache newInstance() {
    if (instance == null) {
      instance = new Cache();
    }
    return instance;
  }

  /**
   * Access to the Cache instance
   *
   * @return The only instance of Cache
   */
  public static synchronized Cache getInstance() {
    if (instance == null) {
      instance = newInstance();
    }
    return instance;
  }

  private Cache() {
    // no external access
  }

  /**
   * Add file to cache
   *
   * @param url File url
   * @param type Cache type
   * @return
   * @throws IOException
   */
  public File add(URL url, Cache.CacheType type) throws IOException {
    return add(url.openStream(), url.toString(), type);
  }

  /**
   * @param http
   * @param cacheType
   * @return
   * @throws Exception
   * @throws IOException
   */
  public File add(HttpGet http, CacheType cacheType) throws IOException, Exception {
    return add(http.getInputStream(true, "ISO-8859-15"), http.getURL().toString(), cacheType);
  }

  private File add(InputStream is, String url, Cache.CacheType type) throws IOException {
    OutputStream os;
    File f = new File(getPath(type) + Utils.md5(url));
    os = new FileOutputStream(f);
    copyStream(is, os);
    os.close();
    is.close();
    return get(url, type);
  }

  /**
   * Get file from cache
   *
   * @param url File url
   * @param type Cache type
   * @return File
   */
  public File get(URL url, Cache.CacheType type) {
    return get(url.toString(), type);
  }

  private File get(String url, Cache.CacheType type) {
    String md5Name = Utils.md5(url);
    File f = new File(getPath(type) + md5Name);
    Long lastModified = f.lastModified();
    if(lastModified < TIMESTAMP) {// File is too old, need update
      return null;
    }
    if (f.exists()) {
      return f;
    }
    return null;
  }

  /**
   * Get image from cache
   *
   * @param image Image url
   * @param type Cache type
   * @return Image
   * @throws IOException
   */
  public Image getImage(URL image, Cache.CacheType type) throws IOException {
    String md5Name = Utils.md5(image.toString());
    File f = new File(getPath(type) + md5Name);
    if (f.exists()) {
      return ImageIO.read(f);
    }
    return null;
  }

  /**
   * Get cache path
   *
   * @param type cache type
   * @return Cache path
   */
  private String getPath(Cache.CacheType type) {
    String path = Settings.cacheDir;
    switch (type) {
      case THUMB:
        path = Settings.thumbCacheDir;
        break;
      case FANART:
        path = Settings.fanartCacheDir;
        break;
      case ACTOR:
        path = Settings.actorCacheDir;
        break;
      case XML:
        path = Settings.xmlCacheDir;
        break;
      case TVSHOWZIP:
        path = Settings.tvshowZipCacheDir;
        break;
      default:
        break;
    }
    return path;
  }

  /**
   * Copy stream from input source to output
   *
   * @param in Input
   * @param out Output
   * @throws IOException
   */
  private void copyStream(InputStream in, OutputStream out) throws IOException {
    final int buffer_size = 1024;
    byte[] bytes = new byte[buffer_size];
    int p;
    while ((p = in.read(bytes, 0, buffer_size)) != -1) {
      out.write(bytes, 0, p);
    }
  }
}
