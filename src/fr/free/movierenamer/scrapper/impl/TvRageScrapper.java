/*
 * movie-renamer
 * Copyright (C) 2012 Nicolas Magré
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
package fr.free.movierenamer.scrapper.impl;

import fr.free.movierenamer.info.*;
import fr.free.movierenamer.info.EpisodeInfo.EpisodeProperty;
import fr.free.movierenamer.scrapper.TvShowScrapper;
import fr.free.movierenamer.searchinfo.TvShow;
import fr.free.movierenamer.utils.EpisodeUtils;
import fr.free.movierenamer.utils.ImageUtils;
import fr.free.movierenamer.utils.WebRequest;
import fr.free.movierenamer.utils.XPathUtils;
import java.net.URL;
import java.util.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Class TvRageScrapper : search tvshow on TvRage
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class TvRageScrapper extends TvShowScrapper {

  private static final String host = "services.tvrage.com";
  private static final String name = "TVRage";
  
  @Override
  public String getName() {
    return name;
  }
  
  @Override
  protected String getHost() {
    return host;
  }
  
  @Override
  public Icon getIcon() {
    return new ImageIcon(ImageUtils.getImageFromJAR("scrapper/tvrage.png"));
  }

  @Override
  protected List<EpisodeInfo> fetchEpisodesInfoList(TvShow tvShow, Locale locale) throws Exception {
    URL episodeListUrl = new URL("http", host, "/feeds/full_show_info.php?sid=" + tvShow.getMediaId());
    Document dom = WebRequest.getXmlDocument(episodeListUrl);

    String seriesName = XPathUtils.selectString("Show/name", dom);
    String seriesStartDate = XPathUtils.selectString("Show/started", dom);

    List<EpisodeInfo> episodes = new ArrayList<EpisodeInfo>(25);
    List<EpisodeInfo> specials = new ArrayList<EpisodeInfo>(5);

    // episodes and specials
    for (Node node : XPathUtils.selectNodes("//episode", dom)) {
      Map<EpisodeProperty, String> fields = new EnumMap<EpisodeProperty, String>(EpisodeProperty.class);
      fields.put(EpisodeProperty.tvShowName, seriesName);
      fields.put(EpisodeProperty.tvShowStartDate, seriesStartDate);
      
      fields.put(EpisodeProperty.name, XPathUtils.getTextContent("title", node));

      Integer episodeNumber = XPathUtils.getIntegerContent("seasonnum", node);
      String seasonIdentifier = XPathUtils.getAttribute("no", node.getParentNode());
      Integer seasonNumber = seasonIdentifier == null ? null : new Integer(seasonIdentifier);
      fields.put(EpisodeProperty.airdate, XPathUtils.getTextContent("airdate", node));

      // check if we have season and episode number, if not it must be a special episode
      if (episodeNumber == null || seasonNumber == null) {
        // handle as special episode
        seasonNumber = XPathUtils.getIntegerContent("season", node);
        Integer specialNumber = EpisodeUtils.filterBySeason(specials, seasonNumber).size() + 1;
        fields.put(EpisodeProperty.episode, episodeNumber.toString());
        fields.put(EpisodeProperty.season, seasonNumber.toString());
        fields.put(EpisodeProperty.special, specialNumber.toString());
        specials.add(new EpisodeInfo(fields));
      } else {
        // handle as normal episode
        fields.put(EpisodeProperty.episode, episodeNumber.toString());
        fields.put(EpisodeProperty.season, seasonNumber.toString());
        episodes.add(new EpisodeInfo(fields));
      }
    }

    // add specials at the end
    episodes.addAll(specials);

    return episodes;
  }
  
  @Override
  protected List<TvShow> searchMedia(String query, Locale locale) throws Exception {
    URL searchUrl = new URL("http", host, "/feeds/full_search.php?show=" + WebRequest.encode(query));
    Document dom = WebRequest.getXmlDocument(searchUrl);

    List<Node> nodes = XPathUtils.selectNodes("Results/show", dom);
    List<TvShow> searchResults = new ArrayList<TvShow>(nodes.size());

    for (Node node : nodes) {
      int showid = Integer.parseInt(XPathUtils.getTextContent("showid", node));
      String name = XPathUtils.getTextContent("name", node);

      searchResults.add(new TvShow(showid, name, null));
    }

    return searchResults;
  }

  @Override
  public TvShowInfo fetchMediaInfo(TvShow tvShow, Locale locale) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Not supported yet.");
    //return null;
  }
  
  @Override
  protected List<ImageInfo> fetchImagesInfo(TvShow tvShow, Locale locale) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Not supported yet.");
    // return null;
  }

  @Override
  protected List<CastingInfo> fetchCastingInfo(TvShow tvShow, Locale locale) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Not supported yet.");
    // return null;
  }

}
