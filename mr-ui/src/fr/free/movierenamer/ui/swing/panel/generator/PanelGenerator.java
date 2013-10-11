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
package fr.free.movierenamer.ui.swing.panel.generator;

import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.text.WebTextField;
import com.alee.laf.toolbar.WebToolBar;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;

/**
 * Class PanelGenerator
 *
 * @author Nicolas Magré
 */
public abstract class PanelGenerator extends WebPanel {

  private static final long serialVersionUID = 1L;
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
  private int maxgridx = 0;
  private boolean endGroup = true;

  protected enum Component {

    CHECKBOX,
    FIELD,
    BUTTON,
    TOOLBAR,
    LABEL,
    CUSTOM_LIST
  }

  protected PanelGenerator() {
    super();
    setLayout(new GridBagLayout());
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
    return getGroupConstraint(gridx, last, resize, false, 1);
  }

  /**
   * Get constraint for horizontal group
   *
   * @param gridx Position on x axis
   * @param last Last component in group
   * @param resize Resize component
   * @return constraint
   */
  protected GridBagConstraints getGroupConstraint(int gridx, boolean last, boolean resize, int level) {
    return getGroupConstraint(gridx, last, resize, false, level);
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
  protected GridBagConstraints getGroupConstraint(int gridx, boolean last, boolean resize, boolean right, int level) {
    Insets inset = (Insets) ((!endGroup) ? groupSeparationInsets.clone() : groupInsets.clone());
    inset.left *= level;
    GridBagConstraints groupConstraint = getGroupConstraint(inset, gridx, last, resize);
    if (level == 0) {
      groupConstraint.gridwidth = level;
      groupConstraint.anchor = GridBagConstraints.WEST;
    }

    if (right) {
      groupConstraint.fill = GridBagConstraints.NONE;
      groupConstraint.anchor = GridBagConstraints.NORTHEAST;
    }

    if (!endGroup && last) {
      endGroup = true;
    }

    maxgridx = Math.max(maxgridx, gridx);

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

  protected GridBagConstraints getGroupSeparationConstraint(int gridx, boolean resize, boolean last) {
    endGroup = last;
    return getGroupConstraint(groupSeparationInsets, gridx, last, resize);
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

    if (resize) {
      defaultConstraint.weightx = 1.0;
      if (gridx == 0) {
        defaultConstraint.gridwidth = maxgridx;
      }
    }

    maxgridx = Math.max(maxgridx, gridx);

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
   * @param i18n
   * @return WebToolBar
   */
  protected WebToolBar createTitle(String title, boolean i18n) {
    return createTitle(title, null, i18n);
  }

  /**
   * Create title toolbar with help button
   *
   * @param title
   * @param helpText
   * @param i18n
   * @return WebToolBar
   */
  protected WebToolBar createTitle(String title, final String helpText, final boolean i18n) {
    WebToolBar toolbar = (WebToolBar) createComponent(Component.TOOLBAR, title);
    toolbar.setMargin(new Insets(0, 5, 0, 5));
    toolbar.setFloatable(false);
    toolbar.setRollover(true);

    WebLabel label = new WebLabel(i18n ? title.toLowerCase() : title);// FIXME i18n
    label.setFont(new Font(textFont, Font.BOLD, subTitleSize));
    toolbar.add(label);

    if (helpText != null) {
      final WebButton button = new WebButton(ImageUtils.HELP_16);
      button.setMargin(0);
      button.setFocusable(false);
      button.setRolloverDarkBorderOnly(true);
      button.setRolloverDecoratedOnly(true);
      button.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
          WebOptionPane.showMessageDialog(PanelGenerator.this, i18n ? helpText.toLowerCase() : helpText, "help", WebOptionPane.PLAIN_MESSAGE);// FIXME i18n
        }
      });
      TooltipManager.setTooltip(button, "help", TooltipWay.down);// FIXME i18n
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
    return createComponent(settingComponent, title, null, Font.BOLD);
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
    return createComponent(settingComponent, title, tooltip, Font.BOLD);
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
        component = new WebButton((title));// FIXME i18n
        break;
      case CHECKBOX:
        component = new WebCheckBox((title));// FIXME i18n
        break;
      case FIELD:
        component = new WebTextField();
        typeface = Font.PLAIN;
        break;
      case TOOLBAR:
        component = new WebToolBar((title));// FIXME i18n
        break;
      case LABEL:
        component = new WebLabel((title));// FIXME i18n
        break;
      default:
        break;
    }

    if (component == null) {
      return null;
    }

    if (tooltip != null) {
      TooltipManager.setTooltip(component, (tooltip), TooltipWay.down);// FIXME i18n
    }
    component.setFont(new Font(textFont, typeface, textSize));
    return component;
  }
}
