/*
 * movie-renamer-core
 * Copyright (C) 2014 Nicolas Magré
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

/**
 * Class Movie
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class Video extends Media {

  protected IdInfo imdbIdInfo;

  protected Video() {
    // used by serializer
    super();
  }

  public Video(IdInfo imdbIdInfo, IdInfo id, String title, String originalTitle, int year, URL thumb) {
    super(id, title, originalTitle, year, thumb);
    this.imdbIdInfo = imdbIdInfo;
  }

  public IdInfo getImdbId() {
    return imdbIdInfo;
  }

  public void setImdbId(IdInfo imdbIdInfo) {
    this.imdbIdInfo = imdbIdInfo;
  }
}
