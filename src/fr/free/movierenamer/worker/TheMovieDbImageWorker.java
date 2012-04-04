/******************************************************************************
 *                                                                             *
 *    Movie Renamer                                                            *
 *    Copyright (C) 2012 Magré Nicolas                                         *
 *                                                                             *
 *    Movie Renamer is free software: you can redistribute it and/or modify    *
 *    it under the terms of the GNU General Public License as published by     *
 *    the Free Software Foundation, either version 3 of the License, or        *
 *    (at your option) any later version.                                      *
 *                                                                             *
 *    This program is distributed in the hope that it will be useful,          *
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            *
 *    GNU General Public License for more details.                             *
 *                                                                             *
 *    You should have received a copy of the GNU General Public License        *
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                             *
 ******************************************************************************/
package fr.free.movierenamer.worker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.bind.DatatypeConverter;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.movie.MovieImage;
import fr.free.movierenamer.parser.xml.TheMovieDbImage;
import fr.free.movierenamer.parser.xml.XMLParser;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.ui.res.TmdbResult;
import fr.free.movierenamer.utils.Images;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Class TheMovieDbImageWorker , get images from theMovieDB by imdbID
 * @author Magré Nicolas
 */
public class TheMovieDbImageWorker extends SwingWorker<MovieImage, String> {

  private Settings setting;
  private String imdbId;
  private Component parent;
  private ResourceBundle bundle = ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle");

  /**
   * Constructor arguments
   * @param imdbId Imdb ID (ttxxxxxx)
   * @param parent Parent component to center joptionpane
   * @param setting Movie Renamer settings
   */
  public TheMovieDbImageWorker(String imdbId, Component parent, Settings setting) {
    this.imdbId = imdbId;
    this.setting = setting;
    this.parent = parent;
  }

  @Override
  protected MovieImage doInBackground() throws InterruptedException {
    MovieImage mvImgs = new MovieImage();
    ArrayList<Images> thumbs = new ArrayList<Images>();
    ArrayList<Images> fanarts = new ArrayList<Images>();

    // Try to get XML from theMovieDB
    try {
      String xmlUrl = new String(DatatypeConverter.parseBase64Binary(setting.xurl)) + "/";
      URL url = new URL(setting.imdbAPIUrlMovieId + xmlUrl + imdbId);
      File f = setting.cache.get(url, Cache.theMovieDBXML);
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
              publish(bundle.getString("retImageFailed"));
              return null;
            }
          }
        }
        setting.cache.add(in, url.toString(), Cache.theMovieDBXML);
        f = setting.cache.get(url, Cache.theMovieDBXML);
      }

      // Parse TheMovieDb XML
      XMLParser<TmdbResult> mmp = new XMLParser<TmdbResult>(f.getAbsolutePath());
      mmp.setParser(new TheMovieDbImage());
      try {
        TmdbResult res = mmp.parseXml();
        if (res.getThumbs() != null) {
          setting.getLogger().log(Level.INFO, "  {0} Thumbs", "" + res.getThumbs().size());
          for (int i = 0; i < res.getThumbs().size(); i++) {
            thumbs.add(res.getThumbs().get(i));
          }
        }
        if (res.getFanarts() != null) {
          setting.getLogger().log(Level.INFO, "  {0} Fanarts", "" + res.getFanarts().size());
          for (int i = 0; i < res.getFanarts().size(); i++) {
            fanarts.add(res.getFanarts().get(i));
          }
        }
      } catch (IOException ex) {
        setting.getLogger().log(Level.SEVERE, ex.toString());
      } catch (InterruptedException ex) {
        setting.getLogger().log(Level.WARNING, ex.toString());
        return null;
      } catch (IllegalArgumentException ex) {
        setting.getLogger().log(Level.SEVERE, ex.toString());
      }
    } catch (IOException ex) {
      setting.getLogger().log(Level.SEVERE, ex.toString());
    }

    mvImgs.setThumbs(thumbs);
    mvImgs.setFanarts(fanarts);
    
    return mvImgs;
  }

  @Override
  protected void process(List<String> chunks) {
    JOptionPane.showMessageDialog(parent, chunks.get(0), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
  }
}
