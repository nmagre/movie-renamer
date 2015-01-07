/*
 * Movie Renamer
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
package fr.free.movierenamer.ui.worker;

import fr.free.movierenamer.ui.bean.IEventInfo;
import fr.free.movierenamer.ui.bean.UIEvent;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.utils.ClassUtils;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import javax.swing.SwingWorker;

/**
 * Class Worker
 *
 * @param <T>
 * @param <V>
 * @author Magré Nicolas
 * @author QUÉMÉNEUR Simon
 */
public abstract class AbstractWorker<T, V> extends SwingWorker<T, V> implements PropertyChangeListener, IEventInfo, IWorker {

  protected AbstractWorker() {
    addPropertyChangeListener(this);
  }

  @Override
  protected T doInBackground() {
    T result = null;
    try {
      result = executeInBackground();
    } catch (Exception ex) {
      UISettings.LOGGER.log(Level.SEVERE, ClassUtils.getStackTrace(ex));
    }
    return result;
  }

  protected abstract T executeInBackground() throws Exception;

  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    if ("progress".equals(evt.getPropertyName())) {
      workerProgress((Integer) evt.getNewValue());
      return;
    }

    if (!(evt.getNewValue() instanceof SwingWorker.StateValue)) {
      return;
    }

    switch ((SwingWorker.StateValue) evt.getNewValue()) {
      case STARTED:
        workerStarted();
        break;
      case PENDING:
        workerPending();
        break;
      case DONE:
        WorkerManager.updateWorkerQueue();

        try {
          workerDone();
        } catch (CancellationException e) {// Worker canceled
          UISettings.LOGGER.log(Level.INFO, String.format("Worker %s canceled", getClass().getSimpleName()));
          workerCanceled();
        } catch (Exception ex) {
          UISettings.LOGGER.log(Level.SEVERE, ClassUtils.getStackTrace(ex));
        }
        break;
      default:
        break;
    }
  }

  protected void workerCanceled() {
    // DO nothing
  }

  protected void workerStarted() {
    // DO nothing
  }

  protected void workerPending() {
    // DO nothing
  }

  protected void workerProgress(int progress) {
    UIEvent.fireUIEvent(UIEvent.Event.WORKER_PROGRESS, this);
  }

  @Override
  public String getParam() {
    return "";
  }

  protected abstract void workerDone() throws Exception;
}
