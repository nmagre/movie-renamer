/*
 * Copyright (C) 2013 Nicolas Magré
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
package fr.free.movierenamer.ui.bean;

import java.io.Serializable;
import java.net.URI;

/**
 * Class UIUpdateFile
 *
 * @author Nicolas Magré
 */
public class UIUpdateFile implements Serializable {

  private final URI url;
  private final String path;
  private final String md5;

  public UIUpdateFile(URI url, String path, String md5) {
    this.url = url;
    this.path = path;
    this.md5 = md5;
  }

  public URI getUrl() {
    return url;
  }

  public String getPath() {
    return path;
  }

  public String getMd5() {
    return md5;
  }
}
