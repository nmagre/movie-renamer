/*
 * Movie Renamer
 * Copyright (C) 2014 Nicolas Magré
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
package fr.free.movierenamer.ui.swing.renderer;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

/**
 * Class ImageUtils
 *
 * @author Nicolas Magré
 */
public class CompoundIcon implements Icon {

  private final Icon[] icons;
  private static final int gap = 5;

  public CompoundIcon(Icon... icons) {
    this.icons = icons;
  }

  @Override
  public void paintIcon(Component cmpnt, Graphics grphcs, int i, int i1) {
    for (Icon icon : icons) {
      int iconY = getOffset(getIconHeight(), icon.getIconHeight(), 0.5f);
      icon.paintIcon(cmpnt, grphcs, i, i1 + iconY);
      i += icon.getIconWidth() + gap;
    }
  }

  @Override
  public int getIconWidth() {
    int width = 0;
    width += (icons.length - 1) * gap;

    for (Icon icon : icons) {
      width += icon.getIconWidth();
    }

    return width;
  }

  @Override
  public int getIconHeight() {
    int height = 0;
    for (Icon icon : icons) {
      height = Math.max(height, icon.getIconHeight());
    }

    return height;
  }

  private int getOffset(int maxValue, int iconValue, float alignment) {
    float offset = (maxValue - iconValue) * alignment;
    return Math.round(offset);
  }

}
