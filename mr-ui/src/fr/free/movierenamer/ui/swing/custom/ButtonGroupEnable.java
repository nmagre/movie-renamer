/*
 * Movie_Renamer
 * Copyright (C) 2015 Nicolas Magré
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
package fr.free.movierenamer.ui.swing.custom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;

/**
 * Class ButtonGroupEnable
 *
 * @author Nicolas Magré
 */
public class ButtonGroupEnable extends ButtonGroup {

    protected final List<AbstractButton> abuttons;
    private ButtonModel selection;

    public ButtonGroupEnable() {
        abuttons = new ArrayList<>();
    }

    @Override
    public void add(AbstractButton ab) {
        if (ab == null) {
            return;
        }
        abuttons.add(ab);
        ab.getModel().setGroup(this);
    }

    @Override
    public void remove(AbstractButton ab) {
        if (ab == null) {
            return;
        }

        abuttons.remove(ab);
        if (ab.getModel() == selection) {
            selection = null;
        }
        ab.getModel().setGroup(null);
    }

    @Override
    public void clearSelection() {
        if (selection != null) {
            ButtonModel oldSelection = selection;
            selection = null;
            oldSelection.setEnabled(true);
        }
    }

    @Override
    public int getButtonCount() {
        return abuttons.size();
    }

    public List<AbstractButton> getButtons() {
        return abuttons;
    }

    @Override
    public Enumeration<AbstractButton> getElements() {
        return Collections.enumeration(abuttons);
    }

    @Override
    public ButtonModel getSelection() {
        return selection;
    }

    @Override
    public boolean isSelected(ButtonModel bm) {
        return selection != null && bm == selection;
    }

    @Override
    public void setSelected(ButtonModel bm, boolean selected) {
        if(selected && bm != null && bm != selection) {
            ButtonModel oldSelection = selection;
            selection = bm;
            if (oldSelection != null) {
                oldSelection.setEnabled(true);
            }
            bm.setEnabled(false);
        }
    }

}
