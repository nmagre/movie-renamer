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
package fr.free.movierenamer.ui.worker;

import com.alee.laf.list.WebList;
import fr.free.movierenamer.scrapper.SubtitleScrapper;
import fr.free.movierenamer.searchinfo.Subtitle;
import fr.free.movierenamer.ui.LoadingDialog.LoadingDialogPos;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.res.UIFile;
import java.beans.PropertyChangeSupport;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 * Class SearchMediaImagesWorker
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaSubtitlesWorker extends AbstractWorker {
  private final UIFile media;
  private final SubtitleScrapper scrapper;
  private final JList subtitlesList;

  /**
   * Constructor arguments
   * 
   * @param errorSupport
   * @param parent
   * @param media
   * @param subtitlesList
   * @param scrapper
   */
  public SearchMediaSubtitlesWorker(PropertyChangeSupport errorSupport, MovieRenamer parent, UIFile media, WebList subtitlesList, SubtitleScrapper scrapper) {
    super(errorSupport, parent);
    this.media = media;
    this.subtitlesList = subtitlesList;
    this.scrapper = scrapper;
  }

  @Override
  protected LoadingDialogPos getLoadingDialogPos() {
    return LoadingDialogPos.subtitles;
  }

  @Override
  public void executeInBackground() throws Exception {
    DefaultListModel subtitlesListModel = new DefaultListModel();
    if (media != null && scrapper != null) {
      String search = media.getSearch();
      List<Subtitle> results = scrapper.search(search);
      int count = results.size();
      for (int i = 0; i < count; i++) {
        if (isCancelled()) {
          return;
        }
        subtitlesListModel.addElement(results.get(i));
        double progress = (i + 1) / (double) count;
        updateLoadingValue((int) (progress * 100));
      }

      // subtitlesList.setCellRenderer(new IconListRenderer<UISearchResult>());
      subtitlesList.setCellRenderer(new DefaultListCellRenderer());
      subtitlesList.setModel(subtitlesListModel);
    }
  }

}
