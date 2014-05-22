/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.free.movierenamer.scrapper.impl;

import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.scrapper.TrailerScrapperTest;
import fr.free.movierenamer.scrapper.impl.trailer.VideoDetectiveScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.searchinfo.Trailer;
import fr.free.movierenamer.utils.ScrapperUtils;
import java.util.List;

/**
 *
 * @author duffy
 */
public class VideoDetectiveTest extends TrailerScrapperTest {

  private VideoDetectiveScrapper videodetective = null;
  private List<Trailer> trailers = null;

  @Override
  public void init() throws Exception {
    videodetective = new VideoDetectiveScrapper();
  }

  @Override
  public void search() throws Exception {

    trailers = videodetective.getTrailer(new Movie(null, new IdInfo(19776, ScrapperUtils.AvailableApiIds.ALLOCINE), "Matrix", "Avatar", null, 1999));
    for (Trailer trailer : trailers) {
      System.out.println(trailer);
    }
  }

  @Override
  public void getTrailerInfo() throws Exception {

  }

}
