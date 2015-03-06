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
import fr.free.movierenamer.scraper.MovieScraper;
import fr.free.movierenamer.scraper.impl.movie.IMDbScraper;
import fr.free.movierenamer.scraper.impl.movie.UniversalScraper;
import fr.free.movierenamer.utils.LocaleUtils.AppLanguages;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.StringUtils;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

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

    reservedCharacter(Boolean.TRUE, SettingsType.FORMAT, SettingsSubType.GENERAL),
    filenameTrim(Boolean.TRUE, SettingsType.FORMAT, SettingsSubType.GENERAL),
    filenameRmDupSpace(Boolean.TRUE, SettingsType.FORMAT, SettingsSubType.GENERAL),
    filenameRomanUpper(Boolean.TRUE, SettingsType.FORMAT, SettingsSubType.GENERAL),
    // movie filename
    movieFilenameFormat("<t> (<y>)", SettingsType.FORMAT, SettingsSubType.MOVIE),
    movieFilenameSeparator(", ", SettingsType.FORMAT, SettingsSubType.MOVIE),
    movieFilenameLimit(3, SettingsType.FORMAT, SettingsSubType.MOVIE),
    movieFilenameCase(StringUtils.CaseConversionType.FIRSTLO, SettingsType.FORMAT, SettingsSubType.MOVIE),
    // format
    stringTimeHour("h", SettingsType.FORMAT, SettingsSubType.TIME),
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
    searchMovieScraper(UniversalScraper.class, SettingsType.SEARCH, SettingsSubType.SCRAPER),
    searchScraperLang(AvailableLanguages.en, SettingsType.SEARCH, SettingsSubType.SCRAPER),
    searchGetTmdbTag(Boolean.TRUE, SettingsType.SEARCH, SettingsSubType.SCRAPER),
    searchGetOnlyLangDep(Boolean.TRUE, SettingsType.SEARCH, SettingsSubType.SCRAPER),
    //searchSetOrigTitle(Boolean.FALSE, SettingsType.SEARCH, SettingsSubType.SCRAPER),
    //    searchTvshowScraper(TheTVDBScraper.class, SettingsType.SEARCH, SettingsSubType.SCRAPER), // (TheTVDBScraper.class.toString()),
    //    searchSubtitleScraper(OpenSubtitlesScraper.class, SettingsType.SEARCH, SettingsSubType.SCRAPER), // (IMDbScraper.class.toString()),// FIXME
    // http param

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
    formatTokenStart("<", SettingsType.ADVANCED, SettingsSubType.FORMATPARSER),
    formatTokenEnd(">", SettingsType.ADVANCED, SettingsSubType.FORMATPARSER),
    formatOptionSeparator(':', SettingsType.ADVANCED, SettingsSubType.FORMATPARSER),
    formatEqualsSeparator('=', SettingsType.ADVANCED, SettingsSubType.FORMATPARSER),
    formatNotEqualsSeparator('!', SettingsType.ADVANCED, SettingsSubType.FORMATPARSER),
    formatValueIndex(Pattern.compile("[a-z]+(\\d+)"), SettingsType.ADVANCED, SettingsSubType.FORMATPARSER),
    matcherNfofileExt(Arrays.asList(new String[]{"nfo", "xml"}), SettingsType.ADVANCED),
    httpRequestTimeOut(30, SettingsType.ADVANCED, SettingsSubType.NETWORK),
    httpCustomUserAgent("", SettingsType.ADVANCED, SettingsSubType.NETWORK),
    // Scraper options
    universalSearchScraper(IMDbScraper.class),
    universalSynopsys(IMDbScraper.class),
    universalCasting(IMDbScraper.class),
    universalRating(IMDbScraper.class),
    universalGenre(IMDbScraper.class),
    universalCountry(IMDbScraper.class);

    private Class<?> vclass;
    private Object defaultValue;
    private SettingsType type;
    private SettingsSubType subType;
    private boolean haschild;

    private SettingsProperty(Object defaultValue) {
      this(defaultValue, null, null);
    }

    private SettingsProperty(Object defaultValue, SettingsType type) {
      this(defaultValue, type, null);
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
  public Class<? extends MovieScraper> getSearchMovieScraper() {
    try {
      return (Class<MovieScraper>) Class.forName(get(SettingsProperty.searchMovieScraper).replace("class ", ""));
    } catch (Exception ex) {
    }

    return IMDbScraper.class;
  }

  @SuppressWarnings("unchecked")
  public Class<? extends MovieScraper> getUniversalSearchMovieScraper() {
    try {
      return (Class<MovieScraper>) Class.forName(get(SettingsProperty.universalSearchScraper).replace("class ", ""));
    } catch (Exception ex) {
    }

    return IMDbScraper.class;
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
  public Class<? extends MovieScraper> getMovieScraperOptionClass(SettingsProperty property) {

    Class<?> clazz = getOptionClass(property);
    if (clazz != null) {
      return (Class<? extends MovieScraper>) clazz;
    }

    return IMDbScraper.class;
  }

//  @SuppressWarnings("unchecked")
//  public Class<? extends TvShowScraper> getSearchTvshowScraper() {
//    try {
//      return (Class<TvShowScraper>) Class.forName(get(SettingsProperty.searchTvshowScraper).replace("class ", ""));
//    } catch (Exception ex) {
//      return TheTVDBScraper.class;
//    }
//  }
//
//  @SuppressWarnings("unchecked")
//  public Class<? extends SubtitleScraper> getSearchSubtitleScraper() {
//    try {
//      return (Class<SubtitleScraper>) Class.forName(get(SettingsProperty.searchSubtitleScraper).replace("class ", ""));
//    } catch (Exception ex) {
//      return SubsceneSubtitleScraper.class;
//    }
//  }
  public AvailableLanguages getSearchScraperLang() {
    return AvailableLanguages.valueOf(get(SettingsProperty.searchScraperLang));
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

  public String getFormatTokenStart() {
    return get(SettingsProperty.formatTokenStart);
  }

  public String getFormatTokenEnd() {
    return get(SettingsProperty.formatTokenEnd);
  }

  public char getFormatOptionSeparator() {
    return get(SettingsProperty.formatOptionSeparator).charAt(0);
  }

  public char getFormatEqualsSeparator() {
    return get(SettingsProperty.formatEqualsSeparator).charAt(0);
  }

  public char getFormatNotEqualsSeparator() {
    return get(SettingsProperty.formatNotEqualsSeparator).charAt(0);
  }

  public Pattern getFormatValueIndex() {
    return Pattern.compile(get(SettingsProperty.formatValueIndex));
  }

  public List<String> getfileExtension() {
    String ext = get(SettingsProperty.fileExtension);
    ext = ext.substring(1, ext.length() - 1);
    return Arrays.asList(ext.split(", "));
  }

  public List<String> getMatcherNfofileExt() {
    String ext = get(SettingsProperty.matcherNfofileExt);
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
