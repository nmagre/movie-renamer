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
 * Class Media
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class Media extends Hyperlink {

  private static final long serialVersionUID = 1L;
  protected int year;
  protected IdInfo idInfo;
  protected IdInfo imdbIdInfo;

  protected Media() {
    // used by serializer
  }

  public Media(IdInfo imdbIdInfo, IdInfo id, String title, String originalTitle, URL thumb, int year) {
    super(title, originalTitle, thumb);
    this.imdbIdInfo = imdbIdInfo;
    this.idInfo = id;
    this.year = year;
  }

  public IdInfo getImdbId() {
    return imdbIdInfo;
  }

  public void setImdbId(IdInfo imdbId) {
    this.imdbIdInfo = imdbId;
  }

  public IdInfo getMediaId() {
    return idInfo;
  }

  public int getYear() {
    return year;
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public String toString() {
    IdInfo id = idInfo != null ? idInfo : imdbIdInfo;
    if (year > 0) {
      if (id.getId() > 0) {
        return super.toString() + String.format(" (%04d) (id:%d)", year, id.getId());
      } else {
        return super.toString() + String.format(" (%04d)", year);
      }
    } else {
      if (id.getId() > 0) {
        return super.toString() + String.format(" (id:%d)", id.getId());
      } else {
        return super.toString();
      }
    }
  }
}
