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
import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.media.MediaPerson;
import fr.free.movierenamer.media.movie.MovieImage;
import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.media.tvshow.TvShowInfo;
import fr.free.movierenamer.ui.MoviePanel;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.SearchResult;
import fr.free.movierenamer.utils.Settings;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.SwingWorker;

/**
 * Class WorkerManager
 *
 * @author Nicolas Magré
 */
public abstract class WorkerManager {

  private static final int IMDB = 0;
  private static final int TMDB = 1;
  private static final int ALLOCINE = 2;

  /**
   * Get media search worker
   *
   * @param media Media
   * @param setting Movie Renamer settings
   * @return Worker depend of media type and settings or null
   */
  public static SwingWorker<ArrayList<SearchResult>, Void> getSearchWorker(Media media, Settings setting) {
    SwingWorker<ArrayList<SearchResult>, Void> worker = null;
    try {
      switch (media.getType()) {
        case Media.MOVIE:
          switch (setting.scrapper) {
            case IMDB:
              worker = new ImdbSearchWorker(media.getSearch(), setting);
              break;
            case TMDB:
              worker = new TmdbSearchWorker(media.getSearch(), setting);
              break;
            case ALLOCINE:
              worker = new AllocineSearchWorker(media.getSearch(), setting);
              break;
            default:
          }
          break;
        case Media.TVSHOW:
          worker = new TvdbSearchWorker(media.getSearch(), setting);
          break;
        default:
          break;
      }
    } catch (MalformedURLException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (UnsupportedEncodingException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }
    return worker;
  }

  public static SwingWorker<MovieInfo, String> getMovieInfoWorker(MediaID id, Settings setting) throws MalformedURLException, ActionNotValidException {
    SwingWorker<MovieInfo, String> worker = null;
    switch (setting.scrapper) {
      case IMDB:
        worker = new ImdbInfoWorker(id, setting);
        break;
      case TMDB:
        System.out.println("TMDB worker");
        worker = new TmdbInfoWorker(id, setting);
        break;
      case ALLOCINE:
        //A faire
        break;
      default:
    }
    return worker;
  }

  public static SwingWorker<MovieImage, Void> getMovieImageWorker(MediaID id, Settings setting) throws ActionNotValidException {
    return new TmdbImageWorker(id, setting);
  }

  public static SwingWorker<Void, Void> getMovieActorWorker(ArrayList<MediaPerson> actors, MoviePanel moviePanel, Settings setting) {
    return new ActorWorker(actors, moviePanel, setting);
  }

  public static SwingWorker<TvShowInfo, String> getTvShowInfoWorker(MediaID id, Settings setting) throws MalformedURLException, ActionNotValidException {
    return new TvdbInfoWorker(id, setting);
  }
}
