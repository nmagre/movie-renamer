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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumMap;
import java.util.Map;

/**
 * Class CastingInfo
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class CastingInfo extends Info {

  private static final long serialVersionUID = 1L;

  public static enum PersonProperty {

    id,
    name,
    character,
    job
  }
  public static final String ACTOR = "Actor";
  public static final String DIRECTOR = "Director";
  public static final String WRITER = "Writer";
  protected Map<PersonProperty, String> fields;
  protected ImageInfo img;

  protected CastingInfo() {
    // used by serializer
  }

  public CastingInfo(Map<PersonProperty, String> fields, ImageInfo img) {
    this.fields = new EnumMap<PersonProperty, String>(fields);
    this.img = img;
  }

  private String get(final PersonProperty key) {
    return fields.get(key);
  }

  public int getId() {
    try {
      return Integer.parseInt(get(PersonProperty.id));
    } catch (NumberFormatException e) {
    }

    return -1;
  }

  public ImageInfo getImageInfo() {
    return img;
  }

  public URI getImage(ImageInfo.ImageSize size) {
    if(img == null) {
      return null;
    }
    
    try {
      return img.getHref(size).toURI();
    } catch (URISyntaxException ex) {
    }

    return null;
  }

  public String getName() {
    return get(PersonProperty.name);
  }

  public String getCharacter() {
    return get(PersonProperty.character);
  }

  public String getJob() {
    return get(PersonProperty.job);
  }

  public boolean isActor() {
    return ACTOR.equalsIgnoreCase(getJob());
  }

  public boolean isDirector() {
    return DIRECTOR.equalsIgnoreCase(getJob());
  }

  public boolean isWriter() {
    return WRITER.equalsIgnoreCase(getJob());
  }

  @Override
  public String toString() {
    return getName();
  }
}
