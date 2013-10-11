/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.free.movierenamer.trailerinfo;

import fr.free.movierenamer.utils.NumberUtils;
import fr.free.movierenamer.utils.URIRequest;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Nicolas Magré
 */
public class Youtube extends AbstractStream {

  private final Pattern urlPattern = Pattern.compile(".*youtube.com/.*");
  private final String host = "www.youtube.com";

  @Override
  protected Map<Quality, URL> getLinks(URL url) throws Exception {
    String html = URIRequest.getDocumentContent(url.toURI());

//System.out.println(html);
    Pattern pattern = Pattern.compile(";ytplayer.config = \\{(.*)\\}.?;<");
    Matcher matcher = pattern.matcher(html);
    if (matcher.find()) {
      String sline = matcher.group(1);
      sline = sline.replaceFirst(".*\"url_encoded_fmt_stream_map\": \"", "");
      sline = sline.replaceFirst("\".*", "");
      sline = sline.replace("%25", "%");
      sline = sline.replace("\\u0026", "&");
      sline = sline.replace("\\", "");

      for (String urlString : sline.split(",")) {
        Map<Integer, String> urls = new HashMap<Integer, String>();

        String[] res = urlString.split("url=http", 2);
        String yurl = "url=http" + res[1] + "&" + res[0];
        String qual = yurl.substring(yurl.indexOf("itag=") + 5, yurl.indexOf("itag=") + 5 + 1 + (yurl.matches(".*itag=[0-9]{2}.*") ? 1 : 0) + (yurl.matches(".*itag=[0-9]{3}.*") ? 1 : 0));
        yurl = yurl.replaceFirst("url=http%3A%2F%2F", "http://");
        yurl = yurl.replaceAll("%3F", "?").replaceAll("%2F", "/").replaceAll("%3B", ";").replaceAll("%2C", ",").replaceAll("%3D", "=").replaceAll("%26", "&").replaceAll("%252C", "%2C").replaceAll("sig=", "signature=").replaceAll("&s=", "&signature=").replaceAll("\\?s=", "?signature=");

        if (countMatches(yurl, "itag=") == 2) {
          yurl = yurl.replaceFirst("itag=[0-9]{1,3}", "");
        }

        if (NumberUtils.isDigit(qual)) {
          int id = Integer.parseInt(qual);
          urls.put(id, yurl);
        }

        for (Entry<Integer, String> key : urls.entrySet()) {
          System.out.println(key.getKey() + " : " + key.getValue() + "\n");
        }
      }
    }
    return null;
  }

  private int countMatches(String str, String search) {
    int count = 0;
    int idx = 0;

    while ((idx = str.indexOf(search, idx)) != -1) {
      idx++;
      count++;
    }

    return count;
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
}
