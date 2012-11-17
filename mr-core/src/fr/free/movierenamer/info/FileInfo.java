/*
 * mr-core
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
package fr.free.movierenamer.info;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.free.movierenamer.namematcher.NameCleaner;
import fr.free.movierenamer.namematcher.TvShowNameMatcher;

/**
 * Class FileInfo
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class FileInfo {

  public enum MediaType {
    MOVIE,
    TVSHOW
  }

  public static boolean isMovie(File file) {// TODO A refaire , améliorer la detection !!!
    String filename = file.getName();

    for (TvShowNameMatcher.TvShowPattern patternToTest : TvShowNameMatcher.TvShowPattern.values()) {
      if (searchPattern(filename, patternToTest.getPattern())) {
        return false;
      }
    }
    String parent = file.getParent();
    if (parent != null) {
      if (parent.matches(".*((?i:season)|(?i:saison)).*")) {
        return false;
      }
    }
    return true;
  }

  /**
   * Search pattern in string
   * 
   * @param text
   *          String to search in
   * @param pattern
   *          Pattern to match
   * @return True if pattern is find in string , False otherwise
   */
  private static boolean searchPattern(String text, Pattern pattern) {
    Matcher searchMatcher = pattern.matcher(text);
    if (searchMatcher.find()) {
      return true;
    }
    return false;
  }

  private final File originalFile;
  private final MediaType type;
  private boolean renamed;
  private String firstSearch;
  private String search;
  private Integer year;
  private final List<String> renamedFiles;

  public FileInfo(File file) {
    this.originalFile = file;
    this.type = isMovie(file) ? MediaType.MOVIE : MediaType.TVSHOW;
    this.renamedFiles = null;
    this.renamed = wasRenamed(file.getAbsolutePath());
    NameCleaner nc = new NameCleaner();
    setSearch(nc.extractName(file.getName(), false));
    setYear(nc.extractYear(file.getName()));
  }

  public String getSearch() {
    return search;
  }

  public Integer getYear() {
    return year;
  }

  public boolean isRenamed() {
    return renamed;
  }

  public MediaType getType() {
    return type;
  }

  public final void setSearch(String search) {
    if (firstSearch == null) {
      firstSearch = search;
    }
    this.search = search;
  }

  public final void setYear(Integer year) {
    this.year = year;
  }

  public void setRenamed(boolean renamed) {
    this.renamed = renamed;
  }

  /**
   * Check if we have already renamed this file
   * 
   * @param file
   *          File to check
   * @return True if file was renamed, False otherwise
   */
  private boolean wasRenamed(String file) {
    if (renamedFiles != null) {
      for (int i = 0; i < renamedFiles.size(); i++) {
        if (renamedFiles.get(i).equals(file)) {
          return true;
        }
      }
    }
    return false;
  }

}
