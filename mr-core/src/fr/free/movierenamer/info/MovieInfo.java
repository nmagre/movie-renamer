/*
 * movie-renamer-core
 * Copyright (C) 2012-2013 Nicolas Magré
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

import fr.free.movierenamer.mediainfo.MediaAudio;
import fr.free.movierenamer.mediainfo.MediaSubTitle;
import fr.free.movierenamer.mediainfo.MediaTag;
import fr.free.movierenamer.mediainfo.MediaVideo;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.Date;
import fr.free.movierenamer.utils.ScrapperUtils.AvailableApiIds;
import fr.free.movierenamer.utils.StringUtils;
import java.net.URI;
import java.net.URL;
import java.util.*;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class MovieInfo
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class MovieInfo extends MediaInfo {

  private static final long serialVersionUID = 1L;

  public static enum MovieProperty {

    originalTitle,
    title,
    overview,
    releasedDate,
    rating,
    tagline,
    certification,
    certificationCode,
    votes,
    budget,
    posterPath,
    collection,
    runtime
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
    GERMANY("FSK 0", "FSK 6", "FSK 12", "FSK 16", "FSK 18");
    private String[] rates;

    private MotionPictureRating(String... rates) {
      this.rates = rates;
    }

    public String getRate(String code) {
      return getRate(code, MotionPictureRating.USA);
    }
    
    public String[] getRates() {
      return rates;
    }

    public String getRate(String code, MotionPictureRating scale) {
      int pos = 0;
      for (String rate : scale.rates) {
        if (code.equals(rate)) {
          return rates[pos];
        }
        pos++;
      }
      return null;
    }

    public static String getMpaaCode(String code, MotionPictureRating scale) {
      
      if(scale.equals(MotionPictureRating.USA)) {
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
  protected final Map<MovieProperty, String> fields;
  protected final IdInfo[] ids;
  protected final String[] genres;
  protected final Locale[] countries;
  protected final String[] studios;

  protected MovieInfo() {
    // used by serializer
    this.fields = null;
    this.ids = null;
    this.genres = null;
    this.countries = null;
    this.studios = null;
  }

  public MovieInfo(Map<MovieProperty, String> fields, List<IdInfo> ids, List<String> genres, List<Locale> countries, List<String> studios) {
    this.fields = (fields != null) ? new EnumMap<MovieProperty, String>(fields) : new EnumMap<MovieInfo.MovieProperty, String>(MovieInfo.MovieProperty.class);
    this.ids = (ids != null) ? ids.toArray(new IdInfo[0]) : new IdInfo[0];
    this.genres = (genres != null) ? genres.toArray(new String[0]) : new String[0];
    this.countries = (countries != null) ? countries.toArray(new Locale[0]) : new Locale[0];
    this.studios = (studios != null) ? studios.toArray(new String[0]) : new String[0];
  }

  public String get(MovieProperty key) {
    return (fields != null) ? fields.get(key) : null;
  }

  public String getOriginalTitle() {
    return get(MovieProperty.originalTitle);
  }

  public String getTitle() {
    return get(MovieProperty.title);
  }

  public String getIdString(AvailableApiIds idType) {
    Integer id = getId(idType);
    if (id != null) {
      return idType.getPrefix() + id;
    }
    return null;
  }

  public Integer getId(AvailableApiIds idType) {
    for (IdInfo id : ids) {
      if (id.getIdType().equals(idType)) {
        return id.getId();
      }
    }

    return null;
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

  public String getTagline() {
    return get(MovieProperty.tagline);
  }

  public String getCertification() {
    return get(MovieProperty.certification);
  }

  public String getCertification(MotionPictureRating type) {
    String code = get(MovieProperty.certificationCode);
    if (code != null && code.length() > 0) {
      return type.getRate(code, type);
    }
    return null;
  }

  public List<String> getGenres() {
    return unmodifiableList(asList(genres));
  }

  public List<Locale> getCountries() {
    return unmodifiableList(asList(countries));
  }

  public List<String> getStudios() {
    return unmodifiableList(asList(studios));
  }

  @Override
  public String getRenamedTitle(String format) {
    // TODO Add more "sub-tag" like ":i" to ignore case : <ot:i>
    // or "~X" to remove caratere : <tt~2> (remove the first two caratere)
    // or "~w" to keep only number
    // ...

    List<String> reservedCharacterList = Arrays.asList(new String[]{"<", ">", ":", "\"", "/", "\\", "|", "?", "*"});
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
    replace.put("<t>", this.getTitle());
    replace.put("<tp>", titlePrefix);
    replace.put("<st>", shortTitle);
    replace.put("<ot>", this.getOriginalTitle());
    replace.put("<tt>", this.getIdString(AvailableApiIds.IMDB));
    replace.put("<y>", this.getYear());
    replace.put("<rt>", this.getRuntime());
    replace.put("<ra>", this.getRating());
    // replace.put("<a>", this.getActorsString(separator, limit));
    // replace.put("<d>", this.getDirectorsString(separator, limit));
    // replace.put("<g>", this.getGenresString(separator, limit));
    // replace.put("<c>", this.getCountriesString(separator, limit));
    if (mtag != null && mtag.libMediaInfo) {
      MediaVideo video = mtag.getMediaVideo();
      // General
      replace.put("<vrt>", mtag.getDuration());
      replace.put("<vcf>", mtag.getContainerFormat());
//      replace.put("<mfs>", mtag.getFileSize());
      // Video
      replace.put("<vc>", video.getCodec());
      replace.put("<vd>", video.getVideoDefinition());
      replace.put("<vr>", video.getVideoResolution());
      replace.put("<vfr>", video.getFrameRate());
      replace.put("<vst>", video.getScanType());
      replace.put("<vfc>", video.getFrameCount());
      replace.put("<vh>", video.getHeight());
      replace.put("<vw>", video.getWidth());
      replace.put("<var>", video.getAspectRatio());
      // Audio
      replace.put("<asc>", mtag.getTagString(MediaTag.Tags.AudioStreamCount, separator, limit));
      replace.put("<ach>", mtag.getTagString(MediaTag.Tags.AudioChannels, separator, limit));
      replace.put("<ac>", mtag.getTagString(MediaTag.Tags.AudioCodec, separator, limit));
      replace.put("<al>", mtag.getTagString(MediaTag.Tags.AudioLanguage, separator, limit));
      replace.put("<att>", mtag.getTagString(MediaTag.Tags.AudioTitle, separator, limit));
      replace.put("<ab>", mtag.getTagString(MediaTag.Tags.AudioBitRate, separator, limit));
      replace.put("<abm>", mtag.getTagString(MediaTag.Tags.AudioBitRateMode, separator, limit));
      // Subtitle
      replace.put("<stc>", mtag.getTagString(MediaTag.Tags.TextStreamCount, separator, limit));
      replace.put("<stt>", mtag.getTagString(MediaTag.Tags.TextTitle, separator, limit));
      replace.put("<stl>", mtag.getTagString(MediaTag.Tags.TextLanguage, separator, limit));
    }

    // replace actors, directors, genres, coutries
    pattern = Pattern.compile("<([adcg])(\\d+)>");
    matcher = pattern.matcher(format);
    while (matcher.find()) {
      int n = Integer.parseInt(matcher.group(2));
      char x = matcher.group(1).charAt(0);
      switch (x) {
        case 'a':
          format = format.replaceAll("<a" + n + ">", applyCase(this.getActors().get(n - 1), renameCase));
          break;
        case 'd':
          format = format.replaceAll("<d" + n + ">", applyCase(this.getDirectors().get(n - 1), renameCase));
          break;
        case 'g':
          format = format.replaceAll("<g" + n + ">", applyCase(this.getGenres().get(n - 1), renameCase));
          break;
        case 'c':
          format = format.replaceAll("<c" + n + ">", applyCase(this.getCountries().get(n - 1).getCountry(), renameCase));
          break;
        default:
          break;
      }
    }

    if (mtag != null && mtag.libMediaInfo) {
      // replace media tags
      pattern = Pattern.compile("<([as]t?[scltb]*)(\\d+)>");
      matcher = pattern.matcher(format);
      List<MediaAudio> audios = mtag.getMediaAudios();
      List<MediaSubTitle> subTitles = mtag.getMediaSubTitles();

      while (matcher.find()) {
        String tag = matcher.group(1);
        int stream = Integer.parseInt(matcher.group(2));
        if (stream <= 0 || (tag.startsWith("a") && stream > audios.size()) || (tag.startsWith("s") && stream > subTitles.size())) {
          continue;
        }

        if (tag.equals("ach")) {
          format = format.replaceAll("<ach" + stream + ">", "" + audios.get(stream - 1).getChannel());
        } else if (tag.equals("ac")) {
          format = format.replaceAll("<ac" + stream + ">", applyCase(audios.get(stream - 1).getCodec(), renameCase));
        } else if (tag.equals("al")) {
          format = format.replaceAll("<al" + stream + ">", applyCase(audios.get(stream - 1).getLanguage().getLanguage(), renameCase));
        } else if (tag.equals("att")) {
          format = format.replaceAll("<att" + stream + ">", applyCase(audios.get(stream - 1).getTitle(), renameCase));
        } else if (tag.equals("ab")) {
          format = format.replaceAll("<ab" + stream + ">", applyCase("" + audios.get(stream - 1).getBitRate(), renameCase));
        } else if (tag.equals("abm")) {
          format = format.replaceAll("<abm" + stream + ">", applyCase(audios.get(stream - 1).getBitRateMode(), renameCase));
        } else if (tag.equals("stt")) {
          format = format.replaceAll("<stt" + stream + ">", applyCase(subTitles.get(stream - 1).getTitle(), renameCase));
        } else if (tag.equals("stl")) {
          format = format.replaceAll("<stl" + stream + ">", applyCase(subTitles.get(stream - 1).getLanguage().getLanguage(), renameCase));
        }
      }
    }

    // la suite ;)
    for (String key : replace.keySet()) {
      Object val = replace.get(key);
      format = format.replaceAll(key, (val != null) ? applyCase(val.toString(), renameCase) : "");
    }

    if (trim) {
      format = format.trim();
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
      format = format.replaceAll("\\s+", " ");
    }

    if (settings.isReservedCharacter()) {
      for (String c : reservedCharacterList) {
        format = format.replace(c, "");
      }
    }
    return format;
  }

  private String applyCase(String str, StringUtils.CaseConversionType renameCase) {
    String res = "";
    switch (renameCase) {
      case UPPER:
        res = str.toUpperCase();
        break;
      case LOWER:
        res = str.toLowerCase();
        break;
      case FIRSTLO:
        res = StringUtils.capitalizedLetter(str, true);
        break;
      case FIRSTLA:
        res = StringUtils.capitalizedLetter(str, false);
        break;
      default:
        res = str;
        break;
    }
    return res;
  }

  @Override
  public String toString() {
    return fields.toString();
  }
}