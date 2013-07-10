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
package fr.free.movierenamer.ui.swing.panel.generator;

import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.text.WebTextField;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.settings.Settings.IProperty;
import fr.free.movierenamer.settings.Settings.SettingsProperty;
import fr.free.movierenamer.settings.Settings.SettingsSubType;
import fr.free.movierenamer.settings.Settings.SettingsType;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIEnum;
import fr.free.movierenamer.ui.bean.UIEvent;
import fr.free.movierenamer.ui.bean.UILang;
import fr.free.movierenamer.ui.bean.UIScrapper;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.settings.UISettings.UISettingsProperty;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.NumberUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * Class Setting dialog
 *
 * @author Nicolas Magré
 */
public class SettingPanel extends JDialog {

  private static final long serialVersionUID = 1L;
  private final UISettings settings = UISettings.getInstance();
  private final List<SettingPanelGen> panels;
  private final List<Settings.IProperty> properties;
  private final MovieRenamer mr;

  /**
   * Creates new form Setting
   *
   * @param mr Movie Renamer main interface
   */
  public SettingPanel(MovieRenamer mr) {
    this.mr = mr;
    panels = new ArrayList<SettingPanelGen>();
    properties = new ArrayList<Settings.IProperty>();

    initComponents();

    properties.addAll(Arrays.asList(UISettingsProperty.values()));
    properties.addAll(Arrays.asList(Settings.SettingsProperty.values()));

    for (SettingsType settingType : SettingsType.values()) {
      SettingPanelGen panel = new SettingPanelGen();
      panel.addSettings(getSettingsType(settingType), settingType.equals(SettingsType.RENAME));
      webTabbedPane1.add(LocaleUtils.i18nExt("settings." + settingType.name().toLowerCase()), panel);
      panels.add(panel);
    }

    pack();
    setModal(true);
    setLocationRelativeTo(mr);
    setTitle(UISettings.APPNAME + " " + LocaleUtils.i18nExt("settings"));
    setIconImage(ImageUtils.iconToImage(ImageUtils.LOGO_32));
  }

  /**
   * Get list of list of IProperty for a SettingsType
   *
   * @param type settings type
   * @return list of list of IProperty
   */
  private List<List<IProperty>> getSettingsType(SettingsType type) {
    List<List<IProperty>> res = new ArrayList<List<IProperty>>();
    List<SettingsSubType> keys = getSettingsSubType(type);
    for (SettingsSubType key : keys) {
      List<IProperty> defKeys = new ArrayList<IProperty>();
      for (IProperty definition : properties) {
        if (key.equals(definition.getSubType()) && type.equals(definition.getType())) {
          defKeys.add(definition);
        }
      }
      res.add(defKeys);
    }
    return res;
  }

  /**
   * Get list of SettingsSubType for a SettingsType
   *
   * @param subType settings subtype
   * @return list of SettingsSubType
   */
  private List<SettingsSubType> getSettingsSubType(SettingsType subType) {
    List<SettingsSubType> defKeys = new ArrayList<SettingsSubType>();
    for (IProperty definition : properties) {
      if (subType.equals(definition.getType()) && !defKeys.contains(definition.getSubType())) {
        defKeys.add(definition.getSubType());
      }
    }
    return defKeys;
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    settingsGroup = new javax.swing.ButtonGroup();
    webTabbedPane1 = new com.alee.laf.tabbedpane.WebTabbedPane();
    webPanel1 = new com.alee.laf.panel.WebPanel();
    resetBtn = new com.alee.laf.button.WebButton();
    saveBtn = new com.alee.laf.button.WebButton();
    cancelBtn = new com.alee.laf.button.WebButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    resetBtn.setIcon(ImageUtils.CLEAR_LIST_16);
    resetBtn.setText(LocaleUtils.i18nExt("Reset")); // NOI18N
    resetBtn.setFocusable(false);
    resetBtn.setRolloverDarkBorderOnly(true);
    resetBtn.setRolloverDecoratedOnly(true);
    resetBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        resetBtnActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout webPanel1Layout = new javax.swing.GroupLayout(webPanel1);
    webPanel1.setLayout(webPanel1Layout);
    webPanel1Layout.setHorizontalGroup(
      webPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(webPanel1Layout.createSequentialGroup()
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(resetBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
    webPanel1Layout.setVerticalGroup(
      webPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(webPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(resetBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    saveBtn.setIcon(ImageUtils.OK_24);
    saveBtn.setText(LocaleUtils.i18nExt("save")); // NOI18N
    saveBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        saveBtnActionPerformed(evt);
      }
    });

    cancelBtn.setIcon(ImageUtils.CANCEL_24);
    cancelBtn.setText(LocaleUtils.i18nExt("cancel")); // NOI18N
    cancelBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelBtnActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(webTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(saveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 387, Short.MAX_VALUE)
        .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
      .addComponent(webPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(webPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(webTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(saveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

  private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
    setVisible(false);
  }//GEN-LAST:event_cancelBtnActionPerformed

  @SuppressWarnings("unchecked")
  private void saveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBtnActionPerformed
    for (SettingPanelGen panel : panels) {
      Map<IProperty, WebCheckBox> checkboxs = panel.getCheckbox();
      Map<IProperty, WebTextField> fields = panel.getField();
      Map<IProperty, WebComboBox> comboboxs = panel.getCombobox();

      // Save checkbox
      for (Map.Entry<IProperty, WebCheckBox> checkbox : checkboxs.entrySet()) {
        try {
          IProperty property = checkbox.getKey();
          String olValue = property.getValue();
          property.setValue(checkbox.getValue().isSelected());
          UIEvent.fireUIEvent(UIEvent.Event.SETTINGS, olValue, property.getValue());
        } catch (IOException ex) {
          UISettings.LOGGER.log(Level.SEVERE, null, ex);
          WebOptionPane.showMessageDialog(mr, LocaleUtils.i18nExt("settings.saveSettingsFailed"), LocaleUtils.i18nExt("error"), JOptionPane.ERROR_MESSAGE);
          return;
        }
      }

      // Save text field
      for (Map.Entry<IProperty, WebTextField> field : fields.entrySet()) {
        try {
          IProperty property = field.getKey();
          String olValue = property.getValue();
          if(field.getKey().getDefaultValue() instanceof Number) {
            if(!NumberUtils.isNumeric(field.getValue().getText())) {
              WebOptionPane.showMessageDialog(mr, String.format(LocaleUtils.i18nExt("error.nan"), LocaleUtils.i18nExt("settings." + property.name().toLowerCase())), LocaleUtils.i18nExt("error"), JOptionPane.ERROR_MESSAGE);
              return;
            }
          }

          property.setValue(field.getValue().getText());
          UIEvent.fireUIEvent(UIEvent.Event.SETTINGS, olValue, property.getValue());
        } catch (IOException ex) {
          UISettings.LOGGER.log(Level.SEVERE, null, ex);
          WebOptionPane.showMessageDialog(mr, LocaleUtils.i18nExt("settings.saveSettingsFailed"), LocaleUtils.i18nExt("error"), JOptionPane.ERROR_MESSAGE);
          return;
        }
      }

      // Save combobox
      for (Map.Entry<IProperty, WebComboBox> combobox : comboboxs.entrySet()) {
        IProperty property = combobox.getKey();
        try {
          if (property.getVclass().isEnum()) {
            String oldValue = property.getValue();
            if (combobox.getValue().getSelectedItem() instanceof UILang) {
              property.setValue(((UILang) combobox.getValue().getSelectedItem()).getLanguage());
            } else {
              property.setValue(((UIEnum) combobox.getValue().getSelectedItem()).getValue());
            }

            UIEvent.fireUIEvent(UIEvent.Event.SETTINGS, oldValue, property.getValue());
          } else if (combobox.getValue().getSelectedItem() instanceof UIScrapper) {
            UIScrapper scrapper = (UIScrapper) combobox.getValue().getSelectedItem();
            Class<?> clazz = Class.forName(property.getValue().replace("class ", ""));
            settings.coreInstance.set((SettingsProperty) property, scrapper.getScrapper().getClass());
            UIEvent.fireUIEvent(UIEvent.Event.SETTINGS, clazz, scrapper.getScrapper().getClass());
          } else {
            UISettings.LOGGER.log(Level.SEVERE, String.format("Unknown property %s : Class %s", property.name(), property.getDefaultValue()));
            continue;
          }
        } catch (ClassNotFoundException ex) {
          Logger.getLogger(SettingPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
          UISettings.LOGGER.log(Level.SEVERE, null, ex);
          WebOptionPane.showMessageDialog(mr, LocaleUtils.i18nExt("settings.saveSettingsFailed"), LocaleUtils.i18n("error"), JOptionPane.ERROR_MESSAGE);
          return;
        }
      }

      /*for (JComponent component : languageRBtns) {// TODO Ask for restart app
       if (((WebRadioButton) component).isSelected()) {
       SettingsProperty.appLanguage.setValue(new Locale(UISupportedLanguage.valueOf(component.getName()).name()).getLanguage());
       break;
       }
       }*/
    }

    mr.updateRenamedTitle();
    setVisible(false);
    dispose();
  }//GEN-LAST:event_saveBtnActionPerformed

  private void resetBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBtnActionPerformed
    // TODO confirm dialog
    try {
      settings.clear();
    } catch (IOException ex) {
      UISettings.LOGGER.log(Level.SEVERE, null, ex);
      WebOptionPane.showMessageDialog(mr, LocaleUtils.i18nExt("settings.saveSettingsFailed"), LocaleUtils.i18nExt("error"), JOptionPane.ERROR_MESSAGE);
    }
  }//GEN-LAST:event_resetBtnActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private com.alee.laf.button.WebButton cancelBtn;
  private com.alee.laf.button.WebButton resetBtn;
  private com.alee.laf.button.WebButton saveBtn;
  private javax.swing.ButtonGroup settingsGroup;
  private com.alee.laf.panel.WebPanel webPanel1;
  private com.alee.laf.tabbedpane.WebTabbedPane webTabbedPane1;
  // End of variables declaration//GEN-END:variables
}
