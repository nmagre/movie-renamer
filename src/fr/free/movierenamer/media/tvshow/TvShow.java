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
package fr.free.movierenamer.media.tvshow;

import fr.free.movierenamer.media.Media;
import fr.free.movierenamer.media.MediaFile;
import fr.free.movierenamer.utils.Settings;

/**
 *
 * @author duffy
 */
public class TvShow implements Media {

  private MediaFile mediaFile;

  //A faire
  
  public TvShow(){
    
  }
  
  @Override
  public MediaFile getMediaFile() {
    return mediaFile;
  }

  @Override
  public void setMediaFile(MediaFile mediaFile) {
    this.mediaFile = mediaFile;
  }

  @Override
  public int getType() {
    return Media.TVSHOW;
  }

  @Override
  public String getSearch() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setSearch(String search) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setInfo(Object info) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getRenamedTitle(String regex, Settings setting) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setId(String id) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
