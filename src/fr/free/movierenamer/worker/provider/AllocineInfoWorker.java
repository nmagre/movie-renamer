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
import fr.free.movierenamer.media.movie.MovieImage;
import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.parser.AllocineInfo;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.worker.HttpWorker;
import fr.free.movierenamer.worker.MovieInfoWorker;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;

/**
 * Class AllocineInfoWorker
 *
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
public class AllocineInfoWorker extends MovieInfoWorker {

  /**
   * Constructor arguments
   *
   * @param errorSupport Swing change support
   * @param id Media id
   * @throws ActionNotValidException
   */
  public AllocineInfoWorker(PropertyChangeSupport errorSupport, MediaID id) throws ActionNotValidException {
    super(errorSupport, id, new HttpWorker<MovieInfo>(errorSupport, new AllocineInfo()));
    if (id.getType() != MediaID.MediaIdType.ALLOCINEID) {
      throw new ActionNotValidException("AllocineInfoWorker can only use allocine ID");
    }
  }

  @Override
  protected MovieInfo executeInBackground() throws Exception {
    MovieInfo movieInfo = movieInfoWorker.startAndGet(Settings.allocineAPIInfo.replace("MEDIA", "movie") + id.getID());// Wait for movie info

    MediaID imdbId = null;
    try {
      XbmcPassionIDLookupWorker xbl = new XbmcPassionIDLookupWorker(errorSupport, id);
      xbl.execute();
      imdbId = xbl.get();// Wait for movie imdb id
    } catch (ActionNotValidException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }

    if (imdbId != null) {
      MovieImage mediaImage = null;
      try {
        TmdbImageWorker imgWorker = new TmdbImageWorker(errorSupport, imdbId);
        imgWorker.execute();
        mediaImage = imgWorker.get();// Wait for movie images
      } catch (ActionNotValidException ex) {
        Settings.LOGGER.log(Level.SEVERE, null, ex);
      }

      if (mediaImage != null) {
        movieInfo.setImages(mediaImage);
      }
    }

    return movieInfo;
  }
//  @Override
//  protected MrParser<MovieImage> getImageParser() throws Exception {
//    return new AllocineImage();
//  }
  // /*
  // * (non-Javadoc)
  // *
  // * @see fr.free.movierenamer.worker.MovieInfoWorker#getImageWorker()
  // */
  // @Override
  // protected MediaImageWorker getImageWorker() {
  // return null;
  // }
  // @Override
  // protected MovieInfo executeInBackground() {
  // MovieInfo movieInfo = null;
  // try {
  // String uri = Settings.allocineAPIInfo.replace("MEDIA", "movie") + id.getID();
  // System.out.println(uri);
  // URL url = new URL(uri);
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
  //
  // if (xmlFile == null) {
  // firePropertyChange("closeLoadingDial", "httpFailed");
  // return null;
  // }
  //
  // //Parse allocine API XML
  // XMLParser<MovieInfo> xmp = new XMLParser<MovieInfo>(xmlFile.getAbsolutePath());
  // xmp.setParser(new AllocineInfo());
  // movieInfo = xmp.parseXml();
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
  // if (movieInfo == null) {
  // firePropertyChange("closeLoadingDial", "scrapperInfoFailed");
  // return null;
  // }
  //
  // setProgress(100);
  // return movieInfo;
  // }
}
