/*
 * mr-core
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

import fr.free.movierenamer.scraper.impl.tvshow.AllocineTvseriesScraper;
import org.junit.Assert;

import fr.free.movierenamer.scraper.TvShowScraperTest;

/**
 * Class AllocineTvseriesScraperTest
 * @author Simon QUÉMÉNEUR
 */
public class AllocineTvseriesScraperTest extends TvShowScraperTest {
  private AllocineTvseriesScraper allocine = null;

  @Override
  public void init() {
    allocine = new AllocineTvseriesScraper();
  }

  @Override
  public void search() throws Exception {
    Assert.fail();
  }

  @Override
  public void getTvShowInfo() throws Exception {
    Assert.fail();
  }

  @Override
  public void getCasting() throws Exception {
    Assert.fail();
  }

  @Override
  public void getImages() throws Exception {
    Assert.fail();
  }

  @Override
  public void getEpisodesInfoList() throws Exception {
    Assert.fail();
  }

}
