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
  protected int imdbId;

  protected Movie() {
    // used by serializer
  }

//  public Movie(Movie obj) {
//    this(obj.name, obj.year, obj.imdbId, obj.movieId);
//  }

  public Movie(int movieId, String name, URL thumb, int year, int imdbId) {
    super(movieId, name, thumb, year, null);
    this.imdbId = imdbId;
  }

  public int getImdbId() {
    return imdbId;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Movie) {
      Movie other = (Movie) object;
      if (imdbId > 0 && other.imdbId > 0) {
        return imdbId == other.imdbId;
      } else if (mediaId > 0 && other.mediaId > 0) {
        return mediaId == other.mediaId;
      }

      return name.equalsIgnoreCase(other.name);
    }

    return false;
  }

//  @Override
//  public Movie clone() {
//    return new Movie(this);
//  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] { name.toLowerCase(), year });
  }
}
