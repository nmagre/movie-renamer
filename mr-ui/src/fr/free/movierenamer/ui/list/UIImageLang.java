/*
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
package fr.free.movierenamer.ui.list;

import fr.free.movierenamer.info.ImageInfo.ImageSize;
import fr.free.movierenamer.settings.Settings;
import java.net.URI;
import java.util.Locale;
import javax.swing.Icon;

/**
 * Class UIImageLang
 * @author Nicolas Magré
 */
public class UIImageLang implements IIconList {

  private Icon icon;
  private final Locale lang;

  public UIImageLang(Locale lang, Icon icon) {
    this.lang = lang;
    this.icon = icon;
  }

  public Locale getLang() {
    return lang;
  }

  @Override
  public Icon getIcon() {
    return icon;
  }

  @Override
  public String toString() {
    return lang.getDisplayCountry(Settings.getInstance().getAppLanguage());
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof UIImageLang)) {
      return false;
    }

    UIImageLang imgLang = (UIImageLang) obj;
    if (imgLang.getIcon().equals(this.icon) && imgLang.getLang().equals(this.lang)) {
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
  public void setIcon(Icon icon) {
    //
  }

  @Override
  public URI getUri(ImageSize size) {
    return null;
  }
}