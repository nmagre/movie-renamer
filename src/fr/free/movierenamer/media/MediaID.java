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
package fr.free.movierenamer.media;

/**
 * Class MediaID
 * 
 * @author Nicolas Magré
 */
public class MediaID {

  public enum MediaIdType {
    IMDBID,
    TMDBID,
    TVDBID,
    ALLOCINEID,
    ALLOCINETVID,
    ALLOCINESEASONID,
    ALLOCINEEPISODEID,
    TVRAGETVID;
  }

  private MediaIdType type;
  private String providerId;

  public MediaID(String id, MediaIdType type) {
    this.providerId = id;
    this.type = type;
  }

  /**
   * Get ID
   * 
   * @return ID
   */
  public String getID() {
    return providerId;
  }

  /**
   * Get type
   * 
   * @return Type
   */
  public MediaIdType getType() {
    return type;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof MediaID) {
      MediaID mID = (MediaID) obj;
      return mID.type == type && mID.providerId.equals(providerId);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 59 * hash + this.type.ordinal();
    hash = 59 * hash + (this.providerId != null ? this.providerId.hashCode() : 0);
    return hash;
  }

  @Override
  public String toString() {
    return providerId;
  }
}
