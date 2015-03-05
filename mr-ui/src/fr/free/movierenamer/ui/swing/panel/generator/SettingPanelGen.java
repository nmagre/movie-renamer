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

import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.GroupingType;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.filechooser.WebFileChooser;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.text.WebPasswordField;
import com.alee.laf.text.WebTextField;
import com.alee.laf.toolbar.WebToolBar;
import com.alee.managers.language.LanguageManager;
import com.alee.managers.popup.PopupWay;
import com.alee.managers.popup.WebButtonPopup;
import fr.free.movierenamer.renamer.Nfo;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.scrapper.ScrapperManager;
import fr.free.movierenamer.scrapper.ScrapperOptions;
import fr.free.movierenamer.scrapper.SubtitleScrapper;
import fr.free.movierenamer.scrapper.TvShowScrapper;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.settings.Settings.SettingsProperty;
import fr.free.movierenamer.settings.XMLSettings.IProperty;
import fr.free.movierenamer.settings.XMLSettings.SettingsSubType;
import fr.free.movierenamer.settings.XMLSettings.SettingsType;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.IIconList;
import fr.free.movierenamer.ui.bean.UIEnum;
import fr.free.movierenamer.ui.bean.UILang;
import fr.free.movierenamer.ui.bean.UIPathSettings;
import fr.free.movierenamer.ui.bean.UIScraper;
import fr.free.movierenamer.ui.bean.UITestSettings;
import fr.free.movierenamer.ui.utils.FlagUtils;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.settings.UISettings.ImageFormat;
import fr.free.movierenamer.ui.swing.ITestActionListener;
import fr.free.movierenamer.ui.swing.dialog.SettingsHelpDialog;
import fr.free.movierenamer.ui.swing.renderer.IconComboRenderer;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import static fr.free.movierenamer.ui.utils.UIUtils.i18n;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.StringUtils;
import fr.free.movierenamer.utils.StringUtils.CaseConversionType;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

/**
 * Class SettingPanel
 *
 * @author Nicolas Magré
 */
public class SettingPanelGen extends PanelGenerator {

  private static final long serialVersionUID = 1L;
  private static final WebFileChooser fileChooser = new WebFileChooser();
  private Map<IProperty, WebCheckBox> checkboxs;
  private Map<IProperty, WebTextField> fields;
  private Map<IProperty, WebPasswordField> passFields;
  private Map<IProperty, WebComboBox> comboboxs;
  private Map<IProperty, WebComboBox> scraperOptComboboxs;
  private static final String settingsi18n = "settings.";
  private final MovieRenamer mr;

  private enum SettingsClazz {

    String,
    Boolean,
    Character,
    Integer,
    Class,
    ArrayList,
    Pattern,
    Subfolder,
    AppLanguages,
    AvailableLanguages,
    CaseConversionType,
    ImageFormat,
    ImageSize,
    NFOtype,
    FileChooserViewType,
    UITestSettings,
    UIPathSettings,
    UIPasswordSettings

  }

  public SettingPanelGen(MovieRenamer mr) {
    super();
    this.mr = mr;
    setLayout(new GridBagLayout());

    fileChooser.setGenerateThumbnails(true);
    fileChooser.setMultiSelectionEnabled(false);
    fileChooser.getWebUI().getFileChooserPanel().setViewType(UISettings.getInstance().getFileChooserViewType());
    fileChooser.setFileSelectionMode(WebFileChooser.DIRECTORIES_ONLY);
  }

  public void generatePanel(SettingsType type, Map<SettingsSubType, List<IProperty>> mapProperties, boolean helpBtn) {

    checkboxs = new HashMap<>();
    fields = new HashMap<>();
    comboboxs = new HashMap<>();
    passFields = new HashMap<>();
    scraperOptComboboxs = new HashMap<>();

    int level;
    boolean charField;
    boolean hasChild;
    boolean isEnabled;
    boolean createTitle;
    JComponent cmp = this;// Will be used for tabbedPane
    String title;
    SettingsClazz clazz;
    GridBagComponent gbComponent;
    final List<JComponent> childs = new ArrayList<>();

    for (List<IProperty> properties : mapProperties.values()) {

      level = 1;
      hasChild = false;
      isEnabled = false;
      createTitle = true;

      for (IProperty property : properties) {

        charField = false;
        gbComponent = null;

        // Create title toolbar (SettingsSubType)
        if (createTitle) {
          Settings.SettingsSubType subType = property.getSubType();
          if (subType != null) {
            add(createTitle(settingsi18n + subType.name().toLowerCase(), type, subType, helpBtn), getTitleConstraint());
          }
          createTitle = false;
        }

        try {
          title = settingsi18n + property.name().toLowerCase();
          clazz = SettingsClazz.valueOf(property.getVclass().getSimpleName());

          switch (clazz) {
            case Character:
              charField = true;
            case String:
            case Integer:
            case Pattern:
              gbComponent = createTextField(cmp, title, level, charField);
              fields.put(property, (WebTextField) gbComponent.getComponent());
              break;

            case Boolean:
              gbComponent = createCheckbox(title, level);
              checkboxs.put(property, (WebCheckBox) gbComponent.getComponent());
              break;

            case Class:
              gbComponent = createClassCombobox(property, cmp, title, level);
              comboboxs.put(property, (WebComboBox) gbComponent.getComponent());
              break;

            case UITestSettings:
              gbComponent = createTestField(property, cmp, level);
              // Test field is not saved
              break;

            case UIPathSettings:
              gbComponent = createPathField(property, cmp, title, level);
              fields.put(property, (WebTextField) gbComponent.getComponent());
              break;

            case ArrayList:
              gbComponent = createList(property, cmp, title, level);
              // TODO edit/save list
              break;

            case UIPasswordSettings:
              gbComponent = createPasswordField(cmp, title, level);
              passFields.put(property, (WebPasswordField) gbComponent.getComponent());
              break;

            // ENUM
            case Subfolder:
            case AppLanguages:
            case AvailableLanguages:
            case CaseConversionType:
            case ImageFormat:
            case ImageSize:
            case NFOtype:
            case FileChooserViewType:
              gbComponent = createEnumCombobox(property, cmp, title, level);
              comboboxs.put(property, (WebComboBox) gbComponent.getComponent());
              break;
          }

          if (gbComponent == null) {
            UISettings.LOGGER.severe(String.format("GridBag component is null for %s", property.name()));
            continue;
          }

          if (hasChild) {// FIXME
            gbComponent.getComponent().setEnabled(isEnabled);
            childs.add(gbComponent.getComponent());
          } else {
            hasChild = property.hasChild();
            if (hasChild) {// FIXME
              level++;
              ((WebCheckBox) gbComponent.getComponent()).addActionListener(new ActionListener() {
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

          cmp.add(gbComponent.getComponent(), gbComponent.getConstraint());
        } catch (Exception ex) {
          UISettings.LOGGER.log(Level.SEVERE, String.format("Unknown component for %s : Class %s", property.name(), property.getVclass()));
        }

      }

    }

    // Add a dummy panel to avoid centering
    add(new JPanel(), getDummyPanelConstraint());
  }

  private GridBagComponent createTextField(JComponent cmp, String title, int level, boolean charField) {
    WebLabel label = (WebLabel) createComponent(Component.LABEL, title);
    JComponent component = createComponent(charField ? Component.CHARFIELD : Component.FIELD, null);
    cmp.add(label, getGroupConstraint(0, false, false, level));

    return new GridBagComponent(component, getGroupConstraint(1, true, true, level));
  }

  private GridBagComponent createCheckbox(String title, int level) {
    JComponent component = createComponent(Component.CHECKBOX, null);
    WebLabel label = (WebLabel) createComponent(Component.LABEL, title);
    label.setMargin(0, 25, 0, 0);
    ((WebCheckBox) component).add(label);

    return new GridBagComponent(component, getGroupConstraint(level));
  }

  @SuppressWarnings("unchecked")
  private GridBagComponent createClassCombobox(IProperty property, JComponent cmp, String title, int level) {
    WebLabel label = (WebLabel) createComponent(Component.LABEL, title);
    JComponent component = new WebComboBox();
    DefaultComboBoxModel<UIScraper> model = new DefaultComboBoxModel<>();

    cmp.add(label, getGroupConstraint(0, false, false, level));

    if (MovieScrapper.class.isAssignableFrom((Class<?>) property.getDefaultValue())) {
      for (MovieScrapper scrapper : ScrapperManager.getMovieScrapperList()) {
        model.addElement(new UIScraper(scrapper));
      }

      final WebButton button = UIUtils.createSettingButton(null);
      button.setRolloverDecoratedOnly(false);
      button.setInnerShadeWidth(3);
      final WebButtonPopup buttonPopup = UIUtils.createPopup(button, PopupWay.downLeft);

      ((WebComboBox) component).addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent ae) {
          setScraperPopupOptions(button, buttonPopup, (UIScraper) ((WebComboBox) ae.getSource()).getSelectedItem());
        }
      });

      cmp.add(button, getGroupConstraint(2, false, false, level));

    } else if (TvShowScrapper.class.isAssignableFrom((Class<?>) property.getDefaultValue())) {
      for (TvShowScrapper scrapper : ScrapperManager.getTvShowScrapperList()) {
        model.addElement(new UIScraper(scrapper));
      }
    } else if (SubtitleScrapper.class.isAssignableFrom((Class<?>) property.getDefaultValue())) {
      for (SubtitleScrapper scrapper : ScrapperManager.getSubtitleScrapperList()) {
        model.addElement(new UIScraper(scrapper));
      }
    } else {
      UISettings.LOGGER.log(Level.SEVERE, String.format("Unknown component for %s : Class %s", property.name(), property.getDefaultValue()));
    }

    ((WebComboBox) component).setModel(model);
    ((WebComboBox) component).setRenderer(new IconComboRenderer<>(component));

    return new GridBagComponent(component, getGroupConstraint(1, true, true, level));
  }

  private GridBagComponent createEnumCombobox(IProperty property, JComponent cmp, String title, int level) {// FIXME
    WebLabel label = (WebLabel) createComponent(Component.LABEL, title);
    JComponent component = new WebComboBox();

    DefaultComboBoxModel<IIconList> model = new DefaultComboBoxModel<>();

    cmp.add(label, getGroupConstraint(0, false, false, level));

    @SuppressWarnings("unchecked")
    Class<? extends Enum<?>> clazz = (Class<? extends Enum<?>>) property.getVclass();

    for (Enum<?> e : clazz.getEnumConstants()) {
      IIconList iicon;
      if (property.getDefaultValue() instanceof LocaleUtils.Language) {
        iicon = FlagUtils.getFlagByLang(e.name());
      } else {
        String imgfolder = null;
        if (property.getDefaultValue() instanceof Nfo.NFOtype) {
          imgfolder = "mediacenter";
        } else if (property.getDefaultValue() instanceof CaseConversionType) {
          imgfolder = "case";
        } else if (property.getDefaultValue() instanceof ImageFormat) {
          imgfolder = "image";
        } else if (property.getDefaultValue() instanceof UISettings.Subfolder) {
          imgfolder = "subfolder";
        }
        iicon = new UIEnum(e, imgfolder != null ? "settings/" + imgfolder : imgfolder);
      }

      model.addElement(iicon);

      if (e.name().equals(property.getValue())) {
        model.setSelectedItem(iicon);
      }
    }
    ((WebComboBox) component).setRenderer(new IconComboRenderer<>(component));
    ((WebComboBox) component).setModel(model);

    return new GridBagComponent(component, getGroupConstraint(1, true, true, level));
  }

  private GridBagComponent createTestField(final IProperty property, JComponent cmp, int level) {
    WebButton button = (WebButton) createComponent(Component.BUTTON, settingsi18n + "test");
    button.setIcon(ImageUtils.TEST_16);
    final JComponent component = createComponent(Component.FIELD, null);
    button.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        UITestSettings testSettings = (UITestSettings) property;
        ITestActionListener actionListener = testSettings.getActionListener();
        actionListener.actionPerformed(e);
        ((WebTextField) component).setText(actionListener.getResult());
        ((WebTextField) component).setEditable(false);
      }

    });

    GridBagConstraints ctr = getGroupConstraint(0, false, false, level);
    ctr.insets.top += 25;
    cmp.add(button, ctr);

    ctr = getGroupConstraint(1, true, true, level);
    ctr.insets.top += 25;

    return new GridBagComponent(component, ctr);
  }

  private GridBagComponent createPathField(final IProperty property, JComponent cmp, String title, int level) {
    WebButton button = (WebButton) createComponent(Component.BUTTON, null);
    button.setIcon(ImageUtils.FOLDERVIDEO_16);
    button.setPreferredSize(null);
    button.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent ae) {
        File file = new File(fields.get(property).getText());
        fileChooser.setCurrentDirectory(file);
        int r = fileChooser.showOpenDialog(SettingPanelGen.this);
        if (r == 0) {
          try {
            String f = fileChooser.getSelectedFile().toString();
            property.setValue(f);
            fields.get(property).setText(f);
          } catch (IOException ex) {
          }
        }
      }
    });

    // Create text field for String and number value
    WebLabel label = (WebLabel) createComponent(Component.LABEL, title);
    JComponent component = createComponent(Component.FIELD, null);

    cmp.add(label, getGroupConstraint(0, false, false, level));
    cmp.add(button, getGroupConstraint(2, false, false, level));

    return new GridBagComponent(component, getGroupConstraint(1, true, true, level));
  }

  private GridBagComponent createPasswordField(JComponent cmp, String title, int level) {
    WebLabel label = (WebLabel) createComponent(Component.LABEL, title);
    JComponent component = createComponent(Component.PASSWORD, null);
    cmp.add(label, getGroupConstraint(0, false, false, level));

    return new GridBagComponent(component, getGroupConstraint(1, true, true, level));
  }

  @SuppressWarnings("unchecked")
  private GridBagComponent createList(IProperty property, JComponent cmp, String title, int level) {
    WebLabel label = (WebLabel) createComponent(Component.LABEL, title);
    DefaultListModel<Object> listModel = new DefaultListModel<>();
    List<Object> list = (List<Object>) property.getDefaultValue();
    WebList wlist = new WebList();
    JComponent component = new WebScrollPane(wlist);

    for (Object obj : list) {
      listModel.addElement(obj);
    }

    GridBagConstraints gcontrainte = getGroupConstraint(0, true, false, level);
    gcontrainte.insets.top += 10;

    cmp.add(label, gcontrainte);

    wlist.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    wlist.setLayoutOrientation(WebList.HORIZONTAL_WRAP);

    wlist.setModel(listModel);

    return new GridBagComponent(component, getGroupConstraint(0, true, true, level));
  }

  public void reset() {
    for (Map.Entry<IProperty, WebCheckBox> checkbox : checkboxs.entrySet()) {
      IProperty property = checkbox.getKey();
      WebCheckBox cb = checkbox.getValue();
      boolean isSelected = Boolean.parseBoolean(property.getValue());
      boolean changed = cb.isSelected() != isSelected;
      cb.setSelected(isSelected);

      // Call listener to enabled/disabled childs
      if (property.hasChild() && changed) {
        for (ActionListener listener : cb.getActionListeners()) {
          listener.actionPerformed(new ActionEvent(cb, ActionEvent.ACTION_PERFORMED, ""));
        }
      }
    }

    for (Map.Entry<IProperty, WebTextField> textfield : fields.entrySet()) {
      IProperty property = textfield.getKey();
      textfield.getValue().setText(property.getValue());
    }

    for (Map.Entry<IProperty, WebPasswordField> passfield : passFields.entrySet()) {
      IProperty property = passfield.getKey();
      passfield.getValue().setText(StringUtils.decrypt(property.getValue()));
    }

    for (Map.Entry<IProperty, WebComboBox> combobox : comboboxs.entrySet()) {
      IProperty property = combobox.getKey();
      WebComboBox cb = combobox.getValue();
      for (int i = 0; i < cb.getItemCount(); i++) {

        IIconList iconlist = (IIconList) cb.getItemAt(i);
        String name = property.getValue();
        if (iconlist.getName().equals(name)) {
          cb.setSelectedIndex(i);
          break;
        }
      }
    }
  }

  // TODO
  private void setScraperPopupOptions(WebButton button, WebButtonPopup buttonPopup, UIScraper scraper) {
    button.setEnabled(false);
    if (!scraper.hasOptions()) {
      return;
    }

    button.setEnabled(true);

    UILang lng = (UILang) comboboxs.get(SettingsProperty.searchScrapperLang).getSelectedItem();
    scraperOptComboboxs.clear();

    Settings settings = Settings.getInstance();
    List<JComponent> cbboxs = new ArrayList<>();
    List<MovieScrapper> scrappers = ScrapperManager.getMovieScrapperList();
    List<MovieScrapper> scrappersLang = ScrapperManager.getMovieScrapperList((LocaleUtils.AvailableLanguages) lng.getLanguage());
    UIScraper uiscrapper;

    for (ScrapperOptions option : scraper.getOptions()) {
      WebLabel label = (WebLabel) createComponent(Component.LABEL, settingsi18n + option.getProperty().name().toLowerCase());
      WebComboBox cbb = new WebComboBox();
      DefaultComboBoxModel<UIScraper> model = new DefaultComboBoxModel<>();
      cbb.setModel(model);

      for (MovieScrapper scrapper : option.isIsLangdep() ? scrappersLang : scrappers) {
        if (scrapper.getClass().equals(scraper.getScraper().getClass())) {
          continue;
        }

        uiscrapper = new UIScraper(scrapper);
        model.addElement(uiscrapper);
        if (settings.getMovieScrapperOptionClass(option.getProperty()).equals(scrapper.getClass())) {
          cbb.setSelectedItem(uiscrapper);
        }
      }

      cbb.setRenderer(new IconComboRenderer<>(cbb));
      cbboxs.add(new GroupPanel(GroupingType.fillAll, 5, label, cbb));
      scraperOptComboboxs.put(option.getProperty(), cbb);
    }

    GroupPanel content = new GroupPanel(5, false, cbboxs.toArray(new JComponent[cbboxs.size()]));
    content.setMargin(15);

    WebScrollPane wsp = new WebScrollPane(content);
    wsp.setPreferredHeight(200);
    wsp.setPreferredWidth(500);
    buttonPopup.setContent(wsp);
  }

  /**
   * Create title toolbar with help button
   *
   * @param title
   * @param type
   * @param subType
   * @param help
   * @return WebToolBar
   */
  protected WebToolBar createTitle(String title, final Settings.SettingsType type, final Settings.SettingsSubType subType, boolean help) {
    WebToolBar toolbar = createTitle(title);

    if (help) {
      final WebButton button = UIUtils.createButton(i18n.getLanguageKey(settingsi18n + "help", false), ImageUtils.HELP_16, false, false);
      button.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
          try {
            SettingsHelpDialog dialog = new SettingsHelpDialog(mr, type, subType);
            dialog.getHelp();
            dialog.setVisible(true);
          } catch (MalformedURLException ex) {
            Logger.getLogger(SettingPanelGen.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      });
      toolbar.addToEnd(button);
    }

    return toolbar;
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

  public Map<IProperty, WebComboBox> getScraperOptCombobox() {
    return Collections.unmodifiableMap(scraperOptComboboxs);
  }

  public Map<IProperty, WebPasswordField> getPassField() {
    return Collections.unmodifiableMap(passFields);
  }

  private class GridBagComponent {

    private final GridBagConstraints constraint;
    private final JComponent component;

    public GridBagComponent(JComponent component, GridBagConstraints constraint) {
      this.component = component;
      this.constraint = constraint;
    }

    public JComponent getComponent() {
      return component;
    }

    public GridBagConstraints getConstraint() {
      return constraint;
    }

  }
}
