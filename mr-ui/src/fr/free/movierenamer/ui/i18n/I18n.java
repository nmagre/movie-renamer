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
package fr.free.movierenamer.ui.i18n;

import com.alee.managers.language.LanguageManager;
import fr.free.movierenamer.ui.settings.UISettings;
import java.util.logging.Level;

/**
 * Class Dictionary
 *
 * @author Nicolas Magré
 */
public class I18n {

  private final String mruiDictionary = "mrui";
  private final String classDictionary;

  public I18n(String classDictionary) {
    this.classDictionary = classDictionary;
  }

  public String getLanguageKey(String key) {
    return getLanguage(key, null, null, true, false);
  }

  public String getLanguageKey(String key, boolean useClassDic) {
    return getLanguage(key, null, null, useClassDic, false);
  }

  public String getLanguageKey(String key, String dict) {
    return getLanguage(key, dict, null, true, false);
  }

  public String getLanguageKey(String key, String dict, String subDict) {
    return getLanguage(key, dict, subDict, true, false);
  }

  public String getLanguage(String key, boolean useClassDic, String... replace) {
    return getLanguage(key, null, null, useClassDic, true, replace);
  }

//  public String getLanguage(String key) {
//    return getLanguage(key, null, null, true);
//  }
//
//  public String getLanguage(String key, String dict) {
//    return getLanguage(key, dict, null, true);
//  }
//
//  public String getLanguage(String key, String dict, String subDict) {
//    return getLanguage(key, dict, subDict, true);
//  }
  private String getLanguage(String key, String dict, String subDict, boolean useClassDic, boolean value, String... replace) {
    String lkey = mruiDictionary + ".";
    if (useClassDic) {
      lkey += classDictionary + ".";
    }

    if (dict != null && !dict.equals("")) {
      lkey += dict + ".";
    }

    if (subDict != null && !subDict.equals("")) {
      lkey += subDict + ".";
    }

    lkey += key;
    String str = LanguageManager.get(lkey);
    if (str.equals(lkey)) {
      UISettings.LOGGER.log(Level.WARNING, String.format("Language Key \"%s\" does not exist", lkey));
    }

    return value ? String.format(str, (Object[]) replace) : lkey;
  }
}
