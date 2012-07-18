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
package fr.free.movierenamer.worker.provider;

import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.media.movie.MovieImage;
import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.parser.ImdbInfo;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.worker.HttpWorker;
import fr.free.movierenamer.worker.MovieInfoWorker;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;

/**
 * Class ImdbInfoWorker , get movie information from imdb
 *
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
public class ImdbInfoWorker extends MovieInfoWorker {

  /**
   * Constructor arguments
   *
   * @param errorSupport Swing change support
   * @param id Media API ID
   * @throws ActionNotValidException
   */
  public ImdbInfoWorker(PropertyChangeSupport errorSupport, MediaID id) throws ActionNotValidException {
    super(errorSupport, id);
    if (id.getType() != MediaID.MediaIdType.IMDBID) {
      throw new ActionNotValidException("ImdbInfoWorker can only use imdb ID");
    }
  }

  @Override
  protected final MovieInfo executeInBackground() throws Exception {
    HttpWorker<MovieInfo> httpWorker = new HttpWorker<MovieInfo>(errorSupport);
    httpWorker.setUri((config.movieScrapperFR ? Settings.imdbMovieUrl_fr : Settings.imdbMovieUrl) + id.getID() + "/combined");
    httpWorker.setParser(new ImdbInfo());
    httpWorker.execute();

    MovieInfo movieInfo = httpWorker.get();// Wait for movie info

    MovieImage mediaImage = null;
    try {
      TmdbImageWorker imgWorker = new TmdbImageWorker(errorSupport, id);
      imgWorker.execute();
      mediaImage = imgWorker.get();// Wait for movie images
    } catch (ActionNotValidException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }

    if (mediaImage != null) {
      movieInfo.setImages(mediaImage);
    }

    return movieInfo;
  }
}
