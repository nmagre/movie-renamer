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
package fr.free.movierenamer.scrapper.impl;

import static org.junit.Assert.assertEquals;

import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.searchinfo.Movie;
import java.util.List;
import java.util.Locale;
import org.junit.Test;

/**
 * Class IMDBScrapperTest
 * 
 * @author Simon QUÉMÉNEUR
 */
public class IMDBScrapperTest extends AbstractScrapperTest {
  private final IMDBScrapper imdb = new IMDBScrapper();

  @Test
  public void search() throws Exception {
    List<Movie> results = imdb.search("il était une fois dans l'ouest", Locale.FRENCH);

    Movie movie = results.get(0);

    assertEquals("Il était une fois dans l'ouest", movie.getName());
    assertEquals(1968, movie.getYear());
    assertEquals(64116, movie.getImdbId());
    assertEquals(64116, movie.getMediaId());
  }

  @Test
  public void getMovieInfo() throws Exception {
    MovieInfo movie = imdb.getInfo(new Movie(64116, null, null, -1, -1), Locale.FRENCH);

    assertEquals("Il était une fois dans l'ouest", movie.getTitle());
    assertEquals("Sergio Leone", movie.getDirectors().get(0));
    assertEquals(Integer.valueOf(175), Integer.valueOf(movie.getRuntime()));
  }
}
