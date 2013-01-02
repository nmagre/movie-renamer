/*
 * movie-renamer-core
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
package fr.free.movierenamer.scrapper;

import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;

/**
 * Class MovieScrapper
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class MovieScrapper extends MediaScrapper<Movie, MovieInfo> {

  protected MovieScrapper(AvailableLanguages... supportedLanguages) {
    super(supportedLanguages);
  }

  // public MovieInfo getMovieInfoByID(int id, Locale language) throws Exception;

  // public MovieInfo getMovieInfoByIMDBID(int imdbid, Locale language) throws Exception;

}
