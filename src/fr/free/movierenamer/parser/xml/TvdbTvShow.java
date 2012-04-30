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

import fr.free.movierenamer.ui.res.SearchResult;
import fr.free.movierenamer.utils.Settings;
import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Nicolas Magré
 */
public class TvdbTvShow extends DefaultHandler implements IParser<ArrayList<SearchResult>> {

  private StringBuffer buffer;
  private boolean french;
  private ArrayList<SearchResult> results;
  private String currentId;
  private String currentName;
  private String currentThumb;
  private String currentLanguage;
  private boolean series;
  
  public TvdbTvShow(boolean french) {
    super();
    this.french = french;
  }

  @Override
  public void startDocument() throws SAXException {
    super.startDocument();
    results = new ArrayList<SearchResult>();
    series = false;
  }

  @Override
  public void endDocument() throws SAXException {
    super.endDocument();
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    buffer = new StringBuffer();
    if (name.equalsIgnoreCase("series")) series = true;
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    if (name.equalsIgnoreCase("series")){
      if((french && currentLanguage.equals("fr")) || !french){
        String thumb = currentThumb == null ? null : currentThumb.length() > 0 ? Settings.tvdbAPIUrlTvShowBanners + currentThumb:null;
        results.add(new SearchResult(currentName, currentId, "TvShow", thumb));
      }
      currentName = currentId = currentThumb = currentLanguage = "";
      series = false;
    }
    if(series){
      if (name.equalsIgnoreCase("seriesid")) currentId = buffer.toString();
      if (name.equalsIgnoreCase("language")) currentLanguage = buffer.toString();
      if (name.equalsIgnoreCase("SeriesName")) currentName = buffer.toString();
      if (name.equalsIgnoreCase("banner")) currentThumb = buffer.toString();
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
  public ArrayList<SearchResult> getObject() {
    return results;
  }
}
