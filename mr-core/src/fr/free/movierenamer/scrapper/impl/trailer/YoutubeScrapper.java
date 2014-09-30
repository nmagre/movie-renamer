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
import fr.free.movierenamer.utils.StringUtils;
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
 * Class YoutubeScrapper
 *
 * @author Nicolas Magré
 */
public class YoutubeScrapper extends TrailerScrapper {

  private static final String host = "youtube.com";
  private static final String name = "Youtube";

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
    String search = movie.getOriginalName();
    if (search == null || search.equals("")) {
      search = movie.getName();
    }

    URL searchUrl = new URL("http", "gdata." + host, "/feeds/api/videos?q=" + URIRequest.encode(search + " trailer"));
    Document dom = URIRequest.getXmlDocument(searchUrl.toURI());
    List<Node> nodes = XPathUtils.selectNodes("feed/entry", dom);

    for (Node node : nodes) {
      Map<TrailerProperty, String> fields = new EnumMap<TrailerProperty, String>(TrailerProperty.class);
      fields.put(TrailerProperty.title, XPathUtils.selectString("title", node));
      fields.put(TrailerProperty.url, XPathUtils.selectString("link[@rel='self']/@href", node));

      String runtime = XPathUtils.selectString("*[local-name()='group']/*[local-name()='duration']/@seconds", node);
      runtime = StringUtils.durationInMinute(runtime);
      fields.put(TrailerProperty.runtime, runtime);
      fields.put(TrailerProperty.provider, name);

      String thumb = XPathUtils.selectString("*[local-name()='group']/*[local-name()='thumbnail']/@url", node);
      if (thumb != null && !thumb.equals("")) {
        fields.put(TrailerProperty.posterPath, thumb);
      }

      trailers.add(new TrailerInfo(fields));
    }

    return trailers;
  }

//  @Override
//  public TrailerInfo fetchTrailerInfo(Trailer trailer) throws Exception {
//
//    Document dom = URIRequest.getXmlDocument(trailer.getURL().toURI());
//
//    Node node = XPathUtils.selectNode("entry", dom);
//    Map<TrailerInfo.TrailerProperty, String> fields = new EnumMap<TrailerInfo.TrailerProperty, String>(TrailerInfo.TrailerProperty.class);
//
//    fields.put(TrailerInfo.TrailerProperty.title, XPathUtils.selectString("title", node));
//    // fields.put(TrailerInfo.TrailerProperty.overview, XPathUtils.selectString("content", node));
//    fields.put(TrailerInfo.TrailerProperty.url, XPathUtils.selectString("//*[local-name()='player']/@url", node));
//    // streamurl
//    String da = "2010-05-22T01:59:00.000Z";
//    Calendar cl = DatatypeConverter.parseDateTime(da);
//    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//    fields.put(TrailerInfo.TrailerProperty.releasedDate, df.format(cl.getTime()).toString());
//    fields.put(TrailerInfo.TrailerProperty.rating, XPathUtils.selectString("//*[local-name()='rating']/@average", node));
//    fields.put(TrailerInfo.TrailerProperty.posterPath, XPathUtils.selectString("//*[local-name()='thumbnail']/@url", node));
//    fields.put(TrailerInfo.TrailerProperty.runtime, XPathUtils.selectString("//*[local-name()='duration']/@seconds", node));
//    Youtube youtube = new Youtube();
//    URL streamUrl = youtube.getLink(new URL(fields.get(TrailerInfo.TrailerProperty.url)));
//    if (streamUrl != null) {
//      fields.put(TrailerInfo.TrailerProperty.streamUrl, streamUrl.toString());
//    }
//
//    return new TrailerInfo(fields);
//  }
}
