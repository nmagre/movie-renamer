/*
 * movie-renamer-core
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.EpisodeInfo;
import fr.free.movierenamer.info.EpisodeInfo.EpisodeProperty;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.TvShowInfo;
import fr.free.movierenamer.info.TvShowInfo.TvShowProperty;
import fr.free.movierenamer.scrapper.TvShowScrapper;
import fr.free.movierenamer.searchinfo.TvShow;
import fr.free.movierenamer.utils.Date;
import fr.free.movierenamer.utils.EpisodeUtils;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.URIRequest;
import fr.free.movierenamer.utils.XPathUtils;
import java.util.Arrays;

/**
 * Class TvRageScrapper : search tvshow on TvRage
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class TvRageScrapper extends TvShowScrapper {

  private static final String host = "services.tvrage.com";
  private static final String name = "TVRage";

  public TvRageScrapper() {
    super(AvailableLanguages.en);
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
  protected List<TvShow> searchMedia(String query, Locale language) throws Exception {
    URL searchUrl = new URL("http", host, "/feeds/search.php?show=" + URIRequest.encode(query));
    Document dom = URIRequest.getXmlDocument(searchUrl.toURI());

    List<Node> nodes = XPathUtils.selectNodes("Results/show", dom);
    List<TvShow> searchResults = new ArrayList<TvShow>(nodes.size());

    for (Node node : nodes) {
      int showid = Integer.parseInt(XPathUtils.getTextContent("showid", node));
      String name = XPathUtils.getTextContent("name", node);
      URL url = getPosterURL(XPathUtils.getTextContent("link", node));
      int year = XPathUtils.getIntegerContent("started", node);

      searchResults.add(new TvShow(showid, name, url, year));
    }

    return searchResults;
  }

  @Override
  protected TvShowInfo fetchMediaInfo(TvShow tvShow, Locale language) throws Exception {
    URL episodeListUrl = new URL("http", host, "/feeds/showinfo.php?sid=" + tvShow.getMediaId());
    Document dom = URIRequest.getXmlDocument(episodeListUrl.toURI());

    Map<TvShowProperty, String> fields = new EnumMap<TvShowProperty, String>(TvShowProperty.class);

    Node node = XPathUtils.selectNode("Showinfo", dom);
    fields.put(TvShowProperty.id, XPathUtils.getTextContent("showid", node));
    fields.put(TvShowProperty.name, XPathUtils.getTextContent("showname", node));
    fields.put(TvShowProperty.firstAired, Date.parse(XPathUtils.getTextContent("startdate", node), "MMM/dd/yyyy", Locale.ENGLISH).toString());
    fields.put(TvShowProperty.status, XPathUtils.getTextContent("status", node));
    URL posterURL = getPosterURL(XPathUtils.getTextContent("showlink", node));
    if (posterURL != null) {
      fields.put(TvShowProperty.posterPath, posterURL.toExternalForm());
    }
    String genres = null;
    for (Node genre : XPathUtils.selectNodes("genres/genre", node)) {
      String toAdd = XPathUtils.getTextContent(genre);
      if (toAdd.length() == 0) {
        continue;
      }
      if (genres == null) {
        genres = new String();
      } else {
        genres += "|";
      }
      genres += toAdd;
    }
    if (genres != null) {
      fields.put(TvShowProperty.genre, genres);
    }
    fields.put(TvShowProperty.runtime, XPathUtils.getTextContent("runtime", node));

    TvShowInfo tvShowInfo = new TvShowInfo(fields);
    return tvShowInfo;
  }

  @Override
  protected List<EpisodeInfo> fetchEpisodesInfoList(TvShow tvShow, Locale language) throws Exception {
    URL episodeListUrl = new URL("http", host, "/feeds/episode_list.php?sid=" + tvShow.getMediaId());
    Document dom = URIRequest.getXmlDocument(episodeListUrl.toURI());

    List<EpisodeInfo> episodes = new ArrayList<EpisodeInfo>(25);
    List<EpisodeInfo> specials = new ArrayList<EpisodeInfo>(5);

    // episodes and specials
    for (Node node : XPathUtils.selectNodes("//episode", dom)) {
      Map<EpisodeProperty, String> fields = new EnumMap<EpisodeProperty, String>(EpisodeProperty.class);
      fields.put(EpisodeProperty.tvShowName, tvShow.getName());
      fields.put(EpisodeProperty.tvShowStartDate, Integer.toString(tvShow.getYear()));

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
  protected List<ImageInfo> fetchImagesInfo(TvShow tvShow, Locale language) throws Exception {
    return null;
  }

  @Override
  protected List<CastingInfo> fetchCastingInfo(TvShow tvShow, Locale language) throws Exception {
    return null;
  }

  private URL getPosterURL(String showlink) {
    URL posterURL;
    if (showlink != null) {
      try {
        URL showURL = new URL(showlink);
        String tvshowPage = URIRequest.getDocumentContent(showURL.toURI());
        Matcher searchMatcher = Pattern.compile("src='(http://images.tvrage.com/shows/.*)'").matcher(tvshowPage);
        if (searchMatcher.find()) {
          String tvrageThumb = searchMatcher.group(1);
          posterURL = new URL(tvrageThumb);
        } else {
          posterURL = null;
        }
      } catch (MalformedURLException ex) {
        posterURL = null;
      } catch (SAXException ex) {
        posterURL = null;
      } catch (IOException ex) {
        posterURL = null;
      } catch (URISyntaxException ex) {
        posterURL = null;
      }
    } else {
      posterURL = null;
    }
    return posterURL;
  }

}
