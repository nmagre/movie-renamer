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

  public void clear() {
    this.clearFanarts();
    this.clearThumbs();
    movieinfo = new MovieInfo();
  }

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

  public String getGenresString() {
    return movieinfo.getGenresString();
  }

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
  public String getMovieDBId() {
    return movieinfo.getMovieDBId();
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
   * @return ArrayList of MoviePerson
   */
  public ArrayList<MoviePerson> getPersons() {
    return movieinfo.getActors();
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
   * @return Movie title renamed
   */
  public String getRenamedTitle(String regExp) {
    String runtime = "";
    if (getRuntime() != -1) runtime += getRuntime();
    String[][] replace = new String[][]{{"<t>", getTitle()}, {"<tt>", getImdbId()}, {"<y>", getYear()},
      {"<rt>", runtime}, {"<ra>", getRating()}, {"<d>", movieinfo.getDirectors()},
      {"<d1>", movieinfo.getFirstDirector()}, {"<g>", movieinfo.getGenresString()},
      {"<g1>", movieinfo.getFirstGenreString()}};
    for (int i = 0; i < replace.length; i++) {
      regExp = regExp.replaceAll(replace[i][0], replace[i][1]);
    }
    String fileName = getFile().getName();
    String ext = fileName.substring(fileName.lastIndexOf('.') + 1);

    return regExp + "." + ext;
  }

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

  public void setTitle(String title) {
    movieinfo.setTitle(title);
  }

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
   * Set movie the movie DB ID
   * @param movieDBId Movie the movie DB ID
   */
  public void setMovieDBId(String movieDBId) {
    movieinfo.setMovieDBId(movieDBId);
  }

  /**
   * Set imdb ID
   * @param imdbId Imdb ID
   */
  public void setImdbId(String imdbId) {
    this.imdbId = imdbId;
  }

  public void clearThumbs() {
    thumbs.clear();
  }

  public void clearFanarts() {
    fanarts.clear();
  }

  public void setImdbTitle(String imdbTitle) {
    this.imdbTitle = imdbTitle;
  }

  public void setThumbs(ArrayList<MovieImage> thumbs){
    this.thumbs = thumbs;
  }

  public void setFanarts(ArrayList<MovieImage> fanarts){
    this.fanarts = fanarts;
  }

  /**
   * Generate XBMC NFO file
   * @return Xbmc NFO file
   */
  public String getNFOFromMovie() {//parcourir 3 fois la liste des acteurs = pas terrible (a refaire)

    String nfo = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n<movie>\n";
    nfo += "  <title>" + Utils.escapeXML(movieinfo.getTitle()) + "</title>\n";
    nfo += "  <originaltitle>" + Utils.escapeXML(movieinfo.getOrigTitle()) + "</originaltitle>\n";
    nfo += "  <sorttitle>" + Utils.escapeXML(movieinfo.getTitle()) + "</sorttitle>\n";
    nfo += "  <set>" + Utils.escapeXML(movieinfo.getSet()) + "</set>\n";
    nfo += "  <rating>" + Utils.escapeXML(movieinfo.getRating()) + "</rating>\n";
    nfo += "  <year>" + Utils.escapeXML(movieinfo.getYear()) + "</year>\n";
    nfo += "  <plot>" + Utils.escapeXML(movieinfo.getSynopsis()) + "</plot>\n";
    nfo += "  <outline>" + Utils.escapeXML(movieinfo.getSynopsis()) + "</outline>\n";
    nfo += "  <tagline>" + Utils.escapeXML(movieinfo.getTagline()) + "</tagline>\n";
    nfo += "  <runtime>" + (movieinfo.getRuntime() == -1 ? "":movieinfo.getRuntime()) + "</runtime>\n";
    nfo += "  <id>" + imdbId + "</id>\n";
    nfo += printArrayString(movieinfo.getGenres(), "genre", "  ");
    nfo += printArrayString(movieinfo.getCountries(), "country", "  ");
    nfo += printArrayString(movieinfo.getStudios(), "studio", "  ");

    for (int i = 0; i < movieinfo.getActors().size(); i++) {
      if (movieinfo.getActors().get(i).getJob().equals("Writer"))
        nfo += "  <credits>" + Utils.escapeXML(movieinfo.getActors().get(i).getName()) + "</credits>\n";
    }

    for (int i = 0; i < movieinfo.getActors().size(); i++) {
      if (movieinfo.getActors().get(i).getJob().equals("Director"))
        nfo += "  <director>" + Utils.escapeXML(movieinfo.getActors().get(i).getName()) + "</director>\n";
    }

    nfo += "  <trailer>" + movieinfo.getTrailer() + "</trailer>\n";

    for (int i = 0; i < movieinfo.getActors().size(); i++) {
      if (!movieinfo.getActors().get(i).getJob().equals("Actor")) continue;
      nfo += "  <actor>\n";
      nfo += "    <name>" + Utils.escapeXML(movieinfo.getActors().get(i).getName()) + "</name>\n";
      for (int j = 0; j < movieinfo.getActors().get(i).getRoles().size(); j++) {
        nfo += "    <role>" + Utils.escapeXML(movieinfo.getActors().get(i).getRoles().get(j)) + "</role>\n";
      }
      nfo += "    <thumb>" + movieinfo.getActors().get(i).getThumb() + "</thumb>\n";
      nfo += "  </actor>\n";
    }

    for (int i = 0; i < thumbs.size(); i++) {
      nfo += "  <thumb preview=\"" + thumbs.get(i).getThumbUrl() + "\">" + thumbs.get(i).getOrigUrl() + "</thumb>\n";
    }

    nfo += "  <fanart>\n";
    for (int i = 0; i < fanarts.size(); i++) {
      nfo += "    <thumb preview=\"" + fanarts.get(i).getThumbUrl() + "\">" + fanarts.get(i).getOrigUrl() + "</thumb>\n";
    }
    nfo += "  </fanart>\n";
    nfo += "</movie>";
    return nfo;
  }

  private String printArrayString(ArrayList<String> arrayString, String tag, String level) {
    String res = "";
    for (int i = 0; i < arrayString.size(); i++) {
      res += level + "<" + tag + ">" + Utils.escapeXML(arrayString.get(i)) + "</" + tag + ">\n";
    }
    return res;
  }

  @Override
  public String toString() {
    return imdbTitle;
  }  
}
