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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.free.movierenamer.scraper;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.searchinfo.Media.MediaType;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.CacheObject;
import fr.free.movierenamer.utils.ClassUtils;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.ScraperUtils;
import fr.free.movierenamer.utils.ScraperUtils.AvailableApiIds;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Class MediaScraper
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class MediaScraper<M extends Media, MI extends MediaInfo> extends SearchScraper<M> {

  protected static final Settings settings = Settings.getInstance();

  protected MediaScraper(AvailableLanguages... supportedLanguages) {
    super(supportedLanguages);
  }

  @Override
  protected final List<M> search(String query, AvailableLanguages language) throws Exception {
    Locale lang = language.getLocale();
    Settings.LOGGER.log(Level.INFO, String.format("Use '%s' to search media for '%s' in '%s'", getName(), query, lang.getDisplayLanguage(Locale.ENGLISH)));
    CacheObject cache = getCache();

    Class<M> genericClazz = ClassUtils.getGenericSuperClassArg(getClass(), 0);

    List<M> results = (cache != null) ? cache.getList(query, language.getLocale(), genericClazz) : null;
    if (results != null) {
      return results;
    }

    // perform actual search
    try {
      URL url = new URL(query);
      if (!hasUrlSupported(url)) {
        url = convertUrl(url);
        if (url == null) {
          throw new Exception(String.format("Unable to convert URL to %s URL", getName()));
        }
      }

      results = searchMedia(url, language);
    } catch (MalformedURLException ex) {
      results = searchMedia(query, language);
    }

    Settings.LOGGER.log(Level.INFO, String.format("'%s' returns %d media for '%s' in '%s'", getName(), results.size(), query, lang.getDisplayLanguage(Locale.ENGLISH)));

    // cache results and return
    return (cache != null) ? cache.putList(query, lang, genericClazz, results) : results;
  }

  protected abstract List<M> searchMedia(String query, AvailableLanguages language) throws Exception;

  protected abstract List<M> searchMedia(URL searchUrl, AvailableLanguages language) throws Exception;

  public boolean hasUrlSupported(URL url) {
    return url.getHost().replace("www.", "").equals(getHost().replace("www.", ""));
  }

  public abstract IdInfo getIdfromURL(URL url);

  public abstract URL getURL(IdInfo id);

  /**
   * Convert a scraper URL to this scraper URL
   *
   * @param url URL for another scraper
   * @return Converted url or null
   */
  private URL convertUrl(URL url) {
    MediaScraper<? extends Media, ? extends MediaInfo> scraper = ScraperUtils.getScraperFor(url, getSupportedMediaType());
    if (scraper == null) {
      return null;
    }

    List<? extends Media> results = null;
    try {
      results = scraper.search(url.toExternalForm());
    } catch (Exception ex) {

    }

    if (results == null || results.isEmpty()) {
      return null;
    }

    IdInfo idinfo = scraper.getIdfromURL(url);
    if (idinfo == null) {
      return null;
    }

    IdInfo lookupId = ScraperUtils.idLookup(getSupportedId(), idinfo, results.get(0));
    if (lookupId == null) {
      return null;
    }

    return getURL(lookupId);
  }

  public final MI getInfo(M search) throws Exception {
    return getInfo(search, getLanguage());
  }

  protected final MI getInfo(M search, AvailableLanguages language) throws Exception {
    Locale lang = language.getLocale();
    Settings.LOGGER.log(Level.INFO, String.format("Use '%s' to get media info for '%s' in '%s'", getName(), search, lang.getDisplayLanguage(Locale.ENGLISH)));
    CacheObject cache = getCache();

    Class<MI> genericClazz = ClassUtils.getGenericSuperClassArg(getClass(), 1);

    MI info = (cache != null) ? cache.getData(search, lang, genericClazz) : null;
    if (info != null) {
      String origtitle = info.getOriginalTitle();
      if (origtitle != null && !origtitle.isEmpty()) {
        search.setOriginalName(origtitle);
      }

      if (!hasSupportedLanguage(language) && settings.isGetOnlyLangDepInfo()) {
        info.unsetUnsupportedLangInfo();
      }
      return info;
    }

    // Fetch id
    IdInfo id = search.getMediaId();
    if (id != null && id.getIdType() != getSupportedId()) {
      id = ScraperUtils.idLookup(getSupportedId(), id, search);
      if (id == null) {
        return info;
      }
    }

    // perform actual search
    info = fetchMediaInfo(search, id, language);
    Settings.LOGGER.log(Level.INFO, String.format("'%s' returns '%s' as info for '%s' in '%s'", getName(), info, search, lang.getDisplayLanguage(Locale.ENGLISH)));

    if (info == null) {
      return info;
    }

    addCutomInfo(info, search, id, language);

    // cache results
    if (cache != null) {
      cache.putData(search, lang, info);
    }

    String origtitle = info.getOriginalTitle();
    if (origtitle != null && !origtitle.isEmpty()) {
      search.setOriginalName(origtitle);
    }

    if (!hasSupportedLanguage(language) && settings.isGetOnlyLangDepInfo()) {
      info.unsetUnsupportedLangInfo();
    }

    return info;
  }

  protected abstract MI fetchMediaInfo(M searchResult, IdInfo id, AvailableLanguages language) throws Exception;

  protected void addCutomInfo(MI info, M search, IdInfo id, AvailableLanguages language) {
    // do nothing
  }

  public final List<ImageInfo> getImages(M search) throws Exception {
    List<ImageInfo> imagesInfo = fetchImagesInfo(search);
    return imagesInfo != null ? imagesInfo : new ArrayList<ImageInfo>();
  }

  protected abstract List<ImageInfo> fetchImagesInfo(M media) throws Exception;

  protected List<ImageInfo> getScraperImages(M searchResult) throws Exception {
    return null;
  }

  public final List<CastingInfo> getCasting(M search, IdInfo id) throws Exception {
    return getCasting(search, id, getLanguage());
  }

  protected final List<CastingInfo> getCasting(M search, IdInfo id, AvailableLanguages language) throws Exception {
    Locale lang = language.getLocale();
    Settings.LOGGER.log(Level.INFO, String.format("Use '%s' to get casting info list for '%s' in '%s'", getName(), search, lang.getDisplayLanguage(Locale.ENGLISH)));
    CacheObject cache = getCache();
    List<CastingInfo> personsInfo = (cache != null) ? cache.getList(search, lang, CastingInfo.class) : null;
    if (personsInfo != null) {
      return personsInfo;
    }

    // perform actual search
    personsInfo = fetchCastingInfo(search, id, language);
    if (personsInfo == null) {
      return null;
    }
    Settings.LOGGER.log(Level.INFO, String.format("'%s' returns %d casting info for '%s' in '%s'", getName(), personsInfo.size(), search, lang.getDisplayLanguage(Locale.ENGLISH)));

    // cache results and return
    return (cache != null) ? cache.putList(search, lang, CastingInfo.class, personsInfo) : personsInfo;
  }

  protected abstract List<CastingInfo> fetchCastingInfo(M search, IdInfo id, AvailableLanguages language) throws Exception;

  public abstract AvailableApiIds getSupportedId();

  public abstract MediaType getSupportedMediaType();

}
