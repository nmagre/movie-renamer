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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.free.movierenamer.info;

import fr.free.movierenamer.searchinfo.Media.MediaType;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.free.movierenamer.utils.DateFormat;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * Class TvShowInfo
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class TvShowInfo extends VideoInfo {

  private static final long serialVersionUID = 1L;

  public static enum TvShowProperty implements InfoProperty {// TODO

    id,
    IMDB_ID,
    firstAired,
    genre,
    language,
    overview,
    rating,
    votes,
    runtime,
    name,
    status,
    posterPath;

    private final boolean languageDepends;

    private TvShowProperty() {
      languageDepends = false;
    }

    private TvShowProperty(final boolean languageDepends) {
      this.languageDepends = languageDepends;
    }

    @Override
    public boolean isLanguageDepends() {
      return languageDepends;
    }
  }
  protected final Map<TvShowProperty, String> fields;

  protected TvShowInfo() {
    // used by serializer
    super(null, null);
    this.fields = null;
  }

  public TvShowInfo(final Map<TvShowProperty, String> fields, List<IdInfo> idsInfo) {
    super(null, null);// FIXME
    this.fields = (fields != null) ? new EnumMap<>(fields) : new EnumMap<TvShowProperty, String>(TvShowProperty.class);
  }

  private String get(final TvShowProperty key) {
    return (fields != null) ? fields.get(key) : null;
  }

  @Override
  protected void unsetUnsupportedLanguageInfo() {// TODO
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public MediaType getMediaType() {
    return MediaType.TVSHOW;
  }

  public Integer getId() {
    try {
      return Integer.parseInt(get(TvShowProperty.id));
    } catch (NumberFormatException e) {
    }
    return null;
  }

  public List<String> getGenres() {
    return split(get(TvShowProperty.genre));
  }

  protected List<String> split(final String values) {// FIXME ya pas déjà ça dans StringUtils ?
    final List<String> items = new ArrayList<>();
    if (values != null && values.length() > 0) {
      for (String it : values.split("[|]")) {
        it = it.trim();
        if (!it.isEmpty()) {
          items.add(it);
        }
      }
    }
    return items;
  }

  public DateFormat getFirstAired() {
    return DateFormat.parse(get(TvShowProperty.firstAired), "yyyy-MM-dd");
  }

  public Integer getImdbId() {
    try {
      return Integer.parseInt(get(TvShowProperty.IMDB_ID).substring(2));
    } catch (NumberFormatException e) {
    }
    return null;
  }

  public Locale getLanguage() {
    try {
      return new Locale(get(TvShowProperty.language));
    } catch (Exception e) {
    }
    return null;
  }

  public String getOverview() {
    return get(TvShowProperty.overview);
  }

  public Double getRating() {
    try {
      return Double.parseDouble(get(TvShowProperty.rating));
    } catch (NumberFormatException e) {
    }
    return null;
  }

  public Integer getVotes() {
    try {
      return Integer.parseInt(get(TvShowProperty.votes));
    } catch (NumberFormatException e) {
    }
    return null;
  }

  public String getName() {
    return get(TvShowProperty.name);
  }

  public String getStatus() {
    return get(TvShowProperty.status);
  }

  public URI getPosterPath() {
    try {
      return new URL(get(TvShowProperty.posterPath)).toURI();
    } catch (MalformedURLException | URISyntaxException e) {
    }
    return null;
  }

  @Override
  protected void setMediaCasting() {// TODO
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected void addFormatTokens(Map<String, Object> tokens) {
    // TODO
  }

  @Override
  public String toString() {
    return fields.toString();
  }
}
