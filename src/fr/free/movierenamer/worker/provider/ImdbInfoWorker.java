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

import fr.free.movierenamer.worker.MovieInfoWorker;

import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.media.movie.MovieImage;
import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.parser.xml.ImdbImage;
import fr.free.movierenamer.parser.xml.ImdbInfo;
import fr.free.movierenamer.parser.xml.MrParser;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Settings;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * Class ImdbInfoWorker , get movie information from imdb
 * 
 * @author Magré Nicolas
 * @author QUÉMÉNEUR Simon
 */
public class ImdbInfoWorker extends MovieInfoWorker {

  /**
   * Constructor arguments
   * 
   * @param errorSupport
   *          Swing change support
   * @param id
   *          Media API ID
   * @throws ActionNotValidException
   */
  public ImdbInfoWorker(SwingPropertyChangeSupport errorSupport, MediaID id) throws ActionNotValidException {
    super(errorSupport, id);
    if (id.getType() != MediaID.IMDBID) {
      throw new ActionNotValidException("ImdbInfoWorker can only use imdb ID");
    }
  }

  @Override
  protected String getSearchUri() throws Exception {
    return (config.movieScrapperFR ? Settings.imdbMovieUrl_fr : Settings.imdbMovieUrl) + id.getID() + "/combined";
  }

  @Override
  protected MrParser<MovieInfo> getInfoParser() throws Exception {
    return new ImdbInfo();
  }

  @Override
  protected MrParser<MovieImage> getImageParser() throws Exception {
    return new ImdbImage();
  }

}
