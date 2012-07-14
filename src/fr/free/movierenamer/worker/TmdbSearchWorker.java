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
package fr.free.movierenamer.worker;

import fr.free.movierenamer.parser.xml.MrParser;
import fr.free.movierenamer.parser.xml.TmdbSearch;
import fr.free.movierenamer.utils.SearchResult;
import fr.free.movierenamer.utils.Settings;
import java.net.URLEncoder;
import java.util.ArrayList;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.xml.bind.DatatypeConverter;

/**
 * Class TmdbSearchWorker
 * 
 * @author Nicolas Magré
 */
public class TmdbSearchWorker extends MovieSearchWorker {

  public TmdbSearchWorker(SwingPropertyChangeSupport errorSupport, String searchTitle) {
    super(errorSupport, searchTitle);
  }

  @Override
  protected String getSearchUri() throws Exception {
    String uri = Settings.tmdbAPISearchUrl + new String(DatatypeConverter.parseBase64Binary(Settings.xurlMdb)) + "/" + URLEncoder.encode(searchTitle, "UTF-8");
    if (config.movieScrapperFR) {
      uri = uri.replace("/en/", "/fr/");
    }
    return uri;
  }

  @Override
  protected MrParser<ArrayList<SearchResult>> getSearchParser() throws Exception {
    return new TmdbSearch();
  }

  // @Override
  // protected ArrayList<SearchResult> performSearch(ArrayList<SearchResult> tmdbSearchResult) throws Exception {
  // for (SearchResult tmdbres : tmdbSearchResult) {
  // String thumb = tmdbres.getThumb();
  // if (thumb != null) {
  // Icon icon = Utils.getSearchThumb(thumb, new Dimension(45, 70));
  // if (icon != null) {
  // tmdbres.setIcon(icon);
  // }
  // }
  // if (tmdbres.getIcon() == null) {
  // tmdbres.setIcon(new ImageIcon(Utils.getImageFromJAR("/image/nothumb.png", getClass())));
  // }
  // }
  // return tmdbSearchResult;
  // }

}
