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

import fr.free.movierenamer.info.IdInfo;
import java.net.URL;
import java.util.Arrays;

/**
 * Class Movie
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class Movie extends Media {

  private static final long serialVersionUID = 1L;
  protected IdInfo id;

  protected Movie() {
    // used by serializer
  }

  // public Movie(Movie obj) {
  // this(obj.name, obj.year, obj.imdbId, obj.movieId);
  // }

  public Movie(IdInfo id, String name, URL thumb, int year) {
    super(id, name, thumb, year, null);
    this.id = id;
  }

  public IdInfo getId() {
    return id;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Movie) {
      Movie other = (Movie) object;
      if (id.equals(other.getMediaId())) {
        return true;
      }

      //return name.equalsIgnoreCase(other.name);
    }

    return false;
  }

  // @Override
  // public Movie clone() {
  // return new Movie(this);
  // }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] {
        name.toLowerCase(), year
    });
  }

  @Override
  public String toString() {
    if (id.getId() > 0) {
      return super.toString() + String.format(" (%s:%d)", id.getIdType().name(), id.getId());
    }
    return super.toString();
  }
}
