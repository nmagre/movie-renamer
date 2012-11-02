/*
 * movie-renamer
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
import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.Map;

/**
 * Class SubtitleInfo
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SubtitleInfo extends Info {
  private static final long serialVersionUID = 1L;

  public static enum SubtitleProperty {
    name,
    href,
    language
  }
  
  protected Map<SubtitleProperty, String> fields;
  
  protected SubtitleInfo() {
    // used by serializer
  }

  public SubtitleInfo(Map<SubtitleProperty, String> fields) {
    this.fields = new EnumMap<SubtitleProperty, String>(fields);
  }

  private String get(SubtitleProperty key) {
    return fields.get(key);
  }

  public String getName() {
    return get(SubtitleProperty.name);
  }

  public URL getHref() {
    try {
      return new URL(get(SubtitleProperty.href));
    } catch (Exception e) {
      return null;
    }
  }

  public String getLanguage() {
    return get(SubtitleProperty.language);
  }
  
  public ByteBuffer getFile() throws Exception {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Not supported yet.");
    //return null;
  }
  
  @Override
  public int hashCode() {
    return getHref().getPath().hashCode();
  }
  
  
  @Override
  public boolean equals(Object object) {
    if (object instanceof SubtitleInfo) {
      SubtitleInfo other = (SubtitleInfo) object;
      return getHref().getPath().equals(other.getHref().getPath());
    }
    
    return false;
  }
  
  
  @Override
  public String toString() {
    return String.format("%s [%s]", getName(), getLanguage());
  }
}
