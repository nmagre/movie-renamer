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
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Class ScreenRushScrapper
 *
 * @author Nicolas Magré
 */
public final class ScreenRushScrapper extends AlloGroupScrapper {

  private static final String host = "www.screenrush.co.uk";
  private static final String imageHost = "images.screenrush.co.uk";
  private static final String name = "Screenrush";
  private static final String search = "search";
  private static final Pattern allocineID = Pattern.compile(".*film-(\\d+).*");
  private static final Pattern allocinePersonID = Pattern.compile(".*star-(\\d+).*");
  private static final Pattern runtimePattern = Pattern.compile("(\\d+)h (\\d+)mn");

  private enum Tag {

    Release_date(InfoTag.Date_de_sortie),
    Directed_by(InfoTag.Réalisé_par),
    Genre(InfoTag.Genre),
    Nationality(InfoTag.Nationalité),
    User(InfoTag.Spectateurs),
    Original_title(InfoTag.Titre_original),
    Budget(InfoTag.Budget),
    Distributor(InfoTag.Distributeur);
    private InfoTag infotag;

    private Tag(InfoTag infotag) {
      this.infotag = infotag;
    }

    public InfoTag getInfoTag() {
      return infotag;
    }
  }

  public ScreenRushScrapper() {
    super(AvailableLanguages.en);
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
    return Locale.ENGLISH;
  }

  @Override
  protected String getSearchString() {
    return search;
  }

  @Override
  protected String getMoviePageString(IdInfo id) {
    return "/films/film-" + id;
  }

  @Override
  protected String getCastingPageString(IdInfo id) {
    return getMoviePageString(id) + "/casting/";
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
  protected MotionPictureRating getRatingScale() {
    return MotionPictureRating.UK;
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