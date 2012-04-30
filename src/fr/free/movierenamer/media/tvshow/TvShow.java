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
import fr.free.movierenamer.utils.TvShowNameMatcher;

/**
 *
 * @author duffy
 */
public class TvShow implements Media {

  private MediaFile tvShowFile;
  private String tvShowId;
  private String search;

  public TvShow(MediaFile tvShowFile) {
    this.tvShowFile = tvShowFile;
    TvShowNameMatcher tvMatcher = new TvShowNameMatcher(tvShowFile);    
    search = tvMatcher.getTvShowName();
    System.out.println("\n  Tv show Title : " + search + "\n");
  }

  @Override
  public MediaFile getMediaFile() {
    return tvShowFile;
  }

  @Override
  public void setMediaFile(MediaFile tvShowFile) {
    this.tvShowFile = tvShowFile;
  }

  @Override
  public int getType() {
    return Media.TVSHOW;
  }

  @Override
  public String getSearch() {//A refaire
    return search;
  }

  @Override
  public void setSearch(String search) {
    this.search = search;
  }

  @Override
  public void setInfo(Object info) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void clear() {// A faire
  }

  @Override
  public String getRenamedTitle(String regex, Settings setting) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setId(String id) {
    tvShowId = id;
  }
}
