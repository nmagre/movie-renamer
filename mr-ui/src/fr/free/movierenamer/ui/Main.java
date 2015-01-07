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
package fr.free.movierenamer.ui;

import com.alee.laf.WebLookAndFeel;
import com.alee.managers.language.LanguageManager;
import com.alee.managers.language.updaters.LanguageUpdaterSettings;
import com.alee.managers.tooltip.TooltipManager;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.utils.UIUtils;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.SwingUtilities;

/**
 * Class Main
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class Main {
// vm option : -Xmx256m -verbose:gc

  private static final UISettings setting = UISettings.getInstance();

  public static void main(String args[]) throws InterruptedException, InvocationTargetException {

    // Fixe JNA crash under 64 bit unix system
    if (System.getProperty("jna.nosys") == null) {
      System.setProperty("jna.nosys", "true");
    }
    
    System.setProperty("net.sf.ehcache.enableShutdownHook","true");
    
    final List<File> files = new ArrayList<>();
    for (String arg : args) {
      files.add(new File(arg));
    }

    // Add language support to tabbedPane
    LanguageUpdaterSettings.useTabComponentNames = true;

    /**
     * Should be in EDT, but loading dialog will not works
     */
    // Install look and feel
    WebLookAndFeel.install();

    // Set font
    UIUtils.setUIFont();

    // Set locale
    Locale.setDefault(setting.coreInstance.getAppLanguage().getLocale());

    // Set UI locale file
    File languageFile = new File(UISettings.APPFOLDER, UISettings.languageFile);
    if (languageFile.exists()) {
      LanguageManager.addDictionary(languageFile);
    } else {
      LanguageManager.addDictionary(Main.class, "i18n/" + UISettings.languageFile);
    }

    // Set look and feel locale
    String lcode = "en";
    List<String> languages = LanguageManager.getSupportedLanguages();
    for (String language : languages) {
      if (language.equals(setting.coreInstance.getAppLanguage().getLocale().getLanguage())) {
        lcode = language;
        break;
      }
    }
    LanguageManager.setLanguage("fr".equals(lcode) ? "en" : "fr"); // FIXME remove

    TooltipManager.setDefaultDelay(1500);

    if (files.isEmpty() && setting.isLoadFileAtStartup()) {
      files.add(new File(setting.getLoadFilePath()));
    }

    final MovieRenamer mr = new MovieRenamer(lcode);
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        mr.setVisible(true);
        if (!files.isEmpty()) {
          mr.loadFiles(files);
        }
      }
    });
  }

}
