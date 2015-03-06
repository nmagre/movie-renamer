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

import fr.free.movierenamer.scraper.TrailerScraper;
import fr.free.movierenamer.searchinfo.Trailer;
import java.net.URL;

/**
 * UISearchTrailerResult
 *
 * @author Nicolas Magré
 */
public class UISearchTrailerResult extends AbstractSearchResult<Trailer, TrailerScraper> {

  public UISearchTrailerResult(Trailer result, TrailerScraper scraper) {
    super(result, scraper);
  }
  
  public String getDuration() {
    return result.getRuntime();
  }
  
  public String getProvider() {
    return result.getProviderName();
  }
  
  public URL getTrailerUrl() {
    return result.getTrailerUrl();
  }

}
