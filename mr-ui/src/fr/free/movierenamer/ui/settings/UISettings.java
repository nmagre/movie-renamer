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
package fr.free.movierenamer.ui.settings;

import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class Settings , Movie Renamer settings Only public and non static attributes
 * are written in conf file !
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public final class UISettings {

  static {
    String appName = getApplicationProperty("application.name");
    String appNameNospace = appName.replace(' ', '_');
    APPNAME = appName;
    VERSION = getApplicationProperty("application.version");
    CORE_VERSION = Settings.VERSION;
    userFolder = System.getProperty("user.home");
    appFolder = Settings.getApplicationFolder();
    configFile = appNameNospace + ".conf";
    renamedFile = "renamed.xml";
    logFile = appNameNospace + ".log";
    LOGGER = Logger.getLogger(appNameNospace + " Logger");
    appSettingsNodeName = appNameNospace;
    settingNodeName = "settings";
  }
  public static final String APPNAME;
  public static final String VERSION;
  public static final String CORE_VERSION;
  public static final File appFolder;
  private static final String userFolder;
  // files
  public static final String configFile;
  public static final String renamedFile;
  private static final String logFile;
  // Logger
  public static final Logger LOGGER;
  // Misc
//  private static boolean xmlError = false;
//  private static String xmlVersion = "";
  // Settings instance
  private static final UISettings instance = new UISettings();
  public final Settings coreInstance = Settings.getInstance();
  // Settings xml conf instance
  private final Document settingsDocument;
  private final Node settingsNode;
  private static final String appSettingsNodeName;
  private static final String settingNodeName;

  public enum SettingLevel {

    NORMAL,
    ADVANCED;
  }

  public enum SettingProvider {

    CORE,
    UI;
  }

  public enum UISettingsProperty implements Settings.IProperty {

    selectFirstMedia(Boolean.TRUE),
    selectFirstResult(Boolean.TRUE),
    scanSubfolder(Boolean.TRUE),
    checkUpdate(Boolean.TRUE),
    showMediaPanel(Boolean.TRUE),
    showActorImage(Boolean.TRUE),
    showThumb(Boolean.TRUE),
    showFanart(Boolean.TRUE),
    showSubtitle(Boolean.FALSE),
    showCdart(Boolean.TRUE),
    showClearart(Boolean.TRUE),
    showLogo(Boolean.TRUE),
    showBanner(Boolean.TRUE),
    generateThumb(Boolean.TRUE),
    generateFanart(Boolean.TRUE),
    generateSubtitles(Boolean.FALSE),
    useExtensionFilter(Boolean.TRUE),
    fileChooserPath(userFolder),
    extensionsList(new String[]{"mkv", "avi", "wmv", "mp4", "m4v", "mov", "ts", "m2ts", "ogm", "mpg", "mpeg", "flv", "iso", "rm", "mov", "asf"}),
    showAdvancedSettings(Boolean.FALSE);
    private Class<?> vclass;
    private Object defaultValue;

    private UISettingsProperty(Object defaultValue) {
      this.vclass = defaultValue.getClass();
      this.defaultValue = defaultValue;
    }

    @Override
    public Class<?> getVclass() {
      return vclass;
    }

    @Override
    public Object getDefaultValue() {
      return defaultValue;
    }

    @Override
    public String getValue() {
      return instance.get(this);
    }

    @Override
    public void setValue(Object object) throws IOException {
      instance.set(this, object);
    }
  }

  public static enum SettingsProperty {

    // UI
    selectFirstMedia(UISettingsProperty.selectFirstMedia, SettingProvider.UI),
    selectFirstResult(UISettingsProperty.selectFirstResult, SettingProvider.UI),
    scanSubfolder(UISettingsProperty.scanSubfolder, SettingProvider.UI),
    checkUpdate(UISettingsProperty.checkUpdate, SettingProvider.UI),
    showMediaPanel(UISettingsProperty.showMediaPanel, SettingProvider.UI),
    showActorImage(UISettingsProperty.showActorImage, SettingProvider.UI),
    showThumb(UISettingsProperty.showThumb, SettingProvider.UI),
    showFanart(UISettingsProperty.showFanart, SettingProvider.UI),
    showSubtitle(UISettingsProperty.showSubtitle, SettingProvider.UI),
    showCdart(UISettingsProperty.showCdart, SettingProvider.UI),
    showClearart(UISettingsProperty.showClearart, SettingProvider.UI),
    showLogo(UISettingsProperty.showLogo, SettingProvider.UI),
    showBanner(UISettingsProperty.showBanner, SettingProvider.UI),
    thumb(UISettingsProperty.generateThumb, SettingProvider.UI),
    fanart(UISettingsProperty.generateFanart, SettingProvider.UI),
    subtitles(UISettingsProperty.generateSubtitles, SettingProvider.UI),
    useExtensionFilter(UISettingsProperty.useExtensionFilter, SettingProvider.UI),
    showAdvancedSettings(UISettingsProperty.showAdvancedSettings, SettingProvider.UI),
    fileChooserPath(UISettingsProperty.fileChooserPath, SettingProvider.UI),
    extensionsList(UISettingsProperty.extensionsList, SettingProvider.UI),
    // CORE
    appLanguage(Settings.SettingsProperty.appLanguage),
    // movie filename
    movieFilenameFormat(Settings.SettingsProperty.movieFilenameFormat),
    movieFilenameSeparator(Settings.SettingsProperty.movieFilenameSeparator),
    movieFilenameLimit(Settings.SettingsProperty.movieFilenameLimit),
    movieFilenameCase(Settings.SettingsProperty.movieFilenameCase),
    movieFilenameTrim(Settings.SettingsProperty.movieFilenameTrim),
    movieFilenameRmDupSpace(Settings.SettingsProperty.movieFilenameRmDupSpace),
    movieFilenameCreateDirectory(Settings.SettingsProperty.movieFilenameCreateDirectory),
    // movie folder
    movieFolderFormat(Settings.SettingsProperty.movieFolderFormat),
    movieFolderSeparator(Settings.SettingsProperty.movieFolderSeparator),
    movieFolderLimit(Settings.SettingsProperty.movieFolderLimit),
    movieFolderCase(Settings.SettingsProperty.movieFolderCase),
    movieFolderTrim(Settings.SettingsProperty.movieFolderTrim),
    movieFolderRmDupSpace(Settings.SettingsProperty.movieFolderRmDupSpace),
    // movie NFO
    movieNfoType(Settings.SettingsProperty.movieNfoType),
    // tvShow
    tvShowFilenameFormat(Settings.SettingsProperty.tvShowFilenameFormat),
    tvShowFilenameSeparator(Settings.SettingsProperty.tvShowFilenameSeparator),
    tvShowFilenameLimit(Settings.SettingsProperty.tvShowFilenameLimit),
    tvShowFilenameCase(Settings.SettingsProperty.tvShowFilenameCase),
    tvShowFilenameTrim(Settings.SettingsProperty.tvShowFilenameTrim),
    tvShowFilenameRmDupSpace(Settings.SettingsProperty.tvShowFilenameRmDupSpace),
    // Cache
    cacheClear(Settings.SettingsProperty.cacheClear),
    // Search
    searchMovieScrapper(Settings.SettingsProperty.searchMovieScrapper),
    searchTvshowScrapper(Settings.SettingsProperty.searchTvshowScrapper),
    searchSubtitleScrapper(Settings.SettingsProperty.searchSubtitleScrapper),
    searchScrapperLang(Settings.SettingsProperty.searchScrapperLang),
    searchSortBySimiYear(Settings.SettingsProperty.searchSortBySimiYear),
    searchNbResult(Settings.SettingsProperty.searchNbResult),
    searchDisplayApproximateResult(Settings.SettingsProperty.searchDisplayApproximateResult),
    // Proxy
    proxyIsOn(Settings.SettingsProperty.proxyIsOn),
    proxyUrl(Settings.SettingsProperty.proxyUrl),
    proxyPort(Settings.SettingsProperty.proxyPort),
    // http param
    httpRequestTimeOut(Settings.SettingsProperty.httpRequestTimeOut, SettingLevel.ADVANCED),
    httpCustomUserAgent(Settings.SettingsProperty.httpCustomUserAgent, SettingLevel.ADVANCED);
    private String lib;
    private SettingProvider provider = SettingProvider.CORE;
    private Class<?> vclass;
    private SettingLevel level = SettingLevel.NORMAL;
    private Settings.IProperty key;

    private SettingsProperty(Settings.IProperty key) {// Only for CORE
      this.key = key;
      this.lib = name();
      this.vclass = key.getVclass();
    }

    private SettingsProperty(Settings.IProperty key, SettingLevel level) {// Only for CORE
      this(key);
      this.level = level;
    }

    private SettingsProperty(Settings.IProperty key, SettingProvider provider) {// Only for UI
      this(key);
      this.provider = provider;
    }

    public Class<?> getVClass() {
      return vclass;
    }

    public SettingProvider getProvider() {
      return provider;
    }

    public SettingLevel getLevel() {
      return level;
    }

    public Settings.IProperty getKey() {
      return key;
    }

    @Override
    public String toString() {
      return lib;
    }
  }

  /**
   * Access to the Settings instance
   *
   * @return The only instance of MR Settings
   */
  public static synchronized UISettings getInstance() {
    return instance;
  }

  /**
   * Constructor
   */
  private UISettings() {
    // Log init
    try {
      File logsRoot = new File(UISettings.appFolder, "logs");
      if (!logsRoot.isDirectory() && !logsRoot.mkdirs()) {
        throw new IOException("Failed to create logs dir: " + logsRoot);
      }
      FileHandler fh = new FileHandler(logsRoot.getAbsolutePath() + File.separator + logFile);
      LOGGER.addHandler(fh);
    } catch (SecurityException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }

    // settingsDocument init
    Document settingsDocument;
    Node settingsNode;
    try {
      File confRoot = new File(UISettings.appFolder, "conf");
      File file = new File(confRoot, configFile);
      settingsDocument = URIRequest.getXmlDocument(file.toURI());
      Node appSettingsNode = XPathUtils.selectNode(appSettingsNodeName, settingsDocument);
      if (!VERSION.equals(XPathUtils.getAttribute("Version", appSettingsNode))) {
        throw new NullPointerException("App version is different");
      }
      settingsNode = XPathUtils.selectNode(settingNodeName, appSettingsNode);
      // TODO convert if version are diff !
    } catch (Exception ex) {
      try {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        docBuilder = docFactory.newDocumentBuilder();

        // root elements
        settingsDocument = docBuilder.newDocument();
        Element rootElement = settingsDocument.createElement(appSettingsNodeName);
        settingsDocument.appendChild(rootElement);

        Attr version = settingsDocument.createAttribute("Version");
        version.setValue(VERSION);
        rootElement.setAttributeNode(version);

        // setting elements
        settingsNode = settingsDocument.createElement(settingNodeName);
        rootElement.appendChild(settingsNode);

      } catch (ParserConfigurationException ex1) {
        settingsDocument = null;
        settingsNode = null;
      }
    }
    this.settingsDocument = settingsDocument;
    this.settingsNode = settingsNode;
    try {
      saveSetting();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
  }

  private synchronized String get(UISettingsProperty key) {
    String value;
    if (key != null) {
      Node found = XPathUtils.selectNode(key.name(), settingsNode);
      if (found != null) {
        value = XPathUtils.getTextContent(found);
      } else {
        value = null;
      }
    } else {
      value = null;
    }
    if (value == null) {
      value = key.getDefaultValue().toString();
    }
    return value;
  }

  private synchronized void set(UISettingsProperty key, Object value) throws IOException {
    if (value != null && key != null) {
      Object savedValue = key.getValue();

      if (savedValue.toString().equals(value.toString())) {
        return;
      }

      Node found = XPathUtils.selectNode(key.name(), this.settingsNode);
      if (found == null) {
        found = settingsDocument.createElement(key.name());
        // param.appendChild(settingsDocument.createTextNode(value.toString()));
        this.settingsNode.appendChild(found);
      }
      found.setTextContent(value.toString());
      saveSetting();
    }
  }

  public synchronized void clear() throws IOException {
    Logger.getLogger(Settings.class.getName()).log(Level.INFO, String.format("Clear UISettings"));
    NodeList list = this.settingsNode.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      this.settingsNode.removeChild(list.item(i));
    }
    saveSetting();
  }

  /**
   * Save setting
   *
   * @return True if setting was saved, False otherwise
   */
  private synchronized boolean saveSetting() throws IOException {
    boolean saveSuccess;
    try {
      LOGGER.log(Level.INFO, "Save configuration to {0}", configFile);
      File confRoot = new File(Settings.appFolder, "conf");
      if (!confRoot.isDirectory() && !confRoot.mkdirs()) {
        throw new IOException("Failed to create conf dir: " + confRoot);
      }
      try {
        // write it to file
        File confFile = new File(confRoot, configFile);
        FileUtils.writeXmlFile(settingsDocument, confFile);
        saveSuccess = true;
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, e.getMessage());
        saveSuccess = false;
      }
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, ex.getMessage());
      saveSuccess = false;
    }
    return saveSuccess;
  }

  public boolean isSelectFirstMedia() {
    return Boolean.parseBoolean(get(UISettings.UISettingsProperty.selectFirstMedia));
  }

  public boolean isSelectFirstResult() {
    return Boolean.parseBoolean(get(UISettings.UISettingsProperty.selectFirstResult));
  }

  public boolean isScanSubfolder() {
    return Boolean.parseBoolean(get(UISettings.UISettingsProperty.scanSubfolder));
  }

  public boolean isCheckUpdate() {
    return Boolean.parseBoolean(get(UISettings.UISettingsProperty.checkUpdate));
  }

  public boolean isShowActorImage() {
    return Boolean.parseBoolean(get(UISettings.UISettingsProperty.showActorImage));
  }

  public boolean isShowThumb() {
    return Boolean.parseBoolean(get(UISettings.UISettingsProperty.showThumb));
  }

  public boolean isShowMediaPanel() {
    return Boolean.parseBoolean(get(UISettings.UISettingsProperty.showMediaPanel));
  }

  public boolean isShowFanart() {
    return Boolean.parseBoolean(get(UISettings.UISettingsProperty.showFanart));
  }

  public boolean isShowSubtitle() {
    return Boolean.parseBoolean(get(UISettings.UISettingsProperty.showSubtitle));
  }

  public boolean isShowCdart() {
    return Boolean.parseBoolean(get(UISettings.UISettingsProperty.showCdart));
  }

  public boolean isShowClearart() {
    return Boolean.parseBoolean(get(UISettings.UISettingsProperty.showClearart));
  }

  public boolean isShowLogo() {
    return Boolean.parseBoolean(get(UISettings.UISettingsProperty.showLogo));
  }

  public boolean isShowBanner() {
    return Boolean.parseBoolean(get(UISettings.UISettingsProperty.showBanner));
  }

  public boolean isGenerateThumb() {
    return Boolean.parseBoolean(get(UISettings.UISettingsProperty.generateThumb));
  }

  public boolean isGenerateFanart() {
    return Boolean.parseBoolean(get(UISettings.UISettingsProperty.generateFanart));
  }

  public boolean isGenerateSubtitles() {
    return Boolean.parseBoolean(get(UISettings.UISettingsProperty.generateSubtitles));
  }

  public boolean isUseExtensionFilter() {
    return Boolean.parseBoolean(get(UISettings.UISettingsProperty.useExtensionFilter));
  }

  public String getFileChooserPath() {
    return get(UISettings.UISettingsProperty.fileChooserPath);
  }

  public String[] getExtensionsList() {
    String res = get(UISettings.UISettingsProperty.extensionsList);
    return res.split(", ");
  }

  public boolean isShowAdvancedSettings() {
    return Boolean.parseBoolean(get(UISettings.UISettingsProperty.showAdvancedSettings));
  }

  public String getVersion() {
    return VERSION;
  }

  private static String getApplicationProperty(String key) {
    return ResourceBundle.getBundle(UISettings.class.getName(), Locale.ROOT).getString(key);
  }
}
