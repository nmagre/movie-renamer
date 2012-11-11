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

import org.junit.Assert;

import fr.free.movierenamer.scrapper.MovieScrapperTest;

/**
 * Class AnidbScrapperTest
 * 
 * @author Simon QUÉMÉNEUR
 */
public class AnidbScrapperTest extends MovieScrapperTest {
  private AnidbScrapper anidb = null;

  @Override
  public void init() {
    anidb = new AnidbScrapper();
  }

  @Override
  public void search() throws Exception {
    Assert.fail();
//    List<Movie> list = anidb.search("princesse mononoke");
//    
//    Movie anime = list.get(0);
//    Assert.assertEquals("Princesse Mononoke", anime.getName());
//    Assert.assertEquals("http://img7.anidb.net/pics/anime/13197.jpg", anime.getURL().toExternalForm());
//    Assert.assertEquals(1997, anime.getYear());
//    Assert.assertEquals(-1, anime.getImdbId());
//    Assert.assertEquals(7, anime.getMediaId());
  }

  @Override
  public void getMovieInfo() throws Exception {
    Assert.fail();
//    anidb.setLocale(Locale.GERMAN);
//    MovieInfo anime = anidb.getInfo(new Movie(7, null, null, -1, -1));
//
//    Assert.assertEquals("?????", anime.getTitle());
  }

  @Override
  public void getCasting() throws Exception {
    Assert.fail();
//    MovieInfo anime = anidb.getInfo(new Movie(7, null, null, -1, -1));
//    Assert.assertEquals("??????", anime.getDirectors().get(0));
  }

  @Override
  public void getImages() throws Exception {
    Assert.fail();
//    List<ImageInfo> images = anidb.getImages(new Movie(7, null, null, -1, -1));
//    Assert.assertEquals("?????", images.get(0).getCategory());
  }
}
