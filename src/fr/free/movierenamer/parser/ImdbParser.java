/*
 * Movie Renamer
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

import fr.free.movierenamer.media.MediaPerson;
import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.ui.res.SearchResult;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Imdb http page parser
 *
 * @author Nicolas Magré
 */
public class ImdbParser {

  private Settings setting;
  private boolean french;
  // Search Page Pattern
  private static final String MOVIEIMDBPATTERN = ">\\d+.<\\/td>.*?a href=.\\/title\\/tt\\d+\\/.[^>]*>([^<]*).*?\\((\\d+).*?\\)";
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
  // Movie Page Pattern
  private static final String IMDBMOVIETITLE = "<meta property=.og:title. content=?(.*?) \\(.*\\d\\d\\d\\d.*\\)";
  private static final String IMDBMOVIETHUMB = "/tt\\d+\".*><img src=\"http://.*.jpg\"\n";
  // Movie Page Combined Pattern
  private static final String IMDBMOVIETITLE_C = "<title>.* \\(.*\\d+.*\\).*</title>";
  private static final String IMDBMOVIETHUMB_C = "title=\".*\" src=.http://ia.media-imdb.com/images/.*>";
  private static final String IMDBMOVIEORIGTITLE = "<span class=\"title-extra\">.*</i></span>";
  private static final String IMDBMOVIEORIGTITLE_FR = "<h5>Alias:</h5>.*\\(titre original\\)";
  private static final String IMDBMOVIEORUNTIME = "<h5>(Runtime|Dur&#xE9;e):</h5><div class=\".*\">\\d+ min";
  private static final String IMDBMOVIERATING = "<b>.[\\.,]./10</b>";
  private static final String IMDBMOVIEVOTES = "<a href=\".*\" class=\".*\">.* votes</a>";
  private static final String IMDBMOVIEDIRECTOR = "src='/rg/directorlist/position-\\d+/images/b.gif.link=name/nm\\d+/';\">.*</a>";
  private static final String IMDBMOVIEWRITER = "src='/rg/writerlist/position-\\d/images/b.gif.link=name/nm\\d+/';\".*</a>";
  private static final String IMDBMOVIEGENRE = "<a href=\"/Sections/Genres/.*/\">.*keywords";
  private static final String IMDBMOVIEGENRE_FR = "<h5>Genre:</h5>\n<div class=.info-content.>\n.*\n</div>";
  private static final String IMDBMOVIETAGLINE = "<div class=\"info-content\">\n.*<a class=\".*\" href=\"/title/tt\\d+/taglines\"";
  private static final String IMDBMOVIEPLOT = "<div class=.info-content.>\n.*(\n?)<a class=..*. href=./title/tt\\d+/plotsummary.";
  private static final String IMDBMOVIECAST = "<h3>((Cast)|(Ensemble))</h3>.*";
  private static final String IMDBMOVIEACTOR = "\"><img src=\".*/rg/castlist/position-\\d+/images/b.gif.link=/name/nm\\d+/';\">.*</td>";
  private static final String IMDBMOVIECOUNTRY = "<h5>((Country:)|(Pays:))</h5><div class=\"info-content\">(.*)<div class=\"info\"";
  private static final String IMDBMOVIESTUDIO = "<h5>((Company:)|(Soci&#xE9;t&#xE9;:))</h5><div class=..*.><a href=..*.>(.*)</a><a";

  public ImdbParser(Settings setting) {
    this.setting = setting;
    this.french = setting.imdbFr;
  }

  /**
   * Parse imdb page to get movies title
   *
   * @param htmlSearchRes Imdb search page or imdb movie page
   * @param searchPage Is a imdb search page or imdb movie page
   * @return Array of ImdbSearchResult
   * @throws IndexOutOfBoundsException  
   */
  public ArrayList<SearchResult> parse(String htmlSearchRes, boolean searchPage) throws IndexOutOfBoundsException {
    ArrayList<SearchResult> found = new ArrayList<SearchResult>();
    int limit = setting.nbResultList[setting.nbResult];

    if (searchPage) {
      Settings.LOGGER.log(Level.INFO, "Imdb Search page");
      found.addAll(findMovies(htmlSearchRes, (french ? POPULARPATTERN_FR : POPULARPATTERN_EN), limit, french, french ? "Populaire" : "Popular"));//Popular title
      found.addAll(findMovies(htmlSearchRes, (french ? EXACTPATTERN_FR : EXACTPATTERN_EN), limit, french, "Exact"));//Exact title
      found.addAll(findMovies(htmlSearchRes, (french ? PARTIALPATTERN_FR : PARTIALPATTERN_EN), limit, french, french ? "Partiel" : "Partial"));//Partial title
      if (found.isEmpty() || setting.displayApproximateResult) {
        found.addAll(findMovies(htmlSearchRes, (french ? APPROXIMATEPATTERN_FR : APPROXIMATEPATTERN_EN), limit, french, french ? "Approximatif" : "Approximate"));
      }
    } else {
      Settings.LOGGER.log(Level.INFO, "Imdb Movie page");
      getMovie(htmlSearchRes, found);
    }
    return found;
  }

  /**
   * Get movies title by result type in Imdb search page
   *
   * @param htmlSearchRes Imdb search page
   * @param searchPattern Pattern of result to retreive
   * @param limit Limitation of returned result
   * @param french Is imdb french page
   * @param type Type of result search
   * @return Array of ImdbSearchResult
   */
  private ArrayList<SearchResult> findMovies(String htmlSearchRes, String searchPattern, int limit, boolean french, String type) throws IndexOutOfBoundsException {
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

              pattern = Pattern.compile(MOVIENAMEPATTERN);
              movieNameMatcher = pattern.matcher(movieImdbMatcher.group());

              if (movieNameMatcher.find()) {
                if ((limit > -1 && count > limit) || count > nbMovie) {
                  break;
                }
                movieName = movieImdbMatcher.group().substring(movieNameMatcher.end(), movieImdbMatcher.group().length()).replaceAll("<\\/a>", Utils.EMPTY);
                movieName = Utils.unEscapeXML(movieName, "ISO-8859-1");

                //Fix unknown date in movie title
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
                  found.add(new SearchResult(movieName, imdbIDMatcher.group(), type, thumb));
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
   * @param found Array to put the results in
   */
  private void getMovie(String moviePage, ArrayList<SearchResult> found) throws IndexOutOfBoundsException {
    Pattern pattern = Pattern.compile(IMDBMOVIETITLE);
    Matcher titleMatcher = pattern.matcher(moviePage);

    try {
      if (titleMatcher.find()) {
        String movieName = titleMatcher.group().substring(titleMatcher.group().indexOf("content=") + 9);
        movieName = Utils.unEscapeXML(movieName, "ISO-8859-1");

        String imdbId = moviePage.substring(moviePage.indexOf("/tt") + 1, moviePage.indexOf("/tt") + 10);
        pattern = Pattern.compile(french ? IMDBMOVIETHUMB_C : IMDBMOVIETHUMB);
        Matcher thumbMatcher = pattern.matcher(moviePage);
        String thumb = null;
        if (thumbMatcher.find()) {
          String thumbnail = thumbMatcher.group();
          if (thumbnail.contains("img src=")) {
            thumb = thumbnail.substring(thumbnail.indexOf("img src=") + 9, thumbnail.indexOf(".jpg") + 4);
          } else {
            thumb = thumbnail.substring(thumbnail.lastIndexOf("src=") + 5, thumbnail.lastIndexOf("\""));
          }
        }
        found.add(new SearchResult(movieName, imdbId, "Exact", thumb));

      } else {
        Settings.LOGGER.log(Level.SEVERE, "imdb page unrecognized");
      }
    } catch (IndexOutOfBoundsException e) {
      Settings.LOGGER.log(Level.SEVERE, e.getMessage());
      throw new IndexOutOfBoundsException("Parse failed : IndexOutOfBoundsException");
    }
  }

  /**
   * Get movie information in imdb movie page combined
   *
   * @param moviePage Imdb movie page
   * @return Movie information
   * @throws IndexOutOfBoundsException  
   */
  public MovieInfo getMovieInfo(String moviePage) throws IndexOutOfBoundsException {
    MovieInfo movieInfo = new MovieInfo();
    //Title + Year
    Pattern pattern = Pattern.compile(IMDBMOVIETITLE_C);
    Matcher searchMatcher = pattern.matcher(moviePage);
    if (searchMatcher.find()) {
      String title = searchMatcher.group();
      title = title.replaceAll("<title>", Utils.EMPTY).replaceAll("</title>", Utils.EMPTY);
      String year = title;
      title = title.substring(0, title.lastIndexOf("(") - 1);
      movieInfo.setTitle(Utils.unEscapeXML(title, "ISO-8859-1"));

      pattern = Pattern.compile("\\(\\d\\d\\d\\d.*\\)");
      searchMatcher = pattern.matcher(year);
      if (searchMatcher.find()) {
        year = searchMatcher.group().replace("(", Utils.EMPTY).replace(")", Utils.EMPTY);
        if (year.contains("/")) {
          year = year.substring(0, year.indexOf("/"));
        }
        if (year.contains(" ")) {
          year = year.substring(0, year.indexOf(" "));
        }
        movieInfo.setYear(year);
      }
    } else {
      Settings.LOGGER.log(Level.SEVERE, "No title found in imdb page");
    }

    // Thumb
    pattern = Pattern.compile(IMDBMOVIETHUMB_C);
    searchMatcher = pattern.matcher(moviePage);
    if (searchMatcher.find()) {
      String imdbThumb = searchMatcher.group();
      imdbThumb = imdbThumb.substring(imdbThumb.lastIndexOf("src=") + 5, imdbThumb.lastIndexOf("\""));
      movieInfo.setImdbThumb(imdbThumb);
    }

    //Original Title
    pattern = Pattern.compile(french ? IMDBMOVIEORIGTITLE_FR : IMDBMOVIEORIGTITLE);
    searchMatcher = pattern.matcher(moviePage);
    if (searchMatcher.find()) {
      String origTitle = searchMatcher.group().replace("&#x22;", "\"");
      if (french) {
        origTitle = origTitle.substring(0, origTitle.lastIndexOf("\""));
        origTitle = origTitle.substring(origTitle.lastIndexOf(">"));
        origTitle = origTitle.substring(origTitle.lastIndexOf("\"") + 1);
      } else {
        origTitle = origTitle.substring(origTitle.indexOf(">") + 1, origTitle.lastIndexOf("<"));
        origTitle = origTitle.replaceAll("\\(.*\\)", "").replaceAll("<.*>", "");
      }
      movieInfo.setOrigTitle(Utils.unEscapeXML(origTitle, "ISO-8859-1"));
    } else {
      movieInfo.setOrigTitle(movieInfo.getTitle());
    }

    //Runtime
    pattern = Pattern.compile(IMDBMOVIEORUNTIME);
    searchMatcher = pattern.matcher(moviePage);
    if (searchMatcher.find()) {
      String runtime = searchMatcher.group();
      runtime = runtime.substring(runtime.lastIndexOf(">") + 1, runtime.length() - 4);
      if (Utils.isDigit(runtime)) {
        movieInfo.setRuntime(runtime);
      }
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
      String imdbId = "";
      if (director.contains("link=name/nm")) {
        int pos = director.indexOf("link=name/nm") + 10;
        imdbId = director.substring(pos, pos + 9);
      }
      director = director.substring(director.indexOf(">") + 1, director.lastIndexOf("<"));
      MediaPerson dir = new MediaPerson(Utils.unEscapeXML(director, "ISO-8859-1"), "", MediaPerson.DIRECTOR);
      dir.setImdbId(imdbId);
      movieInfo.addDirector(dir);
    }

    //Writers
    pattern = Pattern.compile(IMDBMOVIEWRITER);
    searchMatcher = pattern.matcher(moviePage);
    while (searchMatcher.find()) {
      String writer = searchMatcher.group();
      writer = writer.substring(writer.indexOf(">") + 1, writer.lastIndexOf("<"));
      movieInfo.addWriter(new MediaPerson(Utils.unEscapeXML(writer, "ISO-8859-1"), "", MediaPerson.WRITER));
    }

    //TagLine
    searchMatcher.reset();
    pattern = Pattern.compile(IMDBMOVIETAGLINE);
    searchMatcher = pattern.matcher(moviePage);
    if (searchMatcher.find()) {
      String tagline = searchMatcher.group();
      tagline = tagline.substring(tagline.indexOf("\n") + 1, tagline.indexOf("<a") - 1);
      movieInfo.setTagline(Utils.unEscapeXML(tagline, "ISO-8859-1"));
    }

    //Plot
    pattern = Pattern.compile(IMDBMOVIEPLOT);
    searchMatcher = pattern.matcher(moviePage);
    if (searchMatcher.find()) {
      String plot = searchMatcher.group();
      plot = plot.substring(plot.indexOf("\n") + 1, plot.indexOf("<a") - 1);
      movieInfo.setSynopsis(Utils.unEscapeXML(plot, "ISO-8859-1"));
    }

    //Genres
    pattern = Pattern.compile((french ? IMDBMOVIEGENRE_FR : IMDBMOVIEGENRE));
    searchMatcher = pattern.matcher(moviePage);
    if (searchMatcher.find()) {
      String found = french ? searchMatcher.group().split("\n")[2] : searchMatcher.group();
      String[] genres = found.split("\\|");
      for (int i = 0; i < genres.length; i++) {
        String genre = french ? genres[i].trim() : genres[i].substring(genres[i].indexOf(">") + 1, genres[i].indexOf("</a>"));
        movieInfo.addGenre(Utils.unEscapeXML(genre, "ISO-8859-1"));
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
          if (thumbactor.equals("http://i.media-imdb.com/images/b.gif")) {
            thumbactor = "";
          }

          String imdbId = "";
          if (matcher2.group().contains("link=/name/nm")) {
            int pos = matcher2.group().indexOf("link=/name/nm") + 11;
            imdbId = matcher2.group().substring(pos, pos + 9);
          }

          MediaPerson actor = new MediaPerson(Utils.unEscapeXML(name, "ISO-8859-1"), thumbactor, MediaPerson.ACTOR);
          actor.setImdbId(imdbId);

          String role = matcher2.group().substring(matcher2.group().indexOf("class=\"char\""));
          role = role.substring(role.indexOf(">") + 1, role.indexOf("</td>"));
          if (role.contains("href=")) {
            role = role.substring(role.indexOf(">") + 1);
          }

          try {
            if (role.contains("/")) {
              String[] roles = role.split(" / ");
              for (int j = 0; j < roles.length; j++) {
                role = roles[j].replaceAll("</a>", "");
                if (role.contains("href=")) {
                  role = role.substring(role.indexOf(">") + 1);
                }
                actor.addRole(Utils.unEscapeXML(role, "ISO-8859-1"));
              }
            } else {
              actor.addRole(Utils.unEscapeXML(role, "ISO-8859-1"));
            }
          } catch (ActionNotValidException e) {
            Settings.LOGGER.log(Level.SEVERE, e.getMessage());
          }
          movieInfo.addActor(actor);
        }
      }
    }

    //Countries
    pattern = Pattern.compile(IMDBMOVIECOUNTRY);
    searchMatcher = pattern.matcher(moviePage);
    if (searchMatcher.find()) {
      String country = searchMatcher.group();
      if (country.contains("/country/")) {
        country = country.substring(country.indexOf("<a"), country.lastIndexOf("</a>"));
        if (country.contains(" | ")) {
          String[] countries = country.split("\\|");
          for (int i = 0; i < countries.length; i++) {
            country = Utils.unEscapeXML(countries[i], "ISO-8859-1");
            if (country.contains("country")) {
              movieInfo.addCountry(country.substring(country.indexOf(">") + 1, country.indexOf("</")));
            }
          }
        } else {
          country = Utils.unEscapeXML(country, "ISO-8859-1");
          if (country.contains("</")) {
            movieInfo.addCountry(country.substring(country.indexOf(">") + 1, country.indexOf("</")));
          } else {
            movieInfo.addCountry(country.substring(country.indexOf(">") + 1));
          }
        }
      } else {
        country = country.substring(0, country.indexOf("</div></div>"));
        country = country.substring(country.indexOf("info-content") + 14);
        if (country.contains(" | ")) {
          String[] countries = country.split("\\|");
          for (int i = 0; i < countries.length; i++) {
            country = Utils.unEscapeXML(countries[i], "ISO-8859-1");
            movieInfo.addCountry(country);
          }
        } else {
          country = Utils.unEscapeXML(country, "ISO-8859-1");
          movieInfo.addCountry(country);
        }
      }
    }

    //Studio
    pattern = Pattern.compile(IMDBMOVIESTUDIO);
    searchMatcher = pattern.matcher(moviePage);
    while (searchMatcher.find()) {
      String studio = searchMatcher.group();
      studio = studio.substring(studio.indexOf("<a"), studio.lastIndexOf("</a>"));
      studio = studio.substring(studio.lastIndexOf(">") + 1);
      studio = Utils.unEscapeXML(studio, "ISO-8859-1");
      movieInfo.addStudio(studio);
    }

    return movieInfo;
  }
}
