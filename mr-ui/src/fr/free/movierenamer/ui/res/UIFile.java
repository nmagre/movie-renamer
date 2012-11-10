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
package fr.free.movierenamer.ui.res;

import fr.free.movierenamer.mediainfo.MediaTag;
import fr.free.movierenamer.namematcher.MediaNameMatcher;
import fr.free.movierenamer.namematcher.MovieNameMatcher;
import fr.free.movierenamer.namematcher.TvShowNameMatcher;
import fr.free.movierenamer.ui.settings.Settings;
import java.io.File;
import javax.swing.Icon;

/**
 * Class UIFile
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class UIFile implements IIconList {

  public enum MediaType {
    MOVIE,
    TVSHOW
  }

  private final File file;
  private boolean renamed;
  private final boolean wasRenamed;
  private String firstSearch;
  private String search;
  private int year;
  private final UIFile.MediaType type;
  private final MediaTag mtag;

  // private final MediaTag mtag;

  /**
   * Constructor arguments
   * 
   * @param file A media file
   * @param type Media type
   * @param wasRenamed Media was renamed
   */
  public UIFile(File file, UIFile.MediaType type, boolean wasRenamed) {
    this.file = file;
    this.type = type;
    this.wasRenamed = wasRenamed;
    this.mtag = (file != null) ? new MediaTag(file) : null;
    renamed = false;
    // this.mtag = new MediaTag(mediaFile.getFile());
    MediaNameMatcher matcher = null;
    if (type != null) {
      switch (type) {
      case MOVIE:
        matcher = new MovieNameMatcher(file, Settings.getInstance().mediaNameFilters);
        break;
      case TVSHOW:
        matcher = new TvShowNameMatcher(file, Settings.getInstance().mediaNameFilters);
        break;
      }
    }

    if (matcher != null) {
      setSearch(matcher.getName());
      setYear(matcher.getYear());
    }
  }

  /**
   * Get file
   * 
   * @return File
   */
  public File getFile() {
    return file;
  }
  
  /**
   * @return the mtag
   */
  public MediaTag getMediaTag() {
    return mtag;
  }

  /**
   * Set media renamed
   * 
   * @param renamed Renamed
   */
  public void setRenamed(boolean renamed) {
    this.renamed = renamed;
  }

  /**
   * Media is renamed
   * 
   * @return True if media is renamed, false otherwise
   */
  public boolean isRenamed() {
    return renamed;
  }

  /**
   * Media has been renamed
   * 
   * @return True is media was renamed, false otherwise
   */
  public boolean wasRenamed() {
    return wasRenamed;
  }

  /**
   * Get file type
   * 
   * @return Media type
   */
  public UIFile.MediaType getType() {
    return type;
  }

  public final void setSearch(String search) {
    if (firstSearch == null) {
      firstSearch = search;
    }
    this.search = search;
  }

  public final void setYear(int year) {
    this.year = year;
  }

  /**
   * Get media icon to display in list
   * 
   * @return Icon
   */
  @Override
  public Icon getIcon() {
    if (renamed) {
      return Settings.MEDIARENAMEDICON;
    }

    if (wasRenamed) {
      return Settings.MEDIAWASRENAMEDICON;
    }
    return Settings.MEDIAICON;
  }

  @Override
  public String toString() {
    return file.toString();
  }

  public final String getSearch() {
    return search;
  }
}
