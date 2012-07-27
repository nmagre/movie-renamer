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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import javax.swing.*;

/**
 * Class IconListRenderer , Display image + text in list
 *
 * @param <T>
 * @author Nicolas Magré
 */
public class IconListRenderer<T extends IIconList> extends DefaultListCellRenderer {

  private static final long serialVersionUID = 1L;
  private List<T> results;

  /**
   * Constructor arguments
   *
   * @param results List object
   */
  public IconListRenderer(List<T> results) {
    this.results = results;
  }

  @Override
  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

    if(value.toString().startsWith("<sep>")) {
      isSelected = false;
      cellHasFocus = false;
    }
    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

    if (value.toString().startsWith("<sep>")) {
      label.setText(value.toString().substring(5));
      label.setBackground(new Color(224, 234, 241));
      label.setBorder(BorderFactory.createLineBorder(Color.black));
      label.setPreferredSize(new Dimension(2, 20));
      return label;
    }
    
    if (index >= results.size()) {
      return label;
    }

    Icon icon;
    IIconList iicon = results.get(index);
    icon = iicon.getIcon();

    if (icon != null) {
      label.setIcon(icon);
    }
    return label;
  }
}
