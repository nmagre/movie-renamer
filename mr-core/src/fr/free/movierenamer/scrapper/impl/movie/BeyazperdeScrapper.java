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
package fr.free.movierenamer.scrapper.impl.movie;

import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.utils.LocaleUtils;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Class BeyazperdeScrapper
 * @author Nicolas Magré
 */
public final class BeyazperdeScrapper extends AlloGroupScrapper {

  private static final String host = "www.beyazperde.com";
  private static final String imageHost = "images.allocine.fr";
  private static final String name = "Beyazperde";
  private static final String search = "ara";
  private static final Pattern allocineID = Pattern.compile(".*film-(\\d+)-?.*");
  private static final Pattern allocinePersonID = Pattern.compile(".*sanatci-(\\d+).*");
  private static final Pattern runtimePattern = Pattern.compile("(\\d+)s (\\d+)dk");

  private enum Tag {

    Vizyon_Tarihi(AlloGroupScrapper.InfoTag.Date_de_sortie),
    Yönetmen(AlloGroupScrapper.InfoTag.Réalisé_par),
    Tür(AlloGroupScrapper.InfoTag.Genre),
    Ülke(AlloGroupScrapper.InfoTag.Nationalité),
    Kullanıcı(AlloGroupScrapper.InfoTag.Spectateurs),
    Orijinal_adı(AlloGroupScrapper.InfoTag.Titre_original),
    Bütçe(AlloGroupScrapper.InfoTag.Budget),
    Dağıtımcı(AlloGroupScrapper.InfoTag.Distributeur);
    private AlloGroupScrapper.InfoTag infotag;

    private Tag(AlloGroupScrapper.InfoTag infotag) {
      this.infotag = infotag;
    }

    public AlloGroupScrapper.InfoTag getInfoTag() {
      return infotag;
    }
  }

  public BeyazperdeScrapper() {
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
  protected Locale getDefaultLanguage() {
    return LocaleUtils.AvailableLanguages.tr.getLocale();
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
  protected AlloGroupScrapper.InfoTag getInfoTag(String str) {

    try {
      return Tag.valueOf(str.replace(":", "").trim()).getInfoTag();
    } catch (Exception ex) {
    }
    return AlloGroupScrapper.InfoTag.unknown;
  }
}