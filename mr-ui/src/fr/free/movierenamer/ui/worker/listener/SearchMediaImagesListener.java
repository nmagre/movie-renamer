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
import fr.free.movierenamer.ui.panel.LoadingDialog.LoadingDialogPos;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.res.IconListRenderer;
import fr.free.movierenamer.ui.res.UIMediaImage;
import fr.free.movierenamer.ui.worker.SearchMediaImagesWorker;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * Class MediaImagesListener
 *
 * @author Nicolas Magré
 */
public class SearchMediaImagesListener extends AbstractListener<List<UIMediaImage>> {

  private final WebList thumbnailsList;
  private final WebList fanartsList;

  public SearchMediaImagesListener(SearchMediaImagesWorker worker, MovieRenamer mr, WebList thumbnailsList, WebList fanartsList) {
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

    List<UIMediaImage> images = worker.get();
    for (UIMediaImage image : images) {
      switch (image.getType()) {
        case thumb:
          thumbnailsListModel.addElement(image);
          break;
        case fanart:
          fanartsListModel.addElement(image);
          break;
        case banner:
          // TODO
          break;
        case cdart:
          // TODO
          break;
        case logo:
          // TODO
          break;
        case clearart:
          // TODO
          break;
      }
    }

    thumbnailsList.setCellRenderer(new IconListRenderer<UIMediaImage>());
    fanartsList.setCellRenderer(new IconListRenderer<UIMediaImage>());

    thumbnailsList.setModel(thumbnailsListModel);
    fanartsList.setModel(fanartsListModel);
  }
}
