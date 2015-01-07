/*
 * Movie Renamer
 * Copyright (C) 2014 Nicolas Magré
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
package fr.free.movierenamer.ui.bean;

import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.MediaInfo;
import java.util.List;

/**
 * Class UIMediaInfo
 *
 * @author Nicolas Magré
 */
public class UIMediaInfo<T extends MediaInfo> implements UIInfo {

  protected T info;

  public UIMediaInfo(T info) {
    this.info = info;
  }

  public void setInfo(T info) {
    this.info = info;
  }

  public void setIdsInfo(List<IdInfo> ids) {
    info.setIdsInfo(ids);
  }

  public List<IdInfo> getIds() {
    return info.getIdsInfo();
  }

  public String getTitle() {
    return info.getTitle();
  }

  public Integer getYear() {
    return info.getYear();
  }

  public Double getRating() {
    return info.getRating();
  }

  public MediaInfo getInfo() {
    return info;
  }

}
