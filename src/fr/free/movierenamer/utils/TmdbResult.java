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
package fr.free.movierenamer.utils;

import fr.free.movierenamer.ui.res.IIconList;
import fr.free.movierenamer.utils.Images;
import java.util.ArrayList;
import javax.swing.Icon;

/**
 * Class TmdbResult , TheMovieDb result
 * @author Nicolas Magré
 */
public class TmdbResult implements IIconList{

  private String tmdbAPIID;
  private ArrayList<Images> thumbs;
  private ArrayList<Images> fanarts;
  private Icon icon;

  /**
   * Constructor arguments
   * @param tmdbAPIID TheMovieDb api ID
   * @param thumbs Array of thumbs
   * @param fanarts Array of fanarts
   */
  public TmdbResult(String tmdbAPIID, ArrayList<Images> thumbs, ArrayList<Images> fanarts){
    this.tmdbAPIID = tmdbAPIID;
    this.thumbs = thumbs;
    this.fanarts = fanarts;
    this.icon = null;
  }

  /**
   * Get TheMovieDb ID
   * @return TheMovieDb ID
   */
  public String getId(){
    return tmdbAPIID;
  }

  /**
   * Get thumbs
   * @return Array of thumbs
   */
  public ArrayList<Images> getThumbs(){
    return thumbs;
  }

  /**
   * Get fanarts
   * @return Array of fanarts
   */
  public ArrayList<Images> getFanarts(){
    return fanarts;
  }

  @Override
  public Icon getIcon() {
    return icon;
  }
}
