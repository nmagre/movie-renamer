/******************************************************************************
 *                                                                             *
 *    Movie Renamer                                                            *
 *    Copyright (C) 2012 Magré Nicolas                                         *
 *                                                                             *
 *    Movie Renamer is free software: you can redistribute it and/or modify    *
 *    it under the terms of the GNU General Public License as published by     *
 *    the Free Software Foundation, either version 3 of the License, or        *
 *    (at your option) any later version.                                      *
 *                                                                             *
 *    This program is distributed in the hope that it will be useful,          *
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            *
 *    GNU General Public License for more details.                             *
 *                                                                             *
 *    You should have received a copy of the GNU General Public License        *
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                             *
 ******************************************************************************/
package fr.free.movierenamer;

import fr.free.movierenamer.utils.Utils;
import fr.free.movierenamer.utils.Settings;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import fr.free.movierenamer.parser.XMLParser;
import fr.free.movierenamer.ui.MovieRenamer;
import java.util.Locale;

/**
 *
 * @author duffy
 */
public class Main {

  private static MovieRenamer mvr;

  public static void main(String args[]) throws UnsupportedEncodingException, URISyntaxException, ParseException {
    
    final Settings setting = loadSetting();
    
    java.awt.EventQueue.invokeLater(new Runnable() {

      @Override
      public void run() {
        mvr = new MovieRenamer(setting);
        mvr.setVisible(true);
      }
    });
  }

  private static Settings loadSetting() {
    Settings setting = new Settings();
    File file = new File(setting.configFile);
    boolean saved = true;
    if (file.exists())
      try {
        XMLParser<Settings> mmp = new XMLParser<Settings>(setting.configFile, Settings.class);
        setting = mmp.parseXml();
        if (setting.locale.equals("")) {
          if (!Locale.getDefault().getLanguage().equals("fr")) setting.locale = "en";
          else setting.locale = "fr";
          saved = setting.saveSetting();
          setting.xmlVersion = setting.getVersion();
          setting.imdbFr = setting.locale.equals("fr");
        }
        Locale.setDefault((setting.locale.equals("fr") ? new Locale("fr", "FR"):Locale.ENGLISH));
        if (!setting.getVersion().equals(setting.xmlVersion) || setting.xmlError)
          saved = setting.saveSetting();
      } catch (IOException ex) {
        setting.getLogger().log(Level.SEVERE, Utils.getStackTrace("IOException : " + ex.getMessage(), ex.getStackTrace()));
        saved = setting.saveSetting();
      } catch (InterruptedException ex) {
        setting.getLogger().log(Level.SEVERE, Utils.getStackTrace("InterruptedException : " + ex.getMessage(), ex.getStackTrace()));
        saved = setting.saveSetting();
      }
    else{
      saved = setting.saveSetting();
      if (!saved) JOptionPane.showMessageDialog(null, "Unable to save setting", "Error", JOptionPane.ERROR_MESSAGE);
      return loadSetting();
    }
    if (!saved) JOptionPane.showMessageDialog(null, "Unable to save setting", "Error", JOptionPane.ERROR_MESSAGE);
    return setting;
  }
}
