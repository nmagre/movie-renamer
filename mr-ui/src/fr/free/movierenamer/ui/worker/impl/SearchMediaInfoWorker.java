/*
 * Movie Renamer
 * Copyright (C) 2012-2014 Nicolas Magré
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
package fr.free.movierenamer.ui.worker.impl;

import fr.free.movierenamer.info.FileInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.VideoInfo;
import fr.free.movierenamer.scrapper.MediaScrapper;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIMediaInfo;
import fr.free.movierenamer.ui.bean.UIMovieInfo;
import fr.free.movierenamer.ui.bean.UISearchResult;
import fr.free.movierenamer.ui.swing.panel.MediaPanel;
import fr.free.movierenamer.ui.worker.Worker;

/**
 * Class SearchMediaInfosWorker
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaInfoWorker extends Worker<UIMediaInfo> {

  private final MediaScrapper<Media, MediaInfo> scrapper;
  private final UISearchResult searchResult;

  /**
   * Constructor arguments
   *
   * @param mr
   * @param searchResult
   */
  @SuppressWarnings("unchecked")
  public SearchMediaInfoWorker(MovieRenamer mr, UISearchResult searchResult) {
    super(mr);
    this.searchResult = searchResult;
    this.scrapper = (searchResult != null) ? (MediaScrapper<Media, MediaInfo>) searchResult.getScrapper() : null;
  }

  @Override
  public UIMediaInfo executeInBackground() throws Exception {
    UIMediaInfo info = null;
    if (searchResult != null && scrapper != null) {
      Media media = searchResult.getSearchResult();
      MediaInfo inf = scrapper.getInfo(media);
      
      if (inf instanceof VideoInfo) {
        FileInfo fileInfo = mr.getFile().getFileInfo();
        ((VideoInfo) inf).setMediaTag(fileInfo.getMediaTag());
      }

      if (inf instanceof MovieInfo) {
        info = new UIMovieInfo((MovieInfo) inf);
      }

    }

    return info;
  }

  @Override
  protected void workerDone() throws Exception {
    UIMediaInfo info = get();

    // Search info failed, we let the user try again by clearing selection
    if (info == null) {
      mr.getSearchResultList().clearSelection();
      return;
    }

    MediaPanel mediaPanel = mr.getMediaPanel();
    if (mediaPanel != null) {
      mediaPanel.setInfo(info);
    }

    mr.updateRenamedTitle();
    mr.setRenameFieldEnabled();
    mr.setRenamebuttonEnabled();
  }

  @Override
  public String getParam() {
    return String.format("[%s]", searchResult);
  }

  @Override
  public String getDisplayName() {
    return ("worker.searchMediaInfo");// FIXME i18n
  }
}
