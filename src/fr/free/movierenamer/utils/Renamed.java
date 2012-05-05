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
package fr.free.movierenamer.utils;

/**
 * Class Renamed , Renamed movie
 * @author Nicolas Magré
 */
public class Renamed {
//A refaire
//l'inclure dans Media en "MediaRenamed"
//ajouter une rotation sur le fichier XML paramétrable dans "Settings" (ex:10,50,100,illimité) et pourquoi une function "undo" 

  private String title;
  private String date;
  private String movieFileSrc;
  private String movieFileDest;
  private boolean renameFailed;
  private String thumb;
  private String tmdbId;

  /**
   * Constructor arguments
   * @param title Rename title
   */
  public Renamed(String title){
    this.title = title;
    renameFailed = false;
  }

  /**
   * Constructor arguments
   * @param title Rename title
   * @param date Rename date
   * @param movieFileSrc Rename src file
   * @param movieFileDest Rename dest file
   */
  public Renamed(String title, String date, String movieFileSrc,String movieFileDest) {
    this.title = title;
    this.date = date;
    this.movieFileSrc = movieFileSrc;
    this.movieFileDest = movieFileDest;
    renameFailed = false;
  }

  /**
   * Get rename title
   * @return title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Get rename date
   * @return date
   */
  public String getDate() {
    return date;
  }

  /**
   * Get movie source file
   * @return movie file
   */
  public String getMovieFileSrc() {
    return movieFileSrc;
  }

  /**
   * Get movie destination file
   * @return destination file
   */
  public String getMovieFileDest() {
    return movieFileDest;
  }

  /**
   * Set rename title
   * @param title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Set rename date
   * @param date
   */
  public void setDate(String date) {
    this.date = date;
  }

  /**
   * Set movie source file
   * @param movieFileSrc
   */
  public void setMovieFileSrc(String movieFileSrc) {
    this.movieFileSrc = movieFileSrc;
  }

  /**
   * Set movie destination file
   * @param movieFileDest
   */
  public void setMovieFileDest(String movieFileDest) {
    this.movieFileDest = movieFileDest;
  }

  /**
   * Set rename failed
   * @param renameFailed
   */
  public void setRenameFailed(boolean renameFailed){
    this.renameFailed = renameFailed;
  }

  /**
   * Set thumb
   * @param thumb
   */
  public void setThumb(String thumb){
    this.thumb = thumb;
  }

  /**
   * Set themoviedb id
   * @param id
   */
  public void setTmDbId(String id){
    tmdbId = id;
  }

  @Override
  public String toString(){
    String res = "";
    res += "<renamedMovie title=\"" + title.replace("\"", "") + "\">";
    res += "  <tmdbId>" + tmdbId + "</tmdbId>";
    res += "  <movie src=\"" + movieFileSrc.replace("\"", "") + "\" dest=\"" + movieFileDest.replace("\"", "") + "\" />";
    res += "  <thumb>" + thumb + "</thumb>";
    res += "  <date>" + date + "</date>";
    res += "  <failed>" + (renameFailed ? "1":"0") + "</failed>";
    res += "</renamedMovie>";
    return res;
  }
}
