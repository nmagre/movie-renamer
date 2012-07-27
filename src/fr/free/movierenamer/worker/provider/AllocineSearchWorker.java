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
package fr.free.movierenamer.worker.provider;

import fr.free.movierenamer.parser.AllocineSearch;
import fr.free.movierenamer.parser.MrParser;
import fr.free.movierenamer.utils.SearchResult;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.worker.MovieSearchWorker;
import java.beans.PropertyChangeSupport;
import java.net.URLEncoder;
import java.util.List;

/**
 * Class AllocineSearchWorker Search movie or tvshow
 * 
 * @author Nicolas Magré
 */
public class AllocineSearchWorker extends MovieSearchWorker {

  public AllocineSearchWorker(PropertyChangeSupport errorSupport, String searchTitle) {
    super(errorSupport, searchTitle);
  }

  @Override
  protected String getUri() throws Exception {
    return Settings.allocineAPISearch.replace("FILTER", "movie") + URLEncoder.encode(searchTitle, "UTF-8");
  }

  @Override
  protected MrParser<List<SearchResult>> getParser() throws Exception {
    return new AllocineSearch();
  }

  // @Override
  // protected ArrayList<SearchResult> performSearch(ArrayList<SearchResult> allocineSearchResult) {
  // for (SearchResult allores : allocineSearchResult) {
  // String thumb = allores.getThumb();
  // if (thumb != null) {
  // Icon icon = Utils.getSearchThumb(thumb, new Dimension(45, 70));
  // if (icon != null) {
  // allores.setIcon(icon);
  // }
  // }
  // if (allores.getIcon() == null) {
  // allores.setIcon(new ImageIcon(Utils.getImageFromJAR("/image/nothumb.png", getClass())));
  // }
  // }
  //
  // return allocineSearchResult;
  // }

}
