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
package fr.free.movierenamer;

import fr.free.movierenamer.cli.CmdLineParser;
import fr.free.movierenamer.parser.xml.MrSettings;
import fr.free.movierenamer.parser.xml.XMLParser;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Class MainCli
 * @author Nicolas Magré
 */
public class MainCli {

  public static void main(String args[]) {//A faire
    
    CmdLineParser parser = new CmdLineParser();
    CmdLineParser.Option debug = parser.addBooleanOption('d', "debug");
    CmdLineParser.Option verbose = parser.addBooleanOption('v', "verbose");    
    CmdLineParser.Option mode = parser.addStringOption('m', "mode");
            
    try {
      parser.parse(args);
    } catch (CmdLineParser.CmdParserException e) {
      System.err.println(e.getMessage());
     // printUsage();
      System.exit(-1);
    }
    
    String modeValue = (String)parser.getOptionValue(mode);
    
    final Settings setting = loadSetting();

    if (setting.laf.equals("")) {
      setting.laf = Settings.lookAndFeels[0].getName();
    }

    //Clear XML cache
    if (setting.clearXMLCache) {
      Utils.deleteFileInDirectory(new File(Settings.xmlCacheDir));
    }
  }

  /**
   * Load Movie Renamer settings
   *
   * @return Movie Renamer settings
   */
  private static Settings loadSetting() {
    boolean saved;
    Settings setting = Settings.getInstance();
    File file = new File(Settings.configFile);


    if (!file.exists()) {
      saved = setting.saveSetting();
      if (!saved) {
        System.err.println("Error : Unable to save setting");
        return setting;
      }
      return loadSetting();
    }

    saved = false;
    try {
      // Parse Movie Renamer Settings
      XMLParser<Settings> xmlp = new XMLParser<Settings>(Settings.configFile);
      xmlp.setParser(new MrSettings());
      setting = xmlp.parseXml();

      // Define locale on first run
      if (setting.locale.equals("")) {
        if (!Locale.getDefault().getLanguage().equals("fr")) {
          setting.locale = "en";
        } else {
          setting.locale = "fr";
        }
        Settings.xmlVersion = setting.getVersion();// Ensures that the settings file is written once only
        setting.movieScrapperFR = setting.locale.equals("fr");
        setting.tvshowScrapperFR = setting.locale.equals("fr");
      } else {
        saved = true;
      }

      // Set locale
      Locale.setDefault((setting.locale.equals("fr") ? new Locale("fr", "FR") : Locale.ENGLISH));
      if (setting.getVersion().equals(Settings.xmlVersion) && !Settings.xmlError) {
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
        saved = setting.saveSetting();
      }
    }

    if (!saved) {
      System.err.println("Error : Unable to save setting");
    }
    return setting;
  }
}