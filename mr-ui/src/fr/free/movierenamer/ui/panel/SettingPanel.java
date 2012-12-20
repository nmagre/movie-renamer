/*
 * Copyright (C) 2012 duffy
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
package fr.free.movierenamer.ui.panel;

import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.radiobutton.WebRadioButton;
import com.alee.laf.text.WebTextField;
import fr.free.movierenamer.info.NfoInfo;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.panel.PanelGenerator.Component;
import fr.free.movierenamer.ui.panel.setting.SettingPanelGen;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.settings.UISettings.SettingsProperty;
import fr.free.movierenamer.ui.settings.UISettings.UISupportedLanguage;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDialog;

/**
 * Class Setting dialog
 *
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
public class SettingPanel extends JDialog {

  private static final int SUBLEVEL = 2;
  private static final long serialVersionUID = 1L;
  private static SettingPanel instance = null;

  // Panel
  public enum SettingCategory {

    GENERAL(new SettingPanelGen()),
    SEARCH(new SettingPanelGen()),
    RENAME(new SettingPanelGen()),
    MEDIAINFO(new SettingPanelGen()),
    IMAGE(new SettingPanelGen()),
    NETWORK(new SettingPanelGen());
    private SettingPanelGen panel;

    private SettingCategory(SettingPanelGen panel) {
      this.panel = panel;
    }

    public SettingPanelGen getPanel() {
      return panel;
    }

    public String getName() {
      return this.name().toLowerCase();
    }
  }

  // i18n key
  public enum SettingGroup {

    INTERFACE,
    NFO,
    UPDATE,
    LANGUAGE,
    INFORMATION;

    public String getName() {
      return this.name().toLowerCase();
    }
  }

  // i18n key
  public enum SettingsDefinition {

    // GENERAL
    selectFirstMedia(SettingsProperty.selectFirstMedia, SettingCategory.GENERAL, SettingGroup.INTERFACE, null),
    scanSubfolder(SettingsProperty.scanSubfolder, SettingCategory.GENERAL, SettingGroup.INTERFACE, null),
    movieNfoType(SettingsProperty.movieNfoType, SettingCategory.GENERAL, SettingGroup.NFO, nfoRBtns, true),
    checkUpdate(SettingsProperty.checkUpdate, SettingCategory.GENERAL, SettingGroup.UPDATE, null),
    appLanguage(SettingsProperty.appLanguage, SettingCategory.GENERAL, SettingGroup.LANGUAGE, UIlanguageRBtns, true),
    // MEDIAINFO
    showMediaPanel(SettingsProperty.showMediaPanel, SettingCategory.MEDIAINFO, SettingGroup.INFORMATION, null),
    showActorImage(SettingsProperty.showActorImage, SettingCategory.MEDIAINFO, SettingGroup.INFORMATION, null),
    //
    movieFilenameLimit(SettingsProperty.movieFilenameLimit, SettingCategory.RENAME, SettingGroup.INFORMATION, null, SUBLEVEL),
    showThumb(SettingsProperty.showThumb, SettingCategory.RENAME, SettingGroup.INFORMATION, null),
    showFanart(SettingsProperty.showFanart, SettingCategory.SEARCH, SettingGroup.INFORMATION, null),
    showSubtitle(SettingsProperty.showSubtitle, SettingCategory.IMAGE, SettingGroup.INFORMATION, null),
    showCdart(SettingsProperty.showCdart, SettingCategory.MEDIAINFO, SettingGroup.INFORMATION, null),
    showClearart(SettingsProperty.showClearart, SettingCategory.NETWORK, SettingGroup.INFORMATION, null),
    showLogo(SettingsProperty.showLogo, SettingCategory.RENAME, SettingGroup.INFORMATION, null),
    showBanner(SettingsProperty.showBanner, SettingCategory.RENAME, SettingGroup.INFORMATION, null);
    private UISettings.SettingsProperty property;
    private SettingCategory category;
    private PanelGenerator.Component component;
    private SettingGroup group;
    private int indent = 1;
    private String tooltip = null;
    private List<JComponent> jcomponents = null;
    private boolean horizontal = true;

    private SettingsDefinition(UISettings.SettingsProperty property, SettingCategory category, SettingGroup group, String tooltip) {
      this.property = property;
      this.category = category;
      this.group = group;
      this.tooltip = tooltip;
      if (property.getVClass().equals(Boolean.class)) {
        this.component = PanelGenerator.Component.CHECKBOX;
      } else if (property.getVClass().equals(String.class)) {
        this.component = PanelGenerator.Component.FIELD;
      } else if (property.getVClass().equals(Integer.class)) {
        this.component = PanelGenerator.Component.FIELD;
      } else {
        this.component = PanelGenerator.Component.UNKNOWN;
      }
    }

    private SettingsDefinition(SettingsProperty property, SettingCategory category, SettingGroup group, String tooltip, int indent) {
      this(property, category, group, tooltip);
      this.indent = indent;
    }

    private SettingsDefinition(SettingsProperty property, SettingCategory category, SettingGroup group, String tooltip, Component component) {
      this(property, category, group, tooltip);
      this.component = component;
    }

    private SettingsDefinition(SettingsProperty property, SettingCategory category, SettingGroup group, List<JComponent> jcomponents, boolean horizontal) {
      this(property, category, group, null);
      this.jcomponents = jcomponents;
      this.horizontal = horizontal;
      this.component = Component.CUSTOM;
    }

    /**
     * @return the category
     */
    public SettingCategory getCategory() {
      return category;
    }

    /**
     * @return the vclass
     */
    public Class<?> getVclass() {
      return property.getVClass();
    }

    /**
     * @return the indent
     */
    public int getIndent() {
      return indent;
    }

    /**
     * @return the component
     */
    public PanelGenerator.Component getComponent() {
      return component;
    }

    /**
     * @return the tooltip
     */
    public String getTooltip() {
      return tooltip;
    }

    /**
     * @return the key
     */
    public Settings.IProperty getKey() {
      return property.getKey();
    }

    /**
     * @return the level
     */
    public UISettings.SettingLevel getLevel() {
      return property.getLevel();
    }

    /**
     * @return the array of jcomponents
     */
    public List<JComponent> getJComponents() {
      return Collections.unmodifiableList(jcomponents);
    }

    /**
     * @return the orientation horizontal
     */
    public boolean getHorizontal() {
      return horizontal;
    }

    /**
     * @return the group
     */
    public SettingGroup getGroup() {
      return group;
    }

    /**
     * @return the name
     */
    public String getName() {
      return name().toLowerCase();
    }
  }
  // Button group
  private final ButtonGroup nfoGroup;
  private final ButtonGroup UIlanguageGroup;
  // List component
  private static List<JComponent> nfoRBtns;
  private static List<JComponent> UIlanguageRBtns;
  private final UISettings settings = UISettings.getInstance();
  private final MovieRenamer mr;

  public static SettingPanel getInstance(MovieRenamer mr) {
    if (instance == null) {
      instance = new SettingPanel(mr);
    }
    return instance;
  }

  /**
   * Creates new form Setting
   *
   * @param mr Movie Renamer main interface
   */
  private SettingPanel(MovieRenamer mr) {
    this.mr = mr;
    initComponents();
    nfoGroup = new ButtonGroup();
    UIlanguageGroup = new ButtonGroup();

    // Normal/Advanced setting rbtn
    settingsGroup.setSelected(settings.isShowAdvancedSettings() ? advancedRbtn.getModel() : normalRbtn.getModel(), true);

    nfoRBtns = createRadioButtonList(NfoInfo.NFOtype.class, nfoGroup);
    UIlanguageRBtns = createRadioButtonList(UISupportedLanguage.class, UIlanguageGroup);

    nfoGroup.setSelected(((WebRadioButton) nfoRBtns.get(settings.coreInstance.getMovieNfoType().ordinal())).getModel(), true);
    UIlanguageGroup.setSelected(((WebRadioButton) UIlanguageRBtns.get(UISupportedLanguage.valueOf(settings.coreInstance.getAppLanguage().getLanguage()).ordinal())).getModel(), true);

    for (SettingCategory settingcat : SettingCategory.values()) {
      SettingPanelGen panel = settingcat.getPanel();
      panel.addSettings(getSettingsDefinition(settingcat));
      webTabbedPane1.add(LocaleUtils.i18nExt(settingcat.getName()), panel);
    }
    pack();
    setTitle(LocaleUtils.i18nExt("settings"));
    setIconImage(UIUtils.LOGO_32);
  }

  /**
   * Create list of WebRadioButton depends on interface passed
   *
   * @param <T> Interface generic type
   * @param clazz Interface class
   * @param group Button group
   * @return List of jcomponents
   */
  private <T extends Enum<T>> List<JComponent> createRadioButtonList(Class<T> clazz, ButtonGroup group) {
    List<JComponent> components = new ArrayList<JComponent>();
    for (T e : clazz.getEnumConstants()) {
      WebRadioButton rbtn = new WebRadioButton(LocaleUtils.i18nExt(e.name()));
      rbtn.setName(e.name());
      rbtn.setToolTipText(LocaleUtils.i18nExt(e.name() + "tt"));
      group.add(rbtn);
      components.add(rbtn);
    }
    return components;
  }

  /**
   * Get list of list of SettingsDefinition for a SettingCategory
   *
   * @param catkey SettingCategory
   * @return list of list of SettingsDefinition
   */
  private List<List<SettingsDefinition>> getSettingsDefinition(SettingCategory catkey) {
    List<List<SettingsDefinition>> res = new ArrayList<List<SettingsDefinition>>();
    List<SettingGroup> keys = getSettingsGroup(catkey);
    for (SettingGroup key : keys) {
      List<SettingsDefinition> defKeys = new ArrayList<SettingsDefinition>();
      for (SettingsDefinition definition : SettingsDefinition.values()) {
        if (key.equals(definition.getGroup()) && catkey.equals(definition.getCategory())) {
          defKeys.add(definition);
        }
      }
      res.add(defKeys);
    }
    return res;
  }

  /**
   * Get list of SettingGroup for a SettingCategory
   *
   * @param key SettingCategory
   * @return list of SettingGroup
   */
  private List<SettingGroup> getSettingsGroup(SettingCategory key) {
    List<SettingGroup> defKeys = new ArrayList<SettingGroup>();
    for (SettingsDefinition definition : SettingsDefinition.values()) {
      if (key.equals(definition.getCategory()) && !defKeys.contains(definition.getGroup())) {
        defKeys.add(definition.getGroup());
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
    webLabel1 = new com.alee.laf.label.WebLabel();
    normalRbtn = new com.alee.laf.radiobutton.WebRadioButton();
    advancedRbtn = new com.alee.laf.radiobutton.WebRadioButton();
    saveBtn = new com.alee.laf.button.WebButton();
    cancelBtn = new com.alee.laf.button.WebButton();

    webLabel1.setText(LocaleUtils.i18nExt("settings")); // NOI18N
    webLabel1.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N

    settingsGroup.add(normalRbtn);
    normalRbtn.setText(LocaleUtils.i18nExt("normal")); // NOI18N
    normalRbtn.setFont(new java.awt.Font("Ubuntu", 0, 14)); // NOI18N

    settingsGroup.add(advancedRbtn);
    advancedRbtn.setText(LocaleUtils.i18nExt("advanced")); // NOI18N
    advancedRbtn.setFont(new java.awt.Font("Ubuntu", 0, 14)); // NOI18N

    javax.swing.GroupLayout webPanel1Layout = new javax.swing.GroupLayout(webPanel1);
    webPanel1.setLayout(webPanel1Layout);
    webPanel1Layout.setHorizontalGroup(
      webPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(webPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(webLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(normalRbtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(advancedRbtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(273, Short.MAX_VALUE))
    );
    webPanel1Layout.setVerticalGroup(
      webPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(webPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(webPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(webLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(normalRbtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(advancedRbtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    saveBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/ui/dialog-ok-2.png"))); // NOI18N
    saveBtn.setText(LocaleUtils.i18nExt("save")); // NOI18N
    saveBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        saveBtnActionPerformed(evt);
      }
    });

    cancelBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/ui/dialog-cancel-2.png"))); // NOI18N
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
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
      .addComponent(webPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(webPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(webTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(saveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

  private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
    dispose();
  }//GEN-LAST:event_cancelBtnActionPerformed

  private void saveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBtnActionPerformed
    for (SettingCategory settingcat : SettingCategory.values()) {
      SettingPanelGen panel = settingcat.getPanel();
      Map<SettingsDefinition, JComponent> checkboxs = panel.getCheckbox();
      Map<SettingsDefinition, JComponent> fields = panel.getField();

      for (Map.Entry<SettingsDefinition, JComponent> checkbox : checkboxs.entrySet()) {
        try {
          checkbox.getKey().getKey().setValue(((WebCheckBox) checkbox.getValue()).isSelected());
        } catch (IOException ex) {
          UISettings.LOGGER.log(Level.SEVERE, null, ex);
          // TODO show error
          return;
        }
      }

      for (Map.Entry<SettingsDefinition, JComponent> field : fields.entrySet()) {
        try {
          field.getKey().getKey().setValue(((WebTextField) field.getValue()).getText());
        } catch (IOException ex) {
          UISettings.LOGGER.log(Level.SEVERE, null, ex);
          // TODO show error
          return;
        }
      }

      for (JComponent component : nfoRBtns) {
        if (((WebRadioButton) component).isSelected()) {
          Settings.SettingsProperty.movieNfoType.setValue(NfoInfo.NFOtype.valueOf(component.getName()));
          break;
        }
      }

      for (JComponent component : UIlanguageRBtns) {
        if (((WebRadioButton) component).isSelected()) {
          Settings.SettingsProperty.appLanguage.setValue(new Locale(UISupportedLanguage.valueOf(component.getName()).name()).getLanguage());
          break;
        }
      }
    }

    mr.updateRenamedTitle();
    setVisible(false);
    dispose();
  }//GEN-LAST:event_saveBtnActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private com.alee.laf.radiobutton.WebRadioButton advancedRbtn;
  private com.alee.laf.button.WebButton cancelBtn;
  private com.alee.laf.radiobutton.WebRadioButton normalRbtn;
  private com.alee.laf.button.WebButton saveBtn;
  private javax.swing.ButtonGroup settingsGroup;
  private com.alee.laf.label.WebLabel webLabel1;
  private com.alee.laf.panel.WebPanel webPanel1;
  private com.alee.laf.tabbedpane.WebTabbedPane webTabbedPane1;
  // End of variables declaration//GEN-END:variables
}
