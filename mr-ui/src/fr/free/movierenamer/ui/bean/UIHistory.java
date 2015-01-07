/*
 * Movie Renamer
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

import javax.swing.Icon;

/**
 * Class UIHistory
 *
 * @author Nicolas Magré
 */
public class UIHistory {

  private final Icon icon;
  private final String newStr;
  private final String oldStr;

  public UIHistory(Icon icon, String newStr, String oldStr) {
    this.icon = icon;
    this.newStr = newStr;
    this.oldStr = oldStr;
  }

  /**
   * @return the icon
   */
  public Icon getIcon() {
    return icon;
  }

  /**
   * @return the newStr
   */
  public String getNewStr() {
    return newStr;
  }

  /**
   * @return the oldStr
   */
  public String getOldStr() {
    return oldStr;
  }

}
