/*
 * movie-renamer
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
package fr.free.movierenamer.ui.worker;

import fr.free.movierenamer.ui.LoadingDialog;
import fr.free.movierenamer.ui.LoadingDialog.LoadingDialogPos;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.utils.LocaleUtils;
import java.beans.PropertyChangeSupport;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * Class Worker
 * 
 * @param <T>
 * @author Magré Nicolas
 * @author QUÉMÉNEUR Simon
 * 
 */
public abstract class AbstractWorker extends SwingWorker<Void, String> {

  protected final PropertyChangeSupport errorSupport;
  protected final MovieRenamer parent;
  //protected final LoadingDialog loading;

  // private T result;

  protected AbstractWorker(PropertyChangeSupport errorSupport, MovieRenamer parent) {
    this.errorSupport = errorSupport;
    this.parent = parent;
//    this.loading = (parent != null) ? parent.getLoading() : null;
    updateLoadingValue(0);
  }

  @Override
  protected final Void doInBackground() throws Exception {
    // SwingUtilities.invokeLater(new Runnable() {
    // @Override
    // public void run() {
    try {
      executeInBackground();
    } catch (Exception e) {
      firePropertyChange(LoadingDialog.closeEvent, String.format("worker %s failed", AbstractWorker.this.getClass().getSimpleName())); // FIXME i18n
      throw e;
    } finally {
      updateLoadingValue(100);
    }
    // }
    // });
    return null;

  }

  public abstract void executeInBackground() throws Exception;

  protected final void firePropertyChange(String propertyName, String... chunks) {
    if (errorSupport != null) {
      errorSupport.firePropertyChange(propertyName, false, true);
    }
    publish(chunks);
  }

  @Override
  public final void process(List<String> v) {
    JOptionPane.showMessageDialog(null, LocaleUtils.i18n(v.get(0)), LocaleUtils.i18n("error"), JOptionPane.ERROR_MESSAGE);
  }

  public final PropertyChangeSupport getErrorSupport() {
    return errorSupport;
  }

  protected abstract LoadingDialogPos getLoadingDialogPos();

  protected final void updateLoadingValue(int value) {
   /* if (loading != null) {
      loading.setValue(value, getLoadingDialogPos());
    }*/
  }
}
