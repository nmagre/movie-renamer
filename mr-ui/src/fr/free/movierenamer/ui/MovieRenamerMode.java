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
package fr.free.movierenamer.ui;

import fr.free.movierenamer.ui.res.UIFile;
import fr.free.movierenamer.utils.ImageUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import javax.swing.ImageIcon;

/**
 * Enum MovieRenamerMode
 * 
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
public enum MovieRenamerMode {

  MOVIEMODE(LocaleUtils.i18n("movie"), LocaleUtils.i18n("movieMode"), UIFile.MediaType.MOVIE, "movie.png"),
  TVSHOWMODE(LocaleUtils.i18n("tvshow"), LocaleUtils.i18n("tvshowMode"), UIFile.MediaType.TVSHOW, "tv.png");

  private UIFile.MediaType mediaType;
  private String title;
  private String titleMode;
  private ImageIcon icon;

  private MovieRenamerMode(String title, String titleMode, UIFile.MediaType mediaType, String imgName) {
    this.title = title;
    this.titleMode = titleMode;
    this.mediaType = mediaType;
    this.icon = new ImageIcon(ImageUtils.getImageFromJAR(imgName));
  }

  public String getTitle() {
    return title;
  }
  
  public String getTitleMode(){
    return titleMode;
  }

  public UIFile.MediaType getMediaType() {
    return mediaType;
  }

  public ImageIcon getIcon() {
    return icon;
  }
}
