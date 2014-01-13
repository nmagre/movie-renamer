/*
 * Movie Renamer
 * Copyright (C) 2012-2014 Nicolas Magré
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
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.scrapper.SearchScrapper;
import fr.free.movierenamer.searchinfo.Hyperlink;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.utils.Sorter;
import java.awt.Dimension;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import javax.swing.Icon;

/**
 * Class UISearchResult
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class UISearchResult extends Sorter.ISort implements IImage {

  private final Media searchResult;
  private final SearchScrapper<? extends Hyperlink> scrapper;
  private Icon icon = ImageUtils.LOAD_24;

  /**
   * @param searchResult
   * @param scrapper
   */
  public UISearchResult(Media searchResult, SearchScrapper<? extends Hyperlink> scrapper) {
    this.searchResult = searchResult;
    this.scrapper = scrapper;
  }

  @Override
  public Icon getIcon() {
    return icon;
  }

  public SearchScrapper<? extends Hyperlink> getScrapper() {
    return scrapper;
  }

  public Media getSearchResult() {
    return searchResult;
  }

  @Override
  public URI getUri(ImageInfo.ImageSize size) {// FIXME size ???
    if (searchResult.getURL() != null) {
      try {
        return searchResult.getURL().toURI();
      } catch (URISyntaxException ex) {
        UISettings.LOGGER.log(Level.WARNING, null, ex);
      }
    }
    return null;
  }

  @Override
  public int getYear() {
    return searchResult.getYear();
  }

  @Override
  public void setIcon(Icon icon) {
    this.icon = icon;
  }

  @Override
  public String getName() {
    return searchResult.getName();
  }

  @Override
  public String getOriginalTitle() {
    return searchResult.getOriginalTitle();
  }

  @Override
  public long getLength() {
    return getName().length();
  }

  @Override
  protected boolean hasImage() {
    return getUri(null) != null;
  }

  @Override
  public String toString() {
    String toString = getName();

    return toString;
  }

  public String print(boolean showId, boolean showYear) {
    String toString = getName();

    if (showYear) {
      toString += " (" + searchResult.getYear() + ")";
    }

    if (showId) {
      IdInfo id = searchResult.getMediaId();
      if (id == null) {
        id = searchResult.getImdbId();
      }
      toString += " (id:" + id + ")";
    }

    return toString;
  }

  @Override
  public int getId() {
    IdInfo id = searchResult.getMediaId();
    if (id != null) {
      return id.getId();
    }

    id = searchResult.getImdbId();
    if (id != null) {
      return id.getId();
    }

    return -1;
  }
}
