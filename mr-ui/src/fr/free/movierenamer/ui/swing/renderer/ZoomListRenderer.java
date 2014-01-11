/*
 * Copyright (C) 2013-2014 Nicolas Magré
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

import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebListCellRenderer;
import fr.free.movierenamer.ui.bean.UIMediaImage;
import fr.free.movierenamer.ui.swing.SpinningDial;
import fr.free.movierenamer.ui.utils.UIUtils;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.SwingConstants;

/**
 * class ZoomListRenderer
 *
 * @author Nicolas Magré
 */
public class ZoomListRenderer extends WebListCellRenderer {

  private final float defaultRatio;
  private float scale = 2.0F;
  private final int minWidth;

  public ZoomListRenderer(int minWidth, float defaultRatio) {
    this.minWidth = minWidth;
    this.defaultRatio = defaultRatio;
  }

  public void setScale(float scale) {
    this.scale = scale;
  }

  @Override
  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

    UIMediaImage uimedia = (UIMediaImage) value;
    Icon icon = uimedia.getIcon();
    String width = uimedia.getInfo().getWidth() != null ? "" + uimedia.getInfo().getWidth() : "" + icon.getIconWidth();
    String height = uimedia.getInfo().getHeight() != null ? "" + uimedia.getInfo().getWidth() : "" + icon.getIconHeight();

    ZoomImageLbl zlabel = new ZoomImageLbl(width + "x" + height, icon, SwingConstants.CENTER);
    zlabel.setVerticalAlignment(SwingConstants.BOTTOM);
    zlabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    zlabel.setFont(UIUtils.boldFont);
    return zlabel;
  }

  private class ZoomImageLbl extends WebLabel {// Really dirty :(

    private Image originalImage;
    private final int scaleWidth;
    private int scaleheight;

    public ZoomImageLbl(String str, Icon icon, int alignment) {
      super(str, alignment);
      scaleWidth = (int) (minWidth * scale);
      scaleheight = (int) (scaleWidth / defaultRatio);
      if (icon instanceof SpinningDial) {
        originalImage = null;
        setIcon(icon);
      } else {
        setIcon(null);
        if (icon != null) {
          originalImage = ((ImageIcon) icon).getImage();
          double ratio = (double) originalImage.getWidth(null) / originalImage.getHeight(null);
          scaleheight = (int) (scaleWidth / ratio);
        }
      }
    }

    @Override
    public Dimension getPreferredSize() {
      Dimension dim = super.getPreferredSize();
      return new Dimension(dim.width + scaleWidth, dim.height + scaleheight);
    }

    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Insets inset = getBorder().getBorderInsets(this);

      int x = (getWidth() - scaleWidth) / 2 - (inset.left + inset.right) / 2;
      int y = (getHeight() - scaleheight) / 2 - (inset.top + inset.bottom) / 2;

      if (originalImage != null) {
        g.drawImage(originalImage, x, y, scaleWidth, scaleheight, null);
      }
    }
  }
}
