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
package fr.free.movierenamer.ui;

import com.alee.laf.WebLookAndFeel;
import fr.free.movierenamer.ui.settings.Settings;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.LocaleUtils;
import java.util.ResourceBundle;
import javax.swing.SwingUtilities;

/**
 * Class Main
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class Main {

  private static MovieRenamer mvr;

  public static void main(String args[]) {

    // Fixe JNA crash under 64 bit unix system
    if (System.getProperty("jna.nosys") == null) {
      System.setProperty("jna.nosys", "true");
    }

    Settings setting = Settings.getInstance();
    // Set UI locale file
    LocaleUtils.localBundleExt = ResourceBundle.getBundle("fr/free/movierenamer/ui/i18n/Bundle");

    // Install look and feel
    WebLookAndFeel.install();

    // Clear cache
    if (setting.clearCache) {
      Cache.clearAllCache();
    }

    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        mvr = new MovieRenamer();
        mvr.setVisible(true);
      }
    });
  }
}
