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
import fr.free.movierenamer.parser.ImdbParser;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.HttpGet;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * Class ImdbInfoWorker , get movie information from imdb
 *
 * @author Magré Nicolas
 */
public class ImdbInfoWorker extends SwingWorker<MovieInfo, String> {

  private static final int RETRY = 3;
  private HttpGet http;
  private MediaID id;
  private Settings setting;
  private SwingPropertyChangeSupport errorSupport;
  private ResourceBundle bundle = ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle");

  /**
   * Constructor arguments
   *
   * @param errorSupport Swing change support
   * @param id Movie API ID (imdb)
   * @param setting Movie Renamer settings
   * @throws ActionNotValidException
   */
  public ImdbInfoWorker(SwingPropertyChangeSupport errorSupport, MediaID id, Settings setting) throws ActionNotValidException {
    this.errorSupport = errorSupport;
    if (id.getType() != MediaID.IMDBID) {
      throw new ActionNotValidException("ImdbInfoWorker can only use imdb ID");
    }
    this.setting = setting;
    this.id = id;
  }

  @Override
  protected MovieInfo doInBackground() {

    setProgress(0);
    String res = null;
    for (int i = 0; i < RETRY; i++) {
      try {
        http = new HttpGet((setting.imdbFr ? setting.imdbMovieUrl_fr : setting.imdbMovieUrl) + id.getID() + "/combined");
        res = http.sendGetRequest(true, "ISO-8859-15");
        break;
      } catch (Exception ex) {//Don't care about exception, "res" will be null
        Settings.LOGGER.log(Level.SEVERE, null, ex);
        try {
          Thread.sleep(300);
        } catch (InterruptedException e) {
          Settings.LOGGER.log(Level.SEVERE, null, e);
        }
      }
    }

    if (res == null) {//Http request failed
      errorSupport.firePropertyChange("closeLoadingDial", false, true);
      publish("httpFailed");
      return null;
    }

    setProgress(80);

    ImdbParser imdbParser = new ImdbParser(setting);
    MovieInfo mvi = null;
    try {
      mvi = imdbParser.getMovieInfo(res);
    } catch (Exception ex) {//Imdbparser failed
      Settings.LOGGER.log(Level.SEVERE, Utils.getStackTrace("Exception", ex.getStackTrace()));
    }

    if (mvi == null) {
      errorSupport.firePropertyChange("closeLoadingDial", false, true);
      publish("imdbParserFail");
      return null;
    }

    mvi.addID(id);

    try {
      TmdbImageWorker imgWorker = new TmdbImageWorker(errorSupport, id, setting);
      imgWorker.execute();
      MovieImage mvimg = imgWorker.get();
      if (mvimg != null) {
        mvi.setImages(mvimg);
      }
    } catch (InterruptedException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (ExecutionException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (ActionNotValidException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }

    setProgress(100);
    return mvi;
  }

  @Override
  public void process(List<String> v) {
    JOptionPane.showMessageDialog(null, bundle.getString(v.get(0)), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
  }
}
