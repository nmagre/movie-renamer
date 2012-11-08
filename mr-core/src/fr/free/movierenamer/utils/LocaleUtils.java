/*
 * movie-renamer-core
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
package fr.free.movierenamer.utils;

import fr.free.movierenamer.settings.Settings;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * Class LocaleUtils
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public final class LocaleUtils {

  private static final ResourceBundle localBundle = ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle");

  public static Locale[] getAvailableLanguages() {
    return new Locale[] { Locale.ENGLISH, Locale.FRENCH };
  }

  public static Locale getLocale(String code, Locale currentLocale) {
    Locale found = null;
    if (code != null) {
      Locale[] locales = Locale.getAvailableLocales();
      for (Locale l : locales) {
        if (l.getDisplayName().equalsIgnoreCase(code)) {
          found = l;
        } else {
          // country
          if (l.getCountry().equalsIgnoreCase(code)) {
            found = l;
          } else {
            if (((currentLocale == null) ? l.getDisplayCountry() : l.getDisplayCountry(currentLocale)).equalsIgnoreCase(code)) {
              found = l;
            } else {
              // language
              if (l.getLanguage().equalsIgnoreCase(code)) {
                found = l;
              } else {
                if (((currentLocale == null) ? l.getDisplayLanguage() : l.getDisplayLanguage(currentLocale)).equalsIgnoreCase(code)) {
                  found = l;
                }
              }
            }
          }
        }
        if (found != null) {
          break;
        }
      }
    }
    return found;
  }

  /**
   * Get string in i18n files
   * 
   * @param bundleKey CacheKey to find
   * @return String depends on locale
   */
  public static String i18n(String bundleKey) {
    return i18n(bundleKey, bundleKey);
  }

  /**
   * Get string in i18n files
   * 
   * @param bundleKey CacheKey to find
   * @param defaultValue Default value
   * @return String depends on locale or default value if key dos not exist
   */
  public static String i18n(String bundleKey, String defaultValue) {
    if (localBundle.containsKey(bundleKey)) {
      return localBundle.getString(bundleKey);
    } else {
      Settings.LOGGER.log(Level.CONFIG, "No internationlization found for {0}, use default value", bundleKey);
      return defaultValue;
    }
  }

  private LocaleUtils() {
    throw new UnsupportedOperationException();
  }
}
