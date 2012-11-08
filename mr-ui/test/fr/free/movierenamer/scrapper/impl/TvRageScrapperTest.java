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
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.TvShowInfo;
import fr.free.movierenamer.searchinfo.TvShow;
import fr.free.movierenamer.utils.EpisodeUtils;
import java.util.List;
import java.util.Locale;
import org.junit.Test;

/**
 * Class TvRageScrapperTest
 * 
 * @author Simon QUÉMÉNEUR
 */
public final class TvRageScrapperTest extends AbstractScrapperTest {

  private final TvRageScrapper tvrage = new TvRageScrapper();
  
  @Test
  public void search() throws Exception {
    List<TvShow> results = tvrage.search("Buffy");
    
    TvShow tvShow = results.get(0);
    
    assertEquals("Buffy the Vampire Slayer", tvShow.getName());
    assertEquals(2930, tvShow.getMediaId());
  }
  
  @Test
  public void getTvShowInfo() throws Exception {
    TvShowInfo tvShow = tvrage.getInfo(new TvShow(2930, null, null), Locale.FRENCH);

    assertEquals("Transformers", tvShow.getName());
    assertEquals("", tvShow.getBannerUrl().toString());
  }
  
  @Test
  public void getEpisodesInfoList() throws Exception {
    List<TvShow> results = tvrage.search("breaking bad");
    TvShow tvShow = results.get(0);
    
    List<EpisodeInfo> episodes = tvrage.getEpisodesInfoList(tvShow, Locale.ITALIAN);

    List<EpisodeInfo> s03 = EpisodeUtils.filterBySeason(episodes, 3);
    
    assertEquals(13, s03.size());
    
    EpisodeInfo s03e13 = s03.get(12);
    
    assertEquals("Breaking Bad", s03e13.getTvShowName());
    assertEquals("Full Measure", s03e13.getName());
    assertEquals("13", s03e13.getEpisode().toString());
    assertEquals("3", s03e13.getSeason().toString());
    assertEquals("2010-06-13", s03e13.getAirdate().toString());
  }

  @Test
  public void getImages() throws Exception {
    List<ImageInfo> images = tvrage.getImages(new TvShow(1858, null, null), Locale.FRENCH);
    assertEquals("?????", images.get(0).getCategory());
  }
}
