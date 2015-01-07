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

import fr.free.movierenamer.scrapper.impl.movie.AllocineScrapper;
import java.util.List;

import org.junit.Assert;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.scrapper.MovieScrapperTest;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.utils.ScrapperUtils;

/**
 * Class AllocineScrapperTest
 *
 * @author Simon QUÉMÉNEUR
 */
public class AllocineScrapperTest extends MovieScrapperTest {

  private AllocineScrapper allocine = null;

  @Override
  public void init() {
    allocine = new AllocineScrapper();
  }

  @Override
  public void search() throws Exception {
    List<Movie> results = allocine.search("avatar");

    Movie movie = results.get(0);
    Assert.assertEquals("Avatar", movie.getName());
    //Assert.assertEquals("http://images.allocine.fr/medias/nmedia/18/78/95/70/19485155.jpg", movie.getURL().toExternalForm());
    Assert.assertEquals(2009, movie.getYear());
    Assert.assertEquals(61282, movie.getMediaId().getId());
  }

  @Override
  public void getMovieInfo() throws Exception {
    MovieInfo movie = allocine.getInfo(new Movie(null, new IdInfo(40191, ScrapperUtils.AvailableApiIds.ALLOCINE), null, null, null, -1));

    Assert.assertEquals("Eternal Sunshine of the Spotless Mind", movie.getTitle());
    Assert.assertEquals("108", movie.getRuntime().toString());
    Assert.assertEquals("2004-10-06", movie.getReleasedDate().toString());
    Assert.assertEquals("[Comédie dramatique, Science fiction]", movie.getGenres().toString());
    Assert.assertEquals("[USA]", movie.getCountries().toString());
  }

  @Override
  public void getCasting() throws Exception {
    List<CastingInfo> cast = allocine.getCasting(new Movie(null, new IdInfo(40191, ScrapperUtils.AvailableApiIds.ALLOCINE), null, null, null, -1), new IdInfo(40191, ScrapperUtils.AvailableApiIds.ALLOCINE));
    boolean dir = false, actor = false;
    for (CastingInfo info : cast) {

      if (!dir && info.isDirector()) {
        Assert.assertEquals("Michel Gondry", info.getName());
        dir = true;
      }

      if (!actor && info.isActor()) {
        Assert.assertEquals("Jim Carrey", info.getName());
        actor = true;
      }
    }

    if (!dir || !actor) {
      Assert.fail();
    }
  }

  ;

  @Override
  public void getImages() throws Exception {// TODO
    /*List<ImageInfo> images = allocine.getImages(new Movie(new IdInfo(61282, ScrapperUtils.AvailableApiIds.ALLOCINE), null, null, null, -1));
     Assert.assertEquals(ImageCategoryProperty.thumb, images.get(0).getCategory());
     Assert.assertEquals("http://images.allocine.fr/medias/nmedia/18/64/43/65/19211318.jpg", images.get(1).getHref(ImageInfo.ImageSize.big).toExternalForm());// FIXME
     */
  }
}
