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

/**
 *
 * @author duffy
 */
public class ImdbInfoWorker extends SwingWorker<MovieInfo, Void> {

  private HttpGet http;
  private ImdbParser imdbParser;

  public ImdbInfoWorker(String imdbId, String imdbUrl, boolean french, Settings setting) throws MalformedURLException {
    http = new HttpGet(imdbUrl + imdbId + "/combined");
    System.out.println(imdbUrl + imdbId + "/combined");
    imdbParser = new ImdbParser(french, setting);
  }

  @Override
  protected MovieInfo doInBackground() {
    setProgress(0);
    String res = http.sendGetRequest(true);
    setProgress(80);
    MovieInfo mvi = imdbParser.getMovieInfo(res);
    setProgress(100);
    System.out.println(mvi);
    return mvi;
  }
}
