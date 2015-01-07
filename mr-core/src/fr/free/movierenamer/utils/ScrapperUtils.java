/*
 * movie-renamer-core
 * Copyright (C) 2012-2014 Nicolas Magré
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
import fr.free.movierenamer.info.MediaInfo.InfoProperty;
import fr.free.movierenamer.info.MediaInfo.MediaProperty;
import fr.free.movierenamer.scrapper.MediaScrapper;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.scrapper.ScrapperManager;
import fr.free.movierenamer.scrapper.impl.movie.AdorocinemaScrapper;
import fr.free.movierenamer.scrapper.impl.movie.AllocineScrapper;
import fr.free.movierenamer.scrapper.impl.movie.BeyazperdeScrapper;
import fr.free.movierenamer.scrapper.impl.movie.FilmstartsScrapper;
import fr.free.movierenamer.scrapper.impl.movie.IMDbScrapper;
import fr.free.movierenamer.scrapper.impl.movie.KinopoiskScrapper;
import fr.free.movierenamer.scrapper.impl.movie.RottenTomatoesScrapper;
import fr.free.movierenamer.scrapper.impl.movie.SensacineScrapper;
import fr.free.movierenamer.scrapper.impl.movie.TMDbScrapper;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.searchinfo.Media.MediaType;
import static fr.free.movierenamer.searchinfo.Media.MediaType.MOVIE;
import static fr.free.movierenamer.searchinfo.Media.MediaType.TVSHOW;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.settings.Settings;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
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

  public static enum AvailableApiIds implements InfoProperty, Serializable {

    IMDB(MediaType.MOVIE, "www.imdb.com/title/%s", "tt"),
    ALLOCINE(MediaType.MOVIE, "www.allocine.fr/film/fichefilm_gen_cfilm=%s.html"),
    THEMOVIEDB(MediaType.MOVIE, "www.themoviedb.org/movie/%s"),
    TVDB(MediaType.TVSHOW, "thetvdb.com/?tab=series&id=%s"),
    TVRAGE(MediaType.TVSHOW, "www.tvrage.com/shows/id-%s"),
    ROTTENTOMATOES(MediaType.MOVIE, null),
    KINOPOISK(MediaType.MOVIE, "www.kinopoisk.ru/film/%s");
    private final MediaType mediaType;
    private final String prefix;
    private final String link;

    private AvailableApiIds(MediaType mediaType, String link) {
      this(mediaType, link, "");
    }

    private AvailableApiIds(MediaType mediaType, String link, String prefix) {
      this.mediaType = mediaType;
      this.link = link;
      this.prefix = prefix;
    }

    public String getPrefix() {
      return prefix;
    }

    public MediaType getMediaType() {
      return mediaType;
    }

    public static List<AvailableApiIds> getAvailableApiIds(MediaType mediaType) {
      List<AvailableApiIds> list = new ArrayList<AvailableApiIds>();

      for (AvailableApiIds apiId : AvailableApiIds.values()) {
        if (apiId.getMediaType() == mediaType) {
          list.add(apiId);
        }
      }

      return list;
    }

    public String getLink() {
      return link;
    }

    @Override
    public boolean isLanguageDepends() {
      return false;
    }
  }

  public static enum InfoQuality {

    AWESOME,
    GREAT,
    AVERAGE,
    POOR
  }

  public static MediaScrapper<?, ?> getScrapperFor(URL url) {
    List<MovieScrapper> scrapers = ScrapperManager.getMovieScrapperList(); // FIXME add tvshow,...
    for (MovieScrapper scraper : scrapers) {
      if (scraper.hasUrlSupported(url)) {
        return scraper;
      }
    }

    return null;
  }

  public static IdInfo idLookup(AvailableApiIds lookupType, IdInfo id, Media searchResult) {

    switch (searchResult.getMediaType()) {
      case MOVIE:
        return movieIdLookup(lookupType, id, (Movie) searchResult);
      case TVSHOW:
      // TODO
    }

    return null;
  }

  public static IdInfo movieIdLookup(AvailableApiIds lookupType, IdInfo id, Movie searchResult) {
    final List<IdInfo> ids = new ArrayList<IdInfo>();
    if (searchResult != null) {
      final IdInfo mid = searchResult.getMediaId();
      if (mid != null) {
        ids.add(mid);
      }
    }

    if (id != null) {
      ids.add(id);
    }

    // ID Lookup
    switch (lookupType) {
      case IMDB:
        return getImdbId(ids, searchResult);
      case THEMOVIEDB:
        return getTmdbId(ids, searchResult);
      case ROTTENTOMATOES:
        return getRottenId(ids, searchResult);
      case ALLOCINE:
        return getAlloId(ids, searchResult);
      case KINOPOISK:
        return getKinopoiskId(ids, searchResult);
    }

    return null;
  }

  private static IdInfo getIdBySearch(MovieScrapper scrapper, Movie searchResult) {

    String search = searchResult.getOriginalName();
    if (search != null) {
      IdInfo id = getIdBySearch(scrapper, search, searchResult.getYear());
      if (id != null) {
        return id;
      }
    }

    search = searchResult.getName();
    return getIdBySearch(scrapper, search, searchResult.getYear());
  }

  private static IdInfo getIdBySearch(MovieScrapper scrapper, String search, int year) {

    try {
      List<Movie> results;
      String searchTitle = StringUtils.normaliseClean(search);
      results = scrapper.search(search);

      for (Movie result : results) {
        if (searchTitle.equals(StringUtils.normaliseClean(result.getOriginalName())) || searchTitle.equals(StringUtils.normaliseClean(result.getName()))) {
          if (year > 1900) {
            if (year == result.getYear() || year == result.getYear() + 1 || year == result.getYear() - 1) {
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
            imdbid = imdbIdLookupByAlloId(id, searchResult);
          }
          break;
        case ROTTENTOMATOES:
          imdbid = RottenTomatoesScrapper.imdbIdLookup(id);
          break;
        case THEMOVIEDB:
          imdbid = TMDbScrapper.imdbIdLookup(id);
          break;
      }

      if (imdbid != null) {
        return imdbid;
      }
    }

    if (searchResult != null) {
      // try to find imdb id with imdb scraper
      imdbid = getIdBySearch(new IMDbScrapper(), searchResult);
    }

    return imdbid;
  }

  private static IdInfo getTmdbId(List<IdInfo> ids, Movie searchResult) {

    for (IdInfo id : ids) {
      if (id.getIdType() == AvailableApiIds.THEMOVIEDB) {
        return id;
      }
    }

    IdInfo imdbid = getImdbId(ids, searchResult);
    if (imdbid != null) {
      return TMDbScrapper.tmdbIDLookUp(imdbid);
    }

    return getIdBySearch(new TMDbScrapper(), searchResult);
  }

  private static IdInfo getRottenId(List<IdInfo> ids, Movie searchResult) {

    for (IdInfo id : ids) {
      if (id.getIdType() == AvailableApiIds.ROTTENTOMATOES) {
        return id;
      }
    }

    IdInfo imdbid = getImdbId(ids, searchResult);
    if (imdbid != null) {
      imdbid = RottenTomatoesScrapper.rottenTomatoesIdLookUp(imdbid);
      if (imdbid != null) {
        return imdbid;
      }
    }

    return getIdBySearch(new RottenTomatoesScrapper(), searchResult);
  }

  private static IdInfo getKinopoiskId(List<IdInfo> ids, Movie searchResult) {
    for (IdInfo id : ids) {
      if (id.getIdType() == AvailableApiIds.THEMOVIEDB) {
        return id;
      }
    }

    return getIdBySearch(new KinopoiskScrapper(), searchResult);
  }

  private static IdInfo getAlloId(List<IdInfo> ids, Movie searchResult) {
    IdInfo alloId = null;
    for (IdInfo id : ids) {
      switch (id.getIdType()) {
        case IMDB:
          if (searchResult != null) {
            alloId = alloIdLookupByImdbId(id, searchResult);
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
      case es:
        scrapper = new SensacineScrapper();
        break;
    }

    if (scrapper != null) {
      // try to find allo id with allogroup scrapper
      alloId = getIdBySearch(scrapper, searchResult);
    }

    return alloId;
  }

  private static IdInfo imdbIdLookupByAlloId(IdInfo alloId, Movie searchResult) {
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

      }

    } catch (Exception ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }

    return null;
  }

  private static IdInfo alloIdLookupByImdbId(IdInfo imdbId, Movie searchResult) {// TODO need to be improved
    try {

      IdInfo id = idInfoGoogleLookup(AvailableApiIds.ALLOCINE, imdbId, searchResult);
      if (id != null) {
        return id;
      }
    } catch (Exception ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }

    return null;
  }

  private static IdInfo idInfoGoogleLookup(AvailableApiIds lookupType, IdInfo info, Movie searchResult) {
    if (searchResult != null) {
      String search = searchResult.getOriginalName();
      if (search != null) {
        IdInfo id = idInfoGoogleLookup(lookupType, info, search);
        if (id != null) {
          return id;
        }
      }

      search = searchResult.getName();
      return idInfoGoogleLookup(lookupType, info, search);
    }

    return null;
  }

  private static IdInfo idInfoGoogleLookup(AvailableApiIds lookupType, IdInfo info, String search) {
    try {
      String json = URIRequest.getDocumentContent(new URL("http://www.google.fr/search?q=" + URIRequest.encode(StringUtils.normaliseClean(search)) + "&bav=on.2,or.r_qf.&fp=1").toURI());
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

  public static void setTitle(Map<MediaProperty, String> mediaProperties, Media media, Node node) {
    String title = media.getName();
    mediaProperties.put(MediaProperty.title, title);

    if (node != null) {
      String content = getValue(XPathUtils.selectString("text()", node));
      if (content != null) {
        mediaProperties.put(MediaProperty.title, content);
      }
    }
  }

  public static <T extends InfoProperty> boolean addValue(Map<T, String> properties, T property, String value) {

    String val = getValue(value);
    if (val == null) {
      return false;
    }

    properties.put(property, val);

    return true;
  }

  public static <T extends InfoProperty> boolean addValue(Map<T, String> properties, T property, Node node) {

    if (node == null) {
      return false;
    }

    return addValue(properties, property, node.getTextContent());
  }

  public static String getValue(String value) {
    if (value == null) {
      return null;
    }

    value = value.trim();

    if (value.isEmpty()) {
      return null;
    }

    return value;
  }

  public static String getValue(Node node) {
    if (node == null) {
      return null;
    }

    return getValue(node.getTextContent());
  }

  public static void getMultipleValues(List<String> values, String xpath, Object docNode) {
    List<Node> nodes = XPathUtils.selectNodes(xpath, docNode);
    String value;
    for (Node node : nodes) {
      value = ScrapperUtils.getValue(node);
      if (value != null) {
        values.add(value);
      }
    }
  }

  private ScrapperUtils() {
    throw new UnsupportedOperationException();
  }
}
