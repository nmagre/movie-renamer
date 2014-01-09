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
package fr.free.movierenamer.scrapper;

import fr.free.movierenamer.scrapper.impl.movie.AllocineScrapper;
import fr.free.movierenamer.scrapper.impl.movie.AnidbScrapper;
import fr.free.movierenamer.scrapper.impl.movie.IMDbScrapper;
import fr.free.movierenamer.scrapper.impl.movie.TMDbScrapper;
import fr.free.movierenamer.scrapper.impl.tvshow.TheTVDBScrapper;
import fr.free.movierenamer.scrapper.impl.tvshow.TvRageScrapper;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import fr.free.movierenamer.scrapper.impl.OpenSubtitlesScrapper;
import fr.free.movierenamer.scrapper.impl.SubsceneSubtitleScrapper;
import fr.free.movierenamer.scrapper.impl.movie.AdorocinemaScrapper;
import fr.free.movierenamer.scrapper.impl.movie.BeyazperdeScrapper;
import fr.free.movierenamer.scrapper.impl.movie.FilmstartsScrapper;
import fr.free.movierenamer.scrapper.impl.movie.KinopoiskScrapper;
import fr.free.movierenamer.scrapper.impl.movie.RottenTomatoes;
import fr.free.movierenamer.scrapper.impl.movie.ScreenRushScrapper;
import fr.free.movierenamer.scrapper.impl.movie.SensacineScrapper;
import fr.free.movierenamer.scrapper.impl.movie.UniversalScrapper;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;

/**
 * Class ScrapperManager
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class ScrapperManager {

  /**
   * <code>map</code> existing scrapper list
   */
  private static final Map<Class<? extends Scrapper>, Scrapper> map = new LinkedHashMap<Class<? extends Scrapper>, Scrapper>(0);

  static {
    // movie
    getScrapper(AllocineScrapper.class);
    getScrapper(AdorocinemaScrapper.class);
    getScrapper(BeyazperdeScrapper.class);
    getScrapper(FilmstartsScrapper.class);
    getScrapper(ScreenRushScrapper.class);
    getScrapper(SensacineScrapper.class);
    getScrapper(IMDbScrapper.class);
    getScrapper(TMDbScrapper.class);
    getScrapper(UniversalScrapper.class);
    getScrapper(RottenTomatoes.class);
    getScrapper(KinopoiskScrapper.class);
    // tvshow
    getScrapper(TheTVDBScrapper.class);
    getScrapper(TvRageScrapper.class);
    // anime
    getScrapper(AnidbScrapper.class);
    // subtitle
    getScrapper(OpenSubtitlesScrapper.class);
    getScrapper(SubsceneSubtitleScrapper.class);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Scrapper> T getScrapper(Class<T> scrapperClass) {
    T scrapper = null;
    synchronized (map) {
      if (map.containsKey(scrapperClass)) {
        scrapper = (T) map.get(scrapperClass);
      } else {
        int modifier = scrapperClass.getModifiers();
        if (!Modifier.isAbstract(modifier) && !Modifier.isInterface(modifier)) {
          try {
            scrapper = scrapperClass.newInstance();
          } catch (InstantiationException e) {
            Settings.LOGGER.log(Level.SEVERE, null, e);
          } catch (IllegalAccessException e) {
            Settings.LOGGER.log(Level.SEVERE, null, e);
          }
        }
        map.put(scrapperClass, scrapper);
      }
    }
    return scrapper;
  }

  public static MovieScrapper getMovieScrapper() {
    Settings settings = Settings.getInstance();
    MovieScrapper scrapper = getScrapper(settings.getSearchMovieScrapper());
    return scrapper;
  }

//  public static TvShowScrapper getTvShowScrapper() {
//    Settings settings = Settings.getInstance();
//    TvShowScrapper scrapper = getScrapper(settings.getSearchTvshowScrapper());
//    scrapper.setLanguage(settings.getSearchScrapperLang().getLocale());
//    return scrapper;
//  }
//
//  public static SubtitleScrapper getSubtitleScrapper() {
//    Settings settings = Settings.getInstance();
//    SubtitleScrapper scrapper = getScrapper(settings.getSearchSubtitleScrapper());
//    //scrapper.setLocale(settings.getSearchScrapperLang());// FIXME
//    return scrapper;
//  }
  public static List<MovieScrapper> getMovieScrapperList() {
    Settings settings = Settings.getInstance();
    List<MovieScrapper> toRet = new ArrayList<MovieScrapper>();
    for (Class<?> clazz : map.keySet()) {
      if (MovieScrapper.class.isAssignableFrom(clazz)) {
        toRet.add((MovieScrapper) map.get(clazz));
      }
    }
    return toRet;
  }

  public static List<MovieScrapper> getMovieScrapperList(AvailableLanguages language) {
    Settings settings = Settings.getInstance();
    List<MovieScrapper> toRet = new ArrayList<MovieScrapper>();
    for (Class<?> clazz : map.keySet()) {
      if (MovieScrapper.class.isAssignableFrom(clazz) && ((MovieScrapper) map.get(clazz)).hasSupportedLanguage(language)) {
        toRet.add((MovieScrapper) map.get(clazz));
      }
    }
    return toRet;
  }

  public static List<TvShowScrapper> getTvShowScrapperList() {
    List<TvShowScrapper> toRet = new ArrayList<TvShowScrapper>();
    for (Class<?> clazz : map.keySet()) {
      if (TvShowScrapper.class.isAssignableFrom(clazz)) {
        toRet.add((TvShowScrapper) map.get(clazz));
      }
    }
    return toRet;
  }

  public static List<SubtitleScrapper> getSubtitleScrapperList() {
    List<SubtitleScrapper> toRet = new ArrayList<SubtitleScrapper>();
    for (Class<?> clazz : map.keySet()) {
      if (SubtitleScrapper.class.isAssignableFrom(clazz)) {
        toRet.add((SubtitleScrapper) map.get(clazz));
      }
    }
    return toRet;
  }

  // @SuppressWarnings("unchecked")
  // private static List<Class<Scrapper>> getScrappers() {
  // List<Class<Scrapper>> toRet = new ArrayList<Class<Scrapper>>();
  // try {
  // List<Class<?>> classes = ClassUtils.getClasses(ScrapperManager.class.getPackage());
  // for (Class<?> clazz : classes) {
  // if (Scrapper.class.isAssignableFrom(clazz)) {
  // toRet.add((Class<Scrapper>) clazz);
  // }
  // }
  // } catch (ClassNotFoundException e) {
  // e.printStackTrace();
  // }
  // return toRet;
  // }
  private ScrapperManager() {
    throw new UnsupportedOperationException();
  }
}
