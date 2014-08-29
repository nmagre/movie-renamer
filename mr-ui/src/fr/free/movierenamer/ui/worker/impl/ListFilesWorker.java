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

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SeparatorList;
import com.alee.laf.list.WebList;
import com.alee.laf.optionpane.WebOptionPane;
import fr.free.movierenamer.info.FileInfo;
import fr.free.movierenamer.namematcher.NameMatcher;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.ui.settings.UISettings;
import static fr.free.movierenamer.ui.utils.UIUtils.i18n;
import fr.free.movierenamer.ui.worker.ControlWorker;
import fr.free.movierenamer.utils.ClassUtils;
import fr.free.movierenamer.utils.FileUtils;
import fr.free.movierenamer.utils.Sorter;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Class listFilesWorker
 *
 * @author Nicolas Magré
 */
public class ListFilesWorker extends ControlWorker<List<UIFile>, String> {

  private final List<File> files;
  private final EventList<UIFile> eventList;
  private boolean subFolder;
  private final UISettings setting;

  /**
   * Constructor arguments
   *
   * @param files
   * @param mr
   * @param eventList
   */
  public ListFilesWorker(MovieRenamer mr, List<File> files, EventList<UIFile> eventList) {
    super(mr);
    this.files = files;
    this.eventList = eventList;
    setting = UISettings.getInstance();
    subFolder = setting.getScanSubfolder().equals(UISettings.Subfolder.BROWSE);
  }

  /**
   * Retrieve all media files in a folder and subfolder
   *
   * @return List of UIfile
   * @throws InterruptedException
   */
  @Override
  public List<UIFile> executeInBackground() throws InterruptedException {

    List<UIFile> medias = new ArrayList<>();

    if (files == null || files.isEmpty()) {
      return medias;
    }

    if (setting.getScanSubfolder().equals(UISettings.Subfolder.ASK) && subFolder(files)) {
      publishPause("dialog.scanSubFolder");
    }

    try {
      for (File file : files) {
        if (isCancelled()) {
          return new ArrayList<>();
        }

        if (file.isDirectory()) {
          addFiles(medias, file);
        } else {
          boolean addfiletoUI = !setting.isUseExtensionFilter() || FileUtils.checkFileExt(file);
          if (addfiletoUI) {
            addUIfile(medias, file);
          }
        }
      }
    } catch (Exception ex) {
      UISettings.LOGGER.log(Level.SEVERE, ClassUtils.getStackTrace(ex));
    }

    Sorter.sort(medias, Sorter.SorterType.ALPHABETIC);
    return medias;
  }

  /**
   * Check if one directory contains a subdirectory
   *
   * @param files
   * @return true if there is at least one subdirectory, otherwise false
   */
  private boolean subFolder(List<File> files) {
    for (File file : files) {
      if (file.isDirectory()) {
        File[] subDir = file.listFiles(new FileUtils.FolderFilter());
        if (subDir != null && subDir.length > 0) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Scan recursively folders and add media to a list
   *
   * @param medias List of movies
   * @param file File to add or directory to scan
   */
  private void addFiles(List<UIFile> medias, File file) {

    if (isCancelled()) {
      return;
    }

    FileFilter fileFilter = setting.isUseExtensionFilter() ? new FileUtils.ExtensionFileFilter(true, setting.coreInstance.getfileExtension())
            : new FileUtils.FileAndFolderFilter();
    File[] listFiles = file.listFiles(fileFilter);

    if (listFiles == null) {
      UISettings.LOGGER.log(Level.SEVERE, String.format("Directory \"%s\" does not exist or is not a Directory", file.getName()));
      return;
    }

    for (File listFile : listFiles) {
      if (isCancelled()) {
        return;
      }
      
      if (listFile.isDirectory() && subFolder) {
        addFiles(medias, listFile);
      } else if (!setting.isUseExtensionFilter() || FileUtils.checkFileExt(listFile)) {
        addUIfile(medias, listFile);
      }
    }
  }

  private void addUIfile(List<UIFile> medias, File file) {
    String groupName = "";
    FileInfo.MediaType mtype = FileInfo.getMediaType(file);
    switch (mtype) {
      case MOVIE:
        groupName = file.getName().trim().substring(0, 1);
        break;
      case TVSHOW:// TODO
        groupName = NameMatcher.extractName(file.getName());
        break;
    }

    medias.add(new UIFile(file, groupName, mtype));
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void workerDone() throws Exception {
    List<UIFile> medias = get();

    WebList list = mr.getMediaList();
    list.setModel(mr.getMediaFileListModel());
    eventList.addAll(medias);

    if (eventList.isEmpty()) {
      WebOptionPane.showMessageDialog(mr, ("warning.noMediaFound"), ("warning"), WebOptionPane.WARNING_MESSAGE);// FIXME i18n + tooltip
      return;
    }

    mr.setMediaCount(medias.size());
    
    if (UISettings.getInstance().isSelectFirstMedia()) {
      int index = 0;
      if (list.getValueAt(index) instanceof SeparatorList.Separator) {
        index++;
      }
      list.setSelectedIndex(index);
    }

    mr.setClearMediaFileListBtnEnabled();
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void workerCanceled() {
    eventList.clear();
  }

  @Override
  public final void processPause(String v) {
    int res = WebOptionPane.showConfirmDialog(mr, i18n.getLanguage(v, false), i18n.getLanguage("dialog.question", false), WebOptionPane.YES_NO_OPTION, WebOptionPane.QUESTION_MESSAGE);
    
    if (res == WebOptionPane.YES_OPTION) {
      subFolder = true;
    }
  }

  @Override
  public String getParam() {
    return String.format("%s", files);
  }

  @Override
  public String getDisplayName() {
    return ("worker.listFile");// FIXME i18n
  }
}
