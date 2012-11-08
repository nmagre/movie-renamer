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
package fr.free.movierenamer.mediainfo;

import fr.free.movierenamer.ui.res.IIconList;
import fr.free.movierenamer.utils.ImageUtils;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Class MediaSubTitle
 * 
 * @author Nicolas Magré
 */
public class MediaSubTitle implements IIconList {

  private final int stream;
  private String title;
  private String language;

  public int getStream() {
    return stream;
  }

  public MediaSubTitle(int stream) {
    this.stream = stream;
    title = language = "?";
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public Icon getIcon() {
    return new ImageIcon(ImageUtils.getImageFromJAR(String.format("country/%s.png", (language == null) ? "unknown" : language.toLowerCase())));
  }

  @Override
  public String toString() {
    return title;
  }
}
