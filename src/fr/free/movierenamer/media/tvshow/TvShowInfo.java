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
import java.util.List;

/**
 * Class TvShowInfo
 * 
 * @author QUÉMÉNEUR Simon
 */
public class TvShowInfo implements IMediaInfo<TvShowImage> {

  /*
   * (non-Javadoc)
   * 
   * @see fr.free.movierenamer.media.IMediaInfo#getTrailer()
   */
  @Override
  public String getTrailer() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.free.movierenamer.media.IMediaInfo#setTrailer(java.lang.String)
   */
  @Override
  public void setTrailer(String trailer) {
    // TODO Auto-generated method stub

  }

  /**
   * @return
   */
  public List<TvShowSeason> getSeasons() {
    // TODO Auto-generated method stub
    return null;
  }

}
