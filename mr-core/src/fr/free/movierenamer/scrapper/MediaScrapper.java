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
package fr.free.movierenamer.scrapper;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.utils.CacheObject;

/**
 * Class MediaScrapper
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class MediaScrapper<T extends Media, I extends MediaInfo> extends SearchScrapper<T> {

  protected MediaScrapper(Locale defaultLocale) {
    super(defaultLocale);
  }

  @Override
  protected final List<T> search(String query, Locale locale) throws Exception {
    Logger.getLogger(SearchScrapper.class.getName()).log(Level.INFO, String.format("Use '%s' to search media for '%s' in '%s'", getName(), query, locale.getDisplayLanguage(Locale.ENGLISH)));
    CacheObject cache = getCache();
    @SuppressWarnings("unchecked")
    Class<T> genericClazz = (Class<T>) ((ParameterizedType) getClass().getSuperclass().getGenericSuperclass()).getActualTypeArguments()[0]; // TODO put it in Utils !
    List<T> results = (cache != null) ? cache.getList(query, locale, genericClazz) : null;
    if (results != null) {
      return results;
    }

    // perform actual search
    results = searchMedia(query, locale);
    Logger.getLogger(SearchScrapper.class.getName()).log(Level.INFO, String.format("'%s' returns %d media for '%s' in '%s'", getName(), results.size(), query, locale.getDisplayLanguage(Locale.ENGLISH)));
    
    // cache results and return
    return (cache != null) ? cache.putList(query, locale, genericClazz, results) : results;
  }

  protected abstract List<T> searchMedia(String query, Locale locale) throws Exception;

  public final I getInfo(T search) throws Exception {
    return getInfo(search, getLocale());
  }

  protected final I getInfo(T search, Locale locale) throws Exception {
    Logger.getLogger(SearchScrapper.class.getName()).log(Level.INFO, String.format("Use '%s' to get media info for '%s' in '%s'", getName(), search, locale.getDisplayLanguage(Locale.ENGLISH)));
    CacheObject cache = getCache();
    @SuppressWarnings("unchecked")
    Class<I> genericClazz = (Class<I>) ((ParameterizedType) getClass().getSuperclass().getGenericSuperclass()).getActualTypeArguments()[1]; // TODO put it in Utils !
    I info = (cache != null) ? cache.getData(search, locale, genericClazz) : null;
    if (info != null) {
      return info;
    }

    // perform actual search
    info = fetchMediaInfo(search, locale);
    Logger.getLogger(SearchScrapper.class.getName()).log(Level.INFO, String.format("'%s' returns '%s' as info for '%s' in '%s'", getName(), info, search, locale.getDisplayLanguage(Locale.ENGLISH)));

    //let's fetch casting
    List<CastingInfo> casting;
    try {
      casting = getCasting(search, locale);
    } catch (Exception ex) {
      casting = null;
    }
    info.setCasting(casting);
    // info.setImages(getImages(searchResult, locale));

    // cache results and return
    return (cache != null) ? cache.putData(search, locale, info) : info;
  }

  protected abstract I fetchMediaInfo(T searchResult, Locale locale) throws Exception;

  public final List<ImageInfo> getImages(T search) throws Exception {
    return getImages(search, getLocale());
  }

  protected final List<ImageInfo> getImages(T search, Locale locale) throws Exception {
    Logger.getLogger(SearchScrapper.class.getName()).log(Level.INFO, String.format("Use '%s' to get image info list for '%s' in '%s'", getName(), search, locale.getDisplayLanguage(Locale.ENGLISH)));
    CacheObject cache = getCache();
    List<ImageInfo> imagesInfo = (cache != null) ? cache.getList(search, locale, ImageInfo.class) : null;
    if (imagesInfo != null) {
      return imagesInfo;
    }

    // perform actual search
    imagesInfo = fetchImagesInfo(search, locale);
    Logger.getLogger(SearchScrapper.class.getName()).log(Level.INFO, String.format("'%s' returns %d image(s) info for '%s' in '%s'", getName(), imagesInfo.size(), search, locale.getDisplayLanguage(Locale.ENGLISH)));

    // cache results and return
    return (cache != null) ? cache.putList(search, locale, ImageInfo.class, imagesInfo) : imagesInfo;
  }

  protected abstract List<ImageInfo> fetchImagesInfo(T search, Locale locale) throws Exception;

  public final List<CastingInfo> getCasting(T search) throws Exception {
    return getCasting(search, getLocale());
  }

  protected final List<CastingInfo> getCasting(T search, Locale locale) throws Exception {
    Logger.getLogger(SearchScrapper.class.getName()).log(Level.INFO, String.format("Use '%s' to get casting info list for '%s' in '%s'", getName(), search, locale.getDisplayLanguage(Locale.ENGLISH)));
    CacheObject cache = getCache();
    List<CastingInfo> personsInfo = (cache != null) ? cache.getList(search, locale, CastingInfo.class) : null;
    if (personsInfo != null) {
      return personsInfo;
    }

    // perform actual search
    personsInfo = fetchCastingInfo(search, locale);
    Logger.getLogger(SearchScrapper.class.getName()).log(Level.INFO, String.format("'%s' returns %d casting info for '%s' in '%s'", getName(), personsInfo.size(), search, locale.getDisplayLanguage(Locale.ENGLISH)));

    // cache results and return
    return (cache != null) ? cache.putList(search, locale, CastingInfo.class, personsInfo) : personsInfo;
  }

  protected abstract List<CastingInfo> fetchCastingInfo(T search, Locale locale) throws Exception;

}
