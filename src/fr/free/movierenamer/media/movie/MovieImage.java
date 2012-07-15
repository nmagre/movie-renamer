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
package fr.free.movierenamer.media.movie;

import fr.free.movierenamer.media.IMediaImage;
import fr.free.movierenamer.media.MediaImage;
import fr.free.movierenamer.utils.Utils;
import java.util.ArrayList;

/**
 * Class MovieImage
 * @author Magré Nicolas
 */
public class MovieImage implements IMediaImage {
  private ArrayList<MediaImage> thumbs;
  private ArrayList<MediaImage> fanarts;

  public MovieImage(){
    thumbs = new ArrayList<MediaImage>();
    fanarts = new ArrayList<MediaImage>();
  }

  public ArrayList<MediaImage> getThumbs(){
    return thumbs;
  }
  
  public ArrayList<MediaImage> getFanarts(){
    return fanarts;
  }

  public void setThumbs(ArrayList<MediaImage> thumbs){
    this.thumbs = thumbs;
  }

  public void setFanarts(ArrayList<MediaImage> fanarts){
    this.fanarts = fanarts;
  }

  public void addThumb(MediaImage thumb){
    thumbs.add(thumb);
  }

  public void addFanart(MediaImage fanart){
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
