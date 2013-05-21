/*
 * movie-renamer-core
 * Copyright (C) 2012-2013 Nicolas Magré
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

import java.io.Serializable;

/**
 * Class SearchResult
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class SearchResult implements Serializable {

  private static final long serialVersionUID = 1L;
  protected String title;
  protected String originalTitle;

  protected SearchResult() {
    // used by serializer
  }

  public SearchResult(String title, String originalTitle) {
    this.title = title;
    this.originalTitle = (originalTitle == null) ? title : originalTitle;
  }

  public String getName() {
    return title;
  }

  public String getOriginalTitle() {
    return originalTitle;
  }

  @Override
  public String toString() {
    return title;
  }
}
