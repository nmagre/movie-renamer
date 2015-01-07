/*
 * Movie Renamer
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
package fr.free.movierenamer.ui.settings;

import com.alee.laf.filechooser.FileChooserViewType;
import com.alee.laf.rootpane.WebFrame;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.settings.XMLSettings;
import fr.free.movierenamer.settings.XMLSettings.SettingsSubType;
import fr.free.movierenamer.settings.XMLSettings.SettingsType;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Class Settings , Movie Renamer settings Only public and non static attributes
 * are written in conf file !
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public final class UISettings extends XMLSettings {

  static {
    VERSION = getApplicationProperty("application.version");
    CORE_VERSION = Settings.VERSION;
    userFolder = System.getProperty("user.home");
    languagePrefix = "mrui";
    renamedFile = "renamed.xml";
    languageFile = "language.xml";
    LOGGER = Logger.getLogger("UI");
  }

  public static final String VERSION;
  public static final String CORE_VERSION;

  private static final String userFolder;
  public static final String languagePrefix;
  // files
  public static final String renamedFile;
  public static final String languageFile;
  // Logger
  public static final Logger LOGGER;
  // Settings instance
  private static final UISettings instance = new UISettings();
  public final Settings coreInstance = Settings.getInstance();

  @Override
  protected String getAppSettingsNodeName() {
    return APPNAME_NOSPACE;
  }

  public static enum ImageSize {

    ORIGINAL,
    MEDIUM,
    SMALL;
  }

  public static enum ImageFormat {

    PNG,
    JPG,
    GIF
  }

  public static enum Subfolder {

    BROWSE,
    NOTBROWSE,
    ASK
  }

  public enum UISettingsProperty implements XMLSettings.IProperty {

    // General
    selectFirstMedia(Boolean.FALSE, SettingsType.GENERAL, SettingsSubType.GENERAL),
    selectFirstResult(Boolean.TRUE, SettingsType.GENERAL, SettingsSubType.GENERAL),
    moveFileOneByOne(Boolean.TRUE/*, SettingsType.GENERAL, SettingsSubType.GENERAL*/),// not a good idea to let user change this for the moment
    mediaInfoWarning(Boolean.TRUE, SettingsType.GENERAL, SettingsSubType.GENERAL),
    debug(Boolean.FALSE, SettingsType.GENERAL, SettingsSubType.GENERAL),
    scanSubfolder(Subfolder.ASK, SettingsType.GENERAL, SettingsSubType.FOLDER),
    loadFileAtStartup(Boolean.FALSE, SettingsType.GENERAL, SettingsSubType.FOLDER, true),
    loadFilePath(userFolder, SettingsType.GENERAL, SettingsSubType.FOLDER),
    checkupdate(Boolean.TRUE, SettingsType.GENERAL, SettingsSubType.UPDATE),
    // Interface
    mainFrameSaveState(Boolean.FALSE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showStartupAnim(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showMediaPanel(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showImagePanel(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showActorImage(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showIconMediaList(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showFormatField(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showIdResultList(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showYearResultList(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showOrigTitleResultList(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showThumbResultList(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    groupMediaList(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    // Extension
    useExtensionFilter(Boolean.TRUE, SettingsType.EXTENSION, SettingsSubType.GENERAL),
    // Image
    generateThumb(Boolean.TRUE, SettingsType.IMAGE, SettingsSubType.GENERAL),
    generateFanart(Boolean.TRUE, SettingsType.IMAGE, SettingsSubType.GENERAL),
    generateCdart(Boolean.TRUE, SettingsType.IMAGE, SettingsSubType.GENERAL),
    generateClearart(Boolean.TRUE, SettingsType.IMAGE, SettingsSubType.GENERAL),
    generateLogo(Boolean.TRUE, SettingsType.IMAGE, SettingsSubType.GENERAL),
    generateBanner(Boolean.TRUE, SettingsType.IMAGE, SettingsSubType.GENERAL),
    imageFormat(ImageFormat.PNG, SettingsType.IMAGE, SettingsSubType.GENERAL),
    numberImageGallery(15, SettingsType.IMAGE, SettingsSubType.GENERAL),
    imageThumbName("<fileName>-poster.png", SettingsType.IMAGE, SettingsSubType.THUMB),
    imageThumbSize(ImageSize.ORIGINAL, SettingsType.IMAGE, SettingsSubType.THUMB),
    imageThumbResize(Boolean.FALSE, SettingsType.IMAGE, SettingsSubType.THUMB, true),
    imageThumbHeight(720, SettingsType.IMAGE, SettingsSubType.THUMB),
    imageFanartName("<fileName>-fanart.png", SettingsType.IMAGE, SettingsSubType.FANART),
    imageFanartSize(ImageSize.ORIGINAL, SettingsType.IMAGE, SettingsSubType.FANART),
    imageFanartResize(Boolean.FALSE, SettingsType.IMAGE, SettingsSubType.FANART, true),
    imageFanartWidth(1080, SettingsType.IMAGE, SettingsSubType.FANART),
    imageLogoName("<fileName>-clearlogo.png", SettingsType.IMAGE, SettingsSubType.LOGO),
    imageLogoSize(ImageSize.ORIGINAL, SettingsType.IMAGE, SettingsSubType.LOGO),
    imageCdartName("<fileName>-discart.png", SettingsType.IMAGE, SettingsSubType.CDART),
    imageCdartSize(ImageSize.ORIGINAL, SettingsType.IMAGE, SettingsSubType.CDART),
    imageClearartName("<fileName>-clearart.png", SettingsType.IMAGE, SettingsSubType.CLEARART),
    imageClearartSize(ImageSize.ORIGINAL, SettingsType.IMAGE, SettingsSubType.CLEARART),
    imageBannerName("<fileName>-banner.png", SettingsType.IMAGE, SettingsSubType.BANNER),
    imageBannerSize(ImageSize.ORIGINAL, SettingsType.IMAGE, SettingsSubType.BANNER),
    // Search
    useImdbIdInSearch(Boolean.FALSE, SettingsType.SEARCH, SettingsSubType.GENERAL),
    // Misc
    fileChooserPath(userFolder),
    fileChooserViewType(FileChooserViewType.tiles),
    mainFrameLocationX(0),
    mainFrameLocationY(0),
    mainFrameSizeWidth(900),
    mainFrameSizeHeight(830),
    mainFrameState(WebFrame.NORMAL),
    mainFrameFileDivider(180),
    mainFrameMediaDivider(200),
    mainFrameScreen(0);
    private Class<?> vclass;
    private Object defaultValue;
    private SettingsType type;
    private SettingsSubType subType;
    private boolean hasChild;

    private UISettingsProperty(Object defaultValue) {
      this(defaultValue, null, null);
    }

    private UISettingsProperty(Object defaultValue, SettingsType type, SettingsSubType subType) {
      this(defaultValue, type, subType, false);
    }

    private UISettingsProperty(Object defaultValue, SettingsType type, SettingsSubType subType, boolean hasChild) {
      this.vclass = defaultValue.getClass();
      this.defaultValue = defaultValue;
      this.type = type;
      this.subType = subType;
      this.hasChild = hasChild;
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
      return hasChild;
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
    super(LOGGER, APPNAME_NOSPACE + ".log", APPNAME_NOSPACE + ".conf", VERSION);
  }

  @Override
  public synchronized void clear() {
    super.clear();
    coreInstance.clear();
  }

  public boolean isSelectFirstMedia() {
    return Boolean.parseBoolean(get(UISettingsProperty.selectFirstMedia));
  }

  public boolean isSelectFirstResult() {
    return Boolean.parseBoolean(get(UISettingsProperty.selectFirstResult));
  }

  public Subfolder getScanSubfolder() {
    return Subfolder.valueOf(get(UISettingsProperty.scanSubfolder));
  }

  public boolean isCheckupdate() {
    return Boolean.parseBoolean(get(UISettingsProperty.checkupdate));
  }

  public boolean isShowActorImage() {
    return Boolean.parseBoolean(get(UISettingsProperty.showActorImage));
  }

  public boolean isShowMediaPanel() {
    return Boolean.parseBoolean(get(UISettingsProperty.showMediaPanel));
  }

  public boolean isShowImagePanel() {
    return Boolean.parseBoolean(get(UISettingsProperty.showImagePanel));
  }

  public boolean isUseImdbIdInSearch() {
    return Boolean.parseBoolean(get(UISettingsProperty.useImdbIdInSearch));
  }

  public boolean isShowIdResultList() {
    return Boolean.parseBoolean(get(UISettingsProperty.showIdResultList));
  }

  public boolean isShowYearResultList() {
    return Boolean.parseBoolean(get(UISettingsProperty.showYearResultList));
  }

  public boolean isShowOrigTitleResultList() {
    return Boolean.parseBoolean(get(UISettingsProperty.showOrigTitleResultList));
  }

  public boolean isShowThumbResultList() {
    return Boolean.parseBoolean(get(UISettingsProperty.showThumbResultList));
  }

  public boolean isGenerateThumb() {
    return Boolean.parseBoolean(get(UISettingsProperty.generateThumb));
  }

  public boolean isGenerateFanart() {
    return Boolean.parseBoolean(get(UISettingsProperty.generateFanart));
  }

  public boolean isGenerateLogo() {
    return Boolean.parseBoolean(get(UISettingsProperty.generateLogo));
  }

  public boolean isGenerateCdart() {
    return Boolean.parseBoolean(get(UISettingsProperty.generateCdart));
  }

  public boolean isGenerateClearart() {
    return Boolean.parseBoolean(get(UISettingsProperty.generateClearart));
  }

  public boolean isGenerateBanner() {
    return Boolean.parseBoolean(get(UISettingsProperty.generateBanner));
  }

  public boolean isMoveFileOneByOne() {
    return Boolean.parseBoolean(get(UISettingsProperty.moveFileOneByOne));
  }

  public boolean isMediaInfoWarning() {
    return Boolean.parseBoolean(get(UISettingsProperty.mediaInfoWarning));
  }

  public boolean isDebug() {
    return Boolean.parseBoolean(get(UISettingsProperty.debug));
  }

  public boolean isLoadFileAtStartup() {
    return Boolean.parseBoolean(get(UISettingsProperty.loadFileAtStartup));
  }

  public String getLoadFilePath() {
    return get(UISettingsProperty.loadFilePath);
  }

  public ImageFormat getImageFormat() {
    return ImageFormat.valueOf(get(UISettingsProperty.imageFormat));
  }

  public int getNumberImageGallery() {
    return Integer.parseInt(get(UISettingsProperty.numberImageGallery));
  }

  public String getImageThumbName() {
    return get(UISettingsProperty.imageThumbName);
  }

  public boolean isImageThumbResize() {
    return Boolean.parseBoolean(get(UISettingsProperty.imageThumbResize));
  }

  public ImageSize getImageThumbSize() {
    return ImageSize.valueOf(get(UISettingsProperty.imageThumbSize));
  }

  public int getImageThumbHeight() {
    return Integer.parseInt(get(UISettingsProperty.imageThumbHeight));
  }

  public String getImageFanartName() {
    return get(UISettingsProperty.imageFanartName);
  }

  public boolean isImageFanartResize() {
    return Boolean.parseBoolean(get(UISettingsProperty.imageFanartResize));
  }

  public ImageSize getImageFanartSize() {
    return ImageSize.valueOf(get(UISettingsProperty.imageFanartSize));
  }

  public int getImageFanartWidth() {
    return Integer.parseInt(get(UISettingsProperty.imageFanartWidth));
  }

  public ImageSize getImageLogoSize() {
    return ImageSize.valueOf(get(UISettingsProperty.imageLogoSize));
  }

  public ImageSize getImageCdartSize() {
    return ImageSize.valueOf(get(UISettingsProperty.imageCdartSize));
  }

  public ImageSize getImageClearartSize() {
    return ImageSize.valueOf(get(UISettingsProperty.imageClearartSize));
  }

  public ImageSize getImageBannerSize() {
    return ImageSize.valueOf(get(UISettingsProperty.imageBannerSize));
  }

  public boolean isUseExtensionFilter() {
    return Boolean.parseBoolean(get(UISettingsProperty.useExtensionFilter));
  }

  public String getFileChooserPath() {
    return get(UISettingsProperty.fileChooserPath);
  }

  public FileChooserViewType getFileChooserViewType() {
    return FileChooserViewType.valueOf(get(UISettingsProperty.fileChooserViewType));
  }

  public boolean isGroupMediaList() {
    return Boolean.parseBoolean(get(UISettingsProperty.groupMediaList));
  }

  public boolean isShowIconMediaList() {
    return Boolean.parseBoolean(get(UISettingsProperty.showIconMediaList));
  }

  public boolean isShowFormatField() {
    return Boolean.parseBoolean(get(UISettingsProperty.showFormatField));
  }

  public boolean isShowStartupAnim() {
    return Boolean.parseBoolean(get(UISettingsProperty.showStartupAnim));
  }

  public int getMainFrameLocationX() {
    return Integer.parseInt(get(UISettingsProperty.mainFrameLocationX));
  }

  public int getMainFrameLocationY() {
    return Integer.parseInt(get(UISettingsProperty.mainFrameLocationY));
  }

  public int getMainFrameSizeWidth() {
    return Integer.parseInt(get(UISettingsProperty.mainFrameSizeWidth));
  }

  public int getMainFrameSizeHeight() {
    return Integer.parseInt(get(UISettingsProperty.mainFrameSizeHeight));
  }

  public int getMainFrameState() {
    return Integer.parseInt(get(UISettingsProperty.mainFrameState));
  }

  public boolean isMainFrameSaveState() {
    return Boolean.parseBoolean(get(UISettingsProperty.mainFrameSaveState));
  }

  public int getMainFrameScreen() {
    return Integer.parseInt(get(UISettingsProperty.mainFrameScreen));
  }

  public int getMainFrameFileDivider() {
    return Integer.parseInt(get(UISettingsProperty.mainFrameFileDivider));
  }

  public int getMainFrameMediaDivider() {
    return Integer.parseInt(get(UISettingsProperty.mainFrameMediaDivider));
  }

  private static String getApplicationProperty(String key) {
    return ResourceBundle.getBundle(UISettings.class.getName(), Locale.ROOT).getString(key);
  }

  public static String getApplicationVersionNumber() {
    String version = VERSION;
    if (version.contains("_")) {
      version = version.substring(0, version.indexOf("_"));
    }
    return version;
  }
}
