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
package fr.free.movierenamer.ui.res;

import javax.swing.Icon;

/**
 * Class ImdbSearchResult
 * @author Magré Nicolas
 */
public class ImdbSearchResult implements IIconList {

  private String imdbId;
  private String title;
  private String type;
  private String thumb;
  private Icon icon;

  /**
   * Constructor arguments
   * @param title Imdb title
   * @param imdbId Imdb ID
   * @param type Imdb result type (Exact,...)
   * @param thumb Imdb thumb
   */
  public ImdbSearchResult(String title, String imdbId, String type, String thumb) {
    this.imdbId = imdbId;
    this.title = title;
    this.thumb = thumb;
    this.type = type;
  }

  /**
   * Get imdb title
   * @return imdb title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Get imdb ID
   * @return imdb ID
   */
  public String getImdbId() {
    return imdbId;
  }

  /**
   * Get imdb thumb
   * @return imdb thumb
   */
  public String getThumb() {
    return thumb;
  }

  /**
   * Set icon
   * @param icon
   */
  public void setIcon(Icon icon) {
    this.icon = icon;
  }

  @Override
  public Icon getIcon() {
    return icon;
  }

  @Override
  public String toString() {
    return title + " : " + type;
  }
}
