/*
 * movie-renamer
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
package fr.free.movierenamer.media.tvshow;

import fr.free.movierenamer.media.IMediaInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Class TvShowInfo
 *
 * @author QUÉMÉNEUR Simon
 * @author Nicolas Magré
 */
public class TvShowInfo implements IMediaInfo/*
 * <TvShowImage>
 */ {

  private String title;
  private String originalTitle;
  private SxE sxe;
  private String year;
  private String synopsis;
  private String rating;
  private List<TvShowSeason> seasons;

  public TvShowInfo() {
    title = "?";
    originalTitle = "";
    sxe = new SxE();
    year = "";
    synopsis = "";
    rating = "-1";
    seasons = new ArrayList<TvShowSeason>();
  }

  public SxE getSxe() {
    return sxe;
  }

  public List<TvShowSeason> getSeasons() {
    return seasons;
  }
  
  public String getOriginalTitle() {
    return originalTitle;
  }
  
  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  public void setSxe(SxE sxe) {
    this.sxe = sxe;
  }

  public void setSeasons(List<TvShowSeason> seasons) {
    this.seasons = seasons;
  }

  public void setOriginalTitle(String originalTitle) {
    this.originalTitle = originalTitle;
  }

  /**
   * @return the year
   */
  public String getYear() {
    return year;
  }

  /**
   * @param year the year to set
   */
  public void setYear(String year) {
    this.year = year;
  }

  /**
   * @return the synopsis
   */
  public String getSynopsis() {
    return synopsis;
  }

  /**
   * @param synopsis the synopsis to set
   */
  public void setSynopsis(String synopsis) {
    this.synopsis = synopsis;
  }

  /**
   * @return the rating
   */
  public String getRating() {
    return rating;
  }

  /**
   * @param rating the rating to set
   */
  public void setRating(String rating) {
    this.rating = rating;
  }
  
  /**
   * @param title the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }
}
