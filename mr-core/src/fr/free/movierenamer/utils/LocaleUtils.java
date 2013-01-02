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
    ARGENTINA(new Locale("", "AR"), "Argentine", "Argentinien"),
    AUSTRALIA(new Locale("", "AU"), "Australie", "Australien"),
    AUSTRIA(new Locale("", "AT"), "Autriche", "Österreich"),
    BELGIUM(new Locale("", "BE"), "Belgique", "Belgien", "Belgio", "Bélgica"),
    BRAZIL(new Locale("", "BR"), "Brésil", "Brasilien", "Brasile", "Brasil"),
    BULGARIA(new Locale("", "BG"), "Bulgarie", "Bulgarien"),
    CANADA(new Locale("", "CA"), "Kanada", "Canadá"),
    CHINA(new Locale("", "CN"), "Chine"),
    COLOMBIA(new Locale("", "CO"), "Colombie", "Kolumbien"),
    COSTA_RICA(new Locale("", "CR"), "Costarica"),
    CZECH_REPUBLIC(new Locale("", "CZ"), "République tchèque", "Tschechische Republik", "Repubblica Ceca", "República Checa"),
    DENMARK(new Locale("", "DK"), "Danemark", "Dänemark", "Danimarca", "Dinamarca"),
    FINLAND(new Locale("", "FI"), "Finlande", "Finnland", "Finlandia"),
    FRANCE(new Locale("", "FR"), "Frankreich", "Francia"),
    GERMANY(new Locale("", "DE"), "Allemagne", "Deutschland", "Germania", "Alemania"),
    GREECE(new Locale("", "GR"), "Grèce", "Griechenland", "Grecia"),
    HONG_KONG(new Locale("", "HK"), "Hong-Kong", "Hongkong", "Hong kong"),
    HUNGARY(new Locale("", "HU"), "Hongrie", "Ungarn", "Ungheria", "Hungría"),
    ICELAND(new Locale("", "IS"), "Islande", "Island", "Islanda", "Islandia"),
    INDIA(new Locale("", "IN"), "Inde", "Indien"),
    IRAN(new Locale("", "IR"), "Irán"),
    IRELAND(new Locale("", "IE"), "Irlande", "Irland", "Irlanda"),
    ITALY(new Locale("", "IT"), "Italie", "Italien", "Italia"),
    JAPAN(new Locale("", "JP"), "Japon", "Giappone", "Japón"),
    MALAYSIA(new Locale("", "MY"), "Malaisie", "Malasia"),
    MEXICO(new Locale("", "MX"), "Mexique", "Mexiko", "Messico", "México"),
    NETHERLANDS(new Locale("", "NL"), "Pays-Bas", "Niederlande", "Paesi Bassi", "Países Bajos"),
    NEW_ZEALAND(new Locale("", "NZ"), "Nouvelle-Zélande", "Neozelandese", "Nueva Zelandia"),
    PAKISTAN(new Locale("", "PK"), "Pakistán"),
    POLAND(new Locale("", "PL"), "Pologne", "Polen", "Polonia"),
    PORTUGAL(new Locale("", "PT"), "Portogallo"),
    ROMANIA(new Locale("", "RO"), "Roumanie", "Rumänien", "Rumania"),
    RUSSIAN_FEDERATION(new Locale("", "RU"), "Fédération de Russie", "Russia", "Russie", "Federazione Russa", "Federación de Rusia"),
    SINGAPORE(new Locale("", "SG"), "Singapour", "Singapur", "Singapore"),
    SOUTH_AFRICA(new Locale("", "ZA"), "Afrique du Sud", "Südafrika", "Sudafrica", "Sudáfrica"),
    SPAIN(new Locale("", "ES"), "Espagne", "Spanien", "Spagna", "España"),
    SWEDEN(new Locale("", "SE"), "Suède", "Schweden", "Svezia", "Suecia"),
    SWITZERLAND(new Locale("", "CH"), "Suisse", "Schweiz", "Svizzera", "Suiza"),
    THAILAND(new Locale("", "TH"), "Thaïlande", "Thailand", "Thailandia", "Tailandia"),
    UNITED_KINGDOM(new Locale("", "GB"), "Royaume-Uni", "UK", "England", "Angleterre", "Grande-Bretagne", "Vereinigtes Königreich", "Regno Unito", "Reino Unido"),
    UNITED_STATES(new Locale("", "US"), "United States of America", "USA", "U.S.A.", "États-Unis d'Amérique", "États-Unis", "Vereinigte Staaten", "Stati Uniti", "Estados Unidos");
    private List<String> identifier;
    private final Locale locale;

    Countries(Locale locale, String... countries) {
      this.locale = locale;
      identifier = new ArrayList<String>();
      identifier.addAll(Arrays.asList(countries));
    }

    public List<String> getIdentifier() {
      return identifier;
    }

    public Locale getLocale() {
      return locale;
    }

  }

  public interface Language {
    public Locale getLocale();
  }

  public static enum AvailableLanguages implements Language {
    en(Locale.ENGLISH),
    fr(Locale.FRENCH),
    es(new Locale("es", "")),
    it(Locale.ITALIAN),
    de(Locale.GERMAN);
    private final Locale locale;

    private AvailableLanguages(Locale locale) {
      this.locale = locale;
    }

    @Override
    public Locale getLocale() {
      return locale;
    }
  }

  // Only most common languages for video media
  // @see http://www.roseindia.net/tutorials/i18n/locales-list.shtml
  private static enum Languages implements Language {
    Arabic(new Locale("ar", ""), "Arabe", "Arabisch", "Arabo", "árabe"),
    Bulgarian(new Locale("bg", ""), "Bulgare", "Bulgarisch", "Bulgarian", "Búlgaro"),
    Chinese(new Locale("zh", ""), "Chinois", "Cinese", "Chino"),
    Croatian(new Locale("hr", ""), "Croate", "kroatisch", "Croato", "Croata"),
    Dutch(new Locale("nl", ""), "Néerlandais", "Holländer", "Olandese", "Holandés"),
    English(new Locale("en", ""), "Anglais", "Englisch", "Inglese", "Inglés"),
    Finnish(new Locale("fi", ""), "Finlandais", "Finnisch", "Finlandese", "Finlandés"),
    French(new Locale("fr", ""), "Français", "Französisch", "Francese"),
    German(new Locale("de", ""), "Allemand", "Deutsch", "Tedesco", "Alemán"),
    Greek(new Locale("el", ""), "Grecque", "Griechisch", "Greco", "Griego"),
    Hebrew(new Locale("iw", ""), "Hébreu", "Hebräisch", "Ebraico", "Hebreo"),
    Hungarian(new Locale("hu", ""), "Hongrois", "Ungarisch", "Ungherese", "Húngaro"),
    Icelandic(new Locale("is", ""), "Islandais", "Isländisch", "Islandese", "Islandés"),
    Italian(new Locale("it", ""), "Italien", "Italienisch", "italiano"),
    Japanese(new Locale("ja", ""), "Japonais", "Japanisch", "Giapponese", "Japonés"),
    Korean(new Locale("ko", ""), "Coréen", "Koreanisch", "Coreano"),
    Norwegian(new Locale("no", ""), "Norvégien", "Norwegisch", "Norvegese", "Noruego"),
    Polish(new Locale("pl", ""), "Polonais", "Polnisch", "Polacco", "Polaco"),
    Portuguese(new Locale("pt", ""), "Portugais", "Portugiesisch", "Portoghese", "Portugués"),
    Romanian(new Locale("ro", ""), "Roumain", "Rumänisch", "Rumeno", "Rumano"),
    Russian(new Locale("ru", ""), "Russe", "Russisch", "Russo", "Ruso"),
    Spanish(new Locale("es", ""), "Espagnol", "Spanisch", "Spagnolo", "Español"),
    Swedish(new Locale("sv", ""), "Suédois", "Schwedisch", "Svedese", "Sueco"),
    Turkish(new Locale("tr", ""), "Turc", "Türkisch", "Turco"),
    Ukrainian(new Locale("uk", ""), "Ukrainien", "Ukrainisch", "Ucraino", "Ucranio");
    private List<String> identifier;
    private final Locale locale;

    Languages(Locale locale, String... langs) {
      this.locale = locale;
      identifier = new ArrayList<String>();
      identifier.addAll(Arrays.asList(langs));
    }

    public List<String> getIdentifier() {
      return identifier;
    }

    public Locale getLocale() {
      return locale;
    }

  }

  private static final ResourceBundle localBundle = ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle");
  public static ResourceBundle localBundleExt = null;

  public static Locale[] getSupportedDisplayLocales() {
    return new Locale[] {
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
   * @param bundleKey
   *          CacheKey to find
   * @return String depends on locale
   */
  public static String i18n(String bundleKey) {
    return i18n(bundleKey, bundleKey, localBundle);
  }

  public static String i18nExt(String bundleKey) {
    return i18n(bundleKey, bundleKey, localBundleExt);
  }

  public static String i18n(String bundleKey, ResourceBundle lBundle) {
    return i18n(bundleKey, bundleKey, lBundle);
  }

  /**
   * Get string in i18n files
   *
   * @param bundleKey
   *          CacheKey to find
   * @param defaultValue
   *          Default value
   * @return String depends on locale or default value if key dos not exist
   */
  public static String i18n(String bundleKey, String defaultValue, ResourceBundle lBundle) {
    if (lBundle != null && lBundle.containsKey(bundleKey)) {
      return lBundle.getString(bundleKey);
    } else {
      Settings.LOGGER.log(Level.CONFIG, "No internationlization found for {0}, use default value", bundleKey);
      return defaultValue;
    }
  }

  private LocaleUtils() {
    throw new UnsupportedOperationException();
  }
}
