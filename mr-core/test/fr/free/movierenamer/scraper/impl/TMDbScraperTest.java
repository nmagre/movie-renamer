/*
 * movie-renamer-core
 * Copyright (C) 2012 Nicolas MagrÃ©
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

import fr.free.movierenamer.scraper.impl.movie.TMDbScraper;
import java.util.List;

import org.junit.Assert;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.ImageInfo.ImageCategoryProperty;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.scraper.MovieScraperTest;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.ScraperUtils;

/**
 * Class TMDbScraperTest
 *
 * @author Simon QUÉMÉNEUR
 */
public class TMDbScraperTest extends MovieScraperTest {

  private TMDbScraper tmdb = null;

  @Override
  public void init() {
    tmdb = new TMDbScraper();
  }

  @Override
  public void search() throws Exception {
    tmdb.setLanguage(LocaleUtils.AvailableLanguages.zh);
    List<Movie> results = tmdb.search("pulp fiction");

    Movie movie = results.get(0);

    Assert.assertEquals("低俗小说", movie.getName());
    Assert.assertNotNull(movie.getURL());
    Assert.assertEquals(1994, movie.getYear());
    Assert.assertEquals(680, movie.getMediaId().getId());
  }

  @Override
  public void getMovieInfo() throws Exception {
    tmdb.setLanguage(LocaleUtils.AvailableLanguages.de);
    MovieInfo movie = tmdb.getInfo(new Movie(null, new IdInfo(1858, ScraperUtils.AvailableApiIds.THEMOVIEDB), null, null, null, -1));

    Assert.assertEquals(Integer.valueOf(1858), movie.getId(ScraperUtils.AvailableApiIds.THEMOVIEDB));
    Assert.assertEquals(Integer.valueOf(418279), movie.getId(ScraperUtils.AvailableApiIds.IMDB));
    Assert.assertEquals("Transformers", movie.getTitle());
    Assert.assertEquals("2007-07-02", movie.getReleasedDate().toString());
    Assert.assertEquals("[Action, Abenteuer, Science Fiction, Thriller]", movie.getGenres().toString());

  }

  @Override
  public void getCasting() throws Exception {
    List<CastingInfo> cast = tmdb.getCasting(new Movie(null, new IdInfo(1858, ScraperUtils.AvailableApiIds.THEMOVIEDB), null, null, null, -1), new IdInfo(1858, ScraperUtils.AvailableApiIds.THEMOVIEDB));
    boolean dir = false, actor = false;
    for (CastingInfo info : cast) {
      if (!dir && info.isDirector()) {
        Assert.assertEquals("Michael Bay", info.getName());
        dir = true;
      }
      if (!actor && info.isActor()) {
        Assert.assertEquals("Shia LaBeouf", info.getName());
        actor = true;
      }
    }

    if (!dir || !actor) {
      Assert.fail();
    }
  }

  ;

  @Override
  public void getImages() throws Exception {
    List<ImageInfo> images = tmdb.getImages(new Movie(null, new IdInfo(1858, ScraperUtils.AvailableApiIds.THEMOVIEDB), null, null, null, -1));

    Assert.assertEquals(ImageCategoryProperty.fanart, images.get(0).getCategory());
    Assert.assertEquals(Integer.valueOf(1920), images.get(0).getWidth());
    Assert.assertEquals(Integer.valueOf(1080), images.get(0).getHeight());
  }
}
