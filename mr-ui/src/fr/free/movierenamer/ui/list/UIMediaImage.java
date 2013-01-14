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
package fr.free.movierenamer.ui.list;

import fr.free.movierenamer.info.ImageInfo;
import java.net.URL;
import javax.swing.Icon;

/**
 * Class MediaImage
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class UIMediaImage implements IIconList {

  private final ImageInfo info;
  private Icon icon;
  private final ImageInfo.ImageCategoryProperty type;

  public UIMediaImage(ImageInfo info, Icon icon) {
    this.info = info;
    this.type = info == null ? null : info.getCategory();
    this.icon = icon;
  }

  @Override
  public Icon getIcon() {
    return icon;
  }

  public ImageInfo getInfo() {
    return info;
  }

  public URL getUrl() {
    return info.getHref();
  }

  public ImageInfo.ImageCategoryProperty getType() {
    return type;
  }

  @Override
  public String toString() {
    return (info != null) ? info.getDescription() : null;
  }

  @Override
  public void setIcon(Icon icon) {
    this.icon = icon;
  }

}
