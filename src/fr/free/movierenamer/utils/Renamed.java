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

  public Renamed(String title){
    this.title = title;
  }
  
  public Renamed(String title, String date, String movieFileSrc,String movieFileDest) {
    this.title = title;
    this.date = date;
    this.movieFileSrc = movieFileSrc;
    this.movieFileDest = movieFileDest;
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
}
