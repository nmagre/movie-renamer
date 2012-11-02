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
import fr.free.movierenamer.info.TvShowInfo.TvShowProperty;
import fr.free.movierenamer.scrapper.TvShowScrapper;
import fr.free.movierenamer.searchinfo.TvShow;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.EpisodeUtils;
import fr.free.movierenamer.utils.ImageUtils;
import fr.free.movierenamer.utils.WebRequest;
import fr.free.movierenamer.utils.XPathUtils;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Class TheTVDBScrapper : search tvshow on TheTVDB
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public final class TheTVDBScrapper extends TvShowScrapper {
  private static final String host = "www.thetvdb.com";
  private static final String name = "TheTVDB";

  /**
   * @see http://thetvdb.com/?tab=apiregister
   */
  private final String apikey;

  public TheTVDBScrapper() {
    String key = Settings.decodeApkKey(Settings.getApplicationProperty("thetvdb.apkapikey"));
    if (key == null) {
      throw new NullPointerException("apikey must not be null");
    }
    this.apikey = key;
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
  public Icon getIcon() {
    return new ImageIcon(ImageUtils.getImageFromJAR("scrapper/thetvdb.png"));
  }

  @Override
  protected List<EpisodeInfo> fetchEpisodesInfoList(TvShow tvShow, Locale locale) throws Exception {
    Document seriesRecord = getTvShowRecord(tvShow, locale.getLanguage());

    // we could get the series name from the search result, but the language may not match the given parameter
    String seriesName = XPathUtils.selectString("Data/Series/SeriesName", seriesRecord);
    String seriesStartDate = XPathUtils.selectString("Data/Series/FirstAired", seriesRecord);

    List<Node> nodes = XPathUtils.selectNodes("Data/Episode", seriesRecord);

    List<EpisodeInfo> episodes = new ArrayList<EpisodeInfo>(nodes.size());
    List<EpisodeInfo> specials = new ArrayList<EpisodeInfo>(5);

    for (Node node : nodes) {
      Map<EpisodeProperty, String> fields = new EnumMap<EpisodeProperty, String>(EpisodeProperty.class);
      fields.put(EpisodeProperty.tvShowName, seriesName);
      fields.put(EpisodeProperty.tvShowStartDate, seriesStartDate);
      fields.put(EpisodeProperty.name, XPathUtils.getTextContent("EpisodeName", node));
      fields.put(EpisodeProperty.absolute, XPathUtils.getTextContent("absolute_number", node));
      fields.put(EpisodeProperty.airdate, XPathUtils.getTextContent("FirstAired", node));

      Integer episodeNumber = XPathUtils.getIntegerContent("EpisodeNumber", node);
      Integer seasonNumber = XPathUtils.getIntegerContent("SeasonNumber", node);

      if (seasonNumber == null || seasonNumber == 0) {
        // handle as special episode
        Integer airsBefore = XPathUtils.getIntegerContent("airsbefore_season", node);
        if (airsBefore != null) {
          seasonNumber = airsBefore;
        }

        // use given episode number as special number or count specials by ourselves
        Integer specialNumber = (episodeNumber != null) ? episodeNumber : EpisodeUtils.filterBySeason(specials, seasonNumber).size() + 1;
        
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

    EpisodeUtils.sortEpisodes(episodes);

    // add specials at the end
    episodes.addAll(specials);

    return episodes;
  }

  @Override
  protected TvShowInfo fetchMediaInfo(TvShow tvShow, Locale locale) throws Exception {
    URL url = new URL("http", host, "/api/" + apikey + "/series/" + tvShow.getMediaId() + "/" + locale.getLanguage() + ".xml");
    Document dom = WebRequest.getXmlDocument(url);
    
    Node node = XPathUtils.selectNode("//Series", dom);
    Map<TvShowProperty, String> fields = new EnumMap<TvShowProperty, String>(TvShowProperty.class);
    
    fields.put(TvShowProperty.bannerRootUrl, new URL("http", host, "/banners/").toString());
    
    fields.put(TvShowProperty.id, XPathUtils.getTextContent("id", node));
    fields.put(TvShowProperty.IMDB_ID, XPathUtils.getTextContent("IMDB_ID", node));
    fields.put(TvShowProperty.actors, XPathUtils.getTextContent("Actors", node));
    fields.put(TvShowProperty.firstAired, XPathUtils.getTextContent("FirstAired", node));
    fields.put(TvShowProperty.genre, XPathUtils.getTextContent("Genre", node));
    fields.put(TvShowProperty.language, XPathUtils.getTextContent("Language", node));
    fields.put(TvShowProperty.overview, XPathUtils.getTextContent("Overview", node));
    fields.put(TvShowProperty.rating, XPathUtils.getTextContent("Rating", node));
    fields.put(TvShowProperty.votes, XPathUtils.getTextContent("RatingCount", node));
    fields.put(TvShowProperty.runtime, XPathUtils.getTextContent("Runtime", node));
    fields.put(TvShowProperty.name, XPathUtils.getTextContent("SeriesName", node));
    fields.put(TvShowProperty.status, XPathUtils.getTextContent("Status", node));
    fields.put(TvShowProperty.banner, XPathUtils.getTextContent("banner", node));
    fields.put(TvShowProperty.fanart, XPathUtils.getTextContent("fanart", node));
    fields.put(TvShowProperty.poster, XPathUtils.getTextContent("poster", node));
    
    TvShowInfo tvShowInfo = new TvShowInfo(fields);
    return tvShowInfo;
  }

  @Override
  protected List<TvShow> searchMedia(String query, Locale locale) throws Exception {
    URL searchUrl = new URL("http", host, "/api/GetSeries.php?seriesname=" + WebRequest.encode(query) + "&language=" + locale.getLanguage());
    Document dom = WebRequest.getXmlDocument(searchUrl);

    List<Node> nodes = XPathUtils.selectNodes("Data/Series", dom);
    Map<Integer, TvShow> resultSet = new LinkedHashMap<Integer, TvShow>(nodes.size());

    for (Node node : nodes) {
      int sid = XPathUtils.getIntegerContent("seriesid", node);
      String seriesName = XPathUtils.getTextContent("SeriesName", node);

      if (!resultSet.containsKey(sid)) {
        //search can have multiple times the result (fr, en, ...)
        resultSet.put(sid, new TvShow(sid, seriesName, null));
      }
    }

    return new ArrayList<TvShow>(resultSet.values());
  }

  private final Document getTvShowRecord(TvShow tvShow, String languageCode) throws Exception {
    URL tvShowRecord = new URL("http", host, "/api/" + apikey + "/series/" + tvShow.getMediaId() + "/all/" + languageCode + ".zip");

    try {

      ZipInputStream zipInputStream = new ZipInputStream(tvShowRecord.openStream());
      ZipEntry zipEntry;

      try {
        String tvShowRecordName = languageCode + ".xml";

        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
          if (tvShowRecordName.equals(zipEntry.getName())) {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(zipInputStream);
          }
        }

        // zip file must contain the series record
        throw new FileNotFoundException(String.format("Archive must contain %s: %s", tvShowRecordName, tvShowRecord));
      } finally {
        zipInputStream.close();
      }
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException(String.format("TvShow record not found: %s [%s]: %s", tvShow.getName(), languageCode, tvShowRecord));
    }
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