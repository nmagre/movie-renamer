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

import java.util.Locale;

/**
 * Class MediaSubTitle
 * 
 * @author Nicolas Magré
 */
public class MediaSubTitle {

  private final int stream;
  private String title;
  private Locale language;

  public int getStream() {
    return stream;
  }

  public MediaSubTitle(int stream) {
    this.stream = stream;
    title = "?";
    language = Locale.ROOT;
  }

  public Locale getLanguage() {
    return language;
  }

  public void setLanguage(Locale language) {
    this.language = language;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String toString() {
    return title;
  }
}
