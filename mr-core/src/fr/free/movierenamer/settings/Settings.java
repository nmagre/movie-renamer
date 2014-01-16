/*
 * movie-renamer-core
 * Copyright (C) 2012-2014 Nicolas Magré
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
package fr.free.movierenamer.settings;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.jna.NativeLibrary;
import com.sun.jna.Platform;
import fr.free.movierenamer.mediainfo.MediaInfoLibrary;
import fr.free.movierenamer.renamer.Nfo;

import fr.free.movierenamer.renamer.NameCleaner;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.scrapper.impl.movie.IMDbScrapper;
import fr.free.movierenamer.scrapper.impl.movie.UniversalScrapper;
import fr.free.movierenamer.utils.FileUtils;
import fr.free.movierenamer.utils.LocaleUtils.AppLanguages;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.StringUtils;
import fr.free.movierenamer.utils.URIRequest;
import fr.free.movierenamer.utils.XPathUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Class Settings , Movie Renamer settings
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public final class Settings {

  static {
    String appName = getApplicationProperty("application.name");
    String appNameNospace = appName.replace(' ', '_');
    String appModule = getApplicationProperty("application.module.name");
    String appModuleNospace = appModule.replace(' ', '_');
    APPNAME = appName;
    APPMODULE = appModule;
    VERSION = getApplicationProperty("application.module.version");
    appName_nospace = appNameNospace;
    appFolder = getApplicationFolder();
    configFileName = appNameNospace + "_" + appModuleNospace + ".conf";
    logFileName = appNameNospace + "_" + appModuleNospace + ".log";
    LOGGER = Logger.getLogger(appModule);
    appSettingsNodeName = appNameNospace + "_" + appModuleNospace;
    settingNodeName = "settings";
    // Lib mediaInfo
    MEDIAINFO = libMediaInfo();
    IS64BIt = is64BitJvm();
  }
  public static final String APPNAME;
  public static final String APPMODULE;
  public static final String VERSION;
  public static final boolean LINUX = Platform.isLinux();
  public static final boolean WINDOWS = Platform.isWindows();
  public static final boolean IS64BIt;
  public static final File appFolder;
  private static final String appName_nospace;
  //JNA
  public static Boolean MEDIAINFO;
  // files
  private static final String configFileName;
  private static final String logFileName;
  // Logger
  public static final Logger LOGGER;
  private static final String appSettingsNodeName;
  private static final String settingNodeName;
  // Settings instance
  private static final Settings instance = new Settings();
  // Settings xml conf instance
  private final Document settingsDocument;
  private final Node settingsNode;
  private boolean autosave = true;

  public enum SettingsType {

    GENERAL,
    INTERFACE,
    MEDIA,
    SEARCH,
    FORMAT,
    IMAGE,
    NFO,
    EXTENSION,
    NETWORK
  }

  public enum SettingsSubType {

    GENERAL,
    NFO,
    UPDATE,
    LANGUAGE,
    SCRAPER,
    MOVIE,
    //TVSHOW,
    THUMB,
    FANART,
    LOGO,
    CDART,
    CLEARART,
    BANNER,
    PROXY,
    MEDIACENTER,
    SIZE,
    TIME
  }

  public interface IProperty {

    public Class<?> getVclass();

    public Object getDefaultValue();

    public String getValue();

    public String name();

    public SettingsType getType();

    public SettingsSubType getSubType();

    public boolean hasChild();

    public void setValue(Object value) throws IOException;
  }

  public enum SettingsProperty implements IProperty {

    reservedCharacter(Boolean.TRUE, SettingsType.MEDIA, SettingsSubType.GENERAL),
    // movie filename
    movieFilenameFormat("<t> (<y>)", SettingsType.MEDIA, SettingsSubType.MOVIE),
    movieFilenameSeparator(", ", SettingsType.MEDIA, SettingsSubType.MOVIE),
    movieFilenameLimit(3, SettingsType.MEDIA, SettingsSubType.MOVIE),
    movieFilenameCase(StringUtils.CaseConversionType.FIRSTLO, SettingsType.MEDIA, SettingsSubType.MOVIE),
    movieFilenameTrim(Boolean.TRUE, SettingsType.MEDIA, SettingsSubType.MOVIE),
    movieFilenameRmDupSpace(Boolean.TRUE, SettingsType.MEDIA, SettingsSubType.MOVIE),
    movieFilenameRomanUpper(Boolean.TRUE, SettingsType.MEDIA, SettingsSubType.MOVIE),
    // format
    stringSizeUnit(StringUtils.SizeFormat.BYTE, SettingsType.FORMAT, SettingsSubType.SIZE),
    stringSizeSi(Boolean.FALSE, SettingsType.FORMAT, SettingsSubType.SIZE),
    stringTimeHour("h ", SettingsType.FORMAT, SettingsSubType.TIME),
    stringTimeMinute("min ", SettingsType.FORMAT, SettingsSubType.TIME),
    stringTimeSeconde("s ", SettingsType.FORMAT, SettingsSubType.TIME),
    stringTimeMilliSeconde("ms", SettingsType.FORMAT, SettingsSubType.TIME),
    stringTimeShowSeconde(Boolean.FALSE, SettingsType.FORMAT, SettingsSubType.TIME),
    stringTimeShowMillis(Boolean.FALSE, SettingsType.FORMAT, SettingsSubType.TIME),
    // movie NFO
    movieNFOFilename("<fileName>.nfo", SettingsType.NFO, SettingsSubType.GENERAL),
    movieNfoTag(Boolean.TRUE, SettingsType.NFO, SettingsSubType.GENERAL),
    movieNfoImage(Boolean.TRUE, SettingsType.NFO, SettingsSubType.GENERAL),
    movieNfoImdbId(Boolean.TRUE, SettingsType.NFO, SettingsSubType.GENERAL),
    movieNfogenerate(Boolean.TRUE, SettingsType.NFO, SettingsSubType.GENERAL),
    movieNfoType(Nfo.NFOtype.XBMC, SettingsType.NFO, SettingsSubType.MEDIACENTER),
    // tvShow
    //    tvShowFilenameFormat("<st> S<s>E<e> <et>", SettingsType.RENAME, SettingsSubType.TVSHOWFILENAME), // ("<st> S<s>E<e> <et>"),
    //    tvShowFilenameSeparator(", ", SettingsType.RENAME, SettingsSubType.TVSHOWFILENAME), // (", "),
    //    tvShowFilenameLimit(3, SettingsType.RENAME, SettingsSubType.TVSHOWFILENAME), // (Integer.decode("3").toString()),
    //    tvShowFilenameCase(StringUtils.CaseConversionType.FIRSTLO, SettingsType.RENAME, SettingsSubType.TVSHOWFILENAME), // (""),
    //    tvShowFilenameTrim(Boolean.TRUE, SettingsType.RENAME, SettingsSubType.TVSHOWFILENAME), // (Boolean.TRUE.toString()),
    //    tvShowFilenameRmDupSpace(Boolean.TRUE, SettingsType.RENAME, SettingsSubType.TVSHOWFILENAME), // (Boolean.TRUE.toString()),
    // Search
    searchNbResult(15, SettingsType.SEARCH, SettingsSubType.GENERAL),
    searchOrder(Boolean.TRUE, SettingsType.SEARCH, SettingsSubType.GENERAL, true),
    searchOrderThreshold(280, SettingsType.SEARCH, SettingsSubType.GENERAL),
    searchMovieScrapper(UniversalScrapper.class, SettingsType.SEARCH, SettingsSubType.SCRAPER),
    searchGetTmdbTag(Boolean.TRUE, SettingsType.SEARCH, SettingsSubType.SCRAPER),
    //    searchTvshowScrapper(TheTVDBScrapper.class, SettingsType.SEARCH, SettingsSubType.SCRAPPER), // (TheTVDBScrapper.class.toString()),
    //    searchSubtitleScrapper(OpenSubtitlesScrapper.class, SettingsType.SEARCH, SettingsSubType.SCRAPPER), // (IMDbScrapper.class.toString()),// FIXME
    searchScrapperLang(AvailableLanguages.en, SettingsType.GENERAL, SettingsSubType.LANGUAGE),// (Locale.ENGLISH.toString()),
    // http param
    httpRequestTimeOut(30, SettingsType.NETWORK, SettingsSubType.GENERAL),
    httpCustomUserAgent("", SettingsType.NETWORK, SettingsSubType.GENERAL),
    // Proxy
    proxyIsOn(Boolean.FALSE, SettingsType.NETWORK, SettingsSubType.PROXY, true),
    proxyUrl("", SettingsType.NETWORK, SettingsSubType.PROXY),
    proxyPort(80, SettingsType.NETWORK, SettingsSubType.PROXY),
    // Extension
    fileExtension(Arrays.asList(NameCleaner.getCleanerProperty("file.extension").split("\\|")), SettingsType.EXTENSION, SettingsSubType.GENERAL),
    //app lang
    appLanguage(AppLanguages.en, SettingsType.GENERAL, SettingsSubType.LANGUAGE);
    private Class<?> vclass;
    private Object defaultValue;
    private SettingsType type;
    private SettingsSubType subType;
    private boolean haschild;

    private SettingsProperty(Object defaultValue) {
      this(defaultValue, null, null);
    }

    private SettingsProperty(Object defaultValue, SettingsType type, SettingsSubType subType) {
      this(defaultValue, type, subType, false);
    }

    private SettingsProperty(Object defaultValue, SettingsType type, SettingsSubType subType, boolean haschild) {
      this.vclass = defaultValue.getClass();
      this.defaultValue = defaultValue;
      this.type = type;
      this.subType = subType;
      this.haschild = haschild;
      if (!(defaultValue instanceof Boolean) && haschild) {
        throw new UnsupportedOperationException("Only boolean value can have a child");
      }
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
    public void setValue(Object value) {
      instance.set(this, value);
    }

    @Override
    public SettingsType getType() {
      return type;
    }

    @Override
    public SettingsSubType getSubType() {
      return subType;
    }

    @Override
    public boolean hasChild() {
      return haschild;
    }
  }

  /**
   * Access to the Settings instance
   *
   * @return The only instance of MR Settings
   */
  public static Settings getInstance() {
    return instance;
  }

  /**
   * Constructor
   */
  private Settings() {
    // Log init
    try {
      File logsRoot = new File(Settings.appFolder, "logs");
      if (!logsRoot.isDirectory() && !logsRoot.mkdirs()) {
        throw new IOException("Failed to create logs dir: " + logsRoot);
      }
      FileHandler fh = new FileHandler(logsRoot.getAbsolutePath() + File.separator + logFileName);
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
      File confRoot = new File(Settings.appFolder, "conf");
      File file = new File(confRoot, configFileName);
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
    if (autosave) {
      saveSetting();
    }
  }

  private synchronized String get(SettingsProperty key) {
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

  public synchronized void set(SettingsProperty key, Object value) {
    if (value != null && key != null) {
      Object savedValue = key.getValue();

      if (savedValue.toString().equals(value.toString())) {
        return;
      }

      Node found = XPathUtils.selectNode(key.name(), settingsNode);
      if (found == null) {
        found = settingsDocument.createElement(key.name());
        // param.appendChild(settingsDocument.createTextNode(value.toString()));
        settingsNode.appendChild(found);
      }
      found.setTextContent(value.toString());
      if (autosave) {
        saveSetting();
      }
    }
  }

  public synchronized void clear() {
    Settings.LOGGER.log(Level.INFO, String.format("Clear Settings"));
    NodeList list = settingsNode.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      settingsNode.removeChild(list.item(i));
    }
    if (autosave) {
      saveSetting();
    }
  }

  public AppLanguages getAppLanguage() {
    return AppLanguages.valueOf(get(SettingsProperty.appLanguage));
  }

  public boolean isReservedCharacter() {
    return Boolean.parseBoolean(get(SettingsProperty.reservedCharacter));
  }

  public String getMovieFilenameFormat() {
    return get(SettingsProperty.movieFilenameFormat);
  }

  public String getMovieFilenameSeparator() {
    return get(SettingsProperty.movieFilenameSeparator);
  }

  public int getMovieFilenameLimit() {
    return Integer.parseInt(get(SettingsProperty.movieFilenameLimit));
  }

  public StringUtils.CaseConversionType getMovieFilenameCase() {
    return StringUtils.CaseConversionType.valueOf(get(SettingsProperty.movieFilenameCase));
  }

  public boolean isMovieFilenameTrim() {
    return Boolean.parseBoolean(get(SettingsProperty.movieFilenameTrim));
  }

  public boolean isMovieFilenameRmDupSpace() {
    return Boolean.parseBoolean(get(SettingsProperty.movieFilenameRmDupSpace));
  }

  public boolean isMovieFilenameRomanUpper() {
    return Boolean.parseBoolean(get(SettingsProperty.movieFilenameRomanUpper));
  }

  public StringUtils.SizeFormat getStringSizeUnit() {
    return StringUtils.SizeFormat.valueOf(get(SettingsProperty.stringSizeUnit));
  }

  public boolean isStringSizeSi() {
    return Boolean.parseBoolean(get(SettingsProperty.stringSizeSi));
  }

  public String getStringTimeHour() {
    return get(SettingsProperty.stringTimeHour);
  }

  public String getStringTimeMinute() {
    return get(SettingsProperty.stringTimeMinute);
  }

  public String getStringTimeSeconde() {
    return get(SettingsProperty.stringTimeSeconde);
  }

  public String getStringTimeMilliSeconde() {
    return get(SettingsProperty.stringTimeMilliSeconde);
  }

  public boolean isStringTimeShowSeconde() {
    return Boolean.parseBoolean(get(SettingsProperty.stringTimeShowSeconde));
  }

  public boolean isStringTimeShowMillis() {
    return Boolean.parseBoolean(get(SettingsProperty.stringTimeShowMillis));
  }

  public Nfo.NFOtype getMovieNfoType() {
    return Nfo.NFOtype.valueOf(get(SettingsProperty.movieNfoType));
  }

  public String getNFOFileName() {
    return get(SettingsProperty.movieNFOFilename);
  }

  public boolean isMovieNfoTag() {
    return Boolean.parseBoolean(get(SettingsProperty.movieNfoTag));
  }

  public boolean isMovieNfoImage() {
    return Boolean.parseBoolean(get(SettingsProperty.movieNfoImage));
  }

  public boolean isMovieImdbId() {
    return Boolean.parseBoolean(get(SettingsProperty.movieNfoImdbId));
  }

  public boolean isMovieNfogenerate() {
    return Boolean.parseBoolean(get(SettingsProperty.movieNfogenerate));
  }

  public int getSearchNbResult() {
    return Integer.parseInt(get(SettingsProperty.searchNbResult));
  }

  public boolean isGetTmdbTagg() {
    return Boolean.parseBoolean(get(SettingsProperty.searchGetTmdbTag));
  }

  public boolean isSearchOrder() {
    return Boolean.parseBoolean(get(SettingsProperty.searchOrder));
  }

  public int getSearchOrderThreshold() {
    return Integer.parseInt(get(SettingsProperty.searchOrderThreshold));
  }

//  public String getTvShowFilenameFormat() {
//    return get(SettingsProperty.tvShowFilenameFormat);
//  }
//
//  public String getTvShowFilenameSeparator() {
//    return get(SettingsProperty.tvShowFilenameSeparator);
//  }
//
//  public int getTvShowFilenameLimit() {
//    return Integer.parseInt(get(SettingsProperty.tvShowFilenameLimit));
//  }
//
//  public String getTvShowFilenameCase() {
//    return get(SettingsProperty.tvShowFilenameCase);
//  }
//
//  public boolean isTvShowFilenameTrim() {
//    return Boolean.parseBoolean(get(SettingsProperty.tvShowFilenameTrim));
//  }
//
//  public boolean isTvShowFilenameRmDupSpace() {
//    return Boolean.parseBoolean(get(SettingsProperty.tvShowFilenameRmDupSpace));
//  }
  @SuppressWarnings("unchecked")
  public Class<? extends MovieScrapper> getSearchMovieScrapper() {
    try {
      return (Class<MovieScrapper>) Class.forName(get(SettingsProperty.searchMovieScrapper).replace("class ", ""));
    } catch (Exception ex) {
      return IMDbScrapper.class;
    }
  }

//  @SuppressWarnings("unchecked")
//  public Class<? extends TvShowScrapper> getSearchTvshowScrapper() {
//    try {
//      return (Class<TvShowScrapper>) Class.forName(get(SettingsProperty.searchTvshowScrapper).replace("class ", ""));
//    } catch (Exception ex) {
//      return TheTVDBScrapper.class;
//    }
//  }
//
//  @SuppressWarnings("unchecked")
//  public Class<? extends SubtitleScrapper> getSearchSubtitleScrapper() {
//    try {
//      return (Class<SubtitleScrapper>) Class.forName(get(SettingsProperty.searchSubtitleScrapper).replace("class ", ""));
//    } catch (Exception ex) {
//      return SubsceneSubtitleScrapper.class;
//    }
//  }
  public AvailableLanguages getSearchScrapperLang() {
    return AvailableLanguages.valueOf(get(SettingsProperty.searchScrapperLang));
  }

  public boolean isProxyIsOn() {
    return Boolean.parseBoolean(get(SettingsProperty.proxyIsOn));
  }

  public String getProxyUrl() {
    return get(SettingsProperty.proxyUrl);
  }

  public int getProxyPort() {
    return Integer.parseInt(get(SettingsProperty.proxyPort));
  }

  public int getHttpRequestTimeOut() {
    return Integer.parseInt(get(SettingsProperty.httpRequestTimeOut));
  }

  public String getHttpCustomUserAgent() {
    return get(SettingsProperty.httpCustomUserAgent);
  }

  public List<String> getfileExtension() {
    String ext = get(SettingsProperty.fileExtension);
    ext = ext.substring(1, ext.length() - 1);
    return Arrays.asList(ext.split(", "));
  }

  public boolean isAutosave() {
    return autosave;
  }

  public void setAutosave(boolean autosave) {
    this.autosave = autosave;
  }

  /**
   * Save setting
   *
   * @return True if setting was saved, False otherwise
   */
  private synchronized boolean saveSetting() {
    boolean saveSuccess;
    try {
      LOGGER.log(Level.INFO, String.format("Save configuration to %s", configFileName));
      File confRoot = new File(Settings.appFolder, "conf");
      if (!confRoot.isDirectory() && !confRoot.mkdirs()) {
        throw new IOException("Failed to create conf dir: " + confRoot);
      }
      try {
        // write it to file
        File confFile = new File(confRoot, configFileName);
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

  public String getVersion() {
    return VERSION;
  }

  public static String decodeApkKey(String apkkey) {
    return new String(DatatypeConverter.parseBase64Binary(StringUtils.rot13(apkkey)));
  }

  public static String getApplicationProperty(String key) {
    return ResourceBundle.getBundle(Settings.class.getName(), Locale.ROOT).getString(key);
  }

  public static File getApplicationFolder() {
    String applicationDirPath = System.getProperty("application.dir");
    String userHome = System.getProperty("user.home");
    String userDir = System.getProperty("user.dir");
    File applicationFolder;

    if (applicationDirPath != null && applicationDirPath.length() > 0) {
      // use given path
      applicationFolder = new File(applicationDirPath);
    } else if (userHome != null) {
      // create folder in user home
      applicationFolder = new File(userHome, Platform.isWindows() ? appName_nospace : "." + appName_nospace);
    } else {
      // use working directory
      applicationFolder = new File(userDir);
    }

    // create folder if necessary
    if (!applicationFolder.exists()) {
      applicationFolder.mkdirs();// FIXME mkdirs can return false or thrown a SecurityException
    }

    return applicationFolder;
  }

  /**
   * Check if lib media info is installed
   *
   * @return True if lib media info is installed, otherwise false
   */
  private static boolean libMediaInfo() {
    Boolean mediaInfo = null;
    boolean libzen = false;

    if (LINUX) {
      try {
        NativeLibrary.getInstance("zen");
        libzen = true;
      } catch (LinkageError e) {
        Settings.LOGGER.log(Level.WARNING, "Failed to preload libzen");
      }
    }

    if ((LINUX && libzen) || !LINUX) {
      try {
        MediaInfoLibrary.INSTANCE.New();
        mediaInfo = Boolean.TRUE;
      } catch (LinkageError e) {
        mediaInfo = Boolean.FALSE;
      }
    }

    if (mediaInfo == null) {
      mediaInfo = Boolean.FALSE;
    }

    return mediaInfo.equals(Boolean.TRUE);
  }

  private static boolean is64BitJvm() {

    try {
      String res = System.getProperty("sun.arch.data.model");
      if (res != null && !res.isEmpty()) {
        return res.contains("64");
      }
    } catch (Exception ex) {
    }

    try {
      String res = System.getProperty("os.arch");
      if (res != null && !res.isEmpty()) {
        return res.contains("64");
      }
    } catch (Exception ex) {
    }

    try {
      String cmd = System.getProperty("java.home") + "/bin/java -version";
      Scanner s = new Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
      if (s.hasNext()) {
        return s.next().contains("64-Bit");
      }
    } catch (Exception e) {
    }

    return false;
  }
}
