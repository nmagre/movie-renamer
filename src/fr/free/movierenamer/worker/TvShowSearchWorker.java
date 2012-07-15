/*
 * movie-renamer
 * Copyright (C) 2012 QUÉMÉNEUR Simon
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
package fr.free.movierenamer.worker;

import javax.swing.event.SwingPropertyChangeSupport;

/**
 * Class TvShowSearchWorker
 *
 * @author QUÉMÉNEUR Simon
 */
public abstract class TvShowSearchWorker extends MediaSearchWorker {

  /**
   * Constructor arguments
   *
   * @param errorSupport Swing change support
   * @param searchTitle Serie title to search
   */
  public TvShowSearchWorker(SwingPropertyChangeSupport errorSupport, String searchTitle) {
    super(errorSupport, searchTitle);
  }
}
