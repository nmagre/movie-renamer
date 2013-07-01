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
import fr.free.movierenamer.info.MovieInfo.MotionPictureRating;
import fr.free.movierenamer.utils.LocaleUtils;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Class AdorocinemaScrapper
 * @author Nicolas Magré
 */
public final class AdorocinemaScrapper  extends AlloGroupScrapper {

  private static final String host = "www.adorocinema.com";
  private static final String imageHost = "images.allocine.fr";
  private static final String name = "Adorocinema";
  private static final String search = "busca";
  private static final Pattern allocineID = Pattern.compile(".*filme-(\\d+).*");
  private static final Pattern allocinePersonID = Pattern.compile(".*personalidade-(\\d+).*");

  private enum Tag {

    Lançamento(InfoTag.Date_de_sortie),
    Dirigido_por(InfoTag.Réalisé_par),
    Gênero(InfoTag.Genre),
    Nacionalidade(InfoTag.Nationalité),
    Usuários (InfoTag.Spectateurs),
    Título_original(InfoTag.Titre_original),
    Orçamento(InfoTag.Budget),
    Distribuidor(InfoTag.Distributeur);
    private InfoTag infotag;

    private Tag(InfoTag infotag) {
      this.infotag = infotag;
    }

    public InfoTag getInfoTag() {
      return infotag;
    }
  }

  public AdorocinemaScrapper() {
    super(LocaleUtils.AvailableLanguages.pt);
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
    return LocaleUtils.AvailableLanguages.pt.getLocale();
  }

  @Override
  protected String getSearchString() {
    return search;
  }

  @Override
  protected String getMoviePageString(IdInfo id) {
    return "/filmes/filme-" + id;
  }

  @Override
  protected String getCastingPageString(IdInfo id) {
    return getMoviePageString(id) + "/creditos/";
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
  protected String getImageHost() {
    return imageHost;
  }

  @Override
  protected MotionPictureRating getRatingScale() {
    return MotionPictureRating.PORTUGAL;
  }

  @Override
  protected InfoTag getInfoTag(String str) {

    try {
      return Tag.valueOf(str).getInfoTag();
    } catch (Exception ex) {
    }
    return InfoTag.unknown;
  }
}