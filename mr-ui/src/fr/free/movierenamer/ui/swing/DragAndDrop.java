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
package fr.free.movierenamer.ui.swing;

import static fr.free.movierenamer.info.ImageInfo.ImageProperty.url;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.settings.UISettings;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Class DragAndDrop
 *
 * @author Nicolas Magré
 */
public abstract class DragAndDrop implements DropTargetListener {

  private final MovieRenamer mr;

  public DragAndDrop(MovieRenamer parent) {
    mr = parent;
  }

  @Override
  public void dragEnter(DropTargetDragEvent evt) {
  }

  @Override
  public void dragOver(DropTargetDragEvent evt) {
  }

  @Override
  public void dropActionChanged(DropTargetDragEvent evt) {
  }

  @Override
  public void dragExit(DropTargetEvent evt) {
  }

  @Override
  @SuppressWarnings("unchecked")
  public void drop(DropTargetDropEvent evt) {// FIXME put this in a thread and do not block EDT

    // We block the UI thread (EDT) during file process to avoid any other operation
    // This is the only case where it's acceptable to do that
    List<File> files = new ArrayList<>();

    try {
      mr.setCursor(MovieRenamer.hourglassCursor);

      int action = evt.getDropAction();
      evt.acceptDrop(action);

      Transferable data = evt.getTransferable();
      if (data.isDataFlavorSupported(DataFlavor.stringFlavor)) { // Unix/Remote files

        String dropedFile = (String) data.getTransferData(DataFlavor.stringFlavor);
        String[] res = dropedFile.split("\n");
        for (String file : res) {
          if (file.startsWith("file://")) {// Local file
            file = URLDecoder.decode(file.replace("file://", "").replace("\n", ""), "UTF-8");
            file = file.substring(0, file.length() - 1);
            files.add(new File(file));
          } else if (file.startsWith("http") || file.startsWith("www")) {
            File f;
            try {
              URL url = new URL(file);
              f = new File(url.toExternalForm());
              files.add(f);
            } catch (Exception e) {

            }

          }

        }
      } else if (data.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {// Windows
        files.addAll((List<File>) data.getTransferData(DataFlavor.javaFileListFlavor));
      }

      getFiles(files);

    } catch (UnsupportedFlavorException | IOException ex) {
      UISettings.LOGGER.log(Level.SEVERE, null, ex);
    } finally {
      evt.dropComplete(true);
      mr.setCursor(MovieRenamer.normalCursor);
    }
  }

  public abstract void getFiles(List<File> files);
}
