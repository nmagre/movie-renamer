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
import fr.free.movierenamer.ui.IMediaPanel;
import fr.free.movierenamer.ui.LoadingDialog.LoadingDialogPos;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.worker.SearchMediaInfoWorker;

/**
 * Class MediaInfoListener
 *
 * @author Nicolas Magré
 */
public class MediaInfoListener extends AbstractListener<MediaInfo> {

  private final IMediaPanel mediaPanel;

  public MediaInfoListener(SearchMediaInfoWorker worker, MovieRenamer mr, IMediaPanel mediaPanel) {
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
