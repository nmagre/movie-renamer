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

import fr.free.movierenamer.media.MediaFile;
import fr.free.movierenamer.utils.Utils;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.swing.*;

/**
 * Class ContextMenuListMouseListener
 *
 * @author Nicolas Magré
 */
public class ContextMenuListMouseListener extends MouseAdapter {

  private ResourceBundle bundle = ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle");
  private JPopupMenu popup = new JPopupMenu();
  private MediaFile moviefile;
  private PropertyChangeSupport changeSupport;
  private Action search, play, removeList, removeHdd, test;
  private int index;
  private String moviename;

  /**
   * Default construtor
   */
  public ContextMenuListMouseListener() {
    index = -1;
    moviename = "";

    search = new AbstractAction(bundle.getString("search")) {

      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent ae) {
        firePropertyChange("search", null, null);
      }
    };

    play = new AbstractAction(bundle.getString("play")) {

      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent ae) {
        try {
          Desktop.getDesktop().open(moviefile.getFile());
        } catch (IOException e) {
        }
      }
    };

    removeList = new AbstractAction(bundle.getString("removeFromList")) {

      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent ae) {
        firePropertyChange("remove", null, index);
      }
    };

    removeHdd = new AbstractAction(bundle.getString("deleteFile")) {

      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent ae) {
        int n = JOptionPane.showConfirmDialog(null, bundle.getString("removeFile") + Utils.ENDLINE + moviefile.getFile(), bundle.getString("question"), JOptionPane.YES_NO_OPTION);
        if (n == 0) {
          if (!moviefile.getFile().delete()) {
            JOptionPane.showMessageDialog(null, bundle.getString("renameFileFailed"), "Error", JOptionPane.ERROR_MESSAGE);
          } else {
            firePropertyChange("remove", null, index);
          }
        }
      }
    };

    test = new AbstractAction("") {
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent ae) {
      }
    };
    test.setEnabled(false);

    popup.add(search);
    popup.add(new JPopupMenu.Separator());
    popup.add(play);
    popup.add(new JPopupMenu.Separator());
    popup.add(removeList);
    popup.add(removeHdd);
    popup.add(new JPopupMenu.Separator());
    popup.add(test);
    changeSupport = new PropertyChangeSupport(this);
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(listener);
    removeList.addPropertyChangeListener(listener);
    search.addPropertyChangeListener(listener);
    removeHdd.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(listener);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    if (e.getModifiers() == InputEvent.BUTTON3_MASK) {

      if (!(e.getSource() instanceof JList)) {
        return;
      }
      JList list = (JList) e.getSource();
      if (!(list.getSelectedValue() instanceof MediaFile)) {
        return;
      }

      index = list.getSelectedIndex();
      moviefile = (MediaFile) list.getModel().getElementAt(index);
      moviename = moviefile.getFile().getName();
      if (moviename.length() > 30) {
        moviename = moviename.substring(0, 27) + "...";
      }
      popup.remove(7);
      test = new AbstractAction(moviename) {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent ae) {
        }
      };
      test.setEnabled(false);
      popup.add(test);
      popup.show(e.getComponent(), e.getX(), e.getY());
    }
  }
}
