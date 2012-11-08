/*
 * Movie Renamer
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

/**
 * Class Media
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class Media extends Hyperlink {

  private static final long serialVersionUID = 1L;
  protected int mediaId;

  protected Media() {
    // used by serializer
  }

  public Media(int mediaId, String name, URL thumb, SearchResultType type) {
    super(name, thumb, type);
    this.mediaId = mediaId;
  }

  public int getMediaId() {
    return mediaId;
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

}
