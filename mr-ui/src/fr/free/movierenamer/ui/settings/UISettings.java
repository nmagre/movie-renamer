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

  public static enum SettingLevel {

    NORMAL,
    ADVANCED;
  }

  public static enum SettingProvider {

    CORE,
    UI;
  }

  public static enum UISettingsProperty implements Settings.iProperty {

    selectFirstMedia(Boolean.class),
    selectFirstResult(Boolean.class),
    scanSubfolder(Boolean.class),
    checkUpdate(Boolean.class),
    showMediaPanel(Boolean.class),
    showActorImage(Boolean.class),
    showThumb(Boolean.class),
    showFanart(Boolean.class),
    showSubtitle(Boolean.class),
    showCdart(Boolean.class),
    showClearart(Boolean.class),
    showLogo(Boolean.class),
    showBanner(Boolean.class),
    generateThumb(Boolean.class),
    generateFanart(Boolean.class),
    generateSubtitles(Boolean.class),
    useExtensionFilter(Boolean.class),
    fileChooserPath(String.class),
    extensionsList(List.class);
    private Class<?> vclass;

    private UISettingsProperty(Class<?> vclass) {
      this.vclass = vclass;
    }

    @Override
    public Class<?> getVclass() {
      return vclass;
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
    //fileChooserPath(UISettingsProperty.fileChooserPath, SettingProvider.UI), // We don't want to add this option on interface
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
    searchSubtitleScrapper(Settings.SettingsProperty.searchSubtitleScrapper),// FIXME replace by subtitle scrapper class
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
    private Settings.iProperty key;

    private SettingsProperty(Settings.iProperty key) {// Only for CORE
      this.key = key;
      this.lib = name();
      this.vclass = key.getVclass();
    }

    private SettingsProperty(Settings.iProperty key, SettingLevel level) {// Only for CORE
      this(key);
      this.level = level;
    }

    private SettingsProperty(Settings.iProperty key, SettingProvider provider) {// Only for UI
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

    public Settings.iProperty getKey() {
      return key;
    }

    @Override
    public String toString() {
      return lib;
    }
  }

  /**
   * UI Saved settings
   */
  // public Movie.NFO nfoType = Movie.NFO.XBMC;
//  public boolean checkUpdate = false;
//  public String fileChooserPath = System.getProperty("user.home");
//  public Locale locale = Locale.getDefault();
  // Rename movie filename
//  public String movieFilenameFormat = "";
//  public String movieFilenameSeparator = ", ";
//  public int movieFilenameLimit = 3;
//  public StringUtils.CaseConversionType movieFilenameCase = StringUtils.CaseConversionType.FIRSTLA;
//  public boolean movieFilenameTrim = true;
//  public boolean movieFilenameRmDupSpace = true;
//  public boolean movieFilenameCreateDirectory = false;
  // Renamer movie folder
//  public String movieFolderFormat = "<t> (<y>)";
//  public String movieFolderSeparator = ", ";
//  public int movieFolderLimit = 3;
//  public int movieFolderCase = 1;
//  public boolean movieFolderTrim = true;
//  public boolean movieFolderRmDupSpace = true;
  // Rename Tv show filename
//  public String tvShowFilenameFormat = "<st> S<s>E<e> <et>";
//  public String tvShowFilenameSeparator = ", ";
//  public int tvShowFilenameLimit = 3;
//  public int tvShowFilenameCase = 1;
//  public boolean tvShowFilenameTrim = true;
//  public boolean tvShowFilenameRmDupSpace = true;
  // ImageInfo
//  public int thumbSize = 0;
//  public int fanartSize = 0;
//  public int thumbExt = 0;
  // Filter
//  public String[] extensions = {"mkv", "avi", "wmv", "mp4", "m4v", "mov", "ts", "m2ts", "ogm", "mpg", "mpeg", "flv", "iso", "rm", "mov", "asf"};
//  public static String[] nameFilters = {"notv", "readnfo", "repack", "proper$", "nfo$", "extended.cut", "limitededition", "limited", "k-sual", "extended", "uncut", "n° [0-9][0-9][0-9]", "yestv", "stv", "remastered", "limited", "x264", "bluray",
//    "bd5", "bd9", "hddvd", "hdz", "unrated", "dvdrip", "cinefile", "hdmi", "dvd5", "ac3", "culthd", "dvd9", "remux", "edition.platinum", "frenchhqc", "frenchedit", "h264", "bdrip", "brrip", "hdteam", "hddvdrip", "subhd", "xvid", "divx", "null$",
//    "divx511", "vorbis", "=str=", "www", "ffm", "mp3", "divx5", "dvb", "mpa2", "blubyte", "brmp", "avs", "filmhd", "hd4u", "1080p", "1080i", "720p", "720i", "720", "truefrench", "dts", "french", "vostfr", "1cd", "2cd", "vff", " vo$", " vf ", "hd",
//    " cam$ ", "telesync", " ts ", " tc ", "ntsc", " pal$ ", "dvd-r", "dvdscr", "scr$", "r1", "r2", "r3", "r4", "r5", "wp", "subforced", "dvd", "vcd", "avchd", " md"};
//  public List<String> mediaNameFilters;
//  public boolean useExtensionFilter = true;
  // Cache
//  public boolean clearCache = false;
  // Search
//  public Class<? extends MovieScrapper> movieScrapper = IMDbScrapper.class;
//  public Class<? extends TvShowScrapper> tvshowScrapper = TheTVDBScrapper.class;
//  public Class<? extends SubtitleScrapper> subtitleScrapper = OpenSubtitlesScrapper.class;
//  public Locale movieScrapperLang = Locale.ENGLISH;
//  public Locale tvshowScrapperLang = Locale.ENGLISH;
//  public Locale subtitleScrapperLang = Locale.ENGLISH;
//  public boolean displayThumbResult = true;
//  public boolean autoSearchMedia = true;
//  public boolean selectFrstRes = true;
//  public boolean sortBySimiYear = true;
//  public int nbResult = 2;
//  public boolean displayApproximateResult = false;
//  public String customUserAgent = "Mozilla/5.0 (Windows NT 5.1; rv:10.0.2) Gecko/20100101 Firefox/10.0.2";
  // Proxy
//  public boolean useProxy = true;
//  public String proxyUrl = "10.2.1.10";
//  public int proxyPort = 3128;
//  public int requestTimeOut = 30;
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
    } catch(IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
  }

  public synchronized String get(UISettings.UISettingsProperty key) {// FIXME value need to be initialised for UI settings panel generator (only boolean value are required)
    String value;
    if (key != null) {
      Node found = XPathUtils.selectNode(key.name(), this.settingsNode);
      if (found != null) {
        value = XPathUtils.getTextContent(found);
      } else {
        value = null;
      }
    } else {
      value = null;
    }
    if (value == null) {
      throw new NullPointerException("Setting property is null for key : " + key.name());
    }
    return value;
  }

  public synchronized void set(UISettings.UISettingsProperty key, Object value) throws IOException {
    if (value != null && key != null) {
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

//  /**
//   * Load Movie Renamer settings
//   *
//   * @return Movie Renamer settings
//   */
//  private UISettings loadSetting() throws SettingsSaveFailedException {
//    LOGGER.log(Level.INFO, "Load configuration from {0}", configFile);
//    boolean saved;
//    UISettings config = new UISettings();
//    File confRoot = new File(UISettings.appFolder, "conf");
//    File file = new File(confRoot, configFile);
//
//    if (!file.exists()) {
//      // Define locale on first run
//      if (!Locale.getDefault().equals(Locale.FRENCH)) {
//        config.locale = Locale.ENGLISH;
//      } else {
//        config.locale = Locale.FRENCH;
//        config.movieScrapperLang = Locale.FRENCH;
//        config.tvshowScrapperLang = Locale.FRENCH;
//      }
//
//      try {
//        saved = config.saveSetting();
//      } catch (IOException e) {
//        saved = false;
//      }
//
//      if (!saved) {
//        // Set locale
//        Locale.setDefault((config.locale.equals(Locale.FRENCH) ? Locale.FRENCH : Locale.ENGLISH));// FIXME jre local =fr -> fr else english
//        throw new SettingsSaveFailedException(config, LocaleUtils.i18n("saveSettingsFailed") + " " + appFolder.getAbsolutePath());
//      }
//      return loadSetting();
//    }
//
//    saved = false;
//    try {
//      // Parse Movie Renamer Settings
//      Document xml = URIRequest.getXmlDocument(file.toURI());
//      List<Node> nodes = XPathUtils.selectChildren(appName_nospace + "/setting", xml);
//      for (Node node : nodes) {
//        setValue(node.getNodeName(), XPathUtils.getTextContent(node));
//      }
//
//      // Get xml version
//      xmlVersion = XPathUtils.selectNode(appName_nospace, xml).getAttributes().getNamedItem("Version").getNodeValue();
//
//      // Set locale
//      Locale.setDefault(config.locale);
//      if (VERSION.equals(xmlVersion) && !xmlError) {
//        saved = true;
//      }
//
//    } catch (SAXException ex) {
//      LOGGER.log(Level.SEVERE, ClassUtils.getStackTrace("SAXException", ex.getStackTrace()));
//    } catch (IOException ex) {
//      LOGGER.log(Level.SEVERE, ClassUtils.getStackTrace("IOException : " + ex.getMessage(), ex.getStackTrace()));
//    } finally {
//      if (!saved) {
//        try {
//          saved = config.saveSetting();
//        } catch (IOException e) {
//          saved = false;
//        }
//      }
//    }
//
//    if (!saved) {
//      throw new SettingsSaveFailedException(config, LocaleUtils.i18n("saveSettingsFailed") + " " + appFolder.getAbsolutePath());
//    }
//
//    return config;
//  }
  /**
   * Save setting
   *
   * @return True if setting was saved, False otherwise
   */
  private synchronized boolean saveSetting() throws IOException{
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
    try {
      return Boolean.parseBoolean(get(UISettings.UISettingsProperty.selectFirstMedia));
    } catch (Exception ex) {
      return Boolean.TRUE;
    }
  }

  public boolean isSelectFirstResult() {
    try {
      return Boolean.parseBoolean(get(UISettings.UISettingsProperty.selectFirstResult));
    } catch (Exception ex) {
      return Boolean.TRUE;
    }
  }

  public boolean isScanSubfolder() {
    try {
      return Boolean.parseBoolean(get(UISettings.UISettingsProperty.scanSubfolder));
    } catch (Exception ex) {
      return Boolean.TRUE;
    }
  }

  public boolean isCheckUpdate() {
    try {
      return Boolean.parseBoolean(get(UISettings.UISettingsProperty.checkUpdate));
    } catch (Exception ex) {
      return Boolean.TRUE;
    }
  }

  public boolean isShowActorImage() {
    try {
      return Boolean.parseBoolean(get(UISettings.UISettingsProperty.showActorImage));
    } catch (Exception ex) {
      return Boolean.TRUE;
    }
  }

  public boolean isShowThumb() {
    try {
      return Boolean.parseBoolean(get(UISettings.UISettingsProperty.showThumb));
    } catch (Exception ex) {
      return Boolean.TRUE;
    }
  }

  public boolean isShowMediaPanel() {
    try {
      return Boolean.parseBoolean(get(UISettings.UISettingsProperty.showMediaPanel));
    } catch (Exception ex) {
      return Boolean.TRUE;
    }
  }

  public boolean isShowFanart() {
    try {
      return Boolean.parseBoolean(get(UISettings.UISettingsProperty.showFanart));
    } catch (Exception ex) {
      return Boolean.TRUE;
    }
  }

  public boolean isShowSubtitle() {
    try {
      return Boolean.parseBoolean(get(UISettings.UISettingsProperty.showSubtitle));
    } catch (Exception ex) {
      return Boolean.FALSE;
    }
  }

  public boolean isShowCdart() {
    try {
      return Boolean.parseBoolean(get(UISettings.UISettingsProperty.showCdart));
    } catch (Exception ex) {
      return Boolean.TRUE;
    }
  }

  public boolean isShowClearart() {
    try {
      return Boolean.parseBoolean(get(UISettings.UISettingsProperty.showClearart));
    } catch (Exception ex) {
      return Boolean.TRUE;
    }
  }

  public boolean isShowLogo() {
    try {
      return Boolean.parseBoolean(get(UISettings.UISettingsProperty.showLogo));
    } catch (Exception ex) {
      return Boolean.TRUE;
    }
  }

  public boolean isShowBanner() {
    try {
      return Boolean.parseBoolean(get(UISettings.UISettingsProperty.showBanner));
    } catch (Exception ex) {
      return Boolean.TRUE;
    }
  }

  public boolean isGenerateThumb() {
    try {
      return Boolean.parseBoolean(get(UISettings.UISettingsProperty.generateThumb));
    } catch (Exception ex) {
      return Boolean.TRUE;
    }
  }

  public boolean isGenerateFanart() {
    try {
      return Boolean.parseBoolean(get(UISettings.UISettingsProperty.generateFanart));
    } catch (Exception ex) {
      return Boolean.TRUE;
    }
  }

  public boolean isGenerateSubtitles() {
    try {
      return Boolean.parseBoolean(get(UISettings.UISettingsProperty.generateSubtitles));
    } catch (Exception ex) {
      return Boolean.TRUE;
    }
  }

  public boolean isUseExtensionFilter() {
    try {
      return Boolean.parseBoolean(get(UISettings.UISettingsProperty.useExtensionFilter));
    } catch (Exception ex) {
      return Boolean.TRUE;
    }
  }

  public String getFileChooserPath () {
    try {
      return get(UISettings.UISettingsProperty.fileChooserPath);
    } catch (Exception ex) {
      return userFolder;
    }
  }

  public String[] getExtensionsList() {
  try {
      String res = get(UISettings.UISettingsProperty.extensionsList);
      return res.split(", ");
    } catch (Exception ex) {
      return new String[]{"mkv", "avi", "wmv", "mp4", "m4v", "mov", "ts", "m2ts", "ogm", "mpg", "mpeg", "flv", "iso", "rm", "mov", "asf"};
    }
  }

  public String getVersion() {
    return VERSION;
  }

  private static String getApplicationProperty(String key) {
    return ResourceBundle.getBundle(UISettings.class.getName(), Locale.ROOT).getString(key);
  }
}