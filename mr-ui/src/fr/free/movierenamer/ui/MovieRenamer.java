/*
 * Movie Renamer
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
package fr.free.movierenamer.ui;

import com.alee.extended.background.BorderPainter;
import com.alee.extended.filechooser.FilesToChoose;
import com.alee.extended.filechooser.SelectionMode;
import com.alee.extended.filechooser.WebFileChooser;
import com.alee.extended.layout.ToolbarLayout;
import com.alee.extended.transition.ComponentTransition;
import com.alee.extended.transition.effects.fade.FadeTransitionEffect;
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.DefaultListModel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.separator.WebSeparator;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.text.WebTextField;
import com.alee.laf.toolbar.WebToolBar;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.scrapper.MediaScrapper;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.scrapper.TvShowScrapper;
import fr.free.movierenamer.scrapper.impl.ScrapperManager;
import fr.free.movierenamer.scrapper.impl.TMDbScrapper;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.searchinfo.SearchResult;
import fr.free.movierenamer.ui.panel.MediaPanel;
import fr.free.movierenamer.ui.panel.MoviePanel;
import fr.free.movierenamer.ui.panel.SettingPanel;
import fr.free.movierenamer.ui.panel.TvShowPanel;
import fr.free.movierenamer.ui.res.DragAndDrop;
import fr.free.movierenamer.ui.res.IIconList;
import fr.free.movierenamer.ui.res.IconListRenderer;
import fr.free.movierenamer.ui.res.UIFile;
import fr.free.movierenamer.ui.res.UILoader;
import fr.free.movierenamer.ui.res.UIScrapper;
import fr.free.movierenamer.ui.res.UISearchResult;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.settings.UISettings.UISettingsProperty;
import fr.free.movierenamer.ui.utils.FileFilter;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.ui.worker.ImageWorker;
import fr.free.movierenamer.ui.worker.ListFilesWorker;
import fr.free.movierenamer.ui.worker.SearchMediaImagesWorker;
import fr.free.movierenamer.ui.worker.SearchMediaInfoWorker;
import fr.free.movierenamer.ui.worker.SearchMediaSubtitlesWorker;
import fr.free.movierenamer.ui.worker.SearchMediaWorker;
import fr.free.movierenamer.ui.worker.listener.ListFileListener;
import fr.free.movierenamer.ui.worker.listener.SearchMediaImagesListener;
import fr.free.movierenamer.ui.worker.listener.SearchMediaInfoListener;
import fr.free.movierenamer.ui.worker.listener.SearchMediaListener;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.LocaleUtils;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JToolBar.Separator;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Class MovieRenamer
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class MovieRenamer extends JFrame {

  private static final long serialVersionUID = 1L;
  private final UISettings setting = UISettings.getInstance();
  // Current variables
  private MovieRenamerMode currentMode;
  private UIFile currentMedia;
  private UIScrapper UIMovieScrapper = new UIScrapper(ScrapperManager.getScrapper(setting.coreInstance.getSearchMovieScrapper()));
  private UIScrapper UITvShowScrapper = new UIScrapper(ScrapperManager.getScrapper(setting.coreInstance.getSearchTvshowScrapper()));
  private Queue<ImageWorker<? extends IIconList>> imageWorkerQueue = new LinkedList<ImageWorker<? extends IIconList>>();
  // Property change
  private final PropertyChangeSupport settingsChange;
  // Media Panel
  private final MoviePanel moviePnl;
  private final TvShowPanel tvShowPanel;
  private final ComponentTransition containerTransitionMediaPanel;// Media Panel container
  // File chooser
  private final WebFileChooser fileChooser;
  // UI tools
  public static final Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
  public static final Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
  // Clear interface
  public static final boolean CLEAR_MEDIALIST = true;
  public static final boolean CLEAR_SEARCHRESULTLIST = true;
  // Border Blue
  private final BorderPainter<? extends JComponent> blueBorder;
  // Model
  private final DefaultComboBoxModel movieScrapperModel;
  private final DefaultComboBoxModel tvshowScrapperModel;
  // Worker
  private ListFilesWorker listFileWorker;
  private SearchMediaWorker searchWorker;
  private SearchMediaInfoWorker infoWorker;
  private SearchMediaImagesWorker imagesWorker;
  private SearchMediaSubtitlesWorker subtitleWorker;
  // Model
  private final DefaultListModel mediaFileNameModel = new DefaultListModel();
  private final DefaultListModel searchResultModel = new DefaultListModel();
  // Renderer
  private final IconListRenderer<IIconList> loaderListRenderer = new IconListRenderer<IIconList>(true);

  public MovieRenamer() {

    Cache.clearAllCache();//FIXME remove !!!

    moviePnl = new MoviePanel();
    tvShowPanel = new TvShowPanel(this);
    settingsChange = new PropertyChangeSupport(setting);

    blueBorder = UIUtils.getBorder(1, 5, new Color(39, 95, 173));
    movieScrapperModel = new DefaultComboBoxModel();
    tvshowScrapperModel = new DefaultComboBoxModel();

    // Set Movie Renamer mode
    currentMode = MovieRenamerMode.MOVIEMODE;

    // Create media panel container and set transition effect
    containerTransitionMediaPanel = new ComponentTransition(moviePnl);
    containerTransitionMediaPanel.setTransitionEffect(new FadeTransitionEffect());

    fileChooser = new WebFileChooser(this, "");// FIXME add title !

    initComponents();
    init();

    movieModeBtn.setEnabled(false);

    setIconImage(UIUtils.LOGO_32);
    setLocationRelativeTo(null);
    setTitle(UISettings.APPNAME + "-" + setting.getVersion() + " " + currentMode.getTitleMode());
    setVisible(true);

    // Check for Movie Renamer update
    if (setting.isCheckUpdate()) {
      checkUpdate(false);
    }
  }

  private void init() {

    // Add button to main toolbar on right
    mainTb.addToEnd(helpBtn);
    mainTb.addToEnd(new JSeparator(JSeparator.VERTICAL));
    mainTb.addToEnd(updateBtn);
    mainTb.addToEnd(settingBtn);
    mainTb.addToEnd(exitBtn);

    // Add tooltip
    TooltipManager.setTooltip(openBtn, openTooltipLbl, TooltipWay.down);
    //TooltipManager.setTooltip(editBtn, new WebLabel(LocaleUtils.i18nExt("edit"), new ImageIcon(getClass().getResource("/image/accessories-text-editor-6-24.png")), SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(movieModeBtn, new WebLabel(LocaleUtils.i18nExt("movieMode"), ImageUtils.getIconFromJar("ui/movie.png"), SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(tvShowModeBtn, new WebLabel(LocaleUtils.i18nExt("tvshowMode"), ImageUtils.getIconFromJar("ui/tv.png"), SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(helpBtn, new WebLabel(LocaleUtils.i18nExt("help"), ImageUtils.getIconFromJar("ui/system-help-3.png"), SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(updateBtn, new WebLabel(LocaleUtils.i18nExt("updateBtn"), ImageUtils.getIconFromJar("ui/system-software-update-5.png"), SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(settingBtn, new WebLabel(LocaleUtils.i18nExt("settingBtn"), ImageUtils.getIconFromJar("ui/system-settings.png"), SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(exitBtn, new WebLabel(LocaleUtils.i18nExt("exitBtn"), ImageUtils.getIconFromJar("ui/application-exit.png"), SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(searchBtn, new WebLabel(LocaleUtils.i18nExt("search"), ImageUtils.getIconFromJar("ui/search.png"), SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(renameBtn, new WebLabel(LocaleUtils.i18nExt("rename"), ImageUtils.getIconFromJar("ui/dialog-ok-2.png"), SwingConstants.TRAILING), TooltipWay.down);

    // Add media panel container to media split pane
    mediaSp.setBottomComponent(containerTransitionMediaPanel);
    fileFormatField.setText(setting.coreInstance.getMovieFilenameFormat());

    // Set blue border
    mainTb.setPainter(blueBorder);
    renameTb.setPainter(blueBorder);
    mediaFileTb.setPainter(blueBorder);
    searchTb.setPainter(blueBorder);

    for (MovieScrapper scrapper : ScrapperManager.getMovieScrapperList()) {
      movieScrapperModel.addElement(new UIScrapper(scrapper));
    }

    for (TvShowScrapper scrapper : ScrapperManager.getTvShowScrapperList()) {
      tvshowScrapperModel.addElement(new UIScrapper(scrapper));
    }

    scrapperCb.setRenderer(UIUtils.iconListRenderer);
    scrapperCb.setModel(movieScrapperModel);
    scrapperCb.setSelectedItem(UIMovieScrapper);

    //Add drag and drop listener on mediaFileList
    DragAndDrop dropFile = new DragAndDrop(this, mediaFileList) {
      @Override
      public void getFiles(List<File> files) {
        loadFiles(files);
      }
    };
    DropTarget dt = new DropTarget(mediaFileList, dropFile);
    dt.setActive(true);

    mediaFileList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent lse) {
        if (!lse.getValueIsAdjusting()) {
          cancelWorker();
          currentMedia = getSelectedMediaFile();
          if (currentMedia == null) {
            return;
          }
          searchField.setText(currentMedia.getSearch());
          searchMedia();
        }
      }
    });
    mediaFileList.setModel(mediaFileNameModel);

    searchResultList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent lse) {
        if (!lse.getValueIsAdjusting()) {
          searchMediaInfo();
        }
      }
    });
    searchResultList.setModel(searchResultModel);

    // file chooser init
    fileChooser.setFilesToChoose(FilesToChoose.all);
    fileChooser.setSelectionMode(SelectionMode.MULTIPLE_SELECTION);
    FileFilter ff = new FileFilter();
    fileChooser.setPreviewFilter(ff);
    fileChooser.setChooseFilter(ff);
  }

  /**
   *
   * @param files
   */
  private void loadFiles(List<File> files) {
    cancelWorker();
    clearInterface(CLEAR_MEDIALIST, CLEAR_SEARCHRESULTLIST);

    // Add loader image
    mediaFileNameModel.addElement(new UILoader(mediaFileList));
    mediaFileList.setCellRenderer(loaderListRenderer);

    listFileWorker = new ListFilesWorker(files);
    ListFileListener listener = new ListFileListener(listFileWorker, this, mediaFileList, mediaFileNameModel);
    listFileWorker.addPropertyChangeListener(listener);
    listFileWorker.execute();
  }

  /**
   * Search media on web
   */
  private void searchMedia() {
    cancelWorker();
    searchBtn.setEnabled(false);
    searchField.setEnabled(false);

    if (currentMedia == null) {
      return;
    }

    String search = searchField.getText();
    if (search.length() == 0) {
      JOptionPane.showMessageDialog(MovieRenamer.this, LocaleUtils.i18n("noTextToSearch"), LocaleUtils.i18n("error"), JOptionPane.ERROR_MESSAGE);// FIXME use laf dialog
      return;
    }

    clearInterface(!CLEAR_MEDIALIST, CLEAR_SEARCHRESULTLIST);

    // Add loader image
    searchResultModel.addElement(new UILoader(searchResultList));
    searchResultList.setCellRenderer(loaderListRenderer);

    currentMedia.setSearch(search);

    MediaScrapper<? extends Media, ? extends MediaInfo> mediaScrapper = ((UIScrapper) scrapperCb.getSelectedItem()).getScrapper();
    searchWorker = new SearchMediaWorker(currentMedia, mediaScrapper);
    SearchMediaListener listener = new SearchMediaListener(searchWorker, this, searchResultList, currentMedia, searchBtn, searchField, searchResultModel);
    searchWorker.addPropertyChangeListener(listener);
    searchWorker.execute();
  }

  private void searchMediaInfo() {
    cancelWorker();
    UISearchResult searchResult = getSelectedSearchResult();
    if (searchResult == null) {
      return;
    }

    MediaPanel ipanel = getCurrentMediaPanel();
    if (ipanel == null) {
      return;
    }

    infoWorker = new SearchMediaInfoWorker(currentMedia, searchResult);
    imagesWorker = new SearchMediaImagesWorker(searchResult, getImagesSCrapper());
    //subtitleWorker = new SearchMediaSubtitlesWorker(currentMedia, null);

    SearchMediaInfoListener mediaListener = new SearchMediaInfoListener(infoWorker, this, ipanel);
    SearchMediaImagesListener imagesListener = new SearchMediaImagesListener(imagesWorker, this, ipanel);

    infoWorker.addPropertyChangeListener(mediaListener);
    imagesWorker.addPropertyChangeListener(imagesListener);

    infoWorker.execute();
    imagesWorker.execute();
  }

  private MediaScrapper<? extends SearchResult, ? extends MediaInfo> getImagesSCrapper() {
    MediaScrapper<? extends SearchResult, ? extends MediaInfo> scrapper = null;
    switch (currentMode) {
      case MOVIEMODE:
        scrapper = ScrapperManager.getScrapper(TMDbScrapper.class);
        break;
      case TVSHOWMODE:
        //scrapper = ScrapperManager.getScrapper(FanartTV.class); // TODO
        break;
    }
    return scrapper;
  }

  private void cancelWorker() {
    if (listFileWorker != null && !listFileWorker.isDone()) {
      listFileWorker.cancel(true);
    }
    if (searchWorker != null && !searchWorker.isDone()) {
      searchWorker.cancel(true);
    }
    if (infoWorker != null && !infoWorker.isDone()) {
      infoWorker.cancel(true);
    }

    ImageWorker<? extends IIconList> worker;
    while((worker = imageWorkerQueue.poll()) != null) {
      worker.cancel(true);
    }
  }

  public synchronized void addImageWorker(ImageWorker<? extends IIconList> worker){
    imageWorkerQueue.add(worker);
  }

  public UIFile getSelectedMediaFile() {
    UIFile current = null;
    if (mediaFileList != null) {
      Object obj = mediaFileList.getSelectedValue();
      if (obj != null) {
        if (obj instanceof UIFile) {
          current = (UIFile) obj;
          mediaFileList.ensureIndexIsVisible(mediaFileList.getSelectedIndex());
        }
      }
    }
    return current;
  }

  public UISearchResult getSelectedSearchResult() {
    UISearchResult current = null;
    if (searchResultList != null) {
      Object obj = searchResultList.getSelectedValue();
      if (obj != null) {
        if (obj instanceof UISearchResult) {
          current = (UISearchResult) obj;
          searchResultList.ensureIndexIsVisible(searchResultList.getSelectedIndex());
        }
      }
    }
    return current;
  }

  public void updateMediaPanel() {
    if (containerTransitionMediaPanel != null) {
      movieModeBtn.setEnabled(false);
      tvShowModeBtn.setEnabled(false);
      switch (currentMode) {
        case MOVIEMODE:
          containerTransitionMediaPanel.performTransition(moviePnl);
          tvShowModeBtn.setEnabled(true);
          scrapperCb.setModel(movieScrapperModel);
          scrapperCb.setSelectedItem(UIMovieScrapper);
          fileFormatField.setText(setting.coreInstance.getMovieFilenameFormat());
          break;
        case TVSHOWMODE:
          containerTransitionMediaPanel.performTransition(tvShowPanel);
          movieModeBtn.setEnabled(true);
          scrapperCb.setModel(tvshowScrapperModel);
          scrapperCb.setSelectedItem(UITvShowScrapper);
          fileFormatField.setText(setting.coreInstance.getTvShowFilenameFormat());
          break;
      }
    }
  }

  private MediaPanel getCurrentMediaPanel() {
    MediaPanel current = null;
    if (containerTransitionMediaPanel != null) {
      Component compo = containerTransitionMediaPanel.getContent();
      if (compo != null) {
        if (compo instanceof MediaPanel) {
          current = (MediaPanel) compo;
        }
      }
    }
    return current;
  }

  public void updateRenamedTitle() {
    MediaInfo mediaInfo = getCurrentMediaPanel().getMediaInfo();
    if (mediaInfo != null) {
      renameField.setText(mediaInfo.getRenamedTitle(fileFormatField.getText()));
    } else {
      renameField.setText(null);
    }
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
      mediaFileNameModel.removeAllElements();
      mediaFileList.removeAll();
    }

    if (clearSearchResultList) {
      searchResultModel.removeAllElements();
      searchResultList.removeAll();
    }

    moviePnl.clear();
    tvShowPanel.clear();
    updateRenamedTitle();
  }

  /**
   *
   *
   * @return True if current mode support media type, false otherwise
   */
  private boolean checkMediaTypeInCurrentMode() {// TODO

    return true;
  }

  /**
   * Check for Movie Renamer update
   *
   * @param showAlready Show dialog
   */
  public final void checkUpdate(boolean showAlready) {// TODO
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
    helpBtn = new WebButton();
    openTooltipLbl = new JLabel(new ImageIcon(getClass().getResource("/image/ui/folder-video.png")), SwingConstants.TRAILING);
    renameField = new JTextField();
    thumbChk = new WebCheckBox();
    fanartChk = new WebCheckBox();
    nfoChk = new WebCheckBox();
    mainTb = new WebToolBar();
    openBtn = new WebButton();
    openSep = new WebSeparator();
    movieModeBtn = new WebButton();
    tvShowModeBtn = new WebButton();
    modeSep = new Separator();
    fileFormatLbl = new WebLabel();
    fileFormatField = new WebTextField();
    renameTb = new WebToolBar();
    renameBtn = new WebButton();
    listSp = new WebSplitPane();
    mediaSp = new WebSplitPane();
    searchPnl = new WebPanel();
    searchTb = new WebToolBar();
    searchLbl = new WebLabel();
    searchBtn = new WebButton();
    searchField = new WebTextField();
    jScrollPane1 = new JScrollPane();
    searchResultList = new WebList();
    scrapperCb = new JComboBox();
    mediaFilePnl = new WebPanel();
    mediaFileTb = new WebToolBar();
    mediaLbl = new WebLabel();
    mediaFileScp = new JScrollPane();
    mediaFileList = new WebList();

    updateBtn.setIcon(new ImageIcon(getClass().getResource("/image/ui/system-software-update-5.png"))); // NOI18N
    updateBtn.setFocusable(false);
    updateBtn.setHorizontalTextPosition(SwingConstants.CENTER);
    updateBtn.setRolloverDarkBorderOnly(true);
    updateBtn.setRolloverDecoratedOnly(true);
    updateBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
    updateBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        updateBtnActionPerformed(evt);
      }
    });

    settingBtn.setIcon(new ImageIcon(getClass().getResource("/image/ui/system-settings.png"))); // NOI18N
    settingBtn.setFocusable(false);
    settingBtn.setHorizontalTextPosition(SwingConstants.CENTER);
    settingBtn.setRolloverDarkBorderOnly(true);
    settingBtn.setRolloverDecoratedOnly(true);
    settingBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
    settingBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        settingBtnActionPerformed(evt);
      }
    });

    exitBtn.setIcon(new ImageIcon(getClass().getResource("/image/ui/application-exit.png"))); // NOI18N
    exitBtn.setFocusable(false);
    exitBtn.setHorizontalTextPosition(SwingConstants.CENTER);
    exitBtn.setRolloverDarkBorderOnly(true);
    exitBtn.setRolloverDecoratedOnly(true);
    exitBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
    exitBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        exitBtnActionPerformed(evt);
      }
    });

    helpBtn.setIcon(new ImageIcon(getClass().getResource("/image/ui/system-help-3.png"))); // NOI18N
    helpBtn.setFocusable(false);
    helpBtn.setHorizontalTextPosition(SwingConstants.CENTER);
    helpBtn.setRolloverDarkBorderOnly(true);
    helpBtn.setRolloverDecoratedOnly(true);
    helpBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
    helpBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        helpBtnActionPerformed(evt);
      }
    });

    openTooltipLbl.setText(LocaleUtils.i18nExt("openFolderBtn")); // NOI18N

    renameField.setEnabled(false);

    thumbChk.setText(LocaleUtils.i18nExt("thumb")); // NOI18N
    thumbChk.setFocusable(false);

    fanartChk.setText(LocaleUtils.i18nExt("fanart")); // NOI18N
    fanartChk.setFocusable(false);

    nfoChk.setText(LocaleUtils.i18nExt("xbmcNfo")); // NOI18N
    nfoChk.setFocusable(false);

    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setMinimumSize(new Dimension(770, 570));

    mainTb.setFloatable(false);
    mainTb.setRollover(true);
    mainTb.setMargin(new Insets(4, 4, 4, 4));
    mainTb.setRound(10);

    openBtn.setIcon(new ImageIcon(getClass().getResource("/image/ui/folder-video.png"))); // NOI18N
    openBtn.setFocusable(false);
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
    mainTb.add(openSep);

    movieModeBtn.setIcon(new ImageIcon(getClass().getResource("/image/ui/movie.png"))); // NOI18N
    movieModeBtn.setFocusable(false);
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

    tvShowModeBtn.setIcon(new ImageIcon(getClass().getResource("/image/ui/tv.png"))); // NOI18N
    tvShowModeBtn.setFocusable(false);
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

    fileFormatLbl.setText(LocaleUtils.i18nExt("mediaFileFormat")); // NOI18N
    fileFormatLbl.setFont(new Font("Ubuntu", 1, 13)); // NOI18N
    fileFormatLbl.setMargin(new Insets(0, 20, 0, 20));
    mainTb.add(fileFormatLbl);

    fileFormatField.setPreferredSize(new Dimension(250, 27));
    fileFormatField.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent evt) {
        fileFormatFieldKeyReleased(evt);
      }
    });
    mainTb.add(fileFormatField);

    getContentPane().add(mainTb, BorderLayout.PAGE_START);

    renameTb.setFloatable(false);
    renameTb.setRollover(true);
    renameTb.setRound(10);

    renameBtn.setIcon(new ImageIcon(getClass().getResource("/image/ui/dialog-ok-2.png"))); // NOI18N
    renameBtn.setText(LocaleUtils.i18nExt("rename")); // NOI18N
    renameBtn.setEnabled(false);
    renameBtn.setFocusable(false);
    renameBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        renameBtnActionPerformed(evt);
      }
    });
    renameTb.add(renameBtn);
    //Add rename text field
    renameTb.add(renameField, ToolbarLayout.FILL);
    renameTb.add(thumbChk, ToolbarLayout.END);
    renameTb.add(fanartChk, ToolbarLayout.END);
    renameTb.add(nfoChk, ToolbarLayout.END);

    getContentPane().add(renameTb, BorderLayout.PAGE_END);

    mediaSp.setOrientation(JSplitPane.VERTICAL_SPLIT);

    searchPnl.setMargin(new Insets(10, 10, 10, 10));

    searchTb.setFloatable(false);
    searchTb.setRollover(true);
    searchTb.setMargin(new Insets(3, 3, 3, 3));
    searchTb.setRound(5);

    searchLbl.setText(LocaleUtils.i18nExt("search")); // NOI18N
    searchLbl.setFont(new Font("Ubuntu", 1, 14)); // NOI18N
    searchTb.add(searchLbl);

    searchBtn.setIcon(new ImageIcon(getClass().getResource("/image/ui/search.png"))); // NOI18N
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
    jScrollPane1.setViewportView(searchResultList);

    scrapperCb.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        scrapperCbActionPerformed(evt);
      }
    });

    GroupLayout searchPnlLayout = new GroupLayout(searchPnl);
    searchPnl.setLayout(searchPnlLayout);
    searchPnlLayout.setHorizontalGroup(
      searchPnlLayout.createParallelGroup(Alignment.LEADING)
      .addComponent(searchTb, GroupLayout.DEFAULT_SIZE, 796, Short.MAX_VALUE)
      .addGroup(searchPnlLayout.createSequentialGroup()
        .addComponent(searchField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(scrapperCb, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(searchBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
      .addComponent(jScrollPane1, Alignment.TRAILING)
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
        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE))
    );

    searchField.setLeadingComponent(new JLabel(ImageUtils.getIconFromJar("ui/search.png")));

    mediaSp.setTopComponent(searchPnl);

    mediaSp.setContinuousLayout(true);

    listSp.setRightComponent(mediaSp);

    mediaFilePnl.setMargin(new Insets(10, 10, 10, 10));
    mediaFilePnl.setMinimumSize(new Dimension(60, 0));

    mediaFileTb.setFloatable(false);
    mediaFileTb.setRollover(true);
    mediaFileTb.setMargin(new Insets(3, 3, 3, 3));
    mediaFileTb.setRound(5);

    mediaLbl.setText(LocaleUtils.i18nExt("media")); // NOI18N
    mediaLbl.setFont(new Font("Ubuntu", 1, 14)); // NOI18N
    mediaFileTb.add(mediaLbl);

    mediaFileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    mediaFileScp.setViewportView(mediaFileList);

    GroupLayout mediaFilePnlLayout = new GroupLayout(mediaFilePnl);
    mediaFilePnl.setLayout(mediaFilePnlLayout);
    mediaFilePnlLayout.setHorizontalGroup(
      mediaFilePnlLayout.createParallelGroup(Alignment.LEADING)
      .addComponent(mediaFileTb, GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
      .addComponent(mediaFileScp, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
    );
    mediaFilePnlLayout.setVerticalGroup(
      mediaFilePnlLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(mediaFilePnlLayout.createSequentialGroup()
        .addComponent(mediaFileTb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(mediaFileScp, GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE))
    );

    listSp.setLeftComponent(mediaFilePnl);

    listSp.setContinuousLayout(true);

    getContentPane().add(listSp, BorderLayout.CENTER);

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
          UISettings.LOGGER.log(Level.SEVERE, "Failed to save current folder path");// FIXME i18n
        }
      }

      loadFiles(files);
    }
  }//GEN-LAST:event_openBtnActionPerformed

  private void movieModeBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_movieModeBtnActionPerformed
    currentMode = MovieRenamerMode.MOVIEMODE;
    updateMediaPanel();
  }//GEN-LAST:event_movieModeBtnActionPerformed

  private void tvShowModeBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_tvShowModeBtnActionPerformed
    currentMode = MovieRenamerMode.TVSHOWMODE;
    updateMediaPanel();
  }//GEN-LAST:event_tvShowModeBtnActionPerformed

  private void exitBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_exitBtnActionPerformed
    System.exit(0);
  }//GEN-LAST:event_exitBtnActionPerformed

  private void settingBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_settingBtnActionPerformed

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        SettingPanel pnl = SettingPanel.getInstance(MovieRenamer.this);
        pnl.setLocationRelativeTo(MovieRenamer.this);
        pnl.setVisible(true);
      }
    });  }//GEN-LAST:event_settingBtnActionPerformed

  private void updateBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_updateBtnActionPerformed
    checkUpdate(true);
  }//GEN-LAST:event_updateBtnActionPerformed

  private void helpBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_helpBtnActionPerformed
    TooltipManager.showOneTimeTooltip(mediaFileList, new Point(mediaFileList.getWidth() / 2, mediaFileList.getHeight() / 2), "Media list help", TooltipWay.up);
    TooltipManager.showOneTimeTooltip(searchResultList, new Point(searchResultList.getWidth() / 2, searchResultList.getHeight() / 2), "searchResultList list help", TooltipWay.up);
    TooltipManager.showOneTimeTooltip(openBtn, new Point(openBtn.getWidth() / 2, openBtn.getHeight()), openTooltipLbl, TooltipWay.down);
    TooltipManager.showOneTimeTooltip(fileFormatField, new Point(fileFormatField.getWidth() / 2, fileFormatField.getHeight()), "Change filename on the fly", TooltipWay.down);
  }//GEN-LAST:event_helpBtnActionPerformed

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
  }//GEN-LAST:event_fileFormatFieldKeyReleased

  private void scrapperCbActionPerformed(ActionEvent evt) {//GEN-FIRST:event_scrapperCbActionPerformed
    switch (currentMode) {
      case MOVIEMODE:
        UIMovieScrapper = new UIScrapper(((UIScrapper) scrapperCb.getModel().getSelectedItem()).getScrapper());
        break;
      case TVSHOWMODE:
        UITvShowScrapper = new UIScrapper(((UIScrapper) scrapperCb.getSelectedItem()).getScrapper());
        break;
    }
  }//GEN-LAST:event_scrapperCbActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private WebButton exitBtn;
  private WebCheckBox fanartChk;
  private WebTextField fileFormatField;
  private WebLabel fileFormatLbl;
  private WebButton helpBtn;
  private JScrollPane jScrollPane1;
  private WebSplitPane listSp;
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
  private JLabel openTooltipLbl;
  private WebButton renameBtn;
  private JTextField renameField;
  private WebToolBar renameTb;
  private JComboBox scrapperCb;
  private WebButton searchBtn;
  private WebTextField searchField;
  private WebLabel searchLbl;
  private WebPanel searchPnl;
  private WebList searchResultList;
  private WebToolBar searchTb;
  private WebButton settingBtn;
  private WebCheckBox thumbChk;
  private WebButton tvShowModeBtn;
  private WebButton updateBtn;
  // End of variables declaration//GEN-END:variables
}
