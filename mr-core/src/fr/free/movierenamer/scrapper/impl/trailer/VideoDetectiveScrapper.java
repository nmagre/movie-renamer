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

import fr.free.movierenamer.info.TrailerInfo;
import fr.free.movierenamer.info.TrailerInfo.TrailerProperty;
import fr.free.movierenamer.scrapper.TrailerScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.utils.URIRequest;
import fr.free.movierenamer.utils.XPathUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Class VideoDetectiveScrapper
 *
 * @author Nicolas Magré
 */
public class VideoDetectiveScrapper extends TrailerScrapper {

  private static final String host = "www.videodetective.com";
  private static final String name = "Video Detective";

  @Override
  public String getName() {
    return name;
  }

  @Override
  protected String getHost() {
    return host;
  }

  @Override
  protected List<TrailerInfo> searchTrailer(Movie movie) throws Exception {

    List<TrailerInfo> trailers = new ArrayList<TrailerInfo>();
    String search = movie.getOriginalTitle();
    if (search == null || search.equals("")) {
      search = movie.getName();
    }

    URL searchUrl = new URL("http", host, "/browse?filter=mediatype%3aMovie&q=" + URIRequest.encode(search));
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI());
    List<Node> nodes = XPathUtils.selectNodes("//DIV[contains(@class, 'durationwrapper')]", dom);

    for (Node node : nodes) {
      Map<TrailerProperty, String> fields = new EnumMap<TrailerProperty, String>(TrailerProperty.class);
      Node info = XPathUtils.selectNode("DIV[@class='duration']//A[contains(@href, '/movies/')]", node);
      if (info != null) {
        fields.put(TrailerProperty.title, info.getTextContent());
        fields.put(TrailerProperty.url, XPathUtils.getAttribute("href", info));
        fields.put(TrailerProperty.runtime, XPathUtils.selectString("DIV[@class='durationtext']", node).trim());
        fields.put(TrailerProperty.provider, name);

        String thumb = XPathUtils.selectString("A[contains(@href, '/movies/')]/IMG/@src", node);
        if (thumb != null && !thumb.equals("")) {
          fields.put(TrailerProperty.posterPath, thumb);
        }

        trailers.add(new TrailerInfo(fields));
      }
    }

    return trailers;
  }
}
