/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.free.movierenamer.utils;

import fr.free.movierenamer.scraper.impl.movie.IMDbScraper;
import org.junit.Assert;
import org.junit.Test;

public class SorterTest {

  @Test
  public void sortTest() throws Exception {
    IMDbScraper imdb = new IMDbScraper();
    imdb.setLanguage(LocaleUtils.AvailableLanguages.fr);
    
    
    
    String search = "fast and furious";
    System.out.println("Search : " + search);
    Sorter.sortAccurate(imdb.search(search), search, 2010, 0);
    System.out.println();
    
    search = "r3 fast and furious";
    System.out.println("Search : " + search);
    //Sorter.sortAccurate(imdb.search(search), search, 0, 0);
    System.out.println();
    
  }
}
