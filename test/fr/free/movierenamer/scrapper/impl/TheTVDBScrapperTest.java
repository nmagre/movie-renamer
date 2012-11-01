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

import fr.free.movierenamer.info.EpisodeInfo;
import fr.free.movierenamer.info.TvShowInfo;
import fr.free.movierenamer.searchinfo.TvShow;
import java.util.List;
import java.util.Locale;
import org.junit.Test;

/**
 * Class TheTVDBScrapperTest
 * 
 * @author Simon QUÉMÉNEUR
 */
public final class TheTVDBScrapperTest extends AbstractScrapperTest {
  private final TheTVDBScrapper thetvdb = new TheTVDBScrapper();

  @Test
  public void search() throws Exception {
    List<TvShow> results = thetvdb.search("desperate housewives");

    assertEquals(1, results.size());

    TvShow tvShow =  results.get(0);

    assertEquals("Desperate Housewives", tvShow.getName());
    assertEquals(73800, tvShow.getMediaId());
  }
  
  @Test
  public void getTvShowInfo() throws Exception {
    TvShowInfo tvShow = thetvdb.getInfo(new TvShow(82066, null, null), Locale.FRENCH);

    assertEquals("Fringe", tvShow.getName());
    assertEquals("2008-08-26", tvShow.getFirstAired().toString());
    assertEquals("[Drama, Science-Fiction]", tvShow.getGenres().toString());
  }
  
  @Test
  public void getEpisodesInfoList() throws Exception {
    List<EpisodeInfo> episodes = thetvdb.getEpisodesInfoList(new TvShow(81189, null, null), Locale.GERMAN);

    EpisodeInfo first = episodes.get(0);

    assertEquals("Breaking Bad", first.getTvShowName());
    assertEquals("Der Einstieg", first.getName());
    assertEquals("2008-01-20", first.getAirdate().toString());
  }

}
