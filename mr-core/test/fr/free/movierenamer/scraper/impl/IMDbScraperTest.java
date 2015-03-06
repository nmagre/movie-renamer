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
package fr.free.movierenamer.scraper.impl;

import fr.free.movierenamer.scraper.impl.movie.IMDbScraper;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.ImageInfo.ImageCategoryProperty;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.scraper.MovieScraperTest;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.utils.DateFormat;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.ScraperUtils;

/**
 * Class IMDbScraperTest
 *
 * @author Simon QUÉMÉNEUR
 */
public class IMDbScraperTest extends MovieScraperTest {

  private IMDbScraper imdb = null;

  @Override
  public void init() {
    imdb = new IMDbScraper();
  }

  @Override
  public void search() throws Exception {
    imdb.setLanguage(LocaleUtils.AvailableLanguages.fr);
    List<Movie> results = imdb.search("il était une fois dans l'ouest");

    Movie movie = results.get(0);

    Assert.assertEquals("Il était une fois dans l'ouest", movie.getName());
    Assert.assertEquals("http://ia.media-imdb.com/images/M/MV5BMTEyODQzNDkzNjVeQTJeQWpwZ15BbWU4MDgyODk1NDEx._V1_SX32_CR0,0,32,44_AL_.jpg", movie.getURL());
    Assert.assertEquals(1968, movie.getYear());
    Assert.assertEquals(64116, movie.getImdbId().getId());
  }

  @Test
  public void searchOneResult() throws Exception {
    imdb.setLanguage(LocaleUtils.AvailableLanguages.fr);
    List<Movie> results = imdb.search("le pont de la rivière kwai");

    Movie movie = results.get(0);

    Assert.assertEquals("Le pont de la rivière Kwaï", movie.getName());
    Assert.assertEquals("http://ia.media-imdb.com/images/M/MV5BMTc2NzA0NTEwNF5BMl5BanBnXkFtZTcwMzA0MTk3OA@@..jpg", movie.getURL());
    Assert.assertEquals(1957, movie.getYear());
    Assert.assertEquals(50212, movie.getImdbId().getId());

  }

  @Override
  public void getMovieInfo() throws Exception {
    imdb.setLanguage(LocaleUtils.AvailableLanguages.it);
    MovieInfo movie = imdb.getInfo(new Movie(new IdInfo(64116, ScraperUtils.AvailableApiIds.IMDB), null, null, null, null, -1));

    Assert.assertEquals("C'era una volta il West", movie.getTitle());
    Assert.assertEquals(Integer.valueOf(175), Integer.valueOf(movie.getRuntime()));
    Assert.assertEquals("There were three men in her life. One to take her... one to love her... and one to kill her.", movie.getTagline());// language restriction
    Assert.assertEquals(new DateFormat(1968, 12, 21), movie.getReleasedDate());
    Assert.assertEquals("Rated PG-13 for western violence and brief sensuality (Mpaa re-rating) (2003)", movie.getCertification());
    Assert.assertEquals("-12", movie.getCertification(MovieInfo.MotionPictureRating.FRANCE));
  }

  @Override
  public void getCasting() throws Exception {
    boolean success = false;
    List<CastingInfo> cast = imdb.getCasting(new Movie(new IdInfo(64116, ScraperUtils.AvailableApiIds.IMDB), null, null, null, null, -1), new IdInfo(64116, ScraperUtils.AvailableApiIds.IMDB));
    for (CastingInfo info : cast) {
      if (info.isDirector()) {
        success = "Sergio Leone".equals(info.getName());
      }
    }

    Assert.assertTrue(success);
  }

  @Override
  public void getImages() throws Exception {
    List<ImageInfo> images = imdb.getImages(new Movie(new IdInfo(64116, ScraperUtils.AvailableApiIds.IMDB), null, null, null, null, -1));
    Assert.assertEquals(ImageCategoryProperty.fanart, images.get(0).getCategory());
    Assert.assertEquals("http://cf2.imgobject.com/t/p/original/sNaQWTsQFfdhjKweXbgjSHKZ8YS.jpg", images.get(1).getHref(ImageInfo.ImageSize.big).toExternalForm());
  }
}
