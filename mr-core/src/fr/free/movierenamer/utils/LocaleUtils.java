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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;

import fr.free.movierenamer.settings.Settings;

/**
 * Class LocaleUtils
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public final class LocaleUtils {

  // Only most common country for video media
  private enum CustomLocaleCountry {
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

    CustomLocaleCountry(Locale locale, String... countries) {
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

  // Only most common languages for video media
  // @see http://www.roseindia.net/tutorials/i18n/locales-list.shtml
  private enum CustomLocaleLang {
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

    CustomLocaleLang(Locale locale, String... langs) {
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

  public static Locale[] getAvailableLanguages() {
    return new Locale[] {
        Locale.ENGLISH, Locale.FRENCH
    };
  }

  public static Locale getLocale(String code) {
    return getLocale(code, Locale.ROOT);
  }

  public static Locale getLocale(String code, Locale currentLocale) {
    Locale found = null;
    if (code != null) {
      // first search most common
      //1° country
      for (CustomLocaleCountry clc : CustomLocaleCountry.values()) {
        if (code.equalsIgnoreCase(clc.name().replace("_", " "))) {
          found = clc.getLocale();
          break;
        }
        if (code.equalsIgnoreCase(clc.getLocale().getCountry())) {
          found = clc.getLocale();
          break;
        }
        for (String ident : clc.getIdentifier()) {
          if (code.equalsIgnoreCase(ident)) {
            found = clc.getLocale();
            break;
          }
        }
        if (found != null) {
          break;
        }
      }
      //2° lang
      if (found == null) {
        for (CustomLocaleLang cll : CustomLocaleLang.values()) {
          if (code.equalsIgnoreCase(cll.name().replace("_", " "))) {
            found = cll.getLocale();
            break;
          }
          if (code.equalsIgnoreCase(cll.getLocale().getLanguage())) {
            found = cll.getLocale();
            break;
          }
          for (String ident : cll.getIdentifier()) {
            if (code.equalsIgnoreCase(ident)) {
              found = cll.getLocale();
              break;
            }
          }
          if (found != null) {
            break;
          }
        }
      }
      // if not found, search the available locales
      if (found == null) {
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
    }
    
    if (found == null) {
      found = Locale.ROOT;
    }

    return found;
  }

  /**
   * Get string in i18n files
   * 
   * @param bundleKey
   *          CacheKey to find
   * @return String depends on locale
   */
  public static String i18n(String bundleKey) {
    return i18n(bundleKey, bundleKey);
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
