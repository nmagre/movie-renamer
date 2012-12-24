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
import fr.free.movierenamer.ui.panel.MediaPanel;
import fr.free.movierenamer.ui.res.UIPersonImage;
import fr.free.movierenamer.ui.worker.ImageWorker;
import fr.free.movierenamer.ui.worker.SearchMediaCastingWorker;
import java.awt.Dimension;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Class SearchMediaCastingListener
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaCastingListener extends AbstractListener<List<UIPersonImage>> {

  private final MediaPanel ipanel;
  private final Dimension actorListDim = new Dimension(30, 53);

  public SearchMediaCastingListener(SearchMediaCastingWorker worker, MovieRenamer mr, MediaPanel ipanel) {
    super(mr, worker);
    this.ipanel = ipanel;
  }

  @Override
  protected void done() throws Exception {
    List<URI> imagesUri = new ArrayList<URI>();
    List<UIPersonImage> infos = worker.get();
    final DefaultListModel castingModel = ipanel.getCastingModel();

    for(UIPersonImage info : infos){
      imagesUri.add(info.getUri());
    }

    castingModel.addElements(infos);

    ImageWorker<UIPersonImage> imagesWorker = new ImageWorker<UIPersonImage>(imagesUri, castingModel, actorListDim, "ui/unknown.png");
    imagesWorker.execute();
    mr.addImageWorker(imagesWorker);
  }
}
