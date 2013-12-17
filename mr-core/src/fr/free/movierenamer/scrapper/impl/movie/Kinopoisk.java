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
package fr.free.movierenamer.scrapper.impl.movie;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.MovieInfo.MovieProperty;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.NumberUtils;
import fr.free.movierenamer.utils.ScrapperUtils;
import fr.free.movierenamer.utils.URIRequest;
import fr.free.movierenamer.utils.XPathUtils;
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

/**
 * Class Kinopoisk : search movie on Kinopoisk
 *
 * @author Nicolas Magré
 */
public class Kinopoisk extends MovieScrapper {

  private static final String host = "www.kinopoisk.ru";
  private static final String name = "Kinopoisk";
  private static final String imgHost = "http://st.kinopoisk.ru/images/sm_film/";
  private final Pattern mpaaCodePattern = Pattern.compile("Rated ([RPGN][GC]?(?:-\\d{2})?)");

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

  public Kinopoisk() {
    super(LocaleUtils.AvailableLanguages.ru);
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
  protected Locale getDefaultLanguage() {
    return LocaleUtils.AvailableLanguages.ru.getLocale();
  }

  @Override
  protected List<Movie> searchMedia(String query, Locale language) throws Exception {
    URL searchUrl = new URL("http", host, "/s/type/film/list/1/find/" + URIRequest.encode(query));
    return searchMedia(searchUrl, language);
  }

  @Override
  protected List<Movie> searchMedia(URL searchUrl, Locale language) throws Exception {
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI());
    // select movie results
    List< Node> nodes = XPathUtils.selectNodes("//DIV[@class='search_results search_results_last']//DIV[contains(@class,'element')]", dom);
    List<Movie> results = new ArrayList<Movie>(nodes.size());

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
      // Impossible to get title (thera are two title) wich contains image src :(
      URL thumb = new URL(imgHost + id + ".jpg");

      results.add(new Movie(null, new IdInfo(id, ScrapperUtils.AvailableApiIds.KINOPOISK), title, origtitle, thumb, year));
    }

    return results;
  }

  @Override
  protected MovieInfo fetchMediaInfo(Movie movie, Locale language) throws Exception {
    URL searchUrl = new URL("http", host, "/film/" + movie.getMediaId());
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI());

    Map<MovieProperty, String> fields = new EnumMap<MovieProperty, String>(MovieProperty.class);
    Map<MovieInfo.MovieMultipleProperty, List<String>> multipleFields = new EnumMap<MovieInfo.MovieMultipleProperty, List<String>>(MovieInfo.MovieMultipleProperty.class);
    List<String> genres = new ArrayList<String>();
    List<String> countries = new ArrayList<String>();
    List<String> studios = new ArrayList<String>();

    Node titleNode = XPathUtils.selectNode("//H1[@class='moviename-big'][@itemprop='name']", dom);
    if (movie.getName() == null) {
      fields.put(MovieProperty.title, titleNode.getTextContent());
    } else {
      fields.put(MovieProperty.title, movie.getName());
    }

    titleNode = XPathUtils.selectNode("//SPAN[@itemprop='alternativeHeadline']", dom);
    if (movie.getOriginalTitle() == null) {
      fields.put(MovieProperty.originalTitle, titleNode.getTextContent());
    } else {
      fields.put(MovieProperty.title, movie.getOriginalTitle());
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
            fields.put(MovieProperty.releasedDate, syear);
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
            fields.put(MovieProperty.tagline, tagline.replaceAll("[«»]", ""));
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
            fields.put(MovieProperty.budget, budget);
          }
          break;
        case возраст:
          Node certnode = XPathUtils.selectNode("//DIV[contains(@class, 'ageLimit')]", node);
          if (certnode != null) {
            String cert = XPathUtils.getAttribute("class", certnode);
            cert = cert.replace("ageLimit age", "");
            try {
              String mpaa = MovieInfo.MotionPictureRating.getMpaaCode(cert + "+", MovieInfo.MotionPictureRating.RUSSIA);
              fields.put(MovieProperty.certificationCode, mpaa);
            } catch (Exception e) {
              // don't care about
            }
          }
          break;
        case рейтинг_MPAA:// Mpaa
          // Override russian cert by mpaa
          Node tmpaanode = XPathUtils.selectNode("TD/A", node);
          if (tmpaanode != null) {
            fields.put(MovieProperty.certificationCode, XPathUtils.getAttribute("href", tmpaanode).replaceAll(".*rn/", "").replace("/", ""));
          }

          break;
        case время:// runtime
          String runtime = XPathUtils.selectString("TD[@id='runtime']", node);
          if (runtime != null && !runtime.isEmpty()) {
            runtime = runtime.replaceAll("мин.*", "").trim();
            if (NumberUtils.isNumeric(runtime)) {
              fields.put(MovieProperty.runtime, runtime);
            }
          }
          break;
      }
    }

    Node synopNode = XPathUtils.selectNode("//DIV[@class='brand_words'][@itemprop='description']", dom);
    if (synopNode != null) {
      fields.put(MovieProperty.overview, synopNode.getTextContent());
    }

    List<IdInfo> ids = new ArrayList<IdInfo>();
    ids.add(movie.getMediaId());

    multipleFields.put(MovieInfo.MovieMultipleProperty.studios, studios);
    multipleFields.put(MovieInfo.MovieMultipleProperty.countries, countries);
    multipleFields.put(MovieInfo.MovieMultipleProperty.genres, genres);

    MovieInfo movieInfo = new MovieInfo(ids, fields, multipleFields);
    return movieInfo;
  }

  @Override
  protected List<CastingInfo> fetchCastingInfo(Movie search, Locale language) throws Exception {
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
  public ScrapperUtils.InfoQuality getInfoQuality() {
    return ScrapperUtils.InfoQuality.GREAT;
  }

  @Override
  protected List<ImageInfo> fetchImagesInfo(Movie movie) throws Exception {
    movie.setImdbId(ScrapperUtils.imdbIdLookup(null, movie));
    return super.fetchImagesInfo(movie);
  }
}
