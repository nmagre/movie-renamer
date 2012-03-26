/******************************************************************************
 *                                                                             *
 *    Movie Renamer                                                            *
 *    Copyright (C) 2012 Magré Nicolas                                         *
 *                                                                             *
 *    Movie Renamer is free software: you can redistribute it and/or modify    *
 *    it under the terms of the GNU General Public License as published by     *
 *    the Free Software Foundation, either version 3 of the License, or        *
 *    (at your option) any later version.                                      *
 *                                                                             *
 *    This program is distributed in the hope that it will be useful,          *
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            *
 *    GNU General Public License for more details.                             *
 *                                                                             *
 *    You should have received a copy of the GNU General Public License        *
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                             *
 ******************************************************************************/
package fr.free.movierenamer.movie;

import java.io.File;
import java.util.ArrayList;
import fr.free.movierenamer.utils.Utils;

/**
 * Movie Class
 * @author Nicolas Magré
 */
public class Movie {

  private MovieFile movieFile;
  private MovieInfo movieinfo;
  private String imdbId;
  private ArrayList<MovieImage> thumbs;
  private ArrayList<MovieImage> fanarts;
  private String imdbTitle;
  private String filteredFileName;
  private String search;

  /**
   * Constructor arguments
   * @param movieFile A movie file
   * @param filter An array of movie title filters
   */
  public Movie(MovieFile movieFile, String[] filter) {
    this.movieFile = movieFile;
    thumbs = new ArrayList<MovieImage>();
    fanarts = new ArrayList<MovieImage>();
    movieinfo = new MovieInfo();

    String fileName = movieFile.getFile().getName();
    filteredFileName = Utils.getFilteredName(fileName.substring(0, fileName.lastIndexOf(Utils.DOT)), filter);
    search = Utils.getFilteredName(fileName.substring(0, fileName.lastIndexOf(Utils.DOT)), filter);
  }

  /**
   * Clear images and movie information
   */
  public void clear() {
    this.clearFanarts();
    this.clearThumbs();
    movieinfo = new MovieInfo();
  }

  /**
   * Get movie file
   * @return File of movie
   */
  public File getFile() {
    return movieFile.getFile();
  }

  /**
   * Get filtered movie title
   * @return Movie name filtered
   */
  public String getFilteredName() {
    return filteredFileName;
  }

  /**
   * Get search movie title
   * @return Movie name search
   */
  public String getSearch() {
    return search;
  }

  /**
   * Get imdb ID
   * @return Movie Imdb ID
   */
  public String getImdbId() {
    return imdbId;
  }

  /**
   * Get array of thumbnails
   * @return ArrayList of MovieImage
   */
  public ArrayList<MovieImage> getThumbs() {
    return thumbs;
  }

  /**
   * Get array of fanarts
   * @return ArrayList of MovieImage
   */
  public ArrayList<MovieImage> getFanarts() {
    return fanarts;
  }

  /**
   * Get renamed movie title
   * @param regExp Expression to rename movie title with
   * @param letter
   * @return Movie title renamed
   */
  public String getRenamedTitle(String regExp, int letter) {
    String runtime = "";
    if (movieinfo.getRuntime() != -1) runtime += movieinfo.getRuntime();
    String[][] replace = new String[][]{{"<t>", movieinfo.getTitle()}, {"<tt>", getImdbId()}, {"<y>", movieinfo.getYear()},
      {"<rt>", runtime}, {"<ra>", movieinfo.getRating()}, {"<d>", movieinfo.getDirectorsString()},
      {"<d1>", movieinfo.getFirstDirector()}, {"<g>", movieinfo.getGenresString()},
      {"<g1>", movieinfo.getFirstGenreString()}};
    for (int i = 0; i < replace.length; i++) {
      regExp = regExp.replaceAll(replace[i][0], replace[i][1]);
    }
    String fileName = getFile().getName();
    String ext = fileName.substring(fileName.lastIndexOf('.') + 1);

    String res = "";
    switch (letter) {
      case Utils.UPPER:
        res = regExp.toUpperCase() + "." + ext.toUpperCase();
        break;
      case Utils.LOWER:
        res = regExp.toLowerCase() + "." + ext.toLowerCase();
        break;
      case Utils.FIRSTLO:
        res = Utils.capitalizedLetter(regExp, true) + "." + ext.toLowerCase();
        break;
      case Utils.FIRSTLA:
        res = Utils.capitalizedLetter(regExp, false) + "." + ext.toLowerCase();
        break;
      default:
        res = regExp + "." + ext.toLowerCase();
        break;
    }
    if (Utils.isWindows()) res = res.replaceAll(":", "").replaceAll("/", "");
    return res;
  }

  /**
   * Set movie informations
   * @return MovieInfo
   */
  public MovieInfo getMovieInfo() {
    return movieinfo;
  }

  /**
   * Get Imdb title
   * @return Movie Imdb title
   */
  public String getImdbTitle() {
    return imdbTitle;
  }

  /**
   * Add a thumb to movie
   * @param thumb Thumb to add
   */
  public void addThumb(MovieImage thumb) {
    thumbs.add(thumb);
  }

  /**
   * Add a fanart to movie
   * @param fanart Fanart to add
   */
  public void addFanart(MovieImage fanart) {
    fanarts.add(fanart);
  }

  /**
   * Set movie file
   * @param file Movie file to set
   */
  public void setFile(File file) {
    movieFile.setFile(file);
  }

  /**
   * Set movie renamed
   * @param renamed Movie is renamed
   */
  public void setRenamed(boolean renamed) {
    movieFile.setRenamed(renamed);
  }

  /**
   * Set Imdb search string
   * @param search movie to search on Imdb
   */
  public void setSearch(String search) {
    this.search = search;
  }

  /**
   * Set movie informations
   * @param movieinfo Movie informations
   */
  public void setMovieInfo(MovieInfo movieinfo) {
    this.movieinfo = movieinfo;
  }

  /**
   * Set imdb ID
   * @param imdbId Imdb ID
   */
  public void setImdbId(String imdbId) {
    this.imdbId = imdbId;
    movieinfo.setImdbId(imdbId);
  }

  /**
   * Clear thumbs list
   */
  public void clearThumbs() {
    thumbs.clear();
  }

  /**
   * Cleanr fanart list
   */
  public void clearFanarts() {
    fanarts.clear();
  }

  /**
   * Set imdb title
   * @param imdbTitle
   */
  public void setImdbTitle(String imdbTitle) {
    this.imdbTitle = imdbTitle;
  }

  /**
   * Set thumbs list
   * @param thumbs Array of thumbs
   */
  public void setThumbs(ArrayList<MovieImage> thumbs) {
    this.thumbs = thumbs;
  }

  /**
   * Set fanarts list
   * @param fanarts Array of fanarts
   */
  public void setFanarts(ArrayList<MovieImage> fanarts) {
    this.fanarts = fanarts;
  }

  /**
   * Generate XBMC NFO file
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
    nfo.append("  <runtime>").append(movieinfo.getRuntime() == -1 ? "" : movieinfo.getRuntime()).append("</runtime>\n");
    nfo.append("  <mpaa>").append(Utils.escapeXML(movieinfo.getMpaa())).append("</mpaa>\n");
    nfo.append("  <id>").append(movieinfo.getImdbId()).append("</id>\n");
    nfo.append(printArrayString(movieinfo.getSet(), "set", "  "));
    nfo.append(printArrayString(movieinfo.getGenres(), "genre", "  "));
    nfo.append(printArrayString(movieinfo.getCountries(), "country", "  "));
    nfo.append(printArrayString(movieinfo.getStudios(), "studio", "  "));

    ArrayList<MoviePerson> personn = movieinfo.getWriters();
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

    for (int i = 0; i < thumbs.size(); i++) {
      nfo.append("  <thumb preview=\"").append(thumbs.get(i).getThumbUrl()).append("\">").append(thumbs.get(i).getOrigUrl()).append("</thumb>\n");
    }

    nfo.append("  <fanart>");
    for (int i = 0; i < fanarts.size(); i++) {
      nfo.append("\n    <thumb preview=\"").append(fanarts.get(i).getThumbUrl()).append("\">").append(fanarts.get(i).getOrigUrl()).append("</thumb>");
    }
    if(fanarts.size() > 0) nfo.append("\n  ");
    nfo.append("</fanart>\n");
    nfo.append("</movie>");
    return nfo.toString();
  }

  /**
   * Generate Mediaportal NFO file
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
    nfo.append("  <runtime>").append(movieinfo.getRuntime() == -1 ? "" : movieinfo.getRuntime()).append("</runtime>\n");
    nfo.append("  <mpaa>").append(Utils.escapeXML(movieinfo.getMpaa())).append("</mpaa>\n");
    nfo.append("  <votes>").append(movieinfo.getVotes()).append("</votes>\n");
    nfo.append("  <top250></top250>\n");
    nfo.append("  <studio>").append(Utils.escapeXML(movieinfo.getStudiosString().replace("|", "/"))).append("</studio>\n");

    ArrayList<MoviePerson> personn = movieinfo.getDirectors();
    for (int i = 0; i < personn.size(); i++) {
      nfo.append("  <director>").append(Utils.escapeXML(personn.get(i).getName())).append("</director>\n");
      nfo.append("  <directorimdb>").append(Utils.escapeXML(personn.get(i).getImdbId())).append("</directorimdb>\n");
    }

    nfo.append("  <credits>").append(Utils.escapeXML(movieinfo.getWritersString().replace("|", "/"))).append("</credits>\n");
    nfo.append("  <tagline>").append(Utils.escapeXML(movieinfo.getTagline())).append("</tagline>\n");
    nfo.append("  <outline>").append(Utils.escapeXML(movieinfo.getOutline())).append("</outline>\n");
    nfo.append("  <plot>").append(Utils.escapeXML(movieinfo.getSynopsis())).append("</plot>\n");
    nfo.append("  <review></review>\n");

    for (int i = 0; i < thumbs.size(); i++) {
      nfo.append("  <thumb>").append(thumbs.get(i).getOrigUrl()).append("</thumb>\n");
    }

    nfo.append("  <fanart>");
    for (int i = 0; i < fanarts.size(); i++) {
      nfo.append("\n    <thumb>").append(fanarts.get(i).getOrigUrl()).append("</thumb>");
    }
    if( fanarts.size() > 0) nfo.append("\n  ");
    nfo.append("</fanart>\n");

    ArrayList<String> genres = movieinfo.getGenres();
    nfo.append("  <genres>");
    for(int i=0;i<genres.size();i++){
      nfo.append("\n    <genre>").append(Utils.escapeXML(genres.get(i))).append("</genre>");
    }
    if(genres.size() > 0) nfo.append("\n  ");
    nfo.append("<genres>\n");

    personn = movieinfo.getActors();
    for (int i = 0; i < personn.size(); i++) {
      nfo.append("  <actor>\n");
      nfo.append("    <name>").append(Utils.escapeXML(personn.get(i).getName())).append("</name>\n");
      nfo.append("    <role>").append(Utils.escapeXML(Utils.arrayToString(personn.get(i).getRoles(), ", "))).append("</role>\n");
      nfo.append("    <imdb>").append(personn.get(i).getImdbId()).append("</imdb>\n");
      if(!personn.get(i).getThumb().equals(""))
        nfo.append("    <thumb>").append(personn.get(i).getThumb()).append("</thumb>\n");
      nfo.append("  </actor>\n");
    }

    nfo.append("</movie>");
    return nfo.toString();
  }

  /**
   * Transform array to XML string
   * @param arrayString Array of value
   * @param tag XML tag
   * @param level String of space
   * @return String XML
   */
  private String printArrayString(ArrayList<String> arrayString, String tag, String level) {
    StringBuilder res = new StringBuilder();
    for (int i = 0; i < arrayString.size(); i++) {
      res.append(level).append("<").append(tag).append(">").append(Utils.escapeXML(arrayString.get(i))).append("</").append(tag).append(">\n");
    }
    return res.toString();
  }

  @Override
  public String toString() {
    return imdbTitle;
  }
}
