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
package fr.free.movierenamer.ui.utils;

import fr.free.movierenamer.ui.LoadingDialog;

/**
 * Class Loading , Loading info
 * 
 * @author Nicolas Magré
 */
public class Loading {

  private final String title;
  private final boolean indeterminate;
  private final int max;
  private final LoadingDialog.LoadingDialogPos workerKey;

  /**
   * Constructor arguments
   * 
   * @param workerKey 
   * @param indeterminate Loading indeterminate
   */
  public Loading(LoadingDialog.LoadingDialogPos workerKey, boolean indeterminate) {
    this.workerKey = workerKey;
    this.title = (workerKey != null) ? workerKey.toString() : null;
    this.indeterminate = indeterminate;
    this.max = 100;
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
   * Get worker
   * 
   * @return Worker
   */
  public LoadingDialog.LoadingDialogPos getKey() {
    return workerKey;
  }
}
