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

/**
 * Class Images
 *
 * @author Nicolas Magré
 */
public class MediaImage {

  public enum MediaImageType {

    THUMB,
    FANART,
    SEASONTHUMB;
  }

  public enum MediaImageSize {

    THUMB,
    MEDIUM,
    ORIGINAL
  }
  private int id;
  private MediaImageType type;
  private String[] images;

  /**
   * Constructor arguments
   *
   * @param id Image id, "-1" -> image added from web/hdd/..., "0" image from API/NFO/...
   * @param type Media image type
   */
  public MediaImage(int id, MediaImageType type) {
    this.id = id;
    this.type = type;
    images = new String[MediaImageSize.values().length];
  }

  /**
   * Get Id
   *
   * @return Id
   */
  public int getId() {
    return id;
  }

  /**
   * Get media image type
   *
   * @return Media image type
   */
  public MediaImageType getType() {
    return type;
  }

  /**
   * Get image url
   *
   * @param size
   * @return Url string of specified size if exist or low size, if there no url string null
   */
  public String getUrl(MediaImageSize size) {
    if (images[size.ordinal()] != null) {
      return images[size.ordinal()];
    }
    int pos = size.ordinal() - 1;
    if (pos < 0) {
      return null;
    }
    return getUrl(MediaImageSize.values()[pos]);
  }

  /**
   * Set url string
   *
   * @param url Url string
   * @param size Media image size
   */
  public void setUrl(String url, MediaImageSize size) {
    images[size.ordinal()] = url;
  }

  @Override
  public String toString() {
    return getUrl(MediaImageSize.ORIGINAL);
  }
}
