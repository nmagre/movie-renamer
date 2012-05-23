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

import java.io.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;

/**
 * Class Settings , Movie Renamer settings
 * @author Nicolas Magré
 */
public class Settings {

  public static final String APPNAME = "Movie Renamer";
  private final String version = Utils.getRbTok("apps.version");
  private final String userPath = System.getProperty("user.home");
  private final String apkMdb = "BQRjATHjATV3Zwx2AwWxLGOvLwEwZ2WwZQWyBGyvMQx=";
  private final String apkTdb = "DmIOExH5DwV1AwZkZRZ3Zt==";
  private final String movieRenamerFolder = Utils.isWindows() ? "Movie_Renamer" : ".Movie_Renamer";
  //Cache
  public Cache cache;
  public final String cacheDir = userPath + File.separator + movieRenamerFolder + File.separator + "cache" + File.separator;
  public final String imageCacheDir = cacheDir + "images" + File.separator;
  public final String thumbCacheDir = imageCacheDir + "thumbnails" + File.separator;
  public final String fanartCacheDir = imageCacheDir + "fanarts" + File.separator;
  public final String actorCacheDir = imageCacheDir + "actors" + File.separator;
  public final String xmlCacheDir = cacheDir + "XML" + File.separator;
  public final String tvshowZipCacheDir = cacheDir + "Zip" + File.separator;
  //Files
  public final String configFile = userPath + File.separator + movieRenamerFolder + File.separator + "conf" + File.separator + "movie_renamer.conf";
  public final String renamedFile = cacheDir + "renamed.xml";
  private final String logFile = userPath + File.separator + movieRenamerFolder + File.separator + "Logs" + File.separator + "movie_renamer.log";
  //Logger
  public static final Logger LOGGER = Logger.getLogger("Movie Renamer Logger");
  //IMDB
  public final String imdbSearchUrl = "http://www.imdb.com/find?s=tt&q=";
  public final String imdbMovieUrl = "http://www.imdb.com/title/";
  public final String imdbSearchUrl_fr = "http://www.imdb.fr/find?s=tt&q=";
  public final String imdbMovieUrl_fr = "http://www.imdb.fr/title/";
  //The Movie DB
  public final String tmdbAPISearchUrl = "http://api.themoviedb.org/2.1/Movie.search/en/xml/";
  public final String tmdbAPMovieImdbLookUp = "http://api.themoviedb.org/2.1/Movie.imdbLookup/en/xml/";
  public final String tmdbAPIMovieInf = "http://api.themoviedb.org/2.1/Movie.getInfo/en/xml/";
  //Tvdb
  public final String tvdbAPIUrlTvShow = "http://thetvdb.com/api/";
  public static final String tvdbAPIUrlTvShowImage = "http://thetvdb.com/banners/";
  //Allocine
  public final String allocineAPISearch="http://api.allocine.fr/rest/v3/search?partner=yW5kcm9pZC12M3M&filter=movie&striptags=synopsis,synopsisshort&q=";
  public final String allocineAPIInfo = "http://api.allocine.fr/rest/v3/movie?partner=yW5kcm9pZC12M3M&profile=large&filter=movie&striptags=synopsis,synopsisshort&code=";
  // List
  public int[] nbResultList = {-1, 5, 10, 15, 20, 30};
  public String[] thumbExtList = {".jpg", ".tbn", "-thumb.jpg"};
  public String[] fanartExtList = {".jpg", "-fanart.jpg"};
  public String[][] genreFR = {
    {"Action", "Action"}, {"Adventure", "Aventure"}, {"Animation", "Animation"},
    {"Biography", "Biographie"}, {"Comedy", "Comédie"}, {"Crime", "Crime"},
    {"Documentary", "Documentaire"}, {"Drama", "Drame"}, {"Family", "Famille"},
    {"Fantasy", "Fantaisie"}, {"Film-Noir", "Film-Noir"}, {"History", "Histoire"},
    {"Horror", "Horreur"}, {"Music", "Musique"}, {"Musical", "Comédie musicale"},
    {"Mystery", "Mystère"}, {"News", "News"}, {"Reality-TV", "Télé-réalité"},
    {"Romance", "Romance"}, {"Sci-Fi", "Sci-Fi"}, {"Sport", "Sport"},
    {"Talk-Show", "Talk-Show"}, {"Thriller", "Thriller"}, {"War", "Guerre"},
    {"Western", "Western"}
  };
  //LAF
  public final static UIManager.LookAndFeelInfo lookAndFeels[] = UIManager.getInstalledLookAndFeels();
  public boolean lafChanged = false;
  public boolean interfaceChanged = false;
  //Apk
  public String xurlMdb = Utils.rot13(apkMdb);
  public String xurlTdb = Utils.rot13(apkTdb);
  
  // Saved settings
  public String locale = "";
  public String[] nameFilters = {
    "notv", "readnfo", "repack", "proper", "nfo", "extended.cut", "limitededition", "limited", "k-sual",
    "extended", "uncut", "n° [0-9][0-9][0-9]", "yestv", "stv", "remastered", "limited", "x264", "bluray",
    "bd5", "bd9", "hddvd", "hdz", "wes-", "edition.exclusive", "unrated", "walt disney", "dvdrip", "cinefile",
    "hdmi", "dvd5", "ac3", "culthd", "dvd9", "remux", "edition.platinum", "frenchhqc", "proper", "frenchedit",
    "wawamania", "h264", "bdrip", "brrip", "hdteam", "hddvdrip", "subhd", "xvid", "divx", "null", "divx511",
    "vorbis", "=str=", "www", "ffm", "mp3", "-arrows", "divx5", "dvb", "mpa2", "blubyte", "brmp", "avs", "filmhd",
    "hd4u", "1080p", "1080i", "720p", "720i", "720", "-Liber", "truefrench", "dts", "french", "megaexclue",
    "untoucahbles", "vostfr", "1cd", "2cd", "vff", " vo ", " vf ", "forcebleue", "hd", "knob", " ws", "bong",
    "-fiction", " cam ", "telesync", " ts ", " tc ", "ntsc", " pal ", "dvd-r", "dvdscr", "scr", "r1", "r2", "r3", "r4",
    "r5", "wp", "subforced", "dvd", "vcd", "avchd", "-arlbouffiard", "-redbull", " md", "-kong", "-thewarrior777",
    "-LU3UR", "-DELUiSE", "-SURViVAL", "-vodka", "-slim", "_", "-HARIJO", "-ENJOY", "-PTN", "-fwd", "-ARTEFAC",
    "-COGiTO", "-AYMO", "-GKS"
  };
  public String xmlVersion = "";
  public boolean xmlError = false;
  public String[] extensions = {"mkv", "avi", "wmv", "mp4", "m4v", "mov", "ts", "m2ts", "ogm", "mpg", "mpeg", "flv", "iso", "rm", "mov", "asf"};
  public int thumbSize = 0;
  public int fanartSize = 0;
  public int nbResult = 2;
  public String movieFilenameFormat = "<t> (<y>)";
  public int thumbExt = 0;
  public int fanartExt = 1;
  public int renameCase = 1;
  public String movieDir = "";
  public int movieDirRenamedTitle = 0;
  public String commande = "";
  public int nfoType = 0;
  public String separator = ", ";
  public int limit = 3;
  public String laf = "";
  public int scrapper = 0;
  
  // Boolean
  public boolean useExtensionFilter = true;
  public boolean showMovieFilePath = false;
  public boolean scanSubfolder = false;
  public boolean hideRenamedMovie = false;
  public boolean displayApproximateResult = false;
  public boolean displayThumbResult = true;
  public boolean downThumb = true;
  public boolean downFanart = true;
  public boolean downTrailer = false;
  public boolean createMovieDirectory = false;
  public boolean imdbInfo = true;
  public boolean imdbFr = false;
  public boolean selectFrstMovie = false;
  public boolean selectFrstRes = true;
  public boolean movieInfoPanel = true;
  public boolean actorImage = true;
  public boolean thumb = true;
  public boolean fanart = true;
  public boolean checkUpdate = false;
  public boolean showNotaMovieWarn = true;
  public boolean autoSearchMovie = true;
  public boolean rmSpcChar = true;
  public boolean rmDupSpace = true;
  public boolean tvdbFr = false;
  public boolean clearXMLCache = false;

  /**
   * Constructor
   */
  public Settings() {
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
    cache = new Cache(this);
  }

  /**
   * Save setting
   * @return True if setting was saved, False otherwise
   */
  public boolean saveSetting() {
    LOGGER.log(Level.INFO, "Save configuration");
    try {
      String endl = Utils.ENDLINE;
      BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile), "UTF-8"));
      out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + endl);
      out.write("<Movie_Renamer Version=\"" + version + "\">" + endl);
      out.write("  <setting>" + endl);

      // Variables
      out.write("    <locale>" + locale + "</locale>" + endl);
      out.write("    <nameFilters>" + Utils.escapeXML(Utils.arrayToString(nameFilters, "/_", 0)) + "</nameFilters>" + endl);
      out.write("    <extensions>" + Utils.arrayToString(extensions, "/_", 0) + "</extensions>" + endl);
      out.write("    <thumbSize>" + thumbSize + "</thumbSize>" + endl);
      out.write("    <fanartSize>" + fanartSize + "</fanartSize>" + endl);
      out.write("    <nbResult>" + nbResult + "</nbResult>" + endl);
      out.write("    <movieFilenameFormat>" + Utils.escapeXML(movieFilenameFormat) + "</movieFilenameFormat>" + endl);
      out.write("    <thumbExt>" + thumbExt + "</thumbExt>" + endl);
      out.write("    <fanartExt>" + fanartExt + "</fanartExt>" + endl);
      out.write("    <renameCase>" + renameCase + "</renameCase>" + endl);
      out.write("    <movieDir>" + movieDir + "</movieDir>" + endl);
      out.write("    <movieDirRenamedTitle>" + movieDirRenamedTitle + "</movieDirRenamedTitle>" + endl);
      out.write("    <commande>" + Utils.escapeXML(commande) + "</commande>" + endl);
      out.write("    <nfoType>" + nfoType + "</nfoType>" + endl);
      out.write("    <separator>" + separator + "</separator>" + endl);
      out.write("    <limit>" + limit + "</limit>" + endl);
      out.write("    <laf>" + laf + "</laf>" + endl);
      out.write("    <scrapper>" + scrapper + "</scrapper>" + endl);
      
      // booleans
      out.write("    <useExtensionFilter>" + (useExtensionFilter ? 0 : 1) + "</useExtensionFilter>" + endl);
      out.write("    <showMovieFilePath>" + (showMovieFilePath ? 0 : 1) + "</showMovieFilePath>" + endl);
      out.write("    <scanSubfolder>" + (scanSubfolder ? 0 : 1) + "</scanSubfolder>" + endl);
      out.write("    <hideRenamedMovie>" + (hideRenamedMovie ? 0 : 1) + "</hideRenamedMovie>" + endl);
      out.write("    <displayApproximateResult>" + (displayApproximateResult ? 0 : 1) + "</displayApproximateResult>" + endl);
      out.write("    <displayThumbResult>" + (displayThumbResult ? 0 : 1) + "</displayThumbResult>" + endl);
      out.write("    <downThumb>" + (downThumb ? 0 : 1) + "</downThumb>" + endl);
      out.write("    <downFanart>" + (downFanart ? 0 : 1) + "</downFanart>" + endl);
      out.write("    <downTrailer>" + (downTrailer ? 0 : 1) + "</downTrailer>" + endl);
      out.write("    <createMovieDirectory>" + (createMovieDirectory ? 0 : 1) + "</createMovieDirectory>" + endl);
      out.write("    <imdbInfo>" + (imdbInfo ? 0 : 1) + "</imdbInfo>" + endl);
      out.write("    <imdbFr>" + (imdbFr ? 0 : 1) + "</imdbFr>" + endl);
      out.write("    <selectFrstMovie>" + (selectFrstMovie ? 0 : 1) + "</selectFrstMovie>" + endl);
      out.write("    <selectFrstRes>" + (selectFrstRes ? 0 : 1) + "</selectFrstRes>" + endl);
      out.write("    <movieInfoPanel>" + (movieInfoPanel ? 0 : 1) + "</movieInfoPanel>" + endl);
      out.write("    <actorImage>" + (actorImage ? 0 : 1) + "</actorImage>" + endl);
      out.write("    <thumb>" + (thumb ? 0 : 1) + "</thumb>" + endl);
      out.write("    <fanart>" + (fanart ? 0 : 1) + "</fanart>" + endl);
      out.write("    <checkUpdate>" + (checkUpdate ? 0 : 1) + "</checkUpdate>" + endl);
      out.write("    <showNotaMovieWarn>" + (showNotaMovieWarn ? 0 : 1) + "</showNotaMovieWarn>" + endl);
      out.write("    <autoSearchMovie>" + (autoSearchMovie ? 0 : 1) + "</autoSearchMovie>" + endl);
      out.write("    <rmSpcChar>" + (rmSpcChar ? 0 : 1) + "</rmSpcChar>" + endl);
      out.write("    <rmDupSpace>" + (rmDupSpace ? 0 : 1) + "</rmDupSpace>" + endl);
      out.write("    <tvdbFr>" + (tvdbFr ? 0 : 1) + "</tvdbFr>" + endl);
      out.write("    <clearXMLCache>" + (clearXMLCache ? 0 : 1) + "</clearXMLCache>" + endl);

      out.write("  </setting>" + endl);
      out.write("</Movie_Renamer>" + endl);
      out.close();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
      return false;
    }
    return true;
  }

  /**
   * Get Movie Renamer version
   * @return Movie Renamer Version
   */
  public String getVersion() {
    return version;
  }
}
