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
import com.alee.laf.list.WebList;
import com.alee.laf.text.WebTextField;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.scrapper.MediaScrapper;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.searchinfo.SearchResult;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.panel.LoadingDialog.LoadingDialogPos;
import fr.free.movierenamer.ui.res.IconListRenderer;
import fr.free.movierenamer.ui.res.UISearchResult;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.worker.SearchMediaWorker;
import fr.free.movierenamer.utils.LocaleUtils;
import java.util.List;
import java.util.concurrent.CancellationException;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 * Class SearchMediaListener
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaListener extends AbstractListener<List<? extends SearchResult>> {

  private final WebList searchResultList;
  private final MediaScrapper<? extends SearchResult, ? extends MediaInfo> scrapper;
  private final WebButton searchBtn;
  private final WebTextField searchField;

  public SearchMediaListener(SearchMediaWorker worker, MovieRenamer mr, WebList searchResultList, MediaScrapper<? extends SearchResult, ? extends MediaInfo> scrapper, WebButton searchBtn, WebTextField searchField) {
    super(mr, worker);
    this.searchResultList = searchResultList;
    this.scrapper = scrapper;
    this.searchBtn = searchBtn;
    this.searchField = searchField;
  }

  @Override
  protected LoadingDialogPos getLoadingDialogPos() {
    return LoadingDialogPos.search;
  }

  @Override
  protected void done() throws Exception {
    DefaultListModel searchResModel = new DefaultListModel();
    try {
      List<? extends SearchResult> results = worker.get();

        // // Sort result by similarity and year //FIXME Sort result by similarity and year
        // if (count.size() > 1 && setting.sortBySimiYear) {
        // Levenshtein.sortByLevenshteinDistanceYear(currentMedia.getSearch(), currentMedia.getYear(), results);
        // }

        // searchLbl.setText(LocaleUtils.i18n("search") + " : " + count);//FIXME update labels

      for(SearchResult result : results) {
        searchResModel.addElement(new UISearchResult((Media) result, scrapper));
      }

      if (UISettings.getInstance().isShowThumb()) {
        searchResultList.setCellRenderer(new IconListRenderer<UISearchResult>());
      } else {
        searchResultList.setCellRenderer(new DefaultListCellRenderer());
      }
      searchResultList.setModel(searchResModel);

      if (searchResModel.isEmpty()) {
        JOptionPane.showMessageDialog(mr, LocaleUtils.i18n("noResult"), LocaleUtils.i18n("error"), JOptionPane.ERROR_MESSAGE);
      } else if (UISettings.getInstance().isSelectFirstResult()) {
        searchResultList.setSelectedIndex(0);
      }
      searchBtn.setEnabled(true);
      searchField.setEnabled(true);
    }
    catch(CancellationException e) {
      // Worker canceled
    }
  }
}
