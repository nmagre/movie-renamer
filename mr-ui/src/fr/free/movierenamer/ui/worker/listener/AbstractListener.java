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
import fr.free.movierenamer.ui.panel.LoadingDialog;
import fr.free.movierenamer.ui.panel.LoadingDialog.LoadingDialogPos;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.utils.ClassUtils;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

  protected final LoadingDialog loading;
  protected final MovieRenamer mr;
  protected final SwingWorker<T, String> worker;

  public AbstractListener(MovieRenamer mr, SwingWorker<T, String> worker) {
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
      updateLoadingValue(worker.getProgress());
      return;
    }

    switch ((SwingWorker.StateValue) evt.getNewValue()) {
      case STARTED:
        started();
        mr.getLoading().setCursor(MovieRenamer.hourglassCursor);
        mr.setCursor(MovieRenamer.hourglassCursor);
        break;
      case PENDING:
        pending();
        break;
      case DONE:
        try {
          done();
        } catch (Exception ex) {
          UISettings.LOGGER.log(Level.SEVERE, ClassUtils.getStackTrace("Exception", ex.getStackTrace()));
        }
        mr.getLoading().setCursor(MovieRenamer.normalCursor);
        mr.setCursor(MovieRenamer.normalCursor);
        break;
      default:
        updateLoadingValue(worker.getProgress());
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
