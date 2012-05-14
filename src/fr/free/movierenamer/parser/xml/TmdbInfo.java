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

import fr.free.movierenamer.media.MediaPerson;
import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Utils;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class TheMovieDbInfo
 *
 * @author Nicolas Magré
 */
public class TmdbInfo extends DefaultHandler implements IParser<MovieInfo> {

  private StringBuffer buffer;
  private boolean imdbAPIXML;
  private MovieInfo movieinfo;

  public TmdbInfo() {
    super();
  }

  @Override
  public void startDocument() throws SAXException {
    super.startDocument();
    imdbAPIXML = false;
    movieinfo = new MovieInfo();
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    buffer = new StringBuffer();
    if (name.equalsIgnoreCase("OpenSearchDescription")) {
      imdbAPIXML = true;
    }
    if (name.equalsIgnoreCase("country")) {
      movieinfo.addCountry(attributes.getValue("name"));
    }
    if (name.equalsIgnoreCase("person")) {
      String personnJob = attributes.getValue("job");

      if (personnJob.equals("Director") || personnJob.equals("Actor") || personnJob.equals("Writer")) {
        try {
          MediaPerson actor;
          actor = movieinfo.getActorByName(attributes.getValue("name"));
          int job = MediaPerson.ACTOR;
          if (personnJob.equals("Director")) {
            job = MediaPerson.DIRECTOR;
          }
          if (personnJob.equals("Writer")) {
            job = MediaPerson.WRITER;
          }
          if (actor == null) {
            actor = new MediaPerson(attributes.getValue("name"), attributes.getValue("thumb"), job);
            actor.addRole(attributes.getValue("character"));
            movieinfo.addActor(actor);
          } else {
            movieinfo.addRole(actor.getName(), attributes.getValue("character"));
          }
        } catch (ActionNotValidException ex) {
          Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    if (name.equalsIgnoreCase("category")) {
      if (attributes.getValue("type").equals("genre")) {
        movieinfo.addGenre(attributes.getValue("name"));
      }
    }
    if (name.equalsIgnoreCase("studio")) {
      movieinfo.addStudio(attributes.getValue("name"));
    }

  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    if (name.equalsIgnoreCase("OpenSearchDescription")) {
      imdbAPIXML = false;
    }

    if (imdbAPIXML) {
      if (name.equalsIgnoreCase("trailer")) {
        movieinfo.setTrailer(buffer.toString());
      }
      if (name.equalsIgnoreCase("overview")) {
        movieinfo.setSynopsis(buffer.toString());
      }
      if (name.equalsIgnoreCase("original_name")) {
        movieinfo.setOrigTitle(buffer.toString());
      }
      if (name.equalsIgnoreCase("tagline")) {
        movieinfo.setTagline(buffer.toString());
      }
      if (name.equalsIgnoreCase("rating")) {
        if (Utils.isDigit(buffer.toString())) {
          movieinfo.setRating(buffer.toString());
        }
      }
      if (name.equalsIgnoreCase("runtime")) {
        if (Utils.isDigit(buffer.toString())) {
          movieinfo.setRuntime(buffer.toString());
        }
      }
      if (name.equalsIgnoreCase("votes")) {
        movieinfo.setVotes(buffer.toString());
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
    return movieinfo;
  }
}
