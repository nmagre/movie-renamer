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

import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.media.tvshow.SxE;
import fr.free.movierenamer.media.tvshow.TvShowEpisode;
import fr.free.movierenamer.media.tvshow.TvShowInfo;
import fr.free.movierenamer.media.tvshow.TvShowSeason;
import fr.free.movierenamer.parser.AllocineTvEpisode;
import fr.free.movierenamer.parser.AllocineTvInfo;
import fr.free.movierenamer.parser.AllocineTvSeason;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.worker.HttpWorker;
import fr.free.movierenamer.worker.TvShowInfoWorker;
import java.beans.PropertyChangeSupport;
import java.util.List;

/**
 * Class AllocineTvShowInfoWorker
 *
 * @author Nicolas Magré
 */
public class AllocineTvShowInfoWorker extends TvShowInfoWorker {// TODO A faire

  private final SxE sxe;
  private final HttpWorker<List<TvShowSeason>> seasonsWorker = new HttpWorker<List<TvShowSeason>>(errorSupport, new AllocineTvSeason());
  private final HttpWorker<List<TvShowEpisode>> episodesWorker = new HttpWorker<List<TvShowEpisode>>(errorSupport, new AllocineTvEpisode());
  private final HttpWorker<TvShowEpisode> episodeWorker = new HttpWorker<TvShowEpisode>(errorSupport, new AllocineTvInfo());

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
  }
  // @Override
  // protected ArrayList<TvShowSeason> executeInBackground() {
  // System.out.println("AllocineTvShowInfoWorker");
  // System.out.println(sxe);
  // ArrayList<TvShowSeason> seasons = null;
  //
  // try {
  // //Get Season id
  // URL url = new URL(config.allocineAPIInfo.replace("MEDIA", "tvseries") + id.getID());
  // System.out.println(url);
  // File xmlFile = getXML(url);
  //
  // if (xmlFile == null) {
  // firePropertyChange("closeLoadingDial", "httpFailed");
  // return null;
  // }
  //
  // XMLParser<ArrayList<TvShowSeason>> xmp = new XMLParser<ArrayList<TvShowSeason>>(xmlFile.getAbsolutePath());
  // xmp.setParser(new AllocineTvSeason());
  // seasons = xmp.parseXml();
  //
  //
  // //Sort season by season number
  // TvShowSeason.sortSeasons(seasons);
  //
  // String seasonId = null;
  // for (TvShowSeason season : seasons) {
  // if (season.getNum() == sxe.getSeason()) {
  // seasonId = season.getID().getID();
  // break;
  // }
  // }
  //
  // //Absolute number
  // int absnum = 0;
  // int num;
  // if (seasonId == null && sxe.getEpisode() > 0) {
  // for (TvShowSeason season : seasons) {
  // if ((absnum + season.getEpisodeCount()) >= sxe.getEpisode()) {
  // num = sxe.getEpisode() - absnum;
  // sxe.setEpisode(num);
  // sxe.setSeason(season.getNum());
  // seasonId = season.getID().getID();
  // break;
  // }
  // absnum += season.getEpisodeCount();
  // }
  // }
  //
  // if (seasonId == null) {
  // firePropertyChange("closeLoadingDial", "httpFailed");
  // return null;
  // }
  //
  // setProgress(33);
  //
  // //Get episode id
  // url = new URL(config.allocineAPIInfo.replace("MEDIA", "season") + seasonId);
  // System.out.println(url);
  // xmlFile = getXML(url);
  // if (xmlFile == null) {
  // firePropertyChange("closeLoadingDial", "httpFailed");
  // return null;
  // }
  //
  // XMLParser<ArrayList<TvShowEpisode>> xmmp = new XMLParser<ArrayList<TvShowEpisode>>(xmlFile.getAbsolutePath());
  // xmmp.setParser(new AllocineTvEpisode());
  // ArrayList<TvShowEpisode> episodes = xmmp.parseXml();
  //
  // if (sxe.getEpisode() <= 0) {
  // sxe.setEpisode(1);
  // }
  // int ep = -1;
  // for (int i = 0; i < episodes.size(); i++) {
  // if (sxe.getEpisode() == episodes.get(i).getNum()) {
  // ep = i;
  // }
  // }
  //
  // if (ep == -1) {
  // ep = 0;
  // }
  //
  // setProgress(66);
  //
  // //Get episode info
  // url = new URL(config.allocineAPIInfo.replace("MEDIA", "episode") + episodes.get(ep).getIDs().get(0));
  // System.out.println(url);
  // xmlFile = getXML(url);
  // if (xmlFile == null) {
  // firePropertyChange("closeLoadingDial", "httpFailed");
  // return null;
  // }
  //
  // //Parse allocine API XML
  // XMLParser<TvShowEpisode> xmmmp = new XMLParser<TvShowEpisode>(xmlFile.getAbsolutePath());
  // xmmmp.setParser(new AllocineTvInfo());
  // TvShowEpisode tvshowInfo = xmmmp.parseXml();
  // tvshowInfo.setNum(episodes.get(ep).getNum());
  // episodes.remove(ep);
  // episodes.add(tvshowInfo);
  //
  // //Sort episodes by episode number
  // TvShowEpisode.sortEpisodes(episodes);
  //
  // for (int i = 0; i < seasons.size(); i++) {
  // if (sxe.getSeason() == seasons.get(i).getNum()) {
  // TvShowSeason tmps = seasons.get(i);
  // tmps.setEpisodes(episodes);
  // seasons.remove(i);
  // seasons.add(i, tmps);
  // } else {
  // TvShowSeason season = seasons.get(i);
  // seasons.remove(i);
  // for (int j = 0; j < season.getEpisodeCount(); j++) {
  // season.addEpisode(new TvShowEpisode(j + 1));
  // }
  // seasons.add(i, season);
  // }
  // }
  //
  // } catch (IOException ex) {
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // } catch (InterruptedException ex) {
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // } catch (ParserConfigurationException ex) {
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // } catch (SAXException ex) {
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // }
  //
  // if (seasons == null) {
  // firePropertyChange("closeLoadingDial", "scrapperInfoFailed");
  // return null;
  // }
  //
  // for (TvShowSeason season : seasons) {
  // System.out.println(season);
  // }
  //
  // setProgress(100);
  // return seasons;
  // }
  //
  // private File getXML(URL url) {
  // File xmlFile = Cache.getInstance().get(url, Cache.CacheType.XML);
  // if (xmlFile == null) {
  // for (int i = 0; i < RETRY; i++) {
  // InputStream in;
  // try {
  // in = url.openStream();
  // Cache.getInstance().add(in, url.toString(), Cache.CacheType.XML);
  // xmlFile = Cache.getInstance().get(url, Cache.CacheType.XML);
  // break;
  // } catch (Exception e) {//Don't care about exception, "xmlFile" will be null
  // Settings.LOGGER.log(Level.SEVERE, null, e);
  // try {
  // Thread.sleep(300);
  // } catch (InterruptedException ex) {
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // }
  // }
  // }
  // }
  // return xmlFile;
  // }
}
