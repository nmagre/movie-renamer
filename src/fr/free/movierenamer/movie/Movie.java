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
 * @author duffy
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
   * Get genres string
   * @return String of genres separataded by pipe
   */
  public String getGenresString() {
    return movieinfo.getGenresString();
  }

  /**
   * Get imdb thumb
   * @return String 
   */
  public String getImdbThumb() {
    return movieinfo.getImdbThumb();
  }

  /**
   * Get countries string
   * @return String of countries separated by pipe
   */
  public String getCountriesString() {
    return movieinfo.getCountriesString();
  }

  /**
   * Get search movie title
   * @return Movie name search
   */
  public String getSearch() {
    return search;
  }

  /**
   * Get title
   * @return Movie title
   */
  public String getTitle() {
    return movieinfo.getTitle();
  }

  /**
   * Get year
   * @return Movie year or "-1"
   */
  public String getYear() {
    return movieinfo.getYear();
  }

  /**
   * Get imdb ID
   * @return Movie Imdb ID
   */
  public String getImdbId() {
    return imdbId;
  }

  /**
   * Get the movie DB ID
   * @return The movie DB ID
   */
  public String getImdbBId() {
    return movieinfo.getImdbId();
  }

  /**
   * Get genres
   * @return Array of genres
   */
  public ArrayList<String> getGenres() {
    return movieinfo.getGenres();
  }

  /**
   * Get array of movie person
   * @param job Personn job
   * @return ArrayList of MoviePerson
   */
  public ArrayList<MoviePerson> getPersons(int job) {
    switch(job){
      case MoviePerson.ACTOR:
        return movieinfo.getActors();
      case MoviePerson.DIRECTOR:
        return movieinfo.getDirectors();
      case MoviePerson.WRITER:
        return movieinfo.getWriters();
      default:
        break;
    }
    return null;
  }

  public String getDirectorsString(){
    return movieinfo.getDirectorsString();
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
   * Get original title
   * @return Movie original title
   */
  public String getOrigTitle() {
    return movieinfo.getOrigTitle();
  }

  /**
   * Get trailer
   * @return Movie trailer url (youtube url, only with theMovieDB scrap)
   */
  public String getTrailer() {
    return movieinfo.getTrailer();
  }

  /**
   * Get tagline
   * @return Movie tagline
   */
  public String getTagline() {
    return movieinfo.getTagline();
  }

  /**
   * Get countries
   * @return Array of countries
   */
  public ArrayList<String> getCountries() {
    return movieinfo.getCountries();
  }

  /**
   * Get runtime
   * @return Movie runtime or -1
   */
  public int getRuntime() {
    return movieinfo.getRuntime();
  }

  /**
   * Get rating
   * @return Movie rating or "-1"
   */
  public String getRating() {
    return movieinfo.getRating();
  }

  /**
   * Get synopsis
   * @return Movie synopsis
   */
  public String getSynopsis() {
    return movieinfo.getSynopsis();
  }

  /**
   * Get renamed movie title
   * @param regExp Expression to rename movie title with
   * @param letter 
   * @return Movie title renamed
   */
  public String getRenamedTitle(String regExp, int letter) {
    String runtime = "";
    if (getRuntime() != -1) runtime += getRuntime();
    String[][] replace = new String[][]{{"<t>", getTitle()}, {"<tt>", getImdbId()}, {"<y>", getYear()},
      {"<rt>", runtime}, {"<ra>", getRating()}, {"<d>", movieinfo.getDirectorsString()},
      {"<d1>", movieinfo.getFirstDirector()}, {"<g>", movieinfo.getGenresString()},
      {"<g1>", movieinfo.getFirstGenreString()}};
    for (int i = 0; i < replace.length; i++) {
      regExp = regExp.replaceAll(replace[i][0], replace[i][1]);
    }
    String fileName = getFile().getName();
    String ext = fileName.substring(fileName.lastIndexOf('.') + 1);

    String res = "";
    switch(letter){
      case Utils.UPPER:
        res = regExp.toUpperCase() + "." + ext.toUpperCase();
        break;
      case Utils.LOWER:
        res = regExp.toLowerCase()+ "." + ext.toLowerCase();
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
    if(Utils.isWindows()) res = res.replaceAll(":", "").replaceAll("/", "");
    return res;
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
   * Set movie title
   * @param title Movie title to set
   */
  public void setTitle(String title) {
    movieinfo.setTitle(title);
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
  public void setThumbs(ArrayList<MovieImage> thumbs){
    this.thumbs = thumbs;
  }

  /**
   * Set fanarts list
   * @param fanarts Array of fanarts
   */
  public void setFanarts(ArrayList<MovieImage> fanarts){
    this.fanarts = fanarts;
  }

  /**
   * Generate XBMC NFO file
   * @return Xbmc NFO file
   */
  public String getNFOFromMovie() {

    StringBuilder nfo = new StringBuilder();
    nfo.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n<movie>\n");
    nfo.append("  <title>").append(Utils.escapeXML(movieinfo.getTitle())).append("</title>\n");
    nfo.append("  <originaltitle>").append(Utils.escapeXML(movieinfo.getOrigTitle())).append("</originaltitle>\n");
    nfo.append("  <sorttitle>").append(Utils.escapeXML(movieinfo.getTitle())).append("</sorttitle>\n");
    nfo.append("  <set>").append(Utils.escapeXML(movieinfo.getSet())).append("</set>\n");
    nfo.append("  <rating>").append(Utils.escapeXML(movieinfo.getRating())).append("</rating>\n");
    nfo.append("  <year>").append(Utils.escapeXML(movieinfo.getYear())).append("</year>\n");
    nfo.append("  <plot>").append(Utils.escapeXML(movieinfo.getSynopsis())).append("</plot>\n");
    nfo.append("  <outline>").append(Utils.escapeXML(movieinfo.getSynopsis())).append("</outline>\n");
    nfo.append("  <tagline>").append(Utils.escapeXML(movieinfo.getTagline())).append("</tagline>\n");
    nfo.append("  <runtime>").append(movieinfo.getRuntime() == -1 ? "" : movieinfo.getRuntime()).append("</runtime>\n");
    nfo.append("  <id>").append(imdbId).append("</id>\n");
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

    nfo.append("  <fanart>\n");
    for (int i = 0; i < fanarts.size(); i++) {
      nfo.append("    <thumb preview=\"").append(fanarts.get(i).getThumbUrl()).append("\">").append(fanarts.get(i).getOrigUrl()).append("</thumb>\n");
    }
    nfo.append("  </fanart>\n");
    nfo.append("</movie>");
    return nfo.toString();
  }

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
