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

import fr.free.movierenamer.info.TrailerInfo;
import fr.free.movierenamer.scrapper.impl.trailer.VideoDetectiveScrapper;
import fr.free.movierenamer.scrapper.impl.trailer.YoutubeScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UISearchResult;
import fr.free.movierenamer.ui.bean.UITrailer;
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
public class SearchMediaTrailerWorker extends Worker<List<UITrailer>> {

  private final UISearchResult searchResult;

  public SearchMediaTrailerWorker(MovieRenamer mr, UISearchResult searchResult) {
    super(mr);
    this.searchResult = searchResult;
  }

  @Override
  public List<UITrailer> executeInBackground() throws Exception {
    List<UITrailer> trailers = new ArrayList<>();
    VideoDetectiveScrapper viddec = new VideoDetectiveScrapper();
    YoutubeScrapper scrapper = new YoutubeScrapper();
    List<TrailerInfo> trailersInfo;
    trailersInfo = viddec.getTrailer((Movie) searchResult.getSearchResult());// FIXME cast
    if (trailersInfo == null) {
      trailersInfo = new ArrayList<>();
    }
    trailersInfo.addAll(scrapper.getTrailer((Movie) searchResult.getSearchResult()));// FIXME cast

    String title = searchResult.getOriginalTitle();
    if (title == null) {
      title = searchResult.getName();
    }
    title = StringUtils.normaliseClean(title);

    String trailerTitle;
    for (TrailerInfo trailer : trailersInfo) {
      trailerTitle = trailer.getTitle();
      trailerTitle = StringUtils.normaliseClean(trailerTitle);
      if (trailerTitle.contains(title)) {
        trailers.add(new UITrailer(trailer));
      }
    }

    Sorter.sort(trailers, title);

    return trailers;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void workerDone() throws Exception {
    List<UITrailer> trailers = get();
//    TrailerInfoPanel panel = (TrailerInfoPanel) mr.getMediaPanel().getPanel(InfoPanel.PanelType.TRAILER_INFO);
//    if (panel != null) {
//      panel.addTrailers(trailers);
//    }
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
