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
package fr.free.movierenamer.ui.worker;

import fr.free.movierenamer.scrapper.SubtitleScrapper;
import fr.free.movierenamer.searchinfo.Subtitle;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.list.UIFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Class SearchMediaSubtitlesWorker
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaSubtitlesWorker extends AbstractWorker<List<Subtitle>> {// TODO

  private final UIFile media;
  private final SubtitleScrapper scrapper;

  /**
   * Constructor arguments
   *
   * @param mr
   * @param media
   * @param scrapper
   */
  public SearchMediaSubtitlesWorker(MovieRenamer mr, UIFile media, SubtitleScrapper scrapper) {
    super(mr);
    this.media = media;
    this.scrapper = scrapper;
  }

  @Override
  public List<Subtitle> executeInBackground() throws Exception {
    List<Subtitle> results = new ArrayList<Subtitle>();

    if (media != null && scrapper != null) {
      String search = media.getSearch();
      results = scrapper.search(search);
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
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
