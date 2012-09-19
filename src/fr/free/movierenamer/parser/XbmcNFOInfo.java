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
import fr.free.movierenamer.media.MediaPerson;
import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Settings;
import java.util.logging.Level;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Class XbmcNFOInfo
 *
 * @author Nicolas Magré
 */
public class XbmcNFOInfo extends MrParser<MovieInfo> {

  private StringBuffer buffer;
  private MovieInfo movieInfo;
  private MediaPerson currentActor;
  boolean movie;

  public XbmcNFOInfo() {
    super();
  }

  @Override
  public void startDocument() throws SAXException {
    super.startDocument();
    movieInfo = new MovieInfo();
    movie = false;
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    buffer = new StringBuffer();
    if (name.equalsIgnoreCase("movie")) {
      movie = true;
    }
    if (name.equalsIgnoreCase("actor")) {
      currentActor = new MediaPerson(MediaPerson.ACTOR);
    }
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    if (movie) {
      if (name.equalsIgnoreCase("movie")) {
        movie = false;
      } else if (name.equalsIgnoreCase("title")) {
        movieInfo.setTitle(buffer.toString());
      } else if (name.equalsIgnoreCase("originaltitle")) {
        movieInfo.setOriginalTitle(buffer.toString());
      } else if (name.equalsIgnoreCase("rating")) {
        movieInfo.setRating(buffer.toString());
      } else if (name.equalsIgnoreCase("year")) {
        movieInfo.setYear(buffer.toString());
      } else if (name.equalsIgnoreCase("votes")) {
        movieInfo.setVotes(buffer.toString());
      } else if (name.equalsIgnoreCase("outline")) {
        movieInfo.setOutline(buffer.toString());
      } else if (name.equalsIgnoreCase("plot")) {
        movieInfo.setSynopsis(buffer.toString());
      } else if (name.equalsIgnoreCase("tagline")) {
        movieInfo.setTagline(buffer.toString());
      } else if (name.equalsIgnoreCase("runtime")) {
        movieInfo.setRuntime(buffer.toString());
      } else if (name.equalsIgnoreCase("mpaa")) {
        movieInfo.setMpaa(buffer.toString());
      } else if (name.equalsIgnoreCase("playcount")) {
      } else if (name.equalsIgnoreCase("lastplayed")) {
      } else if (name.equalsIgnoreCase("id")) {
        movieInfo.addId(new MediaID(buffer.toString(), buffer.toString().startsWith("tt") ? MediaID.MediaIdType.IMDBID:MediaID.MediaIdType.TMDBID));
      } else if (name.equalsIgnoreCase("genre")) {
        movieInfo.addGenre(buffer.toString());
      } else if (name.equalsIgnoreCase("country")) {
        movieInfo.addCountry(buffer.toString());
      } else if (name.equalsIgnoreCase("credits")) {
        movieInfo.addPerson(new MediaPerson(buffer.toString(), "", MediaPerson.WRITER));
      } else if (name.equalsIgnoreCase("director")) {
        movieInfo.addPerson(new MediaPerson(buffer.toString(), "", MediaPerson.DIRECTOR));
      } else if (name.equalsIgnoreCase("premiered")) {
      } else if (name.equalsIgnoreCase("status")) {
      } else if (name.equalsIgnoreCase("code")) {
      } else if (name.equalsIgnoreCase("aired")) {
      } else if (name.equalsIgnoreCase("studio")) {
        movieInfo.addStudio(buffer.toString());
      } else if (name.equalsIgnoreCase("trailer")) {
        movieInfo.setTrailer(buffer.toString());
      } else if (name.equalsIgnoreCase("actor")) {
        movieInfo.addPerson(currentActor);
        currentActor = null;
      } else if (name.equalsIgnoreCase("thumb")) {
        if (currentActor != null) {
          currentActor.setThumb(buffer.toString());
        }
      } else if (name.equalsIgnoreCase("name")) {
        if (currentActor != null) {
          currentActor.setName(buffer.toString());
        }
      } else if (name.equalsIgnoreCase("sorttitle")) {
        movieInfo.setSortTitle(buffer.toString());
//      } else if (name.equalsIgnoreCase("set")) {
//        movieInfo.addSet(buffer.toString());
      } else if (name.equalsIgnoreCase("role")) {
        try {
          currentActor.addRole(buffer.toString());
        } catch (ActionNotValidException ex) {
          Settings.LOGGER.log(Level.SEVERE, null, ex);
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
  public MovieInfo getObject() {
    return movieInfo;
  }
}
