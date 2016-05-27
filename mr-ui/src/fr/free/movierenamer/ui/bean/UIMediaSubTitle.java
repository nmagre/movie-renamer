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

import fr.free.movierenamer.ui.swing.IIconList;
import fr.free.movierenamer.mediainfo.MediaSubTitle;
import fr.free.movierenamer.ui.utils.FlagUtils;
import javax.swing.Icon;

/**
 * Class UIMediaSubTitle
 *
 * @author Nicolas Magré
 */
public class UIMediaSubTitle implements IIconList {

  private final MediaSubTitle msubtitle;
  private UILang image;

  public UIMediaSubTitle(MediaSubTitle msubtitle) {
    this.msubtitle = msubtitle;
  }

  @Override
  public Icon getIcon() {
    if (image == null) {
      image = FlagUtils.getFlagByLang(msubtitle.getLanguage().getLanguage());
    }
    return image.getIcon();
  }

  @Override
  public String toString() {
    return msubtitle.getTitle();
  }

  @Override
  public String getName() {
    return toString();
  }
}
