/*
 * movie-renamer-core
 * Copyright (C) 2013-2014 Nicolas Magré
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
package fr.free.movierenamer.scrapper.impl.movie;

import fr.free.movierenamer.scrapper.ScraperThread;
import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.scrapper.MediaScrapper;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.scrapper.Scrapper;
import fr.free.movierenamer.scrapper.ScrapperManager;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.ScrapperUtils;
import fr.free.movierenamer.utils.ScrapperUtils.AvailableApiIds;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class UniversalScrapper : Search movie on several scrapper (based on imdb)
 *
 * @author Nicolas Magré
 */
public class UniversalScrapper extends MovieScrapper {// TODO

  private static final String host = "www.imdb.com";
  private static final String name = "Universal";
  private String query;
  private static final AvailableApiIds supportedId = AvailableApiIds.IMDB;
  private final MovieScrapper defaultScrapper = new IMDbScrapper();

  public UniversalScrapper() {
    super(LocaleUtils.AvailableLanguages.values());
  }

  @Override
  public AvailableApiIds getSupportedId() {
    return supportedId;
  }

  @Override
  protected List<Movie> searchMedia(String query, AvailableLanguages language) throws Exception {
    //URL searchUrl = new URL("http", host, "/find?s=tt&ref_=fn_tt&q=" + URIRequest.encode(query));
    this.query = query;
    return searchMedia((URL) null, language);
  }

  @Override
  protected List<Movie> searchMedia(URL searchUrl, AvailableLanguages language) throws Exception {
    List<Movie> movies;

    MovieScrapper movieScrapper = ScrapperManager.getScrapper(IMDbScrapper.class);
    movieScrapper.setLanguage(language);
    // Search on imdb
    if (searchUrl != null) {
      movies = movieScrapper.search(searchUrl.toString());
    } else {
      movies = movieScrapper.search(query);
    }

    // Search on TMDB
    if (movies.isEmpty()) {
      movieScrapper = ScrapperManager.getScrapper(TMDbScrapper.class);
      movieScrapper.setLanguage(language);
      movies = movieScrapper.search(query);
    }

    if (movies.isEmpty()) {
      List<MovieScrapper> scrappers = getScrappersByQuality(language);

      // Search on any scrapper that support current language
      for (MovieScrapper scrapper : scrappers) {
        if (scrapper instanceof UniversalScrapper || scrapper instanceof TMDbScrapper || scrapper instanceof IMDbScrapper) {
          continue;
        }

        movies = scrapper.search(query);
        if (!movies.isEmpty()) {
          break;
        }
      }
    }

    return movies;
  }

  @Override
  protected MovieInfo fetchMediaInfo(Movie searchResult, AvailableLanguages language) throws Exception {
    final Map<MediaInfo.MediaProperty, String> mediaFields = new EnumMap<MediaInfo.MediaProperty, String>(MediaInfo.MediaProperty.class);
    final Map<MovieInfo.MovieProperty, String> fields = new EnumMap<MovieInfo.MovieProperty, String>(MovieInfo.MovieProperty.class);
    final Map<MovieInfo.MovieMultipleProperty, List<String>> multipleFields = new EnumMap<MovieInfo.MovieMultipleProperty, List<String>>(MovieInfo.MovieMultipleProperty.class);

    final List<MovieScrapper> scrappers = getScrappersByQuality(language);
    final List<IdInfo> idsInfo = new ArrayList<IdInfo>();

    int poolSize = 5;
    ExecutorService service = Executors.newFixedThreadPool(poolSize);
    List<Future<MovieInfo>> futures = new ArrayList<Future<MovieInfo>>();

    Future f = service.submit(new ScraperThread<Movie, MovieInfo>(defaultScrapper, searchResult, language));
    futures.add(f);

    for (MovieScrapper scrapper : scrappers) {
      if (scrapper.getName().equals(getName()) || defaultScrapper.getName().equals(scrapper.getName())) {
        continue;
      }

      f = service.submit(new ScraperThread<Movie, MovieInfo>(scrapper, searchResult, language));
      futures.add(f);
    }

    // Wait for all threads and Merge info
    // TODO
    for (Future<MovieInfo> future : futures) {

      try {
        MovieInfo info = future.get();

        String title = mediaFields.get(MediaInfo.MediaProperty.title);
        if (title == null || title.isEmpty()) {
          mediaFields.put(MediaInfo.MediaProperty.title, info.get(MediaInfo.MediaProperty.title));
        }

        String year = mediaFields.get(MediaInfo.MediaProperty.year);
        if (year == null) {
          mediaFields.put(MediaInfo.MediaProperty.year, info.get(MediaInfo.MediaProperty.year));
        }

        String rating = mediaFields.get(MediaInfo.MediaProperty.rating);
        if (rating == null) {
          mediaFields.put(MediaInfo.MediaProperty.rating, info.get(MediaInfo.MediaProperty.rating));
        }

        for (MovieInfo.MovieProperty property : MovieInfo.MovieProperty.values()) {
          String cvalue = fields.get(property);
          String nvalue = info.get(property);
          if ((cvalue == null || cvalue.equals("")) && nvalue != null && !nvalue.equals("")) {
            fields.put(property, nvalue);
          }
        }

        for (MovieInfo.MovieMultipleProperty property : MovieInfo.MovieMultipleProperty.values()) {
          List<String> cvalue = multipleFields.get(property);
          List<String> nvalue = info.get(property);
          if ((cvalue == null || cvalue.isEmpty()) && nvalue != null && !nvalue.isEmpty()) {
            multipleFields.put(property, nvalue);
          }
        }
      } catch (Exception ex) {
        Settings.LOGGER.warning(ex.getMessage());
      }
    }

    service.shutdownNow();

    return new MovieInfo(mediaFields, idsInfo, fields, multipleFields);
  }

  @Override
  protected List<CastingInfo> fetchCastingInfo(Movie search, AvailableLanguages language) throws Exception {// TODO
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected AvailableLanguages getDefaultLanguage() {
    return AvailableLanguages.en;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  protected String getHost() {
    return host;
  }

  @Override
  public ScrapperUtils.InfoQuality getInfoQuality() {
    return ScrapperUtils.InfoQuality.AVERAGE;
  }

  private List<MovieScrapper> getScrappersByQuality(AvailableLanguages lang) {
    List<MovieScrapper> scrappers = ScrapperManager.getMovieScrapperList(lang);
    Collections.sort(scrappers, new Comparator<MovieScrapper>() {

      @Override
      public int compare(MovieScrapper o1, MovieScrapper o2) {
        return o1.getInfoQuality().ordinal() - o2.getInfoQuality().ordinal();
      }
    });

    return scrappers;
  }
}
