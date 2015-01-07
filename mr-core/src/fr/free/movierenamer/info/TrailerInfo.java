/*
 * movie-renamer-core
 * Copyright (C) 2013-2014 Nicolas Magré
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

import fr.free.movierenamer.stream.AbstractStream.Quality;
import fr.free.movierenamer.utils.Date;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import java.net.URL;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Class TrailerInfo
 *
 * @author Nicolas Magré
 */
public class TrailerInfo extends Info {

  protected final Map<Quality, URL> streams;
  protected final Map<TrailerProperty, String> fields;
  protected final AvailableLanguages lang;

  public static enum TrailerProperty {

    title,
    runtime,
    overview,
    releasedDate,
    rating,
    provider;
  }

  public TrailerInfo(Map<TrailerProperty, String> fields, Map<Quality, URL> streams, AvailableLanguages lang) {
    this.streams = streams != null ? streams : new EnumMap<Quality, URL>(Quality.class);
    this.fields = fields != null ? fields : new EnumMap<TrailerProperty, String>(TrailerProperty.class);
    this.lang = lang;
  }

  public String get(final TrailerProperty key) {
    return (fields != null) ? fields.get(key) : null;
  }

  public void set(final TrailerProperty key, final String value) {
    fields.put(key, value);
  }

  public String getTitle() {
    return get(TrailerProperty.title);
  }

  public String getOverview() {
    return get(TrailerProperty.overview);
  }

  public AvailableLanguages getLanguage() {
    return lang;
  }

  public Date getReleasedDate() {
    try {
      return Date.parse(get(TrailerProperty.releasedDate), "yyyy-MM-dd");
    } catch (Exception e) {
    }
    return null;
  }

  public Double getRating() {
    try {
      return new Double(get(TrailerProperty.rating));
    } catch (Exception e) {
    }
    return null;
  }

  public String getRuntime() {
    return get(TrailerProperty.runtime);
  }

  public String getProvider() {
    return get(TrailerProperty.provider);
  }

  @Override
  public String toString() {
    return String.format("Lang=%s %s", lang, fields.toString());
  }
}
