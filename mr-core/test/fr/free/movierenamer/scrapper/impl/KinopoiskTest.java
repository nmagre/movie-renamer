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
package fr.free.movierenamer.scrapper.impl;

import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.scrapper.MovieScrapperTest;
import fr.free.movierenamer.scrapper.impl.movie.KinopoiskScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.utils.ScrapperUtils;

/**
 * Class KinopoiskTest
 *
 * @author Nicolas Magré
 */
public class KinopoiskTest extends MovieScrapperTest {

  private KinopoiskScrapper kinopoist = null;

  @Override
  public void init() {
    kinopoist = new KinopoiskScrapper();
  }

  @Override
  public void search() throws Exception {
//    List<Movie> results = kinopoist.search("matrix");
//
//    Movie movie = results.get(0);
//
//    Assert.assertEquals("Матрица", movie.getName());
//    Assert.assertEquals("The Matrix", movie.getOriginalTitle());
//    Assert.assertEquals("http://st.kinopoisk.ru/images/sm_film/301.jpg", movie.getURL().toExternalForm());
//    Assert.assertEquals(1999, movie.getYear());
//    Assert.assertEquals(301, movie.getMediaId().getId());
  }

  @Override
  public void getMovieInfo() throws Exception {
    MovieInfo movie = kinopoist.getInfo(new Movie(null, new IdInfo(251733, ScrapperUtils.AvailableApiIds.KINOPOISK), null, null, null, -1));
  }

  @Override
  public void getCasting() throws Exception {

  }

  @Override
  public void getImages() throws Exception {

  }

}
