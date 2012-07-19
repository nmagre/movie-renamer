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
import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Class TmdbSearch
 * 
 * @author Nicolas Magré
 */
public class TmdbSearch extends MrParser<ArrayList<SearchResult>> {

  private StringBuffer buffer;
  private ArrayList<SearchResult> results;
  private boolean movies;
  private boolean movie;
  private String currentId;
  private String currentName;
  private String currentYear;
  private String currentThumb;
  
  public TmdbSearch(){
    super();
  }

  @Override
  public void startDocument() throws SAXException {
    super.startDocument();
    results = new ArrayList<SearchResult>();
    currentThumb = currentYear = "";
  }

  @Override
  public void endDocument() throws SAXException {
    super.endDocument();
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    buffer = new StringBuffer();
    if (name.equalsIgnoreCase("movies")) {
      movies = true;
    }
    if (name.equalsIgnoreCase("movie")) {
      movie = true;
    }
    if (movies) {
      if (movie) {
        if (name.equalsIgnoreCase("image")) {
          if(currentThumb.equals("")){
            if(attributes.getValue("type").equals("poster")){
              if(attributes.getValue("size").equals("thumb")){
                currentThumb = attributes.getValue("url");
              }
            }
          }
        }
      }
    }
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    if (name.equalsIgnoreCase("movies")) {
      movies = false;
    }
    if (name.equalsIgnoreCase("movie")) {
      movie = false;
      results.add(new SearchResult(currentName, new MediaID(currentId, MediaID.MediaIdType.TMDBID), SearchResult.SearchResultType.NONE, currentYear, currentThumb));
      currentName = currentId = currentThumb = "";
    }
    if (movies) {
      if (movie) {
        if (name.equalsIgnoreCase("id")) {
          currentId = buffer.toString();
        }
        if (name.equalsIgnoreCase("name")) {
          currentName = buffer.toString();
        }
        if (name.equalsIgnoreCase("released")) {
          if(buffer.toString().contains("-")) {
            currentYear = " (" + buffer.toString().substring(0, buffer.toString().indexOf("-")) + ")";
          }
          else if(buffer.toString().length() > 0){
            currentYear += " (" + buffer.toString() + ")";
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
  public ArrayList<SearchResult> getObject() {
    return results;
  }
}
