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

import fr.free.movierenamer.parser.xml.MrSettings;
import fr.free.movierenamer.parser.xml.XMLParser;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Class Main
 *
 * @author Nicolas Magré
 */
public class Main {

  private static MovieRenamer mvr;

  public static void main(String args[]) throws UnsupportedEncodingException, URISyntaxException, ParseException {
    final Settings setting = loadSetting();


    if (setting.laf.equals("")) {
      setting.laf = Settings.lookAndFeels[0].getName();
    }
    
    try {
      boolean lafFound = false;
      for (int i = 0; i < Settings.lookAndFeels.length; i++) {
        if (Settings.lookAndFeels[i].getName().equals(setting.laf)) {
          UIManager.setLookAndFeel(Settings.lookAndFeels[i].getClassName());
          lafFound = true;
          break;
        }
      }
      
      if(!lafFound){
        setting.laf = Settings.lookAndFeels[0].getName();
        UIManager.setLookAndFeel(Settings.lookAndFeels[0].getClassName());
      }
      
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    } catch (UnsupportedLookAndFeelException ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }

    java.awt.EventQueue.invokeLater(new Runnable() {

      @Override
      public void run() {
        mvr = new MovieRenamer(setting);
        mvr.setVisible(true);
      }
    });
  }

  /**
   * Load Movie Renamer settings
   *
   * @return Movie Renamer settings
   */
  private static Settings loadSetting() {
    boolean saved;
    Settings setting = new Settings();
    File file = new File(setting.configFile);


    if (!file.exists()) {
      saved = setting.saveSetting();
      if (!saved) {
        JOptionPane.showMessageDialog(null, "Unable to save setting", "Error", JOptionPane.ERROR_MESSAGE);
        return setting;
      }
      return loadSetting();
    }

    saved = false;
    try {
      // Parse Movie Renamer Settings
      XMLParser<Settings> xmlp = new XMLParser<Settings>(setting.configFile);
      xmlp.setParser(new MrSettings());
      setting = xmlp.parseXml();

      // Define locale on first run
      if (setting.locale.equals("")) {
        if (!Locale.getDefault().getLanguage().equals("fr")) {
          setting.locale = "en";
        } else {
          setting.locale = "fr";
        }
        setting.xmlVersion = setting.getVersion();// Ensures that the settings file is written once only
        setting.imdbFr = setting.locale.equals("fr");
      } else {
        saved = true;
      }

      // Set locale
      Locale.setDefault((setting.locale.equals("fr") ? new Locale("fr", "FR") : Locale.ENGLISH));
      if (setting.getVersion().equals(setting.xmlVersion) && !setting.xmlError) {
        saved = true;
      }

    } catch (IOException ex) {
      setting.getLogger().log(Level.SEVERE, Utils.getStackTrace("IOException : " + ex.getMessage(), ex.getStackTrace()));
    } catch (InterruptedException ex) {
      setting.getLogger().log(Level.SEVERE, Utils.getStackTrace("InterruptedException : " + ex.getMessage(), ex.getStackTrace()));
    } finally {
      if (!saved) {
        saved = setting.saveSetting();
      }
    }

    if (!saved) {
      JOptionPane.showMessageDialog(null, "Unable to save setting", "Error", JOptionPane.ERROR_MESSAGE);
    }
    return setting;
  }
}
