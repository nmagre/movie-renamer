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
package fr.free.movierenamer.ui.res;

import fr.free.movierenamer.media.MediaImage;
import fr.free.movierenamer.utils.Settings;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Class DropImage , drag and drog images from hard drive or web browser
 *
 * @author Nicolas Magré
 */
public class DropImage implements DropTargetListener {

  private Settings setting;
  private Component component;
  private IMediaPanel mediaPanel;
  private int mediaImageType;
  private int cache;

  /**
   * Constructor arguments
   *
   * @param component Component to set mouse loding cursor
   * @param mediaPanel Movie Renamer media panel
   * @param mediaImageType Media image type
   * @param cache
   * @param setting Movie Renamer settings
   */
  public DropImage(Component component, IMediaPanel mediaPanel, int mediaImageType, int cache, Settings setting) {
    this.component = component;
    this.mediaPanel = mediaPanel;
    this.mediaImageType = mediaImageType;
    this.cache = cache;
    this.setting = setting;
  }

  @Override
  public void dragEnter(DropTargetDragEvent dtde) {
  }

  @Override
  public void dragOver(DropTargetDragEvent dtde) {
  }

  @Override
  public void dropActionChanged(DropTargetDragEvent dtde) {
  }

  @Override
  public void dragExit(DropTargetEvent dte) {
  }

  @Override
  public void drop(final DropTargetDropEvent evt) {

    Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
    final Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);

    int action = evt.getDropAction();
    final Transferable data = evt.getTransferable();
    evt.acceptDrop(action);

    component.setCursor(hourglassCursor);

    //We block UI thread during image process from hard drive or web
    try {
      if (data.isDataFlavorSupported(DataFlavor.stringFlavor)) {// From hard drive
        String dropedFile = (String) data.getTransferData(DataFlavor.stringFlavor);
        String[] res = dropedFile.split("\n");

        for (int i = 0; i < res.length; i++) {
          if (res[i].startsWith("file://")) {
            String file = URLDecoder.decode(res[i].replace("file://", "").replace("\n", ""), "UTF-8");
            file = file.substring(0, file.length() - 1);
            File f = new File(file);
            if (f.exists()) {

              Image img;
              try {
                img = ImageIO.read(f);
              } catch (IllegalArgumentException e) {
                continue;
              }

              MediaImage mvImg = new MediaImage(-1, mediaImageType);
              mvImg.setMidUrl(res[i]);
              mvImg.setOrigUrl(res[i]);
              mvImg.setThumbUrl(res[i]);

              mediaPanel.addImageToList(img, mvImg, true);
            }
          } else if (res[i].startsWith("http") || res[i].startsWith("www")) {// From web browser
            String image = res[i];
            try {
              URL url = new URL(image);
              Image img = setting.cache.getImage(url, cache);
              if (img == null) {
                setting.cache.add(url.openStream(), url.toString(), cache);
                img = setting.cache.getImage(url, cache);
              }
              if (img != null) {
                MediaImage mvImg = new MediaImage(-1, mediaImageType);
                mvImg.setMidUrl(url.toString());
                mvImg.setOrigUrl(url.toString());
                mvImg.setThumbUrl(url.toString());

                mediaPanel.addImageToList(img, mvImg, true);
              }
            } catch (IOException ex) {
              Logger.getLogger(DropImage.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        }
      }
    } catch (UnsupportedFlavorException ex) {
      Settings.LOGGER.log(Level.SEVERE, ex.toString());
    } catch (IOException ex) {
      Settings.LOGGER.log(Level.SEVERE, ex.toString());
    }
    component.setCursor(normalCursor);
  }
}
