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
import javax.imageio.ImageIO;

/**
 * Class Cache , Files cache
 * @author Nicolas Magré
 */
public class Cache {

  //Type
  public static final int THUMB = 0;
  public static final int FANART = 1;
  public static final int ACTOR = 2;
  public static final int TMDBXML = 3;
  public static final int TVSHOWZIP = 4;
  private Settings setting;

  /**
   * Constructor arguments
   * @param setting Movie Renamer settings
   */
  public Cache(Settings setting) {
    this.setting = setting;
  }

  /**
   * Add file to cache
   * @param is File inputStream
   * @param url File url
   * @param type Cache type
   * @throws IOException
   */
  public void add(InputStream is, String url, int type) throws IOException {
    OutputStream os;
    File f = new File(getPath(type) + Utils.md5(url));
    os = new FileOutputStream(f);
    Utils.copyStream(is, os);
    os.close();
    is.close();
  }

  /**
   * Get file from cache
   * @param url File url
   * @param type Cache type
   * @return File
   */
  public File get(URL url, int type) {
    String md5Name = Utils.md5(url.toString());
    File f = new File(getPath(type) + md5Name);
    if (f.exists())
      return f;
    return null;
  }

  /**
   * Get image from cache
   * @param image Image url
   * @param type Cache type
   * @return Image
   * @throws IOException
   */
  public Image getImage(URL image, int type) throws IOException {
    String md5Name = Utils.md5(image.toString());
    File f = new File(getPath(type) + md5Name);
    if (f.exists())
      return ImageIO.read(f);
    return null;
  }

  /**
   * Get cache path
   * @param type cache type
   * @return Cache path
   */
  private String getPath(int type) {
    String path = setting.cacheDir;
    switch (type) {
      case THUMB:
        path = setting.thumbCacheDir;
        break;
      case FANART:
        path = setting.fanartCacheDir;
        break;
      case ACTOR:
        path = setting.actorCacheDir;
        break;
      case TMDBXML:
        path = setting.xmlCacheDir;
        break;
      case TVSHOWZIP:
        path = setting.tvshowZipCacheDir;
      default:
        break;
    }
    return path;
  }
}
