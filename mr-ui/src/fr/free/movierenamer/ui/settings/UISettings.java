/*
 * Movie Renamer
 * Copyright (C) 2012-2013 Nicolas Magré
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
import fr.free.movierenamer.settings.Settings.SettingsSubType;
import fr.free.movierenamer.settings.Settings.SettingsType;
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
    languagePrefix = "mrui";
    configFile = appNameNospace + ".conf";
    renamedFile = "renamed.xml";
    languageFile = "language.xml";
    logFile = appNameNospace + ".log";
    LOGGER = Logger.getLogger("UI");
    appSettingsNodeName = appNameNospace;
    settingNodeName = "settings";
  }
  public static final String APPNAME;
  public static final String VERSION;
  public static final String CORE_VERSION;
  public static final File appFolder;
  private static final String userFolder;
  public static final String languagePrefix;
  // files
  public static final String configFile;
  public static final String renamedFile;
  private static final String logFile;
  public static final String languageFile;
  // Logger
  public static final Logger LOGGER;
  // Settings instance
  private static final UISettings instance = new UISettings();
  public final Settings coreInstance = Settings.getInstance();
  // Settings xml conf instance
  private final Document settingsDocument;
  private final Node settingsNode;
  private static final String appSettingsNodeName;
  private static final String settingNodeName;

  public static enum ImageSize {

    ORIGINAL,
    MEDIUM,
    SMALL;
  }

  public enum UISettingsProperty implements Settings.IProperty {

    selectFirstMedia(Boolean.FALSE, SettingsType.GENERAL, SettingsSubType.GENERAL),
    selectFirstResult(Boolean.TRUE, SettingsType.GENERAL, SettingsSubType.GENERAL),
    scanSubfolder(Boolean.TRUE, SettingsType.GENERAL, SettingsSubType.GENERAL),
    showMediaPanel(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showActorImage(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showThumb(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showFanart(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showSubtitle(Boolean.FALSE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showCdart(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showClearart(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showLogo(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showBanner(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showId(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showYear(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showOrigTitle(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    generateThumb(Boolean.TRUE, SettingsType.RENAME, SettingsSubType.GENERAL),
    generateCdart(Boolean.TRUE, SettingsType.RENAME, SettingsSubType.GENERAL),
    generateClearart(Boolean.TRUE, SettingsType.RENAME, SettingsSubType.GENERAL),
    generateLogo(Boolean.TRUE, SettingsType.RENAME, SettingsSubType.GENERAL),
    generateFanart(Boolean.TRUE, SettingsType.RENAME, SettingsSubType.GENERAL),
    generateBanner(Boolean.TRUE, SettingsType.RENAME, SettingsSubType.GENERAL),
    generateSubtitles(Boolean.FALSE, SettingsType.RENAME, SettingsSubType.GENERAL),
    moveFileOneByOne(Boolean.TRUE, SettingsType.RENAME, SettingsSubType.GENERAL),
    imageThumbName("<fileName>.tbn", SettingsType.IMAGE, SettingsSubType.THUMB),
    imageThumbResize(Boolean.FALSE, SettingsType.IMAGE, SettingsSubType.THUMB, true),
    imageThumbSize(ImageSize.ORIGINAL, SettingsType.IMAGE, SettingsSubType.THUMB),
    imageThumbWidth(720, SettingsType.IMAGE, SettingsSubType.THUMB),
    imageFanartName("<fileName>-fanart.jpg", SettingsType.IMAGE, SettingsSubType.FANART),
    imageFanartResize(Boolean.FALSE, SettingsType.IMAGE, SettingsSubType.FANART, true),
    imageFanartSize(ImageSize.ORIGINAL, SettingsType.IMAGE, SettingsSubType.FANART),
    imageFanartWidth(1080, SettingsType.IMAGE, SettingsSubType.FANART),
    useExtensionFilter(Boolean.TRUE, SettingsType.EXTENSION, SettingsSubType.GENERAL),
    groupMediaList(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showIconMediaList(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showFormatField(Boolean.FALSE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    showStartupAnim(Boolean.TRUE, SettingsType.INTERFACE, SettingsSubType.GENERAL),
    //
    fileChooserPath(userFolder);
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
    LOGGER.log(Level.INFO, String.format("Clear UISettings"));
    NodeList list = this.settingsNode.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      this.settingsNode.removeChild(list.item(i));
    }
    saveSetting();
    coreInstance.clear();
  }

  /**
   * Save setting
   *
   * @return True if setting was saved, False otherwise
   */
  private synchronized boolean saveSetting() throws IOException {
    boolean saveSuccess;
    try {
      LOGGER.log(Level.INFO, String.format("Save configuration to %s", configFile));
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
    return Boolean.parseBoolean(get(UISettingsProperty.selectFirstMedia));
  }

  public boolean isSelectFirstResult() {
    return Boolean.parseBoolean(get(UISettingsProperty.selectFirstResult));
  }

  public boolean isScanSubfolder() {
    return Boolean.parseBoolean(get(UISettingsProperty.scanSubfolder));
  }

  public boolean isShowActorImage() {
    return Boolean.parseBoolean(get(UISettingsProperty.showActorImage));
  }

  public boolean isShowThumb() {
    return Boolean.parseBoolean(get(UISettingsProperty.showThumb));
  }

  public boolean isShowMediaPanel() {
    return Boolean.parseBoolean(get(UISettingsProperty.showMediaPanel));
  }

  public boolean isShowFanart() {
    return Boolean.parseBoolean(get(UISettingsProperty.showFanart));
  }

  public boolean isShowSubtitle() {
    return Boolean.parseBoolean(get(UISettingsProperty.showSubtitle));
  }

  public boolean isShowCdart() {
    return Boolean.parseBoolean(get(UISettingsProperty.showCdart));
  }

  public boolean isShowClearart() {
    return Boolean.parseBoolean(get(UISettingsProperty.showClearart));
  }

  public boolean isShowLogo() {
    return Boolean.parseBoolean(get(UISettingsProperty.showLogo));
  }

  public boolean isShowBanner() {
    return Boolean.parseBoolean(get(UISettingsProperty.showBanner));
  }

  public boolean isShowId() {
    return Boolean.parseBoolean(get(UISettingsProperty.showId));
  }

  public boolean isShowYear() {
    return Boolean.parseBoolean(get(UISettingsProperty.showYear));
  }

  public boolean isShowOrigTitle() {
    return Boolean.parseBoolean(get(UISettingsProperty.showOrigTitle));
  }

  public boolean isGenerateThumb() {
    return Boolean.parseBoolean(get(UISettingsProperty.generateThumb));
  }

  public boolean isGenerateFanart() {
    return Boolean.parseBoolean(get(UISettingsProperty.generateFanart));
  }

  public boolean isGenerateSubtitles() {
    return Boolean.parseBoolean(get(UISettingsProperty.generateSubtitles));
  }

  public boolean isMoveFileOneByOne() {
    return Boolean.parseBoolean(get(UISettingsProperty.moveFileOneByOne));
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

  public int getImageThumbWidth() {
    return Integer.parseInt(get(UISettingsProperty.imageThumbWidth));
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

  public boolean isUseExtensionFilter() {
    return Boolean.parseBoolean(get(UISettingsProperty.useExtensionFilter));
  }

  public String getFileChooserPath() {
    return get(UISettingsProperty.fileChooserPath);
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

  private static String getApplicationProperty(String key) {
    return ResourceBundle.getBundle(UISettings.class.getName(), Locale.ROOT).getString(key);
  }
}
