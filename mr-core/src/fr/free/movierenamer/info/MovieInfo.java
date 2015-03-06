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
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.DateFormat;
import fr.free.movierenamer.utils.ScraperUtils.AvailableApiIds;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

/**
 * Class MovieInfo
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class MovieInfo extends VideoInfo {

  private static final long serialVersionUID = 1L;
  protected final Map<MovieProperty, String> fields;
  protected final Map<MovieMultipleProperty, List<String>> multipleFields;
  protected List<CastingInfo> directors;
  protected List<CastingInfo> actors;
  protected List<CastingInfo> writers;
  protected List<TrailerInfo> trailers;

  public static enum MovieProperty implements InfoProperty {

    sortTitle,
    overview(true),
    tagline(true),
    certification(true),
    certificationCode,// MotionPictureRating can transfrom it
    releasedDate,
    votes,
    budget,
    posterPath,
    collection,
    runtime;
    private final boolean languageDepends;

    private MovieProperty() {
      languageDepends = false;
    }

    private MovieProperty(final boolean languageDepends) {
      this.languageDepends = languageDepends;
    }

    @Override
    public boolean isLanguageDepends() {
      return languageDepends;
    }
  }

  public static enum MovieMultipleProperty implements MultipleInfoProperty {

    genres(true),
    countries(true),
    studios,
    tags;// Obviously language dependent, but we don't care about for the moment
    private final boolean languageDepends;

    private MovieMultipleProperty() {
      languageDepends = false;
    }

    private MovieMultipleProperty(final boolean languageDepends) {
      this.languageDepends = languageDepends;
    }

    @Override
    public boolean isLanguageDepends() {
      return languageDepends;
    }
  }
  /*
   * @see http://en.wikipedia.org/wiki/Motion_picture_rating_system
   */

  public static enum MotionPictureRating {// TODO

    USA("G", "PG", "PG-13", "R", "NC-17"),
    FRANCE("U", "-10", "-12", "-16", "-18"),
    UK("U", "PG", "12", "15", "18"),
    SPAIN("APTA", "7", "13", "16", "18"),
    PORTUGAL("M/4", "M/6", "M/12", "M/16", "M/18"),
    GERMANY("FSK 0", "FSK 6", "FSK 12", "FSK 16", "FSK 18"),
    RUSSIA("0+", "6+", "12+", "16+", "18+");
    private final String[] rates;

    private MotionPictureRating(final String... rates) {
      this.rates = rates;
    }

    private String[] getRates() {
      return rates;
    }

    public String getRate(final String code) {
      int pos = 0;
      for (String rate : USA.getRates()) {
        if (code.equals(rate)) {
          return rates[pos];
        }
        pos++;
      }
      return null;
    }

    public static String getMpaaCode(final String code, final MotionPictureRating scale) {

      if (scale.equals(MotionPictureRating.USA)) {
        return code;
      }

      int pos = 0;
      for (String rate : scale.getRates()) {
        if (code.equals(rate)) {
          return USA.getRates()[pos];
        }
        pos++;
      }
      return null;
    }
  }

  protected MovieInfo() {
    // used by serializer
    super(null, null);
    this.fields = null;
    this.multipleFields = null;
  }

  public MovieInfo(Map<MediaProperty, String> mediaFields, List<IdInfo> idsInfo, Map<MovieProperty, String> fields, Map<MovieMultipleProperty, List<String>> multipleFields) {
    this(mediaFields, idsInfo, fields, multipleFields, null);
  }

  public MovieInfo(Map<MediaProperty, String> mediaFields, List<IdInfo> idsInfo, Map<MovieProperty, String> fields, Map<MovieMultipleProperty, List<String>> multipleFields, List<CastingInfo> casting) {
    super(mediaFields, idsInfo);
    this.fields = (fields != null) ? fields : new EnumMap<MovieProperty, String>(MovieProperty.class);
    this.multipleFields = (multipleFields != null) ? multipleFields : new EnumMap<MovieMultipleProperty, List<String>>(MovieMultipleProperty.class);
    this.casting = (casting != null) ? casting.toArray(new CastingInfo[0]) : new CastingInfo[0];
    setMediaCasting();
  }

  public String get(final MovieProperty key) {
    return (fields != null) ? fields.get(key) : null;
  }

  public void set(MovieProperty key, String value) {
    fields.put((MovieProperty) key, value);
  }

  public void set(MovieMultipleProperty key, String value) {
    multipleFields.put((MovieMultipleProperty) key, Arrays.asList(value.split(", ")));
  }

  @Override
  public MediaType getMediaType() {
    return MediaType.MOVIE;
  }

  @Override
  protected void unsetUnsupportedLanguageInfo() {
    Settings settings = Settings.getInstance();
    Iterator<Entry<MovieProperty, String>> it = fields.entrySet().iterator();
    Entry<MovieProperty, String> entry;

    while (it.hasNext()) {
      entry = it.next();
      if (entry.getKey().isLanguageDepends()) {
        it.remove();
      }
    }

    Iterator<Entry<MovieMultipleProperty, List<String>>> iterator = multipleFields.entrySet().iterator();
    Entry<MovieMultipleProperty, List<String>> mentry;
    MovieMultipleProperty key;

    while (iterator.hasNext()) {
      mentry = iterator.next();
      key = mentry.getKey();
      if (key.equals(MovieMultipleProperty.tags) && settings.isGetTmdbTagg()) {
        continue;
      }

      if (key.isLanguageDepends()) {
        iterator.remove();
      }
    }
  }

  public List<String> get(final MovieMultipleProperty key) {
    return (multipleFields != null && multipleFields.get(key) != null) ? multipleFields.get(key) : new ArrayList<String>();
  }

  public URI getPosterPath() {
    try {
      return new URL(get(MovieProperty.posterPath)).toURI();
    } catch (Exception e) {
    }
    return null;
  }

  public String getOverview() {
    return get(MovieProperty.overview);
  }

  public Integer getVotes() {
    try {
      return Integer.valueOf(get(MovieProperty.votes));
    } catch (Exception e) {
    }
    return null;
  }

  public DateFormat getReleasedDate() {
    try {
      return DateFormat.parse(get(MovieProperty.releasedDate), "yyyy-MM-dd");
    } catch (Exception e) {
    }
    return null;
  }

  public Integer getRuntime() {
    try {
      return Integer.valueOf(get(MovieProperty.runtime));
    } catch (Exception e) {
    }
    return null;
  }

  public String getTagline() {
    return get(MovieProperty.tagline);
  }

  public String getCertification() {
    return get(MovieProperty.certification);
  }

  public String getCertification(final MotionPictureRating type) {
    final String code = get(MovieProperty.certificationCode);
    if (code != null && code.length() > 0) {
      return type.getRate(code);
    }
    return null;
  }

  public List<String> getGenres() {
    final List<String> genres = get(MovieMultipleProperty.genres);
    return genres != null ? genres : new ArrayList<String>();
  }

  public List<String> getCountries() {
    final List<String> countries = get(MovieMultipleProperty.countries);
    return countries != null ? countries : new ArrayList<String>();
  }

  public List<String> getStudios() {
    final List<String> studios = get(MovieMultipleProperty.studios);
    return studios != null ? studios : new ArrayList<String>();
  }

  public List<String> getTags() {
    final List<String> tags = get(MovieMultipleProperty.tags);
    return tags != null ? tags : new ArrayList<String>();
  }

  public List<CastingInfo> getActors() {
    return actors;
  }

  public List<CastingInfo> getDirectors() {
    return directors;
  }

  public List<CastingInfo> getWriters() {
    return writers;
  }

  @Override
  protected void setMediaCasting() {
    actors = new ArrayList<CastingInfo>();
    directors = new ArrayList<CastingInfo>();
    writers = new ArrayList<CastingInfo>();

    if (casting != null) {
      for (CastingInfo cast : casting) {
        if (cast.isActor()) {
          actors.add(cast);
        } else if (cast.isDirector()) {
          directors.add(cast);
        } else if (cast.isWriter()) {
          writers.add(cast);
        }
      }
    }
  }

  @Override
  protected void setReplaceMap(Map<String, Object> replace) {
    replace.put("ot", this.getOriginalTitle());
    replace.put("tt", this.getIdString(AvailableApiIds.IMDB));
    replace.put("imdb", this.getIdString(AvailableApiIds.IMDB));
    replace.put("allo", this.getIdString(AvailableApiIds.ALLOCINE));
    replace.put("kino", this.getIdString(AvailableApiIds.KINOPOISK));
    replace.put("rotten", this.getIdString(AvailableApiIds.ROTTENTOMATOES));
    replace.put("moviedb", this.getIdString(AvailableApiIds.THEMOVIEDB));
    replace.put("y", this.getYear());
    replace.put("rt", this.getRuntime());
    replace.put("ra", this.getRating());
    replace.put("a", this.getActors());
    replace.put("d", this.getDirectors());
    replace.put("g", this.getGenres());
    replace.put("c", this.getCountries());
    replace.put("mpaa", this.getCertification(MotionPictureRating.USA));
  }

  @Override
  public String toString() {
    return super.toString() + String.format(" %s %s", fields.toString(), multipleFields.toString());
  }
}
