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

import fr.free.movierenamer.media.Media;
import javax.swing.ImageIcon;

/**
 * Enum MovieRenamerMode
 * 
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
public enum MovieRenamerMode {

  MOVIEMODE(Utils.i18n("movie"), Media.MediaType.MOVIE, "/image/movie.png"),
  TVSHOWMODE(Utils.i18n("tvshow"), Media.MediaType.TVSHOW, "/image/tv.png");

  private Media.MediaType mediaType;
  private String title;
  private ImageIcon icon;

  private MovieRenamerMode(String title, Media.MediaType mediaType, String imgName) {
    this.title = title;
    this.mediaType = mediaType;
    this.icon = new ImageIcon(Utils.getImageFromJAR(imgName, MovieRenamerMode.class));
  }

  public String getTitle() {
    return title;
  }

  public Media.MediaType getMediaType() {
    return mediaType;
  }

  public ImageIcon getIcon() {
    return icon;
  }
}
