/******************************************************************************
 *                                                                             *
 *    Movie Renamer                                                            *
 *    Copyright (C) 2011 Magré Nicolas                                         *
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
package fr.free.movierenamer.utils;

/**
 *
 * @author duffy
 */
public class Renamed {

  private String title;
  private String date;
  private String movieFileSrc;
  private String movieFileDest;
  private String thumbFileSrc;
  private String thumbFileDest;
  private String fanartFileSrc;
  private String fanartFileDest;
  private String srtFileSrc;
  private String srtFileDst;
  private boolean newDirectory;

  public Renamed(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public String getDate() {
    return date;
  }

  public String getMovieFileSrc() {
    return movieFileSrc;
  }

  public String getMovieFileDest() {
    return movieFileDest;
  }

  public String getThumbFileSrc() {
    return thumbFileSrc;
  }

  public String getThumbFileDest() {
    return thumbFileDest;
  }

  public String getFanartFileSrc() {
    return fanartFileSrc;
  }

  public String getFanartFileDest() {
    return fanartFileDest;
  }

  public String getSrtFileSrc() {
    return srtFileSrc;
  }

  public String getSrtFileDst() {
    return srtFileDst;
  }

  public boolean getNewDirectory() {
    return newDirectory;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public void setMovieFileSrc(String movieFileSrc) {
    this.movieFileSrc = movieFileSrc;
  }

  public void setMovieFileDest(String movieFileDest) {
    this.movieFileDest = movieFileDest;
  }

  public void setThumbFileSrc(String thumbFileSrc) {
    this.thumbFileSrc = thumbFileSrc;
  }

  public void setThumbFileDest(String thumbFileDest) {
    this.thumbFileDest = thumbFileDest;
  }

  public void setFanartFileSrc(String fanartFileSrc) {
    this.fanartFileSrc = fanartFileSrc;
  }

  public void setFanartFileDest(String fanartFileDest) {
    this.fanartFileDest = fanartFileDest;
  }

  public void setSrtFileSrc(String srtFileSrc) {
    this.srtFileSrc = srtFileSrc;
  }

  public void setSrtFileDst(String srtFileDst) {
    this.srtFileDst = srtFileDst;
  }

  public void setNewDirectory(boolean newDirectory) {
    this.newDirectory = newDirectory;
  }
}
