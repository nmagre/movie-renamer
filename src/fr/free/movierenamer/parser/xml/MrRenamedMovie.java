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

import fr.free.movierenamer.utils.Renamed;
import fr.free.movierenamer.utils.Utils;
import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class MrRenamedMovie
 * @author Nicolas Magré
 */
public class MrRenamedMovie extends DefaultHandler implements IParser<ArrayList<Renamed>> {

  private StringBuffer buffer;
  private boolean renamedXML;
  private boolean renamedMovie;
  private ArrayList<Renamed> renameds;
  private Renamed renamed;

  public MrRenamedMovie() {
    super();
  }

  @Override
  public void startDocument() throws SAXException {
    super.startDocument();
    renamedXML = false;
    renamedMovie = false;
    renamed = null;
    renameds = new ArrayList<Renamed>();
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    buffer = new StringBuffer();
    if (name.equalsIgnoreCase("Movie_Renamer_Renamed"))
      renamedXML = true;
    if (name.equalsIgnoreCase("renamedMovie")) {
      renamed = new Renamed(Utils.unEscapeXML(attributes.getValue("title"), "UTF-8"));
      renamedMovie = true;
    }

    if (renamedMovie)
      if (name.equalsIgnoreCase("movie")) {
        renamed.setMovieFileSrc(Utils.unEscapeXML(attributes.getValue("src"), "UTF-8"));
        renamed.setMovieFileDest(Utils.unEscapeXML(attributes.getValue("dest"), "UTF-8"));
      }
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    if (name.equalsIgnoreCase("Movie_Renamer_Renamed"))
      renamedXML = false;

    if (renamedXML)
      if (renamedMovie) {
        if (name.equalsIgnoreCase("renamedMovie")) {
          renameds.add(renamed);
          renamed = null;
          renamedMovie = false;
        }
        if (name.equalsIgnoreCase("thumb")) renamed.setThumb(buffer.toString());
        if (name.equalsIgnoreCase("tmdbid")) renamed.setTmDbId(buffer.toString());
        if (name.equalsIgnoreCase("date")) renamed.setDate(buffer.toString());
      }
    buffer = null;
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    String lecture = new String(ch, start, length);
    if (buffer != null)
      buffer.append(lecture);
  }

  @Override
  public ArrayList<Renamed> getObject() {
    return renameds;
  }
}
