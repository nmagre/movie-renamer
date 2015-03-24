/*
 * movie-renamer-core
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
package fr.free.movierenamer.info;

import fr.free.movierenamer.settings.Settings;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Class ImageInfo
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class ImageInfo extends Info {

  private static final long serialVersionUID = 1L;

  public enum ImageSize {

    small,// Thumb
    medium,
    big// Original
  }

  public static enum ImageProperty {

    language,
    width,
    height,
    url,
    urlTumb,
    urlMid
  }

  public static enum ImageCategoryProperty {

    actor,
    thumb,
    fanart,
    logo,
    banner,
    cdart,
    clearart,
    unknown
  }
  protected Map<ImageProperty, String> fields;
  protected ImageCategoryProperty category;
  protected int id;

  public ImageInfo(final int id, final Map<ImageProperty, String> fields, final ImageCategoryProperty category) {
    this.id = id;
    this.fields = new EnumMap<ImageProperty, String>(fields);
    this.category = category == null ? ImageCategoryProperty.unknown : category;
  }

  private String get(final ImageProperty key) {
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
      return Integer.valueOf(get(ImageProperty.width));
    } catch (NumberFormatException e) {
    }
    return null;
  }

  public Integer getHeight() {
    try {
      return Integer.valueOf(get(ImageProperty.height));
    } catch (NumberFormatException e) {
    }
    return null;
  }

  @SuppressWarnings("fallthrough")
  public URL getHref(final ImageSize size) {
    String url = null;

    switch (size) {
      case small:
        url = get(ImageProperty.urlTumb);
        if (url != null && url.length() > 0) {
          break;
        }

      case medium:
        url = get(ImageProperty.urlMid);
        if (url != null && url.length() > 0) {
          break;
        }

      case big:
        url = get(ImageProperty.url);
        break;
    }

    if (url == null) {
      return null;
    }

    try {
      return new URL(url);
    } catch (MalformedURLException ex) {
      Settings.LOGGER.log(Level.WARNING, null, ex);
    }

    return null;
  }

  public int getId() {
    return id;
  }

  @Override
  public String toString() {
    return fields.toString();
  }
}
