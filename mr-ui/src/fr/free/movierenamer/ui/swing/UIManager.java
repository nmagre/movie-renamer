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

import com.alee.extended.image.DisplayType;
import com.alee.extended.image.WebImage;
import com.alee.laf.StyleConstants;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.optionpane.WebOptionPane;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.scrapper.ScrapperManager;
import fr.free.movierenamer.scrapper.TvShowScrapper;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.ui.Main;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.ui.bean.UIMode;
import fr.free.movierenamer.ui.bean.UIRename;
import fr.free.movierenamer.ui.bean.UIScraper;
import fr.free.movierenamer.ui.bean.UIUpdate;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.swing.dialog.AboutDialog;
import fr.free.movierenamer.ui.swing.dialog.AbstractDialog;
import fr.free.movierenamer.ui.swing.dialog.LoggerDialog;
import fr.free.movierenamer.ui.swing.dialog.SettingDialog;
import fr.free.movierenamer.ui.swing.panel.ImagePanel;
import fr.free.movierenamer.ui.swing.panel.MediaPanel;
import fr.free.movierenamer.ui.swing.panel.MoviePanel;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import static fr.free.movierenamer.ui.utils.UIUtils.i18n;
import fr.free.movierenamer.utils.LocaleUtils.AppLanguages;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Class PanelManager
 *
 * @author Nicolas Magré
 */
public final class UIManager {

  private static SettingDialog settingsDialog;
  private static AboutDialog aboutDialog;
  private static LoggerDialog logDialog;
  private static ImagePanel imagePanel;
  private static final UISettings setting = UISettings.getInstance();
  private static final Map<Settings.IProperty, WebCheckBox> checkboxs;
  private static final WebCheckBox nfoChk = new WebCheckBox();
  private static final WebCheckBox thumbChk = new WebCheckBox();
  private static final WebCheckBox fanartChk = new WebCheckBox();
  private static final WebCheckBox logoChk = new WebCheckBox();
  private static final WebCheckBox cdartChk = new WebCheckBox();
  private static final WebCheckBox clearartChk = new WebCheckBox();
  private static final WebCheckBox bannerChk = new WebCheckBox();
  private static final List<JComponent> movieRenameSettingsCmp;

  //private static final List<JComponent> tvshowRenameSettingsCmp;
  static {
    checkboxs = new HashMap<>();
    checkboxs.put(Settings.SettingsProperty.movieNfogenerate, nfoChk);
    checkboxs.put(UISettings.UISettingsProperty.generateThumb, thumbChk);
    checkboxs.put(UISettings.UISettingsProperty.generateFanart, fanartChk);
    checkboxs.put(UISettings.UISettingsProperty.generateLogo, logoChk);
    checkboxs.put(UISettings.UISettingsProperty.generateCdart, cdartChk);
    checkboxs.put(UISettings.UISettingsProperty.generateClearart, clearartChk);
    checkboxs.put(UISettings.UISettingsProperty.generateBanner, bannerChk);

    movieRenameSettingsCmp = new ArrayList<>();
    movieRenameSettingsCmp.add(nfoChk);
    movieRenameSettingsCmp.add(thumbChk);
    movieRenameSettingsCmp.add(fanartChk);
    movieRenameSettingsCmp.add(logoChk);
    movieRenameSettingsCmp.add(cdartChk);
    movieRenameSettingsCmp.add(clearartChk);
    movieRenameSettingsCmp.add(bannerChk);

  }

  public static void init(MovieRenamer mr) {

    settingsDialog = new SettingDialog(mr);
    aboutDialog = new AboutDialog(mr);
    imagePanel = new ImagePanel(mr);
    logDialog = new LoggerDialog(mr);

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

    nfoChk.setText("NFO");
    thumbChk.setLanguage(i18n.getLanguageKey("image.thumb"));
    fanartChk.setLanguage(i18n.getLanguageKey("image.fanart"));
    logoChk.setLanguage(i18n.getLanguageKey("image.logo"));
    cdartChk.setLanguage(i18n.getLanguageKey("image.cdart"));
    clearartChk.setLanguage(i18n.getLanguageKey("image.clearart"));
    bannerChk.setLanguage(i18n.getLanguageKey("image.banner"));

    // Set checkbox selected
    for (Entry<Settings.IProperty, WebCheckBox> entry : checkboxs.entrySet()) {
      entry.getValue().setSelected(Boolean.parseBoolean(entry.getKey().getValue()));
    }

  }

  public static void setCheckBox(Settings.IProperty property) {
    checkboxs.get(property).setSelected(Boolean.parseBoolean(property.getValue()));
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

  public static void showLogDialog() {
    showDialog(logDialog);
  }

  private static void showDialog(final AbstractDialog dialog) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        dialog.setVisible(true);
      }
    });
  }

  public static void update(MovieRenamer mr, UIUpdate update) {
    String description = setting.coreInstance.getAppLanguage().equals(AppLanguages.fr) ? update.getDescfr() : update.getDescen();
    String str = "<html>" + i18n.getLanguage("dialog.updateAvailable", false, update.getUpdateVersion(), description).replace("\n", "<br>").replace("\\n", "<br>") + "<br><br></html>";
    System.out.println(str);
    int n = WebOptionPane.showConfirmDialog(mr, str, UIUtils.i18n.getLanguage("dialog.question", false), WebOptionPane.YES_NO_OPTION, WebOptionPane.QUESTION_MESSAGE);

    if (n > 0) {
      return;
    }

    String version = UISettings.getApplicationVersionNumber();
    String updateDir = Settings.appFolder + File.separator + "update";
    String installDir = "";
    try {
      installDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath();
    } catch (URISyntaxException ex) {
      // TODO
      UISettings.LOGGER.log(Level.SEVERE, null, ex);
    }

    // TODO check if "installDir" is writable
    try {
      String javaBin = System.getProperty("java.home") + "/bin/java";
      File jarFile = new File(installDir + File.separator + "lib" + File.separator + "Mr-updater.jar");
      String toExec[];

      if (setting.coreInstance.isProxyIsOn()) {
        toExec = new String[]{javaBin, "-Dhttp.proxyHost=" + setting.coreInstance.getProxyUrl(),
          "-Dhttp.proxyPort=" + setting.coreInstance.getProxyPort(), "-jar", jarFile.getPath(), version, installDir, updateDir};
      } else {
        toExec = new String[]{javaBin, "-jar", jarFile.getPath(), version, installDir, updateDir};
      }

      Process p = Runtime.getRuntime().exec(toExec);

      System.exit(0);
    } catch (Exception ex) {
      WebOptionPane.showMessageDialog(mr, "Restart failed :(", "error", WebOptionPane.ERROR_MESSAGE);// FIXME i18n
    }
  }

  /**
   * Create a background panel for startup animation
   *
   * @return WebImage
   */
  public static JComponent createBackgroundPanel() {
    WebImage wi = new WebImage(ImageUtils.BAN) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(new LinearGradientPaint(0, 0, 0, getHeight(), new float[]{0f, 0.4f, 0.6f, 1f},
                new Color[]{StyleConstants.bottomBgColor, Color.WHITE, Color.WHITE, StyleConstants.bottomBgColor}));
        g2d.fill(g2d.getClip() != null ? g2d.getClip() : getVisibleRect());

        super.paintComponent(g);
      }
    };
    wi.setDisplayType(DisplayType.fitComponent);
    wi.setHorizontalAlignment(SwingConstants.CENTER);
    wi.setVerticalAlignment(SwingConstants.CENTER);
    return wi;
  }

  public static UIRename getUIRenamer(UIFile mediaFile, String format, UIMode uimode) {

    UIRename uirename = null;
    Map<UIRename.RenameOption, Boolean> options = new HashMap<>();
    switch (uimode) {
      case MOVIEMODE:
        options.put(UIRename.RenameOption.NFO, nfoChk.isSelected());
        options.put(UIRename.RenameOption.THUMB, thumbChk.isSelected());
        options.put(UIRename.RenameOption.FANART, fanartChk.isSelected());
        options.put(UIRename.RenameOption.LOGO, logoChk.isSelected());
        options.put(UIRename.RenameOption.CDART, cdartChk.isSelected());
        options.put(UIRename.RenameOption.CLEARART, clearartChk.isSelected());
        options.put(UIRename.RenameOption.BANNER, bannerChk.isSelected());
        uirename = new UIRename(mediaFile, format, options);
        break;
      //TODO tvshow
    }

    return uirename;
  }

  public static List<JComponent> getRenameSettingsComponents(UIMode mode) {
    switch (mode) {
      case MOVIEMODE:
        return movieRenameSettingsCmp;
    }

    return null;
  }

  private UIManager() {
    throw new UnsupportedOperationException();
  }
}
