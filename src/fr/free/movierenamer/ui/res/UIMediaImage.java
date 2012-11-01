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
package fr.free.movierenamer.ui.res;

import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.utils.ImageUtils;
import java.awt.Dimension;
import javax.swing.Icon;

/**
 * Class MediaImage
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class UIMediaImage implements IIconList {
  private final Dimension searchListDim = new Dimension(45, 65);
  private final ImageInfo info;
  private final Icon icon;

  public UIMediaImage(ImageInfo info) {
    this.info = info;
    this.icon = ImageUtils.getIcon(this.info.getURI(), searchListDim, "nothumb.png");
  }

  @Override
  public Icon getIcon() {
    return icon;
  }

  public ImageInfo getInfo() {
    return info;
  }

  @Override
  public String toString() {
    return (info != null) ? info.getDescription() : null;
  }

}
