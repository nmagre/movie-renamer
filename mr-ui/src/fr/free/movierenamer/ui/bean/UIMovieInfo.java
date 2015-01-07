/*
 * Movie Renamer
 * Copyright (C) 2014 Nicolas Magré
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
package fr.free.movierenamer.ui.bean;

import fr.free.movierenamer.info.MediaInfo.InfoProperty;
import fr.free.movierenamer.info.MediaInfo.MediaProperty;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.MovieInfo.MovieMultipleProperty;
import fr.free.movierenamer.info.MovieInfo.MovieProperty;
import fr.free.movierenamer.utils.StringUtils;
import java.util.List;

/**
 * Class UIMovieInfo
 *
 * @author Nicolas Magré
 */
public class UIMovieInfo extends UIVideoInfo<MovieInfo> {

  public UIMovieInfo(MovieInfo info) {
    super(info);
  }

  public String get(InfoProperty key) {
    String value = null;
    if (key instanceof MediaProperty) {
      value = info.get((MediaProperty) key);
    } else if (key instanceof MovieProperty) {
      value = info.get((MovieProperty) key);
    } else if (key instanceof MovieMultipleProperty) {
      List<String> values = info.get((MovieMultipleProperty) key);
      value = StringUtils.arrayToString(values, ", ", 0);
    }

    return value;
  }

  public void set(InfoProperty key, String value) {
    if (key instanceof MediaProperty) {
      info.set((MediaProperty) key, value);
    } else if (key instanceof MovieProperty) {
      info.set((MovieProperty) key, value);
    } else if (key instanceof MovieMultipleProperty) {
      info.set((MovieMultipleProperty) key, value != null ? value : "");
    }
  }

  public List<String> getCountries() {
    return ((MovieInfo) info).getCountries();
  }

}
