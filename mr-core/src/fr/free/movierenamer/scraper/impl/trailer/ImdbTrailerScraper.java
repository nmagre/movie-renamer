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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Class ImdbTrailerScraper
 *
 * @author Nicolas Magré
 */
public class ImdbTrailerScraper extends TrailerScraper {

  private static final String host = "www.imdb.com";
  private static final String name = "IMDb";
  private static final Pattern runtimePattern = Pattern.compile("1_ZA(\\d{2}:\\d{2})");
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
    List<Trailer> trailers = new ArrayList<Trailer>();

    // Get id
    IdInfo id = ScraperUtils.idLookup(AvailableApiIds.IMDB, null, media);
    if (id == null) {
      return trailers;
    }

    // Get trailer page
    URL searchUrl = new URL("http", host, "/title/" + id + "/videogallery/content_type-trailer");
    Document dom;
    try {
      dom = URIRequest.getHtmlDocument(searchUrl.toURI());
    } catch (Exception ex) {// No trailer page
      return trailers;
    }

    List<Node> nodes = XPathUtils.selectNodes("//DIV[@class = 'search-results']//DIV", dom);
    if (nodes == null) {
      return trailers;
    }

    for (Node tnode : nodes) {
      String title = XPathUtils.selectString("H2", tnode);
      String runtime = "";
      String turl = XPathUtils.getAttribute("href", XPathUtils.selectNode("H2/A", tnode));
      String img = XPathUtils.getAttribute("loadlate", XPathUtils.selectNode("A/IMG", tnode));
      Matcher matcher = runtimePattern.matcher(img);
      if (matcher.find()) {
        runtime = matcher.group(1);
      }

      if (img.contains("@")) {
        img = img.substring(0, img.indexOf("@")) + "@._V1_SX160.jpg";
      } else {
        img = img.substring(0, img.indexOf("._V1_")) + "SX160.jpg";
      }

      trailers.add(new Trailer(title, runtime, name, new URL(img), new URL("http", host, turl)));
    }

    return trailers;
  }

  @Override
  protected TrailerInfo fetchTrailerInfo(Trailer searchResult) throws Exception {
    return null;
  }

}
