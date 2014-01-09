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
package fr.free.movierenamer.scrapper.impl.tvshow;

import java.util.List;
import java.util.Locale;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.EpisodeInfo;
import fr.free.movierenamer.info.TvShowInfo;
import fr.free.movierenamer.scrapper.TvShowScrapper;
import fr.free.movierenamer.searchinfo.TvShow;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import java.net.URL;

/**
 * Class AllocineTvseriesScrapper
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class AllocineTvseriesScrapper extends TvShowScrapper {

  private static final String host = "api.allocine.fr";
  private static final String name = "Allocine";
  private static final String version = "3";
  private final String apikey;

  public AllocineTvseriesScrapper() {
    super(AvailableLanguages.fr);
    String key = Settings.getApplicationProperty("allocine.apikey");
    if (key == null || key.trim().length() == 0) {
      throw new NullPointerException("apikey must not be null");
    }
    this.apikey = key;
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
    return AvailableLanguages.fr;
  }

  @Override
  protected List<TvShow> searchMedia(String query, AvailableLanguages language) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Not supported yet.");
    // return null;
  }

  @Override
  protected List<TvShow> searchMedia(URL searchUrl, AvailableLanguages language) throws Exception {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected TvShowInfo fetchMediaInfo(TvShow tvShow, AvailableLanguages language) throws Exception {
    // URL searchUrl = new URL("http", host, "/rest/v" + version + "/tvseries?season=" + apikey + "&profile=large&filter=movie&striptags=synopsis,synopsisshort&format=json&code=" + season.getId());
    // URL searchUrl = new URL("http", host, "/rest/v" + version + "/tvseries?episode=" + apikey + "&profile=large&filter=movie&striptags=synopsis,synopsisshort&format=json&code=" + episode.getId());
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Not supported yet.");
    // return null;
  }

  @Override
  protected List<EpisodeInfo> fetchEpisodesInfoList(TvShow tvShow, AvailableLanguages language) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Not supported yet.");
    // return null;
  }

//  @Override
//  protected List<ImageInfo> fetchImagesInfo(TvShow tvShow, Locale language) throws Exception {
//    // TODO Auto-generated method stub
//    throw new UnsupportedOperationException("Not supported yet.");
//    // return null;
//  }
  @Override
  protected List<CastingInfo> fetchCastingInfo(TvShow tvShow, AvailableLanguages language) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Not supported yet.");
    // return null;
  }
}
