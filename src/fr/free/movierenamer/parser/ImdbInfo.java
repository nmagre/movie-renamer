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
package fr.free.movierenamer.parser.xml;

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
  private final ImdbSearch.ImdbLanguage ilang = config.movieScrapperFR ? ImdbSearch.ImdbLanguage.FRENCH : ImdbSearch.ImdbLanguage.ENGLISH;

  public enum ImdbLanguage {// TODO ,imdb.it, imdb.es, ....

    ENGLISH,
    FRENCH;
  }

  public enum ImdbPattern {// TODO ,imdb.it, imdb.es, ....

    IMDBMOVIETITLE("<title>(.* \\(.*\\d+.*\\).*)</title>"),
    IMDBMOVIETHUMB("title=\".*\" src=.http://ia.media-imdb.com/images/.*>"),
    IMDBMOVIEORIGTITLE("<span class=\"title-extra\">.*</i></span>", "<h5>Alias:</h5>.*\\(titre original\\)"),
    IMDBMOVIERUNTIME("<h5>(Runtime|Dur&#xE9;e):</h5><div class=\".*\">\\d+ min"),
    IMDBMOVIERATING("<b>.[\\.,]./10</b>"),
    IMDBMOVIEVOTES("<a href=\".*\" class=\".*\">.* votes</a>"),
    IMDBMOVIEDIRECTOR("src='/rg/directorlist/position-\\d+/images/b.gif.link=name/nm\\d+/';\">.*</a>"),
    IMDBMOVIEWRITER("src='/rg/writerlist/position-\\d/images/b.gif.link=name/nm\\d+/';\".*</a>"),
    IMDBMOVIEGENRE("<a href=\"/Sections/Genres/.*/\">.*keywords", "<h5>Genre:</h5>\n<div class=.info-content.>\n.*\n</div>"),
    IMDBMOVIETAGLINE("<div class=\"info-content\">\n.*<a class=\".*\" href=\"/title/tt\\d+/taglines\""),
    IMDBMOVIEPLOT("<div class=.info-content.>\n.*(\n?)<a class=..*. href=./title/tt\\d+/plotsummary."),
    IMDBMOVIECAST("<h3>((Cast)|(Ensemble))</h3>.*"),
    IMDBMOVIEACTOR("\"><img src=\".*/rg/castlist/position-\\d+/images/b.gif.link=/name/nm\\d+/';\">.*</td>"),
    IMDBMOVIECOUNTRY("<h5>((Country:)|(Pays:))</h5><div class=\"info-content\">(.*)<div class=\"info\""),
    IMDBMOVIESTUDIO("<h5>((Company:)|(Soci&#xE9;t&#xE9;:))</h5><div class=..*.><a href=..*.>(.*)</a><a"),
    IMDBTOP250("<a href=./chart/top\\?tt\\d{7}.>Top 250: #(\\d{1,3})</a>");
    private Pattern[] pattern;

    private ImdbPattern(String... patterns) {
      pattern = new Pattern[patterns.length];
      for (int i = 0; i < patterns.length; i++) {
        pattern[i] = Pattern.compile(patterns[i]);
      }
    }

    public Pattern getPattern(ImdbSearch.ImdbLanguage lang) {
      if (pattern.length == 1) {
        return pattern[0];
      }
      return pattern[lang.ordinal()];
    }

    public String getPatternString(ImdbSearch.ImdbLanguage lang) {
      if (pattern.length == 1) {
        return pattern[0].toString();
      }
      return pattern[lang.ordinal()].toString();
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
    // Title + Year
    Matcher searchMatcher = ImdbPattern.IMDBMOVIETITLE.getPattern(ilang).matcher(moviePage);
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

    // Thumb
    searchMatcher = ImdbPattern.IMDBMOVIETHUMB.getPattern(ilang).matcher(moviePage);
    if (searchMatcher.find()) {
      String imdbThumb = searchMatcher.group();
      imdbThumb = imdbThumb.substring(imdbThumb.lastIndexOf("src=") + 5, imdbThumb.lastIndexOf("\""));
      MediaImage movieThumb = new MediaImage(0, MediaImage.MediaImageType.THUMB);
      movieThumb.setThumbUrl(imdbThumb);
      movieThumb.setMidUrl(imdbThumb);
      movieInfo.addThumb(movieThumb);
    }

    // Original Title
    searchMatcher = ImdbPattern.IMDBMOVIEORIGTITLE.getPattern(ilang).matcher(moviePage);
    if (searchMatcher.find()) {
      String origTitle = searchMatcher.group().replace("&#x22;", "\"");
      switch (ilang) {
        case FRENCH:
          origTitle = origTitle.substring(0, origTitle.lastIndexOf("\""));
          origTitle = origTitle.substring(origTitle.lastIndexOf(">"));
          origTitle = origTitle.substring(origTitle.lastIndexOf("\"") + 1);
          break;
        case ENGLISH:
          origTitle = origTitle.substring(origTitle.indexOf(">") + 1, origTitle.lastIndexOf("<"));
          origTitle = origTitle.replaceAll("\\(.*\\)", "").replaceAll("<.*>", "");
          break;
        default:
          break;
      }
      movieInfo.setOrigTitle(Utils.unEscapeXML(origTitle, "ISO-8859-1"));
    } else {
      movieInfo.setOrigTitle(movieInfo.getTitle());
    }

    // Runtime
    searchMatcher = ImdbPattern.IMDBMOVIERUNTIME.getPattern(ilang).matcher(moviePage);
    if (searchMatcher.find()) {
      String runtime = searchMatcher.group();
      runtime = runtime.substring(runtime.lastIndexOf(">") + 1, runtime.length() - 4);
      if (Utils.isDigit(runtime)) {
        movieInfo.setRuntime(runtime);
      }
    }

    // Rating
    searchMatcher = ImdbPattern.IMDBMOVIERATING.getPattern(ilang).matcher(moviePage);
    if (searchMatcher.find()) {
      String rating = searchMatcher.group();
      rating = rating.replaceAll("<b>", Utils.EMPTY).replaceAll("</b>", "").split("/")[0];
      movieInfo.setRating(rating);
    }

    // Votes
    searchMatcher = ImdbPattern.IMDBMOVIEVOTES.getPattern(ilang).matcher(moviePage);
    if (searchMatcher.find()) {
      String votes = searchMatcher.group();
      votes = votes.substring(votes.lastIndexOf("\"") + 2, votes.lastIndexOf(" votes"));
      movieInfo.setVotes(votes);
    }

    // Directors
    searchMatcher = ImdbPattern.IMDBMOVIEDIRECTOR.getPattern(ilang).matcher(moviePage);
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
      movieInfo.addPerson(dir);
    }

    // Writers
    searchMatcher = ImdbPattern.IMDBMOVIEWRITER.getPattern(ilang).matcher(moviePage);
    while (searchMatcher.find()) {
      String writer = searchMatcher.group();
      writer = writer.substring(writer.indexOf(">") + 1, writer.lastIndexOf("<"));
      movieInfo.addPerson(new MediaPerson(Utils.unEscapeXML(writer, "ISO-8859-1"), "", MediaPerson.WRITER));
    }

    // TagLine
    searchMatcher = ImdbPattern.IMDBMOVIETAGLINE.getPattern(ilang).matcher(moviePage);
    if (searchMatcher.find()) {
      String tagline = searchMatcher.group();
      tagline = tagline.substring(tagline.indexOf("\n") + 1, tagline.indexOf("<a") - 1);
      movieInfo.setTagline(Utils.unEscapeXML(tagline, "ISO-8859-1"));
    }

    // Plot
    searchMatcher = ImdbPattern.IMDBMOVIEPLOT.getPattern(ilang).matcher(moviePage);
    if (searchMatcher.find()) {
      String plot = searchMatcher.group();
      plot = plot.substring(plot.indexOf("\n") + 1, plot.indexOf("<a") - 1);
      movieInfo.setSynopsis(Utils.unEscapeXML(plot, "ISO-8859-1"));
    }

    // Genres
    searchMatcher = ImdbPattern.IMDBMOVIEGENRE.getPattern(ilang).matcher(moviePage);
    if (searchMatcher.find()) {
      String found = "";
      switch (ilang) {
        case FRENCH:
          found = searchMatcher.group().split("\n")[2];
          break;
        case ENGLISH:
          found = searchMatcher.group();
          break;
        default:
          break;
      }

      String[] genres = found.split("\\|");
      for (int i = 0; i < genres.length; i++) {
        String genre = "";
        switch (ilang) {
          case FRENCH:
            genre = genres[i].trim();
            break;
          case ENGLISH:
            genre = genres[i].substring(genres[i].indexOf(">") + 1, genres[i].indexOf("</a>"));
            break;
          default:
            break;
        }

        if (!genre.equals("")) {
          movieInfo.addGenre(Utils.unEscapeXML(genre, "ISO-8859-1"));
        }
      }
    }

    // Actors
    searchMatcher = ImdbPattern.IMDBMOVIECAST.getPattern(ilang).matcher(moviePage);
    if (searchMatcher.find()) {
      String[] actors = searchMatcher.group().split("</tr>");
      for (int i = 0; i < actors.length; i++) {
        Matcher matcher2 = ImdbPattern.IMDBMOVIEACTOR.getPattern(ilang).matcher(actors[i]);
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

    // Countries
    searchMatcher = ImdbPattern.IMDBMOVIECOUNTRY.getPattern(ilang).matcher(moviePage);// FIXME , find "USA | Malaysia:18PL" instead of "USA" (E.g :bienvenue a zombiland with imdb.com)
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

    // Studio
    searchMatcher = ImdbPattern.IMDBMOVIESTUDIO.getPattern(ilang).matcher(moviePage);
    while (searchMatcher.find()) {
      String studio = searchMatcher.group();
      studio = studio.substring(studio.indexOf("<a"), studio.lastIndexOf("</a>"));
      studio = studio.substring(studio.lastIndexOf(">") + 1);
      studio = Utils.unEscapeXML(studio, "ISO-8859-1");
      movieInfo.addStudio(studio);
    }

    // Top 250
    searchMatcher = ImdbPattern.IMDBTOP250.getPattern(ilang).matcher(moviePage);
    if (searchMatcher.find()) {
      String top250 = searchMatcher.group(1);
      if (top250 != null && Utils.isDigit(top250)) {
        movieInfo.setTop250(top250);
      }
    }
    throw ex;
  }

  @Override
  public MovieInfo getObject() {
    return movieInfo;
  }
}
