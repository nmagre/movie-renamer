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
import fr.free.movierenamer.ui.bean.UIMediaImage;
import fr.free.movierenamer.ui.swing.panel.GalleryPanel;
import fr.free.movierenamer.ui.worker.AbstractImageWorker;
import fr.free.movierenamer.utils.LocaleUtils;
import java.awt.Dimension;
import java.util.List;
import javax.swing.Icon;

/**
 * Class GalleryWorker
 *
 * @author Nicolas Magré
 */
public class GalleryWorker extends AbstractImageWorker<UIMediaImage> {

  private final GalleryPanel panel;

  public GalleryWorker(List<UIMediaImage> images, GalleryPanel panel, Dimension imageSize, String defaultImage) {
    this(images, panel, ImageInfo.ImageSize.small, imageSize, defaultImage);
  }

  public GalleryWorker(List<UIMediaImage> images, GalleryPanel panel, ImageInfo.ImageSize size, Dimension imageSize, String defaultImage) {
    super(images, imageSize, size, defaultImage);
    this.panel = panel;
  }

  @Override
  public final void process(List<AbstractImageWorker<UIMediaImage>.ImageChunk> chunks) {
    for (AbstractImageWorker<UIMediaImage>.ImageChunk chunk : chunks) {

      Icon icon = chunk.getIcon();
      int id = chunk.getId();

      panel.addThumbPreview(icon, id);
    }
  }

  @Override
  public String getParam() {
    return String.format("%s [%s images]", panel.getImageProperty().name(), images.size());
  }

  @Override
  public String getDisplayName() {
    return ("worker.gallery");// FIXME i18n
  }
}
