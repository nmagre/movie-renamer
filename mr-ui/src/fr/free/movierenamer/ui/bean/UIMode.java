/*
 * Movie Renamer
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

import fr.free.movierenamer.info.FileInfo;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import javax.swing.ImageIcon;

/**
 * Enum UIMode
 *
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
public enum UIMode {

  MOVIEMODE(UIUtils.i18n.getLanguageKey("movieMode"), "movieMode", FileInfo.MediaType.MOVIE, "ui/24/movie.png"),// FIXME i18n
  TVSHOWMODE(UIUtils.i18n.getLanguageKey("tvshowMode"), "tvshowMode", FileInfo.MediaType.TVSHOW, "ui/24/tv.png");
  private FileInfo.MediaType mediaType;
  private String title;
  private String titleMode;
  private ImageIcon icon;

  private UIMode(String title, String titleMode, FileInfo.MediaType mediaType, String imgName) {
    this.title = title;
    this.titleMode = titleMode;
    this.mediaType = mediaType;
    this.icon = new ImageIcon(ImageUtils.getImageFromJAR(imgName));
  }

  public String getTitle() {
    return title;
  }

  public String getTitleMode() {
    return titleMode;
  }

  public FileInfo.MediaType getMediaType() {
    return mediaType;
  }

  public ImageIcon getIcon() {
    return icon;
  }
}
