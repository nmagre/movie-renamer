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
import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.parser.MrParser;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Utils;
import java.beans.PropertyChangeSupport;
import java.io.File;

/**
 * Class MovieInfoWorker
 * 
 * @author QUÉMÉNEUR Simon
 * @author Nicolas Magré
 */
public abstract class MovieInfoWorker extends MediaInfoWorker<MovieInfo> {

  /**
   * Constructor arguments
   * 
   * @param errorSupport Swing change support
   * @param id Movie API ID
   * @throws ActionNotValidException
   */
  public MovieInfoWorker(PropertyChangeSupport errorSupport, MediaID id) throws ActionNotValidException {
    super(errorSupport, id);
  }

  @Override
  protected final MovieInfo fileAnalysis(File xmlFile) throws Exception {
    MovieInfo info = Utils.parseFile(xmlFile, getParser());
    MediaImages extraImages = loadExtraImages();
    if (extraImages != null) {
      info.addImages(extraImages);
    }
    return info;
  }

  protected abstract MrParser<MovieInfo> getParser() throws Exception;

  // @Override
  // protected final MovieInfo processFile(File xmlFile) throws Exception {
  // MovieInfo info = super.processFile(xmlFile);
  // MediaImages extraImages = loadExtraImages();
  // if(extraImages != null) {
  // info.addImages(extraImages);
  // }
  // return info;
  // }

  protected MediaImages loadExtraImages() throws Exception {
    return null;
  }

}
