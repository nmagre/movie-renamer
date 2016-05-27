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
  protected final Map<MovieProperty, String> movieInfo;
  protected final Map<MovieMultipleProperty, List<String>> movieMultipleInfo;
  protected List<CastingInfo> directors;
  protected List<CastingInfo> actors;
  protected List<CastingInfo> writers;
  protected List<TrailerInfo> trailers;

  public static enum MovieProperty implements MediaInfoProperty {

    sortTitle,
    overview(true),
    tagline(true),
    certification(true),
    certificationCode,// MotionPictureRating can transfrom it
    votes,
    budget,
    award(true),
    posterPath,
    collection;
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
    this(null, null, null);
  }

  public MovieInfo(Map<MediaInfoProperty, String> info, Map<MovieMultipleProperty, List<String>> multipleInfo, List<IdInfo> idsInfo) {
    this(info, multipleInfo, idsInfo, null);
  }

  public MovieInfo(Map<MediaInfoProperty, String> info, Map<MovieMultipleProperty, List<String>> multipleInfo, List<IdInfo> idsInfo, List<CastingInfo> casting) {
    super(info, idsInfo);
    movieInfo = new EnumMap<>(MovieProperty.class);
    movieMultipleInfo = (multipleInfo != null) ? multipleInfo : new EnumMap<MovieMultipleProperty, List<String>>(MovieMultipleProperty.class);
    this.casting = (casting != null) ? casting.toArray(new CastingInfo[casting.size()]) : new CastingInfo[0];

    if (info == null) {
      info = new HashMap<>();
    }

    Iterator<Map.Entry<MediaInfoProperty, String>> it = info.entrySet().iterator();
    Map.Entry<MediaInfoProperty, String> entry;
    MediaInfoProperty property;
    while (it.hasNext()) {
      entry = it.next();
      property = entry.getKey();
      if (property instanceof MovieProperty) {
        movieInfo.put((MovieProperty) property, entry.getValue());
        it.remove();
      }
    }

    setMediaCasting();
  }

  public List<String> get(final MovieMultipleProperty key) {
    return movieMultipleInfo.get(key);
  }

  @Override
  public String get(MediaInfoProperty key) {
    String res = super.get(key);
    if (res != null) {
      return res;
    }

    if (!(key instanceof MovieProperty)) {
      return null;
    }
    return movieInfo.get((MovieProperty) key);
  }

  @Override
  public void set(MediaInfoProperty key, String value) {
    if (!(key instanceof MovieProperty)) {
      return;
    }
    movieInfo.put((MovieProperty) key, value);
  }

  public void set(MovieMultipleProperty key, List<String> values) {
    movieMultipleInfo.put(key, values);
  }

  @Override
  public MediaType getMediaType() {
    return MediaType.MOVIE;
  }

  @Override
  protected void unsetUnsupportedLanguageInfo() {
    Settings settings = Settings.getInstance();
    Iterator<Entry<MovieProperty, String>> it = movieInfo.entrySet().iterator();
    Entry<MovieProperty, String> entry;

    while (it.hasNext()) {
      entry = it.next();
      if (entry.getKey().isLanguageDepends()) {
        it.remove();
      }
    }

    Iterator<Entry<MovieMultipleProperty, List<String>>> iterator = movieMultipleInfo.entrySet().iterator();
    Entry<MovieMultipleProperty, List<String>> mentry;
    MovieMultipleProperty key;

    while (iterator.hasNext()) {
      mentry = iterator.next();
      key = mentry.getKey();
      if (key.equals(MovieMultipleProperty.tags) && settings.isGetTmdbTag(MediaType.MOVIE)) {
        continue;
      }

      if (key.isLanguageDepends()) {
        iterator.remove();
      }
    }
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
      return Integer.parseInt(get(MovieProperty.votes));
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

  public String getCollection() {
    return get(MovieProperty.collection);
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
    actors = new ArrayList<>();
    directors = new ArrayList<>();
    writers = new ArrayList<>();

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
  protected void addFormatTokens(Map<String, Object> tokens) {
    tokens.put("ot", this.getOriginalTitle());
    tokens.put("tt", this.getIdString(AvailableApiIds.IMDB));
    tokens.put("imdb", this.getIdString(AvailableApiIds.IMDB));
    tokens.put("allo", this.getIdString(AvailableApiIds.ALLOCINE));
    tokens.put("kino", this.getIdString(AvailableApiIds.KINOPOISK));
    tokens.put("rotten", this.getIdString(AvailableApiIds.ROTTENTOMATOES));
    tokens.put("tmdb", this.getIdString(AvailableApiIds.THEMOVIEDB));
    tokens.put("y", this.getYear());
    tokens.put("rt", this.getRuntime());
    tokens.put("ra", this.getRating());
    tokens.put("a", this.getActors());
    tokens.put("d", this.getDirectors());
    tokens.put("g", this.getGenres());
    tokens.put("c", this.getCountries());
    tokens.put("mpaa", this.getCertification(MotionPictureRating.USA));
    tokens.put("co", this.getCollection());
  }

  @Override
  public String toString() {
    return super.toString() + String.format(" %s %s", movieInfo.toString(), movieMultipleInfo.toString());
  }
}
