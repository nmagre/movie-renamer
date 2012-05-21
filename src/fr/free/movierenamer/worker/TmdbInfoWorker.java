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
import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.parser.xml.TmdbImage;
import fr.free.movierenamer.parser.xml.TmdbInfo;
import fr.free.movierenamer.parser.xml.XMLParser;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Settings;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Class TmdbInfoWorker, get movie info from the movie database
 *
 * @author Nicolas Magré
 */
public class TmdbInfoWorker extends SwingWorker<MovieInfo, String> {

  private Settings setting;
  private MediaID id;

  /**
   * Constructor arguments
   *
   * @param id
   * @param setting Movie Renamer settings
   * @throws MalformedURLException
   * @throws ActionNotValidException
   */
  public TmdbInfoWorker(MediaID id, Settings setting) throws MalformedURLException, ActionNotValidException {
    if (id.getType() != MediaID.TMDBID) {
      throw new ActionNotValidException("TmdbInfoWorker  can only use tmdb ID");
    }
    this.setting = setting;
    this.id = id;
  }

  @Override
  protected MovieInfo doInBackground() throws Exception {
    MovieInfo movieInfo = new MovieInfo();
    try {
      String xmlUrl = new String(DatatypeConverter.parseBase64Binary(setting.xurlMdb)) + "/";
      String xmlFile = setting.tmdbAPIMovieInf + xmlUrl + id.getID();
      if (setting.imdbFr) {
        xmlFile = xmlFile.replace("/en/", "/fr/");
      }
      
      XMLParser<MovieInfo> xmp = new XMLParser<MovieInfo>(xmlFile);
      xmp.setParser(new TmdbInfo());
      movieInfo = xmp.parseXml();
      
      XMLParser<MovieImage> mmp = new XMLParser<MovieImage>(xmlFile);
      mmp.setParser(new TmdbImage());
      MovieImage movieImg = mmp.parseXml();
      movieInfo.setImages(movieImg);
     
    } catch (IOException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (ParserConfigurationException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (SAXException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }

    setProgress(100);
    return movieInfo;
  }
}
