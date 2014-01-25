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
package fr.free.movierenamer.ui.swing.dialog;

import com.alee.laf.rootpane.WebDialog;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;

/**
 * Class AbstractDialog
 *
 * @author Nicolas Magré
 */
public class AbstractDialog extends WebDialog {

  protected final MovieRenamer mr;

  protected AbstractDialog(final MovieRenamer mr, final String title) {
    this(mr, title, true);
  }

  protected AbstractDialog(final MovieRenamer mr, final String title, final boolean modal) {
    super();
    this.mr = mr;

    setLanguage(title);
    setIconImage(ImageUtils.iconToImage(ImageUtils.LOGO_22));
    setModal(modal);
  }

  @Override
  public final void setVisible(boolean b) {
    if (b) {
      UIUtils.showOnScreen(mr, this);
    }
    super.setVisible(b);
  }

}
