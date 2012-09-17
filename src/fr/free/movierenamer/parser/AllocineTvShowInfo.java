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

  private StringBuffer buffer;
  private final TvShowInfo tvshowInfo;
  private List<TvShowSeason> seasons;
  private TvShowSeason currentSeason;
  private boolean tvseries, seasonList, statistics, nationalityList,genreList,casting;

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
    casting=false;
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    buffer = new StringBuffer();
    if (name.equalsIgnoreCase("tvseries")) {
      tvseries = true;
    }
    if (tvseries) {
      if (name.equalsIgnoreCase("poster")) {
        tvshowInfo.setPoster(attributes.getValue("href"));
      }
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
      if (name.equalsIgnoreCase("nationalityList")) {
        nationalityList = true;
      }
      if (name.equalsIgnoreCase("genreList")) {
        genreList = true;
      }
      if (name.equalsIgnoreCase("casting")) {
        casting = true;
      }
    }
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    if (name.equalsIgnoreCase("tvseries")) {
      tvseries = false;
    }
    if (tvseries) {
      if (name.equalsIgnoreCase("originalTitle")) {
        tvshowInfo.setOriginalTitle(buffer.toString());
        tvshowInfo.setTitle(buffer.toString());
      }
      if (name.equalsIgnoreCase("seasonList")) {
        seasonList = false;
      }
      if (name.equalsIgnoreCase("nationalityList")) {
        nationalityList = false;
      }
      if (name.equalsIgnoreCase("genreList")) {
        genreList = false;
      }
      if (name.equalsIgnoreCase("genreList")) {
        casting = false;
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
      } else if(nationalityList){
        if (name.equalsIgnoreCase("nationality")) {
          tvshowInfo.addCountry(buffer.toString());
        }
      } else if(genreList){
        if (name.equalsIgnoreCase("genre")) {
          tvshowInfo.addGenre(buffer.toString());
        }
      } else {
        if (name.equalsIgnoreCase("synopsis")) {
          tvshowInfo.setSynopsis(buffer.toString());
        }
        if (name.equalsIgnoreCase("userRating")) {
          tvshowInfo.setRating((Float.parseFloat(buffer.toString()) * 2)+""); //set rating out of 10
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
    TvShowSeason.sortSeasons(seasons);//Sort season by season number
    tvshowInfo.setSeasons(seasons);
    return tvshowInfo;
  }
}
