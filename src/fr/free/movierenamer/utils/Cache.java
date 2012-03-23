/******************************************************************************
 *                                                                             *
 *    Movie Renamer                                                            *
 *    Copyright (C) 2012 Magré Nicolas                                         *
 *                                                                             *
 *    Movie Renamer is free software: you can redistribute it and/or modify    *
 *    it under the terms of the GNU General Public License as published by     *
 *    the Free Software Foundation, either version 3 of the License, or        *
 *    (at your option) any later version.                                      *
 *                                                                             *
 *    This program is distributed in the hope that it will be useful,          *
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            *
 *    GNU General Public License for more details.                             *
 *                                                                             *
 *    You should have received a copy of the GNU General Public License        *
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                             *
 ******************************************************************************/
package fr.free.movierenamer.utils;

import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * Class Cache , Files cache
 * @author Nicolas Magré
 */
public class Cache {

  //Type
  public static final int thumb = 0;
  public static final int fanart = 1;
  public static final int actor = 2;
  public static final int theMovieDBXML = 3;
  private Settings setting;

  /**
   * Constructor arguments
   * @param setting Movie Renamer settings
   */
  public Cache(Settings setting) {
    this.setting = setting;
  }

  /**
   * Add file to cahe
   * @param is File inputStream
   * @param url File url
   * @param type Cache type
   * @throws IOException
   */
  public void add(InputStream is, String url, int type) throws IOException {
    OutputStream os = null;
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
      case thumb:
        path = setting.thumbCacheDir;
        break;
      case fanart:
        path = setting.fanartCacheDir;
        break;
      case actor:
        path = setting.actorCacheDir;
        break;
      case theMovieDBXML:
        path = setting.xmlCacheDir;
        break;
      default:
        break;
    }
    return path;
  }
}
