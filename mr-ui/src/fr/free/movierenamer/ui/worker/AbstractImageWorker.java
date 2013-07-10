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
package fr.free.movierenamer.ui.worker;

import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.ui.bean.IImage;
import fr.free.movierenamer.ui.utils.ImageUtils;
import java.awt.Dimension;
import java.util.List;
import javax.swing.Icon;

/**
 * Class AbstractImageWorker
 *
 * @param <T>
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class AbstractImageWorker<T extends IImage> extends AbstractWorker<Icon, AbstractImageWorker<T>.ImageChunk> {

  protected final List<T> images;
  protected final Dimension imageSize;
  protected final String defaultImage;
  protected final ImageInfo.ImageSize size;

  public AbstractImageWorker(List<T> images, Dimension imageSize, ImageInfo.ImageSize size, String defaultImage) {
    this.images = images;
    this.imageSize = imageSize;
    this.defaultImage = defaultImage;
    this.size = size;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Icon executeInBackground() {
    Icon res = null;
    for (T image : images) {
      if (isCancelled()) {
        break;
      }

      res = ImageUtils.getIcon(image.getUri(size), imageSize, defaultImage);
      publish(new ImageChunk(res, image.getId()));
    }

    return res;
  }

  @Override
  protected void workerDone() throws Exception {
    // Do nothing
  }

  public class ImageChunk {

    private int id;
    private Icon icon;

    public ImageChunk(Icon icon, int id) {
      this.icon = icon;
      this.id = id;
    }

    public int getId() {
      return id;
    }

    public Icon getIcon() {
      return icon;
    }
  }
}