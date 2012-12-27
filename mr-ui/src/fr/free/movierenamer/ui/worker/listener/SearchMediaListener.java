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
package fr.free.movierenamer.ui.worker.listener;

import com.alee.laf.button.WebButton;
import com.alee.laf.list.DefaultListModel;
import com.alee.laf.list.WebList;
import com.alee.laf.text.WebTextField;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.res.UIFile;
import fr.free.movierenamer.ui.res.UISearchResult;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.ui.worker.SearchMediaWorker;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.Sorter;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JOptionPane;

/**
 * Class SearchMediaListener
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaListener extends AbstractListener<List<UISearchResult>> {

  private final WebList searchResultList;
  private final WebButton searchBtn;
  private final WebTextField searchField;
  private final UIFile media;
  private final DefaultListModel searchResultModel;

  public SearchMediaListener(SearchMediaWorker worker, MovieRenamer mr, WebList searchResultList, UIFile media, WebButton searchBtn, WebTextField searchField, DefaultListModel searchResultModel) {
    super(mr, worker);
    this.searchResultList = searchResultList;
    this.media = media;
    this.searchBtn = searchBtn;
    this.searchField = searchField;
    this.searchResultModel = searchResultModel;
  }

  @Override
  protected void done() throws Exception {
    // Remove loader
    searchResultModel.removeAllElements();
    try {
      List<UISearchResult> results = worker.get();

      // Sort search results
      Sorter.SorterType type = UISettings.getInstance().coreInstance.getSearchSorter();
      UISettings.LOGGER.log(Level.INFO, "Sort type {0} , year {1} , search {2}", new Object[]{type .name(), media.getYear(), media.getSearch()});
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
        case LEVENSTHEIN:
          Sorter.sort(results, media.getSearch());
          break;
        case LEVEN_YEAR:
          Sorter.sort(results, media.getYear(), media.getSearch());
          break;
      }

      searchResultList.setCellRenderer(UISettings.getInstance().isShowThumb() ? UIUtils.iconListRenderer : new DefaultListCellRenderer());
      searchResultModel.addElements(results);

      if (searchResultModel.isEmpty()) {
        JOptionPane.showMessageDialog(mr, LocaleUtils.i18n("noResult"), LocaleUtils.i18n("error"), JOptionPane.ERROR_MESSAGE);
      } else if (UISettings.getInstance().isSelectFirstResult()) {
        searchResultList.setSelectedIndex(0);
      }
    } catch (CancellationException e) {
      // Worker canceled
    } finally {
      searchBtn.setEnabled(true);
      searchField.setEnabled(true);
    }
  }
}
