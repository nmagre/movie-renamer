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

import com.alee.extended.filechooser.FilesToChoose;
import com.alee.extended.filechooser.SelectionMode;
import com.alee.extended.filechooser.WebFileChooser;
import com.alee.extended.image.transition.TransitionEffect;
import com.alee.extended.layout.ToolbarLayout;
import com.alee.extended.panel.TransitionPanel;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.scrapper.MediaScrapper;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.scrapper.TvShowScrapper;
import fr.free.movierenamer.scrapper.impl.ScrapperManager;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.ui.res.IconListRenderer;
import fr.free.movierenamer.ui.res.UIFile;
import fr.free.movierenamer.ui.res.UIScrapper;
import fr.free.movierenamer.ui.res.UISearchResult;
import fr.free.movierenamer.ui.settings.Settings;
import fr.free.movierenamer.ui.utils.FileFilter;
import fr.free.movierenamer.ui.utils.Loading;
import fr.free.movierenamer.ui.utils.MediaRenamed;
import fr.free.movierenamer.ui.worker.ListFilesWorker;
import fr.free.movierenamer.ui.worker.SearchMediaInfoWorker;
import fr.free.movierenamer.ui.worker.SearchMediaWorker;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.LocaleUtils;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Class MovieRenamer
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class MovieRenamer extends javax.swing.JFrame {

  private static final long serialVersionUID = 1L;
  // File chooser
  private final WebFileChooser fileChooser;
  // loading
  private LoadingDialog loading;

//  private List<MediaFile> mediaFile;
 // private ContextMenuListMouseListener contex;
  private List<MediaRenamed> renamedMediaFile;
  // Current variables
  private MovieRenamerMode currentMode;
//  private Media<? extends MediaData> currentMedia;
  // Worker
/*  private MediaSearchWorker searchWorker;
  private MovieInfoWorker movieInfoWorker;
  private TvShowInfoWorker tvShowInfoWorker;*/
  // Property change
  private final PropertyChangeSupport errorSupport;
  private PropertyChangeSupport settingsChange;
  // Media Panel
  //private MoviePanel moviePnl;
  private TvShowPanel tvShowPanel;

  // Media Panel container
  private final TransitionPanel containerTransitionMediaPanel;
  // Media Panels
  private final MoviePanel moviePnl = new MoviePanel(this);
  // private final TvShowPanel tvShowPanel = new TvShowPanel(this);
 
  // UI tools
  public static final Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
  public static final Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);

  /**
   * Creates new form MovieRenamer
   */
  public MovieRenamer() {
    initComponents();
    
    Cache.clearAllCache();//FIXME remove !!!

    // file chooser init
    fileChooser = new WebFileChooser(this, "");// FIXME add title !
    fileChooser.setFilesToChoose(FilesToChoose.all);
    fileChooser.setSelectionMode(SelectionMode.MULTIPLE_SELECTION);
    FileFilter ff = new FileFilter();
    fileChooser.setPreviewFilter(ff);
    fileChooser.setChooseFilter(ff);

    // UI list listeners
    mediaList.addMouseListener(createMediaListListener());
    searchResultList.addListSelectionListener(createSearchResultListListener());

    // Create media panel container and set transition effect
    containerTransitionMediaPanel = new TransitionPanel();
    containerTransitionMediaPanel.setTransitionEffect(TransitionEffect.fade);
    containerTransitionMediaPanel.add(moviePnl);
    // containerTransitionMediaPanel.add(tvShowPanel);
    
    //lang
    Locale current = Locale.getDefault();
    DefaultComboBoxModel langModel = new DefaultComboBoxModel();
    for (Locale locale : LocaleUtils.getAvailableLanguages()) {
      langModel.addElement(locale.getDisplayLanguage(current));
    }
    langCb.setRenderer(new DefaultListCellRenderer());
    langCb.setModel(langModel);
    langCb.setSelectedItem(current.getDisplayLanguage(current));
    if(langCb.getSelectedIndex() == -1){
      langCb.setSelectedItem(Locale.ENGLISH.getDisplayLanguage(current));
    }
    

    // Create dummy property change support for close loading dialog on error
    errorSupport = new PropertyChangeSupport(new Object());
    errorSupport.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(LoadingDialog.closeEvent)) {
          if (loading != null && loading.isShowing()) {
            //loading.dispose();
          }
        }
      }
    });

    // Add media panel container to media split pane
    searchSp.setBottomComponent(containerTransitionMediaPanel);

    // Add button to menu toolbar on right
    menuTb.addToEnd(helpBtn);
    menuTb.addToEnd(new JSeparator(JSeparator.VERTICAL));
    menuTb.addToEnd(updateBtn);
    menuTb.addToEnd(settingBtn);
    menuTb.addToEnd(exitBtn);

    // add items to renameTb
    renameTb.add(fileFormatField, ToolbarLayout.START);
    renameTb.add(renameField, ToolbarLayout.FILL);
    renameTb.add(renameBtn, ToolbarLayout.END);
    renameTb.add(thumbChk, ToolbarLayout.END);
    renameTb.add(fanartChk, ToolbarLayout.END);
    renameTb.add(nfoChk, ToolbarLayout.END);

    // finally clear all ;)
    movieModeBtn.doClick();

    if (Settings.getInstance().checkUpdate) {
      updateBtn.doClick();
    }
  }

  public UIFile getSelectedMediaFile() {
    UIFile current = null;
    if (mediaList != null) {
      Object obj = mediaList.getSelectedValue();
      if (obj != null) {
        if (obj instanceof UIFile) {
          current = (UIFile) obj;
          mediaList.ensureIndexIsVisible(mediaList.getSelectedIndex());
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
      switch (currentMode) {
      case MOVIEMODE:
      case TVSHOWMODE:
        containerTransitionMediaPanel.switchContent(moviePnl);
        break;
      }
    }
  }

  private IMediaPanel getCurrentMediaPanel() {
    IMediaPanel current = null;
    if (containerTransitionMediaPanel != null) {
      Component compo = containerTransitionMediaPanel.getContent();
      if (compo != null) {
        if (compo instanceof IMediaPanel) {
          current = (IMediaPanel) compo;
        }
      }
    }
    return current;
  }

  public void updateRenamedTitle() {
    MediaInfo currentMedia = getCurrentMediaPanel().getMediaInfo();
    if (currentMedia != null) {
      renameField.setText(currentMedia.getRenamedTitle(fileFormatField.getText()));
    } else {
      renameField.setText(null);
    }
  }

  /**
   * Clear Movie Renamer interface
   * 
   * @param clearMediaList Clear media list
   * @param clearSearchResultList Clear search list
   */
  private void clearInterface(boolean clearMediaList, boolean clearSearchResultList) {
    if (clearMediaList) {
      this.mediaList.removeAll();
    }

    if (clearSearchResultList) {
      this.searchResultList.removeAll();
    }

    getCurrentMediaPanel().clear();
    updateRenamedTitle();
  }

  public PropertyChangeSupport getErrorSupport() {
    return errorSupport;
  }

  /**
   * Create media list mouse listener
   * 
   * @return Mouse listener
   */
  private MouseListener createMediaListListener() {
    return new MouseListener() {

      @Override
      public void mouseClicked(MouseEvent e) {
      }

      @Override
      public void mousePressed(MouseEvent e) {
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        int index = mediaList.locationToIndex(e.getPoint());

        // no index
        if (index == -1) {
          return;
        }

        clearInterface(false, true);

        // No media selected
        if (!mediaList.getCellBounds(index, index).contains(e.getPoint())) {
          mediaList.removeSelectionInterval(index, index);
          return;
        }

        UIFile mediaFile = getSelectedMediaFile();

        searchField.setText(mediaFile.getSearch());

        if (Settings.getInstance().autoSearchMedia) {
          searchBtn.doClick();
        }

        renameField.setEnabled(true);
      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent e) {
      }
    };
  }

  /**
   * Create search result list selection listener
   * 
   * @return
   */
  private ListSelectionListener createSearchResultListListener() {
    return new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent evt) {
        // UIFile currentMedia = getSelectedMediaFile();
        if (searchResultList.getSelectedIndex() == -1) {
          return;
        }

        if (evt.getValueIsAdjusting()) {
          return;
        }

        // Show loading dialog if auto select first result is not enabled
        if (!loading.isShown()) {
          loadDial(false, true, false, false);
        }

        clearInterface(false, false);

        UISearchResult sres = getSelectedSearchResult();
        UIFile sfile = getSelectedMediaFile();

        SearchMediaInfoWorker worker = new SearchMediaInfoWorker(errorSupport, MovieRenamer.this, getCurrentMediaPanel(), sfile, sres);
        worker.execute();
      }
    };
  }

  /**
   * Display loading dialog
   */
  private void loadFilesDial() {
    List<Loading> loadings = new ArrayList<Loading>();
    loadings.add(new Loading(LoadingDialog.LoadingDialogPos.files, true));
    loading = new LoadingDialog(this, loadings);
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        loading.setVisible(true);
      }
    });
  }

  void loadDial(boolean withSearch, boolean withMedia, boolean withPerson, boolean withSubs) {
    Settings conf = Settings.getInstance();
    List<Loading> loadings = new ArrayList<Loading>();
    if (withSearch) {
      loadings.add(new Loading(LoadingDialog.LoadingDialogPos.search, true));
    }
    if (withMedia) {
      if (conf.selectFrstRes) {
        loadings.add(new Loading(LoadingDialog.LoadingDialogPos.inf, true));
        if (conf.movieInfoPanel && conf.thumb) {
          loadings.add(new Loading(LoadingDialog.LoadingDialogPos.images, false));
        }
        if (conf.movieInfoPanel && conf.actorImage) {
          loadings.add(new Loading(LoadingDialog.LoadingDialogPos.casting, false));
        }
      }
    }
    if (withPerson) {
      loadings.add(new Loading(LoadingDialog.LoadingDialogPos.person, true));
    }
    if (withSubs) {
      if (conf.movieInfoPanel && conf.subtitles) {
        loadings.add(new Loading(LoadingDialog.LoadingDialogPos.subtitles, false));
      }
    }
    loading = new LoadingDialog(this, loadings);
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        loading.setVisible(true);
      }
    });
  }

  public LoadingDialog getLoading() {
    return loading;
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        updateBtn = new com.alee.laf.button.WebButton();
        settingBtn = new com.alee.laf.button.WebButton();
        helpBtn = new com.alee.laf.button.WebButton();
        exitBtn = new com.alee.laf.button.WebButton();
        fileFormatField = new com.alee.laf.text.WebTextField();
        renameField = new javax.swing.JTextField();
        renameBtn = new com.alee.laf.button.WebButton();
        thumbChk = new com.alee.laf.checkbox.WebCheckBox();
        fanartChk = new com.alee.laf.checkbox.WebCheckBox();
        nfoChk = new com.alee.laf.checkbox.WebCheckBox();
        menuTb = new com.alee.laf.toolbar.WebToolBar();
        openBtn = new com.alee.laf.button.WebButton();
        separator1 = new javax.swing.JToolBar.Separator();
        movieModeBtn = new com.alee.laf.button.WebButton();
        tvShowModeBtn = new com.alee.laf.button.WebButton();
        mainPnl = new com.alee.laf.panel.WebPanel();
        centerSp = new javax.swing.JSplitPane();
        mediaListPnl = new com.alee.laf.panel.WebPanel();
        mediScroll = new javax.swing.JScrollPane();
        mediaList = new com.alee.laf.list.WebList();
        mediaTb = new com.alee.laf.toolbar.WebToolBar();
        mediaLbl = new com.alee.laf.label.WebLabel();
        searchSp = new javax.swing.JSplitPane();
        searchPnl = new com.alee.laf.panel.WebPanel();
        searchTb = new com.alee.laf.toolbar.WebToolBar();
        searchLbl = new com.alee.laf.label.WebLabel();
        searchField = new com.alee.laf.text.WebTextField();
        scrapperCb = new com.alee.laf.combobox.WebComboBox();
        langCb = new com.alee.laf.combobox.WebComboBox();
        searchBtn = new com.alee.laf.button.WebButton();
        searchScroll = new javax.swing.JScrollPane();
        searchResultList = new com.alee.laf.list.WebList();
        renameTb = new com.alee.laf.toolbar.WebToolBar();

        updateBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/ui/system-software-update-5.png"))); // NOI18N
        updateBtn.setFocusable(false);
        updateBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        updateBtn.setRolloverDarkBorderOnly(true);
        updateBtn.setRolloverDecoratedOnly(true);
        updateBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        updateBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateBtnActionPerformed(evt);
            }
        });

        settingBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/ui/system-settings.png"))); // NOI18N
        settingBtn.setFocusable(false);
        settingBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        settingBtn.setRolloverDarkBorderOnly(true);
        settingBtn.setRolloverDecoratedOnly(true);
        settingBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        settingBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingBtnActionPerformed(evt);
            }
        });

        helpBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/ui/system-help-3.png"))); // NOI18N
        helpBtn.setFocusable(false);
        helpBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        helpBtn.setRolloverDarkBorderOnly(true);
        helpBtn.setRolloverDecoratedOnly(true);
        helpBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        helpBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpBtnActionPerformed(evt);
            }
        });

        exitBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/ui/application-exit.png"))); // NOI18N
        exitBtn.setFocusable(false);
        exitBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exitBtn.setRolloverDarkBorderOnly(true);
        exitBtn.setRolloverDecoratedOnly(true);
        exitBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        exitBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitBtnActionPerformed(evt);
            }
        });

        fileFormatField.setPreferredSize(new java.awt.Dimension(250, 27));
        fileFormatField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fileFormatFieldKeyReleased(evt);
            }
        });

        renameField.setEnabled(false);

        renameBtn.setText(LocaleUtils.i18n("rename")); // NOI18N
        renameBtn.setEnabled(false);
        renameBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameBtnActionPerformed(evt);
            }
        });

        thumbChk.setText(LocaleUtils.i18n("thumb")); // NOI18N
        thumbChk.setFocusable(false);
        thumbChk.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        thumbChk.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        fanartChk.setText(LocaleUtils.i18n("fanart")); // NOI18N
        fanartChk.setFocusable(false);
        fanartChk.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fanartChk.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        nfoChk.setText(LocaleUtils.i18n("nfoXbmc")); // NOI18N
        nfoChk.setFocusable(false);
        nfoChk.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nfoChk.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        menuTb.setFloatable(false);
        menuTb.setRollover(true);
        menuTb.setRound(10);

        openBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/ui/folder-video.png"))); // NOI18N
        openBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openBtn.setRolloverDarkBorderOnly(true);
        openBtn.setRolloverDecoratedOnly(true);
        openBtn.setRolloverShadeOnly(true);
        openBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openBtnActionPerformed(evt);
            }
        });
        menuTb.add(openBtn);
        menuTb.add(separator1);

        movieModeBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/ui/movie.png"))); // NOI18N
        movieModeBtn.setFocusable(false);
        movieModeBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        movieModeBtn.setRolloverDarkBorderOnly(true);
        movieModeBtn.setRolloverDecoratedOnly(true);
        movieModeBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        movieModeBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                movieModeBtnActionPerformed(evt);
            }
        });
        menuTb.add(movieModeBtn);

        tvShowModeBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/ui/tv.png"))); // NOI18N
        tvShowModeBtn.setFocusable(false);
        tvShowModeBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tvShowModeBtn.setRolloverDarkBorderOnly(true);
        tvShowModeBtn.setRolloverDecoratedOnly(true);
        tvShowModeBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tvShowModeBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tvShowModeBtnActionPerformed(evt);
            }
        });
        menuTb.add(tvShowModeBtn);

        getContentPane().add(menuTb, java.awt.BorderLayout.PAGE_START);

        mainPnl.setMargin(new java.awt.Insets(1, 1, 1, 1));
        mainPnl.setShadeWidth(2);

        centerSp.setDividerLocation(300);

        mediaListPnl.setMargin(new java.awt.Insets(10, 10, 10, 10));
        mediaListPnl.setMinimumSize(new java.awt.Dimension(60, 0));

        mediaList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        mediScroll.setViewportView(mediaList);

        mediaTb.setFloatable(false);
        mediaTb.setRollover(true);
        mediaTb.setRound(0);

        mediaLbl.setText(LocaleUtils.i18n("medias")); // NOI18N
        mediaLbl.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        mediaTb.add(mediaLbl);

        javax.swing.GroupLayout mediaListPnlLayout = new javax.swing.GroupLayout(mediaListPnl);
        mediaListPnl.setLayout(mediaListPnlLayout);
        mediaListPnlLayout.setHorizontalGroup(
            mediaListPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mediScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
            .addComponent(mediaTb, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
        );
        mediaListPnlLayout.setVerticalGroup(
            mediaListPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mediaListPnlLayout.createSequentialGroup()
                .addComponent(mediaTb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mediScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 792, Short.MAX_VALUE))
        );

        centerSp.setLeftComponent(mediaListPnl);

        searchSp.setDividerLocation(170);
        searchSp.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        searchPnl.setMargin(new java.awt.Insets(10, 10, 10, 10));

        searchTb.setFloatable(false);
        searchTb.setRollover(true);
        searchTb.setRound(0);

        searchLbl.setText(LocaleUtils.i18n("search")); // NOI18N
        searchLbl.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        searchTb.add(searchLbl);

        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchFieldKeyReleased(evt);
            }
        });

        scrapperCb.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scrapperCbActionPerformed(evt);
            }
        });

        searchBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/ui/search.png"))); // NOI18N
        searchBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBtnActionPerformed(evt);
            }
        });

        searchResultList.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        searchScroll.setViewportView(searchResultList);

        javax.swing.GroupLayout searchPnlLayout = new javax.swing.GroupLayout(searchPnl);
        searchPnl.setLayout(searchPnlLayout);
        searchPnlLayout.setHorizontalGroup(
            searchPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(searchTb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(searchPnlLayout.createSequentialGroup()
                .addComponent(searchField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrapperCb, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(langCb, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(searchScroll, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 989, Short.MAX_VALUE)
        );
        searchPnlLayout.setVerticalGroup(
            searchPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPnlLayout.createSequentialGroup()
                .addComponent(searchTb, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(searchPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(searchPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(scrapperCb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(langCb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(searchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))
        );

        searchField.setLeadingComponent(new JLabel(new javax.swing.ImageIcon(getClass().getResource("/image/ui/search.png"))));

        searchSp.setLeftComponent(searchPnl);

        centerSp.setRightComponent(searchSp);

        javax.swing.GroupLayout mainPnlLayout = new javax.swing.GroupLayout(mainPnl);
        mainPnl.setLayout(mainPnlLayout);
        mainPnlLayout.setHorizontalGroup(
            mainPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(centerSp)
        );
        mainPnlLayout.setVerticalGroup(
            mainPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(centerSp)
        );

        getContentPane().add(mainPnl, java.awt.BorderLayout.CENTER);

        renameTb.setFloatable(false);
        renameTb.setRollover(true);
        renameTb.setRound(10);
        getContentPane().add(renameTb, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

  private void renameBtnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_renameBtnActionPerformed
    int index = mediaList.getSelectedIndex();
    if (index == -1) {
      JOptionPane.showMessageDialog(MovieRenamer.this, LocaleUtils.i18n("noMovieSelected"), LocaleUtils.i18n("error"), JOptionPane.ERROR_MESSAGE);
      return;
    }

  }// GEN-LAST:event_renameBtnActionPerformed

  private void fileFormatFieldKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_fileFormatFieldKeyReleased
    updateRenamedTitle();
  }// GEN-LAST:event_fileFormatFieldKeyReleased

  private void movieModeBtnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_movieModeBtnActionPerformed
    currentMode = MovieRenamerMode.MOVIEMODE;
    movieModeBtn.setEnabled(false);
    tvShowModeBtn.setEnabled(true);
    
    DefaultComboBoxModel scrapperNameModel = new DefaultComboBoxModel();
    for (MovieScrapper scrapper : ScrapperManager.getMovieScrapperList()) {
      scrapperNameModel.addElement(new UIScrapper(scrapper)); 
    }
    scrapperCb.setRenderer(new IconListRenderer<UIScrapper>());
    scrapperCb.setModel(scrapperNameModel);
    scrapperCb.setSelectedItem(new UIScrapper(ScrapperManager.getScrapper(Settings.getInstance().movieScrapper)));
    
    clearInterface(false, true);
    
    fileFormatField.setText(Settings.getInstance().movieFilenameFormat);
  }// GEN-LAST:event_movieModeBtnActionPerformed

  private void tvShowModeBtnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tvShowModeBtnActionPerformed
    currentMode = MovieRenamerMode.TVSHOWMODE;
    movieModeBtn.setEnabled(true);
    tvShowModeBtn.setEnabled(false);
    DefaultComboBoxModel scrapperNameModel = new DefaultComboBoxModel();
    for (TvShowScrapper scrapper : ScrapperManager.getTvShowScrapperList()) {
      scrapperNameModel.addElement(new UIScrapper(scrapper));
    }
    scrapperCb.setRenderer(new IconListRenderer<UIScrapper>());
    scrapperCb.setModel(scrapperNameModel);
    scrapperCb.setSelectedItem(new UIScrapper(ScrapperManager.getScrapper(Settings.getInstance().tvshowScrapper)));
    clearInterface(false, true);
    fileFormatField.setText(Settings.getInstance().tvShowFilenameFormat);
  }// GEN-LAST:event_tvShowModeBtnActionPerformed

  private void updateBtnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_updateBtnActionPerformed
    // TODO
    // String ver = setting.getVersion();
    // if (!ver.equals("")) {
    // try {
    // URL url = new URL("http://movierenamer.free.fr/update.php?version=" + ver.substring(0, setting.getVersion().lastIndexOf("_")) + "&amp;lang=" + setting.locale);
    // HttpGet http = new HttpGet(url);
    // String newVerFile = http.sendGetRequest(false, "UTF-8");
    // if (newVerFile.equals("")) {
    // if (showAlready) {
    // JOptionPane.showMessageDialog(this, Utils.i18n("alreadyUpToDate"), Utils.i18n("update"), JOptionPane.INFORMATION_MESSAGE);
    // }
    // return;
    // }
    // String newVer = newVerFile.substring(newVerFile.lastIndexOf("-") + 1, newVerFile.lastIndexOf("."));
    // URL urlHist = new URL("http://movierenamer.free.fr/update.php?getHistory&lang=" + setting.locale);
    // http.setUrl(urlHist);
    // String history = http.sendGetRequest(false, "UTF-8");
    // File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    // int n = JOptionPane.showConfirmDialog(this, Utils.ENDLINE + Utils.i18n("newVersionAvailable")
    // + Utils.SPACE + newVer + Utils.ENDLINE + Utils.i18n("updateMr") + Utils.SPACE + ver
    // + Utils.SPACE + Utils.i18n("to") + Utils.SPACE
    // + newVer + " ?\n\n" + history, "Question", JOptionPane.YES_NO_OPTION);
    // if (n == 0) {
    // url = new URL("http://movierenamer.free.fr/" + newVerFile.replaceAll(" ", "%20"));
    //
    // Utils.downloadFile(url, jarFile.getAbsolutePath());
    //
    // n = JOptionPane.showConfirmDialog(this, Settings.APPNAME + Utils.SPACE + Utils.i18n("wantRestartAppUpdate"), "Question", JOptionPane.YES_NO_OPTION);
    // if (n == JOptionPane.YES_OPTION) {
    // if (!Utils.restartApplication(jarFile)) {
    // JOptionPane.showMessageDialog(this, Utils.i18n("cantRestart"), Utils.i18n("error"), JOptionPane.ERROR_MESSAGE);
    // } else {
    // dispose();
    // System.exit(0);
    // }
    // }
    // }
    // } catch (Exception e) {
    // JOptionPane.showMessageDialog(this, Utils.i18n("checkUpdateFailed") + Utils.ENDLINE + e.getMessage(), Utils.i18n("error"), JOptionPane.ERROR_MESSAGE);
    // }
    // }
  }// GEN-LAST:event_updateBtnActionPerformed

  private void settingBtnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_settingBtnActionPerformed
    // TODO
    // final Setting set = new Setting(setting, settingsChange, this);
    // java.awt.EventQueue.invokeLater(new Runnable() {
    //
    // @Override
    // public void run() {
    // set.setVisible(true);
    // }
  }// GEN-LAST:event_settingBtnActionPerformed

  private void helpBtnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_helpBtnActionPerformed
    // TODO
    // TooltipManager.showOneTimeTooltip(mediaList, new Point(mediaList.getWidth() / 2, mediaList.getHeight() / 2), "Media list help", TooltipWay.up);
    // TooltipManager.showOneTimeTooltip(searchResultList, new Point(searchResultList.getWidth() / 2, searchResultList.getHeight() / 2), "searchResultList list help", TooltipWay.up);
    // TooltipManager.showOneTimeTooltip(openBtn, new Point(openBtn.getWidth() / 2, openBtn.getHeight()), openTooltipLbl, TooltipWay.down);
    // TooltipManager.showOneTimeTooltip(fileFormatField, new Point(fileFormatField.getWidth() / 2, fileFormatField.getHeight()), "Change filename on the fly", TooltipWay.down);
  }// GEN-LAST:event_helpBtnActionPerformed

  private void exitBtnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_exitBtnActionPerformed
    System.exit(0);
  }// GEN-LAST:event_exitBtnActionPerformed

  private void scrapperCbActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_scrapperCbActionPerformed
    clearInterface(false, false);
  }// GEN-LAST:event_scrapperCbActionPerformed

  private void openBtnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_openBtnActionPerformed
    Settings settings = Settings.getInstance();
    fileChooser.setCurrentDirectory(new File(settings.fileChooserPath));
    int r = fileChooser.showDialog();
    if (r == 0) {
      clearInterface(true, true);
      List<File> files = fileChooser.getSelectedFiles();
      if (!files.isEmpty()) {// Remember path
        settings.fileChooserPath = files.get(0).getParent();
        try {
          settings.saveSetting();
        } catch (IOException e) {
          Settings.LOGGER.log(Level.SEVERE, "Failed to save current folder path");
        }
      }

      loadFilesDial();

      ListFilesWorker lfw = new ListFilesWorker(errorSupport, this, files, mediaList, null);
      // FileWorkerListener listener = new FileWorkerListener(this, mediaList, lfw);
      // lfw.addPropertyChangeListener(listener);
      lfw.execute();
    }
  }// GEN-LAST:event_openBtnActionPerformed

//<<<<<<< HEAD //TODO ??? what is that ????
//    @Override
//    @SuppressWarnings("unchecked")
//    public void propertyChange(PropertyChangeEvent evt) {
//
//      if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
//        try {
//          MovieInfo movieInfo = worker.get();
//          if (movieInfo == null) {
//            loading.setValue(100, WorkerManager.WORKERID.INFOWORKER);
//            return;
//          }
//
//          if (setting.movieInfoPanel) {
//            if (setting.thumb) {
//              ImageWorker thumbWorker = WorkerManager.getMediaImageWorker(movieInfo.getThumbs(), MediaImage.MediaImageSize.THUMB, Cache.CacheType.THUMB, moviePnl);
//              thumbWorker.addPropertyChangeListener(new workerListener(thumbWorker, WorkerManager.WORKERID.THUMBWORKER));
//              thumbWorker.execute();
//            }
//            if (setting.fanart) {
//              ImageWorker fanartWorker = WorkerManager.getMediaImageWorker(movieInfo.getFanarts(), MediaImage.MediaImageSize.THUMB, Cache.CacheType.FANART, moviePnl);
//              fanartWorker.addPropertyChangeListener(new workerListener(fanartWorker, WorkerManager.WORKERID.FANARTWORKER));
//              fanartWorker.execute();
//            }
//          }
//
//          ((Media<MovieInfo>) currentMedia).setInfo(movieInfo);
//          moviePnl.addMovieInfo((Movie) currentMedia);
//
//          renameField.setText(currentMedia.getRenamedTitle(fileFormatField.getText()));
//          renameBtn.setEnabled(true);
//          renameField.setEnabled(true);
//          editBtn.setEnabled(true);
//
//          ActorWorker actor = WorkerManager.getMovieActorWorker(movieInfo.getActors(), moviePnl);
//          actor.addPropertyChangeListener(new workerListener(actor, WorkerManager.WORKERID.ACTORWORKER));
//          actor.execute();
//          loading.setValue(100, WorkerManager.WORKERID.INFOWORKER);
//
//        } catch (InterruptedException ex) {
//          Settings.LOGGER.log(Level.SEVERE, null, ex);
//        } catch (ExecutionException ex) {
//          Settings.LOGGER.log(Level.SEVERE, null, ex);
//        } catch (NullPointerException ex) {
//          Settings.LOGGER.log(Level.SEVERE, Utils.getStackTrace("NullPointerException", ex.getStackTrace()));
//          // TODO display error
//        }
//      } else {
//        loading.setValue(worker.getProgress(), WorkerManager.WORKERID.INFOWORKER);
//      }
//=======
  private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_searchBtnActionPerformed
    if (searchField.getText().length() == 0) {
      JOptionPane.showMessageDialog(MovieRenamer.this, LocaleUtils.i18n("noTextToSearch"), LocaleUtils.i18n("error"), JOptionPane.ERROR_MESSAGE);
      return;
    }
    clearInterface(false, true);
    UIFile currentMedia = getSelectedMediaFile();
    if (currentMedia == null) {
      currentMedia = new UIFile(null, null, false);
    }
    currentMedia.setSearch(searchField.getText());

//<<<<<<< HEAD
//    @Override
//    @SuppressWarnings("unchecked")
//    public void propertyChange(PropertyChangeEvent evt) {
//      if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
//        try {
//          TvShowInfo seasons = worker.get();
//          if (seasons == null) {
//            System.out.println("Season is null");
//            loading.setValue(100, WorkerManager.WORKERID.INFOWORKER);
//            return;
//          }
//
//          ((Media<TvShowInfo>) currentMedia).setInfo(seasons);
//          tvShowPanel.addTvshowInfo((TvShow) currentMedia);//seasons.getSeasons(), ((TvShow) currentMedia).getSearchSxe());
//          
//          renameField.setText(currentMedia.getRenamedTitle(fileFormatField.getText()));
//          renameBtn.setEnabled(true);
//          renameField.setEnabled(true);
//          editBtn.setEnabled(true);
//
//          ActorWorker actor = WorkerManager.getMovieActorWorker(seasons.getActors(), tvShowPanel);
//          actor.addPropertyChangeListener(new workerListener(actor, WorkerManager.WORKERID.ACTORWORKER));
//          actor.execute();
//          loading.setValue(100, WorkerManager.WORKERID.INFOWORKER);
//
//        } catch (InterruptedException ex) {
//          Settings.LOGGER.log(Level.SEVERE, null, ex);
//        } catch (ExecutionException ex) {
//          Settings.LOGGER.log(Level.SEVERE, null, ex);
//        }
//
//        loading.setValue(100, WorkerManager.WORKERID.INFOWORKER);
//      } else {
//        loading.setValue(worker.getProgress(), WorkerManager.WORKERID.INFOWORKER);
//      }
//    }
//  }

    loadDial(true, true, false, false);

    @SuppressWarnings("unchecked")
    MediaScrapper<? extends Media, ? extends MediaInfo> mediaScrapper = ((UIScrapper)scrapperCb.getSelectedItem()).getScrapper();
    SearchMediaWorker searchWorker = new SearchMediaWorker(errorSupport, this, currentMedia, searchResultList, mediaScrapper);
    // SearchMediaWorkerListener listener = new SearchMediaWorkerListener(this, searchResultList, searchWorker);
    // searchWorker.addPropertyChangeListener(listener);
    searchWorker.execute();

    // SubtitleScrapper subtitleScrapper=ScrapperManager.getSubtitleScrapper();
    // WebList subtitlesList = getCurrentMediaPanel().getSubtitlesList();
    // subtitlesList.removeAll();
    // SearchMediaSubtitlesWorker subtitlesWorker = new SearchMediaSubtitlesWorker(errorSupport, this, currentMedia, subtitlesList, subtitleScrapper);
    // subtitlesWorker.execute();
  }// GEN-LAST:event_searchBtnActionPerformed

  private void searchFieldKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_searchFieldKeyReleased
    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
      searchBtn.doClick();
    }
  }// GEN-LAST:event_searchFieldKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane centerSp;
    private com.alee.laf.button.WebButton exitBtn;
    private com.alee.laf.checkbox.WebCheckBox fanartChk;
    private com.alee.laf.text.WebTextField fileFormatField;
    private com.alee.laf.button.WebButton helpBtn;
    private com.alee.laf.combobox.WebComboBox langCb;
    private com.alee.laf.panel.WebPanel mainPnl;
    private javax.swing.JScrollPane mediScroll;
    private com.alee.laf.label.WebLabel mediaLbl;
    private com.alee.laf.list.WebList mediaList;
    private com.alee.laf.panel.WebPanel mediaListPnl;
    private com.alee.laf.toolbar.WebToolBar mediaTb;
    private com.alee.laf.toolbar.WebToolBar menuTb;
    private com.alee.laf.button.WebButton movieModeBtn;
    private com.alee.laf.checkbox.WebCheckBox nfoChk;
    private com.alee.laf.button.WebButton openBtn;
    private com.alee.laf.button.WebButton renameBtn;
    private javax.swing.JTextField renameField;
    private com.alee.laf.toolbar.WebToolBar renameTb;
    private com.alee.laf.combobox.WebComboBox scrapperCb;
    private com.alee.laf.button.WebButton searchBtn;
    private com.alee.laf.text.WebTextField searchField;
    private com.alee.laf.label.WebLabel searchLbl;
    private com.alee.laf.panel.WebPanel searchPnl;
    private javax.swing.JList searchResultList;
    private javax.swing.JScrollPane searchScroll;
    private javax.swing.JSplitPane searchSp;
    private com.alee.laf.toolbar.WebToolBar searchTb;
    private javax.swing.JToolBar.Separator separator1;
    private com.alee.laf.button.WebButton settingBtn;
    private com.alee.laf.checkbox.WebCheckBox thumbChk;
    private com.alee.laf.button.WebButton tvShowModeBtn;
    private com.alee.laf.button.WebButton updateBtn;
    // End of variables declaration//GEN-END:variables

}
