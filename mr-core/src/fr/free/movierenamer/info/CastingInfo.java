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

import java.net.URI;
import java.net.URL;
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
    job,
    picturePath
  }
  public static final String ACTOR = "Actor";
  public static final String DIRECTOR = "Director";
  public static final String WRITER = "Writer";
  protected Map<PersonProperty, String> fields;

  protected CastingInfo() {
    // used by serializer
  }

  public CastingInfo(Map<PersonProperty, String> fields) {
    this.fields = new EnumMap<PersonProperty, String>(fields);
  }

  private String get(PersonProperty key) {
    return fields.get(key);
  }

  public int getId() {
    try {
      return new Integer(get(PersonProperty.id));
    } catch (Exception e) {
    }
    return -1;
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

  public URI getPicturePath() {
    try {
      return new URL(get(PersonProperty.picturePath)).toURI();
    } catch (Exception e) {
      return null;
    }
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
    // return String.format("{name: %s, character: %s, job: %s}", name, character, job);
    return fields.toString();
  }
}