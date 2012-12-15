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
import java.awt.Color;
import java.awt.Image;
import javax.swing.Icon;
import javax.swing.ImageIcon;

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
  public static final Icon MEDIAWARNINGICON = new ImageIcon(ImageUtils.getImageFromJAR("ui/film-error.png"));
  public static final Icon STAR = ImageUtils.getIconFromJar("ui/star.png");
  public static final Icon STAR_HALF = ImageUtils.getIconFromJar("ui/star-half.png");
  public static final Icon STAR_EMPTY = ImageUtils.getIconFromJar("ui/star-empty.png");
  public static final Image LOGO_32 = ImageUtils.getImageFromJAR("ui/icon-32.png");

  public static BorderPainter getBorder() {
    BorderPainter border = new BorderPainter();
    return border;
  }

  public static BorderPainter getBorder(int size) {
    BorderPainter border = new BorderPainter();
    border.setWidth(size);
    return border;
  }

  public static BorderPainter getBorder(int size, int round) {
    BorderPainter border = new BorderPainter();
    border.setWidth(size);
    border.setRound(round);
    return border;
  }

  public static BorderPainter getBorder(int size, int round, Color color) {
    BorderPainter border = new BorderPainter();
    border.setWidth(size);
    border.setRound(round);
    border.setColor(color);
    return border;
  }

  private UIUtils() {
    throw new UnsupportedOperationException();
  }
}
