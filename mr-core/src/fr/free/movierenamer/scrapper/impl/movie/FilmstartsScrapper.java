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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class FilmstartsScrapper
 * @author Nicolas Magré
 */
public class FilmstartsScrapper extends AlloGroupScrapper {

  private static final String host = "www.filmstarts.de";
  private static final String imageHost = "images.allocine.fr";
  private static final String name = "Filmstarts";
  private static final String search = "suche";
  private static final Pattern allocineID = Pattern.compile(".*kritiken/(\\d+)-?.*");
  private static final Pattern allocineLongID = Pattern.compile(".*kritiken/(\\d+.*)[\\\\\\.].*");
  private static final Pattern allocinePersonID = Pattern.compile(".*personen/(\\d+)-.*");
  private static final Pattern runtimePattern = Pattern.compile("(\\d+) Std. (\\d+) Min.");

  private static enum Tag {

    Kinostart(InfoTag.Date_de_sortie),
    Regie(InfoTag.Réalisé_par),
    Genre(InfoTag.Genre),
    Nationalität(InfoTag.Nationalité),
    User(InfoTag.Spectateurs),
    Originaltitel(InfoTag.Titre_original),
    Budget(InfoTag.Budget),
    Verleiher(InfoTag.Distributeur);
    private InfoTag infotag;

    private Tag(InfoTag infotag) {
      this.infotag = infotag;
    }

    public InfoTag getInfoTag() {
      return infotag;
    }
    
    public static void test() {
      
    }
  }

  public FilmstartsScrapper() {
    super(LocaleUtils.AvailableLanguages.de);
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
    return Locale.GERMAN;
  }

  @Override
  protected String getSearchString() {
    return search;
  }

  @Override
  protected String getMoviePageString(IdInfo id) {
    return "/kritiken/" + (id.getLongId() != null ? id.getLongId() : id) + ".html";
  }

  @Override
  protected String getCastingPageString(IdInfo id) {
    return "/kritiken/" + (id.getLongId() != null ? id.getLongId() : id) + "/castcrew.html";
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
  protected String getLongId(String str) {
    Matcher matcher = allocineLongID.matcher(str);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }

  @Override
  protected MotionPictureRating getRatingScale() {
    return MotionPictureRating.GERMANY;
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