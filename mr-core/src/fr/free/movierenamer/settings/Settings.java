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

import fr.free.movierenamer.renamer.NameCleaner;
import fr.free.movierenamer.renamer.Nfo.NFOtype;
import fr.free.movierenamer.scraper.MediaScraper;
import fr.free.movierenamer.scraper.MovieScraper;
import fr.free.movierenamer.scraper.impl.movie.IMDbScraper;
import fr.free.movierenamer.scraper.impl.movie.UniversalScraper;
import fr.free.movierenamer.scraper.impl.tvshow.TheTVDBScraper;
import fr.free.movierenamer.searchinfo.Media.MediaType;
import fr.free.movierenamer.utils.LocaleUtils.AppLanguages;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.StringUtils;
import fr.free.movierenamer.utils.StringUtils.CaseConversionType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

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
  private static final Map<MediaType, Class> InfoScraper = new HashMap<>();
  private static final Map<MediaType, String> FilenameFormat = new HashMap<>();
  private static final Map<MediaType, String> FilenameSeparator = new HashMap<>();
  private static final Map<MediaType, Integer> FilenameLimit = new HashMap<>();
  private static final Map<MediaType, CaseConversionType> FilenameCase = new HashMap<>();
  private static final Map<MediaType, String> NFOFilename = new HashMap<>();
  private static final Map<MediaType, Boolean> NfoTag = new HashMap<>();
  private static final Map<MediaType, Boolean> NfoImage = new HashMap<>();
  private static final Map<MediaType, Boolean> NfoImdbId = new HashMap<>();
  private static final Map<MediaType, Boolean> Nfogenerate = new HashMap<>();
  private static final Map<MediaType, NFOtype> NfoType = new HashMap<>();
  private static final Map<MediaType, Boolean> MediaRuntime = new HashMap<>();
  private static final Map<MediaType, Boolean> TmdbTag = new HashMap<>();
  private static final Map<MediaType, Boolean> FilenameReplaceSpace = new HashMap<>();
  private static final Map<MediaType, String> FilenameReplaceSpaceBy = new HashMap<>();

  // Logger
  public static final Logger LOGGER;

  static {
    String appModule = getApplicationProperty("application.module.name");
    String appModuleNospace = appModule.replace(' ', '_');

    APPMODULE = appModule;
    APPMODULE_NOSPACE = appModule.replace(' ', '_');
    VERSION = getApplicationProperty("application.module.version");
    LOGGER = Logger.getLogger(appModule);

    InfoScraper.put(MediaType.MOVIE, UniversalScraper.class);
    InfoScraper.put(MediaType.TVSHOW, TheTVDBScraper.class);
    MediaRuntime.put(MediaType.MOVIE, Boolean.FALSE);
    TmdbTag.put(MediaType.MOVIE, Boolean.TRUE);
    FilenameFormat.put(MediaType.MOVIE, "<t> (<y>)");
    FilenameFormat.put(MediaType.TVSHOW, "<Sx> (<y>)");
    FilenameSeparator.put(MediaType.MOVIE, ", ");
    FilenameLimit.put(MediaType.MOVIE, 3);
    FilenameCase.put(MediaType.MOVIE, CaseConversionType.FIRSTLO);
    FilenameReplaceSpace.put(MediaType.MOVIE, Boolean.FALSE);
    FilenameReplaceSpaceBy.put(MediaType.MOVIE, ".");
    FilenameReplaceSpace.put(MediaType.TVSHOW, Boolean.FALSE);
    FilenameReplaceSpaceBy.put(MediaType.TVSHOW, ".");

    NFOFilename.put(MediaType.MOVIE, "<fileName>.nfo");
    NfoTag.put(MediaType.MOVIE, Boolean.TRUE);
    NfoImage.put(MediaType.MOVIE, Boolean.TRUE);
    NfoImdbId.put(MediaType.MOVIE, Boolean.TRUE);
    Nfogenerate.put(MediaType.MOVIE, Boolean.TRUE);
    NfoType.put(MediaType.MOVIE, NFOtype.XBMC);
  }

  public enum SettingsProperty implements ISimpleProperty {

    reservedCharacter(Boolean.TRUE, SettingsType.FILE, SettingsSubType.GENERAL),
    filenameTrim(Boolean.TRUE, SettingsType.FILE, SettingsSubType.GENERAL),
    filenameRmDupSpace(Boolean.TRUE, SettingsType.FILE, SettingsSubType.GENERAL),
    filenameRomanUpper(Boolean.TRUE, SettingsType.FILE, SettingsSubType.GENERAL),
    // format
    stringTimeHour("h", SettingsType.MISCELLANEOUS, SettingsSubType.TIME),
    stringTimeMinute("min ", SettingsType.MISCELLANEOUS, SettingsSubType.TIME),
    stringTimeSeconde("s ", SettingsType.MISCELLANEOUS, SettingsSubType.TIME),
    stringTimeMilliSeconde("ms", SettingsType.MISCELLANEOUS, SettingsSubType.TIME),
    stringTimeShowSeconde(Boolean.FALSE, SettingsType.MISCELLANEOUS, SettingsSubType.TIME),
    stringTimeShowMillis(Boolean.FALSE, SettingsType.MISCELLANEOUS, SettingsSubType.TIME),
    // Search
    searchNbResult(15, SettingsType.INFORMATION, SettingsSubType.SEARCH),
    searchOrder(Boolean.TRUE, SettingsType.INFORMATION, SettingsSubType.SEARCH),
    searchOrderThreshold(0.7F, SettingsType.INFORMATION, SettingsSubType.SEARCH, searchOrder),
    searchScraperLang(AvailableLanguages.en, SettingsType.INFORMATION, SettingsSubType.SCRAPER),
    searchGetOnlyLangDep(Boolean.TRUE, SettingsType.INFORMATION, SettingsSubType.SCRAPER),
    // Proxy
    proxyIsOn(Boolean.FALSE, SettingsType.NETWORK, SettingsSubType.PROXY),
    proxyUrl("", SettingsType.NETWORK, SettingsSubType.PROXY, proxyIsOn),
    proxyPort(80, SettingsType.NETWORK, SettingsSubType.PROXY, proxyIsOn),
    proxyUser("", SettingsType.NETWORK, SettingsSubType.PROXY, proxyIsOn),
    proxyPass(new char[0], SettingsType.NETWORK, SettingsSubType.PROXY, proxyIsOn, SettingsPropertyType.PASSWORD),
    proxyIsSocks(Boolean.FALSE, SettingsType.NETWORK, SettingsSubType.PROXY, proxyIsOn),
    // Extension
    fileExtension(Arrays.asList(NameCleaner.getCleanerProperty("file.extension").split("\\|")), SettingsType.MISCELLANEOUS, SettingsSubType.GENERAL),
    //app lang
    appLanguage(AppLanguages.en, SettingsType.GENERAL, SettingsSubType.LANGUAGE),
    formatTokenStart("<", SettingsType.ADVANCED, SettingsSubType.FORMATPARSER),
    formatTokenEnd(">", SettingsType.ADVANCED, SettingsSubType.FORMATPARSER),
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

    private final Class<?> vclass;
    private final Object defaultValue;
    private final SettingsType type;
    private final SettingsSubType subType;
    private final SettingsProperty parent;
    private final SettingsPropertyType pType;
    private boolean haschild = false;

    private SettingsProperty(Object defaultValue) {
      this(defaultValue, null, null);
    }

    private SettingsProperty(Object defaultValue, SettingsType type) {
      this(defaultValue, type, null);
    }

    private SettingsProperty(Object defaultValue, SettingsType type, SettingsSubType subType) {
      this(defaultValue, type, subType, (SettingsProperty) null);
    }

    private SettingsProperty(Object defaultValue, SettingsType type, SettingsSubType subType, SettingsPropertyType pType) {
      this(defaultValue, type, subType, null, pType);
    }

    private SettingsProperty(Object defaultValue, SettingsType type, SettingsSubType subType, SettingsProperty parent) {
      this(defaultValue, type, subType, parent, SettingsPropertyType.NONE);
    }

    private SettingsProperty(Object defaultValue, SettingsType type, SettingsSubType subType, SettingsProperty parent, SettingsPropertyType pType) {
      this.vclass = defaultValue.getClass();
      this.defaultValue = defaultValue;
      this.type = type;
      this.subType = subType;
      this.parent = parent;
      this.pType = pType;

      if (parent != null) {
        parent.setHasChild();
        if (!(parent.getDefaultValue() instanceof Boolean)) {
          throw new UnsupportedOperationException("Only boolean value can have a child");
        }
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
    public boolean isChild() {
      return parent != null;
    }

    @Override
    public IProperty getParent() {
      return parent;
    }

    @Override
    public SettingsPropertyType getPropertyType() {
      return pType;
    }

    @Override
    public boolean hasChild() {
      return haschild;
    }

    @Override
    public void setHasChild() {
      haschild = true;
    }

  }

  public enum SettingsMediaProperty implements IMediaProperty {

    infoScraper(InfoScraper, SettingsType.INFORMATION, SettingsSubType.SCRAPER),
    infoUseFileRuntime(MediaRuntime, SettingsType.INFORMATION, SettingsSubType.SCRAPER),
    infoGetTmdbTag(TmdbTag, SettingsType.INFORMATION, SettingsSubType.SCRAPER),
    mediaFilenameFormat(FilenameFormat, SettingsType.FILE, SettingsSubType.GENERAL),
    mediaFilenameSeparator(FilenameSeparator, SettingsType.FILE, SettingsSubType.GENERAL),
    mediaFilenameLimit(FilenameLimit, SettingsType.FILE, SettingsSubType.GENERAL),
    mediaFilenameCase(FilenameCase, SettingsType.FILE, SettingsSubType.GENERAL),
    mediaFilenameReplaceSpace(FilenameReplaceSpace, SettingsType.FILE, SettingsSubType.GENERAL),
    mediaFilenameReplaceSpaceBy(FilenameReplaceSpaceBy, SettingsType.FILE, SettingsSubType.GENERAL, mediaFilenameReplaceSpace),
    mediaNFOFilename(NFOFilename, SettingsType.NFO, SettingsSubType.GENERAL),
    mediaNfoTag(NfoTag, SettingsType.NFO, SettingsSubType.GENERAL),
    mediaNfoImage(NfoImage, SettingsType.NFO, SettingsSubType.GENERAL),
    mediaNfoImdbId(NfoImdbId, SettingsType.NFO, SettingsSubType.GENERAL),
    mediaNfogenerate(Nfogenerate, SettingsType.NFO, SettingsSubType.GENERAL),
    mediaNfoType(NfoType, SettingsType.NFO, SettingsSubType.MEDIACENTER);

    private final Class<?> vclass;
    private final Map<MediaType, ?> defaultValue;
    private final SettingsType type;
    private final SettingsSubType subType;
    private final SettingsMediaProperty parent;
    private final List<MediaType> mediatypes;
    private boolean haschild = false;

    private SettingsMediaProperty(Map<MediaType, ?> defaultValue) {
      this(defaultValue, null, null);
    }

    private SettingsMediaProperty(Map<MediaType, ?> defaultValue, SettingsType type) {
      this(defaultValue, type, null);
    }

    private SettingsMediaProperty(Map<MediaType, ?> defaultValue, SettingsType type, SettingsSubType subType) {
      this(defaultValue, type, subType, null);
    }

    private SettingsMediaProperty(Map<MediaType, ?> defaultValue, SettingsType type, SettingsSubType subType, SettingsMediaProperty parent) {
      Map.Entry<MediaType, ?> entry = defaultValue.entrySet().iterator().next();
      mediatypes = new ArrayList<>(defaultValue.keySet());

      this.vclass = entry.getValue().getClass();
      this.defaultValue = defaultValue;
      this.type = type;
      this.subType = subType;
      this.parent = parent;

      if (parent != null) {
        parent.setHasChild();
        if (!(parent.getVclass().getSimpleName().equalsIgnoreCase("boolean"))) {
          throw new UnsupportedOperationException("Only boolean value can have a child");
        }
      }
    }

    @Override
    public Class<?> getVclass() {
      return vclass;
    }

    @Override
    public Class<?> getKclass() {
      return MediaType.class;
    }

    @Override
    public Object getDefaultValue(MediaType mediaType) {
      return defaultValue.get(mediaType);
    }

    @Override
    public String getValue(MediaType mediaType) {
      return instance.get(this, mediaType);
    }

    @Override
    public void setValue(MediaType mediaType, Object value) {
      instance.set(this, mediaType, value);
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
    public boolean isChild() {
      return parent != null;
    }

    @Override
    public IProperty getParent() {
      return parent;
    }

    @Override
    public boolean hasMediaType(MediaType mediaType) {
      return mediatypes.contains(mediaType);
    }

    @Override
    public boolean hasChild() {
      return haschild;
    }

    @Override
    public void setHasChild() {
      haschild = true;
    }

  }

  public enum LogLevel {

    SEVERE(Level.SEVERE),
    WARNING(Level.WARNING),
    INFO(Level.INFO),
    CONFIG(Level.CONFIG),
    FINE(Level.FINE),
    FINER(Level.FINER),
    FINEST(Level.FINEST);
    private final Level level;

    private LogLevel(Level level) {
      this.level = level;
    }

    public Level getLevel() {
      return level;
    }
  }

  // Settings instance
  private static final Settings instance = new Settings();

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

  public static void setLogLevel(Level level) {
    LOGGER.setLevel(level);
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

  public String getMediaFilenameFormat(MediaType mediaType) {
    return get(SettingsMediaProperty.mediaFilenameFormat, mediaType);
  }

  public String getMediaFilenameSeparator(MediaType mediaType) {
    return get(SettingsMediaProperty.mediaFilenameSeparator, mediaType);
  }

  public int getMediaFilenameLimit(MediaType mediaType) {
    return Integer.parseInt(get(SettingsMediaProperty.mediaFilenameLimit, mediaType));
  }

  public CaseConversionType getMediaFilenameCase(MediaType mediaType) {
    return StringUtils.CaseConversionType.valueOf(get(SettingsMediaProperty.mediaFilenameCase, mediaType));
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

  public boolean isFilenameReplaceSpace(MediaType mediaType) {
    return Boolean.parseBoolean(get(SettingsMediaProperty.mediaFilenameReplaceSpace, mediaType));
  }

  public String getFilenameReplaceSpaceBy(MediaType mediaType) {
    return get(SettingsMediaProperty.mediaFilenameReplaceSpaceBy, mediaType);
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

  public NFOtype getMediaNfoType(MediaType mediaType) {
    return NFOtype.valueOf(get(SettingsMediaProperty.mediaNfoType, mediaType));
  }

  public String getMediaNFOFileName(MediaType mediaType) {
    return get(SettingsMediaProperty.mediaNFOFilename, mediaType);
  }

  public boolean isMediaNfoTag(MediaType mediaType) {
    return Boolean.parseBoolean(get(SettingsMediaProperty.mediaNfoTag, mediaType));
  }

  public boolean isMediaNfoImage(MediaType mediaType) {
    return Boolean.parseBoolean(get(SettingsMediaProperty.mediaNfoImage, mediaType));
  }

  public boolean isMediaImdbId(MediaType mediaType) {
    return Boolean.parseBoolean(get(SettingsMediaProperty.mediaNfoImdbId, mediaType));
  }

  public boolean isMediaNfogenerate(MediaType mediaType) {
    return Boolean.parseBoolean(get(SettingsMediaProperty.mediaNfogenerate, mediaType));
  }

  public int getSearchNbResult() {
    return Integer.parseInt(get(SettingsProperty.searchNbResult));
  }

  public boolean isGetTmdbTag(MediaType mediaType) {
    return Boolean.parseBoolean(get(SettingsMediaProperty.infoGetTmdbTag, mediaType));
  }

  public boolean isGetOnlyLangDepInfo() {
    return Boolean.parseBoolean(get(SettingsProperty.searchGetOnlyLangDep));
  }

  public boolean isUseFileRuntime(MediaType mediaType) {
    return Boolean.parseBoolean(get(SettingsMediaProperty.infoUseFileRuntime, mediaType));
  }

  public boolean isSetOrigTitle() {
    return false; /*Boolean.parseBoolean(get(SettingsProperty.searchSetOrigTitle));*/

  }

  public boolean isSearchOrder() {
    return Boolean.parseBoolean(get(SettingsProperty.searchOrder));
  }

  public float getSearchOrderThreshold() {
    return Float.parseFloat(get(SettingsProperty.searchOrderThreshold));
  }

  @SuppressWarnings("unchecked")
  public Class<? extends MediaScraper> getInfoScraper(MediaType mediaType) {
    try {
      return (Class<MediaScraper>) Class.forName(get(SettingsMediaProperty.infoScraper, mediaType).replace("class ", ""));

    } catch (Exception ex) {
    }

    return mediaType.getDefaultScraper();
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

  public static String
    getApplicationProperty(String key) {
    return ResourceBundle.getBundle(Settings.class
      .getName(), Locale.ROOT).getString(key);
  }

}
