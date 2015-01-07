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

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import fr.free.movierenamer.info.EpisodeInfo;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.TvShowInfo;
import fr.free.movierenamer.scrapper.impl.image.FanartTVshowImagesScrapper;
import fr.free.movierenamer.searchinfo.TvShow;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.CacheObject;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Class TvShowScrapper
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class TvShowScrapper extends MediaScrapper<TvShow, TvShowInfo> {

  protected TvShowScrapper(AvailableLanguages... supportedLanguages) {
    super(supportedLanguages);
  }

  protected abstract List<EpisodeInfo> fetchEpisodesInfoList(TvShow tvShow, AvailableLanguages language) throws Exception;

  public final List<EpisodeInfo> getEpisodesInfoList(TvShow tvShow) throws Exception {
    return getEpisodesInfoList(tvShow, getLanguage());
  }

  protected final List<EpisodeInfo> getEpisodesInfoList(TvShow tvShow, AvailableLanguages language) throws Exception {
    Locale lang = language.getLocale();
    Settings.LOGGER.log(Level.INFO, String.format("Use '%s' to get episode info list for '%s' in '%s'", getName(), tvShow, lang.getDisplayLanguage(Locale.ENGLISH)));
    CacheObject cache = getCache();
    List<EpisodeInfo> episodes = (cache != null) ? cache.getList(tvShow, lang, EpisodeInfo.class) : null;
    if (episodes != null) {
      return episodes;
    }

    // perform actual search
    episodes = fetchEpisodesInfoList(tvShow, language);
    Settings.LOGGER.log(Level.INFO, String.format("'%s' returns %d episode info for '%s' in '%s'", getName(), episodes.size(), tvShow, lang.getDisplayLanguage(Locale.ENGLISH)));

    // cache results and return
    return (cache != null) ? cache.putList(tvShow, lang, EpisodeInfo.class, episodes) : episodes;
  }

  @Override
  protected final List<ImageInfo> fetchImagesInfo(TvShow tvshow) throws Exception {

    List<ImageInfo> imagesInfo = new ArrayList<ImageInfo>();

    // TODO tvdb images
    // Try to get images from fanart.tv
    FanartTVshowImagesScrapper fanartImagesSc = new FanartTVshowImagesScrapper();
    List<ImageInfo> tmpImagesInfo = fanartImagesSc.getImages(tvshow);
    if (tmpImagesInfo != null) {
      imagesInfo.addAll(tmpImagesInfo);
    }

    // use scrapper default get image
    if (imagesInfo.isEmpty()) {
      tmpImagesInfo = getScrapperImages(tvshow);
      if (tmpImagesInfo != null) {
        imagesInfo.addAll(tmpImagesInfo);
      }
    }

    return imagesInfo;
  }
  
    @Override
  public IdInfo getIdfromURL(URL url) {// TODO remove
    return null;
  }

  @Override
  public URL getURL(IdInfo id) {// TODO remove
    return null;
  }
}
