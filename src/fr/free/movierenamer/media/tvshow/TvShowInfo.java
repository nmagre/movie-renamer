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

import fr.free.movierenamer.utils.Utils;

import fr.free.movierenamer.media.MediaInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Class TvShowInfo
 *
 * @author QUÉMÉNEUR Simon
 * @author Nicolas Magré
 */
public class TvShowInfo extends MediaInfo {

  private final SxE sxe;
  private String poster;
  private List<TvShowSeason> seasons;

  public TvShowInfo() {
    super();
    sxe = new SxE();
    poster = "";
    seasons = new ArrayList<TvShowSeason>();
  }

  public SxE getSxe() {
    return sxe;
  }

  public List<TvShowSeason> getSeasons() {
    return seasons;
  }
  
  /**
   * @param seasons the seasons to set
   */
  public void setSeasons(List<TvShowSeason> seasons) {
    this.seasons = seasons;
  }

  /**
   * @return the poster
   */
  public String getPoster() {
    return poster;
  }
  
  /**
   * @param poster the poster to set
   */
  public void setPoster(String poster) {
    this.poster = poster;
  }
  
  /**
   * @param sxe the sxe to set
   */
  public void setSxe(SxE sxe) {
    this.sxe.setEpisode(sxe.getEpisode());
    this.sxe.setSeason(sxe.getSeason());
  }
  
}
