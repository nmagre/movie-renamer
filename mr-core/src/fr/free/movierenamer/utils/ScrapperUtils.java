/*
 * movie-renamer-core
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

/**
 * Class ScrapperUtils
 *
 * @author Nicolas Magré
 */
public final class ScrapperUtils {

  public static enum ScrapperProperty {

  }



  public static enum AvailableApiIds {

    IMDB("tt"),
    ALLOCINE(),
    TMDB(),
    TVDB(),
    TVRAGE;
    private String prefix;

    private AvailableApiIds() {
      this("");
    }

    private AvailableApiIds(String prefix) {
      this.prefix = prefix;
    }

    public String getPrefix() {
      return prefix;
    }
  }

  private ScrapperUtils() {
    throw new UnsupportedOperationException();
  }
}
