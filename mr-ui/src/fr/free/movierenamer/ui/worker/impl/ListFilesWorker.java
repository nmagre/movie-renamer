/*
 * movie-renamer
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

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.EventListModel;
import com.alee.laf.list.WebList;
import com.alee.laf.optionpane.WebOptionPane;
import fr.free.movierenamer.info.FileInfo;
import fr.free.movierenamer.namematcher.TvShowEpisodeNumMatcher;
import fr.free.movierenamer.namematcher.TvShowNameMatcher;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.IIconList;
import fr.free.movierenamer.ui.swing.IconListRenderer;
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.worker.AbstractWorker;
import fr.free.movierenamer.utils.FileUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.Sorter;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Class listFilesWorker ,get List of media files in files list
 *
 * @author Magré Nicolas
 */
public class ListFilesWorker extends AbstractWorker<List<UIFile>> {

  private final List<File> files;
  private final WebList list;
  private final EventList<UIFile> eventList;
  private final EventListModel<UIFile> model;
  private boolean subFolder;
  private final UISettings setting;
  private boolean paused = false;
  private final FilenameFilter folderFilter = new FilenameFilter() {
    @Override
    public boolean accept(File dir, String name) {
      return new File(dir.getAbsolutePath() + File.separator + name).isDirectory();
    }
  };

  /**
   * Constructor arguments
   *
   * @param files
   * @param mr
   * @param list
   * @param eventList
   * @param model
   */
  public ListFilesWorker(MovieRenamer mr, List<File> files, WebList list, EventList<UIFile> eventList, EventListModel<UIFile> model) {
    super(mr);
    this.files = files;
    this.list = list;
    this.eventList = eventList;
    this.model = model;
    setting = UISettings.getInstance();
    subFolder = setting.isScanSubfolder();
  }

  /**
   * Retreive all media files in a folder and subfolder
   *
   * @return List of UIfile
   * @throws InterruptedException
   */
  @Override
  public List<UIFile> executeInBackground() throws InterruptedException {

    List<UIFile> medias = new ArrayList<UIFile>();

    if (files == null || files.isEmpty()) {
      return medias;
    }

    if (!subFolder && subFolder(files)) {
      publish(LocaleUtils.i18n("scanSubFolder"));
      pause();
    }

    int count = files.size();
    for (int i = 0; i < count; i++) {
      if (isCancelled()) {
        UISettings.LOGGER.log(Level.INFO, "ListFilesWorker Cancelled");
        return new ArrayList<UIFile>();
      }

      if (files.get(i).isDirectory()) {
        addFiles(medias, files.get(i));
      } else {
        boolean addfiletoUI = !setting.isUseExtensionFilter() || FileUtils.checkFileExt(files.get(i).getName());
        if (addfiletoUI) {
          addUIfile(medias, files.get(i));
        }
      }
    }

    Sorter.sort(medias, Sorter.SorterType.ALPHABETIC);
    return medias;
  }

  private void pause() {
    paused = true;
    while (paused && !isCancelled()) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException ex) {
      }
    }
  }

  private void resume() {
    paused = false;
  }

  @Override
  public final void process(List<String> v) {
    super.process(v);
    resume();
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
        File[] subDir = file.listFiles(folderFilter);
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
    File[] listFiles = file.listFiles();
    if (listFiles == null) {
      UISettings.LOGGER.log(Level.SEVERE, String.format("Directory \"%s\" does not exist or is not a Directory", file.getName()));
      return;
    }

    for (int i = 0; i < listFiles.length; i++) {
      if (listFiles[i].isDirectory() && subFolder) {
        addFiles(medias, listFiles[i]);
      } else if (!setting.isUseExtensionFilter() || FileUtils.checkFileExt(listFiles[i].getName())) {
        addUIfile(medias, listFiles[i]);
      }
    }
  }

  private void addUIfile(List<UIFile> medias, File file) {
    FileInfo fileInfo = new FileInfo(file);
    String groupName = fileInfo.getFile().getName().substring(0, 1);
    switch (fileInfo.getType()) {
      case MOVIE:
        break;
      case TVSHOW:
        TvShowNameMatcher nameMatcher = new TvShowNameMatcher(file, new ArrayList<String>());
        TvShowEpisodeNumMatcher epMatch = new TvShowEpisodeNumMatcher(file);
        groupName = nameMatcher.getName() + " " + LocaleUtils.i18nExt("season") + " " + epMatch.matchEpisode().seasonL0();
        break;
    }
    medias.add(new UIFile(new FileInfo(file), groupName));
  }

  @Override
  protected void workerDone() throws Exception {
    List<UIFile> medias = get();

    list.setCellRenderer(new IconListRenderer<IIconList>(false));

    eventList.addAll(medias);
    list.setModel(model);

    if (eventList.isEmpty()) {
      WebOptionPane.showMessageDialog(mr, LocaleUtils.i18n("noMediaFound"), LocaleUtils.i18n("warning"), WebOptionPane.WARNING_MESSAGE);// FIXME i18n
    } else if (UISettings.getInstance().isSelectFirstMedia()) {
      int index = 0;
      if(model.getElementAt(index) instanceof SeparatorList.Separator) {
        index++;
      }
      list.setSelectedIndex(index);
    }
  }
}