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
package fr.free.movierenamer.parser;

import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.utils.SearchResult;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.SAXException;

/**
 * Class ImdbSearch
 *
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
public class ImdbSearch extends MrParser<List<SearchResult>> {

  private List<SearchResult> results;
  private static final Pattern IMDBURL = Pattern.compile("http://www.imdb.com/title/tt\\d+/");
  private static final String CHARSET = "ISO-8859-1";
  private final URL realUrl;

  public enum ImdbPattern {

    POPULARPATTERN(".*?Popular Titles.*?Displaying (\\d+)(.*?)</table>"),
    EXACTPATTERN("Titles \\(Exact Matches.*?Displaying (\\d+)(.*?)</table>"),
    PARTIALPATTERN("Titles \\(Partial.*?Displaying (\\d+)(.*?)</table>"),
    APPROXIMATEPATTERN("Titles \\(Approx.*?Displaying (\\d+)(.*?)</table>"),
    MOVIEIMDBPATTERN("(?:<img src=.(http://ia.media-imdb.com/images.*?\\.jpg).*?)?>\\d+.<\\/td>.*?a href=.\\/title\\/(tt\\d+)\\/.[^>]*>([^<]*).*?\\((\\d+)?.*?\\).?(\\(.*?\\) )?"),
    IMDBMOVIETITLE("<meta property=.og:title. content=.(.*?) \\(.*?(\\d\\d\\d\\d).*\\)?."),
    IMDBMOVIEID("tt\\d+{7}"),
    IMDBMOVIETHUMB("<meta property=.og:image. content=.(http://.*.jpg).");
    private Pattern pattern;

    private ImdbPattern(String pattern) {
      this.pattern = Pattern.compile(pattern);
    }

    public Pattern getPattern() {
      return pattern;
    }

    public String getPatternString() {
      return pattern.toString();
    }
  }
  /**
   * The exception to bypass parsing file ;)
   */
  private final NOSAXException nsEx = new NOSAXException();

  public ImdbSearch(URL url) {
    super();
    realUrl = url;
  }

  @Override
  public void startDocument() throws SAXException {
    results = new ArrayList<SearchResult>();

    String htmlSearchRes = getContent(CHARSET);

    if (htmlSearchRes != null && !htmlSearchRes.contains("<b>No Matches.</b>")) {
      Matcher moviePageMatcher = IMDBURL.matcher(realUrl.toString());
      boolean searchPage = !moviePageMatcher.find();

      if (searchPage) {
        int limit = Settings.nbResultList[config.nbResult];

        Settings.LOGGER.log(Level.INFO, "Imdb Search page");
        results.addAll(findMovies(htmlSearchRes, ImdbPattern.POPULARPATTERN.getPattern(), limit, SearchResult.SearchResultType.POPULAR));
        results.addAll(findMovies(htmlSearchRes, ImdbPattern.EXACTPATTERN.getPattern(), limit, SearchResult.SearchResultType.EXACT));
        results.addAll(findMovies(htmlSearchRes, ImdbPattern.PARTIALPATTERN.getPattern(), limit, SearchResult.SearchResultType.PARTIAL));
        if (results.isEmpty() || config.displayApproximateResult) {
          results.addAll(findMovies(htmlSearchRes, ImdbPattern.APPROXIMATEPATTERN.getPattern(), limit, SearchResult.SearchResultType.APPROXIMATE));
        }
      } else {
        Settings.LOGGER.log(Level.INFO, "Imdb Movie page");
        results.add(getMovie(htmlSearchRes));
      }
    }
    throw nsEx;
  }

  @Override
  public List<SearchResult> getObject() {
    return results;
  }

  /**
   * Get movies title by result type in Imdb search page
   *
   * @param htmlSearchRes Imdb search page
   * @param searchPattern Pattern of result to retreive
   * @param movieFilenameLimit Limitation of returned result
   * @param french Is imdb french page
   * @param type Type of result search
   * @return Array of ImdbSearchResult
   */
  private List<SearchResult> findMovies(String htmlSearchRes, Pattern searchPattern, int limit, SearchResult.SearchResultType type) throws ParserBugException {
    List<SearchResult> found = new ArrayList<SearchResult>();
    Matcher searchResult = searchPattern.matcher(htmlSearchRes);

    try {
      if (searchResult.find()) {
        int count = 1;
        searchResult = ImdbPattern.MOVIEIMDBPATTERN.getPattern().matcher(searchResult.group(2));
        while (searchResult.find()) {
          if (searchResult.group(5) != null) {// We do not keep results that are not a movie
            if (!searchResult.group(5).equals("TV")) {
              continue;
            }
          }

          String movieTitle = searchResult.group(3);
          if (movieTitle != null) {
            String thumb = null;
            String year = "";
            movieTitle = Utils.unEscapeXML(movieTitle, CHARSET);

            if (searchResult.group(1) != null) {// Thumb found
              thumb = searchResult.group(1).replaceAll("SY\\d{2}_SX\\d{2}", "SY70_SX100");
            }

            if (searchResult.group(4) != null) {// Year found
              year = searchResult.group(4);
            }

            found.add(new SearchResult(movieTitle, new MediaID(searchResult.group(2), MediaID.MediaIdType.IMDBID), type, year, thumb));
          }
          count++;
          if ((limit > -1 && count > limit)) {
            break;
          }
        }
      }
    } catch (NullPointerException ex) {
      Settings.LOGGER.log(Level.SEVERE, Utils.getStackTrace("NullPointerException", ex.getStackTrace()));
      throw new ParserBugException("ImdbSearch parser bug : findMovies");
    }
    return found;
  }

  /**
   * Get movie title in imdb movie page
   *
   * @param moviePage Imdb movie page
   * @return The result
   */
  private SearchResult getMovie(String moviePage) throws ParserBugException {
    Matcher matcher = ImdbPattern.IMDBMOVIETITLE.getPattern().matcher(moviePage);

    try {
      if (matcher.find()) {
        String movieName = Utils.unEscapeXML(matcher.group(1), CHARSET);
        String year = "";
        if (matcher.group(2) != null) {
          year = matcher.group(2);
        }

        String id = null;
        matcher = ImdbPattern.IMDBMOVIEID.getPattern().matcher(moviePage);
        if (matcher.find()) {
          id = matcher.group();
        }

        String thumb = null;
        matcher = ImdbPattern.IMDBMOVIETHUMB.getPattern().matcher(moviePage);
        if (matcher.find()) {
          thumb = matcher.group(1);
        }
        return new SearchResult(movieName, new MediaID(id, MediaID.MediaIdType.IMDBID), SearchResult.SearchResultType.EXACT, year, thumb);
      } else {
        Settings.LOGGER.log(Level.SEVERE, "imdb page unrecognized");
      }
    } catch (NullPointerException ex) {
      Settings.LOGGER.log(Level.SEVERE, Utils.getStackTrace("NullPointerException", ex.getStackTrace()));
      throw new ParserBugException("ImdbSearch parser bug : findMovies");
    }
    return null;
  }
}
