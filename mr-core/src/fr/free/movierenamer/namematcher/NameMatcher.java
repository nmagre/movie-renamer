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

/**
 * Class NameMatcher
 * @author Nicolas Magré
 */
class NameMatcher {

  public static final int HIGH = 3;
  public static final int MEDIUM = 1;
  public static final int LOW = 2;
  private int priority;
  private String name;
  private String result;

  /**
   * Constructor
   *
   * @param name Matcher name
   * @param priority Matcher priority
   */
  public NameMatcher(String name, int priority) {
    this.name = name;
    this.priority = priority;
    result = "";
  }

  public String getName() {
    return name;
  }

  public String getMatch() {
    return result;
  }

  public void setMatch(String result) {
    if (result != null && result.length() > 0) {
      this.result = result;
    }
  }

  public int getPriority() {
    return priority;
  }

  public boolean found() {
    return result.length() > 0;
  }

  @Override
  public String toString() {
    return name + " : " + result;
  }
}