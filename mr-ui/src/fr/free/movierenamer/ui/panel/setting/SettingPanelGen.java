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
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.ui.panel.PanelGenerator;
import fr.free.movierenamer.ui.panel.SettingPanel.SettingsDefinition;
import fr.free.movierenamer.ui.settings.UISettings;
import java.awt.GridBagLayout;
import java.util.List;
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

  public SettingPanelGen() {
    super();
    setLayout(new GridBagLayout());
  }

  public void addSettings(List<List<SettingsDefinition>> settings) {
    for (List<SettingsDefinition> group : settings) {
      add(createTitle(group.get(0).getGroup().name()), getTitleConstraint());
      for (SettingsDefinition definition : group) {
        switch (definition.getComponent()) {
          case BUTTON:
          case CHECKBOX:
          case FIELD:
          case LABEL:
          case RADIOBUTTON:
          case TOOLBAR:
            JComponent component = createComponent(definition.getComponent(), definition.getName());
            if (definition.getComponent() == Component.CHECKBOX && definition.getVclass().equals(Boolean.class)) {
              if (definition.getProvider() == UISettings.SettingProvider.CORE) {
                ((WebCheckBox) component).setSelected(Boolean.parseBoolean(Settings.getInstance().get((Settings.SettingsProperty) definition.getKey())));
              }
              else {
                ((WebCheckBox) component).setSelected(Boolean.parseBoolean(UISettings.getInstance().get((UISettings.UISettingsProperty) definition.getKey())));
              }
            }
            add(component, getGroupConstraint(definition.getIndent()));
            break;
          case CUSTOM:
            List<JComponent> components = definition.getJComponents();
            int size = components.size();
            for (int i = 0; i < size; i++) {
              if (definition.getHorizontal()) {
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
}
