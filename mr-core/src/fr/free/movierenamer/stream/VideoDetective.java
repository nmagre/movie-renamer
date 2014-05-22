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

import fr.free.movierenamer.utils.JSONUtils;
import fr.free.movierenamer.utils.URIRequest;
import java.net.URL;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.json.simple.JSONObject;

/**
 * Class VideoDetective
 *
 * @author Nicolas Magré
 */
public class VideoDetective extends AbstractStream {

  private static final Pattern urlPattern = Pattern.compile("www\\.videodetective.com/movies/.*/\\d+");
  private static final String host = "www.dailymotion.com";
  private static final String name = "Video Detective";

  @Override
  public Map<Quality, URL> getLinks(URL url) throws Exception {
    Map<Quality, URL> links = new EnumMap<Quality, URL>(Quality.class);

    String strUrl = url.toString();
    String id = strUrl.substring(strUrl.lastIndexOf("/") + 1);
    String uri = "http://video.internetvideoarchive.net/player/6/configuration.ashx?customerid=" + id + "&publishedid=7299&reporttag=vdbetatitle&playerid=641&autolist=0&domain=www.videodetective.com&maxrate=high&minrate=low&socialplayer=false";

    JSONObject json = URIRequest.getJsonDocument(new URL(uri).toURI());
    String error = JSONUtils.selectString("/error", json);
    if (error != null && !error.equals("")) {
      return links;
    }

    List<JSONObject> sources = JSONUtils.selectList("//sources", json);
    for (JSONObject source : sources) {
      String label = JSONUtils.selectString("label", source);
      if (label == null) {
        continue;
      }

      if (label.equals("2500 kbs") || label.equals("1500 kbs")) {
        if (links.get(Quality.HD) == null) {
          links.put(Quality.HD, new URL(JSONUtils.selectString("file", source)));
        }
      } else if (label.equals("750 kbs") || label.equals("450 kbs")) {
        if (links.get(Quality.SD) == null) {
          links.put(Quality.SD, new URL(JSONUtils.selectString("file", source)));
        }
      } else if (label.equals("212 kbs") || label.equals("80 kbs")) {
        if (links.get(Quality.SD) == null) {
          links.put(Quality.LD, new URL(JSONUtils.selectString("file", source)));
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
    return name;
  }

  @Override
  protected String getHost() {
    return host;
  }
}
