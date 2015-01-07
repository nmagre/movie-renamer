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
package fr.free.movierenamer.scrapper.impl;

import fr.free.movierenamer.scrapper.impl.image.FanartTVImagesScrapper;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.ImageInfo;
import org.junit.Assert;

import fr.free.movierenamer.scrapper.ImageScrapperTest;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.utils.ScrapperUtils;
import java.util.List;

/**
 * Class FanartTVTest
 *
 * @author Simon QUÉMÉNEUR
 * @author Nicolas Magré
 */
public class FanartTVTest extends ImageScrapperTest {

  private FanartTVImagesScrapper fanarttv = null;

  @Override
  public void init() {
    fanarttv = new FanartTVImagesScrapper();
  }

  @Override
  public void getImages() throws Exception {
    List<ImageInfo> images = fanarttv.getImages(new Movie(null, new IdInfo(19995, ScrapperUtils.AvailableApiIds.THEMOVIEDB), null, null, null, -1));
    Assert.assertEquals(ImageInfo.ImageCategoryProperty.logo, images.get(0).getCategory());
    Assert.assertEquals("http://assets.fanart.tv/fanart/movies/19995/hdmovielogo/avatar-503e0262ba196.png", images.get(1).getHref(ImageInfo.ImageSize.big).toExternalForm());
  }
}
