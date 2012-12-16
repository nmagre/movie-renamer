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
package fr.free.movierenamer.ui;

import com.alee.laf.radiobutton.WebRadioButton;
import fr.free.movierenamer.info.NfoInfo;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.ui.panel.PanelGenerator;
import fr.free.movierenamer.ui.panel.PanelGenerator.Component;
import fr.free.movierenamer.ui.panel.setting.SettingPanel;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.settings.UISettings.SettingsProperty;
import fr.free.movierenamer.utils.LocaleUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDialog;

/**
 * Class Setting dialog
 *
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
public class Setting extends JDialog {

  private static final int SUBLEVEL = 2;
  private static final long serialVersionUID = 1L;

  // Panel
  public enum SettingCategory {

    GENERAL(new SettingPanel()),
    SEARCH(new SettingPanel()),
    RENAME(new SettingPanel()),
    MEDIAINFO(new SettingPanel()),
    IMAGE(new SettingPanel()),
    NETWORK(new SettingPanel());
    private SettingPanel panel;

    private SettingCategory(SettingPanel panel) {
      this.panel = panel;
    }

    public SettingPanel getPanel() {
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
    appLanguage(SettingsProperty.appLanguage, SettingCategory.GENERAL, SettingGroup.LANGUAGE, null),
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
        System.out.println("Error : " + property.name());
      }
    }

    private SettingsDefinition(SettingsProperty property, SettingCategory category, SettingGroup group, List<JComponent> jcomponents, boolean horizontal) {
      this.property = property;
      this.category = category;
      this.group = group;
      this.jcomponents = jcomponents;
      this.horizontal = horizontal;
      this.component = Component.CUSTOM;
    }

    private SettingsDefinition(SettingsProperty property, SettingCategory category, SettingGroup group, String tooltip, Component component) {
      this(property, category, group, tooltip);
      this.component = component;
    }

    private SettingsDefinition(SettingsProperty property, SettingCategory category, SettingGroup group, String tooltip, int indent) {
      this(property, category, group, tooltip);
      this.indent = indent;
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
     * @return the provider
     */
    public UISettings.SettingProvider getProvider() {
      return property.getProvider();
    }

    /**
     * @return the key
     */
    public Settings.iProperty getKey() {
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
  private static ButtonGroup nfoGroup;
  private static ButtonGroup languageGroup;
  // List component
  private static List<JComponent> nfoRBtns;
  private static List<JComponent> languageRBtns;

  /**
   * Creates new form Setting
   */
  public Setting() {
    initComponents();
    nfoGroup = new ButtonGroup();
    languageGroup = new ButtonGroup();
    nfoRBtns = createList(NfoInfo.NFOtype.class, nfoGroup);
    //languageRBtns = createList(, nfoGroup);

    nfoGroup.setSelected(((WebRadioButton) nfoRBtns.get(Settings.getInstance().getMovieNfoType().ordinal())).getModel(), true);

    for (SettingCategory settingcat : SettingCategory.values()) {
      SettingPanel panel = settingcat.getPanel();
      panel.addSettings(getSettingsDefinition(settingcat));
      webTabbedPane1.add(LocaleUtils.i18nExt(settingcat.getName()), panel);
    }
    pack();
  }

  private <T extends Enum<T>> List<JComponent> createList(Class<T> clazz, ButtonGroup group) {
    List<JComponent> components = new ArrayList<JComponent>();
    for (T e : clazz.getEnumConstants()) {
      WebRadioButton rbtn = new WebRadioButton(LocaleUtils.i18nExt(e.name()));
      rbtn.setToolTipText(LocaleUtils.i18nExt(e.name() + "tt"));
      group.add(rbtn);
      components.add(rbtn);
    }
    return components;
  }

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

    webTabbedPane1 = new com.alee.laf.tabbedpane.WebTabbedPane();

    getContentPane().add(webTabbedPane1, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private com.alee.laf.tabbedpane.WebTabbedPane webTabbedPane1;
  // End of variables declaration//GEN-END:variables
}
