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
package fr.free.movierenamer.ui.worker.listener;

import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.utils.ClassUtils;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import javax.swing.SwingWorker;

/**
 * Class AbstractListener
 *
 * @param <T>
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class AbstractListener<T> implements PropertyChangeListener {

  protected final MovieRenamer mr;
  protected final SwingWorker<T, String> worker;

  public AbstractListener(MovieRenamer mr, SwingWorker<T, String> worker) {
    this.mr = mr;
    this.worker = worker;
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    if (!(evt.getNewValue() instanceof SwingWorker.StateValue)) {
      return;
    }

    switch ((SwingWorker.StateValue) evt.getNewValue()) {
      case STARTED:
        started();
        break;
      case PENDING:
        pending();
        break;
      case DONE:
        try {
          done();
        }  catch(CancellationException e){
          // Worker canceled
        } catch (Exception ex) {
          UISettings.LOGGER.log(Level.SEVERE, ClassUtils.getStackTrace(ex.getMessage(), ex.getStackTrace()));
        }
        break;
      default:
        break;
    }
  }

  protected void started() {
    // DO nothing
  }

  protected final void pending() {
    // DO nothing
  }

  protected abstract void done() throws Exception;
}
