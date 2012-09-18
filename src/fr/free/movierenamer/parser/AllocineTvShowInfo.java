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

import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.util.logging.Level;

import fr.free.movierenamer.media.movie.MovieInfo;

import fr.free.movierenamer.media.MediaImage;
import fr.free.movierenamer.media.MediaPerson;

import fr.free.movierenamer.media.MediaID;

import java.util.ArrayList;

import fr.free.movierenamer.media.tvshow.TvShowSeason;
import java.util.List;

import fr.free.movierenamer.media.tvshow.TvShowInfo;

import fr.free.movierenamer.media.tvshow.TvShowEpisode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Class AllocineTvShowInfo
 * 
 * @author Nicolas Magré
 */
public class AllocineTvShowInfo extends MrParser<TvShowInfo> {

  private static final String ACTORCODE = "8001";
  private static final String DIRECTORCODE = "8002";

  private StringBuffer buffer;
  private final TvShowInfo tvshowInfo;
  private MediaPerson person;
  private String personName;
  private List<TvShowSeason> seasons;
  private TvShowSeason currentSeason;
  private boolean tvseries, seasonList, statistics, nationalityList, genreList, casting, mperson, thumb, fanart, mediaPicture;

  public AllocineTvShowInfo() {
    super();
    tvshowInfo = new TvShowInfo();
    seasons = new ArrayList<TvShowSeason>();
  }

  @Override
  public void startDocument() throws SAXException {
    super.startDocument();
    tvseries = false;
    seasonList = false;
    statistics = false;
    nationalityList = false;
    genreList = false;
    casting = false;
    mperson = false;
    thumb = false;
    fanart = false;
    mediaPicture = false;
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    buffer = new StringBuffer();
    if (name.equalsIgnoreCase("tvseries")) {
      tvseries = true;
    }
    if (tvseries) {
      if (name.equalsIgnoreCase("seasonList")) {
        seasonList = true;
      }
      if (seasonList) {
        if (name.equalsIgnoreCase("season")) {
          currentSeason = new TvShowSeason(new MediaID(attributes.getValue("code"), MediaID.MediaIdType.ALLOCINESEASONID));
        }
        if (name.equalsIgnoreCase("statistics")) {
          statistics = true;
        }
      }
      if (name.equalsIgnoreCase("trailer")) {
        tvshowInfo.setTrailer(attributes.getValue("href"));
      }
      if (name.equalsIgnoreCase("casting")) {
        casting = true;
      }

      if (name.equalsIgnoreCase("poster")) {
        tvshowInfo.setPoster(attributes.getValue("href"));
        // MediaImage movieThumb = new MediaImage(0, MediaImage.MediaImageType.THUMB);
        // for (MediaImage.MediaImageSize size : MediaImage.MediaImageSize.values()) {
        // movieThumb.setUrl(attributes.getValue("href"), size);
        // }
        // tvshowInfo.addThumb(movieThumb);
      }
      if (name.equalsIgnoreCase("media")) {
        mediaPicture = attributes.getValue("class").equals("picture");
      }
      if (name.equalsIgnoreCase("type")) {
        thumb = attributes.getValue("code").equals("31001");
        fanart = attributes.getValue("code").equals("31006");
      }
      if (mediaPicture) {
        // if (name.equalsIgnoreCase("thumbnail")) {
        // if (!tvshowInfo.getThumbs().get(0).getUrl(MediaImage.MediaImageSize.THUMB).equals(attributes.getValue("href"))) {
        // MediaImage movieimg = new MediaImage(0, thumb ? MediaImage.MediaImageType.THUMB : MediaImage.MediaImageType.FANART);
        // for (MediaImage.MediaImageSize size : MediaImage.MediaImageSize.values()) {
        // movieimg.setUrl(attributes.getValue("href"), size);
        // }
        // if (thumb) {
        // tvshowInfo.addThumb(movieimg);
        // } else {
        // tvshowInfo.addFanart(movieimg);
        // }
        // }
        // }
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
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    if (name.equalsIgnoreCase("tvseries")) {
      tvseries = false;
      if (tvshowInfo.getTitle().equals("")) {
        tvshowInfo.setTitle(tvshowInfo.getOriginalTitle());
      }
    }
    if (name.equalsIgnoreCase("casting")) {
      casting = false;
    }
    if (name.equalsIgnoreCase("media")) {
      mediaPicture = false;
    }
    if (name.equalsIgnoreCase("type")) {
      thumb = false;
      fanart = false;
    }

    if (tvseries) {
      if (name.equalsIgnoreCase("originalTitle")) {
        tvshowInfo.setOriginalTitle(buffer.toString());
        tvshowInfo.setTitle(buffer.toString());
      }
      if (name.equalsIgnoreCase("seasonList")) {
        seasonList = false;
      }
      if (seasonList) {
        if (name.equalsIgnoreCase("statistics")) {
          statistics = false;
        }
        if (statistics) {
          if (name.equalsIgnoreCase("userRating")) {
            currentSeason.setRating(buffer.toString());
          }
        }
        if (name.equalsIgnoreCase("seasonNumber")) {
          currentSeason.setNum(Integer.parseInt(buffer.toString()));
        }
        if (name.equalsIgnoreCase("episodeCount")) {
          currentSeason.setEpisodeCount(Integer.parseInt(buffer.toString()));
        }
        if (name.equalsIgnoreCase("season")) {
          seasons.add(currentSeason);
        }
      }
      if (name.equalsIgnoreCase("productionYear")) {
        tvshowInfo.setYear(buffer.toString());
      }
      if (name.equalsIgnoreCase("nationality")) {
        tvshowInfo.addCountry(buffer.toString());
      }
      if (name.equalsIgnoreCase("genre")) {
        tvshowInfo.addGenre(buffer.toString());
      }
      if (name.equalsIgnoreCase("formatTime")) {
        if (Utils.isDigit(buffer.toString())) {
          int runtime = Integer.parseInt(buffer.toString());// Get runtime in minutes
          tvshowInfo.setRuntime("" + runtime);
        }
      }
      if (name.equalsIgnoreCase("productionStatus")) {
        tvshowInfo.setProductionStatus(buffer.toString());
      }
      if (name.equalsIgnoreCase("synopsis")) {
        tvshowInfo.setSynopsis(buffer.toString());
      }
      // if (name.equalsIgnoreCase("synopsisShort")) {
      // tvshowInfo.setOutline(buffer.toString());
      // }
      // if (name.equalsIgnoreCase("certificate")) {
      // tvshowInfo.setMpaa(buffer.toString());
      // }
      if (name.equalsIgnoreCase("userRating")) {
        if (Utils.isDigit(buffer.toString())) {
          float rating = Float.parseFloat(buffer.toString());
          rating *= 2;// Allocine rating is on 5
          tvshowInfo.setRating("" + rating);
        }
      }
      if (name.equalsIgnoreCase("userReviewCount")) {
        tvshowInfo.setVotes(buffer.toString());
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
            if (person.getJob() == MediaPerson.DIRECTOR || person.getJob() == MediaPerson.ACTOR) {
              tvshowInfo.addPerson(person);
            }
          }
          person = null;
        }
      }
    }
    buffer = null;

    // if (name.equalsIgnoreCase("tvseries")) {
    // tvseries = false;
    // }
    // if (tvseries) {
    // if (name.equalsIgnoreCase("originalTitle")) {
    // tvshowInfo.setOriginalTitle(buffer.toString());
    // tvshowInfo.setTitle(buffer.toString());
    // }
    // if (name.equalsIgnoreCase("seasonList")) {
    // seasonList = false;
    // }
    // if (name.equalsIgnoreCase("nationalityList")) {
    // nationalityList = false;
    // }
    // if (name.equalsIgnoreCase("genreList")) {
    // genreList = false;
    // }
    // if (name.equalsIgnoreCase("genreList")) {
    // casting = false;
    // }
    // if (seasonList) {
    // if (name.equalsIgnoreCase("statistics")) {
    // statistics = false;
    // }
    // if (statistics) {
    // if (name.equalsIgnoreCase("userRating")) {
    // currentSeason.setRating(buffer.toString());
    // }
    // }
    // if (name.equalsIgnoreCase("seasonNumber")) {
    // currentSeason.setNum(Integer.parseInt(buffer.toString()));
    // }
    // if (name.equalsIgnoreCase("episodeCount")) {
    // currentSeason.setEpisodeCount(Integer.parseInt(buffer.toString()));
    // }
    // if (name.equalsIgnoreCase("season")) {
    // seasons.add(currentSeason);
    // }
    // } else if (nationalityList) {
    // if (name.equalsIgnoreCase("nationality")) {
    // tvshowInfo.addCountry(buffer.toString());
    // }
    // } else if (genreList) {
    // if (name.equalsIgnoreCase("genre")) {
    // tvshowInfo.addGenre(buffer.toString());
    // }
    // } else {
    // if (name.equalsIgnoreCase("synopsis")) {
    // tvshowInfo.setSynopsis(buffer.toString());
    // }
    // if (name.equalsIgnoreCase("userRating")) {
    // tvshowInfo.setRating((Float.parseFloat(buffer.toString()) * 2) + ""); // set rating out of 10
    // }
    //
    // }
    // }
    // buffer = null;
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    String lecture = new String(ch, start, length);
    if (buffer != null) {
      buffer.append(lecture);
    }
  }

  @Override
  public TvShowInfo getObject() {
    TvShowSeason.sortSeasons(seasons);// Sort season by season number
    tvshowInfo.setSeasons(seasons);
    return tvshowInfo;
  }
}
