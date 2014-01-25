/*
 * Movie Renamer
 * Copyright (C) 2014 Nicolas Magré
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
package fr.free.movierenamer.ui.worker;

import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.settings.UISettings;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import javax.swing.SwingUtilities;

/**
 * Class ControlWorker, worker with pause/resume control
 *
 * @author Nicolas Magré
 */
public abstract class ControlWorker<T, V> extends Worker<T> {

  public ControlWorker(MovieRenamer mr) {
    super(mr);
  }

  protected final void publishPause(final V chunk) {
    try {
      SwingUtilities.invokeAndWait(new Runnable() {

        @Override
        public void run() {
          try {
            processPause(chunk);
          } catch (Exception ex) {
            // Prevent any bug, sometimes weblaf notification thrown an exception (i hope it will be fixed)
            UISettings.LOGGER.log(Level.SEVERE, null, ex);
          }
        }
      });
    } catch (InterruptedException | InvocationTargetException ex) {
      UISettings.LOGGER.log(Level.SEVERE, null, ex);
    }
  }

  protected abstract void processPause(V v);

}
