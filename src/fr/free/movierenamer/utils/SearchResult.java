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
package fr.free.movierenamer.utils;

import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.ui.res.IIconList;
import javax.swing.Icon;

/**
 * Class SearchResult
 *
 * @author Magré Nicolas
 */
public class SearchResult implements IIconList {

  public enum SearchResultType {
    NONE,
    EXACT,
    POPULAR,
    PARTIAL,
    APPROXIMATE;

    @Override
    public String toString() {
      // FIXME use internationalization
      return Utils.capitalizedLetter(super.toString(), true);
    }
  }

  private MediaID id;
  private String title;
  private String year;
  private SearchResultType type;
  private String thumb;
  private Icon icon;

  /**
   * Constructor arguments
   *
   * @param title Media title
   * @param id API ID
   * @param type result type (Exact,...)
   * @param year Release year
   * @param thumb Thumbnail
   */
  public SearchResult(String title, MediaID id, SearchResultType type, String year, String thumb) {
    this.id = id;
    this.title = title;
    this.year = year;
    this.thumb = thumb;
    this.type = type;
  }

  /**
   * Get title
   *
   * @return Media Title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Get API ID
   *
   * @return API ID
   */
  public MediaID getId() {
    return id;
  }

  /**
   * Get Thumbnail
   *
   * @return Thumbnail
   */
  public String getThumb() {
    return thumb;
  }

  /**
   * Set media icon
   *
   * @param icon
   */
  public void setIcon(Icon icon) {
    this.icon = icon;
  }

  /**
   * Get media icon
   * @return Media icon
   */
  @Override
  public Icon getIcon() {
    return icon;
  }

  @Override
  public String toString() {
    String myear = "";
    if(!year.equals("")){
      myear = " (" + year + ")";
    }
    if (type == SearchResultType.NONE) {
      return title + myear;
    }

    return title + myear + " : " + type;
  }
}
