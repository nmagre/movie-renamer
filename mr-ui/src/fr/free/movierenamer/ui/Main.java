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
import fr.free.movierenamer.ui.swing.dialog.LoadingDialog;
import fr.free.movierenamer.ui.utils.UIUtils;
import java.io.File;
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
    private static LoadingDialog loadingDial;

    /**
     * main method
     *
     * @param args List of file to load (file, folder or both)
     */
    public static void main(String args[]) {

        // Set logger level
        //UISettings.setAppLogLevel(setting.getLogLevel().getLevel());
        
        // Fixe JNA crash under 64 bit unix system
        if (System.getProperty("jna.nosys") == null) {
            System.setProperty("jna.nosys", "true");
        }

        System.setProperty("net.sf.ehcache.enableShutdownHook", "true");

        final List<File> files = new ArrayList<>();
        for (String arg : args) {
            files.add(new File(arg));
        }

        if (files.isEmpty() && setting.isLoadFileAtStartup()) {
            files.add(new File(setting.getLoadFilePath()));
        }
        
        // Set font
        UIUtils.setUIFont();

        // Set locale
        Locale locale = setting.coreInstance.getAppLanguage().getLocale();
        Locale.setDefault(locale);
        LanguageManager.setDefaultLanguage(LanguageManager.ENGLISH);
        LanguageUpdaterSettings.useTabComponentNames = true;

        TooltipManager.setDefaultDelay(1500);

        // Install look and feel
        WebLookAndFeel.install();

        // Set look and feel locale
        String lcode = "en";
        List<String> languages = LanguageManager.getSupportedLanguages();
        for (String language : languages) {
            if (language.equals(locale.getLanguage())) {
                lcode = language;
                break;
            }
        }
        LanguageManager.setLanguage(lcode);

        // Set UI locale file
        File languageFile = new File(UISettings.APPFOLDER, UISettings.languageFile);
        if (languageFile.exists()) {
            LanguageManager.addDictionary(languageFile);
        } else {
            LanguageManager.addDictionary(Main.class, "i18n/" + UISettings.languageFile);
        }

        // Loading dialog
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                loadingDial = new LoadingDialog();
            }
        });

        final MovieRenamer mr = new MovieRenamer();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mr.setVisible(true);
                loadingDial.hideDial();
                loadingDial = null;

                if (!files.isEmpty()) {
                    mr.loadFiles(files);
                }

            }
        });

    }

}
