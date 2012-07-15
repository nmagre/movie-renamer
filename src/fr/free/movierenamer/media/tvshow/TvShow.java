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

import fr.free.movierenamer.matcher.TvShowEpisodeMatcher;
import fr.free.movierenamer.matcher.TvShowNameMatcher;
import fr.free.movierenamer.media.Media;
import fr.free.movierenamer.media.MediaFile;
import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.media.MediaTag;
import fr.free.movierenamer.utils.Settings;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author duffy
 */
public class TvShow implements Media {//A faire

  private MediaID mediaId;
  private MediaFile tvShowFile;
  private ArrayList<TvShowSeason> seasons;
  private MediaTag mtag;
  private SxE sxe;
  private String search;

  public TvShow(MediaFile tvShowFile, List<String> regexs) {
    this.tvShowFile = tvShowFile;
    TvShowNameMatcher tvMatcher = new TvShowNameMatcher(tvShowFile, regexs);
    search = tvMatcher.getTvShowName();
    sxe = new TvShowEpisodeMatcher(tvShowFile.getFile().getParent() + File.separator + tvShowFile.getFile().getName()).matchEpisode();
    seasons = new ArrayList<TvShowSeason>();
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
  public MediaType getType() {
    return Media.MediaType.TVSHOW;
  }

  @Override
  public String getSearch() {
    return search;
  }
  
  public SxE getSearchSxe() {
    return sxe;
  }

  @Override
  public void setSearch(String search) {
    this.search = search;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void setInfo(Object info) {
    if (info instanceof ArrayList) {
      if(((ArrayList)info).size() > 0 && ((ArrayList)info).get(0).getClass() == TvShowSeason.class) {
        seasons = (ArrayList<TvShowSeason>) info;
      }
    }
  }

  @Override
  public void clear() {// A faire
  }

  @Override
  public String getRenamedTitle(String regex, Settings setting) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setMediaID(MediaID id) {
    mediaId = id;
  }

  @Override
  public MediaID getMediaId(int IDtype) {
    if (mediaId.getType() == IDtype) {
      return mediaId;
    }
/*
    for (MediaID mid : tvshowInfo.getIDs()) {
      if (mid.getType() == IDtype) {
        return mid;
      }
    }*/

    return null;
  }

  @Override
  public int getYear() {
    return -1;
  }
}
