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
import com.alee.managers.language.data.TooltipWay;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.notification.NotificationOption;
import com.alee.managers.notification.WebNotificationPopup;
import com.alee.managers.popup.PopupWay;
import static com.alee.managers.popup.PopupWay.upCenter;
import static com.alee.managers.popup.PopupWay.upLeft;
import static com.alee.managers.popup.PopupWay.upRight;
import com.alee.managers.popup.WebButtonPopup;
import com.alee.managers.tooltip.TooltipManager;
import com.sun.jna.Platform;
import fr.free.movierenamer.ui.swing.IIconList;
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.ui.i18n.I18n;
import fr.free.movierenamer.ui.swing.renderer.IconListRenderer;
import fr.free.movierenamer.ui.swing.renderer.TrailerListRenderer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.Enumeration;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 * Class UIUtils
 *
 * @author Nicolas Magré
 */
public final class UIUtils {

  private static final int NOTIFICATION_DELAY = 3000;
  public static final Dimension buttonSize = new Dimension(110, 27);
  public static final Dimension listImageSize = new Dimension(45, 70);
  public static final I18n i18n = new I18n("main");
  public static final IconListRenderer<IIconList> iconListRenderer = new IconListRenderer<>();
  public static final TrailerListRenderer trailerListRenderer = new TrailerListRenderer();
  public static final Comparator<UIFile> groupFileComparator = new Comparator<UIFile>() {
    @Override
    public int compare(UIFile stringOne, UIFile stringTwo) {
      return stringOne.getGroupName().toLowerCase().compareTo(stringTwo.getGroupName().toLowerCase());
    }
  };
  public static final String fontName;
  public static final Font defaultFont;
  public static final Font boldFont;
  public static final Font titleFont;
  public static final Font italicFont;

  static {
    fontName = Platform.isWindows() ? "Courier New" : "Lucida Typewriter";// FIXME "Courier New" is so ugly, we need to find another best font
    defaultFont = getFont(11);
    boldFont = getFont(Font.BOLD, 11);
    titleFont = getFont(Font.BOLD, 12);
    italicFont = getFont(Font.ITALIC, 11);
  }

  public static Font getFont(int size) {
    return getFont(Font.PLAIN, size);
  }

  public static Font getFont(int style, int size) {
    return new Font(fontName, style, size);
  }

  /**
   * Move and center frameToMove to the screen of the mainFrame
   *
   * @param mainFrame Main Window
   * @param frameToMove Window to move
   */
  public static void showOnScreen(Window mainFrame, Window frameToMove) {
    showOnScreen(frameToMove, getScreen(mainFrame));
  }

  /**
   * Move frame to screen "screen" centered/fullscreen
   *
   * @param frame Window to move
   * @param screen Screen device to display Window
   */
  public static void showOnScreen(Window frame, int screen) {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] gd = ge.getScreenDevices();

    if (gd.length > 0) {
      Rectangle bound = gd[0].getDefaultConfiguration().getBounds();

      if (screen > -1 && screen < gd.length) {
        bound = gd[screen].getDefaultConfiguration().getBounds();
      }

      int x = (int) bound.getCenterX() - frame.getWidth() / 2;
      int y = (int) bound.getCenterY() - frame.getHeight() / 2;
      frame.setLocation(x, y);
    }

  }

  public static void showOnScreen(Frame frame, int screen, int x, int y, int state) {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] gd = ge.getScreenDevices();

    if (gd.length > 0) {
      Rectangle bound = gd[0].getDefaultConfiguration().getBounds();

      if (screen > -1 && screen < gd.length) {
        bound = gd[screen].getDefaultConfiguration().getBounds();
      }

      // If frame is OOB, we center it
      if (!bound.contains(new Point(x, y))) {
        x = (int) bound.getCenterX() - frame.getWidth() / 2;
        y = (int) bound.getCenterY() - frame.getHeight() / 2;
      }

      frame.setLocation(x, y);
    }
    frame.setExtendedState(state);
  }

  /**
   * Get screen device number where the Window is displayed
   *
   * @param frame Window
   * @return screen device number or 0
   */
  public static int getScreen(Window frame) {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsConfiguration fge = frame.getGraphicsConfiguration();
    GraphicsDevice fs = fge.getDevice();
    GraphicsDevice[] gds = ge.getScreenDevices();

    int i = 0;
    for (GraphicsDevice gd : gds) {
      if (gd.equals(fs)) {
        return i;
      }
      i++;
    }

    return 0;
  }

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

  public static void showNoResultNotification(String search) {
    showNotification(i18n.getLanguage("notification.noresult", false, search), ImageUtils.NORESULT_24, NOTIFICATION_DELAY);
  }

  public static void showErrorNotification(String str) {
    showNotification(str, ImageUtils.ERROR_24, null, NotificationOption.accept);
  }

  public static void showWrongNotification(String str) {
    showNotification(str, ImageUtils.STOP_24, NOTIFICATION_DELAY);
  }

  public static void showWarningNotification(String str) {
    showNotification(str, ImageUtils.STOP_24, NOTIFICATION_DELAY);
  }

  public static void showNotification(String str) {
    showNotification(str, ImageUtils.INFO_24, NOTIFICATION_DELAY);
  }

  private static void showNotification(String str, Icon icon, Integer delay, NotificationOption... options) {
    final WebNotificationPopup notificationPopup = new WebNotificationPopup();
    notificationPopup.setIcon(icon);
    if (delay != null) {
      notificationPopup.setDisplayTime(delay);
    }
    WebLabel label = new WebLabel("<html>" + str.replace("\\n", "<br>") + "</html>");
    notificationPopup.setContent(label);
    if (options != null) {
      notificationPopup.setOptions(options);
    }

    NotificationManager.showNotification(notificationPopup);
  }

  public static void setUIFont() {
    FontUIResource f = new FontUIResource(defaultFont);
    Enumeration<Object> keys = UIManager.getDefaults().keys();
    
    while (keys.hasMoreElements()) {
      Object key = keys.nextElement();
      Object value = UIManager.get(key);
      if (value != null && value instanceof javax.swing.plaf.FontUIResource) {
        UIManager.put(key, f);
      }
    }

    f = new FontUIResource(boldFont);
    UIManager.put("Button.font", f);
  }

  /**
   * Get selected object (IIconList) in list
   *
   * @param <T> IIconList
   * @param list List
   * @return Selected object (IIconList) or null
   */
  @SuppressWarnings("unchecked")
  public static <T extends IIconList> T getSelectedElement(final WebList list) {
    T current = null;
    if (list != null) {
      Object obj = list.getSelectedValue();
      if (obj != null) {
        if (obj instanceof IIconList) {
          current = (T) obj;
          list.ensureIndexIsVisible(list.getSelectedIndex());
        }
      }
    }
    return current;
  }

  private UIUtils() {
    throw new UnsupportedOperationException();
  }
}
