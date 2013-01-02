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
package fr.free.movierenamer.ui.panel.setting;

import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.text.WebTextField;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.ui.panel.PanelGenerator;
import fr.free.movierenamer.ui.panel.SettingPanel.SettingsDefinition;
import fr.free.movierenamer.utils.LocaleUtils;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Class SettingPanel
 *
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
public class SettingPanelGen extends PanelGenerator {

  private static final long serialVersionUID = 1L;
  private Map<SettingsDefinition, JComponent> checkboxs;
  private Map<SettingsDefinition, JComponent> fields;

  public SettingPanelGen() {
    super();
    setLayout(new GridBagLayout());
  }

  public void addSettings(List<List<SettingsDefinition>> settingsDef) {

    checkboxs = new EnumMap<SettingsDefinition, JComponent>(SettingsDefinition.class);
    fields = new EnumMap<SettingsDefinition, JComponent>(SettingsDefinition.class);

    for (List<SettingsDefinition> group : settingsDef) {
      add(createTitle(group.get(0).getSubTitle().name(), group.get(0).getSubTitle().getHelp()), getTitleConstraint());
      for (SettingsDefinition definition : group) {
        switch (definition.getComponent()) {
          //case BUTTON:
          case CHECKBOX:
          case FIELD:
            //case LABEL:
            //case TOOLBAR:
            JComponent component = createComponent(definition.getComponent(), definition.getName());
            if (definition.getComponent() == Component.CHECKBOX && definition.getVclass().equals(Boolean.class)) {
              ((WebCheckBox) component).setSelected(Boolean.parseBoolean(definition.getKey().getValue()));
              add(component, getGroupConstraint(definition.getIndent()));
              TooltipManager.setTooltip(component, LocaleUtils.i18nExt(definition.getName() + "tt"), TooltipWay.down);
              checkboxs.put(definition, component);
            } else if (definition.getComponent() == Component.FIELD) {
              WebLabel label = new WebLabel(definition.getName());
              ((WebTextField) component).setText(definition.getKey().getValue());
              label.setFont(new Font(textFont, Font.BOLD, textSize));
              add(label, getGroupConstraint(0, false, false));
              add(component, getGroupConstraint(1, true, false));
              fields.put(definition, component);
            }
            break;
          case CUSTOM:
            component = definition.getJComponent();
            component.setFont(new Font(textFont, Font.BOLD, textSize));
            if (component instanceof WebComboBox) {
              WebLabel label = new WebLabel(definition.getName());
              label.setFont(new Font(textFont, Font.BOLD, textSize));
              add(label, getGroupConstraint(0, false, false));
              add(definition.getJComponent(), getGroupConstraint(1, true, false));
            } else {
              add(definition.getJComponent(), getGroupConstraint());
            }
            break;
          case CUSTOM_LIST:
            List<JComponent> components = definition.getJComponentsList();
            int size = components.size();
            for (int i = 0; i < size; i++) {
              if (definition.isHorizontal()) {
                add(components.get(i), getGroupConstraint(i, !((i + 1) < size)));
              } else {
                add(components.get(i), getGroupConstraint());
              }
            }
            break;
          case UNKNOWN:
            Settings.LOGGER.log(Level.SEVERE, "Unknown component for {0}", definition.getName());
            break;
        }
      }
    }
    add(new JPanel(), getDummyPanelConstraint());

    repaint();
    validate();
  }

  public Map<SettingsDefinition, JComponent> getCheckbox() {
    return Collections.unmodifiableMap(checkboxs);
  }

  public Map<SettingsDefinition, JComponent> getField() {
    return Collections.unmodifiableMap(fields);
  }
}
