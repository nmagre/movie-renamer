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

import com.alee.laf.list.DefaultListModel;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.panel.IMediaPanel;
import fr.free.movierenamer.ui.res.UILoader;
import fr.free.movierenamer.ui.res.UIMediaImage;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.ui.worker.ImageWorker;
import fr.free.movierenamer.ui.worker.SearchMediaImagesWorker;
import java.awt.Dimension;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Class SearchMediaImagesListener
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaImagesListener extends AbstractListener<List<UIMediaImage>> {

  private final  IMediaPanel ipanel;
  private final Dimension thumbDim = new Dimension(160, 200);
  private final Dimension fanartDim = new Dimension(200, 160);

  public SearchMediaImagesListener(SearchMediaImagesWorker worker, MovieRenamer mr, IMediaPanel ipanel) {
    super(mr, worker);
    this.ipanel = ipanel;
  }

  @Override
  protected void done() throws Exception {
    List<URI> fanartUrl = new ArrayList<URI>();
    List<URI> thumbUrl = new ArrayList<URI>();
    final DefaultListModel thumbnailsListModel = ipanel.getThumbnailsModel();
    final DefaultListModel fanartsListModel = ipanel.getFanartsModel();

    List<UIMediaImage> images = worker.get();
    for (UIMediaImage image : images) {
      switch (image.getType()) {
        case thumb:
          thumbUrl.add(image.getUrl().toURI());
          image.setIcon(UIUtils.getAnimatedLoader(ipanel.getThumbnailsList()));
          thumbnailsListModel.addElement(image);
          break;
        case fanart:
          fanartUrl.add(image.getUrl().toURI());
          image.setIcon(UIUtils.getAnimatedLoader(ipanel.getFanartsList()));
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

    ImageWorker<UIMediaImage> fanartWorker = new ImageWorker<UIMediaImage>(fanartUrl, fanartsListModel, fanartDim, null);
    ImageWorker<UIMediaImage> thumbWorker = new ImageWorker<UIMediaImage>(thumbUrl, thumbnailsListModel, thumbDim, null);

    fanartWorker.execute();
    thumbWorker.execute();
  }
}
