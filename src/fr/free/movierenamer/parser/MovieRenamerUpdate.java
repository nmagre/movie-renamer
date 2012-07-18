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

import fr.free.movierenamer.utils.Update;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * class MovieRenamerUpdate
 * @author Nicolas Magré
 */
public class MovieRenamerUpdate extends MrParser<Update> {

  private StringBuffer buffer;
  private Update update;
  private boolean updateXML;
  private boolean desc;

  public MovieRenamerUpdate(String version) {
    super();
    if (version.contains("_")) {
      version = version.substring(0, version.indexOf("_"));
    }
    update = new Update(version);
    updateXML = false;
    desc = false;
  }

  @Override
  public void startDocument() throws SAXException {
    super.startDocument();
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    buffer = new StringBuffer();
    if (name.equalsIgnoreCase("update")) {
      updateXML = true;
    }
    if (name.equalsIgnoreCase("description")) {
      desc = true;
    }
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    if (name.equalsIgnoreCase("update")) {
      updateXML = false;
    }

    if (updateXML) {
      if (name.equalsIgnoreCase("description")) {
        desc = false;
      }
      if (name.equalsIgnoreCase("version")) {
        update.setVersion(buffer.toString());
      }
      if (name.equalsIgnoreCase("url")) {
        update.setUrl(buffer.toString());
      }
      if (desc) {
        if (name.equalsIgnoreCase("en")) {
          update.setDescEN(buffer.toString());
        }
        if (name.equalsIgnoreCase("fr")) {
          update.setDescFR(buffer.toString());
        }
      }
    }
    buffer = null;
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    String lecture = new String(ch, start, length);
    if (buffer != null) {
      buffer.append(lecture);
    }
  }

  @Override
  public Update getObject() {
    return update;
  }
}
