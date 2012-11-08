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
package fr.free.movierenamer.namematcher;

import java.util.Comparator;

/**
 * Class StringLengthComparable, compare two string length
 * @author Nicolas Magré
 */
class StringLengthComparable implements Comparator<String> {

  @Override
  public int compare(String s1, String s2) {
    if (s1.length() == 2) {
      return 1;
    }
    if (s2.length() == 2) {
      return -1;
    }
    return s1.length() - s2.length();
  }
}
