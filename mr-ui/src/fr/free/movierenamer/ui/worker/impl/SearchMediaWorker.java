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

import com.alee.laf.list.WebList;
import fr.free.movierenamer.scrapper.SearchScrapper;
import fr.free.movierenamer.searchinfo.Hyperlink;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.ui.bean.UISearchResult;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.swing.ImageListModel;
import fr.free.movierenamer.ui.worker.Worker;
import fr.free.movierenamer.ui.worker.WorkerManager;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.Sorter;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JOptionPane;

/**
 * Class SearchMediaWorker
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaWorker extends Worker<List<UISearchResult>> {

  private final UIFile media;
  private final SearchScrapper<? extends Hyperlink> scrapper;
  private final WebList searchResultList;
  private final ImageListModel<UISearchResult> searchResultModel;
  private final Dimension searchListDim = new Dimension(45, 65);

  /**
   * Constructor arguments
   *
   * @param mr
   * @param media
   */
  public SearchMediaWorker(MovieRenamer mr, UIFile media) {
    super(mr);
    this.media = media;
    this.scrapper = mr.getScrapper().getScrapper();
    this.searchResultList = mr.getSearchResultList();
    this.searchResultModel = mr.getSearchResultListModel();
  }

  @Override
  @SuppressWarnings("unchecked")// FIXME
  public List<UISearchResult> executeInBackground() throws Exception {
    List<UISearchResult> results = new ArrayList<UISearchResult>();
    List<? extends Media> res;

    if (media != null && scrapper != null) {
      String search = media.getSearch();
      res = (List<? extends Media>) scrapper.search(search);// FIXME
      int count = res.size();

      for (int i = 0; i < count; i++) {
        if (isCancelled()) {
          return new ArrayList<UISearchResult>();
        }

        results.add(new UISearchResult(res.get(i), scrapper));
      }
    }

    return results;
  }

  @Override
  protected void workerDone() throws Exception {

    searchResultList.setModel(searchResultModel);

    List<UISearchResult> results = get();

    if (results == null) {
      mr.setSearchEnabled();
      return;
    }

    // Sort search results
    Sorter.SorterType type = UISettings.getInstance().coreInstance.getSearchSorter();
    UISettings.LOGGER.log(Level.INFO, String.format("Sort type %s, year %s , search %s", type.name(), media.getYear(), media.getSearch()));
    switch (type) {
      case ALPHABETIC:
      case LENGTH:
      case YEAR:
        Sorter.sort(results, type);
        break;
      case YEAR_ROUND:
      case ALPHA_YEAR:
        Sorter.sort(results, type, media.getYear());
        break;
      case SIMMETRICS:
        UISettings.LOGGER.log(Level.INFO, "Sort SIMMETRICS");
        Sorter.sort(results, media.getSearch());
        break;
      case LEVEN_YEAR:
        Sorter.sort(results, media.getYear(), media.getSearch());
        break;
      default:
      // Do nothing
      }

    searchResultModel.addAll(results);

    if (searchResultModel.isEmpty()) {
      JOptionPane.showMessageDialog(mr, LocaleUtils.i18nExt("noResult"), LocaleUtils.i18nExt("warning"), JOptionPane.ERROR_MESSAGE);// FIXME web dialog + i18n
    } else {
      if (UISettings.getInstance().isSelectFirstResult()) {
        searchResultList.setSelectedIndex(0);
      }
      WorkerManager.fetchImages(results, searchResultModel, searchListDim, "ui/nothumb.png");
    }

    mr.setSearchEnabled();
  }

  @Override
  protected void workerCanceled() {
    mr.setSearchEnabled();
  }
  
  @Override
  public String getParam() {
    return String.format("[%s]", media);
  }

  @Override
  public String getDisplayName() {
    return LocaleUtils.i18nExt("worker.searchMedia");
  }
}
