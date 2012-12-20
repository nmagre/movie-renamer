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

import fr.free.movierenamer.info.FileInfo;
import fr.free.movierenamer.mediainfo.MediaTag;
import fr.free.movierenamer.ui.utils.UIUtils;
import java.io.File;
import javax.swing.Icon;

/**
 * Class UIFile
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class UIFile implements ISort {

  private final FileInfo file;
  private final MediaTag mtag;

  // private final MediaTag mtag;
  /**
   * Constructor arguments
   *
   * @param file A mediaInfo file
   */
  public UIFile(FileInfo file) {
    this.file = file;
    this.mtag = (file != null) ? new MediaTag(file.getFile()) : null;
  }

  /**
   * Get file
   *
   * @return File
   */
  public File getFile() {
    return file.getFile();
  }

  /**
   * @return the mtag
   */
  public MediaTag getMediaTag() {
    return mtag;
  }

  /**
   * Media has been renamed
   *
   * @return True is media was renamed, false otherwise
   */
  public boolean wasRenamed() {
    return file.wasRenamed();
  }

  /**
   * Get file type
   *
   * @return Media type
   */
  public FileInfo.MediaType getType() {
    return file.getType();
  }

  public final void setSearch(String search) {
    file.setSearch(search);
  }

  public int getYear() {
     return file.getYear();
  }

  /**
   * Get media icon to display in list
   *
   * @return Icon
   */
  @Override
  public Icon getIcon() {// TODO
//    if (renamed) {
//      return UIUtils.MEDIARENAMEDICON;
//    }

    if (wasRenamed()) {
      return UIUtils.MEDIAWASRENAMEDICON;
    }
    return UIUtils.MEDIAICON;
  }

  @Override
  public String toString() {
    return file.getFile().getName();
  }

  public final String getSearch() {
    return file.getSearch();
  }

  @Override
  public void setIcon(Icon icon) {
    // DO nothing
  }

  @Override
  public String getName() {
    return toString();
  }
}
