/*
 * movie-renamer-core
 * Copyright (C) 2014 Nicolas Magré
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

import com.sun.jna.NativeLibrary;
import com.sun.jna.Platform;
import fr.free.movierenamer.mediainfo.MediaInfoLibrary;
import fr.free.movierenamer.utils.FileUtils;
import fr.free.movierenamer.utils.StringUtils;
import fr.free.movierenamer.utils.URIRequest;
import fr.free.movierenamer.utils.XPathUtils;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;
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
 * Class XMLSettings , Movie Renamer settings
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class XMLSettings {

  public static final String APPNAME;
  public static final String APPNAME_NOSPACE;
  public static final boolean LINUX;
  public static final boolean WINDOWS;
  public static final boolean IS64BIt;
  public static final File APPFOLDER;
  public static final Boolean MEDIAINFO;
  public static final String AUTHOR;
  public static final String CONTRIB;
  public static final String LICENSE;
  public static final String COPYRIGHT;
  public static final String PROVIDER;
  public static final String HOST;
  private static final Logger LOGGER;
  private static final String[] SYSPOP;
  private static final String settingNodeName;
  private final String configFileName;
  private boolean autosave = true;
  // Settings xml conf instance
  private Document settingsDocument;
  private Node settingsNode;

  static {
    LOGGER = Logger.getLogger(XMLSettings.class.getSimpleName());
    SYSPOP = new String[]{"sun.arch.data.model", "os.arch"};

    String appName = getApplicationProperty("application.name");
    String appNameNospace = appName.replace(' ', '_');

    APPNAME = appName;
    APPNAME_NOSPACE = appNameNospace;
    LINUX = Platform.isLinux();
    WINDOWS = Platform.isWindows();
    IS64BIt = is64BitJvm();
    APPFOLDER = getApplicationFolder();
    MEDIAINFO = libMediaInfo();
    AUTHOR = getApplicationProperty("application.author");
    CONTRIB = getApplicationProperty("application.contributors");
    LICENSE = getApplicationProperty("application.license");
    COPYRIGHT = getApplicationProperty("application.copyright");
    PROVIDER = getApplicationProperty("application.provider");
    HOST = getApplicationProperty("application.host");

    settingNodeName = "settings";
  }

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
    FOLDER,
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

  protected XMLSettings(Logger logger, String logFileName, String configFileName, String version) {
    this.configFileName = configFileName;
    setLogger(logger, logFileName);
    loadSettings(version);
  }

  private void setLogger(Logger logger, String logFileName) {
    // Log init
    try {
      File logsRoot = new File(APPFOLDER, "logs");
      if (!logsRoot.isDirectory() && !logsRoot.mkdirs()) {
        throw new IOException("Failed to create logs dir: " + logsRoot);
      }
      FileHandler fh = new FileHandler(logsRoot.getAbsolutePath() + File.separator + logFileName);
      logger.addHandler(fh);
    } catch (SecurityException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
  }

  private void loadSettings(String version) {
    // settingsDocument init
    Document settingsDocument;
    Node settingsNode;
    String appSettingsNodeName = getAppSettingsNodeName();
    boolean savesettings = false;

    try {
      File confRoot = new File(APPFOLDER, "conf");
      File file = new File(confRoot, configFileName);
      settingsDocument = URIRequest.getXmlDocument(file.toURI());
      Node appSettingsNode = XPathUtils.selectNode(appSettingsNodeName, settingsDocument);
      String xmlVersion = XPathUtils.getAttribute("Version", appSettingsNode);

      if (!version.equals(xmlVersion)) {// TODO convert
        LOGGER.info(String.format("Config file version mismatch app version \"%s\" , %s file version \"%s\"", version, configFileName, xmlVersion));
        Attr xversion = settingsDocument.createAttribute("Version");
        xversion.setValue(version);
        settingsDocument.getDocumentElement().setAttributeNode(xversion);
        savesettings = true;
      }
      settingsNode = XPathUtils.selectNode(settingNodeName, appSettingsNode);
    } catch (Exception ex) {
      try {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        docBuilder = docFactory.newDocumentBuilder();

        // root elements
        settingsDocument = docBuilder.newDocument();
        Element rootElement = settingsDocument.createElement(appSettingsNodeName);
        settingsDocument.appendChild(rootElement);

        Attr xversion = settingsDocument.createAttribute("Version");
        xversion.setValue(version);
        rootElement.setAttributeNode(xversion);

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

    if (savesettings) {
      saveSetting();
    }
  }

  private static String getApplicationProperty(String key) {
    return ResourceBundle.getBundle(XMLSettings.class.getName(), Locale.ROOT).getString(key);
  }

  protected abstract String getAppSettingsNodeName();

  public boolean isAutosave() {
    return autosave;
  }

  public void setAutosave(boolean autosave) {
    this.autosave = autosave;
  }

  protected final synchronized String get(IProperty key) {
    String value = null;
    if (key == null) {
      return value;
    }

    Node found = XPathUtils.selectNode(key.name(), settingsNode);
    if (found != null) {
      value = XPathUtils.getTextContent(found);
    }

    if (value == null) {
      if (key.getDefaultValue() instanceof char[]) {
        value = "";
      } else {
        value = key.getDefaultValue().toString();
      }
    }

    return value;
  }

  public synchronized void set(IProperty key, Object value) {
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

      // Pass
      if (key.getDefaultValue() instanceof char[]) {
        found.setTextContent(StringUtils.encrypt(value.toString().getBytes()));
      } else {
        found.setTextContent(value.toString());
      }
      if (autosave) {
        saveSetting();
      }
    }
  }

  public synchronized void clear() {
    LOGGER.log(Level.INFO, String.format("Clear Settings"));
    NodeList list = settingsNode.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      settingsNode.removeChild(list.item(i));
    }

    if (autosave) {
      saveSetting();
    }
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
      File confRoot = new File(APPFOLDER, "conf");
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

  private static File getApplicationFolder() {
    String applicationDirPath = System.getProperty("application.dir");
    String userHome = System.getProperty("user.home");
    String userDir = System.getProperty("user.dir");
    File applicationFolder;

    if (applicationDirPath != null && applicationDirPath.length() > 0) {
      // use given path
      applicationFolder = new File(applicationDirPath);
    } else if (userHome != null) {
      // create folder in user home
      applicationFolder = new File(userHome, WINDOWS ? APPNAME_NOSPACE : "." + APPNAME_NOSPACE);
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

  private static boolean is64BitJvm() {

    String res;
    for (String prop : SYSPOP) {
      try {
        res = System.getProperty(prop);
        if (res != null && !res.isEmpty()) {
          return res.contains("64");
        }
      } catch (Exception ex) {
      }
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
        LOGGER.log(Level.WARNING, "Failed to preload libzen");
      }
    }

    if ((LINUX && libzen) || !LINUX) {
      try {
        MediaInfoLibrary.INSTANCE.New();
        mediaInfo = Boolean.TRUE;
      } catch (LinkageError e) {
        mediaInfo = Boolean.FALSE;
        LOGGER.log(Level.WARNING, e.getMessage());
      }
    }

    if (mediaInfo == null) {
      mediaInfo = Boolean.FALSE;
    }

    return mediaInfo.equals(Boolean.TRUE);
  }
}
