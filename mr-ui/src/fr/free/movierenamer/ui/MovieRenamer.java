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
package fr.free.movierenamer.ui;

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
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;
import com.alee.utils.swing.AncestorAdapter;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.scrapper.ScrapperManager;
import fr.free.movierenamer.scrapper.TvShowScrapper;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.ui.bean.IEventInfo;
import fr.free.movierenamer.ui.bean.IEventListener;
import fr.free.movierenamer.ui.bean.IIconList;
import fr.free.movierenamer.ui.bean.UIEvent;
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.ui.bean.UILoader;
import fr.free.movierenamer.ui.bean.UIMode;
import fr.free.movierenamer.ui.bean.UIScrapper;
import fr.free.movierenamer.ui.bean.UISearchResult;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.settings.UISettings.UISettingsProperty;
import fr.free.movierenamer.ui.swing.DragAndDrop;
import fr.free.movierenamer.ui.swing.ImageListModel;
import fr.free.movierenamer.ui.swing.ListTooltip;
import fr.free.movierenamer.ui.swing.MediaListRenderer;
import fr.free.movierenamer.ui.swing.SearchResultListRenderer;
import fr.free.movierenamer.ui.swing.TaskPopup;
import fr.free.movierenamer.ui.swing.panel.ImagePanel;
import fr.free.movierenamer.ui.swing.panel.Loading;
import fr.free.movierenamer.ui.swing.panel.LogPanel;
import fr.free.movierenamer.ui.swing.panel.TaskPanel;
import fr.free.movierenamer.ui.swing.panel.generator.SettingPanel;
import fr.free.movierenamer.ui.swing.panel.generator.info.MediaPanel;
import fr.free.movierenamer.ui.swing.panel.generator.info.MoviePanel;
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
  private String movieFileFormat = setting.coreInstance.getMovieFilenameFormat();
  private String tvshowFileFormat = setting.coreInstance.getTvShowFilenameFormat();
  // Scrapper
  private UIScrapper currentScrapper;
  // Media Panel
  private MediaPanel<? extends MediaInfo> mediaPanel;
  private final MoviePanel moviePnl;
//  private final TvShowPanel tvShowPanel;
  private final ComponentTransition containerTransitionMediaPanel;// Media Panel container
  // Log Panel
  private final LogPanel logPanel = new LogPanel();
  //Settings panel
  private final SettingPanel settingsDialog;
  // File chooser
  private final WebFileChooser fileChooser = new WebFileChooser();
  // Clear interface
  private final boolean CLEAR_MEDIALIST = true;
  private final boolean CLEAR_SEARCHRESULTLIST = true;
  // Model
  private EventList<UIFile> mediaFileEventList = new BasicEventList<UIFile>();
  private final ImageListModel<UISearchResult> searchResultModel = new ImageListModel<UISearchResult>();
  private final DefaultComboBoxModel<UIScrapper> movieScrapperModel = new DefaultComboBoxModel<UIScrapper>();
  private final DefaultComboBoxModel<UIScrapper> tvshowScrapperModel = new DefaultComboBoxModel<UIScrapper>();
  private final EventListModel<UIFile> mediaFileSeparatorModel;
  private final EventListModel<UIFile> mediaFileModel = new EventListModel<UIFile>(mediaFileEventList);
  private final MediaListRenderer mediaFileListRenderer = new MediaListRenderer();
  private final SearchResultListRenderer searchResultListRenderer = new SearchResultListRenderer();
  //
  private final DefaultListModel<UILoader> loaderModel = new DefaultListModel<UILoader>();
  // Separator
  private final SeparatorList<UIFile> mediaFileSeparator = new SeparatorList<UIFile>(mediaFileEventList, UIUtils.groupFileComparator, 1, 1000);
  // List option checkbox
  private final WebCheckBox showIconMediaListChk;
  private final WebCheckBox showIconResultListChk;
  private final WebCheckBox showIdResultListChk;
  private final WebCheckBox showYearResultListChk;
  private final WebCheckBox showOrigTitleResultListChk;
  private final WebCheckBox showFormatFieldChk;
  // UI tools
  public static final Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
  public static final Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
  // Renamer worker queue
  private final Queue<RenamerWorker> renamerWorkerQueue = new LinkedList<RenamerWorker>();
  // Task popup
  private final TaskPopup taskPopup = new TaskPopup(this);
  //
  private final ListSelectionListener mediaFileListListener = createMediaFileListListener();
  private final ImagePanel imgPnl;

  public MovieRenamer(List<File> files) {
    super();

    Loading loading = new Loading();

    //Cache.clearAllCache();//FIXME remove !!!


    mediaFileSeparatorModel = new EventListModel<UIFile>(mediaFileSeparator);

    // Set Movie Renamer mode
    currentMode = UIMode.MOVIEMODE;

    initComponents();

    // Panels
    moviePnl = new MoviePanel();
    settingsDialog = new SettingPanel(this);

    imgPnl = new ImagePanel(this);
    JScrollPane imgPanelScrollPane = new JScrollPane(imgPnl);
    imgPanelScrollPane.setBorder(null);
    centerPanel.add(imgPanelScrollPane, BorderLayout.EAST);

    // Create media panel container and set transition effect
    containerTransitionMediaPanel = new ComponentTransition(moviePnl);
    containerTransitionMediaPanel.setTransitionEffect(new FadeTransitionEffect());

    // Setting popup options
    showIconMediaListChk = UIUtils.createShowIconChk(mediaFileList, setting.isShowIconMediaList(), UIUtils.i18n.getLanguageKey("showIcon", "popupmenu"));
    showIconResultListChk = UIUtils.createShowIconChk(searchResultList, setting.isShowThumb(), UIUtils.i18n.getLanguageKey("showIcon", "popupmenu"));

    showIdResultListChk = UIUtils.createShowChk(searchResultList, SearchResultListRenderer.Property.showId, setting.isShowId(), UIUtils.i18n.getLanguageKey("showId", "popupmenu"));
    showYearResultListChk = UIUtils.createShowChk(searchResultList, SearchResultListRenderer.Property.showYear, setting.isShowYear(), UIUtils.i18n.getLanguageKey("showYear", "popupmenu"));
    showOrigTitleResultListChk = UIUtils.createShowChk(searchResultList, SearchResultListRenderer.Property.showOrigTitle, setting.isShowOrigTitle(), UIUtils.i18n.getLanguageKey("showOrigTitle", "popupmenu"));

    showFormatFieldChk = new WebCheckBox(UIUtils.i18n.getLanguageKey("showFormatField", "popupmenu"));
    showFormatFieldChk.setSelected(setting.isShowFormatField());
    showFormatFieldChk.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        fileFormatField.setVisible(showFormatFieldChk.isSelected());
      }
    });

    init();

    setIconImage(ImageUtils.iconToImage(ImageUtils.LOGO_22));
    setLocationRelativeTo(null);
    setLanguage(UIUtils.i18n.getLanguageKey("title"), UISettings.APPNAME, UISettings.VERSION);

    if (!files.isEmpty()) {
      loadFiles(files);
    }

    loading.setVisible(false);

    // Check for Movie Renamer update
    Timer updateTimer = new Timer(3000, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // TODO check update
      }
    });
    updateTimer.setRepeats(false);
    updateTimer.start();
  }

  private void init() {

    // List loading model
    loaderModel.addElement(new UILoader());

    // Add button to main toolbar
    mainTb.addToEnd(logsBtn);
    mainTb.addToEnd(new JSeparator(JSeparator.VERTICAL));
    mainTb.addToEnd(updateBtn);
    mainTb.addToEnd(settingBtn);
    mainTb.addToEnd(exitBtn);

    // Add media panel container to media split pane
    mediaSp.setBottomComponent(containerTransitionMediaPanel);

    // Init Movie Scrapper model
    for (MovieScrapper scrapper : ScrapperManager.getMovieScrapperList()) {
      movieScrapperModel.addElement(new UIScrapper(scrapper));
    }

    // Init TvShow Scrapper model
    for (TvShowScrapper scrapper : ScrapperManager.getTvShowScrapperList()) {
      tvshowScrapperModel.addElement(new UIScrapper(scrapper));
    }

    scrapperCb.setRenderer(UIUtils.iconListRenderer);
    setSelectedScrapper();

    loadMediaPanel();

    // List tooltip listener
    ListTooltip mediaFileTooltip = new ListTooltip();
    ListTooltip searchResultTooltip = new ListTooltip(1200, true);

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
    mediaFileTb.addToEnd(UIUtils.createSettingButton(PopupWay.downRight, toggleGroup, showIconMediaListChk));
    searchTb.addToEnd(UIUtils.createSettingButton(PopupWay.downLeft, showIconResultListChk, showIdResultListChk, showYearResultListChk, showOrigTitleResultListChk));

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

    // Add MovieRenamer (Main UI) to UIEvent receiver
    UIEvent.addEventListener(MovieRenamer.class, this);
  }

  /**
   * Create a background panel for animation
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
   * @param param
   */
  @Override
  public void UIEventHandler(UIEvent.Event event, IEventInfo info, Object param) {

    UISettings.LOGGER.info(String.format("%s receive event %s %s [%s]", getClass().getSimpleName(), event, (info != null ? info : ""), param));

    switch (event) {
      case WORKER_STARTED:
        statusLbl.setText(info.getDisplayName());
        statusLbl.setIcon(ImageUtils.LOAD_8);
        break;
      case WORKER_RUNNING:
        statusLbl.setText(info.getDisplayName());
        break;
      case WORKER_ALL_DONE:
        statusLbl.setText("");
        statusLbl.setIcon(null);
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
                  ((TaskPanel) cmp).setStatus(renamerWorkerQueue.size() + " More");
                }
                break;
              }
            }

            taskPopup.update();

            if (!hasRenamerWorker) {
              RenamerWorker rworker = renamerWorkerQueue.poll();
              TaskPanel tpanel = rworker.getTaskPanel();
              if (renamerWorkerQueue.size() > 0) {
                tpanel.setStatus(renamerWorkerQueue.size() + " More");
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
                tpanel.setStatus(renamerWorkerQueue.size() + " More");
              }
              statusBar.addToEnd(tpanel);
              statusBar.revalidate();
            }
          }

          taskPopup.update();
        }
        break;
      case SETTINGS:
        if (param instanceof Settings.IProperty) {
          if (param instanceof Settings.SettingsProperty) {
            Settings.SettingsProperty sproperty = (Settings.SettingsProperty) param;
            System.out.println(sproperty.name() + " changed to " + sproperty.getValue());
            switch (sproperty) {
              case movieFilenameCase:
              case movieFilenameCreateDirectory:
              case movieFilenameFormat:
              case movieFilenameRmDupSpace:
              case movieFilenameLimit:
              case movieFilenameSeparator:
              case movieFilenameTrim:
                movieFileFormat = setting.coreInstance.getMovieFilenameFormat();
                if (currentMode.equals(UIMode.MOVIEMODE)) {
                  fileFormatField.setText(movieFileFormat);
                }
                updateRenamedTitle();
                break;
              case searchMovieScrapper:
              case searchTvshowScrapper:
                setSelectedScrapper();
                break;
              case appLanguage:
                LanguageManager.setLanguage(sproperty.getValue());
                LanguageManager.updateAllComponents();
                break;
            }
          }

          if (param instanceof UISettings.UISettingsProperty) {
            UISettings.UISettingsProperty uisproperty = (UISettings.UISettingsProperty) param;
            System.out.println(uisproperty.name() + " changed to " + uisproperty.getValue());
            switch (uisproperty) {

            }

          }
        }
        break;
    }
  }

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
            return;
          }

          currentMedia = mediaFile;
          searchField.setText(currentMedia.getSearch());
          mediaPanel.clearfileInfoPanel();

          searchMedia();
          WorkerManager.getFileInfo(MovieRenamer.this, currentMedia);// We start get file info after otherwise "searchmedia" will stop it
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
  private void loadFiles(List<File> files) {
    clearMediaFileListBtn.setEnabled(false);
    clearInterface(CLEAR_MEDIALIST, CLEAR_SEARCHRESULTLIST);
    mediaFileList.setModel(loaderModel);
    WorkerManager.listFiles(this, files, mediaFileEventList);
  }

  /*
   * Load media panel with fade effect
   */
  private void loadMediaPanel() {
    movieModeBtn.setEnabled(false);
    tvShowModeBtn.setEnabled(false);
    switch (currentMode) {
      case MOVIEMODE:
        mediaPanel = moviePnl;
        containerTransitionMediaPanel.performTransition(moviePnl);
        tvShowModeBtn.setEnabled(true);
        scrapperCb.setModel(movieScrapperModel);
        fileFormatField.setText(movieFileFormat);
        break;
      case TVSHOWMODE:
//        containerTransitionMediaPanel.performTransition(tvShowPanel);
//        movieModeBtn.setEnabled(true);
//        scrapperCb.setModel(tvshowScrapperModel);
//        fileFormatField.setText(tvshowFileFormat);
        break;
    }

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
  private void searchMedia() {

    if (currentMedia == null) {
      return;
    }

    String search = searchField.getText();
    if (search.length() == 0) {
      TooltipManager.showOneTimeTooltip(searchField, null, "<html><center>One-time <font color=red>HTML</font> tooltip<br>"
              + "<font size=2>just click anywhere to close it</font></center></html>", TooltipWay.down);// FIXME i18n
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

    imgPnl.clearPanel();
    mediaPanel.clear();
    updateRenamedTitle();

    // Stop all running workers except search result list image worker (thumbnail in result list)
    WorkerManager.stopExcept(ImageWorker.class, UISearchResult.class);

    WorkerManager.searchInfo(this, searchResult);
    WorkerManager.searchImage(this, searchResult);
  }

  /**
   * Clear Movie Renamer interface
   *
   * @param mediaList Clear media list
   * @param searchList Clear search list
   */
  private void clearInterface(boolean clearMediaList, boolean clearSearchResultList) {

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

    imgPnl.clearPanel();
    mediaPanel.clear();
    updateRenamedTitle();
    renameBtn.setEnabled(false);
    renameField.setEnabled(false);

    // Stop all running workers
    WorkerManager.stop();
  }

  /**
   *******************************
   *
   * GETTER
   *
   ********************************
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

  public final UIScrapper getScrapper() {
    return currentScrapper;
  }

  public final UIFile getFile() {
    return currentMedia;
  }

  public final ImagePanel getImagePanel() {
    return imgPnl;
  }

  public final MediaPanel<? extends MediaInfo> getMediaPanel() {
    return mediaPanel;
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
  private void setSelectedScrapper() {
    movieScrapperModel.setSelectedItem(new UIScrapper(ScrapperManager.getMovieScrapper()));
    tvshowScrapperModel.setSelectedItem(new UIScrapper(ScrapperManager.getTvShowScrapper()));
    currentScrapper = (UIScrapper) (currentMode.equals(UIMode.MOVIEMODE) ? movieScrapperModel.getSelectedItem() : tvshowScrapperModel.getSelectedItem());
  }

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

  public void updateRenamedTitle() {// TODO
    MediaInfo mediaInfo = getMediaPanel().getInfo();

    if (mediaInfo != null) {
      renameField.setText(mediaInfo.getRenamedTitle(fileFormatField.getText()));
    } else {
      renameField.setText(null);
    }
  }

  public void setRenameEnabled() {
    renameBtn.setEnabled(true);
    renameField.setEnabled(true);
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    updateBtn = UIUtils.createButton(UIUtils.i18n.getLanguageKey("toptb.update"), ImageUtils.UPDATE_24, ImageUtils.UPDATE_16);
    settingBtn = UIUtils.createButton(UIUtils.i18n.getLanguageKey("toptb.settings"), ImageUtils.SETTING_24, ImageUtils.SETTING_16, MovieRenamer.this, Hotkey.CTRL_S);
    exitBtn = UIUtils.createButton(UIUtils.i18n.getLanguageKey("toptb.quit"), ImageUtils.APPLICATIONEXIT_24, ImageUtils.APPLICATIONEXIT_16, MovieRenamer.this, Hotkey.CTRL_Q);
    renameField = new JTextField();
    thumbChk = new WebCheckBox();
    fanartChk = new WebCheckBox();
    nfoChk = new WebCheckBox();
    toggleGroup = new WebButton();
    fileFormatField = new WebTextField();
    logsBtn = UIUtils.createButton(UIUtils.i18n.getLanguageKey("toptb.logs"), ImageUtils.INFO_24, ImageUtils.INFO_16);
    clearMediaFileListBtn = UIUtils.createButton(UIUtils.i18n.getLanguageKey("mediatb.clear"), ImageUtils.CLEAR_LIST_16);
    containerTransition = new ComponentTransition();
    mainTb = new WebToolBar();
    openBtn = UIUtils.createButton("toptb.open", ImageUtils.FOLDERVIDEO_24, ImageUtils.FOLDERVIDEO_16, MovieRenamer.this, Hotkey.CTRL_O);
    openSep = new Separator();
    movieModeBtn = UIUtils.createButton("toptb.movieMode", ImageUtils.MOVIE_24, ImageUtils.MOVIE_16, MovieRenamer.this, Hotkey.CTRL_F);
    tvShowModeBtn = UIUtils.createButton("toptb.tvshowMode", ImageUtils.TV_24, ImageUtils.TV_16, MovieRenamer.this, Hotkey.CTRL_T);
    modeSep = new Separator();
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
    searchBtn = new WebButton();
    searchField = new WebTextField();
    searchResultListSp = new JScrollPane();
    searchResultList = new WebList();
    scrapperCb = new WebComboBox();
    webPanel1 = new WebPanel();
    renameTb = new WebToolBar();
    renameBtn = new WebButton();
    statusBar = new WebStatusBar();
    webLabel1 = new WebLabel();
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

    thumbChk.setLanguage(UIUtils.i18n.getLanguageKey("renametb.settingspopup.thumb"));

    fanartChk.setLanguage(UIUtils.i18n.getLanguageKey("renametb.settingspopup.fanart"));

    nfoChk.setLanguage(UIUtils.i18n.getLanguageKey("renametb.settingspopup.nfo"));// FIXME XBMC, ...

    toggleGroup.setIcon(ImageUtils.FILEVIEW_16);

    fileFormatField.setPreferredSize(new Dimension(250, 27));
    fileFormatField.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent evt) {
        fileFormatFieldKeyReleased(evt);
      }
    });

    logsBtn.setIcon(ImageUtils.INFO_24);
    logsBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        logsBtnActionPerformed(evt);
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
    mainTb.setMargin(new Insets(4, 4, 4, 4));
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

    mediaFilePnl.setMargin(new Insets(10, 10, 10, 10));
    mediaFilePnl.setMinimumSize(new Dimension(160, 50));
    mediaFilePnl.setPreferredSize(new Dimension(160, 200));

    mediaFileTb.setFloatable(false);
    mediaFileTb.setMargin(new Insets(0, 5, 0, 5));
    mediaFileTb.setRound(5);

    mediaLbl.setLanguage(UIUtils.i18n.getLanguageKey("mediatb.media"));
    mediaLbl.setIcon(ImageUtils.MEDIA_16);
    mediaLbl.setFont(new Font("Ubuntu", 1, 13)); // NOI18N
    mediaFileTb.add(mediaLbl);

    mediaFileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    mediaFileList.setFocusable(false);
    mediaFileScp.setViewportView(mediaFileList);

    GroupLayout mediaFilePnlLayout = new GroupLayout(mediaFilePnl);
    mediaFilePnl.setLayout(mediaFilePnlLayout);
    mediaFilePnlLayout.setHorizontalGroup(
      mediaFilePnlLayout.createParallelGroup(Alignment.LEADING)
      .addComponent(mediaFileTb, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(mediaFileScp, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
    );
    mediaFilePnlLayout.setVerticalGroup(
      mediaFilePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(mediaFilePnlLayout.createSequentialGroup()
        .addComponent(mediaFileTb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(mediaFileScp, GroupLayout.DEFAULT_SIZE, 690, Short.MAX_VALUE))
    );

    listSp.setLeftComponent(mediaFilePnl);

    centerPanel.setPreferredSize(new Dimension(600, 400));

    mediaSp.setOrientation(JSplitPane.VERTICAL_SPLIT);
    mediaSp.setMinimumSize(new Dimension(300, 500));
    mediaSp.setPreferredSize(new Dimension(300, 500));
    mediaSp.setContinuousLayout(true);

    searchPnl.setMargin(new Insets(10, 10, 10, 10));
    searchPnl.setPreferredSize(new Dimension(300, 180));

    searchTb.setFloatable(false);
    searchTb.setRollover(true);
    searchTb.setMargin(new Insets(1, 5, 1, 5));
    searchTb.setRound(5);

    searchLbl.setLanguage(UIUtils.i18n.getLanguageKey("searchtb.search"));
    searchLbl.setIcon(ImageUtils.SEARCH_16);
    searchLbl.setFont(new Font("Ubuntu", 1, 13)); // NOI18N
    searchTb.add(searchLbl);

    searchBtn.setIcon(ImageUtils.SEARCH_16);
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

    searchResultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    searchResultList.setFixedCellHeight(75);
    searchResultList.setFocusable(false);
    searchResultListSp.setViewportView(searchResultList);

    scrapperCb.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        scrapperCbActionPerformed(evt);
      }
    });

    GroupLayout searchPnlLayout = new GroupLayout(searchPnl);
    searchPnl.setLayout(searchPnlLayout);
    searchPnlLayout.setHorizontalGroup(
      searchPnlLayout.createParallelGroup(Alignment.LEADING)
      .addComponent(searchTb, GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
      .addGroup(searchPnlLayout.createSequentialGroup()
        .addComponent(searchField, GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(scrapperCb, GroupLayout.PREFERRED_SIZE, 143, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(searchBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
      .addComponent(searchResultListSp, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
    );
    searchPnlLayout.setVerticalGroup(
      searchPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(searchPnlLayout.createSequentialGroup()
        .addComponent(searchTb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addGroup(searchPnlLayout.createParallelGroup(Alignment.TRAILING)
          .addComponent(searchField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(searchBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(scrapperCb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(searchResultListSp, GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
        .addContainerGap())
    );

    searchField.setLeadingComponent(new JLabel(ImageUtils.SEARCH_16));

    mediaSp.setTopComponent(searchPnl);

    centerPanel.add(mediaSp, BorderLayout.CENTER);

    listSp.setRightComponent(centerPanel);

    listSp.setContinuousLayout(true);

    containerTransition.add(listSp, BorderLayout.CENTER);

    getContentPane().add(containerTransition, BorderLayout.CENTER);

    renameTb.setFloatable(false);
    renameTb.setRollover(true);
    renameTb.setRound(10);

    renameBtn.setLanguage(UIUtils.i18n.getLanguageKey("renametb.rename"));

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

    webPanel1.add(renameTb, BorderLayout.NORTH);

    statusBar.setMargin(new Insets(2, 10, 2, 10));

    webLabel1.setIcon(new ImageIcon(getClass().getResource("/image/ui/16/media-warn.png"))); // NOI18N
    webLabel1.setText("Media info is not installed");
    statusBar.add(webLabel1);
    statusBar.add(statusLbl);

    webPanel1.add(statusBar, BorderLayout.SOUTH);

    getContentPane().add(webPanel1, BorderLayout.SOUTH);

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

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        settingsDialog.setVisible(true);
      }
    });  }//GEN-LAST:event_settingBtnActionPerformed

  private void updateBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_updateBtnActionPerformed
    //checkUpdate(true); // TODO
  }//GEN-LAST:event_updateBtnActionPerformed

  private void renameBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_renameBtnActionPerformed
    WorkerManager.rename(currentMedia.getFile(), renameField.getText());
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
    switch (currentMode) {
      case MOVIEMODE:
        movieFileFormat = fileFormatField.getText();
        break;
      case TVSHOWMODE:
        tvshowFileFormat = fileFormatField.getText();
        break;
    }
  }//GEN-LAST:event_fileFormatFieldKeyReleased

  private void logsBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_logsBtnActionPerformed
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        logPanel.setVisible(true);
        logPanel.setLocationRelativeTo(MovieRenamer.this);
      }
    });
  }//GEN-LAST:event_logsBtnActionPerformed

  private void clearMediaFileListBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_clearMediaFileListBtnActionPerformed
    clearInterface(CLEAR_MEDIALIST, CLEAR_SEARCHRESULTLIST);
    clearMediaFileListBtn.setEnabled(false);
  }//GEN-LAST:event_clearMediaFileListBtnActionPerformed

  private void scrapperCbActionPerformed(ActionEvent evt) {//GEN-FIRST:event_scrapperCbActionPerformed
    currentScrapper = (UIScrapper) scrapperCb.getModel().getSelectedItem();
  }//GEN-LAST:event_scrapperCbActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private WebPanel centerPanel;
  private WebButton clearMediaFileListBtn;
  private ComponentTransition containerTransition;
  private WebButton exitBtn;
  private WebCheckBox fanartChk;
  private WebTextField fileFormatField;
  private WebSplitPane listSp;
  private WebButton logsBtn;
  private WebToolBar mainTb;
  private WebList mediaFileList;
  private WebPanel mediaFilePnl;
  private JScrollPane mediaFileScp;
  private WebToolBar mediaFileTb;
  private WebLabel mediaLbl;
  private WebSplitPane mediaSp;
  private Separator modeSep;
  private WebButton movieModeBtn;
  private WebCheckBox nfoChk;
  private WebButton openBtn;
  private Separator openSep;
  private WebButton renameBtn;
  private JTextField renameField;
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
  private WebLabel webLabel1;
  private WebPanel webPanel1;
  // End of variables declaration//GEN-END:variables
}
