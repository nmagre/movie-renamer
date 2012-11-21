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

import com.alee.laf.list.WebList;
import fr.free.movierenamer.searchinfo.Subtitle;
import fr.free.movierenamer.ui.LoadingDialog.LoadingDialogPos;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.worker.SearchMediaSubtitlesWorker;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;

/**
 * Class MediaSubtitlesListener
 *
 * @author Nicolas Magré
 */
public class MediaSubtitlesListener extends AbstractListener<List<Subtitle>> {

  private final WebList subtitlesList;

  public MediaSubtitlesListener(SearchMediaSubtitlesWorker worker, MovieRenamer mr, WebList subtitlesList) {
    super(mr, worker);
    this.subtitlesList = subtitlesList;
  }

  @Override
  protected LoadingDialogPos getLoadingDialogPos() {
    return LoadingDialogPos.subtitles;
  }

  @Override
  protected void done() throws Exception {
    DefaultListModel subtitlesListModel = new DefaultListModel();
    List<Subtitle> subtitles = worker.get();

    for (Subtitle subtitle : subtitles) {
      subtitlesListModel.addElement(subtitle);
    }

    // subtitlesList.setCellRenderer(new IconListRenderer<UISearchResult>());
    subtitlesList.setCellRenderer(new DefaultListCellRenderer());
    subtitlesList.setModel(subtitlesListModel);
  }
}
