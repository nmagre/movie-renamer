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
package fr.free.movierenamer.scraper.impl.movie;

import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import java.util.regex.Pattern;

/**
 * Class BeyazperdeScraper
 *
 * @author Nicolas Magré
 */
public final class BeyazperdeScraper extends AlloGroupScraper {

  private static final String host = "www.beyazperde.com";
  private static final String imageHost = "images.allocine.fr";
  private static final String name = "Beyazperde";
  private static final String search = "ara";
  private static final Pattern allocineID = Pattern.compile(".*film-(\\d+)-?.*");
  private static final Pattern allocinePersonID = Pattern.compile(".*sanatci-(\\d+).*");
  private static final Pattern runtimePattern = Pattern.compile("(\\d+)s (\\d+)dk");

  private enum Tag {

    Vizyon_Tarihi(AlloGroupScraper.InfoTag.Date_de_sortie),
    Yönetmen(AlloGroupScraper.InfoTag.Réalisé_par),
    Tür(AlloGroupScraper.InfoTag.Genre),
    Ülke(AlloGroupScraper.InfoTag.Nationalité),
    Kullanıcı(AlloGroupScraper.InfoTag.Spectateurs),
    Orijinal_adı(AlloGroupScraper.InfoTag.Titre_original),
    Bütçe(AlloGroupScraper.InfoTag.Budget),
    Dağıtımcı(AlloGroupScraper.InfoTag.Distributeur);
    private final AlloGroupScraper.InfoTag infotag;

    private Tag(AlloGroupScraper.InfoTag infotag) {
      this.infotag = infotag;
    }

    public AlloGroupScraper.InfoTag getInfoTag() {
      return infotag;
    }
  }

  public BeyazperdeScraper() {
    super(LocaleUtils.AvailableLanguages.tr);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  protected String getHost() {
    return host;
  }

  @Override
  protected AvailableLanguages getDefaultLanguage() {
    return AvailableLanguages.tr;
  }

  @Override
  protected String getSearchString() {
    return search;
  }

  @Override
  protected String getMoviePageString(IdInfo id) {
    return "/filmler/film-" + id;
  }

  @Override
  protected String getCastingPageString(IdInfo id) {
    return getMoviePageString(id) + "/oyuncular/";
  }

  @Override
  protected Pattern getIdPattern() {
    return allocineID;
  }

  @Override
  protected Pattern getPersonIdPattern() {
    return allocinePersonID;
  }

  @Override
  protected Pattern getRuntimePattern() {
    return runtimePattern;
  }

  @Override
  protected String getImageHost() {
    return imageHost;
  }

  @Override
  protected MovieInfo.MotionPictureRating getRatingScale() {
    return null;
  }

  @Override
  protected AlloGroupScraper.InfoTag getInfoTag(String str) {

    try {
      return Tag.valueOf(str.replace(":", "").trim()).getInfoTag();
    } catch (Exception ex) {
    }
    return AlloGroupScraper.InfoTag.unknown;
  }
}
