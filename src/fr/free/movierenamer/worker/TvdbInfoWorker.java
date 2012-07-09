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

import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.media.tvshow.TvShowSeason;
import fr.free.movierenamer.parser.xml.TvdbInfo;
import fr.free.movierenamer.parser.xml.TvdbUpdate;
import fr.free.movierenamer.parser.xml.XMLParser;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.Settings;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Class TvdbInfoWorker
 *
 * @author Nicolas Magré
 */
//A faire
public class TvdbInfoWorker extends SwingWorker<ArrayList<TvShowSeason>, String> {

  private static final int RETRY = 3;
  private MediaID id;
  private Settings setting;

  public TvdbInfoWorker(MediaID id, Settings setting) throws ActionNotValidException {
    if (id.getType() != MediaID.TVDBID) {
      throw new ActionNotValidException("TvdbInfoWorker can only use tvdb id");
    }
    this.id = id;
    this.setting = setting;
  }

  @Override
  protected ArrayList<TvShowSeason> doInBackground() {
    System.out.println("TvdbInfoWorker");
    ArrayList<TvShowSeason> seasons = new ArrayList<TvShowSeason>();
    try {
      String xmlUrl = new String(DatatypeConverter.parseBase64Binary(setting.xurlTdb)) + "/";
      URL url = new URL(setting.tvdbAPIUrlTvShow + xmlUrl + "series/" + id.getID() + "/all/" + (setting.tvshowScrapperFR ? "fr" : "en") + ".zip");
      File f = setting.cache.get(url, Cache.TVSHOWZIP);
      if (f == null) {
        for (int i = 0; i < RETRY; i++) {
          InputStream in;
          try {
            in = url.openStream();
            setting.cache.add(in, url.toString(), Cache.TVSHOWZIP);
            f = setting.cache.get(url, Cache.TVSHOWZIP);
            break;
          } catch (Exception e) {//Don't care about exception
            Settings.LOGGER.log(Level.SEVERE, null, e);
            try {
              Thread.sleep(300);
            } catch (InterruptedException ex) {
              Settings.LOGGER.log(Level.SEVERE, null, ex);
            }
          }
        }
      } else {
        //Check if there is an update for this serie
        long time = f.lastModified();
        URL urlup = new URL(setting.tvdbAPIUrlTvShow + "Updates.php?type=series&time=" + time);
        XMLParser<ArrayList<MediaID>> xmp = new XMLParser<ArrayList<MediaID>>(urlup.toString());
        xmp.setParser(new TvdbUpdate());
        ArrayList<MediaID> ids = xmp.parseXml();
        boolean needUpdate = false;
        for (MediaID mid : ids) {
          if (mid.equals(this.id)) {
            needUpdate = true;
            break;
          }
        }
        if (needUpdate) {
          for (int i = 0; i < RETRY; i++) {
            InputStream in;
            try {
              in = url.openStream();
              setting.cache.add(in, url.toString(), Cache.TVSHOWZIP);
              f = setting.cache.get(url, Cache.TVSHOWZIP);
              break;
            } catch (Exception e) {//Don't care about exception
              Settings.LOGGER.log(Level.SEVERE, null, e);
              try {
                Thread.sleep(300);
              } catch (InterruptedException ex) {
                Settings.LOGGER.log(Level.SEVERE, null, ex);
              }
            }
          }
        }
      }

      if (f == null) {//A faire
        //error
        return null;
      }

      XMLParser<ArrayList<TvShowSeason>> xmp = new XMLParser<ArrayList<TvShowSeason>>(f.getAbsolutePath(), (setting.tvshowScrapperFR ? "fr" : "en") + ".xml");
      xmp.setParser(new TvdbInfo());
      seasons = xmp.parseXml();

      for (TvShowSeason season : seasons) {
        System.out.println(season);
      }

    } catch (InterruptedException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (ParserConfigurationException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (SAXException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }
    return seasons;
  }
}
