/*
 * movie-renamer
 * Copyright (C) 2012 QUÉMÉNEUR Simon
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
import fr.free.movierenamer.media.movie.MovieImage;
import fr.free.movierenamer.parser.xml.MrParser;
import fr.free.movierenamer.utils.ActionNotValidException;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * Class ImdbImageWorker
 * 
 * @author QUÉMÉNEUR Simon
 */
public class ImdbImageWorker extends MediaImageWorker {

  /**
   * @param errorSupport
   * @param id
   * @throws ActionNotValidException
   */
  public ImdbImageWorker(SwingPropertyChangeSupport errorSupport, MediaID id) throws ActionNotValidException {
    super(errorSupport, id);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.free.movierenamer.worker.HttpWorker#getSearchUri()
   */
  @Override
  protected String getSearchUri() throws Exception {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.free.movierenamer.worker.MediaImageWorker#getImageParser()
   */
  @Override
  protected MrParser<MovieImage> getImageParser() throws Exception {
    return null;
  }

}
