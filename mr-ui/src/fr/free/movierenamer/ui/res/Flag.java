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
package fr.free.movierenamer.ui.res;

import fr.free.movierenamer.ui.utils.ImageUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.ImageIcon;

/**
 * Class Flag
 * 
 * @author Nicolas Magré
 */
public abstract class Flag {

  private static final ImageIcon Algeria = new ImageIcon(ImageUtils.getImageFromJAR("country/Algeria.png"));
  private static final ImageIcon Argentina = new ImageIcon(ImageUtils.getImageFromJAR("country/Argentina.png"));
  private static final ImageIcon Australia = new ImageIcon(ImageUtils.getImageFromJAR("country/Australia.png"));
  private static final ImageIcon Austria = new ImageIcon(ImageUtils.getImageFromJAR("country/Austria.png"));
  private static final ImageIcon Belgium = new ImageIcon(ImageUtils.getImageFromJAR("country/Belgium.png"));
  private static final ImageIcon Brazil = new ImageIcon(ImageUtils.getImageFromJAR("country/Brazil.png"));
  private static final ImageIcon Bulgaria = new ImageIcon(ImageUtils.getImageFromJAR("country/Bulgaria.png"));
  private static final ImageIcon Canada = new ImageIcon(ImageUtils.getImageFromJAR("country/Canada.png"));
  private static final ImageIcon China = new ImageIcon(ImageUtils.getImageFromJAR("country/China.png"));
  private static final ImageIcon Colombia = new ImageIcon(ImageUtils.getImageFromJAR("country/Colombia.png"));
  private static final ImageIcon Costa_Rica = new ImageIcon(ImageUtils.getImageFromJAR("country/Costa_Rica.png"));
  private static final ImageIcon Croatia = new ImageIcon(ImageUtils.getImageFromJAR("country/Croatia.png"));
  private static final ImageIcon Czech_Republic = new ImageIcon(ImageUtils.getImageFromJAR("country/Czech_Republic.png"));
  private static final ImageIcon Denmark = new ImageIcon(ImageUtils.getImageFromJAR("country/Denmark.png"));
  private static final ImageIcon Finland = new ImageIcon(ImageUtils.getImageFromJAR("country/Finland.png"));
  private static final ImageIcon France = new ImageIcon(ImageUtils.getImageFromJAR("country/France.png"));
  private static final ImageIcon Germany = new ImageIcon(ImageUtils.getImageFromJAR("country/Germany.png"));
  private static final ImageIcon Greece = new ImageIcon(ImageUtils.getImageFromJAR("country/Greece.png"));
  private static final ImageIcon Hong_Kong = new ImageIcon(ImageUtils.getImageFromJAR("country/Hong_Kong.png"));
  private static final ImageIcon Hungary = new ImageIcon(ImageUtils.getImageFromJAR("country/Hungary.png"));
  private static final ImageIcon Iceland = new ImageIcon(ImageUtils.getImageFromJAR("country/Iceland.png"));
  private static final ImageIcon India = new ImageIcon(ImageUtils.getImageFromJAR("country/India.png"));
  private static final ImageIcon Iran = new ImageIcon(ImageUtils.getImageFromJAR("country/Iran.png"));
  private static final ImageIcon Ireland = new ImageIcon(ImageUtils.getImageFromJAR("country/Ireland.png"));
  private static final ImageIcon Italy = new ImageIcon(ImageUtils.getImageFromJAR("country/Italy.png"));
  private static final ImageIcon Israel = new ImageIcon(ImageUtils.getImageFromJAR("country/Israel.png"));
  private static final ImageIcon Japan = new ImageIcon(ImageUtils.getImageFromJAR("country/Japan.png"));
  private static final ImageIcon Malaysia = new ImageIcon(ImageUtils.getImageFromJAR("country/Malaysia.png"));
  private static final ImageIcon Mexico = new ImageIcon(ImageUtils.getImageFromJAR("country/Mexico.png"));
  private static final ImageIcon Netherlands = new ImageIcon(ImageUtils.getImageFromJAR("country/Netherlands.png"));
  private static final ImageIcon New_Zealand = new ImageIcon(ImageUtils.getImageFromJAR("country/New_Zealand.png"));
  private static final ImageIcon Norway = new ImageIcon(ImageUtils.getImageFromJAR("country/Norway.png"));
  private static final ImageIcon Pakistan = new ImageIcon(ImageUtils.getImageFromJAR("country/Pakistan.png"));
  private static final ImageIcon Poland = new ImageIcon(ImageUtils.getImageFromJAR("country/Poland.png"));
  private static final ImageIcon Portugal = new ImageIcon(ImageUtils.getImageFromJAR("country/Portugal.png"));
  private static final ImageIcon Romania = new ImageIcon(ImageUtils.getImageFromJAR("country/Romania.png"));
  private static final ImageIcon Russian_Federation = new ImageIcon(ImageUtils.getImageFromJAR("country/Russian_Federation.png"));
  private static final ImageIcon Singapore = new ImageIcon(ImageUtils.getImageFromJAR("country/Singapore.png"));
  private static final ImageIcon South_Africa = new ImageIcon(ImageUtils.getImageFromJAR("country/South_Africa.png"));
  private static final ImageIcon South_Korea = new ImageIcon(ImageUtils.getImageFromJAR("country/South_Korea.png"));
  private static final ImageIcon Spain = new ImageIcon(ImageUtils.getImageFromJAR("country/Spain.png"));
  private static final ImageIcon Sweden = new ImageIcon(ImageUtils.getImageFromJAR("country/Sweden.png"));
  private static final ImageIcon Switzerland = new ImageIcon(ImageUtils.getImageFromJAR("country/Switzerland.png"));
  private static final ImageIcon Thailand = new ImageIcon(ImageUtils.getImageFromJAR("country/Thailand.png"));
  private static final ImageIcon Turkey = new ImageIcon(ImageUtils.getImageFromJAR("country/Turkey.png"));
  private static final ImageIcon Ukraine = new ImageIcon(ImageUtils.getImageFromJAR("country/Ukraine.png"));
  private static final ImageIcon United_Kingdom = new ImageIcon(ImageUtils.getImageFromJAR("country/United_Kingdom.png"));
  private static final ImageIcon United_States = new ImageIcon(ImageUtils.getImageFromJAR("country/United_States_of_America.png"));
  //Unknown flag
  private static final ImageIcon Unknown = new ImageIcon(ImageUtils.getImageFromJAR("country/Unknown.png"));

  // Only most common country for video media
  private enum Countries {
    ARGENTINA(Argentina, "Argentine", "AR","Argentinien"),
    AUSTRALIA(Australia, "Australie", "AU","Australien"),
    AUSTRIA(Austria, "Autriche", "AT", "Österreich"),
    BELGIUM(Belgium, "Belgique", "BE", "Belgien", "Belgio", "Bélgica"),
    BRAZIL(Brazil, "Brésil", "BR", "Brasilien", "Brasile", "Brasil"),
    BULGARIA(Bulgaria, "Bulgarie", "BG", "Bulgarien"),
    CANADA(Canada, "CA", "Kanada", "Canadá"),
    CHINA(China, "Chine", "CN"),
    COLOMBIA(Colombia, "Colombie", "CO", "Kolumbien"),
    COSTA_RICA(Costa_Rica, "CR", "Costarica"),
    CZECH_REPUBLIC(Czech_Republic, "République tchèque", "CZ", "Tschechische Republik", "Repubblica Ceca", "República Checa"),
    DENMARK(Denmark, "Danemark", "DK", "Dänemark", "Danimarca", "Dinamarca"),
    FINLAND(Finland, "Finlande", "FI", "Finnland", "Finlandia"),
    FRANCE(France, "FR", "Frankreich", "Francia"),
    GERMANY(Germany, "Allemagne", "DE", "Deutschland", "Germania", "Alemania"),
    GREECE(Greece, "Grèce", "GR", "Griechenland", "Grecia"),
    HONG_KONG(Hong_Kong, "Hong-Kong", "HK", "Hongkong", "Hong kong"),
    HUNGARY(Hungary, "Hongrie", "HU", "Ungarn", "Ungheria", "Hungría"),
    ICELAND(Iceland, "Islande", "IS", "Island", "Islanda", "Islandia"),
    INDIA(India, "Inde", "IN", "Indien"),
    IRAN(Iran, "IR", "Irán"),
    IRELAND(Ireland, "Irlande", "IE", "Irland", "Irlanda"),
    ITALY(Italy, "Italie", "IT", "Italien", "Italia"),
    JAPAN(Japan, "Japon", "JP", "Giappone", "Japón"),
    MALAYSIA(Malaysia, "Malaisie", "MY", "Malasia"),
    MEXICO(Mexico, "Mexique", "MX", "Mexiko", "Messico", "México"),
    NETHERLANDS(Netherlands, "Pays-Bas", "NL", "Niederlande", "Paesi Bassi", "Países Bajos"),
    NEW_ZEALAND(New_Zealand, "Nouvelle-Zélande", "NZ", "Neozelandese", "Nueva Zelandia"),
    PAKISTAN(Pakistan, "PK", "Pakistán"),
    POLAND(Poland, "Pologne", "PL", "Polen", "Polonia"),
    PORTUGAL(Portugal, "PT", "Portogallo"),
    ROMANIA(Romania, "Roumanie", "RO", "Rumänien", "Rumania"),
    RUSSIAN_FEDERATION(Russian_Federation, "Fédération de Russie", "Russia", "Russie", "RU", "Federazione Russa", "Federación de Rusia"),
    SINGAPORE(Singapore, "Singapour", "SG", "Singapur", "Singapore"),
    SOUTH_AFRICA(South_Africa, "Afrique du Sud", "ZA", "Südafrika", "Sudafrica", "Sudáfrica"),
    SPAIN(Spain, "Espagne", "ES", "Spanien", "Spagna", "España"),
    SWEDEN(Sweden, "Suède", "SE", "Schweden", "Svezia", "Suecia"),
    SWITZERLAND(Switzerland, "Suisse", "CH", "Schweiz", "Svizzera", "Suiza"),
    THAILAND(Thailand, "Thaïlande", "TH", "Thailand", "Thailandia", "Tailandia"),
    UNITED_KINGDOM(United_Kingdom, "Royaume-Uni", "England", "Angleterre", "Grande-Bretagne", "UK", "Vereinigtes Königreich", "Regno Unito", "Reino Unido"),
    UNITED_STATES(United_States, "United States of America", "USA", "U.S.A.", "États-Unis d'Amérique", "États-Unis", "US", "Vereinigte Staaten", "Stati Uniti", "Estados Unidos");
    private final List<String> identifier;
    public final ImageIcon flag;

    Countries(ImageIcon flag, String... countries) {
      this.flag = flag;
      identifier = new ArrayList<String>();
      identifier.addAll(Arrays.asList(countries));
    }

    public List<String> getIdentifier() {
      return identifier;
    }
  }

  // Only most common languages for video media
  private enum Lang {
    Arabic(Algeria, "Arabe", "Arabisch", "Arabo", "árabe"),
    Bulgarian(Bulgaria, "Bulgare", "Bulgarisch", "Bulgarian", "Búlgaro"),
    Chinese(China, "Chinois", "Cinese", "Chino"),
    Croatian(Croatia, "Croate", "kroatisch", "Croato", "Croata"),
    Dutch(Netherlands, "Néerlandais", "Holländer", "Olandese", "Holandés"),
    English(United_Kingdom, "Anglais", "Englisch", "Inglese", "Inglés"),
    Finnish(Finland, "Finlandais", "Finnisch", "Finlandese", "Finlandés"),
    French(France, "Français", "Französisch", "Francese"),
    German(Germany, "Allemand", "Deutsch", "Tedesco", "Alemán"),
    Greek(Greece, "Grecque", "Griechisch", "Greco", "Griego"),
    Hebrew(Israel, "Hébreu", "Hebräisch", "Ebraico", "Hebreo"),
    Hindi(India),
    Hungarian(Hungary, "Hongrois", "Ungarisch", "Ungherese", "Húngaro"),
    Icelandic(Iceland, "Islandais", "Isländisch", "Islandese", "Islandés"),
    Italian(Italy, "Italien", "Italienisch", "italiano"),
    Japanese(Japan, "Japonais", "Japanisch", "Giapponese", "Japonés"),
    Korean(South_Korea, "Coréen", "Koreanisch", "Coreano"),
    Norwegian(Norway, "Norvégien", "Norwegisch", "Norvegese", "Noruego"),
    Persian(Iran, "Persan", "Persisch", "Persiano", "Persa"),
    Polish(Poland, "Polonais", "Polnisch", "Polacco", "Polaco"),
    Portuguese(Portugal, "Portugais", "Portugiesisch", "Portoghese", "Portugués"),
    Punjabi(Pakistan, "Panjabi"),
    Romanian(Romania, "Roumain", "Rumänisch", "Rumeno", "Rumano"),
    Russian(Russian_Federation, "Russe", "Russisch", "Russo", "Ruso"),
    Spanish(Spain, "Espagnol", "Spanisch", "Spagnolo", "Español"),
    Swedish(Sweden, "Suédois", "Schwedisch", "Svedese", "Sueco"),
    Turkish(Turkey, "Turc", "Türkisch", "Turco"),
    Ukrainian(Ukraine, "Ukrainien", "Ukrainisch", "Ucraino", "Ucranio");
    private final List<String> identifier;
    public final ImageIcon flag;

    Lang(ImageIcon flag, String... langs) {
      this.flag = flag;
      identifier = new ArrayList<String>();
      identifier.addAll(Arrays.asList(langs));
    }

    public List<String> getIdentifier() {
      return identifier;
    }
  }

  public static ImageIcon getFlagByCountry(String country) {
    country = country.toLowerCase().trim();
    for (Countries ct : Countries.values()) {
      if (country.equalsIgnoreCase(ct.name().replace("_", " "))) {
        return ct.flag;
      }
      for (String ident : ct.getIdentifier()) {
        if (country.equalsIgnoreCase(ident)) {
          return ct.flag;
        }
      }
    }

    return Unknown;
  }

  public static ImageIcon getFlagByLang(String lang) {
    lang = lang.toLowerCase().trim();
    for (Lang lg : Lang.values()) {
      if (lang.equalsIgnoreCase(lg.name())) {
        return lg.flag;
      }
      for (String ident : lg.getIdentifier()) {
        if (lang.equalsIgnoreCase(ident)) {
          return lg.flag;
        }
      }
    }
    return Unknown;
  }
}
