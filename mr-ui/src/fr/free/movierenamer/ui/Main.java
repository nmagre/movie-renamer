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
package fr.free.movierenamer.ui;

import com.alee.laf.WebLookAndFeel;
import com.alee.managers.language.LanguageManager;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.utils.LocaleUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.SwingUtilities;

/**
 * Class Main
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class Main {
// vm option : -Xmx256m -verbose:gc

  private static MovieRenamer mr;
  private static UISettings setting = UISettings.getInstance();

  public static void main(String args[]) {

    // Fixe JNA crash under 64 bit unix system
    if (System.getProperty("jna.nosys") == null) {
      System.setProperty("jna.nosys", "true");
    }

    List<File> files = new ArrayList<File>();
    for (String arg : args) {
      files.add(new File(arg));
    }

    // Set locale
    Locale.setDefault(setting.coreInstance.getAppLanguage().getLocale());

    // Set UI locale file
    LocaleUtils.localBundleExt = ResourceBundle.getBundle("fr/free/movierenamer/ui/i18n/Bundle");

    // Install look and feel
    WebLookAndFeel.install();

    // Set look and feel locale
    String lcode = "en";
    List<String> languages = LanguageManager.getSupportedLanguages();
    for (String language : languages) {
      if (language.equals(setting.coreInstance.getAppLanguage().getLocale().getLanguage())) {
        lcode = language;
        break;
      }
    }
    LanguageManager.setLanguage(lcode);

//    new Thread(new Runnable() {
//      @Override
//      public void run() {
//        while (true) {
//          Runtime runtime = Runtime.getRuntime();
//
//          NumberFormat format = NumberFormat.getInstance();
//
//          StringBuilder sb = new StringBuilder();
//          long maxMemory = runtime.maxMemory();
//          long allocatedMemory = runtime.totalMemory();
//          long freeMemory = runtime.freeMemory();
//
//          sb.append("free memory: " + format.format(freeMemory / 1024) + "\n");
//          sb.append("allocated memory: " + format.format(allocatedMemory / 1024) + "\n");
//          sb.append("max memory: " + format.format(maxMemory / 1024) + "\n");
//          sb.append("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024) + "\n");
//          System.out.println(sb.toString());
//          try {
//            Thread.sleep(20000);
//          } catch (InterruptedException ex) {
//            UISettings.LOGGER.log(Level.SEVERE, null, ex);
//          }
//        }
//      }
//    }).start();

    mr = new MovieRenamer(files);

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        mr.setVisible(true);
      }
    });
  }
}
