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

import fr.free.movierenamer.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.bind.DatatypeConverter;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.movie.Movie;
import fr.free.movierenamer.movie.MovieInfo;
import fr.free.movierenamer.parser.XMLParser;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.ui.res.tmdbResult;

/**
 *
 * @author duffy
 */
public class TheMovieDbInfoWorker extends SwingWorker<Movie, Void> {

  private Movie movie;
  private Settings setting;

  public TheMovieDbInfoWorker(Movie movie, Settings setting) {
    this.movie = movie;
    this.setting = setting;
  }

  @Override
  protected Movie doInBackground() throws InterruptedException {
    try {
      String xmlUrl = new String(DatatypeConverter.parseBase64Binary(setting.xurl)) + "/";
      URL url = new URL(setting.imdbAPIUrlMovieId + xmlUrl + movie.getImdbId());
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
            } catch (IOException exe) {//A refaire (not in EDT)
              JOptionPane.showMessageDialog(null, "Can't retreive Movie info\n" + "Try to refresh", "Error", JOptionPane.ERROR_MESSAGE);
              return null;
            }
          }
        }
        setting.cache.add(in, url.toString(), Cache.theMovieDBXML);
        f = setting.cache.get(url, Cache.theMovieDBXML);
      }
      
      XMLParser<tmdbResult> mmp = new XMLParser<tmdbResult>(f.getAbsolutePath(), tmdbResult.class);
      try {
        tmdbResult res = mmp.parseXml();
        if (!res.getId().equals("")) movie.setMovieDBId(res.getId());
        if (res.getThumbs() != null) {
          setting.getLogger().log(Level.INFO, "  {0} Thumbs", "" + res.getThumbs().size());
          for (int i = 0; i < res.getThumbs().size(); i++) {
            movie.addThumb(res.getThumbs().get(i));
          }
        }
        if (res.getFanarts() != null) {
          setting.getLogger().log(Level.INFO, "  {0} Fanarts", "" + res.getFanarts().size());
          for (int i = 0; i < res.getFanarts().size(); i++) {
            movie.addFanart(res.getFanarts().get(i));
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

      if (movie.getMovieDBId().equals(Utils.EMPTY)) return movie;
      XMLParser<MovieInfo> movieinfo = new XMLParser<MovieInfo>(setting.imdbAPIUrlMovieInf + xmlUrl + movie.getMovieDBId(), MovieInfo.class);
      /*try {
        movie.setMovieInfo(movieinfo.parseXml());
      } catch (IOException ex) {
        setting.getLogger().log(Level.SEVERE, "{0}\n{1}{2}", new Object[]{ex.toString(), setting.imdbAPIUrlMovieInf, movie.getMovieDBId()});
      } catch (InterruptedException ex) {
        setting.getLogger().log(Level.WARNING, ex.toString());
        return null;
      }*/
    } catch (IOException ex) {
      Logger.getLogger(TheMovieDbInfoWorker.class.getName()).log(Level.SEVERE, null, ex);
    }
    return movie;
  }
}
