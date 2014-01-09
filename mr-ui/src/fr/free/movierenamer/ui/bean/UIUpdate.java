/*
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.free.movierenamer.ui.bean;

import java.io.Serializable;

/**
 * Class UIUpdate
 *
 * @author Nicolas Magré
 */
public class UIUpdate implements Serializable {

  private final boolean updateAvailable;
  private final String updateVersion;
  private final String descen;
  private final String descfr;

  public UIUpdate(String updateVersion, boolean updateAvailable, String descen, String descfr) {
    this.updateVersion = updateVersion;
    this.descen = descen;
    this.descfr = descfr;
    this.updateAvailable = updateAvailable;
  }

  public boolean isUpdateAvailable() {
    return updateAvailable;
  }

  public String getUpdateVersion() {
    return updateVersion;
  }

  public String getDescen() {
    return descen;
  }

  public String getDescfr() {
    return descfr;
  }

  @Override
  public String toString() {
    String str = "Update available : " + updateAvailable + "\n";
    str += "Update version : " + updateVersion + "\n";
    str += "Desc en : " + descen;
    str += "Desc fr : " + descfr;
    return str;
  }
}
