/*
 * Movie Renamer
 * Copyright (C) 2012-2014 Nicolas Magré
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
import fr.free.movierenamer.ui.utils.ImageUtils;
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

  private File file;
  private FileInfo fileInfo;
  private final String groupName;
  private Icon icon;
  private String search;
  private FileInfo.MediaType mtype;

  /**
   * Constructor arguments
   *
   * @param file A mediaInfo file
   * @param groupName
   * @param mtype
   */
  public UIFile(File file, String groupName, FileInfo.MediaType mtype) {
    this.file = file;
    this.groupName = groupName;
    this.mtype = mtype;
    this.icon = null;

    fileInfo = null;
  }

  public FileInfo getFileInfo() {
    if (fileInfo == null) {
      fileInfo = new FileInfo(file);
    }

    return fileInfo;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public void setFileInfo(FileInfo fileInfo) {
    this.fileInfo = fileInfo;
  }

  public String getSearch() {
    if (search == null || search.isEmpty()) {
      search = getFileInfo().getSearch();
    }
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
    return getFileInfo().getYear();
  }

  /**
   * Get media icon to display in list
   *
   * @return Icon
   */
  @Override
  public Icon getIcon() {// TODO return icon if file was renamed and id renamed

    if (icon != null) {
      return icon;
    }

    if (getFileInfo().wasRenamed()) {
      return ImageUtils.LOGO_22;
    }

    switch (getMtype()) {
      case MOVIE:
        return ImageUtils.MOVIE_16;
      case TVSHOW:
        return ImageUtils.TV_16;
    }

    /*if (wasRenamed()) {
     return ImageUtils.LOGO_22;
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
    return ImageUtils.MEDIA_16;
  }

  public void setIcon(Icon icon) {
    this.icon = icon;
  }

  @Override
  public String toString() {
    return file.getName();
  }

  @Override
  public String getName() {
    return file.getName();
  }

  @Override
  public long getLength() {
    return getFile().length();
  }

  public String getGroupName() {
    return groupName;
  }

  public FileInfo.MediaType getMtype() {
    return mtype;
  }
}
