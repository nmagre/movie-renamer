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
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.YTdecodeUrl;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Class TmdbInfoWorker, get movie info from the movie database
 *
 * @author Nicolas Magré
 */
public class TmdbInfoWorker extends SwingWorker<MovieInfo, String> {

  private static final int RETRY = 3;
  private Settings setting;
  private MediaID id;
  private ResourceBundle bundle = ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle");
  private SwingPropertyChangeSupport errorSupport;

  /**
   * Constructor arguments
   *
   * @param errorSupport
   * @param id
   * @param setting Movie Renamer settings
   * @throws ActionNotValidException
   */
  public TmdbInfoWorker(SwingPropertyChangeSupport errorSupport, MediaID id, Settings setting) throws ActionNotValidException {
    this.errorSupport = errorSupport;
    if (id.getType() != MediaID.TMDBID) {
      throw new ActionNotValidException("TmdbInfoWorker  can only use tmdb ID");
    }
    this.setting = setting;
    this.id = id;
  }

  @Override
  protected MovieInfo doInBackground() {
    MovieInfo movieInfo = null;
    MovieImage movieImage = null;
    try {
      String uri = setting.tmdbAPIMovieInf + new String(DatatypeConverter.parseBase64Binary(setting.xurlMdb)) + "/" + id.getID();
      if (setting.movieScrapperFR) {
        uri = uri.replace("/en/", "/fr/");
      }

      URL url = new URL(uri);
      File xmlFile = setting.cache.get(url, Cache.XML);
      if (xmlFile == null) {
        for (int i = 0; i < RETRY; i++) {
          InputStream in;
          try {
            in = url.openStream();
            setting.cache.add(in, url.toString(), Cache.XML);
            xmlFile = setting.cache.get(url, Cache.XML);
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

      if (xmlFile == null) {
        errorSupport.firePropertyChange("closeLoadingDial", false, true);
        publish("httpFailed");
        return null;
      }

      //Parse TMDB API XML
      XMLParser<MovieInfo> xmp = new XMLParser<MovieInfo>(xmlFile.getAbsolutePath());
      xmp.setParser(new TmdbInfo());
      movieInfo = xmp.parseXml();

      XMLParser<MovieImage> xmmp = new XMLParser<MovieImage>(xmlFile.getAbsolutePath());
      xmmp.setParser(new TmdbImage());
      movieImage = xmmp.parseXml();

    } catch (IOException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (ParserConfigurationException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (SAXException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }

    if (movieInfo == null) {
      errorSupport.firePropertyChange("closeLoadingDial", false, true);
      publish("scrapperInfoFailed");
      return null;
    }

    if (movieImage == null) {
      errorSupport.firePropertyChange("closeLoadingDial", false, true);
      publish("scrapperInfoFailed");
      return null;
    }
        
    movieInfo.setImages(movieImage);
    if(!movieInfo.getTrailer().equals("")){
      String trailer = YTdecodeUrl.getRealUrl(movieInfo.getTrailer(), YTdecodeUrl.HD);
      if(trailer != null) {
        movieInfo.setTrailer(trailer);
      }
    }
        
    setProgress(100);
    return movieInfo;
  }

  @Override
  public void process(List<String> v) {
    JOptionPane.showMessageDialog(null, bundle.getString(v.get(0)), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
  }
}
