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

import java.io.Serializable;
import java.util.*;

/**
 * Class Images
 *
 * @author Nicolas Magré
 */
public class MediaImage implements Serializable {
  
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
  private String language;

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
    language = "";
  }

  /**
   * Constructor arguments
   *
   * @param id Image id, "-1" -> image added from web/hdd/..., "0" image from API/NFO/...
   * @param type Media image type
   * @param language Image language
   */
  public MediaImage(int id, MediaImageType type, String language) {
    this.id = id;
    this.type = type;
    images = new String[MediaImageSize.values().length];
    this.language = language;
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
   * Get image language
   *
   * @return iso_639_1 of language or empty string
   */
  public String getLanguage() {
    return language;
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

  /**
   * Set image language (iso_639_1)
   *
   * @param language
   */
  public void setLanguage(String language) {
    if (language != null) {
      this.language = language;
    }
  }

  /**
   * Sort media list by language and add at beginning specified language if it is found
   *
   * @param language iso_639_1
   * @param list List to sort
   */
  public static void sortByLanguage(String language, List<MediaImage> list) {
    List<MediaImage> tmp = new ArrayList<MediaImage>();
    
    if (list == null) {
      return;
    }
    
    for (int i = 0; i < list.size(); i++) {
      MediaImage image = list.get(i);
      if (image.getLanguage().equalsIgnoreCase(language)) {
        tmp.add(image);
        list.remove(i);
      }
    }
    
    
    Comparator<MediaImage> mediaImageComparator = new Comparator<MediaImage>() {
      
      @Override
      public int compare(MediaImage o1, MediaImage o2) {
        return o1.getLanguage().compareTo(o2.getLanguage());
      }
    };
    
    Collections.sort(list, mediaImageComparator);
    Collections.sort(tmp, mediaImageComparator);
    
    for (int i = 0; i < tmp.size(); i++) {
      list.add(i, tmp.get(i));
    }
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final MediaImage other = (MediaImage) obj;
    if (this.id != other.id) {
      return false;
    }
    if (this.type != other.type) {
      return false;
    }
    if (!Arrays.deepEquals(this.images, other.images)) {
      return false;
    }
    return true;
  }
  
  @Override
  public int hashCode() {
    int hash = 3;
    hash = 43 * hash + this.id;
    hash = 43 * hash + (this.type != null ? this.type.hashCode() : 0);
    hash = 43 * hash + Arrays.deepHashCode(this.images);
    return hash;
  }
  
  @Override
  public String toString() {
    return getUrl(MediaImageSize.ORIGINAL);
  }
}
