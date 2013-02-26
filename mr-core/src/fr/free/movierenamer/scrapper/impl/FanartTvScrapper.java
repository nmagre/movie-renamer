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

import fr.free.movierenamer.scrapper.ImageScrapper;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.LocaleUtils;

/**
 * Class FanartTvScrapper
 *
 * @author Simon QUÉMÉNEUR
 * @author Nicolas Magré
 */
public abstract class FanartTvScrapper<M extends Media> extends ImageScrapper<M> {

  private static final String host = "api.fanart.tv/webservice";
  private static final String name = "FanartTV";
  protected final String apikey;

  protected enum ImageType {

    hdmovielogo, // logo HD
    movielogo, // logo
    movieart, // clearart
    hdmovieart, // clearart
    moviebackground,// fanart
    moviebanner, // banner (not useful)
    moviedisc, // cdart
    //moviethumb; // thumb but not really no
  }

  protected FanartTvScrapper() {
    super(LocaleUtils.AvailableLanguages.en);
    String key = Settings.decodeApkKey(Settings.getApplicationProperty("fanarttv.apkapikey"));
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

}
