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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.free.movierenamer.searchinfo;

import fr.free.movierenamer.info.IdInfo;
import java.net.URL;
import java.util.Arrays;

/**
 * Class Movie
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class Movie extends Video {

  private static final long serialVersionUID = 1L;

  protected Movie() {
    // used by serializer
    super();
  }

  public Movie(IdInfo imdbId, IdInfo id, String title, String originalTitle, URL thumb, int year) {
    super(imdbId, id != null ? id : imdbId, title, originalTitle, thumb, year);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Movie) {
      Movie other = (Movie) object;
      if (idInfo != null && idInfo.equals(other.getMediaId())) {
        return true;
      }

      if (imdbIdInfo != null && imdbIdInfo.equals(other.getImdbId())) {
        return true;
      }

      return title.equalsIgnoreCase(other.title);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[]{
      title, year
    });
  }

  @Override
  public String toString() {
    if (idInfo != null && idInfo.getId() > 0) {
      return super.toString() + String.format(" (%s:%d)", idInfo.getIdType().name(), idInfo.getId());
    }

    return super.toString();
  }
}
