/*
 * Copyright (C) 2012-2013 Nicolas Magré
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

import fr.free.movierenamer.renamer.MoveFile;
import fr.free.movierenamer.ui.bean.UIEvent;
import fr.free.movierenamer.ui.swing.panel.TaskPanel;
import fr.free.movierenamer.ui.worker.AbstractWorker;
import fr.free.movierenamer.utils.FileUtils;
import java.io.File;
import java.util.List;

/**
 * Class RenamerWorker
 *
 * @author Nicolas Magré
 */
public class RenamerWorker extends AbstractWorker<Void, Integer> {

  private TaskPanel tpanel;
  private MoveFile moveThread;
  private String RenamedTitle;
  private File source;
  private String param;

  public RenamerWorker(File source, String RenamedTitle) {
    super();
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
    File destFile = new File(RenamedTitle + "." + ext);
    if (!destFile.isAbsolute()) {
      destFile = new File(mediaFolder, RenamedTitle + "." + ext);
    }

    moveThread = new MoveFile(source, destFile);
    moveThread.start();
    while (moveThread.isAlive()) {
      publish(moveThread.getProgress());
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
  protected void process(List<Integer> chunks) {
    tpanel.setProgress(chunks.get(chunks.size() - 1));
  }

  @Override
  public String getDisplayName() {
    return "Renamer";// FIXME i18n
  }
}
