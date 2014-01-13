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
package fr.free.movierenamer.ui.swing;

import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.scrapper.ScrapperManager;
import fr.free.movierenamer.scrapper.TvShowScrapper;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIMode;
import fr.free.movierenamer.ui.bean.UIScraper;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.swing.dialog.AboutDialog;
import fr.free.movierenamer.ui.swing.dialog.AbstractDialog;
import fr.free.movierenamer.ui.swing.dialog.SettingDialog;
import fr.free.movierenamer.ui.swing.panel.ImagePanel;
import fr.free.movierenamer.ui.swing.panel.MediaPanel;
import fr.free.movierenamer.ui.swing.panel.MoviePanel;
import java.util.HashMap;
import java.util.Map;
import javax.swing.SwingUtilities;

/**
 * Class PanelManager
 *
 * @author Nicolas Magré
 */
public final class UIManager {

  private static SettingDialog settingsDialog;
  private static AboutDialog aboutDialog;
  private static ImagePanel imagePanel;

  public static void init(MovieRenamer mr) {
    UISettings setting = UISettings.getInstance();
    settingsDialog = new SettingDialog(mr);
    aboutDialog = new AboutDialog(mr);
    imagePanel = new ImagePanel(mr);

    for (UIMode mode : UIMode.values()) {
      switch (mode) {
        case MOVIEMODE:
          // Init Movie Scrapper model
          for (MovieScrapper scrapper : ScrapperManager.getMovieScrapperList()) {
            mode.addScrapper(new UIScraper(scrapper));
          }
          mode.getScraperModel().setSelectedItem(new UIScraper(ScrapperManager.getMovieScrapper()));
          mode.setFileformat(setting.coreInstance.getMovieFilenameFormat());
          break;
        case TVSHOWMODE:// TODO TvShow
          // Init TvShow Scrapper model
          for (TvShowScrapper scrapper : ScrapperManager.getTvShowScrapperList()) {
            mode.addScrapper(new UIScraper(scrapper));
          }
          //mode.getScrapperModel().setSelectedItem(new UIScrapper(ScrapperManager.getTvShowScrapper()));
          //mode.setFileformat(setting.coreInstance.gettvShowFilenameFormat());
          break;

      }
    }
  }

  public static ImagePanel getImagePanel() {
    return imagePanel;
  }

  public static void clearImagePanel() {
    imagePanel.clearPanel();
  }

  public static Map<UIMode, MediaPanel<? extends MediaInfo>> createMediaPanel(MovieRenamer mr) {
    Map<UIMode, MediaPanel<? extends MediaInfo>> panels = new HashMap<>();
    MediaPanel<? extends MediaInfo> panel;
    for (UIMode mode : UIMode.values()) {
      switch (mode) {
        case MOVIEMODE:
          panel = new MoviePanel(mr);
          break;
        case TVSHOWMODE:
          panel = null;// TODO tvshow panel
          break;
        default:
          panel = null;
      }

      panels.put(mode, panel);
    }
    return panels;
  }

  public static void showSettingsDialog() {
    showDialog(settingsDialog);
  }

  public static void showAboutDialog() {
    showDialog(aboutDialog);
  }

  private static void showDialog(final AbstractDialog dialog) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        dialog.setVisible(true);
      }
    });
  }

  private UIManager() {
    throw new UnsupportedOperationException();
  }
}
