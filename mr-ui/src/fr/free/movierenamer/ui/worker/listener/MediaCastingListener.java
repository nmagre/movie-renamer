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
import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.ui.LoadingDialog.LoadingDialogPos;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.res.IconListRenderer;
import fr.free.movierenamer.ui.res.UIPersonImage;
import fr.free.movierenamer.ui.worker.SearchMediaCastingWorker;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultListModel;

/**
 * Class MediaCastingListener
 *
 * @author Nicolas Magré
 */
public class MediaCastingListener extends WorkerListener<List<CastingInfo>> {

  private final WebList castingList;

  public MediaCastingListener(SearchMediaCastingWorker worker, MovieRenamer mr, WebList castingList) {
    super(mr, worker);
    this.castingList = castingList;
  }

  @Override
  protected LoadingDialogPos getLoadingDialogPos() {
    return LoadingDialogPos.casting;
  }

  @Override
  protected void done() throws InterruptedException, ExecutionException {
    DefaultListModel personsListModel = new DefaultListModel();
    List<CastingInfo> infos = worker.get();

    for (CastingInfo info : infos) {
      personsListModel.addElement(new UIPersonImage(info));
    }

    castingList.setCellRenderer(new IconListRenderer<UIPersonImage>());
    castingList.setModel(personsListModel);
  }
}
