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
package fr.free.movierenamer.ui.bean;

import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.ui.utils.ImageUtils;
import java.net.URI;
import javax.swing.Icon;

/**
 * Class UILoader
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class UILoader implements IIconList {

  private final Icon icon = ImageUtils.LOAD_24;

  @Override
  public Icon getIcon() {
    return icon;
  }

  @Override
  public void setIcon(Icon icon) {
    // DO nothing
  }

  @Override
  public String toString() {
    return "Loading ...";// FIXME i18n
  }

  @Override
  public URI getUri(ImageInfo.ImageSize size) {
    return null;
  }
}
