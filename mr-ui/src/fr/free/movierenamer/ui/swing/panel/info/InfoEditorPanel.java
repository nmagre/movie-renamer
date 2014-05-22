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
import com.alee.laf.list.WebList;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextField;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.IEventInfo;
import fr.free.movierenamer.ui.bean.UIEditor;
import fr.free.movierenamer.ui.bean.UIEvent;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.worker.impl.SearchMediaInfoWorker;
import java.awt.GridBagConstraints;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

/**
 * Class InfoEditorPanel
 *
 * @author Nicolas Magré
 */
public abstract class InfoEditorPanel<T> extends InfoPanel<T> {

  protected Map<MediaInfo.InfoProperty, UIEditor> map;
  protected final MovieRenamer mr;
  private int pos;

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
    createEditableField(lbl, editor, gridWidth, 1, false, true);
  }

  protected void createEditableField(String i18nkey, UIEditor editor, int gridWidth, int nbElement, boolean inline, boolean last) {
    WebLabel label = (WebLabel) createComponent(Component.LABEL, i18nkey);
    label.setDrawShade(true);

    JComponent component = editor.getComponent();
    component.setEnabled(false);
    boolean isText = component instanceof WebTextField;
    boolean isList = component instanceof WebList;

    add(label, (isText || !isText && inline) ? getGroupConstraint(pos++, false, false) : getGroupSeparationConstraint());

    GridBagConstraints c = getGroupConstraint(pos++, false, true);
    if (!inline && isText) {
      c.gridwidth = gridWidth - 2;// -2 -> label + edit/cancel button
      pos--;
    } else if (inline && !last && nbElement * 3 < gridWidth && (isText || isList)) {// We resize first element for inline, to feet panel
      c.gridwidth = (gridWidth - nbElement * 3) + 1;
      pos += 3;
    } else if (!isText && !isList) {
      c.gridwidth = gridWidth - 1;
    } else if (isList) {
      c.gridwidth = gridWidth - 2;
      pos = gridWidth - 1;
    }

    if (isText) {
      add(component, c);
    } else {
      WebScrollPane wscp = new WebScrollPane(component);
      wscp.setEnabled(false);
      add(wscp, c);
      if (!isList) {
        pos = 1;
      }
    }

    add(editor.getButton(), getGroupConstraint(inline ? pos++ : (gridWidth - pos), inline ? last : true, false));

    if (last) {
      pos = 0;
    }
  }

  @Override
  public final void UIEventHandler(UIEvent.Event event, IEventInfo info, Object object, Object param) {
    UISettings.LOGGER.finer(String.format("%s receive event %s %s", getClass().getSimpleName(), event, (info != null ? info : "")));

    switch (event) {
      case EDIT:
        for (UIEditor editor : map.values()) {
          editor.setEditable();
        }
        break;
      case EDITED:
        if (object != null && object instanceof MediaInfo.InfoProperty) {
          MediaInfo.InfoProperty property = (MediaInfo.InfoProperty) object;
          UIEditor editor = map.get(property);
          editor.setValue(param);
          map.put(property, editor);
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
