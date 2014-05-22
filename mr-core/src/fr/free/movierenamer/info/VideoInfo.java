/*
 * movie-renamer-core
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
package fr.free.movierenamer.info;

import fr.free.movierenamer.mediainfo.MediaTag;
import java.util.List;
import java.util.Map;

/**
 * Class MediaInfo
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class VideoInfo extends MediaInfo {

  protected MediaTag mtag;
  
  public VideoInfo(Map<MediaProperty, String> mediaFields, List<IdInfo> idsInfo) {
    super(mediaFields, idsInfo);
  }

  public MediaTag getMediaTag() {
    return mtag;
  }

  public void setMediaTag(final MediaTag mtag) {
    this.mtag = mtag;
  }

}
