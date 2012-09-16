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
package fr.free.movierenamer.media;

import fr.free.movierenamer.media.Media.MediaType;
import fr.free.movierenamer.ui.res.IIconList;
import fr.free.movierenamer.ui.res.UiUtils;
import java.io.File;
import javax.swing.Icon;

/**
 * Class MovieFile
 * 
 * @author Nicolas Magré
 */
public final class MediaFile implements IIconList {

  private File file;
  private boolean renamed;
  private boolean showPath;
  private boolean wasRenamed;
  private Media.MediaType type;

  /**
   * Constructor arguments
   * 
   * @param file A media file
   * @param type Media type
   * @param wasRenamed Media was renamed
   * @param showPath Display path in toString()
   */
  public MediaFile(File file, MediaType type, boolean wasRenamed, boolean showPath) {
    this.file = file;
    this.type = type;
    this.wasRenamed = wasRenamed;
    this.showPath = showPath;
    renamed = false;
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
   * Set file
   * 
   * @param file File
   */
  public void setFile(File file) {
    this.file = file;
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
  public MediaType getType() {
    return type;
  }

  /**
   * Set file type
   * 
   * @param type Media type
   */
  public void setType(MediaType type) {
    this.type = type;
  }

  /**
   * Get media icon to display in list
   * 
   * @return Icon
   */
  @Override
  public Icon getIcon() {
    if (renamed) {
      return UiUtils.MEDIARENAMEDICON;
    }

    if (wasRenamed) {
      return UiUtils.MEDIAWASRENAMEDICON;
    }
    return UiUtils.MEDIAICON;
  }

  @Override
  public String toString() {
    return (showPath ? file.toString() : file.getName());
  }
}
