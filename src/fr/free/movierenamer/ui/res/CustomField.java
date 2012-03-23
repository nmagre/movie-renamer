/******************************************************************************
 *                                                                             *
 *    Movie Renamer                                                            *
 *    Copyright (C) 2011 Magré Nicolas                                         *
 *                                                                             *
 *    Movie Renamer is free software: you can redistribute it and/or modify    *
 *    it under the terms of the GNU General Public License as published by     *
 *    the Free Software Foundation, either version 3 of the License, or        *
 *    (at your option) any later version.                                      *
 *                                                                             *
 *    This program is distributed in the hope that it will be useful,          *
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            *
 *    GNU General Public License for more details.                             *
 *                                                                             *
 *    You should have received a copy of the GNU General Public License        *
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                             *
 ******************************************************************************/
package fr.free.movierenamer.ui.res;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 * Class CustomField
 * @author Nicolas Magré
 */
public class CustomField {

  private String initValue;
  private JTextComponent textComponent;
  private JButton cancelBtn;
  private Color color;

  /**
   * Constructor arguments
   * @param textComponent A text component
   * @param cancelBtn A button
   */
  public CustomField(JTextComponent textComponent, JButton cancelBtn) {
    initValue = "";
    this.textComponent = textComponent;
    this.cancelBtn = cancelBtn;
    color = textComponent.getForeground();

    this.textComponent.getDocument().addDocumentListener(new DocumentListener() {

      @Override
      public void changedUpdate(DocumentEvent e) {
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        CustomField.this.textComponent.setForeground(!initValue.equals(CustomField.this.textComponent.getText()) ?new Color(128, 138, 153):color);
        CustomField.this.cancelBtn.setEnabled(!initValue.equals(CustomField.this.textComponent.getText()));
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        CustomField.this.textComponent.setForeground(!initValue.equals(CustomField.this.textComponent.getText()) ? new Color(128, 138, 153):color);
        CustomField.this.cancelBtn.setEnabled(!initValue.equals(CustomField.this.textComponent.getText()));
      }
    });

    cancelBtn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        resetField();
      }
    });
  }

  /**
   * Reset text component with init value
   */
  public void resetField() {
    textComponent.setText(initValue);
  }

  /**
   * Set text
   * @param text Text to set in text component
   */
  public void setText(String text){
    textComponent.setText(text);
  }

  /**
   * Set init value
   * @param initText Text to set in text component
   */
  public void setInitValue(String initText){
    initValue = initText;
    textComponent.setText(initValue);
    cancelBtn.setEnabled(false);
  }
}
