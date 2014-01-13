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

import fr.free.movierenamer.ui.swing.UIManager;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.EventListModel;
import com.alee.extended.image.DisplayType;
import com.alee.extended.image.WebImage;
import com.alee.extended.layout.ToolbarLayout;
import com.alee.extended.statusbar.WebStatusBar;
import com.alee.extended.transition.ComponentTransition;
import com.alee.extended.transition.effects.Direction;
import com.alee.extended.transition.effects.curtain.CurtainTransitionEffect;
import com.alee.extended.transition.effects.curtain.CurtainType;
import com.alee.extended.transition.effects.fade.FadeTransitionEffect;
import com.alee.laf.StyleConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
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
import com.alee.managers.language.LanguageManager;
import com.alee.managers.popup.PopupWay;
import com.alee.managers.popup.WebButtonPopup;
import com.alee.managers.tooltip.TooltipWay;
import com.alee.utils.swing.AncestorAdapter;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.ui.bean.*;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.settings.UISettings.UISettingsProperty;
import fr.free.movierenamer.ui.swing.*;
import fr.free.movierenamer.ui.swing.dialog.LoadingDialog;
import fr.free.movierenamer.ui.swing.panel.*;
import fr.free.movierenamer.ui.swing.renderer.*;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.ui.worker.WorkerManager;
import fr.free.movierenamer.ui.worker.impl.ImageWorker;
import fr.free.movierenamer.ui.worker.impl.RenamerWorker;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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
import fr.free.movierenamer.utils.Cache;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Class MovieRenamer
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class MovieRenamer extends WebFrame implements IEventListener {

  private static final long serialVersionUID = 1L;
  private final UISettings setting = UISettings.getInstance();
  // Current variables
  private UIMode currentMode;
  private UIFile currentMedia;
  private boolean groupFile;
  // Panel
  private Map<UIMode, MediaPanel<? extends MediaInfo>> mediaPanels;
  private final WebPanel mainPanelNoImage;
  private final WebPanel mainPanelMini;
  private final WebPanel mainPanelMiniWithImage;

  private ComponentTransition containerTransitionMediaPanel;// Media Panel container
  private final WebFileChooser fileChooser = new WebFileChooser();
  // Clear interface
  private final boolean CLEAR_MEDIALIST = true;
  private final boolean CLEAR_SEARCHRESULTLIST = true;
  // Model
  private final EventList<UIFile> mediaFileEventList = new BasicEventList<>();
  private final ImageListModel<UISearchResult> searchResultModel = new ImageListModel<>();
  private final EventListModel<UIFile> mediaFileSeparatorModel;
  private final EventListModel<UIFile> mediaFileModel = new EventListModel<>(mediaFileEventList);
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
  // Renamer worker queue
  private final Queue<RenamerWorker> renamerWorkerQueue = new LinkedList<>();
  // Task popup
  private final TaskPopup taskPopup = new TaskPopup(this);
  // List tooltip listener
  private final ListTooltip mediaFileTooltip = new ListTooltip();
  private final ListTooltip searchResultTooltip = new ListTooltip(1200, true);
  // Misc
  private final ListSelectionListener mediaFileListListener = createMediaFileListListener();
  private final ContextMenuFieldMouseListener contextMenuFieldMouseListener = new ContextMenuFieldMouseListener();
  private WebButtonPopup mediaFileListsettingBtn;
  private List<ImageInfo> images;
  private LoadingDialog loadingDial;

  public MovieRenamer(List<File> files) {
    super();

    loadingDial = new LoadingDialog();

    // Cache.clearAllCache();// FIXME remove !!!
    mediaFileSeparatorModel = new EventListModel<>(mediaFileSeparator);

    // Set Movie Renamer mode
    currentMode = UIMode.MOVIEMODE;

    // Main panel , we use several panels to keep animation when interface change
    mainPanelNoImage = new WebPanel(new BorderLayout());
    mainPanelMini = new WebPanel(new BorderLayout());
    mainPanelMiniWithImage = new WebPanel(new BorderLayout());

    initComponents();

    setIconImage(ImageUtils.iconToImage(ImageUtils.LOGO_22));
    setLanguage(UIUtils.i18n.getLanguageKey("title"), UISettings.APPNAME, UISettings.VERSION);

    init(files);
  }

  @Override
  public void setVisible(boolean b) {
    if (b) {
      UIUtils.showOnScreen(this);
    }
    super.setVisible(b);
  }

  @SuppressWarnings("unchecked")// Weblaf is in java 6, and combobox, .. use generic type now. We must unchecked on setRenderer, .. method :(
  private void init(List<File> files) {
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
    showMediaPanelChk = new WebCheckBox(UIUtils.i18n.getLanguageKey("showMediaPanel", "popupmenu"));
    showMediaPanelChk.setSelected(setting.isShowMediaPanel());
    showMediaPanelChk.addActionListener(createShowPanelListener());

    showImagePanelChk = new WebCheckBox(UIUtils.i18n.getLanguageKey("showImagePanel", "popupmenu"));
    showImagePanelChk.setSelected(setting.isShowImagePanel());
    showImagePanelChk.addActionListener(createShowPanelListener());

    showIdResultListChk = UIUtils.createShowChk(searchResultList, SearchResultListRenderer.Property.showId,
            setting.isShowIdResultList(), UIUtils.i18n.getLanguageKey("showId", "popupmenu"));
    showYearResultListChk = UIUtils.createShowChk(searchResultList, SearchResultListRenderer.Property.showYear,
            setting.isShowYearResultList(), UIUtils.i18n.getLanguageKey("showYear", "popupmenu"));
    showOrigTitleResultListChk = UIUtils.createShowChk(searchResultList, SearchResultListRenderer.Property.showOrigTitle,
            setting.isShowOrigTitleResultList(), UIUtils.i18n.getLanguageKey("showOrigTitle", "popupmenu"));

    showFormatFieldChk = new WebCheckBox(UIUtils.i18n.getLanguageKey("showFormatField", "popupmenu"));
    showFormatFieldChk.setSelected(setting.isShowFormatField());
    showFormatFieldChk.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        fileFormatField.setVisible(showFormatFieldChk.isSelected());
      }
    });

    // List loading model
    loaderModel.addElement(new UILoader());

    // Add button to main toolbar
    mainTb.addToEnd(aboutBtn);
    mainTb.addToEnd(new JSeparator(JSeparator.VERTICAL));
    mainTb.addToEnd(updateBtn);
    mainTb.addToEnd(settingBtn);
    mainTb.addToEnd(exitBtn);

    WebButton button = UIUtils.createSettingButton(null);
    mediaFileListsettingBtn = UIUtils.createPopup(button, PopupWay.downRight, toggleGroup, showIconMediaListChk, showMediaPanelChk, showImagePanelChk);

    scrapperCb.setRenderer(new IconComboRenderer<>(scrapperCb));

    loadMediaPanel();

    mediaFileList.addListSelectionListener(mediaFileListListener);
    addDragAndDropListener(mediaFileList);//Add drag and drop listener on mediaFileList

    mediaFileList.addMouseListener(mediaFileTooltip);
    mediaFileList.addMouseMotionListener(mediaFileTooltip);

    mediaFileList.setCellRenderer(mediaFileListRenderer);

    searchResultList.addListSelectionListener(createSearchResultListListener());
    searchResultList.setCellRenderer(searchResultListRenderer);
    searchResultList.addMouseListener(searchResultTooltip);
    searchResultList.addMouseMotionListener(searchResultTooltip);

    fileChooser.setGenerateThumbnails(true);

    toggleGroup.setLanguage(UIUtils.i18n.getLanguageKey("mediatb.settingspopup.group"));
    toggleGroup.addActionListener(createToggleGroupListener());
    groupFile = setting.isGroupMediaList();
    setToggleGroupIcon();

    mediaFileTb.addToEnd(clearMediaFileListBtn);

    // Add settings button in toolbar
    renameTb.addToEnd(UIUtils.createSettingButton(PopupWay.upLeft, thumbChk, fanartChk, nfoChk, showFormatFieldChk));
    mediaFileTb.addToEnd(button);
    searchTb.addToEnd(UIUtils.createSettingButton(PopupWay.downLeft, showIconResultListChk, showIdResultListChk, showYearResultListChk, showOrigTitleResultListChk));

    // Add context menu on textfield (right click menu)
    searchField.addMouseListener(contextMenuFieldMouseListener);
    renameField.addMouseListener(contextMenuFieldMouseListener);
    fileFormatField.addMouseListener(contextMenuFieldMouseListener);

    // add mouse listener on status bar for task popup
    statusBar.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent e) {
        if (taskPopup.isShowing()) {
          taskPopup.hidePopup();
          return;
        }

        if (!renamerWorkerQueue.isEmpty()) {
          if (!taskPopup.isShowing()) {
            taskPopup.armPopup();
            // Center on taskPanel in statusBar
            for (Component cmp : statusBar.getComponents()) {
              if (cmp instanceof TaskPanel) {
                taskPopup.showPopup(cmp);
                return;
              }
            }
            taskPopup.showPopup(statusBar);
          }
        }
      }
    });

    // Start up animation
    if (setting.isShowStartupAnim()) {
      remove(containerTransition);
      containerTransition.setTransitionEffect(new FadeTransitionEffect());
      final ComponentTransition appearanceTransition = new ComponentTransition(createBackgroundPanel()) {
        @Override
        public Dimension getPreferredSize() {
          return containerTransition.getPreferredSize();
        }
      };

      CurtainTransitionEffect effect = new CurtainTransitionEffect();
      effect.setDirection(Direction.down);
      effect.setType(CurtainType.slide);
      effect.setSpeed(60);
      appearanceTransition.setTransitionEffect(effect);
      appearanceTransition.addAncestorListener(new AncestorAdapter() {
        @Override
        public void ancestorAdded(AncestorEvent event) {
          appearanceTransition.delayTransition(500, containerTransition);
        }
      });

      add(appearanceTransition, BorderLayout.CENTER);
    }

    // Set panel
    setMediaPanel();

    statusBar.setVisible(false);

    // Font
    mediaLbl.setFont(UIUtils.titleFont);
    searchLbl.setFont(UIUtils.titleFont);

    // Add MovieRenamer (Main UI) to UIEvent receiver
    UIEvent.addEventListener(MovieRenamer.class, this);

    if (!files.isEmpty()) {
      loadFiles(files);
    }

    InitTimer initTimer = new InitTimer(this, mediainfoStatusLbl);
    initTimer.start();

    loadingDial.hideDial();
    loadingDial = null;
  }

  /**
   * Create a background panel for startup animation
   *
   * @return WebImage
   */
  private JComponent createBackgroundPanel() {
    WebImage wi = new WebImage(ImageUtils.BAN) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(new LinearGradientPaint(0, 0, 0, getHeight(), new float[]{0f, 0.4f, 0.6f, 1f},
                new Color[]{StyleConstants.bottomBgColor, Color.WHITE, Color.WHITE, StyleConstants.bottomBgColor}));
        g2d.fill(g2d.getClip() != null ? g2d.getClip() : getVisibleRect());

        super.paintComponent(g);
      }
    };
    wi.setDisplayType(DisplayType.fitComponent);
    wi.setHorizontalAlignment(SwingConstants.CENTER);
    wi.setVerticalAlignment(SwingConstants.CENTER);
    return wi;
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
   * @param object
   * @param param
   */
  @Override
  public void UIEventHandler(UIEvent.Event event, IEventInfo info, Object object, Object param) {

    UISettings.LOGGER.fine(String.format("%s receive event %s %s [%s]", getClass().getSimpleName(), event, (info != null ? info : ""), param));

    switch (event) {
      case WORKER_STARTED:// FIXME
        statusLbl.setText(info.getDisplayName());
        statusLbl.setIcon(ImageUtils.LOAD_8);
        statusBar.setVisible(true);
        break;
      case WORKER_RUNNING:// FIXME
        statusLbl.setText(info.getDisplayName());
        statusBar.setVisible(true);
        break;
      case WORKER_ALL_DONE:// FIXME
        statusLbl.setText("");
        statusLbl.setIcon(null);
        statusBar.setVisible(false);
        break;
      case DOWNLOAD_START:
        if (loadingDial == null) {
          loadingDial = new LoadingDialog();
        }
        break;
      case DOWNLOAD_DONE:
        if (loadingDial != null) {
          loadingDial.dispose();
        }
        loadingDial = null;
        break;
      case RENAME_FILE:
        if (info instanceof RenamerWorker) {
          synchronized (renamerWorkerQueue) {
            renamerWorkerQueue.add((RenamerWorker) info);

            boolean hasRenamerWorker = false;
            for (Component cmp : statusBar.getComponents()) {
              if (cmp instanceof TaskPanel) {
                hasRenamerWorker = true;
                if (renamerWorkerQueue.size() > 0) {
                  ((TaskPanel) cmp).setStatus(renamerWorkerQueue.size() + " More");// FIXME i18n
                }
                break;
              }
            }

            taskPopup.update();

            if (!hasRenamerWorker) {
              RenamerWorker rworker = renamerWorkerQueue.poll();
              TaskPanel tpanel = rworker.getTaskPanel();
              if (renamerWorkerQueue.size() > 0) {
                tpanel.setStatus(renamerWorkerQueue.size() + " More");// FIXME i18n
              }
              statusBar.addToEnd(tpanel);
              statusBar.revalidate();
              if (setting.isMoveFileOneByOne()) {
                rworker.execute();
              }
            }
          }
        }
        break;
      case RENAME_FILE_DONE:
        if (info instanceof RenamerWorker) {
          synchronized (renamerWorkerQueue) {
            renamerWorkerQueue.remove((RenamerWorker) info);
            TaskPanel tpanel = ((RenamerWorker) info).getTaskPanel();
            if (statusBar.isAncestorOf(tpanel)) {
              statusBar.remove(tpanel);
              statusBar.revalidate();
            }

            if (!renamerWorkerQueue.isEmpty()) {
              RenamerWorker rworker = renamerWorkerQueue.poll();
              if (setting.isMoveFileOneByOne()) {
                rworker.execute();
              }

              tpanel = rworker.getTaskPanel();
              if (renamerWorkerQueue.size() > 0) {
                tpanel.setStatus(renamerWorkerQueue.size() + " More");// FIXME i18n
              }
              statusBar.addToEnd(tpanel);
              statusBar.revalidate();
            }
          }

          taskPopup.update();
        }
        break;
      case SETTINGS:// TODO
        if (param instanceof Settings.IProperty) {
          if (param instanceof Settings.SettingsProperty) {
            Settings.SettingsProperty sproperty = (Settings.SettingsProperty) param;
            System.out.println(sproperty.name() + " changed to " + sproperty.getValue());
            switch (sproperty) {
              case movieFilenameCase:
              case movieFilenameFormat:
              case movieFilenameRmDupSpace:
              case movieFilenameLimit:
              case movieFilenameSeparator:
              case movieFilenameTrim:
                UIMode.MOVIEMODE.setFileformat(setting.coreInstance.getMovieFilenameFormat());
                updateRenamedTitle();
                break;
              case searchMovieScrapper:
                //case searchTvshowScrapper:
                break;
              case appLanguage:
                LanguageManager.setLanguage(sproperty.getValue());
                LanguageManager.updateAllComponents();
                break;
            }
          }

          if (param instanceof UISettings.UISettingsProperty) {
            boolean updatePanel = false;
            UISettings.UISettingsProperty uisproperty = (UISettings.UISettingsProperty) param;
            System.out.println(uisproperty.name() + " changed to " + uisproperty.getValue());// FIXME remove
            switch (uisproperty) {
              case showMediaPanel:
                showMediaPanelChk.setSelected(Boolean.parseBoolean(uisproperty.getValue()));
                updatePanel = true;
                break;
              case showImagePanel:
                showImagePanelChk.setSelected(Boolean.parseBoolean(uisproperty.getValue()));
                updatePanel = true;
                break;
              case screenDevice:
                UIUtils.showOnScreen(this);
                break;
            }

            if (updatePanel) {
              setMediaPanel();
            }
          }
        }
        break;

      case UPDATE_AVAILABLE:// FIXME i18n
        UIUpdate update = (UIUpdate) param;
        int n = WebOptionPane.showConfirmDialog(MovieRenamer.this,
                "An update is available.\nDo you want to update Mr to " + update.getUpdateVersion() + "\n\n" + update.getDescen(),
                UIUtils.i18n.getLanguage("dialog.question", false), WebOptionPane.YES_NO_OPTION, WebOptionPane.QUESTION_MESSAGE
        );

        if (n > 0) {
          return;
        }

        String version = UISettings.getApplicationVersionNumber();
        String updateDir = Settings.appFolder + File.separator + "update";
        String installDir = "";
        try {
          installDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath();
        } catch (URISyntaxException ex) {
          // TODO
          UISettings.LOGGER.log(Level.SEVERE, null, ex);
        }

        // TODO check if "installDir" is writable
        try {

          String javaBin = System.getProperty("java.home") + "/bin/java";
          File jarFile = new File(installDir + File.separator + "lib" + File.separator + "Mr-updater.jar");
          String toExec[];

          if (setting.coreInstance.isProxyIsOn()) {
            toExec = new String[]{javaBin, "-Dhttp.proxyHost=" + setting.coreInstance.getProxyUrl(),
              "-Dhttp.proxyPort=" + setting.coreInstance.getProxyPort(), "-jar", jarFile.getPath(), version, installDir, updateDir};
          } else {
            toExec = new String[]{javaBin, "-jar", jarFile.getPath(), version, installDir, updateDir};
          }

          Process p = Runtime.getRuntime().exec(toExec);
          dispose();
          System.exit(0);
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Restart failed :(", "error", JOptionPane.ERROR_MESSAGE);// FIXME i18n
        }
        break;
      case NO_UPDATE:
        // TODO
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
        if (!lse.getValueIsAdjusting()) {
          UIFile mediaFile = null;

          try {
            mediaFile = getSelectedMediaFile();
          } catch (ClassCastException ex) {// Spinningdial (user cancel)
            clearInterface(CLEAR_MEDIALIST, CLEAR_SEARCHRESULTLIST);
          }

          if (mediaFile == null) {
            UISettings.LOGGER.log(Level.SEVERE, "Media file is null for : {0}", lse.toString());
            return;
          }

          currentMedia = mediaFile;
          searchField.setText(currentMedia.getSearch());

          searchMedia();
        }
      }
    };
  }

  private ListSelectionListener createSearchResultListListener() {
    return new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent lse) {
        if (!lse.getValueIsAdjusting()) {
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
      public void getFiles(List<File> files) {
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
  private void loadFiles(List<File> files) {
    clearMediaFileListBtn.setEnabled(false);
    clearInterface(CLEAR_MEDIALIST, CLEAR_SEARCHRESULTLIST);
    mediaFileList.setModel(loaderModel);
    WorkerManager.listFiles(this, files, mediaFileEventList);
  }

  /*
   * Load media panel with fade effect
   */
  @SuppressWarnings("unchecked")
  private void loadMediaPanel() {
    movieModeBtn.setEnabled(false);
    tvShowModeBtn.setEnabled(false);

    switch (currentMode) {
      case MOVIEMODE:
        tvShowModeBtn.setEnabled(true);
        break;
      case TVSHOWMODE:
        movieModeBtn.setEnabled(true);
        break;
    }

    scrapperCb.setModel(currentMode.getScraperModel());
    fileFormatField.setText(currentMode.getFileFormat());
    containerTransitionMediaPanel.performTransition(mediaPanels.get(currentMode));
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
  private void searchMedia() {

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

    WorkerManager.search(this, currentMedia);
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

    WorkerManager.searchInfo(this, searchResult);
    if (showMediaPanelChk.isSelected()) {
      WorkerManager.searchImage(this, searchResult);
    }
  }

  /**
   * Clear Movie Renamer interface
   *
   * @param mediaList Clear media list
   * @param searchList Clear search list
   */
  private void clearInterface(boolean clearMediaList, boolean clearSearchResultList, Class... clazz) {

    if (clearMediaList) {
      searchField.setText(null);
      mediaFileEventList.clear();
      mediaFileSeparator.clear();
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
    WorkerManager.stop();
    System.gc();
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
  public final EventListModel<UIFile> getMediaFileListModel() {
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

  public final UIScraper getScraper() {
    return currentMode.getSelectedScraper();
  }

  public final UIFile getFile() {
    return currentMedia;
  }

  public final MediaPanel<? extends MediaInfo> getMediaPanel() {
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
    return getSelectedElement(mediaFileList);
  }

  /**
   * Get selected search result
   *
   * @return UISearchResult selected or null
   */
  private UISearchResult getSelectedSearchResult() {
    return getSelectedElement(searchResultList);
  }

  /**
   * Get selected object (IIconList) in list
   *
   * @param list List
   * @return Selected object (IIconList) or null
   */
  @SuppressWarnings("unchecked")
  private <T extends IIconList> T getSelectedElement(WebList list) {
    T current = null;
    if (list != null) {
      Object obj = list.getSelectedValue();
      if (obj != null) {
        if (obj instanceof IIconList) {
          current = (T) obj;
          list.ensureIndexIsVisible(list.getSelectedIndex());
        }
      }
    }
    return current;
  }

  public Queue<RenamerWorker> getTaskWorker() {
    return renamerWorkerQueue;// FIXME unmodifiable
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

  public void updateRenamedTitle() {
    MediaInfo mediaInfo = getMediaPanel().getInfo();
    renameField.setText(null);

    if (mediaInfo != null) {
      renameField.setText(mediaInfo.getRenamedTitle(fileFormatField.getText()));
    }
  }

  public void setRenameFieldEnabled() {
    renameField.setEnabled(true);
  }

  public synchronized void setRenamebuttonEnabled() {
    // We need to wait for searchMediaWorker and searchimageWorker
    // FIXME if user disable image
    MediaInfo mediaInfo = getMediaPanel().getInfo();
    if (mediaInfo != null && images != null) {
      renameBtn.setEnabled(true);
    }
  }

  public void setImageInfo(List<ImageInfo> images) {
    this.images = images;
  }

  private void setMediaPanel() {
    mediaFileListsettingBtn.hidePopup();
    if (showMediaPanelChk.isSelected()) {
      searchResultTooltip.setTooltipWay(TooltipWay.right);
      mediaFileTooltip.setTooltipWay(TooltipWay.right);
      UISearchResult searchResult = getSelectedSearchResult();
      if (searchResult != null) {// FIXME check if searchImage was launched before (not important, the cache will be used)
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
    renameField = new JTextField();
    thumbChk = new WebCheckBox();
    fanartChk = new WebCheckBox();
    nfoChk = new WebCheckBox();
    toggleGroup = new WebButton();
    fileFormatField = new WebTextField();
    aboutBtn = UIUtils.createButton(i18n.getLanguageKey("toptb.about"), ImageUtils.INFO_24, ImageUtils.INFO_16);
    clearMediaFileListBtn = UIUtils.createButton(i18n.getLanguageKey("mediatb.clear"), ImageUtils.CLEAR_LIST_16, ImageUtils.CLEAR_LIST_16);
    containerTransition = new ComponentTransition();
    mainTb = new WebToolBar();
    openBtn = UIUtils.createButton(i18n.getLanguageKey("toptb.open"), ImageUtils.FOLDERVIDEO_24, ImageUtils.FOLDERVIDEO_16, Hotkey.CTRL_O, MovieRenamer.this);
    openSep = new Separator();
    movieModeBtn = UIUtils.createButton(i18n.getLanguageKey("toptb.movieMode"), ImageUtils.MOVIE_24, ImageUtils.MOVIE_16, Hotkey.CTRL_F, MovieRenamer.this);
    tvShowModeBtn = UIUtils.createButton(i18n.getLanguageKey("toptb.tvshowMode"), ImageUtils.TV_24, ImageUtils.TV_16, Hotkey.CTRL_T, MovieRenamer.this);
    modeSep = new Separator();
    mainContainerTransition = new ComponentTransition();
    mainPanel = new WebPanel();
    listSp = new WebSplitPane();
    mediaFilePnl = new WebPanel();
    mediaFileTb = new WebToolBar();
    mediaLbl = new WebLabel();
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
    scrapperCb = new WebComboBox();
    renamePnl = new WebPanel();
    renameTb = new WebToolBar();
    renameBtn = new WebButton();
    statusBar = new WebStatusBar();
    mediainfoStatusLbl = new WebLabel();
    statusLbl = new WebLabel();

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

    renameField.setEnabled(false);

    thumbChk.setLanguage(i18n.getLanguageKey("renametb.settingspopup.thumb"));

    fanartChk.setLanguage(i18n.getLanguageKey("renametb.settingspopup.fanart"));

    nfoChk.setLanguage(i18n.getLanguageKey("renametb.settingspopup.nfo"));// FIXME XBMC, ...

    toggleGroup.setIcon(ImageUtils.FILEVIEW_16);

    fileFormatField.setPreferredSize(new Dimension(250, 27));
    fileFormatField.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent evt) {
        fileFormatFieldKeyReleased(evt);
      }
    });

    aboutBtn.setIcon(ImageUtils.INFO_24);
    aboutBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        aboutBtnActionPerformed(evt);
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

    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setMinimumSize(new Dimension(800, 600));

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
    mainTb.add(openSep);

    movieModeBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        movieModeBtnActionPerformed(evt);
      }
    });
    mainTb.add(movieModeBtn);

    tvShowModeBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        tvShowModeBtnActionPerformed(evt);
      }
    });
    mainTb.add(tvShowModeBtn);
    mainTb.add(modeSep);

    containerTransition.add(mainTb, BorderLayout.PAGE_START);

    mediaFilePnl.setMargin(new Insets(4, 5, 4, 5));

    mediaFileTb.setFloatable(false);
    mediaFileTb.setMargin(new Insets(0, 4, 0, 4));
    mediaFileTb.setRound(5);

    mediaLbl.setLanguage(i18n.getLanguageKey("mediatb.media"));
    mediaLbl.setIcon(ImageUtils.MEDIA_16);
    mediaFileTb.add(mediaLbl);

    mediaFileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    mediaFileScp.setViewportView(mediaFileList);

    GroupLayout mediaFilePnlLayout = new GroupLayout(mediaFilePnl);
    mediaFilePnl.setLayout(mediaFilePnlLayout);
    mediaFilePnlLayout.setHorizontalGroup(
      mediaFilePnlLayout.createParallelGroup(Alignment.LEADING)
      .addComponent(mediaFileTb, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(mediaFileScp, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
    );
    mediaFilePnlLayout.setVerticalGroup(
      mediaFilePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(mediaFilePnlLayout.createSequentialGroup()
        .addComponent(mediaFileTb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(mediaFileScp, GroupLayout.DEFAULT_SIZE, 726, Short.MAX_VALUE))
    );

    listSp.setLeftComponent(mediaFilePnl);

    centerPanel.setPreferredSize(new Dimension(600, 400));

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

    scrapperCb.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
    scrapperCb.setDrawFocus(false);

    GroupLayout searchPnlLayout = new GroupLayout(searchPnl);
    searchPnl.setLayout(searchPnlLayout);
    searchPnlLayout.setHorizontalGroup(
      searchPnlLayout.createParallelGroup(Alignment.LEADING)
      .addComponent(searchTb, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addGroup(searchPnlLayout.createSequentialGroup()
        .addComponent(searchField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(scrapperCb, GroupLayout.PREFERRED_SIZE, 173, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(searchBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
      .addComponent(searchResultListSp, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
    );
    searchPnlLayout.setVerticalGroup(
      searchPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(searchPnlLayout.createSequentialGroup()
        .addComponent(searchTb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addGroup(searchPnlLayout.createParallelGroup(Alignment.LEADING)
          .addComponent(searchField, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(searchBtn, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(scrapperCb, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
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

    renameBtn.setLanguage(i18n.getLanguageKey("renametb.rename"));

    renameBtn.setIcon(ImageUtils.OK_16);
    renameBtn.setEnabled(false);
    renameBtn.setFocusable(false);
    renameBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        renameBtnActionPerformed(evt);
      }
    });
    renameTb.add(renameBtn);
    renameTb.add(fileFormatField);
    //Add rename text field
    renameTb.add(renameField, ToolbarLayout.FILL);

    renamePnl.add(renameTb, BorderLayout.NORTH);

    statusBar.setMargin(new Insets(2, 10, 2, 10));
    statusBar.add(mediainfoStatusLbl);
    statusBar.add(statusLbl);

    renamePnl.add(statusBar, BorderLayout.SOUTH);

    getContentPane().add(renamePnl, BorderLayout.SOUTH);

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void openBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_openBtnActionPerformed
    fileChooser.setCurrentDirectory(new File(setting.getFileChooserPath()));
    int r = fileChooser.showOpenDialog(this);
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

  private void movieModeBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_movieModeBtnActionPerformed
    currentMode = UIMode.MOVIEMODE;
    loadMediaPanel();
  }//GEN-LAST:event_movieModeBtnActionPerformed

  private void tvShowModeBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_tvShowModeBtnActionPerformed
    WebOptionPane.showMessageDialog(MovieRenamer.this, "Tv show is not available for the moment but will be added soon.", "Unavailable", JOptionPane.INFORMATION_MESSAGE);// FIXME i18n ou pas
//    currentMode = UIMode.TVSHOWMODE;
//    loadMediaPanel();
  }//GEN-LAST:event_tvShowModeBtnActionPerformed

  private void exitBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_exitBtnActionPerformed
    boolean runningProcess = false;
    for (Component cmp : statusBar.getComponents()) {
      if (cmp instanceof TaskPanel) {
        runningProcess = true;
        break;
      }
    }

    if (!runningProcess) {
      runningProcess = !renamerWorkerQueue.isEmpty();
    }

    if (runningProcess) {
      int n = WebOptionPane.showConfirmDialog(MovieRenamer.this, UIUtils.i18n.getLanguage("dialog.wantrestart", false, Settings.APPNAME),
              UIUtils.i18n.getLanguage("dialog.question", false), WebOptionPane.YES_NO_OPTION, WebOptionPane.QUESTION_MESSAGE);
      if (n > 0) {
        return;
      }
      // TODO stop process + clean
    }

    System.exit(0);
  }//GEN-LAST:event_exitBtnActionPerformed

  private void settingBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_settingBtnActionPerformed
    UIManager.showSettingsDialog();
  }//GEN-LAST:event_settingBtnActionPerformed

  private void updateBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_updateBtnActionPerformed
    CheckUpdateWorker updateWorker = new CheckUpdateWorker(MovieRenamer.this, true);
    updateWorker.execute();
  }//GEN-LAST:event_updateBtnActionPerformed

  private void renameBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_renameBtnActionPerformed
    WorkerManager.rename(this, currentMedia.getFile(), renameField.getText());
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

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private WebButton aboutBtn;
  private WebPanel centerPanel;
  private WebButton clearMediaFileListBtn;
  private ComponentTransition containerTransition;
  private WebButton exitBtn;
  private WebCheckBox fanartChk;
  private WebTextField fileFormatField;
  private WebSplitPane listSp;
  private ComponentTransition mainContainerTransition;
  private WebPanel mainPanel;
  private WebToolBar mainTb;
  private WebList mediaFileList;
  private WebPanel mediaFilePnl;
  private JScrollPane mediaFileScp;
  private WebToolBar mediaFileTb;
  private WebLabel mediaLbl;
  private WebSplitPane mediaSp;
  private WebLabel mediainfoStatusLbl;
  private Separator modeSep;
  private WebButton movieModeBtn;
  private WebCheckBox nfoChk;
  private WebButton openBtn;
  private Separator openSep;
  private WebButton renameBtn;
  private JTextField renameField;
  private WebPanel renamePnl;
  private WebToolBar renameTb;
  private WebComboBox scrapperCb;
  private WebButton searchBtn;
  private WebTextField searchField;
  private WebLabel searchLbl;
  private WebPanel searchPnl;
  private WebList searchResultList;
  private JScrollPane searchResultListSp;
  private WebToolBar searchTb;
  private WebButton settingBtn;
  private WebStatusBar statusBar;
  private WebLabel statusLbl;
  private WebCheckBox thumbChk;
  private WebButton toggleGroup;
  private WebButton tvShowModeBtn;
  private WebButton updateBtn;
  // End of variables declaration//GEN-END:variables
}
