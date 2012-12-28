/*
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
package fr.free.movierenamer.ui.utils;

import com.alee.extended.background.BorderPainter;
import com.alee.laf.list.WebList;
import fr.free.movierenamer.ui.res.IIconList;
import fr.free.movierenamer.ui.res.IconListRenderer;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.ImageObserver;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 * Class MediaRenamed, Renamed movie
 *
 * @author Nicolas Magré
 */
public class UIUtils {

  // images
  public static final Icon MEDIARENAMEDICON = ImageUtils.getIconFromJar("ui/icon-32.png");
  public static final Icon MEDIAWASRENAMEDICON = ImageUtils.getIconFromJar("ui/icon-22.png");
  public static final Icon MEDIAICON = ImageUtils.getIconFromJar("ui/film.png");
  public static final Icon MEDIAWARNINGICON = ImageUtils.getIconFromJar("ui/film-error.png");
  public static final Icon STAR = ImageUtils.getIconFromJar("ui/star.png");
  public static final Icon STAR_HALF = ImageUtils.getIconFromJar("ui/star-half.png");
  public static final Icon STAR_EMPTY = ImageUtils.getIconFromJar("ui/star-empty.png");
  public static final Image LOGO_32 = ImageUtils.getImageFromJAR("ui/icon-32.png");
  public static final Icon HELP = ImageUtils.getIconFromJar("ui/system-help-3.png");
  public static final Icon HELPDISABLED = ImageUtils.getIconFromJar("ui/system-help-3-disabled.png");
  private static final Icon loader = ImageUtils.getIconFromJar("ui/loader.gif");
  public static final IconListRenderer<IIconList> iconListRenderer = new IconListRenderer<IIconList>(false);

  public static BorderPainter<? extends JComponent> getBorder() {
    BorderPainter<? extends JComponent> border = new BorderPainter<JComponent>();
    return border;
  }

  public static BorderPainter<? extends JComponent> getBorder(int size) {
    BorderPainter<? extends JComponent> border = new BorderPainter<JComponent>();
    border.setWidth(size);
    return border;
  }

  public static BorderPainter<? extends JComponent> getBorder(int size, int round) {
    BorderPainter<? extends JComponent> border = new BorderPainter<JComponent>();
    border.setWidth(size);
    border.setRound(round);
    return border;
  }

  public static BorderPainter<? extends JComponent> getBorder(int size, int round, Color color) {
    BorderPainter<? extends JComponent> border = new BorderPainter<JComponent>();
    border.setWidth(size);
    border.setRound(round);
    border.setColor(color);
    return border;
  }

  public static Icon getAnimatedLoader(final WebList list, final int row) {
    ImageIcon icon = (ImageIcon) loader;
    icon.setImageObserver(new ImageObserver() {
      @Override
      public boolean imageUpdate(Image img, int infoflags, int x, int y, int w, int h) {
        if ((infoflags & (FRAMEBITS | ALLBITS)) != 0 && list.isShowing()) {
          list.repaint(list.getCellBounds(row, row));
        }
        return (infoflags & (ALLBITS | ABORT)) == 0;
      }
    });
    return loader;
  }

  private UIUtils() {
    throw new UnsupportedOperationException();
  }
}
