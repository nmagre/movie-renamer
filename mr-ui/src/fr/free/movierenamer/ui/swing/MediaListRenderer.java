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

import ca.odell.glazedlists.SeparatorList;
import com.alee.laf.label.WebLabel;
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.ui.settings.UISettings;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JList;

/**
 * Class MediaListRenderer
 *
 * @author Nicolas Magré
 */
public class MediaListRenderer extends IconListRenderer<UIFile> {

  private static final long serialVersionUID = 1L;
  private static final UISettings settings = UISettings.getInstance();

  public enum Property implements IRendererProperty {

    showGroup(settings.isGroupMediaList());
    private boolean value = true;

    private Property(boolean value) {
      this.value = value;
    }

    @Override
    public boolean isEnabled() {
      return value;
    }

    @Override
    public void setEnabled(boolean value) {
      this.value = value;
    }
  }

  @Override
  protected WebLabel getListCellRendererComponent(JList list, WebLabel label, Object value, int index) {
    // Media list separator
    if (value instanceof SeparatorList.Separator && Property.showGroup.isEnabled()) {
      SeparatorList.Separator separator = (SeparatorList.Separator) value;
      UIFile file = (UIFile) separator.getGroup().get(0);
      label.setText(file.getGroupName());

      label.setFont(label.getFont().deriveFont(Font.BOLD));
      label.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 0));
      return label;
    }

    return label;
  }
}
