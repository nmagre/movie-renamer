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
import fr.free.movierenamer.ui.swing.dialog.GalleryDialog;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.ui.worker.AbstractImageWorker;
import java.awt.Dimension;
import java.util.List;
import javax.swing.Icon;

/**
 * Class GalleryWorker
 *
 * @author Nicolas Magré
 */
public class GalleryWorker extends AbstractImageWorker<UIMediaImage> {

  private final GalleryDialog panel;

  public GalleryWorker(WorkerId wid, List<UIMediaImage> images, GalleryDialog panel, Dimension resize, Icon defaultImage) {
    this(wid, images, panel, ImageInfo.ImageSize.small, resize, defaultImage);
  }

  public GalleryWorker(WorkerId wid, List<UIMediaImage> images, GalleryDialog panel, ImageInfo.ImageSize size, Dimension resize, Icon defaultImage) {
    super(wid, images, size, resize, defaultImage, true);
    this.panel = panel;
  }

  @Override
  public final void process(List<AbstractImageWorker<UIMediaImage>.ImageChunk> chunks) {
    for (AbstractImageWorker<UIMediaImage>.ImageChunk chunk : chunks) {

      Icon icon = chunk.getIcon();
      int id = chunk.getId();

      panel.setImage(icon, id);
    }
  }

  @Override
  public String getDisplayName() {

    String type = "Image";
    switch (wid) {
      case IMAGE_GALLERY_BANNER:
        type = UIUtils.i18n.getLanguage("main.image.banner", false);
        break;
      case IMAGE_GALLERY_CDART:
        type = UIUtils.i18n.getLanguage("main.image.cdart", false);
        break;
      case IMAGE_GALLERY_CLEARART:
        type = UIUtils.i18n.getLanguage("main.image.clearart", false);
        break;
      case IMAGE_GALLERY_FANART:
        type = UIUtils.i18n.getLanguage("main.image.fanart", false);
        break;
      case IMAGE_GALLERY_LOGO:
        type = UIUtils.i18n.getLanguage("main.image.logo", false);
        break;
      case IMAGE_GALLERY_REMOTE:
        //type = UIUtils.i18n.getLanguage("image.", false);
        break;
      case IMAGE_GALLERY_THUMB:
        type = UIUtils.i18n.getLanguage("main.image.thumb", false);
        break;
    }

    return UIUtils.i18n.getLanguage("main.image.gallery", false) + " " + type;
  }

  @Override
  protected String getName() {
    return "Gallery : " + wid;
  }
}
