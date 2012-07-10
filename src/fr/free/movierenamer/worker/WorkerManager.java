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
import fr.free.movierenamer.media.MediaImage;
import fr.free.movierenamer.media.MediaPerson;
import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.media.tvshow.SxE;
import fr.free.movierenamer.media.tvshow.TvShowSeason;
import fr.free.movierenamer.ui.MoviePanel;
import fr.free.movierenamer.ui.res.IMediaPanel;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.SearchResult;
import fr.free.movierenamer.utils.Settings;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * Class WorkerManager
 *
 * @author Nicolas Magré
 */
public abstract class WorkerManager {

  private static final int IMDB = 0;
  private static final int TMDB = 1;
  private static final int ALLOCINE = 2;
  private static final int TVDB = 0;
  private static final int ALLOCINETV = 1;

  /**
   * Get media search worker
   *
   * @param errorSupport
   * @param media Media
   * @param setting Movie Renamer settings
   * @return Worker depend of media type and settings or null
   */
  public static SwingWorker<ArrayList<SearchResult>, String> getSearchWorker(SwingPropertyChangeSupport errorSupport, Media media, Settings setting) {
    SwingWorker<ArrayList<SearchResult>, String> worker = null;
    Settings.LOGGER.log(Level.INFO, "Search : {0}", media.getSearch());

    switch (media.getType()) {
      case Media.MOVIE:
        switch (setting.movieScrapper) {
          case IMDB:
            worker = new ImdbSearchWorker(errorSupport, media.getSearch(), setting);
            break;
          case TMDB:
            worker = new TmdbSearchWorker(errorSupport, media.getSearch(), setting);
            break;
          case ALLOCINE:
            worker = new AllocineSearchWorker(errorSupport, false, media.getSearch(), setting);
            break;
          default:
        }
        break;
      case Media.TVSHOW:
        switch (setting.tvshowScrapper) {
          case TVDB:
            worker = new TvdbSearchWorker(errorSupport, media.getSearch(), setting);
            break;
          case ALLOCINETV:
            worker = new AllocineSearchWorker(errorSupport, true, media.getSearch(), setting);
            break;
          default:
        }
        break;
      default:
        break;
    }

    return worker;
  }

  public static SwingWorker<MovieInfo, String> getMovieInfoWorker(SwingPropertyChangeSupport errorSupport, MediaID id, Settings setting) throws ActionNotValidException {
    SwingWorker<MovieInfo, String> worker = null;
    switch (setting.movieScrapper) {
      case IMDB:
        worker = new ImdbInfoWorker(errorSupport, id, setting);
        break;
      case TMDB:
        worker = new TmdbInfoWorker(errorSupport, id, setting);
        break;
      case ALLOCINE:
        worker = new AllocineInfoWorker(errorSupport, id, setting);
        break;
      default:
    }
    Settings.LOGGER.log(Level.INFO, "Information API ID : {0}", id.getID());
    return worker;
  }

  public static SwingWorker<Void, Void> getMediaImageWorker(ArrayList<MediaImage> array, int cache, IMediaPanel mediaPanel, Settings setting) {
    return new MediaImageWorker(array, cache, mediaPanel, setting);
  }

  public static SwingWorker<Void, Void> getMovieActorWorker(ArrayList<MediaPerson> actors, MoviePanel moviePanel, Settings setting) {
    return new ActorWorker(actors, moviePanel, setting);
  }

  public static SwingWorker<ArrayList<TvShowSeason>, String> getTvShowInfoWorker(SwingPropertyChangeSupport errorSupport, MediaID id, SxE sxe, Settings setting) throws ActionNotValidException {
    SwingWorker<ArrayList<TvShowSeason>, String> worker = null;
    switch (setting.tvshowScrapper) {
      case TVDB:
        worker = new TvdbInfoWorker(errorSupport, id, setting);
        break;
      case ALLOCINETV:
        worker = new AllocineInfoTvWorker(errorSupport, id, sxe, setting);
        break;
      default:
    }
    return worker;
  }
}
