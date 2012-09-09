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

  private String originalTitle;
  private SxE sxe;
  private List<TvShowSeason> seasons;

  public TvShowInfo() {
    sxe = new SxE();
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

  public void setSxe(SxE sxe) {
    this.sxe = sxe;
  }

  public void setSeasons(List<TvShowSeason> seasons) {
    this.seasons = seasons;
  }

  public void setOriginalTitle(String originalTitle) {
    this.originalTitle = originalTitle;
  }
}
