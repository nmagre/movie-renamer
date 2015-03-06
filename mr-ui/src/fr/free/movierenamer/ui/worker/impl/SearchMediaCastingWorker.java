/*
 * Movie Renamer
 * Copyright (C) 2012-2014 Nicolas Magré
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

import com.alee.laf.list.WebList;
import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIPersonImage;
import fr.free.movierenamer.ui.swing.ImageListModel;
import fr.free.movierenamer.ui.worker.Worker;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

/**
 * Class SearchMediaCastingWorker
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaCastingWorker extends Worker<List<UIPersonImage>> {// Not used

  private final MediaInfo info;
  private final WebList castingList;
  private final Dimension actorListDim = new Dimension(30, 53);
  private final ImageListModel<UIPersonImage> castingModel;

  /**
   * Constructor arguments
   *
   * @param mr
   * @param info
   * @param castingList
   * @param castingModel
   */
  public SearchMediaCastingWorker(MovieRenamer mr, MediaInfo info, WebList castingList, ImageListModel<UIPersonImage> castingModel) {
    super(mr);
    this.info = info;
    this.castingList = castingList;
    this.castingModel = castingModel;
  }

  @Override
  public List<UIPersonImage> executeInBackground() throws Exception {
    List<UIPersonImage> persons = new ArrayList<>();
    List<CastingInfo> infos;

//    if (info != null) {
//      infos = info.getCasting();
//      int count = infos.size();
//      for (int i = 0; i < count; i++) {
//        if (isCancelled()) {
//          return new ArrayList<>();
//        }
//
//        persons.add(new UIPersonImage(infos.get(i)));
//      }
//    }

    return persons;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void workerDone() throws Exception {
    List<UIPersonImage> infos = get();
    castingList.setModel(castingModel);
    if (infos != null) {
      castingModel.addAll(infos);
      //List<UIPersonImage> tmp = new ArrayList<>(infos);
      //WorkerManager.fetchImages(tmp, castingModel, ImageUtils.UNKNOWN, UISettings.getInstance().isShowActorImage());
    }
  }

  @Override
  public String getDisplayName() {
    return ("worker.searchCasting");// FIXME i18n
  }

  @Override
  public WorkerId getWorkerId() {
    return WorkerId.SEARCH_CASTING;
  }
}
