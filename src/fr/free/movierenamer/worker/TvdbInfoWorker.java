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
import fr.free.movierenamer.media.tvshow.TvShowInfo;
import fr.free.movierenamer.parser.xml.TvdbInfo;
import fr.free.movierenamer.parser.xml.XMLParser;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.Settings;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class TvdbInfoWorker extends SwingWorker<TvShowInfo, String> {

  private MediaID id;
  private Settings setting;

  public TvdbInfoWorker(MediaID id, Settings setting) throws ActionNotValidException {
    if(id.getType() != MediaID.TVDBID) {
      throw new ActionNotValidException("TvdbInfoWorker can only use tvdb id");
    }
    this.id = id;
    this.setting = setting;
  }

  @Override
  protected TvShowInfo doInBackground() {
    TvShowInfo tvShowInfo = null;
    try {
      System.out.println("Tv Show info worker start");
      String xmlUrl = new String(DatatypeConverter.parseBase64Binary(setting.xurlTdb)) + "/";
      URL url = new URL(setting.tvdbAPIUrlTvShow + xmlUrl + "series/" + id.getID() + "/all/" + (setting.tvdbFr ? "fr" : "en") + ".zip");
      File f = setting.cache.get(url, Cache.TVSHOWZIP);
      if (f == null) {//A refaire, XMLPArser peut lire un fichier depuis une URL
        InputStream in;
        try {
          in = url.openStream();
        } catch (IOException e) {
          try {
            Thread.sleep(1200);
            in = url.openStream();
          } catch (IOException ex) {
            try {
              Thread.sleep(600);
              in = url.openStream();
            } catch (IOException exe) {
              //A refaire , traiter erreur
              return null;
            }
          }
        }
        setting.cache.add(in, url.toString(), Cache.TVSHOWZIP);
        f = setting.cache.get(url, Cache.TVSHOWZIP);
      }

      XMLParser<TvShowInfo> xmp = new XMLParser<TvShowInfo>(f.getAbsolutePath(), (setting.tvdbFr ? "fr" : "en") + ".xml");
      xmp.setParser(new TvdbInfo());
      tvShowInfo = xmp.parseXml();

    } catch (InterruptedException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (ParserConfigurationException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (SAXException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }
    return tvShowInfo;
  }
}
