/*
 * movie-renamer-core
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
package fr.free.movierenamer.utils;

import java.text.Collator;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.logging.Level;

import fr.free.movierenamer.settings.Settings;

/**
 * Class LocaleUtils
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public final class LocaleUtils {

  public interface Country {

    public Locale getLocale();
  }

  // Only most common country for video media
  private static enum Countries implements Country {

    ARGENTINA(new Locale("", "AR")),
    AUSTRALIA(new Locale("", "AU")),
    AUSTRIA(new Locale("", "AT")),
    BELGIUM(new Locale("", "BE")),
    BRAZIL(new Locale("", "BR")),
    BULGARIA(new Locale("", "BG")),
    CANADA(new Locale("", "CA")),
    CHINA(new Locale("", "CN")),
    COLOMBIA(new Locale("", "CO")),
    COSTA_RICA(new Locale("", "CR")),
    CZECH_REPUBLIC(new Locale("", "CZ")),
    DENMARK(new Locale("", "DK")),
    FINLAND(new Locale("", "FI")),
    FRANCE(new Locale("", "FR")),
    GERMANY(new Locale("", "DE")),
    GREECE(new Locale("", "GR")),
    HONG_KONG(new Locale("", "HK"), "Hong-Kong", "Hongkong", "Hong kong"),
    HUNGARY(new Locale("", "HU")),
    ICELAND(new Locale("", "IS")),
    INDIA(new Locale("", "IN")),
    IRAN(new Locale("", "IR")),
    IRELAND(new Locale("", "IE")),
    ITALY(new Locale("", "IT")),
    JAPAN(new Locale("", "JP")),
    MALAYSIA(new Locale("", "MY")),
    MEXICO(new Locale("", "MX")),
    NETHERLANDS(new Locale("", "NL")),
    NEW_ZEALAND(new Locale("", "NZ")),
    PAKISTAN(new Locale("", "PK")),
    POLAND(new Locale("", "PL")),
    PORTUGAL(new Locale("", "PT")),
    ROMANIA(new Locale("", "RO")),
    RUSSIAN_FEDERATION(new Locale("", "RU"), "Fédération de Russie", "Russia", "Russie", "Federazione Russa", "Federación de Rusia"),
    SINGAPORE(new Locale("", "SG")),
    SOUTH_AFRICA(new Locale("", "ZA")),
    SPAIN(new Locale("", "ES")),
    SWEDEN(new Locale("", "SE")),
    SWITZERLAND(new Locale("", "CH")),
    THAILAND(new Locale("", "TH")),
    UNITED_KINGDOM(new Locale("", "GB"), "Royaume-Uni", "UK", "England", "Angleterre", "Grande-Bretagne"),
    UNITED_STATES(new Locale("", "US"), "United States of America", "USA", "U.S.A.", "États-Unis d'Amérique", "États-Unis");
    private List<String> identifier;
    private final Locale locale;

    Countries(Locale locale, String... countries) {
      this.locale = locale;
      identifier = new ArrayList<String>();
      identifier.addAll(Arrays.asList(countries));
      String ident = getLocaleProperty("country." + locale.getCountry());
      identifier.addAll(Arrays.asList(ident.split("\\|")));
    }

    public List<String> getIdentifier() {
      return identifier;
    }

    @Override
    public Locale getLocale() {
      return locale;
    }
  }

  public interface Language {

    public Locale getLocale();

    public String name();

    public String getDisplayName();
  }

  public static enum AppLanguages implements Language {

    fr(Locale.FRENCH),
    en(Locale.US);
    private final Locale locale;
    private final String name;

    private AppLanguages(Locale locale) {
      this.locale = locale;
      this.name = locale.getDisplayName(locale);
    }

    private AppLanguages(Locale locale, String name) {
      this.locale = locale;
      this.name = name;
    }

    @Override
    public String getDisplayName() {
      return name;
    }

    @Override
    public Locale getLocale() {
      return locale;
    }
  }

  public static enum AvailableLanguages implements Language {

    ar(new Locale("ar", "MA"), "lang.arabic"),
    bg(new Locale("bg", "BG"), "lang.bulgarian"),
    zh(Locale.CHINA),
    da(new Locale("da", "DK"), "lang.danish"),
    hr(new Locale("hr", "HR"), "lang.croatian"),
    cs(new Locale("cs", "CZ"), "lang.czech"),
    nl(new Locale("nl", "NL"), "lang.dutch"),
    en(Locale.UK),
    fi(new Locale("fi", "FI"), "lang.finnish"),
    fr(Locale.FRANCE),
    de(Locale.GERMANY),
    el(new Locale("el", "GR"), "lang.greek"),
    iw(new Locale("iw", "IL"), "lang.hebrew"),
    hu(new Locale("hu", "HU"), "lang.hungarian"),
    is(new Locale("is", "IS"), "lang.icelandic"),
    it(Locale.ITALY),
    ja(Locale.JAPAN),
    ko(Locale.KOREA),
    no(new Locale("no", "NO"), "lang.norwegian"),
    pl(new Locale("pl", "PL"), "lang.polish"),
    pt(new Locale("pt", "PT"), "lang.portuguese"),
    ro(new Locale("ro", "RO"), "lang.romanian"),
    ru(new Locale("ru", "RU"), "lang.russian"),
    sl(new Locale("sl", "SI"), "lang.slovenian"),
    es(new Locale("es", "ES"), "lang.spanish"),
    sv(new Locale("sv", "SE"), "lang.swedish"),
    tr(new Locale("tr", "TR"), "lang.turkish"),
    uk(new Locale("uk", "UA"), "lang.ukrainian");
    private final Locale locale;
    private final String name;

    private AvailableLanguages(Locale locale) {
      this.locale = locale;
      this.name = null;
    }

    private AvailableLanguages(Locale locale, String name) {
      this.locale = locale;
      this.name = name;
    }

    @Override
    public String getDisplayName() {
      return name != null ? i18n(name) : StringUtils.capitalizedLetter(locale.getDisplayLanguage(Settings.getInstance().getAppLanguage().getLocale()), true);
    }

    @Override
    public Locale getLocale() {
      return locale;
    }
  }

  // Only most common languages for video media
  // @see http://www.roseindia.net/tutorials/i18n/locales-list.shtml
  private static enum Languages implements Language {

    Arabic(new Locale("ar", "")),
    Bulgarian(new Locale("bg", "")),
    Chinese(new Locale("zh", "")),
    Croatian(new Locale("hr", "")),
    Dutch(new Locale("nl", "")),
    English(new Locale("en", "")),
    Finnish(new Locale("fi", "")),
    French(new Locale("fr", "")),
    German(new Locale("de", "")),
    Greek(new Locale("el", "")),
    Hebrew(new Locale("iw", ""), "Hébreu", "Hebräisch", "Ebraico", "Hebreo"),
    Hungarian(new Locale("hu", "")),
    Icelandic(new Locale("is", "")),
    Italian(new Locale("it", "")),
    Japanese(new Locale("ja", "")),
    Korean(new Locale("ko", "")),
    Norwegian(new Locale("no", "")),
    Polish(new Locale("pl", "")),
    Portuguese(new Locale("pt", "")),
    Romanian(new Locale("ro", "")),
    Russian(new Locale("ru", "")),
    Spanish(new Locale("es", "")),
    Swedish(new Locale("sv", "")),
    Turkish(new Locale("tr", "")),
    Ukrainian(new Locale("uk", ""));
    private final List<String> identifier;
    private final Locale locale;

    Languages(Locale locale, String... langs) {
      this.locale = locale;
      identifier = new ArrayList<String>();
      identifier.addAll(Arrays.asList(langs));
      String ident = getLocaleProperty("language." + locale.getLanguage());
      identifier.addAll(Arrays.asList(ident.split("\\|")));
    }

    public List<String> getIdentifier() {
      return identifier;
    }

    @Override
    public Locale getLocale() {
      return locale;
    }

    @Override
    public String getDisplayName() {
      return locale.getDisplayLanguage(Settings.getInstance().getAppLanguage().getLocale());
    }
  }
  private static final ResourceBundle localBundle = ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle");

  public static Locale[] getSupportedDisplayLocales() {
    return new Locale[]{
      Locale.ENGLISH, Locale.FRENCH
    };
  }

  public static Locale findLanguage(String languageName, Locale... supportedDisplayLocales) {
    Locale lang = null;

    for (Languages cll : Languages.values()) {
      if (languageName.equalsIgnoreCase(cll.name().replace("_", " "))) {
        // check enum name
        lang = cll.getLocale();
      } else if (languageName.equalsIgnoreCase(cll.getLocale().getLanguage())) {
        // check enum locale language code
        lang = cll.getLocale();
      } else {
        // check enum locale display language
        for (Locale supportedLocale : supportedDisplayLocales) {
          if (languageName.equalsIgnoreCase(cll.getLocale().getDisplayLanguage(supportedLocale))) {
            lang = cll.getLocale();
            break;
          }
        }
      }
      if (lang == null) {
        // check enum identifier
        for (String ident : cll.getIdentifier()) {
          if (languageName.equalsIgnoreCase(ident)) {
            lang = cll.getLocale();
            break;
          }
        }
      }
      if (lang != null) {
        break;
      }
    }

    // if not found, search on map
    if (lang == null) {
      lang = getLanguageMap(supportedDisplayLocales).get(languageName);
    }

    // finally set to ROOT
    if (lang == null) {
      lang = Locale.ROOT;
    }

    return lang;
  }

  public static Map<String, Locale> getLanguageMap(Locale... supportedDisplayLocales) {
    return getLanguageMap(null, supportedDisplayLocales);
  }

  public static Map<String, Locale> getLanguageMap(List<String> removeTokens, Locale... supportedDisplayLocales) {
    Collator collator = Collator.getInstance(Locale.ROOT);
    collator.setDecomposition(Collator.FULL_DECOMPOSITION);
    collator.setStrength(Collator.PRIMARY);

    @SuppressWarnings({
      "unchecked", "rawtypes"
    })
    Comparator<String> order = (Comparator) collator;
    Map<String, Locale> languageMap = new TreeMap<String, Locale>(order);

    for (String code : Locale.getISOLanguages()) {
      Locale locale = new Locale(code);
      languageMap.put(locale.getLanguage(), locale);
      languageMap.put(locale.getISO3Language(), locale);

      // map display language names for given locales
      for (Locale language : new HashSet<Locale>(Arrays.asList(supportedDisplayLocales))) {
        // make sure language name is properly normalized so accents and whatever don't break the regex pattern syntax
        String languageName = Normalizer.normalize(locale.getDisplayLanguage(language), Form.NFKD);
        languageMap.put(languageName, locale);
      }
    }

    // remove illegal tokens
    languageMap.remove("");
    languageMap.remove("II");
    languageMap.remove("III");
    languageMap.remove("VI");
    languageMap.remove("VII");
    languageMap.remove("VIII");
    languageMap.remove("IX");

    if (removeTokens != null) {
      for (String token : removeTokens) {
        languageMap.remove(token);
      }
    }

    Map<String, Locale> result = Collections.unmodifiableMap(languageMap);
    return result;
  }

  public static Locale findCountry(String countryName, Locale... supportedDisplayLocales) {
    Locale country = null;

    for (Countries clc : Countries.values()) {
      if (countryName.equalsIgnoreCase(clc.name().replace("_", " "))) {
        // check enum name
        country = clc.getLocale();
      } else if (countryName.equalsIgnoreCase(clc.getLocale().getCountry())) {
        // check enum locale country code
        country = clc.getLocale();
      } else {
        // check enum locale display country
        for (Locale supportedLocale : supportedDisplayLocales) {
          if (countryName.equalsIgnoreCase(clc.getLocale().getDisplayCountry(supportedLocale))) {
            country = clc.getLocale();
            break;
          }
        }
      }
      
      if (country == null) {
        // check enum identifier
        for (String ident : clc.getIdentifier()) {
          if (countryName.equalsIgnoreCase(ident)) {
            country = clc.getLocale();
            break;
          }
        }
      }
      if (country != null) {
        break;
      }
    }

    // if not found, search on map
    if (country == null) {
      country = getCountryMap(supportedDisplayLocales).get(countryName);
    }

    // finally set to ROOT
    if (country == null) {
      country = Locale.ROOT;
    }

    return country;
  }

  public static Map<String, Locale> getCountryMap(Locale... supportedDisplayLocales) {
    Collator collator = Collator.getInstance(Locale.ROOT);
    collator.setDecomposition(Collator.FULL_DECOMPOSITION);
    collator.setStrength(Collator.PRIMARY);

    @SuppressWarnings({
      "unchecked", "rawtypes"
    })
    Comparator<String> order = (Comparator) collator;
    Map<String, Locale> countryMap = new TreeMap<String, Locale>(order);

    for (String code : Locale.getISOCountries()) {
      Locale locale = new Locale(code);
      countryMap.put(locale.getCountry(), locale);
      countryMap.put(locale.getISO3Country(), locale);

      // map display country names for given locales
      for (Locale country : new HashSet<Locale>(Arrays.asList(supportedDisplayLocales))) {
        // make sure country name is properly normalized so accents and whatever don't break the regex pattern syntax
        String countryName = Normalizer.normalize(locale.getDisplayCountry(country), Form.NFKD);
        countryMap.put(countryName, locale);
      }
    }

    // remove illegal tokens
    countryMap.remove("");

    Map<String, Locale> result = Collections.unmodifiableMap(countryMap);
    return result;
  }

  /**
   * Get string in i18n files
   *
   * @param bundleKey CacheKey to find
   * @return String depends on locale
   */
  public static String i18n(String bundleKey) {
    return i18n(bundleKey, bundleKey, localBundle);
  }

  public static String i18n(String bundleKey, ResourceBundle lBundle) {
    return i18n(bundleKey, bundleKey, lBundle);
  }

  /**
   * Get string in i18n files
   *
   * @param bundleKey CacheKey to find
   * @param defaultValue Default value
   * @param lBundle
   * @return String depends on locale or default value if key does not exist
   */
  public static String i18n(String bundleKey, String defaultValue, ResourceBundle lBundle) {
    if (lBundle != null && lBundle.containsKey(bundleKey)) {
      return lBundle.getString(bundleKey);
    }

    Settings.LOGGER.log(Level.CONFIG, String.format("No internationlization found for %s, use default value", bundleKey));
    return defaultValue;
  }

  private static String getLocaleProperty(String key) {
    return ResourceBundle.getBundle(LocaleUtils.class.getName(), Locale.ROOT).getString(key);
  }

  private LocaleUtils() {
    throw new UnsupportedOperationException();
  }
}
