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

import javax.swing.JTextField;

import fr.free.movierenamer.ui.TvShowPanel;

import fr.free.movierenamer.media.movie.MovieInfo;

import fr.free.movierenamer.media.mediainfo.MITag;
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
 * @author QUÉMÉNEUR Simon
 */
public class TvShow extends Media<TvShowInfo> {// TODO

  private SxE sxe;
  private TvShowEpisode selectedEpisode;

  public TvShow(MediaFile tvShowFile, JTextField renameField) {
    super(tvShowFile, new TvShowInfo(), renameField);
    TvShowNameMatcher tvMatcher = new TvShowNameMatcher(tvShowFile, conf.mediaNameFilters);
    setSearch(tvMatcher.getTvShowName());
    sxe = new TvShowEpisodeMatcher(tvShowFile.getFile()).matchEpisode();
  }

  @Override
  public MediaType getType() {
    return Media.MediaType.TVSHOW;
  }

  public SxE getSearchSxe() {
    return sxe;
  }

  @Override
  public String getRenamedTitle(String regExp) {
    // TODO
    String shortTitle = mediaInfo.getTitle();
    TvShowEpisode episode = selectedEpisode;
    String[][] replace = new String[][]{
        {"<st>", shortTitle},
        {"<et>", episode.getTitle()},
        {"<s>", episode.getSeason().getNum()+""},
          {"<e>", episode.getNum()+""}
      };
    for (int i = 0; i < replace.length; i++) {
      regExp = regExp.replaceAll(replace[i][0], replace[i][1]);
    }
    return regExp;
  }
  
  /**
   * @param selectedEpisode the selectedEpisode to set
   */
  public void setSelectedEpisode(TvShowEpisode selectedEpisode) {
    this.selectedEpisode = selectedEpisode;
  }

}
