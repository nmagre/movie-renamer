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
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.list.WebList;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.radiobutton.WebRadioButton;
import com.alee.laf.text.WebTextField;
import fr.free.movierenamer.info.NfoInfo;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.scrapper.TvShowScrapper;
import fr.free.movierenamer.scrapper.impl.ScrapperManager;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.settings.Settings.SettingsProperty;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.panel.PanelGenerator.Component;
import fr.free.movierenamer.ui.panel.setting.SettingPanelGen;
import fr.free.movierenamer.ui.res.UIScrapper;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.settings.UISettings.UISettingsProperty;
import fr.free.movierenamer.ui.settings.UISettings.UISupportedLanguage;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.Sorter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

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
  public enum SettingSubTitle {

    GENERAL,
    NFO,
    UPDATE,
    CACHE,
    LANGUAGE,
    MOVIESCRAPPER,
    TVSHOWSCRAPPER,
    SORTRESULT,
    INFORMATION;

    public String getName() {
      return this.name().toLowerCase();
    }
  }

  public static enum SettingsDefinition {// TODO add more constructor see PanelGenerator, ...

    // GENERAL
    selectFirstMedia(UISettingsProperty.selectFirstMedia, SettingCategory.GENERAL, SettingSubTitle.GENERAL),
    selectFirstResult(UISettingsProperty.selectFirstResult, SettingCategory.GENERAL, SettingSubTitle.GENERAL),
    scanSubfolder(UISettingsProperty.scanSubfolder, SettingCategory.GENERAL, SettingSubTitle.GENERAL),
    generateThumb(UISettingsProperty.generateThumb, SettingCategory.GENERAL, SettingSubTitle.GENERAL),
    generateFanart(UISettingsProperty.generateFanart, SettingCategory.GENERAL, SettingSubTitle.GENERAL),
    generateSubtitles(UISettingsProperty.generateSubtitles, SettingCategory.GENERAL, SettingSubTitle.GENERAL),
    useExtensionFilter(UISettingsProperty.useExtensionFilter, SettingCategory.GENERAL, SettingSubTitle.GENERAL),
    //extensionsList(UISettingsProperty.extensionsList, SettingCategory.GENERAL, SettingSubTitle.GENERAL, extensionsLst), // FIXME create a panel instead
    checkUpdate(UISettingsProperty.checkUpdate, SettingCategory.GENERAL, SettingSubTitle.UPDATE),
    cacheClear(SettingsProperty.cacheClear, SettingCategory.GENERAL, SettingSubTitle.CACHE),
    movieNfoType(SettingsProperty.movieNfoType, SettingCategory.GENERAL, SettingSubTitle.NFO, nfoRBtns, true),
    appLanguage(SettingsProperty.appLanguage, SettingCategory.GENERAL, SettingSubTitle.LANGUAGE, UIlanguageRBtns, true),
    // MEDIAINFO
    showMediaPanel(UISettingsProperty.showMediaPanel, SettingCategory.MEDIAINFO, SettingSubTitle.GENERAL),
    showActorImage(UISettingsProperty.showActorImage, SettingCategory.MEDIAINFO, SettingSubTitle.GENERAL),
    showThumb(UISettingsProperty.showThumb, SettingCategory.MEDIAINFO, SettingSubTitle.GENERAL),
    showFanart(UISettingsProperty.showFanart, SettingCategory.MEDIAINFO, SettingSubTitle.GENERAL),
    showSubtitle(UISettingsProperty.showSubtitle, SettingCategory.MEDIAINFO, SettingSubTitle.GENERAL),
    showCdart(UISettingsProperty.showCdart, SettingCategory.MEDIAINFO, SettingSubTitle.GENERAL),
    showClearart(UISettingsProperty.showClearart, SettingCategory.MEDIAINFO, SettingSubTitle.GENERAL),
    showLogo(UISettingsProperty.showLogo, SettingCategory.MEDIAINFO, SettingSubTitle.GENERAL),
    showBanner(UISettingsProperty.showBanner, SettingCategory.MEDIAINFO, SettingSubTitle.GENERAL),
    // SEARCH
    searchMovieScrapper(SettingsProperty.searchMovieScrapper, SettingCategory.SEARCH, SettingSubTitle.MOVIESCRAPPER, movieScrapperCb),
    searchTvshowScrapper(SettingsProperty.searchTvshowScrapper, SettingCategory.SEARCH, SettingSubTitle.TVSHOWSCRAPPER, tvshowScrapperCb),
    searchSubtitleScrapper(SettingsProperty.searchSubtitleScrapper, SettingCategory.SEARCH, SettingSubTitle.GENERAL),
    searchScrapperLang(SettingsProperty.searchScrapperLang, SettingCategory.SEARCH, SettingSubTitle.GENERAL),
    searchSorter(SettingsProperty.searchSort, SettingCategory.SEARCH, SettingSubTitle.GENERAL),
    searchNbResult(SettingsProperty.searchNbResult, SettingCategory.SEARCH, SettingSubTitle.SORTRESULT, searchSorters, false),
    searchDisplayApproximateResult(SettingsProperty.searchDisplayApproximateResult, SettingCategory.SEARCH, SettingSubTitle.GENERAL),
    // RENAME
    reservedCharacter(SettingsProperty.reservedCharacter, SettingCategory.RENAME, SettingSubTitle.GENERAL),
    movieFilenameFormat(SettingsProperty.movieFilenameFormat, SettingCategory.RENAME, SettingSubTitle.GENERAL),
    movieFilenameSeparator(SettingsProperty.movieFilenameSeparator, SettingCategory.RENAME, SettingSubTitle.GENERAL),
    movieFilenameLimit(SettingsProperty.movieFilenameLimit, SettingCategory.RENAME, SettingSubTitle.GENERAL),
    movieFilenameCase(SettingsProperty.movieFilenameCase, SettingCategory.RENAME, SettingSubTitle.GENERAL),
    movieFilenameTrim(SettingsProperty.movieFilenameTrim, SettingCategory.RENAME, SettingSubTitle.GENERAL),
    movieFilenameRmDupSpace(SettingsProperty.movieFilenameRmDupSpace, SettingCategory.RENAME, SettingSubTitle.GENERAL),
    movieFilenameCreateDirectory(SettingsProperty.movieFilenameCreateDirectory, SettingCategory.RENAME, SettingSubTitle.GENERAL),
    // IMAGE
    imageThumbName(UISettingsProperty.imageThumbName, SettingCategory.IMAGE, SettingSubTitle.GENERAL, thumbNameCb),
    imageThumbExt(UISettingsProperty.imageThumbExt, SettingCategory.IMAGE, SettingSubTitle.GENERAL, thumbExtCb),
    imageThumbResize(UISettingsProperty.imageThumbResize, SettingCategory.IMAGE, SettingSubTitle.GENERAL),
    imageThumbSize(UISettingsProperty.imageThumbSize, SettingCategory.IMAGE, SettingSubTitle.GENERAL),
    imageThumbWidth(UISettingsProperty.imageThumbWidth, SettingCategory.IMAGE, SettingSubTitle.GENERAL),
    imageThumbResizeLarger(UISettingsProperty.imageThumbResizeLarger, SettingCategory.IMAGE, SettingSubTitle.GENERAL),
    imageFanartName(UISettingsProperty.imageFanartName, SettingCategory.IMAGE, SettingSubTitle.GENERAL, fanartNameCb),
    imageFanartResize(UISettingsProperty.imageFanartResize, SettingCategory.IMAGE, SettingSubTitle.GENERAL),
    imageFanartWidth(UISettingsProperty.imageFanartWidth, SettingCategory.IMAGE, SettingSubTitle.GENERAL),
    imageFanartResizeLarger(UISettingsProperty.imageFanartResizeLarger, SettingCategory.IMAGE, SettingSubTitle.GENERAL),
    imageFanartSize(UISettingsProperty.imageFanartSize, SettingCategory.IMAGE, SettingSubTitle.GENERAL),
    // movie folder
    movieFolderFormat(SettingsProperty.movieFolderFormat, SettingCategory.RENAME, SettingSubTitle.GENERAL),
    movieFolderSeparator(SettingsProperty.movieFolderSeparator, SettingCategory.RENAME, SettingSubTitle.GENERAL),
    movieFolderLimit(SettingsProperty.movieFolderLimit, SettingCategory.RENAME, SettingSubTitle.GENERAL),
    movieFolderCase(SettingsProperty.movieFolderCase, SettingCategory.RENAME, SettingSubTitle.GENERAL),
    movieFolderTrim(SettingsProperty.movieFolderTrim, SettingCategory.RENAME, SettingSubTitle.GENERAL),
    movieFolderRmDupSpace(SettingsProperty.movieFolderRmDupSpace, SettingCategory.RENAME, SettingSubTitle.GENERAL),
    // tvShow
    tvShowFilenameFormat(SettingsProperty.tvShowFilenameFormat, SettingCategory.RENAME, SettingSubTitle.GENERAL),
    tvShowFilenameSeparator(SettingsProperty.tvShowFilenameSeparator, SettingCategory.RENAME, SettingSubTitle.GENERAL),
    tvShowFilenameLimit(SettingsProperty.tvShowFilenameLimit, SettingCategory.RENAME, SettingSubTitle.GENERAL),
    tvShowFilenameCase(SettingsProperty.tvShowFilenameCase, SettingCategory.RENAME, SettingSubTitle.GENERAL),
    tvShowFilenameTrim(SettingsProperty.tvShowFilenameTrim, SettingCategory.RENAME, SettingSubTitle.GENERAL),
    tvShowFilenameRmDupSpace(SettingsProperty.tvShowFilenameRmDupSpace, SettingCategory.RENAME, SettingSubTitle.GENERAL),
    // Proxy
    proxyIsOn(SettingsProperty.proxyIsOn, SettingCategory.NETWORK, SettingSubTitle.GENERAL),
    proxyUrl(SettingsProperty.proxyUrl, SettingCategory.NETWORK, SettingSubTitle.GENERAL),
    proxyPort(SettingsProperty.proxyPort, SettingCategory.NETWORK, SettingSubTitle.GENERAL),
    // http param
    httpRequestTimeOut(SettingsProperty.httpRequestTimeOut, SettingCategory.NETWORK, SettingSubTitle.GENERAL),
    httpCustomUserAgent(SettingsProperty.httpCustomUserAgent, SettingCategory.NETWORK, SettingSubTitle.GENERAL);
    private Settings.IProperty property;
    private String lib;
    private SettingCategory category;
    private PanelGenerator.Component component;
    private SettingSubTitle group;
    private int indent = 1;
    private List<JComponent> jcomponents = null;
    private JComponent jcomponent = null;
    private boolean horizontal = true;

    private SettingsDefinition(Settings.IProperty property, SettingCategory category, SettingSubTitle group) {
      this.property = property;
      this.category = category;
      this.group = group;
      if (property.getVclass().equals(Boolean.class)) {
        this.component = PanelGenerator.Component.CHECKBOX;
      } else if (property.getVclass().equals(String.class)) {
        this.component = PanelGenerator.Component.FIELD;
      } else if (property.getVclass().equals(Integer.class)) {
        this.component = PanelGenerator.Component.FIELD;
      } else {
        this.component = PanelGenerator.Component.UNKNOWN;
      }
    }

    private SettingsDefinition(Settings.IProperty property, SettingCategory category, SettingSubTitle group, int indent) {
      this(property, category, group);
      this.indent = indent;
    }

    private SettingsDefinition(Settings.IProperty property, SettingCategory category, SettingSubTitle group, Component component) {
      this(property, category, group);
      this.component = component;
    }

    private SettingsDefinition(Settings.IProperty property, SettingCategory category, SettingSubTitle group, List<JComponent> jcomponents, boolean horizontal) {
      this(property, category, group);
      this.jcomponents = jcomponents;
      this.horizontal = horizontal;
      this.component = Component.CUSTOM_LIST;
    }

    private SettingsDefinition(Settings.IProperty property, SettingCategory category, SettingSubTitle group, JComponent jcomponent) {
      this(property, category, group);
      this.jcomponent = jcomponent;
      this.component = Component.CUSTOM;
    }

    /**
     * @return the category
     */
    public SettingCategory getCategory() {
      return category;
    }

    /**
     * @return the group
     */
    public SettingSubTitle getSubTitle() {
      return group;
    }

    /**
     * @return the component
     */
    public PanelGenerator.Component getComponent() {
      return component;
    }

    public Class<?> getVclass() {
      return property.getVclass();
    }

    public Settings.IProperty getKey() {
      return property;
    }

    /**
     * @return the orientation horizontal
     */
    public boolean isHorizontal() {
      return horizontal;
    }

    /**
     * @return the array of jcomponents
     */
    public List<JComponent> getJComponentsList() {
      return Collections.unmodifiableList(jcomponents);
    }

    /**
     * @return the jcomponent
     */
    public JComponent getJComponent() {
      return jcomponent;
    }

    /**
     * @return the indent
     */
    public int getIndent() {
      return indent;
    }

    /**
     * @return the name
     */
    public String getName() {
      return name();
    }

    @Override
    public String toString() {
      return lib;
    }
  }
  // List
  private static WebList extensionsLst;
  // Button group
  private final ButtonGroup nfoGroup;
  private final ButtonGroup UIlanguageGroup;
  private final ButtonGroup searchSorterGroup;
  // List component
  private static List<JComponent> nfoRBtns;
  private static List<JComponent> UIlanguageRBtns;
  private static List<JComponent> searchSorters;
  //
  private final DefaultComboBoxModel movieScrapperModel;
  private final DefaultComboBoxModel tvshowScrapperModel;
  //
  private static WebComboBox movieScrapperCb;
  private static WebComboBox tvshowScrapperCb;
  private static WebComboBox thumbNameCb;
  private static WebComboBox thumbExtCb;
  private static WebComboBox fanartNameCb;

  //
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
    extensionsLst = new WebList(settings.getExtensionsList().toArray());
    initList(extensionsLst);

    nfoGroup = new ButtonGroup();
    UIlanguageGroup = new ButtonGroup();
    searchSorterGroup = new ButtonGroup();

    // Normal/Advanced setting rbtn
    //settingsGroup.setSelected(settings.isShowAdvancedSettings() ? advancedRbtn.getModel() : normalRbtn.getModel(), true);

    nfoRBtns = createRadioButtonList(NfoInfo.NFOtype.class, nfoGroup);
    UIlanguageRBtns = createRadioButtonList(UISupportedLanguage.class, UIlanguageGroup);
    searchSorters = createRadioButtonList(Sorter.SorterType.class, searchSorterGroup);

    movieScrapperModel = new DefaultComboBoxModel();
    tvshowScrapperModel = new DefaultComboBoxModel();

    for (MovieScrapper scrapper : ScrapperManager.getMovieScrapperList()) {
      movieScrapperModel.addElement(new UIScrapper(scrapper));
    }

    for (TvShowScrapper scrapper : ScrapperManager.getTvShowScrapperList()) {
      tvshowScrapperModel.addElement(new UIScrapper(scrapper));
    }

    thumbNameCb = createComboBox(UISettings.ThumbName.class);
    thumbExtCb = createComboBox(UISettings.ThumbExt.class);
    fanartNameCb = createComboBox(UISettings.FanartName.class);

    movieScrapperCb = new WebComboBox();
    movieScrapperCb.setRenderer(UIUtils.iconListRenderer);
    movieScrapperCb.setModel(movieScrapperModel);
    //movieScrapperCb.setSelectedItem(UIMovieScrapper);
    tvshowScrapperCb = new WebComboBox();
    tvshowScrapperCb.setRenderer(UIUtils.iconListRenderer);
    tvshowScrapperCb.setModel(tvshowScrapperModel);
    //movieScrapperCb.setSelectedItem(UIMovieScrapper);

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
   * Create list of WebRadioButton depends on enum passed
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
   * Create combobox depends on enum passed
   *
   * @param <T> Interface generic type
   * @param clazz Interface class
   * @return WebComboBox
   */
  private <T extends Enum<T>> WebComboBox createComboBox(Class<T> clazz) {
    WebComboBox cbb = new WebComboBox();
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    for (T e : clazz.getEnumConstants()) {
      model.addElement(e.name());
    }
    cbb.setModel(model);
    return cbb;
  }

  /**
   * Get list of list of SettingsDefinition for a SettingCategory
   *
   * @param catkey SettingCategory
   * @return list of list of SettingsDefinition
   */
  private List<List<SettingsDefinition>> getSettingsDefinition(SettingCategory catkey) {
    List<List<SettingsDefinition>> res = new ArrayList<List<SettingsDefinition>>();
    List<SettingSubTitle> keys = getSettingsGroup(catkey);
    for (SettingSubTitle key : keys) {
      List<SettingsDefinition> defKeys = new ArrayList<SettingsDefinition>();
      for (SettingsDefinition definition : SettingsDefinition.values()) {
        if (key.equals(definition.getSubTitle()) && catkey.equals(definition.getCategory())) {
          defKeys.add(definition);
        }
      }
      res.add(defKeys);
    }
    return res;
  }

  /**
   * Get list of SettingSubTitle for a SettingCategory
   *
   * @param key SettingCategory
   * @return list of SettingSubTitle
   */
  private List<SettingSubTitle> getSettingsGroup(SettingCategory key) {
    List<SettingSubTitle> defKeys = new ArrayList<SettingSubTitle>();
    for (SettingsDefinition definition : SettingsDefinition.values()) {
      if (key.equals(definition.getCategory()) && !defKeys.contains(definition.getSubTitle())) {
        defKeys.add(definition.getSubTitle());
      }
    }
    return defKeys;
  }

  private void initList(WebList jlist) {
    jlist.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    jlist.setLayoutOrientation(WebList.HORIZONTAL_WRAP);
    jlist.setVisibleRowCount(-1);
    jlist.setSelectedIndex(0);
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
          WebOptionPane.showMessageDialog(mr, LocaleUtils.i18n("saveSettingsFailed"), LocaleUtils.i18n("error"), JOptionPane.ERROR_MESSAGE);
          return;
        }
      }

      for (Map.Entry<SettingsDefinition, JComponent> field : fields.entrySet()) {
        try {
          field.getKey().getKey().setValue(((WebTextField) field.getValue()).getText());
        } catch (IOException ex) {
          UISettings.LOGGER.log(Level.SEVERE, null, ex);
          WebOptionPane.showMessageDialog(mr, LocaleUtils.i18n("saveSettingsFailed"), LocaleUtils.i18n("error"), JOptionPane.ERROR_MESSAGE);
          return;
        }
      }

      for (JComponent component : nfoRBtns) {
        if (((WebRadioButton) component).isSelected()) {
          SettingsProperty.movieNfoType.setValue(NfoInfo.NFOtype.valueOf(component.getName()));
          break;
        }
      }

      for (JComponent component : UIlanguageRBtns) {
        if (((WebRadioButton) component).isSelected()) {
          SettingsProperty.appLanguage.setValue(new Locale(UISupportedLanguage.valueOf(component.getName()).name()).getLanguage());
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
