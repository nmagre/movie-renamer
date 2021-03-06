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
package fr.free.movierenamer.ui;

import fr.free.movierenamer.ui.event.IEventListener;
import fr.free.movierenamer.ui.event.IEventInfo;
import fr.free.movierenamer.ui.event.UIEvent;
import fr.free.movierenamer.ui.swing.custom.ListTooltip;
import fr.free.movierenamer.ui.swing.model.ImageListModel;
import fr.free.movierenamer.ui.swing.custom.DragAndDrop;
import fr.free.movierenamer.ui.swing.custom.ContextMenuField;
import fr.free.movierenamer.ui.swing.UIManager;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.DefaultEventListModel;
import com.alee.extended.layout.ToolbarLayout;
import com.alee.extended.statusbar.WebStatusBar;
import com.alee.extended.transition.ComponentTransition;
import com.alee.extended.transition.effects.Direction;
import com.alee.extended.transition.effects.curtain.CurtainTransitionEffect;
import com.alee.extended.transition.effects.curtain.CurtainType;
import com.alee.extended.transition.effects.fade.FadeTransitionEffect;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.filechooser.FileChooserViewType;
import com.alee.laf.filechooser.WebFileChooser;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.text.WebTextField;
import com.alee.laf.toolbar.WebToolBar;
import com.alee.managers.hotkey.Hotkey;
import com.alee.managers.language.data.TooltipWay;
import com.alee.managers.popup.PopupWay;
import com.alee.managers.popup.WebButtonPopup;
import com.alee.utils.swing.AncestorAdapter;
import fr.free.movierenamer.searchinfo.Media.MediaType;
import fr.free.movierenamer.settings.Settings.SettingsMediaProperty;
import fr.free.movierenamer.settings.Settings.SettingsProperty;
import fr.free.movierenamer.settings.XMLSettings.IProperty;
import fr.free.movierenamer.ui.bean.*;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.settings.UISettings.UISettingsProperty;
import fr.free.movierenamer.ui.swing.UIManager.UIMode;
import static fr.free.movierenamer.ui.swing.UIManager.group;
import fr.free.movierenamer.ui.swing.dialog.LoadingDialog;
import fr.free.movierenamer.ui.swing.panel.*;
import fr.free.movierenamer.ui.swing.renderer.*;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.ui.worker.WorkerManager;
import fr.free.movierenamer.ui.worker.impl.ImageWorker;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JToolBar.Separator;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.AncestorEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import static fr.free.movierenamer.ui.utils.UIUtils.i18n;
import fr.free.movierenamer.ui.worker.impl.CheckUpdateWorker;
import fr.free.movierenamer.ui.worker.impl.GetFilesInfoWorker;
import fr.free.movierenamer.utils.FileUtils;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Map;
import javax.swing.filechooser.FileFilter;

/**
 * Class MovieRenamer
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class MovieRenamer extends WebFrame implements IEventListener {

    private static final long serialVersionUID = 1L;
    private static final Dimension frameSize = new Dimension(900, 830);
    private static final UISettings setting = UISettings.getInstance();
    // Current variables
    private UIMode currentMode;
    private UIFile currentMedia;
    private boolean groupFile;
    // Panel
    private Map<UIMode, MediaPanel<? extends UIMediaInfo<?>, ?>> mediaPanels;
    private final WebPanel mainPanelNoImage;
    private final WebPanel mainPanelMini;
    private final WebPanel mainPanelMiniWithImage;
    private final SettingsPanel settingsPanel;

    private ComponentTransition containerTransitionMediaPanel;// Media Panel container
    private final WebFileChooser fileChooser = new WebFileChooser();
    // Clear interface
    private final boolean CLEAR_MEDIALIST = true;
    private final boolean CLEAR_SEARCHRESULTLIST = true;
    // Model
    private final EventList<UIFile> mediaFileEventList = new BasicEventList<>();
    private final ImageListModel<UISearchResult> searchResultModel = new ImageListModel<>();
    private final DefaultEventListModel<UIFile> mediaFileSeparatorModel;
    private final DefaultEventListModel<UIFile> mediaFileModel = new DefaultEventListModel<>(mediaFileEventList);
    private final MediaListRenderer mediaFileListRenderer = new MediaListRenderer();
    private final SearchResultListRenderer searchResultListRenderer = new SearchResultListRenderer();
    private final DefaultListModel<UILoader> loaderModel = new DefaultListModel<>();
    // Separator
    private final SeparatorList<UIFile> mediaFileSeparator = new SeparatorList<>(mediaFileEventList, UIUtils.groupFileComparator, 1, 1000);
    // List option checkbox
    private WebCheckBox showIconMediaListChk;
    private WebCheckBox showIconResultListChk;
    private WebCheckBox showIdResultListChk;
    private WebCheckBox showYearResultListChk;
    private WebCheckBox showOrigTitleResultListChk;
    private WebCheckBox showFormatFieldChk;
    private WebCheckBox showMediaPanelChk;
    private WebCheckBox showImagePanelChk;
    // UI tools
    public static final Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
    public static final Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    // List tooltip listener
    private final ListTooltip mediaFileTooltip = new ListTooltip();
    private final ListTooltip searchResultTooltip = new ListTooltip(1200, true);
    // Misc
    private final ListSelectionListener mediaFileListListener = createMediaFileListListener();
    private final ContextMenuField contextMenuField = new ContextMenuField();
    private WebButtonPopup mediaFileListsettingBtn;
    private LoadingDialog loadingDial;

    public MovieRenamer() {
        super();

        mediaFileSeparatorModel = new DefaultEventListModel<>(mediaFileSeparator);

        // Set Movie Renamer mode
        currentMode = UIMode.MOVIEMODE;

        // Main panel , we use several panels to keep animation when interface change
        mainPanelNoImage = new WebPanel(new BorderLayout());
        mainPanelMini = new WebPanel(new BorderLayout());
        mainPanelMiniWithImage = new WebPanel(new BorderLayout());
        settingsPanel = new SettingsPanel(this);

        initComponents();

        setIconImage(ImageUtils.iconToImage(ImageUtils.LOGO_22));
        setLanguage(UIUtils.i18n.getLanguageKey("title"), UISettings.APPNAME, UISettings.VERSION);

        addWindowListener(createWindowListener());

        setSize(frameSize);
        init();
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            if (setting.isMainFrameSaveState()) {
                setSize(new Dimension(setting.getMainFrameSizeWidth(), setting.getMainFrameSizeHeight()));
                UIUtils.showOnScreen(this, setting.getMainFrameScreen(), setting.getMainFrameLocationX(), setting.getMainFrameLocationY(), setting.getMainFrameState());
                listSp.setDividerLocation(setting.getMainFrameFileDivider());
                mediaSp.setDividerLocation(setting.getMainFrameInfoDivider());
            } else {
                UIUtils.showOnScreen(this, 0, -1, -1, WebFrame.NORMAL);
            }
        }
        super.setVisible(b);
    }

    @SuppressWarnings("unchecked")
    private void init() {
        // Panels/dialog/... init
        UIManager.init(this);

        mediaPanels = UIManager.createMediaPanel(this);
        mainPanel.add(UIManager.getImagePanel(), BorderLayout.EAST);

        // Create media panel container and set transition effect
        containerTransitionMediaPanel = new ComponentTransition(mediaPanels.get(currentMode));
        containerTransitionMediaPanel.setTransitionEffect(new FadeTransitionEffect());
        mainContainerTransition.setTransitionEffect(new FadeTransitionEffect());

        // Setting popup options
        showIconMediaListChk = UIUtils.createShowIconChk(mediaFileList, setting.isShowIconMediaList(),
                UIUtils.i18n.getLanguageKey("showIcon", "mediatb.settingspopup"));
        showIconResultListChk = UIUtils.createShowIconChk(searchResultList, setting.isShowThumbResultList(),
                UIUtils.i18n.getLanguageKey("showIcon", "searchtb.settingspopup"));
        showMediaPanelChk = new WebCheckBox();
        showMediaPanelChk.setLanguage(UIUtils.i18n.getLanguageKey("showMediaPanel", "mediatb.settingspopup"));
        showMediaPanelChk.setSelected(setting.isShowMediaPanel());
        showMediaPanelChk.addActionListener(createShowPanelListener());

        showImagePanelChk = new WebCheckBox();
        showImagePanelChk.setLanguage(UIUtils.i18n.getLanguageKey("showImagePanel", "mediatb.settingspopup"));
        showImagePanelChk.setSelected(setting.isShowImagePanel());
        showImagePanelChk.addActionListener(createShowPanelListener());

        showIdResultListChk = UIUtils.createShowChk(searchResultList, SearchResultListRenderer.Property.showId,
                setting.isShowIdResultList(), UIUtils.i18n.getLanguageKey("showId", "searchtb.settingspopup"));
        showYearResultListChk = UIUtils.createShowChk(searchResultList, SearchResultListRenderer.Property.showYear,
                setting.isShowYearResultList(), UIUtils.i18n.getLanguageKey("showYear", "searchtb.settingspopup"));
        showOrigTitleResultListChk = UIUtils.createShowChk(searchResultList, SearchResultListRenderer.Property.showOrigTitle,
                setting.isShowOrigTitleResultList(), UIUtils.i18n.getLanguageKey("showOrigTitle", "searchtb.settingspopup"));

        showFormatFieldChk = new WebCheckBox();
        showFormatFieldChk.setLanguage(UIUtils.i18n.getLanguageKey("showFormatField", "renametb.settingspopup"));
        showFormatFieldChk.setSelected(setting.isShowFormatField());
        showFormatFieldChk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFormatfield();
            }
        });

        // List loading model
        loaderModel.addElement(new UILoader());

        // Add button to main toolbar
        mainTb.addToEnd(logBtn);
        mainTb.addToEnd(aboutBtn);
        mainTb.addToEnd(new JSeparator(JSeparator.VERTICAL));
        mainTb.addToEnd(updateBtn);
        mainTb.addToEnd(settingBtn);
        mainTb.addToEnd(exitBtn);

        WebButton button = UIUtils.createSettingButton(null);
        mediaFileListsettingBtn = UIUtils.createPopup(button, PopupWay.downRight, toggleGroup, showIconMediaListChk, showMediaPanelChk, showImagePanelChk);

        scraperCb.setRenderer(new IconComboRenderer<>(scraperCb));

        renameTb.add(renameBtn);
        renameTb.add(renameField, ToolbarLayout.FILL);
        loadMediaPanel();

        mediaFileList.addListSelectionListener(mediaFileListListener);
        addDragAndDropListener(mediaFileList);// Add drag and drop listener on mediaFileList

        mediaFileList.addMouseListener(mediaFileTooltip);
        mediaFileList.addMouseMotionListener(mediaFileTooltip);

        mediaFileList.setCellRenderer(mediaFileListRenderer);

        searchResultList.addListSelectionListener(createSearchResultListListener());
        searchResultList.setCellRenderer(searchResultListRenderer);
        searchResultList.addMouseListener(searchResultTooltip);
        searchResultList.addMouseMotionListener(searchResultTooltip);

        fileChooser.setGenerateThumbnails(true);
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.getWebUI().getFileChooserPanel().setViewType(setting.getFileChooserViewType());

        fileChooser.addChoosableFileFilter(new FileFilter() {

            @Override
            public String getDescription() {
                return i18n.getLanguage("movie", false);
            }

            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }

                List<String> fileExtensions = setting.coreInstance.getfileExtension();
                return FileUtils.checkFileExt(f, fileExtensions.toArray(new String[fileExtensions.size()]));
            }
        });

        toggleGroup.setLanguage(UIUtils.i18n.getLanguageKey("mediatb.settingspopup.group"));
        toggleGroup.addActionListener(createToggleGroupListener());
        groupFile = setting.isGroupMediaList();
        setToggleGroupIcon();

        mediaFileTb.addToEnd(clearMediaFileListBtn);

        // Add settings button in toolbar
        mediaFileTb.addToEnd(button);
        searchTb.addToEnd(UIUtils.createSettingButton(PopupWay.downLeft, showIconResultListChk, showIdResultListChk, showYearResultListChk, showOrigTitleResultListChk));

        // Add context menu on textfield (right click menu)
        searchField.addMouseListener(contextMenuField);
        renameField.addMouseListener(contextMenuField);
        fileFormatField.addMouseListener(contextMenuField);

        // add mouse listener on status bar for task popup
        //statusBar.addMouseListener(createStatusBarListener());
        statusBar.add(new StatusPanel(), BorderLayout.CENTER);

        // Start up animation
        if (setting.isShowStartupAnim()) {
            remove(containerTransition);
            containerTransition.setTransitionEffect(new FadeTransitionEffect());
            final ComponentTransition appearanceTransition = new ComponentTransition(UIManager.createBackgroundPanel()) {
                private static final long serialVersionUID = 1L;

                @Override
                public Dimension getPreferredSize() {
                    return containerTransition.getPreferredSize();
                }
            };

            CurtainTransitionEffect effect = new CurtainTransitionEffect();
            effect.setDirection(Direction.down);
            effect.setType(CurtainType.fade);
            //effect.setSpeed(this.getSize().width / 10);

            appearanceTransition.setTransitionEffect(effect);
            appearanceTransition.addAncestorListener(new AncestorAdapter() {
                @Override
                public void ancestorAdded(AncestorEvent event) {
                    appearanceTransition.delayTransition(500, containerTransition);
                }
            });

            add(appearanceTransition, BorderLayout.CENTER);
        }

        int index = mainTb.getComponentIndex(openSep) + 1;
        // Add mode button
        for (UIMode mode : UIMode.values()) {
            mainTb.add(mode.getModebutton(this), index++);
        }

        //setSelected(true);
        // Set panel
        setMediaPanel();

        //statusBar.setVisible(false);
        // Font
        mediaLbl.setFont(UIUtils.titleFont);
        searchLbl.setFont(UIUtils.titleFont);

        // Add MovieRenamer (Main UI) to UIEvent receiver
        UIEvent.addEventListener(MovieRenamer.class, this);

        // Start timer (update, mediainfo)
        //UIManager.startInitTimer(this, mediainfoStatusLbl);
        // Start rename thread
        WorkerManager.startRenameThread();

    }

    /**
     *****************************
     *
     * LISTENER
     *
     *****************************
     */
    /**
     * UIEventHandler, handle ui event
     *
     * @param event
     * @param info
     * @param oldObj
     * @param newObj
     */
    @Override
    @SuppressWarnings("fallthrough")
    public void UIEventHandler(UIEvent.Event event, IEventInfo info, Object oldObj, Object newObj) {

        UISettings.LOGGER.fine(String.format("%s receive event %s %s [%s : %s]", getClass().getSimpleName(), event, (info != null ? info : ""), oldObj, newObj));

        switch (event) {
            case DOWNLOAD_START:// TODO
                if (loadingDial == null) {
                    loadingDial = new LoadingDialog();
                }
                break;
            case DOWNLOAD_DONE:// TODO
                if (loadingDial != null) {
                    loadingDial.dispose();
                }
                loadingDial = null;
                break;
            case RENAME_FILE:// TODO
                int index = mediaFileList.getSelectedIndex();

                if (index != -1) {
                    UIFile item = (UIFile) getMediaFileListModel().getElementAt(index);
                    item.setIcon(ImageUtils.LOAD_8);
                }

                break;
            case RENAME_FILE_DONE:// TODO
                UIFile uifile = (UIFile) (oldObj != null ? oldObj : newObj);
                index = mediaFileEventList.indexOf(uifile);

                if (index != -1) {
                    UIFile nuiFile = (UIFile) newObj;
                    UIFile item = mediaFileEventList.get(index);
                    item.setFile(nuiFile.getFile());
                    item.setIcon(null);
                }

                break;
            case SETTINGS:// TODO
//
//                if (info == null || !(info instanceof UIEventInfo)) {
//                    UISettings.LOGGER.warning("IEventInfo is null or not a UIEventInfo");
//                    return;
//                }
//
//                if (newObj instanceof IProperty) {
//                    applySettings((IProperty) newObj, (MediaType) ((UIEventInfo) info).getEventObject());
//                }
//
//                if (newObj instanceof IProperty) {
//
//                    if (newObj instanceof SettingsMediaProperty) {
//                        SettingsMediaProperty sproperty = (SettingsMediaProperty) newObj;
//
//                        switch (sproperty) {
//                            case mediaFilenameSeparator:
//                                break;
//                        }
//
//                    }
//
//                    if (newObj instanceof SettingsProperty) {
//                        SettingsProperty sproperty = (SettingsProperty) newObj;
//                        System.out.println(sproperty.name() + " changed to " + sproperty.getValue());// FIXME remove
//                        switch (sproperty) {
//                            //case mediaFilenameFormat:
//                            //UIMode.MOVIEMODE.setFileformat(sproperty.getValue());
//                            //case mediaFilenameCase:
//                            //case mediaFilenameLimit:
//                            case filenameRmDupSpace:
//                            case filenameTrim:
//                            case reservedCharacter:
//                                updateRenamedTitle();
//                                break;
//                            case appLanguage:
//                                LanguageManager.setLanguage(sproperty.getValue());
//                                settingsPanel.updateListLanguage();
//                                // FIXME update UI
//                                break;
//                            //case movieNfogenerate:
////                UIManager.setCheckBox(sproperty);
//                            //              break;
//                            default:
//                                UISettings.LOGGER.warning(String.format("Property %s is not defined in UIEventHandler", sproperty));
//                        }
//                    }
//
//                    if (newObj instanceof UISettingsProperty) {
//                        boolean updatePanel = false;
//                        UISettingsProperty uisproperty = (UISettingsProperty) newObj;
//                        System.out.println(uisproperty.name() + " changed to " + uisproperty.getValue());// FIXME remove
//                        switch (uisproperty) {
//                            case showMediaPanel:
//                                showMediaPanelChk.setSelected(Boolean.parseBoolean(uisproperty.getValue()));
//                                updatePanel = true;
//                                break;
//                            case showImagePanel:
//                                showImagePanelChk.setSelected(Boolean.parseBoolean(uisproperty.getValue()));
//                                updatePanel = true;
//                                break;
//                            case showFormatField:
//                                showFormatFieldChk.setSelected(Boolean.parseBoolean(uisproperty.getValue()));
//                                setFormatfield();
//                                break;
//                            case downloadBanner:
//                            case downloadCdart:
//                            case downloadClearart:
//                            case downloadFanart:
//                            case downloadLogo:
//                            case downloadThumb:
//                                UIManager.setCheckBox(uisproperty);
//                                break;
//                            case showIconMediaList:
//                                showIconMediaListChk.setSelected(Boolean.parseBoolean(uisproperty.getValue()));
//                                showIconMediaListChk.getActionListeners()[0].actionPerformed(null);// FIXME so dirty :(
//                                break;
//                            case groupMediaList:
//                                groupFile = !Boolean.parseBoolean(uisproperty.getValue());// FIXME so dirty :(
//                                toggleGroup.getActionListeners()[0].actionPerformed(null);// FIXME so dirty :(
//                                break;
//                            case logLevel:
//                                UISettings.setAppLogLevel(Level.parse(uisproperty.getValue()));
//                                break;
//                        }
//
//                        if (updatePanel) {
//                            setMediaPanel();
//                        }
//                    }
//                }
                break;

            case UPDATE_AVAILABLE:
                UIManager.update(this, (UIUpdate) newObj);
                break;
            case NO_UPDATE:
                UIUtils.showNotification(i18n.getLanguage("dialog.alreadyUptodate", false));
                break;
            case REFRESH_MEDIAINFO:
                UISearchResult searchResult = getSelectedSearchResult();
                if (searchResult == null) {
                    return;
                }

                searchMediaInfo(searchResult);
                break;
        }
    }

    @SuppressWarnings("unchecked")
    private ActionListener createToggleGroupListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int index = mediaFileList.getSelectedIndex();
                Object obj = mediaFileList.getSelectedValue();
                groupFile = !groupFile;

                MediaListRenderer.Property.showGroup.setEnabled(groupFile);
                mediaFileList.setModel(getMediaFileListModel());

                if (index > -1 && obj != null) {
                    mediaFileList.removeListSelectionListener(mediaFileListListener);
                    mediaFileList.setSelectedValue(obj, false);
                    mediaFileList.ensureIndexIsVisible(mediaFileList.getSelectedIndex());
                    mediaFileList.addListSelectionListener(mediaFileListListener);
                }
                mediaFileList.revalidate();
                setToggleGroupIcon();
            }
        };
    }

    private ListSelectionListener createMediaFileListListener() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                WebList lsm = (WebList) lse.getSource();
                if (!lsm.getValueIsAdjusting() && !lsm.isSelectionEmpty()) {
                    UIFile mediaFile;

                    if (lsm.getSelectedValue() instanceof SeparatorList.Separator) {
                        clearInterface(!CLEAR_MEDIALIST, CLEAR_SEARCHRESULTLIST);
                        return;
                    }

                    try {
                        mediaFile = getSelectedMediaFile();
                    } catch (ClassCastException ex) {// Spinningdial (user cancel)
                        clearInterface(!CLEAR_MEDIALIST, CLEAR_SEARCHRESULTLIST);
                        return;
                    }

                    if (mediaFile == null) {
                        clearInterface(!CLEAR_MEDIALIST, CLEAR_SEARCHRESULTLIST);
                        searchField.setText("");
                        UISettings.LOGGER.log(Level.SEVERE, "Media file is null for : {0}", lse.toString());
                        return;
                    }

                    currentMedia = mediaFile;
                    WorkerManager.setSearch(MovieRenamer.this, currentMedia);
                }
            }
        };
    }

    private ListSelectionListener createSearchResultListListener() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                WebList lsm = (WebList) lse.getSource();
                if (!lsm.getValueIsAdjusting() && !lsm.isSelectionEmpty()) {
                    UISearchResult searchResult = null;
                    try {
                        searchResult = getSelectedSearchResult();
                    } catch (ClassCastException ex) {// Spinningdial (user cancel)
                        clearInterface(!CLEAR_MEDIALIST, CLEAR_SEARCHRESULTLIST);
                    }

                    if (searchResult == null) {
                        return;
                    }

                    searchMediaInfo(searchResult);
                }
            }
        };
    }

    private void addDragAndDropListener(WebList list) {
        DragAndDrop dropFile = new DragAndDrop(this) {
            @Override
            public void getFiles(List<File> files, List<URL> urls) {
                loadFiles(files);
            }
        };

        DropTarget dt = new DropTarget(list, dropFile);
        dt.setActive(true);
    }

    private ActionListener createShowPanelListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setMediaPanel();
            }
        };
    }

    private WindowAdapter createWindowListener() {
        return new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                closeApp();
            }
        };
    }

    /**
     *******************************
     *
     * LOADER
     *
     *******************************
     */
    /**
     * Load and show media files
     *
     * @param files
     */
    @SuppressWarnings("unchecked")
    public void loadFiles(List<File> files) {
        clearMediaFileListBtn.setEnabled(false);
        clearInterface(CLEAR_MEDIALIST, CLEAR_SEARCHRESULTLIST);
        mediaFileList.setModel(loaderModel);
        WorkerManager.listFiles(this, files, mediaFileEventList);
    }

    public void setMode(UIMode mode) {
        currentMode = mode;
        loadMediaPanel();
    }

    /*
     * Load media panel with fade effect
     */
    @SuppressWarnings("unchecked")
    private void loadMediaPanel() {

        renameTb.setVisible(true);

        setMediaPanel();

        scraperCb.setModel(currentMode.getScraperModel());
        fileFormatField.setText(currentMode.getFileFormat());
        containerTransitionMediaPanel.performTransition(mediaPanels.get(currentMode));
        setFormatfield();

        // Remove setting button
        Component cmp = renameTb.getLastComponent();

        if (cmp instanceof WebButton) {
            renameTb.remove(cmp);
        }

        List<JComponent> components = UIManager.getRenameSettingsComponents(currentMode);
        components.add(showFormatFieldChk);
        // Add settings button in toolbar
        renameTb.addToEnd(UIUtils.createSettingButton(PopupWay.upLeft, components.toArray(new JComponent[components.size()])));
        renameTb.revalidate();
    }

    /**
     *******************************
     *
     * ACTION
     *
     *******************************
     */
    /**
     * Search media on web
     */
    @SuppressWarnings("unchecked")
    public void searchMedia() {

        if (currentMedia == null) {
            return;
        }

        String search = searchField.getText().trim();
        if (search.length() == 0) {
            UIUtils.showWrongNotification(i18n.getLanguage("error.searchempty", false));
            return;
        }

        clearInterface(!CLEAR_MEDIALIST, CLEAR_SEARCHRESULTLIST);

        searchResultList.setModel(loaderModel);
        currentMedia.setSearch(search);
        WorkerManager.search(this, currentMedia, currentMode.getSearchExtraParam(currentMedia));
    }

    /*
     * Search for media information
     */
    private void searchMediaInfo(UISearchResult searchResult) {

        if (searchResult == null || currentMedia == null) {
            return;
        }

        // Stop all running workers except search result list image worker (thumbnail in result list)
        clearInterface(!CLEAR_MEDIALIST, !CLEAR_SEARCHRESULTLIST, ImageWorker.class, UISearchResult.class);

        WorkerManager.searchInfo(this, searchResult, currentMode);

        if (showMediaPanelChk.isSelected()) {
            if (currentMode.hasImage() && showImagePanelChk.isSelected()) {
                WorkerManager.searchImage(this, searchResult);
            }

            if (currentMode.hasTrailer()) {
                // WorkerManager.searchTrailer(this, searchResult);
            }
        }
    }

    /**
     * Clear Movie Renamer interface
     *
     * @param mediaList Clear media list
     * @param searchList Clear search list
     */
    @SuppressWarnings("unchecked")
    private void clearInterface(boolean clearMediaList, boolean clearSearchResultList, Class... clazz) {

        if (clearMediaList) {
            searchField.setText(null);
            mediaFileEventList.clear();
            mediaFileSeparator.clear();
            mediaFileList.setModel(getMediaFileListModel());
            mediaCount.setText("");
        }

        if (clearSearchResultList) {
            searchBtn.setEnabled(false);
            searchField.setEnabled(false);
            searchResultModel.clear();
        }

        UIManager.clearImagePanel();
        mediaPanels.get(currentMode).clear();
        updateRenamedTitle();
        renameBtn.setEnabled(false);
        renameField.setEnabled(false);

        if (clazz.length == 2) {
            WorkerManager.stopExcept(clazz[0], clazz[1]);
            return;
        }

        // Stop all running workers
        if (clearMediaList) {
            WorkerManager.stop();
        } else {
            WorkerManager.stopExcept(GetFilesInfoWorker.class, null);
        }

    }

    public void closeSettings() {
        currentMode.getModebutton(this).doClick();
    }

    /**
     * Update renamed title in rename field
     */
    public void updateRenamedTitle() {
        UIMediaInfo<?> mediaInfo = getMediaPanel().getInfo();
        renameField.setText(null);

        if (mediaInfo != null) {
            renameField.setText(mediaInfo.getInfo().getRenamedTitle(currentMedia.getFileInfo(), fileFormatField.getText(), currentMode.getMediaType()));
        }
    }

    /**
     * Close Movie Renamer
     */
    private void closeApp() {// FIXME check if rename worker is done

//        if (!renameWorkerDone) {
//            int n = WebOptionPane.showConfirmDialog(MovieRenamer.this, UIUtils.i18n.getLanguage("dialog.wantrestart", false, Settings.APPNAME),
//                    UIUtils.i18n.getLanguage("dialog.question", false), WebOptionPane.YES_NO_OPTION, WebOptionPane.QUESTION_MESSAGE);
//
//            if (n != 0) {
//                return;
//            }
//        }
        clearInterface(CLEAR_MEDIALIST, CLEAR_SEARCHRESULTLIST);
        WorkerManager.stopRenameThread();

        if (setting.isMainFrameSaveState()) {
            try {
                UISettings.UISettingsProperty.mainFrameState.setValue(this.getExtendedState());
                Point location = this.getLocation();
                UISettings.UISettingsProperty.mainFrameLocationX.setValue(location.x);
                UISettings.UISettingsProperty.mainFrameLocationY.setValue(location.y);
                UISettings.UISettingsProperty.mainFrameScreen.setValue(UIUtils.getScreen(this));
                UISettings.UISettingsProperty.mainFrameFileDivider.setValue(listSp.getDividerLocation());
                UISettings.UISettingsProperty.mainFrameInfoDivider.setValue(mediaSp.getDividerLocation());
                Dimension wsize = this.getSize();
                UISettings.UISettingsProperty.mainFrameSizeWidth.setValue(wsize.width);
                UISettings.UISettingsProperty.mainFrameSizeHeight.setValue(wsize.height);
            } catch (IOException ex) {
                UISettings.LOGGER.log(Level.SEVERE, null, ex);
            }
        }

        System.exit(0);
    }

    private void applySettings(IProperty property, MediaType mediaType) {
        if (property instanceof SettingsProperty) {
            applySettings((SettingsProperty) property);
        } else if (property instanceof SettingsMediaProperty) {
            applyMediaSettings((SettingsMediaProperty) property, mediaType);
        } else if (property instanceof UISettingsProperty) {
            applyUISettings((UISettingsProperty) property);
        }
    }

    private void applySettings(SettingsProperty property) {

    }

    private void applyMediaSettings(SettingsMediaProperty property, MediaType mediaType) {

    }

    private void applyUISettings(UISettingsProperty property) {

    }

    /**
     *******************************
     *
     * GETTER
     *
     ********************************
     */
    /**
     *
     * @return
     */
    public DefaultEventListModel<UIFile> getMediaFileListModel() {
        return groupFile ? mediaFileSeparatorModel : mediaFileModel;
    }

    public final ImageListModel<UISearchResult> getSearchResultListModel() {
        return searchResultModel;
    }

    public final WebList getMediaList() {
        return mediaFileList;
    }

    public final WebList getSearchResultList() {
        return searchResultList;
    }

    public final UIMode getMode() {
        return currentMode;
    }

    public final UIScraper getUIScraper() {
        return currentMode.getSelectedScraper();
    }

    public final MediaPanel<? extends UIMediaInfo<?>, ?> getMediaPanel() {
        return mediaPanels.get(currentMode);
    }

    private WebPanel getMainPanel(boolean showMediaPanel, boolean showImagePanel) {

        mainPanel.removeAll();
        mainPanelNoImage.removeAll();
        mainPanelMini.removeAll();
        mainPanelMiniWithImage.removeAll();
        mediaSp.remove(searchPnl);
        mediaSp.setTopComponent(null);
        centerPanel.removeAll();

        listSp.setOrientation(showMediaPanel ? WebSplitPane.HORIZONTAL_SPLIT : WebSplitPane.VERTICAL_SPLIT);

        int display = 0;
        if (showMediaPanel) {
            display |= 0x1;
            mediaSp.setTopComponent(searchPnl);
            mediaSp.setBottomComponent(containerTransitionMediaPanel);
            centerPanel.add(mediaSp, BorderLayout.CENTER);
        } else {
            centerPanel.add(searchPnl, BorderLayout.CENTER);
        }

        if (showImagePanel) {
            display |= 0x2;
        }

        switch (display) {
            case 0:// main panel mini (Media list + search)
                mainPanelMini.add(listSp, BorderLayout.CENTER);
                return mainPanelMini;
            case 1:// main panel without image
                mainPanelNoImage.add(listSp, BorderLayout.CENTER);
                return mainPanelNoImage;
            case 2:// main panel mini (Media list + search) + image
                mainPanelMiniWithImage.add(listSp, BorderLayout.CENTER);
                mainPanelMiniWithImage.add(UIManager.getImagePanel(), BorderLayout.EAST);
                return mainPanelMiniWithImage;
            case 3:// main panel (normal, with all panels)
                mainPanel.add(listSp, BorderLayout.CENTER);
                mainPanel.add(UIManager.getImagePanel(), BorderLayout.EAST);
                return mainPanel;
        }

        return mainPanel;
    }

    public boolean isShowIconResult() {
        return showIconResultListChk.isSelected();
    }

    /**
     * Get selected media file
     *
     * @return UIFile selected or null
     */
    private UIFile getSelectedMediaFile() {
        return UIUtils.getSelectedElement(mediaFileList);
    }

    /**
     * Get selected search result
     *
     * @return UISearchResult selected or null
     */
    private UISearchResult getSelectedSearchResult() {
        return UIUtils.getSelectedElement(searchResultList);
    }

    /**
     *******************************
     *
     * SETTER
     *
     ********************************
     */
    private void setToggleGroupIcon() {
        toggleGroup.setIcon(groupFile ? ImageUtils.GROUPVIEW_16 : ImageUtils.FILEVIEW_16);
    }

    public void setClearMediaFileListBtnEnabled() {
        clearMediaFileListBtn.setEnabled(true);
    }

    public void setSearchEnabled() {
        searchBtn.setEnabled(true);
        searchField.setEnabled(true);
    }

    public void setSearch() {
        searchField.setText(currentMedia.getSearch());
    }

    public void setRenameFieldEnabled() {
        renameField.setEnabled(true);
    }

    public synchronized void setRenamebuttonEnabled() {
        // We need to wait for searchMediaWorker and searchimageWorker
        // FIXME if user disable image
        UIMediaInfo<?> mediaInfo = getMediaPanel().getInfo();
        if (mediaInfo != null) {
            renameBtn.setEnabled(true);
        }
    }

    private void setMediaPanel() {
        mediaFileListsettingBtn.hidePopup();
        if (showMediaPanelChk.isSelected()) {
            searchResultTooltip.setTooltipWay(TooltipWay.right);
            mediaFileTooltip.setTooltipWay(TooltipWay.right);

            UISearchResult searchResult = getSelectedSearchResult();
            if (searchResult != null && currentMode.hasImage() && showImagePanelChk.isSelected()) {// FIXME check if searchImage was launched before (not important, the cache will be used)
                WorkerManager.searchImage(this, searchResult);
            }

            mediaFileListsettingBtn.setPopupWay(PopupWay.downRight);
        } else {
            searchResultTooltip.setTooltipWay(TooltipWay.down);
            mediaFileTooltip.setTooltipWay(TooltipWay.down);
            mediaFileListsettingBtn.setPopupWay(PopupWay.downLeft);
        }

        mainContainerTransition.performTransition(getMainPanel(showMediaPanelChk.isSelected(), showImagePanelChk.isSelected()));
    }

    private void setFormatfield() {
        if (showFormatFieldChk.isSelected()) {
            renameTb.add(fileFormatField, 1);
        } else {
            if (fileFormatField.getParent() == renameTb) {
                renameTb.remove(fileFormatField);
            }
        }
        renameTb.revalidate();
    }

    public void setMediaCount(int count) {
        mediaCount.setText("(" + count + ")");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        updateBtn = UIUtils.createButton(i18n.getLanguageKey("toptb.update"), ImageUtils.UPDATE_24, ImageUtils.UPDATE_16);
        settingBtn = UIUtils.createButton(i18n.getLanguageKey("toptb.settings"), ImageUtils.SETTING_24, ImageUtils.SETTING_16, Hotkey.CTRL_S, MovieRenamer.this);
        exitBtn = UIUtils.createButton(i18n.getLanguageKey("toptb.quit"), ImageUtils.APPLICATIONEXIT_24, ImageUtils.APPLICATIONEXIT_16, Hotkey.CTRL_Q, MovieRenamer.this);
        toggleGroup = new WebButton();
        aboutBtn = UIUtils.createButton(i18n.getLanguageKey("toptb.about"), ImageUtils.HELP_24, ImageUtils.HELP_16);
        renameBtn = new WebButton();
        clearMediaFileListBtn = UIUtils.createButton(i18n.getLanguageKey("mediatb.clear"), ImageUtils.CLEAR_LIST_16, ImageUtils.CLEAR_LIST_16);
        logBtn = UIUtils.createButton(i18n.getLanguageKey("toptb.log"), ImageUtils.INFO_24, ImageUtils.INFO_16);
        renameField = new WebTextField();
        fileFormatField = new WebTextField();
        containerTransition = new ComponentTransition();
        mainTb = new WebToolBar();
        openBtn = UIUtils.createButton(i18n.getLanguageKey("toptb.open"), ImageUtils.FOLDERVIDEO_24, ImageUtils.FOLDERVIDEO_16, Hotkey.CTRL_O, MovieRenamer.this);
        historyBtn = UIUtils.createButton(i18n.getLanguageKey("toptb.history"), ImageUtils.HISTORY_24, ImageUtils.HISTORY_16, Hotkey.CTRL_P, MovieRenamer.this);
        openSep = new Separator();
        modeSep = new Separator();
        mainContainerTransition = new ComponentTransition();
        mainPanel = new WebPanel();
        listSp = new WebSplitPane();
        mediaFilePnl = new WebPanel();
        mediaFileTb = new WebToolBar();
        mediaLbl = new WebLabel();
        mediaCount = new WebLabel();
        mediaFileScp = new JScrollPane();
        mediaFileList = new WebList();
        centerPanel = new WebPanel();
        mediaSp = new WebSplitPane();
        searchPnl = new WebPanel();
        searchTb = new WebToolBar();
        searchLbl = new WebLabel();
        searchBtn = UIUtils.createButton(i18n.getLanguageKey("searchtb.search"), ImageUtils.SEARCH_16, true, ImageUtils.SEARCH_16);
        searchField = new WebTextField();
        searchResultListSp = new JScrollPane();
        searchResultList = new WebList();
        scraperCb = new WebComboBox();
        renamePnl = new WebPanel();
        renameTb = new WebToolBar();
        statusBar = new WebStatusBar();

        updateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                updateBtnActionPerformed(evt);
            }
        });

        settingBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                settingBtnActionPerformed(evt);
            }
        });

        exitBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        exitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                exitBtnActionPerformed(evt);
            }
        });

        toggleGroup.setIcon(ImageUtils.FILEVIEW_16);

        aboutBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                aboutBtnActionPerformed(evt);
            }
        });

        renameBtn.setLanguage(i18n.getLanguageKey("renametb.rename"));

        renameBtn.setIcon(ImageUtils.OK_16);
        renameBtn.setEnabled(false);
        renameBtn.setFocusable(false);
        renameBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                renameBtnActionPerformed(evt);
            }
        });

        clearMediaFileListBtn.setAlignmentY(0.0F);
        clearMediaFileListBtn.setEnabled(false);
        clearMediaFileListBtn.setFocusPainted(true);
        clearMediaFileListBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                clearMediaFileListBtnActionPerformed(evt);
            }
        });

        logBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                logBtnActionPerformed(evt);
            }
        });

        fileFormatField.setPreferredSize(new Dimension(250, 27));
        fileFormatField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                fileFormatFieldKeyReleased(evt);
            }
        });

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 580));

        containerTransition.setLayout(new BorderLayout());

        mainTb.setFloatable(false);
        mainTb.setRollover(true);
        mainTb.setMargin(new Insets(1, 4, 1, 4));
        mainTb.setRound(10);

        openBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                openBtnActionPerformed(evt);
            }
        });
        mainTb.add(openBtn);

        historyBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        historyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                historyBtnActionPerformed(evt);
            }
        });
        mainTb.add(historyBtn);
        mainTb.add(openSep);
        mainTb.add(modeSep);

        containerTransition.add(mainTb, BorderLayout.PAGE_START);

        listSp.setDividerLocation(180);

        mediaFilePnl.setMargin(new Insets(4, 5, 4, 5));

        mediaFileTb.setFloatable(false);
        mediaFileTb.setMargin(new Insets(0, 4, 0, 4));
        mediaFileTb.setRound(5);

        mediaLbl.setLanguage(i18n.getLanguageKey("mediatb.media"));
        mediaLbl.setIcon(ImageUtils.MEDIA_16);
        mediaFileTb.add(mediaLbl);
        mediaFileTb.add(mediaCount);

        mediaFileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mediaFileScp.setViewportView(mediaFileList);

        GroupLayout mediaFilePnlLayout = new GroupLayout(mediaFilePnl);
        mediaFilePnl.setLayout(mediaFilePnlLayout);
        mediaFilePnlLayout.setHorizontalGroup(mediaFilePnlLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(mediaFileTb, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(mediaFileScp, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
        );
        mediaFilePnlLayout.setVerticalGroup(mediaFilePnlLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(mediaFilePnlLayout.createSequentialGroup()
                .addComponent(mediaFileTb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(mediaFileScp, GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE))
        );

        listSp.setLeftComponent(mediaFilePnl);

        mediaSp.setDividerLocation(200);
        mediaSp.setOrientation(JSplitPane.VERTICAL_SPLIT);
        mediaSp.setMinimumSize(new Dimension(300, 500));
        mediaSp.setPreferredSize(new Dimension(300, 500));
        mediaSp.setContinuousLayout(true);

        searchPnl.setMargin(new Insets(4, 5, 4, 5));
        searchPnl.setMinimumSize(new Dimension(0, 200));
        searchPnl.setPreferredSize(new Dimension(300, 200));

        searchTb.setFloatable(false);
        searchTb.setRollover(true);
        searchTb.setMargin(new Insets(0, 4, 0, 4));
        searchTb.setRound(5);

        searchLbl.setLanguage(i18n.getLanguageKey("searchtb.search"));
        searchLbl.setIcon(ImageUtils.LOOK_16);
        searchTb.add(searchLbl);

        searchBtn.setEnabled(false);
        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                searchBtnActionPerformed(evt);
            }
        });

        searchField.setEnabled(false);
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                searchFieldKeyReleased(evt);
            }
        });

        searchResultListSp.setMinimumSize(new Dimension(80, 25));

        searchResultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchResultList.setFixedCellHeight(75);
        searchResultList.setFocusable(false);
        searchResultListSp.setViewportView(searchResultList);

        scraperCb.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
        scraperCb.setDrawFocus(false);

        GroupLayout searchPnlLayout = new GroupLayout(searchPnl);
        searchPnl.setLayout(searchPnlLayout);
        searchPnlLayout.setHorizontalGroup(searchPnlLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(searchTb, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(searchPnlLayout.createSequentialGroup()
                .addComponent(searchField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(scraperCb, GroupLayout.PREFERRED_SIZE, 173, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(searchBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addComponent(searchResultListSp, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 766, Short.MAX_VALUE)
        );
        searchPnlLayout.setVerticalGroup(searchPnlLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(searchPnlLayout.createSequentialGroup()
                .addComponent(searchTb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(searchPnlLayout.createParallelGroup(Alignment.LEADING)
                    .addComponent(searchField, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchBtn, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(scraperCb, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(searchResultListSp, GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
        );

        searchField.setLeadingComponent(new JLabel(ImageUtils.SEARCH_16));

        mediaSp.setTopComponent(searchPnl);

        centerPanel.add(mediaSp, BorderLayout.CENTER);

        listSp.setRightComponent(centerPanel);

        listSp.setContinuousLayout(true);

        mainPanel.add(listSp, BorderLayout.CENTER);

        mainContainerTransition.add(mainPanel);

        containerTransition.add(mainContainerTransition, BorderLayout.CENTER);

        getContentPane().add(containerTransition, BorderLayout.CENTER);

        renameTb.setFloatable(false);
        renameTb.setRollover(true);
        renameTb.setRound(10);
        renamePnl.add(renameTb, BorderLayout.NORTH);

        statusBar.setMargin(new Insets(2, 10, 2, 10));
        statusBar.setPreferredSize(new Dimension(20, 23));
        statusBar.setLayout(new BorderLayout());
        renamePnl.add(statusBar, BorderLayout.SOUTH);

        getContentPane().add(renamePnl, BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

  private void openBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_openBtnActionPerformed
      File file = new File(setting.getFileChooserPath());

      fileChooser.setCurrentDirectory(file);
      fileChooser.getWebUI().getFileChooserPanel().setViewType(setting.getFileChooserViewType());
      int r = fileChooser.showOpenDialog(this);

      try {
          FileChooserViewType fcvt = fileChooser.getWebUI().getFileChooserPanel().getViewType();
          UISettingsProperty.fileChooserViewType.setValue(fcvt);
      } catch (IOException e) {
          UISettings.LOGGER.log(Level.SEVERE, e.getMessage());
          WebOptionPane.showMessageDialog(MovieRenamer.this, UIUtils.i18n.getLanguage("error.failSaveFolderPath", false), UIUtils.i18n.getLanguage("error.error", false), WebOptionPane.ERROR_MESSAGE);
      }

      if (r == 0) {
          List<File> files = Arrays.asList(fileChooser.getSelectedFiles());
          if (!files.isEmpty()) {// Remember path
              try {
                  UISettingsProperty.fileChooserPath.setValue(files.get(0).getParent());
              } catch (IOException e) {
                  UISettings.LOGGER.log(Level.SEVERE, e.getMessage());
                  WebOptionPane.showMessageDialog(MovieRenamer.this, UIUtils.i18n.getLanguage("error.failSaveFolderPath", false), UIUtils.i18n.getLanguage("error.error", false), WebOptionPane.ERROR_MESSAGE);
              }
          }

          loadFiles(files);
      }
  }//GEN-LAST:event_openBtnActionPerformed

  private void exitBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_exitBtnActionPerformed
      closeApp();
  }//GEN-LAST:event_exitBtnActionPerformed

  private void settingBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_settingBtnActionPerformed

      //SwingUtils.setEnabledRecursively(mainTb, false);
      renameTb.setVisible(false);
      exitBtn.setEnabled(true);
      group.setSelected(settingBtn.getModel(), true);
      settingsPanel.reset();
      mainContainerTransition.performTransition(settingsPanel);
  }//GEN-LAST:event_settingBtnActionPerformed

  private void updateBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_updateBtnActionPerformed
      CheckUpdateWorker updateWorker = new CheckUpdateWorker(MovieRenamer.this, true);
      updateWorker.execute();
  }//GEN-LAST:event_updateBtnActionPerformed

  private void renameBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_renameBtnActionPerformed

      UIFile mediaFile = getSelectedMediaFile();// We don't use "currentMedia" to avoid lost of ref
      if (mediaFile == null) {// TODO error
          return;
      }

      try {
          WorkerManager.rename(this, mediaFile, UIManager.getUIRenamer(mediaFile, renameField.getText(), currentMode));
      } catch (InterruptedException ex) {
          UISettings.LOGGER.log(Level.SEVERE, null, ex);
      }

      clearInterface(!CLEAR_MEDIALIST, CLEAR_SEARCHRESULTLIST);

      int index = mediaFileList.getSelectedIndex();
      if (index != -1) {
          index++;
          DefaultEventListModel<UIFile> model = getMediaFileListModel();
          if (index < model.getSize()) {
              if (model.getElementAt(index) instanceof SeparatorList.Separator) {
                  index++;
              }

              mediaFileList.setSelectedIndex(index);
          } else {
              UIUtils.showNotification("end of list reached");// FIXME i18n
          }
      }

  }//GEN-LAST:event_renameBtnActionPerformed

  private void searchBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
      searchMedia();
  }//GEN-LAST:event_searchBtnActionPerformed

  private void searchFieldKeyReleased(KeyEvent evt) {//GEN-FIRST:event_searchFieldKeyReleased
      if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
          searchBtnActionPerformed(null);
      }
  }//GEN-LAST:event_searchFieldKeyReleased

  private void fileFormatFieldKeyReleased(KeyEvent evt) {//GEN-FIRST:event_fileFormatFieldKeyReleased
      updateRenamedTitle();
      currentMode.setFileformat(fileFormatField.getText());
  }//GEN-LAST:event_fileFormatFieldKeyReleased

  private void aboutBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_aboutBtnActionPerformed
      UIManager.showAboutDialog();
  }//GEN-LAST:event_aboutBtnActionPerformed

  private void clearMediaFileListBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_clearMediaFileListBtnActionPerformed
      clearInterface(CLEAR_MEDIALIST, CLEAR_SEARCHRESULTLIST);
      clearMediaFileListBtn.setEnabled(false);
  }//GEN-LAST:event_clearMediaFileListBtnActionPerformed

  private void logBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_logBtnActionPerformed
      UIManager.showLogDialog();
  }//GEN-LAST:event_logBtnActionPerformed

  private void historyBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_historyBtnActionPerformed
      UIManager.showHistoryDialog();
  }//GEN-LAST:event_historyBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private WebButton aboutBtn;
    private WebPanel centerPanel;
    private WebButton clearMediaFileListBtn;
    private ComponentTransition containerTransition;
    private WebButton exitBtn;
    private WebTextField fileFormatField;
    private WebButton historyBtn;
    private WebSplitPane listSp;
    private WebButton logBtn;
    private ComponentTransition mainContainerTransition;
    private WebPanel mainPanel;
    private WebToolBar mainTb;
    private WebLabel mediaCount;
    private WebList mediaFileList;
    private WebPanel mediaFilePnl;
    private JScrollPane mediaFileScp;
    private WebToolBar mediaFileTb;
    private WebLabel mediaLbl;
    private WebSplitPane mediaSp;
    private Separator modeSep;
    private WebButton openBtn;
    private Separator openSep;
    private WebButton renameBtn;
    private WebTextField renameField;
    private WebPanel renamePnl;
    private WebToolBar renameTb;
    private WebComboBox scraperCb;
    private WebButton searchBtn;
    private WebTextField searchField;
    private WebLabel searchLbl;
    private WebPanel searchPnl;
    private WebList searchResultList;
    private JScrollPane searchResultListSp;
    private WebToolBar searchTb;
    private WebButton settingBtn;
    private WebStatusBar statusBar;
    private WebButton toggleGroup;
    private WebButton updateBtn;
    // End of variables declaration//GEN-END:variables
}
