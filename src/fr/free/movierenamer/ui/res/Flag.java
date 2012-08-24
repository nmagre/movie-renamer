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

import fr.free.movierenamer.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.ImageIcon;

/**
 * Class Flag
 * @author Nicolas Magré
 */
public abstract class Flag {

  private static final ImageIcon Algeria = new ImageIcon(Utils.getImageFromJAR("/image/country/Algeria.png", Utils.class));
  private static final ImageIcon Argentina = new ImageIcon(Utils.getImageFromJAR("/image/country/Argentina.png", Utils.class));
  private static final ImageIcon Australia = new ImageIcon(Utils.getImageFromJAR("/image/country/Australia.png", Utils.class));
  private static final ImageIcon Austria = new ImageIcon(Utils.getImageFromJAR("/image/country/Austria.png", Utils.class));
  private static final ImageIcon Belgium = new ImageIcon(Utils.getImageFromJAR("/image/country/Belgium.png", Utils.class));
  private static final ImageIcon Brazil = new ImageIcon(Utils.getImageFromJAR("/image/country/Brazil.png", Utils.class));
  private static final ImageIcon Bulgaria = new ImageIcon(Utils.getImageFromJAR("/image/country/Bulgaria.png", Utils.class));
  private static final ImageIcon Canada = new ImageIcon(Utils.getImageFromJAR("/image/country/Canada.png", Utils.class));
  private static final ImageIcon China = new ImageIcon(Utils.getImageFromJAR("/image/country/China.png", Utils.class));
  private static final ImageIcon Colombia = new ImageIcon(Utils.getImageFromJAR("/image/country/Colombia.png", Utils.class));
  private static final ImageIcon Costa_Rica = new ImageIcon(Utils.getImageFromJAR("/image/country/Costa_Rica.png", Utils.class));
  private static final ImageIcon Croatia = new ImageIcon(Utils.getImageFromJAR("/image/country/Croatia.png", Utils.class));
  private static final ImageIcon Czech_Republic = new ImageIcon(Utils.getImageFromJAR("/image/country/Czech_Republic.png", Utils.class));
  private static final ImageIcon Denmark = new ImageIcon(Utils.getImageFromJAR("/image/country/Denmark.png", Utils.class));
  private static final ImageIcon Finland = new ImageIcon(Utils.getImageFromJAR("/image/country/Finland.png", Utils.class));
  private static final ImageIcon France = new ImageIcon(Utils.getImageFromJAR("/image/country/France.png", Utils.class));
  private static final ImageIcon Germany = new ImageIcon(Utils.getImageFromJAR("/image/country/Germany.png", Utils.class));
  private static final ImageIcon Greece = new ImageIcon(Utils.getImageFromJAR("/image/country/Greece.png", Utils.class));
  private static final ImageIcon Hong_Kong = new ImageIcon(Utils.getImageFromJAR("/image/country/Hong_Kong.png", Utils.class));
  private static final ImageIcon Hungary = new ImageIcon(Utils.getImageFromJAR("/image/country/Hungary.png", Utils.class));
  private static final ImageIcon Iceland = new ImageIcon(Utils.getImageFromJAR("/image/country/Iceland.png", Utils.class));
  private static final ImageIcon India = new ImageIcon(Utils.getImageFromJAR("/image/country/India.png", Utils.class));
  private static final ImageIcon Iran = new ImageIcon(Utils.getImageFromJAR("/image/country/Iran.png", Utils.class));
  private static final ImageIcon Ireland = new ImageIcon(Utils.getImageFromJAR("/image/country/Ireland.png", Utils.class));
  private static final ImageIcon Italy = new ImageIcon(Utils.getImageFromJAR("/image/country/Italy.png", Utils.class));
  private static final ImageIcon Israel = new ImageIcon(Utils.getImageFromJAR("/image/country/Israel.png", Utils.class));
  private static final ImageIcon Japan = new ImageIcon(Utils.getImageFromJAR("/image/country/Japan.png", Utils.class));
  private static final ImageIcon Malaysia = new ImageIcon(Utils.getImageFromJAR("/image/country/Malaysia.png", Utils.class));
  private static final ImageIcon Mexico = new ImageIcon(Utils.getImageFromJAR("/image/country/Mexico.png", Utils.class));
  private static final ImageIcon Netherlands = new ImageIcon(Utils.getImageFromJAR("/image/country/Netherlands.png", Utils.class));
  private static final ImageIcon New_Zealand = new ImageIcon(Utils.getImageFromJAR("/image/country/New_Zealand.png", Utils.class));
  private static final ImageIcon Norway = new ImageIcon(Utils.getImageFromJAR("/image/country/Norway.png", Utils.class));
  private static final ImageIcon Pakistan = new ImageIcon(Utils.getImageFromJAR("/image/country/Pakistan.png", Utils.class));
  private static final ImageIcon Poland = new ImageIcon(Utils.getImageFromJAR("/image/country/Poland.png", Utils.class));
  private static final ImageIcon Portugal = new ImageIcon(Utils.getImageFromJAR("/image/country/Portugal.png", Utils.class));
  private static final ImageIcon Romania = new ImageIcon(Utils.getImageFromJAR("/image/country/Romania.png", Utils.class));
  private static final ImageIcon Russian_Federation = new ImageIcon(Utils.getImageFromJAR("/image/country/Russian_Federation.png", Utils.class));
  private static final ImageIcon Singapore = new ImageIcon(Utils.getImageFromJAR("/image/country/Singapore.png", Utils.class));
  private static final ImageIcon South_Africa = new ImageIcon(Utils.getImageFromJAR("/image/country/South_Africa.png", Utils.class));
  private static final ImageIcon South_Korea = new ImageIcon(Utils.getImageFromJAR("/image/country/South_Korea.png", Utils.class));
  private static final ImageIcon Spain = new ImageIcon(Utils.getImageFromJAR("/image/country/Spain.png", Utils.class));
  private static final ImageIcon Sweden = new ImageIcon(Utils.getImageFromJAR("/image/country/Sweden.png", Utils.class));
  private static final ImageIcon Switzerland = new ImageIcon(Utils.getImageFromJAR("/image/country/Switzerland.png", Utils.class));
  private static final ImageIcon Thailand = new ImageIcon(Utils.getImageFromJAR("/image/country/Thailand.png", Utils.class));
  private static final ImageIcon Turkey = new ImageIcon(Utils.getImageFromJAR("/image/country/Turkey.png", Utils.class));
  private static final ImageIcon Ukraine = new ImageIcon(Utils.getImageFromJAR("/image/country/Ukraine.png", Utils.class));
  private static final ImageIcon United_Kingdom = new ImageIcon(Utils.getImageFromJAR("/image/country/United_Kingdom.png", Utils.class));
  private static final ImageIcon United_States = new ImageIcon(Utils.getImageFromJAR("/image/country/United_States_of_America.png", Utils.class));
  //Unknown flag
  private static final ImageIcon Unknown = new ImageIcon(Utils.getImageFromJAR("/image/country/Unknown.png", Utils.class));

  // Only most common country for video media
  private enum Countries {

    ARGENTINA(Argentina, "Argentine", "AR"),
    AUSTRALIA(Australia, "Australie", "AU"),
    AUSTRIA(Austria, "Autriche", "AT"),
    BELGIUM(Belgium, "Belgique", "BE"),
    BRAZIL(Brazil, "Brésil", "BR"),
    BULGARIA(Bulgaria, "Bulgarie", "BG"),
    CANADA(Canada, "CA"),
    CHINA(China, "Chine", "CN"),
    COLOMBIA(Colombia, "Colombie", "CO"),
    COSTA_RICA(Costa_Rica, "CR"),
    CZECH_REPUBLIC(Czech_Republic, "République tchèque", "CZ"),
    DENMARK(Denmark, "Danemark", "DK"),
    FINLAND(Finland, "Finlande", "FI"),
    FRANCE(France, "FR"),
    GERMANY(Germany, "Allemagne", "DE"),
    GREECE(Greece, "Grèce", "GR"),
    HONG_KONG(Hong_Kong, "Hong-Kong", "HK"),
    HUNGARY(Hungary, "Hongrie", "HU"),
    ICELAND(Iceland, "Islande", "IS"),
    INDIA(India, "Inde", "IN"),
    IRAN(Iran, "IR"),
    IRELAND(Ireland, "Irlande", "IE"),
    ITALY(Italy, "Italie", "IT"),
    JAPAN(Japan, "Japon", "JP"),
    MALAYSIA(Malaysia, "Malaisie", "MY"),
    MEXICO(Mexico, "Mexique", "MX"),
    NETHERLANDS(Netherlands, "Pays-Bas", "NL"),
    NEW_ZEALAND(New_Zealand, "Nouvelle-Zélande", "NZ"),
    PAKISTAN(Pakistan, "PK"),
    POLAND(Poland, "Pologne", "PL"),
    PORTUGAL(Portugal, "PT"),
    ROMANIA(Romania, "Roumanie", "RO"),
    RUSSIAN_FEDERATION(Russian_Federation, "Fédération de Russie", "Russia", "Russie", "RU"),
    SINGAPORE(Singapore, "Singapour", "SG"),
    SOUTH_AFRICA(South_Africa, "Afrique du Sud", "ZA"),
    SPAIN(Spain, "Espagne", "ES"),
    SWEDEN(Sweden, "Suède", "SE"),
    SWITZERLAND(Switzerland, "Suisse", "CH"),
    THAILAND(Thailand, "Thaïlande", "TH"),
    UNITED_KINGDOM(United_Kingdom, "Royaume-Uni", "England", "Angleterre", "UK"),
    UNITED_STATES(United_States, "United States of America", "USA", "États-Unis d'Amérique", "États-Unis", "US");
    private List<String> identifier;
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

    Arabic(Algeria, "Arabe"),
    Bulgarian(Bulgaria, "Bulgare"),
    Chinese(China, "Chinois"),
    Croatian(Croatia, "Croate"),
    Dutch(Netherlands, "Néerlandais"),
    English(United_Kingdom, "Anglais"),
    Finnish(Finland, "Finlandais"),
    French(France, "Français"),
    German(Germany, "Allemand"),
    Greek(Greece, "Grecque"),
    Hebrew(Israel, "hébreu"),
    Hindi(India),
    Hungarian(Hungary, "Hongrois"),
    Icelandic(Iceland, "Islandais"),
    Italian(Italy, "Italien"),
    Japanese(Japan, "Japonais"),
    Korean(South_Korea, "Coréen"),
    Norwegian(Norway, "Norvégien"),
    Persian(Iran, "Persan"),
    Polish(Poland, "Polonais"),
    Portuguese(Portugal, "Portugais"),
    Punjabi(Pakistan),
    Romanian(Romania, "Roumain"),
    Russian(Russian_Federation, "Russe"),
    Spanish(Spain, "Espagnol"),
    Swedish(Sweden, "Suédois"),
    Turkish(Turkey, "Turc"),
    Ukrainian(Ukraine, "Ukrainien");
    private List<String> identifier;
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
