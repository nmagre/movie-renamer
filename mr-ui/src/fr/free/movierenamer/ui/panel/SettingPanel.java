/*
 * Copyright (C) 2012 Nicolas Magré
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
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.scrapper.TvShowScrapper;
import fr.free.movierenamer.scrapper.impl.ScrapperManager;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.settings.Settings.SettingsProperty;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.list.UIScrapper;
import fr.free.movierenamer.ui.panel.generator.PanelGenerator;
import fr.free.movierenamer.ui.panel.generator.PanelGenerator.Component;
import fr.free.movierenamer.ui.panel.generator.SettingPanelGen;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.settings.UISettings.SettingPropertyChange;
import fr.free.movierenamer.ui.settings.UISettings.UISettingsProperty;
import fr.free.movierenamer.ui.settings.UISettings.UISupportedLanguage;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
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
  //private static SettingPanel instance = null;

  // Panel
  public enum Category {

    GENERAL,
    SEARCH,
    RENAME,
    MEDIAINFO,
    IMAGE,
    NETWORK,
    ABOUT;

    public String getName() {
      return this.name().toLowerCase();
    }
  }

  // i18n key, i18n key help
  public enum SubTitle {

    GENERAL("helpGeneral"),
    NFO("helpNfo"),
    UPDATE(null),
    CACHE(null),
    LANGUAGE(null),
    SCRAPPER("helpScrapper"),
    SORTRESULT("helpSortResult"),
    INFORMATION("helpInformation"),
    INTERFACE(null);
    private final String help;

    private SubTitle(String help) {
      this.help = help;
    }

    public String getName() {
      return this.name().toLowerCase();
    }

    public String getHelp() {
      return help;
    }
  }

  public static enum SettingsDefinition {// TODO add more constructor see PanelGenerator, ...

    // GENERAL
    selectFirstMedia(UISettingsProperty.selectFirstMedia, Category.GENERAL, SubTitle.GENERAL),
    selectFirstResult(UISettingsProperty.selectFirstResult, Category.GENERAL, SubTitle.GENERAL),
    scanSubfolder(UISettingsProperty.scanSubfolder, Category.GENERAL, SubTitle.GENERAL),
    generateThumb(UISettingsProperty.generateThumb, Category.GENERAL, SubTitle.GENERAL),
    generateFanart(UISettingsProperty.generateFanart, Category.GENERAL, SubTitle.GENERAL),
    generateSubtitles(UISettingsProperty.generateSubtitles, Category.GENERAL, SubTitle.GENERAL),
    useExtensionFilter(UISettingsProperty.useExtensionFilter, Category.GENERAL, SubTitle.GENERAL),
    groupMediaList(UISettingsProperty.groupMediaList, Category.GENERAL, SubTitle.INTERFACE),
    showIconMediaList(UISettingsProperty.showIconMediaList, Category.GENERAL, SubTitle.INTERFACE),
    showFormatField(UISettingsProperty.showIconMediaList, Category.GENERAL, SubTitle.INTERFACE),
    //extensionsList(UISettingsProperty.extensionsList, Category.GENERAL, SubTitle.GENERAL, extensionsLst), // FIXME create a panel instead
    checkUpdate(UISettingsProperty.checkUpdate, Category.GENERAL, SubTitle.UPDATE),
    cacheClear(SettingsProperty.cacheClear, Category.GENERAL, SubTitle.CACHE),
    movieNfoType(SettingsProperty.movieNfoType, Category.GENERAL, SubTitle.NFO, nfoRBtns, true),
    appLanguage(SettingsProperty.appLanguage, Category.GENERAL, SubTitle.LANGUAGE, languageRBtns, true),
    // MEDIAINFO
    showMediaPanel(UISettingsProperty.showMediaPanel, Category.MEDIAINFO, SubTitle.GENERAL),
    showActorImage(UISettingsProperty.showActorImage, Category.MEDIAINFO, SubTitle.GENERAL),
    showThumb(UISettingsProperty.showThumb, Category.MEDIAINFO, SubTitle.GENERAL),
    showFanart(UISettingsProperty.showFanart, Category.MEDIAINFO, SubTitle.GENERAL),
    showSubtitle(UISettingsProperty.showSubtitle, Category.MEDIAINFO, SubTitle.GENERAL),
    showCdart(UISettingsProperty.showCdart, Category.MEDIAINFO, SubTitle.GENERAL),
    showClearart(UISettingsProperty.showClearart, Category.MEDIAINFO, SubTitle.GENERAL),
    showLogo(UISettingsProperty.showLogo, Category.MEDIAINFO, SubTitle.GENERAL),
    showBanner(UISettingsProperty.showBanner, Category.MEDIAINFO, SubTitle.GENERAL),
    // SEARCH
    searchMovieScrapper(SettingsProperty.searchMovieScrapper, Category.SEARCH, SubTitle.SCRAPPER, movieScrapperCb),
    searchMovieScrapperLang(SettingsProperty.searchMovieScrapperLang, Category.SEARCH, SubTitle.SCRAPPER, searchMovieScrapperLangRBtns, true),
    searchTvshowScrapper(SettingsProperty.searchTvshowScrapper, Category.SEARCH, SubTitle.SCRAPPER, tvshowScrapperCb),
    searchTvshowScrapperLang(SettingsProperty.searchTvshowScrapperLang, Category.SEARCH, SubTitle.SCRAPPER, searchtvshowScrapperLangRBtns, true),
    searchSubtitleScrapper(SettingsProperty.searchSubtitleScrapper, Category.SEARCH, SubTitle.GENERAL),
    searchSorter(SettingsProperty.searchSort, Category.SEARCH, SubTitle.SORTRESULT, searchSorterRBtns, false),
    searchNbResult(SettingsProperty.searchNbResult, Category.SEARCH, SubTitle.GENERAL),
    searchDisplayApproximateResult(SettingsProperty.searchDisplayApproximateResult, Category.SEARCH, SubTitle.GENERAL),
    // RENAME
    reservedCharacter(SettingsProperty.reservedCharacter, Category.RENAME, SubTitle.GENERAL),
    movieFilenameFormat(SettingsProperty.movieFilenameFormat, Category.RENAME, SubTitle.GENERAL),
    movieFilenameSeparator(SettingsProperty.movieFilenameSeparator, Category.RENAME, SubTitle.GENERAL),
    movieFilenameLimit(SettingsProperty.movieFilenameLimit, Category.RENAME, SubTitle.GENERAL),
    movieFilenameCase(SettingsProperty.movieFilenameCase, Category.RENAME, SubTitle.GENERAL),
    movieFilenameTrim(SettingsProperty.movieFilenameTrim, Category.RENAME, SubTitle.GENERAL),
    movieFilenameRmDupSpace(SettingsProperty.movieFilenameRmDupSpace, Category.RENAME, SubTitle.GENERAL),
    movieFilenameCreateDirectory(SettingsProperty.movieFilenameCreateDirectory, Category.RENAME, SubTitle.GENERAL),
    // IMAGE
    imageThumbName(UISettingsProperty.imageThumbName, Category.IMAGE, SubTitle.GENERAL, thumbNameCb),
    imageThumbExt(UISettingsProperty.imageThumbExt, Category.IMAGE, SubTitle.GENERAL, thumbExtCb),
    imageThumbSize(UISettingsProperty.imageThumbSize, Category.IMAGE, SubTitle.GENERAL, thumbSizeCb),
    imageThumbResize(UISettingsProperty.imageThumbResize, Category.IMAGE, SubTitle.GENERAL),
    imageThumbWidth(UISettingsProperty.imageThumbWidth, Category.IMAGE, SubTitle.GENERAL, SUBLEVEL),
    imageFanartName(UISettingsProperty.imageFanartName, Category.IMAGE, SubTitle.GENERAL, fanartNameCb),
    imageFanartResize(UISettingsProperty.imageFanartResize, Category.IMAGE, SubTitle.GENERAL),
    imageFanartWidth(UISettingsProperty.imageFanartWidth, Category.IMAGE, SubTitle.GENERAL, SUBLEVEL),
    imageFanartSize(UISettingsProperty.imageFanartSize, Category.IMAGE, SubTitle.GENERAL, fanartSizeCb),
    // movie folder
    movieFolderFormat(SettingsProperty.movieFolderFormat, Category.RENAME, SubTitle.GENERAL),
    movieFolderSeparator(SettingsProperty.movieFolderSeparator, Category.RENAME, SubTitle.GENERAL),
    movieFolderLimit(SettingsProperty.movieFolderLimit, Category.RENAME, SubTitle.GENERAL),
    movieFolderCase(SettingsProperty.movieFolderCase, Category.RENAME, SubTitle.GENERAL),
    movieFolderTrim(SettingsProperty.movieFolderTrim, Category.RENAME, SubTitle.GENERAL),
    movieFolderRmDupSpace(SettingsProperty.movieFolderRmDupSpace, Category.RENAME, SubTitle.GENERAL),
    // tvShow
    tvShowFilenameFormat(SettingsProperty.tvShowFilenameFormat, Category.RENAME, SubTitle.GENERAL),
    tvShowFilenameSeparator(SettingsProperty.tvShowFilenameSeparator, Category.RENAME, SubTitle.GENERAL),
    tvShowFilenameLimit(SettingsProperty.tvShowFilenameLimit, Category.RENAME, SubTitle.GENERAL),
    tvShowFilenameCase(SettingsProperty.tvShowFilenameCase, Category.RENAME, SubTitle.GENERAL),
    tvShowFilenameTrim(SettingsProperty.tvShowFilenameTrim, Category.RENAME, SubTitle.GENERAL),
    tvShowFilenameRmDupSpace(SettingsProperty.tvShowFilenameRmDupSpace, Category.RENAME, SubTitle.GENERAL),
    // Proxy
    proxyIsOn(SettingsProperty.proxyIsOn, Category.NETWORK, SubTitle.GENERAL),
    proxyUrl(SettingsProperty.proxyUrl, Category.NETWORK, SubTitle.GENERAL, SUBLEVEL),
    proxyPort(SettingsProperty.proxyPort, Category.NETWORK, SubTitle.GENERAL, SUBLEVEL),
    // http param
    httpRequestTimeOut(SettingsProperty.httpRequestTimeOut, Category.NETWORK, SubTitle.GENERAL, SUBLEVEL),
    httpCustomUserAgent(SettingsProperty.httpCustomUserAgent, Category.NETWORK, SubTitle.GENERAL, SUBLEVEL);
    private Settings.IProperty property;
    private String lib;
    private Category category;
    private PanelGenerator.Component component;
    private SubTitle subTitle;
    private int indent = 1;
    private List<JComponent> jcomponents = null;
    private JComponent jcomponent = null;
    private boolean horizontal = true;

    private SettingsDefinition(Settings.IProperty property, Category category, SubTitle subTitle) {
      this.property = property;
      this.category = category;
      this.subTitle = subTitle;
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

    private SettingsDefinition(Settings.IProperty property, Category category, SubTitle subTitle, int indent) {
      this(property, category, subTitle);
      this.indent = indent;
    }

    private SettingsDefinition(Settings.IProperty property, Category category, SubTitle subTitle, Component component) {
      this(property, category, subTitle);
      this.component = component;
    }

    private SettingsDefinition(Settings.IProperty property, Category category, SubTitle subTitle, List<JComponent> jcomponents, boolean horizontal) {
      this(property, category, subTitle);
      this.jcomponents = jcomponents;
      this.horizontal = horizontal;
      this.component = Component.CUSTOM_LIST;
    }

    private SettingsDefinition(Settings.IProperty property, Category category, SubTitle subTitle, JComponent jcomponent) {
      this(property, category, subTitle);
      this.jcomponent = jcomponent;
      this.component = Component.CUSTOM;
    }

    public Category getCategory() {
      return category;
    }

    public SubTitle getSubTitle() {
      return subTitle;
    }

    public PanelGenerator.Component getComponent() {
      return component;
    }

    public Class<?> getVclass() {
      return property.getVclass();
    }

    public Settings.IProperty getKey() {
      return property;
    }

    public boolean isHorizontal() {
      return horizontal;
    }

    public List<JComponent> getJComponentsList() {
      return Collections.unmodifiableList(jcomponents);
    }

    public JComponent getJComponent() {
      return jcomponent;
    }

    public int getIndent() {
      return indent;
    }

    public String getName() {
      return name();
    }

    @Override
    public String toString() {
      return lib;
    }
  }
  // GENERAL
  private static List<JComponent> nfoRBtns;
  private static List<JComponent> languageRBtns;
  // SEARCH
  private static WebComboBox movieScrapperCb;
  private static WebComboBox tvshowScrapperCb;
  private final DefaultComboBoxModel movieScrapperModel;
  private final DefaultComboBoxModel tvshowScrapperModel;
  private static List<JComponent> searchMovieScrapperLangRBtns;
  private static List<JComponent> searchtvshowScrapperLangRBtns;
  // List component
  private static List<JComponent> searchSorterRBtns;
  // IMAGE
  private static WebComboBox thumbNameCb;
  private static WebComboBox thumbExtCb;
  private static WebComboBox thumbSizeCb;
  private static WebComboBox fanartNameCb;
  private static WebComboBox fanartSizeCb;
  // Misc
  private final UISettings settings = UISettings.getInstance();
  private final List<SettingPanelGen> panels;
  private final Map<List<JComponent>, PropertyClass> radioBtns;
  private final Map<PropertyClass, WebComboBox> comboboxs;
  private final MovieRenamer mr;
  private final PropertyChangeSupport settingsChange;

  /**
   * Creates new form Setting
   *
   * @param mr Movie Renamer main interface
   * @param settingsChange
   */
  public SettingPanel(MovieRenamer mr, PropertyChangeSupport settingsChange) {
    this.mr = mr;
    this.settingsChange = settingsChange;
    panels = new ArrayList<SettingPanelGen>();
    initComponents();

    // Normal/Advanced setting rbtn
    settingsGroup.setSelected(settings.isShowAdvancedSettings() ? advancedRbtn.getModel() : normalRbtn.getModel(), true);
    radioBtns = new LinkedHashMap<List<JComponent>, PropertyClass>();
    comboboxs = new LinkedHashMap<PropertyClass, WebComboBox>();

    // GENERAL
    nfoRBtns = createRadioButtonList(settings.coreInstance.getMovieNfoType(), SettingsProperty.movieNfoType);
    languageRBtns = createRadioButtonList(UISupportedLanguage.valueOf(settings.coreInstance.getAppLanguage().getLanguage()), null);

    for (JComponent compo : nfoRBtns) {// FIXME
      Icon icon = ImageUtils.getIconFromJar("mediacenter/" + compo.getName().toLowerCase() + ".png");
      ((WebRadioButton) compo).setIcon(icon);
    }

    // SEARCH
    AvailableLanguages selected;
    List<AvailableLanguages> languages;

    languages = ScrapperManager.getMovieScrapper().getSupportedLanguages();
    selected = getSearchLangSelected(settings.coreInstance.getSearchMovieScrapperLang().getLanguage(), languages);
    searchMovieScrapperLangRBtns = createRadioButtonList(selected, Settings.SettingsProperty.searchMovieScrapperLang);
    setSearchScrapperLangRbtn(searchMovieScrapperLangRBtns, selected, languages);

    languages = ScrapperManager.getTvShowScrapper().getSupportedLanguages();
    selected = getSearchLangSelected(settings.coreInstance.getSearchTvshowScrapperLang().getLanguage(), languages);
    searchtvshowScrapperLangRBtns = createRadioButtonList(selected, Settings.SettingsProperty.searchTvshowScrapperLang);
    setSearchScrapperLangRbtn(searchtvshowScrapperLangRBtns, selected, languages);

    searchSorterRBtns = createRadioButtonList(settings.coreInstance.getSearchSorter(), SettingsProperty.searchSort);
    movieScrapperModel = new DefaultComboBoxModel();
    tvshowScrapperModel = new DefaultComboBoxModel();

    for (MovieScrapper scrapper : ScrapperManager.getMovieScrapperList()) {
      movieScrapperModel.addElement(new UIScrapper(scrapper));
    }

    for (TvShowScrapper scrapper : ScrapperManager.getTvShowScrapperList()) {
      tvshowScrapperModel.addElement(new UIScrapper(scrapper));
    }

    movieScrapperCb = new WebComboBox();
    movieScrapperCb.setRenderer(UIUtils.iconListRenderer);
    movieScrapperCb.setModel(movieScrapperModel);
    movieScrapperModel.setSelectedItem(new UIScrapper(ScrapperManager.getMovieScrapper()));
    movieScrapperCb.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        List<AvailableLanguages> lang = ((UIScrapper) movieScrapperCb.getSelectedItem()).getScrapper().getSupportedLanguages();
        setSearchScrapperLangRbtn(searchMovieScrapperLangRBtns, settings.coreInstance.getSearchMovieScrapperLang().getLanguage(), lang);
      }
    });

    tvshowScrapperCb = new WebComboBox();
    tvshowScrapperCb.setRenderer(UIUtils.iconListRenderer);
    tvshowScrapperCb.setModel(tvshowScrapperModel);
    tvshowScrapperModel.setSelectedItem(new UIScrapper(ScrapperManager.getTvShowScrapper()));
    tvshowScrapperCb.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        List<AvailableLanguages> lang = ((UIScrapper) tvshowScrapperCb.getSelectedItem()).getScrapper().getSupportedLanguages();
        setSearchScrapperLangRbtn(searchtvshowScrapperLangRBtns, settings.coreInstance.getSearchTvshowScrapperLang().getLanguage(), lang);
      }
    });

    // IMAGE
    thumbNameCb = createComboBox(settings.getImageThumbName(), UISettingsProperty.imageThumbName);
    thumbExtCb = createComboBox(settings.getImageThumbExt(), UISettingsProperty.imageThumbExt);
    thumbSizeCb = createComboBox(settings.getImageThumbSize(), UISettingsProperty.imageThumbSize);
    fanartNameCb = createComboBox(settings.getImageFanartName(), UISettingsProperty.imageFanartName);
    fanartSizeCb = createComboBox(settings.getImageFanartSize(), UISettingsProperty.imageFanartSize);

    for (Category settingcat : Category.values()) {
      SettingPanelGen panel = new SettingPanelGen();
      panel.addSettings(getSettingsDefinition(settingcat));
      webTabbedPane1.add(LocaleUtils.i18nExt(settingcat.getName()), panel);
      panels.add(panel);
    }
    pack();
    setModal(true);
    setTitle(UISettings.APPNAME + " " + LocaleUtils.i18nExt("settings"));
    setIconImage(ImageUtils.iconToImage(ImageUtils.LOGO_32));
  }

  private void setSearchScrapperLangRbtn(List<JComponent> components, AvailableLanguages selected, List<AvailableLanguages> languages) {
    for (JComponent component : components) {
      component.setEnabled(isInLangList(languages, component.getName()));
      if (component.getName().equals(selected.getLocale().getLanguage())) {
        ((WebRadioButton) component).setSelected(true);
      }
    }
  }

  private void setSearchScrapperLangRbtn(List<JComponent> components, String lang, List<AvailableLanguages> languages) {
    AvailableLanguages selected = getSearchLangSelected(lang, languages);
    setSearchScrapperLangRbtn(components, selected, languages);
  }

  private AvailableLanguages getSearchLangSelected(String lang, List<AvailableLanguages> languages) {
    AvailableLanguages selected = AvailableLanguages.valueOf(lang);
    if (!languages.contains(selected)) {
      selected = languages.get(0);
    }
    return selected;
  }

  private boolean isInLangList(List<AvailableLanguages> languages, String name) {
    for (AvailableLanguages lang : languages) {
      if (lang.getLocale().getLanguage().equals(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Create list of WebRadioButton depends on enum passed
   *
   * @param <T> enum generic type
   * @param subTitle Button subTitle
   * @return List of jcomponents
   */
  private <T extends Enum<T>> List<JComponent> createRadioButtonList(T selected, Settings.IProperty property) {
    List<JComponent> components = new ArrayList<JComponent>();
    ButtonGroup group = new ButtonGroup();
    Class<T> clazz = selected.getDeclaringClass();
    for (T e : clazz.getEnumConstants()) {
      WebRadioButton rbtn = new WebRadioButton(LocaleUtils.i18nExt(e.name()));
      rbtn.setName(e.name());
      group.add(rbtn);
      components.add(rbtn);
      if (e.equals(selected)) {
        group.setSelected(rbtn.getModel(), true);
      }
    }
    radioBtns.put(components, new PropertyClass(property, clazz));
    return components;
  }

  /**
   * Create combobox depends on enum passed
   *
   * @param <T> enum generic type
   * @param clazz enum class
   * @return WebComboBox
   */
  private <T extends Enum<T>> WebComboBox createComboBox(T selected, Settings.IProperty property) {
    WebComboBox cbb = new WebComboBox();
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    Class<T> clazz = selected.getDeclaringClass();
    for (T e : clazz.getEnumConstants()) {
      model.addElement(e.name());
      if (e.equals(selected)) {
        model.setSelectedItem(e.name());
      }
    }
    cbb.setModel(model);
    comboboxs.put(new PropertyClass(property, clazz), cbb);
    return cbb;
  }

  /**
   * Get list of list of SettingsDefinition for a Category
   *
   * @param catkey Category
   * @return list of list of SettingsDefinition
   */
  private List<List<SettingsDefinition>> getSettingsDefinition(Category catkey) {
    List<List<SettingsDefinition>> res = new ArrayList<List<SettingsDefinition>>();
    List<SubTitle> keys = getSettingsGroup(catkey);
    for (SubTitle key : keys) {
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
   * Get list of SubTitle for a Category
   *
   * @param key Category
   * @return list of SubTitle
   */
  private List<SubTitle> getSettingsGroup(Category key) {
    List<SubTitle> defKeys = new ArrayList<SubTitle>();
    for (SettingsDefinition definition : SettingsDefinition.values()) {
      if (key.equals(definition.getCategory()) && !defKeys.contains(definition.getSubTitle())) {
        defKeys.add(definition.getSubTitle());
      }
    }
    return defKeys;
  }

  private class PropertyClass {

    private final Settings.IProperty property;
    private final Class<? extends Enum<?>> clazz;

    public PropertyClass(Settings.IProperty property, Class<? extends Enum<?>> clazz) {
      this.property = property;
      this.clazz = clazz;
    }

    public Class<? extends Enum<?>> getClazz() {
      return clazz;
    }

    public Settings.IProperty getProperty() {
      return property;
    }
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
    resetBtn = new com.alee.laf.button.WebButton();
    saveBtn = new com.alee.laf.button.WebButton();
    cancelBtn = new com.alee.laf.button.WebButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    webLabel1.setText(LocaleUtils.i18nExt("settings")); // NOI18N
    webLabel1.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N

    settingsGroup.add(normalRbtn);
    normalRbtn.setText(LocaleUtils.i18nExt("normal")); // NOI18N
    normalRbtn.setFont(new java.awt.Font("Ubuntu", 0, 14)); // NOI18N

    settingsGroup.add(advancedRbtn);
    advancedRbtn.setText(LocaleUtils.i18nExt("advanced")); // NOI18N
    advancedRbtn.setFont(new java.awt.Font("Ubuntu", 0, 14)); // NOI18N

    resetBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/ui/16/logs.png"))); // NOI18N
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
        .addContainerGap()
        .addComponent(webLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(normalRbtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(advancedRbtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 243, Short.MAX_VALUE)
        .addComponent(resetBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
    webPanel1Layout.setVerticalGroup(
      webPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(webPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(webPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(webLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(normalRbtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(advancedRbtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(resetBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
    dispose();
  }//GEN-LAST:event_cancelBtnActionPerformed

  @SuppressWarnings("unchecked")
  private void saveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBtnActionPerformed
    for (SettingPanelGen panel : panels) {
      Map<SettingsDefinition, JComponent> checkboxs = panel.getCheckbox();
      Map<SettingsDefinition, JComponent> fields = panel.getField();

      // Save checkbox
      for (Map.Entry<SettingsDefinition, JComponent> checkbox : checkboxs.entrySet()) {
        try {
          checkbox.getKey().getKey().setValue(((WebCheckBox) checkbox.getValue()).isSelected());
        } catch (IOException ex) {
          UISettings.LOGGER.log(Level.SEVERE, null, ex);
          WebOptionPane.showMessageDialog(mr, LocaleUtils.i18n("saveSettingsFailed"), LocaleUtils.i18n("error"), JOptionPane.ERROR_MESSAGE);
          return;
        }
      }

      // Save text field
      for (Map.Entry<SettingsDefinition, JComponent> field : fields.entrySet()) {
        try {
          field.getKey().getKey().setValue(((WebTextField) field.getValue()).getText());
        } catch (IOException ex) {
          UISettings.LOGGER.log(Level.SEVERE, null, ex);
          WebOptionPane.showMessageDialog(mr, LocaleUtils.i18n("saveSettingsFailed"), LocaleUtils.i18n("error"), JOptionPane.ERROR_MESSAGE);
          return;
        }
      }

      // Save radio buttons
      for (Map.Entry<List<JComponent>, PropertyClass> radioBtn : radioBtns.entrySet()) {
        for (JComponent component : radioBtn.getKey()) {
          if (((WebRadioButton) component).isSelected()) {
            PropertyClass pcls = radioBtn.getValue();
            Settings.IProperty property = pcls.getProperty();
            Class<? extends Enum> clazz = pcls.getClazz();
            if (property != null) {
              try {
                property.setValue(Enum.valueOf(clazz, component.getName()));
              } catch (IOException ex) {
                UISettings.LOGGER.log(Level.SEVERE, null, ex);
                WebOptionPane.showMessageDialog(mr, LocaleUtils.i18n("saveSettingsFailed"), LocaleUtils.i18n("error"), JOptionPane.ERROR_MESSAGE);
                return;
              }
            }
          }
        }
      }

      // Save combobox
      for (Map.Entry<PropertyClass, WebComboBox> combobox : comboboxs.entrySet()) {
        PropertyClass pcls = combobox.getKey();
        Settings.IProperty property = pcls.getProperty();
        Class<? extends Enum> clazz = pcls.getClazz();
        if (property != null) {
          try {
            property.setValue(Enum.valueOf(clazz, (String) combobox.getValue().getSelectedItem()));
          } catch (IOException ex) {
            UISettings.LOGGER.log(Level.SEVERE, null, ex);
            WebOptionPane.showMessageDialog(mr, LocaleUtils.i18n("saveSettingsFailed"), LocaleUtils.i18n("error"), JOptionPane.ERROR_MESSAGE);
            return;
          }
        }
      }

      UIScrapper mvscrapper = (UIScrapper) movieScrapperCb.getSelectedItem();
      Class<? extends MovieScrapper> oldMovieScapper = settings.coreInstance.getSearchMovieScrapper();
      settings.coreInstance.set(SettingsProperty.searchMovieScrapper, mvscrapper.getScrapper().getClass());
      settingsChange.firePropertyChange(SettingPropertyChange.SEARCHMOVIESCRAPPER.name(), oldMovieScapper, mvscrapper.getScrapper().getClass());

      UIScrapper tvscrapper = (UIScrapper) tvshowScrapperCb.getSelectedItem();
      Class<? extends TvShowScrapper> oldTvScrapper = settings.coreInstance.getSearchTvshowScrapper();
      settings.coreInstance.set(SettingsProperty.searchTvshowScrapper, tvscrapper.getScrapper().getClass());
      settingsChange.firePropertyChange(SettingPropertyChange.SEARCHMTVSHOWSCRAPPER.name(), oldTvScrapper, tvscrapper.getScrapper().getClass());

      for (JComponent component : languageRBtns) {// TODO Ask for restart app
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

  private void resetBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBtnActionPerformed
    try {
      settings.clear();
    } catch (IOException ex) {
      UISettings.LOGGER.log(Level.SEVERE, null, ex);
      WebOptionPane.showMessageDialog(mr, LocaleUtils.i18n("saveSettingsFailed"), LocaleUtils.i18n("error"), JOptionPane.ERROR_MESSAGE);
    }
  }//GEN-LAST:event_resetBtnActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private com.alee.laf.radiobutton.WebRadioButton advancedRbtn;
  private com.alee.laf.button.WebButton cancelBtn;
  private com.alee.laf.radiobutton.WebRadioButton normalRbtn;
  private com.alee.laf.button.WebButton resetBtn;
  private com.alee.laf.button.WebButton saveBtn;
  private javax.swing.ButtonGroup settingsGroup;
  private com.alee.laf.label.WebLabel webLabel1;
  private com.alee.laf.panel.WebPanel webPanel1;
  private com.alee.laf.tabbedpane.WebTabbedPane webTabbedPane1;
  // End of variables declaration//GEN-END:variables
}
