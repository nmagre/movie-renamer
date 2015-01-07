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
import fr.free.movierenamer.info.MediaInfo.InfoProperty;
import fr.free.movierenamer.info.MediaInfo.MediaProperty;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.MovieInfo.MovieMultipleProperty;
import fr.free.movierenamer.info.MovieInfo.MovieProperty;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.scrapper.ScrapperManager;
import fr.free.movierenamer.scrapper.ScrapperOptions;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.settings.Settings.SettingsProperty;
import fr.free.movierenamer.utils.ClassUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.ScrapperUtils;
import fr.free.movierenamer.utils.ScrapperUtils.AvailableApiIds;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class UniversalScrapper : Search movie on several scrapper
 *
 * @author Nicolas Magré
 */
public class UniversalScrapper extends MovieScrapper {

  private static final int POOL_SIZE = 5;
  private static final String host = "www.imdb.com";
  private static final String name = "Universal";
  private static final AvailableApiIds supportedId = AvailableApiIds.IMDB;
  private final MovieScrapper searchScraper;
  private String query;
  private static final List<ScrapperOptions> options = Arrays.asList(new ScrapperOptions[]{
    new ScrapperOptions(SettingsProperty.universalSearchScraper),
    new ScrapperOptions(SettingsProperty.universalSynopsys, true),
    new ScrapperOptions(SettingsProperty.universalCasting),
    new ScrapperOptions(SettingsProperty.universalRating),
    new ScrapperOptions(SettingsProperty.universalGenre, true),
    new ScrapperOptions(SettingsProperty.universalCountry, true)
  });
  private final static Map<InfoProperty, SettingsProperty> scraperOptions = new HashMap<InfoProperty, SettingsProperty>();

  static {
    scraperOptions.put(MovieProperty.overview, SettingsProperty.universalSynopsys);
    scraperOptions.put(MediaProperty.rating, SettingsProperty.universalRating);
    scraperOptions.put(MovieMultipleProperty.genres, SettingsProperty.universalGenre);
    scraperOptions.put(MovieMultipleProperty.countries, SettingsProperty.universalCountry);
  }

  public UniversalScrapper() {
    super(LocaleUtils.AvailableLanguages.values());
    searchScraper = ScrapperManager.getScrapper(settings.getUniversalSearchMovieScrapper());
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
  public List<ScrapperOptions> getScraperOptions() {
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
      movies = searchScraper.search(searchUrl.toString());
    } else {
      movies = searchScraper.search(query);
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
  protected MovieInfo fetchMediaInfo(final Movie searchResult, IdInfo id, AvailableLanguages language) throws Exception {
    final Map<MediaProperty, String> defaultMediaFields = new EnumMap<MediaProperty, String>(MediaProperty.class);
    final Map<MovieProperty, String> defaultFields = new EnumMap<MovieProperty, String>(MovieProperty.class);
    final Map<MovieMultipleProperty, List<String>> defaultMultipleFields = new EnumMap<MovieMultipleProperty, List<String>>(MovieMultipleProperty.class);
    final List<CastingInfo> defaultCastings = new ArrayList<CastingInfo>();

    final Map<MediaProperty, String> mediaFields = new EnumMap<MediaProperty, String>(MediaProperty.class);
    final Map<MovieProperty, String> fields = new EnumMap<MovieProperty, String>(MovieProperty.class);
    final Map<MovieMultipleProperty, List<String>> multipleFields = new EnumMap<MovieMultipleProperty, List<String>>(MovieMultipleProperty.class);

    final List<MovieScrapper> scrappers = getScrappersByQuality(language);
    final List<IdInfo> idsInfo = new ArrayList<IdInfo>();
    final Map<Future<MovieInfo>, Class<?>> futureProvider = new HashMap<Future<MovieInfo>, Class<?>>();
    final List<CastingInfo> castings = new ArrayList<CastingInfo>();

    addIdInfo(idsInfo, id);

    // Launch each scraper in his own thread
    int nbthread = 1;
    ExecutorService service = Executors.newFixedThreadPool(POOL_SIZE);
    CompletionService<MovieInfo> pool = new ExecutorCompletionService<MovieInfo>(service);

    futureProvider.put(pool.submit(new ScraperThread<Movie, MovieInfo>(searchScraper, searchResult, language)), searchScraper.getClass());

    for (MovieScrapper scrapper : scrappers) {
      // We do not add current scraper (Universal) and search Scraper
      if (scrapper.getName().equals(getName()) || searchScraper.getName().equals(scrapper.getName())) {
        continue;
      }

      futureProvider.put(pool.submit(new ScraperThread<Movie, MovieInfo>(scrapper, searchResult, language)), scrapper.getClass());
      nbthread++;
    }

    // Wait for all threads
    for (int i = 0; i < nbthread; i++) {

      try {
        Future<MovieInfo> future = pool.take();
        MovieInfo info = future.get();

        if (info == null) {// FIXME should not be null
          System.out.println("INFO IS NULL : " + searchScraper.getClass());
          continue;
        }

        for (IdInfo idInf : info.getIdsInfo()) {
          addIdInfo(idsInfo, idInf);
        }

        // Store search scraper infos as default values
        Class<?> provider = futureProvider.get(future);
        if (provider.equals(searchScraper.getClass())) {

          setMediaFieldsInfo(defaultMediaFields, mediaFields, info, provider);

          setFieldsValue(defaultFields, fields, info, provider);
          setMultipleFieldsValue(defaultMultipleFields, multipleFields, info, provider);

          // Set casting
          List<CastingInfo> casting = info.getCasting();
          setCasting(defaultCastings, castings, casting, provider);

          continue;
        }

        setMediaFieldsInfo(defaultMediaFields, mediaFields, info, provider);

        setFieldsValue(defaultFields, fields, info, provider);
        setMultipleFieldsValue(defaultMultipleFields, multipleFields, info, provider);

        // Set casting
        List<CastingInfo> casting = info.getCasting();
        setCasting(defaultCastings, castings, casting, provider);

      } catch (Exception ex) {
        Settings.LOGGER.warning(ClassUtils.getStackTrace(ex));
      }
    }

    // Merge
    String value, nvalue;
    List<String> values, nvalues;
    for (MediaProperty property : MediaProperty.values()) {
      value = defaultMediaFields.get(property);
      nvalue = mediaFields.get(property);
      if ((value == null || value.isEmpty()) && nvalue != null && !nvalue.isEmpty()) {
        defaultMediaFields.put(property, nvalue);
      }
    }

    for (MovieProperty property : MovieProperty.values()) {
      value = defaultFields.get(property);
      nvalue = fields.get(property);
      if ((value == null || value.isEmpty()) && nvalue != null && !nvalue.isEmpty()) {
        defaultFields.put(property, nvalue);
      }
    }

    for (MovieMultipleProperty property : MovieMultipleProperty.values()) {
      values = defaultMultipleFields.get(property);
      nvalues = multipleFields.get(property);
      if ((values == null || values.isEmpty()) && nvalues != null && !nvalues.isEmpty()) {
        defaultMultipleFields.put(property, nvalues);
      }
    }

    if (defaultCastings.isEmpty()) {
      defaultCastings.addAll(castings);
    }

    service.shutdownNow();

    return new MovieInfo(defaultMediaFields, idsInfo, defaultFields, defaultMultipleFields, defaultCastings);
  }

  private void setFieldsValue(Map<MovieProperty, String> defaultsFields, Map<MovieProperty, String> fields, MovieInfo info, Class<?> provider) {
    SettingsProperty sprop;
    String cvalue, nvalue;
    for (MovieProperty property : MovieProperty.values()) {

      cvalue = fields.get(property);
      nvalue = info.get(property);

      sprop = scraperOptions.get(property);
      if (sprop != null && settings.getMovieScrapperOptionClass(sprop).equals(provider)) {
        defaultsFields.put(property, nvalue);
        continue;
      }

      if ((cvalue == null || cvalue.equals("")) && nvalue != null && !nvalue.equals("")) {
        fields.put(property, nvalue);
      }
    }
  }

  private void setMultipleFieldsValue(Map<MovieMultipleProperty, List<String>> defaultsFields,
          Map<MovieMultipleProperty, List<String>> fields, MovieInfo info, Class<?> provider) {

    SettingsProperty sprop;
    List<String> cvalue, nvalue;
    for (MovieMultipleProperty property : MovieMultipleProperty.values()) {
      cvalue = fields.get(property);
      nvalue = info.get(property);

      sprop = scraperOptions.get(property);
      if (sprop != null && settings.getMovieScrapperOptionClass(sprop).equals(provider)) {
        defaultsFields.put(property, nvalue);
        continue;
      }

      if ((cvalue == null || cvalue.isEmpty()) && nvalue != null && !nvalue.isEmpty()) {
        fields.put(property, nvalue);
      }
    }
  }

  private void setMediaFieldsInfo(Map<MediaProperty, String> defaultsMediaFields, Map<MediaProperty, String> mediaFields,
          MovieInfo info, Class<?> provider) {

    String value;
    SettingsProperty sprop;
    for (MediaProperty property : MediaProperty.values()) {
      sprop = scraperOptions.get(property);
      if (sprop != null && settings.getMovieScrapperOptionClass(sprop).equals(provider)) {
        defaultsMediaFields.put(property, info.get(property));
        continue;
      }

      value = mediaFields.get(property);
      if (value == null || value.isEmpty()) {
        mediaFields.put(property, info.get(property));
      }
    }
  }

  private void setCasting(List<CastingInfo> defaultCastings, List<CastingInfo> castings, List<CastingInfo> infoCasting, Class<?> provider) {
    if (infoCasting != null && !infoCasting.isEmpty()) {
      if (settings.getMovieScrapperOptionClass(SettingsProperty.universalCasting).equals(provider)) {
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
