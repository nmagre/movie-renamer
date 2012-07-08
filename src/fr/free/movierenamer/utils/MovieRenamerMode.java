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
package fr.free.movierenamer.utils;

import javax.swing.ImageIcon;

/**
 * Class MovieRenamerMode
 *
 * @author Nicolas Magré
 */
public class MovieRenamerMode {

  public static final int MOVIEMODE = 0;
  public static final int TVSHOWMODE = 1;
  private int mode;
  private int mediaType;
  private String title;
  private static final ImageIcon[] modeIcon = new ImageIcon[]{//Mode icon
    new ImageIcon(Utils.getImageFromJAR("/image/movie.png", MovieRenamerMode.class)),
    new ImageIcon(Utils.getImageFromJAR("/image/tv.png", MovieRenamerMode.class))
  };

  public MovieRenamerMode(String title, int mode, int mediaType) {
    this.title = title;
    this.mode = mode;
    this.mediaType = mediaType;
  }

  public String getTitle() {
    return title;
  }

  public int getMediaType() {
    return mediaType;
  }

  public ImageIcon getIcon() {
    return modeIcon[mode];
  }
}
