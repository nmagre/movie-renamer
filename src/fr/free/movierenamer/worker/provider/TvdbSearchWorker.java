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

import fr.free.movierenamer.parser.MrParser;
import fr.free.movierenamer.parser.TvdbSearch;
import fr.free.movierenamer.utils.SearchResult;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.worker.TvShowSearchWorker;
import java.beans.PropertyChangeSupport;
import java.net.URLEncoder;
import java.util.List;

/**
 * Class TvdbSearchWorker ,Search tv Show on tvdb
 *
 * @author Nicolas Magre
 */
public class TvdbSearchWorker extends TvShowSearchWorker {

  public TvdbSearchWorker(PropertyChangeSupport errorSupport, String tvShowName) {
    super(errorSupport, tvShowName);
  }

  @Override
  protected String getUri() throws Exception {// FIXME language
    return Settings.tvdbAPIUrlTvShow + "GetSeries.php?language=" + (true ? "fr" : "en") + "&seriesname=" + URLEncoder.encode(searchTitle, "UTF-8");
  }

  @Override
  protected MrParser<List<SearchResult>> getParser() throws Exception {// FIXME language
    return new TvdbSearch(true);
  }

  // @Override
  // protected ArrayList<SearchResult> performSearch(ArrayList<SearchResult> tvdbSearchResult) throws Exception {
  // for (SearchResult res : tvdbSearchResult) {
  // String thumb = res.getThumb();
  // if (thumb != null) {
  // Icon icon = Utils.getSearchThumb(thumb, new Dimension(200, 70));
  // if (icon != null) {
  // res.setIcon(icon);
  // }
  // }
  // if (res.getIcon() == null) {
  // res.setIcon(new ImageIcon(Utils.getImageFromJAR("/image/icon-48.png", getClass())));
  // }
  // }
  //
  // return tvdbSearchResult;
  // }

}
