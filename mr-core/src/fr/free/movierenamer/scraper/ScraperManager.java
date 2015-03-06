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
package fr.free.movierenamer.scraper;

import fr.free.movierenamer.scraper.impl.movie.AllocineScraper;
import fr.free.movierenamer.scraper.impl.movie.IMDbScraper;
import fr.free.movierenamer.scraper.impl.movie.TMDbScraper;
import fr.free.movierenamer.scraper.impl.tvshow.TheTVDBScraper;
import fr.free.movierenamer.scraper.impl.tvshow.TvRageScraper;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import fr.free.movierenamer.scraper.impl.OpenSubtitlesScraper;
import fr.free.movierenamer.scraper.impl.SubsceneSubtitleScraper;
import fr.free.movierenamer.scraper.impl.movie.AdorocinemaScraper;
import fr.free.movierenamer.scraper.impl.movie.BeyazperdeScraper;
import fr.free.movierenamer.scraper.impl.movie.FilmstartsScraper;
import fr.free.movierenamer.scraper.impl.movie.KinopoiskScraper;
import fr.free.movierenamer.scraper.impl.movie.RottenTomatoesScraper;
import fr.free.movierenamer.scraper.impl.movie.SensacineScraper;
import fr.free.movierenamer.scraper.impl.movie.TracktScraper;
import fr.free.movierenamer.scraper.impl.movie.UniversalScraper;
import fr.free.movierenamer.scraper.impl.trailer.AllocineTrailerScraper;
import fr.free.movierenamer.scraper.impl.trailer.ImdbTrailerScraper;
import fr.free.movierenamer.scraper.impl.trailer.TrailerAddictScraper;
import fr.free.movierenamer.scraper.impl.trailer.VideoDetectiveScraper;
import fr.free.movierenamer.scraper.impl.trailer.YoutubeTrailerScraper;
import fr.free.movierenamer.searchinfo.Media.MediaType;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;

/**
 * Class ScraperManager
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class ScraperManager {

  /**
   * <code>map</code> existing scraper list
   */
  private static final Map<Class<? extends Scraper>, Scraper> map = new LinkedHashMap<Class<? extends Scraper>, Scraper>(0);
  private static final Settings settings = Settings.getInstance();

  static {
    // movie
    getScraper(AllocineScraper.class);
    getScraper(AdorocinemaScraper.class);
    getScraper(BeyazperdeScraper.class);
    getScraper(FilmstartsScraper.class);
    getScraper(SensacineScraper.class);
    getScraper(IMDbScraper.class);
    getScraper(TMDbScraper.class);
    getScraper(UniversalScraper.class);
    getScraper(RottenTomatoesScraper.class);
    getScraper(KinopoiskScraper.class);
    getScraper(TracktScraper.class);
    // tvshow
    getScraper(TheTVDBScraper.class);
    getScraper(TvRageScraper.class);
    // anime
    // getScraper(AnidbScraper.class);
    // subtitle
    getScraper(OpenSubtitlesScraper.class);
    getScraper(SubsceneSubtitleScraper.class);
    //Trailer
    getScraper(AllocineTrailerScraper.class);
    getScraper(ImdbTrailerScraper.class);
    getScraper(TrailerAddictScraper.class);
    getScraper(VideoDetectiveScraper.class);
    getScraper(YoutubeTrailerScraper.class);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Scraper> T getScraper(Class<T> scraperClass) {
    T scraper = null;
    synchronized (map) {
      if (map.containsKey(scraperClass)) {
        scraper = (T) map.get(scraperClass);
      } else {
        int modifier = scraperClass.getModifiers();
        if (!Modifier.isAbstract(modifier) && !Modifier.isInterface(modifier)) {
          try {
            scraper = scraperClass.newInstance();
          } catch (InstantiationException e) {
            Settings.LOGGER.log(Level.SEVERE, null, e);
          } catch (IllegalAccessException e) {
            Settings.LOGGER.log(Level.SEVERE, null, e);
          }
        }
        map.put(scraperClass, scraper);
      }
    }
    return scraper;
  }

  public static MediaScraper<?, ?> getMediaScraper(MediaType mtype) {
    switch (mtype) {

      case MOVIE:
        return getMovieScraper();

      case TVSHOW:
        return getTvShowScraper(); 
    }

    return null;
  }

  public static List<MediaScraper<?, ?>> getMediaScraperList(MediaType mtype) {
    return getMediaScraperList(mtype, null);
  }

  @SuppressWarnings("unchecked")
  public static List<MediaScraper<?, ?>> getMediaScraperList(MediaType mtype, AvailableLanguages language) {
    List<MediaScraper<?, ?>> mediaScrapers = new ArrayList<MediaScraper<?, ?>>();
    Class<? extends MediaScraper<?, ?>> clazz = getScraperClass(mtype);

    if (clazz != null) {
      mediaScrapers = getScrapers((Class<MediaScraper<?, ?>>) clazz, language);
    }

    return mediaScrapers;
  }

  private static Class<? extends MediaScraper<?, ?>> getScraperClass(MediaType mtype) {
    Class<? extends MediaScraper<?, ?>> clazz = null;

    switch (mtype) {

      case MOVIE:
        clazz = MovieScraper.class;
        break;

      case TVSHOW:
        clazz = TvShowScraper.class;
        break;
    }

    return clazz;
  }

  public static MovieScraper getMovieScraper() {
    return (MovieScraper) map.get(settings.getSearchMovieScraper());
  }

  public static List<MovieScraper> getMovieScraperList() {
    return getMovieScraperList(null);
  }

  public static List<MovieScraper> getMovieScraperList(AvailableLanguages language) {
    return getScrapers(MovieScraper.class, language);
  }

  public static TvShowScraper getTvShowScraper() {// TODO return tvshow scraper
    return null; //(TvShowScraper) map.get(settings.getSearchTvShowScraper());
  }

  public static List<TvShowScraper> getTvShowScraperList() {
    return getTvShowScraperList(null);
  }

  public static List<TvShowScraper> getTvShowScraperList(AvailableLanguages language) {
    return getScrapers(TvShowScraper.class, language);
  }

  @SuppressWarnings("unchecked")
  private static <T extends SearchScraper<?>> List<T> getScrapers(Class<T> sclazz, AvailableLanguages language) {
    List<T> toRet = new ArrayList<T>();
    for (Class<?> clazz : map.keySet()) {
      if (sclazz.isAssignableFrom(clazz)) {
        T scraper = (T) map.get(clazz);
        if (language != null && !scraper.hasSupportedLanguage(language)) {
          continue;
        }

        toRet.add(scraper);
      }
    }

    return toRet;
  }

  public static List<TrailerScraper> getTrailerScraperList(MediaType type) {
    List<TrailerScraper> toRet = new ArrayList<TrailerScraper>();
    for (Class<?> clazz : map.keySet()) {
      if (TrailerScraper.class.isAssignableFrom(clazz)) {
        if (type != null && !((TrailerScraper) map.get(clazz)).getSupportedMediaType().contains(type)) {
          continue;
        }

        toRet.add((TrailerScraper) map.get(clazz));
      }
    }

    return toRet;
  }

  public static List<SubtitleScraper> getSubtitleScraperList() {
    List<SubtitleScraper> toRet = new ArrayList<SubtitleScraper>();
    for (Class<?> clazz : map.keySet()) {
      if (SubtitleScraper.class.isAssignableFrom(clazz)) {
        toRet.add((SubtitleScraper) map.get(clazz));
      }
    }
    return getScrapers(SubtitleScraper.class, null);
  }

  // @SuppressWarnings("unchecked")
  // private static List<Class<Scraper>> getScrapers() {
  // List<Class<Scraper>> toRet = new ArrayList<Class<Scraper>>();
  // try {
  // List<Class<?>> classes = ClassUtils.getClasses(ScraperManager.class.getPackage());
  // for (Class<?> clazz : classes) {
  // if (Scraper.class.isAssignableFrom(clazz)) {
  // toRet.add((Class<Scraper>) clazz);
  // }
  // }
  // } catch (ClassNotFoundException e) {
  // e.printStackTrace();
  // }
  // return toRet;
  // }
  private ScraperManager() {
    throw new UnsupportedOperationException();
  }
}
