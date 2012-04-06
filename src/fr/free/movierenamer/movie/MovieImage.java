/******************************************************************************
 *                                                                             *
 *    Movie Renamer                                                            *
 *    Copyright (C) 2012 Magré Nicolas                                         *
 *                                                                             *
 *    Movie Renamer is free software: you can redistribute it and/or modify    *
 *    it under the terms of the GNU General Public License as published by     *
 *    the Free Software Foundation, either version 3 of the License, or        *
 *    (at your option) any later version.                                      *
 *                                                                             *
 *    This program is distributed in the hope that it will be useful,          *
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            *
 *    GNU General Public License for more details.                             *
 *                                                                             *
 *    You should have received a copy of the GNU General Public License        *
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                             *
 ******************************************************************************/
package fr.free.movierenamer.movie;

import fr.free.movierenamer.utils.Images;
import fr.free.movierenamer.utils.Utils;
import java.util.ArrayList;

/**
 * Class MovieImage
 * @author Magré Nicolas
 */
public class MovieImage {
  private ArrayList<Images> thumbs;
  private ArrayList<Images> fanarts;

  public MovieImage(){
    thumbs = new ArrayList<Images>();
    fanarts = new ArrayList<Images>();
  }

  public ArrayList<Images> getThumbs(){
    return thumbs;
  }
  
  public ArrayList<Images> getFanarts(){
    return fanarts;
  }

  public void setThumbs(ArrayList<Images> thumbs){
    this.thumbs = thumbs;
  }

  public void setFanarts(ArrayList<Images> fanarts){
    this.fanarts = fanarts;
  }

  public void addThumb(Images thumb){
    thumbs.add(thumb);
  }

  public void addFanart(Images fanart){
    fanarts.add(fanart);
  }

  public void clearThumbs(){
    thumbs.clear();
  }

  public void clearFanarts(){
    fanarts.clear();
  }

  @Override
  public String toString(){
    String res = "Thumbnails :\n  ";
    res += Utils.arrayToString(thumbs.toArray(),"\n  " , 0);
    res += "\nFanarts :\n  ";
    res += Utils.arrayToString(fanarts.toArray(),"\n  " , 0);
   return res;
  }
}
