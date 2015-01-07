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
package fr.free.movierenamer.ui.worker;

/**
 * Interface IWorker
 *
 * @author Nicolas Magré
 */
public interface IWorker {

  public enum WorkerId {

    LIST_FILE,
    GET_FILE_INFO,
    SEARCH,
    SEARCH_INFO,
    SEARCH_IMAGE,
    SEARCH_TRAILER,
    SEARCH_SUBTITLE,
    SEARCH_CASTING,
    SEARCH_ID,
    SEARCH_SET,
    RENAME,
    HELP,
    DOWNLOAD,
    UPDATE,
    IMAGE_SEARCH_RESULT,
    IMAGE_INFO_ACTOR,
    IMAGE_INFO_DIRECTOR,
    IMAGE_INFO_TRAILER,
    IMAGE_GALLERY_REMOTE,
    IMAGE_GALLERY_BANNER,
    IMAGE_GALLERY_CDART,
    IMAGE_GALLERY_CLEARART,
    IMAGE_GALLERY_LOGO,
    IMAGE_GALLERY_THUMB,
    IMAGE_GALLERY_FANART
  }

  public abstract WorkerId getWorkerId();
}
