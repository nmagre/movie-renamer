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
import fr.free.movierenamer.media.movie.MovieImage;
import fr.free.movierenamer.parser.xml.TmdbImage;
import fr.free.movierenamer.parser.xml.XMLParser;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Class TheMovieDbImageWorker , get images from theMovieDB with imdbID
 *
 * @author Magré Nicolas
 */
public class TmdbImageWorker extends SwingWorker<MovieImage, Void> {

  private Settings setting;
  private MediaID id;

  /**
   * Constructor arguments
   *
   * @param id Media Id (imdb ID)
   * @param setting Movie Renamer settings
   * @throws ActionNotValidException
   */
  public TmdbImageWorker(MediaID id, Settings setting) throws ActionNotValidException {
    if (id.getType() != MediaID.IMDBID) {
      throw new ActionNotValidException("TmdbImageWorker can only use imdb ID");
    }
    this.id = id;
    this.setting = setting;
  }

  @Override
  protected MovieImage doInBackground() throws InterruptedException {
    MovieImage mvImgs = new MovieImage();

    // Try to get XML from theMovieDB
    try {
      String xmlUrl = new String(DatatypeConverter.parseBase64Binary(setting.xurlMdb)) + "/";
      URL url = new URL(setting.tmdbAPMovieImdbLookUp + xmlUrl + id.getID());
      File f = setting.cache.get(url, Cache.TMDBXML);
      if (f == null) {
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
        setting.cache.add(in, url.toString(), Cache.TMDBXML);
        f = setting.cache.get(url, Cache.TMDBXML);
      }

      // Parse TheMovieDb XML
      XMLParser<MovieImage> mmp = new XMLParser<MovieImage>(f.getAbsolutePath());
      mmp.setParser(new TmdbImage());
      try {
        mvImgs = mmp.parseXml();
        if (mvImgs.getThumbs() != null) {
          Settings.LOGGER.log(Level.INFO, "  {0} Thumbs", "" + mvImgs.getThumbs().size());
        }
        if (mvImgs.getFanarts() != null) {
          Settings.LOGGER.log(Level.INFO, "  {0} Fanarts", "" + mvImgs.getFanarts().size());
        }
      } catch (ParserConfigurationException ex) {
        Settings.LOGGER.log(Level.SEVERE, Utils.getStackTrace("ParserConfigurationException", ex.getStackTrace()));
      } catch (SAXException ex) {
        Settings.LOGGER.log(Level.SEVERE, Utils.getStackTrace("SAXException", ex.getStackTrace()));
      } catch (IOException ex) {
        Settings.LOGGER.log(Level.SEVERE, ex.toString());
      } catch (InterruptedException ex) {
        Settings.LOGGER.log(Level.WARNING, ex.toString());
        return null;
      } catch (IllegalArgumentException ex) {
        Settings.LOGGER.log(Level.SEVERE, Utils.getStackTrace("IllegalArgumentException", ex.getStackTrace()));
      }
    } catch (IOException ex) {
      Settings.LOGGER.log(Level.SEVERE, Utils.getStackTrace("IOException", ex.getStackTrace()));
    }
    return mvImgs;
  }
}
