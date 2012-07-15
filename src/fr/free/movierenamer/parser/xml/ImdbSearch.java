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
  private final boolean french = config.movieScrapperFR;

  // Search Page Pattern
  private static final String MOVIEIMDBPATTERN = ">\\d+.<\\/td>.*?a href=.\\/title\\/tt\\d+\\/.[^>]*>([^<]*).*?\\((\\d+).*?\\).?(\\(.*?\\) )?";
  private static final String MOVIENAMEPATTERN = ";\">";
  private static final String IMDBIDPATTERN = "tt\\d+";
  private static final String MOVIETHUMB = "/title/IMDBID/';.><img src=.http://ia.media-imdb.com/images/.*?. width=.\\d+. height=.\\d+. border=.\\d.>";
  private static final String POPULARPATTERN_EN = ".*?Popular Titles.*?Displaying (\\d+).*";
  private static final String POPULARPATTERN_FR = ".*?Titres Populaires.*?Affichant (\\d+)(.*)";
  private static final String EXACTPATTERN_EN = "Titles \\(Exact Matches.*?Displaying (\\d+)(.*)";
  private static final String EXACTPATTERN_FR = "Titres \\(R&#xE9;sultats Exacts.*?Affichant (\\d+)(.*)";
  private static final String PARTIALPATTERN_EN = "Titles \\(Partial.*?Displaying (\\d+)(.*)";
  private static final String PARTIALPATTERN_FR = "Titres \\(R&#xE9;sultats Partiels.*?Affichant (\\d+)(.*)";
  private static final String APPROXIMATEPATTERN_EN = "Titles \\(Approx.*?Displaying (\\d+)(.*)";
  private static final String APPROXIMATEPATTERN_FR = "Titres \\(R&#xE9;sultats Approximatif.*?Affichant (\\d+)(.*)";

  private static final String IMDBMOVIETITLE = "<meta property=.og:title. content=?(.*?) \\(.*\\d\\d\\d\\d.*\\)";
  private static final String IMDBMOVIETHUMB = "/tt\\d+\".*><img src=\"http://.*.jpg\"\n";
//  private static final String IMDBMOVIETITLE_C = "<title>(.* \\(.*\\d+.*\\).*)</title>";
  private static final String IMDBMOVIETHUMB_C = "title=\".*\" src=.http://ia.media-imdb.com/images/.*>";
  
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
        results.addAll(findMovies(htmlSearchRes, (french ? POPULARPATTERN_FR : POPULARPATTERN_EN), limit, french, SearchResult.SearchResultType.POPULAR));// Popular title
        results.addAll(findMovies(htmlSearchRes, (french ? EXACTPATTERN_FR : EXACTPATTERN_EN), limit, french, SearchResult.SearchResultType.EXACT));// Exact title
        results.addAll(findMovies(htmlSearchRes, (french ? PARTIALPATTERN_FR : PARTIALPATTERN_EN), limit, french, SearchResult.SearchResultType.PARTIAL));// Partial title
        if (results.isEmpty() || config.displayApproximateResult) {
          results.addAll(findMovies(htmlSearchRes, (french ? APPROXIMATEPATTERN_FR : APPROXIMATEPATTERN_EN), limit, french, SearchResult.SearchResultType.APPROXIMATE));
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
  private ArrayList<SearchResult> findMovies(String htmlSearchRes, String searchPattern, int limit, boolean french, SearchResult.SearchResultType type) throws IndexOutOfBoundsException {
    ArrayList<SearchResult> found = new ArrayList<SearchResult>();
    Pattern pattern = Pattern.compile(searchPattern);
    Matcher titleMatcher = pattern.matcher(htmlSearchRes);
    Matcher movieImdbMatcher, movieNameMatcher, imdbIDMatcher, thumbMatcher;
    String movieName;

    String start = (!french ? "Displaying " : "Affichant ");
    String stop = (!french ? "Result" : "R&#xE9;sultat");
    try {
      if (titleMatcher.find()) {
        int count = 1;
        String grp = titleMatcher.group();
        String[] grps = grp.split("</table>");
        for (int i = 0; i < grps.length; i++) {
          titleMatcher = pattern.matcher(grps[i]);
          if (titleMatcher.find()) {
            pattern = Pattern.compile(MOVIEIMDBPATTERN);
            movieImdbMatcher = pattern.matcher(grps[i]);

            int nbMovie = Integer.parseInt(grps[i].substring(grps[i].lastIndexOf(start) + start.length(), grps[i].lastIndexOf(stop) - 1));
            while (movieImdbMatcher.find()) {
              if (movieImdbMatcher.group(3) != null) {
                if (!movieImdbMatcher.group(3).equals("TV")) {
                  continue;
                }
              }
              pattern = Pattern.compile(MOVIENAMEPATTERN);
              movieNameMatcher = pattern.matcher(movieImdbMatcher.group());

              if (movieNameMatcher.find()) {
                if ((limit > -1 && count > limit) || count > nbMovie) {//
                  break;
                }
                movieName = movieImdbMatcher.group().substring(movieNameMatcher.end(), movieImdbMatcher.group().length()).replaceAll("<\\/a>", Utils.EMPTY);
                movieName = Utils.unEscapeXML(movieName, "ISO-8859-1");

                // Fix unknown date in movie title
                if (movieName.contains("(????)")) {
                  movieName = movieName.substring(0, movieName.indexOf("(????)") + 6);
                }

                pattern = Pattern.compile(IMDBIDPATTERN);
                imdbIDMatcher = pattern.matcher(movieImdbMatcher.group());

                if (imdbIDMatcher.find()) {// movie title + imdbID found
                  String thumb = MOVIETHUMB.replace("IMDBID", imdbIDMatcher.group());
                  pattern = Pattern.compile(thumb);
                  thumbMatcher = pattern.matcher(htmlSearchRes);
                  thumb = null;
                  if (thumbMatcher.find()) {
                    thumb = thumbMatcher.group().substring(thumbMatcher.group().indexOf("img src=") + 9, thumbMatcher.group().indexOf(".jpg") + 4);
                  }
                  found.add(new SearchResult(movieName, new MediaID(imdbIDMatcher.group(), MediaID.IMDBID), type, thumb));
                }
                count++;
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
   Pattern pattern = Pattern.compile(IMDBMOVIETITLE);
   Matcher titleMatcher = pattern.matcher(moviePage);

   try {
     if (titleMatcher.find()) {
       // Extract the movie name
       String movieName = titleMatcher.group().substring(titleMatcher.group().indexOf("content=") + 9);
       movieName = Utils.unEscapeXML(movieName, "ISO-8859-1");
       // Extract the movie Id
       String imdbId = moviePage.substring(moviePage.indexOf("/tt") + 1, moviePage.indexOf("/tt") + 10);
       pattern = Pattern.compile(french ? IMDBMOVIETHUMB_C : IMDBMOVIETHUMB);
       Matcher thumbMatcher = pattern.matcher(moviePage);
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
