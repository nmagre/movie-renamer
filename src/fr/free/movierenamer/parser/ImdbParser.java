/******************************************************************************
 *                                                                             *
 *    Movie Renamer                                                            *
 *    Copyright (C) 2012 Magré Nicolas                                         *
 *                                                                             *
 *    Movie Renamer is free software: you can redistribute it and/or modify    *
 *    it under the terms of the GNU General Public License as published by     *
 *    the Free Software Foundation, either version 3 of the License, or        *
 *    (at your option) any later version.                                      *
 *                                                                             *
 *    This program is distributed in the hope that it will be useful,          *
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            *
 *    GNU General Public License for more details.                             *
 *                                                                             *
 *    You should have received a copy of the GNU General Public License        *
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                             *
 ******************************************************************************/
package fr.free.movierenamer.parser;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import fr.free.movierenamer.utils.ImdbSearchResult;
import fr.free.movierenamer.movie.MovieInfo;
import fr.free.movierenamer.movie.MoviePerson;
import fr.free.movierenamer.utils.Utils;
import fr.free.movierenamer.utils.Settings;

/**
 *
 * @author duffy
 */
public class ImdbParser {
  
  private Settings setting;
  private boolean french;
    
  // Search Page Pattern
  private final String MOVIEIMDBPATTERN = ">\\d+.<\\/td>.*?a href=.\\/title\\/tt\\d+\\/.[^>]*>([^<]*).*?\\((\\d+).*?\\)";
  private final String MOVIENAMEPATTERN = ";\">";
  private final String IMDBIDPATTERN = "tt\\d+";
  private final String MOVIETHUMB = "/title/IMDBID/';.><img src=.http://ia.media-imdb.com/images/.*?. width=.\\d+. height=.\\d+. border=.\\d.>";
  private final String POPULARPATTERN_EN = ".*?Popular Titles.*?Displaying (\\d+).*";
  private final String POPULARPATTERN_FR = ".*?Titres Populaires.*?Affichant (\\d+)(.*)";
  private final String EXACTPATTERN_EN = "Titles \\(Exact Matches.*?Displaying (\\d+)(.*)";
  private final String EXACTPATTERN_FR = "Titres \\(R&#xE9;sultats Exacts.*?Affichant (\\d+)(.*)";
  private final String PARTIALPATTERN_EN = "Titles \\(Partial.*?Displaying (\\d+)(.*)";
  private final String PARTIALPATTERN_FR = "Titres \\(R&#xE9;sultats Partiels.*?Affichant (\\d+)(.*)";
  private final String APPROXIMATEPATTERN_EN = "Titles \\(Approx.*?Displaying (\\d+)(.*)";  
  private final String APPROXIMATEPATTERN_FR = "Titres \\(R&#xE9;sultats Approximatif.*?Affichant (\\d+)(.*)";
  
  // Movie Page Pattern
  private final String IMDBMOVIETITLE = "<meta property=.og:title. content=?(.*?) \\(.*\\d\\d\\d\\d.*\\)";
  private final String IMDBMOVIETHUMB = "/tt\\d+\".*><img src=\"http://.*.jpg\"\n";
  
  // Movie Page Combined Pattern
  private final String IMDBMOVIETITLE_1 = "<title>.* \\(.*\\d\\d\\d\\d\\.*\\)</title>";
  private final String IMDBMOVIEORIGTITLE = "<span class=\"title-extra\">.*</i></span>";
  private final String IMDBMOVIEORUNTIME = "<h5>(Runtime|Dur&#xE9;e):</h5><div class=\".*\">\\d+ min";
  private final String IMDBMOVIERATING = "<b>.[\\.,]./10</b>";
  private final String IMDBMOVIEVOTES = "<a href=\".*\" class=\".*\">.* votes</a>";
  private final String IMDBMOVIEDIRECTOR = "src='/rg/directorlist/position-\\d+/images/b.gif.link=name/nm\\d+/';\">.*</a>";
  private final String IMDBMOVIEWRITER = "src='/rg/writerlist/position-\\d/images/b.gif.link=name/nm\\d+/';\".*</a>";
  private final String IMDBMOVIEGENRE = "<a href=\"/Sections/Genres/.*/\">.*keywords";
  private final String IMDBMOVIEGENRE_FR = "";
  private final String IMDBMOVIETAGLINE = "<div class=\"info-content\">\n.*<a class=\".*\" href=\"/title/tt\\d+/taglines\"";
  private final String IMDBMOVIEPLOT = "<div class=.info-content.>\n.*(\n?)<a class=..*. href=./title/tt\\d+/plotsummary.";
  private final String IMDBMOVIECAST = "<h3>Cast</h3>.*</table></div>";
  private final String IMDBMOVIEACTOR = "/?;\"><img src=\".*/rg/castlist/position-\\d+/images/b.gif.link=/name/nm\\d+/';\">.*</td>";
  private final String IMDBMOVIECOUNTRY = "<h5>Country:</h5><div class=\".*\"><a href=\".*\">(.*)</a></div>";
  private final String IMDBMOVIESTUDIO = "<h5>Company:</h5><div class=..*.><a href=..*.>(.*)</a><a";

  public ImdbParser(boolean french, Settings setting){
      this.setting = setting;
      this.french = french;
  }
  
  public ArrayList<ImdbSearchResult> parse(String htmlSearchRes, boolean searchPage) {
    ArrayList<ImdbSearchResult> found = new ArrayList<ImdbSearchResult>();
    int limit = setting.nbResultList[setting.nbResult];

    if (searchPage) {
      setting.getLogger().log(Level.INFO, "Imdb Search page");
      found.addAll(findMovies(htmlSearchRes, (french ? POPULARPATTERN_FR : POPULARPATTERN_EN), limit, french, "Popular"));//Popular title
      found.addAll(findMovies(htmlSearchRes, (french ? EXACTPATTERN_FR : EXACTPATTERN_EN), limit, french, "Exact"));//Exact title
      found.addAll(findMovies(htmlSearchRes, (french ? PARTIALPATTERN_FR : PARTIALPATTERN_EN), limit, french, "Partial"));//Partial title
      if (found.isEmpty() || setting.displayApproximateResult)
        found.addAll(findMovies(htmlSearchRes, (french ? APPROXIMATEPATTERN_FR : APPROXIMATEPATTERN_EN), limit, french, "Approximate"));
    } else {
      setting.getLogger().log(Level.INFO, "Imdb Movie page");
      getMovie(htmlSearchRes, found);
    }
    return found;
  }

  private ArrayList<ImdbSearchResult> findMovies(String htmlSearchRes, String searchPattern, int limit, boolean french, String type) {
    ArrayList<ImdbSearchResult> found = new ArrayList<ImdbSearchResult>();
    Pattern pattern = Pattern.compile(searchPattern);
    Matcher titleMatcher = pattern.matcher(htmlSearchRes);
    Matcher movieImdbMatcher, movieNameMatcher, imdbIDMatcher, thumbMatcher;
    String movieName = "";
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
            System.out.println(nbMovie);
            while (movieImdbMatcher.find()) {

              pattern = Pattern.compile(MOVIENAMEPATTERN);
              movieNameMatcher = pattern.matcher(movieImdbMatcher.group());

              if (movieNameMatcher.find()) {
                if ((limit > -1 && count > limit) || count > nbMovie)
                  break;
                movieName = movieImdbMatcher.group().substring(movieNameMatcher.end(), movieImdbMatcher.group().length()).replaceAll("<\\/a>", Utils.EMPTY);
                movieName = decodeXMLString(movieName);

                //Fix unknown date in movie title
                if (movieName.contains("(????)"))
                  movieName = movieName.substring(0, movieName.indexOf("(????)") + 6);

                pattern = Pattern.compile(IMDBIDPATTERN);
                imdbIDMatcher = pattern.matcher(movieImdbMatcher.group());

                if (imdbIDMatcher.find()) {// movie title + imdbID found
                  String thumb = MOVIETHUMB.replace("IMDBID", imdbIDMatcher.group());
                  pattern = Pattern.compile(thumb);
                  thumbMatcher = pattern.matcher(htmlSearchRes);
                  thumb = null;
                  if (thumbMatcher.find())
                    thumb = thumbMatcher.group().substring(thumbMatcher.group().indexOf("img src=") + 9, thumbMatcher.group().indexOf(".jpg") + 4);
                  found.add(new ImdbSearchResult(movieName, imdbIDMatcher.group(), type, thumb));
                }
                count++;
              }
            }
            break;
          }
        }
      }
    } catch (IndexOutOfBoundsException e) {
      e.printStackTrace();
      setting.getLogger().log(Level.SEVERE, "{0} {1}", new Object[]{e.getMessage(), type});
    } catch (IllegalArgumentException e) {
      setting.getLogger().log(Level.SEVERE, "{0} {1}", new Object[]{e.getMessage(), type});
    }
    return found;
  }

  private void getMovie(String moviePage, ArrayList<ImdbSearchResult> found) {
    Pattern pattern = Pattern.compile(IMDBMOVIETITLE);
    Matcher titleMatcher = pattern.matcher(moviePage);

    try {
      if (titleMatcher.find()) {
        String movieName = titleMatcher.group().substring(titleMatcher.group().indexOf("content=") + 9);
        movieName = decodeXMLString(movieName);

        String imdbId = moviePage.substring(moviePage.indexOf("/tt") + 1, moviePage.indexOf("/tt") + 10);
        pattern = Pattern.compile(IMDBMOVIETHUMB);
        Matcher thumbMatcher = pattern.matcher(moviePage);
        String thumb = null;
        if (thumbMatcher.find())
          thumb = thumbMatcher.group().substring(thumbMatcher.group().indexOf("img src=") + 9, thumbMatcher.group().indexOf(".jpg") + 4);
        found.add(new ImdbSearchResult(movieName, imdbId, "Exact", thumb));

      } else setting.getLogger().log(Level.SEVERE, "imdb page unrecognized");
    } catch (IndexOutOfBoundsException e) {
      setting.getLogger().log(Level.SEVERE, e.getMessage());
      throw new IndexOutOfBoundsException("Parse failed : IndexOutOfBoundsException");
    }
  }

  public MovieInfo getMovieInfo(String moviePage) {
    MovieInfo movieInfo = new MovieInfo();
    try {
      //Title + Year
      Pattern pattern = Pattern.compile(IMDBMOVIETITLE_1);
      Matcher searchMatcher = pattern.matcher(moviePage);
      if (searchMatcher.find()) {
        String title = "";
        title = searchMatcher.group();
        title = title.replaceAll("<title>", Utils.EMPTY).replaceAll("</title>", Utils.EMPTY);
        String year = title;
        title = title.substring(0, title.indexOf("(") - 1);
        movieInfo.setTitle(decodeXMLString(title));

        pattern = Pattern.compile("\\d\\d\\d\\d");
        searchMatcher = pattern.matcher(year);
        if (searchMatcher.find()) {
          year = searchMatcher.group();
          movieInfo.setYear(year);
        }
      }

      //Original Title
      pattern = Pattern.compile(IMDBMOVIEORIGTITLE);
      searchMatcher = pattern.matcher(moviePage);
      if (searchMatcher.find()) {
        String origTitle = searchMatcher.group();
        origTitle = origTitle.substring(origTitle.indexOf(">") + 1, origTitle.lastIndexOf("<"));
        origTitle = origTitle.replaceAll("\\(.*\\)", "").replaceAll("<.*>", "");
        movieInfo.setOrigTitle(decodeXMLString(origTitle));
      } else movieInfo.setOrigTitle(movieInfo.getTitle());

      //Runtime
      pattern = Pattern.compile(IMDBMOVIEORUNTIME);
      searchMatcher = pattern.matcher(moviePage);
      if (searchMatcher.find()) {
        String runtime = searchMatcher.group();
        runtime = runtime.substring(runtime.lastIndexOf(">") + 1, runtime.length() - 4);
        if (Utils.isDigit(runtime))
          movieInfo.setRuntime(Integer.parseInt(runtime));
      }

      //Rating
      pattern = Pattern.compile(IMDBMOVIERATING);
      searchMatcher = pattern.matcher(moviePage);
      if (searchMatcher.find()) {
        String rating = searchMatcher.group();
        rating = rating.replaceAll("<b>", Utils.EMPTY).replaceAll("</b>", "").split("/")[0];
        movieInfo.setRating(rating);
      }

      //Votes
      pattern = Pattern.compile(IMDBMOVIEVOTES);
      searchMatcher = pattern.matcher(moviePage);
      if (searchMatcher.find()) {
        String votes = searchMatcher.group();
        votes = votes.substring(votes.lastIndexOf("\"") + 2, votes.lastIndexOf(" votes"));
        movieInfo.setVotes(votes);
      }

      //Directors
      pattern = Pattern.compile(IMDBMOVIEDIRECTOR);
      searchMatcher = pattern.matcher(moviePage);
      while (searchMatcher.find()) {
        String director = searchMatcher.group();
        director = director.substring(director.indexOf(">") + 1, director.lastIndexOf("<"));
        movieInfo.addActor(new MoviePerson(decodeXMLString(director), "", "Director"));
      }

      //Writers
      pattern = Pattern.compile(IMDBMOVIEWRITER);
      searchMatcher = pattern.matcher(moviePage);
      while (searchMatcher.find()) {
        String writer = searchMatcher.group();
        writer = writer.substring(writer.indexOf(">") + 1, writer.lastIndexOf("<"));
        movieInfo.addActor(new MoviePerson(decodeXMLString(writer), "", "Writer"));
      }

      //TagLine
      searchMatcher.reset();
      pattern = Pattern.compile(IMDBMOVIETAGLINE);
      searchMatcher = pattern.matcher(moviePage);
      if (searchMatcher.find()) {
        String tagline = searchMatcher.group();
        tagline = tagline.substring(tagline.indexOf("\n") + 1, tagline.indexOf("<a") - 1);
        movieInfo.setTagline(decodeXMLString(tagline));
      }

      //Plot
      pattern = Pattern.compile(IMDBMOVIEPLOT);
      searchMatcher = pattern.matcher(moviePage);
      if (searchMatcher.find()) {
        String plot = searchMatcher.group();
        plot = plot.substring(plot.indexOf("\n") + 1, plot.indexOf("<a") - 1);
        movieInfo.setSynopsis(decodeXMLString(plot));
      }

      //Genres
      pattern = Pattern.compile((french ? IMDBMOVIEGENRE_FR:IMDBMOVIEGENRE));
      searchMatcher = pattern.matcher(moviePage);
      if (searchMatcher.find()) {
        String[] genres = searchMatcher.group().split("\\|");
        for (int i = 0; i < genres.length; i++) {
          String genre = genres[i].substring(genres[i].indexOf(">") + 1, genres[i].indexOf("</a>"));
          movieInfo.addGenre(decodeXMLString(genre));
        }
      }

      //Actors
      pattern = Pattern.compile(IMDBMOVIECAST);
      searchMatcher = pattern.matcher(moviePage);
      if (searchMatcher.find()) {
        String[] actors = searchMatcher.group().split("</tr>");
        for (int i = 0; i < actors.length; i++) {
          pattern = Pattern.compile(IMDBMOVIEACTOR);
          Matcher matcher2 = pattern.matcher(actors[i]);
          boolean thumb = !actors[i].contains("no_photo");
          if (matcher2.find()) {
            String thumbactor = "";
            if (thumb) {
              String actorThumb = matcher2.group().substring(matcher2.group().indexOf("src=") + 5, matcher2.group().indexOf("width") - 2);
              thumbactor = actorThumb.replaceAll("SY\\d+", "SY214").replaceAll("SX\\d+", "SX314");
            }

            String name = matcher2.group().substring(matcher2.group().indexOf("onclick="), matcher2.group().indexOf("</a></td><td"));
            name = name.substring(name.indexOf(">") + 1);
            MoviePerson actor = new MoviePerson(decodeXMLString(name), thumbactor, "Actor");
            String role = matcher2.group().substring(matcher2.group().indexOf("class=\"char\""));
            role = role.substring(role.indexOf(">") + 1, role.indexOf("</td>"));
            if (role.contains("href=")) role = role.substring(role.indexOf(">") + 1);
            if (role.contains("/")) {
              String[] roles = role.split(" / ");
              for (int j = 0; j < roles.length; j++) {
                role = roles[j].replaceAll("</a>", "");
                if (role.contains("href=")) role = role.substring(role.indexOf(">") + 1);
                actor.addRole(decodeXMLString(role));
              }
            } else
              actor.addRole(decodeXMLString(role));
            movieInfo.addActor(actor);
          }
        }
      }

      //Countries
      pattern = Pattern.compile(IMDBMOVIECOUNTRY);
      searchMatcher = pattern.matcher(moviePage);
      while (searchMatcher.find()) {
        String country = searchMatcher.group();
        country = country.substring(country.indexOf("<a"), country.lastIndexOf("</a>"));
        if (country.contains(" | ")) {
          String[] countries = country.split("\\|");
          for (int i = 0; i < countries.length; i++) {
            country = decodeXMLString(countries[i]);
            if (country.contains("country"))
              movieInfo.addCountry(country.substring(country.indexOf(">") + 1, country.indexOf("</")));
          }
        } else {
          country = decodeXMLString(country);
          if (country.contains("</"))
            movieInfo.addCountry(country.substring(country.indexOf(">") + 1, country.indexOf("</")));
          else
            movieInfo.addCountry(country.substring(country.indexOf(">") + 1));
        }
      }
      
      //Studio
      pattern = Pattern.compile(IMDBMOVIESTUDIO);
      searchMatcher = pattern.matcher(moviePage);
      while (searchMatcher.find()) {
        String studio = searchMatcher.group();
        studio = studio.substring(studio.indexOf("<a"), studio.lastIndexOf("</a>"));
        studio = studio.substring(studio.lastIndexOf(">") + 1);
        studio = decodeXMLString(studio);
        movieInfo.addStudio(studio);
      }
    } catch (IndexOutOfBoundsException e) {
      setting.getLogger().log(Level.SEVERE, e.getMessage());
      throw new IndexOutOfBoundsException("Parse failed : IndexOutOfBoundsException");
    }
    return movieInfo;
  }

  private String decodeXMLString(String text) {
    try {
      text = text.replaceAll("&#x(\\w\\w);", "%$1");
      text = URLDecoder.decode(text.replaceAll("% ", "%25 "), "ISO-8859-1");
      return text.trim();
    } catch (UnsupportedEncodingException e) {
      setting.getLogger().log(Level.SEVERE, e.getMessage());
    }
    return "";
  }
}
