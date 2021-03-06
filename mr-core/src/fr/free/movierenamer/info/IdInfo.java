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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.free.movierenamer.info;

import fr.free.movierenamer.utils.ScraperUtils.AvailableApiIds;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class IdInfo
 *
 * @author Nicolas Magré
 */
public class IdInfo extends Info {

  private static final long serialVersionUID = 1L;
  private int id;
  private AvailableApiIds idType;
  private String longId;

  public IdInfo(final int id, final AvailableApiIds idType) {
    this(id, null, idType);
  }

  public IdInfo(final int id, final String longId, final AvailableApiIds idType) {
    this.id = id;
    this.longId = longId;
    this.idType = idType;
  }

  public int getId() {
    return id;
  }

  public AvailableApiIds getIdType() {
    return idType;
  }

  public String getLongId() {
    return longId;
  }

  public URL getLink() {
    try {
      return new URL("http", String.format(idType.getLink(), toString()), "");
    } catch (MalformedURLException ex) {
      Logger.getLogger(IdInfo.class.getName()).log(Level.SEVERE, null, ex);
    }

    return null;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof IdInfo) {
      final IdInfo idinfo = (IdInfo) obj;
      if (idinfo.getId() == id && idinfo.getIdType() == idType) {
        return true;
      }
    }

    return false;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 53 * hash + this.id;
    hash = 53 * hash + (this.idType != null ? this.idType.hashCode() : 0);
    return hash;
  }

  @Override
  public String toString() {
    if (idType.equals(AvailableApiIds.IMDB)) {
      return idType.getPrefix() + String.format("%07d", id);
    }
    return idType.getPrefix() + id;
  }
}
