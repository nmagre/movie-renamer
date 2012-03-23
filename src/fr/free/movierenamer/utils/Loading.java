/******************************************************************************
 *                                                                             *
 *    Movie Renamer                                                            *
 *    Copyright (C) 2012 Magré Nicolas                                         *
 *                                                                             *
 *    Movie Renamer is free software: you can redistribute it and/or modify    *
 *    it under the terms of the GNU General Public License as published by     *
 *    the Free Software Foundation, either version 3 of the License, or        *
 *    (at your option) any later version.                                      *
 *                                                                             *
 *    This program is distributed in the hope that it will be useful,          *
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            *
 *    GNU General Public License for more details.                             *
 *                                                                             *
 *    You should have received a copy of the GNU General Public License        *
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                             *
 ******************************************************************************/
package fr.free.movierenamer.utils;

/**
 * Class Loading , Loading info
 * @author Nicolas Magré
 */
public class Loading {

  private String title;
  private boolean indeterminate;
  private int max;
  private int idWorker;

  /**
   * Constructor arguments
   * @param title Loading title
   * @param indeterminate Loading indeterminate
   * @param max Loading max value
   * @param idWorker Loading worker ID
   */
  public Loading(String title, boolean indeterminate, int max, int idWorker) {
    this.title = title;
    this.indeterminate = indeterminate;
    this.max = max;
    this.idWorker = idWorker;
  }

  /**
   * Get loading title
   * @return title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Get indeterminate
   * @return indeterminate
   */
  public boolean getIndeterminate() {
    return indeterminate;
  }

  /**
   * Get mac value
   * @return max value
   */
  public int getMax() {
    return max;
  }

  /**
   * Get worker ID
   * @return WorkerID
   */
  public int getId() {
    return idWorker;
  }
}
