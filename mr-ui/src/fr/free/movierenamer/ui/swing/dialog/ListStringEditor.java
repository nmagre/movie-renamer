/*
 * Copyright (C) 2015 duffy
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
package fr.free.movierenamer.ui.swing.dialog;

import com.alee.laf.optionpane.WebOptionPane;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.event.IEventListener;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author duffy
 */
public class ListStringEditor extends ListEditor<String> {

    public ListStringEditor(MovieRenamer mr, List<Object> objectList, Class<? extends IEventListener> sendEventTo) {
        super(mr, objectList, sendEventTo);
    }

    public ListStringEditor(MovieRenamer mr, List<Object> objectList, Class<? extends IEventListener> sendEventTo, boolean moveBtn) {
        super(mr, objectList, sendEventTo, moveBtn);
    }

    @Override
    protected String createNewValue() {
        return (String) WebOptionPane.showInputDialog(this, "test", "test", JOptionPane.INFORMATION_MESSAGE, null, null, null);
    }

}
