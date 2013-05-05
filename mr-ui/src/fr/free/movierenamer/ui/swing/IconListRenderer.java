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
import com.alee.laf.list.WebListCellRenderer;
import fr.free.movierenamer.ui.bean.IIconList;
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.ui.bean.UISearchResult;
import fr.free.movierenamer.ui.settings.UISettings;
import java.awt.Component;
import java.awt.Font;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JList;

/**
 * Class IconListRenderer , Display image + text in list
 *
 * @param <T>
 * @author Nicolas Magré
 */
public class IconListRenderer<T extends IIconList> extends WebListCellRenderer {

  private static final long serialVersionUID = 1L;
  private boolean horizontalAlign = false;
  private boolean showGroup = true;
  private boolean showIcon = true;

  public IconListRenderer(boolean horizontalAlign) {
    this.horizontalAlign = horizontalAlign;
  }

  public void showGroup(boolean showGroup) {
    this.showGroup = showGroup;
  }

  public void showIcon(boolean showIcon) {
    this.showIcon = showIcon;
  }

  @Override
  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    WebLabel label = (WebLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

    if (horizontalAlign) {
      label.setHorizontalAlignment(WebLabel.CENTER);
    }

    // Media list separator
    if (value instanceof SeparatorList.Separator && showGroup) {
      SeparatorList.Separator separator = (SeparatorList.Separator) value;
      UIFile file = (UIFile) separator.getGroup().get(0);
      label.setText(file.getGroupName());

      label.setFont(label.getFont().deriveFont(Font.BOLD));
      label.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 0));
      return label;
    }

    label.setFont(label.getFont().deriveFont(Font.PLAIN));
    label.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 0));


    IIconList obj;
    try {
      obj = (IIconList) value;
    } catch (ClassCastException e) {
      UISettings.LOGGER.log(Level.SEVERE, String.format("IconListRenderer ClassCastException : IIconList != %s", value.getClass().getSimpleName()));
      return label;
    }

    Icon icon = obj.getIcon();

    if (value instanceof UISearchResult) {
      UISearchResult sres = (UISearchResult) value;
      String text = label.getText();
      if(!text.equals(sres.getOriginalTitle())) {
        label.setText("<html><b>" + text + "</b><br><i>" + sres.getOriginalTitle() + "</i></html>");
      }
    }

    if (icon != null && showIcon) {
      label.setIcon(icon);
    }

    label.setOpaque(true);
    return label;
  }
}
