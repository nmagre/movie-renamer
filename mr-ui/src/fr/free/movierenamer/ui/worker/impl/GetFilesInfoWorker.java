/*
 * Movie Renamer
 * Copyright (C) 2014 Nicolas Magré
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
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.ui.worker.AbstractWorker;
import java.util.List;

/**
 * Class GetFilesInfoWorker
 *
 * @author Nicolas Magré
 */
public class GetFilesInfoWorker extends AbstractWorker<Void, Void> {
  
  private final List<UIFile> files;
  
  public GetFilesInfoWorker(List<UIFile> files) {
    this.files = files;
  }
  
  @Override
  protected Void executeInBackground() throws Exception {
    
    int total = files.size();
    int count = 0;
    for (UIFile file : files) {
      if (file.getFileInfo() == null) {
        file.setFileInfo(new FileInfo(file.getFile()));
      }
      
      count++;
      setProgress((count * 100) / total);
    }
    
    return null;
  }
  
  @Override
  protected void workerDone() throws Exception {
    
  }
  
  @Override
  public String getDisplayName() {
    return "Get file info";// FIXME i18n
  }
  
  @Override
  public WorkerId getWorkerId() {
    return WorkerId.GET_FILE_INFO;
  }
  
}
