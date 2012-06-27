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
package fr.free.movierenamer.parser;

import fr.free.movierenamer.media.MediaID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Nicolas Magré
 */
public class XbmcPassionIDLookupParser {

  private static final String ALLOPATTERN = "a href=\"http://www.allocine.fr/film/fichefilm_gen_cfilm=\\d+.html\" style=\"color: #CCCCCC;\" target=\"_blank\">(\\d+)</a>";
  private static final String IMDBPATTERN = "a href=\" http://us.imdb.com/title/tt\\d+/\" style=\"color: #CCCCCC;\" target=\"_blank\">(\\d+)</a>";

  public MediaID getAlloId(String html) {
    Pattern pattern = Pattern.compile(ALLOPATTERN);
    Matcher idMatcher = pattern.matcher(html);
    if (idMatcher.find()) {
      return new MediaID(idMatcher.group(1), MediaID.ALLOCINEID);
    }
    return null;
  }

  public MediaID getImdbId(String html) {
    Pattern pattern = Pattern.compile(IMDBPATTERN);
    Matcher idMatcher = pattern.matcher(html);
    if (idMatcher.find()) {
      return new MediaID("tt" + idMatcher.group(1), MediaID.IMDBID);
    }
    return null;
  }
}
