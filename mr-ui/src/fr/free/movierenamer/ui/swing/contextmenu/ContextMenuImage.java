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
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;

/**
 * Class ContextMenuImageMouseListener
 *
 * @author Nicolas Magré
 */
public class ContextMenuImage extends MouseAdapter {

  private final JPopupMenu popup = new JPopupMenu();

  private final Action delete;
  private WebList list;

  public ContextMenuImage() {
    delete = new AbstractAction(UIUtils.i18n.getLanguage("rmenu.delete", false), ImageUtils.DELETE_16) {
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent ae) {
        
      }
    };
  }

}
