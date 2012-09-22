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

import fr.free.movierenamer.media.MediaData;
import fr.free.movierenamer.utils.Utils;
import java.util.ArrayList;
import java.util.List;

/**
 * Class MovieInfo
 * 
 * @author Nicolas Magré
 */
public class MovieInfo extends MediaData {

  private String outline;
  private String tagline;
  private String mpaa;
  private String top250;
  private boolean watched;
  private List<String> set;// Saga
  private List<String> studios;

  /**
   * Default constructor
   */
  public MovieInfo() {
    super();
    outline = "";
    tagline = "";
    top250 = "0";
    watched = false;
    mpaa = "";
    set = new ArrayList<String>();
    studios = new ArrayList<String>();
  }

  /**
   * Get outline
   * 
   * @return Outline
   */
  public String getOutline() {
    return outline;
  }

  /**
   * Get tagline
   * 
   * @return Tagline
   */
  public String getTagline() {
    return tagline;
  }

  /**
   * Get top 250
   * 
   * @return 0 or top 250 position
   */
  public String getTop250() {
    return top250;
  }

  /**
   * Movie was watched
   * 
   * @return Ture or false
   */
  public boolean getWatched() {
    return watched;
  }

  /**
   * Get set
   * 
   * @return Array of set
   */
  public List<String> getSet() {
    return set;
  }

  /**
   * Get mpaa
   * 
   * @return Mpaa
   */
  public String getMpaa() {
    return mpaa;
  }

  /**
   * Get studios
   * 
   * @return Array of studios
   */
  public List<String> getStudios() {
    return studios;
  }

  /**
   * Get studios to string
   * 
   * @param separator Separator
   * @param limit Number of studios to return (0 for all)
   * @return Studios separated by separator
   */
  public String getStudiosString(String separator, int limit) {
    return Utils.arrayToString(studios, separator, limit);
  }

  /**
   * Get sets to string
   * 
   * @param separator Separator
   * @param limit Number of sets to return (0 for all)
   * @return Sets separated by separator
   */
  public String getSetString(String separator, int limit) {
    return Utils.arrayToString(set, separator, limit);
  }

  /**
   * Set studios
   * 
   * @param studios Array of studio
   */
  public void setStudios(List<String> studios) {
    this.studios = studios;
  }

  /**
   * Set sets
   * 
   * @param set Array of sets
   */
  public void setSet(List<String> set) {
    this.set = set;
  }

  /**
   * Set outline
   * 
   * @param outline Outline
   */
  public void setOutline(String outline) {
    this.outline = outline;
  }

  /**
   * Set tagline
   * 
   * @param tagline Tagline
   */
  public void setTagline(String tagline) {
    this.tagline = tagline;
  }

  /**
   * Set top 250
   * 
   * @param top250
   */
  public void setTop250(String top250) {
    this.top250 = top250;
  }

  /**
   * Set movie watched
   * 
   * @param watched
   */
  public void setWatched(boolean watched) {
    this.watched = watched;
  }

//  /**
//   * Set sets
//   * 
//   * @param set Array of set
//   */
//  public void setSet(List<String> set) {
//    this.set = set;
//  }

  /**
   * Set mpaa
   * 
   * @param mpaa Mpaa
   */
  public void setMpaa(String mpaa) {
    this.mpaa = mpaa;
  }
  
  /**
   * Add studio
   * 
   * @param studio Studio
   */
  public void addStudio(String studio) {
    studios.add(studio);
  }

//  /**
//   * Add set
//   * 
//   * @param strSet Set
//   */
//  public void addSet(String strSet) {
//    set.add(strSet);
//  }

//  @Override
//  public boolean equals(Object obj) {
//    if (obj == null) {
//      return false;
//    }
//    if (getClass() != obj.getClass()) {
//      return false;
//    }
//    final MovieInfo other = (MovieInfo) obj;
//    if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
//      return false;
//    }
//    if ((this.sortTitle == null) ? (other.sortTitle != null) : !this.sortTitle.equals(other.sortTitle)) {
//      return false;
//    }
//    if ((this.thumb == null) ? (other.thumb != null) : !this.thumb.equals(other.thumb)) {
//      return false;
//    }
//    if ((this.trailer == null) ? (other.trailer != null) : !this.trailer.equals(other.trailer)) {
//      return false;
//    }
//    if ((this.synopsis == null) ? (other.synopsis != null) : !this.synopsis.equals(other.synopsis)) {
//      return false;
//    }
//    if ((this.outline == null) ? (other.outline != null) : !this.outline.equals(other.outline)) {
//      return false;
//    }
//    if ((this.tagline == null) ? (other.tagline != null) : !this.tagline.equals(other.tagline)) {
//      return false;
//    }
//    if ((this.origTitle == null) ? (other.origTitle != null) : !this.origTitle.equals(other.origTitle)) {
//      return false;
//    }
//    if ((this.rating == null) ? (other.rating != null) : !this.rating.equals(other.rating)) {
//      return false;
//    }
//    if ((this.mpaa == null) ? (other.mpaa != null) : !this.mpaa.equals(other.mpaa)) {
//      return false;
//    }
//    if ((this.runtime == null) ? (other.runtime != null) : !this.runtime.equals(other.runtime)) {
//      return false;
//    }
//    if ((this.year == null) ? (other.year != null) : !this.year.equals(other.year)) {
//      return false;
//    }
//    if ((this.votes == null) ? (other.votes != null) : !this.votes.equals(other.votes)) {
//      return false;
//    }
//    if ((this.top250 == null) ? (other.top250 != null) : !this.top250.equals(other.top250)) {
//      return false;
//    }
//    if (this.watched != other.watched) {
//      return false;
//    }
//    if (this.movieImages != other.movieImages && (this.movieImages == null || !this.movieImages.equals(other.movieImages))) {
//      return false;
//    }
//    if (this.movieIDs != other.movieIDs && (this.movieIDs == null || !this.movieIDs.equals(other.movieIDs))) {
//      return false;
//    }
//    if (this.set != other.set && (this.set == null || !this.set.equals(other.set))) {
//      return false;
//    }
//    if (this.genres != other.genres && (this.genres == null || !this.genres.equals(other.genres))) {
//      return false;
//    }
//    if (this.studios != other.studios && (this.studios == null || !this.studios.equals(other.studios))) {
//      return false;
//    }
//    if (this.actors != other.actors && (this.actors == null || !this.actors.equals(other.actors))) {
//      return false;
//    }
//    if (this.directors != other.directors && (this.directors == null || !this.directors.equals(other.directors))) {
//      return false;
//    }
//    if (this.writers != other.writers && (this.writers == null || !this.writers.equals(other.writers))) {
//      return false;
//    }
//    if (this.countries != other.countries && (this.countries == null || !this.countries.equals(other.countries))) {
//      return false;
//    }
//    return true;
//  }
//
//  @Override
//  public int hashCode() {
//    int hash = 7;
//    hash = 97 * hash + (this.title != null ? this.title.hashCode() : 0);
//    hash = 97 * hash + (this.sortTitle != null ? this.sortTitle.hashCode() : 0);
//    hash = 97 * hash + (this.thumb != null ? this.thumb.hashCode() : 0);
//    hash = 97 * hash + (this.trailer != null ? this.trailer.hashCode() : 0);
//    hash = 97 * hash + (this.synopsis != null ? this.synopsis.hashCode() : 0);
//    hash = 97 * hash + (this.outline != null ? this.outline.hashCode() : 0);
//    hash = 97 * hash + (this.tagline != null ? this.tagline.hashCode() : 0);
//    hash = 97 * hash + (this.origTitle != null ? this.origTitle.hashCode() : 0);
//    hash = 97 * hash + (this.rating != null ? this.rating.hashCode() : 0);
//    hash = 97 * hash + (this.mpaa != null ? this.mpaa.hashCode() : 0);
//    hash = 97 * hash + (this.runtime != null ? this.runtime.hashCode() : 0);
//    hash = 97 * hash + (this.year != null ? this.year.hashCode() : 0);
//    hash = 97 * hash + (this.votes != null ? this.votes.hashCode() : 0);
//    hash = 97 * hash + (this.top250 != null ? this.top250.hashCode() : 0);
//    hash = 97 * hash + (this.watched ? 1 : 0);
//    hash = 97 * hash + (this.movieImages != null ? this.movieImages.hashCode() : 0);
//    hash = 97 * hash + (this.movieIDs != null ? this.movieIDs.hashCode() : 0);
//    hash = 97 * hash + (this.set != null ? this.set.hashCode() : 0);
//    hash = 97 * hash + (this.genres != null ? this.genres.hashCode() : 0);
//    hash = 97 * hash + (this.studios != null ? this.studios.hashCode() : 0);
//    hash = 97 * hash + (this.actors != null ? this.actors.hashCode() : 0);
//    hash = 97 * hash + (this.directors != null ? this.directors.hashCode() : 0);
//    hash = 97 * hash + (this.writers != null ? this.writers.hashCode() : 0);
//    hash = 97 * hash + (this.countries != null ? this.countries.hashCode() : 0);
//    return hash;
//  }
//  
//  @Override
//  public String toString() {
//    StringBuilder res = new StringBuilder();
//    res.append(title).append(Utils.ENDLINE);
//    res.append("  Trailer : ").append(trailer).append(Utils.ENDLINE);
//    res.append("  Thumbnail : ").append(thumb).append(Utils.ENDLINE);
//    res.append("  Synopsis : ").append(synopsis).append(Utils.ENDLINE);
//    res.append("  Short-Synopsis : ").append(outline).append(Utils.ENDLINE);
//    res.append("  Set(Saga) : ").append(getSetString(" | ", 0)).append(Utils.ENDLINE);
//    res.append("  Mpaa : ").append(mpaa).append(Utils.ENDLINE);
//    res.append("  Tagline : ").append(tagline).append(Utils.ENDLINE);
//    res.append("  OrigTitle : ").append(origTitle).append(Utils.ENDLINE);
//    res.append("  Rating : ").append(rating).append(Utils.ENDLINE);
//    res.append("  Runtime : ").append(runtime).append(Utils.ENDLINE);
//    res.append("  Year : ").append(year).append(Utils.ENDLINE);
//    res.append("  Vote : ").append(votes).append(Utils.ENDLINE);
//    res.append("  Top 250 : ").append(top250).append(Utils.ENDLINE);
//    res.append("  Watched : ").append(watched ? "True" : "False").append(Utils.ENDLINE);
//    res.append("  Genre : ").append(Utils.arrayToString(genres, " | ", 0)).append(Utils.ENDLINE);
//    res.append("  Studio : ").append(Utils.arrayToString(studios, " | ", 0)).append(Utils.ENDLINE);
//    res.append("  Country : ").append(Utils.arrayToString(countries, " | ", 0)).append(Utils.ENDLINE);
//    res.append("  Director : ").append(getDirectorsString(" | ", 0)).append(Utils.ENDLINE);
//    res.append("  Writer : ").append(getWritersString(" | ", 0)).append(Utils.ENDLINE);
//    res.append("  Actor :\n");
//
//    for (int i = 0; i < actors.size(); i++) {
//      res.append("    ").append(actors.get(i).getName()).append(" : ").append(actors.get(i).getRoles()).append(Utils.ENDLINE);
//    }
//    res.append(movieImages.toString());
//
//    res.append(Utils.ENDLINE);
//    
//    res.append("IDs :").append(Utils.ENDLINE);
//    for(MediaID id : movieIDs) {
//      res.append(id);
//    }
//    res.append(Utils.ENDLINE);
//    res.append("Images : ").append(Utils.ENDLINE).append(movieImages);
//    
//    return res.toString();
//  }
}
