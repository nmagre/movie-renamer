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

import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.scrapper.MediaScrapper;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.searchinfo.SearchResult;
import fr.free.movierenamer.ui.res.UIMediaImage;
import fr.free.movierenamer.ui.res.UISearchResult;
import fr.free.movierenamer.ui.utils.ImageUtils;
import java.awt.Dimension;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;

/**
 * Class SearchMediaImagesWorker
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaImagesWorker extends AbstractWorker<List<UIMediaImage>> {

  private final UISearchResult searchResult;
  private final MediaScrapper<Media, MediaInfo> scrapper;
  private final Dimension searchListDim = new Dimension(45, 65);

  /**
   * Constructor arguments
   *
   * @param errorSupport
   * @param searchResult
   * @param scrapper
   */
  @SuppressWarnings("unchecked")
  public SearchMediaImagesWorker(PropertyChangeSupport errorSupport, UISearchResult searchResult, MediaScrapper<? extends SearchResult, ? extends MediaInfo> scrapper) {
    super(errorSupport);
    this.searchResult = searchResult;
    this.scrapper = (MediaScrapper<Media, MediaInfo>) scrapper;
  }

  @Override
  public List<UIMediaImage> executeInBackground() throws Exception {
    List<ImageInfo> infos;
    List<UIMediaImage> mediaImages = new ArrayList<UIMediaImage>();

    if (searchResult != null && scrapper != null) {
      System.out.println("Launch images search");
      Media media = searchResult.getSearchResult();
      infos = scrapper.getImages(media);
      int count = infos.size();
      System.out.println("Images size : " + count);
      for (int i = 0; i < count; i++) {
        if (isCancelled()) {
          return new ArrayList<UIMediaImage>();
        }
        Icon icon = ImageUtils.getIcon(infos.get(i).getHref(), searchListDim, "nothumb.png");
        mediaImages.add(new UIMediaImage(infos.get(i), icon));
        System.out.println("Add " + infos.get(i).getCategory().name() + " : " + infos.get(i).getHref());
        double progress = (i + 1) / (double) count;
        setProgress((int) (progress * 100));
      }
    }

    return mediaImages;
  }
}
