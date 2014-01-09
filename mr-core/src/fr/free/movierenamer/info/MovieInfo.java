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

import fr.free.movierenamer.mediainfo.MediaAudio;
import fr.free.movierenamer.mediainfo.MediaSubTitle;
import fr.free.movierenamer.mediainfo.MediaVideo;
import fr.free.movierenamer.renamer.FormatReplacing;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.Date;
import fr.free.movierenamer.utils.ScrapperUtils.AvailableApiIds;
import fr.free.movierenamer.utils.StringUtils;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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

    private MovieProperty(final boolean languageDepends) {
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

  public String get(final MovieProperty key) {
    return (fields != null) ? fields.get(key) : null;
  }

  public void set(final InfoProperty key, final String value) {

    if (key instanceof MovieProperty) {
      fields.put((MovieProperty) key, value);
    } else if (key instanceof MovieMultipleProperty) {
      multipleFields.put((MovieMultipleProperty) key, Arrays.asList(value.split(", ")));
    }
  }

  @Override
  public InfoType getInfoType() {
    return InfoType.MOVIE;
  }

  public List<String> get(final MovieMultipleProperty key) {
    return (List<String>) ((multipleFields != null && multipleFields.get(key) != null) ? multipleFields.get(key) : new ArrayList<String>());
  }

  public String getOriginalTitle() {
    return get(MovieProperty.originalTitle);
  }

  public String getTitle() {
    return get(MovieProperty.title);
  }

  public String getIdString(final AvailableApiIds idType) {
    final Integer id = getId(idType);
    if (id != null) {
      return idType.getPrefix() + id;
    }
    return null;
  }

  public Integer getId(final AvailableApiIds idType) {

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
    } catch (MalformedURLException e) {
    } catch (URISyntaxException e) {
    }
    return null;
  }

  public String getOverview() {
    return get(MovieProperty.overview);
  }

  public Integer getVotes() {
    try {
      return Integer.valueOf(get(MovieProperty.votes));
    } catch (NumberFormatException e) {
    }
    return null;
  }

  public Double getRating() {
    try {
      return new Double(get(MovieProperty.rating));
    } catch (NumberFormatException e) {
    }
    return null;
  }

  public Date getReleasedDate() {
    try {
      return Date.parse(get(MovieProperty.releasedDate), "yyyy-MM-dd");
    } catch (Exception e) {
    }
    return null;
  }

  public Integer getYear() {
    try {
      return Date.parse(get(MovieProperty.releasedDate), "yyyy").getYear();
    } catch (Exception e) {
    }
    return null;
  }

  public Integer getRuntime() {
    try {
      return Integer.valueOf(get(MovieProperty.runtime));
    } catch (NumberFormatException e) {
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

  @Override
  public String getRenamedTitle(String format) {
    final Settings settings = Settings.getInstance();
    return getRenamedTitle(format, settings.getMovieFilenameCase(), settings.getMovieFilenameSeparator(), settings.getMovieFilenameLimit(),
            settings.isReservedCharacter(), settings.isMovieFilenameRmDupSpace(), settings.isMovieFilenameTrim());
  }

  @Override
  public String getRenamedTitle(final String format, final StringUtils.CaseConversionType renameCase, final String filenameSeparator,
          final int filenameLimit, final boolean reservedCharacter, final boolean rmDupSpace, final boolean trim) {

    final List<String> reservedCharacterList = Arrays.asList(new String[]{"<", ">", ":", "\"", "/", "\\", "|", "?", "*"});

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

    final Map<String, Object> replace = new HashMap<String, Object>();
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
    replace.put("<mpaa>", this.getCertification(MotionPictureRating.USA));

    // Media info
    if (mtag != null && mtag.libMediaInfo) {
      final MediaVideo video = mtag.getMediaVideo();
      final List<MediaAudio> audios = mtag.getMediaAudios();
      final List<MediaSubTitle> subTitles = mtag.getMediaSubTitles();
      // Audio
      final List<String> aChannels = new ArrayList<String>();
      final List<String> aCodecs = new ArrayList<String>();
      final List<String> aLanguages = new ArrayList<String>();
      final List<String> aTitles = new ArrayList<String>();
      final List<Integer> aBitrates = new ArrayList<Integer>();
      final List<String> aRatemodes = new ArrayList<String>();
      // Subtitle
      final List<String> sTitles = new ArrayList<String>();
      final List<String> sLanguages = new ArrayList<String>();

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

    final FormatReplacing freplace = new FormatReplacing(replace, renameCase, filenameSeparator, filenameLimit);
    String res = freplace.getReplacedString(format);

    if (reservedCharacter) {
      for (String c : reservedCharacterList) {
        if (!c.equals(File.separator)) {
          res = res.replace(c, "");
        }
      }
    }

    if (rmDupSpace) {
      res = res.replaceAll("\\s+", " ");
    }

    if (trim) {
      res = res.trim();
    }

    return res;
  }

  @Override
  public String toString() {
    return String.format("%s %s", fields.toString(), multipleFields.toString());
  }
}
