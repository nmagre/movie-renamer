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

import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.parser.ImdbParser;
import fr.free.movierenamer.utils.HttpGet;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.net.MalformedURLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * Class ImdbInfoWorker , get movie information from imdb
 *
 * @author Magré Nicolas
 */
public class ImdbInfoWorker extends SwingWorker<MovieInfo, String> {

  private static final int RETRY = 3;
  private HttpGet http;
  private String imdbId;
  private Settings setting;
  private ResourceBundle bundle = ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle");


  /**
   * Constructor arguments
   *
   * @param imdbId Imdb Id
   * @param setting Movie Renamer settings
   * @throws MalformedURLException
   */
  public ImdbInfoWorker(String imdbId, Settings setting) throws MalformedURLException {
    http = new HttpGet((setting.imdbFr ? setting.imdbMovieUrl_fr : setting.imdbMovieUrl) + imdbId + "/combined");
    this.setting = setting;
    this.imdbId = imdbId;
  }

  @Override
  protected MovieInfo doInBackground() {

    setProgress(0);
    String res = null;
    for (int i = 0; i < RETRY; i++) {
      try {
        res = http.sendGetRequest(true, "ISO-8859-15");
        break;
      } catch (Exception e) {//Don't care about exception, res will be null
        Settings.LOGGER.log(Level.SEVERE, null, e);
        try {
          Thread.sleep(300);
        } catch (InterruptedException ex) {
          Settings.LOGGER.log(Level.SEVERE, null, ex);
        }
      }
    }

    if (res == null) {//Http request failed
      publish("httpFailed");
      return null;
    }

    setProgress(80);

    ImdbParser imdbParser = new ImdbParser(setting);
    MovieInfo mvi = null;
    try {
      mvi = imdbParser.getMovieInfo(res);
    } catch (IndexOutOfBoundsException ex) {//Imdbparser failed
      Settings.LOGGER.log(Level.SEVERE, Utils.getStackTrace("IndexOutOfBoundsException", ex.getStackTrace()));
      publish("imdbParserFail");
    }

    if (mvi != null) {
      mvi.setImdbId(imdbId);
    }
    else {
      Settings.LOGGER.log(Level.SEVERE, null, "Imdbparser failed and we don't know why");
      publish("imdbParserFail");
    }

    setProgress(100);
    return mvi;
  }

  @Override
  public void process(List<String> v) {
    JOptionPane.showMessageDialog(null, bundle.getString(v.get(0)), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
  }
}
