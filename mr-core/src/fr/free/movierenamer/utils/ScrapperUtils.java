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
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.scrapper.impl.movie.AdorocinemaScrapper;
import fr.free.movierenamer.scrapper.impl.movie.AllocineScrapper;
import fr.free.movierenamer.scrapper.impl.movie.BeyazperdeScrapper;
import fr.free.movierenamer.scrapper.impl.movie.FilmstartsScrapper;
import fr.free.movierenamer.scrapper.impl.movie.IMDbScrapper;
import fr.free.movierenamer.scrapper.impl.movie.RottenTomatoes;
import fr.free.movierenamer.scrapper.impl.movie.ScreenRushScrapper;
import fr.free.movierenamer.scrapper.impl.movie.SensacineScrapper;
import fr.free.movierenamer.scrapper.impl.movie.TMDbScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.settings.Settings;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
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
  private static final Pattern imdbIdUrlPattern = Pattern.compile("www\\.imdb\\.com\\/title\\/tt(\\d+{7})");
  private static final Pattern alloIdUrlPattern = Pattern.compile("www\\.allocine\\.fr\\/film\\/fichefilm_gen_cfilm\\=(\\d+)\\.html");

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

  public static enum AvailableApiIds implements Serializable {

    IMDB("tt"),
    ALLOCINE(),
    TMDB(),
    TVDB(),
    TVRAGE,
    ROTTEN,
    KINOPOISK;
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

  public static enum InfoQuality {

    AWESOME,
    GREAT,
    AVERAGE,
    POOR
  }

  public static IdInfo imdbIdLookup(IdInfo id, Movie searchResult) {

    if (searchResult != null) {
      if (searchResult.getImdbId() != null) {
        return searchResult.getImdbId();
      }
    }

    return idLookup(AvailableApiIds.IMDB, id, searchResult);
  }

  public static IdInfo alloIdLookup(IdInfo id, Movie searchResult) {
    return idLookup(AvailableApiIds.ALLOCINE, id, searchResult);
  }

  private static IdInfo idLookup(AvailableApiIds lookupType, IdInfo id, Movie searchResult) {
    List<IdInfo> ids = new ArrayList<IdInfo>();
    if (searchResult != null) {
      IdInfo mid = searchResult.getMediaId();
      if (mid != null) {
        ids.add(mid);
      }
    }

    if (id != null) {
      ids.add(id);
    }

    switch (lookupType) {
      case IMDB:
        return getImdbId(ids, searchResult);
      case ALLOCINE:
        return getAlloId(ids, searchResult);
    }

    return null;
  }

  private static IdInfo getIdBySearch(MovieScrapper scrapper, Movie searchResult) {
    try {
      String searchTitle = StringUtils.normaliseClean(searchResult.getOriginalTitle());
      List<Movie> results;
      results = scrapper.search(searchTitle);
      for (Movie result : results) {
        if (searchTitle.equalsIgnoreCase(StringUtils.normaliseClean(result.getOriginalTitle())) || searchTitle.equalsIgnoreCase(StringUtils.normaliseClean(result.getName()))) {
          if (searchResult.getYear() > 1900) {
            if (searchResult.getYear() == result.getYear()) {
              return result.getImdbId() != null ? result.getImdbId() : result.getMediaId();
            }
          }
        }
      }
    } catch (Exception ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }

    return null;
  }

  private static IdInfo getImdbId(List<IdInfo> ids, Movie searchResult) {
    IdInfo imdbid = null;
    for (IdInfo id : ids) {
      switch (id.getIdType()) {
        case IMDB:
          return id;
        case ALLOCINE:
          if (searchResult != null) {
            imdbid = imdbIdLookupByAlloId(id, searchResult.getOriginalTitle());
          }
          break;
        case ROTTEN:
          imdbid = RottenTomatoes.imdbIdLookup(id);
          break;
        case TMDB:
          imdbid = TMDbScrapper.imdbIdLookup(id);
          break;
      }

      if (imdbid != null) {
        return imdbid;
      }
    }

    if (searchResult != null) {
      // ty to find imdb id with imdb scrapper
      imdbid = getIdBySearch(new IMDbScrapper(), searchResult);
    }

    return imdbid;
  }

  private static IdInfo getAlloId(List<IdInfo> ids, Movie searchResult) {
    IdInfo alloId = null;
    for (IdInfo id : ids) {
      switch (id.getIdType()) {
        case IMDB:
          if (searchResult != null) {
            alloId = alloIdLookupByImdbId(id, searchResult.getOriginalTitle());
          }
          break;
        case ALLOCINE:
          return id;
      }

      if (alloId != null) {
        return alloId;
      }
    }

    if (searchResult != null) {
      alloId = searchAlloId(searchResult);
    }

    return alloId;
  }

  private static IdInfo searchAlloId(Movie searchResult) {
    IdInfo alloId = null;
    MovieScrapper scrapper = null;
    switch (Settings.getInstance().getSearchScrapperLang()) {
      case pt:
        scrapper = new AdorocinemaScrapper();
        break;
      case fr:
        scrapper = new AllocineScrapper();
        break;
      case tr:
        scrapper = new BeyazperdeScrapper();
        break;
      case de:
        scrapper = new FilmstartsScrapper();
        break;
      case en:
        scrapper = new ScreenRushScrapper();
        break;
      case es:
        scrapper = new SensacineScrapper();
    }

    if (scrapper != null) {
      // ty to find allo id with allogroup scrapper
      alloId = getIdBySearch(scrapper, searchResult);
    }

    return alloId;
  }

  private static IdInfo imdbIdLookupByAlloId(IdInfo alloId, String title) {
    IdInfo imdbId = null;
    try {
      if (alloId.getIdType() == AvailableApiIds.ALLOCINE) {

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

        if (title != null && !title.isEmpty()) {
          imdbId = idInfoGoogleLookup(AvailableApiIds.IMDB, alloId, title);
          if (imdbId != null) {
            return imdbId;
          }
        }
      }

    } catch (Exception ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }

    return imdbId;
  }

  private static IdInfo alloIdLookupByImdbId(IdInfo imdbId, String title) {// TODO need to be improved
    try {
      IdInfo id = idInfoGoogleLookup(AvailableApiIds.ALLOCINE, imdbId, title);
      if (id != null) {
        return id;
      }
    } catch (Exception ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }

    return null;
  }

  private static IdInfo idInfoGoogleLookup(AvailableApiIds lookupType, IdInfo info, String title) {
    try {
      String json = URIRequest.getDocumentContent(new URL("http://www.google.fr/search?q=" + URIRequest.encode(StringUtils.normaliseClean(title)) + "&bav=on.2,or.r_qf.&fp=1").toURI());
      Pattern pattern = alloIdUrlPattern;
      Pattern lookupPattern = imdbIdUrlPattern;
      switch (lookupType) {
        case ALLOCINE:
          pattern = imdbIdUrlPattern;
          lookupPattern = alloIdUrlPattern;
          break;
        case IMDB:
          break;
        default:
          throw new UnsupportedOperationException(lookupType.name() + " is not supported by IdInfoGoogleLookup");
      }

      Matcher matcher = pattern.matcher(json);
      if (matcher.find()) {
        String id = matcher.group(1);
        if (!NumberUtils.isDigit(id)) {
          return null;
        }

        matcher = lookupPattern.matcher(json);
        if (matcher.find()) {
          String lid = matcher.group(1);
          if (!NumberUtils.isDigit(lid)) {
            return null;
          }

          switch (lookupType) {
            case ALLOCINE:
              if (info == null || info.getId() == Integer.parseInt(id)) {
                return new IdInfo(Integer.parseInt(lid), AvailableApiIds.ALLOCINE);
              }

            case IMDB:
              if (info == null || info.getId() == Integer.parseInt(id)) {
                return new IdInfo(Integer.parseInt(id), "tt" + lid, AvailableApiIds.IMDB);
              }
          }

        }
      }
    } catch (SAXException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (URISyntaxException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }

    return null;
  }

  private ScrapperUtils() {
    throw new UnsupportedOperationException();
  }
}
