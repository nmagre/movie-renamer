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

import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.panel.IMediaPanel;
import fr.free.movierenamer.ui.panel.LoadingDialog.LoadingDialogPos;
import fr.free.movierenamer.ui.worker.SearchMediaInfoWorker;

/**
 * Class SearchMediaInfoListener
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaInfoListener extends AbstractListener<MediaInfo> {

  private final IMediaPanel mediaPanel;

  public SearchMediaInfoListener(SearchMediaInfoWorker worker, MovieRenamer mr, IMediaPanel mediaPanel) {
    super(mr, worker);
    this.mediaPanel = mediaPanel;
  }

  @Override
  protected LoadingDialogPos getLoadingDialogPos() {
    return LoadingDialogPos.inf;
  }

  @Override
  protected void done() throws Exception {
    MediaInfo info = worker.get();
    if (info != null) {
      mediaPanel.setMediaInfo(info);
      mr.updateRenamedTitle();
    }
  }
}
