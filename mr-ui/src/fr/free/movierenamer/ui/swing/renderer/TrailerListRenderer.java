/*
 * Copyright (C) 2014  Nicolas Magré
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
package fr.free.movierenamer.ui.swing.renderer;

import com.alee.laf.label.WebLabel;
import fr.free.movierenamer.ui.bean.UISearchTrailerResult;
import javax.swing.JList;
import javax.swing.SwingConstants;

/**
 * Class TrailerRenderer
 *
 * @author Nicolas Magré
 */
public class TrailerListRenderer extends IconListRenderer<UISearchTrailerResult> {

  private static final long serialVersionUID = 1L;

  @Override
  protected WebLabel getListCellRendererComponent(JList list, WebLabel label, Object value, int index) {

    if (value instanceof UISearchTrailerResult) {
      UISearchTrailerResult trailer = (UISearchTrailerResult) value;
      String providerImg = getClass().getResource("/image/scrapper/" + trailer.getProvider().toLowerCase().replace(" ", "_") + ".png").toString();
      String text = "<html><body style='width:200px'><center><img src='" + providerImg + "'>&nbsp;&nbsp;<b>" + trailer.getName() + "</b><br><i>" + trailer.getDuration() + "</i></center></body></html>";

      label.setHorizontalTextPosition(SwingConstants.CENTER);
      label.setVerticalAlignment(SwingConstants.BOTTOM);
      label.setHorizontalAlignment(SwingConstants.CENTER);
      label.setVerticalTextPosition(SwingConstants.BOTTOM);
      label.setText(text);
    }

    return label;
  }
}
