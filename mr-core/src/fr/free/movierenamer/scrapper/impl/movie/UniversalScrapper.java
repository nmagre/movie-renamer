/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.free.movierenamer.scrapper.impl.movie;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.scrapper.ScrapperManager;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.ScrapperUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

/**
 * Class UniversalScrapper : Search movie on several scrapper (based on imdb)
 *
 * @author Nicolas Magré
 */
public class UniversalScrapper extends MovieScrapper {// TODO

  private static final String host = "www.imdb.com";
  private static final String name = "Universal";
  private String query;

  public UniversalScrapper() {
    super(LocaleUtils.AvailableLanguages.values());
  }

  @Override
  protected List<Movie> searchMedia(String query, Locale language) throws Exception {
    //URL searchUrl = new URL("http", host, "/find?s=tt&ref_=fn_tt&q=" + URIRequest.encode(query));
    this.query = query;
    return searchMedia((URL) null, language);
  }

  @Override
  protected List<Movie> searchMedia(URL searchUrl, Locale language) throws Exception {
    List<Movie> movies;

    // Search on imdb
    movies = ScrapperManager.getScrapper(IMDbScrapper.class).search(query);

    // Search on TMDB
    if (movies.isEmpty()) {
      movies = ScrapperManager.getScrapper(TMDbScrapper.class).search(query);
    }

    if (movies.isEmpty()) {
      List<MovieScrapper> scrappers = ScrapperManager.getMovieScrapperList(AvailableLanguages.valueOf(language.getLanguage()));

      // Search on any scrapper that support current language
      for (MovieScrapper scrapper : scrappers) {
        if (scrapper instanceof UniversalScrapper || scrapper instanceof TMDbScrapper) {
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
  protected MovieInfo fetchMediaInfo(Movie searchResult, Locale language) throws Exception {
    AvailableLanguages lang = AvailableLanguages.valueOf(language.getLanguage());
    Map<MovieInfo.MovieProperty, String> fields = new EnumMap<MovieInfo.MovieProperty, String>(MovieInfo.MovieProperty.class);
    Map<MovieInfo.MovieMultipleProperty, List<?>> multipleFields = new EnumMap<MovieInfo.MovieMultipleProperty, List<?>>(MovieInfo.MovieMultipleProperty.class);

    List<MovieScrapper> scrappers = ScrapperManager.getMovieScrapperList(lang);
    List<MovieInfo> infos = new ArrayList<MovieInfo>();

    IdInfo idInfo = searchResult.getId();


    for (MovieScrapper scrapper : scrappers) {
      if (scrapper.getName().equals(getName())) {
        continue;
      }

      try {
        Movie result = searchResult;
        switch (idInfo.getIdType()) {
          case IMDB:
          case TMDB:
            if (AlloGroupScrapper.class.isAssignableFrom(scrapper.getClass())) {
              IdInfo alloId = ScrapperUtils.alloIdLookup(searchResult);
              if (alloId == null) {
                continue;
              }

              result = new Movie(alloId, searchResult.getName(), searchResult.getOriginalTitle(), searchResult.getURL(), searchResult.getYear());
            }
            break;
          case ALLOCINE:
            if (!(AlloGroupScrapper.class.isAssignableFrom(scrapper.getClass()))) {
              IdInfo imdbId = ScrapperUtils.imdbIdLookup(idInfo, searchResult);
              if (imdbId == null) {
                continue;
              }

              result = new Movie(imdbId, searchResult.getName(), searchResult.getOriginalTitle(), searchResult.getURL(), searchResult.getYear());
            }
            break;
          default:
            result = searchResult;
        }

        MovieInfo info = scrapper.getInfo(result);

        // Merge info
        // TODO
        for (MovieInfo.MovieProperty property : MovieInfo.MovieProperty.values()) {
          if (scrapper.getClass() == IMDbScrapper.class) {
            if (!property.isLanguageDepends()) {
              String cvalue = fields.get(property);
              String nvalue = info.get(property);
              if ((cvalue == null || cvalue.equals("")) && nvalue != null && !nvalue.equals("")) {
                fields.put(property, nvalue);
              }
            }
          } else {
            String cvalue = fields.get(property);
            String nvalue = info.get(property);
            if ((cvalue == null || cvalue.equals("")) && nvalue != null && !nvalue.equals("")) {
              fields.put(property, nvalue);
            }
          }
        }

        for (MovieInfo.MovieMultipleProperty property : MovieInfo.MovieMultipleProperty.values()) {
          if (scrapper.getClass() == IMDbScrapper.class) {
            if (!property.isLanguageDepends()) {
              List<?> cvalue = multipleFields.get(property);
              List<?> nvalue = info.get(property);
              if ((cvalue == null || cvalue.isEmpty()) && nvalue != null && !nvalue.isEmpty()) {
                multipleFields.put(property, nvalue);
              }
            }
          } else {
            List<?> cvalue = multipleFields.get(property);
            List<?> nvalue = info.get(property);
            if ((cvalue == null || cvalue.isEmpty()) && nvalue != null && !nvalue.isEmpty()) {
              multipleFields.put(property, nvalue);
            }
          }
        }
      } catch (Exception ex) {
        //Settings.LOGGER.log(Level.SEVERE, null, ex);
      }
    }

    return new MovieInfo(fields, multipleFields);
  }

  @Override
  protected List<CastingInfo> fetchCastingInfo(Movie search, Locale language) throws Exception {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected Locale getDefaultLanguage() {
    return Locale.ENGLISH;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  protected String getHost() {
    return host;
  }
}
