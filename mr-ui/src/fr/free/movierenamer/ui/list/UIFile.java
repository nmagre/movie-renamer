/*
 * Movie Renamer
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
package fr.free.movierenamer.ui.list;

import fr.free.movierenamer.info.FileInfo;
import fr.free.movierenamer.mediainfo.MediaTag;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.utils.Sorter;
import java.io.File;
import javax.swing.Icon;

/**
 * Class UIFile
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class UIFile extends Sorter.ISort implements IIconList {

  private final FileInfo file;
  private final MediaTag mtag;
  private String groupName;

  /**
   * Constructor arguments
   *
   * @param file A mediaInfo file
   * @param groupName
   */
  public UIFile(FileInfo file, String groupName) {
    this.file = file;
    this.groupName = groupName;
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

  @Override
  public int getYear() {
    return file.getYear();
  }

  /**
   * Get media icon to display in list
   *
   * @return Icon
   */
  @Override
  public Icon getIcon() {
    if (wasRenamed()) {
      return ImageUtils.LOGO_22;
    }

    switch (file.getType()) {
      case MOVIE:
        return ImageUtils.MOVIE_16;
      case TVSHOW:
        return ImageUtils.TV_16;
    }

    //return ImageUtils.MEDIA;
    return null; // FIXME
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

  @Override
  public long getLength() {
    return getFile().length();
  }

  /**
   * @return the groupName
   */
  public String getGroupName() {
    return groupName;
  }
}
