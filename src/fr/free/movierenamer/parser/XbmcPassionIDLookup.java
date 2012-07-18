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
package fr.free.movierenamer.parser.xml;

import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.parser.xml.MrParser;
import fr.free.movierenamer.parser.xml.NOSAXException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.SAXException;

/**
 * Class XbmcPassionIDLookup
 *
 * @author Nicolas Magré
 */
public class XbmcPassionIDLookup extends MrParser<MediaID> {

  private static final Pattern ALLOPATTERN = Pattern.compile("a href=\"http://www.allocine.fr/film/fichefilm_gen_cfilm=\\d+.html\" style=\"color: #CCCCCC;\" target=\"_blank\">(\\d+)</a>");
  private static final Pattern IMDBPATTERN = Pattern.compile("a href=\" http://us.imdb.com/title/tt\\d+/\" style=\"color: #CCCCCC;\" target=\"_blank\">(\\d+)</a>");
  /**
   * The exception to bypass parsing file ;)
   */
  private final NOSAXException ex = new NOSAXException();
  private MediaID id;
  private MediaID lookupId;

  public XbmcPassionIDLookup(MediaID id) {
    this.id = id;
  }

  @Override
  public void startDocument() throws SAXException {
    lookupId = null;
    switch (id.getType()) {
      case IMDBID:
        lookupId = getAlloId(getContent("UTF-8"));
        break;
      case ALLOCINEID:
        lookupId = getImdbId(getContent("UTF-8"));
        break;
      default:
        break;
    }
    throw ex;
  }

  public MediaID getAlloId(String html) {
    Matcher idMatcher = ALLOPATTERN.matcher(html);
    if (idMatcher.find()) {
      return new MediaID(idMatcher.group(1), MediaID.MediaIdType.ALLOCINEID);
    }
    return null;
  }

  public MediaID getImdbId(String html) {
    Matcher idMatcher = IMDBPATTERN.matcher(html);
    if (idMatcher.find()) {
      return new MediaID(String.format("tt%07d", Integer.parseInt(idMatcher.group(1))), MediaID.MediaIdType.IMDBID);
    }
    return null;
  }

  @Override
  public MediaID getObject() {
    return lookupId;
  }
}