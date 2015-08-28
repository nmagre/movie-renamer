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
package fr.free.movierenamer.scraper.impl.trailer;

import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.TrailerInfo;
import fr.free.movierenamer.scraper.TrailerScraper;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.searchinfo.Media.MediaType;
import fr.free.movierenamer.searchinfo.Trailer;
import fr.free.movierenamer.utils.ScraperUtils;
import fr.free.movierenamer.utils.ScraperUtils.AvailableApiIds;
import fr.free.movierenamer.utils.URIRequest;
import fr.free.movierenamer.utils.XPathUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Class AllocineTrailerScraper
 *
 * @author Nicolas Magré
 */
public class AllocineTrailerScraper extends TrailerScraper {

  private static final String host = "www.allocine.fr";
  private static final String name = "Allocine";
  private static final MediaType[] supportedType = new MediaType[]{MediaType.MOVIE};

  @Override
  public String getName() {
    return name;
  }

  @Override
  protected String getHost() {
    return host;
  }

  @Override
  public List<MediaType> getSupportedMediaType() {
    return Arrays.asList(supportedType);
  }

  @Override
  protected List<Trailer> searchTrailer(Media media) throws Exception {

    List<Trailer> trailers = new ArrayList<>();

    // Get id
    IdInfo id = media.getMediaType().idLookup(AvailableApiIds.ALLOCINE, null, media);
    if (id == null) {
      return trailers;
    }

    // Get trailer page
    URL searchUrl = new URL("http", host, "/videos/fichefilm-" + id + "/toutes/");
    Document dom;
    try {
      dom = URIRequest.getHtmlDocument(searchUrl.toURI());
    } catch (Exception ex) {// No trailer page
      System.out.println(ex.getMessage());
      return trailers;
    }

    Node node = XPathUtils.selectNode("//DIV[contains(@class, 'titlebar') and contains(., 'Bandes-annonces')]/following-sibling::SECTION[1]", dom);
    if (node == null) {
      return trailers;
    }

    List<Node> nodes = XPathUtils.selectNodes("DIV/ARTICLE//FIGURE", node);
    for (Node tnode : nodes) {
      String title = XPathUtils.selectString("FIGCAPTION/DIV[@class = 'media-meta-figcaption-inner']/DIV", tnode);
      String runtime = XPathUtils.selectString("DIV//TIME", tnode);
      String turl = XPathUtils.getAttribute("href", XPathUtils.selectNode("FIGCAPTION//SPAN/A", tnode));

      String img = XPathUtils.getAttribute("data-attr", XPathUtils.selectNode("DIV//IMG", tnode));
      if (img != null) {
        img = img.replace("{\"src\":\"", "").replace("\"}", "");
      } else {
        img = XPathUtils.getAttribute("src", XPathUtils.selectNode("DIV//IMG", tnode));
      }
      trailers.add(new Trailer(title, runtime, name, new URL(img), new URL("http", host, turl)));
    }

    return trailers;
  }

  @Override
  protected TrailerInfo fetchTrailerInfo(Trailer searchResult) throws Exception {
    return null;
  }

  @Override
  public InfoQuality getQuality() {
    return InfoQuality.GREAT;
  }

}
