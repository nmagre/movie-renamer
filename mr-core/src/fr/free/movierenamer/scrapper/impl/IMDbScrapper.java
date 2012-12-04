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

import java.net.MalformedURLException;
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
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.ImageInfo.ImageCategoryProperty;
import fr.free.movierenamer.info.ImageInfo.ImageProperty;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.MovieInfo.MovieProperty;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.searchinfo.SearchResult;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.ClassUtils;
import fr.free.movierenamer.utils.NumberUtils;
import fr.free.movierenamer.utils.StringUtils;
import fr.free.movierenamer.utils.WebRequest;
import fr.free.movierenamer.utils.XPathUtils;

/**
 * Class IMDbScrapper : search movie on IMDB
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class IMDbScrapper extends MovieScrapper {

  private static final String defaultHost = "www.imdb.com";
  private static final String name = "IMDb";
  private static final String CHARSET = "ISO-8859-1";
  private String host;

  public IMDbScrapper() {
    super(Locale.ENGLISH);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  protected String getHost() {
    return getHost(getLocale());
  }

  @Override
  public boolean hasLocaleSupport() {
    return true;
  }

  protected final String getHost(Locale locale) {
    if (host == null) {
      host = defaultHost;
      if (locale != null && !locale.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
        try {
          URL url = new URL("http", defaultHost.replace("com", locale.getLanguage()), "");
          int responseCode = WebRequest.getResponseCode(url);
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

  public enum ImdbSearchPattern {

    SEARCHIMDBPATTERN("<tr class=.findResult\\b[^>]*>.*?tt(\\d+).*?\\w\\w_\\d+. >(.*?)</td> </tr>"),
    MOVIEIMDBPATTERN("(http://.*?\\.(?:png|jpg)).*?<a[^>]+>(.*?)</a>(.*)"),
    IMDBMOVIETITLE("<meta property=.og:title. content=.(.*?) \\(.*?(\\d\\d\\d\\d).*\\)?."),
    IMDBMOVIEID("tt(\\d+{7})"),
    IMDBMOVIETHUMB("<meta property=.og:image. content=.(http://.*.jpg).");
    private final Pattern pattern;

    private ImdbSearchPattern(String pattern) {
      this.pattern = Pattern.compile(pattern);
    }

    public Pattern getPattern() {
      return pattern;
    }

    public String getPatternString() {
      return pattern.toString();
    }
  }

  private enum DoNotKeep {
    TV_Series,
    Video_Game,
    TV_Episode,
    TV_Mini_DASH_Series;
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
    TAGLINE("<div class=\"info-content\">\n(.*)<a class=\".*\" href=\"/title/tt\\d+/taglines\""), // Only on .com  site (english)
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

  @Override
  protected List<Movie> searchMedia(String query, Locale locale) throws Exception {
    // http://www.imdb.com/find?s=tt&ref_=fn_tt&q=
    URL searchUrl = new URL("http", getHost(locale), "/find?s=tt&ref_=fn_tt&q=" + WebRequest.encode(query));
    boolean mode = true;

    if (mode) {
      Document dom = WebRequest.getHtmlDocument(searchUrl.toURI());

      // select movie links followed by year in parenthesis
      List<Node> nodes = XPathUtils.selectNodes("//TABLE//A[substring-after(substring-before(following::text(),')'),'(')]", dom);
      List<Movie> results = new ArrayList<Movie>(nodes.size());

      for (Node node : nodes) {
        try {
          String name = node.getTextContent().trim();
          if (name.startsWith("\"")) {
            continue;
          }
          String year = node.getNextSibling().getTextContent().replaceAll("[\\p{Punct}\\p{Space}]+", ""); // remove non-number characters
          String href = XPathUtils.getAttribute("href", node);
          int imdbid = findImdbId(href);

          URL thumb;
          try {
            String imgPath = XPathUtils.getAttribute("src", node.getParentNode().getPreviousSibling().getPreviousSibling().getChildNodes().item(1).getFirstChild());
            thumb = new URL(imgPath.replaceAll("S[XY]\\d+_S[XY]\\d+", "SY70_SX100"));
          } catch (Exception ex) {
            thumb = null;
          }

          results.add(new Movie(imdbid, name, thumb, Integer.parseInt(year), imdbid));
        } catch (Exception e) {
          // ignore
        }
      }

      // we might have been redirected to the movie page
      if (results.isEmpty()) {// Maybe check if it's a URL forwarding is better ^^
        try {
          int imdbid = findImdbId(XPathUtils.selectString("//LINK[@rel='canonical']/@href", dom));
          MovieInfo info = fetchMediaInfo(new Movie(imdbid, null, null, -1, imdbid), locale);
          URL thumb;
          try {
            String imgPath = info.getPosterPath().toURL().toExternalForm();
            thumb = new URL(imgPath.replaceAll("S[XY]\\d+_S[XY]\\d+", "SY70_SX100"));
          } catch (Exception ex) {
            thumb = null;
          }
          Movie movie = new Movie(imdbid, info.getTitle(), thumb, info.getYear(), imdbid);// FIXME !!!! getMovieDescriptor(imdbid, locale);
          if (movie != null) {
            results.add(movie);
          }
        } catch (Exception e) {
          // ignore, can't find movie
        }
      }

      return results;
    } else {
      // FIXME Imdb search page changed, now it's in UTF-8 and all regex are deprecated
      String moviePage = WebRequest.getDocumentContent(searchUrl.toURI());

      List<Movie> results = new ArrayList<Movie>();
      results.addAll(findSearchMovies(moviePage, ImdbSearchPattern.SEARCHIMDBPATTERN.getPattern(), 30));// FIxME get limit from settings

      // are we redirected to the movie page ?
      if (results.isEmpty()) {
        results.add(getSearchMovie(moviePage));
      }

      return results;
    }
  }

  /**
   * Get movies title by result type in Imdb search page
   *
   * @param htmlSearchRes Imdb search page
   * @param searchPattern Pattern of result to retreive
   * @param limit Limitation of returned result
   * @return Array of ImdbSearchResult
   */
  private List<Movie> findSearchMovies(String htmlSearchRes, Pattern searchPattern, int limit) {
    List<Movie> found = new ArrayList<Movie>();
    Matcher searchResult = searchPattern.matcher(htmlSearchRes);

    try {
      int count = 0;
      while (searchResult.find()) {
        htmlSearchRes = searchResult.group(2);
        int imdbID = Integer.parseInt(searchResult.group(1));
        Matcher movieMatcher = ImdbSearchPattern.MOVIEIMDBPATTERN.getPattern().matcher(htmlSearchRes);

        if (movieMatcher.find()) {
          URL thumb;
          String title;
          int year;

          try {
            thumb = new URL(movieMatcher.group(1).replaceAll("S[XY]\\d+_S[XY]\\d+", "SY70_SX100"));
          } catch (MalformedURLException e) {
            thumb = null;
          }

          title = movieMatcher.group(2).trim();
          year = Integer.parseInt(movieMatcher.group(3).replaceAll(".*\\((\\d\\d\\d\\d)\\).*", "$1"));

          boolean valid = true;
          for (int i = 0; i < DoNotKeep.values().length; i++) {
            if (title.contains(DoNotKeep.values()[i].name().replace("_DASH_", "-").replace("_", " "))) {
              valid = false;
              break;
            }
          }

          if (valid) {
            found.add(new Movie(imdbID, title, thumb, year, imdbID));
            count++;
          }
          if (limit != -1 && limit <= count) {
            break;
          }
        }
      }
    } catch (NullPointerException ex) {
      Settings.LOGGER.log(Level.SEVERE, ClassUtils.getStackTrace("NullPointerException", ex.getStackTrace()));
    }
    return found;
  }

  /**
   * Get movie title in imdb movie page
   *
   * @param moviePage Imdb movie page
   * @return The result
   */
  private Movie getSearchMovie(String moviePage) {
    Matcher matcher = ImdbSearchPattern.IMDBMOVIETITLE.getPattern().matcher(moviePage);

    try {
      if (matcher.find()) {
        String movieName = StringUtils.unEscapeXML(matcher.group(1), CHARSET);
        int year = -1;
        if (matcher.group(2) != null) {
          year = Integer.parseInt(matcher.group(2));
        }

        int imdbid = -1;
        matcher = ImdbSearchPattern.IMDBMOVIEID.getPattern().matcher(moviePage);
        if (matcher.find()) {
          imdbid = findImdbId(matcher.group());
        }

        URL thumb = null;
        matcher = ImdbSearchPattern.IMDBMOVIETHUMB.getPattern().matcher(moviePage);
        if (matcher.find()) {
          try {
            thumb = new URL(matcher.group(1));
          } catch (MalformedURLException e) {
            thumb = null;
          }
        }
        return new Movie(imdbid, movieName, thumb, year, imdbid);// , EXACT, ));
      } else {
        Settings.LOGGER.log(Level.SEVERE, "imdb page unrecognized");
      }
    } catch (NullPointerException ex) {
      Settings.LOGGER.log(Level.SEVERE, ClassUtils.getStackTrace("NullPointerException", ex.getStackTrace()));
    }
    return null;
  }

  @Override
  protected MovieInfo fetchMediaInfo(Movie movie, Locale locale) throws Exception {
    // http://www.imdb.com/title/
    // or http://www.deanclatworthy.com/imdb/
    // or new URL("http", "www.imdb.com",
    // String.format("/title/tt%07d/releaseinfo", movie.getMovieId())
    // new URL("http", "www.imdb.com", String.format("/title/tt%07d/combined",
    // movie.getMovieId())
    URL searchUrl = new URL("http", getHost(locale), String.format("/title/tt%07d/combined", movie.getMediaId()));
    String moviePage = WebRequest.getDocumentContent(searchUrl.toURI());

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
          } else {
            year = -1;
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

    List<String> genres = new ArrayList<String>();
    // Genres
    searchMatcher = ImdbInfoPattern.GENRE.getPattern().matcher(moviePage);
    if (searchMatcher.find()) {
      String[] foundGenres = searchMatcher.group(1).split("\\|");
      for (int i = 0; i < foundGenres.length; i++) {
        String genre;
        if (Settings.getInstance().getSearchScrapperLang().getLanguage().equals(Locale.ENGLISH)) {
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
    // case ENGLISH:
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

    MovieInfo movieInfo = new MovieInfo(fields, genres, countries);
    return movieInfo;
  }

  protected int findImdbId(String source) {
    Matcher matcher = ImdbSearchPattern.IMDBMOVIEID.pattern.matcher(source);

    if (matcher.find()) {
      return Integer.parseInt(matcher.group(1));
    }

    // not found
    throw new IllegalArgumentException(String.format("Cannot find imdb id: %s", source));
  }

  @Override
  protected List<ImageInfo> fetchImagesInfo(Movie movie, Locale locale) throws Exception {
    URL searchUrl = new URL("http", getHost(locale), String.format("/title/tt%07d/mediaindex", movie.getMediaId()));
    String imagesPage = WebRequest.getDocumentContent(searchUrl.toURI());

    List<ImageInfo> images = new ArrayList<ImageInfo>();

    Matcher searchMatcher = ImdbInfoPattern.THUMB.getPattern().matcher(imagesPage);
    while (searchMatcher.find()) {
      String url = searchMatcher.group(1);
      Map<ImageProperty, String> imageFields = new EnumMap<ImageProperty, String>(ImageProperty.class);
      imageFields.put(ImageProperty.url, url.replaceAll("CR[\\d,]+_SS\\d+", "SY214_SX314"));
      images.add(new ImageInfo(imageFields, ImageCategoryProperty.unknown));
    }
    return images;
  }

  @Override
  protected List<CastingInfo> fetchCastingInfo(Movie movie, Locale locale) throws Exception {
    URL searchUrl = new URL("http", getHost(locale), String.format("/title/tt%07d/combined", movie.getMediaId()));
    String moviePage = WebRequest.getDocumentContent(searchUrl.toURI());

    List<CastingInfo> casting = new ArrayList<CastingInfo>();
    // Directors
    Matcher searchMatcher = ImdbInfoPattern.DIRECTOR.getPattern().matcher(moviePage);
    while (searchMatcher.find()) {
      String director = searchMatcher.group(2);
      String imdbId = searchMatcher.group(1);
      Map<PersonProperty, String> personFields = new EnumMap<PersonProperty, String>(PersonProperty.class);
      personFields.put(PersonProperty.id, imdbId);
      personFields.put(PersonProperty.name, StringUtils.unEscapeXML(director, CHARSET));
      personFields.put(PersonProperty.job, CastingInfo.DIRECTOR);
      casting.add(new CastingInfo(personFields));
    }

    // Writers
    searchMatcher = ImdbInfoPattern.WRITER.getPattern().matcher(moviePage);
    while (searchMatcher.find()) {
      String writer = searchMatcher.group(2);
      String imdbId = searchMatcher.group(1);
      Map<PersonProperty, String> personFields = new EnumMap<PersonProperty, String>(PersonProperty.class);
      personFields.put(PersonProperty.id, imdbId);
      personFields.put(PersonProperty.name, StringUtils.unEscapeXML(writer, CHARSET));
      personFields.put(PersonProperty.job, CastingInfo.WRITER);
      casting.add(new CastingInfo(personFields));
    }

    // Actors
    // TODO code not rewrite, i think we can do something much better ^^
    searchMatcher = ImdbInfoPattern.CAST.getPattern().matcher(moviePage);
    if (searchMatcher.find()) {
      String[] actors = searchMatcher.group().split("</tr>");
      for (int i = 0; i < actors.length; i++) {
        Matcher matcher2 = ImdbInfoPattern.ACTOR.getPattern().matcher(actors[i]);
        boolean thumb = !actors[i].contains("no_photo");
        if (matcher2.find()) {
          String thumbactor = "";
          if (thumb) {
            String actorThumb = matcher2.group().substring(matcher2.group().indexOf("src=") + 5, matcher2.group().indexOf("width") - 2);
            thumbactor = actorThumb.replaceAll("S[XY]\\d+_S[XY]\\d+", "SY214_SX314");
          }

          String name = matcher2.group().substring(matcher2.group().indexOf("onclick="), matcher2.group().indexOf("</a></td><td"));
          name = name.substring(name.indexOf(">") + 1);
          if (thumbactor.equals("http://i.media-imdb.com/images/b.gif")) {
            thumbactor = "";
          }

          String imdbId = "";
          if (matcher2.group().contains("link=/name/nm")) {
            int pos = matcher2.group().indexOf("link=/name/nm") + 11;
            imdbId = matcher2.group().substring(pos + 2, pos + 9);
          }

          String role = matcher2.group().substring(matcher2.group().indexOf("class=\"char\""));
          role = role.substring(role.indexOf(">") + 1, role.indexOf("</td>"));
          if (role.contains("href=")) {
            role = role.substring(role.indexOf(">") + 1);
            role = role.substring(0, role.indexOf("<"));
          }

          Map<PersonProperty, String> personFields = new EnumMap<PersonProperty, String>(PersonProperty.class);
          personFields.put(PersonProperty.id, imdbId);
          personFields.put(PersonProperty.name, StringUtils.unEscapeXML(name, CHARSET));
          personFields.put(PersonProperty.job, CastingInfo.ACTOR);
          personFields.put(PersonProperty.character, role);
          personFields.put(PersonProperty.picturePath, thumbactor);
          casting.add(new CastingInfo(personFields));
        }
      }
    }

    return casting;
  }
}
