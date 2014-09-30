/*
 * movie-renamer-core
 * Copyright (C) 2012-2014 Nicolas Magré
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

import fr.free.movierenamer.utils.ObjectUtils;
import java.net.URL;
import java.util.Arrays;

/**
 * Class Hyperlink : A search result
 *
 * @author Simon QUÉMÉNEUR
 * @author Nicolas Magré
 */
public abstract class Hyperlink extends SearchResult {

  private static final long serialVersionUID = 1L;
  protected URL url;

  protected Hyperlink() {
    // used by serializer
  }

  /**
   * Constructor
   *
   * @param name Result name
   * @param originalName Result original name
   * @param year Year
   * @param url Result url
   */
  public Hyperlink(String name, String originalName, int year, URL url) {
    super(name, originalName, year);
    this.url = url;
  }

  /**
   * Get hyperlink URL
   *
   * @return Hyperlink URL or null
   */
  public URL getURL() {
    return url;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Hyperlink) {
      Hyperlink other = (Hyperlink) object;
      return ObjectUtils.compare(name, other.getName()) && ObjectUtils.compare(url, other.getURL())
              && ObjectUtils.compare(originalName, other.getOriginalName());
    }

    return false;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[]{
      name, (url == null) ? null : url.toString()
    });
  }

  @Override
  public String toString() {
    if (url != null) {
      return super.toString() + String.format(" (url:%s)", url.toExternalForm());
    }

    return super.toString();
  }
}
