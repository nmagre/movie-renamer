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
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.managers.hotkey.ButtonHotkeyRunnable;
import com.alee.managers.hotkey.HotkeyData;
import com.alee.managers.hotkey.HotkeyManager;
import com.alee.managers.popup.PopupWay;
import static com.alee.managers.popup.PopupWay.upCenter;
import static com.alee.managers.popup.PopupWay.upLeft;
import static com.alee.managers.popup.PopupWay.upRight;
import com.alee.managers.popup.WebButtonPopup;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;
import fr.free.movierenamer.ui.bean.IIconList;
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.ui.i18n.I18n;
import fr.free.movierenamer.ui.swing.renderer.IconListRenderer;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

/**
 * Class UIUtils
 *
 * @author Nicolas Magré
 */
public final class UIUtils {

  public static final I18n i18n = new I18n("main");
  public static final IconListRenderer<IIconList> iconListRenderer = new IconListRenderer<>();
  public static final Comparator<UIFile> groupFileComparator = new Comparator<UIFile>() {
    @Override
    public int compare(UIFile stringOne, UIFile stringTwo) {
      return stringOne.getGroupName().toLowerCase().compareTo(stringTwo.getGroupName().toLowerCase());
    }
  };

  /**
   * Create a decorated button without icon and tooltip
   *
   * @param language I18n key
   * @return Button
   */
  public static WebButton createButtons(String language) {
    return createButton(language, null);
  }

  /**
   * Create a decorated button with an icon and without tooltip
   *
   * @param language I18n key
   * @param icon Button icon
   * @return Button
   */
  public static WebButton createButton(String language, Icon icon) {
    return createButton(language, icon, true);
  }

  public static WebButton createButton(String language, Icon icon, boolean decorated) {
    return createButton(language, icon, decorated, decorated);
  }

  public static WebButton createButton(String language, Icon icon, boolean decorated, boolean addString) {
    if (addString) {
      return createButton(language, icon, decorated, addString, null, null, null, null);
    }
    return createButton(language, icon, decorated, icon);
  }

  /*
   Button with tooltip
   */
  public static WebButton createButton(String language, Icon icon, Icon smallIcon) {
    return createButton(language, icon, smallIcon, TooltipWay.down);
  }

  public static WebButton createButton(String language, Icon icon, boolean decorated, Icon smallIcon) {
    return createButton(language, icon, decorated, false, smallIcon, null, null, null);
  }

  public static WebButton createButton(String language, Icon icon, Icon smallIcon, TooltipWay way) {
    return createButton(language, icon, smallIcon, way, null, null);
  }

  public static WebButton createButton(String language, Icon icon, Icon smallIcon, HotkeyData hd, Component cmpnt) {
    return createButton(language, icon, smallIcon, TooltipWay.down, hd, cmpnt);
  }

  public static WebButton createButton(String language, Icon icon, Icon smallIcon, TooltipWay way, HotkeyData hd, Component cmpnt) {
    return createButton(language, icon, false, false, smallIcon, way, hd, cmpnt);
  }

  public static WebButton createButton(String language, Icon icon, boolean decorated, boolean addString,
          Icon smallIcon, TooltipWay way, HotkeyData hd, Component cmpnt) {

    WebButton button = new WebButton();
    button.setIcon(icon);
    if (addString) {
      button.setLanguage(language);
    } else {
      if (cmpnt != null && hd != null) {
        HotkeyManager.registerHotkey(cmpnt, button, hd, new ButtonHotkeyRunnable(button), way);
      }

      WebLabel label = new WebLabel(smallIcon != null ? smallIcon : icon, SwingConstants.TRAILING);
      label.setLanguage(language);
      TooltipManager.setTooltip(button, label, way);
    }

    if (!decorated) {
      button.setRolloverDecoratedOnly(true);
    }
    button.setFocusable(false);
    button.setInnerShadeWidth(0);
    button.setRound(2);
    button.setAlignmentY(0.0F);

    return button;
  }

  public static WebButton createSettingButton(PopupWay way, JComponent... components) {
    WebButton button = new WebButton();
    button.setIcon(ImageUtils.SETTING_16);
    button.setAlignmentY(0.0F);
    button.setInnerShadeWidth(0);
    button.setLeftRightSpacing(1);
    button.setRolloverDecoratedOnly(true);
    button.setFocusable(false);
    button.setRound(2);

    TooltipWay ttway = TooltipWay.down;
    if (way != null) {
      createPopup(button, way, components);
      switch (way) {
        case upCenter:
        case upLeft:
        case upRight:
          ttway = TooltipWay.up;
          break;
      }
    }

    WebLabel tooltipLbl = new WebLabel(ImageUtils.SETTING_16, SwingConstants.TRAILING);
    tooltipLbl.setLanguage(i18n.getLanguageKey("toptb.settings"));

    TooltipManager.setTooltip(button, tooltipLbl, ttway);
    return button;
  }

  public static WebButtonPopup createPopup(WebButton button, PopupWay way, JComponent... components) {
    WebButtonPopup popup = new WebButtonPopup(button, way);
    GroupPanel content = new GroupPanel(5, false, components);
    content.setMargin(15);
    popup.setContent(content);

    return popup;
  }

  public static WebCheckBox createShowIconChk(final WebList list, boolean selected, String text) {
    return createShowChk(list, null, selected, text);
  }

  public static WebCheckBox createShowChk(final WebList list, final IconListRenderer.IRendererProperty property, boolean selected, String text) {
    final WebCheckBox checkbox = new WebCheckBox();
    checkbox.setLanguage(text);
    checkbox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        if (property != null) {
          property.setEnabled(checkbox.isSelected());
        } else {
          ((IconListRenderer) list.getCellRenderer()).showIcon(checkbox.isSelected());
        }

        list.revalidate();
        list.repaint();
      }
    });
    checkbox.setSelected(selected);
    return checkbox;
  }

  private UIUtils() {
    throw new UnsupportedOperationException();
  }
}
