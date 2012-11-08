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
 * Class AllocineScrapperTest
 * 
 * @author Simon QUÉMÉNEUR
 */
public class AllocineScrapperTest extends AbstractScrapperTest {
  private final AllocineScrapper allocine = new AllocineScrapper();

  @Test
  public void search() throws Exception {
    List<Movie> results = allocine.search("avatar", Locale.FRENCH);

    Movie movie = results.get(0);
    assertEquals("Avatar", movie.getName());
    assertEquals(2009, movie.getYear());
    assertEquals(-1, movie.getImdbId());
    assertEquals(61282, movie.getMediaId());
  }

  @Test
  public void getMovieInfo() throws Exception {
    MovieInfo movie = allocine.getInfo(new Movie(40191, null, null, -1, -1), Locale.FRENCH);

    assertEquals("Eternal Sunshine of the Spotless Mind", movie.getTitle());
    assertEquals("2004-10-06", movie.getReleasedDate().toString());
    assertEquals("[Comédie dramatique, Science fiction]", movie.getGenres().toString());
    assertEquals("Jim Carrey", movie.getActors().get(0));
    assertEquals("Michel Gondry", movie.getDirectors().get(0));
  }
}
