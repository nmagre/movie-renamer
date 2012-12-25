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

import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.scrapper.MediaScrapper;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.searchinfo.SearchResult;
import fr.free.movierenamer.ui.res.UIFile;
import fr.free.movierenamer.ui.res.UISearchResult;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.utils.ImageUtils;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.Icon;

/**
 * Class SearchMediaWorker
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaWorker extends AbstractWorker<List<UISearchResult>> {

  private final UIFile media;
  private final MediaScrapper<? extends SearchResult, ? extends MediaInfo> scrapper;
  private final Dimension searchListDim = new Dimension(45, 65);

  /**
   * Constructor arguments
   *
   * @param media
   * @param scrapper
   */
  public SearchMediaWorker(UIFile media, MediaScrapper<? extends SearchResult, ? extends MediaInfo> scrapper) {
    super();
    this.media = media;
    this.scrapper = scrapper;
  }

  @Override
  public List<UISearchResult> executeInBackground() throws Exception {
    List<UISearchResult> results = new ArrayList<UISearchResult>();
    List<? extends Media> res;

    if (media != null && scrapper != null) {
      String search = media.getSearch();
      res = (List<? extends Media>) scrapper.search(search);
      int count = res.size();

      for (int i = 0; i < count; i++) {
        if (isCancelled()) {
          UISettings.LOGGER.log(Level.INFO, "SearchMediaWorker Cancelled");
          return new ArrayList<UISearchResult>();
        }
        Icon icon = ImageUtils.getIcon(res.get(i).getURL(), searchListDim, "ui/nothumb.png");
        results.add(new UISearchResult(res.get(i), scrapper, icon));
      }
    }

    

    return results;
  }
}
