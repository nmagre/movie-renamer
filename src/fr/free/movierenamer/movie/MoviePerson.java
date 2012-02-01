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

import java.util.ArrayList;

/**
 * Class MoviePerson
 * @author duffy
 */
public class MoviePerson {
  private String name;
  private String thumb;
  private String job;// Actor, Realisator
  private ArrayList<String> roles;// just for actor

  /**
   * Constructor arguments
   * @param name Person name
   * @param thumb Person thumbnail
   * @param job Person job (actor,director,writer,...)
   */
  public MoviePerson(String name, String thumb, String job){
    this.name = name;
    this.thumb = thumb;
    this.job = job;
    roles = new ArrayList<String>();
  }

  /**
   * Get name
   * @return Person name
   */
  public String getName(){
    return name;
  }

  /**
   * Get thumbnail
   * @return Person thumbnail
   */
  public String getThumb(){
    return thumb;
  }
  
  /**
   * Get job
   * @return Person job
   */
  public String getJob(){
    return job;
  }
  
  /**
   * Get roles
   * @return Person movie roles
   */
  public ArrayList<String> getRoles(){
    return roles;
  }

  /**
   * Add role (only for actors)
   * @param role Role
   */
  public void addRole(String role) {
    roles.add(role);
  }
}
