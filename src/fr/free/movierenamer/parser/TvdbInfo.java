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

import java.util.Date;

import fr.free.movierenamer.utils.Utils;

import fr.free.movierenamer.media.tvshow.TvShowEpisode;
import fr.free.movierenamer.media.tvshow.TvShowInfo;
import fr.free.movierenamer.media.tvshow.TvShowSeason;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Class TvdbInfo
 * 
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
public class TvdbInfo extends MrParser<TvShowInfo> {

  private StringBuffer buffer;
  private final TvShowInfo tvshowInfo;
  private List<TvShowSeason> seasons;
  private boolean data, series, episode;
  private String title;
  private String plot;
  private String rating;
  private String votes;
  private int seasonNum;
  private int episodeNum;
  private TvShowSeason currentSeason;
  private TvShowEpisode currentEpisode;

  public TvdbInfo() {
    super();
    tvshowInfo = new TvShowInfo();
  }

  @Override
  public void startDocument() throws SAXException {
    super.startDocument();
    seasons = new ArrayList<TvShowSeason>();
    data = false;
    currentSeason = null;
    currentEpisode = null;
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    buffer = new StringBuffer();
    if (name.equalsIgnoreCase("Data")) {
      data = true;
    }
    if (data) {
      if (name.equalsIgnoreCase("Series")) {
        series = true;
      }
      if (name.equalsIgnoreCase("Episode")) {
        episode = true;
        currentEpisode = new TvShowEpisode();
      }
    }
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    if (name.equalsIgnoreCase("Data")) {
      data = false;
      if (currentSeason != null) {
        seasons.add(currentSeason);
      }
    }
    if (data) {
      if (name.equalsIgnoreCase("Series")) {
        series = false;
      }
      if (name.equalsIgnoreCase("Episode")) {
        episode = false;
        if (currentSeason == null) {
          currentSeason = new TvShowSeason(seasonNum);
        } else if (currentSeason.getNum() != seasonNum) {
          seasons.add(currentSeason);
          currentSeason = new TvShowSeason(seasonNum);
        }
        currentSeason.addEpisode(currentEpisode);
        currentEpisode = null;
      }
      if (series) {
        if (name.equalsIgnoreCase("SeriesName")) {
          tvshowInfo.setTitle(buffer.toString());
          tvshowInfo.setOriginalTitle(buffer.toString());
        }
        if (name.equalsIgnoreCase("FirstAired")) {
//          tvshowInfo.setYear(""+new Date(buffer.toString()).getYear()); //deprecated, just to test !!!
        }
        if (name.equalsIgnoreCase("Genre")) {
          for(String genre : buffer.toString().split("\\|")) {
            if(genre!=null && genre.length()>0) {
              tvshowInfo.addGenre(genre);
            }
          }
        }
        if (name.equalsIgnoreCase("Actors")) {
          System.out.println(buffer.toString());
        }
        if (name.equalsIgnoreCase("Overview")) {
          tvshowInfo.setSynopsis(buffer.toString());
        }
        if (name.equalsIgnoreCase("Rating")) {
          tvshowInfo.setRating(buffer.toString());
        }
        if (name.equalsIgnoreCase("RatingCount")) {
//          System.out.println(buffer.toString());
        }
        if (name.equalsIgnoreCase("Runtime")) {
          if (Utils.isDigit(buffer.toString())) {
            int runtime = Integer.parseInt(buffer.toString());// Get runtime in minutes
            tvshowInfo.setRuntime("" + runtime);
          }
        }
        if (name.equalsIgnoreCase("banner")) {
          System.out.println(buffer.toString());
        }
        if (name.equalsIgnoreCase("fanart")) {
          System.out.println(buffer.toString());
        }
        if (name.equalsIgnoreCase("poster")) {
          System.out.println(buffer.toString());
        }
      }
      if (episode) {
        if (name.equalsIgnoreCase("EpisodeName")) {
          currentEpisode.setTitle(buffer.toString());
        }
        if (name.equalsIgnoreCase("SeasonNumber")) {
          seasonNum = Integer.parseInt(buffer.toString());
        }
        if (name.equalsIgnoreCase("EpisodeNumber")) {
          currentEpisode.setNum(Integer.parseInt(buffer.toString()));
        }
        if (name.equalsIgnoreCase("Overview")) {
          currentEpisode.setSynopsis(buffer.toString());
        }
        if (name.equalsIgnoreCase("Rating")) {
          currentEpisode.setRating(buffer.toString());
        }
        if (name.equalsIgnoreCase("RatingCount")) {
          currentEpisode.setVotes(buffer.toString());
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
  public TvShowInfo getObject() {
    TvShowSeason.sortSeasons(seasons);// Sort season by season number
    tvshowInfo.setSeasons(seasons);
    return tvshowInfo;
  }
}
