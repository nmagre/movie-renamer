/*
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
import fr.free.movierenamer.ui.bean.IIconList;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.utils.ImageUtils;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.Icon;
import javax.swing.SwingWorker;

/**
 * Class ImageWorker
 *
 * @param <T>
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class ImageWorker<T extends IIconList> extends SwingWorker<Icon, ImageWorker.ImageChunk> {

  private final List<T> images;
  private final Dimension imageSize;
  private final String defaultImage;
  private final DefaultListModel model;
  private ImageInfo.ImageSize size;
  private T image;

  public ImageWorker(T image, Dimension imageSize, String defaultImage, ImageInfo.ImageSize size) {
    images = new ArrayList<T>();
    this.image = image;
    this.imageSize = imageSize;
    this.defaultImage = defaultImage;
    this.size = size;
    this.model = null;
  }

  public ImageWorker(List<T> images, DefaultListModel model, Dimension imageSize, String defaultImage) {
    this.images = images;
    this.model = model;
    this.imageSize = imageSize;
    this.defaultImage = defaultImage;
    this.size = ImageInfo.ImageSize.small;
  }

//  public ImageWorker(List<T> images, GalleryPanel gallery, String defaultImage, ImageInfo.ImageSize size) {
//    this.images = images;
//    this.model = null;
//    this.gallery = gallery;
//    this.imageSize = null;
//    this.defaultImage = defaultImage;
//    this.size = size;
//  }
  @Override
  protected Icon doInBackground() {
    if (model == null) {
      Icon icon = ImageUtils.getIcon(image.getUri(size), imageSize, defaultImage);
      return icon;
    }

    int count = 0;
    for (T image : images) {

      if (isCancelled()) {
        UISettings.LOGGER.log(Level.INFO, String.format("Worker ImageWorker canceled"));
        break;
      }

      Icon icon = ImageUtils.getIcon(image.getUri(size), imageSize, defaultImage);
      publish(new ImageChunk(icon, count++));
    }

    return (images.size() > 0) ? images.get(0).getIcon() : null;
  }

  @Override
  public final void process(List<ImageWorker.ImageChunk> chunks) {
    for (ImageWorker.ImageChunk chunk : chunks) {

      Icon icon = chunk.getIcon();
      int index = chunk.getIndex();
      if (index >= model.size()) {
        continue;
      }

      @SuppressWarnings("unchecked")
      T obj = (T) model.get(index);

      obj.setIcon(icon);
      model.setElementAt(obj, index);
    }
  }

  public class ImageChunk {

    private int index;
    private Icon icon;

    public ImageChunk(Icon icon, int index) {
      this.icon = icon;
      this.index = index;
    }

    public int getIndex() {
      return index;
    }

    public Icon getIcon() {
      return icon;
    }
  }
}