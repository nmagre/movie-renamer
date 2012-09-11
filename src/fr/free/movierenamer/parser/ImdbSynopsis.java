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

import fr.free.movierenamer.utils.Utils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.SAXException;

/**
 * Class ImdbSynopsis
 * @author Nicolas Magré
 */
public class ImdbSynopsis extends MrParser<String> {

  private static final Pattern SynopPattern = Pattern.compile("(?:(?:<p class=.plotpar.>)|(?:<div id=.swiki.\\d\\.\\d.>\n))\n((?:[^<].*[\\.\\w>\"]\n){1,})(?:\n</div>)?");
  private final NOSAXException ex = new NOSAXException();
  private String synopsis;

  public ImdbSynopsis() {
    super();
    synopsis = "";
  }

  @Override
  public void startDocument() throws SAXException {
    String synopsisPage = getContent("ISO-8859-1");
    Matcher synop = SynopPattern.matcher(synopsisPage);
    if(synop.find()) {
      synopsis = synop.group(1).replaceAll("<.*?>", "").trim();
      synopsis = Utils.unEscapeXML(synopsis, "ISO-8859-1");
    }
    throw ex;
  }

  @Override
  public String getObject() {
    return synopsis;
  }
}
