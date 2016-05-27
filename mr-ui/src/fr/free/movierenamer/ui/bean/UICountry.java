/*
 * Movie Renamer
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
package fr.free.movierenamer.ui.bean;

import fr.free.movierenamer.ui.swing.IIconList;
import fr.free.movierenamer.ui.utils.FlagUtils;
import javax.swing.Icon;

/**
 * Class UICountry
 *
 * @author Nicolas Magré
 */
public class UICountry implements IIconList {

  private final Icon icon;
  private final String country;

  public UICountry(String country, Icon icon) {
    this.country = country;
    this.icon = icon;
  }

  @Override
  public Icon getIcon() {
    return icon != null ? icon : FlagUtils.Unknown;
  }

  @Override
  public String toString() {
    return country;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof UICountry)) {
      return false;
    }

    UICountry uicountry = (UICountry) obj;

    return uicountry.getIcon().equals(this.icon) && uicountry.getName().equals(this.getName());
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 59 * hash + (this.icon != null ? this.icon.hashCode() : 0);
    hash = 59 * hash + (this.country != null ? this.country.hashCode() : 0);
    return hash;
  }

  @Override
  public String getName() {
    return country;
  }
}
