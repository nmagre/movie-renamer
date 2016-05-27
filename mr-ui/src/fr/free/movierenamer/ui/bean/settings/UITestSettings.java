/*
 * Movie Renamer
 * Copyright (C) 2014-2015 Nicolas Magré
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
package fr.free.movierenamer.ui.bean.settings;

import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.text.WebTextField;
import fr.free.movierenamer.ui.swing.panel.generator.SettingPanelGen;
import java.util.Map;

/**
 * Class UITestSettings
 *
 * @author Nicolas Magré
 */
public abstract class UITestSettings {

    
    public abstract String getResult(Map<WebCheckBox, SettingPanelGen.SettingsProperty> checkboxs,
            Map<WebTextField, SettingPanelGen.SettingsProperty> fields, Map<WebComboBox, SettingPanelGen.SettingsProperty> comboboxs);
}
