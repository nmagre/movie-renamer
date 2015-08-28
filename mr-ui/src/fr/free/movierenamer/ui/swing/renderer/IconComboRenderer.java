/*
 * Copyright (C) 2013-2015 Nicolas Magré
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
package fr.free.movierenamer.ui.swing.renderer;

import com.alee.laf.combobox.WebComboBoxCellRenderer;
import com.alee.laf.label.WebLabel;
import fr.free.movierenamer.ui.bean.IIconList;
import fr.free.movierenamer.ui.settings.UISettings;
import java.awt.Component;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JList;

/**
 * Class IconComboRenderer
 *
 * @author Nicolas Magré
 */
public class IconComboRenderer<T extends IIconList> extends WebComboBoxCellRenderer {

    public IconComboRenderer(Component component) {
        super();
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        WebLabel label = (WebLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 0));

        if (value == null) {
            return label;
        }

        T obj;
        try {
            obj = (T) value;
            Icon icon = obj.getIcon();

            if (icon != null && true) {
                label.setIcon(icon);
            }
        } catch (ClassCastException e) {
            UISettings.LOGGER.log(Level.SEVERE, String.format("IconListRenderer ClassCastException : IIconList != %s", value.getClass().getSimpleName()));
        }

        return label;
    }
}
