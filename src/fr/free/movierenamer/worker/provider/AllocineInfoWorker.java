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
import fr.free.movierenamer.parser.AllocineInfo;
import fr.free.movierenamer.parser.MrParser;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.worker.MovieInfoWorker;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;

/**
 * Class AllocineInfoWorker
 * 
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
public class AllocineInfoWorker extends MovieInfoWorker {

  /**
   * Constructor arguments
   * 
   * @param errorSupport Swing change support
   * @param id Media id
   * @throws ActionNotValidException
   */
  public AllocineInfoWorker(PropertyChangeSupport errorSupport, MediaID id) throws ActionNotValidException {
    super(errorSupport, id);
    if (id.getType() != MediaID.MediaIdType.ALLOCINEID) {
      throw new ActionNotValidException("AllocineInfoWorker can only use allocine ID");
    }
  }

  @Override
  protected String getUri() throws Exception {
    return Settings.allocineAPIInfo.replace("MEDIA", "movie") + id.getID();
  }

  @Override
  protected MrParser<MovieInfo> getParser() throws Exception {
    return new AllocineInfo();
  }

  @Override
  protected MediaImages loadExtraImages() throws Exception {
    MediaImages mediaImage = null;
    try {
      XbmcPassionIDLookupWorker xbl = new XbmcPassionIDLookupWorker(errorSupport, id);
      MediaID imdbId = xbl.executeInBackground();
      if (imdbId != null) {
        TmdbImageWorker imgWorker = new TmdbImageWorker(errorSupport, imdbId);
        mediaImage = imgWorker.executeInBackground();
      }

    } catch (ActionNotValidException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }

    return mediaImage;
  }

}
