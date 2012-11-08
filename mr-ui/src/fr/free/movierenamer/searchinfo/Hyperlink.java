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
package fr.free.movierenamer.searchinfo;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

/**
 * Class Hyperlink
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class Hyperlink extends SearchResult {
  private static final long serialVersionUID = 1L;
  protected URL url;

  protected Hyperlink() {
    // used by serializer
  }

  public Hyperlink(String name, URL url, SearchResultType type) {
    super(name, type);
    this.url = url;
  }

  public URL getURL() {
    return url;
  }

  public URI getURI() {
    try {
      return (url == null) ? null : url.toURI();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Hyperlink) {
      Hyperlink other = (Hyperlink) object;
      return name.equals(name) && ((url == null) ? "" : url.toString()).toString().equals((other.url == null) ? "" : other.url.toString());
    }

    return false;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] { name, (url == null) ? null : url.toString() });
  }

//  @Override
//  public Hyperlink clone() {
//    return new Hyperlink(this);
//  }
}
