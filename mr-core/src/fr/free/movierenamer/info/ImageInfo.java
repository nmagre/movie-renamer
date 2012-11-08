/*
 * movie-renamer-core
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
package fr.free.movierenamer.info;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

/**
 * Class ImageInfo
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class ImageInfo extends Info {

  private static final long serialVersionUID = 1L;

  public static enum ImageProperty {
    language,
    width,
    height,
    url,
    desc
  }

  public static enum ImageCategoryProperty {
    actor,
    thumb,
    fanart,
    unknown
  }

  protected Map<ImageProperty, String> fields;
  protected ImageCategoryProperty category;

  public ImageInfo(Map<ImageProperty, String> fields, ImageCategoryProperty category) {
    this.fields = new EnumMap<ImageProperty, String>(fields);
    if(category == null) {
      this.category = ImageCategoryProperty.unknown;
    } else {
      this.category = category;
    }
  }

  private String get(ImageProperty key) {
    return fields.get(key);
  }

  public ImageCategoryProperty getCategory() {
    return this.category;
  }

  public String getLanguage() {
    return get(ImageProperty.language);
  }

  public Integer getWidth() {
    try {
      return new Integer(get(ImageProperty.width));
    } catch (Exception e) {
      return null;
    }
  }

  public Integer getHeight() {
    try {
      return new Integer(get(ImageProperty.height));
    } catch (Exception e) {
      return null;
    }
  }

  public URL getHref() {
    try {
      return new URL(get(ImageProperty.url));
    } catch (Exception e) {
      return null;
    }
  }

//  public URI getURI() {
//    try {
//      return getHref().toURI();
//    } catch (URISyntaxException e) {
//      throw new RuntimeException(e);
//    }
//  }

  public String getDescription() {
    return get(ImageProperty.desc);
  }

  @Override
  public String toString() {
    return fields.toString();
  }
}