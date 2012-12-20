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

import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebListCellRenderer;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JList;

/**
 * Class IconListRenderer , Display image + text in list
 *
 * @param <T>
 * @author Nicolas Magré
 */
public class IconListRenderer<T extends IIconList> extends WebListCellRenderer {

  private static final long serialVersionUID = 1L;
  private boolean horizontalAlign = false;

  public IconListRenderer(boolean horizontalAlign) {
    this.horizontalAlign = horizontalAlign;
  }

  @Override
  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    WebLabel label = (WebLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    IIconList obj = (IIconList) value;

    if(horizontalAlign){
      label.setHorizontalAlignment(WebLabel.CENTER);
    }

    Icon icon = obj.getIcon();

    if (icon != null) {
      label.setIcon(icon);
    }

    label.setOpaque(true);
    return label;
  }
}
