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

import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.filechooser.WebFileChooser;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.text.WebPasswordField;
import com.alee.laf.text.WebTextField;
import com.alee.laf.toolbar.WebToolBar;
import com.alee.managers.popup.PopupWay;
import com.alee.managers.popup.WebButtonPopup;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.scraper.MediaScraper;
import fr.free.movierenamer.scraper.MovieScraper;
import fr.free.movierenamer.scraper.ScraperManager;
import fr.free.movierenamer.searchinfo.Media.MediaType;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.settings.XMLSettings.IMediaProperty;
import fr.free.movierenamer.settings.XMLSettings.IProperty;
import fr.free.movierenamer.settings.XMLSettings.ISimpleProperty;
import fr.free.movierenamer.settings.XMLSettings.SettingsSubType;
import fr.free.movierenamer.settings.XMLSettings.SettingsType;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.IIconList;
import fr.free.movierenamer.ui.bean.IImage;
import fr.free.movierenamer.ui.bean.UIEnum;
import fr.free.movierenamer.ui.bean.UIEvent;
import fr.free.movierenamer.ui.bean.UIEventInfo;
import fr.free.movierenamer.ui.bean.UILang;
import fr.free.movierenamer.ui.bean.UIScraper;
import fr.free.movierenamer.ui.utils.FlagUtils;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.swing.contextmenu.ContextMenuField;
import fr.free.movierenamer.ui.swing.dialog.SettingsHelpDialog;
import fr.free.movierenamer.ui.swing.renderer.IconComboRenderer;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import static fr.free.movierenamer.ui.utils.UIUtils.i18n;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.LocaleUtils.Language;
import fr.free.movierenamer.utils.NumberUtils;
import fr.free.movierenamer.utils.StringUtils;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
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
    private static final String settingsi18n = "settings.";
    private final ContextMenuField contextMenuField = new ContextMenuField();
    private Map<WebCheckBox, SettingsProperty> checkboxs;
    private Map<WebTextField, SettingsProperty> fields;
    private Map<WebPasswordField, SettingsProperty> passFields;
    private Map<WebComboBox, SettingsProperty> comboboxs;
    private Map<WebComboBox, SettingsProperty> scraperOptComboboxs;
    private final MovieRenamer mr;

    private static enum MediaTypeIcon implements IImage {

        MOVIE(MediaType.MOVIE, ImageUtils.MOVIE_16),
        TVSHOW(MediaType.TVSHOW, ImageUtils.TV_16),;

        private final MediaType mediaType;
        private final Icon icon;

        private MediaTypeIcon(MediaType mediaType, Icon icon) {
            this.mediaType = mediaType;
            this.icon = icon;
        }

        public MediaType getMediaType() {
            return mediaType;
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
        public Icon getIcon() {
            return icon;
        }

        @Override
        public String getName() {
            return UIUtils.i18n.getLanguage(name().toLowerCase(), false);
        }

    }

    /**
     * Define icon for enum (weblist icon)
     */
    private static enum EnumIcon {

        AvailableLanguages(null) {
                    @Override
                    public IIconList getIcon(Enum<?> value) {
                        return FlagUtils.getFlagByLang(value.name());
                    }
                },
        AppLanguages(null) {
                    @Override
                    public IIconList getIcon(Enum<?> value) {
                        return FlagUtils.getFlagByLang(value.name());
                    }
                },
        NFOType("mediacenter"),
        CaseConversionType("case"),
        ImageFormat("image"),
        Subfolder("subfolder");
        private final String folder;

        private EnumIcon(String folder) {
            this.folder = folder;
        }

        public IIconList getIcon(Enum<?> value) {
            return new UIEnum(value, folder != null ? "settings/" + folder : folder);
        }

    }

    private static enum SettingsClazz {

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
        LogLevel,
        UITestSettings,
        UIPathSettings,
        UIPasswordSettings;
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

    public void generatePanel(SettingsType type, Map<SettingsSubType, List<IProperty>> mapProperties, Map<SettingsSubType, List<IMediaProperty>> mapMediaProperties,
            boolean helpBtn) {

        checkboxs = new HashMap<>();
        fields = new HashMap<>();
        comboboxs = new HashMap<>();
        passFields = new HashMap<>();
        scraperOptComboboxs = new HashMap<>();

        boolean createTitle;

        for (List<IProperty> properties : mapProperties.values()) {

            final List<JComponent> childs = new ArrayList<>();
            createTitle = true;

            // Add "normal" settings"
            for (IProperty property : properties) {
                addSettings(type, property, this, null, childs, createTitle, helpBtn);
                createTitle = false;
            }

            // Add media settings for current "SettingsType"
            List<IMediaProperty> mediaProperties = mapMediaProperties.get(properties.get(0).getSubType());
            if (mediaProperties != null && !mediaProperties.isEmpty()) {
                mapMediaProperties.remove(properties.get(0).getSubType());
                WebTabbedPane tabbed = addMediaSettings(mediaProperties, type, false, helpBtn);
                GridBagConstraints gc = getGroupConstraint(0, true, true, 1);
                gc.insets.top *= 10;
                this.add(tabbed, gc);
            }

        }

        // Add media settings
        for (List<IMediaProperty> mediaProperties : mapMediaProperties.values()) {
            createTitle = true;

            if (!mediaProperties.isEmpty()) {
                WebTabbedPane tabbed = addMediaSettings(mediaProperties, type, createTitle, helpBtn);
                this.add(tabbed, getGroupConstraint(0, true, true, 1));
            }
        }

        // Add a dummy panel to avoid centering
        add(new JPanel(), getDummyPanelConstraint());
    }

    private void addSettings(SettingsType type, IProperty property, JComponent container, MediaType mediaType, final List<JComponent> childs,
            boolean createTitle, boolean helpBtn) {

        GridBagComponent gbComponent = addComponent(container, type, mediaType, property, createTitle, helpBtn);
        if (gbComponent == null) {
            return;
        }

        if (property.isChild()) {
            gbComponent.getComponent().setEnabled(false);
            childs.add(gbComponent.getComponent());
            return;
        }

        if (property.hasChild()) {
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

    private WebTabbedPane addMediaSettings(List<IMediaProperty> mediaProperties, SettingsType type, boolean createTitle, boolean helpBtn) {

        Map<MediaType, List<JComponent>> mchilds = new HashMap<>();
        WebTabbedPane tabbed = new WebTabbedPane();

        for (MediaTypeIcon mediaTypeIcon : MediaTypeIcon.values()) {
            List<IMediaProperty> mProperties = new ArrayList<>();
            for (IMediaProperty propertie : mediaProperties) {
                if (propertie.hasMediaType(mediaTypeIcon.getMediaType())) {
                    mProperties.add(propertie);
                }
            }

            if (mProperties.isEmpty()) {
                continue;
            }

            JComponent cmp = new WebPanel();
            cmp.setLayout(new GridBagLayout());

            for (IMediaProperty mproperty : mProperties) {

                List<JComponent> childs = mchilds.get(mediaTypeIcon.getMediaType());
                if (childs == null) {
                    childs = new ArrayList<>();
                    mchilds.put(mediaTypeIcon.getMediaType(), childs);
                }

                addSettings(type, mproperty, cmp, mediaTypeIcon.getMediaType(), childs, createTitle, helpBtn);
                createTitle = false;
            }

            tabbed.addTab(mediaTypeIcon.getName(), mediaTypeIcon.getIcon(), cmp);
        }

        return tabbed;
    }

    private GridBagComponent addComponent(JComponent cmp, SettingsType type, MediaType mediaType, IProperty property, boolean createTitle, boolean helpBtn) {
        String title;
        SettingsClazz clazz;
        GridBagComponent gbComponent = null;
        boolean charField = false;
        int level = property.isChild() ? 2 : 1;

        // Create title toolbar (SettingsSubType)
        if (createTitle) {
            Settings.SettingsSubType subType = property.getSubType();
            if (subType != null) {
                add(createTitle(settingsi18n + subType.name().toLowerCase(), type, subType, helpBtn), getTitleConstraint());
            }
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
                    fields.put((WebTextField) gbComponent.getComponent(), new SettingsProperty(property, mediaType));
                    break;

                case Boolean:
                    gbComponent = createCheckbox(cmp, title, level);
                    checkboxs.put((WebCheckBox) gbComponent.getComponent(), new SettingsProperty(property, mediaType));
                    break;

                case Class:
                    gbComponent = createClassCombobox(property, cmp, mediaType, title, level);
                    comboboxs.put((WebComboBox) gbComponent.getComponent(), new SettingsProperty(property, mediaType));
                    break;

                case UITestSettings:
                    gbComponent = createTestField(property, cmp, level);
                    // Test field is not saved
                    break;

                case UIPathSettings:
                    gbComponent = createPathField(cmp, title, level);
                    fields.put((WebTextField) gbComponent.getComponent(), new SettingsProperty(property, mediaType));
                    break;

                case ArrayList:
                    gbComponent = createList(property, cmp, mediaType, title, level);
                    // TODO edit/save list
                    break;

                case UIPasswordSettings:
                    gbComponent = createPasswordField(cmp, title, level);
                    passFields.put((WebPasswordField) gbComponent.getComponent(), new SettingsProperty(property, mediaType));
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
                case LogLevel:

                    gbComponent = createEnumCombobox(property, cmp, mediaType, title, level);
                    comboboxs.put((WebComboBox) gbComponent.getComponent(), new SettingsProperty(property, mediaType));
                    break;
            }

            if (gbComponent == null) {
                UISettings.LOGGER.severe(String.format("GridBag component is null for %s", property.name()));
                return null;
            }

            cmp.add(gbComponent.getComponent(), gbComponent.getConstraint());
        } catch (Exception ex) {
            UISettings.LOGGER.log(Level.SEVERE, String.format("Unknown component for %s : %s", property.name(), property.getVclass()));
        }

        return gbComponent;
    }

    private GridBagComponent createTextField(JComponent cmp, String title, int level, boolean charField) {
        JComponent component = createComponent(charField ? Component.CHARFIELD : Component.FIELD, null);
        component.addMouseListener(contextMenuField);
        cmp.add(createComponent(Component.LABEL, title), getGroupConstraint(0, false, false, level));

        return new GridBagComponent(component, getGroupConstraint(1, true, true, level));
    }

    private GridBagComponent createCheckbox(JComponent cmp, String title, int level) {
        JComponent component = createComponent(Component.CHECKBOX, null);
        cmp.add(createComponent(Component.LABEL, title), getGroupConstraint(0, false, false, level));

        return new GridBagComponent(component, getGroupConstraint(1, true, true, level));
    }

    @SuppressWarnings("unchecked")
    private GridBagComponent createClassCombobox(IProperty property, JComponent cmp, MediaType mediaType, String title, int level) {
        JComponent component = new WebComboBox();
        DefaultComboBoxModel<UIScraper> model = new DefaultComboBoxModel<>();

        cmp.add(createComponent(Component.LABEL, title), getGroupConstraint(0, false, false, level));

        for (MediaScraper scraper : ScraperManager.getMediaScrapers(mediaType)) {
            model.addElement(new UIScraper(scraper));
        }

        if (MovieScraper.class.isAssignableFrom((Class<?>) property.getVclass())) {// FIXME if, else if, ...

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

        }

        ((WebComboBox) component).setModel(model);
        ((WebComboBox) component).setRenderer(new IconComboRenderer<>(component));

        return new GridBagComponent(component, getGroupConstraint(1, true, true, level));
    }

    private GridBagComponent createEnumCombobox(IProperty property, JComponent cmp, MediaType mediaType, String title, int level) {
        JComponent component = new WebComboBox();
        DefaultComboBoxModel<IIconList> model = new DefaultComboBoxModel<>();

        cmp.add(createComponent(Component.LABEL, title), getGroupConstraint(0, false, false, level));

        @SuppressWarnings("unchecked")
        Class<? extends Enum<?>> clazz = (Class<? extends Enum<?>>) property.getVclass();

        Object value;
        if (mediaType != null && property instanceof IMediaProperty) {
            value = ((IMediaProperty) property).getDefaultValue(mediaType);
        } else {
            value = ((ISimpleProperty) property).getDefaultValue();
        }

        for (Enum<?> e : clazz.getEnumConstants()) {

            IIconList iicon;
            try {
                iicon = EnumIcon.valueOf(clazz.getSimpleName()).getIcon(e);
            } catch (Exception ex) {
                iicon = new UIEnum(e, null);
            }

            model.addElement(iicon);

            if (e.name().equals(value)) {
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
        component.addMouseListener(contextMenuField);
//        button.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                UITestSettings testSettings = (UITestSettings) property;
//                ITestActionListener actionListener = testSettings.getActionListener();
//                actionListener.actionPerformed(e);
//                ((WebTextField) component).setText(actionListener.getResult());
//                ((WebTextField) component).setEditable(false);
//            }
//
//        });

        GridBagConstraints ctr = getGroupConstraint(0, false, false, level);
        ctr.insets.top += 25;
        cmp.add(button, ctr);

        ctr = getGroupConstraint(1, true, true, level);
        ctr.insets.top += 25;

        return new GridBagComponent(component, ctr);
    }

    private GridBagComponent createPathField(JComponent cmp, String title, int level) {
        WebButton button = (WebButton) createComponent(Component.BUTTON, null);
        button.setIcon(ImageUtils.FOLDERVIDEO_16);
        button.setPreferredSize(null);

        WebLabel label = (WebLabel) createComponent(Component.LABEL, title);
        final JComponent component = createComponent(Component.FIELD, null);
        component.addMouseListener(contextMenuField);

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                File file = new File(((WebTextField) component).getText());
                fileChooser.setCurrentDirectory(file);
                int r = fileChooser.showOpenDialog(SettingPanelGen.this);
                if (r == 0) {
                    String f = fileChooser.getSelectedFile().toString();
                    ((WebTextField) component).setText(f);
                }
            }
        });

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

    private GridBagComponent createList(IProperty property, JComponent cmp, MediaType mediaType, String title, int level) {
        WebLabel label = (WebLabel) createComponent(Component.LABEL, title);
        DefaultListModel<Object> listModel = new DefaultListModel<>();
        WebList wlist = new WebList();
        JComponent component = new WebScrollPane(wlist);

        List<Object> list;
        if (mediaType != null && property instanceof IMediaProperty) {
            list = (List<Object>) ((IMediaProperty) property).getDefaultValue(mediaType);
        } else {
            list = (List<Object>) ((ISimpleProperty) property).getDefaultValue();
        }

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
        String value;
        SettingsProperty settingsProperty;
        IProperty property;
        WebCheckBox checkbox;
        WebTextField field;
        WebPasswordField pField;
        WebComboBox combobox;

        for (Entry<WebCheckBox, SettingsProperty> entry : checkboxs.entrySet()) {
            settingsProperty = entry.getValue();
            property = settingsProperty.getProperty();
            checkbox = entry.getKey();

            if (property instanceof IMediaProperty) {
                value = ((IMediaProperty) property).getValue(settingsProperty.getMediaType());
            } else {
                value = ((ISimpleProperty) property).getValue();
            }

            boolean isSelected = Boolean.parseBoolean(value);
            boolean changed = checkbox.isSelected() != isSelected;
            checkbox.setSelected(isSelected);

            // Call listener to enabled/disabled childs
            if (property.hasChild() && changed) {
                for (ActionListener listener : checkbox.getActionListeners()) {
                    listener.actionPerformed(new ActionEvent(checkbox, ActionEvent.ACTION_PERFORMED, ""));
                }
            }
        }

        for (Entry<WebTextField, SettingsProperty> entry : fields.entrySet()) {
            settingsProperty = entry.getValue();
            property = settingsProperty.getProperty();
            field = entry.getKey();

            if (property instanceof IMediaProperty) {
                value = ((IMediaProperty) property).getValue(settingsProperty.getMediaType());
            } else {
                value = ((ISimpleProperty) property).getValue();
            }
            field.setText(value);
        }

        for (Entry<WebPasswordField, SettingsProperty> entry : passFields.entrySet()) {
            settingsProperty = entry.getValue();
            property = settingsProperty.getProperty();
            pField = entry.getKey();

            if (property instanceof IMediaProperty) {
                value = ((IMediaProperty) property).getValue(settingsProperty.getMediaType());
            } else {
                value = ((ISimpleProperty) property).getValue();
            }
            pField.setText(StringUtils.decrypt(value));
        }

        for (Entry<WebComboBox, SettingsProperty> entry : comboboxs.entrySet()) {
            settingsProperty = entry.getValue();
            property = settingsProperty.getProperty();
            combobox = entry.getKey();

            for (int i = 0; i < combobox.getItemCount(); i++) {

                IIconList iconlist = (IIconList) combobox.getItemAt(i);
                if (property instanceof IMediaProperty) {
                    value = ((IMediaProperty) property).getValue(settingsProperty.getMediaType());
                } else {
                    value = ((ISimpleProperty) property).getValue();
                }

                if (iconlist.getName().equals(value)) {
                    combobox.setSelectedIndex(i);
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

//        button.setEnabled(true);
//
//        UILang lng = (UILang) comboboxs.get(SettingsProperty.searchScraperLang).getSelectedItem();
//        scraperOptComboboxs.clear();
//
//        Settings settings = Settings.getInstance();
//        List<JComponent> cbboxs = new ArrayList<>();
//        List<MovieScraper> scrapers = ScraperManager.getMovieScraperList();
//        List<MovieScraper> scrapersLang = ScraperManager.getMovieScraperList((LocaleUtils.AvailableLanguages) lng.getLanguage());
//        UIScraper uiscraper;
//
//        for (ScraperOptions option : scraper.getOptions()) {
//            WebLabel label = (WebLabel) createComponent(Component.LABEL, settingsi18n + option.getProperty().name().toLowerCase());
//            WebComboBox cbb = new WebComboBox();
//            DefaultComboBoxModel<UIScraper> model = new DefaultComboBoxModel<>();
//            cbb.setModel(model);
//
//            for (MovieScraper movieScraper : option.isIsLangdep() ? scrapersLang : scrapers) {
//                if (movieScraper.getClass().equals(scraper.getScraper().getClass())) {
//                    continue;
//                }
//
//                uiscraper = new UIScraper(movieScraper);
//                model.addElement(uiscraper);
//                if (settings.getMovieScraperOptionClass(option.getProperty()).equals(movieScraper.getClass())) {
//                    cbb.setSelectedItem(uiscraper);
//                }
//            }
//
//            cbb.setRenderer(new IconComboRenderer<>(cbb));
//            cbboxs.add(new GroupPanel(GroupingType.fillAll, 5, label, cbb));
//            scraperOptComboboxs.put(cbb, option.getProperty());
//        }
//
//        GroupPanel content = new GroupPanel(5, false, cbboxs.toArray(new JComponent[cbboxs.size()]));
//        content.setMargin(15);
//
//        WebScrollPane wsp = new WebScrollPane(content);
//        wsp.setPreferredHeight(200);
//        wsp.setPreferredWidth(500);
//        buttonPopup.setContent(wsp);
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

    public void save() { // FIXME Settings should not be save if there is an error (like NAN). Same thing for event

        SettingsProperty settingsProperty;
        IProperty property;
        String oldValue;

        try {

            // Save checkbox
            for (Map.Entry<WebCheckBox, SettingsProperty> checkbox : checkboxs.entrySet()) {

                settingsProperty = checkbox.getValue();
                property = settingsProperty.getProperty();

                boolean isSelected = checkbox.getKey().isSelected();
                if (property instanceof ISimpleProperty) {
                    oldValue = ((ISimpleProperty) property).getValue();
                    ((ISimpleProperty) property).setValue(isSelected);
                } else {
                    oldValue = ((IMediaProperty) property).getValue(settingsProperty.getMediaType());
                    ((IMediaProperty) property).setValue(settingsProperty.getMediaType(), isSelected);
                }

                if (!oldValue.equals(String.valueOf(isSelected))) {
                    UIEvent.fireUIEvent(UIEvent.Event.SETTINGS, new UIEventInfo("", settingsProperty.getMediaType()), null, property);
                }
            }

            // Save text field
            for (Map.Entry<WebTextField, SettingsProperty> field : fields.entrySet()) {
                settingsProperty = field.getValue();
                property = settingsProperty.getProperty();

                String value = field.getKey().getText();
                Object defaultValue;
                if (property instanceof ISimpleProperty) {
                    oldValue = ((ISimpleProperty) property).getValue();
                    defaultValue = ((ISimpleProperty) property).getDefaultValue();
                    ((ISimpleProperty) property).setValue(value);
                } else {
                    oldValue = ((IMediaProperty) property).getValue(settingsProperty.getMediaType());
                    defaultValue = ((IMediaProperty) property).getDefaultValue(settingsProperty.getMediaType());
                    ((IMediaProperty) property).setValue(settingsProperty.getMediaType(), value);
                }

                if (defaultValue instanceof Number) {
                    if (!NumberUtils.isNumeric(value)) {
                        WebOptionPane.showMessageDialog(mr, i18n.getLanguage("error.nan", false, i18n.getLanguage("settings." + property.name().toLowerCase(), false)),
                                i18n.getLanguage("error.error", false), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else if (defaultValue instanceof Pattern) {
                    try {
                        Pattern.compile(value);
                    } catch (Exception ex) {
                        WebOptionPane.showMessageDialog(mr, i18n.getLanguage("error.invalidPattern", false, i18n.getLanguage("settings." + property.name().toLowerCase(), false)),
                                i18n.getLanguage("error.error", false), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                if (!oldValue.equals(value)) {
                    UIEvent.fireUIEvent(UIEvent.Event.SETTINGS, new UIEventInfo("", settingsProperty.getMediaType()), null, property);
                }
            }

            // Save password field
            for (Map.Entry<WebPasswordField, SettingsProperty> field : passFields.entrySet()) {
                settingsProperty = field.getValue();
                property = settingsProperty.getProperty();
                String value = new String(field.getKey().getPassword());
                if (property instanceof ISimpleProperty) {
                    oldValue = ((ISimpleProperty) property).getValue();
                    ((ISimpleProperty) property).setValue(value);
                } else {
                    oldValue = ((IMediaProperty) property).getValue(settingsProperty.getMediaType());
                    ((IMediaProperty) property).setValue(settingsProperty.getMediaType(), value);
                }

                if (!oldValue.equals(value)) {
                    UIEvent.fireUIEvent(UIEvent.Event.SETTINGS, new UIEventInfo("", settingsProperty.getMediaType()), null, property);
                }
            }

        } catch (IOException ex) {
            UISettings.LOGGER.log(Level.SEVERE, null, ex);
            WebOptionPane.showMessageDialog(mr, UIUtils.i18n.getLanguage("error.saveSettingsFailed", false), UIUtils.i18n.getLanguage("error.error", false), JOptionPane.ERROR_MESSAGE);
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

    private void saveCombobox(Map<WebComboBox, SettingsProperty> comboboxs) {
        for (Map.Entry<WebComboBox, SettingsProperty> combobox : comboboxs.entrySet()) {
            SettingsProperty settingsProperty = combobox.getValue();
            IProperty property = settingsProperty.getProperty();

            try {

                String oldValue;
                boolean isEnum = ((IProperty) property).getVclass().isEnum();
                if (property instanceof ISimpleProperty) {
                    oldValue = ((ISimpleProperty) property).getValue();
                } else {
                    oldValue = ((IMediaProperty) property).getValue(settingsProperty.getMediaType());
                }

                WebComboBox cmb = combobox.getKey();
                String value;
                Object item = cmb.getSelectedItem();
                if (isEnum) {
                    if (cmb.getSelectedItem() instanceof UILang) {
                        Language lang = ((UILang) item).getLanguage();
                        value = lang.toString();

                        if (property instanceof ISimpleProperty) {
                            ((ISimpleProperty) property).setValue(lang);
                        } else {
                            ((IMediaProperty) property).setValue(settingsProperty.getMediaType(), lang);
                        }

                    } else {
                        Enum<?> eval = ((UIEnum) item).getValue();
                        value = eval.toString();
                        if (property instanceof ISimpleProperty) {
                            ((ISimpleProperty) property).setValue(value);
                        } else {
                            ((IMediaProperty) property).setValue(settingsProperty.getMediaType(), eval);
                        }
                    }

                    if (!oldValue.equals(value)) {
                        UIEvent.fireUIEvent(UIEvent.Event.SETTINGS, new UIEventInfo("", settingsProperty.getMediaType()), null, property);
                    }

                } else if (item instanceof UIScraper) {
                    UIScraper scraper = (UIScraper) item;
                    value = scraper.getScraper().getClass().toString();
                    if (property instanceof ISimpleProperty) {
                        oldValue = ((ISimpleProperty) property).getValue();
                        ((ISimpleProperty) property).setValue(scraper.getScraper().getClass());
                    } else {
                        oldValue = ((IMediaProperty) property).getValue(settingsProperty.getMediaType());
                        ((IMediaProperty) property).setValue(settingsProperty.getMediaType(), scraper.getScraper().getClass());
                    }

                    if (!oldValue.equals(value)) {
                        UIEvent.fireUIEvent(UIEvent.Event.SETTINGS, new UIEventInfo("", settingsProperty.getMediaType()), null, property);
                    }
                } else {
                    UISettings.LOGGER.log(Level.SEVERE, String.format("saveCombobox : Unknown property %s", settingsProperty.property.name()));
                }

            } catch (IOException ex) {
                UISettings.LOGGER.log(Level.SEVERE, null, ex);
                WebOptionPane.showMessageDialog(mr, UIUtils.i18n.getLanguage("error.saveSettingsFailed", false), UIUtils.i18n.getLanguage("error.error", false), JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
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

    public class SettingsProperty {

        private final IProperty property;
        private final MediaType mediaType;

        public SettingsProperty(IProperty property, MediaType mediaType) {
            this.property = property;
            this.mediaType = mediaType;
        }

        public IProperty getProperty() {
            return property;
        }

        public MediaType getMediaType() {
            return mediaType;
        }

    }
}
