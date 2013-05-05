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
package fr.free.movierenamer.scrapper.impl.movie;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.CastingInfo.PersonProperty;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.ImageInfo.ImageCategoryProperty;
import fr.free.movierenamer.info.ImageInfo.ImageProperty;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.MovieInfo.MovieProperty;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.NumberUtils;
import fr.free.movierenamer.utils.ScrapperUtils;
import fr.free.movierenamer.utils.StringUtils;
import fr.free.movierenamer.utils.URIRequest;
import fr.free.movierenamer.utils.XPathUtils;
import java.util.Arrays;

/**
 * Class IMDbScrapper : search movie on IMDB
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class IMDbScrapper extends MovieScrapper {

  private static final String defaultHost = "www.imdb.com";
  private static final String name = "IMDb";
  private static final String CHARSET = URIRequest.ISO;
  private String host;

  public IMDbScrapper() {
    super(AvailableLanguages.en/*, AvailableLanguages.fr, AvailableLanguages.es, AvailableLanguages.it, AvailableLanguages.de*/);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  protected String getHost() {
    return getHost(getLanguage());
  }

  protected final String getHost(Locale language) {
    if (host == null) {
      host = defaultHost;
      if (language != null && !language.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
        try {
          URL url = new URL("http", defaultHost.replace("com", language.getLanguage()), "");
          int responseCode = URIRequest.getResponseCode(url);
          if (responseCode == 200) {
            host = url.getHost();
          } else {
            host = defaultHost;
          }
        } catch (Exception ex) {
          host = defaultHost;
        }
      } else {
        host = defaultHost;
      }
    }
    return host;
  }

  public enum ImdbInfoPattern {

    TITLE("<title>(.* \\(.*\\d+.*\\).*)</title>"),
    THUMB("src=\"(http://ia.media-imdb.com/images/.*)\""),
    ORIGTITLE("info-content.>\\s+\"(.*)\"&nbsp.*?[Oo]riginal"),
    RUNTIME("<h5>.*?:</h5><div class=\".*\">.*?(\\d+) [Mm]in"),
    RATING("<b>(.[\\.,].)/10</b>"),
    VOTES("tn15more.>(.*) (?:vot..?|Stimmen)</a>"),
    DIRECTOR("src=./rg/directorlist/position-\\d+/images/b.gif.link=name/nm(\\d+)/.;\">(.*)</a>"),
    WRITER("src=./rg/writerlist/position-\\d/images/b.gif.link=name/nm(\\d+)/.;\">(.*)</a>"),
    GENRE("<h5>G(?:e|&#xE9;)n...?:</h5>\n.*info-content.*\n(.*)"),
    TAGLINE("<div class=\"info-content\">\n(.*)<a class=\".*\" href=\"/title/tt\\d+/taglines\""), // Only on .com site (english)
    PLOT("<div class=.info-content.>\n(.*)(?:\n?)<a class=..*. href=./title/tt\\d+/plotsummary."),
    CAST("<h3>((Cast)|(Ensemble)|(Besetzung)|(Reparto))</h3>.*"),
    ACTOR("\"><img src=\".*/rg/castlist/position-\\d+/images/b.gif.link=/name/nm\\d+/';\">.*</td>"),
    COUNTRY("<h5>(?:(?:Country)|(?:Pays)|(?:Nazionalit&#xE0;)|(?:Pa&#xED;s)|(?:Land)):</h5><div class=\"info-content\">(.*?)</div></div><div class=\"info\"><h5>.*?:</h5>"),
    STUDIO("<h5>(?:(?:Company)|(?:Soci&#xE9;t&#xE9;)|(?:Compagnia)|(?:Compa&#xF1;&#xED;a)|(?:Firma)):</h5><div class=..*.><a href=..*.>(.*)</a><a"),
    TOP250("<a href=./chart/top\\?tt\\d{7}.>(?:(?:Top 250)|(?:Las 250 m&#xE1;s votadas)): #(\\d{1,3})</a>");
    private final Pattern pattern;

    private ImdbInfoPattern(String pattern) {
      this.pattern = Pattern.compile(pattern);
    }

    public Pattern getPattern() {
      return pattern;
    }

    public String getPatternString() {
      return pattern.toString();
    }
  }

  private String createImgPath(String imgPath) {
    return imgPath.replaceAll("S[XY]\\d+(.)+\\.jpg", "SY70_SX100.jpg");
  }

  @Override
  protected List<Movie> searchMedia(String query, Locale language) throws Exception {
    // http://www.imdb.com/find?s=tt&ref_=fn_tt&q=
    // Only title -> ref_=fn_tt
    // Only movie -> ref_=fn_ft
    // Add an option to select between both (default "title" because "movie" does not find video)
    URL searchUrl = new URL("http", getHost(language), "/find?s=tt&ref_=fn_tt&q=" + URIRequest.encode(query));
    return searchMedia(searchUrl, language);

  }

  @Override
  protected List<Movie> searchMedia(URL searchUrl, Locale language) throws Exception {
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI());

    // select movie results
    List<Node> nodes = XPathUtils.selectNodes("//TABLE[@class='findList']//TR", dom);
    List<Movie> results = new ArrayList<Movie>(nodes.size());

    for (Node node : nodes) {
      try {
        Node retNode = XPathUtils.selectNode("TD[@class='result_text']", node);
        String title = XPathUtils.selectNode("A", retNode).getTextContent().trim();
        Matcher m = Pattern.compile("\\((\\d{4}).*\\)").matcher(retNode.getTextContent());
        String year;
        if (m.find()) {
          year = m.group(1);
        } else {
          year = "-1";
        }
        // String year = retNode.getTextContent().replaceAll(title, "").replaceAll("\\D", "").replaceAll("[\\p{Punct}\\p{Space}]+", ""); // remove non-number characters //FIXME à refaire !
        String href = XPathUtils.getAttribute("href", XPathUtils.selectNode("A", retNode));
        int imdbid = findImdbId(href);
        URL thumb;
        try {
          String imgPath = XPathUtils.getAttribute("src", XPathUtils.selectNode("TD[@class='primary_photo']/A/IMG", node));
          thumb = new URL(createImgPath(imgPath));
        } catch (Exception ex) {
          thumb = null;
        }

        results.add(new Movie(new IdInfo(imdbid, ScrapperUtils.AvailableApiIds.IMDB), title, null, thumb, Integer.parseInt(year)));
      } catch (Exception e) {
        // ignore
      }
    }


    // we might have been redirected to the movie page
    if (results.isEmpty()) {
      try {
        int imdbid = findImdbId(XPathUtils.selectString("//LINK[@rel='canonical']/@href", dom));
        MovieInfo info = fetchMediaInfo(new Movie(new IdInfo(imdbid, ScrapperUtils.AvailableApiIds.IMDB), null, null, null, -1), language);
        URL thumb;
        try {
          String imgPath = info.getPosterPath().toURL().toExternalForm();
          thumb = new URL(createImgPath(imgPath));
        } catch (Exception ex) {
          thumb = null;
        }
        Movie movie = new Movie(new IdInfo(imdbid, ScrapperUtils.AvailableApiIds.IMDB), info.getTitle(), null, thumb, info.getYear());
        if (movie != null) {
          results.add(movie);
        }
      } catch (Exception e) {
        // ignore, can't find movie
      }
    }


    return results;
  }

  @Override
  protected MovieInfo fetchMediaInfo(Movie movie, Locale language) throws Exception {
    // http://www.imdb.com/title/
    // or http://www.deanclatworthy.com/imdb/
    // or new URL("http", "www.imdb.com",
    // String.format("/title/tt%07d/releaseinfo", movie.getMovieId())
    // new URL("http", "www.imdb.com", String.format("/title/tt%07d/combined",
    // movie.getMovieId())
    URL searchUrl = new URL("http", getHost(language), String.format("/title/%s/combined", movie.getMediaId()));
    String moviePage = URIRequest.getDocumentContent(searchUrl.toURI());

    Pattern pattern;

    Map<MovieProperty, String> fields = new EnumMap<MovieProperty, String>(MovieProperty.class);

    // Title + Year
    Matcher searchMatcher = ImdbInfoPattern.TITLE.getPattern().matcher(moviePage);
    if (searchMatcher.find()) {
      String res = searchMatcher.group(1);

      pattern = Pattern.compile("(.*)\\(\\d{4}\\).\\(.*\\)");// Fixed issue 7, E.g: 6 Guns (2010) (V)
      searchMatcher = pattern.matcher(res);
      String title;
      if (searchMatcher.find()) {
        title = searchMatcher.group(1);
      } else {
        title = res.substring(0, res.lastIndexOf("(") - 1);
      }
      fields.put(MovieProperty.title, StringUtils.unEscapeXML(title, CHARSET));

      // Get year
      pattern = Pattern.compile("\\((\\d{4}).*\\)");
      searchMatcher = pattern.matcher(res);
      if (searchMatcher.find()) {
        res = searchMatcher.group(1);
        if (res != null && NumberUtils.isDigit(res)) {
          int year = Integer.parseInt(res);
          if (year >= 1900 && year <= Calendar.getInstance().get(Calendar.YEAR)) {// Before all "movies" producted are more short video than a movie
            fields.put(MovieProperty.releasedDate, res);
          }
        }
      }
    } else {
      Settings.LOGGER.log(Level.SEVERE, "No title found in imdb page");
    }

    // Original Title
    searchMatcher = ImdbInfoPattern.ORIGTITLE.getPattern().matcher(moviePage);
    if (searchMatcher.find()) {
      fields.put(MovieProperty.originalTitle, StringUtils.unEscapeXML(searchMatcher.group(1), CHARSET));
    }

    // Runtime
    searchMatcher = ImdbInfoPattern.RUNTIME.getPattern().matcher(moviePage);
    if (searchMatcher.find()) {
      String runtime = searchMatcher.group(1);
      fields.put(MovieProperty.runtime, runtime);
    }

    // Rating
    searchMatcher = ImdbInfoPattern.RATING.getPattern().matcher(moviePage);
    if (searchMatcher.find()) {
      String rating = searchMatcher.group(1);
      fields.put(MovieProperty.rating, rating);
    }

    // Votes
    searchMatcher = ImdbInfoPattern.VOTES.getPattern().matcher(moviePage);
    if (searchMatcher.find()) {
      String votes = searchMatcher.group(1).replaceAll("[., ]", "");
      fields.put(MovieProperty.votes, votes);
    }

    // // TagLine
    // searchMatcher = ImdbPattern.TAGLINE.getPattern().matcher(moviePage);
    // if (searchMatcher.find()) {
    // String tagline = searchMatcher.group(1);
    // movieInfo.setTagline(StringUtils.unEscapeXML(tagline, CHARSET));
    // }

    // Plot
    searchMatcher = ImdbInfoPattern.PLOT.getPattern().matcher(moviePage);
    if (searchMatcher.find()) {
      String plot = searchMatcher.group(1);
      fields.put(MovieProperty.overview, StringUtils.unEscapeXML(plot, CHARSET));
    }

    // // Studio
    // searchMatcher = ImdbPattern.STUDIO.getPattern().matcher(moviePage);
    // while (searchMatcher.find()) {
    // String studio = searchMatcher.group(1);
    // studio = StringUtils.unEscapeXML(studio, CHARSET);
    // movieInfo.addStudio(studio);
    // }

    // // Top 250
    // searchMatcher = ImdbPattern.TOP250.getPattern().matcher(moviePage);
    // if (searchMatcher.find()) {
    // String top250 = searchMatcher.group(1);
    // if (top250 != null && NumberUtils.isDigit(top250)) {
    // movieInfo.setTop250(top250);
    // }
    // }

    // Thumb
    searchMatcher = ImdbInfoPattern.THUMB.getPattern().matcher(moviePage);
    if (searchMatcher.find()) {
      String imdbThumb = searchMatcher.group(1);
      fields.put(MovieProperty.posterPath, imdbThumb);
    }

    List<IdInfo> ids = new ArrayList<IdInfo>();// TODO check if id in page is the same as movie id
    ids.add(movie.getId());

    List<String> genres = new ArrayList<String>();
    // Genres
    searchMatcher = ImdbInfoPattern.GENRE.getPattern().matcher(moviePage);
    if (searchMatcher.find()) {
      String[] foundGenres = searchMatcher.group(1).split("\\|");
      for (int i = 0; i < foundGenres.length; i++) {
        String genre;
        if (Settings.getInstance().getSearchMovieScrapperLang().equals(Locale.ENGLISH)) {
          genre = foundGenres[i].substring(foundGenres[i].indexOf(">") + 1, foundGenres[i].indexOf("</a>")).trim();
          if (genre.equals("See more")) {
            genre = "";
          }
        } else {
          genre = foundGenres[i].trim();
        }

        if (!genre.equals("")) {
          genres.add(StringUtils.unEscapeXML(genre, CHARSET));
        }
      }
    }

    List<Locale> countries = new ArrayList<Locale>();
    // // Countries
    // searchMatcher = ImdbPattern.COUNTRY.getPattern().matcher(moviePage);
    // if (searchMatcher.find()) {
    // String[] countries = searchMatcher.group(1).split("\\|");
    // for (int i = 0; i < countries.length; i++) {
    // String country;
    // switch (config.movieScrapperLang) {
    // case en:
    // country = countries[i].substring(countries[i].indexOf(">") + 1,
    // countries[i].indexOf("</a>")).trim();
    // break;
    // default:
    // country = countries[i].trim();
    // break;
    // }
    // if (!country.equals("")) {
    // country = StringUtils.unEscapeXML(country, CHARSET);
    // movieInfo.addCountry(country);
    // }
    // }
    // }

    List<String> studios = new ArrayList<String>();

    MovieInfo movieInfo = new MovieInfo(fields, ids, genres, countries, studios);
    return movieInfo;
  }

  protected int findImdbId(String source) {
    Matcher matcher = Pattern.compile("tt(\\d+{7})").matcher(source);

    if (matcher.find()) {
      return Integer.parseInt(matcher.group(1));
    }

    // not found
    // throw new IllegalArgumentException(String.format("Cannot find imdb id: %s", source));
    return 0;
  }

  protected int findCastImdbId(String source) {
    Matcher matcher = Pattern.compile("nm(\\d+{7})").matcher(source);

    if (matcher.find()) {
      return Integer.parseInt(matcher.group(1));
    }

    // not found
    // throw new IllegalArgumentException(String.format("Cannot find cast imdb id: %s", source));
    return 0;
  }

  @Override
  protected List<ImageInfo> getScrapperImages(Movie movie, Locale language) throws Exception {
    URL searchUrl = new URL("http", getHost(language), String.format("/title/%s/mediaindex", movie.getMediaId()));
    String imagesPage = URIRequest.getDocumentContent(searchUrl.toURI());

    List<ImageInfo> images = new ArrayList<ImageInfo>();

    Matcher searchMatcher = Pattern.compile("src=\"(http://ia.media-imdb.com/images/.*)\"").matcher(imagesPage);
    while (searchMatcher.find()) {
      String url = searchMatcher.group(1);
      Map<ImageProperty, String> imageFields = new EnumMap<ImageProperty, String>(ImageProperty.class);
      imageFields.put(ImageProperty.url, url.replaceAll("CR[\\d,]+_SS\\d+", "SY214_SX314"));
      images.add(new ImageInfo(imageFields, ImageCategoryProperty.unknown));
    }
    return images;
  }

  @Override
  protected List<CastingInfo> fetchCastingInfo(Movie movie, Locale language) throws Exception {
    URL searchUrl = new URL("http", getHost(language), String.format("/title/%s/fullcredits", movie.getMediaId()));
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI());

    List<CastingInfo> casting = new ArrayList<CastingInfo>();

    List<Node> castNodes = XPathUtils.selectNodes("//TABLE[@class='cast']//TR", dom);
    for (Node node : castNodes) {
      Node actorNode = XPathUtils.selectNode("TD[@class='nm']", node);
      if (actorNode != null) {
        Node pictureNode = XPathUtils.selectNode("TD[@class='hs']//IMG", node);
        Node characterNode = XPathUtils.selectNode("TD[@class='char']", node);

        Map<PersonProperty, String> personFields = fetchPersonIdAndName(actorNode);
        if (personFields != null) {
          String picture = (pictureNode != null) ? createImgPath(XPathUtils.getAttribute("src", pictureNode)) : "";
          if (picture.contains("no_photo")) {
            picture = "";
          }
          personFields.put(PersonProperty.job, CastingInfo.ACTOR);
          personFields.put(PersonProperty.character, (characterNode != null) ? StringUtils.unEscapeXML(characterNode.getTextContent().trim(), CHARSET) : "");
          personFields.put(PersonProperty.picturePath, picture);
          casting.add(new CastingInfo(personFields));
        }
      }
    }

    List<Node> directorNodes = XPathUtils.selectNodes("//TABLE[.//A[@name='directors']]//TR", dom);
    for (Node node : directorNodes) {
      Map<PersonProperty, String> personFields = fetchPersonIdAndName(node);
      if (personFields != null) {
        personFields.put(PersonProperty.job, CastingInfo.DIRECTOR);
        casting.add(new CastingInfo(personFields));
      }
    }

    List<Node> writerNodes = XPathUtils.selectNodes("//TABLE[.//A[@name='writers']]//TR", dom);
    for (Node node : writerNodes) {
      Map<PersonProperty, String> personFields = fetchPersonIdAndName(node);
      if (personFields != null) {
        personFields.put(PersonProperty.job, CastingInfo.WRITER);
        casting.add(new CastingInfo(personFields));
      }
    }

    return casting;
  }

  private Map<PersonProperty, String> fetchPersonIdAndName(Node node) {
    if (node != null) {
      Node link = XPathUtils.selectNode(".//A", node);
      if (link != null) {
        String pname = StringUtils.unEscapeXML(link.getTextContent().trim(), CHARSET);
        if (pname.length() > 1) {
          int imdbId = findCastImdbId(XPathUtils.getAttribute("href", link));
          if (imdbId != 0) {
            Map<PersonProperty, String> personFields = new EnumMap<PersonProperty, String>(PersonProperty.class);
            personFields.put(PersonProperty.id, Integer.toString(imdbId));
            personFields.put(PersonProperty.name, pname);

            return personFields;
          }
        }
      }
    }
    return null;
  }
}
