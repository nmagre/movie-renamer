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

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

/**
 * Class HttpGet , Send http request and get web page in a string
 * 
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
public class HttpGet {

  private final Settings config = Settings.getInstance();
  private URL url;
  private URL realURL;

  /**
   * Default constructor
   */
  public HttpGet() {
    url = null;
    realURL = null;
  }

  /**
   * Constructor arguments
   * 
   * @param url Page url
   */
  public HttpGet(URL url) {
    this.url = url;
  }

  /**
   * Constructor arguments
   * 
   * @param uri Page url
   * @throws MalformedURLException
   */
  public HttpGet(String uri) throws MalformedURLException {
    url = new URL(uri);
    realURL = null;
  }

  /**
   * Get url
   * 
   * @return Page url
   */
  public URL getURL() {
    if (realURL != null) {
      return realURL;
    }
    return url;
  }

  /**
   * Set url
   * 
   * @param url Page url
   */
  public void setUrl(URL url) {
    realURL = null;
    this.url = url;
  }

  /**
   * Set url
   * 
   * @param uri Page url
   * @throws MalformedURLException
   */
  public void setUrl(String uri) throws MalformedURLException {
    realURL = null;
    url = new URL(uri);
  }

  public InputStream getInputStream(boolean fakeUserAgent, String encode) throws Exception {
    if (url == null) {
      return null;
    }
    realURL = null;

    try {
      URLConnection conn;
      if (config.useProxy) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(config.proxyUrl, config.proxyPort));
        conn = url.openConnection(proxy);
      } else {
        conn = url.openConnection();
      }

      if (fakeUserAgent) {
        System.setProperty("http.agent", Utils.EMPTY);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
      }

      conn.setReadTimeout(3000);

      return conn.getInputStream();
    } catch (Exception e) {
      throw new Exception("HTTP Get " + Utils.i18n("error") + Utils.SPACE + ":" + e);
    }
  }

  /**
   * Get web page as string
   * 
   * @param fakeUserAgent Use a fake user agent
   * @param encode
   * @return Web page or null
   * @throws Exception
   */
  public String sendGetRequest(boolean fakeUserAgent, String encode) throws Exception {
    if (url == null) {
      return null;
    }
    realURL = null;
    String result = null;

    try {
      URLConnection conn;
      if (config.useProxy) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(config.proxyUrl, config.proxyPort));
        conn = url.openConnection(proxy);
      } else {
        conn = url.openConnection();
      }

      if (fakeUserAgent) {
        System.setProperty("http.agent", Utils.EMPTY);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
      }

      conn.setReadTimeout(3000);

      result = Utils.getInputStreamContent(conn.getInputStream(), encode);
    } catch (Exception e) {
      throw new Exception("HTTP Get " + Utils.i18n("error") + Utils.SPACE + ":" + e);
    }
    return result;
  }
}
