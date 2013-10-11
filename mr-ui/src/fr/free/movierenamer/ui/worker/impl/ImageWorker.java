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
import fr.free.movierenamer.ui.bean.IImage;
import fr.free.movierenamer.ui.swing.ImageListModel;
import fr.free.movierenamer.ui.worker.AbstractImageWorker;
import fr.free.movierenamer.utils.LocaleUtils;
import java.awt.Dimension;
import java.util.List;
import javax.swing.Icon;

/**
 * Class ImageWorker
 *
 * @param <T>
 * @author Nicolas Magré
 */
public class ImageWorker<T extends IImage> extends AbstractImageWorker<T> {

  private final ImageListModel<T> model;

  public ImageWorker(List<T> images, ImageListModel<T> model, Dimension imageSize, String defaultImage) {
    this(images, model, ImageInfo.ImageSize.small, imageSize, defaultImage);
  }

  public ImageWorker(List<T> images, ImageListModel<T> model, ImageInfo.ImageSize size, Dimension imageSize, String defaultImage) {
    super(images, imageSize, size, defaultImage);
    this.model = model;
  }

  @Override
  public final void process(List<AbstractImageWorker<T>.ImageChunk> chunks) {
    if (model == null) {
      return;
    }

    for (AbstractImageWorker<T>.ImageChunk chunk : chunks) {

      Icon icon = chunk.getIcon();
      int id = chunk.getId();

      T obj = model.getElementById(id);
      if (obj != null) {
        obj.setIcon(icon);
        model.setElement(obj);
      }
    }
  }

  @Override
  public String getParam() {
    return String.format("%s [%s %s images]", getClass().getGenericSuperclass(), size.name(), images.size());
  }

  @Override
  public String getDisplayName() {
    return ("worker.image");// FIXME i18n
  }
}
