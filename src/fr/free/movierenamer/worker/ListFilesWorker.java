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
package fr.free.movierenamer.worker;

import fr.free.movierenamer.matcher.TvShowEpisodeMatcher;
import fr.free.movierenamer.media.Media;
import fr.free.movierenamer.media.MediaFile;
import fr.free.movierenamer.media.MediaRenamed;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingWorker;

/**
 * Class listFilesWorker ,get List of media files in files list
 *
 * @author Magré Nicolas
 */
public class ListFilesWorker extends SwingWorker<ArrayList<MediaFile>, Void> {

  private Settings setting;
  private boolean subFolders;
  private ArrayList<File> files;
  private int nbFiles;
  private int count;
  private String currentParent;
  private ArrayList<MediaRenamed> renamed;

  /**
   * Constructor arguments
   *
   * @param files List of files
   * @param renamed List of renamed files
   * @param subFolders Scan subfolders
   * @param setting Movie Renamer settings
   */
  public ListFilesWorker(ArrayList<File> files, ArrayList<MediaRenamed> renamed, boolean subFolders, Settings setting) {
    this.setting = setting;
    this.renamed = renamed;
    this.files = files;
    this.subFolders = subFolders;
    this.nbFiles = -1;
    count = 0;
    currentParent = "";
  }

  /**
   * Constructor arguments
   *
   * @param files List of files
   * @param renamed List of renamed files
   * @param subFolders Scan subfolders
   * @param nbFiles Number of subfolders (only in first directory) for progressBar
   * @param setting Movie Renamer settings
   */
  public ListFilesWorker(ArrayList<File> files, ArrayList<MediaRenamed> renamed, boolean subFolders, int nbFiles, Settings setting) {
    this.setting = setting;
    this.renamed = renamed;
    this.files = files;
    this.subFolders = subFolders;
    this.nbFiles = nbFiles;
    count = 0;
    currentParent = "";
  }

  /**
   * Retreive all media files in a folder and subfolder
   *
   * @return ArrayList of movies file
   */
  @Override
  protected ArrayList<MediaFile> doInBackground() {
    ArrayList<MediaFile> medias = new ArrayList<MediaFile>();
    for (File file : files) {
      if (isCancelled()) {
        return null;
      }

      if (file.isDirectory()) {
        currentParent = file.getName();
        getFiles(medias, file);
      } else if (!setting.useExtensionFilter || Utils.checkFileExt(file.getName(), setting.extensions)) {
        addMediaFile(medias, file);
      }
    }
    Collections.sort(medias, new MediaFileNameComparator());
    return medias;
  }

  /**
   * Scan recursively folders and add media to a list
   *
   * @param medias List of movies
   * @param file File to add or directory to scan
   */
  private void getFiles(ArrayList<MediaFile> medias, File file) {
    if (isCancelled()) {
      return;
    }

    File[] listFiles = file.listFiles();
    if (listFiles == null) {
      Settings.LOGGER.log(Level.SEVERE, "Directory \"{0}\" does not exist or is not a Directory", file.getName());
      return;
    }

    for (int i = 0; i < listFiles.length; i++) {
      if (listFiles[i].isDirectory() && subFolders) {
        getFiles(medias, listFiles[i]);
        if (listFiles[i].getParentFile().getName().equals(currentParent) && count < nbFiles) {
          count++;
          setProgress(((count * 100) / nbFiles));
        }
      } else if (!setting.useExtensionFilter || Utils.checkFileExt(listFiles[i].getName(), setting.extensions)) {
        addMediaFile(medias, listFiles[i]);
      }
    }
  }

  /**
   * Add file to media files list
   * @param medias Media file list
   * @param file File to add
   */
  private void addMediaFile(ArrayList<MediaFile> medias, File file) {
    boolean wasrenamed = wasRenamed(file.getAbsolutePath());
    int type = isMovie(file) ? Media.MOVIE : Media.TVSHOW;
    medias.add(new MediaFile(file, type, wasrenamed, setting.showMovieFilePath));
  }

  /**
   * Check if Movie Renamer has already renamed this file
   *
   * @param file File to check
   * @return True if file was renamed by Movie Renamer, False otherwise
   */
  private boolean wasRenamed(String file) {
    for (int i = 0; i < renamed.size(); i++) {
      if (renamed.get(i).getMovieFileDest().equals(file)) {
        return true;
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
  static public boolean isMovie(File file) {//A refaire , amélioré la detection
    String filename = file.getName();
    if (searchPattern(filename, TvShowEpisodeMatcher.seasonPattern)) {
      return false;
    }
    if (searchPattern(filename, TvShowEpisodeMatcher.episodePattern)) {
      return false;
    }
    if (searchPattern(filename, TvShowEpisodeMatcher.SxEPattern)) {
      return false;
    }
    if (searchPattern(filename, TvShowEpisodeMatcher.SxEPattern2)) {
      return false;
    }
    if (searchPattern(filename, TvShowEpisodeMatcher.SxEPattern3)) {
      return false;
    }
    if (searchPattern(filename, TvShowEpisodeMatcher.SxEPattern4)) {
      return false;
    }
    if (searchPattern(filename, TvShowEpisodeMatcher.SxEPattern5)) {
      return false;
    }
    if (searchPattern(filename, TvShowEpisodeMatcher.SxEPattern6)) {
      return false;
    }
    if (searchPattern(filename, TvShowEpisodeMatcher.SxEPattern7)) {
      return false;
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
   * @param sPattern Pattern
   * @return True if pattern is find in string , False otherwise
   */
  private static boolean searchPattern(String text, String sPattern) {
    Pattern pattern = Pattern.compile(sPattern);
    Matcher searchMatcher = pattern.matcher(text);
    if (searchMatcher.find()) {
      return true;
    }
    return false;
  }

  /**
   * class MediaFileNameComparator , compare two filename
   */
  private static class MediaFileNameComparator implements Comparator<MediaFile>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(MediaFile s1, MediaFile s2) {
      return s1.getFile().getName().compareTo(s2.getFile().getName());
    }
  }
}
