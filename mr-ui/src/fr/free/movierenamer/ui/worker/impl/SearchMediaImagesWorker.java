/*
 * Movie Renamer
 * Copyright (C) 2012-2013 Nicolas Magré
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

import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.scrapper.MediaScrapper;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIMediaImage;
import fr.free.movierenamer.ui.bean.UISearchResult;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.swing.UIManager;
import fr.free.movierenamer.ui.swing.panel.ImagePanel;
import fr.free.movierenamer.ui.worker.Worker;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Class SearchMediaImagesWorker
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaImagesWorker extends Worker<List<UIMediaImage>> {

  private final UISearchResult searchResult;
  private final MediaScrapper<Media, MediaInfo> scrapper;
  private List<ImageInfo> infos;

  /**
   * Constructor arguments
   *
   * @param mr
   * @param searchResult
   */
  @SuppressWarnings("unchecked")
  public SearchMediaImagesWorker(MovieRenamer mr, UISearchResult searchResult) {
    super(mr);
    this.searchResult = searchResult;
    this.scrapper = (searchResult != null) ? (MediaScrapper<Media, MediaInfo>) searchResult.getScrapper() : null;
  }

  @Override
  public List<UIMediaImage> executeInBackground() throws Exception {

    List<UIMediaImage> mediaImages = new ArrayList<>();

    if (searchResult == null) {
      return mediaImages;
    }

    Media media = searchResult.getSearchResult();
    if (scrapper != null && media != null) {
      infos = scrapper.getImages(media);
      if (infos != null) {
        int count = infos.size();
        for (int i = 0; i < count; i++) {
          if (isCancelled()) {
            UISettings.LOGGER.log(Level.INFO, "SearchMediaImagesWorker Cancelled");
            return new ArrayList<>();
          }

          mediaImages.add(new UIMediaImage(infos.get(i)));
        }
      }
    }

    return mediaImages;
  }

  private List<UIMediaImage> getImagesByType(List<UIMediaImage> images, ImageInfo.ImageCategoryProperty property) {
    List<UIMediaImage> res = new ArrayList<>();
    for (UIMediaImage image : images) {
      if (image.getType().equals(property)) {
        res.add(image);
      }
    }

    return res;
  }

  @Override
  protected void workerDone() throws Exception {
    List<UIMediaImage> images = get();

    if (images == null) {
      return;
    }

    ImagePanel panel = UIManager.getImagePanel();
    for (ImageInfo.ImageCategoryProperty key : panel.getSupportedImages()) {
      List<UIMediaImage> mimages = getImagesByType(images, key);
      panel.addImages(mimages, key);
    }
    panel.enabledListener();

    if (infos != null) {
      mr.setImageInfo(infos);
    }

    mr.setRenamebuttonEnabled();
  }

  @Override
  public String getParam() {
    return String.format("[%s]", searchResult);
  }

  @Override
  public String getDisplayName() {
    return ("worker.searchImages");// FIXME i18n
  }
}
