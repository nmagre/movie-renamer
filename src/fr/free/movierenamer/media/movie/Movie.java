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

import fr.free.movierenamer.media.MediaID;

import fr.free.movierenamer.matcher.MovieNameMatcher;
import fr.free.movierenamer.media.*;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class Movie
 *
 * @author Nicolas Magré
 */
public class Movie implements Media {

  private MediaID mediaId;
  private MediaFile movieFile;
  private MovieInfo movieinfo;
  private MediaTag mtag;
  private String search;
  private String year;

  /**
   * Constructor arguments
   *
   * @param movieFile A movie file
   * @param filter An array of movie title filters
   */
  public Movie(MediaFile movieFile, List<String> filter) {
    this.movieFile = movieFile;
    movieinfo = new MovieInfo();
    MovieNameMatcher nameMatcher = new MovieNameMatcher(movieFile, filter);
    search = nameMatcher.getMovieName();
    year = nameMatcher.getYear();
    mtag = new MediaTag(movieFile.getFile());
  }

  /**
   * Clear images and movie information
   */
  @Override
  public void clear() {
    movieinfo = new MovieInfo();
  }

  /**
   * Get movie file
   *
   * @return File of movie
   */
  public File getFile() {
    return movieFile.getFile();
  }

  /**
   * Get search movie title
   *
   * @return Movie name search
   */
  @Override
  public String getSearch() {
    return search;
  }
  
  @Override
  public int getYear(){
    if(year.equals("")) {
      return -1;
    }
    return Integer.parseInt(year);
  }

  /**
   * Get renamed movie title
   *
   * @param setting Movie Renamer settings
   * @param regExp Expression to rename movie title with
   * @return Movie title renamed
   */
  @Override
  public String getRenamedTitle(String regExp, Settings setting) {
    String separator = setting.movieFilenameSeparator;
    int limit = setting.movieFilenameLimit;
    Utils.CaseConversionType renameCase = setting.movieFilenameCase;
    boolean trim = setting.movieFilenameTrim;
    
    String titlePrefix = "";
    String shortTitle = movieinfo.getTitle();

    Pattern pattern = Pattern.compile("^((le|la|les|the)\\s|(l\\'))(.*)", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(movieinfo.getTitle());
    if(matcher.find() && matcher.groupCount() >= 2 ) {
      titlePrefix = matcher.group(1);
      shortTitle = matcher.group(matcher.groupCount());
    }

    String runtime = "";
    if (!movieinfo.getRuntime().equals("-1")) {
      runtime += movieinfo.getRuntime();
    }
    
    String[][] replace = new String[][]{
      {"<t>", "<tp><st>"},
      {"<tp>", titlePrefix},
      {"<st>", shortTitle},
      {"<ot>", movieinfo.getOrigTitle()},
      {"<tt>", mediaId.getID()},
      {"<y>", movieinfo.getYear()},
      {"<rt>", runtime},
      {"<ra>", movieinfo.getRating()},
      {"<a>", movieinfo.getActorsString(separator, limit)},
      {"<d>", movieinfo.getDirectorsString(separator, limit)},
      {"<g>", movieinfo.getGenresString(separator, limit)},
      {"<c>", movieinfo.getCountriesString(separator, limit)},
      {"<mrt>", mtag.getDuration()},
      {"<mfs>", mtag.getFileSize()},
      {"<mc>", mtag.getVideoCodec()},
      {"<mdc>", mtag.getVideoDefinitionCategory()},
      {"<mf>", mtag.getVideoFormat()},
      {"<mfr>", mtag.getVideoFrameRate()},
      {"<mr>", mtag.getVideoResolution()},
      {"<mcf>", mtag.getContainerFormat()},
      {"<mach>", mtag.getAudioChannelsString(separator, limit)},
      {"<mac>", mtag.getAudioCodecString(separator, limit)},
      {"<mal>", mtag.getAudioLanguageString(separator, limit)},
      {"<matt>",  mtag.getAudioTitleString(separator, limit)},
      {"<mtt>", mtag.getTextTitleString(separator, limit)}
    };
    
    //replace actors, directors, genres, coutries
    pattern = Pattern.compile("<([adcg])(\\d+)>");
    matcher = pattern.matcher(regExp);
    while (matcher.find()) {
      int n = Integer.parseInt(matcher.group(2));
      char x = matcher.group(1).charAt(0);
      switch (x) {
        case 'a':
          regExp = regExp.replaceAll("<a\\d+>", movieinfo.getActorN(n));
          break;
        case 'd':
          regExp = regExp.replaceAll("<d\\d+>", movieinfo.getDirectorN(n));
          break;
        case 'g':
          regExp = regExp.replaceAll("<g\\d+>", movieinfo.getGenreN(n));
          break;
        case 'c':
          regExp = regExp.replaceAll("<c\\d+>", movieinfo.getCountryN(n));
          break;
        default:
          break;
      }
    }

    //replace audio attr
    pattern = Pattern.compile("<(ma?[chtl]*)(\\d+)>");
    matcher = pattern.matcher(regExp);
    while (matcher.find()) {
      String tag = matcher.group(1);
      int n = Integer.parseInt(matcher.group(2));
      if(tag.equals("mach")){
        regExp = regExp.replaceAll("<mach\\d+>", mtag.getAudioChannelsStringN(n));
      }
      else if(tag.equals("mac")){
        regExp = regExp.replaceAll("<mac\\d+>", mtag.getAudioCodecStringN(n));
      }
      else if(tag.equals("mal")){
        regExp = regExp.replaceAll("<mal\\d+>",mtag.getAudioLanguageStringN(n));
      }
      else if(tag.equals("matt")){
        regExp = regExp.replaceAll("<matt\\d+>", mtag.getAudioTitleStringN(n));
      }
      else if(tag.equals("mtt")){
        regExp = regExp.replaceAll("<mtt\\d+>", mtag.getTextTitleStringN(n));
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

    if (setting.movieFilenameRmDupSpace) {
      res = res.replaceAll("\\s+", " ");
    }
    return res;
  }

  /**
   * Get movie informations
   *
   * @return MovieInfo
   */
  public MovieInfo getMovieInfo() {
    return movieinfo;
  }

  @Override
  public MediaID getMediaId(MediaID.MediaIdType IDtype) {
    if (mediaId.getType() == IDtype) {
      return mediaId;
    }

    for (MediaID mid : movieinfo.getIDs()) {
      if (mid.getType() == IDtype) {
        return mid;
      }
    }

    return null;
  }

  /**
   * Set movie file
   *
   * @param file Movie file to set
   */
  public void setFile(File file) {
    movieFile.setFile(file);
  }

  /**
   * Set movie renamed
   *
   * @param renamed Movie is renamed
   */
  public void setRenamed(boolean renamed) {
    movieFile.setRenamed(renamed);
  }

  /**
   * Set Imdb search string
   *
   * @param search movie to search on Imdb
   */
  @Override
  public void setSearch(String search) {
    this.search = search;
  }

  /**
   * Set movie informations
   *
   * @param movieinfo Movie informations
   */
  public void setMovieInfo(MovieInfo movieinfo) {
    this.movieinfo = movieinfo;
  }

  /**
   * Generate XBMC NFO file
   *
   * @return Xbmc NFO file
   */
  public String getXbmcNFOFromMovie() {

    StringBuilder nfo = new StringBuilder();
    nfo.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n<movie>\n");
    nfo.append("  <title>").append(Utils.escapeXML(movieinfo.getTitle())).append("</title>\n");
    nfo.append("  <originaltitle>").append(Utils.escapeXML(movieinfo.getOrigTitle())).append("</originaltitle>\n");
    nfo.append("  <sorttitle>").append(Utils.escapeXML(movieinfo.getSortTitle())).append("</sorttitle>\n");
    nfo.append("  <rating>").append(Utils.escapeXML(movieinfo.getRating())).append("</rating>\n");
    nfo.append("  <votes>").append(movieinfo.getVotes()).append("</votes>\n");
    nfo.append("  <year>").append(Utils.escapeXML(movieinfo.getYear())).append("</year>\n");
    nfo.append("  <plot>").append(Utils.escapeXML(movieinfo.getSynopsis())).append("</plot>\n");
    nfo.append("  <outline>").append(Utils.escapeXML(movieinfo.getOutline())).append("</outline>\n");
    nfo.append("  <tagline>").append(Utils.escapeXML(movieinfo.getTagline())).append("</tagline>\n");
    nfo.append("  <runtime>").append(movieinfo.getRuntime().equals("-1") ? "" : movieinfo.getRuntime()).append("</runtime>\n");
    nfo.append("  <top250>").append(movieinfo.getTop250()).append("</top250>\n");
    nfo.append("  <playcount>").append(movieinfo.getWatched() ? "1" : "0").append("</playcount>\n");
    nfo.append("  <watched>").append(movieinfo.getWatched() ? "true" : "false").append("</watched>\n");
    nfo.append("  <mpaa>").append(Utils.escapeXML(movieinfo.getMpaa())).append("</mpaa>\n");
    nfo.append("  <id>").append(mediaId.getID()).append("</id>\n");
    nfo.append(printArrayString(movieinfo.getSet(), "set", "  "));
    nfo.append(printArrayString(movieinfo.getGenres(), "genre", "  "));
    nfo.append(printArrayString(movieinfo.getCountries(), "country", "  "));
    nfo.append(printArrayString(movieinfo.getStudios(), "studio", "  "));

    List<MediaPerson> personn = movieinfo.getWriters();
    for (int i = 0; i < personn.size(); i++) {
      nfo.append("  <credits>").append(Utils.escapeXML(personn.get(i).getName())).append("</credits>\n");
    }

    personn = movieinfo.getDirectors();
    for (int i = 0; i < personn.size(); i++) {
      nfo.append("  <director>").append(Utils.escapeXML(personn.get(i).getName())).append("</director>\n");
    }

    nfo.append("  <trailer>").append(movieinfo.getTrailer()).append("</trailer>\n");

    personn = movieinfo.getActors();
    for (int i = 0; i < personn.size(); i++) {
      nfo.append("  <actor>\n");
      nfo.append("    <name>").append(Utils.escapeXML(personn.get(i).getName())).append("</name>\n");
      for (int j = 0; j < personn.get(i).getRoles().size(); j++) {
        nfo.append("    <role>").append(Utils.escapeXML(personn.get(i).getRoles().get(j))).append("</role>\n");
      }
      nfo.append("    <thumb>").append(personn.get(i).getThumb()).append("</thumb>\n");
      nfo.append("  </actor>\n");
    }

    List<MediaImage> thumbs = movieinfo.getThumbs();
    for (int i = 0; i < thumbs.size(); i++) {
      nfo.append("  <thumb preview=\"").append(thumbs.get(i).getThumbUrl()).append("\">").append(thumbs.get(i).getOrigUrl()).append("</thumb>\n");
    }

    List<MediaImage> fanarts = movieinfo.getFanarts();
    nfo.append("  <fanart>");
    for (int i = 0; i < fanarts.size(); i++) {
      nfo.append("\n    <thumb preview=\"").append(fanarts.get(i).getThumbUrl()).append("\">").append(fanarts.get(i).getOrigUrl()).append("</thumb>");
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
    nfo.append("  <title>").append(Utils.escapeXML(movieinfo.getTitle())).append("</title>\n");
    nfo.append("  <language></language>\n");
    nfo.append(printArrayString(movieinfo.getCountries(), "country", "  "));
    nfo.append("  <year>").append(Utils.escapeXML(movieinfo.getYear())).append("</year>\n");
    nfo.append("  <rating>").append(Utils.escapeXML(movieinfo.getRating())).append("</rating>\n");
    nfo.append("  <runtime>").append(movieinfo.getRuntime().equals("-1") ? "" : movieinfo.getRuntime()).append("</runtime>\n");
    nfo.append("  <mpaa>").append(Utils.escapeXML(movieinfo.getMpaa())).append("</mpaa>\n");
    nfo.append("  <votes>").append(movieinfo.getVotes()).append("</votes>\n");
    nfo.append("  <studio>").append(Utils.escapeXML(movieinfo.getStudiosString(" / ", 0))).append("</studio>\n");

    List<MediaPerson> personn = movieinfo.getDirectors();
    for (int i = 0; i < personn.size(); i++) {
      nfo.append("  <director>").append(Utils.escapeXML(personn.get(i).getName())).append("</director>\n");
      nfo.append("  <directorimdb>").append(Utils.escapeXML(personn.get(i).getImdbId())).append("</directorimdb>\n");
    }

    nfo.append("  <credits>").append(Utils.escapeXML(movieinfo.getWritersString(" / ", 0))).append("</credits>\n");
    nfo.append("  <tagline>").append(Utils.escapeXML(movieinfo.getTagline())).append("</tagline>\n");
    nfo.append("  <outline>").append(Utils.escapeXML(movieinfo.getOutline())).append("</outline>\n");
    nfo.append("  <plot>").append(Utils.escapeXML(movieinfo.getSynopsis())).append("</plot>\n");
    nfo.append("  <review></review>\n");

    List<MediaImage> thumbs = movieinfo.getThumbs();
    for (int i = 0; i < thumbs.size(); i++) {
      nfo.append("  <thumb>").append(thumbs.get(i).getOrigUrl()).append("</thumb>\n");
    }

    List<MediaImage> fanarts = movieinfo.getFanarts();
    nfo.append("  <fanart>");
    for (int i = 0; i < fanarts.size(); i++) {
      nfo.append("\n    <thumb>").append(fanarts.get(i).getOrigUrl()).append("</thumb>");
    }
    if (fanarts.size() > 0) {
      nfo.append("\n  ");
    }
    nfo.append("</fanart>\n");

    List<String> genres = movieinfo.getGenres();
    nfo.append("  <genres>");
    for (int i = 0; i < genres.size(); i++) {
      nfo.append("\n    <genre>").append(Utils.escapeXML(genres.get(i))).append("</genre>");
    }
    if (genres.size() > 0) {
      nfo.append("\n  ");
    }
    nfo.append("<genres>\n");

    personn = movieinfo.getActors();
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
  public String toString() {
    String res = movieFile.toString() + "\n";
    res += movieinfo.toString();
    return res;
  }

  @Override
  public MediaFile getMediaFile() {
    return movieFile;
  }

  @Override
  public void setMediaFile(MediaFile mediaFile) {
    movieFile = mediaFile;
  }
  
  @Override
  public void addMediaID(MediaID id) {
    if(getMediaId(id.getType()) == null){
      movieinfo.addID(id);
    }
  }

  @Override
  public MediaType getType() {
    return Media.MediaType.MOVIE;
  }

  @Override
  public void setInfo(Object info) {
    if (info instanceof MovieInfo) {
      movieinfo = (MovieInfo) info;
    } else {
      movieinfo = new MovieInfo();
    }
  }

  @Override
  public void setMediaID(MediaID id) {
    mediaId = id;
  }
}
