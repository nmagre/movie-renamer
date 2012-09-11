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

import fr.free.movierenamer.media.movie.MovieInfo;

import fr.free.movierenamer.media.mediainfo.MediaTag;
import fr.free.movierenamer.matcher.TvShowEpisodeMatcher;
import fr.free.movierenamer.matcher.TvShowNameMatcher;
import fr.free.movierenamer.media.MediaImage.MediaImageType;
import fr.free.movierenamer.media.*;
import fr.free.movierenamer.utils.ActionNotValidException;
import java.io.File;
import java.util.List;

/**
 * Class TvShow
 * @author Nicolas Magré
 */
public class TvShow implements Media {// TODO

  private MediaID mediaId;
  private MediaFile tvShowFile;
  private TvShowInfo tvShowInfo;
  private MediaTag mtag;
  private SxE sxe;
  private String search;

  public TvShow(MediaFile tvShowFile) {
    this.tvShowFile = tvShowFile;
    TvShowNameMatcher tvMatcher = new TvShowNameMatcher(tvShowFile, conf.mediaNameFilters);
    search = tvMatcher.getTvShowName();
    sxe = new TvShowEpisodeMatcher(tvShowFile.getFile()).matchEpisode();
    tvShowInfo = new TvShowInfo();
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
  public void setInfo(Object info) {
    if (info instanceof TvShowInfo) {
      tvShowInfo = (TvShowInfo) info;
    } else {
      tvShowInfo = new TvShowInfo();
    }
  }

  @Override
  public void clear() {
    // TODO
  }

  @Override
  public String getRenamedTitle(String regExp) {
    // TODO
    return "";
  }

  @Override
  public void setMediaID(MediaID id) {
    mediaId = id;
  }

  @Override
  public MediaID getMediaId(MediaID.MediaIdType IDtype) {
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

  @Override
  public void addMediaID(MediaID id) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setDefaultSearch() {
    // TODO
  }

  @Override
  public List<MediaPerson> getActors() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<MediaImage> getImages(MediaImageType type) throws ActionNotValidException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  /**
   * @return the tvShowInfo
   */
  public TvShowInfo getTvShowInfo() {
    return tvShowInfo;
  }
}
