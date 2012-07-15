/*
 * movie-renamer
 * Copyright (C) 2012 QUÉMÉNEUR Simon
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
package fr.free.movierenamer.parser.xml;

import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.utils.SearchResult;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.net.URL;
import java.util.ArrayList;
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
public class ImdbSearch extends MrParser<ArrayList<SearchResult>> {

  private ArrayList<SearchResult> results;
  private final ImdbLanguage ilang = config.movieScrapperFR ? ImdbLanguage.FRENCH : ImdbLanguage.ENGLISH;

  public enum ImdbLanguage {// TODO , a réfléchir, imdb.fr ne sert à rien pour la recherche revient au même que le .com

    ENGLISH,
    FRENCH;
  }

  public enum ImdbPattern {

    MOVIEIMDBPATTERN(">\\d+.<\\/td>.*?a href=.\\/title\\/tt\\d+\\/.[^>]*>([^<]*).*?\\((\\d+).*?\\).?(\\(.*?\\) )?"),
    MOVIENAMEPATTERN(";\">"),
    IMDBIDPATTERN("tt\\d+"),
    MOVIETHUMB("/title/IMDBID/';.><img src=.http://ia.media-imdb.com/images/.*?. width=.\\d+. height=.\\d+. border=.\\d.>"),
    POPULARPATTERN(".*?Popular Titles.*?Displaying (\\d+).*", ".*?Titres Populaires.*?Affichant (\\d+)(.*)"),
    EXACTPATTERN("Titles \\(Exact Matches.*?Displaying (\\d+)(.*)", "Titres \\(R&#xE9;sultats Exacts.*?Affichant (\\d+)(.*)"),
    PARTIALPATTERN("Titles \\(Partial.*?Displaying (\\d+)(.*)", "Titres \\(R&#xE9;sultats Partiels.*?Affichant (\\d+)(.*)"),
    APPROXIMATEPATTERN("Titles \\(Approx.*?Displaying (\\d+)(.*)", "Titres \\(R&#xE9;sultats Approximatif.*?Affichant (\\d+)(.*)"),
    IMDBMOVIETITLE("<meta property=.og:title. content=?(.*?) \\(.*\\d\\d\\d\\d.*\\)"),
    IMDBMOVIETHUMB("/tt\\d+\".*><img src=\"http://.*.jpg\"\n", "title=\".*\" src=.http://ia.media-imdb.com/images/.*>");
    private Pattern[] pattern;

    private ImdbPattern(String... patterns) {
      pattern = new Pattern[patterns.length];
      for (int i = 0; i < patterns.length; i++) {
        pattern[i] = Pattern.compile(patterns[i]);
      }
    }

    public Pattern getPattern(ImdbLanguage lang) {
      if (pattern.length == 1) {
        return pattern[0];
      }
      return pattern[lang.ordinal()];
    }

    public String getPatternString(ImdbLanguage lang) {
      if (pattern.length == 1) {
        return pattern[0].toString();
      }
      return pattern[lang.ordinal()].toString();
    }
  }
  /**
   * The exception to bypass parsing file ;)
   */
  private final NOSAXException nsEx = new NOSAXException();
  private final URL realUrl;

  public ImdbSearch(URL url) {
    super();
    realUrl = url;
  }

  @Override
  public void startDocument() throws SAXException {
    results = new ArrayList<SearchResult>();

    String htmlSearchRes = getContent("ISO-8859-1");

    if (htmlSearchRes != null && !htmlSearchRes.contains("<b>No Matches.</b>")) {
      Pattern pattern = Pattern.compile("http://www.imdb.(com|fr)/title/tt\\d+/");
      Matcher moviePageMatcher = pattern.matcher(realUrl.toString());
      boolean searchPage = !moviePageMatcher.find();

      if (searchPage) {
        int limit = Settings.nbResultList[config.nbResult];

        Settings.LOGGER.log(Level.INFO, "Imdb Search page");
        results.addAll(findMovies(htmlSearchRes, ImdbPattern.POPULARPATTERN.getPattern(ilang), limit, SearchResult.SearchResultType.POPULAR));// Popular title
        results.addAll(findMovies(htmlSearchRes, ImdbPattern.EXACTPATTERN.getPattern(ilang), limit, SearchResult.SearchResultType.EXACT));// Exact title
        results.addAll(findMovies(htmlSearchRes, ImdbPattern.PARTIALPATTERN.getPattern(ilang), limit, SearchResult.SearchResultType.PARTIAL));// Partial title
        if (results.isEmpty() || config.displayApproximateResult) {
          results.addAll(findMovies(htmlSearchRes, ImdbPattern.APPROXIMATEPATTERN.getPattern(ilang), limit, SearchResult.SearchResultType.APPROXIMATE));
        }
      } else {
        Settings.LOGGER.log(Level.INFO, "Imdb Movie page");
        results.add(getMovie(htmlSearchRes));
      }
    }
    throw nsEx;
  }

  @Override
  public ArrayList<SearchResult> getObject() {
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
  private ArrayList<SearchResult> findMovies(String htmlSearchRes, Pattern searchPattern, int limit, SearchResult.SearchResultType type) throws IndexOutOfBoundsException {
    ArrayList<SearchResult> found = new ArrayList<SearchResult>();
    Matcher titleMatcher = searchPattern.matcher(htmlSearchRes);
    Matcher movieImdbMatcher, movieNameMatcher, imdbIDMatcher, thumbMatcher;
    String movieName;

    try {
      if (titleMatcher.find()) {
        int count = 1;
        String grp = titleMatcher.group();
        String[] grps = grp.split("</table>");
        for (int i = 0; i < grps.length; i++) {
          titleMatcher = searchPattern.matcher(grps[i]);
          if (titleMatcher.find()) {
            movieImdbMatcher = ImdbPattern.MOVIEIMDBPATTERN.getPattern(ilang).matcher(grps[i]);

            while (movieImdbMatcher.find()) {
              if (movieImdbMatcher.group(3) != null) {
                if (!movieImdbMatcher.group(3).equals("TV")) {
                  continue;
                }
              }

              movieNameMatcher = ImdbPattern.MOVIENAMEPATTERN.getPattern(ilang).matcher(movieImdbMatcher.group());

              if (movieNameMatcher.find()) {
                movieName = movieImdbMatcher.group().substring(movieNameMatcher.end(), movieImdbMatcher.group().length()).replaceAll("<\\/a>", Utils.EMPTY);
                movieName = Utils.unEscapeXML(movieName, "ISO-8859-1");

                // Fix unknown date in movie title
                if (movieName.contains("(????)")) {
                  movieName = movieName.substring(0, movieName.indexOf("(????)") + 6);
                }

                imdbIDMatcher = ImdbPattern.IMDBIDPATTERN.getPattern(ilang).matcher(movieImdbMatcher.group());

                if (imdbIDMatcher.find()) {// movie title + imdbID found
                  String thumb = ImdbPattern.MOVIETHUMB.getPatternString(ilang).replace("IMDBID", imdbIDMatcher.group());
                  Pattern pattern = Pattern.compile(thumb);
                  thumbMatcher = pattern.matcher(htmlSearchRes);
                  thumb = null;
                  if (thumbMatcher.find()) {
                    thumb = thumbMatcher.group().substring(thumbMatcher.group().indexOf("img src=") + 9, thumbMatcher.group().indexOf(".jpg") + 4);
                  }
                  found.add(new SearchResult(movieName, new MediaID(imdbIDMatcher.group(), MediaID.IMDBID), type, thumb));
                }
                
                count++;
                if ((limit > -1 && count > limit)) {
                  break;
                }
              }
            }
            break;
          }
        }
      }
    } catch (IllegalArgumentException ex) {
      Settings.LOGGER.log(Level.SEVERE, Utils.getStackTrace("IllegalArgumentException", ex.getStackTrace()));
    }
    return found;
  }

  /**
   * Get movie title in imdb movie page
   *
   * @param moviePage Imdb movie page
   * @return The result
   */
  private SearchResult getMovie(String moviePage) throws IndexOutOfBoundsException {
    Matcher titleMatcher = ImdbPattern.IMDBMOVIETITLE.getPattern(ilang).matcher(moviePage);

    try {
      if (titleMatcher.find()) {
        // Extract the movie name
        String movieName = titleMatcher.group().substring(titleMatcher.group().indexOf("content=") + 9);
        movieName = Utils.unEscapeXML(movieName, "ISO-8859-1");
        // Extract the movie Id
        String imdbId = moviePage.substring(moviePage.indexOf("/tt") + 1, moviePage.indexOf("/tt") + 10);
        Matcher thumbMatcher = ImdbPattern.IMDBMOVIETHUMB.getPattern(ilang).matcher(moviePage);
        // Extract thumb
        String thumb = null;
        if (thumbMatcher.find()) {
          String thumbnail = thumbMatcher.group();
          if (thumbnail.contains("img src=")) {
            thumb = thumbnail.substring(thumbnail.indexOf("img src=") + 9, thumbnail.indexOf(".jpg") + 4);
          } else {
            thumb = thumbnail.substring(thumbnail.lastIndexOf("src=") + 5, thumbnail.lastIndexOf("\""));
          }
        }
        return new SearchResult(movieName, new MediaID(imdbId, MediaID.IMDBID), SearchResult.SearchResultType.EXACT, thumb);

      } else {
        Settings.LOGGER.log(Level.SEVERE, "imdb page unrecognized");
      }
    } catch (IndexOutOfBoundsException e) {
      Settings.LOGGER.log(Level.SEVERE, e.getMessage());
      throw new IndexOutOfBoundsException("Parse failed : IndexOutOfBoundsException");
    }
    return null;
  }
}
