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
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.scrapper.MediaScrapper;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.ui.LoadingDialog.LoadingDialogPos;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.res.IconListRenderer;
import fr.free.movierenamer.ui.res.UIMediaImage;
import fr.free.movierenamer.ui.res.UISearchResult;
import java.beans.PropertyChangeSupport;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * Class SearchMediaImagesWorker
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaImagesWorker extends AbstractWorker {
  private final UISearchResult searchResult;
  private final MediaScrapper<Media, MediaInfo> scrapper;
  private final WebList thumbnailsList;
  private final WebList fanartsList;

  /**
   * Constructor arguments
   * 
   * @param parent
   * @param fanartList
   * @param thumbnailsList
   * @param media
   */
  public SearchMediaImagesWorker(PropertyChangeSupport errorSupport, MovieRenamer parent, WebList thumbnailsList, WebList fanartsList, UISearchResult searchResult) {
    super(errorSupport, parent);
    this.searchResult = searchResult;
    this.scrapper = (searchResult != null) ? (MediaScrapper<Media, MediaInfo>) searchResult.getScrapper() : null;
    this.thumbnailsList = thumbnailsList;
    this.fanartsList = fanartsList;
  }

  @Override
  protected LoadingDialogPos getLoadingDialogPos() {
    return LoadingDialogPos.images;
  }

  @Override
  public void executeInBackground() throws Exception {
    DefaultListModel thumbnailsListModel = new DefaultListModel();
    DefaultListModel fanartsListModel = new DefaultListModel();
    if (searchResult != null && scrapper != null) {
      Media media = searchResult.getSearchResult();
      List<ImageInfo> infos = scrapper.getImages(media);
      int count = infos.size();
      for (int i = 0; i < count; i++) {
        if (isCancelled()) {
          return;
        }
        ImageInfo info = infos.get(i);
        switch (info.getCategory()) {
        case thumb:
          thumbnailsListModel.addElement(new UIMediaImage(info));
          break;
        case fanart:
          fanartsListModel.addElement(new UIMediaImage(info));
          break;
        }
        double progress = (i + 1) / (double) count;
        updateLoadingValue((int) (progress * 100));
      }
    }

    thumbnailsList.setCellRenderer(new IconListRenderer<UISearchResult>());
    thumbnailsList.setModel(thumbnailsListModel);
    fanartsList.setCellRenderer(new IconListRenderer<UISearchResult>());
    fanartsList.setModel(fanartsListModel);
  }
}
