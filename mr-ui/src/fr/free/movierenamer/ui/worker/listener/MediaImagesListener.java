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
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.ui.LoadingDialog.LoadingDialogPos;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.res.IconListRenderer;
import fr.free.movierenamer.ui.res.UIMediaImage;
import fr.free.movierenamer.ui.res.UISearchResult;
import fr.free.movierenamer.ui.worker.SearchMediaImagesWorker;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * Class MediaImagesListener
 *
 * @author Nicolas Magré
 */
public class MediaImagesListener extends WorkerListener<List<ImageInfo>> {

  private final WebList thumbnailsList;
  private final WebList fanartsList;

  public MediaImagesListener(SearchMediaImagesWorker worker, MovieRenamer mr, WebList thumbnailsList, WebList fanartsList) {
    super(mr, worker);
    this.thumbnailsList = thumbnailsList;
    this.fanartsList = fanartsList;
  }

  @Override
  protected LoadingDialogPos getLoadingDialogPos() {
    return LoadingDialogPos.images;
  }

  @Override
  protected void done() throws Exception {
    DefaultListModel thumbnailsListModel = new DefaultListModel();
    DefaultListModel fanartsListModel = new DefaultListModel();

    List<ImageInfo> infos = worker.get();
    for (ImageInfo info : infos) {
      switch (info.getCategory()) {
        case thumb:
          thumbnailsListModel.addElement(new UIMediaImage(info));
          break;
        case fanart:
          fanartsListModel.addElement(new UIMediaImage(info));
          break;
      }
    }

    thumbnailsList.setCellRenderer(new IconListRenderer<UISearchResult>());
    thumbnailsList.setModel(thumbnailsListModel);
    fanartsList.setCellRenderer(new IconListRenderer<UISearchResult>());
    fanartsList.setModel(fanartsListModel);
  }
}
