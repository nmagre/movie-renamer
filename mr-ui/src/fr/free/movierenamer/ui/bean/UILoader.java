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

import com.alee.laf.list.WebList;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.ui.utils.UIUtils;
import java.net.URI;
import javax.swing.Icon;

/**
 * Class UILoader
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class UILoader implements IIconList {

  private final WebList list;
  private final int row;
  public UILoader(WebList list, int row) {
    this.list = list;
    this.row = row;
  }

  @Override
  public Icon getIcon() {
    return UIUtils.getAnimatedLoader(list, row);
  }

  @Override
  public void setIcon(Icon icon) {
    // DO nothing
  }

  @Override
  public String toString() {
    return "";
  }

  @Override
  public URI getUri(ImageInfo.ImageSize size) {
    return null;
  }

}
