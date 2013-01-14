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
import fr.free.movierenamer.ui.list.UIMediaImage;
import fr.free.movierenamer.ui.list.UISearchResult;
import java.util.ArrayList;
import java.util.List;

/**
 * Class SearchMediaImagesWorker
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaImagesWorker extends AbstractWorker<List<UIMediaImage>> {

  private final UISearchResult searchResult;
  private final MediaScrapper<Media, MediaInfo> scrapper;

  /**
   * Constructor arguments
   *
   * @param searchResult
   */
  @SuppressWarnings("unchecked")
  public SearchMediaImagesWorker(UISearchResult searchResult) {
    super();
    this.searchResult = searchResult;
    this.scrapper = (searchResult != null) ? (MediaScrapper<Media, MediaInfo>) searchResult.getScrapper() : null;
  }

  @Override
  public List<UIMediaImage> executeInBackground() throws Exception {
    List<ImageInfo> infos;
    List<UIMediaImage> mediaImages = new ArrayList<UIMediaImage>();

    Media media = searchResult.getSearchResult();
    if (searchResult != null && scrapper != null && media != null) {

      infos = scrapper.getImages(media);
      int count = infos.size();
      for (int i = 0; i < count; i++) {
        if (isCancelled()) {
          return new ArrayList<UIMediaImage>();
        }

        mediaImages.add(new UIMediaImage(infos.get(i), null));
      }
    }

    return mediaImages;
  }
}
