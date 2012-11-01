/*
 * movie-renamer
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
package fr.free.movierenamer.info;

import fr.free.movierenamer.utils.Date;
import java.util.EnumMap;
import java.util.Map;

/**
 * Class EpisodeInfo
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class EpisodeInfo extends Info {

  private static final long serialVersionUID = 1L;

  public static enum EpisodeProperty {
    tvShowName,
    tvShowStartDate,
    season,
    episode,
    name,
    absolute,
    special,
    airdate
  }

  protected Map<EpisodeProperty, String> fields;

  protected EpisodeInfo() {
    // used by serializer
  }

  public EpisodeInfo(Map<EpisodeProperty, String> fields) {
    this.fields = new EnumMap<EpisodeProperty, String>(fields);
  }

  public String get(Object key) {
    return fields.get(EpisodeProperty.valueOf(key.toString()));
  }

  public String get(EpisodeProperty key) {
    return fields.get(key);
  }

  public String getTvShowName() {
    return get(EpisodeProperty.tvShowName);
  }

  public Date getTvShowStartDate() {
    return Date.parse(get(EpisodeProperty.tvShowStartDate), "yyyy-MM-dd");
  }

  public Integer getSeason() {
    try {
      return Integer.parseInt(get(EpisodeProperty.season));
    } catch (Exception e) {
      return null;
    }
  }

  public Integer getEpisode() {
    try {
      return Integer.parseInt(get(EpisodeProperty.episode));
    } catch (Exception e) {
      return null;
    }
  }

  public String getName() {
    return get(EpisodeProperty.name);
  }

  public Integer getAbsolute() {
    try {
      return Integer.parseInt(get(EpisodeProperty.absolute));
    } catch (Exception e) {
      return null;
    }
  }

  public Integer getSpecial() {
    try {
      return Integer.parseInt(get(EpisodeProperty.special));
    } catch (Exception e) {
      return null;
    }
  }

  public Date getAirdate() {
    return Date.parse(get(EpisodeProperty.airdate), "yyyy-MM-dd");
  }

  @Override
  public String toString() {
    return fields.toString();
  }
}