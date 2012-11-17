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

import fr.free.movierenamer.ui.LoadingDialog;
import fr.free.movierenamer.ui.LoadingDialog.LoadingDialogPos;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.settings.Settings;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import javax.swing.SwingWorker;

/**
 * Class WorkerListener
 *
 * @param <T>
 * @author Nicolas Magré
 */
public abstract class WorkerListener<T> implements PropertyChangeListener {

  protected final LoadingDialog loading;
  protected final MovieRenamer mr;
  protected final SwingWorker<T, String> worker;

  public WorkerListener(MovieRenamer mr, SwingWorker<T, String> worker) {
    this.mr = mr;
    this.loading = (mr != null) ? mr.getLoading() : null;
    this.worker = worker;
  }

  protected abstract LoadingDialogPos getLoadingDialogPos();

  protected final void updateLoadingValue(int value) {
    if (loading != null) {
      loading.setValue(value, getLoadingDialogPos());
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    if (!(evt.getNewValue() instanceof SwingWorker.StateValue)) {
      return;
    }

    switch ((SwingWorker.StateValue) evt.getNewValue()) {
      case STARTED:
        started();
        mr.setCursor(MovieRenamer.hourglassCursor);
        break;
      case PENDING:
        pending();
        break;
      case DONE:
        try {
          done();
        } catch (Exception ex) {
          Settings.LOGGER.log(Level.SEVERE, null, ex);
        }
        mr.setCursor(MovieRenamer.normalCursor);
        break;
      default:
        break;
    }
  }

  protected final void started() {
    updateLoadingValue(0);
  }

  protected final void pending() {
    updateLoadingValue(worker.getProgress());
  }

  protected abstract void done() throws Exception;
}
