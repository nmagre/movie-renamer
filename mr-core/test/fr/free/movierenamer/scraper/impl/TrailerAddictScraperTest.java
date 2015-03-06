/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.free.movierenamer.scraper.impl;

import fr.free.movierenamer.utils.URIRequest;
import java.io.File;
import java.util.Scanner;
import org.junit.Test;

/**
 *
 * @author duffy
 */
public class TrailerAddictScraperTest {

  @Test
  public void test() throws Exception {
//    TrailerAddictScraper scraper = new TrailerAddictScraper();
//    scraper.getTrailer(new Movie(new IdInfo(499549, ScraperUtils.AvailableApiIds.IMDB), null, "", null, null, -1));

    File file = new File("/home/duffy/Desktop/test");
    String doc = URIRequest.getDocumentContent(file.toURI());
    Scanner sc = new Scanner(doc.replaceAll("\n", ""));
    sc.useDelimiter("&");
    while (sc.hasNext()) {
      String str = sc.next();
      if (str.startsWith("fileurl=")) {
        System.out.println(str.replace("fileurl=", ""));
      } else if (str.startsWith("image=")) {
        System.out.println(str.replace("image=", ""));
      }
    }
  }
}
