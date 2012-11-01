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

import com.alee.laf.list.WebList;
import fr.free.movierenamer.matcher.TvShowNameMatcher;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.ui.LoadingDialog.LoadingDialogPos;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.res.IconListRenderer;
import fr.free.movierenamer.ui.res.UIFile;
import fr.free.movierenamer.ui.utils.MediaRenamed;
import fr.free.movierenamer.utils.FileUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 * Class listFilesWorker ,get List of media files in files list
 * 
 * @author Magré Nicolas
 */
public class ListFilesWorker extends AbstractWorker {

  private final List<File> files;
  private final List<MediaRenamed> renamed;
  private String currentParent;
  private boolean subFolder;
  private int count;
  private int nbFiles;
  private final WebList mediaList;

  private final FilenameFilter folderFilter = new FilenameFilter() {
    @Override
    public boolean accept(File dir, String name) {
      return new File(dir.getAbsolutePath() + File.separator + name).isDirectory();
    }
  };

  /**
   * Constructor arguments
   * 
   * @param mediaList
   */
  public ListFilesWorker(PropertyChangeSupport errorSupport, MovieRenamer parent, List<File> files, WebList mediaList, List<MediaRenamed> renamed) {
    super(errorSupport, parent);
    this.renamed = renamed;
    this.files = files;
    subFolder = Settings.getInstance().scanSubfolder;
    this.mediaList = mediaList;
  }

  @Override
  protected LoadingDialogPos getLoadingDialogPos() {
    return LoadingDialogPos.files;
  }

  /**
   * Retreive all media files in a folder and subfolder
   * 
   * @return ArrayList of movies file
   */
  @Override
  public void executeInBackground() {
    DefaultListModel mediaFileNameModel = new DefaultListModel();
    if (files != null) {
      // TODO reprocess this !!!

      List<UIFile> medias = new ArrayList<UIFile>();
      nbFiles = 0;
      count = 0;
      currentParent = "";

      // let's parse the selected folder
      if (!subFolder) {
        for (File file : files) {
          if (isCancelled()) {
            return;
          }
          if (file.isDirectory()) {
            File[] subDir = file.listFiles(folderFilter);
            if (subDir != null) {
              nbFiles += subDir.length;
              if (subDir.length > 0) {
                if (!subFolder) {
                  parent.setCursor(MovieRenamer.normalCursor);
                  int n = JOptionPane.showConfirmDialog(parent, LocaleUtils.i18n("scanSubFolder"), LocaleUtils.i18n("question"), JOptionPane.YES_NO_OPTION);
                  if (n == JOptionPane.NO_OPTION) {
                    break;
                  }
                  subFolder = true;
                }
              }
            }
          }
        }
      }

      for (File file : files) {
        if (isCancelled()) {
          return;
        }

        if (file.isDirectory()) {
          currentParent = file.getName();
          getFiles(medias, file);
        } else if (!Settings.getInstance().useExtensionFilter || FileUtils.checkFileExt(file.getName(), Settings.getInstance().extensions)) {
          addUIFiles(medias, file);
        }
      }
      // Collections.sort(medias, new UIFilesNameComparator());

      // let's process the UI
      for (int i = 0; i < medias.size(); i++) {
        mediaFileNameModel.addElement(medias.get(i));
      }

      // mediaLbl.setText(LocaleUtils.i18n("media") + " : " + mediaFileNameModel.size());//FIXME ???
    }

    mediaList.setCellRenderer(new IconListRenderer<UIFile>());
    mediaList.setModel(mediaFileNameModel);

    if (mediaFileNameModel.isEmpty()) {
      JOptionPane.showMessageDialog(parent, LocaleUtils.i18n("noMovieFound"), LocaleUtils.i18n("error"), JOptionPane.ERROR_MESSAGE);// FIXME change movie by media
    } else if (Settings.getInstance().selectFrstMedia) {
      mediaList.setSelectedIndex(0);
    }
  }

  /**
   * Scan recursively folders and add media to a list
   * 
   * @param medias List of movies
   * @param file File to add or directory to scan
   */
  private void getFiles(List<UIFile> medias, File file) {
    if (isCancelled()) {
      return;
    }

    File[] listFiles = file.listFiles();
    if (listFiles == null) {
      Settings.LOGGER.log(Level.SEVERE, "Directory \"{0}\" does not exist or is not a Directory", file.getName());
      return;
    }

    for (int i = 0; i < listFiles.length; i++) {
      if (listFiles[i].isDirectory() && subFolder) {
        getFiles(medias, listFiles[i]);
        if (listFiles[i].getParentFile().getName().equals(currentParent) && count < nbFiles) {
          count++;
          setProgress(((count * 100) / nbFiles));
        }
      } else if (!Settings.getInstance().useExtensionFilter || FileUtils.checkFileExt(listFiles[i].getName(), Settings.getInstance().extensions)) {
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
  public static boolean isMovie(File file) {// TODO A refaire , améliorer la detection !!!
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

  // /**
  // * class UIFilesNameComparator , compare two filename
  // */
  // private static class UIFilesNameComparator implements Comparator<UIFile>, Serializable {
  //
  // private static final long serialVersionUID = 1L;
  //
  // @Override
  // public int compare(UIFile s1, UIFile s2) {
  // return s1.getFile().getName().compareTo(s2.getFile().getName());
  // }
  // }
}