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
package fr.free.movierenamer.ui.worker.impl;

import com.alee.laf.button.WebButton;
import com.alee.laf.list.DefaultListModel;
import com.alee.laf.list.WebList;
import com.alee.laf.text.WebTextField;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.scrapper.MediaScrapper;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.searchinfo.SearchResult;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.ui.bean.UISearchResult;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.worker.AbstractWorker;
import fr.free.movierenamer.ui.worker.WorkerManager;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.Sorter;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import javax.swing.JOptionPane;

/**
 * Class SearchMediaWorker
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaWorker extends AbstractWorker<List<UISearchResult>> {

  private final UIFile media;
  private final MediaScrapper<? extends SearchResult, ? extends MediaInfo> scrapper;
  private final WebList searchResultList;
  private final WebButton searchBtn;
  private final WebTextField searchField;
  private final DefaultListModel searchResultModel;
  private final Dimension searchListDim = new Dimension(45, 65);

  /**
   * Constructor arguments
   *
   * @param mr
   * @param media
   * @param scrapper
   * @param searchResultList
   * @param searchBtn
   * @param searchField
   * @param searchResultModel
   */
  public SearchMediaWorker(MovieRenamer mr, UIFile media, MediaScrapper<? extends SearchResult, ? extends MediaInfo> scrapper, WebList searchResultList, WebButton searchBtn, WebTextField searchField, DefaultListModel searchResultModel) {
    super(mr);
    this.media = media;
    this.scrapper = scrapper;
    this.searchResultList = searchResultList;
    this.searchBtn = searchBtn;
    this.searchField = searchField;
    this.searchResultModel = searchResultModel;
  }

  @Override
  public List<UISearchResult> executeInBackground() throws Exception {
    List<UISearchResult> results = new ArrayList<UISearchResult>();
    List<? extends Media> res;

    if (media != null && scrapper != null) {
      String search = media.getSearch();
      res = (List<? extends Media>) scrapper.search(search);
      int count = res.size();

      for (int i = 0; i < count; i++) {
        if (isCancelled()) {
          UISettings.LOGGER.log(Level.INFO, "SearchMediaWorker Cancelled");
          return new ArrayList<UISearchResult>();
        }

        results.add(new UISearchResult(res.get(i), scrapper));
      }
    }

    return results;
  }

  @Override
  protected void workerDone() throws Exception {
    // Remove loader
    searchResultModel.removeAllElements();

    try {
      List<UISearchResult> results = get();
      if (results == null) {
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

      searchResultModel.addElements(results);

      if (searchResultModel.isEmpty()) {
        JOptionPane.showMessageDialog(mr, LocaleUtils.i18n("noResult"), LocaleUtils.i18n("error"), JOptionPane.ERROR_MESSAGE);// FIXME web dialog
      } else {
        if (UISettings.getInstance().isSelectFirstResult()) {
          searchResultList.setSelectedIndex(0);
        }
        WorkerManager.fetchImages(this.getClass(), results, searchResultModel, searchListDim, "ui/nothumb.png");
      }

    } catch (CancellationException e) {
      // Worker canceled
      UISettings.LOGGER.log(Level.INFO, "SearchMediaWorker Cancelled");
    } finally {
      searchBtn.setEnabled(true);
      searchField.setEnabled(true);
    }
  }
}
