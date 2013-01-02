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

import java.util.List;
import java.util.Locale;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.EpisodeInfo;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.TvShowInfo;
import fr.free.movierenamer.scrapper.TvShowScrapper;
import fr.free.movierenamer.searchinfo.TvShow;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;

/**
 * Class AnidbScrapper : search tvshow on anidb (Anime DataBase)
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class AnidbTvseriesScrapper extends TvShowScrapper {

  private final String host = "anidb.net";
  private final String name = "AniDB";

  public AnidbTvseriesScrapper() {
    super(AvailableLanguages.en);
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
  protected List<TvShow> searchMedia(String query, Locale language) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Not supported yet.");
    // return null;
  }

  @Override
  protected TvShowInfo fetchMediaInfo(TvShow tvShow, Locale language) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Not supported yet.");
    // return null;
  }

  @Override
  protected List<EpisodeInfo> fetchEpisodesInfoList(TvShow tvShow, Locale language) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Not supported yet.");
    // return null;
  }

  @Override
  protected List<ImageInfo> fetchImagesInfo(TvShow tvShow, Locale language) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Not supported yet.");
    // return null;
  }

  @Override
  protected List<CastingInfo> fetchCastingInfo(TvShow tvShow, Locale language) throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Not supported yet.");
    // return null;
  }

}
