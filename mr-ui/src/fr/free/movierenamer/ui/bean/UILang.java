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

import fr.free.movierenamer.utils.LocaleUtils.Language;
import java.util.Locale;
import javax.swing.Icon;

/**
 * Class UILang
 *
 * @author Nicolas Magré
 */
public class UILang implements IIconList {

  private Icon icon;
  private final Language lang;

  public UILang(Language lang, Icon icon) {
    this.lang = lang;
    this.icon = icon;
  }

  public Language getLanguage() {
    return lang;
  }

  public Locale getLang() {
    return lang != null ? lang.getLocale() : Locale.ROOT;
  }

  @Override
  public Icon getIcon() {
    return icon;
  }

  @Override
  public String toString() {
    return lang != null ? lang.getDisplayName() : "????";
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof UILang)) {
      return false;
    }

    UILang imgLang = (UILang) obj;
    if (imgLang.getIcon().equals(this.icon) && imgLang.getLang().equals(this.getLang())) {
      return true;
    }

    return false;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 59 * hash + (this.icon != null ? this.icon.hashCode() : 0);
    hash = 59 * hash + (this.lang != null ? this.lang.hashCode() : 0);
    return hash;
  }

  @Override
  public String getName() {
    return lang.getLocale().getLanguage();
  }
}
