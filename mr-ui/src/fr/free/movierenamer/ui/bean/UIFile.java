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
package fr.free.movierenamer.ui.bean;

import fr.free.movierenamer.info.FileInfo;
import fr.free.movierenamer.info.ImageInfo.ImageSize;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.utils.Sorter;
import java.io.File;
import java.net.URI;
import javax.swing.Icon;

/**
 * Class UIFile
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class UIFile extends Sorter.ISort implements IIconList {

  private final File file;
  private FileInfo fileInfo;
  private String groupName;
  private Icon icon;
  private String search;

  /**
   * Constructor arguments
   *
   * @param file A mediaInfo file
   * @param groupName
   */
  public UIFile(File file, String groupName) {
    this.file = file;
    this.groupName = groupName;
    this.icon = ImageUtils.MEDIA_16;

    fileInfo = null;
    search = null;
  }

  public FileInfo getFileInfo() {
    return fileInfo;
  }

  public void setFileInfo(FileInfo fileInfo) {
    this.fileInfo = fileInfo;
  }

  public String getSearch() {
    return search;
  }

  public void setSearch(String search) {
    this.search = search;
  }

  /**
   * Get file
   *
   * @return File
   */
  public File getFile() {
    return file;
  }

  @Override
  public int getYear() {
    return -1; //FIXME
  }

  /**
   * Get media icon to display in list
   *
   * @return Icon
   */
  @Override
  public Icon getIcon() {
    /*if (wasRenamed()) {
      return ImageUtils.LOGO_22;// FIXME change icon
    }

    if (fileInfo == null) {
      getFileInfo();
    }

    switch (fileInfo.getType()) {
      case MOVIE:
        return ImageUtils.MOVIE_16;
      case TVSHOW:
        return ImageUtils.TV_16;
    }

    //return ImageUtils.MEDIA;
    * */
    return icon; // FIXME
  }

  @Override
  public String toString() {
    return file.getName();
  }

  @Override
  public void setIcon(Icon icon) {
    this.icon = icon;
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

  @Override
  public URI getUri(ImageSize size) {
    return file.toURI();
  }
}
