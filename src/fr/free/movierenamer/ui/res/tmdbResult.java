package fr.free.movierenamer.ui.res;

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



import java.util.ArrayList;
import javax.swing.Icon;
import fr.free.movierenamer.movie.MovieImage;

/**
 *
 * @author duffy
 */
public class tmdbResult implements IIconList{

  private String tmdbAPIID;
  private ArrayList<MovieImage> thumbs;
  private ArrayList<MovieImage> fanarts;
  private Icon icon;

  public tmdbResult(String tmdbAPIID, ArrayList<MovieImage> thumbs, ArrayList<MovieImage> fanarts){
    this.tmdbAPIID = tmdbAPIID;
    this.thumbs = thumbs;
    this.fanarts = fanarts;
    this.icon = null;
  }

  public String getId(){
    return tmdbAPIID;
  }

  public ArrayList<MovieImage> getThumbs(){
    return thumbs;
  }

  public ArrayList<MovieImage> getFanarts(){
    return fanarts;
  }

  @Override
  public Icon getIcon() {
    return icon;
  }
}
