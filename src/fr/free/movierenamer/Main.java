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

import com.alee.laf.WebLookAndFeel;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.SettingsSaveFailedException;
import fr.free.movierenamer.utils.Utils;
import java.io.File;
import javax.swing.JOptionPane;

/**
 * Class Main
 *
 * @author Nicolas Magré
 */
public class Main {

  private static MovieRenamer mvr;
  
  public static void main(String args[]) {
    
    Settings setting;
    try {
      setting = Settings.newInstance();
    } catch (SettingsSaveFailedException ex) {
       JOptionPane.showMessageDialog(null, ex.getMessage(), Utils.i18n("error"), JOptionPane.ERROR_MESSAGE);
      setting = ex.getDefaultSettings();
    }

    // Install look and feel
    WebLookAndFeel.install();
    
   /* if (setting.laf.equals("")) {
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

      if (!lafFound) {
        setting.laf = Settings.lookAndFeels[0].getName();
        UIManager.setLookAndFeel(Settings.lookAndFeels[0].getClassName());
      }

    } catch (ClassNotFoundException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (UnsupportedLookAndFeelException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }
*/
    //Clear XML cache
    if (setting.clearXMLCache) {
      Utils.deleteFileInDirectory(new File(Settings.xmlCacheDir));
    }
    
    final Settings config = setting;
    java.awt.EventQueue.invokeLater(new Runnable() {

      @Override
      public void run() {
        mvr = new MovieRenamer(config);
        mvr.setVisible(true);
      }
    });
  }
}
