/*
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
import fr.free.movierenamer.utils.ClassUtils;
import fr.free.movierenamer.utils.LocaleUtils;
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
    } catch (InvalidUrlException e) {
      UISettings.LOGGER.log(Level.SEVERE, ClassUtils.getStackTrace("InvalidUrlException", e.getStackTrace()));
      publish(String.format("InvalidUrlException %s failed", Worker.this.getClass().getSimpleName())); // FIXME i18n
    } catch (Exception ex) {
      UISettings.LOGGER.log(Level.SEVERE, ClassUtils.getStackTrace(ex.getCause().toString(), ex.getStackTrace()));
      publish(String.format("worker %s failed", Worker.this.getClass().getSimpleName())); // FIXME i18n
    }
    return result;
  }

  @Override
  protected void process(List<String> v) {
    WebOptionPane.showMessageDialog(null, LocaleUtils.i18n(v.get(0)), LocaleUtils.i18n("error"), JOptionPane.ERROR_MESSAGE);
  }
}
