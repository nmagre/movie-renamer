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
import fr.free.movierenamer.parser.MrParser;
import fr.free.movierenamer.parser.TmdbImage;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.worker.MediaImageWorker;
import java.beans.PropertyChangeSupport;
import javax.xml.bind.DatatypeConverter;

/**
 * Class TmdbImageWorker
 *
 * @author Nicolas Magré
 */
public class TmdbImageWorker extends MediaImageWorker<MovieImage> {

  public TmdbImageWorker(PropertyChangeSupport errorSupport, MediaID id) throws ActionNotValidException {
    super(errorSupport, id);
  }

  @Override
  protected String getUri() throws Exception {
    return Settings.tmdbAPIV3Images.replace("ID", id.getID())  + new String(DatatypeConverter.parseBase64Binary(Settings.xurlMdb));
  }

  @Override
  protected MrParser<MovieImage> getParser() throws Exception {
    return new TmdbImage();
  }
}
