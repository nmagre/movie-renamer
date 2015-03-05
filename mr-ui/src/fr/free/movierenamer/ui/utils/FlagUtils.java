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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.free.movierenamer.ui.utils;

import fr.free.movierenamer.ui.bean.UILang;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.utils.LocaleUtils;
import java.util.Locale;
import java.util.logging.Level;
import javax.swing.Icon;

/**
 * Class Flag
 *
 * @author Nicolas Magré
 */
public abstract class FlagUtils {

  //Unknown flag
  public static final Icon Unknown = ImageUtils.getIconFromJar("country/unknown.png");

  // Only most common flag for video media
  private enum FlagsIcon {

    AR("es", ImageUtils.getIconFromJar("country/ar.png")),
    AU("en", ImageUtils.getIconFromJar("country/au.png")),
    AT("de", ImageUtils.getIconFromJar("country/at.png")),
    BE("fr", ImageUtils.getIconFromJar("country/be.png")),
    BR("pt", ImageUtils.getIconFromJar("country/br.png")),
    BG("bg", ImageUtils.getIconFromJar("country/bg.png"), true),
    CA("en", ImageUtils.getIconFromJar("country/ca.png")),
    CN("zh", ImageUtils.getIconFromJar("country/cn.png"), true),
    CO("es", ImageUtils.getIconFromJar("country/co.png")),
    CR("es", ImageUtils.getIconFromJar("country/cr.png")),
    CZ("cs", ImageUtils.getIconFromJar("country/cz.png"), true),
    DK("da", ImageUtils.getIconFromJar("country/dk.png"), true),
    FI("fi", ImageUtils.getIconFromJar("country/fi.png"), true),
    FR("fr", ImageUtils.getIconFromJar("country/fr.png"), true),
    DE("de", ImageUtils.getIconFromJar("country/de.png"), true),
    GR("el", ImageUtils.getIconFromJar("country/gr.png"), true),
    HK("zh", ImageUtils.getIconFromJar("country/hk.png")),
    HU("hu", ImageUtils.getIconFromJar("country/hu.png"), true),
    IS("is", ImageUtils.getIconFromJar("country/is.png"), true),
    IN("hi", ImageUtils.getIconFromJar("country/in.png"), true),
    IR("fa", ImageUtils.getIconFromJar("country/ir.png"), true),
    IE("ga", ImageUtils.getIconFromJar("country/ie.png"), true),
    IT("it", ImageUtils.getIconFromJar("country/it.png"), true),
    JP("ja", ImageUtils.getIconFromJar("country/jp.png"), true),
    MY("ms", ImageUtils.getIconFromJar("country/my.png"), true),
    MX("es", ImageUtils.getIconFromJar("country/mx.png")),
    NL("nl", ImageUtils.getIconFromJar("country/nl.png"), true),
    NZ("en", ImageUtils.getIconFromJar("country/nz.png")),
    PK("ur", ImageUtils.getIconFromJar("country/pk.png"), true),
    PL("pl", ImageUtils.getIconFromJar("country/pl.png"), true),
    PT("pt", ImageUtils.getIconFromJar("country/pt.png"), true),
    RO("ro", ImageUtils.getIconFromJar("country/ro.png"), true),
    RU("ru", ImageUtils.getIconFromJar("country/ru.png"), true),
    SG("zh", ImageUtils.getIconFromJar("country/sg.png")),
    ZA("en", ImageUtils.getIconFromJar("country/za.png")),
    ES("es", ImageUtils.getIconFromJar("country/es.png"), true),
    SE("sv", ImageUtils.getIconFromJar("country/se.png"), true),
    CH("fr", ImageUtils.getIconFromJar("country/ch.png")),
    TH("th", ImageUtils.getIconFromJar("country/th.png"), true),
    GB("en", ImageUtils.getIconFromJar("country/gb.png"), true),
    US("en", ImageUtils.getIconFromJar("country/us.png")),
    UA("uk", ImageUtils.getIconFromJar("country/ua.png"), true),
    TR("tr", ImageUtils.getIconFromJar("country/tr.png"), true),
    HR("hr", ImageUtils.getIconFromJar("country/hr.png"), true),
    IL("iw", ImageUtils.getIconFromJar("country/il.png"), true),
    KR("ko", ImageUtils.getIconFromJar("country/kr.png"), true),
    NO("no", ImageUtils.getIconFromJar("country/no.png"), true),
    SI("sl", ImageUtils.getIconFromJar("country/si.png"), true),
    MA("ar", ImageUtils.getIconFromJar("country/ma.png"), true);

    private final Icon flag;
    private final String language;
    private final boolean defaultFlag;

    private FlagsIcon(String language, Icon flag) {
      this(language, flag, false);
    }

    private FlagsIcon(String language, Icon flag, boolean defaultFlag) {
      this.language = language;
      this.flag = flag;
      this.defaultFlag = defaultFlag;
    }

    public boolean isDefaultFlag() {
      return defaultFlag;
    }

    public String getLanguage() {
      return language;
    }

    public Icon getFlagIcon() {
      return flag;
    }
  }

  public static UILang getFlagByLang(String code) {
    if (code == null || code.length() == 0 || code.equals("xx")) {// tmdb return xx if there is no language
      return new UILang(null, Unknown);
    }

    code = code.toLowerCase();

    Locale langLocal = LocaleUtils.findLanguage(code);
    Locale countryLocale = LocaleUtils.findCountry(code);

    try {

      for (FlagsIcon lFlag : FlagsIcon.values()) {

        if (lFlag.isDefaultFlag() && lFlag.getLanguage().equalsIgnoreCase(code)) {
          return new UILang(LocaleUtils.AvailableLanguages.valueOf(lFlag.getLanguage()), lFlag.getFlagIcon());
        }

        if (langLocal != null) {

          if (lFlag.isDefaultFlag() && lFlag.getLanguage().equalsIgnoreCase(langLocal.getLanguage())) {
            return new UILang(LocaleUtils.AvailableLanguages.valueOf(lFlag.getLanguage()), lFlag.getFlagIcon());
          }
        }
      }

    } catch (IllegalArgumentException ex) {
      // No flag image :(
    }

    UISettings.LOGGER.log(Level.WARNING, String.format("Flag not found : %s", code));

    return new UILang(null, Unknown);
  }

  public static String getCountryCodeByLang(String lang) {

    for (FlagsIcon lFlag : FlagsIcon.values()) {
      if (lFlag.isDefaultFlag() && lFlag.getLanguage().equals(lang)) {
        return lFlag.name();
      }
    }

    return null;
  }

  public static Icon getFlagByCountry(String code) {
    if (code == null || code.length() == 0 || code.equals("xx")) {// tmdb return xx if there is no language
      return Unknown;
    }

    code = code.toLowerCase().trim();

    Locale langLocal = LocaleUtils.findLanguage(code);
    Locale countryLocale = LocaleUtils.findCountry(code);

    try {

      for (FlagsIcon lFlag : FlagsIcon.values()) {

        if (lFlag.name().equalsIgnoreCase(code)) {
          return lFlag.getFlagIcon();
        }

        if (countryLocale != null) {
          if (countryLocale.getCountry().equalsIgnoreCase(lFlag.name())) {
            return lFlag.getFlagIcon();
          }
        }

      }
    } catch (IllegalArgumentException ex) {
      // No flag image :(
    }

    UISettings.LOGGER.log(Level.WARNING, String.format("Flag not found : %s", code));

    return Unknown;
  }

}
