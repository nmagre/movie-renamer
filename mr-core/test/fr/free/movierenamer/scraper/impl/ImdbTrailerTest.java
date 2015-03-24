/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.free.movierenamer.scraper.impl;

import org.junit.Assert;
import org.junit.Test;

import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.TrailerInfo;
import fr.free.movierenamer.scraper.TrailerScraperTest;
import fr.free.movierenamer.scraper.impl.trailer.ImdbTrailerScraper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.searchinfo.Trailer;
import fr.free.movierenamer.utils.ScraperUtils;
import java.util.List;

/**
 *
 * @author duffy
 */
public class ImdbTrailerTest extends TrailerScraperTest {

  private ImdbTrailerScraper imts;
  private List<Trailer> trailers = null;

  @Override
  public void init() throws Exception {
    imts = new ImdbTrailerScraper();
  }

  @Override
  public void search() throws Exception {
    trailers = imts.getTrailer(new Movie(null, new IdInfo(186452, ScraperUtils.AvailableApiIds.ALLOCINE), "lucy", "lucy", null, 2014));

    for (Trailer trailer : trailers) {
      System.out.println(trailer);

      TrailerInfo info = imts.getInfo(trailer);
      System.out.println(info);

    }
  }

  @Override
  public void getTrailerInfo() throws Exception {

  }

}
