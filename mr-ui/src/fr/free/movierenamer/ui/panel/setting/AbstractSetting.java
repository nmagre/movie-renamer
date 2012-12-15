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

import fr.free.movierenamer.ui.Setting;
import fr.free.movierenamer.ui.panel.PanelGenerator;
import java.util.List;
import javax.swing.JPanel;

/**
 * Class AbstractSetting
 *
 * @author Nicolas Magré
 */
public abstract class AbstractSetting extends PanelGenerator {

  protected AbstractSetting() {
    super();
  }

  public void addSettings(List<List<Setting.SettingsDefinition>> settings) {
    for (List<Setting.SettingsDefinition> group : settings) {
      add(createTitle(group.get(0).getGroup().name()), getTitleConstraint());
      for (Setting.SettingsDefinition definition : group) {
        add(createComponent(definition.getComponent(), definition.name()), getGroupConstraint(definition.getIndent()));
      }
    }
    add(new JPanel(), getDummyPanelConstraint());

    repaint();
    validate();
  }

  protected abstract boolean checkValue();

  protected abstract boolean needRestart();

  protected abstract boolean fireProperty();
}
