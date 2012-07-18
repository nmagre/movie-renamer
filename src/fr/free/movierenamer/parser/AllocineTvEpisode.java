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
import fr.free.movierenamer.media.tvshow.TvShowEpisode;
import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * 
 * @author Nicolas Magré
 */
public class AllocineTvEpisode extends MrParser<ArrayList<TvShowEpisode>> {

  private StringBuffer buffer;
  private TvShowEpisode currentEpisode;
  private ArrayList<TvShowEpisode> tvShowEpisodes;
  private boolean season, episodeList, episode;

  public AllocineTvEpisode() {
    super();
  }

  @Override
  public void startDocument() throws SAXException {
    super.startDocument();
    tvShowEpisodes = new ArrayList<TvShowEpisode>();
    season = episodeList = episode = false;
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    buffer = new StringBuffer();
    if (name.equalsIgnoreCase("season")) {
      season = true;
    }
    if (season) {
      if (name.equalsIgnoreCase("episodeList")) {
        episodeList = true;
      }
      if (episodeList) {
        if (name.equalsIgnoreCase("episode")) {
          episode = true;
          currentEpisode = new TvShowEpisode();
          currentEpisode.addID(new MediaID(attributes.getValue("code"), MediaID.MediaIdType.ALLOCINEEPISODEID));
        }
      }
    }
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    if (name.equalsIgnoreCase("season")) {
      season = false;
    }
    if (season) {
      if (name.equalsIgnoreCase("episodeList")) {
        episodeList = false;
      }
      if (episodeList) {
        if (episode) {
          if (name.equalsIgnoreCase("episodeNumberSeason")) {
            currentEpisode.setNum(Integer.parseInt(buffer.toString()));
          }
          if(name.equalsIgnoreCase("originalTitle")) {
            currentEpisode.setOriginalTitle(buffer.toString());
          }
          if(name.equalsIgnoreCase("title")) {
            currentEpisode.setTitle(buffer.toString());
          }
        }
        if (name.equalsIgnoreCase("episode")) {
          tvShowEpisodes.add(currentEpisode);
          episode = false;
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
  public ArrayList<TvShowEpisode> getObject() {
    return tvShowEpisodes;
  }
}
