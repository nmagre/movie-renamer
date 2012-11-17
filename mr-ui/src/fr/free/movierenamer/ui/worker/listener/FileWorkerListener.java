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
import com.alee.laf.list.WebList;
import com.alee.laf.optionpane.WebOptionPane;
import fr.free.movierenamer.ui.LoadingDialog.LoadingDialogPos;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.res.IconListRenderer;
import fr.free.movierenamer.ui.res.UIFile;
import fr.free.movierenamer.ui.settings.Settings;
import fr.free.movierenamer.ui.worker.ListFilesWorker;
import fr.free.movierenamer.utils.LocaleUtils;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Class FileWorkerListener
 *
 * @author Nicolas Magré
 */
public class FileWorkerListener extends WorkerListener<List<UIFile>> {

  private final WebList mediaList;

  public FileWorkerListener(ListFilesWorker worker, MovieRenamer mr, WebList mediaList) {
    super(mr, worker);
    this.mediaList = mediaList;
  }

  @Override
  protected LoadingDialogPos getLoadingDialogPos() {
    return LoadingDialogPos.files;
  }

  @Override
  protected void done() throws InterruptedException, ExecutionException {

    List<UIFile> medias = worker.get();
    DefaultListModel mediaFileNameModel = new DefaultListModel();

    updateLoadingValue(100);

    for (UIFile media : medias) {
      mediaFileNameModel.addElement(media);
    }

    mediaList.setCellRenderer(new IconListRenderer<UIFile>());
    mediaList.setModel(mediaFileNameModel);

    if (mediaFileNameModel.isEmpty()) {
      WebOptionPane.showMessageDialog(mr, LocaleUtils.i18n("noMediaFound"), LocaleUtils.i18n("error"), WebOptionPane.ERROR_MESSAGE);
    } else if (Settings.getInstance().selectFrstMedia) {
      mediaList.setSelectedIndex(0);
    }
  }
}
