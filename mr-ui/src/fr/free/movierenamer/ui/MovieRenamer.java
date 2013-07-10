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
import com.alee.extended.filechooser.SelectionMode;
import com.alee.extended.filechooser.WebFileChooser;
import com.alee.extended.image.DisplayType;
import com.alee.extended.image.WebImage;
import com.alee.extended.layout.ToolbarLayout;
import com.alee.extended.transition.ComponentTransition;
import com.alee.extended.transition.effects.Direction;
import com.alee.extended.transition.effects.curtain.CurtainTransitionEffect;
import com.alee.extended.transition.effects.curtain.CurtainType;
import com.alee.extended.transition.effects.fade.FadeTransitionEffect;
import com.alee.laf.StyleConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.DefaultListModel;
import com.alee.laf.list.WebList;
import com.alee.laf.optionpane.WebOptionPane;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebFrame;
import com.alee.laf.separator.WebSeparator;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.text.WebTextField;
import com.alee.laf.toolbar.WebToolBar;
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
import fr.free.movierenamer.ui.swing.IconListRenderer;
import fr.free.movierenamer.ui.swing.ImageListModel;
import fr.free.movierenamer.ui.swing.ListTooltip;
import fr.free.movierenamer.ui.swing.MediaListRenderer;
import fr.free.movierenamer.ui.swing.SearchResultListRenderer;
import fr.free.movierenamer.ui.swing.panel.ImagePanel;
import fr.free.movierenamer.ui.swing.panel.Loading;
import fr.free.movierenamer.ui.swing.panel.LogPanel;
import fr.free.movierenamer.ui.swing.panel.generator.SettingPanel;
import fr.free.movierenamer.ui.swing.panel.generator.info.MediaPanel;
import fr.free.movierenamer.ui.swing.panel.generator.info.MoviePanel;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.ui.worker.WorkerManager;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.LocaleUtils;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
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
  // Scrapper
  private UIScrapper currentScrapper;
  // Media Panel
  private MediaPanel<? extends MediaInfo> mediaPanel;
  private final MoviePanel moviePnl;
//  private final TvShowPanel tvShowPanel;
  private final ComponentTransition containerTransitionMediaPanel;// Media Panel container
  // Log Panel
  private final LogPanel logPanel = new LogPanel();
  // File chooser
  private final WebFileChooser fileChooser = new WebFileChooser(this, LocaleUtils.i18nExt("popup.fileChooser.browse"));
  // Clear interface
  private final boolean CLEAR_MEDIALIST = true;
  private final boolean CLEAR_SEARCHRESULTLIST = true;
  // Model
  private EventList<UIFile> mediaFileEventList = new BasicEventList<UIFile>();
  private final ImageListModel<UISearchResult> searchResultModel = new ImageListModel<UISearchResult>();
  private final DefaultComboBoxModel movieScrapperModel = new DefaultComboBoxModel();
  private final DefaultComboBoxModel tvshowScrapperModel = new DefaultComboBoxModel();
  private final EventListModel<UIFile> mediaFileSeparatorModel;
  private final EventListModel<UIFile> mediaFileModel = new EventListModel<UIFile>(mediaFileEventList);
  private final MediaListRenderer mediaFileListRenderer = new MediaListRenderer();
  private final SearchResultListRenderer searchResultListRenderer = new SearchResultListRenderer();
  //
  private final DefaultListModel loaderModel = new DefaultListModel();
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
  //
  private final ListSelectionListener mediaFileListListener = createMediaFileListListener();
  private final ImagePanel imgPnl;

  public MovieRenamer(List<File> files) {
    super();

    Loading loading = new Loading();

    Cache.clearAllCache();//FIXME remove !!!

    moviePnl = new MoviePanel();
//    tvShowPanel = new TvShowPanel(this);

    // Add Movie Renamer logger to log panel
   /* UISettings.LOGGER.addHandler(logPanel.getHandler());
     Settings.LOGGER.addHandler(logPanel.getHandler());*/

    mediaFileSeparatorModel = new EventListModel<UIFile>(mediaFileSeparator);

    // Set Movie Renamer mode
    currentMode = UIMode.MOVIEMODE;

    // Create media panel container and set transition effect
    containerTransitionMediaPanel = new ComponentTransition(moviePnl);
    containerTransitionMediaPanel.setTransitionEffect(new FadeTransitionEffect());

    initComponents();
    remove(containerTransition);
    containerTransition.setTransitionEffect(new FadeTransitionEffect());

    imgPnl = new ImagePanel(this);
    JScrollPane imgPanelScrollPane = new JScrollPane(imgPnl);
    imgPanelScrollPane.setBorder(null);
    centerPanel.add(imgPanelScrollPane, BorderLayout.EAST);

    // List option
    showIconMediaListChk = createShowIconChk(mediaFileList, setting.isShowIconMediaList(), "popup.menu.showIcon");
    showIconResultListChk = createShowIconChk(searchResultList, setting.isShowThumb(), "popup.menu.showIcon");

    showIdResultListChk = createShowChk(searchResultList, SearchResultListRenderer.Property.showId, setting.isShowId(), "popup.menu.showId");
    showYearResultListChk = createShowChk(searchResultList, SearchResultListRenderer.Property.showYear, setting.isShowYear(), "popup.menu.showYear");
    showOrigTitleResultListChk = createShowChk(searchResultList, SearchResultListRenderer.Property.showOrigTitle, setting.isShowOrigTitle(), "popup.menu.showOrigTitle");

    showFormatFieldChk = new WebCheckBox(LocaleUtils.i18nExt("popup.menu.showFormatField"));
    showFormatFieldChk.setSelected(setting.isShowFormatField());
    showFormatFieldChk.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // TODO
      }
    });

    init();

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

    setIconImage(ImageUtils.iconToImage(ImageUtils.LOGO_32));
    setLocationRelativeTo(null);
    setTitle(UISettings.APPNAME + "-" + setting.getVersion() + " " + currentMode.getTitleMode());

    // Check for Movie Renamer update
    if (setting.isCheckUpdate()) {// FIXME run after UI diplayed (separated thread or timer)
//      checkUpdate(false);
    }

    if (!files.isEmpty()) {
      loadFiles(files);
    }

    loading.setVisible(false);
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

    // Add tooltip
    TooltipManager.setDefaultDelay(1000);
    TooltipManager.setTooltip(openBtn, new WebLabel(LocaleUtils.i18nExt("tooltip.openFolder"), ImageUtils.FOLDERVIDEO_16), TooltipWay.down);
    //TooltipManager.setTooltip(editBtn, new WebLabel(LocaleUtils.i18nExt("edit"), new ImageIcon(getClass().getResource("/image/accessories-text-editor-6-24.png")), SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(movieModeBtn, new WebLabel(LocaleUtils.i18nExt("tooltip.movieMode"), ImageUtils.MOVIE_16, SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(tvShowModeBtn, new WebLabel(LocaleUtils.i18nExt("tooltip.tvshowMode"), ImageUtils.TV_16, SwingConstants.TRAILING), TooltipWay.down);
    //TooltipManager.setTooltip(helpBtn, new WebLabel(LocaleUtils.i18nExt("tooltip.help"), ImageUtils.HELP_16, SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(updateBtn, new WebLabel(LocaleUtils.i18nExt("tooltip.update"), ImageUtils.UPDATE_16, SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(logsBtn, new WebLabel(LocaleUtils.i18nExt("tooltip.logs"), ImageUtils.INFO_16, SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(settingBtn, new WebLabel(LocaleUtils.i18nExt("tooltip.settings"), ImageUtils.SETTING_16, SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(exitBtn, new WebLabel(LocaleUtils.i18nExt("tooltip.exit"), ImageUtils.APPLICATIONEXIT_16, SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(searchBtn, new WebLabel(LocaleUtils.i18nExt("tooltip.search"), ImageUtils.SEARCH_16, SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(renameBtn, new WebLabel(LocaleUtils.i18nExt("tooltip.rename"), ImageUtils.OK_16, SwingConstants.TRAILING), TooltipWay.up);
    TooltipManager.setTooltip(clearMediaFileListBtn, new WebLabel(LocaleUtils.i18nExt("tooltip.clearList"), ImageUtils.CLEAR_LIST_16, SwingConstants.TRAILING), TooltipWay.down);

    // Add media panel container to media split pane
    mediaSp.setBottomComponent(containerTransitionMediaPanel);

    // Init Movie Scrapper model
    for (MovieScrapper scrapper : ScrapperManager.getMovieScrapperList()) {
      movieScrapperModel.addElement(new UIScrapper(scrapper));
    }
    movieScrapperModel.setSelectedItem(new UIScrapper(ScrapperManager.getMovieScrapper()));

    // Init TvShow Scrapper model
    for (TvShowScrapper scrapper : ScrapperManager.getTvShowScrapperList()) {
      tvshowScrapperModel.addElement(new UIScrapper(scrapper));
    }
    tvshowScrapperModel.setSelectedItem(new UIScrapper(ScrapperManager.getTvShowScrapper()));

    scrapperCb.setRenderer(UIUtils.iconListRenderer);
    currentScrapper = (UIScrapper) movieScrapperModel.getSelectedItem();

    loadMediaPanel();

    // List tooltip listener
    ListTooltip mediaFileTooltip = new ListTooltip();
    ListTooltip searchResultTooltip = new ListTooltip(1200, true);

    mediaFileList.addListSelectionListener(mediaFileListListener);
    addDragAndDropListener(mediaFileList);//Add drag and drop listener on mediaFileList
    mediaFileList.addMouseListener(mediaFileTooltip);
    mediaFileList.addMouseMotionListener(mediaFileTooltip);

    mediaFileList.setCellRenderer(mediaFileListRenderer);

    searchResultList.addListSelectionListener(createSearchResultList());
    searchResultList.setCellRenderer(searchResultListRenderer);
    searchResultList.addMouseListener(searchResultTooltip);
    searchResultList.addMouseMotionListener(searchResultTooltip);
    // file chooser init
    // fileChooser.setFilesToChoose(FilesToChoose.all);

//    fileChooser.setAvailableFilter(new FileFilter());
    fileChooser.setSelectionMode(SelectionMode.MULTIPLE_SELECTION);
//    fileChooser.setPreviewFilter(ff);
//    fileChooser.setChooseFilter(ff);

    toggleGroup.setText(LocaleUtils.i18nExt("tooltip.switchDisplay"));
    toggleGroup.addActionListener(createToggleGroupListener());
    groupFile = setting.isGroupMediaList();
    setToggleGroupIcon();

    mediaFileTb.addToEnd(clearMediaFileListBtn);

    // Add settings button in toolbar
    renameTb.addToEnd(UIUtils.createSettingButton(PopupWay.upLeft, thumbChk, fanartChk, nfoChk, showFormatFieldChk));
    mediaFileTb.addToEnd(UIUtils.createSettingButton(PopupWay.downRight, toggleGroup, showIconMediaListChk));
    searchTb.addToEnd(UIUtils.createSettingButton(PopupWay.downLeft, showIconResultListChk, showIdResultListChk, showYearResultListChk, showOrigTitleResultListChk));

    // Add MovieRenamer (Main UI) to UIEvent receiver
    UIEvent.addEventListener(MovieRenamer.class, this);
  }

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

  private WebCheckBox createShowIconChk(final WebList list, boolean selected, String text) {
    return createShowChk(list, null, selected, text);
  }

  private WebCheckBox createShowChk(final WebList list, final IconListRenderer.IRendererProperty property, boolean selected, String text) {
    final WebCheckBox checkbox = new WebCheckBox(LocaleUtils.i18nExt(text));
    checkbox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        if (property != null) {
          property.setEnabled(checkbox.isSelected());
        } else {
          ((IconListRenderer) list.getCellRenderer()).showIcon(checkbox.isSelected());
        }
        list.revalidate();
        list.repaint();
      }
    });
    checkbox.setSelected(selected);
    return checkbox;
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

  @Override
  public void UIEventHandler(UIEvent.Event event, IEventInfo info, Object param) {

    UISettings.LOGGER.finer(String.format("%s receive event %s %s", getClass().getSimpleName(), event, (info != null ? info : "")));

    switch (event) {
      case WORKER_STARTED:
        statusLbl.setText(info.getDisplayName());
        statusLbl.setIcon(ImageUtils.LOAD_24);
        break;
      case WORKER_RUNNING:
        statusLbl.setText(info.getDisplayName());
        break;
      case WORKER_ALL_DONE:
        statusLbl.setText("");
        statusLbl.setIcon(null);
        break;
      case SETTINGS:
        if (param instanceof Settings.IProperty) {
          if (param instanceof Settings.SettingsProperty) {
            Settings.SettingsProperty sproperty = (Settings.SettingsProperty) param;
            System.out.println(sproperty.name() + " changed to " + sproperty.getValue());
          }

          if (param instanceof UISettings.UISettingsProperty) {
            UISettings.UISettingsProperty uisproperty = (UISettings.UISettingsProperty) param;
            System.out.println(uisproperty.name() + " changed to " + uisproperty.getValue());
          }
        }
        break;
    }
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

  private ListSelectionListener createSearchResultList() {
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
        fileFormatField.setText(setting.coreInstance.getMovieFilenameFormat());//FIXME user change
        break;
      case TVSHOWMODE:
//        containerTransitionMediaPanel.performTransition(tvShowPanel);
//        movieModeBtn.setEnabled(true);
//        scrapperCb.setModel(tvshowScrapperModel);
//        fileFormatField.setText(setting.coreInstance.getTvShowFilenameFormat());//FIXME user change
        break;
    }

    setTitle(UISettings.APPNAME + "-" + setting.getVersion() + " " + currentMode.getTitleMode());
  }

  /**
   * Search media on web
   */
  private void searchMedia() {

    if (currentMedia == null) {
      return;
    }

    String search = searchField.getText();
    if (search.length() == 0) {
      WebOptionPane.showMessageDialog(MovieRenamer.this, LocaleUtils.i18nExt("error.searchEmpty"), LocaleUtils.i18nExt("error"), JOptionPane.ERROR_MESSAGE);
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
    
    clearInterface(!CLEAR_MEDIALIST, !CLEAR_SEARCHRESULTLIST);

    WorkerManager.searchInfo(this, searchResult);
    WorkerManager.searchImage(this, searchResult);
  }

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
 /*   MediaInfo mediaInfo = getMediaPanel().getMediaInfo();
     if (mediaInfo != null) {
     renameField.setText(mediaInfo.getRenamedTitle(fileFormatField.getText()));
     } else {
     renameField.setText(null);
     }*/
  }

  /**
   * Clear Movie Renamer interface
   *
   * @param mediaList Clear media list
   * @param searchList Clear search list
   */
  private void clearInterface(boolean clearMediaList, boolean clearSearchResultList) {// FIXME improve memory free

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

    // Stop all running workers
    WorkerManager.stop();
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    updateBtn = new WebButton();
    settingBtn = new WebButton();
    exitBtn = new WebButton();
    renameField = new JTextField();
    thumbChk = new WebCheckBox();
    fanartChk = new WebCheckBox();
    nfoChk = new WebCheckBox();
    toggleGroup = new WebButton();
    fileFormatField = new WebTextField();
    logsBtn = new WebButton();
    clearMediaFileListBtn = new WebButton();
    containerTransition = new ComponentTransition();
    mainTb = new WebToolBar();
    openBtn = new WebButton();
    openSep = new WebSeparator();
    movieModeBtn = new WebButton();
    tvShowModeBtn = new WebButton();
    modeSep = new Separator();
    statusLbl = new WebLabel();
    renameTb = new WebToolBar();
    renameBtn = new WebButton();
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
    scrapperCb = (JComboBox) new WebComboBox();

    updateBtn.setIcon(ImageUtils.UPDATE_24);
    updateBtn.setRolloverDarkBorderOnly(true);
    updateBtn.setRolloverDecoratedOnly(true);
    updateBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        updateBtnActionPerformed(evt);
      }
    });

    settingBtn.setIcon(ImageUtils.SETTING_24);
    settingBtn.setHorizontalTextPosition(SwingConstants.CENTER);
    settingBtn.setRolloverDarkBorderOnly(true);
    settingBtn.setRolloverDecoratedOnly(true);
    settingBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
    settingBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        settingBtnActionPerformed(evt);
      }
    });

    exitBtn.setIcon(ImageUtils.APPLICATIONEXIT_24);
    exitBtn.setHorizontalTextPosition(SwingConstants.CENTER);
    exitBtn.setRolloverDarkBorderOnly(true);
    exitBtn.setRolloverDecoratedOnly(true);
    exitBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
    exitBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        exitBtnActionPerformed(evt);
      }
    });

    renameField.setEnabled(false);

    thumbChk.setText(LocaleUtils.i18nExt("thumb")); // NOI18N

    fanartChk.setText(LocaleUtils.i18nExt("fanart")); // NOI18N

    nfoChk.setText(LocaleUtils.i18nExt("xbmcNfo")); // NOI18N

    toggleGroup.setIcon(ImageUtils.FILEVIEW_16);
    toggleGroup.setRolloverDarkBorderOnly(true);
    toggleGroup.setRolloverDecoratedOnly(true);

    fileFormatField.setPreferredSize(new Dimension(250, 27));
    fileFormatField.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent evt) {
        fileFormatFieldKeyReleased(evt);
      }
    });

    logsBtn.setIcon(ImageUtils.INFO_24);
    logsBtn.setRolloverDarkBorderOnly(true);
    logsBtn.setRolloverDecoratedOnly(true);
    logsBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        logsBtnActionPerformed(evt);
      }
    });

    clearMediaFileListBtn.setIcon(new ImageIcon(getClass().getResource("/image/ui/16/media_playlist_clear.png"))); // NOI18N
    clearMediaFileListBtn.setAlignmentY(0.0F);
    clearMediaFileListBtn.setEnabled(false);
    clearMediaFileListBtn.setFocusPainted(true);
    clearMediaFileListBtn.setInnerShadeWidth(0);
    clearMediaFileListBtn.setLeftRightSpacing(1);
    clearMediaFileListBtn.setRolloverDarkBorderOnly(true);
    clearMediaFileListBtn.setRolloverDecoratedOnly(true);
    clearMediaFileListBtn.setRound(2);
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

    openBtn.setIcon(ImageUtils.FOLDERVIDEO_24);
    openBtn.setHorizontalTextPosition(SwingConstants.CENTER);
    openBtn.setRolloverDarkBorderOnly(true);
    openBtn.setRolloverDecoratedOnly(true);
    openBtn.setRolloverShadeOnly(true);
    openBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
    openBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        openBtnActionPerformed(evt);
      }
    });
    mainTb.add(openBtn);

    openSep.setFocusable(true);
    mainTb.add(openSep);

    movieModeBtn.setIcon(ImageUtils.MOVIE_24);
    movieModeBtn.setHorizontalTextPosition(SwingConstants.CENTER);
    movieModeBtn.setRolloverDarkBorderOnly(true);
    movieModeBtn.setRolloverDecoratedOnly(true);
    movieModeBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
    movieModeBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        movieModeBtnActionPerformed(evt);
      }
    });
    mainTb.add(movieModeBtn);

    tvShowModeBtn.setIcon(ImageUtils.TV_24);
    tvShowModeBtn.setHorizontalTextPosition(SwingConstants.CENTER);
    tvShowModeBtn.setRolloverDarkBorderOnly(true);
    tvShowModeBtn.setRolloverDecoratedOnly(true);
    tvShowModeBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
    tvShowModeBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        tvShowModeBtnActionPerformed(evt);
      }
    });
    mainTb.add(tvShowModeBtn);
    mainTb.add(modeSep);
    mainTb.add(statusLbl);

    containerTransition.add(mainTb, BorderLayout.PAGE_START);

    renameTb.setFloatable(false);
    renameTb.setRollover(true);
    renameTb.setRound(10);

    renameBtn.setIcon(ImageUtils.OK_24);
    renameBtn.setText(LocaleUtils.i18nExt("rename")); // NOI18N
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

    containerTransition.add(renameTb, BorderLayout.PAGE_END);

    mediaFilePnl.setMargin(new Insets(10, 10, 10, 10));
    mediaFilePnl.setMinimumSize(new Dimension(160, 50));
    mediaFilePnl.setPreferredSize(new Dimension(160, 200));

    mediaFileTb.setFloatable(false);
    mediaFileTb.setRollover(true);
    mediaFileTb.setMargin(new Insets(0, 5, 0, 5));
    mediaFileTb.setRound(5);

    mediaLbl.setIcon(ImageUtils.MEDIA_16);
    mediaLbl.setText(LocaleUtils.i18nExt("media")); // NOI18N
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
        .addComponent(mediaFileScp, GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE))
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

    searchLbl.setIcon(ImageUtils.SEARCH_16);
    searchLbl.setText(LocaleUtils.i18nExt("search")); // NOI18N
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
      .addComponent(searchTb, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addGroup(searchPnlLayout.createSequentialGroup()
        .addComponent(searchField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(scrapperCb, GroupLayout.PREFERRED_SIZE, 143, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(searchBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
      .addComponent(searchResultListSp, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
    );
    searchPnlLayout.setVerticalGroup(
      searchPnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(searchPnlLayout.createSequentialGroup()
        .addComponent(searchTb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addGroup(searchPnlLayout.createParallelGroup(Alignment.TRAILING)
          .addGroup(searchPnlLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(searchField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(scrapperCb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addComponent(searchBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(searchResultListSp, GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
        .addContainerGap())
    );

    searchField.setLeadingComponent(new JLabel(ImageUtils.SEARCH_16));

    mediaSp.setTopComponent(searchPnl);

    centerPanel.add(mediaSp, BorderLayout.CENTER);

    listSp.setRightComponent(centerPanel);

    listSp.setContinuousLayout(true);

    containerTransition.add(listSp, BorderLayout.CENTER);

    getContentPane().add(containerTransition, BorderLayout.CENTER);

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void openBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_openBtnActionPerformed
    fileChooser.setCurrentDirectory(new File(setting.getFileChooserPath()));
    int r = fileChooser.showDialog();
    if (r == 0) {
      List<File> files = fileChooser.getSelectedFiles();
      if (!files.isEmpty()) {// Remember path
        try {
          UISettingsProperty.fileChooserPath.setValue(files.get(0).getParent());
        } catch (IOException e) {
          UISettings.LOGGER.log(Level.SEVERE, e.getMessage());
          WebOptionPane.showMessageDialog(MovieRenamer.this, LocaleUtils.i18nExt("error.failedSaveFolderPath"), LocaleUtils.i18nExt("error"), JOptionPane.ERROR_MESSAGE);
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
    WebOptionPane.showMessageDialog(MovieRenamer.this, "Tv show is not available for the moment but will be added soon.", "Unavailable", JOptionPane.INFORMATION_MESSAGE);
//    currentMode = UIMode.TVSHOWMODE;
//    loadMediaPanel();
  }//GEN-LAST:event_tvShowModeBtnActionPerformed

  private void exitBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_exitBtnActionPerformed
    System.exit(0);
  }//GEN-LAST:event_exitBtnActionPerformed

  private void settingBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_settingBtnActionPerformed
    final SettingPanel settingsDialog = new SettingPanel(this);
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
  }//GEN-LAST:event_fileFormatFieldKeyReleased

  private void scrapperCbActionPerformed(ActionEvent evt) {//GEN-FIRST:event_scrapperCbActionPerformed
    currentScrapper = (UIScrapper) scrapperCb.getModel().getSelectedItem();
  }//GEN-LAST:event_scrapperCbActionPerformed

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
  private WebSeparator openSep;
  private WebButton renameBtn;
  private JTextField renameField;
  private WebToolBar renameTb;
  private JComboBox scrapperCb;
  private WebButton searchBtn;
  private WebTextField searchField;
  private WebLabel searchLbl;
  private WebPanel searchPnl;
  private WebList searchResultList;
  private JScrollPane searchResultListSp;
  private WebToolBar searchTb;
  private WebButton settingBtn;
  private WebLabel statusLbl;
  private WebCheckBox thumbChk;
  private WebButton toggleGroup;
  private WebButton tvShowModeBtn;
  private WebButton updateBtn;
  // End of variables declaration//GEN-END:variables
}
