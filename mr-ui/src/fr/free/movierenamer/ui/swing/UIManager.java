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

import fr.free.movierenamer.ui.swing.custom.ButtonGroupEnable;
import com.alee.extended.image.DisplayType;
import com.alee.extended.image.WebImage;
import com.alee.global.StyleConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.managers.hotkey.Hotkey;
import com.alee.managers.hotkey.HotkeyData;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.scraper.MediaScraper;
import fr.free.movierenamer.scraper.ScraperManager;
import fr.free.movierenamer.scraper.SearchParam;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.searchinfo.Media.MediaType;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.settings.Settings.SettingsMediaProperty;
import fr.free.movierenamer.settings.XMLSettings;
import fr.free.movierenamer.settings.XMLSettings.IProperty;
import fr.free.movierenamer.settings.XMLSettings.ISimpleProperty;
import fr.free.movierenamer.ui.Main;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.ui.bean.UIMediaInfo;
import fr.free.movierenamer.ui.bean.UIMovieInfo;
import fr.free.movierenamer.ui.bean.UIRename;
import fr.free.movierenamer.ui.bean.UIScraper;
import fr.free.movierenamer.ui.bean.UIUpdate;
import fr.free.movierenamer.ui.bean.settings.UIMovieTestSettings;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.settings.UISettings.UISettingsProperty;
import fr.free.movierenamer.ui.swing.dialog.AboutDialog;
import fr.free.movierenamer.ui.swing.dialog.AbstractDialog;
import fr.free.movierenamer.ui.swing.dialog.HistoryDialog;
import fr.free.movierenamer.ui.swing.dialog.LoggerDialog;
import fr.free.movierenamer.ui.swing.dialog.MediaInfoDownloadDialog;
import fr.free.movierenamer.ui.swing.panel.ImagePanel;
import fr.free.movierenamer.ui.swing.panel.MediaPanel;
import fr.free.movierenamer.ui.swing.panel.MoviePanel;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import static fr.free.movierenamer.ui.utils.UIUtils.i18n;
import fr.free.movierenamer.ui.worker.impl.CheckUpdateWorker;
import fr.free.movierenamer.utils.LocaleUtils.AppLanguages;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Class UIManager
 *
 * @author Nicolas Magré
 */
public final class UIManager {

    private static AboutDialog aboutDialog;
    private static LoggerDialog logDialog;
    private static ImagePanel imagePanel;
    private static HistoryDialog historyDialog;
    private static final UISettings setting = UISettings.getInstance();
    private static final Map<IProperty, WebCheckBox> checkboxs;
    private static final WebCheckBox nfoChk = new WebCheckBox();
    private static final WebCheckBox thumbChk = new WebCheckBox();
    private static final WebCheckBox fanartChk = new WebCheckBox();
    private static final WebCheckBox logoChk = new WebCheckBox();
    private static final WebCheckBox cdartChk = new WebCheckBox();
    private static final WebCheckBox clearartChk = new WebCheckBox();
    private static final WebCheckBox bannerChk = new WebCheckBox();
    private static final List<JComponent> movieRenameSettingsCmp;
    public static final ButtonGroupEnable group = new ButtonGroupEnable();
    public static final XMLSettings.SettingsType formatTypeTestLoc = XMLSettings.SettingsType.FILE;// Button test location
    public static final XMLSettings.SettingsSubType formatSubTypeTestLoc = XMLSettings.SettingsSubType.GENERAL;// Button test location

    static {
        checkboxs = new HashMap<>();
        checkboxs.put(SettingsMediaProperty.mediaNfogenerate, nfoChk);
        checkboxs.put(UISettingsProperty.downloadThumb, thumbChk);
        checkboxs.put(UISettingsProperty.downloadFanart, fanartChk);
        checkboxs.put(UISettingsProperty.downloadLogo, logoChk);
        checkboxs.put(UISettingsProperty.downloadCdart, cdartChk);
        checkboxs.put(UISettingsProperty.downloadClearart, clearartChk);
        checkboxs.put(UISettingsProperty.downloadBanner, bannerChk);

        // Add option needed for movie
        movieRenameSettingsCmp = new ArrayList<>();
        movieRenameSettingsCmp.add(nfoChk);
        movieRenameSettingsCmp.add(thumbChk);
        movieRenameSettingsCmp.add(fanartChk);
        movieRenameSettingsCmp.add(logoChk);
        movieRenameSettingsCmp.add(cdartChk);
        movieRenameSettingsCmp.add(clearartChk);
        movieRenameSettingsCmp.add(bannerChk);

    }

    public enum UIMode {

        MOVIEMODE(UIUtils.i18n.getLanguageKey("movieMode", "toptb"), MediaType.MOVIE, ImageUtils.MOVIE_24, ImageUtils.MOVIE_16, Hotkey.CTRL_M,
                true, true, MovieInfo.class, UIMovieInfo.class) {

                    @Override
                    public MediaPanel<? extends UIMediaInfo<?>, ? extends MediaInfo> getModePanel(MovieRenamer mr) {
                        return new MoviePanel(mr);
                    }

                    @Override
                    public XMLSettings.IMediaProperty getFormatTest() {
                        return new UIMovieTestSettings(formatTypeTestLoc, formatSubTypeTestLoc);
                    }

                    @Override
                    public SearchParam getSearchExtraParam(UIFile media) {
                        SearchParam sep = new SearchParam();
                        sep.setProperty("year", Integer.toString(media.getYear()));
                        
                        return sep;
                    }

                };//TVSHOWMODE(UIUtils.i18n.getLanguageKey("tvshowMode", "toptb"), "tvshowMode", MediaType.TVSHOW, ImageUtils.TV_24, ImageUtils.TV_16, Hotkey.CTRL_T, TvShowInfo.class, TvShowInfo.class),

        private final MediaType mediaType;
        private final String title;
        private final Icon icon_24;
        private final Icon icon_16;
        private final HotkeyData hotkey;
        private final DefaultComboBoxModel<UIScraper> scraperModel = new DefaultComboBoxModel<>();
        private final Class<?> infoClazz;
        private final Class<?> uiInfoClazz;
        private final IProperty[] renameOptions;
        private final boolean hasImage;
        private final boolean hasTrailer;
        private MediaPanel<? extends UIMediaInfo<?>, ? extends MediaInfo> panel;
        private WebButton modeButton = null;
        private String fileFormat;

        private UIMode(String title, MediaType mediaType, Icon icon_24, Icon icon_16, HotkeyData hotkey, boolean hasImage, boolean hasTrailer,
                Class<?> infoClazz, Class<?> uiInfoClazz, IProperty... renameOptions) {

            this.title = title;
            this.mediaType = mediaType;
            this.renameOptions = renameOptions;
            this.icon_24 = icon_24;
            this.icon_16 = icon_16;
            this.hotkey = hotkey;
            this.infoClazz = infoClazz;
            this.uiInfoClazz = uiInfoClazz;
            this.hasImage = hasImage;
            this.hasTrailer = hasTrailer;
            fileFormat = Settings.getInstance().getMediaFilenameFormat(mediaType);

            List<MediaScraper> mediaScrapers = ScraperManager.getMediaScrapers(mediaType);
            for (MediaScraper mediaScraper : mediaScrapers) {
                scraperModel.addElement(new UIScraper(mediaScraper));
            }
            scraperModel.setSelectedItem(new UIScraper(ScraperManager.getMediaScraper(mediaType)));
        }

        public String getTitle() {
            return title;
        }

        public Media.MediaType getMediaType() {
            return mediaType;
        }

        public Icon getBigIcon() {
            return icon_24;
        }

        public Icon getSmallIcon() {
            return icon_16;
        }

        public DefaultComboBoxModel<UIScraper> getScraperModel() {
            return scraperModel;
        }

        public UIScraper getSelectedScraper() {
            return (UIScraper) scraperModel.getSelectedItem();
        }

        public void setUniversalScraper() {
            UIScraper scraper;
            for (int i = 0; i < scraperModel.getSize(); i++) {
                scraper = scraperModel.getElementAt(i);
                if (scraper.getName().equals("Universal")) {
                    scraperModel.setSelectedItem(scraper);
                    break;
                }
            }
        }

        public void setScraper(UIScraper scraper) {
            if (scraperModel.getIndexOf(scraper) >= 0) {
                scraperModel.setSelectedItem(scraper);
            }
        }

        public String getFileFormat() {
            return fileFormat;
        }

        public void setFileformat(String fileFormat) {
            this.fileFormat = fileFormat;
        }

        public UIMediaInfo<?> toUIInfo(MediaInfo info) throws Exception {
            return (UIMediaInfo<?>) uiInfoClazz.getConstructor(infoClazz).newInstance(info);
        }

        public boolean hasImage() {
            return hasImage;
        }

        public boolean hasTrailer() {
            return hasTrailer;
        }

        public WebButton getModebutton(final MovieRenamer mr) {
            if (modeButton != null) {
                return modeButton;
            }

            modeButton = UIUtils.createButton(title, icon_24, icon_16, hotkey, mr);
            group.add(modeButton);

            final UIMode mode = this;
            modeButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    mr.setMode(mode);
                    group.setSelected(((WebButton) ae.getSource()).getModel(), true);
                }
            });

            return modeButton;
        }

        public MediaPanel<? extends UIMediaInfo<?>, ? extends MediaInfo> getMediaPanel(MovieRenamer mr) {
            if (panel != null) {
                return panel;
            }
            panel = getModePanel(mr);

            return panel;
        }

        protected abstract MediaPanel<? extends UIMediaInfo<?>, ? extends MediaInfo> getModePanel(MovieRenamer mr);

        public abstract XMLSettings.IMediaProperty getFormatTest();

        public abstract SearchParam getSearchExtraParam(UIFile media);

    }

    public static void init(MovieRenamer mr) {

        // Create dialogs
        aboutDialog = new AboutDialog(mr);
        imagePanel = new ImagePanel(mr);
        logDialog = new LoggerDialog(mr);
        historyDialog = new HistoryDialog(mr);

        nfoChk.setText("NFO");
        thumbChk.setLanguage(i18n.getLanguageKey("image.thumb"));
        fanartChk.setLanguage(i18n.getLanguageKey("image.fanart"));
        logoChk.setLanguage(i18n.getLanguageKey("image.logo"));
        cdartChk.setLanguage(i18n.getLanguageKey("image.cdart"));
        clearartChk.setLanguage(i18n.getLanguageKey("image.clearart"));
        bannerChk.setLanguage(i18n.getLanguageKey("image.banner"));

        // Set checkbox selected
        for (Entry<IProperty, WebCheckBox> entry : checkboxs.entrySet()) {
            //entry.getValue().setSelected(Boolean.parseBoolean(entry.getKey().getValue())); // FIXME set checkbox state
        }

    }

    public static void setCheckBox(ISimpleProperty property) {
        checkboxs.get(property).setSelected(Boolean.parseBoolean(property.getValue()));
    }

    public static ImagePanel getImagePanel() {
        return imagePanel;
    }

    public static void clearImagePanel() {
        imagePanel.clearPanel();
    }

    public static Map<UIMode, MediaPanel<? extends UIMediaInfo<?>, ? extends MediaInfo>> createMediaPanel(MovieRenamer mr) {
        Map<UIMode, MediaPanel<? extends UIMediaInfo<?>, ? extends MediaInfo>> panels = new HashMap<>();
        for (UIMode mode : UIMode.values()) {
            panels.put(mode, mode.getMediaPanel(mr));
        }
        return panels;
    }

    public static void showAboutDialog() {
        showDialog(aboutDialog);
    }

    public static void showLogDialog() {
        showDialog(logDialog);
    }

    public static void showHistoryDialog() {
        showDialog(historyDialog);
    }

    private static void showDialog(final AbstractDialog dialog) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dialog.setVisible(true);
            }
        });
    }

    public static void update(MovieRenamer mr, UIUpdate update) {// TODO Check if it's standalone version
        String description = setting.coreInstance.getAppLanguage().equals(AppLanguages.fr) ? update.getDescfr() : update.getDescen();
        String str = "<html>" + i18n.getLanguage("dialog.updateAvailable", false, update.getUpdateVersion(), description).replace("\n", "<br>").replace("\\n", "<br>") + "<br><br></html>";
        int n = WebOptionPane.showConfirmDialog(mr, str, UIUtils.i18n.getLanguage("dialog.question", false), WebOptionPane.YES_NO_OPTION, WebOptionPane.QUESTION_MESSAGE);

        if (n > 0) {
            return;
        }

        String version = UISettings.getApplicationVersionNumber();
        String updateDir = Settings.APPFOLDER + File.separator + "update";
        String installDir = "";
        try {
            installDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath();
        } catch (URISyntaxException ex) {
            // TODO
            UISettings.LOGGER.log(Level.SEVERE, null, ex);
        }

        // TODO check if "installDir" is writable. 
        try {
            String javaBin = System.getProperty("java.home") + "/bin/java";
            File jarFile = new File(installDir + File.separator + "lib" + File.separator + "Mr-updater.jar");
            String toExec[];

            if (setting.coreInstance.isProxyIsOn()) {
                String host = setting.coreInstance.isProxySocks() ? "-DsocksProxyHost" : "-Dhttp.proxyHost=";
                String port = setting.coreInstance.isProxySocks() ? "-DsocksProxyPort" : "-Dhttp.proxyPort=";
                String user = setting.coreInstance.getProxyUser();
                String pass = new String(setting.coreInstance.getProxyPass());

                if (user.length() > 0) {
                    toExec = new String[]{javaBin, host + setting.coreInstance.getProxyUrl(),
                        port + setting.coreInstance.getProxyPort(), "-Dhttp.proxyUser" + user, "-Dhttp.proxyPassword" + pass,
                        "-jar", jarFile.getPath(), version, installDir, updateDir};// FIXME really unsecure, password is visible in process list :-(
                } else {
                    toExec = new String[]{javaBin, host + setting.coreInstance.getProxyUrl(),
                        port + setting.coreInstance.getProxyPort(), "-jar", jarFile.getPath(), version, installDir, updateDir};
                }

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
                uirename = new UIRename(mediaFile, format, options, uimode.getMediaType());
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

        return movieRenameSettingsCmp;
    }

    public static void startInitTimer(final MovieRenamer mr, final WebLabel mediainfoStatusLbl) {
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
                    UIUtils.showWarningNotification(i18n.getLanguage("error.mediaInfoNotInstalled", false));
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
        } else {
            // check for update
            if (setting.isCheckupdate()) {
                updateTimer.start();
            }
        }

    }

    private UIManager() {
        throw new UnsupportedOperationException();
    }
}
