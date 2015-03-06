/*
 * Copyright (C) 2013-2014 Nicolas Magré
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

import fr.free.movierenamer.scraper.impl.trailer.UniversalTrailerScraper;
import fr.free.movierenamer.searchinfo.Trailer;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UISearchResult;
import fr.free.movierenamer.ui.bean.UISearchTrailerResult;
import fr.free.movierenamer.ui.swing.panel.info.InfoPanel;
import fr.free.movierenamer.ui.swing.panel.info.TrailerInfoPanel;
import fr.free.movierenamer.ui.worker.Worker;
import fr.free.movierenamer.utils.Sorter;
import fr.free.movierenamer.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Class SearchMediaTrailerWorker
 *
 * @author Nicolas Magré
 */
public class SearchMediaTrailerWorker extends Worker<List<UISearchTrailerResult>> {

  private final UISearchResult searchResult;

  public SearchMediaTrailerWorker(MovieRenamer mr, UISearchResult searchResult) {
    super(mr);
    this.searchResult = searchResult;
  }

  @Override
  public List<UISearchTrailerResult> executeInBackground() throws Exception {
    List<UISearchTrailerResult> trailers = new ArrayList<>();
    UniversalTrailerScraper scraper = new UniversalTrailerScraper();
    List<Trailer> trailersInfo = scraper.getTrailer(searchResult.getSearchResult());

    String title = searchResult.getOriginalName();
    if (title == null) {
      title = searchResult.getName();
    }
    title = StringUtils.normaliseClean(title);

    for (Trailer trailer : trailersInfo) {
      System.out.println(trailer);
      trailers.add(new UISearchTrailerResult(trailer, scraper));
    }

    Sorter.sort(trailers, title);
    TrailerInfoPanel panel = (TrailerInfoPanel) mr.getMediaPanel().getPanel(InfoPanel.PanelType.TRAILER_INFO);// FIXME
    if (panel != null) {
      panel.addTrailers(trailers);
    }

    return trailers;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void workerDone() throws Exception {
    List<UISearchTrailerResult> trailers = get();
//    TrailerInfoPanel panel = (TrailerInfoPanel) mr.getMediaPanel().getPanel(InfoPanel.PanelType.TRAILER_INFO);
//    if (panel != null) {
//      panel.addTrailers(trailers);
//    }
  }

  @Override
  public String getDisplayName() {
    return ("Trailer");// FIXME i18n
  }

  @Override
  public WorkerId getWorkerId() {
    return WorkerId.SEARCH_TRAILER;
  }
}
