/*
 * movie-renamer-core
 * Copyright (C) 2012-2014 Nicolas Magré
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

import fr.free.movierenamer.utils.Sorter;
import java.io.Serializable;

/**
 * Class SearchResult : A search result that can be sorted
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class SearchResult extends Sorter.ISort implements Serializable {

  private static final long serialVersionUID = 1L;
  protected int year;
  protected String name;
  protected String originalName;

  protected SearchResult() {
    // used by serializer
  }

  /**
   * Constructor
   *
   * @param name Result name
   * @param originalName Result original name
   * @param year Year
   */
  public SearchResult(String name, String originalName, int year) {
    this.name = name;
    this.originalName = originalName;
    this.year = year;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getOriginalName() {
    return originalName;
  }

  @Override
  public int getYear() {
    return year;
  }

  @Override
  public String toString() {
    if (year > 0) {
      return name + String.format(" (%04d)", year);
    }
    
    return name;
  }
}
