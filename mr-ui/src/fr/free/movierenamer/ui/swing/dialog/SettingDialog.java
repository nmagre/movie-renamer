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
package fr.free.movierenamer.ui.swing.dialog;

import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.text.WebTextField;
import com.alee.managers.language.LanguageManager;
import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.mediainfo.MediaAudio;
import fr.free.movierenamer.mediainfo.MediaSubTitle;
import fr.free.movierenamer.mediainfo.MediaTag;
import fr.free.movierenamer.mediainfo.MediaVideo;
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
import fr.free.movierenamer.ui.bean.UITestSettings;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.settings.UISettings.UISettingsProperty;
import fr.free.movierenamer.ui.swing.ITestActionListener;
import fr.free.movierenamer.ui.swing.panel.generator.SettingPanelGen;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import static fr.free.movierenamer.ui.utils.UIUtils.i18n;
import fr.free.movierenamer.utils.NumberUtils;
import fr.free.movierenamer.utils.ScrapperUtils;
import fr.free.movierenamer.utils.StringUtils;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * Class Setting dialog
 *
 * @author Nicolas Magré
 */
public class SettingDialog extends JDialog {

  private static final long serialVersionUID = 1L;
  private final UISettings settings = UISettings.getInstance();
  private final Map<SettingsType, SettingPanelGen> panels;
  private final List<Settings.IProperty> properties;
  private final MovieRenamer mr;

  private static enum mainTabIcon {

    GENERAL(ImageUtils.SETTING_16),
    MEDIA(ImageUtils.MEDIA_16),
    SEARCH(ImageUtils.SSEARCH_16),
    INTERFACE(ImageUtils.WINDOW_16),
    FORMAT(ImageUtils.FORMAT_16),
    IMAGE(ImageUtils.IMAGE_16),
    NFO(ImageUtils.TEXTFILE_16),
    EXTENSION(ImageUtils.OTHER_16),
    NETWORK(ImageUtils.NETWORK_16);

    private final Icon icon;

    private mainTabIcon(Icon icon) {
      this.icon = icon;
    }

    public Icon getIcon() {
      return icon;
    }
  }

  /**
   * Creates new form Setting
   *
   * @param mr Movie Renamer main interface
   */
  public SettingDialog(MovieRenamer mr) {
    this.mr = mr;
    panels = new LinkedHashMap<>();
    properties = new ArrayList<>();

    initComponents();

    properties.addAll(Arrays.asList(UISettingsProperty.values()));
    properties.addAll(Arrays.asList(Settings.SettingsProperty.values()));

    LanguageManager.registerComponent(mainTabbedPane, "mrui.settings.maintab");
    mainTabbedPane.setFont(UIUtils.titleFont);

    for (SettingsType settingType : SettingsType.values()) {
      final SettingPanelGen panel = new SettingPanelGen(mr);
      panel.addSettings(settingType, getSettings(settingType));
      panel.setName(settingType.name().toLowerCase());
      mainTabbedPane.addTab("", getMainTabIcon(settingType.name()), panel);
      panels.put(settingType, panel);
      panel.reset();// Set diplay value
    }

    pack();
    setModal(true);
    setTitle(UISettings.APPNAME + " " + ("settings"));// FIXME i18n
    setIconImage(ImageUtils.iconToImage(ImageUtils.LOGO_32));
  }

  @Override
  public void setVisible(boolean b) {
    UIUtils.showOnScreen(mr, this);
    super.setVisible(b);
  }

  private Icon getMainTabIcon(String name) {

    try {
      return mainTabIcon.valueOf(name).getIcon();
    } catch (Exception e) {
    }

    return ImageUtils.SETTING_16;
  }

  /**
   * Get all Settings of type "type"
   *
   * @param type settings type
   * @return Map of all settings of "type" by subtype
   */
  private Map<SettingsSubType, List<IProperty>> getSettings(SettingsType type) {
    List<List<IProperty>> res = new ArrayList<>();
    Map<SettingsSubType, List<IProperty>> map = new LinkedHashMap<>();

    for (IProperty definition : properties) {
      if (!type.equals(definition.getType())) {
        continue;
      }

      List<IProperty> tproperties = map.get(definition.getSubType());
      if (tproperties == null) {
        tproperties = new ArrayList<>();
      }
      tproperties.add(definition);
      map.put(definition.getSubType(), tproperties);
    }

    return addTest(map, type);
  }

  /**
   * Add UITestSettings if needed. UITestSettings is use to create "test" button
   *
   * @param map Map of all settings of a type by subtype
   * @param type SettingsType
   * @return Map of all settings of "type" by subtype + UITestSettings if needed
   */
  private Map<SettingsSubType, List<IProperty>> addTest(Map<SettingsSubType, List<IProperty>> map, final SettingsType type) {
    switch (type) {
      case FORMAT:
        List<IProperty> tproperties = map.get(SettingsSubType.TIME);
        tproperties.add(new UITestSettings(type, SettingsSubType.TIME) {
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
                  Map<IProperty, WebTextField> fields = panels.get(type).getField();
                  Map<IProperty, WebCheckBox> checkbox = panels.get(type).getCheckbox();
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
        map.put(SettingsSubType.TIME, tproperties);
        break;

      case MEDIA:
        tproperties = map.get(SettingsSubType.MOVIE);
        tproperties.add(new UITestSettings(type, SettingsSubType.MOVIE) {
          private ITestActionListener listener;
          private MovieInfo info;

          @Override
          public ITestActionListener getActionListener() {
            if (info == null) {
              Map<MovieInfo.MovieProperty, String> fields = new HashMap<>();
              fields.put(MovieInfo.MovieProperty.certificationCode, "R");
              fields.put(MovieInfo.MovieProperty.originalTitle, "The Matrix");
              fields.put(MovieInfo.MovieProperty.rating, "8.7");
              fields.put(MovieInfo.MovieProperty.releasedDate, "1999");
              fields.put(MovieInfo.MovieProperty.runtime, "136");
              fields.put(MovieInfo.MovieProperty.title, "Matrix");

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
              ids.add(new IdInfo(133093, ScrapperUtils.AvailableApiIds.IMDB));
              info = new MovieInfo(ids, fields, multipleFields);

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
              persons.add(new CastingInfo(d));
              persons.add(new CastingInfo(d1));
              persons.add(new CastingInfo(a));
              persons.add(new CastingInfo(a1));
              persons.add(new CastingInfo(a2));
              persons.add(new CastingInfo(a3));
              persons.add(new CastingInfo(a4));
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
                  Map<IProperty, WebTextField> fields = panels.get(type).getField();
                  Map<IProperty, WebCheckBox> checkbox = panels.get(type).getCheckbox();
                  Map<IProperty, WebComboBox> combobox = panels.get(type).getCombobox();
                  result = info.getRenamedTitle(fields.get(Settings.SettingsProperty.movieFilenameFormat).getText(),
                          (StringUtils.CaseConversionType) ((UIEnum) combobox.get(Settings.SettingsProperty.movieFilenameCase).getSelectedItem()).getValue(),
                          fields.get(Settings.SettingsProperty.movieFilenameSeparator).getText(),
                          Integer.parseInt(fields.get(Settings.SettingsProperty.movieFilenameLimit).getText()),// FIXME check if it is an integer before
                          checkbox.get(Settings.SettingsProperty.reservedCharacter).isSelected(),
                          checkbox.get(Settings.SettingsProperty.movieFilenameRmDupSpace).isSelected(),
                          checkbox.get(Settings.SettingsProperty.movieFilenameTrim).isSelected());
                }
              };
            }

            return listener;
          }
        });
        break;
    }
    return map;
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
    mainTabbedPane = new com.alee.laf.tabbedpane.WebTabbedPane();
    saveBtn = UIUtils.createButton(i18n.getLanguageKey("save", false), ImageUtils.OK_16);
    cancelBtn = UIUtils.createButton(i18n.getLanguageKey("cancel", false), ImageUtils.CANCEL_16);

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    saveBtn.setPreferredSize(UIUtils.buttonSize);
    saveBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        saveBtnActionPerformed(evt);
      }
    });

    cancelBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelBtnActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(mainTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(saveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 483, Short.MAX_VALUE)
        .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(mainTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(cancelBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
          .addComponent(saveBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

  private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
    setVisible(false);
    for (SettingPanelGen panel : panels.values()) {
      panel.reset();
    }
  }//GEN-LAST:event_cancelBtnActionPerformed

  @SuppressWarnings("unchecked")
  private void saveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBtnActionPerformed
    for (SettingPanelGen panel : panels.values()) {
      Map<IProperty, WebCheckBox> checkboxs = panel.getCheckbox();
      Map<IProperty, WebTextField> fields = panel.getField();
      Map<IProperty, WebComboBox> comboboxs = panel.getCombobox();

      // Save checkbox
      for (Map.Entry<IProperty, WebCheckBox> checkbox : checkboxs.entrySet()) {
        try {
          IProperty property = checkbox.getKey();
          String oldValue = property.getValue();
          property.setValue(checkbox.getValue().isSelected());

          UIEvent.fireUIEvent(UIEvent.Event.SETTINGS, oldValue.equals(property.getValue()) ? property : null, property);
        } catch (IOException ex) {
          UISettings.LOGGER.log(Level.SEVERE, null, ex);
          WebOptionPane.showMessageDialog(mr, ("settings.saveSettingsFailed"), ("error"), JOptionPane.ERROR_MESSAGE);// FIXME i18n
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
          }

          property.setValue(field.getValue().getText());

          UIEvent.fireUIEvent(UIEvent.Event.SETTINGS, oldValue.equals(property.getValue()) ? property : null, property);
        } catch (IOException ex) {
          UISettings.LOGGER.log(Level.SEVERE, null, ex);
          WebOptionPane.showMessageDialog(mr, ("settings.saveSettingsFailed"), ("error"), JOptionPane.ERROR_MESSAGE);// FIXME i18n
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

            UIEvent.fireUIEvent(UIEvent.Event.SETTINGS, oldValue.equals(property.getValue()) ? property : null, property);

          } else if (combobox.getValue().getSelectedItem() instanceof UIScrapper) {
            UIScrapper scrapper = (UIScrapper) combobox.getValue().getSelectedItem();
            Class<?> clazz = Class.forName(property.getValue().replace("class ", ""));
            settings.coreInstance.set((SettingsProperty) property, scrapper.getScrapper().getClass());
            UIEvent.fireUIEvent(UIEvent.Event.SETTINGS, clazz.equals(scrapper.getScrapper().getClass()) ? property : null, property);
          } else {
            UISettings.LOGGER.log(Level.SEVERE, String.format("Unknown property %s : Class %s", property.name(), property.getDefaultValue()));
          }
        } catch (ClassNotFoundException ex) {
          Logger.getLogger(SettingDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
          UISettings.LOGGER.log(Level.SEVERE, null, ex);
          WebOptionPane.showMessageDialog(mr, ("settings.saveSettingsFailed"), ("error"), JOptionPane.ERROR_MESSAGE);// FIXME i18n
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
  }//GEN-LAST:event_saveBtnActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private com.alee.laf.button.WebButton cancelBtn;
  private com.alee.laf.tabbedpane.WebTabbedPane mainTabbedPane;
  private com.alee.laf.button.WebButton saveBtn;
  private javax.swing.ButtonGroup settingsGroup;
  // End of variables declaration//GEN-END:variables
}
