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
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.mediainfo.MediaAudio;
import fr.free.movierenamer.mediainfo.MediaTag;
import fr.free.movierenamer.mediainfo.MediaVideo;
import fr.free.movierenamer.renamer.Renamer;
import fr.free.movierenamer.searchinfo.Media.MediaType;
import static fr.free.movierenamer.searchinfo.Media.MediaType.TVSHOW;
import fr.free.movierenamer.ui.swing.renderer.CompoundIcon;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.utils.Sorter;
import fr.free.movierenamer.utils.StringUtils;
import java.io.File;
import javax.swing.Icon;

/**
 * Class UIFile
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class UIFile extends Sorter.ISort implements IIconList, IHtmlListTooltip {

  private File file;
  private FileInfo fileInfo;
  private String groupName;
  private Icon icon;
  private String search;
  private MediaType mtype;

  /**
   * Constructor arguments
   *
   * @param file A mediaInfo file
   * @param groupName
   * @param mtype
   */
  public UIFile(File file, String groupName, MediaType mtype) {
    this.file = file;
    this.groupName = groupName;
    this.mtype = mtype;
    this.icon = null;
    fileInfo = null;
  }

  private synchronized FileInfo getSFileInfo() {
    if (fileInfo == null) {
      setFileInfo(new FileInfo(file));
    }

    return fileInfo;
  }

  public FileInfo getFileInfo() {
    return getSFileInfo();
  }

  public IdInfo getImdbId() {
    return getSFileInfo().getImdbId();
  }

  public void setFile(File file) {
    this.file = file;
  }

  public synchronized void setFileInfo(FileInfo fileInfo) {
    this.fileInfo = fileInfo;
    this.mtype = fileInfo.getType();
    setIcon();
  }

  public String getSearch() {
    if (search == null || search.isEmpty()) {
      search = getSFileInfo().getSearch();
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
    return getSFileInfo().getYear();
  }

  /**
   * Get media icon to display in list
   *
   * @return Icon
   */
  @Override
  public Icon getIcon() {

    if (icon == null) {
      return ImageUtils.LOAD_16;
    }

    return icon;
  }

  private void setIcon() {// TODO return icon if file was renamed and id renamed

    FileInfo fileInfo = getFileInfo();
    icon = ImageUtils.MEDIA_16;

    switch (getMtype()) {
      case MOVIE:
        icon = ImageUtils.MOVIE_16;
        if (fileInfo.getImdbId() != null) {
          icon = ImageUtils.MOVIE_IMDB;
        }
        break;
      case TVSHOW:
        icon = ImageUtils.TV_16;
        if (fileInfo.getImdbId() != null) {
          icon = ImageUtils.TVSHOW_IMDB;
        }
        break;
    }

    if (Renamer.getInstance().wasRenamed(fileInfo)) {
      icon = new CompoundIcon(ImageUtils.LOGO_22, icon);
    }

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

  public String getMd5Hash() {
    return fileInfo.get(FileInfo.FileProperty.md5);
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public MediaType getMtype() {
    return mtype;
  }

  @Override
  public String getHtmlTooltip() {

    MediaTag tag = getFileInfo().getMediaTag();
    String str = "<html>";
    str += "<img src =\"" + ImageUtils.getImagePath("ui/16/movie.png") + "\">&nbsp;";
    str += "<font color=#4e89a4>" + toString() + "</font><br><hr>";
    str += "<img src =\"" + ImageUtils.getImagePath("ui/16/search.png") + "\">&nbsp;";
    str += getSearch() + "<br>";

    if (getYear() != -1) {
      str += "<img src =\"" + ImageUtils.getImagePath("ui/16/ssearch.png") + "\">&nbsp;";
      str += getYear() + "<br>";
    }

    if (getImdbId() != null) {
      str += "<img src =\"" + ImageUtils.getImagePath("scrapper/imdb.png") + "\">&nbsp;";
      str += getImdbId() + "<br>";
    }

    str += "<img src =\"" + ImageUtils.getImagePath("ui/16/other.png") + "\">&nbsp;";
    str += StringUtils.humanReadableByteCount(getLength()) + "<br>";
    if (tag != null) {
      MediaVideo mvideo = tag.getMediaVideo();
      MediaAudio maudio = tag.getMediaAudios().get(0);
      mvideo.getAspectRatio();
      
      
      
      str += "<img width =\"82\" height=\"40\" src =\"" + ImageUtils.getImagePath("mediaflags/video/" + mvideo.getCodec().toLowerCase() + ".png") + "\">&nbsp;";
      str += "<img width =\"82\" height=\"40\" src =\"" + ImageUtils.getImagePath("mediaflags/video/" + mvideo.getVideoDefinition().toLowerCase() + ".png") + "\">&nbsp;";
      str += "<img width =\"82\" height=\"40\" src =\"" + ImageUtils.getImagePath("mediaflags/audio/" + maudio.getCodec().toLowerCase() + ".png") + "\">&nbsp;";
      str += "<img width =\"82\" height=\"40\" src =\"" + ImageUtils.getImagePath("mediaflags/audio/" + maudio.getNbChannel() + ".png") + "\">&nbsp;";
    }
    str += "<html>";

    return str;
  }

}
