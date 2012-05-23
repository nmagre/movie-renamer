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
package fr.free.movierenamer.ui.res;

import fr.free.movierenamer.media.MediaImage;
import java.awt.Image;

/**
 * Interface IMediaPanel
 * @author Nicolas Magré
 */
public interface IMediaPanel {
  /**
   * Add image to media panel
   * @param img Image
   * @param mediaImage Media image
   * @param selectLast Select last image instead of first
   */
  void addImageToList(Image img, MediaImage mediaImage,  boolean selectLast);
  
  /**
   * Add actor to media panel
   * @param actor Actor name
   * @param actorImg Actor image
   * @param desc Actor description (html -> name + image + roles)
   */
  void addActorToList(String actor,Image actorImg, String desc);
}
