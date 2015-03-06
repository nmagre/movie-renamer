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
import fr.free.movierenamer.scraper.SearchScraper;
import fr.free.movierenamer.searchinfo.Hyperlink;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.searchinfo.Media.MediaType;

/**
 * Class UISearchResult
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class UISearchResult extends AbstractSearchResult<Media, SearchScraper<? extends Hyperlink>> {

  /**
   * @param searchResult
   * @param scraper
   */
  public UISearchResult(Media searchResult, SearchScraper<? extends Hyperlink> scraper) {
    super(searchResult, scraper);
  }

  public String print(boolean showId, boolean showYear) {
    String toString = getName();

    if (showYear && result.getYear() > 0) {
      toString += " (" + result.getYear() + ")";
    }

    if (showId) {
      IdInfo id = result.getMediaId();
      if (id != null) {
        toString += " (id:" + id + ")";
      }
    }

    return toString;
  }

  public MediaType getMediaType() {
    return result.getMediaType();
  }

  @Override
  public int getId() {
    IdInfo id = result.getMediaId();
    if (id != null) {
      return id.getId();
    }

    return -1;
  }
}
