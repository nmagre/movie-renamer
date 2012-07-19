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

import fr.free.movierenamer.parser.ImdbSearch;
import fr.free.movierenamer.parser.MrParser;
import fr.free.movierenamer.utils.SearchResult;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.worker.MovieSearchWorker;
import java.beans.PropertyChangeSupport;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Class ImdbSearchWorker, Search on imdb
 * 
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
public class ImdbSearchWorker extends MovieSearchWorker {

  /**
   * Constructor arguments
   * 
   * @param errorSupport
   *          Swing change support
   * @param searchTitle
   *          Movie title to search
   */
  public ImdbSearchWorker(PropertyChangeSupport errorSupport, String searchTitle) {
    super(errorSupport, searchTitle);
  }

  @Override
  protected String getUri() throws Exception {
    return Settings.imdbSearchUrl + URLEncoder.encode(searchTitle, "ISO-8859-1");
  }

  @Override
  protected MrParser<ArrayList<SearchResult>> getParser() throws Exception {
    return new ImdbSearch(getUrl());
  }

  // @Override
  // protected ArrayList<SearchResult> performSearch(ArrayList<SearchResult> imdbSearchResult) throws Exception {
  // for (SearchResult imsres : imdbSearchResult) {
  // String thumb = imsres.getThumb();
  // if (thumb != null) {
  // Icon icon = Utils.getSearchThumb(thumb, new Dimension(45, 70));
  // if (icon != null) {
  // imsres.setIcon(icon);
  // }
  // }
  // if (imsres.getIcon() == null) {
  // imsres.setIcon(new ImageIcon(Utils.getImageFromJAR("/image/nothumb.png", getClass())));
  // }
  // // setProgress((30 + (++i * 70)) / imdbSearchResult.size());
  // }
  // return imdbSearchResult;
  // }

  // @Override
  // protected ArrayList<SearchResult> executeInBackground() {
  // ArrayList<SearchResult> imdbSearchResult = null;
  //
  // setProgress(0);
  // String searchres = null;
  // try {
  // http = new HttpGet();
  // searchres = http.sendGetRequest(true, "ISO-8859-15");
  // } catch (Exception e) {
  // Settings.LOGGER.log(Level.SEVERE, e.toString());
  // }
  //
  // if (searchres == null) {
  // firePropertyChange("closeLoadingDial", "httpFailed");
  // return null;
  // }
  //
  // setProgress(30);
  //
  // if (searchres != null && !searchres.contains("<b>No Matches.</b>")) {
  // boolean searchPage = !http.getURL().toString().matches("http://www.imdb.(com|fr)/title/tt\\d+/");
  // try {
  // imdbSearchResult = imdbParser.parse(searchres, searchPage);
  // int i = 0;
  // for (SearchResult imsres : imdbSearchResult) {
  // String thumb = imsres.getThumb();
  // if (thumb != null) {
  // Icon icon = Utils.getSearchThumb(thumb, config.cache, new Dimension(45, 70));
  // if (icon != null) {
  // imsres.setIcon(icon);
  // }
  // }
  // if (imsres.getIcon() == null) {
  // imsres.setIcon(new ImageIcon(Utils.getImageFromJAR("/image/nothumb.png", getClass())));
  // }
  // setProgress((30 + (++i * 70)) / imdbSearchResult.size());
  // }
  // } catch (Exception ex) {
  // Settings.LOGGER.log(Level.SEVERE, Utils.getStackTrace("Exception", ex.getStackTrace()));
  // }
  // }
  //
  // if (imdbSearchResult == null) {
  // firePropertyChange("closeLoadingDial", "scrapperSeachFailed");
  // return null;
  // }
  //
  // setProgress(100);
  // Settings.LOGGER.log(Level.INFO, "found : {0} Movies", imdbSearchResult.size());
  // return imdbSearchResult;
  // }
}
