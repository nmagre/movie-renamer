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

import com.alee.laf.list.DefaultListModel;
import com.alee.laf.optionpane.WebOptionPane;
import fr.free.movierenamer.exception.InvalidUrlException;
import fr.free.movierenamer.info.ImageInfo.ImageSize;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.list.IIconList;
import fr.free.movierenamer.ui.panel.GalleryPanel;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.utils.ClassUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * Class Worker
 *
 * @param <T>
 * @author Magré Nicolas
 * @author QUÉMÉNEUR Simon
 */
public abstract class AbstractWorker<T> extends SwingWorker<T, String> implements PropertyChangeListener {

  protected final MovieRenamer mr;

  public AbstractWorker(MovieRenamer mr) {
    this.mr = mr;
    addPropertyChangeListener(this);
  }

  @Override
  protected final T doInBackground() {
    T result = null;
    try {
      result = executeInBackground();
    } catch (InvalidUrlException e) {
      UISettings.LOGGER.log(Level.SEVERE, ClassUtils.getStackTrace("InvalidUrlException", e.getStackTrace()));
      publish(String.format("InvalidUrlException %s failed", AbstractWorker.this.getClass().getSimpleName())); // FIXME i18n
    } catch (Exception ex) {
      UISettings.LOGGER.log(Level.SEVERE, ClassUtils.getStackTrace(ex.getCause().toString(), ex.getStackTrace()));
      publish(String.format("worker %s failed", AbstractWorker.this.getClass().getSimpleName())); // FIXME i18n
    }
    return result;
  }

  protected abstract T executeInBackground() throws Exception;

  @Override
  protected void process(List<String> v) {
    WebOptionPane.showMessageDialog(null, LocaleUtils.i18n(v.get(0)), LocaleUtils.i18n("error"), JOptionPane.ERROR_MESSAGE);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {

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
        try {
          workerDone();
        } catch (CancellationException e) {
          // Worker canceled
          UISettings.LOGGER.log(Level.INFO, String.format("Worker %s canceled", e.getClass().getSimpleName()));
        } catch (Exception ex) {
          UISettings.LOGGER.log(Level.SEVERE, ClassUtils.getStackTrace("Exception", ex.getStackTrace()));
        }
        break;
      default:
        break;
    }
  }

  protected <V extends IIconList> void getImages(List<V> images, DefaultListModel model, Dimension imageSize) {
    ImageWorker<V> imagesWorker = new ImageWorker<V>(images, model, imageSize, "ui/unknown.png");
    imagesWorker.execute();
    mr.addWorker(imagesWorker);
  }

  protected <V extends IIconList> void getImages(List<V> images, GalleryPanel gallery) {
    ImageWorker<V> imagesWorker = new ImageWorker<V>(images, gallery, "ui/unknown.png", ImageSize.small);
    imagesWorker.execute();
    mr.addWorker(imagesWorker);
  }

  protected void workerStarted() {
    // DO nothing
  }

  protected void workerPending() {
    // DO nothing
  }

  protected abstract void workerDone() throws Exception;
}
