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
package fr.free.movierenamer.ui.bean;

import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.swing.dialog.LoggerDialog;
import fr.free.movierenamer.ui.utils.UIUtils;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.swing.SwingUtilities;

/**
 * Class UILogHandler
 *
 * @author Nicolas Magré
 */
public class UILogHandler extends Handler {

  private LoggerDialog dialog = null;
  private static final UISettings settings = UISettings.getInstance();

  public UILogHandler(LoggerDialog dialog) {
    this.dialog = dialog;
  }

  @Override
  public synchronized void publish(final LogRecord record) {
    if (!isLoggable(record)) {
      return;
    }

    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        dialog.addLog(record);
        if (settings.isDebug()) {
          if (record.getLevel().equals(Level.SEVERE)) {
            UIUtils.showErrorNotification(record.getMessage().replace("\n", "\\n"));
          }

          if (record.getLevel().equals(Level.WARNING)) {
            UIUtils.showWarningNotification(record.getMessage().replace("\n", "\\n"));
          }
        }
      }
    });
  }

  public void setLogLevel(Level level) {
    Settings.LOGGER.setLevel(level);
    UISettings.LOGGER.setLevel(level);
    setLevel(level);
  }

  @Override
  public void close() {
  }

  @Override
  public void flush() {
  }
}
