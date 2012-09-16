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

import fr.free.movierenamer.media.MediaImages;

import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.parser.ImdbInfo;
import fr.free.movierenamer.parser.MrParser;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Settings;
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
  protected String getUri() throws Exception {
    String url;
    switch(config.movieScrapperLang) {
      case ENGLISH:
        url = Settings.imdbMovieUrl;
        break;
      case FRENCH:
        url = Settings.imdbMovieUrl.replace("com", "fr");
        break;
      case ITALIAN:
        url = Settings.imdbMovieUrl.replace("com", "it");
        break;
      case SPANISH:
        url = Settings.imdbMovieUrl.replace("com", "es");
        break;
      case DEUTSCH:
        url = Settings.imdbMovieUrl.replace("com", "de");
        break;
      default:
        url = Settings.imdbMovieUrl;
        break;
    }
    return url + id.getID() + "/combined";
  }
  
  @Override
  protected MrParser<MovieInfo> getParser() throws Exception {
    return new ImdbInfo();
  }
  
  @Override
  protected MediaImages loadExtraImages() throws Exception {
    MediaImages mediaImage = null;
    try {
      TmdbImageWorker imgWorker = new TmdbImageWorker(errorSupport, id);
      mediaImage = imgWorker.executeInBackground();
    } catch (ActionNotValidException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }

    return mediaImage;
  }

 /* @Override
  protected final MovieInfo executeInBackground() throws Exception {
    
    MovieInfo movieInfo = movieInfoWorker.startAndGet(url + id.getID() + "/combined");// Wait for movie info
    // Get entire synopsis
    if(!movieInfo.getSynopsis().equals("")) {
      HttpWorker<String> hsynopsis = new HttpWorker<String>(errorSupport, new ImdbSynopsis());
      String synopsis = hsynopsis.startAndGet(url + id.getID() + "/plotsummary");// Wait for movie synopsis
      if(synopsis != null && !synopsis.equals("")) {
        movieInfo.setSynopsis(synopsis);
      }
    }

    MovieImages mediaImage = null;
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
  }*/
}
