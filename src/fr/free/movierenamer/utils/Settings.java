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

import fr.free.movierenamer.parser.MrSettings;
import fr.free.movierenamer.parser.XMLParser;
import fr.free.movierenamer.worker.WorkerManager;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Class Settings , Movie Renamer settings <br> Only public and non static attributes are written in conf file !
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
  //TvRage
  public static final String tvRageAPIsearch = "http://services.tvrage.com/feeds/search.php?show=";
  // List
  public static int[] nbResultList = {-1, 5, 10, 15, 20, 30};
  public static String[] thumbExtList = {".jpg", ".tbn", "-thumb.jpg"};
  public static String[] fanartExtList = {".jpg", "-fanart.jpg"};
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
  public String[] extensions = {"mkv", "avi", "wmv", "mp4", "m4v", "mov", "ts", "m2ts", "ogm", "mpg", "mpeg", "flv", "iso", "rm", "mov", "asf"};
  public static String[] nameFilters = {"notv", "readnfo", "repack", "proper$", "nfo$", "extended.cut", "limitededition", "limited", "k-sual", "extended", "uncut", "n° [0-9][0-9][0-9]", "yestv", "stv", "remastered", "limited", "x264", "bluray",
    "bd5", "bd9", "hddvd", "hdz", "unrated", "dvdrip", "cinefile", "hdmi", "dvd5", "ac3", "culthd", "dvd9", "remux", "edition.platinum", "frenchhqc", "frenchedit", "h264", "bdrip", "brrip", "hdteam", "hddvdrip", "subhd", "xvid", "divx", "null$",
    "divx511", "vorbis", "=str=", "www", "ffm", "mp3", "divx5", "dvb", "mpa2", "blubyte", "brmp", "avs", "filmhd", "hd4u", "1080p", "1080i", "720p", "720i", "720", "truefrench", "dts", "french", "vostfr", "1cd", "2cd", "vff", " vo$", " vf ", "hd",
    " cam$ ", "telesync", " ts ", " tc ", "ntsc", " pal$ ", "dvd-r", "dvdscr", "scr$", "r1", "r2", "r3", "r4", "r5", "wp", "subforced", "dvd", "vcd", "avchd", " md"};
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
  public static synchronized Settings getInstance() {
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
      LOGGER.log(Level.SEVERE, Utils.getStackTrace("ParserConfigurationException", ex.getStackTrace()));
    } catch (SAXException ex) {
      LOGGER.log(Level.SEVERE, Utils.getStackTrace("SAXException", ex.getStackTrace()));
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, Utils.getStackTrace("IOException : " + ex.getMessage(), ex.getStackTrace()));
    } catch (InterruptedException ex) {
      LOGGER.log(Level.SEVERE, Utils.getStackTrace("InterruptedException : " + ex.getMessage(), ex.getStackTrace()));
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
            value = Utils.arrayToString((ArrayList<?>) value, Settings.arrayEscapeChar, 0);
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
        value = configValue.split(Settings.arrayEscapeChar);
      } else if (Collection.class.isAssignableFrom(field.getType())) {
        // Collection field
        value = Utils.stringToArray(configValue, Settings.arrayEscapeChar);
      } else if (field.getType().isEnum()) {
        // Enum field
        @SuppressWarnings("unchecked")
        Enum<?> en = Enum.valueOf(field.getType().asSubclass(Enum.class), configValue);
        value = en;
      } else if (Utils.isNumeric(field.getType())) {
        @SuppressWarnings("unchecked")
        Class<Number> type = (Class<Number>) field.getType();
        value = Utils.convertToNumber(type, configValue);// Integer.valueOf(configValue);
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
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (!(obj instanceof Settings)) {
      return false;
    }

    Settings older = (Settings) obj;
    Collection<Field> olderFields = older.getSettingsFields();
    Collection<Field> currentFields = this.getSettingsFields();
    if (currentFields.size() != olderFields.size()) {
      return false;
    }

    Iterator<Field> targetIt = currentFields.iterator();
    for (Field field : olderFields) {
      try {
        if (!field.get(older).equals(targetIt.next().get(this))) {
          return false;
        }
      } catch (IllegalArgumentException ex) {
        LOGGER.log(Level.SEVERE, null, ex);
      } catch (IllegalAccessException ex) {
        LOGGER.log(Level.SEVERE, null, ex);
      }
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    Collection<Field> fields = this.getSettingsFields();
    for (Field field : fields) {
      try {
        if (field.getType().getName().equalsIgnoreCase(Boolean.class.getSimpleName())) {
          // Boolean field
          hash = 29 * hash + (((Boolean) field.get(this)) ? 1 : 0);
        } else if (field.getType().isArray()) {
          // Array field
          hash = 29 * hash + Arrays.deepHashCode((Object[]) field.get(this));
        } else if (Collection.class.isAssignableFrom(field.getType())) {
          // Collection field
          hash = 29 * hash + ((Collection) field.get(this)).hashCode();
        } else if (field.getType().isEnum()) {
          // Enum field
          hash = 29 * hash + ((Enum<?>) field.get(this)).hashCode();
        } else if (Utils.isNumeric(field.getType())) {
          @SuppressWarnings("unchecked")
          Class<Number> type = (Class<Number>) field.getType();
          hash = 29 * hash + Utils.convertToNumber(type,  (String) field.get(this)).hashCode();
        }
      } catch (SecurityException e) {
        LOGGER.log(Level.WARNING, e.getMessage());
      } catch (IllegalArgumentException e) {
        LOGGER.log(Level.WARNING, "Configuration value is not in the goot format !", e);
      } catch (IllegalAccessException e) {
        LOGGER.log(Level.WARNING, e.getMessage());
      }
    }

    return hash;
  }

  @Override
  public Settings clone() throws CloneNotSupportedException {
    return (Settings) super.clone();
  }
}
