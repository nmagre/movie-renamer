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
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.media.tvshow.TvShowSeason;
import fr.free.movierenamer.parser.xml.TvdbInfo;
import fr.free.movierenamer.parser.xml.TvdbUpdate;
import fr.free.movierenamer.parser.xml.XMLParser;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;

/**
 * Class TvdbInfoWorker
 *
 * @author Nicolas Magré
 */
public class TvdbInfoWorker extends SwingWorker<List<TvShowSeason>, String> {

  private static final int RETRY = 3;
  private MediaID id;
  private Settings setting;
  private SwingPropertyChangeSupport errorSupport;

  public TvdbInfoWorker(SwingPropertyChangeSupport errorSupport, MediaID id, Settings setting) throws ActionNotValidException {
    this.errorSupport = errorSupport;
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
      String xmlUrl = new String(DatatypeConverter.parseBase64Binary(Settings.xurlTdb)) + "/";
      URL url = new URL(Settings.tvdbAPIUrlTvShow + xmlUrl + "series/" + id.getID() + "/all/" + (setting.tvshowScrapperFR ? "fr" : "en") + ".zip");
      File f = Cache.getInstance().get(url, Cache.CacheType.TVSHOWZIP);
      if (f == null) {
        for (int i = 0; i < RETRY; i++) {
          InputStream in;
          try {
            in = url.openStream();
            Cache.getInstance().add(in, url.toString(), Cache.CacheType.TVSHOWZIP);
            f = Cache.getInstance().get(url, Cache.CacheType.TVSHOWZIP);
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
        URL urlup = new URL(Settings.tvdbAPIUrlTvShow + "Updates.php?type=series&time=" + time);
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
              Cache.getInstance().add(in, url.toString(), Cache.CacheType.TVSHOWZIP);
              f = Cache.getInstance().get(url, Cache.CacheType.TVSHOWZIP);
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

      if (f == null) {
        errorSupport.firePropertyChange("closeLoadingDial", false, true);
        publish("httpFailed");
        return null;
      }

      XMLParser<ArrayList<TvShowSeason>> xmp = new XMLParser<ArrayList<TvShowSeason>>(f.getAbsolutePath(), (setting.tvshowScrapperFR ? "fr" : "en") + ".xml");
      xmp.setParser(new TvdbInfo());
      seasons = xmp.parseXml();

    } catch (InterruptedException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (ParserConfigurationException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (SAXException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }

    setProgress(100);

    return seasons;
  }

  @Override
  public void process(List<String> v) {
    JOptionPane.showMessageDialog(null, Utils.i18n(v.get(0)), Utils.i18n("error"), JOptionPane.ERROR_MESSAGE);
  }
}
