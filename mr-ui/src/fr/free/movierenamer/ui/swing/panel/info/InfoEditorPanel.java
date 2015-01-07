/*
 * Movie Renamer
 * Copyright (C) 2012-2014 Nicolas Magré
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
package fr.free.movierenamer.ui.swing.panel.info;

import com.alee.laf.label.WebLabel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextField;
import fr.free.movierenamer.info.MediaInfo.InfoProperty;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.IEventInfo;
import fr.free.movierenamer.ui.bean.UIEditor;
import fr.free.movierenamer.ui.bean.UIEvent;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.worker.impl.SearchMediaInfoWorker;
import java.awt.GridBagConstraints;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

/**
 * Class InfoEditorPanel
 *
 * @author Nicolas Magré
 * @param <T>
 */
public abstract class InfoEditorPanel<T> extends InfoPanel<T> {

  private static final long serialVersionUID = 1L;

  protected Map<InfoProperty, UIEditor> map;
  protected final MovieRenamer mr;
  private int pos;
  protected static final int EDIT_WIDTH = 3;// label + component + edit/cancel button

  protected InfoEditorPanel(MovieRenamer mr) {
    super();
    this.mr = mr;
    map = new HashMap<>();
    pos = 0;

    // Register to UI event
    regiterUIEvent();
  }

  protected void createEditableField(String lbl, UIEditor editor, int gridWidth) {
    pos = 0;
    createEditableField(lbl, editor, gridWidth, 1, true);
  }

  protected void createEditableField(String i18nkey, UIEditor editor, int gridWidth, int nbElement, boolean last) {
    WebLabel label = (WebLabel) createComponent(Component.LABEL, i18nkey);
    label.setDrawShade(true);

    JComponent component = editor.getEditableComponent();
    component.setEnabled(false);

    if (editor.hasMoreComponents()) {
      nbElement += editor.nbMoreComponents();
    }

    add(label, getGroupConstraint(pos++, false, false));

    GridBagConstraints c = getGroupConstraint(pos, false, true);
    c.gridwidth = gridWidth - EDIT_WIDTH + 1;// Resize to fit grid width

    if (nbElement > 1) {
      c.gridwidth = 1;
      if (pos == 1 && nbElement * EDIT_WIDTH < gridWidth) {// Resize first element to fit empty spaces
        c.gridwidth = gridWidth - (nbElement * EDIT_WIDTH) + 1;
      }
    }

    pos += c.gridwidth;

    // Add a scrollpane if needed
    if (!(component instanceof WebTextField)) {
      component = new WebScrollPane(component);
      component.setEnabled(false);
    }

    add(component, c);

    if (editor.hasMoreComponents()) {
      List<JComponent> components = editor.getComponents();
      for (JComponent cp : components) {
        add(cp, getGroupConstraint(pos++, false, false));
      }
    }

    add(editor.getButton(), getGroupConstraint(pos++, last, false));

    if (last) {
      pos = 0;
    }
  }

  @Override
  public void UIEventHandler(UIEvent.Event event, IEventInfo info, Object object, Object param) {
    UISettings.LOGGER.finer(String.format("%s receive event %s %s", getClass().getSimpleName(), event, (info != null ? info : "")));

    switch (event) {
      case EDIT:
        for (UIEditor editor : map.values()) {
          editor.setEditable();
        }
        break;
      case WORKER_STARTED:
        if (info.getClass().equals(SearchMediaInfoWorker.class)) {
          setPanelEnabled(false);
        }
        break;
      case WORKER_DONE:
        if (info.getClass().equals(SearchMediaInfoWorker.class)) {
          setPanelEnabled(true);
        }
        break;
    }
  }

  private void setPanelEnabled(boolean enabled) {
    for (UIEditor editor : map.values()) {
      editor.setEnabled(enabled);
    }

    java.awt.Component[] components = getComponents();
    for (java.awt.Component component : components) {
      if (component instanceof JScrollPane) {
        component.setEnabled(enabled);
      }
    }
  }

  @Override
  public void clear() {
    for (UIEditor editor : map.values()) {
      editor.reset();
    }
    setPanelEnabled(false);
  }
}
