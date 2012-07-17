/*
 * movie-renamer
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
 * Class AllocineImage
 * 
 * @author QUÉMÉNEUR Simon
 */
public class AllocineImage extends MrParser<MovieImage> {
  private StringBuffer buffer;
  private final MovieImage movieImage;
  private ArrayList<MediaImage> thumbs;
  private ArrayList<MediaImage> fanarts;
  private boolean movie;
  private boolean mediaPicture;

  public AllocineImage() {
    super();
    movieImage = new MovieImage();
    thumbs = new ArrayList<MediaImage>();
    fanarts = new ArrayList<MediaImage>();
  }

  @Override
  public void startDocument() throws SAXException {
    movie = false;
    mediaPicture = false;
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    buffer = new StringBuffer();
    if (name.equalsIgnoreCase("movie")) {
      movie = true;
    }
    if (movie) {
      if (name.equalsIgnoreCase("poster")) {
        MediaImage movieThumb = new MediaImage(0, MediaImage.MediaImageType.THUMB);
        movieThumb.setThumbUrl(attributes.getValue("href"));
        movieThumb.setMidUrl(attributes.getValue("href"));
        movieThumb.setOrigUrl(attributes.getValue("href"));
        thumbs.add(movieThumb);
      }
      if (name.equalsIgnoreCase("media")) {
        mediaPicture = attributes.getValue("class").equals("picture");
      }
      if (mediaPicture) {
        if (name.equalsIgnoreCase("thumbnail")) {
          if (!thumbs.get(0).getThumbUrl().equals(attributes.getValue("href"))) {
            MediaImage movieFanart = new MediaImage(0, MediaImage.MediaImageType.FANART);
            movieFanart.setThumbUrl(attributes.getValue("href"));
            movieFanart.setMidUrl(attributes.getValue("href"));
            fanarts.add(movieFanart);
          }
        }
      }

      if (name.equalsIgnoreCase("media")) {
        mediaPicture = attributes.getValue("class").equals("picture");
      }
      if (mediaPicture) {
        if (name.equalsIgnoreCase("thumbnail")) {
          // if (!movieinfo.getThumbs().get(0).getThumbUrl().equals(attributes.getValue("href"))) {
          MediaImage movieFanart = new MediaImage(0, MediaImage.MediaImageType.FANART);
          movieFanart.setThumbUrl(attributes.getValue("href"));
          movieFanart.setMidUrl(attributes.getValue("href"));
          fanarts.add(movieFanart);
        }
      }
    }
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    if (name.equalsIgnoreCase("movie")) {
      movie = false;
    }
    if (name.equalsIgnoreCase("media")) {
      mediaPicture = false;
    }
    buffer = null;
  }

  @Override
  public void endDocument() throws SAXException {
    movieImage.setThumbs(thumbs);
    movieImage.setFanarts(fanarts);
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    String lecture = new String(ch, start, length);
    if (buffer != null) {
      buffer.append(lecture);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.free.movierenamer.parser.xml.MrParser#getObject()
   */
  @Override
  public MovieImage getObject() {
    return movieImage;
  }

}
