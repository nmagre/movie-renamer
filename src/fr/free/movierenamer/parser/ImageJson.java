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
package fr.free.movierenamer.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Nicolas Magré
 */
public class ImageJson {

  private int id;
  private List<Map<String, Object>> posters;
  private List<Map<String, Object>> backdrops;

  public ImageJson() {
    posters = new ArrayList<Map<String, Object>>();
    backdrops = new ArrayList<Map<String, Object>>();
    id = 0;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public List<Map<String, Object>> getBackdrops() {
    return backdrops;
  }

  public void setBackdrops(List<Map<String, Object>> backdrops) {
    this.backdrops = backdrops;
  }

  public List<Map<String, Object>> getPosters() {
    return posters;
  }

  public void setPosters(List<Map<String, Object>> posters) {
    this.posters = posters;
  }
}
