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

import fr.free.movierenamer.media.mediainfo.MITag;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Settings;
import java.io.File;
import java.util.List;
import javax.swing.JTextField;

/**
 * Interface Media
 * 
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
public abstract class Media<T extends MediaInfo> {

  public final Settings conf = Settings.getInstance();
  private final MediaFile mediaFile;
  protected MediaID mediaId;
  protected final MITag mtag;
  protected T mediaInfo;
  private final JTextField renameField;
  private String firstSearch;
  private String search;
  private String year;

  public enum MediaType {
    MOVIE,
    TVSHOW
  }

  /**
   * Constructor arguments
   * 
   * @param mediaFile A media file
   * @param mediaInfo
   * @param renameField
   */
  public Media(MediaFile mediaFile, T mediaInfo, JTextField renameField) {
    this.mediaFile = mediaFile;
    this.mediaInfo = mediaInfo;
    this.renameField = renameField;
    this.mtag = new MITag(mediaFile.getFile());
    System.out.println(this.mtag);
  }

  /**
   * @return the mediaFile
   */
  public final MediaFile getMediaFile() {
    return mediaFile;
  }

  /**
   * @param search the search to set
   */
  public final void setSearch(String search) {
    if (firstSearch == null) {
      firstSearch = search;
    }
    this.search = search;
  }

  /**
   * @param year the year to set
   */
  public final void setYear(String year) {
    this.year = year;
  }

  /**
   * @param mfile
   */
  public final void setMediaFile(MediaFile mfile) {
    System.err.println("method to delete !!!!");
  }

  public final File getFile() {
    return mediaFile.getFile();
  }

  public final MediaID getMediaId(MediaID.MediaIdType IDtype) {
    if (mediaId.getType() == IDtype) {
      return mediaId;
    }

    for (MediaID mid : mediaInfo.getIDs()) {
      if (mid.getType() == IDtype) {
        return mid;
      }
    }

    return null;
  }

  /**
   * Get media type (MOVIE, TVSHOW, ...)
   * 
   * @return Media type
   */
  public abstract MediaType getType();

  /**
   * Get renamed title
   * 
   * @param format Mr rename file format
   * @return Renamed title
   */
  public abstract String getRenamedTitle(String format);

  /**
   * Get Media API id
   * 
   * @param IDtype ID type
   * @return Media Api ID or null
   */
  // public MediaID getMediaId(MediaID.MediaIdType IDtype);

  /**
   * Get year if it found in title or directory name
   * 
   * @return
   */
  // public String getProbaleYear();
  /**
   * Get search string
   * 
   * @return Search string
   */
  public final String getSearch() {
    return search;
  }

  /**
   * Get media year if it found in filename
   * 
   * @return Year or -1 if it not found
   */
  public final int getYear() {
    try {
      return Integer.parseInt(year);
    } catch (NumberFormatException nfe) {
      return -1;
    }
  }

  /**
   * Get list of actors
   * 
   * @return
   */
  public final List<MediaPerson> getActors() {
    return mediaInfo.getActors();
  }

  /**
   * Get media images
   * 
   * @param type Image type
   * @return list of images
   * @throws ActionNotValidException
   */
  public final List<MediaImage> getImages(MediaImage.MediaImageType type) throws ActionNotValidException {
    return mediaInfo.getImages(type);
  }
  
  /**
   * @return the mediaInfo
   */
  public final T getInfo() {
    return mediaInfo;
  }

  /**
   * Set Search
   * 
   * @param search Search string
   */
  // public void setSearch(String search);

  /**
   * Set default search, if settings changed
   */
  // public void setDefaultSearch();

  /**
   * Set media info
   * 
   * @param info Media info
   */
  public final void setInfo(T mediaInfo) {
    this.mediaInfo = mediaInfo;
  }

  /**
   * @param mediaId the mediaId to set
   */
  public final void setMediaId(MediaID mediaId) {
    this.mediaId = mediaId;
  }

  public final void addMediaID(MediaID id) {
    if (getMediaId(id.getType()) == null) {
      mediaInfo.addId(id);
    }
  }

  /**
   * Clear media (images,info,..)
   */
  public final void clear() {
    mediaInfo.clear();
  }

  @Override
  public final String toString() {
    String res = mediaFile.toString() + "\n";
    res += mediaFile.toString();
    return res;
  }

  /**
   * 
   */
  public final void resetDefaultSearch() {
    setSearch(firstSearch);
  }

}
