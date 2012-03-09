/******************************************************************************
 *                                                                             *
 *    Movie Renamer                                                            *
 *    Copyright (C) 2011 Magré Nicolas                                         *
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

import fr.free.movierenamer.movie.MovieFile;
import fr.free.movierenamer.utils.Utils;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.media.MediaLocator;
import javax.media.NoProcessorException;
import javax.media.format.VideoFormat;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

/**
 *
 * @author duffy
 */
public class ContextMenuListMouseListener extends MouseAdapter {

  private JPopupMenu popup = new JPopupMenu();
  private MovieFile moviefile;
  private Action play;
  private Action info;
  private Action refresh;

  public ContextMenuListMouseListener() {
    play = new AbstractAction("Play") {

      @Override
      public void actionPerformed(ActionEvent ae) {
        try {
          Desktop.getDesktop().open(moviefile.getFile());
        } catch (IOException e) {
        }
      }
    };

    info = new AbstractAction("Info") {

      @Override
      public void actionPerformed(ActionEvent ae) {
        String message = "";
        try {
          VideoFormat format = Utils.getFormat(new MediaLocator(moviefile.getFile().toURI().toURL()));
          message = format.toString();
        } catch (NoProcessorException ex) {
          //Logger.getLogger(MovieRenamer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
          //Logger.getLogger(MovieRenamer.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (message.equals("")) message = "Can't retreive file information";
        JOptionPane.showMessageDialog(null, message, "File information", JOptionPane.INFORMATION_MESSAGE);
      }
    };

    refresh = new AbstractAction("Refresh") {

      @Override
      public void actionPerformed(ActionEvent ae) {
        
      }
    };

    popup.add(play);
    popup.add(info);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
      if (!(e.getSource() instanceof JList))
        return;
      JList list = (JList) e.getSource();
      if (!(list.getSelectedValue() instanceof MovieFile)) return;

      int location = list.locationToIndex(e.getPoint());
      moviefile = (MovieFile) list.getModel().getElementAt(list.locationToIndex(e.getPoint()));
      int x, y;
      x = e.getX();
      y = e.getY();
      if (list.getSelectedIndex() == location) popup.add(refresh);
      else if (popup.getComponentCount() == 3) popup.remove(2);
      popup.show(e.getComponent(), x, y);
    }
  }
}
