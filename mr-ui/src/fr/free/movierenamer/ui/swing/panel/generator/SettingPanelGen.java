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
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.text.WebTextField;
import fr.free.movierenamer.info.NfoInfo;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.scrapper.ScrapperManager;
import fr.free.movierenamer.scrapper.SubtitleScrapper;
import fr.free.movierenamer.scrapper.TvShowScrapper;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.settings.Settings.IProperty;
import fr.free.movierenamer.ui.bean.IIconList;
import fr.free.movierenamer.ui.bean.UIEnum;
import fr.free.movierenamer.ui.bean.UIScrapper;
import fr.free.movierenamer.ui.res.Flag;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.StringUtils.CaseConversionType;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Class SettingPanel
 *
 * @author Nicolas Magré
 */
public class SettingPanelGen extends PanelGenerator {

  private static final long serialVersionUID = 1L;
  private final Dimension comboboxDim = new Dimension(200, 25);
  private Map<IProperty, WebCheckBox> checkboxs;
  private Map<IProperty, WebTextField> fields;
  private Map<IProperty, WebComboBox> comboboxs;
  private final String settingsi18n = "settings.";

  public SettingPanelGen() {
    super();
    setLayout(new GridBagLayout());
  }

  public void addSettings(List<List<IProperty>> settingsDef, boolean tabbed) {

    checkboxs = new HashMap<IProperty, WebCheckBox>();
    fields = new HashMap<IProperty, WebTextField>();
    comboboxs = new HashMap<IProperty, WebComboBox>();
    WebTabbedPane tabbedPane = new WebTabbedPane();

    for (List<IProperty> group : settingsDef) {

      int level = 1;
      WebPanel panel = null;

      String type = group.get(0).getType().name();
      String subtype = group.get(0).getSubType().name();
      if (group.get(0).getType().equals(Settings.SettingsType.RENAME) && !group.get(0).getSubType().equals(Settings.SettingsSubType.GENERAL)) {
        panel = new WebPanel();
        panel.setLayout(new GridBagLayout());
        if (tabbedPane.getTabCount() == 0) {
          add(createTitle(settingsi18n + type, settingsi18n + type + "." + subtype, true), getTitleConstraint());
        }
      } else {
        add(createTitle(settingsi18n + subtype, settingsi18n + type + "." + subtype, true), getTitleConstraint());
      }

      boolean hasChild = false;
      boolean isEnabled = false;
      final List<JComponent> childs = new ArrayList<JComponent>();

      for (IProperty property : group) {
        GridBagConstraints constraint = getGroupConstraint(level);
        final JComponent component;
        String title = LocaleUtils.i18nExt(settingsi18n + property.name().toLowerCase());

        if (property.getVclass().equals(Boolean.class)) {

          // Create checkbox for boolean value
          component = createComponent(Component.CHECKBOX, title);
          isEnabled = Boolean.parseBoolean(property.getValue());
          ((WebCheckBox) component).setSelected(isEnabled);
          checkboxs.put(property, (WebCheckBox) component);

        } else if (property.getVclass().equals(String.class) || property.getDefaultValue() instanceof Number) {

          // Create text field for String and number value
          WebLabel label = (WebLabel) createComponent(Component.LABEL, title);
          component = createComponent(Component.FIELD, null);
          ((WebTextField) component).setText(property.getValue());

          if (tabbed && panel != null) {
            panel.add(label, getGroupConstraint(0, false, false, level));
          } else {
            add(label, getGroupConstraint(0, false, false, level));
          }
          constraint = getGroupConstraint(1, true, true, level);
          fields.put(property, (WebTextField) component);

        } else if (property.getVclass().isEnum()) {

          // Enum
          WebLabel label = (WebLabel) createComponent(Component.LABEL, title);
          component = new WebComboBox();
          //component.setPreferredSize(comboboxDim);
          DefaultComboBoxModel model = new DefaultComboBoxModel();

          if (tabbed && panel != null) {
            panel.add(label, getGroupConstraint(0, false, false, level));
          } else {
            add(label, getGroupConstraint(0, false, false, level));
          }

          constraint = getGroupConstraint(1, true, /*false*/ true, level);

          @SuppressWarnings("unchecked")
          Class<? extends Enum<?>> clazz = (Class<? extends Enum<?>>) property.getVclass();

          for (Enum<?> e : clazz.getEnumConstants()) {
            IIconList iicon;
            if (property.getDefaultValue() instanceof LocaleUtils.Language) {
              iicon = Flag.getFlag(e.name());
            } else {
              String imgfolder = null;
              if (property.getDefaultValue() instanceof NfoInfo.NFOtype) {
                imgfolder = "mediacenter";
              } else if (property.getDefaultValue() instanceof CaseConversionType) {
                imgfolder = "case";
              }
              iicon = new UIEnum(e, imgfolder);
            }

            model.addElement(iicon);

            if (e.name().equals(property.getValue())) {
              model.setSelectedItem(iicon);
            }
          }
          ((WebComboBox) component).setRenderer(UIUtils.iconListRenderer);
          ((WebComboBox) component).setModel(model);
          comboboxs.put(property, ((WebComboBox) component));

        } else if (property.getDefaultValue() instanceof Class) {
          // Class (Scrapper)
          WebLabel label = (WebLabel) createComponent(Component.LABEL, title);
          component = new WebComboBox();
          //component.setPreferredSize(comboboxDim);
          DefaultComboBoxModel model = new DefaultComboBoxModel();

          if (tabbed && panel != null) {
            panel.add(label, getGroupConstraint(0, false, false, level));
          } else {
            add(label, getGroupConstraint(0, false, false, level));
          }

          constraint = getGroupConstraint(1, true, /*false*/ true, level);

          if (MovieScrapper.class.isAssignableFrom((Class<?>) property.getDefaultValue())) {
            for (MovieScrapper scrapper : ScrapperManager.getMovieScrapperList()) {
              model.addElement(new UIScrapper(scrapper));
            }
            model.setSelectedItem(new UIScrapper(ScrapperManager.getMovieScrapper()));
          } else if (TvShowScrapper.class.isAssignableFrom((Class<?>) property.getDefaultValue())) {
            for (TvShowScrapper scrapper : ScrapperManager.getTvShowScrapperList()) {
              model.addElement(new UIScrapper(scrapper));
            }
            model.setSelectedItem(new UIScrapper(ScrapperManager.getTvShowScrapper()));
          } else if (SubtitleScrapper.class.isAssignableFrom((Class<?>) property.getDefaultValue())) {
            for (SubtitleScrapper scrapper : ScrapperManager.getSubtitleScrapperList()) {
              model.addElement(new UIScrapper(scrapper));
            }
            model.setSelectedItem(new UIScrapper(ScrapperManager.getSubtitleScrapper()));
          } else {
            UISettings.LOGGER.log(Level.SEVERE, String.format("Unknown component for %s : Class %s", property.name(), property.getDefaultValue()));
            continue;
          }

          ((WebComboBox) component).setModel(model);
          ((WebComboBox) component).setRenderer(UIUtils.iconListRenderer);
          comboboxs.put(property, ((WebComboBox) component));
        } else {
          UISettings.LOGGER.log(Level.SEVERE, String.format("Unknown component for %s : Class %s", property.name(), property.getVclass()));
          continue;
        }

        if (hasChild) {
          component.setEnabled(isEnabled);
          childs.add(component);
        } else {
          hasChild = property.hasChild();
          if (hasChild) {
            level++;
            ((WebCheckBox) component).addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent ae) {
                boolean enabled = ((WebCheckBox) ae.getSource()).isSelected();
                for (JComponent child : childs) {
                  child.setEnabled(enabled);
                }
              }
            });
          }
        }

        if (tabbed && panel != null) {
          panel.add(component, constraint);
        } else {
          add(component, constraint);
        }
      }

      if (tabbed && panel != null) {
        tabbedPane.add(LocaleUtils.i18nExt(settingsi18n + group.get(0).getSubType().name().toLowerCase()), panel);
      }
    }

    if (tabbed) {
      add(tabbedPane, getGroupConstraint(0, true, true, 1));
    }
    add(new JPanel(), getDummyPanelConstraint());

    repaint();
    validate();
  }

  public Map<IProperty, WebCheckBox> getCheckbox() {
    return Collections.unmodifiableMap(checkboxs);
  }

  public Map<IProperty, WebTextField> getField() {
    return Collections.unmodifiableMap(fields);
  }

  public Map<IProperty, WebComboBox> getCombobox() {
    return Collections.unmodifiableMap(comboboxs);
  }
}
