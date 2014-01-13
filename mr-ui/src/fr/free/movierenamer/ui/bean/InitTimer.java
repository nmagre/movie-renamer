/*
 * Copyright (C) 2014 duffy
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

import com.alee.laf.label.WebLabel;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.swing.dialog.MediaInfoDownloadDialog;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import static fr.free.movierenamer.ui.utils.UIUtils.i18n;
import fr.free.movierenamer.ui.worker.impl.CheckUpdateWorker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 *
 * @author duffy
 */
public class InitTimer {

  private static final UISettings setting = UISettings.getInstance();
  private final MovieRenamer mr;
  private final WebLabel mediainfoStatusLbl;

  public InitTimer(MovieRenamer mr, WebLabel mediainfoStatusLbl) {
    this.mr = mr;
    this.mediainfoStatusLbl = mediainfoStatusLbl;
  }

  public void start() {

    // check for update timer
    final Timer updateTimer = new Timer(3000, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        CheckUpdateWorker updateWorker = new CheckUpdateWorker(mr, false);
        updateWorker.execute();
      }
    });
    updateTimer.setRepeats(false);

    // Media info warning or download dialog (only for windows)
    final Timer mediainfoTimer = new Timer(2500, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (Settings.WINDOWS) {
          new MediaInfoDownloadDialog(mr).setVisible(true);
        } else {
          UIUtils.showWarningNotification(i18n.getLanguage("error.noWritePermission", false));
          // Start check update
          if (setting.isCheckupdate()) {
            updateTimer.start();
          }
        }

      }
    });

    if (!Settings.MEDIAINFO) {
      mediainfoStatusLbl.setLanguage(i18n.getLanguageKey("error.mediaInfoNotInstalled", false));
      mediainfoStatusLbl.setIcon(ImageUtils.MEDIAWARN_16);

      if (setting.isMediaInfoWarning()) {
        mediainfoTimer.setRepeats(false);
        mediainfoTimer.start();
      } else {
        // check for update
        if (setting.isCheckupdate()) {
          updateTimer.start();
        }
      }
    }

  }
}
