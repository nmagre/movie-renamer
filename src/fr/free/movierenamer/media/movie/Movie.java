/*
 * Movie Renamer
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
package fr.free.movierenamer.media.movie;

import javax.swing.JTextField;

import fr.free.movierenamer.matcher.MovieNameMatcher;
import fr.free.movierenamer.media.MediaImage.MediaImageType;
import fr.free.movierenamer.media.*;
import fr.free.movierenamer.media.mediainfo.MITag;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Utils;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class Movie
 *
 * @author Nicolas Magré
 */
public class Movie extends Media<MovieInfo>{

  public enum NFO {
    XBMC,
    MEDIAPORTAL,
    YAMJ
  }

  /**
   * Constructor arguments
   *
   * @param movieFile A movie file
   * @param renameField 
   */
  public Movie(MediaFile movieFile, JTextField renameField) {
    super(movieFile, new MovieInfo(), renameField);
    MovieNameMatcher nameMatcher = new MovieNameMatcher(movieFile, conf.mediaNameFilters);
    setSearch(nameMatcher.getMovieName());
    setYear(nameMatcher.getYear());
  }

  public MITag getMediaTag() {
    return mtag;
  }

  /**
   * Get renamed movie title
   *
   * @param regExp Mr rename rgex
   * @return Movie title renamed
   */
  @Override
  public String getRenamedTitle(String regExp) {// TODO ajouter le dossier (dépend des options)
    String separator = conf.movieFilenameSeparator;
    int limit = conf.movieFilenameLimit;
    Utils.CaseConversionType renameCase = conf.movieFilenameCase;
    boolean trim = conf.movieFilenameTrim;

    String titlePrefix = "";
    String shortTitle = mediaInfo.getTitle();

    Pattern pattern = Pattern.compile("^((le|la|les|the)\\s|(l\\'))(.*)", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(mediaInfo.getTitle());
    if (matcher.find() && matcher.groupCount() >= 2) {
      titlePrefix = matcher.group(1).trim();
      shortTitle = matcher.group(matcher.groupCount()).trim();
    }

    String runtime = "";
    if (!mediaInfo.getRuntime().equals("-1")) {
      runtime += mediaInfo.getRuntime();
    }

    String[][] replace = new String[][]{
      {"<t>", mediaInfo.getTitle()},
      {"<tp>", titlePrefix},
      {"<st>", shortTitle},
      {"<ot>", mediaInfo.getOriginalTitle()},
      {"<tt>", mediaId.getID()},
      {"<y>", mediaInfo.getYear()},
      {"<rt>", runtime},
      {"<ra>", mediaInfo.getRating()},
      {"<a>", mediaInfo.getActorsString(separator, limit)},
      {"<d>", mediaInfo.getDirectorsString(separator, limit)},
      {"<g>", mediaInfo.getGenresString(separator, limit)},
      {"<c>", mediaInfo.getCountriesString(separator, limit)},
      {"<mrt>", mtag.getDuration()},
      {"<mfs>", mtag.getFileSize()},
      {"<mc>", mtag.getVideoCodec()},
      {"<mdc>", mtag.getVideoDefinitionCategory()},
      {"<mf>", mtag.getVideoFormat()},
      {"<mfr>", mtag.getVideoFrameRate()},
      {"<mr>", mtag.getVideoResolution()},
      {"<mcf>", mtag.getContainerFormat()},
      {"<mach>", mtag.getTagString(MITag.TagList.AudioChannel, separator, limit)},
      {"<mac>", mtag.getTagString(MITag.TagList.AudioCodec, separator, limit)},
      {"<mal>", mtag.getTagString(MITag.TagList.AudioLanguage, separator, limit)},
      {"<matt>", mtag.getTagString(MITag.TagList.AudioTitleString, separator, limit)},
      {"<mtt>", mtag.getTagString(MITag.TagList.TextTitle, separator, limit)}
    };

    //replace actors, directors, genres, coutries
    pattern = Pattern.compile("<([adcg])(\\d+)>");
    matcher = pattern.matcher(regExp);
    while (matcher.find()) {
      int n = Integer.parseInt(matcher.group(2));
      char x = matcher.group(1).charAt(0);
      switch (x) {
        case 'a':
          regExp = regExp.replaceAll("<a\\d+>", mediaInfo.getActorN(n));
          break;
        case 'd':
          regExp = regExp.replaceAll("<d\\d+>", mediaInfo.getDirectorN(n));
          break;
        case 'g':
          regExp = regExp.replaceAll("<g\\d+>", mediaInfo.getGenreN(n));
          break;
        case 'c':
          regExp = regExp.replaceAll("<c\\d+>", mediaInfo.getCountryN(n));
          break;
        default:
          break;
      }
    }

    //replace media tags
    pattern = Pattern.compile("<(ma?[chtl]*)(\\d+)>");
    matcher = pattern.matcher(regExp);
    while (matcher.find()) {
      String tag = matcher.group(1);
      int stream = Integer.parseInt(matcher.group(2));
      stream--;// Offset start at 0, (E.g : For <mac3> -> stream == 2)
      if (tag.equals("mach")) {
        regExp = regExp.replaceAll("<mach\\d+>", mtag.getAudioChannels(stream));
      } else if (tag.equals("mac")) {
        regExp = regExp.replaceAll("<mac\\d+>", mtag.getAudioCodec(stream));
      } else if (tag.equals("mal")) {
        regExp = regExp.replaceAll("<mal\\d+>", mtag.getAudioLanguage(stream));
      } else if (tag.equals("matt")) {
        regExp = regExp.replaceAll("<matt\\d+>", mtag.getAudioTitle(stream));
      } else if (tag.equals("mtt")) {
        regExp = regExp.replaceAll("<mtt\\d+>", mtag.getTextTitle(stream));
      }
    }

    //la suite ;)
    for (int i = 0; i < replace.length; i++) {
      regExp = regExp.replaceAll(replace[i][0], replace[i][1]);
    }

    if (trim) {
      regExp = regExp.trim();
    }

    //Case conversion
    String res;
    switch (renameCase) {
      case UPPER:
        res = regExp.toUpperCase();
        break;
      case LOWER:
        res = regExp.toLowerCase();
        break;
      case FIRSTLO:
        res = Utils.capitalizedLetter(regExp, true);
        break;
      case FIRSTLA:
        res = Utils.capitalizedLetter(regExp, false);
        break;
      default:
        res = regExp;
        break;
    }

    //extension
    String fileName = getFile().getName();
    String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
    switch (renameCase) {
      case UPPER:
        ext = "." + ext.toUpperCase();
        break;
      default:
        ext = "." + ext.toLowerCase();
        break;
    }

    //construct new file name
    res += ext;

    if (Utils.isWindows()) {
      res = res.replaceAll(":", "").replaceAll("/", "");
    }

    if (conf.movieFilenameRmDupSpace) {
      res = res.replaceAll("\\s+", " ");
    }
    return res;
  }
  /**
   * Generate XBMC NFO file
   *
   * @return Xbmc NFO file
   */
  public String getXbmcNFOFromMovie() {

    StringBuilder nfo = new StringBuilder();
    nfo.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n<movie>\n");
    nfo.append("  <title>").append(Utils.escapeXML(mediaInfo.getTitle())).append("</title>\n");
    nfo.append("  <originaltitle>").append(Utils.escapeXML(mediaInfo.getOriginalTitle())).append("</originaltitle>\n");
    nfo.append("  <sorttitle>").append(Utils.escapeXML(mediaInfo.getSortTitle())).append("</sorttitle>\n");
    nfo.append("  <rating>").append(Utils.escapeXML(mediaInfo.getRating())).append("</rating>\n");
    nfo.append("  <votes>").append(mediaInfo.getVotes()).append("</votes>\n");
    nfo.append("  <year>").append(Utils.escapeXML(mediaInfo.getYear())).append("</year>\n");
    nfo.append("  <plot>").append(Utils.escapeXML(mediaInfo.getSynopsis())).append("</plot>\n");
    nfo.append("  <outline>").append(Utils.escapeXML(mediaInfo.getOutline())).append("</outline>\n");
    nfo.append("  <tagline>").append(Utils.escapeXML(mediaInfo.getTagline())).append("</tagline>\n");
    nfo.append("  <runtime>").append(mediaInfo.getRuntime().equals("-1") ? "" : mediaInfo.getRuntime()).append("</runtime>\n");
    nfo.append("  <top250>").append(mediaInfo.getTop250()).append("</top250>\n");
    nfo.append("  <playcount>").append(mediaInfo.getWatched() ? "1" : "0").append("</playcount>\n");
    nfo.append("  <watched>").append(mediaInfo.getWatched() ? "true" : "false").append("</watched>\n");
    nfo.append("  <mpaa>").append(Utils.escapeXML(mediaInfo.getMpaa())).append("</mpaa>\n");
    
    MediaID imdbID = getMediaId(MediaID.MediaIdType.IMDBID);
    if(imdbID != null){
      nfo.append("  <id>").append(imdbID).append("</id>\n");
    }
    for(MediaID id : mediaInfo.getIDs()){
      if(id.getType() != MediaID.MediaIdType.IMDBID) {
        nfo.append("  <").append(id.getType().name()).append("id>").append(imdbID);
        nfo.append("</").append(id.getType().name()).append("id>\n");
      }
    }
    
    nfo.append(printArrayString(mediaInfo.getSet(), "set", "  "));
    nfo.append(printArrayString(mediaInfo.getGenres(), "genre", "  "));
    nfo.append(printArrayString(mediaInfo.getCountries(), "country", "  "));
    nfo.append(printArrayString(mediaInfo.getStudios(), "studio", "  "));

    List<MediaPerson> personn = mediaInfo.getWriters();
    for (int i = 0; i < personn.size(); i++) {
      nfo.append("  <credits>").append(Utils.escapeXML(personn.get(i).getName())).append("</credits>\n");
    }

    personn = mediaInfo.getDirectors();
    for (int i = 0; i < personn.size(); i++) {
      nfo.append("  <director>").append(Utils.escapeXML(personn.get(i).getName())).append("</director>\n");
    }

    nfo.append("  <trailer>").append(mediaInfo.getTrailer()).append("</trailer>\n");

    personn = mediaInfo.getActors();
    for (int i = 0; i < personn.size(); i++) {
      nfo.append("  <actor>\n");
      nfo.append("    <name>").append(Utils.escapeXML(personn.get(i).getName())).append("</name>\n");
      for (int j = 0; j < personn.get(i).getRoles().size(); j++) {
        nfo.append("    <role>").append(Utils.escapeXML(personn.get(i).getRoles().get(j))).append("</role>\n");
      }
      nfo.append("    <thumb>").append(personn.get(i).getThumb()).append("</thumb>\n");
      nfo.append("  </actor>\n");
    }

    List<MediaImage> thumbs = mediaInfo.getThumbs();
    for (int i = 0; i < thumbs.size(); i++) {
      nfo.append("  <thumb preview=\"").append(thumbs.get(i).getUrl(MediaImage.MediaImageSize.THUMB));
      nfo.append("\">").append(thumbs.get(i).getUrl(MediaImage.MediaImageSize.ORIGINAL)).append("</thumb>\n");
    }

    List<MediaImage> fanarts = mediaInfo.getFanarts();
    nfo.append("  <fanart>");
    for (int i = 0; i < fanarts.size(); i++) {
      nfo.append("\n    <thumb preview=\"").append(fanarts.get(i).getUrl(MediaImage.MediaImageSize.THUMB)).append("\">");
      nfo.append(fanarts.get(i).getUrl(MediaImage.MediaImageSize.ORIGINAL)).append("</thumb>");
    }
    if (fanarts.size() > 0) {
      nfo.append("\n  ");
    }
    nfo.append("</fanart>\n");
    nfo.append("</movie>");
    return nfo.toString();
  }

  /**
   * Generate YAMJ NFO file
   *
   * @return YAMJ NFO file
   */
  public String getYamjNFOFromMovie() {

    StringBuilder nfo = new StringBuilder();
    nfo.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n<movie>\n");
    nfo.append("  <title>").append(Utils.escapeXML(mediaInfo.getTitle())).append("</title>\n");
    nfo.append("  <originaltitle>").append(Utils.escapeXML(mediaInfo.getOriginalTitle())).append("</originaltitle>\n");
    nfo.append("  <sorttitle>").append(Utils.escapeXML(mediaInfo.getSortTitle())).append("</sorttitle>\n");
    nfo.append("  <rating>").append(Utils.escapeXML(mediaInfo.getRating())).append("</rating>\n");
    nfo.append("  <votes>").append(mediaInfo.getVotes()).append("</votes>\n");
    nfo.append("  <year>").append(Utils.escapeXML(mediaInfo.getYear())).append("</year>\n");
    nfo.append("  <plot>").append(Utils.escapeXML(mediaInfo.getSynopsis())).append("</plot>\n");
    nfo.append("  <outline>").append(Utils.escapeXML(mediaInfo.getOutline())).append("</outline>\n");
    nfo.append("  <tagline>").append(Utils.escapeXML(mediaInfo.getTagline())).append("</tagline>\n");
    nfo.append("  <runtime>").append(mediaInfo.getRuntime().equals("-1") ? "" : mediaInfo.getRuntime()).append("</runtime>\n");
    nfo.append("  <top250>").append(mediaInfo.getTop250()).append("</top250>\n");
    nfo.append("  <playcount>").append(mediaInfo.getWatched() ? "1" : "0").append("</playcount>\n");
    nfo.append("  <watched>").append(mediaInfo.getWatched() ? "true" : "false").append("</watched>\n");
    nfo.append("  <mpaa>").append(Utils.escapeXML(mediaInfo.getMpaa())).append("</mpaa>\n");
    
    MediaID imdbID = getMediaId(MediaID.MediaIdType.IMDBID);
    if(imdbID != null){
      nfo.append("  <id>").append(imdbID).append("</id>\n");
    }
    for(MediaID id : mediaInfo.getIDs()){
      if(id.getType() != MediaID.MediaIdType.IMDBID) {
        nfo.append("  <").append(id.getType().name()).append("id>").append(imdbID);
        nfo.append("</").append(id.getType().name()).append("id>\n");
      }
    }
    
    nfo.append(printArrayString(mediaInfo.getSet(), "set", "  "));
    nfo.append(printArrayString(mediaInfo.getGenres(), "genre", "  "));
    nfo.append(printArrayString(mediaInfo.getCountries(), "country", "  "));
    nfo.append(printArrayString(mediaInfo.getStudios(), "studio", "  "));

    List<MediaPerson> personn = mediaInfo.getWriters();
    nfo.append("<credits>\n");
    for (int i = 0; i < personn.size(); i++) {
      nfo.append("  <writer>").append(Utils.escapeXML(personn.get(i).getName())).append("</writer>\n");
    }
    nfo.append("</credits>\n");
    
    personn = mediaInfo.getDirectors();
    for (int i = 0; i < personn.size(); i++) {
      nfo.append("  <director>").append(Utils.escapeXML(personn.get(i).getName())).append("</director>\n");
    }

    nfo.append("  <trailer>").append(mediaInfo.getTrailer()).append("</trailer>\n");

    personn = mediaInfo.getActors();
    for (int i = 0; i < personn.size(); i++) {
      nfo.append("  <actor>\n");
      nfo.append("    <name>").append(Utils.escapeXML(personn.get(i).getName())).append("</name>\n");
      for (int j = 0; j < personn.get(i).getRoles().size(); j++) {
        nfo.append("    <role>").append(Utils.escapeXML(personn.get(i).getRoles().get(j))).append("</role>\n");
      }
      nfo.append("    <thumb>").append(personn.get(i).getThumb()).append("</thumb>\n");
      nfo.append("  </actor>\n");
    }

    List<MediaImage> thumbs = mediaInfo.getThumbs();
    for (int i = 0; i < thumbs.size(); i++) {
      nfo.append("  <thumb preview=\"").append(thumbs.get(i).getUrl(MediaImage.MediaImageSize.THUMB));
      nfo.append("\">").append(thumbs.get(i).getUrl(MediaImage.MediaImageSize.ORIGINAL)).append("</thumb>\n");
    }

    List<MediaImage> fanarts = mediaInfo.getFanarts();
    nfo.append("  <fanart>");
    for (int i = 0; i < fanarts.size(); i++) {
      nfo.append("\n    <thumb preview=\"").append(fanarts.get(i).getUrl(MediaImage.MediaImageSize.THUMB)).append("\">");
      nfo.append(fanarts.get(i).getUrl(MediaImage.MediaImageSize.ORIGINAL)).append("</thumb>");
    }
    if (fanarts.size() > 0) {
      nfo.append("\n  ");
    }
    nfo.append("</fanart>\n");
    nfo.append("</movie>");
    return nfo.toString();
  }

  /**
   * Generate Mediaportal NFO file
   *
   * @return Mediaportal NFO file
   */
  public String getMediaPortalNFOFromMovie() {

    StringBuilder nfo = new StringBuilder();
    nfo.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<movie>\n");
    nfo.append("  <title>").append(Utils.escapeXML(mediaInfo.getTitle())).append("</title>\n");
    nfo.append("  <language></language>\n");
    nfo.append(printArrayString(mediaInfo.getCountries(), "country", "  "));
    nfo.append("  <year>").append(Utils.escapeXML(mediaInfo.getYear())).append("</year>\n");
    nfo.append("  <rating>").append(Utils.escapeXML(mediaInfo.getRating())).append("</rating>\n");
    nfo.append("  <runtime>").append(mediaInfo.getRuntime().equals("-1") ? "" : mediaInfo.getRuntime()).append("</runtime>\n");
    nfo.append("  <mpaa>").append(Utils.escapeXML(mediaInfo.getMpaa())).append("</mpaa>\n");
    nfo.append("  <votes>").append(mediaInfo.getVotes()).append("</votes>\n");
    nfo.append("  <studio>").append(Utils.escapeXML(mediaInfo.getStudiosString(" / ", 0))).append("</studio>\n");

    List<MediaPerson> personn = mediaInfo.getDirectors();
    for (int i = 0; i < personn.size(); i++) {
      nfo.append("  <director>").append(Utils.escapeXML(personn.get(i).getName())).append("</director>\n");
      nfo.append("  <directorimdb>").append(Utils.escapeXML(personn.get(i).getImdbId())).append("</directorimdb>\n");
    }

    nfo.append("  <credits>").append(Utils.escapeXML(mediaInfo.getWritersString(" / ", 0))).append("</credits>\n");
    nfo.append("  <tagline>").append(Utils.escapeXML(mediaInfo.getTagline())).append("</tagline>\n");
    nfo.append("  <outline>").append(Utils.escapeXML(mediaInfo.getOutline())).append("</outline>\n");
    nfo.append("  <plot>").append(Utils.escapeXML(mediaInfo.getSynopsis())).append("</plot>\n");
    nfo.append("  <review></review>\n");

    List<MediaImage> thumbs = mediaInfo.getThumbs();
    for (int i = 0; i < thumbs.size(); i++) {
      nfo.append("  <thumb>").append(thumbs.get(i).getUrl(MediaImage.MediaImageSize.ORIGINAL)).append("</thumb>\n");
    }

    List<MediaImage> fanarts = mediaInfo.getFanarts();
    nfo.append("  <fanart>");
    for (int i = 0; i < fanarts.size(); i++) {
      nfo.append("\n    <thumb>").append(fanarts.get(i).getUrl(MediaImage.MediaImageSize.ORIGINAL)).append("</thumb>");
    }
    if (fanarts.size() > 0) {
      nfo.append("\n  ");
    }
    nfo.append("</fanart>\n");

    List<String> genres = mediaInfo.getGenres();
    nfo.append("  <genres>");
    for (int i = 0; i < genres.size(); i++) {
      nfo.append("\n    <genre>").append(Utils.escapeXML(genres.get(i))).append("</genre>");
    }
    if (genres.size() > 0) {
      nfo.append("\n  ");
    }
    nfo.append("<genres>\n");

    personn = mediaInfo.getActors();
    for (int i = 0; i < personn.size(); i++) {
      nfo.append("  <actor>\n");
      nfo.append("    <name>").append(Utils.escapeXML(personn.get(i).getName())).append("</name>\n");
      nfo.append("    <role>").append(Utils.escapeXML(Utils.arrayToString(personn.get(i).getRoles(), ", ", 0))).append("</role>\n");
      nfo.append("    <imdb>").append(personn.get(i).getImdbId()).append("</imdb>\n");
      if (!personn.get(i).getThumb().equals("")) {
        nfo.append("    <thumb>").append(personn.get(i).getThumb()).append("</thumb>\n");
      }
      nfo.append("  </actor>\n");
    }

    nfo.append("</movie>");
    return nfo.toString();
  }

  /**
   * Transform array to XML string
   *
   * @param arrayString Array of value
   * @param tag XML tag
   * @param level String of space
   * @return String XML
   */
  private String printArrayString(List<String> arrayString, String tag, String level) {
    StringBuilder res = new StringBuilder();
    for (int i = 0; i < arrayString.size(); i++) {
      res.append(level).append("<").append(tag).append(">").append(Utils.escapeXML(arrayString.get(i))).append("</").append(tag).append(">\n");
    }
    return res.toString();
  }


  @Override
  public MediaType getType() {
    return Media.MediaType.MOVIE;
  }

}
