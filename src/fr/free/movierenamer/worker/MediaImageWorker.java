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
import fr.free.movierenamer.media.MediaImages;
import fr.free.movierenamer.parser.MrParser;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Utils;
import java.beans.PropertyChangeSupport;
import java.io.File;

/**
 * Class MediaImageWorker
 * 
 * @param <T>
 * @author Nicolas Magré
 */
public abstract class MediaImageWorker extends HttpWorker<MediaImages> {

  protected final MediaID id;

  /**
   * Constructor arguments
   * 
   * @param errorSupport Swing change support
   * @param id
   * @throws ActionNotValidException
   */
  public MediaImageWorker(PropertyChangeSupport errorSupport, MediaID id) throws ActionNotValidException {
    super(errorSupport);
    this.id = id;
  }

  @Override
  protected final MediaImages fileAnalysis(File xmlFile) throws Exception {
    MediaImages movieImage = Utils.parseFile(xmlFile, getParser());

    if (movieImage == null) {
      firePropertyChange("closeLoadingDial", "scrapperInfoFailed");
      return null;
    }

    setProgress(100);
    return movieImage;
  }

  @Override
  protected abstract String getUri() throws Exception;

  protected abstract MrParser<MediaImages> getParser() throws Exception;
}
