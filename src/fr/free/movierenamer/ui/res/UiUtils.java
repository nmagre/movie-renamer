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

import fr.free.movierenamer.utils.Utils;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Class UiUtils
 * @author Nicolas Magré
 */
public abstract class UiUtils {
  public static final Icon MEDIARENAMEDICON = new ImageIcon(Utils.getImageFromJAR("/image/icon-32.png", Utils.class));
  public static final Icon MEDIAWASRENAMEDICON = new ImageIcon(Utils.getImageFromJAR("/image/icon-22.png", Utils.class));
  public static final Icon MEDIAICON = new ImageIcon(Utils.getImageFromJAR("/image/film.png", Utils.class));
  public static final Icon MEDIAWARNINGICON = new ImageIcon(Utils.getImageFromJAR("/image/film-error.png", Utils.class));
  
}
