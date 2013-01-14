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
import fr.free.movierenamer.ui.list.IIconList;
import fr.free.movierenamer.ui.utils.ImageUtils;
import java.awt.Dimension;
import java.net.URI;
import java.util.List;
import javax.swing.Icon;
import javax.swing.SwingWorker;

/**
 * Class ImageWorker
 *
 * @param <T>
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class ImageWorker<T extends IIconList> extends SwingWorker<Void, ImageWorker<T>.ImageChunk> {

  private final List<URI> images;
  private final Dimension imageSize;
  private final String defaultImage;
  private final DefaultListModel model;

  public ImageWorker(List<URI> images, DefaultListModel model, Dimension imageSize, String defaultImage) {
    this.images = images;
    this.model = model;
    this.imageSize = imageSize;
    this.defaultImage = defaultImage;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Void doInBackground() {

    if (model != null) {
      for (int i = 0; i < images.size(); i++) {
        if (isCancelled()) {
          break;
        }

        Icon icon = ImageUtils.getIcon(images.get(i), imageSize, defaultImage);
        ImageWorker<T>.ImageChunk chunk = new ImageWorker<T>.ImageChunk(icon, i);
        publish(chunk);
      }
    }
    return null;
  }

  @Override
  public final void process(List<ImageWorker<T>.ImageChunk> chunks) {
    for (ImageWorker<T>.ImageChunk chunk : chunks) {
      Icon icon = chunk.getIcon();
      int index = chunk.getIndex();
      if (index >= model.size()) {
        return;
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
