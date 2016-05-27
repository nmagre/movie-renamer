/*
 * movie-renamer-core
 * Copyright (C) 2012-2013 Nicolas Magré
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
package fr.free.movierenamer.scraper.impl.movie;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.info.MediaInfo.MediaProperty;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.MovieInfo.MovieMultipleProperty;
import fr.free.movierenamer.info.MovieInfo.MovieProperty;
import fr.free.movierenamer.info.VideoInfo.VideoProperty;
import fr.free.movierenamer.scraper.MovieScraper;
import fr.free.movierenamer.scraper.SearchParam;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.NumberUtils;
import fr.free.movierenamer.utils.ScraperUtils;
import fr.free.movierenamer.utils.ScraperUtils.AvailableApiIds;
import fr.free.movierenamer.utils.URIRequest;
import fr.free.movierenamer.utils.XPathUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Class Kinopoisk : search movie on Kinopoisk
 *
 * @author Nicolas Magré
 */
public class KinopoiskScraper extends MovieScraper {

  private static final String host = "www.kinopoisk.ru";
  private static final String name = "Kinopoisk";
  private static final String imgHost = "http://st.kinopoisk.ru/images/sm_film/";
  private final Pattern mpaaCodePattern = Pattern.compile("Rated ([RPGN][GC]?(?:-\\d{2})?)");
  private static final AvailableApiIds supportedId = AvailableApiIds.KINOPOISK;

  private enum MInfo {

    год,// Year
    страна,// Country
    слоган,// Tagline
    жанр,// Genre
    бюджет,// Budget
    рейтинг_MPAA,// Mpaa
    время,// runtime
    возраст// Russian certification

  }

  public KinopoiskScraper() {
    super(AvailableLanguages.ru);
  }

  @Override
  public AvailableApiIds getSupportedId() {
    return supportedId;
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
  protected AvailableLanguages getDefaultLanguage() {
    return AvailableLanguages.ru;
  }

  @Override
  public IdInfo getIdfromURL(URL url) {
    try {
      return new IdInfo(findKinopoiskId(url.toExternalForm()), supportedId);
    } catch (Exception ex) {
    }

    return null;
  }

  @Override
  public URL getURL(IdInfo id) {
    try {
      return new URL("http", host, "/film/" + id);
    } catch (MalformedURLException ex) {
    }

    return null;
  }

  @Override
  protected List<Movie> searchMedia(String query, SearchParam sep, AvailableLanguages language) throws Exception {
    URL searchUrl = new URL("http", host, "/s/type/film/list/1/find/" + URIRequest.encode(query));
    return searchMedia(searchUrl, sep, language);
  }

  @Override
  protected List<Movie> searchMedia(URL searchUrl, SearchParam sep, AvailableLanguages language) throws Exception {
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI());
    // select movie results
    List< Node> nodes = XPathUtils.selectNodes("//DIV[@class='search_results search_results_last']//DIV[contains(@class,'element')]", dom);
    List<Movie> results = new ArrayList<>(nodes.size());

    for (Node node : nodes) {
      Node infoNode = XPathUtils.selectNode("DIV[@class='info']", node);
      String title = XPathUtils.selectNode("P/A", infoNode).getTextContent().trim();
      // Skip video, tv serie
      if (title.contains("(TB)") || title.contains("(видео)") || title.contains("(сериал)")) {
        continue;
      }

      String origtitle = XPathUtils.selectNode("SPAN[@class='gray']", infoNode).getTextContent().trim();
      origtitle = origtitle.replaceAll(",? ?\\d+ мин", "").trim();

      String syear = XPathUtils.selectString("P/SPAN[@class='year']", infoNode);
      Integer year = -1;
      if (syear != null && NumberUtils.isDigit(syear)) {
        year = Integer.parseInt(syear);
      }

      int id = findKinopoiskId(XPathUtils.getAttribute("href", XPathUtils.selectNode("P/A", infoNode)));
      // Impossible to get title (thera are two title attribute) which contains image src :(
      URL thumb = new URL(imgHost + id + ".jpg");// FIXME trouver un moyen de by-passer ce prob pour pas avoir l'image "no image' de kinopoisk

      results.add(new Movie(null, new IdInfo(id, ScraperUtils.AvailableApiIds.KINOPOISK), title, origtitle, thumb, year));
    }

    return results;
  }

  @Override
  protected MovieInfo fetchMediaInfo(Movie movie, IdInfo id, AvailableLanguages language) throws Exception {
    URL searchUrl = new URL("http", host, "/film/" + id);
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI());

    Map<MediaInfo.MediaInfoProperty, String> info = new HashMap<>();
    Map<MovieMultipleProperty, List<String>> multipleInfo = new EnumMap<>(MovieMultipleProperty.class);
    List<String> genres = new ArrayList<>();
    List<String> countries = new ArrayList<>();
    List<String> studios = new ArrayList<>();

    String title = movie.getName();
    Node titleNode = XPathUtils.selectNode("//H1[@class='moviename-big'][@itemprop='name']", dom);
    if (titleNode != null) {
      title = titleNode.getTextContent();
    }
    info.put(MediaInfo.MediaProperty.title, title);

    titleNode = XPathUtils.selectNode("//SPAN[@itemprop='alternativeHeadline']", dom);
    if (titleNode != null) {
      info.put(MediaProperty.originalTitle, titleNode.getTextContent());
    } else {
      info.put(MediaProperty.originalTitle, movie.getOriginalName());
    }

    List<Node> nodes = XPathUtils.selectNodes("//TABLE[@class='info']//TR", dom);
    for (Node node : nodes) {
      String infoname = XPathUtils.selectString("TD[@class='type']", node);
      if (infoname == null || infoname.equals("")) {
        continue;
      }

      MInfo minfo;
      try {
        minfo = MInfo.valueOf(infoname.replace(" ", "_"));
      } catch (IllegalArgumentException e) {
        continue;
      }

      switch (minfo) {
        case год: // Year
          String syear = XPathUtils.selectString("TD//A", node);
          if (syear != null && NumberUtils.isNumeric(syear)) {
            info.put(VideoProperty.releasedDate, syear);
            info.put(MediaInfo.MediaProperty.year, syear);
          }
          break;
        case страна:// Country
          for (Node cnode : XPathUtils.selectNodes("TD/DIV/A", node)) {
            countries.add(cnode.getTextContent());
          }
          break;
        case слоган:// Tagline
          String tagline = XPathUtils.selectString("TD[2]", node);
          if (tagline != null && !tagline.isEmpty()) {
            info.put(MovieProperty.tagline, tagline.replaceAll("[«»]", ""));
          }
          break;
        case жанр:// Genre
          for (Node cnode : XPathUtils.selectNodes("TD/SPAN/A", node)) {
            genres.add(cnode.getTextContent());
          }
          break;
        case бюджет:// Budget
          String budget = XPathUtils.selectString("TD/DIV/A", node);
          if (budget != null && !budget.isEmpty()) {
            info.put(MovieProperty.budget, budget);
          }
          break;
        case возраст:
          Node certnode = XPathUtils.selectNode("//DIV[contains(@class, 'ageLimit')]", node);
          if (certnode != null) {
            String cert = XPathUtils.getAttribute("class", certnode);
            cert = cert.replace("ageLimit age", "");
            try {
              String mpaa = MovieInfo.MotionPictureRating.getMpaaCode(cert + "+", MovieInfo.MotionPictureRating.RUSSIA);
              info.put(MovieProperty.certificationCode, mpaa);
            } catch (Exception e) {
              // don't care about
            }
          }
          break;
        case рейтинг_MPAA:// Mpaa
          // Override russian cert by mpaa
          Node tmpaanode = XPathUtils.selectNode("TD/A", node);
          if (tmpaanode != null) {
            info.put(MovieProperty.certificationCode, XPathUtils.getAttribute("href", tmpaanode).replaceAll(".*rn/", "").replace("/", ""));
          }

          tmpaanode = XPathUtils.selectNode("TD/SPAN", node);
          if (tmpaanode != null) {
            info.put(MovieProperty.certification, tmpaanode.getTextContent().trim());
          }

          break;
        case время:// runtime
          String runtime = XPathUtils.selectString("TD[@id='runtime']", node);
          if (runtime != null && !runtime.isEmpty()) {
            runtime = runtime.replaceAll("мин.*", "").trim();
            if (NumberUtils.isNumeric(runtime)) {
              info.put(VideoProperty.runtime, runtime);
            }
          }
          break;
      }
    }

    // Rating
    Node rateNode = XPathUtils.selectNode("//SPAN[@class = 'rating_ball']", dom);
    if (rateNode != null) {
      String rate = rateNode.getTextContent().trim();
      try {
        Double rating = Double.parseDouble(rate);
        info.put(MediaInfo.MediaProperty.rating, "" + rating);
      } catch (Exception ex) {
      }
    }

    // vote
    rateNode = XPathUtils.selectNode("//SPAN[@class = 'ratingCount']", dom);
    if (rateNode != null) {
      info.put(MovieProperty.votes, rateNode.getTextContent());
    }

    Node synopNode = XPathUtils.selectNode("//DIV[@class='brand_words'][@itemprop='description']", dom);
    if (synopNode != null) {
      info.put(MovieProperty.overview, synopNode.getTextContent());
    }

    // Studios
    try {
      searchUrl = new URL("http", host, "/film/" + movie.getMediaId() + "/studio/");
      dom = URIRequest.getHtmlDocument(searchUrl.toURI());
      nodes = XPathUtils.selectNodes("//TABLE//TR/TD/B[text() = \"Производство:\"]/../../..//TR/TD/A", dom);
      for (Node node : nodes) {
        studios.add(node.getTextContent().trim());
      }
    } catch (Exception ex) {

    }

    List<IdInfo> ids = new ArrayList<>();
    ids.add(movie.getMediaId());
    ids.add(id);

    multipleInfo.put(MovieMultipleProperty.studios, studios);
    multipleInfo.put(MovieMultipleProperty.countries, countries);
    multipleInfo.put(MovieMultipleProperty.genres, genres);

    MovieInfo movieInfo = new MovieInfo(info, multipleInfo, ids);
    return movieInfo;
  }

  @Override
  protected List<CastingInfo> fetchCastingInfo(Movie search, IdInfo id, AvailableLanguages language) throws Exception {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  private int findKinopoiskId(String source) {
    Matcher matcher = Pattern.compile("film/(\\d+)/").matcher(source);

    if (matcher.find()) {
      return Integer.parseInt(matcher.group(1));
    }

    // not found
    throw new IllegalArgumentException(String.format("Cannot find imdb id: %s", source));
  }

  @Override
  public InfoQuality getQuality() {
    return InfoQuality.GREAT;
  }

  @Override
  protected List<ImageInfo> fetchImagesInfo(Movie movie) throws Exception {
    movie.setImdbId(ScraperUtils.movieIdLookup(AvailableApiIds.IMDB, null, movie, settings.getSearchScraperLang()));
    return super.fetchImagesInfo(movie);
  }
}
