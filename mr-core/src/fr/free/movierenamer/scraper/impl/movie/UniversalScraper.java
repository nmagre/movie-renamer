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
package fr.free.movierenamer.scraper.impl.movie;

import fr.free.movierenamer.scraper.ScraperThread;
import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.MediaInfo.InfoProperty;
import fr.free.movierenamer.info.MediaInfo.MediaInfoProperty;
import fr.free.movierenamer.info.MediaInfo.MediaProperty;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.MovieInfo.MovieMultipleProperty;
import fr.free.movierenamer.info.MovieInfo.MovieProperty;
import fr.free.movierenamer.info.VideoInfo.VideoProperty;
import fr.free.movierenamer.scraper.MediaScraper;
import fr.free.movierenamer.scraper.MovieScraper;
import fr.free.movierenamer.scraper.ScraperManager;
import fr.free.movierenamer.scraper.ScraperOptions;
import fr.free.movierenamer.searchinfo.Media.MediaType;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.settings.Settings.SettingsProperty;
import fr.free.movierenamer.utils.ClassUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.ScraperUtils.AvailableApiIds;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

/**
 * Class UniversalScraper : Search movie on several scraper
 *
 * @author Nicolas Magré
 */
public class UniversalScraper extends MovieScraper {

  private static final int POOL_SIZE = 5;
  private static final String host = "www.imdb.com";
  private static final String name = "Universal";
  private static final AvailableApiIds supportedId = AvailableApiIds.IMDB;
  private final MovieScraper searchScraper;
  private String query;
  private static final List<ScraperOptions> options = Arrays.asList(new ScraperOptions[]{
    new ScraperOptions(SettingsProperty.universalSearchScraper),
    new ScraperOptions(SettingsProperty.universalSynopsys, true),
    new ScraperOptions(SettingsProperty.universalCasting),
    new ScraperOptions(SettingsProperty.universalRating),
    new ScraperOptions(SettingsProperty.universalGenre, true),
    new ScraperOptions(SettingsProperty.universalCountry, true)
  });
  private final static Map<InfoProperty, SettingsProperty> scraperOptions = new HashMap<>();

  static {
    scraperOptions.put(MovieProperty.overview, SettingsProperty.universalSynopsys);
    scraperOptions.put(MediaProperty.rating, SettingsProperty.universalRating);
    scraperOptions.put(MovieMultipleProperty.genres, SettingsProperty.universalGenre);
    scraperOptions.put(MovieMultipleProperty.countries, SettingsProperty.universalCountry);
  }

  public UniversalScraper() {
    super(LocaleUtils.AvailableLanguages.values());
    searchScraper = ScraperManager.getScraper(settings.getUniversalSearchMovieScraper());
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
  protected AvailableLanguages getDefaultLanguage() {
    return AvailableLanguages.en;
  }

  @Override
  public AvailableApiIds getSupportedId() {
    return supportedId;
  }

  @Override
  public List<ScraperOptions> getScraperOptions() {
    return options;
  }

  @Override
  public IdInfo getIdfromURL(URL url) {
    return searchScraper.getIdfromURL(url);
  }

  @Override
  public URL getURL(IdInfo id) {
    return searchScraper.getURL(id);
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

    searchScraper.setLanguage(language);

    if (searchUrl != null) {
      movies = searchScraper.search(searchUrl.toString(), 0);
    } else {
      movies = searchScraper.search(query, 0);
    }

    if (movies.isEmpty()) {
      List<MediaScraper> scrapers = ScraperManager.getScrapersByQuality(MediaType.MOVIE, language);

      // Search on any scraper that support current language
      for (MediaScraper scraper : scrapers) {// FIXME seems to be weird. Need to be check
        if (scraper instanceof UniversalScraper || scraper instanceof TMDbScraper || scraper instanceof IMDbScraper) {
          continue;
        }

        movies = scraper.search(query, 0);
        if (!movies.isEmpty()) {
          break;
        }
      }
    }

    return movies;
  }

  @Override
  protected MovieInfo fetchMediaInfo(final Movie searchResult, IdInfo id, AvailableLanguages language) throws Exception {
    Map<MediaInfoProperty, String> defaultInfo = new HashMap<>();
    Map<MovieMultipleProperty, List<String>> defaultMultipleInfo = new EnumMap<>(MovieMultipleProperty.class);
    List<CastingInfo> defaultCastings = new ArrayList<>();

    Map<MediaInfoProperty, String> info = new HashMap<>();
    Map<MovieMultipleProperty, List<String>> multipleInfo = new EnumMap<>(MovieMultipleProperty.class);

    List<MediaScraper> scrapers = ScraperManager.getScrapersByQuality(MediaType.MOVIE, language);
    List<IdInfo> idsInfo = new ArrayList<>();
    Map<Future<MovieInfo>, Class<?>> futureProvider = new HashMap<>();
    List<CastingInfo> castings = new ArrayList<>();

    addIdInfo(idsInfo, id);

    // Launch each scraper in his own thread
    int nbthread = 1;
    ExecutorService service = Executors.newFixedThreadPool(POOL_SIZE);
    CompletionService<MovieInfo> pool = new ExecutorCompletionService<>(service);

    futureProvider.put(pool.submit(new ScraperThread<>(searchScraper, searchResult)), searchScraper.getClass());

    for (MediaScraper scraper : scrapers) {
      // We do not add current scraper (Universal) and search Scraper
      if (scraper.getName().equals(getName()) || searchScraper.getName().equals(scraper.getName())) {
        continue;
      }

      futureProvider.put(pool.submit(new ScraperThread<>((MovieScraper) scraper, searchResult)), scraper.getClass());
      nbthread++;
    }

    // Wait for all threads
    for (int i = 0; i < nbthread; i++) {

      try {
        Future<MovieInfo> future = pool.take();
        MovieInfo movieInfo = future.get();

        if (movieInfo == null) {// FIXME should not be null
          Settings.LOGGER.log(Level.SEVERE, String.format("INFO IS NULL : %s", searchScraper.getClass()));
          continue;
        }

        for (IdInfo idInf : movieInfo.getIdsInfo()) {
          addIdInfo(idsInfo, idInf);
        }

        // Store search scraper infos as default values
        Class<?> provider = futureProvider.get(future);
        boolean asDefault = provider.equals(searchScraper.getClass());
        setInfoValue(defaultInfo, info, movieInfo, provider, asDefault);
        setMultipleFieldsValue(defaultMultipleInfo, multipleInfo, movieInfo, provider, asDefault);

        // Set casting
        List<CastingInfo> casting = movieInfo.getCasting();
        setCasting(defaultCastings, castings, casting, provider);

      } catch (Exception ex) {
        Settings.LOGGER.warning(ClassUtils.getStackTrace(ex));
      }
    }

    // Merge
    mergeInfoValue(defaultInfo, info);

    List<String> values, nvalues;
    for (MovieMultipleProperty property : MovieMultipleProperty.values()) {
      values = defaultMultipleInfo.get(property);
      nvalues = multipleInfo.get(property);
      if ((values == null || values.isEmpty()) && nvalues != null && !nvalues.isEmpty()) {
        defaultMultipleInfo.put(property, nvalues);
      }
    }

    if (defaultCastings.isEmpty()) {
      defaultCastings.addAll(castings);
    }

    service.shutdownNow();

    return new MovieInfo(defaultInfo, defaultMultipleInfo, idsInfo);
  }

  private void setInfoValue(Map<MediaInfoProperty, String> defaultInfo, Map<MediaInfoProperty, String> info, MovieInfo movieInfo, Class<?> provider, boolean asDefault) {
    setInfo(defaultInfo, info, movieInfo, MediaProperty.class, provider, asDefault);
    setInfo(defaultInfo, info, movieInfo, VideoProperty.class, provider, asDefault);
    setInfo(defaultInfo, info, movieInfo, MovieProperty.class, provider, asDefault);
  }

  private <T extends Enum<T> & MediaInfoProperty> void setInfo(Map<MediaInfoProperty, String> defaultInfo, Map<MediaInfoProperty, String> info,
    MovieInfo movieInfo, Class<T> clazz, Class<?> provider, boolean asDefault) {

    SettingsProperty sprop;
    String cvalue, nvalue;
    for (T property : clazz.getEnumConstants()) {
      cvalue = info.get(property);
      nvalue = movieInfo.get(property);

      sprop = scraperOptions.get(property);
      if (sprop != null && (asDefault || settings.getMovieScraperOptionClass(sprop).equals(provider))) {
        if (asDefault && defaultInfo.get(property) != null) {
          continue;
        }

        defaultInfo.put(property, nvalue);
        continue;
      }

      if ((cvalue == null || cvalue.equals("")) && nvalue != null && !nvalue.equals("")) {
        info.put(property, nvalue);
      }
    }
  }

  private void setMultipleFieldsValue(Map<MovieMultipleProperty, List<String>> defaultInfo,
    Map<MovieMultipleProperty, List<String>> info, MovieInfo movieInfo, Class<?> provider, boolean asDefault) {

    SettingsProperty sprop;
    List<String> cvalue, nvalue;
    for (MovieMultipleProperty property : MovieMultipleProperty.values()) {
      cvalue = info.get(property);
      nvalue = movieInfo.get(property);

      sprop = scraperOptions.get(property);
      if (sprop != null && (asDefault || settings.getMovieScraperOptionClass(sprop).equals(provider))) {
        if (asDefault && defaultInfo.get(property) != null) {
          continue;
        }

        defaultInfo.put(property, nvalue);
        continue;
      }

      if ((cvalue == null || cvalue.isEmpty()) && nvalue != null && !nvalue.isEmpty()) {
        info.put(property, nvalue);
      }
    }
  }

  private void mergeInfoValue(Map<MediaInfoProperty, String> defaultInfo, Map<MediaInfoProperty, String> info) {
    mergeInfo(defaultInfo, info, MediaProperty.class);
    mergeInfo(defaultInfo, info, VideoProperty.class);
    mergeInfo(defaultInfo, info, MovieProperty.class);
  }

  private <T extends Enum<T> & MediaInfoProperty> void mergeInfo(Map<MediaInfoProperty, String> defaultInfo, Map<MediaInfoProperty, String> info, Class<T> clazz) {
    String value, nvalue;
    for (T property : clazz.getEnumConstants()) {
      value = defaultInfo.get(property);
      nvalue = info.get(property);
      if ((value == null || value.isEmpty()) && nvalue != null && !nvalue.isEmpty()) {
        defaultInfo.put(property, nvalue);
      }
    }
  }

  private void setCasting(List<CastingInfo> defaultCastings, List<CastingInfo> castings, List<CastingInfo> infoCasting, Class<?> provider) {
    if (infoCasting != null && !infoCasting.isEmpty()) {
      if (settings.getMovieScraperOptionClass(SettingsProperty.universalCasting).equals(provider)) {
        defaultCastings.addAll(infoCasting);
      } else {
        castings.addAll(infoCasting);
      }
    }
  }

  private void addIdInfo(List<IdInfo> idInfos, IdInfo idInfo) {

    if (idInfo == null) {
      return;
    }

    boolean alreadyIn = false;
    for (IdInfo id : idInfos) {
      if (id.getIdType() == idInfo.getIdType()) {
        alreadyIn = true;
        break;
      }
    }

    if (!alreadyIn) {
      idInfos.add(idInfo);
    }
  }

  @Override
  protected List<CastingInfo> fetchCastingInfo(Movie search, IdInfo id, AvailableLanguages language) throws Exception {
    // Casting was fetch in fetchMediaInfo
    return null;
  }

  @Override
  public InfoQuality getQuality() {
    return InfoQuality.AWESOME;
  }

}
