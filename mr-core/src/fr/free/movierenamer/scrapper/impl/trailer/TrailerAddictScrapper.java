/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.free.movierenamer.scrapper.impl.trailer;

import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.TrailerInfo;
import fr.free.movierenamer.scrapper.TrailerScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.searchinfo.Trailer;
import fr.free.movierenamer.utils.URIRequest;
import fr.free.movierenamer.utils.XPathUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author duffy
 */
public class TrailerAddictScrapper extends TrailerScrapper {

  private static final String www = "www.";
  private static final String api = "api.";
  private static final String simpleapi = "simpleapi.";
  private static final String host = "traileraddict.com";
  private static final Pattern tagsPattern = Pattern.compile(host + "\\/trailer\\/(\\w+)/");

  public TrailerAddictScrapper() {

  }

  @Override
  public String getName() {
    return "Trailer Addict";
  }

  @Override
  public String getHost() {
    return www + host;
  }

  @Override
  protected List<Trailer> searchTrailer(Movie movie) throws Exception {
    List<Trailer> trailers = new ArrayList<Trailer>();
    IdInfo imdbId = movie.getImdbId();
    String tag = null;
    if (imdbId != null) {
      URL url = new URL("http", api + host, "/?imdb=" + String.format("%07d", imdbId.getId()));
      Document dom = URIRequest.getXmlDocument(url.toURI());
      Node node = XPathUtils.selectNode("//trailer", dom);
      if (node != null) {
        String link = XPathUtils.selectString("link", node);
        Matcher matcher = tagsPattern.matcher(link);
        if (matcher.find()) {
          tag = matcher.group(1);
        }
      }
    }

    if (tag != null) {
      URL url = new URL("http", www + host, "/tags/" + tag);
      Document dom = URIRequest.getHtmlDocument(url.toURI());
      List<Node> nodes = XPathUtils.selectNodes("//DD[@id='one-ddcontent']/A", dom);
      for (Node node : nodes) {
        trailers.add(new Trailer(node.getTextContent(), new URL("http", simpleapi + host, XPathUtils.getAttribute("href", node))));
      }
    }

    return trailers;
  }

  @Override
  public TrailerInfo fetchTrailerInfo(Trailer trailer) throws Exception {

    Document dom = URIRequest.getXmlDocument(trailer.getURL().toURI());
    String title = XPathUtils.selectString("//trailer/title", dom);
    String tid = XPathUtils.selectString("//trailer/trailer_id", dom);
    String description = XPathUtils.selectString("//trailer/description", dom);
    URL thumb = null;
    URL streamUrl = null;

    URL url = new URL("http", www + host, "/fvar.php?tid=" + tid);
    String doc = URIRequest.getDocumentContent(url.toURI());
    Scanner sc = new Scanner(doc.replaceAll("\n", ""));
    sc.useDelimiter("&");
    while (sc.hasNext()) {
      String str = sc.next();
      if (str.startsWith("fileurl=")) {
        streamUrl = new URL((str.replace("fileurl=", "")));
      } else if (str.startsWith("image=")) {
        thumb = new URL((str.replace("image=", "")));
      }
    }

    // TODO if streamUrl == null
    return new TrailerInfo(title, description, thumb, streamUrl);
  }
}
