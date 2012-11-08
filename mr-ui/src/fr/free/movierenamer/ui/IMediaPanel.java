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
package fr.free.movierenamer.ui;

import com.alee.laf.list.WebList;
import fr.free.movierenamer.info.MediaInfo;

/**
 * Interface IMediaPanel
 * 
 * @author Nicolas Magré
 */
public interface IMediaPanel {

//  /**
//   * Add image to media panel
//   *
//   * @param img ImageInfo
//   * @param mediaImage Media image
//   * @param selectLast Select last image instead of first
//   */
//  void addImageToList(Image img, MediaImage mediaImage, boolean selectLast);

//  /**
//   * Add actor to media panel
//   *
//   * @param actor Actor name
//   * @param actorImg Actor image
//   * @param desc Actor description (html -> name + image + roles)
//   */
//  void addActorToList(String actor, ActorImage actorImg, String desc);
  
//  /**
//   * Add subtitle to media panel
//   * 
//   * @param subtitle
//   */
//  void addSubtitleToList(Subtitle subtitle);

  /**
   * Clear media panel
   */
  void clear();

  void setMediaInfo(MediaInfo mediaInfo);

  MediaInfo getMediaInfo();

  WebList getCastingList();
  WebList getThumbnailsList();
  WebList getFanartsList();
  WebList getSubtitlesList();
  
}
