/******************************************************************************
 *                                                                             *
 *    Movie Renamer                                                            *
 *    Copyright (C) 2012 Magré Nicolas                                         *
 *                                                                             *
 *    Movie Renamer is free software: you can redistribute it and/or modify    *
 *    it under the terms of the GNU General Public License as published by     *
 *    the Free Software Foundation, either version 3 of the License, or        *
 *    (at your option) any later version.                                      *
 *                                                                             *
 *    This program is distributed in the hope that it will be useful,          *
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            *
 *    GNU General Public License for more details.                             *
 *                                                                             *
 *    You should have received a copy of the GNU General Public License        *
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                             *
 ******************************************************************************/
package fr.free.movierenamer.ui.res;

import fr.free.movierenamer.utils.Settings;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.worker.ListFilesWorker;

/**
 *
 * @author duffy
 */
public class DropFile implements DropTargetListener {

  private Settings setting;
  private JFrame parent;
  private MovieRenamer.fileWorkerListener listener;
  private FilenameFilter folderFilter = new FilenameFilter() {

    @Override
    public boolean accept(File dir, String name) {
      return new File(dir.getAbsolutePath() + File.separator + name).isDirectory();
    }
  };

  public DropFile(Settings setting, MovieRenamer.fileWorkerListener listener, JFrame parent) {
    this.setting = setting;
    this.parent = parent;
    this.listener = listener;
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

  @SuppressWarnings("unchecked") //Remove cast warning from Object to List<File>
  @Override
  public void drop(DropTargetDropEvent evt) {

    int action = evt.getDropAction();
    evt.acceptDrop(action);
    try {
      Transferable data = evt.getTransferable();
      ArrayList<File> files = new ArrayList<File>();
      if (data.isDataFlavorSupported(DataFlavor.stringFlavor)) { // Unix

        String dropedFile = (String) data.getTransferData(DataFlavor.stringFlavor);
        String[] res = dropedFile.split("\n");
        System.out.println(dropedFile);
        if(data.isDataFlavorSupported(DataFlavor.imageFlavor)){
          System.out.println("DROPPED FILE IS AN IMAGE");
        }

        for (int i = 0; i < res.length; i++) {
          String file = URLDecoder.decode(res[i].replace("file://", "").replace("\n", ""), "UTF-8");
          file = file.substring(0, file.length() - 1);
          files.add(new File(file));
        }
      } else if (data.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){// Windows
        files.addAll((List<File>) data.getTransferData(DataFlavor.javaFileListFlavor));
      }      

      setMovies(files);

    } catch (UnsupportedFlavorException e) {
      JOptionPane.showMessageDialog(parent, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(parent, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
      evt.dropComplete(true);
    }
  }

  public void setMovies(ArrayList<File> files) {
    boolean subFolders = false;
    int count = 0;
    for (File file : files) {
      if (file.isDirectory()) {
        File[] subDir = file.listFiles(folderFilter);
        if (subDir != null) {
          count += subDir.length;
          if (subDir.length > 0)
            if (!subFolders) {//A refaire (running in EDT ?)
              int n = JOptionPane.showConfirmDialog(parent, "One or more folder(s) contain subfolder(s)\nWould you like to scan subfolder(s) ?", "Question", JOptionPane.YES_NO_OPTION);
              subFolders = !(n != 0);
            }
        }
      }
    }

    ListFilesWorker lft = new ListFilesWorker(files, subFolders, count, setting);
    listener.setWorker(lft);
    lft.addPropertyChangeListener(listener);
    lft.execute();
  }
}
