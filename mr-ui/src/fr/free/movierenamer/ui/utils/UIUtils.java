/*
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
package fr.free.movierenamer.ui.utils;

import com.alee.extended.panel.GroupPanel;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.managers.popup.PopupWay;
import com.alee.managers.popup.WebButtonPopup;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;
import fr.free.movierenamer.ui.bean.IIconList;
import fr.free.movierenamer.ui.swing.IconListRenderer;
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.utils.LocaleUtils;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.Comparator;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

/**
 * Class MediaRenamed, Renamed movie
 *
 * @author Nicolas Magré
 */
public final class UIUtils {

  public static final IconListRenderer<IIconList> iconListRenderer = new IconListRenderer<IIconList>(false);
  public static final Comparator<UIFile> groupFileComparator = new Comparator<UIFile>() {
    @Override
    public int compare(UIFile stringOne, UIFile stringTwo) {
      return stringOne.getGroupName().toLowerCase().compareTo(stringTwo.getGroupName().toLowerCase());
    }
  };

  public static Icon getAnimatedLoader(final WebList list, final int row) {
    ImageIcon icon = (ImageIcon) ImageUtils.LOADER;
    icon.setImageObserver(new ImageObserver() {
      @Override
      public boolean imageUpdate(Image img, int infoflags, int x, int y, int w, int h) {
        if ((infoflags & (FRAMEBITS | ALLBITS)) != 0 && list.isShowing()) {
          list.repaint(list.getCellBounds(row, row));
        }
        return (infoflags & (ALLBITS | ABORT)) == 0;
      }
    });
    return icon;
  }

  public static WebButton createSettingButton(PopupWay way, String tooltip, JComponent... components) {
    return createSettingButton(way, tooltip, false, components);
  }

  public static WebButton createSettingButton(PopupWay way, String tooltip, boolean decorated, JComponent... components) {
    WebButton button = new WebButton();
    button.setIcon(ImageUtils.SETTING_16);
    button.setUndecorated(!decorated);
    createPopup(button, way, components);
    TooltipWay ttway = TooltipWay.down;
    switch (way) {
      case upCenter:
      case upLeft:
      case upRight:
        ttway = TooltipWay.up;
        break;
    }
    TooltipManager.setTooltip(button, new WebLabel(LocaleUtils.i18nExt(tooltip), ImageUtils.SETTING_16, SwingConstants.TRAILING), ttway);
    return button;
  }

  private static WebButtonPopup createPopup(WebButton button, PopupWay way, JComponent... components) {
    WebButtonPopup popup = new WebButtonPopup(button, way);
    GroupPanel content = new GroupPanel(5, false, (Object[]) components);
    content.setMargin(15);
    popup.setContent(content);

    return popup;
  }

  private UIUtils() {
    throw new UnsupportedOperationException();
  }
}
