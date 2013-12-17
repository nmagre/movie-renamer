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
import fr.free.movierenamer.mediainfo.MediaVideo;
import fr.free.movierenamer.renamer.FormatReplacing;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.Date;
import fr.free.movierenamer.utils.ScrapperUtils.AvailableApiIds;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.*;
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
  private List<IdInfo> idsInfo;

  public static enum MovieProperty implements InfoProperty {

    title(true),
    originalTitle,
    sortTitle,
    overview(true),
    releasedDate,// Obviously language dependent, but we don't care about for the moment
    rating,
    tagline(true),
    certification(true),
    certificationCode,// MotionPictureRating can transfrom it
    votes,
    budget,
    posterPath,
    collection,
    runtime;
    private boolean languageDepends;

    private MovieProperty() {
      languageDepends = false;
    }

    private MovieProperty(boolean languageDepends) {
      this.languageDepends = languageDepends;
    }

    @Override
    public boolean isLanguageDepends() {
      return languageDepends;
    }
  }

  public static enum MovieMultipleProperty implements InfoProperty {

    genres(true),
    countries,
    studios,
    tags;// Obviously language dependent, but we don't care about for the moment
    private final boolean languageDepends;

    private MovieMultipleProperty() {
      languageDepends = false;
    }

    private MovieMultipleProperty(boolean languageDepends) {
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

    private MotionPictureRating(String... rates) {
      this.rates = rates;
    }

    public String[] getRates() {
      return rates;
    }

    public String getRate(String code) {
      int pos = 0;
      for (String rate : USA.getRates()) {
        if (code.equals(rate)) {
          return rates[pos];
        }
        pos++;
      }
      return null;
    }

    public static String getMpaaCode(String code, MotionPictureRating scale) {

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
  protected final Map<MovieProperty, String> fields;
  protected final Map<MovieMultipleProperty, List<String>> multipleFields;

  protected MovieInfo() {
    // used by serializer
    this.fields = null;
    this.multipleFields = null;
  }

  public MovieInfo(List<IdInfo> idsInfo, Map<MovieProperty, String> fields, Map<MovieMultipleProperty, List<String>> multipleFields) {
    this.idsInfo = idsInfo;
    this.fields = (fields != null) ? new EnumMap<MovieProperty, String>(fields) : new EnumMap<MovieProperty, String>(MovieProperty.class);
    this.multipleFields = (multipleFields != null) ? new EnumMap<MovieMultipleProperty, List<String>>(multipleFields) : new EnumMap<MovieMultipleProperty, List<String>>(MovieMultipleProperty.class);
  }

  public String get(MovieProperty key) {
    return (fields != null) ? fields.get(key) : null;
  }

  public List<String> get(MovieMultipleProperty key) {
    return (List<String>) ((multipleFields != null) ? multipleFields.get(key) : new ArrayList<String>());
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

    for (IdInfo id : idsInfo) {
      if (id.getIdType().equals(idType)) {
        return id.getId();
      }
    }

    return null;
  }

//  public void addId(IdInfo id) {
//    if (getId(id.getIdType()) == null) {
//      idsInfo.add(id);
//    }
//  }
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
      return type.getRate(code);
    }
    return null;
  }

  public List<String> getGenres() {
    List<String> genres = get(MovieMultipleProperty.genres);
    return genres != null ? genres : new ArrayList<String>();
  }

  public List<String> getCountries() {
    List<String> countries = get(MovieMultipleProperty.countries);
    return countries != null ? countries : new ArrayList<String>();
  }

  public List<String> getStudios() {
    List<String> studios = get(MovieMultipleProperty.studios);
    return studios != null ? studios : new ArrayList<String>();
  }

  public List<String> getTags() {
    List<String> tags = get(MovieMultipleProperty.tags);
    return tags != null ? tags : new ArrayList<String>();
  }

  @Override
  public String getRenamedTitle(String format) {

    List<String> reservedCharacterList = Arrays.asList(new String[]{"<", ">", ":", "\"", "/", "\\", "|", "?", "*"});
    Settings settings = Settings.getInstance();

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
    replace.put("<a>", this.getActors());
    replace.put("<d>", this.getDirectors());
    replace.put("<g>", this.getGenres());
    replace.put("<c>", this.getCountries());

    // Media info
    if (mtag != null && mtag.libMediaInfo) {
      MediaVideo video = mtag.getMediaVideo();
      List<MediaAudio> audios = mtag.getMediaAudios();
      List<MediaSubTitle> subTitles = mtag.getMediaSubTitles();
      // Audio
      List<Integer> aChannels = new ArrayList<Integer>();
      List<String> aCodecs = new ArrayList<String>();
      List<String> aLanguages = new ArrayList<String>();
      List<String> aTitles = new ArrayList<String>();
      List<Integer> aBitrates = new ArrayList<Integer>();
      List<String> aRatemodes = new ArrayList<String>();
      // Subtitle
      List<String> sTitles = new ArrayList<String>();
      List<String> sLanguages = new ArrayList<String>();

      for (MediaAudio audio : audios) {
        aChannels.add(audio.getChannel());
        aCodecs.add(audio.getCodec());
        aLanguages.add(audio.getLanguage().getLanguage());
        aTitles.add(audio.getTitle());
        aBitrates.add(audio.getBitRate());
        aRatemodes.add(audio.getBitRateMode());
      }

      for (MediaSubTitle subTitle : subTitles) {
        sTitles.add(subTitle.getTitle());
        sLanguages.add(subTitle.getLanguage().getLanguage());
      }

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
      replace.put("<ach>", aChannels);
      replace.put("<ac>", aCodecs);
      replace.put("<al>", aLanguages);
      replace.put("<att>", aTitles);
      replace.put("<ab>", aBitrates);
      replace.put("<abm>", aRatemodes);
      // Subtitle
      replace.put("<stt>", sTitles);
      replace.put("<stl>", sLanguages);
    }

    FormatReplacing freplace = new FormatReplacing(replace);
    format = freplace.getReplacedString(format);

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
    if (settings.isReservedCharacter()) {
      for (String c : reservedCharacterList) {
        if (!c.equals(File.separator)) {
          format = format.replace(c, "");
        }
      }
    }

    if (settings.isMovieFilenameRmDupSpace()) {
      format = format.replaceAll("\\s+", " ");
    }

    if (settings.isMovieFilenameTrim()) {
      format = format.trim();
    }

    return format;
  }

  @Override
  public String toString() {
    return String.format("%s %s", fields.toString(), multipleFields.toString());
  }
}
