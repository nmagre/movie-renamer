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
import fr.free.movierenamer.media.tvshow.TvShowImage;
import fr.free.movierenamer.media.tvshow.TvShowInfo;
import fr.free.movierenamer.parser.xml.MrParser;
import fr.free.movierenamer.parser.xml.TvdbImage;
import fr.free.movierenamer.parser.xml.TvdbInfo;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Cache.CacheType;
import fr.free.movierenamer.worker.TvShowInfoWorker;
import java.beans.PropertyChangeSupport;
import javax.xml.bind.DatatypeConverter;

/**
 * Class TvdbInfoWorker
 * 
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
// TODO A faire
public class TvdbInfoWorker extends TvShowInfoWorker {

  public TvdbInfoWorker(PropertyChangeSupport errorSupport, MediaID id) throws ActionNotValidException {
    super(errorSupport, id);
    if (id.getType() != MediaID.MediaIdType.TVDBID) {
      throw new ActionNotValidException("TvdbInfoWorker can only use tvdb id");
    }
  }

  @Override
  protected String getSearchUri() throws Exception {
    String xmlUrl = new String(DatatypeConverter.parseBase64Binary(config.xurlTdb)) + "/";
    return config.tvdbAPIUrlTvShow + xmlUrl + "series/" + id.getID() + "/all/" + (config.tvshowScrapperFR ? "fr" : "en") + ".zip";
  }

  @Override
  protected MrParser<TvShowInfo> getInfoParser() throws Exception {
    return new TvdbInfo();
  }

  @Override
  protected MrParser<TvShowImage> getImageParser() throws Exception {
    return new TvdbImage();
  }

  @Override
  protected CacheType getCacheType() {
    return CacheType.TVSHOWZIP;
  }

  // @Override
  // protected ArrayList<TvShowSeason> executeInBackground() {
  // System.out.println("TvdbInfoWorker");
  // ArrayList<TvShowSeason> seasons = new ArrayList<TvShowSeason>();
  // Cache cache = Cache.getInstance();
  // try {
  // String xmlUrl = new String(DatatypeConverter.parseBase64Binary(config.xurlTdb)) + "/";
  // URL url = new URL(config.tvdbAPIUrlTvShow + xmlUrl + "series/" + id.getID() + "/all/" + (config.tvshowScrapperFR ? "fr" : "en") + ".zip");
  // File f = cache.get(url, Cache.CacheType.TVSHOWZIP);
  // if (f == null) {
  // for (int i = 0; i < RETRY; i++) {
  // InputStream in;
  // try {
  // in = url.openStream();
  // cache.add(in, url.toString(), Cache.CacheType.TVSHOWZIP);
  // f = cache.get(url, Cache.CacheType.TVSHOWZIP);
  // break;
  // } catch (Exception e) {//Don't care about exception
  // Settings.LOGGER.log(Level.SEVERE, null, e);
  // try {
  // Thread.sleep(300);
  // } catch (InterruptedException ex) {
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // }
  // }
  // }
  // } else {
  // //Check if there is an update for this serie
  // long time = f.lastModified();
  // URL urlup = new URL(config.tvdbAPIUrlTvShow + "Updates.php?type=series&time=" + time);
  // XMLParser<ArrayList<MediaID>> xmp = new XMLParser<ArrayList<MediaID>>(urlup.toString());
  // xmp.setParser(new TvdbUpdate());
  // ArrayList<MediaID> ids = xmp.parseXml();
  // boolean needUpdate = false;
  // for (MediaID mid : ids) {
  // if (mid.equals(this.id)) {
  // needUpdate = true;
  // break;
  // }
  // }
  // if (needUpdate) {
  // for (int i = 0; i < RETRY; i++) {
  // InputStream in;
  // try {
  // in = url.openStream();
  // cache.add(in, url.toString(), Cache.CacheType.TVSHOWZIP);
  // f = cache.get(url, Cache.CacheType.TVSHOWZIP);
  // break;
  // } catch (Exception e) {//Don't care about exception
  // Settings.LOGGER.log(Level.SEVERE, null, e);
  // try {
  // Thread.sleep(300);
  // } catch (InterruptedException ex) {
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // }
  // }
  // }
  // }
  // }
  //
  // if (f == null) {//A faire
  // //error
  // return null;
  // }
  //
  // XMLParser<ArrayList<TvShowSeason>> xmp = new XMLParser<ArrayList<TvShowSeason>>(f.getAbsolutePath(), (config.tvshowScrapperFR ? "fr" : "en") + ".xml");
  // xmp.setParser(new TvdbInfo());
  // seasons = xmp.parseXml();
  //
  // for (TvShowSeason season : seasons) {
  // System.out.println(season);
  // }
  //
  // } catch (InterruptedException ex) {
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // } catch (ParserConfigurationException ex) {
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // } catch (SAXException ex) {
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // } catch (IOException ex) {
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // }
  // return seasons;
  // }
}
