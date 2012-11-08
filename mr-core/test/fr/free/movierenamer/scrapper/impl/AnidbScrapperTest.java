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

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Locale;

import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.scrapper.MovieScrapperTest;
import fr.free.movierenamer.searchinfo.Movie;

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
    List<Movie> list = anidb.search("princesse mononoke");
    
    Movie anime = list.get(0);
    assertEquals("Princesse Mononoke", anime.getName());
    assertEquals("http://img7.anidb.net/pics/anime/13197.jpg", anime.getURL().toExternalForm());
    assertEquals(1997, anime.getYear());
    assertEquals(-1, anime.getImdbId());
    assertEquals(7, anime.getMediaId());
  }

  @Override
  public void getMovieInfo() throws Exception {
    anidb.setLocale(Locale.GERMAN);
    MovieInfo anime = anidb.getInfo(new Movie(7, null, null, -1, -1));

    assertEquals("I?????", anime.getTitle());
  }

  @Override
  public void getCasting() throws Exception {
    MovieInfo anime = anidb.getInfo(new Movie(7, null, null, -1, -1));
    assertEquals("??????", anime.getDirectors().get(0));
  };

  @Override
  public void getImages() throws Exception {
    List<ImageInfo> images = anidb.getImages(new Movie(7, null, null, -1, -1));
    assertEquals("?????", images.get(0).getCategory());
  }
}
