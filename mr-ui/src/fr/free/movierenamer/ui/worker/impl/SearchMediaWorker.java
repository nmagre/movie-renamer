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

import com.alee.laf.list.WebList;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.scraper.SearchScraper;
import fr.free.movierenamer.searchinfo.Hyperlink;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.ui.bean.UISearchResult;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.swing.ImageListModel;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.ui.worker.Worker;
import fr.free.movierenamer.ui.worker.WorkerManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Class SearchMediaWorker
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaWorker extends Worker<List<UISearchResult>> {

  private final UIFile media;
  private final SearchScraper<? extends Media> scraper;
  private final WebList searchResultList;
  private final ImageListModel<UISearchResult> searchResultModel;

  /**
   * Constructor arguments
   *
   * @param mr
   * @param media
   */
  public SearchMediaWorker(MovieRenamer mr, UIFile media) {
    super(mr);
    this.media = media;
    this.scraper = mr.getUIScraper().getScraper();
    this.searchResultList = mr.getSearchResultList();
    this.searchResultModel = mr.getSearchResultListModel();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<UISearchResult> executeInBackground() throws Exception {
    List<UISearchResult> results = new ArrayList<>();
    List<? extends Media> res;

    if (media != null && scraper != null) {
      String search = media.getSearch();
      res = scraper.search(search, media.getYear());
      int count = res.size();

      for (int i = 0; i < count; i++) {
        if (isCancelled()) {
          return new ArrayList<>();
        }

        results.add(new UISearchResult(res.get(i), scraper));
      }
    }

    int nbres = Settings.getInstance().getSearchNbResult();
    if (results.size() > nbres) {
      while (results.size() > nbres) {
        results.remove(results.size() - 1);
      }
    }

    return results;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void workerDone() throws Exception {

    mr.setSearchEnabled();
    searchResultList.setModel(searchResultModel);

    List<UISearchResult> results = get();

    if (results == null) {
      return;
    }

    searchResultModel.addAll(results);

    if (searchResultModel.isEmpty()) {
      UIUtils.showNoResultNotification(media.getSearch());
    } else {
      if (UISettings.getInstance().isSelectFirstResult()) {
        searchResultList.setSelectedIndex(0);
      }

      WorkerManager.fetchImages(WorkerId.IMAGE_SEARCH_RESULT, searchResultModel, ImageInfo.ImageSize.small, UIUtils.listImageSize, ImageUtils.NO_IMAGE, mr.isShowIconResult());
    }
  }

  @Override
  protected void workerCanceled() {
    mr.setSearchEnabled();
  }

  @Override
  public String getDisplayName() {
    return UIUtils.i18n.getLanguage("main.statusTb.searchmedia", false);
  }

  @Override
  public WorkerId getWorkerId() {
    return WorkerId.SEARCH;
  }
}
