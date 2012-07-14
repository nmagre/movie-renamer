/*
 * Movie Renamer
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
package fr.free.movierenamer.utils;

import fr.free.movierenamer.parser.xml.MrSettings;
import fr.free.movierenamer.parser.xml.XMLParser;
import fr.free.movierenamer.worker.WorkerManager;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Class Settings , Movie Renamer settings <br>
 * Only public and non static attributes are written in conf file !
 * 
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
public class Settings implements Cloneable {

  /**
   * App general settings
   */
  // General
  public static final String APPNAME = Utils.getAppTok("apps");
  public static final String VERSION = Utils.getAppTok("apps.version");
  private static final String userPath = System.getProperty("user.home");
  private static final String apkMdb = "BQRjATHjATV3Zwx2AwWxLGOvLwEwZ2WwZQWyBGyvMQx=";
  private static final String apkTdb = "DmIOExH5DwV1AwZkZRZ3Zt==";
  private final static String movieRenamerFolder = Utils.isWindows() ? "Movie_Renamer" : ".Movie_Renamer";
  public static final String mrFolder = userPath + File.separator + movieRenamerFolder;

  // XML
  public static final String movieRenamerTag = Settings.APPNAME.replace(' ', '_');
  public static final String versionAtt = "Version";
  public static final String settingTag = "setting";
  public static final String arrayEscapeChar = "/_";
  public static final String sZero = "0";

  // Cache
  public static final String cacheDir = userPath + File.separator + movieRenamerFolder + File.separator + "cache" + File.separator;
  public static final String imageCacheDir = cacheDir + "images" + File.separator;
  public static final String thumbCacheDir = imageCacheDir + "thumbnails" + File.separator;
  public static final String fanartCacheDir = imageCacheDir + "fanarts" + File.separator;
  public static final String actorCacheDir = imageCacheDir + "actors" + File.separator;
  public static final String xmlCacheDir = cacheDir + "XML" + File.separator;
  public static final String tvshowZipCacheDir = cacheDir + "Zip" + File.separator;

  // Files
  public static final String configFile = userPath + File.separator + movieRenamerFolder + File.separator + "conf" + File.separator + "movie_renamer.conf";
  public static final String renamedFile = cacheDir + "renamed.xml";
  private static final String logFile = userPath + File.separator + movieRenamerFolder + File.separator + "Logs" + File.separator + "movie_renamer.log";

  // Logger
  public static final Logger LOGGER = Logger.getLogger(APPNAME + " Logger");

  // IMDB
  public static final String imdbSearchUrl = "http://www.imdb.com/find?s=tt&q=";
  public static final String imdbMovieUrl = "http://www.imdb.com/title/";
  public static final String imdbSearchUrl_fr = "http://www.imdb.fr/find?s=tt&q=";
  public static final String imdbMovieUrl_fr = "http://www.imdb.fr/title/";
  // The Movie DB
  public static final String tmdbAPISearchUrl = "http://api.themoviedb.org/2.1/Movie.search/en/xml/";
  public static final String tmdbAPMovieImdbLookUp = "http://api.themoviedb.org/2.1/Movie.imdbLookup/en/xml/";
  public static final String tmdbAPIMovieInf = "http://api.themoviedb.org/2.1/Movie.getInfo/en/xml/";
  // Tvdb
  public static final String tvdbAPIUrlTvShow = "http://thetvdb.com/api/";
  public static final String tvdbAPIUrlTvShowImage = "http://thetvdb.com/banners/";
  // Allocine
  public static final String allocineAPISearch = "http://api.allocine.fr/rest/v3/search?partner=yW5kcm9pZC12M3M&filter=FILTER&striptags=synopsis,synopsisshort&q=";
  public static final String allocineAPIInfo = "http://api.allocine.fr/rest/v3/MEDIA?partner=yW5kcm9pZC12M3M&profile=large&filter=MEDIA&striptags=synopsis,synopsisshort&code=";
  // Xbmc Passion
  public static final String xbmcPassionImdblookup = "http://passion-xbmc.org/scraper/ajax.php?Ajax=Home&";
  // List
  public static int[] nbResultList = { -1, 5, 10, 15, 20, 30 };
  public static String[] thumbExtList = { ".jpg", ".tbn", "-thumb.jpg" };
  public static String[] fanartExtList = { ".jpg", "-fanart.jpg" };
  // LAF
  public static final UIManager.LookAndFeelInfo lookAndFeels[] = UIManager.getInstalledLookAndFeels();
  public static boolean lafChanged = false;
  public static boolean interfaceChanged = false;
  // Apk
  public static String xurlMdb = Utils.rot13(apkMdb);
  public static String xurlTdb = Utils.rot13(apkTdb);
  public static boolean xmlError = false;
  // Misc
  public static String xmlVersion = "";

  /**
   * Saved settings *
   */
  // General
  public boolean selectFrstMedia = false;
  public boolean scanSubfolder = false;
  public boolean showNotaMovieWarn = true;
  public boolean movieInfoPanel = true;
  public boolean actorImage = true;
  public boolean thumb = true;
  public boolean fanart = true;
  public String laf = "";
  public int nfoType = 0;
  public boolean checkUpdate = false;
  public String locale = "";
  // Rename movie filename
  public String movieFilenameFormat = "<t> (<y>)";
  public String movieFilenameSeparator = ", ";
  public int movieFilenameLimit = 3;
  public Utils.CaseConversionType movieFilenameCase = Utils.CaseConversionType.FIRSTLA;
  public boolean movieFilenameTrim = true;
  public boolean movieFilenameRmDupSpace = true;
  public boolean movieFilenameCreateDirectory = false;
  // Renamer movie folder
  public String movieFolderFormat = "<t> (<y>)";
  public String movieFolderSeparator = ", ";
  public int movieFolderLimit = 3;
  public int movieFolderCase = 1;
  public boolean movieFolderTrim = true;
  public boolean movieFolderRmDupSpace = true;
  // Rename Tv show filename
  public String tvShowFilenameFormat = "<st> S<s>E<e> <et>";
  public String tvShowFilenameSeparator = ", ";
  public int tvShowFilenameLimit = 3;
  public int tvShowFilenameCase = 1;
  public boolean tvShowFilenameTrim = true;
  public boolean tvShowFilenameRmDupSpace = true;
  // Image
  public int thumbSize = 0;
  public int fanartSize = 0;
  public int thumbExt = 0;
  // Filter
  public String[] extensions = { "mkv", "avi", "wmv", "mp4", "m4v", "mov", "ts", "m2ts", "ogm", "mpg", "mpeg", "flv", "iso", "rm", "mov", "asf" };
  public static String[] nameFilters = { "notv", "readnfo", "repack", "proper$", "nfo$", "extended.cut", "limitededition", "limited", "k-sual", "extended", "uncut", "n° [0-9][0-9][0-9]", "yestv", "stv", "remastered", "limited", "x264", "bluray",
      "bd5", "bd9", "hddvd", "hdz", "unrated", "dvdrip", "cinefile", "hdmi", "dvd5", "ac3", "culthd", "dvd9", "remux", "edition.platinum", "frenchhqc", "frenchedit", "h264", "bdrip", "brrip", "hdteam", "hddvdrip", "subhd", "xvid", "divx", "null$",
      "divx511", "vorbis", "=str=", "www", "ffm", "mp3", "divx5", "dvb", "mpa2", "blubyte", "brmp", "avs", "filmhd", "hd4u", "1080p", "1080i", "720p", "720i", "720", "truefrench", "dts", "french", "vostfr", "1cd", "2cd", "vff", " vo$", " vf ", "hd",
      " cam$ ", "telesync", " ts ", " tc ", "ntsc", " pal$ ", "dvd-r", "dvdscr", "scr$", "r1", "r2", "r3", "r4", "r5", "wp", "subforced", "dvd", "vcd", "avchd", " md" };
  public ArrayList<String> mediaNameFilters;
  public boolean useExtensionFilter = true;
  // Cache
  public boolean clearXMLCache = false;
  // Search
  public WorkerManager.MovieScrapper movieScrapper = WorkerManager.MovieScrapper.IMDB;
  public WorkerManager.TVShowScrapper tvshowScrapper = WorkerManager.TVShowScrapper.TVDB;
  public boolean movieScrapperFR = false;
  public boolean tvshowScrapperFR = false;
  public boolean displayThumbResult = true;
  public boolean autoSearchMedia = true;
  public boolean selectFrstRes = true;
  public boolean sortBySimiYear = true;
  public int nbResult = 2;
  public boolean displayApproximateResult = false;
  // Proxy
  public boolean useProxy = false;
  public String proxyUrl = "";
  public int proxyPort = 80;
  // Not used
  public boolean showMovieFilePath = false;
  public boolean hideRenamedMedia = false;

  // The only instance of Settings
  private static Settings instance;

  /**
   * Private build for singleton fix
   * 
   * @return
   */
  private static synchronized Settings newInstance() {
    if (instance == null) {
      instance = new Settings();
      // if new, just load values
      instance.loadSetting();
    }
    return instance;
  }

  /**
   * Access to the Settings instance
   * 
   * @return The only instance of MR Settings
   */
  public static Settings getInstance() {
    if (instance == null) {
      instance = newInstance();
    }
    return instance;
  }

  /**
   * Constructor
   */
  private Settings() {
    mediaNameFilters = new ArrayList<String>();
    mediaNameFilters.addAll(Arrays.asList(nameFilters));
    Utils.createFilePath(configFile, false);
    Utils.createFilePath(fanartCacheDir, true);
    Utils.createFilePath(thumbCacheDir, true);
    Utils.createFilePath(actorCacheDir, true);
    Utils.createFilePath(xmlCacheDir, true);
    Utils.createFilePath(tvshowZipCacheDir, true);
    Utils.createFilePath(logFile, false);
    try {
      FileHandler fh = new FileHandler(logFile);
      LOGGER.addHandler(fh);
    } catch (SecurityException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
  }

  /**
   * Load Movie Renamer settings
   * 
   * @return Movie Renamer settings
   */
  private Settings loadSetting() {
    boolean saved;
    Settings config = new Settings();
    File file = new File(Settings.configFile);

    if (!file.exists()) {
      saved = config.saveSetting();
      if (!saved) {
        // Set locale
        Locale.setDefault((config.locale.equals("fr") ? new Locale("fr", "FR") : Locale.ENGLISH));
        JOptionPane.showMessageDialog(null, Utils.i18n("saveSettingsFailed") + " " + Settings.mrFolder, Utils.i18n("error"), JOptionPane.ERROR_MESSAGE);
        return config;
      }
      return loadSetting();
    }

    saved = false;
    try {
      // Parse Movie Renamer Settings
      XMLParser<Settings> xmlp = new XMLParser<Settings>(Settings.configFile);
      xmlp.setParser(new MrSettings());
      config = xmlp.parseXml();

      // Define locale on first run
      if (config.locale.equals("")) {
        if (!Locale.getDefault().getLanguage().equals("fr")) {
          config.locale = "en";
        } else {
          config.locale = "fr";
        }
        Settings.xmlVersion = Settings.VERSION;// Ensures that the settings
                                               // file is written once only
        config.movieScrapperFR = config.locale.equals("fr");
        config.tvshowScrapperFR = config.locale.equals("fr");
      } else {
        saved = true;
      }

      // Set locale
      Locale.setDefault((config.locale.equals("fr") ? new Locale("fr", "FR") : Locale.ENGLISH));
      if (Settings.VERSION.equals(Settings.xmlVersion) && !Settings.xmlError) {
        saved = true;
      }

    } catch (ParserConfigurationException ex) {
      Settings.LOGGER.log(Level.SEVERE, Utils.getStackTrace("ParserConfigurationException", ex.getStackTrace()));
    } catch (SAXException ex) {
      Settings.LOGGER.log(Level.SEVERE, Utils.getStackTrace("SAXException", ex.getStackTrace()));
    } catch (IOException ex) {
      Settings.LOGGER.log(Level.SEVERE, Utils.getStackTrace("IOException : " + ex.getMessage(), ex.getStackTrace()));
    } catch (InterruptedException ex) {
      Settings.LOGGER.log(Level.SEVERE, Utils.getStackTrace("InterruptedException : " + ex.getMessage(), ex.getStackTrace()));
    } finally {
      if (!saved) {
        if (!Settings.xmlVersion.equals("Beta_2.0")) {
          int n = JOptionPane.showConfirmDialog(null, Utils.i18n("resetRegexFilter"), Utils.i18n("question"), JOptionPane.YES_NO_OPTION);
          if (n == JOptionPane.OK_OPTION) {
            config.mediaNameFilters = new ArrayList<String>();
            config.mediaNameFilters.addAll(Arrays.asList(Settings.nameFilters));
          }
        }
        saved = config.saveSetting();
      }
    }

    if (!saved) {
      JOptionPane.showMessageDialog(null, Utils.i18n("saveSettingsFailed") + " " + Settings.mrFolder, Utils.i18n("error"), JOptionPane.ERROR_MESSAGE);
    }

    return config;
  }

  /**
   * Save setting
   * 
   * @return True if setting was saved, False otherwise
   */
  public boolean saveSetting() {
    LOGGER.log(Level.INFO, "Save configuration to {0}", configFile);
    try {
      String endl = Utils.ENDLINE;
      BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile), "UTF-8"));
      out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + endl);
      out.write("<" + Settings.movieRenamerTag + " " + Settings.versionAtt + "=\"" + VERSION + "\">" + endl);
      out.write("  <" + Settings.settingTag + ">" + endl);

      // Variables
      for (Field field : getSettingsFields()) {
        try {
          Object value = field.get(this);
          if (field.getType().getName().equalsIgnoreCase(Boolean.class.getSimpleName())) {
            // to string for boolean field
            value = ((Boolean) value) ? Settings.sZero : "1";
          } else if (field.getType().isArray()) {
            // to string for array fields
            value = Utils.arrayToString((Object[]) value, Settings.arrayEscapeChar, 0);
          } else if (Collection.class.isAssignableFrom(field.getType())) {
            // to string for Collection fields
            value = Utils.arrayToString((ArrayList<?>)value, Settings.arrayEscapeChar, 0);
          }
          out.write("    <" + field.getName() + ">" + Utils.escapeXML(value.toString()) + "</" + field.getName() + ">" + endl);
        } catch (IllegalArgumentException e) {
          LOGGER.log(Level.WARNING, e.getMessage());
        } catch (IllegalAccessException e) {
          LOGGER.log(Level.WARNING, e.getMessage());
        }
      }
      out.write("  </" + Settings.settingTag + ">" + endl);
      out.write("</" + Settings.movieRenamerTag + ">" + endl);
      out.close();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
      return false;
    }
    return true;
  }

  /**
   * Get the user settings fields
   * 
   * @return
   */
  private Collection<Field> getSettingsFields() {
    Collection<Field> results = new ArrayList<Field>();
    for (Field field : this.getClass().getDeclaredFields()) {
      int mod = field.getModifiers();
      if (Modifier.isPublic(mod) && !Modifier.isStatic(mod)) {
        results.add(field);
      }
    }
    return results;
  }

  /**
   * Set a value using field name
   * 
   * @param fieldName
   * @param configValue
   */
  public void setValue(String fieldName, String configValue) {
    try {
      Field field = this.getClass().getField(fieldName);
      Object value;
      if (field.getType().getName().equalsIgnoreCase(Boolean.class.getSimpleName())) {
        // Boolean field
        value = Settings.sZero.equals(configValue);
      } else if (field.getType().isArray()) {
        // Array field
        value =   configValue.split(Settings.arrayEscapeChar);
      } else if (Collection.class.isAssignableFrom(field.getType())) {
        // Collection field
        value = Utils.stringToArray(configValue, Settings.arrayEscapeChar);
      } else if (field.getType().isEnum()) {
        // Enum field
          @SuppressWarnings("unchecked")
          Enum<?> en = Enum.valueOf(field.getType().asSubclass(Enum.class), configValue);
        value = en;
      } else if (Utils.isNumeric(field.getType())) {
        value = Integer.valueOf(configValue); // FIXME Convertir en autre que Integer ? pas faux, mais je crois pas qu'il ya aura des float ou long ou ... un jours
      } else {
        // other parsing
        if (Settings.xmlVersion.compareToIgnoreCase("1.2.2_Alpha") < 0) {// Older setting file
          value = configValue.replace("$_", "<").replace("_$", ">");
        } else {
          value = Utils.unEscapeXML(configValue, "UTF-8");
        }
      }
      field.set(this, value);
    } catch (SecurityException e) {
      LOGGER.log(Level.WARNING, e.getMessage());
    } catch (NoSuchFieldException e) {
      LOGGER.log(Level.CONFIG, "Configuration field no longer exists", e);
    } catch (IllegalArgumentException e) {
      LOGGER.log(Level.WARNING, "Configuration value is not in the goot format !", e);
    } catch (IllegalAccessException e) {
      LOGGER.log(Level.WARNING, e.getMessage());
    }
  }

  public String getVersion() {
    return VERSION;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Settings) {
      Settings obj = (Settings) object;
      if (this.selectFrstMedia != obj.selectFrstMedia) {
        return false;
      }
      if (this.scanSubfolder != obj.scanSubfolder) {
        return false;
      }
      if (this.showNotaMovieWarn != obj.showNotaMovieWarn) {
        return false;
      }
      if (this.movieInfoPanel != obj.movieInfoPanel) {
        return false;
      }
      if (this.actorImage != obj.actorImage) {
        return false;
      }
      if (this.thumb != obj.thumb) {
        return false;
      }
      if (this.fanart != obj.fanart) {
        return false;
      }
      if (!this.laf.equals(obj.laf)) {
        return false;
      }
      if (this.nfoType != obj.nfoType) {
        return false;
      }
      if (this.checkUpdate != obj.checkUpdate) {
        return false;
      }
      if (!this.locale.equals(obj.locale)) {
        return false;
      }
      if (!this.movieFilenameFormat.equals(obj.movieFilenameFormat)) {
        return false;
      }
      if (!this.movieFilenameSeparator.equals(obj.movieFilenameSeparator)) {
        return false;
      }
      if (this.movieFilenameLimit != obj.movieFilenameLimit) {
        return false;
      }
      if (this.movieFilenameCase != obj.movieFilenameCase) {
        return false;
      }
      if (this.movieFilenameTrim != obj.movieFilenameTrim) {
        return false;
      }
      if (this.movieFilenameRmDupSpace != obj.movieFilenameRmDupSpace) {
        return false;
      }
      if (this.movieFilenameCreateDirectory != obj.movieFilenameCreateDirectory) {
        return false;
      }
      if (!this.movieFolderFormat.equals(obj.movieFolderFormat)) {
        return false;
      }
      if (!this.movieFolderSeparator.equals(obj.movieFolderSeparator)) {
        return false;
      }
      if (this.movieFolderLimit != obj.movieFolderLimit) {
        return false;
      }
      if (this.movieFolderCase != obj.movieFolderCase) {
        return false;
      }
      if (this.movieFolderTrim != obj.movieFolderTrim) {
        return false;
      }
      if (this.movieFolderRmDupSpace != obj.movieFolderRmDupSpace) {
        return false;
      }
      if (!this.tvShowFilenameFormat.equals(obj.tvShowFilenameFormat)) {
        return false;
      }
      if (!this.tvShowFilenameSeparator.equals(obj.tvShowFilenameSeparator)) {
        return false;
      }
      if (this.tvShowFilenameLimit != obj.tvShowFilenameLimit) {
        return false;
      }
      if (this.tvShowFilenameCase != obj.tvShowFilenameCase) {
        return false;
      }
      if (this.tvShowFilenameTrim != obj.tvShowFilenameTrim) {
        return false;
      }
      if (this.tvShowFilenameRmDupSpace != obj.tvShowFilenameRmDupSpace) {
        return false;
      }
      if (this.thumbSize != obj.thumbSize) {
        return false;
      }
      if (this.fanartSize != obj.fanartSize) {
        return false;
      }
      if (this.thumbExt != obj.thumbExt) {
        return false;
      }
      if (this.extensions != obj.extensions) {
        return false;
      }
      if (this.useExtensionFilter != obj.useExtensionFilter) {
        return false;
      }
      if (this.clearXMLCache != obj.clearXMLCache) {
        return false;
      }
      if (this.movieScrapper != obj.movieScrapper) {
        return false;
      }
      if (this.tvshowScrapper != obj.tvshowScrapper) {
        return false;
      }
      if (this.movieScrapperFR != obj.movieScrapperFR) {
        return false;
      }
      if (this.tvshowScrapperFR != obj.tvshowScrapperFR) {
        return false;
      }
      if (this.displayThumbResult != obj.displayThumbResult) {
        return false;
      }
      if (this.autoSearchMedia != obj.autoSearchMedia) {
        return false;
      }
      if (this.selectFrstRes != obj.selectFrstRes) {
        return false;
      }
      if (this.sortBySimiYear != obj.sortBySimiYear) {
        return false;
      }
      if (this.nbResult != obj.nbResult) {
        return false;
      }
      if (this.displayApproximateResult != obj.displayApproximateResult) {
        return false;
      }
    } else {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash + (this.interfaceChanged ? 1 : 0);
    hash = 97 * hash + (this.selectFrstMedia ? 1 : 0);
    hash = 97 * hash + (this.scanSubfolder ? 1 : 0);
    hash = 97 * hash + (this.showNotaMovieWarn ? 1 : 0);
    hash = 97 * hash + (this.movieInfoPanel ? 1 : 0);
    hash = 97 * hash + (this.actorImage ? 1 : 0);
    hash = 97 * hash + (this.thumb ? 1 : 0);
    hash = 97 * hash + (this.fanart ? 1 : 0);
    hash = 97 * hash + (this.laf != null ? this.laf.hashCode() : 0);
    hash = 97 * hash + this.nfoType;
    hash = 97 * hash + (this.checkUpdate ? 1 : 0);
    hash = 97 * hash + (this.locale != null ? this.locale.hashCode() : 0);
    hash = 97 * hash + (this.movieFilenameFormat != null ? this.movieFilenameFormat.hashCode() : 0);
    hash = 97 * hash + (this.movieFilenameSeparator != null ? this.movieFilenameSeparator.hashCode() : 0);
    hash = 97 * hash + this.movieFilenameLimit;
    hash = 97 * hash + this.movieFilenameCase.ordinal();
    hash = 97 * hash + (this.movieFilenameTrim ? 1 : 0);
    hash = 97 * hash + (this.movieFilenameRmDupSpace ? 1 : 0);
    hash = 97 * hash + (this.movieFilenameCreateDirectory ? 1 : 0);
    hash = 97 * hash + (this.movieFolderFormat != null ? this.movieFolderFormat.hashCode() : 0);
    hash = 97 * hash + (this.movieFolderSeparator != null ? this.movieFolderSeparator.hashCode() : 0);
    hash = 97 * hash + this.movieFolderLimit;
    hash = 97 * hash + this.movieFolderCase;
    hash = 97 * hash + (this.movieFolderTrim ? 1 : 0);
    hash = 97 * hash + (this.movieFolderRmDupSpace ? 1 : 0);
    hash = 97 * hash + (this.tvShowFilenameFormat != null ? this.tvShowFilenameFormat.hashCode() : 0);
    hash = 97 * hash + (this.tvShowFilenameSeparator != null ? this.tvShowFilenameSeparator.hashCode() : 0);
    hash = 97 * hash + this.tvShowFilenameLimit;
    hash = 97 * hash + this.tvShowFilenameCase;
    hash = 97 * hash + (this.tvShowFilenameTrim ? 1 : 0);
    hash = 97 * hash + (this.tvShowFilenameRmDupSpace ? 1 : 0);
    hash = 97 * hash + this.thumbSize;
    hash = 97 * hash + this.fanartSize;
    hash = 97 * hash + this.thumbExt;
    hash = 97 * hash + Arrays.deepHashCode(this.extensions);
    hash = 97 * hash + (this.mediaNameFilters != null ? this.mediaNameFilters.hashCode() : 0);
    hash = 97 * hash + (this.useExtensionFilter ? 1 : 0);
    hash = 97 * hash + (this.clearXMLCache ? 1 : 0);
    hash = 97 * hash + this.movieScrapper.ordinal();
    hash = 97 * hash + this.tvshowScrapper.ordinal();
    hash = 97 * hash + (this.movieScrapperFR ? 1 : 0);
    hash = 97 * hash + (this.tvshowScrapperFR ? 1 : 0);
    hash = 97 * hash + (this.displayThumbResult ? 1 : 0);
    hash = 97 * hash + (this.autoSearchMedia ? 1 : 0);
    hash = 97 * hash + (this.selectFrstRes ? 1 : 0);
    hash = 97 * hash + (this.sortBySimiYear ? 1 : 0);
    hash = 97 * hash + this.nbResult;
    hash = 97 * hash + (this.displayApproximateResult ? 1 : 0);
    hash = 97 * hash + (this.showMovieFilePath ? 1 : 0);
    hash = 97 * hash + (this.hideRenamedMedia ? 1 : 0);
    return hash;
  }
  
  @Override
  public Settings clone() throws CloneNotSupportedException{
    return (Settings) super.clone();
  }
}
