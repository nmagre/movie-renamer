/*
 * Movie Renamer
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
package fr.free.movierenamer.ui.bean;

import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Class UIEnum
 *
 * @author Nicolas Magré
 */
public class UIEnum implements IIconList {

  private Enum<?> enumValue;
  private Icon icon;
  private String imgFolder;

  public UIEnum(Enum<?> enumValue, String imgFolder) {
    this.enumValue = enumValue;
    this.imgFolder = imgFolder;
  }

  public Enum<?> getValue() {
    return enumValue;
  }

  @Override
  public Icon getIcon() {
    if (icon == null && imgFolder != null) {
      icon = new ImageIcon(ImageUtils.getImageFromJAR(String.format(imgFolder + "/%s.png", enumValue.name().toLowerCase())));
    }
    return icon;
  }

  @Override
  public String toString() {
    return UIUtils.i18n.getLanguage("settings." + enumValue.name().toLowerCase(), false);
  }

  @Override
  public String getName() {
    return enumValue.name();
  }
}
