/*
 * movie-renamer-core
 * Copyright (C) 2013 Nicolas Magré
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
package fr.free.movierenamer.searchinfo;

import java.net.URL;

/**
 * Class Trailer
 *
 * @author Nicolas Magré
 */
public class Trailer extends Hyperlink {

  private static final long serialVersionUID = 1L;

  protected Trailer() {
    // used by serializer
  }

  public Trailer(String title, URL url) {
    super(title, null, url);
  }

}
