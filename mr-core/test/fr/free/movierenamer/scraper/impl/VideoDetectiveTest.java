/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.free.movierenamer.scraper.impl;

import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.TrailerInfo;
import fr.free.movierenamer.scraper.TrailerScraperTest;
import fr.free.movierenamer.scraper.impl.trailer.VideoDetectiveScraper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.searchinfo.Trailer;
import fr.free.movierenamer.utils.ScraperUtils;
import java.util.List;

/**
 *
 * @author duffy
 */
public class VideoDetectiveTest extends TrailerScraperTest {

  private VideoDetectiveScraper videodetective = null;
  private List<Trailer> trailers = null;

  @Override
  public void init() throws Exception {
    videodetective = new VideoDetectiveScraper();
  }

  @Override
  public void search() throws Exception {

    trailers = videodetective.getTrailer(new Movie(null, new IdInfo(19776, ScraperUtils.AvailableApiIds.ALLOCINE), "lucy", "lucy", null, 2014));
    int i = 0;
    for (Trailer trailer : trailers) {
      System.out.println(trailer);
      if (i == 0) {
        TrailerInfo info = videodetective.getInfo(trailer);
        System.out.println(info);
      }
      i++;
    }
  }

  @Override
  public void getTrailerInfo() throws Exception {

  }

}
