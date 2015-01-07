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
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.searchinfo.Media.MediaType;
import fr.free.movierenamer.searchinfo.Trailer;
import fr.free.movierenamer.stream.AbstractStream.Quality;
import fr.free.movierenamer.stream.Youtube;
import fr.free.movierenamer.utils.StringUtils;
import fr.free.movierenamer.utils.URIRequest;
import fr.free.movierenamer.utils.XPathUtils;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.DatatypeConverter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Class YoutubeScrapper
 *
 * @author Nicolas Magré
 */
public class YoutubeTrailerScrapper extends TrailerScrapper {

  private static final String host = "youtube.com";
  private static final String name = "Youtube";
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
    String search = media.getOriginalName();
    if (search == null || search.equals("")) {
      search = media.getName();
    }

    URL searchUrl = new URL("http", "gdata." + host, "/feeds/api/videos?q=" + URIRequest.encode(search + " trailer"));
    Document dom = URIRequest.getXmlDocument(searchUrl.toURI());
    List<Node> nodes = XPathUtils.selectNodes("feed/entry", dom);

    for (Node node : nodes) {
      String title = XPathUtils.selectString("title", node);

      if (!StringUtils.containsIgnoreCase(title, "trailer") && !StringUtils.containsIgnoreCase(title, "teaser")) {
        continue;
      }

      URL turl = new URL(XPathUtils.selectString("link[@rel='self']/@href", node));

      String runtime = XPathUtils.selectString("*[local-name()='group']/*[local-name()='duration']/@seconds", node);
      runtime = StringUtils.durationInMinute(runtime);

      String thumb = XPathUtils.selectString("*[local-name()='group']/*[local-name()='thumbnail']/@url", node);
      URL thumbUrl = null;
      if (thumb != null && !thumb.equals("")) {
        thumbUrl = new URL(thumb);
      }

      trailers.add(new Trailer(title, runtime, name, thumbUrl, turl));
    }

    return trailers;
  }

  @Override
  public TrailerInfo fetchTrailerInfo(Trailer trailer) throws Exception {

    Document dom = URIRequest.getXmlDocument(trailer.getURL());

    Node node = XPathUtils.selectNode("entry", dom);
    Map<TrailerProperty, String> fields = new EnumMap<TrailerProperty, String>(TrailerProperty.class);

    fields.put(TrailerProperty.title, XPathUtils.selectString("title", node));
    fields.put(TrailerProperty.overview, XPathUtils.selectString("content", node));

    String da = XPathUtils.selectString("published", node);
    Calendar cl = DatatypeConverter.parseDateTime(da);
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    fields.put(TrailerProperty.releasedDate, df.format(cl.getTime()));
    fields.put(TrailerProperty.rating, XPathUtils.selectString("//*[local-name()='rating']/@average", node));
    fields.put(TrailerProperty.runtime, XPathUtils.selectString("//*[local-name()='duration']/@seconds", node));
    fields.put(TrailerProperty.provider, name);

    Youtube youtube = new Youtube();
    Map<Quality, URL> streamUrl = youtube.getLinks(new URL(XPathUtils.selectString("//*[local-name()='player']/@url", node)));// FIXME get all urls

    return new TrailerInfo(fields, streamUrl, null);
  }

}
