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

import fr.free.movierenamer.mediainfo.MediaTag;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class MediaInfo
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class MediaInfo extends Info {

  private static final long serialVersionUID = 1L;

  protected CastingInfo[] casting;

//  protected ImageInfo[] images;

  protected MediaTag mtag;
  
  public List<CastingInfo> getPersons() {
    return Collections.unmodifiableList(Arrays.asList(casting));
  }

  public List<CastingInfo> getCast() {
    List<CastingInfo> actors = new ArrayList<CastingInfo>();
    if (casting != null) {
      for (CastingInfo castingInfo : casting) {
        if (castingInfo.isActor()) {
          actors.add(castingInfo);
        }
      }
    }
    return actors;
  }

  public List<String> getDirectors() {
    List<String> directors = new ArrayList<String>();
    if (casting != null) {
      for (CastingInfo castingInfo : casting) {
        if (castingInfo.isDirector())
          directors.add(castingInfo.getName());
      }
    }
    return directors;
  }

  public List<String> getActors() {
    List<String> actors = new ArrayList<String>();
    for (CastingInfo actor : getCast()) {
      actors.add(actor.getName());
    }
    return actors;
  }

//  public List<ImageInfo> getImages() {
//    return Collections.unmodifiableList(Arrays.asList(images));
//  }

//  public List<ImageInfo> getFanarts() {
//    return Collections.unmodifiableList(Arrays.asList(images));
//  }
//
//  public List<ImageInfo> getThumbs() {
//    return Collections.unmodifiableList(Arrays.asList(images));
//  }
//
//  public void setImages(List<ImageInfo> images) {
//    this.images = (images == null) ? null : images.toArray(new ImageInfo[images.size()]);
//  }
  
  public MediaTag getMediaTag() {
    return mtag;
  }
  
  public void setMediaTag(MediaTag mtag) {
    this.mtag = mtag;
  }

  public void setCasting(List<CastingInfo> persons) {
    this.casting = (persons == null) ? null : persons.toArray(new CastingInfo[persons.size()]);
  }

  public abstract String getRenamedTitle(String format);

}
