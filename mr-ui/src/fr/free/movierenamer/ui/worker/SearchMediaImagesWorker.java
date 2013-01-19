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

import com.alee.laf.list.DefaultListModel;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.scrapper.MediaScrapper;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.list.UIMediaImage;
import fr.free.movierenamer.ui.list.UISearchResult;
import fr.free.movierenamer.ui.panel.GalleryPanel;
import fr.free.movierenamer.ui.panel.MediaPanel;
import fr.free.movierenamer.ui.settings.UISettings;
import java.awt.Dimension;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * Class SearchMediaImagesWorker
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaImagesWorker extends AbstractWorker<List<UIMediaImage>> {

  private final UISearchResult searchResult;
  private final MediaScrapper<Media, MediaInfo> scrapper;
  private static MediaPanel mediapanel;
  private static final Dimension thumbDim = new Dimension(120, 200);
  private static final Dimension fanartDim = new Dimension(200, 160);

  /**
   * Constructor arguments
   *
   * @param mr
   * @param searchResult
   */
  @SuppressWarnings("unchecked")
  public SearchMediaImagesWorker(MovieRenamer mr, UISearchResult searchResult) {
    super(mr);
    mediapanel = mr.getCurrentMediaPanel();
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
          UISettings.LOGGER.log(Level.INFO, "SearchMediaImagesWorker Cancelled");
          return new ArrayList<UIMediaImage>();
        }

        mediaImages.add(new UIMediaImage(infos.get(i), null));
      }
    }

    return mediaImages;
  }

  private enum UIImageCategoryProperty {

    thumb(mediapanel.getGalleryPanel(), ImageInfo.ImageCategoryProperty.thumb),
    fanart(mediapanel.getGalleryPanel(), ImageInfo.ImageCategoryProperty.fanart),
    banner(mediapanel.getGalleryPanel(), ImageInfo.ImageCategoryProperty.banner),
    cdart(mediapanel.getGalleryPanel(), ImageInfo.ImageCategoryProperty.cdart),
    logo(mediapanel.getGalleryPanel(), ImageInfo.ImageCategoryProperty.logo),
    clearart(mediapanel.getGalleryPanel(), ImageInfo.ImageCategoryProperty.clearart);
    private final DefaultListModel model;
    private Dimension dim;
    private final ImageInfo.ImageCategoryProperty imgProperty;
    private List<URI> urls = new ArrayList<URI>();
    private final GalleryPanel gallery;

    private UIImageCategoryProperty(DefaultListModel model, ImageInfo.ImageCategoryProperty imgProperty) {
      this.model = model;
      this.imgProperty = imgProperty;
      this.dim = thumbDim;
      gallery = null;
    }

    private UIImageCategoryProperty(GalleryPanel gallery, ImageInfo.ImageCategoryProperty imgProperty) {
      model = null;
      this.gallery = gallery;
      this.imgProperty = imgProperty;
      this.dim = thumbDim;
    }

    private UIImageCategoryProperty(DefaultListModel model, ImageInfo.ImageCategoryProperty imgProperty, Dimension dim) {
      this(model, imgProperty);
      this.dim = dim;
    }

    public List<URI> getUrls() {
      return Collections.unmodifiableList(urls);
    }

    public DefaultListModel getModel() {
      return model;
    }

    public GalleryPanel getGallery() {
      return gallery;
    }

    public ImageInfo.ImageCategoryProperty getImageProperty() {
      return imgProperty;
    }

    public Dimension getDimension() {
      return dim;
    }
  }

  private List<UIMediaImage> getImagesByType(List<UIMediaImage> images, ImageInfo.ImageCategoryProperty property) {
    List<UIMediaImage> res = new ArrayList<UIMediaImage>();
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
    System.out.println("nb images : " + images.size());
    for (UIImageCategoryProperty key : UIImageCategoryProperty.values()) {
      List<UIMediaImage> mimages = getImagesByType(images, key.getImageProperty());
      if (key.getModel() != null) {
        getImages(mimages, key.getModel(), key.getDimension());
      }
      if (key.getGallery() != null) {
        System.out.println("added nb images : " + mimages.size());
        getImages(mimages, key.getGallery(), null);
      }
    }
  }
}
