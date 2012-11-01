/*
 * testMR
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

import fr.free.movierenamer.searchinfo.Movie;
import java.util.List;
import org.junit.Test;

/**
 * Class AnidbScrapperTest
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class AnidbScrapperTest extends AbstractScrapperTest {
  private final AnidbScrapper anidb = new AnidbScrapper();

  @Test
  public void search() throws Exception {
    List<Movie> list = anidb.search("one piece");
    
    Movie movie = list.get(0);
    assertEquals("One Piece", movie.getName());
    assertEquals(69, movie.getMediaId());
  }
}
