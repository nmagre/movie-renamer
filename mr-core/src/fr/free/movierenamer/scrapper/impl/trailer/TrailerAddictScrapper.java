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
package fr.free.movierenamer.scrapper.impl.trailer;

import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.TrailerInfo;
import fr.free.movierenamer.scrapper.TrailerScrapper;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.searchinfo.Media.MediaType;
import fr.free.movierenamer.searchinfo.Trailer;
import fr.free.movierenamer.utils.URIRequest;
import fr.free.movierenamer.utils.XPathUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Class TrailerAddictScrapper
 *
 * @author Nicolas Magré
 */
public class TrailerAddictScrapper extends TrailerScrapper {

  private static final String www = "www.";
  private static final String api = "api.";
  private static final String simpleapi = "simpleapi.";
  private static final String host = "traileraddict.com";
  private static final Pattern tagsPattern = Pattern.compile(host + "\\/(\\w+)\\/");
  private static final MediaType[] supportedType = new MediaType[]{Media.MediaType.MOVIE};

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
  public List<Media.MediaType> getSupportedMediaType() {
    return Arrays.asList(supportedType);
  }

  @Override
  protected List<Trailer> searchTrailer(Media media) throws Exception {
    List<Trailer> trailers = new ArrayList<Trailer>();
    IdInfo imdbId = null;/*media.getImdbId();*/

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
        //trailers.add(new Trailer(node.getTextContent(), new URL("http", simpleapi + host, XPathUtils.getAttribute("href", node))));
      }
    }

    return trailers;
  }

//  @Override
//  public TrailerInfo fetchTrailerInfo(Trailer trailer) throws Exception {
//
//    Document dom = URIRequest.getXmlDocument(trailer.getURL().toURI());
//    String title = XPathUtils.selectString("//trailer/title", dom);
//    String tid = XPathUtils.selectString("//trailer/trailer_id", dom);
//    String description = XPathUtils.selectString("//trailer/description", dom);
//    URL thumb = null;
//    URL streamUrl = null;
//
//    URL url = new URL("http", www + host, "/fvar.php?tid=" + tid);
//    String doc = URIRequest.getDocumentContent(url.toURI());
//    Scanner sc = new Scanner(doc.replaceAll("\n", ""));
//    sc.useDelimiter("&");
//    while (sc.hasNext()) {
//      String str = sc.next();
//      if (str.startsWith("fileurl=")) {
//        streamUrl = new URL((str.replace("fileurl=", "")));
//      } else if (str.startsWith("image=")) {
//        thumb = new URL((str.replace("image=", "")));
//      }
//    }
//
//    // TODO if streamUrl == null
//    return null;
//  }
  @Override
  protected TrailerInfo fetchTrailerInfo(Trailer searchResult) throws Exception {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
