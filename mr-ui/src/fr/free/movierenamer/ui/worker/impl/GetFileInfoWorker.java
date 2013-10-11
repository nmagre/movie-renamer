/*
 * Movie Renamer
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

import fr.free.movierenamer.info.FileInfo;
import fr.free.movierenamer.renamer.NameCleaner;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.ui.worker.Worker;
import fr.free.movierenamer.utils.LocaleUtils;

/**
 * Class GetFileInfoWorker
 *
 * @author Nicolas Magré
 */
public class GetFileInfoWorker extends Worker<FileInfo> {

  private final UIFile file;

  public GetFileInfoWorker(MovieRenamer mr, UIFile file) {
    super(mr);
    this.file = file;
  }

  @Override
  protected FileInfo executeInBackground() throws Exception {
    file.setSearch(NameCleaner.extractName(file.getFile().getName(), false));
    return new FileInfo(file.getFile());
  }

  @Override
  protected void workerDone() throws Exception {
    FileInfo fileInfo = get();
    file.setFileInfo(fileInfo);
    mr.getMediaPanel().setFileInfo(fileInfo);
  }

  @Override
  public String getParam() {
    return String.format("%s", file);
  }

  @Override
  public String getDisplayName() {
    return ("worker.fileinfo");// FIXME i18n
  }
}
