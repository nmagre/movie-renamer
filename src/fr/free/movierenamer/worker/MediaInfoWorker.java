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

import fr.free.movierenamer.parser.MrParser;
import fr.free.movierenamer.utils.SearchResult;
import java.util.List;

import fr.free.movierenamer.media.MediaInfo;
import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.utils.ActionNotValidException;
import java.beans.PropertyChangeSupport;

/**
 * Class MediaInfoWorker
 *
 * @param <T>
 * @author QUÉMÉNEUR Simon
 * @author Nicolas Magré
 */
public abstract class MediaInfoWorker<T extends MediaInfo> extends HttpWorker<T> {

  protected final MediaID id;
  protected final PropertyChangeSupport errorSupport;

  /**
   * Constructor arguments
   *
   * @param errorSupport Swing change support
   * @param id Media ID
   * @throws ActionNotValidException
   */
  public MediaInfoWorker(PropertyChangeSupport errorSupport, MediaID id) throws ActionNotValidException {
    super(errorSupport);
    this.errorSupport = errorSupport;
    this.id = id;
  }
}
