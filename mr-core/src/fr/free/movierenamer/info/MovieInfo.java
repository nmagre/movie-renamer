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
package fr.free.movierenamer.info;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import fr.free.movierenamer.mediainfo.MediaTag;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.Date;
import fr.free.movierenamer.utils.StringUtils;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Icon;

/**
 * Class MovieInfo
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class MovieInfo extends MediaInfo {

  private static final long serialVersionUID = 1L;

  public static enum MovieProperty {
    id,
    IMDB_ID,
    originalTitle,
    title,
    overview,
    releasedDate,
    rating,
    votes,
    budget,
    posterPath,
    runtime
  }

  protected final Map<MovieProperty, String> fields;

  protected final String[] genres;

  protected final Locale[] countries;

  protected MovieInfo() {
    // used by serializer
    this.fields = null;
    this.genres = null;
    this.countries = null;
  }

  public MovieInfo(Map<MovieProperty, String> fields, List<String> genres, List<Locale> countries) {
    this.fields = (fields != null) ? new EnumMap<MovieProperty, String>(fields) : new EnumMap<MovieInfo.MovieProperty, String>(MovieInfo.MovieProperty.class);
    this.genres = (genres != null) ? genres.toArray(new String[0]) : new String[0];
    this.countries = (countries != null) ? countries.toArray(new Locale[0]) : new Locale[0];
  }

  private String get(MovieProperty key) {
    return (fields != null) ? fields.get(key) : null;
  }

  public String getOriginalTitle() {
    return get(MovieProperty.originalTitle);
  }

  public String getTitle() {
    return get(MovieProperty.title);
  }

  public Integer getId() {
    try {
      return new Integer(get(MovieProperty.id));
    } catch (Exception e) {
      return null;
    }
  }

  public Integer getImdbId() {
    try {
      return new Integer(get(MovieProperty.IMDB_ID).substring(2));
    } catch (Exception e) {
      return null;
    }
  }

  public URI getPosterPath() {
    try {
      return new URL(get(MovieProperty.posterPath)).toURI();
    } catch (Exception e) {
      return null;
    }
  }

  public String getOverview() {
    return get(MovieProperty.overview);
  }

  public Integer getVotes() {
    try {
      return new Integer(get(MovieProperty.votes));
    } catch (Exception e) {
      return null;
    }
  }

  public Double getRating() {
    try {
      return new Double(get(MovieProperty.rating));
    } catch (Exception e) {
      return null;
    }
  }

  public Date getReleasedDate() {
    try {
      return Date.parse(get(MovieProperty.releasedDate), "yyyy-MM-dd");
    } catch (Exception e) {
      return null;
    }
  }

  public Integer getYear() {
    try {
      return Date.parse(get(MovieProperty.releasedDate), "yyyy").getYear();
    } catch (Exception e) {
      return null;
    }
  }

  public Integer getRuntime() {
    try {
      return new Integer(get(MovieProperty.runtime));
    } catch (Exception e) {
      return null;
    }
  }

  public List<String> getGenres() {
    return unmodifiableList(asList(genres));
  }

  public List<Locale> getCountries() {
    return unmodifiableList(asList(countries));
  }

  @Override
  public String getRenamedTitle(String format) {
    // TODO Apply case by tag to avoid this : <ot> IMDBID <tt> -> <ot> Imdbid <tt> (don't change user input)
    // Add more "sub-tag" like ":i" to ignore case : <ot:i>
    // or "~X" to remove caratere : <tt~2> (remove the first two caratere)
    // or "~w" to keep only number
    // ...
    // TODO Remove caracter which are not supported by all OS (need to add an option in settings for that)
    Settings settings = Settings.getInstance();
    String separator = settings.getMovieFilenameSeparator();
    int limit = settings.getMovieFilenameLimit();
    StringUtils.CaseConversionType renameCase = settings.getMovieFilenameCase();
    boolean trim = settings.isMovieFilenameTrim();

    String titlePrefix = "";
    String shortTitle = this.getTitle();

    Pattern pattern;
    Matcher matcher;

    if (shortTitle != null) {
      pattern = Pattern.compile("^((le|la|les|the)\\s|(l\\'))(.*)", Pattern.CASE_INSENSITIVE);
      matcher = pattern.matcher(shortTitle);
      if (matcher.find() && matcher.groupCount() >= 2) {
        titlePrefix = matcher.group(1).trim();
        shortTitle = matcher.group(matcher.groupCount()).trim();
      }
    }

    Map<String, Object> replace = new HashMap<String, Object>();
    replace.put("<t>", shortTitle);
    replace.put("<tp>", titlePrefix);
    replace.put("<st>", shortTitle);
    replace.put("<ot>", this.getOriginalTitle());
    replace.put("<tt>", this.getId());
    replace.put("<y>", this.getYear());
    replace.put("<rt>", this.getRuntime());
    replace.put("<ra>", this.getRating());
    // replace.put("<a>", this.getActorsString(separator, limit));
    // replace.put("<d>", this.getDirectorsString(separator, limit));
    // replace.put("<g>", this.getGenresString(separator, limit));
    // replace.put("<c>", this.getCountriesString(separator, limit));
    if (mtag != null) {
      replace.put("<mrt>", mtag.getDuration());
      replace.put("<mfs>", mtag.getFileSize());
      replace.put("<mc>", mtag.getVideoCodec());
      replace.put("<mdc>", mtag.getVideoDefinitionCategory());
      replace.put("<mf>", mtag.getVideoFormat());
      replace.put("<mfr>", mtag.getVideoFrameRate());
      replace.put("<mr>", mtag.getVideoResolution());
      replace.put("<mcf>", mtag.getContainerFormat());
      replace.put("<mach>", mtag.getTagString(MediaTag.TagList.AudioChannel, separator, limit));
      replace.put("<mac>", mtag.getTagString(MediaTag.TagList.AudioCodec, separator, limit));
      replace.put("<mal>", mtag.getTagString(MediaTag.TagList.AudioLanguage, separator, limit));
      replace.put("<matt>", mtag.getTagString(MediaTag.TagList.AudioTitleString, separator, limit));
      replace.put("<mtt>", mtag.getTagString(MediaTag.TagList.TextTitle, separator, limit));
    }

    // replace actors, directors, genres, coutries
    pattern = Pattern.compile("<([adcg])(\\d+)>");
    matcher = pattern.matcher(format);
    while (matcher.find()) {
      int n = Integer.parseInt(matcher.group(2));
      char x = matcher.group(1).charAt(0);
      switch (x) {
      case 'a':
        format = format.replaceAll("<a\\d+>", this.getActors().get(n - 1));
        break;
      case 'd':
        format = format.replaceAll("<d\\d+>", this.getDirectors().get(n - 1));
        break;
      case 'g':
        format = format.replaceAll("<g\\d+>", this.getGenres().get(n - 1));
        break;
      case 'c':
        format = format.replaceAll("<c\\d+>", this.getCountries().get(n - 1).getCountry());
        break;
      default:
        break;
      }
    }

    // replace media tags
    pattern = Pattern.compile("<(ma?[chtl]*)(\\d+)>");
    matcher = pattern.matcher(format);
    while (matcher.find()) {
      String tag = matcher.group(1);
      int stream = Integer.parseInt(matcher.group(2));
      stream--;// Offset start at 0, (E.g : For <mac3> -> stream == 2)
      if (tag.equals("mach")) {
        format = format.replaceAll("<mach\\d+>", mtag.getAudioChannels(stream));
      } else if (tag.equals("mac")) {
        format = format.replaceAll("<mac\\d+>", mtag.getAudioCodec(stream));
      } else if (tag.equals("mal")) {
        format = format.replaceAll("<mal\\d+>", mtag.getAudioLanguage(stream));
      } else if (tag.equals("matt")) {
        format = format.replaceAll("<matt\\d+>", mtag.getAudioTitle(stream));
      } else if (tag.equals("mtt")) {
        format = format.replaceAll("<mtt\\d+>", mtag.getTextTitle(stream));
      }
    }

    // la suite ;)
    for (String key : replace.keySet()) {
      Object val = replace.get(key);
      format = format.replaceAll(key, (val != null) ? val.toString() : "");
    }

    if (trim) {
      format = format.trim();
    }

    // Case conversion
    String res;
    switch (renameCase) {
    case UPPER:
      res = format.toUpperCase();
      break;
    case LOWER:
      res = format.toLowerCase();
      break;
    case FIRSTLO:
      res = StringUtils.capitalizedLetter(format, true);
      break;
    case FIRSTLA:
      res = StringUtils.capitalizedLetter(format, false);
      break;
    default:
      res = format;
      break;
    }

    // // extension
    // String fileName = getFile().getName();
    // String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
    // switch (renameCase) {
    // case UPPER:
    // ext = "." + ext.toUpperCase();
    // break;
    // default:
    // ext = "." + ext.toLowerCase();
    // break;
    // }
    //
    // // construct new file name
    // res += ext;
    //
    // if (Utils.isWindows()) {
    // res = res.replaceAll(":", "").replaceAll("/", "");
    // }

    if (settings.isMovieFilenameRmDupSpace()) {
      res = res.replaceAll("\\s+", " ");
    }
    return res;
  }

  @Override
  public String toString() {
    return fields.toString();
  }
}