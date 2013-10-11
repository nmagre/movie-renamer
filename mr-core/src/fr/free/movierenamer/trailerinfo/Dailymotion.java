/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.free.movierenamer.trailerinfo;

import fr.free.movierenamer.utils.URIRequest;
import java.net.URL;
import java.net.URLDecoder;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Nicolas Magré
 */
public class Dailymotion extends AbstractStream {

  private final Pattern urlPattern = Pattern.compile("http://(www\\.)?dailymotion\\.com/((embed/)?video/[a-z0-9\\-_]+|swf(/video)?/[a-zA-Z0-9]+)");
  private final Pattern videoSourcePattern = Pattern.compile("\"sequence\":\"([^<>\"]*?)\"");
  private final String host = "www.dailymotion.com";
  private final String[] videoQuality = new String[]{"hd1080URL", "hd720URL", "hqURL", "sdURL", "ldURL"};

  @Override
  protected Map<Quality, URL> getLinks(URL url) throws Exception {
    String html = URIRequest.getDocumentContent(url.toURI());
    if (html.contains("Dailymotion – 404 Not Found")) {
      return null;
    }

    if (html.contains("Access forbidden")) {
      return null;
    }

    Matcher matcher = videoSourcePattern.matcher(html);
    if (matcher.find()) {
      Map<Quality, URL> links = new EnumMap<Quality, URL>(Quality.class);
      String sources = URLDecoder.decode(matcher.group(1), "UTF-8").replace("\\", "");
      for (String quality : videoQuality) {
        Pattern pattern = Pattern.compile("\"" + quality + "\":\"([^<>\"]*?)\"");
        Matcher sourceMatcher = pattern.matcher(sources);
        if (sourceMatcher.find()) {
          if (sourceMatcher.group(1).contains("mp4")) {
            if (quality.equals("ldURL")) {
              links.put(Quality.LD, new URL(sourceMatcher.group(1)));
            } else if (quality.equals("sdURL")) {
              links.put(Quality.LD, new URL(sourceMatcher.group(1)));
            } else if (!links.containsKey(Quality.HD)) {
              links.put(Quality.HD, new URL(sourceMatcher.group(1)));
            }
          }
        }
      }

      return links;
    }

    return null;
  }

  @Override
  protected boolean isUrlSupported(URL url) {
    return urlPattern.matcher(url.toExternalForm()).find();
  }

  @Override
  public String getName() {
    return "Dailymotion";
  }

  @Override
  protected String getHost() {
    return host;
  }
}
