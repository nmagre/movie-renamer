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
package fr.free.movierenamer.ui.bean;

import fr.free.movierenamer.ui.event.UIEvent;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.managers.language.data.TooltipWay;
import com.alee.managers.tooltip.TooltipManager;
import fr.free.movierenamer.info.MediaInfo.InfoProperty;
import fr.free.movierenamer.info.MediaInfo.MultipleInfoProperty;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.swing.custom.ContextMenuField;
import fr.free.movierenamer.ui.swing.dialog.MultipleValueEditorDialog;
import fr.free.movierenamer.ui.swing.panel.info.InfoEditorPanel;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 * Class UIEditor
 *
 * @author Nicolas Magré
 */
public class UIEditor {

  private final WebButton cancelButton = new WebButton(ImageUtils.CANCEL_16);
  private final WebButton editButton = new WebButton(ImageUtils.EDIT_16);
  private final boolean isTextComponent;
  private final Color defaultColor;
  private final Color modifiedColor = new Color(29, 86, 171);
  private boolean editable = false;
  private final boolean multipleValue;
  private final WebButton button;
  private final MovieRenamer mr;
  private final InfoProperty property;
  //
  private DocumentListener docListener;
  private String defaultValue;
  private String value;
  private JComponent[] components;
  private JComponent editableComponent;

  public UIEditor(MovieRenamer mr, InfoProperty property, JComponent... components) {

    if (components == null || components.length == 0) {
      throw new UnsupportedOperationException();
    }

    this.components = new JComponent[components.length - 1];
    boolean supported = false;
    int pos = 0;
    for (JComponent component : components) {
      // Get first supported component as "editable component"
      if (!supported && (component instanceof JTextComponent || component instanceof JList)) {
        supported = true;
        this.editableComponent = component;
      } else {
        this.components[pos++] = component;
      }
    }

    if (!supported) {
      throw new UnsupportedOperationException();
    }

    this.mr = mr;
    this.multipleValue = property != null && property instanceof MultipleInfoProperty;
    this.property = property;
    isTextComponent = editableComponent instanceof JTextComponent;
    defaultColor = editableComponent.getForeground();
    defaultValue = null;
    value = null;

    button = multipleValue ? editButton : cancelButton;

    button.setEnabled(false);
    button.setVisible(false);
    setEnableComponents(false);

    if (!multipleValue) {
      createCancelListener();
    } else {
      createEditorListener();
    }

    if (isTextComponent) {
      ((JTextComponent) editableComponent).setEditable(false);
      ((JTextComponent) editableComponent).addMouseListener(new ContextMenuField());
    }
  }

  /**
   * Create editor button listener
   *
   * Open a dialog to edit values (only for multiple values)
   */
  private void createEditorListener() {
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        List<Object> values = new ArrayList<>();
        if (!isTextComponent) {
          WebList list = (WebList) editableComponent;
          ListModel model = list.getModel();
          int size = model.getSize();
          for (int i = 0; i < size; i++) {
            values.add(model.getElementAt(i));
          }
        } else {
          String value = ((JTextComponent) editableComponent).getText();
          values.addAll(Arrays.asList(value.split(", ")));
        }

        MultipleValueEditorDialog mvdial = new MultipleValueEditorDialog(mr, values, property);
        mvdial.setVisible(true);
      }
    });
  }

  /**
   * Create cancel button listener
   *
   * Set default value
   */
  private void createCancelListener() {
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        setValue(defaultValue);
        button.setEnabled(false);
      }
    });
  }

  /**
   * Create document listener
   *
   * If value is changed fire an event to update current UIMediaInfo value and
   * change font color
   *
   * @param textComponent A text component
   * @return DocumentListener
   */
  private DocumentListener createDocumentListener(final JTextComponent textComponent) {
    return new DocumentListener() {
      @Override
      public void changedUpdate(DocumentEvent e) {
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        edited(textComponent);
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        edited(textComponent);
      }
    };
  }

  /**
   * Change font color and update UIMediaInfo value
   *
   * @param textComponent A text component
   */
  private void edited(JTextComponent textComponent) {// TODO font color and fixe modified value
    textComponent.setForeground(!textComponent.getText().equals(defaultValue != null ? defaultValue : "") ? modifiedColor : defaultColor);
    cancelButton.setEnabled(!textComponent.getText().equals(defaultValue != null ? defaultValue : ""));

    if (cancelButton.isEnabled()) {
      WebLabel label = new WebLabel("", ImageUtils.CANCEL_16, SwingConstants.TRAILING);
      label.setLanguage(UIUtils.i18n.getLanguageKey("cancel", false));
      TooltipManager.setTooltip(cancelButton, label, TooltipWay.down);
    } else {
      TooltipManager.removeTooltips(editableComponent);
    }

    setEnableComponents(!textComponent.getText().isEmpty());
    if (property != null && !(property instanceof MultipleInfoProperty)) {
      value = textComponent.getText();
      // Refresh UIMediaInfo value
      UIEvent.fireUIEvent(UIEvent.Event.EDITED, InfoEditorPanel.class, null, property, value);
    }
    mr.updateRenamedTitle();
  }

  public JComponent getEditableComponent() {
    return editableComponent;
  }

  public List<JComponent> getComponents() {
    return Arrays.asList(components);
  }

  public boolean hasMoreComponents() {
    return components.length > 0;
  }

  public int nbMoreComponents() {
    return components.length;
  }

  public boolean isMultipleValue() {
      return multipleValue;
  }
  
  private void setEnableComponents(boolean enabled) {
    for (JComponent component : components) {
      component.setEnabled(enabled);
    }
  }

  @SuppressWarnings("unchecked")
  public void setValue(String value) {
    this.defaultValue = value;
    this.value = value;

    if (isTextComponent) {

      setEnableComponents(value != null ? !value.isEmpty() : false);

      ((JTextComponent) editableComponent).setText(value != null ? value : "");
      ((JTextComponent) editableComponent).setCaretPosition(0);
      ((JTextComponent) editableComponent).getDocument().addDocumentListener(docListener);
      ((JTextComponent) editableComponent).setCaretPosition(0);
    } else {
      ((DefaultListModel) ((JList) editableComponent).getModel()).addElement(value);
      setEnableComponents(true);
    }
  }

  public String getValue() {
    return value;
  }

  public void setEditable() {
    editable = !editable;
    button.setVisible(editable);

    if (multipleValue) {
      button.setEnabled(editable);
    }

    if (editable) {
      if (isTextComponent) {
        docListener = createDocumentListener((JTextComponent) editableComponent);
        ((JTextComponent) editableComponent).getDocument().addDocumentListener(docListener);
      }
    } else {
      if (isTextComponent) {
        ((JTextComponent) editableComponent).getDocument().removeDocumentListener(docListener);
      }
    }

    if (isTextComponent && !multipleValue) {
      ((JTextComponent) editableComponent).setEditable(editable);
    }
  }

  public void setEnabled(boolean enabled) {
    editableComponent.setEnabled(enabled);
    if (multipleValue) {
      button.setEnabled(enabled);
    }
  }

  public WebButton getButton() {
    return button;
  }

  public void reset() {
    defaultValue = null;
    if (isTextComponent) {
      ((JTextComponent) editableComponent).setText("");
      ((JTextComponent) editableComponent).getDocument().removeDocumentListener(docListener);
    } else {
      ((JList) editableComponent).removeAll();
    }

    setEnableComponents(false);
  }
}
