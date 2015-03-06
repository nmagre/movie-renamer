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

import fr.free.movierenamer.info.TrailerInfo;
import fr.free.movierenamer.info.TrailerInfo.TrailerProperty;
import fr.free.movierenamer.scraper.TrailerScraper;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.searchinfo.Media.MediaType;
import fr.free.movierenamer.searchinfo.Trailer;
import fr.free.movierenamer.stream.AbstractStream.Quality;
import fr.free.movierenamer.utils.JSONUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.StringUtils;
import fr.free.movierenamer.utils.URIRequest;
import fr.free.movierenamer.utils.XPathUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Class VideoDetectiveScraper
 *
 * @author Nicolas Magré
 */
public class VideoDetectiveScraper extends TrailerScraper {

  private static final String host = "www.videodetective.com";
  private static final String name = "Video Detective";
  private static final Pattern cleanTitle = Pattern.compile("(.*\\w):");
  private static final Pattern cleanTitle2 = Pattern.compile("(.*)\\(.*\\)$");
  private static final Pattern idPattern = Pattern.compile(".*/(\\d+)$");
  private static final String cusomerId = "69249";
  private static final String infoUrl = "http://video.internetvideoarchive.net/player/6/configuration.ashx?customerid=%s&publishedid=%s&reporttag=vdbetatitle&playerid=641&autolist=0&domain=www.videodetective.com&maxrate=high&minrate=low&socialplayer=false";
  private static final MediaType[] supportedType = new MediaType[]{MediaType.MOVIE};

  private enum Bitrate {

    B_2500_kbs(Quality.HD),
    B_1500_kbs(Quality.HDR),
    B_750_kbs(Quality.SD),
    B_450_kbs(Quality.SD),
    B_212_kbs(Quality.LD),
    B_80_kbs(Quality.LD);

    private final Quality qual;

    private Bitrate(Quality qual) {
      this.qual = qual;
    }

    public Quality getQuality() {
      return qual;
    }
  }

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

    URL searchUrl = new URL("http", host, "/browse?filter=mediatype%3aMovie+Alternate+Version&q=" + URIRequest.encode(search));
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI());
    List<Node> nodes = XPathUtils.selectNodes("//DIV[contains(@class, 'durationwrapper')]", dom);

    for (Node node : nodes) {
      Node info = XPathUtils.selectNode("DIV[@class='duration']//A[contains(@href, '/movies/')]", node);
      if (info != null) {

        String title = info.getTextContent();
        if (!isTrailerof(title, media)) {
          continue;
        }

        String runtime = XPathUtils.selectString("DIV[@class='durationtext']", node).trim();

        URL thumbUrl = null;
        String thumb = XPathUtils.selectString("A[contains(@href, '/movies/')]/IMG/@src", node);
        if (thumb != null && !thumb.equals("")) {
          try {
            thumbUrl = new URL(thumb);
          } catch (MalformedURLException ex) {
          }
        }

        URL trailerUrl = null;
        String turlStr = XPathUtils.getAttribute("href", info);
        try {
          trailerUrl = new URL("http", host, turlStr);
        } catch (MalformedURLException ex) {
        }

        Node lnode = XPathUtils.selectNode("DIV[@class='duration']//DIV[last()]/text()[preceding-sibling::BR]", node);
        AvailableLanguages lang = null;
        if (lnode != null) {
          Locale local = LocaleUtils.findLanguage(lnode.getTextContent().trim());
          if (local != null) {
            try {
              lang = AvailableLanguages.valueOf(local.getLanguage());
            } catch (Exception ex) {
            }
          }
        }

        trailers.add(new Trailer(title, runtime, name, thumbUrl, trailerUrl, lang));
      }
    }

    return trailers;
  }

  @Override
  protected TrailerInfo fetchTrailerInfo(Trailer searchResult) throws Exception {

    URL url = searchResult.getTrailerUrl();
    Matcher matcher = idPattern.matcher(url.toExternalForm());
    if (!matcher.find()) {
      throw new Exception("No id found for " + searchResult);
    }

    Map<TrailerProperty, String> info = new EnumMap<TrailerProperty, String>(TrailerProperty.class);
    Map<Quality, URL> streams = new EnumMap<Quality, URL>(Quality.class);

    String id = matcher.group(1);

    String urlStr = String.format(infoUrl, cusomerId, id);
    URL uri = new URL(urlStr);

    JSONObject json = URIRequest.getJsonDocument(uri.toURI());
    List<JSONObject> pnodes = JSONUtils.selectList("playlist", json);

    info.put(TrailerProperty.title, searchResult.getName());
    info.put(TrailerProperty.provider, searchResult.getProviderName());
    info.put(TrailerProperty.runtime, searchResult.getRuntime());
    info.put(TrailerProperty.overview, JSONUtils.selectString("description", pnodes.get(0)));

    List<JSONObject> nodes = JSONUtils.selectList("sources", pnodes.get(0));
    for (JSONObject node : nodes) {
      String label = JSONUtils.selectString("label", node);
      if (label != null) {
        Bitrate bt;
        try {
          bt = Bitrate.valueOf("B_" + (label.replace(" ", "_")));
        } catch (Exception ex) {
          continue;
        }

        if (streams.containsKey(bt.getQuality())) {
          continue;
        }

        streams.put(bt.getQuality(), new URL(JSONUtils.selectString("file", node)));
      }
    }

    return new TrailerInfo(info, streams, searchResult.getLang());
  }

  private boolean isTrailerof(String title, Media media) {

    title = title.toLowerCase().trim();
    String mtitle = media.getName().toLowerCase();
    String motitle = media.getOriginalName();
    if (motitle != null) {
      motitle = motitle.toLowerCase();
    }

    if (title.contains(mtitle) || (motitle != null && title.contains(motitle))) {
      return true;
    }

    Matcher matcher = cleanTitle.matcher(title);
    if (matcher.find()) {
      title = matcher.group(1);
    }

    matcher = cleanTitle2.matcher(title);
    if (matcher.find()) {
      title = matcher.group(1);
    }

    title = StringUtils.normaliseClean(title);
    mtitle = StringUtils.normaliseClean(mtitle);
    motitle = StringUtils.normaliseClean(motitle);

    return title.equals(mtitle) || (motitle != null && title.equals(motitle)) || title.contains(mtitle) || (motitle != null && title.contains(motitle));
  }

}
