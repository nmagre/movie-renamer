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

import fr.free.movierenamer.info.IdInfo;
import java.net.URL;

/**
 * Class TvShow
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class TvShow extends Video {

  private static final long serialVersionUID = 1L;

  protected TvShow() {
    // used by serializer
    super();
  }

  public TvShow(IdInfo imdbId, IdInfo id, String seriesName, URL thumb, int year) {
    super(imdbId, id, seriesName, null, thumb, year);
  }

  @Override
  public int hashCode() {
    return idInfo.getId();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof TvShow) {
      TvShow other = (TvShow) object;
      return this.idInfo.equals(other.idInfo);
    }

    return false;
  }

  @Override
  public String toString() {
    return super.toString();
  }

}
