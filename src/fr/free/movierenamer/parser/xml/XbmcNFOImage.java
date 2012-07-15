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
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Class XbmcNFOImage
 *
 * @author Nicolas Magré
 */
public class XbmcNFOImage extends MrParser<MovieImage> {// TODO A refaire, parser mieux que sa

  private StringBuffer buffer;
  private MovieImage movieImage;
  private boolean movie;
  private MediaImage image;
  private boolean fanart;
  private boolean thumb;

  public XbmcNFOImage() {
    super();
  }

  @Override
  public void startDocument() throws SAXException {
    super.startDocument();
    movieImage = new MovieImage();
    movie = false;
    fanart = false;
    thumb = false;
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    buffer = new StringBuffer();
    if (name.equalsIgnoreCase("movie")) {
      movie = true;
    }
    if (name.equalsIgnoreCase("fanart")) {
      fanart = true;
    }
    if (name.equalsIgnoreCase("thumb")) {
      if (attributes.getValue("preview") != null) {
        thumb = true;
        image = new MediaImage(0, MediaImage.MediaImageType.THUMB);//FIXME A refaire, selon le cas (fanart,tumb,...)
        image.setThumbUrl(attributes.getValue("preview"));
      }
    }
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    if (movie) {
      if (name.equalsIgnoreCase("movie")) {
        movie = false;
      }
    }
    if (name.equalsIgnoreCase("fanart")) {
      fanart = true;
    }
    if (name.equalsIgnoreCase("thumb")) {
      if (thumb) {
        image.setOrigUrl(buffer.toString());
        image.setMidUrl(buffer.toString());
        if (fanart) {
          movieImage.addFanart(image);
        } else {
          movieImage.addThumb(image);
        }
      }
      thumb = false;
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
