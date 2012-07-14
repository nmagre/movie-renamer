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
package fr.free.movierenamer.worker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.media.tvshow.SxE;
import fr.free.movierenamer.media.tvshow.TvShowEpisode;
import fr.free.movierenamer.media.tvshow.TvShowSeason;
import fr.free.movierenamer.parser.xml.AllocineTVSeason;
import fr.free.movierenamer.parser.xml.AllocineTvEpisode;
import fr.free.movierenamer.parser.xml.AllocineTvInfo;
import fr.free.movierenamer.parser.xml.XMLParser;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;

/**
 * Class AllocineInfoTvWorker
 *
 * @author Nicolas Magré
 */
public class AllocineInfoTvWorker extends SwingWorker<List<TvShowSeason>, String> {
  
  private static final int RETRY = 3;
  private Settings setting;
  private MediaID id;
  private SwingPropertyChangeSupport errorSupport;
  private SxE sxe;

  /**
   * Constructor arguments
   *
   * @param errorSupport Swing change support
   * @param id Media id
   * @param sxe
   * @param setting Movie Renamer settings
   * @throws ActionNotValidException
   */
  public AllocineInfoTvWorker(SwingPropertyChangeSupport errorSupport, MediaID id, SxE sxe, Settings setting) throws ActionNotValidException {
    this.errorSupport = errorSupport;
    if (id.getType() != MediaID.ALLOCINETVID) {
      throw new ActionNotValidException("AllocineInfoWorker can only use allocine ID");
    }
    this.setting = setting;
    this.sxe = sxe;
    this.id = id;
  }
  
  @Override
  protected ArrayList<TvShowSeason> doInBackground() {
    System.out.println("AllocineInfoTvWorker");
    System.out.println(sxe);
    ArrayList<TvShowSeason> seasons = null;
    
    try {
      //Get Season id
      URL url = new URL(setting.allocineAPIInfo.replace("MEDIA", "tvseries") + id.getID());
      System.out.println(url);
      File xmlFile = getXML(url);
      
      if (xmlFile == null) {
        errorSupport.firePropertyChange("closeLoadingDial", false, true);
        publish("httpFailed");
        return null;
      }
      
      XMLParser<ArrayList<TvShowSeason>> xmp = new XMLParser<ArrayList<TvShowSeason>>(xmlFile.getAbsolutePath());
      xmp.setParser(new AllocineTVSeason());
      seasons = xmp.parseXml();

      //Sort season by season number
      TvShowSeason.sortSeasons(seasons);
      
      String seasonId = null;
      for (TvShowSeason season : seasons) {
        if (season.getNum() == sxe.getSeason()) {
          seasonId = season.getID().getID();
          break;
        }
      }

      //Absolute number
      int absnum = 0;
      int num;
      if (seasonId == null && sxe.getEpisode() > 0) {
        for (TvShowSeason season : seasons) {
          if ((absnum + season.getEpisodeCount()) >= sxe.getEpisode()) {
            num = sxe.getEpisode() - absnum;
            sxe.setEpisode(num);
            sxe.setSeason(season.getNum());
            seasonId = season.getID().getID();
            break;
          }
          absnum += season.getEpisodeCount();
        }
      }
      
      if (seasonId == null) {
        errorSupport.firePropertyChange("closeLoadingDial", false, true);
        publish("httpFailed");
        return null;
      }
      
      setProgress(33);

      //Get episode id
      url = new URL(setting.allocineAPIInfo.replace("MEDIA", "season") + seasonId);
      System.out.println(url);
      xmlFile = getXML(url);
      if (xmlFile == null) {
        errorSupport.firePropertyChange("closeLoadingDial", false, true);
        publish("httpFailed");
        return null;
      }
      
      XMLParser<ArrayList<TvShowEpisode>> xmmp = new XMLParser<ArrayList<TvShowEpisode>>(xmlFile.getAbsolutePath());
      xmmp.setParser(new AllocineTvEpisode());
      ArrayList<TvShowEpisode> episodes = xmmp.parseXml();
      
      if (sxe.getEpisode() <= 0) {
        sxe.setEpisode(1);
      }
      int ep = -1;
      for (int i = 0; i < episodes.size(); i++) {
        if (sxe.getEpisode() == episodes.get(i).getNum()) {
          ep = i;
        }
      }
      
      if (ep == -1) {
        ep = 0;
      }
      
      setProgress(66);

      //Get episode info
      url = new URL(setting.allocineAPIInfo.replace("MEDIA", "episode") + episodes.get(ep).getIDs().get(0));
      System.out.println(url);
      xmlFile = getXML(url);
      if (xmlFile == null) {
        errorSupport.firePropertyChange("closeLoadingDial", false, true);
        publish("httpFailed");
        return null;
      }

      //Parse allocine API XML
      XMLParser<TvShowEpisode> xmmmp = new XMLParser<TvShowEpisode>(xmlFile.getAbsolutePath());
      xmmmp.setParser(new AllocineTvInfo());
      TvShowEpisode tvshowInfo = xmmmp.parseXml();
      tvshowInfo.setNum(episodes.get(ep).getNum());
      episodes.remove(ep);
      episodes.add(tvshowInfo);

      //Sort episodes by episode number
      TvShowEpisode.sortEpisodes(episodes);
      
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
      
    } catch (IOException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (ParserConfigurationException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (SAXException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }
    
    if (seasons == null) {
      errorSupport.firePropertyChange("closeLoadingDial", false, true);
      publish("scrapperInfoFailed");
      return null;
    }
    
    setProgress(100);
    return seasons;
  }
  
  private File getXML(URL url) {
    File xmlFile = Cache.getInstance().get(url, Cache.CacheType.XML);
    if (xmlFile == null) {
      for (int i = 0; i < RETRY; i++) {
        InputStream in;
        try {
          in = url.openStream();
          Cache.getInstance().add(in, url.toString(), Cache.CacheType.XML);
          xmlFile = Cache.getInstance().get(url, Cache.CacheType.XML);
          break;
        } catch (Exception e) {//Don't care about exception, "xmlFile" will be null
          Settings.LOGGER.log(Level.SEVERE, null, e);
          try {
            Thread.sleep(300);
          } catch (InterruptedException ex) {
            Settings.LOGGER.log(Level.SEVERE, null, ex);
          }
        }
      }
    }
    return xmlFile;
  }
  
  @Override
  public void process(List<String> v) {
    JOptionPane.showMessageDialog(null, Utils.i18n(v.get(0)), Utils.i18n("error"), JOptionPane.ERROR_MESSAGE);
  }
}
