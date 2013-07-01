/*
 * Movie Renamer
 * Copyright (C) 2012-2013 Nicolas Magré
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
import fr.free.movierenamer.scrapper.MediaScrapper;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UISearchResult;
import fr.free.movierenamer.ui.swing.panel.generator.info.MediaPanel;
import fr.free.movierenamer.ui.worker.Worker;

/**
 * Class SearchMediaInfosWorker
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaInfoWorker extends Worker<MediaInfo> {

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
  public MediaInfo executeInBackground() throws Exception {
    MediaInfo info = null;
    if (searchResult != null && scrapper != null) {
      Media media = searchResult.getSearchResult();
      info = scrapper.getInfo(media);
      FileInfo fileInfo = mr.getMediaPanel().getFileInfo();
      // If GetFileInfoWorker is not done, we get file info in this thread
      if(fileInfo == null) {
        fileInfo = new FileInfo(mr.getFile().getFile());
      }
      info.setMediaTag(fileInfo.getMediaTag());
    }
    return info;
  }

  @Override
  protected void workerDone() throws Exception {
    MediaInfo info = get();

    // Search info failed, we let the user try again by clearing selection
    if (info == null) {
      mr.getSearchResultList().clearSelection();
      return;
    }

    @SuppressWarnings("unchecked")
    MediaPanel<MediaInfo> mediaPanel = (MediaPanel<MediaInfo>) mr.getMediaPanel();
    mediaPanel.setInfo(info);
    
    mr.updateRenamedTitle();
  }

  @Override
  protected String getName() {
    return "Search Media Info";
  }
}
