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

import fr.free.movierenamer.media.tvshow.TvShowEpisode;
import fr.free.movierenamer.media.tvshow.TvShowInfo;
import fr.free.movierenamer.media.tvshow.TvShowSeason;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Class TvdbInfo
 * @author Nicolas Magré
 */
public class TvdbInfo extends MrParser<TvShowInfo> {

  private StringBuffer buffer;
  private List<TvShowSeason> seasons;
  private boolean data;
  private String title;
  private String plot;
  private String rating;
  private String votes;
  private int season;
  private int episode;
  private TvShowSeason currentSeason;
  private TvShowEpisode currentEpisode;

  public TvdbInfo() {
    super();
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
  public void endDocument() throws SAXException {
    super.endDocument();
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    buffer = new StringBuffer();
    if (name.equalsIgnoreCase("Data")) {
      data = true;
    }
    if (name.equalsIgnoreCase("Episode")) {
      currentEpisode = new TvShowEpisode();
      title = "";
      plot = "";
      rating = "";
      votes = "";
      episode = -1;
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
      if (name.equalsIgnoreCase("Episode")) {
        currentEpisode.setNum(episode);
        currentEpisode.setTitle(title);
        currentEpisode.setPlot(plot);
        currentEpisode.setRating(rating);
        currentEpisode.setVotes(votes);
        if (currentSeason == null) {
          currentSeason = new TvShowSeason(season);
        } else if (currentSeason.getNum() != season) {
          seasons.add(currentSeason);
          currentSeason = new TvShowSeason(season);
        }
        currentSeason.addEpisode(currentEpisode);
      }
      if (name.equalsIgnoreCase("EpisodeName")) {
        title = buffer.toString();
      }
      if (name.equalsIgnoreCase("SeasonNumber")) {
        season = Integer.parseInt(buffer.toString());
      }
      if (name.equalsIgnoreCase("EpisodeNumber")) {
        episode = Integer.parseInt(buffer.toString());
      }
      if (name.equalsIgnoreCase("Overview")) {
        plot = buffer.toString();
      }
      if (name.equalsIgnoreCase("Rating")) {
        rating = buffer.toString();
      }
      if (name.equalsIgnoreCase("RatingCount")) {
        votes = buffer.toString();
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
    return null;// seasons;
  }
}
