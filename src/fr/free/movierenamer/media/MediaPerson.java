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

import fr.free.movierenamer.utils.ActionNotValidException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class MediaPerson
 *
 * @author Nicolas Magré
 */
public class MediaPerson {

  public static final int ACTOR = 0;
  public static final int DIRECTOR = 1;
  public static final int WRITER = 2;
  private String name;
  private String thumb;
  private int job;// Actor, director, writer
  private List<String> roles;// just for actor
  private String imdbId;//Only use for media portal

  public MediaPerson(int job) {
    name = "";
    thumb = "";
    this.job = job;
    imdbId = "";
    roles = new ArrayList<String>();
  }

  /**
   * Constructor arguments
   *
   * @param name Person name
   * @param thumb Person thumbnail
   * @param job Person job (actor,director,writer,...)
   */
  public MediaPerson(String name, String thumb, int job) {
    this.name = name;
    this.thumb = thumb;
    this.job = job;
    imdbId = "";
    roles = new ArrayList<String>();
  }

  /**
   * Get name
   *
   * @return Person name
   */
  public String getName() {
    return name;
  }

  /**
   * Get thumbnail
   *
   * @return Person thumbnail
   */
  public String getThumb() {
    return thumb;
  }

  /**
   * Get job
   *
   * @return Person job
   */
  public int getJob() {
    return job;
  }

  /**
   * Get roles
   *
   * @return Person movie roles
   */
  public List<String> getRoles() {
    return roles;
  }

  /**
   * Get imdb ID
   *
   * @return Imdb ID
   */
  public String getImdbId() {
    return imdbId;
  }

  /**
   * Set name
   *
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Set imdb ID
   *
   * @param imdbId
   */
  public void setImdbId(String imdbId) {
    this.imdbId = imdbId;
  }

  /**
   * Set thumb
   *
   * @param thumb
   */
  public void setThumb(String thumb) {
    this.thumb = thumb;
  }

  /**
   * Add role (only for actors)
   *
   * @param role Role
   * @throws ActionNotValidException
   */
  public void addRole(String role) throws ActionNotValidException {
    if (job != ACTOR) {
      throw new ActionNotValidException("Only actor can have a role");
    } else {
      roles.add(role);
    }
  }

  @Override
  public String toString() {
    return name;
  }
}
