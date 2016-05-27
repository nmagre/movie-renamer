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
import fr.free.movierenamer.scraper.impl.movie.UniversalScraper;
import fr.free.movierenamer.scraper.impl.trailer.AllocineTrailerScraper;
import fr.free.movierenamer.scraper.impl.trailer.ImdbTrailerScraper;
import fr.free.movierenamer.scraper.impl.trailer.TrailerAddictScraper;
import fr.free.movierenamer.scraper.impl.trailer.VideoDetectiveScraper;
import fr.free.movierenamer.scraper.impl.trailer.YoutubeTrailerScraper;
import fr.free.movierenamer.searchinfo.Media.MediaType;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import java.util.Collections;
import java.util.Comparator;

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
  private static final Map<Class<? extends Scraper>, Scraper> map = new LinkedHashMap<>(0);
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
    //getScraper(TracktScraper.class);
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

  public static MediaScraper getMediaScraper(MediaType mtype) {
    return (MediaScraper) map.get(settings.getInfoScraper(mtype));
  }

  public static List<MediaScraper> getMediaScrapers(Class<? extends MediaScraper> sclazz) {
    return getMediaScrapers(sclazz, null);
  }

  public static List<MediaScraper> getMediaScrapers(MediaType mtype) {
    return getMediaScrapers(mtype.getScraperTypeClass(), null);
  }

  public static List<MediaScraper> getMediaScrapers(MediaType mtype, AvailableLanguages language) {
    return getMediaScrapers(mtype.getScraperTypeClass(), language);
  }

  @SuppressWarnings("unchecked")
  private static List<MediaScraper> getMediaScrapers(Class<? extends MediaScraper> sclazz, AvailableLanguages language) {
    List<MediaScraper> toRet = new ArrayList<>();
    for (Class<?> clazz : map.keySet()) {
      if (sclazz.isAssignableFrom(clazz)) {
        MediaScraper scraper = (MediaScraper) map.get(clazz);
        if (language != null && !scraper.hasSupportedLanguage(language)) {
          continue;
        }

        toRet.add(scraper);
      }
    }

    return toRet;
  }

  public static List<MediaScraper> getScrapersByQuality(MediaType mtype) {
    return getScrapersByQuality(mtype, null);
  }

  public static List<MediaScraper> getScrapersByQuality(MediaType mtype, AvailableLanguages language) {
    List<MediaScraper> scrapers = ScraperManager.getMediaScrapers(mtype, language);
    Collections.sort(scrapers, new Comparator<MediaScraper>() {

      @Override
      public int compare(MediaScraper o1, MediaScraper o2) {
        return o1.getQuality().ordinal() - o2.getQuality().ordinal();
      }
    });
    
    return scrapers;
  }

  public static List<TrailerScraper> getTrailerScraperList(MediaType type) {
    List<TrailerScraper> toRet = new ArrayList<>();
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

//  public static List<SubtitleScraper> getSubtitleScraperList() {
//    return getScrapers(SubtitleScraper.class, null);
//  }
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
