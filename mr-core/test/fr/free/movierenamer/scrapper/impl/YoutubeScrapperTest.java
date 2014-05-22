/*
 * movie-renamer-core
 * Copyright (C) 2014 Nicolas Magré
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

import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.TrailerInfo;
import fr.free.movierenamer.scrapper.TrailerScrapperTest;
import fr.free.movierenamer.scrapper.impl.trailer.YoutubeScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.searchinfo.Trailer;
import fr.free.movierenamer.utils.ScrapperUtils;
import java.util.List;

/**
 * Class TrailerScrapperTest
 *
 * @author Nicolas Magré
 */
public class YoutubeScrapperTest extends TrailerScrapperTest {

  private YoutubeScrapper youtube = null;
  private List<Trailer> trailers = null;

  @Override
  public void init() throws Exception {
    youtube = new YoutubeScrapper();
  }

  @Override
  public void search() throws Exception {

//    trailers = youtube.getTrailer(new Movie(null, new IdInfo(19776, ScrapperUtils.AvailableApiIds.ALLOCINE), "Matrix", "The Matrix", null, 1999));
//    for (Trailer trailer : trailers) {
//      System.out.println(trailer);
//    }
  }

  @Override
  public void getTrailerInfo() throws Exception {
    trailers = youtube.getTrailer(new Movie(null, new IdInfo(19776, ScrapperUtils.AvailableApiIds.ALLOCINE), "avatar", "harry potter", null, 1999));

    for (Trailer trailer : trailers) {
      TrailerInfo info = youtube.fetchTrailerInfo(trailer);
      System.out.println(info);
      break;
    }
  }

}
