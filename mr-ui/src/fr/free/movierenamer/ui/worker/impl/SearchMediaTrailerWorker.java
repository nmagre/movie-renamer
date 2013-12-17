/*
 * Copyright (C) 2013 duffy
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
package fr.free.movierenamer.ui.worker.impl;

import fr.free.movierenamer.scrapper.impl.trailer.TrailerAddictScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.searchinfo.Trailer;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UISearchResult;
import fr.free.movierenamer.ui.swing.panel.info.InfoPanel;
import fr.free.movierenamer.ui.swing.panel.info.TrailerInfoPanel;
import fr.free.movierenamer.ui.worker.Worker;
import java.util.List;

/**
 *
 * @author duffy
 */
public class SearchMediaTrailerWorker extends Worker<List<Trailer>> {

  private final UISearchResult searchResult;

  public SearchMediaTrailerWorker(MovieRenamer mr, UISearchResult searchResult) {
    super(mr);
    this.searchResult = searchResult;
  }

  @Override
  public List<Trailer> executeInBackground() throws Exception {
    List<Trailer> trailers;
    TrailerAddictScrapper scrapper = new TrailerAddictScrapper();
    trailers = scrapper.getTrailer((Movie) searchResult.getSearchResult());// FIXME cast
    return trailers;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void workerDone() throws Exception {
    List<Trailer> trailers = get();
    TrailerInfoPanel panel = (TrailerInfoPanel) mr.getMediaPanel().getPanel(InfoPanel.PanelType.TRAILER_INFO);
    panel.addTrailers(trailers);
  }

  @Override
  public String getParam() {
    return "";
  }

  @Override
  public String getDisplayName() {
    return ("Trailer");// FIXME i18n
  }
}
