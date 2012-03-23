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

import java.net.MalformedURLException;
import javax.swing.SwingWorker;
import fr.free.movierenamer.utils.HttpGet;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.movie.MovieInfo;
import fr.free.movierenamer.parser.ImdbParser;
import java.awt.Component;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;

/**
 * Class ImdbInfoWorker , get movie information from imdb
 * @author Magré Nicolas
 */
public class ImdbInfoWorker extends SwingWorker<MovieInfo, String> {

  private HttpGet http;
  private ImdbParser imdbParser;
  private Component parent;
  private String imdbId;
  private ResourceBundle bundle = ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle");

  /**
   * Constructor arguments
   * @param parent Parent component to center joptionpane
   * @param imdbId Imdb Id
   * @param setting Movie Renamer settings
   * @throws MalformedURLException
   */
  public ImdbInfoWorker(Component parent, String imdbId, Settings setting) throws MalformedURLException {
    this.parent = parent;
    http = new HttpGet((setting.imdbFr ? setting.imdbMovieUrl_fr:setting.imdbMovieUrl) + imdbId + "/combined");
    imdbParser = new ImdbParser(setting);
    this.imdbId = imdbId;
  }

  @Override
  protected MovieInfo doInBackground() {
    setProgress(0);
    String res = null;
    try{
      res = http.sendGetRequest(true);
    }
    catch(Exception e){
      publish(e.getMessage());
      setProgress(100);
      return null;
    }
    setProgress(80);
    MovieInfo mvi = imdbParser.getMovieInfo(res);
    mvi.setImdbId(imdbId);
    setProgress(100);
    return mvi;
  }

  @Override
  protected void process(List<String> chunks) {
    JOptionPane.showMessageDialog(parent, chunks.get(0), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
  }
}
