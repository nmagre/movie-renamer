/*
 * Movie Renamer
 * Copyright (C) 2012-2013 Nicolas Magré
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

import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.scrapper.MediaScrapper;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.utils.Sorter;
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
public class UISearchResult extends Sorter.ISort implements IIconList {

  private final Media searchResult;
  private final MediaScrapper<? extends Media, ? extends MediaInfo> scrapper;
  private Icon icon = ImageUtils.LOAD_24;
  private boolean showYear = true;
  private boolean showId = true;

  /**
   * @param searchResult
   * @param scrapper
   */
  public UISearchResult(Media searchResult, MediaScrapper<? extends Media, ? extends MediaInfo> scrapper) {
    this.searchResult = searchResult;
    this.scrapper = scrapper;
  }

  @Override
  public Icon getIcon() {
    return icon;
  }

  public MediaScrapper<? extends Media, ? extends MediaInfo> getScrapper() {
    return scrapper;
  }

  public Media getSearchResult() {
    return searchResult;
  }

  public void showYear(boolean showYear) {
    this.showYear = showYear;
  }

  public void showId(boolean showId) {
    this.showId = showId;
  }

  @Override
  public URI getUri(ImageInfo.ImageSize size) {
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

  public String getOriginalTitle() {
    return searchResult.getOriginalTitle();
  }

  @Override
  public long getLength() {
    return getName().length();
  }

  @Override
  public String toString() {
    String toString = getName();
    if (showYear) {
      toString += " (" + searchResult.getYear() + ")";
    }

    if (showId) {
      toString += " (id:" + searchResult.getMediaId() + ")";
    }
    return toString;
  }
}
