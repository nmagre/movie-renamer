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

import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.searchinfo.Movie;
import java.util.List;
import java.util.Locale;
import org.junit.Test;

/**
 * Class TMDBScrapperTest
 * 
 * @author Simon QUÉMÉNEUR
 */
public class TMDBScrapperTest extends AbstractScrapperTest {
  private final TMDBScrapper tmdb = new TMDBScrapper();

  @Test
  public void search() throws Exception {
    List<Movie> results = tmdb.search("pulp fiction", Locale.CHINESE);

    Movie movie = results.get(0);

    assertEquals("低俗小说", movie.getName());
    assertEquals(1994, movie.getYear());
    assertEquals(-1, movie.getImdbId());
    assertEquals(680, movie.getMediaId());
  }

  @Test
  public void getMovieInfo() throws Exception {
    MovieInfo movie = tmdb.getInfo(new Movie(1858, null, null, -1, -1), Locale.FRENCH);

    assertEquals("Transformers", movie.getTitle());
    assertEquals("2007-07-03", movie.getReleasedDate().toString());
    assertEquals("[Aventure, Action, Thriller, Science-Fiction]", movie.getGenres().toString());
    assertEquals("Shia LaBeouf", movie.getActors().get(0));
    assertEquals("Michael Bay", movie.getDirectors().get(0));
  }

  @Test
  public void getImages() throws Exception {
    List<ImageInfo> images = tmdb.getImages(new Movie(1858, null, null, -1, -1), Locale.FRENCH);
    assertEquals("backdrop", images.get(0).getCategory());
    assertEquals("http://cf2.imgobject.com/t/p/original/p4OHBbXfxToWF4e36uEhQMSidWu.jpg", images.get(0).getUrl().toString());
  }

}