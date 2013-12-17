/*
 * Copyright (C) 2013 duffy
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
import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.ui.bean.UIPersonImage;
import fr.free.movierenamer.ui.settings.UISettings;
import javax.swing.JList;

/**
 * Class CastingListRenderer
 *
 * @author Nicolas Magré
 */
public class CastingListRenderer extends IconListRenderer<UIPersonImage> {

  private static final UISettings settings = UISettings.getInstance();

  @Override
  protected WebLabel getListCellRendererComponent(JList list, WebLabel label, Object value, int index) {

    if (value instanceof UIPersonImage) {
      UIPersonImage person = (UIPersonImage) value;
      CastingInfo info = person.getInfo();

      String text = "<html>";
      if (!info.getJob().equals("Actor")) {
        text += "<b>" + info.getJob() + "</b><br>";
      }

      if (info.getCharacter() != null) {
        text += info.getName() + "<br><i>" + info.getCharacter() + "</i></html>";
      } else {
        text += "</b>" + info.getName() + "</b></html>";
      }

      label.setText(text);
    }

    if (!settings.isShowActorImage()) {
      label.setIcon(null);
    }

    return label;
  }
}
