/*
 * movie-renamer
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
package fr.free.movierenamer.ui.worker;

import fr.free.movierenamer.namematcher.TvShowNameMatcher;
import fr.free.movierenamer.ui.res.UIFile;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.utils.MediaRenamed;
import fr.free.movierenamer.utils.FileUtils;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class listFilesWorker ,get List of media files in files list
 *
 * @author Magré Nicolas
 */
public class ListFilesWorker extends AbstractWorker<List<UIFile>> {

  private final List<File> files;
  private final List<MediaRenamed> renamed;
  private boolean subFolder;
  private final UISettings setting;
  private final FilenameFilter folderFilter = new FilenameFilter() {

    @Override
    public boolean accept(File dir, String name) {
      return new File(dir.getAbsolutePath() + File.separator + name).isDirectory();
    }
  };

  /**
   * Constructor arguments
   *
   * @param errorSupport
   * @param files
   * @param renamed
   */
  public ListFilesWorker(PropertyChangeSupport errorSupport, List<File> files, List<MediaRenamed> renamed) {
    super(errorSupport);
    this.renamed = renamed;
    this.files = files;
    setting = UISettings.getInstance();
    //subFolder = setting.scanSubfolder;// FIXME
  }

  /**
   * Retreive all media files in a folder and subfolder
   *
   * @return
   */
  @Override
  public List<UIFile> executeInBackground() {

    List<UIFile> medias = new ArrayList<UIFile>();

    if (files == null || files.isEmpty()) {
      return medias;
    }

    if (!subFolder && subFolder(files)) {// FIXME hide loading dialog et re-show after
//      int n = JOptionPane.showConfirmDialog(parent, LocaleUtils.i18n("scanSubFolder"), LocaleUtils.i18n("question"), JOptionPane.YES_NO_OPTION);// FIXME use weblookandfeel dialog
//      if (n != JOptionPane.NO_OPTION) {
//        subFolder = true;
//      }
    }

    int count = files.size();
    for (int i = 0; i < count; i++) {
      if (isCancelled()) {// User cancel
        UISettings.LOGGER.log(Level.INFO, "ListFilesWorker Cancelled");
        return medias;
      }

      if (files.get(i).isDirectory()) {
        addFiles(medias, files.get(i));
      } else {
        boolean addfiletoUI = !setting.useExtensionFilter || FileUtils.checkFileExt(files.get(i).getName(), setting.extensions);
        if (addfiletoUI) {
          addUIFiles(medias, files.get(i));
        }
      }
    }

    return medias;
  }

  /**
   * Check if one of directory contain a subdirectory
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
      UISettings.LOGGER.log(Level.SEVERE, "Directory \"{0}\" does not exist or is not a Directory", file.getName());
      return;
    }

    for (int i = 0; i < listFiles.length; i++) {
      if (listFiles[i].isDirectory() && subFolder) {
        addFiles(medias, listFiles[i]);
      } else if (!setting.useExtensionFilter || FileUtils.checkFileExt(listFiles[i].getName(), setting.extensions)) {
        addUIFiles(medias, listFiles[i]);
      }
    }
  }

  /**
   * Add file to media files list
   *
   * @param medias Media file list
   * @param file File to add
   */
  private void addUIFiles(List<UIFile> medias, File file) {
    boolean wasrenamed = wasRenamed(file.getAbsolutePath());
    UIFile.MediaType type = isMovie(file) ? UIFile.MediaType.MOVIE : UIFile.MediaType.TVSHOW;
    medias.add(new UIFile(file, type, wasrenamed));
  }

  /**
   * Check if Movie Renamer has already renamed this file
   *
   * @param file File to check
   * @return True if file was renamed by Movie Renamer, False otherwise
   */
  private boolean wasRenamed(String file) {
    if (renamed != null) {
      for (int i = 0; i < renamed.size(); i++) {
        if (renamed.get(i).getMovieFileDest().equals(file)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Check if file is a movie
   *
   * @param file File to check
   * @return True if file is a movie, false otherwise
   */
  private static boolean isMovie(File file) {// TODO A refaire , améliorer la detection !!!
    String filename = file.getName();

    for (TvShowNameMatcher.TvShowPattern patternToTest : TvShowNameMatcher.TvShowPattern.values()) {
      if (searchPattern(filename, patternToTest.getPattern())) {
        return false;
      }
    }
    if (file.getParent().matches(".*((?i:season)|(?i:saison)).*")) {
      return false;
    }
    return true;
  }

  /**
   * Search pattern in string
   *
   * @param text String to search in
   * @param pattern Pattern to match
   * @return True if pattern is find in string , False otherwise
   */
  private static boolean searchPattern(String text, Pattern pattern) {
    Matcher searchMatcher = pattern.matcher(text);
    if (searchMatcher.find()) {
      return true;
    }
    return false;
  }
}