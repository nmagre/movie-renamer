/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.free.movierenamer.scrapper.impl;

import fr.free.movierenamer.utils.URIRequest;
import java.io.File;
import java.util.Scanner;
import org.junit.Test;

/**
 *
 * @author duffy
 */
public class TrailerAddictScrapperTest {

  @Test
  public void test() throws Exception {
//    TrailerAddictScrapper scrapper = new TrailerAddictScrapper();
//    scrapper.getTrailer(new Movie(new IdInfo(499549, ScrapperUtils.AvailableApiIds.IMDB), null, "", null, null, -1));

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
