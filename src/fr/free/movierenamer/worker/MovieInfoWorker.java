/*
 * movie-renamer
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
import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.utils.ActionNotValidException;
import java.beans.PropertyChangeSupport;

/**
 * Class MrInfoWorker
 *
 * @author QUÉMÉNEUR Simon
 * @author Nicolas Magré
 */
public abstract class MovieInfoWorker extends MediaInfoWorker<MovieInfo> {

  protected HttpWorker<MovieInfo> movieInfoWorker;
  /**
   * Constructor arguments
   *
   * @param errorSupport Swing change support
   * @param id Movie API ID
   * @param movieInfoWorker 
   * @throws ActionNotValidException
   */
  public MovieInfoWorker(PropertyChangeSupport errorSupport, MediaID id, HttpWorker<MovieInfo> movieInfoWorker) throws ActionNotValidException {
    super(errorSupport, id);
    this.movieInfoWorker = movieInfoWorker;
  }
  // /*
  // * (non-Javadoc)
  // *
  // * @see fr.free.movierenamer.worker.HttpWorker#loadImages(java.lang.Object)
  // */
  // @Override
  // protected final MovieInfo loadImages(MovieInfo mvi) throws Exception {
  // mvi.addID(id);
  // MovieImage movieImage;
  // try {
  // MediaImageWorker imgWorker = getImageWorker();// new TmdbImageWorker(getErrorSupport(), id);
  // if (imgWorker != null) {
  // imgWorker.execute();
  // MovieImage mvimg = imgWorker.get(3, TimeUnit.SECONDS);
  // if (mvimg != null) {
  // mvi.setImages(mvimg);
  // }
  // }
  // } catch (InterruptedException ex) {
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // } catch (ExecutionException ex) {
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // }
  //
  // if (movieImage == null) {
  // firePropertyChange("closeLoadingDial", "scrapperInfoFailed");
  // return null;
  // }
  //
  // movieInfo.setImages(movieImage);
  // if (!movieInfo.getTrailer().equals("")) {
  // String trailer = YTdecodeUrl.getRealUrl(movieInfo.getTrailer(), YTdecodeUrl.HD);
  // if (trailer != null) {
  // movieInfo.setTrailer(trailer);
  // }
  // }
  //
  // return mvi;
  // }
  //
  // protected abstract MediaImageWorker getImageWorker() throws Exception;
  // @Override
  // protected final MovieInfo executeInBackground() throws Exception
  // {
  // setProgress(0);
  // String res = null;
  // for (int i = 0; i < RETRY; i++) {
  // try {
  // http = new HttpGet();
  // res = http.sendGetRequest(true, "ISO-8859-15");
  // break;
  // } catch (Exception ex) {//Don't care about exception, "res" will be null
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // try {
  // Thread.sleep(300);
  // } catch (InterruptedException e) {
  // Settings.LOGGER.log(Level.SEVERE, null, e);
  // }
  // }
  // }
  //
  // if (res == null) {//Http request failed
  // firePropertyChange("closeLoadingDial", "httpFailed");
  // return null;
  // }
  //
  // setProgress(80);
  //
  // MrParser<MovieInfo> imdbParser = gerParser();
  // MovieInfo mvi = null;
  // try {
  // mvi = imdbParser.getMovieInfo(res);
  // } catch (Exception ex) {//Imdbparser failed
  // Settings.LOGGER.log(Level.SEVERE, Utils.getStackTrace("Exception", ex.getStackTrace()));
  // }
  //
  // if (mvi == null) {
  // firePropertyChange("closeLoadingDial", "imdbParserFail");
  // return null;
  // }
  //
  // mvi.addID(id);
  //
  // try {
  // TmdbImageWorker imgWorker = new TmdbImageWorker(getErrorSupport(), id);
  // imgWorker.execute();
  // MovieImage mvimg = imgWorker.get();
  // if (mvimg != null) {
  // mvi.setImages(mvimg);
  // }
  // } catch (InterruptedException ex) {
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // } catch (ExecutionException ex) {
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // } catch (ActionNotValidException ex) {
  // Settings.LOGGER.log(Level.SEVERE, null, ex);
  // }
  //
  // setProgress(100);
  // return mvi;
  // }
}
