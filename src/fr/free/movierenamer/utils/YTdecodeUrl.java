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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Class YTdecodeUrl
 *
 * @author Nicolas Magré
 */
public abstract class YTdecodeUrl {
  // /!\ Do not use this code to download video files, this is only to view video trailer in XBMC

  public static final int HD = 0;
  public static final int MD = 1;
  public static final int SD = 2;

  /**
   * Return decoded URL or null
   *
   * @param uri Url to decode
   * @param quality Video quality
   * @return decoded URL or null
   */
  public static String getRealUrl(String uri, int quality) {
    String result = null;
    if (!isYTurl(uri)) {
      return result;
    }
    try {
      URL url = new URL(uri);
      URLConnection conn = url.openConnection();
      conn.setReadTimeout(2000);

      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line;
      ArrayList<String> videoUrl = new ArrayList<String>();
      while ((line = rd.readLine()) != null) {
        if (line.matches("(.*)generate_204(.*)")) {
          line = line.replaceFirst("img.src = '?", "");
          line = line.replaceFirst("';", "");
          line = line.replaceFirst("\\u0026", "&");
          line = line.replaceAll("\\\\", "");
          line = line.replaceAll("\\s", "");
        }

        if (line.matches("(.*)\"url_encoded_fmt_stream_map\":(.*)")) {
          HashMap<String, String> realSrcVideoUrl = new HashMap<String, String>();
          line = line.replaceFirst(".*\"url_encoded_fmt_stream_map\": \"", "").replaceFirst("\".*", "").replace("%25", "%").replace("\\u0026", "&").replace("\\", "");

          String[] urlStrings = line.split(",");
          for (String urlString : urlStrings) {
            String[] escUrlp = urlString.split("&itag=");
            escUrlp[0] = escUrlp[0].replaceFirst("url=http%3A%2F%2F", "http://");
            escUrlp[0] = escUrlp[0].replaceAll("%3F", "?").replaceAll("%2F", "/").replaceAll("%3D", "=").replaceAll("%26", "&");
            escUrlp[0] = escUrlp[0].replaceFirst("&quality=.*", "");
            realSrcVideoUrl.put(escUrlp[1], escUrlp[0]);
          }

          switch (quality) {
            case HD:
              videoUrl.add(0, realSrcVideoUrl.get("37"));
              videoUrl.add(1, realSrcVideoUrl.get("22"));
              videoUrl.add(2, realSrcVideoUrl.get("35"));
              videoUrl.add(3, realSrcVideoUrl.get("34"));
              videoUrl.add(4, realSrcVideoUrl.get("18"));
              videoUrl.add(5, realSrcVideoUrl.get("5"));
              break;
            case MD:
              videoUrl.add(0, realSrcVideoUrl.get("35"));
              videoUrl.add(1, realSrcVideoUrl.get("34"));
              videoUrl.add(2, realSrcVideoUrl.get("18"));
              videoUrl.add(3, realSrcVideoUrl.get("5"));
              break;
            case SD:
              videoUrl.add(0, realSrcVideoUrl.get("18"));
              videoUrl.add(1, realSrcVideoUrl.get("5"));
              break;
          }

          for (int x = videoUrl.size() - 1; x >= 0; x--) {
            if (videoUrl.get(x) == null) {
              videoUrl.remove(x);
            }
          }

          result = videoUrl.get(0);
        }
      }
      rd.close();
    } catch (Exception e) {
      Settings.LOGGER.log(Level.SEVERE, e.toString());
    }
    return result;
  }

  /**
   * Check if URL is an YT URL
   *
   * @param url Url
   * @return True if URL is YT URL, false otherwise
   */
  public static boolean isYTurl(String url) {
    if (url.matches("http://www.youtube.com/watch\\?v=(.*)")) {
      return true;
    }
    return false;
  }
}
