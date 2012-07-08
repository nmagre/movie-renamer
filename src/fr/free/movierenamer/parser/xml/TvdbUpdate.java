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
import fr.free.movierenamer.utils.SearchResult;
import fr.free.movierenamer.utils.Settings;
import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Nicolas Magré
 */
public class TvdbUpdate extends DefaultHandler implements IParser<ArrayList<MediaID>> {

  private StringBuffer buffer;
  private ArrayList<MediaID> mediaIds;
  private boolean update;

  @Override
  public void startDocument() throws SAXException {
    super.startDocument();
    mediaIds = new ArrayList<MediaID>();
    update = false;
  }

  @Override
  public void endDocument() throws SAXException {
    super.endDocument();
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    buffer = new StringBuffer();
    if(name.equalsIgnoreCase("Items")){
      update = true;
    }
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    if(name.equalsIgnoreCase("Items")){
      update = false;
    }
    if(update){
      if(name.equalsIgnoreCase("Series")){
        mediaIds.add(new MediaID(buffer.toString(), MediaID.TVDBID));
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
  public ArrayList<MediaID> getObject() {
    return mediaIds;
  }
}
