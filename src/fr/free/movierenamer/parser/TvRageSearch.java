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
import fr.free.movierenamer.utils.SearchResult;
import fr.free.movierenamer.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Class TvRageSearch
 *
 * @author Nicolas Magré
 */
public class TvRageSearch extends MrParser<List<SearchResult>> {

  private StringBuffer buffer;
  private List<SearchResult> results;
  private boolean result;
  private boolean show;
  private String currentId;
  private String currentName;
  private String currentYear;
  private static final String THUMB = "http://images.tvrage.com/shows/DIR/ID.jpg";

  public TvRageSearch() {
    super();
  }

  @Override
  public void startDocument() throws SAXException {
    super.startDocument();
    results = new ArrayList<SearchResult>();
    result = show = false;
  }

  @Override
  public void endDocument() throws SAXException {
    super.endDocument();
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    buffer = new StringBuffer();
    if (name.equalsIgnoreCase("Results")) {
      result = true;
      currentName = currentId = currentYear = "";
    }
    if (result) {
      if (name.equalsIgnoreCase("show")) {
        show = true;
      }
    }
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    if (name.equalsIgnoreCase("Results")) {
      result = false;
    }
    if (name.equalsIgnoreCase("show")) {
      show = false;
      int dirId = -1;
      if(currentId.length() > 3){
        String tmp = currentId.substring(0, currentId.length() - 3);
        if(Utils.isDigit(tmp)){
          dirId = Integer.parseInt(tmp) + 1;
        }
      }
      else {
        dirId = 1;
      }
      
      String thumb = "";
      if(dirId != -1) {
        thumb = THUMB.replace("DIR", ""+dirId).replace("ID", currentId);
      }
      results.add(new SearchResult(currentName, new MediaID(currentId, MediaID.MediaIdType.TVRAGETVID), SearchResult.SearchResultType.NONE, currentYear, thumb));
    }
    if (result) {
      if (show) {
        if (name.equalsIgnoreCase("showid")) {
          currentId = buffer.toString();
        }
        if (name.equalsIgnoreCase("name")) {
          currentName = buffer.toString();
        }
        if (name.equalsIgnoreCase("started")) {
          currentYear = " (" + buffer.toString() + ")";
        }
        if (name.equalsIgnoreCase("ended")) {
          if (!buffer.toString().equals("0")) {
            currentYear += " - (" + buffer.toString() + ")";
          }
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
  public List<SearchResult> getObject() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
