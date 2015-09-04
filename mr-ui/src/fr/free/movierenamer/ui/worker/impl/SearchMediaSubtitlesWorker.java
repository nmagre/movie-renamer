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
package fr.free.movierenamer.ui.worker.impl;

import fr.free.movierenamer.scraper.SubtitleScraper;
import fr.free.movierenamer.searchinfo.Subtitle;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.ui.worker.Worker;
import java.util.ArrayList;
import java.util.List;

/**
 * Class SearchMediaSubtitlesWorker
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaSubtitlesWorker extends Worker<List<Subtitle>> {// TODO

  private final UIFile media;
  private final SubtitleScraper scraper;

  /**
   * Constructor arguments
   *
   * @param mr
   * @param media
   * @param scraper
   */
  public SearchMediaSubtitlesWorker(MovieRenamer mr, UIFile media, SubtitleScraper scraper) {
    super(mr);
    this.media = media;
    this.scraper = scraper;
  }

  @Override
  public List<Subtitle> executeInBackground() throws Exception {
    List<Subtitle> results = new ArrayList<>();

    if (media != null && scraper != null) {
      String search = media.getSearch();
      results = scraper.search(search, 0);
      int count = results.size();
      for (int i = 0; i < count; i++) {
        if (isCancelled()) {
          return results;
        }
        double progress = (i + 1) / (double) count;
        setProgress((int) (progress * 100));
      }
    }

    return results;
  }

  @Override
  protected void workerDone() throws Exception {
    // TODO
  }

  @Override
  public String getDisplayName() {
    return ("worker.searchSubtitle");// FIXME i18n
  }

  @Override
  public WorkerId getWorkerId() {
    return WorkerId.SEARCH_SUBTITLE;
  }
}
