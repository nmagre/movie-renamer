/*
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

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.VideoInfo;
import fr.free.movierenamer.mediainfo.MediaTag;
import java.util.ArrayList;
import java.util.List;

/**
 * Class UIVideoInfo
 *
 * @author Nicolas Magré
 */
public class UIVideoInfo<T extends VideoInfo> extends UIMediaInfo<T> {

  protected List<UIPersonImage> castings;

  public UIVideoInfo(T info) {
    super(info);
    castings = new ArrayList<>();
    for (CastingInfo casting : info.getCasting()) {
      castings.add(new UIPersonImage(casting));
    }
  }

  public void setMediaTag(MediaTag mediaTag) {
    ((T) info).setMediaTag(mediaTag);
  }

  @Override
  public void setInfo(T info) {
    super.setInfo(info);

    for (CastingInfo casting : info.getCasting()) {
      castings.add(new UIPersonImage(casting));
    }
  }

  public List<UIPersonImage> getCasting() {
    return castings;
  }

}
