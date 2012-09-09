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

import fr.free.movierenamer.parser.MrParser;

import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.parser.TmdbInfo;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.worker.HttpWorker;
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
   * @throws ActionNotValidException
   */
  public TmdbInfoWorker(PropertyChangeSupport errorSupport, MediaID id) throws ActionNotValidException {
    super(errorSupport, id);
    if (id.getType() != MediaID.MediaIdType.TMDBID) {
      throw new ActionNotValidException("TmdbInfoWorker can only use tmdb ID");
    }
  }

  @Override
  protected String getUri() throws Exception {
    String uri = Settings.tmdbAPIMovieInf + new String(DatatypeConverter.parseBase64Binary(Settings.xurlMdb)) + "/" + id.getID();
    
    switch(config.movieScrapperLang){
      case FRENCH:
        uri = uri.replace("/en/", "/fr/");
        break;
      case ITALIAN:
        uri = uri.replace("/en/", "/it/");
        break;
      case SPANISH:
        uri = uri.replace("/en/", "/es/");
        break;
      case ENGLISH:
      default:
        break;
    }
    return uri;
  }

  @Override
  protected MrParser<MovieInfo> getParser() throws Exception {
    return new TmdbInfo();
  }
}
