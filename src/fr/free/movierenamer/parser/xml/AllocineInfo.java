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
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.util.logging.Level;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Nicolas Magré
 */
public class AllocineInfo extends DefaultHandler implements IParser<MovieInfo> {

  private static final String ACTORCODE = "8001";
  private static final String DIRECTORCODE = "8002";
  private StringBuffer buffer;
  private boolean movie;
  private boolean casting;
  private boolean mperson;
  private MovieInfo movieinfo;
  private MediaPerson person;
  private String personName;
  private int count;

  public AllocineInfo() {
    super();
    movieinfo = new MovieInfo();
    count = 0;
  }

  @Override
  public void startDocument() throws SAXException {
    super.startDocument();
    movie = false;
    casting = false;
    mperson = false;
    person = null;
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    buffer = new StringBuffer();
    if (name.equalsIgnoreCase("movie")) {
      movie = true;
    }
    if (movie) {
      if (name.equalsIgnoreCase("trailer")) {
        movieinfo.setTrailer(attributes.getValue("href"));
      }
      if (name.equalsIgnoreCase("casting")) {
        casting = true;
      }
      if (casting) {
        if (name.equalsIgnoreCase("person")) {
          mperson = true;
        }
        if (name.equalsIgnoreCase("castMember")) {
          person = null;
          personName = "";
        }
        if (name.equalsIgnoreCase("activity")) {
          String jobCode = attributes.getValue("code");
          if (jobCode.equals(ACTORCODE) || jobCode.equals(DIRECTORCODE)) {
            person = new MediaPerson(jobCode.equals(ACTORCODE) ? MediaPerson.ACTOR : MediaPerson.DIRECTOR);
            person.setName(personName);
          } else {
            person = null;
          }
        }
        if (person != null) {
          if (name.equalsIgnoreCase("picture")) {
            person.setThumb(attributes.getValue("href"));
          }
        }
      }
    }
    count++;
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    if (name.equalsIgnoreCase("movie")) {
      movie = false;
      if (movieinfo.getTitle().equals("")) {
        movieinfo.setTitle(movieinfo.getOrigTitle());
      }
    }
    if (name.equalsIgnoreCase("casting")) {
      casting = false;
    }

    if (movie) {
      if (name.equalsIgnoreCase("originalTitle")) {
        movieinfo.setOrigTitle(buffer.toString());
      }
      if (name.equalsIgnoreCase("title")) {
        if (count == 4) {
          movieinfo.setTitle(buffer.toString());
        }
      }
      if (name.equalsIgnoreCase("productionYear")) {
        movieinfo.setYear(buffer.toString());
      }
      if (name.equalsIgnoreCase("nationality")) {
        movieinfo.addCountry(buffer.toString());
      }
      if (name.equalsIgnoreCase("genre")) {
        movieinfo.addGenre(buffer.toString());
      }
      if (name.equalsIgnoreCase("runtime")) {
        if (Utils.isDigit(buffer.toString())) {
          int runtime = Integer.parseInt(buffer.toString());//Get runtime in seconds
          runtime /= 60;
          movieinfo.setRuntime("" + runtime);
        }
      }
      if (name.equalsIgnoreCase("synopsis")) {
        movieinfo.setSynopsis(buffer.toString());
      }
      if (name.equalsIgnoreCase("certificate")) {
        movieinfo.setMpaa(buffer.toString());
      }

      if (casting) {
        if (name.equalsIgnoreCase("person")) {
          mperson = false;
        }
        if (mperson) {
          if (name.equalsIgnoreCase("name")) {
            personName = buffer.toString();
          }
        }
        if (name.equalsIgnoreCase("role")) {
          if (person != null && person.getJob() == MediaPerson.ACTOR) {
            try {
              person.addRole(buffer.toString());
            } catch (ActionNotValidException ex) {
              Settings.LOGGER.log(Level.SEVERE, null, ex);
            }
          }
        }
        if (name.equalsIgnoreCase("castMember")) {
          if (person != null) {
            if (person.getJob() == MediaPerson.DIRECTOR) {
              movieinfo.addDirector(person);
            }
            if (person.getJob() == MediaPerson.ACTOR) {
              movieinfo.addActor(person);
            }
          }
          person = null;
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
    return movieinfo;
  }
}
