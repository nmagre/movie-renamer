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
package fr.free.movierenamer.worker.provider;

import fr.free.movierenamer.parser.AllocineTvInfo;

import fr.free.movierenamer.parser.AllocineTvEpisode;

import fr.free.movierenamer.worker.HttpWorker;

import fr.free.movierenamer.worker.HttpWorker;

import fr.free.movierenamer.media.tvshow.TvShowEpisode;

import java.util.List;

import fr.free.movierenamer.media.tvshow.TvShowSeason;

import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.media.tvshow.SxE;
import fr.free.movierenamer.media.tvshow.TvShowInfo;
import fr.free.movierenamer.parser.AllocineTvShowInfo;
import fr.free.movierenamer.parser.MrParser;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.worker.TvShowInfoWorker;
import java.beans.PropertyChangeSupport;
import java.io.File;

/**
 * Class AllocineTvShowInfoWorker
 *
 * @author Nicolas Magré
 */
public class AllocineTvShowInfoWorker extends TvShowInfoWorker {// TODO A faire

  //The episode num to search
  private final SxE sxe;
//  private final HttpWorker<List<TvShowSeason>> seasonsWorker = new HttpWorker<List<TvShowSeason>>(errorSupport, new AllocineTvSeason());
//  private final HttpWorker<List<TvShowEpisode>> episodesWorker = new HttpWorker<List<TvShowEpisode>>(errorSupport, new AllocineTvEpisode());
//  private final HttpWorker<TvShowEpisode> episodeWorker = new HttpWorker<TvShowEpisode>(errorSupport, new AllocineTvShowInfo());

  /**
   * Constructor arguments
   *
   * @param errorSupport Swing change support
   * @param id Media id
   * @param sxe
   * @throws ActionNotValidException
   */
  public AllocineTvShowInfoWorker(PropertyChangeSupport errorSupport, MediaID id, SxE sxe) throws ActionNotValidException {
    super(errorSupport, id);
    if (id.getType() != MediaID.MediaIdType.ALLOCINETVID) {
      throw new ActionNotValidException("AllocineTvInfoWorker can only use allocine ID");
    }
    this.sxe = sxe;
  }

  @Override
  protected String getUri() throws Exception {
    return Settings.allocineAPIInfo.replace("MEDIA", "tvseries") + id.getID();
  }

  @Override
  protected MrParser<TvShowInfo> getParser() throws Exception {
    return new AllocineTvShowInfo();
  }
  
  @Override
  protected final TvShowInfo processFile(File xmlFile) throws Exception {
    TvShowInfo info = super.processFile(xmlFile);
    //TODO : process extra season and episodes info
    
    MediaID seasonId = null;
    List<TvShowSeason> seasons = info.getSeasons();
  //Absolute number
    if (sxe.getSeason() == 0 || sxe.getEpisode() == 0) {
      int absnum = 0;
      int num;
      for (TvShowSeason season : seasons) {
        if ((absnum + season.getEpisodeCount()) >= sxe.getAbs()) {
          num = sxe.getEpisode() - absnum;
          sxe.setEpisode(num);
          sxe.setSeason(season.getNum());
          seasonId = season.getID();
          break;
        }
        absnum += season.getEpisodeCount();
      }
    }

    // Season number seems to be not right, get first season
    if (seasonId == null && (sxe.getSeason() <= 0 || sxe.getSeason() > seasons.size())) {
      sxe.setSeason(1);
    }

    // Get season id
    for (TvShowSeason season : info.getSeasons()) {
      if (season.getNum() == sxe.getSeason()) {
        seasonId = season.getID();
        break;
      }
    }

    final MediaID seasonIdF = seasonId;
    // Get episodes for this season
    List<TvShowEpisode> episodes = new HttpWorker<List<TvShowEpisode>>(errorSupport) {

      @Override
      protected String getUri() throws Exception {
        return Settings.allocineAPIInfo.replace("MEDIA", "season") + seasonIdF.getID();
      }

      @Override
      protected MrParser<List<TvShowEpisode>> getParser() throws Exception {
        return new AllocineTvEpisode();
      }
    
    }.executeInBackground();
    TvShowEpisode.sortEpisodes(episodes);       
        
    // Episode number seems to be not right, get first episode
    if (sxe.getEpisode() <= 0) {
      sxe.setEpisode(1);
    }

    int ep = -1;
    for (int i = 0; i < episodes.size(); i++) {
      if (sxe.getEpisode() == episodes.get(i).getNum()) {
        ep = i;
        break;
      }
    }

    // Episode not found
    if (ep == -1) {
      ep = 1;
    }
    
    final MediaID epF =  episodes.get(ep).getIDs().get(0);

    TvShowEpisode episode = new HttpWorker<TvShowEpisode>(errorSupport) {

      @Override
      protected String getUri() throws Exception {
        return Settings.allocineAPIInfo.replace("MEDIA", "episode") +epF.getID();
      }

      @Override
      protected MrParser<TvShowEpisode> getParser() throws Exception {
        return new AllocineTvInfo();
      }
    
    }.executeInBackground();
    // Add episode info to episodes list
//    episode.setNum(episodes.get(ep).getNum());
    episodes.remove(ep);
    episodes.add(episode);

    // Create dummy episodes for all seasons and add episodes for this season
    for (int i = 0; i < seasons.size(); i++) {
      if (sxe.getSeason() == seasons.get(i).getNum()) {
        TvShowSeason tmps = seasons.get(i);
        tmps.setEpisodes(episodes);
        seasons.remove(i);
        seasons.add(i, tmps);
      } else {
        TvShowSeason season = seasons.get(i);
        seasons.remove(i);
        for (int j = 0; j < season.getEpisodeCount(); j++) {
          season.addEpisode(new TvShowEpisode(j + 1));
        }
        seasons.add(i, season);
      }
    }
    
//    for (TvShowSeason season : seasons) {
//      System.out.println(season);
//    }
    
    info.setSxe(sxe);
    
    return info;
  }
  
//    @Override
//    protected TvShowInfo processExtraInfo(TvShowInfo originalFile) throws Exception {
//      String seasonId = null;
//   // Get serie seasons
//      List<TvShowSeason> seasons = seasonsWorker.startAndGet(Settings.allocineAPIInfo.replace("MEDIA", "tvseries") + id.getID());
//      TvShowSeason.sortSeasons(seasons);//Sort season by season number
//      return originalFile;
//    }

  /*@Override
  protected TvShowInfo executeInBackground() throws Exception {
    String seasonId = null;

    // Get serie seasons
    List<TvShowSeason> seasons = seasonsWorker.startAndGet(Settings.allocineAPIInfo.replace("MEDIA", "tvseries") + id.getID());
    TvShowSeason.sortSeasons(seasons);//Sort season by season number

    //Absolute number
    if (sxe.getSeason() == 0 || sxe.getEpisode() == 0) {
      int absnum = 0;
      int num;
      for (TvShowSeason season : seasons) {
        if ((absnum + season.getEpisodeCount()) >= sxe.getAbs()) {
          num = sxe.getEpisode() - absnum;
          sxe.setEpisode(num);
          sxe.setSeason(season.getNum());
          seasonId = season.getID().getID();
          break;
        }
        absnum += season.getEpisodeCount();
      }
    }

    // Season number seems to be not right, get first season
    if (seasonId == null && (sxe.getSeason() <= 0 || sxe.getSeason() > seasons.size())) {
      sxe.setSeason(1);
    }

    // Get season id
    for (TvShowSeason season : seasons) {
      if (season.getNum() == sxe.getSeason()) {
        seasonId = season.getID().getID();
        break;
      }
    }

    // Get episodes for this season
    List<TvShowEpisode> episodes = episodesWorker.startAndGet(Settings.allocineAPIInfo.replace("MEDIA", "season") + seasonId);
    TvShowEpisode.sortEpisodes(episodes);//Sort episodes by episode number

    // Episode number seems to be not right, get first episode
    if (sxe.getEpisode() <= 0) {
      sxe.setEpisode(1);
    }

    int ep = -1;
    for (int i = 0; i < episodes.size(); i++) {
      if (sxe.getEpisode() == episodes.get(i).getNum()) {
        ep = i;
      }
    }

    // Episode not found
    if (ep == -1) {
      ep = 1;
    }

    TvShowEpisode episode = episodeWorker.startAndGet(Settings.allocineAPIInfo.replace("MEDIA", "episode") + episodes.get(ep).getIDs().get(0));
    // Add episode info to episodes list
    episode.setNum(episodes.get(ep).getNum());
    episodes.remove(ep);
    episodes.add(episode);

    // Create dummy episodes for all seasons and add episodes for this season
    for (int i = 0; i < seasons.size(); i++) {
      if (sxe.getSeason() == seasons.get(i).getNum()) {
        TvShowSeason tmps = seasons.get(i);
        tmps.setEpisodes(episodes);
        seasons.remove(i);
        seasons.add(i, tmps);
      } else {
        TvShowSeason season = seasons.get(i);
        seasons.remove(i);
        for (int j = 0; j < season.getEpisodeCount(); j++) {
          season.addEpisode(new TvShowEpisode(j + 1));
        }
        seasons.add(i, season);
      }
    }
    
    for (TvShowSeason season : seasons) {
      System.out.println(season);
    }
    
    TvShowInfo tvShowInfo = new TvShowInfo();
    tvShowInfo.setSeasons(seasons);
    tvShowInfo.setSxe(sxe);

    return tvShowInfo;
  }*/
}
