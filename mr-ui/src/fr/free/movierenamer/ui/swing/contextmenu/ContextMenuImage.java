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
package fr.free.movierenamer.ui.swing.contextmenu;

import com.alee.laf.list.WebList;
import com.alee.laf.menu.WebPopupMenu;
import fr.free.movierenamer.ui.bean.IImage;
import fr.free.movierenamer.ui.swing.ImageListModel;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * Class ContextMenuImageMouseListener
 *
 * @author Nicolas Magré
 */
public class ContextMenuImage extends MouseAdapter {

  private final WebPopupMenu menu = new WebPopupMenu();

  private final Action add;
  private final Action edit;
  private final Action delete;
  private WebList list;
  private IImage iimage = null;

  public ContextMenuImage() {

    add = new AbstractAction(UIUtils.i18n.getLanguage("rmenu.add", false), ImageUtils.PLUS_16) {
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent ae) {
        //list.remove(index);
      }
    };

    edit = new AbstractAction(UIUtils.i18n.getLanguage("rmenu.edit", false), ImageUtils.EDIT_16) {
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent ae) {
        //list.remove(index);
      }
    };

    delete = new AbstractAction(UIUtils.i18n.getLanguage("rmenu.delete", false), ImageUtils.DELETE_16) {
      private static final long serialVersionUID = 1L;

      @Override
      @SuppressWarnings("unchecked")
      public void actionPerformed(ActionEvent ae) {
        ImageListModel<IImage> model = (ImageListModel<IImage>) list.getModel();
        model.removeElement(iimage);
      }
    };

    menu.add(add);
    menu.add(edit);
    menu.addSeparator();
    menu.add(delete);
  }

  @Override
  public void mouseClicked(MouseEvent e) {

    if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
      if (!(e.getSource() instanceof WebList)) {
        return;
      }

      list = (WebList) e.getSource();

      if (list.getModel().getSize() <= 0) {
        return;
      }

      int index = list.locationToIndex(e.getPoint());
      if (index >= 0) {
        list.setSelectedIndex(index);
        iimage = (IImage) list.getSelectedValue();
        menu.show(e.getComponent(), e.getX(), e.getY());
      }
    }

  }

}
