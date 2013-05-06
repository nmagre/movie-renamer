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

import com.alee.laf.list.DefaultListModel;
import com.alee.laf.list.WebList;
import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIPersonImage;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.worker.AbstractWorker;
import fr.free.movierenamer.ui.worker.WorkerManager;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Class SearchMediaCastingWorker
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaCastingWorker extends AbstractWorker<List<UIPersonImage>> {

  private final MediaInfo info;
  private final WebList castingList;
  private final Dimension actorListDim = new Dimension(30, 53);
  private final DefaultListModel castingModel;

  /**
   * Constructor arguments
   *
   * @param mr
   * @param info
   * @param castingList
   * @param castingModel
   */
  public SearchMediaCastingWorker(MovieRenamer mr, MediaInfo info, WebList castingList, DefaultListModel castingModel) {
    super(mr);
    this.info = info;
    this.castingList = castingList;
    this.castingModel = castingModel;
  }

  @Override
  public List<UIPersonImage> executeInBackground() throws Exception {
    List<UIPersonImage> persons = new ArrayList<UIPersonImage>();
    List<CastingInfo> infos;

    if (info != null) {
      infos = info.getCast();
      int count = infos.size();
      for (int i = 0; i < count; i++) {
        if (isCancelled()) {
          UISettings.LOGGER.log(Level.INFO, "SearchMediaCastingWorker Cancelled");
          return new ArrayList<UIPersonImage>();
        }

        persons.add(new UIPersonImage(infos.get(i)));
      }
    }

    return persons;
  }

  @Override
  protected void workerDone() throws Exception {
    List<UIPersonImage> infos = get();
    castingList.setModel(castingModel);
    if (infos != null) {
      castingModel.addElements(infos);
      WorkerManager.fetchImages(this.getClass(), infos, castingModel, actorListDim, "ui/unknown.png");
    }
  }
}
