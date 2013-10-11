/*
 * Movie Renamer
 * Copyright (C) 2012-2013 Nicolas Magré
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

import com.alee.laf.optionpane.WebOptionPane;
import fr.free.movierenamer.exception.InvalidUrlException;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.utils.ClassUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JOptionPane;

/**
 * Class Worker
 *
 * @param <T>
 * @author Nicolas Magré
 */
public abstract class Worker<T> extends AbstractWorker<T, String> {

  protected final MovieRenamer mr;

  public Worker(MovieRenamer mr) {
    this.mr = mr;
  }

  @Override
  protected final T doInBackground() {
    T result = null;
    try {
      result = executeInBackground();
    } catch (InvalidUrlException ex) {
      UISettings.LOGGER.log(Level.SEVERE, ClassUtils.getStackTrace(ex));
      publish(String.format("InvalidUrlException %s failed\n%s", getClass().getSimpleName(), ex.getLocalizedMessage())); // FIXME i18n
    } catch (UnknownHostException ex) {
      UISettings.LOGGER.log(Level.SEVERE, ClassUtils.getStackTrace(ex));
      publish(UIUtils.i18n.getLanguage("error.network.connection", false));
    } catch (SocketTimeoutException ex) {
      UISettings.LOGGER.log(Level.WARNING, ex.getCause().toString());
      publish(UIUtils.i18n.getLanguage("error.network.timeout", false));
    } catch (SocketException ex) {
      UISettings.LOGGER.log(Level.SEVERE, ClassUtils.getStackTrace(ex));
      publish(UIUtils.i18n.getLanguage("error.network.connection", false) + "\n\n" + ex.getLocalizedMessage());
    } catch (Exception ex) {
      UISettings.LOGGER.log(Level.SEVERE, ClassUtils.getStackTrace(ex));
      publish(UIUtils.i18n.getLanguage("error.unknown", false, getClass().getSimpleName(), ex.getLocalizedMessage()));
    }
    return result;
  }

  @Override
  protected void process(List<String> v) {
    WebOptionPane.showMessageDialog(mr, v.get(0) + "\n", UIUtils.i18n.getLanguage("error.error", false), JOptionPane.ERROR_MESSAGE);
  }
}
