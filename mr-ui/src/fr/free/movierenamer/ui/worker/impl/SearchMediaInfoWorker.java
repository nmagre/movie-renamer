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

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.FileInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.scrapper.MediaScrapper;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIPersonImage;
import fr.free.movierenamer.ui.bean.UISearchResult;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.swing.panel.MediaPanel;
import fr.free.movierenamer.ui.swing.panel.info.CastingInfoPanel;
import fr.free.movierenamer.ui.swing.panel.info.InfoPanel;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.ui.worker.Worker;
import fr.free.movierenamer.ui.worker.WorkerManager;
import java.util.ArrayList;
import java.util.List;

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
      FileInfo fileInfo = mr.getFile().getFileInfo();
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

    List<CastingInfo> casting = info.getCasting();
    if (!casting.isEmpty()) {
      CastingInfoPanel<?> castingPanel = (CastingInfoPanel) mr.getMediaPanel().getPanel(InfoPanel.PanelType.CASTING_INFO);
      if (castingPanel != null) {
        // Copy to avoid reference issue
        List<UIPersonImage> tmp = new ArrayList<>(castingPanel.getActors());
        List<UIPersonImage> tmp1 = new ArrayList<>(castingPanel.getDirectors());

        WorkerManager.fetchImages(tmp, castingPanel.getActorListModel(), UIUtils.listImageSize, ImageUtils.UNKNOWN, UISettings.getInstance().isShowActorImage());
        WorkerManager.fetchImages(tmp1, castingPanel.getDirectorListModel(), UIUtils.listImageSize, ImageUtils.UNKNOWN, UISettings.getInstance().isShowActorImage());
      }
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
