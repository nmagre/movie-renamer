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

import fr.free.movierenamer.utils.Settings;

/**
 * Interface Media
 *
 * @author Nicolas Magré
 */
public interface Media {

  public static final int MOVIE = 0;
  public static final int TVSHOW = 1;

  /**
   * Get media file
   *
   * @return A media file
   */
  public MediaFile getMediaFile();

  /**
   * Set media file
   *
   * @param mediaFile A media file
   */
  public void setMediaFile(MediaFile mediaFile);

  /**
   * Get media type (MOVIE, TVSHOW, ...)
   *
   * @return Media type
   */
  public int getType();

  /**
   * Get renamed title
   *
   * @param regex Expression to rename media title with
   * @param setting Movie Renamer settings
   * @return Renamed title
   */
  public String getRenamedTitle(String regex, Settings setting);

  /**
   * Get Media API id
   * @param IDtype ID type
   * @return Media Api ID or null
   */
  public MediaID getMediaId(int IDtype);
  
  /**
   * Get year if it found in title or directory name
   * @return 
   */
  //public String getProbaleYear();
  
  /**
   * Get search string
   *
   * @return Search string
   */
  public String getSearch();

  /**
   * Set Search
   *
   * @param search Search string
   */
  public void setSearch(String search);

  /**
   * Set media info
   *
   * @param info Media info
   */
  public void setInfo(Object info);

  /**
   * Add Meda ID
   *
   * @param id
   */
  public void setMediaID(MediaID id);

  /**
   * Clear media (images,info,..)
   */
  public void clear();
}
