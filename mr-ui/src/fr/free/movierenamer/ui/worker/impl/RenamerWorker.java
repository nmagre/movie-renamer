/*
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

import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.renamer.MoveFile;
import fr.free.movierenamer.renamer.Nfo;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIEvent;
import fr.free.movierenamer.ui.swing.dialog.FileConflictDialog;
import fr.free.movierenamer.ui.swing.dialog.FileConflictDialog.Action;
import fr.free.movierenamer.ui.swing.panel.TaskPanel;
import fr.free.movierenamer.ui.swing.panel.info.InfoPanel;
import static fr.free.movierenamer.ui.utils.UIUtils.i18n;
import fr.free.movierenamer.ui.worker.ControlWorker;
import fr.free.movierenamer.utils.FileUtils;
import java.io.File;
import java.util.List;

/**
 * Class RenamerWorker
 *
 * @author Nicolas Magré
 */
public class RenamerWorker extends ControlWorker<Void> {

  private final TaskPanel tpanel;
  private MoveFile moveThread;
  private final String RenamedTitle;
  private final File source;
  private File destFile;
  private String param;

  public RenamerWorker(MovieRenamer mr, File source, String RenamedTitle) {
    super(mr);
    this.source = source;
    this.RenamedTitle = RenamedTitle;
    param = new File(RenamedTitle).getName();
    if (param.length() > 30) {
      param = param.substring(0, 27) + "...";
    }
    tpanel = new TaskPanel(param);
  }

  @Override
  protected Void executeInBackground() throws Exception {
    File mediaFolder = source.getParentFile();

    String ext = FileUtils.getExtension(source);
    destFile = new File(RenamedTitle + "." + ext);
    if (!destFile.isAbsolute()) {
      destFile = new File(mediaFolder, RenamedTitle + "." + ext);
    }

    MediaInfo info = mr.getMediaPanel().getPanel(InfoPanel.PanelType.INFO).getInfo();
    Nfo nfo = new Nfo(info, null);
    nfo.writeNFO();

    if (destFile.exists()) {
      publishPause(i18n.getLanguageKey("dialog.replacefile"));
    }

    if (source.equals(destFile)) {
      return null;
    }

    if (destFile.exists()) {
      publishPause("");
      if (isCancelled()) {
        return null;
      }
    }

    moveThread = new MoveFile(source, destFile);
    moveThread.start();
    while (moveThread.isAlive()) {
      setProgress(moveThread.getProgress());
      Thread.sleep(500);
    }
    moveThread.join();
    return null;
  }

  public TaskPanel getTaskPanel() {
    return tpanel;
  }

  @Override
  protected void workerDone() throws Exception {
    UIEvent.fireUIEvent(UIEvent.Event.RENAME_FILE_DONE, this);
  }

  @Override
  public String getParam() {
    return param;
  }

  @Override
  protected void processPause(List<String> chunks) {// TODO
    FileConflictDialog conflictdialog = new FileConflictDialog(mr, destFile, source);
    conflictdialog.setVisible(true);
    Action action = conflictdialog.getAction();
    switch (action) {
      case cancel:
        cancel(true);
        break;
      case replace:

        break;
      case skip:

        break;
    }
  }

  @Override
  protected void workerProgress(int progress) {
    tpanel.setProgress(progress);
  }

  @Override
  public String getDisplayName() {
    return "Renamer";// FIXME i18n
  }
}
