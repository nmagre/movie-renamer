/*
 * movie-renamer-core
 * Copyright (C) 2013 Nicolas Magré
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

import java.net.URL;

/**
 * Class TrailerInfo
 *
 * @author Nicolas Magré
 */
public class TrailerInfo extends Info {

  private final String title;
  private final String description;
  private final URL thumb;
  private final URL streamUrl;

  public TrailerInfo(final String title, final String description, final URL thumb, final URL streamUrl) {
    this.title = title;
    this.description = description;
    this.thumb = thumb;
    this.streamUrl = streamUrl;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public URL getThumb() {
    return thumb;
  }

  public URL getStreamUrl() {
    return streamUrl;
  }

}
