/*
 * Movie Renamer
 * Copyright (C) 2015 Nicolas Magré
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
package fr.free.movierenamer.ui.swing.panel;

import com.alee.extended.transition.effects.fade.FadeTransitionEffect;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.list.WebList;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.text.WebPasswordField;
import com.alee.laf.text.WebTextField;
import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.mediainfo.MediaAudio;
import fr.free.movierenamer.mediainfo.MediaSubTitle;
import fr.free.movierenamer.mediainfo.MediaTag;
import fr.free.movierenamer.mediainfo.MediaVideo;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.settings.Settings.SettingsProperty;
import fr.free.movierenamer.settings.XMLSettings;
import fr.free.movierenamer.settings.XMLSettings.IProperty;
import fr.free.movierenamer.settings.XMLSettings.ISettingsType;
import fr.free.movierenamer.settings.XMLSettings.SettingsSubType;
import fr.free.movierenamer.settings.XMLSettings.SettingsType;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.IImage;
import fr.free.movierenamer.ui.bean.UIEnum;
import fr.free.movierenamer.ui.bean.UIEvent;
import fr.free.movierenamer.ui.bean.UILang;
import fr.free.movierenamer.ui.bean.UIPasswordSettings;
import fr.free.movierenamer.ui.bean.UIPathSettings;
import fr.free.movierenamer.ui.bean.UIScraper;
import fr.free.movierenamer.ui.bean.UITestSettings;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.swing.ITestActionListener;
import fr.free.movierenamer.ui.swing.ImageListModel;
import fr.free.movierenamer.ui.swing.panel.generator.SettingPanelGen;
import fr.free.movierenamer.ui.swing.renderer.IconListRenderer;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import static fr.free.movierenamer.ui.utils.UIUtils.i18n;
import fr.free.movierenamer.utils.NumberUtils;
import fr.free.movierenamer.utils.ScraperUtils;
import fr.free.movierenamer.utils.StringUtils;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Class SettingsPanel
 *
 * @author Nicolas Magré
 */
public class SettingsPanel extends WebPanel {

  private final MovieRenamer mr;
  private final UISettings settings = UISettings.getInstance();
  private final Map<SettingsTypeIcon, SettingPanelGen> panels;
  private final List<IProperty> properties;
  private final ImageListModel<SettingsTypeIcon> settingsListModel = new ImageListModel<>();

  private static enum SettingsTypeIcon implements IImage {

    GENERAL(ImageUtils.SETTING_16, SettingsType.GENERAL, SettingsSubType.GENERAL),
    //MEDIA(ImageUtils.MEDIA_16, SettingsType.MEDIA),
    SEARCH(ImageUtils.SSEARCH_16, SettingsType.SEARCH),
    INTERFACE(ImageUtils.WINDOW_16, SettingsType.INTERFACE),
    FORMAT(ImageUtils.FORMAT_16, SettingsType.FORMAT),
    IMAGE(ImageUtils.IMAGE_16, SettingsType.IMAGE),
    NFO(ImageUtils.TEXTFILE_16, SettingsType.NFO),
    EXTENSION(ImageUtils.OTHER_16, SettingsType.EXTENSION),
    NETWORK(ImageUtils.NETWORK_16, SettingsType.NETWORK),
    ADVANCED(ImageUtils.WARNING_16, SettingsType.ADVANCED);
    // SubType

    private final Icon icon;
    private final ISettingsType settingsType;
    private SettingsSubType settingsSubType = null;
    private final boolean isSubType;
    private boolean hasSubtype = false;

    private SettingsTypeIcon(Icon icon, SettingsType settingsType) {
      this.icon = icon;
      this.settingsType = settingsType;
      isSubType = false;
    }

    private SettingsTypeIcon(Icon icon, SettingsSubType settingsSubType) {
      this.icon = icon;
      this.settingsType = settingsSubType;
      isSubType = true;
    }

    private SettingsTypeIcon(Icon icon, SettingsType settingsType, SettingsSubType settingsSubType) {
      this.icon = icon;
      this.settingsType = settingsType;
      isSubType = false;
      hasSubtype = true;
      this.settingsSubType = settingsSubType;
    }

    public ISettingsType getSettingsType() {
      return settingsType;
    }

    public boolean isSubType() {
      return isSubType;
    }

    public boolean hasSubType() {
      return hasSubtype;
    }

    public SettingsSubType getSettingsSubType() {
      return settingsSubType;
    }

    @Override
    public Icon getIcon() {
      return icon;
    }

    @Override
    public void setIcon(Icon icon) {

    }

    @Override
    public URI getUri(ImageInfo.ImageSize size) {
      return null;
    }

    @Override
    public int getId() {
      return ordinal();
    }

    @Override
    public String getName() {
      return UIUtils.i18n.getLanguage("settings." + name().toLowerCase(), false);
    }

    @Override
    public String toString() {
      return getName();
    }

  }

  /**
   * Creates new form SettingsPanel
   */
  public SettingsPanel(MovieRenamer mr) {
    this.mr = mr;
    panels = new LinkedHashMap<>();
    properties = new ArrayList<>();

    initComponents();

    optionPanel.setTransitionEffect(new FadeTransitionEffect());
    settingsList.setModel(settingsListModel);
    settingsList.setCellRenderer(new IconListRenderer<SettingsTypeIcon>());
    settingsList.addListSelectionListener(createSettingsListListener());

    properties.addAll(Arrays.asList(UISettings.UISettingsProperty.values()));
    properties.addAll(Arrays.asList(Settings.SettingsProperty.values()));

    setFont(UIUtils.titleFont);

    SettingsType settingsType;
    SettingPanelGen panel;
    for (SettingsTypeIcon mti : SettingsTypeIcon.values()) {
      if (mti.isSubType()) {
        continue;
      }

      settingsType = (SettingsType) mti.getSettingsType();
      panel = new SettingPanelGen(mr);
      panel.generatePanel(settingsType, getSettings(mti), !settingsType.equals(SettingsType.ADVANCED));
      settingsListModel.add(mti);
      panels.put(mti, panel);
    }

    settingsList.setSelectedIndex(0);
  }

  private ListSelectionListener createSettingsListListener() {
    return new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent lse) {
        WebList lsm = (WebList) lse.getSource();
        if (!lsm.getValueIsAdjusting() && !lsm.isSelectionEmpty()) {
          SettingsTypeIcon mti = (SettingsTypeIcon) UIUtils.getSelectedElement(lsm);
          optionPanel.performTransition(panels.get(mti));
          if (mti.getSettingsType().equals(SettingsType.ADVANCED)) {
            WebOptionPane.showMessageDialog(mr, i18n.getLanguage("settings.advancedWarning", false),
                    i18n.getLanguage("warning", false), JOptionPane.WARNING_MESSAGE);
          }
        }
      }
    };
  }

  public void reset() {
    for (SettingPanelGen panel : panels.values()) {
      panel.reset();
    }
  }

  /**
   * Get all Settings of type "type"
   *
   * @param type settings type
   * @return Map of all settings of "type" by subtype
   */
  private Map<SettingsSubType, List<IProperty>> getSettings(SettingsTypeIcon mti) {
    final Map<SettingsSubType, List<IProperty>> map = new LinkedHashMap<>();

    for (IProperty definition : properties) {
      if (!mti.getSettingsType().equals(definition.getType())) {
        continue;
      }

      List<IProperty> tproperties = map.get(definition.getSubType());
      if (tproperties == null) {
        tproperties = new ArrayList<>();
      }

      tproperties.add(definition);
      map.put(definition.getSubType(), tproperties);
    }

    // Add specific UI settings
    addTest(map, mti);
    addPassword(map);
    addPath(map);

    return map;
  }

  /**
   * Change IProperty to UIPathSettings if property name end with "Path"
   *
   * @param map Map of all settings of a type by subtype
   * @return Map of all settings of "type" by subtype + UIPathSettings if needed
   */
  private void addPath(final Map<SettingsSubType, List<IProperty>> map) {
    addCustomUI(map, "Path", UIPathSettings.class);
  }

  /**
   * Change IProperty to UIPasswordSettings if property name end with "Pass"
   *
   * @param map Map of all settings of a type by subtype
   * @return Map of all settings of "type" by subtype + UIPathSettings if needed
   */
  private void addPassword(final Map<SettingsSubType, List<IProperty>> map) {
    addCustomUI(map, "Pass", UIPasswordSettings.class);
  }

  /**
   * Replace all properties with name end with "name" by a "Class<T>" object.
   * 
   * @param <T> XMLSettings.IProperty
   * @param map 
   * @param name 
   * @param clazz 
   */
  private <T extends XMLSettings.IProperty> void addCustomUI(Map<SettingsSubType, List<IProperty>> map, String name, Class<T> clazz) {
    Iterator<Map.Entry<SettingsSubType, List<IProperty>>> mapit = map.entrySet().iterator();
    Map.Entry<SettingsSubType, List<IProperty>> entry;
    IProperty property;

    int index;
    int pos;
    while (mapit.hasNext()) {
      entry = mapit.next();
      List<IProperty> tproperties = entry.getValue();
      Iterator<IProperty> it = tproperties.iterator();
      List<T> customUI = new ArrayList<>();

      index = -1;
      pos = 0;
      while (it.hasNext()) {
        property = it.next();
        if (property.name().endsWith(name)) {
          it.remove();
          if (index == -1) {
            index = pos;
          }

          try {
            customUI.add(clazz.getConstructor(XMLSettings.IProperty.class).newInstance(property));
          } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            UISettings.LOGGER.log(Level.SEVERE, null, ex);
          }
        }
        pos++;
      }

      // Keep order
      if (index != -1) {
        tproperties.addAll(index, customUI);
      } else {
        tproperties.addAll(customUI);
      }
      map.put(entry.getKey(), tproperties);
    }
  }

  /**
   * Add UITestSettings if needed. UITestSettings is use to create "test" button
   *
   * @param map Map of all settings of a type by subtype
   * @param type SettingsType
   * @return Map of all settings of "type" by subtype + UITestSettings if needed
   */
  private void addTest(Map<SettingsSubType, List<IProperty>> map, final SettingsTypeIcon mti) {

    SettingsType type = (SettingsType) mti.getSettingsType();
    for (SettingsSubType subType : XMLSettings.SettingsSubType.values()) {
      List<IProperty> tproperties = map.get(subType);
      if (tproperties != null) {

        switch (subType) {
          case TIME:
            tproperties.add(new UITestSettings(type, subType) {
              ITestActionListener listener;

              @Override
              public ITestActionListener getActionListener() {
                if (listener == null) {
                  listener = new ITestActionListener() {
                    String result = "";

                    @Override
                    public String getResult() {
                      return result;
                    }

                    @Override
                    public void actionPerformed(ActionEvent e) {
                      Map<IProperty, WebTextField> fields = panels.get(mti).getField();
                      Map<IProperty, WebCheckBox> checkbox = panels.get(mti).getCheckbox();
                      result = StringUtils.humanReadableTime(6223334, fields.get(Settings.SettingsProperty.stringTimeHour).getText(),
                              fields.get(Settings.SettingsProperty.stringTimeMinute).getText(),
                              fields.get(Settings.SettingsProperty.stringTimeSeconde).getText(),
                              fields.get(Settings.SettingsProperty.stringTimeMilliSeconde).getText(),
                              checkbox.get(Settings.SettingsProperty.stringTimeShowSeconde).isSelected(),
                              checkbox.get(Settings.SettingsProperty.stringTimeShowMillis).isSelected());
                    }
                  };
                }

                return listener;
              }
            });
            break;

          case MOVIE:
            tproperties.add(new UITestSettings(type, subType) {
              private ITestActionListener listener;
              private MovieInfo info;

              @Override
              public ITestActionListener getActionListener() {
                if (info == null) {
                  Map<MediaInfo.MediaProperty, String> mediaFields = new HashMap<>();
                  mediaFields.put(MediaInfo.MediaProperty.rating, "8.7");
                  mediaFields.put(MediaInfo.MediaProperty.title, "Matrix");
                  mediaFields.put(MediaInfo.MediaProperty.year, "1999");
                  mediaFields.put(MediaInfo.MediaProperty.originalTitle, "The Matrix");

                  Map<MovieInfo.MovieProperty, String> fields = new HashMap<>();
                  fields.put(MovieInfo.MovieProperty.certificationCode, "R");
                  fields.put(MovieInfo.MovieProperty.releasedDate, "1999-03-31");
                  fields.put(MovieInfo.MovieProperty.runtime, "136");
                  //fields.put(MediaInfo.MediaProperty.title, "Matrix");

                  Map<MovieInfo.MovieMultipleProperty, List<String>> multipleFields = new HashMap<>();
                  List<String> genres = new ArrayList<>();
                  genres.add("Action");
                  genres.add("Adventure");
                  genres.add("Sci-Fi");
                  multipleFields.put(MovieInfo.MovieMultipleProperty.genres, genres);

                  List<String> countries = new ArrayList<>();
                  countries.add("USA");
                  countries.add("Australia");
                  multipleFields.put(MovieInfo.MovieMultipleProperty.countries, countries);

                  List<IdInfo> ids = new ArrayList<>();
                  ids.add(new IdInfo(133093, ScraperUtils.AvailableApiIds.IMDB));
                  info = new MovieInfo(mediaFields, ids, fields, multipleFields);

                  List<MediaAudio> audios = new ArrayList<>();
                  MediaAudio audio = new MediaAudio(0);
                  MediaAudio audio1 = new MediaAudio(1);
                  audio.setBitRate(1509750);
                  audio1.setBitRate(754500);
                  audio.setBitRateMode("CBR");
                  audio1.setBitRateMode("CBR");
                  audio.setChannel("5.1");
                  audio1.setChannel("2.0");
                  audio.setCodec("DTS");
                  audio1.setCodec("MP3");
                  audio.setLanguage(Locale.ENGLISH);
                  audio1.setLanguage(Locale.FRENCH);
                  audio.setTitle("English DTS 1509kbps");
                  audio1.setTitle("French MP3");
                  audios.add(audio);
                  audios.add(audio1);

                  List<MediaSubTitle> subtitles = new ArrayList<>();
                  MediaSubTitle subtitle = new MediaSubTitle(0);
                  MediaSubTitle subtitle1 = new MediaSubTitle(1);
                  subtitle.setLanguage(Locale.ENGLISH);
                  subtitle.setTitle("English subforced");
                  subtitle1.setLanguage(Locale.FRENCH);
                  subtitle1.setTitle("French");
                  subtitles.add(subtitle);
                  subtitles.add(subtitle1);

                  MediaVideo mvideo = new MediaVideo();
                  mvideo.setAspectRatio(1.778F);
                  mvideo.setCodec("divx");
                  mvideo.setFrameCount(196072L);
                  mvideo.setFrameRate(23.976);
                  mvideo.setHeight(1080);
                  mvideo.setScanType("Progressive");
                  mvideo.setWidth(1920);

                  MediaTag mediaTag = new MediaTag(null);
                  mediaTag.setContainerFormat("Matroska");
                  mediaTag.setDuration(9701696L);
                  mediaTag.setMediaAudio(audios);
                  mediaTag.setMediaSubtitles(subtitles);
                  mediaTag.setMediaVideo(mvideo);
                  info.setMediaTag(mediaTag);

                  Map<CastingInfo.PersonProperty, String> d = new HashMap<>();
                  Map<CastingInfo.PersonProperty, String> d1 = new HashMap<>();
                  d.put(CastingInfo.PersonProperty.job, "DIRECTOR");
                  d.put(CastingInfo.PersonProperty.name, "Andy Wachowski");
                  d1.put(CastingInfo.PersonProperty.job, "DIRECTOR");
                  d1.put(CastingInfo.PersonProperty.name, "Lana Wachowski");

                  Map<CastingInfo.PersonProperty, String> a = new HashMap<>();
                  Map<CastingInfo.PersonProperty, String> a1 = new HashMap<>();
                  Map<CastingInfo.PersonProperty, String> a2 = new HashMap<>();
                  Map<CastingInfo.PersonProperty, String> a3 = new HashMap<>();
                  Map<CastingInfo.PersonProperty, String> a4 = new HashMap<>();
                  a.put(CastingInfo.PersonProperty.job, "ACTOR");
                  a1.put(CastingInfo.PersonProperty.job, "ACTOR");
                  a2.put(CastingInfo.PersonProperty.job, "ACTOR");
                  a3.put(CastingInfo.PersonProperty.job, "ACTOR");
                  a4.put(CastingInfo.PersonProperty.job, "ACTOR");
                  a.put(CastingInfo.PersonProperty.name, "Keanu Reeves");
                  a1.put(CastingInfo.PersonProperty.name, "Laurence Fishburne");
                  a2.put(CastingInfo.PersonProperty.name, "Carrie-Anne Moss");
                  a3.put(CastingInfo.PersonProperty.name, "Hugo Weaving");
                  a4.put(CastingInfo.PersonProperty.name, "Gloria Foster");

                  List<CastingInfo> persons = new ArrayList<>();
                  persons.add(new CastingInfo(d, null));
                  persons.add(new CastingInfo(d1, null));
                  persons.add(new CastingInfo(a, null));
                  persons.add(new CastingInfo(a1, null));
                  persons.add(new CastingInfo(a2, null));
                  persons.add(new CastingInfo(a3, null));
                  persons.add(new CastingInfo(a4, null));
                  info.setCasting(persons);
                }

                if (listener == null) {
                  listener = new ITestActionListener() {
                    String result = "";

                    @Override
                    public String getResult() {
                      return result;
                    }

                    @Override
                    public void actionPerformed(ActionEvent e) {
                      Map<IProperty, WebTextField> fields = panels.get(mti).getField();
                      Map<IProperty, WebCheckBox> checkbox = panels.get(mti).getCheckbox();
                      Map<IProperty, WebComboBox> combobox = panels.get(mti).getCombobox();
                      result = info.getRenamedTitle("Default filename.avi", fields.get(Settings.SettingsProperty.movieFilenameFormat).getText(),
                              (StringUtils.CaseConversionType) ((UIEnum) combobox.get(Settings.SettingsProperty.movieFilenameCase).getSelectedItem()).getValue(),
                              fields.get(Settings.SettingsProperty.movieFilenameSeparator).getText(),
                              Integer.parseInt(fields.get(Settings.SettingsProperty.movieFilenameLimit).getText()),// FIXME check if it is an integer before
                              checkbox.get(Settings.SettingsProperty.reservedCharacter).isSelected(),
                              checkbox.get(Settings.SettingsProperty.filenameRmDupSpace).isSelected(),
                              checkbox.get(Settings.SettingsProperty.filenameTrim).isSelected());
                    }
                  };
                }

                return listener;
              }
            });
            break;
        }

        map.put(subType, tproperties);
      }

    }
  }

  private void saveCombobox(Map<IProperty, WebComboBox> comboboxs) {
    for (Map.Entry<IProperty, WebComboBox> combobox : comboboxs.entrySet()) {
      IProperty property = combobox.getKey();
      try {
        if (property.getVclass().isEnum()) {
          String oldValue = property.getValue();
          if (combobox.getValue().getSelectedItem() instanceof UILang) {
            property.setValue(((UILang) combobox.getValue().getSelectedItem()).getLanguage());

            // Update Settings list
            if (property.equals(SettingsProperty.appLanguage)) {
              settingsListModel.update();
            }

          } else {
            property.setValue(((UIEnum) combobox.getValue().getSelectedItem()).getValue());
          }

          UIEvent.fireUIEvent(UIEvent.Event.SETTINGS, oldValue.equals(property.getValue()) ? property : null, property);

        } else if (combobox.getValue().getSelectedItem() instanceof UIScraper) {
          UIScraper scraper = (UIScraper) combobox.getValue().getSelectedItem();
          Class<?> clazz = Class.forName(property.getValue().replace("class ", ""));
          settings.coreInstance.set((Settings.SettingsProperty) property, scraper.getScraper().getClass());
          UIEvent.fireUIEvent(UIEvent.Event.SETTINGS, clazz.equals(scraper.getScraper().getClass()) ? property : null, property);
        } else {
          UISettings.LOGGER.log(Level.SEVERE, String.format("Unknown property %s : Class %s", property.name(), property.getDefaultValue()));
        }
      } catch (ClassNotFoundException ex) {
        UISettings.LOGGER.severe(ex.getMessage());
      } catch (IOException ex) {
        UISettings.LOGGER.log(Level.SEVERE, null, ex);
        WebOptionPane.showMessageDialog(mr, UIUtils.i18n.getLanguage("error.saveSettingsFailed", false), UIUtils.i18n.getLanguage("error.error", false), JOptionPane.ERROR_MESSAGE);
        return;
      }
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

    jScrollPane1 = new javax.swing.JScrollPane();
    settingsList = new com.alee.laf.list.WebList();
    saveBtn = UIUtils.createButton(i18n.getLanguageKey("save", false), ImageUtils.OK_16);
    jScrollPane2 = new javax.swing.JScrollPane();
    optionPanel = new com.alee.extended.transition.ComponentTransition();
    clearBtn = UIUtils.createButton(i18n.getLanguageKey("resetSettings", false), ImageUtils.CLEAR_LIST_16);

    jScrollPane1.setViewportView(settingsList);

    saveBtn.setPreferredSize(UIUtils.buttonSize);
    saveBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        saveBtnActionPerformed(evt);
      }
    });

    jScrollPane2.setViewportView(optionPanel);

    clearBtn.setPreferredSize(UIUtils.buttonSize);
    clearBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        clearBtnActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addComponent(saveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(clearBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
          .addComponent(jScrollPane2))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(saveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(clearBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

  private void saveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBtnActionPerformed
    for (SettingPanelGen panel : panels.values()) {
      Map<IProperty, WebCheckBox> checkboxs = panel.getCheckbox();
      Map<IProperty, WebTextField> fields = panel.getField();
      Map<IProperty, WebComboBox> comboboxs = panel.getCombobox();
      Map<IProperty, WebComboBox> scraperOptComboboxs = panel.getScraperOptCombobox();
      Map<IProperty, WebPasswordField> passFields = panel.getPassField();

      // Save checkbox
      for (Map.Entry<IProperty, WebCheckBox> checkbox : checkboxs.entrySet()) {
        try {
          IProperty property = checkbox.getKey();
          String oldValue = property.getValue();
          property.setValue(checkbox.getValue().isSelected());

          UIEvent.fireUIEvent(UIEvent.Event.SETTINGS, oldValue.equals(property.getValue()) ? property : null, property);
        } catch (IOException ex) {
          UISettings.LOGGER.log(Level.SEVERE, null, ex);
          WebOptionPane.showMessageDialog(mr, UIUtils.i18n.getLanguage("error.saveSettingsFailed", false), UIUtils.i18n.getLanguage("error.error", false), JOptionPane.ERROR_MESSAGE);
          return;
        }
      }

      // Save text field
      for (Map.Entry<IProperty, WebTextField> field : fields.entrySet()) {
        try {
          IProperty property = field.getKey();
          String oldValue = property.getValue();
          if (field.getKey().getDefaultValue() instanceof Number) {
            if (!NumberUtils.isNumeric(field.getValue().getText())) {
              WebOptionPane.showMessageDialog(mr, i18n.getLanguage("error.nan", false, i18n.getLanguage("settings." + property.name().toLowerCase(), false)),
                      i18n.getLanguage("error.error", false), JOptionPane.ERROR_MESSAGE);
              return;
            }
          } else if (field.getKey().getDefaultValue() instanceof Pattern) {
            try {
              Pattern.compile(field.getValue().getText());
            } catch (Exception ex) {
              WebOptionPane.showMessageDialog(mr, i18n.getLanguage("error.invalidPattern", false, i18n.getLanguage("settings." + property.name().toLowerCase(), false)),
                      i18n.getLanguage("error.error", false), JOptionPane.ERROR_MESSAGE);
            }
          }

          String str = field.getValue().getText();
          if (field.getKey().getDefaultValue() instanceof Character) {
            if (str.length() == 1) {
              property.setValue(field.getValue().getText().charAt(0));
            }
          } else {
            property.setValue(field.getValue().getText());
          }

          UIEvent.fireUIEvent(UIEvent.Event.SETTINGS, oldValue.equals(property.getValue()) ? property : null, property);
        } catch (IOException ex) {
          UISettings.LOGGER.log(Level.SEVERE, null, ex);
          WebOptionPane.showMessageDialog(mr, UIUtils.i18n.getLanguage("error.saveSettingsFailed", false), UIUtils.i18n.getLanguage("error.error", false), JOptionPane.ERROR_MESSAGE);
          return;
        }
      }

      // Save password field
      for (Map.Entry<IProperty, WebPasswordField> field : passFields.entrySet()) {
        try {
          IProperty property = field.getKey();
          String oldValue = property.getValue();
          property.setValue(new String(field.getValue().getPassword()));

          UIEvent.fireUIEvent(UIEvent.Event.SETTINGS, oldValue.equals(property.getValue()) ? property : null, property);
        } catch (IOException ex) {
          UISettings.LOGGER.log(Level.SEVERE, null, ex);
          WebOptionPane.showMessageDialog(mr, UIUtils.i18n.getLanguage("error.saveSettingsFailed", false), UIUtils.i18n.getLanguage("error.error", false), JOptionPane.ERROR_MESSAGE);
          return;
        }
      }

      // Save combobox
      saveCombobox(comboboxs);
      saveCombobox(scraperOptComboboxs);

      /*for (JComponent component : languageRBtns) {// TODO Ask for restart app
       if (((WebRadioButton) component).isSelected()) {
       SettingsProperty.appLanguage.setValue(new Locale(UISupportedLanguage.valueOf(component.getName()).name()).getLanguage());
       break;
       }
       }*/
    }

    mr.updateRenamedTitle();
  }//GEN-LAST:event_saveBtnActionPerformed

  private void clearBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearBtnActionPerformed
    // TODO ask : Are you sure ....
    settings.clear();
    reset();
  }//GEN-LAST:event_clearBtnActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private com.alee.laf.button.WebButton clearBtn;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private com.alee.extended.transition.ComponentTransition optionPanel;
  private com.alee.laf.button.WebButton saveBtn;
  private com.alee.laf.list.WebList settingsList;
  // End of variables declaration//GEN-END:variables
}
