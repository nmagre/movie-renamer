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
package fr.free.movierenamer.stream;

import fr.free.movierenamer.utils.StringUtils;
import fr.free.movierenamer.utils.URIRequest;
import java.net.URL;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class Youtube
 *
 * @author Nicolas Magré
 */
public class Youtube extends AbstractStream {

  private static final Pattern urlPattern = Pattern.compile(".*youtube.com/.*");
  private static final String host = "www.youtube.com";
  private static final Map<String, String> replace;
  private static final Map<Integer, Video> quality;

  public enum Codec {

    MPEG,
    FLV,
    WEBM
  }

  static {
    replace = new LinkedHashMap<String, String>();
    replace.put("%25", "%");
    replace.put("\\u0026", "&");
    replace.put("url=http%3A%2F%2F", "http://");
    replace.put("%3F", "?");
    replace.put("%2F", "/");
    replace.put("%3B", ";");
    replace.put("%2C", ",");
    replace.put("%3D", "=");
    replace.put("%26", "&");
    replace.put("%252C", "%2C");
    replace.put("sig=", "signature=");
    replace.put("&s=", "&signature=");
    replace.put("?s=", "?signature=");

    quality = new LinkedHashMap<Integer, Video>();
    // MPEG
    quality.put(138, new Video(Quality.UHD, Codec.MPEG));
    quality.put(137, new Video(Quality.HD, Codec.MPEG));
    quality.put(136, new Video(Quality.HD, Codec.MPEG));
    quality.put(37, new Video(Quality.HD, Codec.MPEG));
    quality.put(22, new Video(Quality.HDR, Codec.MPEG));
    quality.put(135, new Video(Quality.SD, Codec.MPEG));
    quality.put(134, new Video(Quality.SD, Codec.MPEG));
    quality.put(18, new Video(Quality.SD, Codec.MPEG));
    quality.put(133, new Video(Quality.LD, Codec.MPEG));
    quality.put(160, new Video(Quality.LD, Codec.MPEG));
    quality.put(36, new Video(Quality.LD, Codec.MPEG));
    quality.put(17, new Video(Quality.LD, Codec.MPEG));
    quality.put(84, new Video(Quality.HD3D, Codec.MPEG));
    quality.put(82, new Video(Quality.SD3D, Codec.MPEG));
    // FLV
    quality.put(35, new Video(Quality.SD, Codec.FLV));
    quality.put(34, new Video(Quality.SD, Codec.FLV));
    quality.put(5, new Video(Quality.LD, Codec.FLV));
    // WEBM
    quality.put(46, new Video(Quality.HD, Codec.WEBM));
    quality.put(45, new Video(Quality.HDR, Codec.WEBM));
    quality.put(44, new Video(Quality.SD, Codec.WEBM));
    quality.put(43, new Video(Quality.SD, Codec.WEBM));
    quality.put(44, new Video(Quality.SD, Codec.WEBM));
    quality.put(100, new Video(Quality.HD3D, Codec.WEBM));
    quality.put(102, new Video(Quality.SD3D, Codec.WEBM));
//    // AUDIO only
//    dontcare.put(140, new Video(Quality.AUDIO, Codec.MPEG));
//    dontcare.put(171, new Video(Quality.AUDIO, Codec.WEBM));
//    // VIDEO only
//    dontcare.put(242, new Video(Quality.LD, Codec.WEBM));
//    dontcare.put(243, new Video(Quality.SD, Codec.WEBM));
//    dontcare.put(244, new Video(Quality.SD, Codec.WEBM));
//    dontcare.put(247, new Video(Quality.HD, Codec.WEBM));
//    dontcare.put(248, new Video(Quality.HD, Codec.WEBM));
//    dontcare.put(264, new Video(Quality.UHD, Codec.WEBM));

  }

  @Override
  public Map<Quality, URL> getLinks(URL url) throws Exception {
    String html = URIRequest.getDocumentContent(url.toURI());
    Map<Quality, URL> links = new EnumMap<Quality, URL>(Quality.class);

    Pattern pattern = Pattern.compile("<script>(.*(?:\"adaptive_fmts\":|\"url_encoded_fmt_stream_map\":).*)</script>");
    Matcher matcher = pattern.matcher(html);
    if (matcher.find()) {

      String javascript = matcher.group(1);
      String uri0 = "";
      String uri1 = "";

      if (javascript.contains("\"url_encoded_fmt_stream_map\": \"")) {
        uri1 = StringUtils.substringBetween(javascript, ".*\"url_encoded_fmt_stream_map\": \"", "");
      }

      if (javascript.contains("\"adaptive_fmts\": \"")) {
        uri0 = StringUtils.substringBetween(javascript, "\"adaptive_fmts\": \"", "\"");
      }

      String uri = uri0 + "," + uri1;
      String[] yturls = uri.split(",");

      for (String yturl : yturls) {

        if (yturl.matches(".*conn=rtmpe.*") || !yturl.contains("url=http")) {
          break;
        }

        try {
          String[] res = yturl.split("url=http", 2);
          String yurl = "url=http" + res[1] + "&" + res[0];
          String qual = yurl.substring(yurl.indexOf("itag=") + 5, yurl.indexOf("itag=") + 5 + 1 + (yurl.matches(".*itag=[0-9]{2}.*") ? 1 : 0) + (yurl.matches(".*itag=[0-9]{3}.*") ? 1 : 0));

          for (Map.Entry<String, String> entry : replace.entrySet()) {
            yurl = yurl.replace(entry.getKey(), entry.getValue());
          }

          if (StringUtils.countMatches(yurl, "itag=") == 2) {
            yurl = yurl.replaceFirst("itag=[0-9]{1,3}", "");
          }

          Video video = quality.get(Integer.parseInt(qual));
          if (video != null) {
            if (links.get(video.getQuality()) == null) {
              links.put(video.getQuality(), new URL(yurl));
            }
          }

        } catch (Exception ex) {
          // don't care
        }

      }
    }

    return links;
  }

  @Override
  protected boolean isUrlSupported(URL url) {
    return urlPattern.matcher(url.toExternalForm()).find();
  }

  @Override
  public String getName() {
    return "Youtube";
  }

  @Override
  protected String getHost() {
    return host;
  }

  private static class Video {

    private final Quality quality;
    private final Codec codec;

    public Video(Quality quality, Codec codec) {
      this.quality = quality;
      this.codec = codec;
    }

    @Override
    public String toString() {
      return "{" + getQuality().name() + "} [" + getCodec().name() + "]";
    }

    public Quality getQuality() {
      return quality;
    }

    public Codec getCodec() {
      return codec;
    }

  }
}
