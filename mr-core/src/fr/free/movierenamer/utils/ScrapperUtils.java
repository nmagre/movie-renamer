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
package fr.free.movierenamer.utils;

import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.scrapper.impl.movie.AllocineScrapper;
import fr.free.movierenamer.scrapper.impl.movie.IMDbScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.settings.Settings;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Class ScrapperUtils
 *
 * @author Nicolas Magré
 */
public final class ScrapperUtils {

  private static final String imdbIdLookupHost = "passion-xbmc.org";
  private static final Pattern imdbIdPattern = Pattern.compile("tt(\\d+{7})");

  public static enum TmdbImageSize {

    backdrop("w300", "w780"),
    poster("w92", "w185"),
    cast("w45", "w185");
    private String small;
    private String medium;
    private String big;

    private TmdbImageSize(String small, String medium) {
      this.small = small;
      this.medium = medium;
      this.big = "original";
    }

    public String getSmall() {
      return small;
    }

    public String getMedium() {
      return medium;
    }

    public String getBig() {
      return big;
    }
  }

  public static enum AvailableApiIds {

    IMDB("tt"),
    ALLOCINE(),
    TMDB(),
    TVDB(),
    TVRAGE;
    private String prefix;

    private AvailableApiIds() {
      this("");
    }

    private AvailableApiIds(String prefix) {
      this.prefix = prefix;
    }

    public String getPrefix() {
      return prefix;
    }
  }

  public static IdInfo alloIdLookup(Movie searchResult) {

    IdInfo idinfo = searchResult.getId();

    if (idinfo.getIdType() == AvailableApiIds.ALLOCINE) {
      return idinfo;
    }

    if (searchResult.getOriginalTitle() == null || searchResult.getOriginalTitle().equals("")) {
      return null;
    }


    String searchTitle = StringUtils.normaliseClean(searchResult.getOriginalTitle());

    try {
      AllocineScrapper alloScrapper = new AllocineScrapper();
      List<Movie> results = alloScrapper.search(searchTitle);
      for (Movie result : results) {

        if (searchTitle.equalsIgnoreCase(StringUtils.normaliseClean(result.getOriginalTitle())) && searchResult.getYear() == result.getYear()) {
          if (searchResult.getYear() > 1900) {
            return result.getId();
          }
        }
      }
    } catch (Exception ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }

    return null;

  }

  public static IdInfo imdbIdLookup(IdInfo alloId, Movie searchResult) {

    if (alloId.getIdType() != AvailableApiIds.ALLOCINE) {
      return null;
    }

    try {
      Document dom = URIRequest.getHtmlDocument(new URL("http", imdbIdLookupHost, "/scraper/index2.php?Page=ViewMovie&ID=" + alloId.getId()).toURI());
      try {
        String id = XPathUtils.getAttribute("href", XPathUtils.selectNode("//A[contains(@href, 'imdb.com/')]", dom));

        if (id != null && !id.equals("")) {
          Matcher matcher = imdbIdPattern.matcher(id);
          if (matcher.find()) {
            return new IdInfo(Integer.parseInt(matcher.group(1)), ScrapperUtils.AvailableApiIds.IMDB);
          }
        }
      } catch (NullPointerException Ex) {
        // Imdb id not found
      }

      if (searchResult != null) {
        String searchTitle = StringUtils.normaliseClean(searchResult.getOriginalTitle());
        IMDbScrapper imdbScrapper = new IMDbScrapper();
        List<Movie> results = imdbScrapper.search(searchTitle);
        for (Movie result : results) {

          if (searchTitle.equalsIgnoreCase(StringUtils.normaliseClean(result.getOriginalTitle())) && searchResult.getYear() == result.getYear()) {
            if (searchResult.getYear() > 1900) {
              return result.getId();
            }
          }
        }
      }

    } catch (URISyntaxException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (SAXException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (Exception ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }

    return null;
  }

  private ScrapperUtils() {
    throw new UnsupportedOperationException();
  }
}
