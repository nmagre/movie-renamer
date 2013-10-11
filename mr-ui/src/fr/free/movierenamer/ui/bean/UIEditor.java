/*
 * Movie Renamer
 * Copyright (C) 2012-2013 Nicolas Magré
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

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;
import fr.free.movierenamer.ui.swing.ContextMenuFieldMouseListener;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
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

  private JComponent component;
  private final WebButton cancelButton = new WebButton(ImageUtils.CANCEL_16);
  private final WebButton editButton = new WebButton(ImageUtils.EDIT_16);
  private final boolean isTextComponent;
  private final Color defaultColor;
  private final Color modifiedColor = new Color(29, 86, 171);
  private final Font defaultFont;
  private final Font modifiedFont = new Font("Ubuntu", Font.BOLD, 12);
  private DocumentListener docListener;
  private Object defaultValue;
  private boolean editable = false;
  private final boolean multipleValue;
  private final WebButton button;

  public UIEditor(JComponent component, boolean multipleValue) {
    if (!(component instanceof JTextComponent) && !(component instanceof JList)) {
      throw new UnsupportedOperationException();
    }

    this.component = component;
    this.multipleValue = multipleValue;
    isTextComponent = component instanceof JTextComponent;
    defaultColor = component.getForeground();
    defaultFont = component.getFont();
    defaultValue = null;

    button = multipleValue ? editButton : cancelButton;

    button.setEnabled(multipleValue);
    button.setVisible(false);

    if (!multipleValue) {
      createCancelListener();
    } else {
      // TODO
    }

    if (isTextComponent) {
      ((JTextComponent) component).setEditable(false);
      ((JTextComponent) component).addMouseListener(new ContextMenuFieldMouseListener());
    }
  }

  private void createCancelListener() {
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        setValue(defaultValue);
        button.setEnabled(false);
      }
    });
  }

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

  private void edited(JTextComponent textComponent) {
    textComponent.setForeground(!textComponent.getText().equals(defaultValue != null ? defaultValue : "") ? modifiedColor : defaultColor);
    textComponent.setFont(!textComponent.getText().equals(defaultValue != null ? defaultValue : "") ? modifiedFont : defaultFont);
    cancelButton.setEnabled(!textComponent.getText().equals(defaultValue != null ? defaultValue : ""));
    if (cancelButton.isEnabled()) {
      WebLabel label = new WebLabel("", ImageUtils.CANCEL_16, SwingConstants.TRAILING);
      label.setLanguage(UIUtils.i18n.getLanguageKey("cancel", false));
      TooltipManager.setTooltip(cancelButton, label, TooltipWay.down);
    } else {
      TooltipManager.removeTooltips(component);
    }
  }

  public JComponent getComponent() {
    return component;
  }

  public void setValue(Object value) {
    this.defaultValue = value;
    if (isTextComponent) {
      ((JTextComponent) component).setText(value != null ? value.toString() : "");
      ((JTextComponent) component).getDocument().addDocumentListener(docListener);
    } else {
      ((DefaultListModel) ((JList) component).getModel()).addElement(value);
    }
  }

  public void setEditable() {
    editable = !editable;
    button.setVisible(editable);

    if (editable) {
      if (isTextComponent) {
        docListener = createDocumentListener((JTextComponent) component);
        ((JTextComponent) component).getDocument().addDocumentListener(docListener);
      }
    } else {
      if (isTextComponent) {
        ((JTextComponent) component).getDocument().removeDocumentListener(docListener);
      }
    }

    if (isTextComponent && !multipleValue) {
      ((JTextComponent) component).setEditable(editable);
    }
  }

  public void setEnabled(boolean enabled) {
    component.setEnabled(enabled);
  }

  public WebButton getButton() {
    return button;
  }

  public void reset() {
    defaultValue = null;
    if (isTextComponent) {
      ((JTextComponent) component).setText("");
      ((JTextComponent) component).getDocument().removeDocumentListener(docListener);
    } else {
      ((JList) component).removeAll();
    }
  }
}
