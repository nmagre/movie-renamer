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

import fr.free.movierenamer.media.MediaImage;
import fr.free.movierenamer.media.MediaPerson;
import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.SAXException;

/**
 * Class ImdbInfo
 *
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
public class ImdbInfo extends MrParser<MovieInfo> {

  private final MovieInfo movieInfo;

  public enum ImdbPattern {

    TITLE("<title>(.* \\(.*\\d+.*\\).*)</title>"),
    THUMB("title=\".*\" src=.(http://ia.media-imdb.com/images/.*)>"),
    ORIGTITLE("info-content.>\\s+\"(.*)\"&nbsp.*?[Oo]riginal"),
    RUNTIME("<h5>.*?:</h5><div class=\".*\">.*?(\\d+) [Mm]in"),
    RATING("<b>(.[\\.,].)/10</b>"),
    VOTES("tn15more.>(.*) (?:vot..?|Stimmen)</a>"),
    DIRECTOR("src=./rg/directorlist/position-\\d+/images/b.gif.link=name/nm(\\d+)/.;\">(.*)</a>"),
    WRITER("src=./rg/writerlist/position-\\d/images/b.gif.link=name/nm(\\d+)/.;\">(.*)</a>"),
    GENRE("<h5>G(?:e|&#xE9;)n...?:</h5>\n.*info-content.*\n(.*)"),
    TAGLINE("<div class=\"info-content\">\n(.*)<a class=\".*\" href=\"/title/tt\\d+/taglines\""),// Only on .com site (english)
    PLOT("<div class=.info-content.>\n(.*)(?:\n?)<a class=..*. href=./title/tt\\d+/plotsummary."),
    CAST("<h3>((Cast)|(Ensemble)|(Besetzung)|(Reparto))</h3>.*"),
    ACTOR("\"><img src=\".*/rg/castlist/position-\\d+/images/b.gif.link=/name/nm\\d+/';\">.*</td>"),
    COUNTRY("<h5>(?:(?:Country)|(?:Pays)|(?:Nazionalit&#xE0;)|(?:Pa&#xED;s)|(?:Land)):</h5><div class=\"info-content\">(.*?)</div></div><div class=\"info\"><h5>.*?:</h5>"),
    STUDIO("<h5>(?:(?:Company)|(?:Soci&#xE9;t&#xE9;)|(?:Compagnia)|(?:Compa&#xF1;&#xED;a)|(?:Firma)):</h5><div class=..*.><a href=..*.>(.*)</a><a"),
    TOP250("<a href=./chart/top\\?tt\\d{7}.>(?:(?:Top 250)|(?:Las 250 m&#xE1;s votadas)): #(\\d{1,3})</a>");
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
  private final NOSAXException ex = new NOSAXException();

  public ImdbInfo() {
    super();
    movieInfo = new MovieInfo();
  }

  @Override
  public void startDocument() throws SAXException {
    String moviePage = getContent("ISO-8859-1");
    Pattern pattern;
    long begin = System.currentTimeMillis();

    // Title + Year
    Matcher searchMatcher = ImdbPattern.TITLE.getPattern().matcher(moviePage);
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
      movieInfo.setTitle(Utils.unEscapeXML(title, "ISO-8859-1"));

      // Get year
      pattern = Pattern.compile("\\((\\d{4}).*\\)");
      searchMatcher = pattern.matcher(res);
      if (searchMatcher.find()) {
        res = searchMatcher.group(1);
        if (res != null && Utils.isDigit(res)) {
          int year = Integer.parseInt(res);
          if (year >= 1900 && year <= Calendar.getInstance().get(Calendar.YEAR)) {// Before all "movies" producted are more short video than a movie
            movieInfo.setYear("" + year);
          }
        }
      }
    } else {
      Settings.LOGGER.log(Level.SEVERE, "No title found in imdb page");
    }
    Settings.LOGGER.log(Level.INFO, "Title + year in : {0}", ((float) (System.currentTimeMillis() - begin)) / 1000f);

    // Thumb
    searchMatcher = ImdbPattern.THUMB.getPattern().matcher(moviePage);
    if (searchMatcher.find()) {
      String imdbThumb = searchMatcher.group(1);
      MediaImage movieThumb = new MediaImage(0, MediaImage.MediaImageType.THUMB);
      for (MediaImage.MediaImageSize size : MediaImage.MediaImageSize.values()) {
        movieThumb.setUrl(imdbThumb, size);
      }
      movieInfo.addThumb(movieThumb);
    }

    Settings.LOGGER.log(Level.INFO, "Thumb +  : {0}", ((float) (System.currentTimeMillis() - begin)) / 1000f);

    // Original Title
    searchMatcher = ImdbPattern.ORIGTITLE.getPattern().matcher(moviePage);
    if (searchMatcher.find()) {
      movieInfo.setOrigTitle(Utils.unEscapeXML(searchMatcher.group(1), "ISO-8859-1"));
    } else {
      movieInfo.setOrigTitle(movieInfo.getTitle());
    }

    Settings.LOGGER.log(Level.INFO, "Original title +  : {0}", ((float) (System.currentTimeMillis() - begin)) / 1000f);

    // Runtime
    searchMatcher = ImdbPattern.RUNTIME.getPattern().matcher(moviePage);
    if (searchMatcher.find()) {
      String runtime = searchMatcher.group(1);
      if (Utils.isDigit(runtime)) {
        movieInfo.setRuntime(runtime);
      }
    }

    Settings.LOGGER.log(Level.INFO, "Runtime +  : {0}", ((float) (System.currentTimeMillis() - begin)) / 1000f);

    // Rating
    searchMatcher = ImdbPattern.RATING.getPattern().matcher(moviePage);
    if (searchMatcher.find()) {
      String rating = searchMatcher.group(1);
      movieInfo.setRating(rating);
    }
    
        Settings.LOGGER.log(Level.INFO, "Rating +  : {0}", ((float) (System.currentTimeMillis() - begin)) / 1000f);

    // Votes
    searchMatcher = ImdbPattern.VOTES.getPattern().matcher(moviePage);
    if (searchMatcher.find()) {
      String votes = searchMatcher.group(1).replaceAll("[., ]", "");
      movieInfo.setVotes(votes);
    }
    
        Settings.LOGGER.log(Level.INFO, "Votes +  : {0}", ((float) (System.currentTimeMillis() - begin)) / 1000f);

    // Directors
    searchMatcher = ImdbPattern.DIRECTOR.getPattern().matcher(moviePage);
    while (searchMatcher.find()) {
      String director = searchMatcher.group(2);
      String imdbId = searchMatcher.group(1);
      MediaPerson dir = new MediaPerson(Utils.unEscapeXML(director, "ISO-8859-1"), "", MediaPerson.DIRECTOR);
      dir.setImdbId(imdbId);
      movieInfo.addPerson(dir);
    }
    
        Settings.LOGGER.log(Level.INFO, "Directors +  : {0}", ((float) (System.currentTimeMillis() - begin)) / 1000f);

    // Writers
    searchMatcher = ImdbPattern.WRITER.getPattern().matcher(moviePage);
    while (searchMatcher.find()) {
      String writer = searchMatcher.group(2);
      //String imdbId = searchMatcher.group(1); // Not used
      movieInfo.addPerson(new MediaPerson(Utils.unEscapeXML(writer, "ISO-8859-1"), "", MediaPerson.WRITER));
    }
    
        Settings.LOGGER.log(Level.INFO, "Writers +  : {0}", ((float) (System.currentTimeMillis() - begin)) / 1000f);

    // TagLine
    searchMatcher = ImdbPattern.TAGLINE.getPattern().matcher(moviePage);
    if (searchMatcher.find()) {
      String tagline = searchMatcher.group(1);
      movieInfo.setTagline(Utils.unEscapeXML(tagline, "ISO-8859-1"));
    }
    
        Settings.LOGGER.log(Level.INFO, "TagLine +  : {0}", ((float) (System.currentTimeMillis() - begin)) / 1000f);

    // Plot
    searchMatcher = ImdbPattern.PLOT.getPattern().matcher(moviePage);
    if (searchMatcher.find()) {
      String plot = searchMatcher.group(1);
      movieInfo.setSynopsis(Utils.unEscapeXML(plot, "ISO-8859-1"));
    }
    
    Settings.LOGGER.log(Level.INFO, "Plot +  : {0}", ((float) (System.currentTimeMillis() - begin)) / 1000f);

    // Genres
    searchMatcher = ImdbPattern.GENRE.getPattern().matcher(moviePage);
    if (searchMatcher.find()) {
      String[] genres = searchMatcher.group(1).split("\\|");
      for (int i = 0; i < genres.length; i++) {
        String genre;
        switch (config.movieScrapperLang) {
          case ENGLISH:
            genre = genres[i].substring(genres[i].indexOf(">") + 1, genres[i].indexOf("</a>")).trim();
            if (genre.equals("See more")) {
              genre = "";
            }
            break;
          default:
            genre = genres[i].trim();
            break;
        }

        if (!genre.equals("")) {
          movieInfo.addGenre(Utils.unEscapeXML(genre, "ISO-8859-1"));
        }
      }
    }
    
    Settings.LOGGER.log(Level.INFO, "Genres +  : {0}", ((float) (System.currentTimeMillis() - begin)) / 1000f);

    // Actors
    // TODO code not rewrite, i think we can do something much better ^^
    searchMatcher = ImdbPattern.CAST.getPattern().matcher(moviePage);
    if (searchMatcher.find()) {
      String[] actors = searchMatcher.group().split("</tr>");
      for (int i = 0; i < actors.length; i++) {
        Matcher matcher2 = ImdbPattern.ACTOR.getPattern().matcher(actors[i]);
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
          movieInfo.addPerson(actor);
        }
      }
    }
    
    Settings.LOGGER.log(Level.INFO, "Actors +  : {0}", ((float) (System.currentTimeMillis() - begin)) / 1000f);

    // Countries
    searchMatcher = ImdbPattern.COUNTRY.getPattern().matcher(moviePage);// FIXME , find "USA | Malaysia:18PL" instead of "USA" (E.g :bienvenue a zombiland with imdb.com)
    if (searchMatcher.find()) {
      String[] countries = searchMatcher.group(1).split("\\|");
      for (int i = 0; i < countries.length; i++) {
        String country;
        switch (config.movieScrapperLang) {
          case ENGLISH:
            country = countries[i].substring(countries[i].indexOf(">") + 1, countries[i].indexOf("</a>")).trim();
            break;
          default:
            country = countries[i].trim();
            break;
        }
        if (!country.equals("")) {
          country = Utils.unEscapeXML(country, "ISO-8859-1");
          movieInfo.addCountry(country);
        }
      }
    }
    
    Settings.LOGGER.log(Level.INFO, "Countries +  : {0}", ((float) (System.currentTimeMillis() - begin)) / 1000f);


    // Studio
    searchMatcher = ImdbPattern.STUDIO.getPattern().matcher(moviePage);
    while (searchMatcher.find()) {
      String studio = searchMatcher.group(1);
      studio = Utils.unEscapeXML(studio, "ISO-8859-1");
      movieInfo.addStudio(studio);
    }
    
    Settings.LOGGER.log(Level.INFO, "Studio +  : {0}", ((float) (System.currentTimeMillis() - begin)) / 1000f);

    // Top 250
    searchMatcher = ImdbPattern.TOP250.getPattern().matcher(moviePage);
    if (searchMatcher.find()) {
      String top250 = searchMatcher.group(1);
      if (top250 != null && Utils.isDigit(top250)) {
        movieInfo.setTop250(top250);
      }
    }
    
    Settings.LOGGER.log(Level.INFO, "Totale time : {0}", ((float) (System.currentTimeMillis() - begin)) / 1000f);
    throw ex;
  }

  @Override
  public MovieInfo getObject() {
    return movieInfo;
  }
}
