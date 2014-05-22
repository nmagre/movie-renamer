/*
 * Movie Renamer
 * Copyright (C) 2014 Nicolas Magré
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
package fr.free.movierenamer.ui.swing;

import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIMediaImage;
import fr.free.movierenamer.ui.swing.dialog.GalleryDialog;
import fr.free.movierenamer.ui.swing.panel.ImagePanel;
import fr.free.movierenamer.ui.utils.ImageUtils;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class DragAndDropGallery
 *
 * @author Nicolas Magré
 */
public class DragAndDropGallery extends DragAndDrop {

  private final GalleryDialog gdialog;

  public DragAndDropGallery(MovieRenamer mr, GalleryDialog gdialog) {
    super(mr);
    this.gdialog = gdialog;
  }

  @Override
  public void getFiles(List<File> files, List<URL> urls) {
    for (File file : files) {// FIXME need to be improved ?

      if (ImageUtils.isImage(file.getName())) {
        String furi = file.toURI().toString();
        Map<ImageInfo.ImageProperty, String> fields = new EnumMap<>(ImageInfo.ImageProperty.class);
        fields.put(ImageInfo.ImageProperty.url, furi);
        UIMediaImage image = new UIMediaImage(new ImageInfo(gdialog.getUserAdId(), fields, gdialog.getImageProperty()));

        try {
          image.setIcon(ImageUtils.getIcon(file.toURI().toURL(), null, ImageUtils.WARNING));
        } catch (MalformedURLException ex) {// FIXME show error
          Logger.getLogger(ImagePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        gdialog.addLocaleImage(image);
      }
    }

    for (URL url : urls) {
      if (ImageUtils.isImage(url.toExternalForm())) {
        Map<ImageInfo.ImageProperty, String> fields = new EnumMap<>(ImageInfo.ImageProperty.class);
        fields.put(ImageInfo.ImageProperty.url, url.toExternalForm());
        UIMediaImage image = new UIMediaImage(new ImageInfo(gdialog.getUserAdId(), fields, gdialog.getImageProperty()));
        gdialog.addRemoteImage(image);
      }
    }
  }

}
