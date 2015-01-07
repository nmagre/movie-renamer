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

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import fr.free.movierenamer.renamer.Nfo;

import fr.free.movierenamer.renamer.NameCleaner;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.scrapper.impl.movie.IMDbScrapper;
import fr.free.movierenamer.scrapper.impl.movie.UniversalScrapper;
import fr.free.movierenamer.utils.LocaleUtils.AppLanguages;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.StringUtils;
import java.util.Arrays;
import java.util.List;

/**
 * Class Settings , Movie Renamer settings
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public final class Settings extends XMLSettings {

  public static final String APPMODULE;
  public static final String APPMODULE_NOSPACE;
  public static final String VERSION;

  // Logger
  public static final Logger LOGGER;

  static {
    String appModule = getApplicationProperty("application.module.name");
    String appModuleNospace = appModule.replace(' ', '_');

    APPMODULE = appModule;
    APPMODULE_NOSPACE = appModule.replace(' ', '_');
    VERSION = getApplicationProperty("application.module.version");
    LOGGER = Logger.getLogger(appModule);
  }

  // Settings instance
  private static final Settings instance = new Settings();

  public enum SettingsProperty implements IProperty {

    reservedCharacter(Boolean.TRUE, SettingsType.MEDIA, SettingsSubType.GENERAL),
    filenameTrim(Boolean.TRUE, SettingsType.MEDIA, SettingsSubType.GENERAL),
    filenameRmDupSpace(Boolean.TRUE, SettingsType.MEDIA, SettingsSubType.GENERAL),
    filenameRomanUpper(Boolean.TRUE, SettingsType.MEDIA, SettingsSubType.GENERAL),
    // movie filename
    movieFilenameFormat("<t> (<y>)", SettingsType.MEDIA, SettingsSubType.MOVIE),
    movieFilenameSeparator(", ", SettingsType.MEDIA, SettingsSubType.MOVIE),
    movieFilenameLimit(3, SettingsType.MEDIA, SettingsSubType.MOVIE),
    movieFilenameCase(StringUtils.CaseConversionType.FIRSTLO, SettingsType.MEDIA, SettingsSubType.MOVIE),
    // format
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
    searchScrapperLang(AvailableLanguages.en, SettingsType.SEARCH, SettingsSubType.SCRAPER),
    searchGetTmdbTag(Boolean.TRUE, SettingsType.SEARCH, SettingsSubType.SCRAPER),
    searchGetOnlyLangDep(Boolean.TRUE, SettingsType.SEARCH, SettingsSubType.SCRAPER),
    //searchSetOrigTitle(Boolean.FALSE, SettingsType.SEARCH, SettingsSubType.SCRAPER),
    //    searchTvshowScrapper(TheTVDBScrapper.class, SettingsType.SEARCH, SettingsSubType.SCRAPPER), // (TheTVDBScrapper.class.toString()),
    //    searchSubtitleScrapper(OpenSubtitlesScrapper.class, SettingsType.SEARCH, SettingsSubType.SCRAPPER), // (IMDbScrapper.class.toString()),// FIXME
    // http param
    httpRequestTimeOut(30, SettingsType.NETWORK, SettingsSubType.GENERAL),
    httpCustomUserAgent("", SettingsType.NETWORK, SettingsSubType.GENERAL),
    // Proxy
    proxyIsOn(Boolean.FALSE, SettingsType.NETWORK, SettingsSubType.PROXY, true),
    proxyUrl("", SettingsType.NETWORK, SettingsSubType.PROXY),
    proxyPort(80, SettingsType.NETWORK, SettingsSubType.PROXY),
    proxyUser("", SettingsType.NETWORK, SettingsSubType.PROXY),
    proxyPass(new char[0], SettingsType.NETWORK, SettingsSubType.PROXY),
    proxyIsSocks(Boolean.FALSE, SettingsType.NETWORK, SettingsSubType.PROXY),
    // Extension
    fileExtension(Arrays.asList(NameCleaner.getCleanerProperty("file.extension").split("\\|")), SettingsType.EXTENSION, SettingsSubType.GENERAL),
    //app lang
    appLanguage(AppLanguages.en, SettingsType.GENERAL, SettingsSubType.LANGUAGE),
    // Scraper options
    universalSearchScraper(IMDbScrapper.class),
    universalSynopsys(IMDbScrapper.class),
    universalCasting(IMDbScrapper.class),
    universalRating(IMDbScrapper.class),
    universalGenre(IMDbScrapper.class),
    universalCountry(IMDbScrapper.class);
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
   * Constructor
   */
  private Settings() {
    super(LOGGER, APPNAME_NOSPACE + "_" + APPMODULE_NOSPACE + ".log", APPNAME_NOSPACE + "_" + APPMODULE_NOSPACE + ".conf", VERSION);
  }

  /**
   * Access to the Settings instance
   *
   * @return The only instance of MR Settings
   */
  public static Settings getInstance() {
    return instance;
  }

  @Override
  protected String getAppSettingsNodeName() {
    return APPNAME_NOSPACE + "_" + APPMODULE_NOSPACE;
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

  public boolean isFilenameTrim() {
    return Boolean.parseBoolean(get(SettingsProperty.filenameTrim));
  }

  public boolean isFilenameRmDupSpace() {
    return Boolean.parseBoolean(get(SettingsProperty.filenameRmDupSpace));
  }

  public boolean isFilenameRomanUpper() {
    return Boolean.parseBoolean(get(SettingsProperty.filenameRomanUpper));
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

  public boolean isGetOnlyLangDepInfo() {
    return Boolean.parseBoolean(get(SettingsProperty.searchGetOnlyLangDep));

  }

  public boolean isSetOrigTitle() {
    return false; /*Boolean.parseBoolean(get(SettingsProperty.searchSetOrigTitle));*/

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
    }

    return IMDbScrapper.class;
  }

  @SuppressWarnings("unchecked")
  public Class<? extends MovieScrapper> getUniversalSearchMovieScrapper() {
    try {
      return (Class<MovieScrapper>) Class.forName(get(SettingsProperty.universalSearchScraper).replace("class ", ""));
    } catch (Exception ex) {
    }

    return IMDbScrapper.class;
  }

  public Class<?> getOptionClass(SettingsProperty property) {
    if (!(property.getDefaultValue() instanceof Class)) {
      return null;
    }

    try {
      return (Class<?>) Class.forName(get(property).replace("class ", ""));
    } catch (ClassNotFoundException ex) {
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  public Class<? extends MovieScrapper> getMovieScrapperOptionClass(SettingsProperty property) {

    Class<?> clazz = getOptionClass(property);
    if (clazz != null) {
      return (Class<? extends MovieScrapper>) clazz;
    }

    return IMDbScrapper.class;
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

  public String getProxyUser() {
    return get(SettingsProperty.proxyUser);
  }

  public char[] getProxyPass() {
    return StringUtils.decrypt(get(SettingsProperty.proxyPass)).toCharArray();
  }

  public boolean isProxySocks() {
    return Boolean.parseBoolean(get(SettingsProperty.proxyIsSocks));
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

  public static String decodeApkKey(String apkkey) {
    return new String(DatatypeConverter.parseBase64Binary(StringUtils.rot13(apkkey)));
  }

  public static String getApplicationProperty(String key) {
    return ResourceBundle.getBundle(Settings.class.getName(), Locale.ROOT).getString(key);
  }

}
