/*
 * movie-renamer-core
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

import fr.free.movierenamer.utils.StringUtils;
import java.io.Serializable;

/**
 * Class SearchResult
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class SearchResult implements Serializable {

  public static enum SearchResultType {
    NONE,
    EXACT,
    POPULAR,
    PARTIAL,
    APPROXIMATE;

    @Override
    public String toString() {
      // FIXME use internationalization
      return StringUtils.capitalizedLetter(super.toString(), true);
    }
  }

  private static final long serialVersionUID = 1L;
  protected String title;
  protected SearchResultType type;

  protected SearchResult() {
    // used by serializer
  }

  public SearchResult(String title, SearchResultType type) {
    this.title = (title == null) ? "" : title;
    this.type = (type == null) ? SearchResultType.NONE : type;
  }

  public String getName() {
    return title;
  }

  public String getOriginalTitle() {
    return title;
  }

  public SearchResultType getType() {
    return type;
  }

  @Override
  public String toString() {
    if (type == null || type == SearchResultType.NONE) {
      return title;
    }

    return String.format("%s : %s)", title, type);
  }
}
