/******************************************************************************
 *                                                                             *
 *    Movie Renamer                                                            *
 *    Copyright (C) 2012 Magré Nicolas                                         *
 *                                                                             *
 *    Movie Renamer is free software: you can redistribute it and/or modify    *
 *    it under the terms of the GNU General Public License as published by     *
 *    the Free Software Foundation, either version 3 of the License, or        *
 *    (at your option) any later version.                                      *
 *                                                                             *
 *    This program is distributed in the hope that it will be useful,          *
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            *
 *    GNU General Public License for more details.                             *
 *                                                                             *
 *    You should have received a copy of the GNU General Public License        *
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                             *
 ******************************************************************************/
package fr.free.movierenamer.worker;

import fr.free.movierenamer.utils.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingWorker;
import fr.free.movierenamer.movie.MovieFile;
import fr.free.movierenamer.utils.Settings;

/**
 * Class listFilesWorker
 * @author duffy
 */
public class ListFilesWorker extends SwingWorker<ArrayList<MovieFile>, Void> {

  private Settings setting;
  private boolean subFolders;
  private ArrayList<File> files;
  private int nbFiles;
  private int count;
  private String currentParent;

  public ListFilesWorker(ArrayList<File> files, boolean subFolders, Settings setting) {
    this.setting = setting;
    this.files = files;
    this.subFolders = subFolders;
    this.nbFiles = -1;
    count = 0;
    currentParent = "";
  }

  public ListFilesWorker(ArrayList<File> files, boolean subFolders, int nbFiles, Settings setting) {
    this.setting = setting;
    this.files = files;
    this.subFolders = subFolders;
    this.nbFiles = nbFiles;
    count = 0;
    currentParent = "";
  }

  /**
   * Retreive all movies files in a folder and subfolder
   * @return ArrayList of movies file
   */
  @Override
  protected ArrayList<MovieFile> doInBackground() {
    ArrayList<MovieFile> movies = new ArrayList<MovieFile>();
    for (int i = 0; i < files.size(); i++) {
      if (files.get(i).isDirectory()) {
        currentParent = files.get(i).getName();
        getFiles(movies, files.get(i));
      } else
        if (Utils.checkFile(files.get(i).getName(), setting))
          movies.add(new MovieFile(files.get(i), false, !isMovie(files.get(i))));
    }
    Collections.sort(movies, new MyFileComparable());
    return movies;
  }

  private void getFiles(ArrayList<MovieFile> movies, File file) {
    File[] listFiles = file.listFiles();
    if (listFiles == null) {
      setting.getLogger().log(Level.SEVERE, "Directory \"{0}\" does not exist or is not a Directory", file.getName());
      return;
    }
    for (int i = 0; i < listFiles.length; i++) {
      if (listFiles[i].isDirectory() && subFolders) {
        getFiles(movies, listFiles[i]);
        if (listFiles[i].getParentFile().getName().equals(currentParent)) {
          count++;
          setProgress((int) ((count * 100) / nbFiles));
        }
      }
      else if(Utils.checkFile(listFiles[i].getName(), setting))
        movies.add(new MovieFile(listFiles[i], false, !isMovie(listFiles[i])));
    }
  }

  private boolean isMovie(File file){
    if(file.length() < 400000000) return false;
    String filename = file.getName();
    if(searchPattern(filename, "\\d++x\\d++.?\\d++x\\d++")) return false;
    if(searchPattern(filename, "\\d++[eE]\\d\\d")) return false;
    if(searchPattern(filename, "[sS]\\d++[eE]\\d++")) return false;
    if(searchPattern(filename, "[sS]\\d++.[eE]\\d++")) return false;
    if(searchPattern(filename, "\\d++x\\d++")) return false;
    if(searchPattern(filename, "\\(\\d\\d\\d\\)")) return false;
    if(searchPattern(filename, "[eE][pP].?\\d++")) return false;
    return true;
  }

  private boolean searchPattern(String text, String sPattern){
    Pattern pattern = Pattern.compile(sPattern);
    Matcher searchMatcher = pattern.matcher(text);
    if(searchMatcher.find()) return true;
    return false;
  }

  private class MyFileComparable implements Comparator<MovieFile> {

    @Override
    public int compare(MovieFile s1, MovieFile s2) {
      return s1.getFile().getName().compareTo(s2.getFile().getName());
    }
  }
}
