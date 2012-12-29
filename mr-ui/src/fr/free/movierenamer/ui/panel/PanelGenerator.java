/*
 * Copyright (C) 2012 duffy
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
package fr.free.movierenamer.ui.panel;

import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.text.WebTextField;
import com.alee.laf.toolbar.WebToolBar;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Class PanelGenerator
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class PanelGenerator extends JPanel {

  // Font
  protected final int titleSize = 14;
  protected final int subTitleSize = 13;
  protected final int textSize = 12;
  protected final String textFont = "Ubuntu";
  // Inset size
  protected final int smallMargin = 3;
  protected final int mediumMargin = 15;
//  private final int bigMargin = 20;
  // Insets
  protected final Insets titleInsets = new Insets(smallMargin, 0, mediumMargin, 0);
  protected final Insets groupInsets = new Insets(smallMargin, mediumMargin, smallMargin, mediumMargin);
  protected final Insets groupSeparationInsets = new Insets(mediumMargin, mediumMargin, smallMargin, mediumMargin);
  // Constraints
  private GridBagConstraints constraint = new GridBagConstraints();
  private final GridBagConstraints dummyPanelConstraint = new GridBagConstraints();
  // Grid Y position
  private int gridy = 0;
  private boolean endGroup = true;

  public enum Component {

    CHECKBOX,
    FIELD,
    BUTTON,
    TOOLBAR,
    LABEL,
    CUSTOM,
    CUSTOM_LIST,
    UNKNOWN;
  }

  protected PanelGenerator() {
    super();
    setFont(new Font(textFont, Font.BOLD, titleSize));
  }

  /**
   * Create a constraint for dummy panel
   *
   * @return constraint
   */
  protected GridBagConstraints getDummyPanelConstraint() {
    dummyPanelConstraint.gridx = 0;
    dummyPanelConstraint.gridy = gridy;
    dummyPanelConstraint.weighty = 1.0;
    return dummyPanelConstraint;
  }

  /**
   * Get default constraint
   *
   * @param inset Insets to set
   * @return constraint
   */
  private GridBagConstraints defaultConstraint(Insets inset) {
    constraint.fill = GridBagConstraints.HORIZONTAL;
    constraint.gridy = gridy++;
    constraint.gridx = 0;
    constraint.weightx = 1.0;
    constraint.anchor = GridBagConstraints.WEST;
    constraint.gridwidth = 0;
    if (inset != null) {
      constraint.insets = inset;
    }
    return constraint;
  }

  /**
   * Get constraint for title
   *
   * @return constraint
   */
  protected GridBagConstraints getTitleConstraint() {
    if (gridy > 1) {
      titleInsets.top = mediumMargin;
    }
    return defaultConstraint(titleInsets);
  }

  /**
   * Get constraint for group
   *
   * @return constraint
   */
  protected GridBagConstraints getGroupConstraint() {
    return getGroupConstraint(1);
  }

  /**
   * Get constraint for group and sub-group (level)
   *
   * @param level Offset of group ( => margin on left)
   * @return constraint
   */
  protected GridBagConstraints getGroupConstraint(int level) {
    Insets inset = (Insets) groupInsets.clone();
    inset.left *= level;
    return defaultConstraint(inset);
  }

  /**
   * Get constraint for horizontal group
   *
   * @param gridx Position on x axis
   * @param last Last component in group
   * @return constraint
   */
  protected GridBagConstraints getGroupConstraint(int gridx, boolean last) {
    return getGroupConstraint(gridx, last, false);
  }

  /**
   * Get constraint for horizontal group
   *
   * @param gridx Position on x axis
   * @param last Last component in group
   * @param resize Resize component
   * @return constraint
   */
  protected GridBagConstraints getGroupConstraint(int gridx, boolean last, boolean resize) {
    return getGroupConstraint(gridx, last, resize, false);
  }

  /**
   * Get constraint for horizontal group on right or left
   *
   * @param gridx Position on x axis
   * @param last Last component in group
   * @param resize Resize component
   * @param right Place component on right
   * @return constraint
   */
  protected GridBagConstraints getGroupConstraint(int gridx, boolean last, boolean resize, boolean right) {
    Insets inset = (!endGroup) ? groupSeparationInsets : groupInsets;
    GridBagConstraints groupConstraint = getGroupConstraint(inset, gridx, last, resize);
    if (right) {
      groupConstraint.fill = GridBagConstraints.NONE;
      groupConstraint.anchor = GridBagConstraints.NORTHEAST;
    }

    if (!endGroup && last) {
      endGroup = true;
    }

    return groupConstraint;
  }

  /**
   * Get constraint for separation group (when there is more than one group)
   *
   * @return constraint
   */
  protected GridBagConstraints getGroupSeparationConstraint() {
    return defaultConstraint(groupSeparationInsets);
  }

  /**
   * Get constraint for horizontal separation group (when there is more than one
   * group)
   *
   * @param gridx X position
   * @param resize Resize component
   * @return constraint
   */
  protected GridBagConstraints getGroupSeparationConstraint(int gridx, boolean resize) {
    endGroup = false;
    return getGroupConstraint(groupSeparationInsets, gridx, false, resize);
  }

  /**
   * Get constraint for group and horizontal group
   *
   * @param inset Group inset
   * @param gridx X position
   * @param last Last component
   * @param resize Resize component
   * @return constraint
   */
  private GridBagConstraints getGroupConstraint(Insets inset, int gridx, boolean last, boolean resize) {
    GridBagConstraints defaultConstraint = defaultConstraint(inset);
    defaultConstraint.gridy = --gridy;
    defaultConstraint.gridx = gridx;
    defaultConstraint.weightx = 0;
    defaultConstraint.gridwidth = 1;
    defaultConstraint.fill = resize ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
    if (last) {
      gridy++;
    }
    return defaultConstraint;
  }

  /**
   * Create title toolbar
   *
   * @param title
   * @return WebToolBar
   */
  protected WebToolBar createTitle(String title) {
    WebToolBar toolbar = (WebToolBar) createComponent(Component.TOOLBAR, title);
    toolbar.setFloatable(false);
    toolbar.setRollover(true);

    WebLabel label = new WebLabel(LocaleUtils.i18nExt(title));
    label.setFont(new Font(textFont, Font.BOLD, subTitleSize));
    toolbar.add(label);
    return toolbar;
  }

  /**
   * Create title toolbar with help button
   *
   * @param title
   * @param helpText
   * @return WebToolBar
   */
  protected WebToolBar createTitle(String title, final String helpText) {
    WebToolBar toolbar = (WebToolBar) createComponent(Component.TOOLBAR, title);
    toolbar.setFloatable(false);
    toolbar.setRollover(true);

    WebLabel label = new WebLabel(LocaleUtils.i18nExt(title));
    label.setFont(new Font(textFont, Font.BOLD, subTitleSize));
    toolbar.add(label);

    if (helpText != null) {
      final WebButton button = new WebButton(UIUtils.HELP);
      button.setMargin(0);
      button.setUndecorated(true);
      button.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
          WebOptionPane.showMessageDialog(PanelGenerator.this, LocaleUtils.i18nExt(helpText), LocaleUtils.i18nExt("help"), WebOptionPane.PLAIN_MESSAGE);
        }
      });
      button.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
          button.setIcon(UIUtils.HELPDISABLED);
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
          button.setIcon(UIUtils.HELP);
        }
      });
      TooltipManager.setTooltip(button, LocaleUtils.i18nExt("help"), TooltipWay.down);
      toolbar.addToEnd(button);
    }

    return toolbar;
  }

  /**
   * Create weblookandfeel component
   *
   * @param settingComponent Component to create
   * @param title Title to set
   * @return Component
   */
  protected JComponent createComponent(Component settingComponent, String title) {
    return createComponent(settingComponent, title, null, Font.PLAIN);
  }

  /**
   * Create weblookandfeel component
   *
   * @param settingComponent Component to create
   * @param title Title to set
   * @param tooltip Tooltip to set
   * @return Component
   */
  protected JComponent createComponent(Component settingComponent, String title, String tooltip) {
    return createComponent(settingComponent, title, tooltip, Font.PLAIN);
  }

  /**
   * Create weblookandfeel component
   *
   * @param settingComponent Component to create
   * @param title Title to set
   * @param tooltip Tooltip to set
   * @param typeface Type face (Bold, plain,...)
   * @return Component
   */
  protected JComponent createComponent(Component settingComponent, String title, String tooltip, int typeface) {
    JComponent component = null;
    switch (settingComponent) {
      case BUTTON:
        component = new WebButton(LocaleUtils.i18nExt(title));
        break;
      case CHECKBOX:
        component = new WebCheckBox(LocaleUtils.i18nExt(title));
        break;
      case FIELD:
        component = new WebTextField(LocaleUtils.i18nExt(title));
        break;
      case TOOLBAR:
        component = new WebToolBar(LocaleUtils.i18nExt(title));
        break;
      case LABEL:
        component = new WebLabel(LocaleUtils.i18nExt(title));
        break;
      default:
        break;
    }

    if (component == null) {
      return null;
    }

    if (tooltip != null) {
      component.setToolTipText(LocaleUtils.i18nExt(tooltip));
    }
    component.setFont(new Font(textFont, typeface, textSize));
    return component;
  }
}
