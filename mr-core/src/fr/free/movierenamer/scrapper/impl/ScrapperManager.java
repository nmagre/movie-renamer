/*
 * movie-renamer-core
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
package fr.free.movierenamer.scrapper.impl;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.scrapper.Scrapper;
import fr.free.movierenamer.scrapper.SubtitleScrapper;
import fr.free.movierenamer.scrapper.TvShowScrapper;
import fr.free.movierenamer.settings.Settings;

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
    getScrapper(IMDbScrapper.class);
    getScrapper(TMDbScrapper.class);
    // tvshow
    getScrapper(SerienjunkiesScrapper.class);
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
    scrapper.setLocale(settings.getSearchScrapperLang());
    return scrapper;
  }

  public static TvShowScrapper getTvShowScrapper() {
    Settings settings = Settings.getInstance();
    TvShowScrapper scrapper = getScrapper(settings.getSearchTvshowScrapper());
    scrapper.setLocale(settings.getSearchScrapperLang());
    return scrapper;
  }

  public static SubtitleScrapper getSubtitleScrapper() {
    Settings settings = Settings.getInstance();
    SubtitleScrapper scrapper = getScrapper(settings.getSearchSubtitleScrapper());
    scrapper.setLocale(settings.getSearchScrapperLang());
    return scrapper;
  }

  public static List<MovieScrapper> getMovieScrapperList() {
    List<MovieScrapper> toRet = new ArrayList<MovieScrapper>();
    for (Class<?> clazz : map.keySet()) {
      if (MovieScrapper.class.isAssignableFrom(clazz)) {
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
