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
package fr.free.movierenamer.worker;

import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.beans.PropertyChangeSupport;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * Class Worker
 * 
 * @param <T> 
 * @author QUÉMÉNEUR Simon
 * 
 */
public abstract class Worker<T> extends SwingWorker<T, String> {

  protected final Settings config = Settings.getInstance();
  private final PropertyChangeSupport errorSupport;

  public Worker() {
    this.errorSupport = null;
  }

  public Worker(PropertyChangeSupport errorSupport) {
    this.errorSupport = errorSupport;
  }

  @Override
  protected final T doInBackground() throws Exception {
    return executeInBackground();
  }

  protected abstract T executeInBackground() throws Exception;

  protected final void firePropertyChange(String propertyName, String... chunks) {
    if (errorSupport != null) {
      errorSupport.firePropertyChange(propertyName, false, true);
    }
    publish(chunks);
  }

  @Override
  public final void process(List<String> v) {
    JOptionPane.showMessageDialog(null, Utils.i18n(v.get(0)), Utils.i18n("error"), JOptionPane.ERROR_MESSAGE);
  }

  public final PropertyChangeSupport getErrorSupport() {
    return errorSupport;
  }

  // /**
  // * @return The Parser for the URI
  // * @throws Exception
  // */
  // protected abstract MrParser<T> gerParser() throws Exception;

}
