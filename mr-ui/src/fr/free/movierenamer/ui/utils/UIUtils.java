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
package fr.free.movierenamer.ui.utils;

import com.alee.extended.panel.GroupPanel;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.managers.popup.PopupWay;
import com.alee.managers.popup.WebButtonPopup;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;
import fr.free.movierenamer.ui.bean.IIconList;
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.ui.swing.IconListRenderer;
import fr.free.movierenamer.utils.LocaleUtils;
import java.util.Comparator;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

/**
 * Class MediaRenamed, Renamed movie
 *
 * @author Nicolas Magré
 */
public final class UIUtils {

  public static final IconListRenderer<IIconList> iconListRenderer = new IconListRenderer<IIconList>();
  public static final Comparator<UIFile> groupFileComparator = new Comparator<UIFile>() {
    @Override
    public int compare(UIFile stringOne, UIFile stringTwo) {
      return stringOne.getGroupName().toLowerCase().compareTo(stringTwo.getGroupName().toLowerCase());
    }
  };

  public static WebButton createSettingButton(PopupWay way, JComponent... components) {
    WebButton button = new WebButton();
    button.setIcon(ImageUtils.SETTING_16);
    //button.setMargin(0, 0, 0, 0);
    button.setAlignmentY(0.0F);
    button.setFocusPainted(true);
    button.setInnerShadeWidth(0);
    button.setLeftRightSpacing(1);
    button.setRolloverDarkBorderOnly(true);
    button.setRolloverDecoratedOnly(true);
    button.setRound(2);

    createPopup(button, way, components);
    TooltipWay ttway = TooltipWay.down;
    switch (way) {
      case upCenter:
      case upLeft:
      case upRight:
        ttway = TooltipWay.up;
        break;
    }
    TooltipManager.setTooltip(button, new WebLabel(LocaleUtils.i18nExt("tooltip.settings"), ImageUtils.SETTING_16, SwingConstants.TRAILING), ttway);
    return button;
  }

  private static WebButtonPopup createPopup(WebButton button, PopupWay way, JComponent... components) {
    WebButtonPopup popup = new WebButtonPopup(button, way);
    GroupPanel content = new GroupPanel(5, false, components);
    content.setMargin(15);
    popup.setContent(content);

    return popup;
  }

  private UIUtils() {
    throw new UnsupportedOperationException();
  }
}
