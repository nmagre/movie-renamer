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
import fr.free.movierenamer.parser.xml.MrParser;
import fr.free.movierenamer.parser.xml.TmdbImage;
import fr.free.movierenamer.parser.xml.TmdbInfo;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.worker.MovieInfoWorker;
import java.beans.PropertyChangeSupport;
import javax.xml.bind.DatatypeConverter;

/**
 * Class TmdbInfoWorker, get movie info from the movie database
 * 
 * @author Nicolas Magré
 */
public class TmdbInfoWorker extends MovieInfoWorker {

  /**
   * Constructor arguments
   *
   * @param errorSupport
   * @param id
   * @param setting Movie Renamer settings
   * @throws ActionNotValidException
   */
  public TmdbInfoWorker(PropertyChangeSupport errorSupport, MediaID id) throws ActionNotValidException {
    super(errorSupport, id);
    if (id.getType() != MediaID.MediaIdType.TMDBID) {
      throw new ActionNotValidException("TmdbInfoWorker  can only use tmdb ID");
    }
  }

  @Override
  protected String getSearchUri() throws Exception {
    String uri = Settings.tmdbAPIMovieInf + new String(DatatypeConverter.parseBase64Binary(Settings.xurlMdb)) + "/" + id.getID();
    if (config.movieScrapperFR) {
      uri = uri.replace("/en/", "/fr/");
    }
    return uri;
  }

  @Override
  protected MrParser<MovieInfo> getInfoParser() throws Exception {
    return new TmdbInfo();
  }

  @Override
  protected MrParser<MovieImage> getImageParser() throws Exception {
    return new TmdbImage();
  }

}
