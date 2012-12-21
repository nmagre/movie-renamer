/*
 * Copyright (C) 2012 duffy
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
import fr.free.movierenamer.ui.res.IIconList;
import fr.free.movierenamer.ui.utils.ImageUtils;
import java.awt.Dimension;
import java.net.URI;
import java.util.List;
import javax.swing.Icon;
import javax.swing.SwingWorker;

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
    for (int i = 0; i < images.size(); i++) {
      Icon icon = ImageUtils.getIcon(images.get(i), imageSize, defaultImage);
      publish(new ImageWorker<T>.ImageChunk(icon, i));
    }

    return null;
  }

  @Override
  public final void process(List<ImageWorker<T>.ImageChunk> chunk) {
    Icon icon = chunk.get(0).getIcon();
    int index = chunk.get(0).getIndex();

    @SuppressWarnings("unchecked")
    T obj = (T) model.get(index);

    if (icon != null) {
      obj.setIcon(icon);
      model.setElementAt(obj, index);
    } else {
      model.removeElementAt(index);
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
