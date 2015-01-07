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

import fr.free.movierenamer.scrapper.impl.tvshow.TvRageScrapper;
import java.util.List;

import org.junit.Assert;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.EpisodeInfo;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.TvShowInfo;
import fr.free.movierenamer.scrapper.TvShowScrapperTest;
import fr.free.movierenamer.searchinfo.TvShow;
import fr.free.movierenamer.utils.EpisodeUtils;
import fr.free.movierenamer.utils.ScrapperUtils;

/**
 * Class TvRageScrapperTest
 *
 * @author Simon QUÉMÉNEUR
 */
public final class TvRageScrapperTest extends TvShowScrapperTest {

  private TvRageScrapper tvrage = null;

  @Override
  public void init() {
    tvrage = new TvRageScrapper();
  }

  @Override
  public void search() throws Exception {
    List<TvShow> results = tvrage.search("Buffy");

    TvShow tvShow = results.get(0);

    Assert.assertEquals("Buffy the Vampire Slayer", tvShow.getName());
    Assert.assertEquals("http://images.tvrage.com/shows/3/2930.jpg", tvShow.getURL().toExternalForm());
    Assert.assertEquals(1997, tvShow.getYear());
    Assert.assertEquals(2930, tvShow.getMediaId());
  }

  @Override
  public void getTvShowInfo() throws Exception {
    TvShowInfo tvShow = tvrage.getInfo(new TvShow(null, new IdInfo(27811, ScrapperUtils.AvailableApiIds.TVRAGE), null, null, -1));

    Assert.assertEquals("Homeland", tvShow.getName());
    Assert.assertEquals("2011-10-02", tvShow.getFirstAired().toString());
    Assert.assertEquals("Returning Series", tvShow.getStatus());
    Assert.assertEquals("[Action, Crime, Current Events, Drama, Family, Military/War, Politics, Religion, Romance/Dating, Thriller]", tvShow.getGenres().toString());
    Assert.assertEquals("http://images.tvrage.com/shows/28/27811.jpg", tvShow.getPosterPath().toString());
  }

  @Override
  public void getEpisodesInfoList() throws Exception {
    List<TvShow> results = tvrage.search("breaking bad");
    TvShow tvShow = results.get(0);

    List<EpisodeInfo> episodes = tvrage.getEpisodesInfoList(tvShow);

    List<EpisodeInfo> s03 = EpisodeUtils.filterBySeason(episodes, 3);

    Assert.assertEquals(13, s03.size());

    EpisodeInfo s03e13 = s03.get(12);

    Assert.assertEquals("Breaking Bad", s03e13.getTvShowName());
    Assert.assertEquals("Full Measure", s03e13.getName());
    Assert.assertEquals("13", s03e13.getEpisode().toString());
    Assert.assertEquals("3", s03e13.getSeason().toString());
    Assert.assertEquals("2010-06-13", s03e13.getAirdate().toString());
  }

  @Override
  public void getCasting() throws Exception {
    List<CastingInfo> cast = tvrage.getCasting(new TvShow(null, new IdInfo(2930, ScrapperUtils.AvailableApiIds.TVRAGE), null, null, -1), new IdInfo(2930, ScrapperUtils.AvailableApiIds.TVRAGE));
    Assert.assertEquals(null, cast);
  }

  ;

  @Override
  public void getImages() throws Exception {
    List<ImageInfo> images = tvrage.getImages(new TvShow(null, new IdInfo(1858, ScrapperUtils.AvailableApiIds.TVRAGE), null, null, -1));
    Assert.assertEquals(null, images);
  }
}
