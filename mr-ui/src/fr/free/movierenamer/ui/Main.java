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
import com.alee.managers.language.LanguageManager;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.utils.LocaleUtils;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
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

    UISettings setting = UISettings.getInstance();

    // Set UI locale
    Locale.setDefault(setting.coreInstance.getAppLanguage());

    UISettings.LOGGER.setLevel(Level.INFO);
    Settings.LOGGER.setLevel(Level.INFO);

    // Set UI locale file
    LocaleUtils.localBundleExt = ResourceBundle.getBundle("fr/free/movierenamer/ui/i18n/Bundle");

    // Install look and feel
    WebLookAndFeel.install();

    // Set look and feel locale
    List<String> languages = LanguageManager.getSupportedLanguages();
    String lcode = "en";
    for (String language : languages) {
      if (language.equals(setting.coreInstance.getAppLanguage().getLanguage())) {
        lcode = language;
        break;
      }
    }

    LanguageManager.setLanguage(lcode);

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        mvr = new MovieRenamer();
        mvr.setVisible(true);
      }
    });
  }
}
