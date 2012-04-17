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
import java.net.MalformedURLException;
import javax.swing.SwingWorker;

/**
 * Class ImdbInfoWorker , get movie information from imdb
 * @author Magré Nicolas
 */
public class ImdbInfoWorker extends SwingWorker<MovieInfo, Void> {

  private HttpGet http;
  private ImdbParser imdbParser;
  private String imdbId;

  /**
   * Constructor arguments
   * @param parent Parent component to center joptionpane
   * @param imdbId Imdb Id
   * @param setting Movie Renamer settings
   * @throws MalformedURLException
   */
  public ImdbInfoWorker(String imdbId, Settings setting) throws MalformedURLException {
    http = new HttpGet((setting.imdbFr ? setting.imdbMovieUrl_fr:setting.imdbMovieUrl) + imdbId + "/combined");
    imdbParser = new ImdbParser(setting);
    this.imdbId = imdbId;
  }

  @Override
  protected MovieInfo doInBackground() {
    setProgress(0);
    String res;
    try{
      res = http.sendGetRequest(true, "ISO-8859-15");
    }
    catch(Exception e){
      //A refaire
      setProgress(100);
      return null;
    }
    setProgress(80);
    MovieInfo mvi = imdbParser.getMovieInfo(res);
    mvi.setImdbId(imdbId);
    setProgress(100);
    return mvi;
  }
}
