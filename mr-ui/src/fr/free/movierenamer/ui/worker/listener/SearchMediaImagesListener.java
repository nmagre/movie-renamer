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
package fr.free.movierenamer.ui.worker.listener;

import com.alee.laf.list.DefaultListModel;
import com.alee.laf.list.WebList;
import fr.free.movierenamer.info.ImageInfo.ImageCategoryProperty;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.panel.MediaPanel;
import fr.free.movierenamer.ui.list.UIMediaImage;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.ui.worker.ImageWorker;
import fr.free.movierenamer.ui.worker.SearchMediaImagesWorker;
import java.awt.Dimension;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * Class SearchMediaImagesListener
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaImagesListener extends AbstractListener<List<UIMediaImage>> {

  private static MediaPanel mediapanel;
  private static final Dimension thumbDim = new Dimension(120, 200);
  private static final Dimension fanartDim = new Dimension(200, 160);

  public SearchMediaImagesListener(SearchMediaImagesWorker worker, MovieRenamer mr, final MediaPanel ipanel) {
    super(mr, worker);
    mediapanel = ipanel;
  }

  private enum UIImageCategoryProperty {

    thumb(mediapanel.getThumbnailsList(), mediapanel.getThumbnailsModel(), ImageCategoryProperty.thumb),
    //fanart(mediapanel.getFanartsList()., mediapanel.getFanartsModel(), ImageCategoryProperty.fanart),
    banner(mediapanel.getBannersList(), mediapanel.getBannersModel(), ImageCategoryProperty.banner),
    cdart(mediapanel.getCdartsList(), mediapanel.getCdartsModel(), ImageCategoryProperty.cdart),
    logo(mediapanel.getLogosList(), mediapanel.getLogosModel(), ImageCategoryProperty.logo),
    clearart(mediapanel.getClearartsList(), mediapanel.getClearartsModel(), ImageCategoryProperty.clearart);
    private final WebList list;
    private final DefaultListModel model;
    private Dimension dim;
    private final ImageCategoryProperty imgProperty;
    private List<URI> urls = new ArrayList<URI>();

    private UIImageCategoryProperty(WebList list, DefaultListModel model, ImageCategoryProperty imgProperty) {
      this.list = list;
      this.model = model;
      this.imgProperty = imgProperty;
      this.dim = thumbDim;
    }

    private UIImageCategoryProperty(WebList list, DefaultListModel model, ImageCategoryProperty imgProperty, Dimension dim) {
      this(list, model, imgProperty);
      this.dim = dim;
    }

    public void addMediaImage(UIMediaImage image) {
      try {
        urls.add(image.getUrl().toURI());
        image.setIcon(UIUtils.getAnimatedLoader(list, model.getSize()));
        model.addElement(image);
      } catch (URISyntaxException ex) {
        UISettings.LOGGER.log(Level.WARNING, ex.getMessage());
      }
    }

    public List<URI> getUrls() {
      return Collections.unmodifiableList(urls);
    }

    public DefaultListModel getModel() {
      return model;
    }

    public ImageCategoryProperty getImageProperty() {
      return imgProperty;
    }

    public Dimension getDimension() {
      return dim;
    }
  }

  @Override
  protected void done() throws Exception {

    List<UIMediaImage> images = worker.get();
    if(images == null) {
      return;
    }

    for (UIMediaImage image : images) {
      addMediaImage(image);
    }

    for (UIImageCategoryProperty key : UIImageCategoryProperty.values()) {
      ImageWorker<UIMediaImage> imageWorker = new ImageWorker<UIMediaImage>(key.getUrls(), key.getModel(), key.getDimension(), null);
      imageWorker.execute();
      mr.addWorker(imageWorker);
    }
  }

  private void addMediaImage(UIMediaImage image) {
    UIImageCategoryProperty property = getProperty(image.getType());
    if (property != null && property.getModel() != null) {
      property.addMediaImage(image);
    }
  }

  private UIImageCategoryProperty getProperty(ImageCategoryProperty key) {
    for (UIImageCategoryProperty property : UIImageCategoryProperty.values()) {
      if (property.getImageProperty().equals(key)) {
        return property;
      }
    }
    return null;
  }
}
