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

import fr.free.movierenamer.media.MediaImage;
import fr.free.movierenamer.media.movie.MovieImage;
import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Class TheMovieDbImage
 *
 * @author Nicolas Magré
 */
public class TmdbImage extends MrParser<MovieImage> {

  private ArrayList<MediaImage> thumbs;
  private ArrayList<MediaImage> fanarts;
  private StringBuffer buffer;
  private boolean imdbAPIXML;
  private boolean images;
  private MediaImage currentMovieImage;
  private String lastAttribute;
  private String currentId;
  private MovieImage movieImage;

  public TmdbImage() {
    super();
  }

  @Override
  public void startDocument() throws SAXException {
    super.startDocument();
    imdbAPIXML = false;
    images = false;
    currentMovieImage = null;
    lastAttribute = "";
    currentId = "";
    thumbs = new ArrayList<MediaImage>();
    fanarts = new ArrayList<MediaImage>();
    movieImage = new MovieImage();
  }

  @Override
  public void endDocument() throws SAXException {
    super.endDocument();
    movieImage.setThumbs(thumbs);
    movieImage.setFanarts(fanarts);
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    buffer = new StringBuffer();
    if (name.equalsIgnoreCase("OpenSearchDescription")) {
      imdbAPIXML = true;
    }
    if (name.equalsIgnoreCase("images")) {
      images = true;
    }

    if (imdbAPIXML) {
      if (images) {
        if (name.equalsIgnoreCase("image")) {
          if (!currentId.equals(attributes.getValue("id"))) {
            if (currentMovieImage != null) {
              if (lastAttribute.equals("poster")) {
                thumbs.add(currentMovieImage);
              } else {
                fanarts.add(currentMovieImage);
              }
            }
            currentId = attributes.getValue("id");
            currentMovieImage = new MediaImage(0, attributes.getValue("type").equals("poster") ? MediaImage.MediaImageType.THUMB : MediaImage.MediaImageType.FANART);
            lastAttribute = attributes.getValue("type");
          }
          if (attributes.getValue("size").equals("original")) {
            currentMovieImage.setOrigUrl(attributes.getValue("url").replace(".png", ".jpg"));// API bug png is jpg on server
          }
          if (attributes.getValue("size").equals("thumb")) {
            currentMovieImage.setThumbUrl(attributes.getValue("url").replace(".png", ".jpg"));
          }
          if (attributes.getValue("size").equals("mid") || attributes.getValue("type").equals("poster")) {
            currentMovieImage.setMidUrl(attributes.getValue("url").replace(".png", ".jpg"));
          }
        }
      }
    }
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    if (name.equalsIgnoreCase("OpenSearchDescription")) {
      imdbAPIXML = false;
    }
    if (name.equalsIgnoreCase("images")) {
      images = false;
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
  public MovieImage getObject() {
    return movieImage;
  }
}