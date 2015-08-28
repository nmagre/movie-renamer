/*
 * Movie Renamer
 * Copyright (C) 2012-2014 Nicolas Magré
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
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.ui.worker.AbstractImageWorker;
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

  public ImageWorker(WorkerId wid, ImageListModel<T> model, Dimension resize, Icon defaultImage, boolean downloadImage) {
    this(wid, model, ImageInfo.ImageSize.small, resize, defaultImage, downloadImage);
  }

  public ImageWorker(WorkerId wid, ImageListModel<T> model, ImageInfo.ImageSize size, Dimension resize, Icon defaultImage, boolean downloadImage) {
    super(wid, model.getAll(), size, resize, defaultImage, downloadImage);
    this.model = model;
   // this.sendEvent = false;
  }

  @Override
  public final void process(List<AbstractImageWorker<T>.ImageChunk> chunks) {// TODO remove image on error
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
  public String getDisplayName() {

    String type = "???";
    switch (wid) {
      case IMAGE_INFO_ACTOR:
        type = UIUtils.i18n.getLanguage("main.statusTb.actor", false);
        break;
      case IMAGE_INFO_DIRECTOR:
        type = UIUtils.i18n.getLanguage("main.statusTb.directorwriter", false);
        break;
      case IMAGE_INFO_TRAILER:
        type = UIUtils.i18n.getLanguage("main.statusTb.trailer", false);
        break;
      case IMAGE_SEARCH_RESULT:
        type = UIUtils.i18n.getLanguage("main.statusTb.search", false);
        break;
    }

    return UIUtils.i18n.getLanguage("main.image", false) + " " + type;
  }

  @Override
  protected String getName() {
    return "ImageWorker : " + model.getClass();
  }
}
