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

import fr.free.movierenamer.media.Media;
import fr.free.movierenamer.media.MediaPerson;
import fr.free.movierenamer.media.movie.MovieImage;
import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.media.tvshow.TvShowInfo;
import fr.free.movierenamer.ui.MoviePanel;
import fr.free.movierenamer.ui.res.SearchResult;
import fr.free.movierenamer.utils.Settings;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.SwingWorker;

/**
 * Class WorkerManager
 * @author Nicolas Magré
 */
public abstract class WorkerManager {

  public static SwingWorker<ArrayList<SearchResult>, Void> getSearchWorker(Media media, Settings setting) {
    SwingWorker<ArrayList<SearchResult>, Void> worker = null;
    try {
      switch (media.getType()) {
        case Media.MOVIE:
          worker = new ImdbSearchWorker(media.getSearch(), setting);
          break;
        case Media.TVSHOW:
          worker = new TvdbSearchWorker(media.getSearch(), setting);
          break;
        default :
          break;
      }
    } catch (MalformedURLException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (UnsupportedEncodingException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }
    return worker;
  }
  
  public static SwingWorker<MovieInfo, Void> getMovieInfoWorker(String imdb, Settings setting) throws MalformedURLException{
      return new ImdbInfoWorker(imdb, setting);
  }
  
  public static SwingWorker<MovieImage, Void> getMovieImageWorker(String imdb, Settings setting){
    return new TheMovieDbImageWorker(imdb, setting);
  }
  
  public static SwingWorker<Void, Void> getMovieActorWorker(ArrayList<MediaPerson> actors,MoviePanel moviePanel, Settings setting){
    return new ActorWorker(actors, moviePanel, setting);
  }
  
  public static SwingWorker<TvShowInfo, Void> getTvShowInfoWorker(String tvdbId, Settings setting) throws MalformedURLException{
    return new TvdbInfoWorker(tvdbId, setting);
  }
}
