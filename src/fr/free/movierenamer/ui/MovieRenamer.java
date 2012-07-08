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

import fr.free.movierenamer.Main;
import fr.free.movierenamer.media.*;
import fr.free.movierenamer.media.movie.Movie;
import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.media.tvshow.TvShow;
import fr.free.movierenamer.media.tvshow.TvShowSeason;
import fr.free.movierenamer.parser.xml.MrRenamedMovie;
import fr.free.movierenamer.parser.xml.XMLParser;
import fr.free.movierenamer.ui.res.ContextMenuFieldMouseListener;
import fr.free.movierenamer.ui.res.ContextMenuListMouseListener;
import fr.free.movierenamer.ui.res.DropFile;
import fr.free.movierenamer.ui.res.IconListRenderer;
import fr.free.movierenamer.utils.*;
import fr.free.movierenamer.worker.ListFilesWorker;
import fr.free.movierenamer.worker.WorkerManager;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.dnd.DropTarget;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JToolBar.Separator;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Class MovieRenamer
 *
 * @author Nicolas Magré
 */
public class MovieRenamer extends JFrame {

  private Settings setting;
  private DefaultListModel mediaFileNameModel;
  private DefaultListModel searchResModel;
  private Media currentMedia;
  private ResourceBundle bundle = ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle");
  private final String sError = bundle.getString("error");
  private DropFile dropFile;
  private LoadingDialog loading;
  private final int SEARCHWORKER = 0;
  private final int INFOWORKER = 1;
  private final int THUMBWORKER = 2;
  private final int FANARTWORKER = 3;
  private final int ACTORWORKER = 4;
  private ArrayList<MediaFile> mediaFile;
  private ContextMenuListMouseListener contex;
  private ArrayList<MediaRenamed> renamedMediaFile;
  private MoviePanel moviePnl;
  //Movie Renamer mode
  private final MovieRenamerMode[] modes = new MovieRenamerMode[]{
    new MovieRenamerMode("Movie", MovieRenamerMode.MOVIEMODE, Media.MOVIE),
    new MovieRenamerMode("TvShow", MovieRenamerMode.TVSHOWMODE, Media.TVSHOW)
  };
  private MovieRenamerMode currentMode;
  private SwingWorker<MovieInfo, String> movieInfoWorker;
  private SwingWorker<ArrayList<SearchResult>, String> searchWorker;
  private SwingPropertyChangeSupport errorSupport;

  public MovieRenamer(Settings setting) {

    this.setting = setting;

    contex = new ContextMenuListMouseListener();
    contex.addPropertyChangeListener(contextMenuListener);

    initComponents();

    fileChooser.setFileFilter(new MediaFileFilter(setting));
    fileChooser.setAcceptAllFileFilterUsed(false);//Remove AcceptAll as an available choice in the choosable filter list

    mediaList.addListSelectionListener(mediaListSelectionListener);
    searchResultList.addListSelectionListener(searchresultListListener);

    moviePnl = new MoviePanel(setting, editActionListener);

    loadRenamedMovie();

    //Add drag and drop listener on mediaList
    dropFile = new DropFile(setting, renamedMediaFile, new FileWorkerListener(), MovieRenamer.this);
    DropTarget dt = new DropTarget(mediaList, dropFile);
    dt.setActive(true);

    currentMode = modes[MovieRenamerMode.MOVIEMODE];
    movieModeBtn.setEnabled(false);

    errorSupport = new SwingPropertyChangeSupport(new Object());
    errorSupport.addPropertyChangeListener(new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("closeLoadingDial")) {
          loading.dispose();
        }
      }
    });

    loadInterface();

    setIconImage(Utils.getImageFromJAR("/image/icon-32.png", getClass()));
    setTitle(Settings.APPNAME + "-" + setting.getVersion() + "  Movie Mode");
    setLocationRelativeTo(null);

    setVisible(true);

    if (setting.checkUpdate) {
      checkUpdate(false);
    }
  }
  //Right click context menu listener
  private PropertyChangeListener contextMenuListener = new PropertyChangeListener() {

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
      if (pce.getPropertyName().equals("remove")) {
        int index = (Integer) pce.getNewValue();
        if (index == -1) {
          return;
        }

        mediaFile.remove(index);
        mediaFileNameModel.remove(index);
        clearInterface(false, true);
      } else if (pce.getPropertyName().equals("search")) {
        if (currentMedia == null) {
          return;
        }
        searchMedia();
      }
    }
  };
  //MediaList selection listener
  private ListSelectionListener mediaListSelectionListener = new ListSelectionListener() {

    @Override
    public void valueChanged(ListSelectionEvent evt) {
      if (mediaList.getSelectedIndex() == -1) {
        return;
      }

      if (evt.getValueIsAdjusting()) {
        return;
      }

      MediaFile mediaFile = (MediaFile) mediaList.getSelectedValue();
      mediaList.ensureIndexIsVisible(mediaList.getSelectedIndex());

      //Check if media type is in current mode
      if (!checkMediaTypeInCurrentMode(mediaFile)) {
        return;
      }

      clearInterface(false, true);

      switch (mediaFile.getType()) {
        case Media.MOVIE:
          currentMedia = new Movie(mediaFile, MovieRenamer.this.setting.mediaNameFilters);
          break;
        case Media.TVSHOW:
          currentMedia = new TvShow(mediaFile, setting.mediaNameFilters);
          break;
        default:
          return;
      }

      currentMedia.setMediaFile(mediaFile);

      searchField.setText(currentMedia.getSearch());
      renameBtn.setEnabled(false);
      editBtn.setEnabled(false);
      renamedField.setText(Utils.EMPTY);
      renamedField.setEnabled(false);

      searchBtn.setEnabled(!MovieRenamer.this.setting.autoSearchMedia);
      searchField.setEnabled(!MovieRenamer.this.setting.autoSearchMedia);

      if (MovieRenamer.this.setting.autoSearchMedia) {
        searchMedia();
      }
    }
  };
  //Search list selection listener
  private ListSelectionListener searchresultListListener = new ListSelectionListener() {

    @Override
    public void valueChanged(ListSelectionEvent evt) {
      try {
        if (searchResultList.getSelectedIndex() == -1) {
          return;
        }

        if (evt.getValueIsAdjusting()) {
          return;
        }

        //Show loading dialog if auto select first result is not enabled 
        if (!loading.isShown()) {
          loadDial(false);
        }

        clearInterface(false, false);
        currentMedia.clear();

        SearchResult sres = (SearchResult) searchResultList.getSelectedValue();
        currentMedia.setMediaID(sres.getId());

        switch (currentMedia.getType()) {
          case Media.MOVIE:
            if (movieInfoWorker != null && !movieInfoWorker.isDone()) {
              return;
            }
            //Get movie info
            movieInfoWorker = WorkerManager.getMovieInfoWorker(errorSupport, sres.getId(), MovieRenamer.this.setting);
            if (movieInfoWorker == null) {
              //A faire , afficher erreur
              return;
            }
            movieInfoWorker.addPropertyChangeListener(new MovieInfoListener(movieInfoWorker));
            movieInfoWorker.execute();
            break;
          case Media.TVSHOW:
            SwingWorker<ArrayList<TvShowSeason>, String> tworker = WorkerManager.getTvShowInfoWorker(errorSupport, sres.getId(), ((TvShow) currentMedia).getSearchSxe(), MovieRenamer.this.setting);
            TvShowInfoListener tsil = new TvShowInfoListener(tworker);
            tworker.addPropertyChangeListener(tsil);
            tworker.execute();
            break;
        }
      } catch (ActionNotValidException ex) {
        Settings.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
  };
  //Edit button action listener
  private ActionListener editActionListener = new ActionListener() {

    @Override
    public void actionPerformed(ActionEvent evt) {
      editBtnActionPerformed(evt);
    }
  };

  /**
   * Set mouse icon loading or default
   *
   * @param loading Icon loading
   */
  public void setMouseIcon(boolean loading) {
    Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
    Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    setCursor(loading ? hourglassCursor : normalCursor);
  }

  private void loadRenamedMovie() {
    renamedMediaFile = new ArrayList<MediaRenamed>();
    if (new File(setting.renamedFile).exists()) {
      XMLParser<ArrayList<MediaRenamed>> mmp = new XMLParser<ArrayList<MediaRenamed>>(setting.renamedFile);
      mmp.setParser(new MrRenamedMovie());
      try {
        renamedMediaFile = mmp.parseXml();
      } catch (ParserConfigurationException ex) {
        Settings.LOGGER.log(Level.SEVERE, ex.toString());
      } catch (SAXException ex) {
        Settings.LOGGER.log(Level.SEVERE, Utils.getStackTrace("SAXException", ex.getStackTrace()));
      } catch (InterruptedException ex) {
        Settings.LOGGER.log(Level.SEVERE, ex.toString());
      } catch (IOException ex) {
        Settings.LOGGER.log(Level.SEVERE, ex.toString());
      }
    }
  }

  private void loadInterface() {//A refaire

    if (!setting.movieInfoPanel) {
      searchSp.remove(moviePnl);
      centerSp.remove(searchSp);
      centerSp.add(searchPnl);
      centerSp.setOrientation(JSplitPane.VERTICAL_SPLIT);
    } else {
      centerSp.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
      if (centerSp.getBottomComponent().equals(searchPnl)) {
        centerSp.remove(searchPnl);
        searchSp.add(searchPnl);
        centerSp.add(searchSp);
      }
      moviePnl.setDisplay(setting);
      searchSp.setBottomComponent(moviePnl);
      centerSp.setDividerLocation(300);
      searchSp.setDividerLocation(200);
    }

    thumbChk.setVisible(setting.movieInfoPanel && setting.thumb);
    fanartChk.setVisible(setting.movieInfoPanel && setting.fanart);

    nfoChk.setVisible(setting.movieInfoPanel);
    editBtn.setVisible(setting.movieInfoPanel);

    centerPnl.validate();
    centerPnl.repaint();
  }

  private void clearInterface(boolean movieList, boolean searchList) {//A refaire

    if (currentMedia != null) {
      currentMedia.clear();
    }

    if (movieList) {
      if (mediaFileNameModel != null) {
        mediaFileNameModel.clear();
      }
      ((TitledBorder) mediaScroll.getBorder()).setTitle(bundle.getString("movies"));
      mediaScroll.validate();
      mediaScroll.repaint();
    }

    if (searchList) {
      if (searchResModel != null) {
        searchResModel.clear();
      }
      resultLbl.setText(bundle.getString("searchResListTitle"));
      searchBtn.setEnabled(false);
      searchField.setEnabled(false);
      renamedField.setText("");
      renamedField.setEnabled(false);
      searchField.setText("");
    }
    moviePnl.clearList();
    renameBtn.setEnabled(false);
    editBtn.setEnabled(false);
  }

  /**
   * Check if media type is supported by current mode and ask user on what to do.
   *
   * @return True if current mode support media type, false otherwise
   */
  private boolean checkMediaTypeInCurrentMode(MediaFile mediaFile) {//A refaire , i18n

    if (mediaFile.getType() == currentMode.getMediaType()) {
      return true;
    }

    String[] choices = new String[]{"Change type", "Auto Change Mode", "Cancel"};
    int res = JOptionPane.showOptionDialog(MovieRenamer.this,
            "Movie Renamer think that " + mediaFile.getFile().getName() + " is not a " + currentMode.getTitle() + ".\n"
            + "If you are sure that media is a " + currentMode.getTitle() + " ,just click \"Change type\"\n"
            + "If not you can select \"Auto change Mode\" to change Movie Renamer mode automatically\nor \"Cancel\"",
            "Media type not correspond to mode type", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, choices, "");
    switch (res) {
      case 0:
        mediaFile.setType(currentMode.getMediaType());
        break;
      case 1://A faire , change mode
        int mode = currentMode.getMediaType();
        mode = mode == MovieRenamerMode.MOVIEMODE ? MovieRenamerMode.TVSHOWMODE : MovieRenamerMode.MOVIEMODE;
        currentMode = modes[mode];
        movieModeBtn.setEnabled(mode == MovieRenamerMode.TVSHOWMODE);
        tvShowModeBtn.setEnabled(mode == MovieRenamerMode.MOVIEMODE);
        break;
      case 2:
        return false;
      default:
    }
    return true;
  }

  /**
   * Search media on web
   */
  private void searchMedia() {
    if (searchWorker != null && !searchWorker.isDone()) {
      return;
    }
    loadDial(true);
    SearchWorkerListener sl = new SearchWorkerListener();
    searchWorker = WorkerManager.getSearchWorker(errorSupport, currentMedia, MovieRenamer.this.setting);
    if (searchWorker == null) {
      //A faire ajouter dialog erreur
      return;
    }
    searchWorker.addPropertyChangeListener(sl);
    sl.setWorker(searchWorker);
    searchWorker.execute();
  }

  /**
   * Display loading dialog
   *
   * @param search Add search to loading dialog
   */
  private void loadDial(boolean search) {//A refaire pour les série
    ArrayList<Loading> loadings = getLoading(search);
    loading = new LoadingDialog(loadings, this);
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        loading.setVisible(true);
      }
    });
  }

  /**
   * Get loading array
   *
   * @param search Add search to loading array
   * @return Array of loading
   */
  private ArrayList<Loading> getLoading(boolean search) {//A refaire , depend du mode
    ArrayList<Loading> loadings = new ArrayList<Loading>();

    if (search) {
      loadings.add(new Loading(bundle.getString("mediaSearch"), true, 100, SEARCHWORKER));
    }

    switch (currentMedia.getType()) {
      case Media.MOVIE:
        if (!search || (search && MovieRenamer.this.setting.selectFrstRes)) {
          loadings.add(new Loading(bundle.getString("movieInf"), true, 100, INFOWORKER));
          if (setting.movieInfoPanel && setting.thumb) {
            loadings.add(new Loading(bundle.getString("thumbnails"), false, 100, THUMBWORKER));
          }
          if (setting.movieInfoPanel && setting.fanart) {
            loadings.add(new Loading("Fanarts", false, 100, FANARTWORKER));
          }
          if (setting.movieInfoPanel && setting.actorImage) {
            loadings.add(new Loading(bundle.getString("actors"), false, 100, ACTORWORKER));
          }
        }
        break;
      case Media.TVSHOW://A faire
        break;
      default:
        break;
    }

    return loadings;
  }

  /**
   * Check for Movie Renamer update
   *
   * @param showAlready Show dialog
   */
  public final void checkUpdate(boolean showAlready) {
    String ver = setting.getVersion();
    if (!ver.equals("")) {
      try {
        URL url = new URL("http://movierenamer.free.fr/update.php?version=" + ver.substring(0, setting.getVersion().lastIndexOf("_")) + "&amp;lang=" + setting.locale);
        HttpGet http = new HttpGet(url);
        String newVerFile = http.sendGetRequest(false, "UTF-8");
        if (newVerFile.equals("")) {
          if (showAlready) {
            JOptionPane.showMessageDialog(this, bundle.getString("alreadyUpToDate"), bundle.getString("update"), JOptionPane.INFORMATION_MESSAGE);
          }
          return;
        }
        String newVer = newVerFile.substring(newVerFile.lastIndexOf("-") + 1, newVerFile.lastIndexOf("."));
        URL urlHist = new URL("http://movierenamer.free.fr/update.php?getHistory&lang=" + setting.locale);
        http.setUrl(urlHist);
        String history = http.sendGetRequest(false, "UTF-8");
        File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        int n = JOptionPane.showConfirmDialog(this, Utils.ENDLINE + bundle.getString("newVersionAvailable")
                + Utils.SPACE + newVer + Utils.ENDLINE + bundle.getString("updateMr") + Utils.SPACE + ver
                + Utils.SPACE + bundle.getString("to") + Utils.SPACE
                + newVer + " ?\n\n" + history, "Question", JOptionPane.YES_NO_OPTION);
        if (n == 0) {
          url = new URL("http://movierenamer.free.fr/" + newVerFile.replaceAll(" ", "%20"));

          Utils.downloadFile(url, jarFile.getAbsolutePath());

          n = JOptionPane.showConfirmDialog(this, Settings.APPNAME + Utils.SPACE + bundle.getString("wantRestartAppUpdate"), "Question", JOptionPane.YES_NO_OPTION);
          if (n == JOptionPane.YES_OPTION) {
            if (!Utils.restartApplication(jarFile)) {
              JOptionPane.showMessageDialog(this, bundle.getString("cantRestart"), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
            } else {
              dispose();
              System.exit(0);
            }
          }
        }
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, bundle.getString("checkUpdateFailed") + Utils.ENDLINE + e.getMessage(), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new JFileChooser();
        batchProcessMenu = new JPopupMenu();
        jMenuItem1 = new JMenuItem();
        topTb = new JToolBar();
        openBtn = new JButton();
        jSeparator1 = new Separator();
        editBtn = new JButton();
        separator = new Separator();
        movieModeBtn = new JButton();
        tvShowModeBtn = new JButton();
        updateBtn = new JButton();
        settingBtn = new JButton();
        exitBtn = new JButton();
        centerPnl = new JPanel();
        centerSp = new JSplitPane();
        mediaScroll = new JScrollPane();
        mediaList = new JList();
        searchSp = new JSplitPane();
        searchPnl = new JPanel();
        searchScroll = new JScrollPane();
        searchResultList = new JList();
        searchField = new JTextField();
        searchBtn = new JButton();
        resultLbl = new JLabel();
        btmTb = new JToolBar();
        renameBtn = new JButton();
        renamedField = new JTextField();
        thumbChk = new JCheckBox();
        fanartChk = new JCheckBox();
        nfoChk = new JCheckBox();

        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);

        jMenuItem1.setText("jMenuItem1");
        batchProcessMenu.add(jMenuItem1);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(770, 570));

        topTb.setFloatable(false);
        topTb.setRollover(true);

        openBtn.setIcon(new ImageIcon(getClass().getResource("/image/folder-video.png")));         ResourceBundle bundle = ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle"); // NOI18N
        openBtn.setToolTipText(bundle.getString("openFolderBtn")); // NOI18N
        openBtn.setFocusable(false);
        openBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        openBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        openBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                openBtnActionPerformed(evt);
            }
        });
        topTb.add(openBtn);
        topTb.add(jSeparator1);

        editBtn.setIcon(new ImageIcon(getClass().getResource("/image/accessories-text-editor-6-24.png")));         editBtn.setToolTipText(bundle.getString("edit")); // NOI18N
        editBtn.setEnabled(false);
        editBtn.setFocusable(false);
        editBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        editBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        editBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                editBtnActionPerformed(evt);
            }
        });
        topTb.add(editBtn);
        topTb.add(separator);
        topTb.add(Box.createHorizontalGlue());

        movieModeBtn.setIcon(new ImageIcon(getClass().getResource("/image/movie.png")));         movieModeBtn.setFocusable(false);
        movieModeBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        movieModeBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        movieModeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                movieModeBtnActionPerformed(evt);
            }
        });
        topTb.add(movieModeBtn);

        tvShowModeBtn.setIcon(new ImageIcon(getClass().getResource("/image/tv.png")));         tvShowModeBtn.setFocusable(false);
        tvShowModeBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        tvShowModeBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        tvShowModeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                tvShowModeBtnActionPerformed(evt);
            }
        });
        topTb.add(tvShowModeBtn);

        updateBtn.setIcon(new ImageIcon(getClass().getResource("/image/system-software-update-5.png")));         updateBtn.setToolTipText(bundle.getString("updateBtn")); // NOI18N
        updateBtn.setFocusable(false);
        updateBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        updateBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        updateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                updateBtnActionPerformed(evt);
            }
        });
        topTb.add(updateBtn);

        settingBtn.setIcon(new ImageIcon(getClass().getResource("/image/system-settings.png")));         settingBtn.setToolTipText(bundle.getString("settingBtn")); // NOI18N
        settingBtn.setFocusable(false);
        settingBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        settingBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        settingBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                settingBtnActionPerformed(evt);
            }
        });
        topTb.add(settingBtn);

        exitBtn.setIcon(new ImageIcon(getClass().getResource("/image/application-exit.png")));         exitBtn.setToolTipText(bundle.getString("exitBtn")); // NOI18N
        exitBtn.setFocusable(false);
        exitBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        exitBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        exitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                exitBtnActionPerformed(evt);
            }
        });
        topTb.add(exitBtn);

        getContentPane().add(topTb, BorderLayout.PAGE_START);

        centerSp.setDividerLocation(300);

        mediaScroll.setBorder(BorderFactory.createTitledBorder(null, bundle.getString("movieListTitle"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 13))); 
        mediaList.setFont(new Font("Dialog", 0, 12));         mediaList.addMouseListener(contex);
        mediaScroll.setViewportView(mediaList);

        centerSp.setTopComponent(mediaScroll);

        searchSp.setDividerLocation(200);
        searchSp.setOrientation(JSplitPane.VERTICAL_SPLIT);

        searchPnl.setBorder(BorderFactory.createTitledBorder(null, bundle.getString("searchTitle"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 13))); 
        searchResultList.setFont(new Font("Dialog", 0, 12));         searchScroll.setViewportView(searchResultList);

        searchField.setEnabled(false);
        searchField.addMouseListener(new ContextMenuFieldMouseListener());
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                searchFieldKeyReleased(evt);
            }
        });

        searchBtn.setIcon(new ImageIcon(getClass().getResource("/image/system-search-3.png")));         searchBtn.setToolTipText(bundle.getString("searchOnImdb")); // NOI18N
        searchBtn.setEnabled(false);
        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                searchBtnActionPerformed(evt);
            }
        });

        resultLbl.setText(bundle.getString("searchResListTitle")); // NOI18N

        GroupLayout searchPnlLayout = new GroupLayout(searchPnl);
        searchPnl.setLayout(searchPnlLayout);
        searchPnlLayout.setHorizontalGroup(
            searchPnlLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, searchPnlLayout.createSequentialGroup()
                .addComponent(searchField, GroupLayout.DEFAULT_SIZE, 601, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(searchBtn, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
            .addGroup(searchPnlLayout.createSequentialGroup()
                .addComponent(resultLbl)
                .addContainerGap(544, Short.MAX_VALUE))
            .addComponent(searchScroll, GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
        );
        searchPnlLayout.setVerticalGroup(
            searchPnlLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(searchPnlLayout.createSequentialGroup()
                .addGroup(searchPnlLayout.createParallelGroup(Alignment.LEADING)
                    .addComponent(searchBtn, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(resultLbl)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(searchScroll, GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE))
        );

        searchSp.setLeftComponent(searchPnl);

        centerSp.setRightComponent(searchSp);

        GroupLayout centerPnlLayout = new GroupLayout(centerPnl);
        centerPnl.setLayout(centerPnlLayout);
        centerPnlLayout.setHorizontalGroup(
            centerPnlLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(centerSp)
        );
        centerPnlLayout.setVerticalGroup(
            centerPnlLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(centerSp, GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
        );

        getContentPane().add(centerPnl, BorderLayout.CENTER);

        btmTb.setFloatable(false);
        btmTb.setRollover(true);

        renameBtn.setIcon(new ImageIcon(getClass().getResource("/image/dialog-ok-2.png")));         renameBtn.setText(bundle.getString("rename")); // NOI18N
        renameBtn.setEnabled(false);
        renameBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        renameBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                renameBtnActionPerformed(evt);
            }
        });
        btmTb.add(renameBtn);

        renamedField.setEnabled(false);
        renamedField.setPreferredSize(new Dimension(600, 25));
        renamedField.addMouseListener(new ContextMenuFieldMouseListener());
        btmTb.add(renamedField);

        thumbChk.setText(bundle.getString("thumb")); // NOI18N
        thumbChk.setToolTipText(bundle.getString("downThumb")); // NOI18N
        thumbChk.setFocusable(false);
        thumbChk.setVerticalTextPosition(SwingConstants.BOTTOM);
        btmTb.add(thumbChk);

        fanartChk.setText("Fanart");
        fanartChk.setToolTipText(bundle.getString("downFanart")); // NOI18N
        fanartChk.setFocusable(false);
        fanartChk.setVerticalTextPosition(SwingConstants.BOTTOM);
        btmTb.add(fanartChk);

        nfoChk.setText(bundle.getString("nfoXbmc")); // NOI18N
        nfoChk.setToolTipText(bundle.getString("genNFO")); // NOI18N
        nfoChk.setFocusable(false);
        nfoChk.setVerticalTextPosition(SwingConstants.BOTTOM);
        btmTb.add(nfoChk);

        getContentPane().add(btmTb, BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void openBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_openBtnActionPerformed
      int n = fileChooser.showOpenDialog(this);
      if (n == 0) {
        File[] selectedFiles = fileChooser.getSelectedFiles();
        ArrayList<File> files = new ArrayList<File>(Arrays.asList(selectedFiles));
        dropFile.setMovies(files);
      }
    }//GEN-LAST:event_openBtnActionPerformed

    private void exitBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_exitBtnActionPerformed
      System.exit(0);
    }//GEN-LAST:event_exitBtnActionPerformed

    private void settingBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_settingBtnActionPerformed
      //A refaire
      final Setting set = new Setting(setting, this);
      set.addWindowListener(new WindowListener() {

        @Override
        public void windowOpened(WindowEvent e) {
        }

        @Override
        public void windowClosing(WindowEvent e) {
        }

        @Override
        public void windowClosed(WindowEvent e) {
          setting = set.getSetting();
          /*
           * nfoChk.setText(setting.nfoType == 0 ? bundle.getString("nfoXbmc") : bundle.getString("nfoMediaPortal")); if (setting.interfaceChanged) { setting.interfaceChanged = false; loadInterface();
           * if (currentMedia == null) { return; } if (setting.movieInfoPanel) { SwingWorker<MovieImage, Void> tmdbiw = null; SwingWorker<Void, Void> actor = null;
           *
           * Movie movie = (Movie) currentMedia; if (setting.actorImage) { moviePnl.clearActorList(); actor = WorkerManager.getMovieActorWorker(movie.getMovieInfo().getActors(), moviePnl, setting);
           * actor.addPropertyChangeListener(new MovieImageListener(actor, ACTORWORKER)); }
           *
           * if (setting.thumb || setting.fanart) { if (setting.thumb) { moviePnl.clearThumbList(); } if (setting.fanart) { moviePnl.clearFanartList(); }
           */
          /*
           * try { tmdbiw = WorkerManager.getMovieImageWorker(movie.getMediaId(MediaID.IMDBID), setting); tmdbiw.addPropertyChangeListener(new MovieInfoListener(tmdbiw)); } catch
           * (ActionNotValidException ex) { Settings.LOGGER.log(Level.SEVERE, null, ex); }
           */
          /*
           * }
           * if (setting.thumb || setting.fanart || setting.actorImage) { loadDial(false); } if ((actor != null || tmdbiw != null)) { loading.setValue(100, INFOWORKER); }
           *
           * if (actor != null) { actor.execute(); }
           *
           * if (tmdbiw != null) { tmdbiw.execute(); } } }
           *
           * if (currentMedia != null) { String dir = ""; if (setting.createMovieDirectory) { if (setting.movieDirRenamedTitle == 2) { dir = setting.movieDir + File.separator; } else { boolean
           * origTitle = setting.movieFilenameFormat.contains("<ot>"); String regex = setting.movieDirRenamedTitle == 1 ? setting.movieFilenameFormat : (origTitle ? "<ot>" : "<t>"); dir =
           * currentMedia.getRenamedTitle(regex, setting); dir = dir.substring(0, dir.lastIndexOf(".")); dir += File.separator; } } renamedField.setText(dir +
           * currentMedia.getRenamedTitle(setting.movieFilenameFormat, setting)); }
           *
           * if (setting.lafChanged) { setting.lafChanged = false; try { for (int i = 0; i < Settings.lookAndFeels.length; i++) { if (Settings.lookAndFeels[i].getName().equals(setting.laf)) {
           * UIManager.setLookAndFeel(Settings.lookAndFeels[i].getClassName()); break; } } SwingUtilities.updateComponentTreeUI(MovieRenamer.this); } catch (ClassNotFoundException ex) {
           * Settings.LOGGER.log(Level.SEVERE, null, ex); } catch (InstantiationException ex) { Settings.LOGGER.log(Level.SEVERE, null, ex); } catch (IllegalAccessException ex) {
           * Settings.LOGGER.log(Level.SEVERE, null, ex); } catch (UnsupportedLookAndFeelException ex) { Settings.LOGGER.log(Level.SEVERE, null, ex); } pack(); }
           */
        }

        @Override
        public void windowIconified(WindowEvent e) {
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
        }

        @Override
        public void windowActivated(WindowEvent e) {
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
        }
      });

      java.awt.EventQueue.invokeLater(new Runnable() {

        @Override
        public void run() {
          set.setVisible(true);
        }
      });
    }//GEN-LAST:event_settingBtnActionPerformed

    private void searchBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
      currentMedia.setSearch(searchField.getText());
      clearInterface(false, true);
      searchField.setText(currentMedia.getSearch());
      renameBtn.setEnabled(false);
      editBtn.setEnabled(false);
      renamedField.setText(Utils.EMPTY);
      renamedField.setEnabled(false);
      searchMedia();
    }//GEN-LAST:event_searchBtnActionPerformed

    private void renameBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_renameBtnActionPerformed
      //A refaire
      setMouseIcon(true);

      int index = mediaList.getSelectedIndex();
      if (index == -1) {
        setMouseIcon(false);
        JOptionPane.showMessageDialog(MovieRenamer.this, bundle.getString("noMovieSelected"), sError, JOptionPane.ERROR_MESSAGE);
        return;
      }

      if (currentMedia.getType() == Media.MOVIE) {
        Movie movie = (Movie) currentMedia;
        ArrayList<MediaImage> images = moviePnl.getAddedThumb();
        for (int i = 0; i < images.size(); i++) {
          //  movie.addThumb(images.get(i));
        }

        images = moviePnl.getAddedFanart();
        for (int i = 0; i < images.size(); i++) {
          //movie.addFanart(images.get(i));
        }
        boolean origTitle = setting.movieFilenameFormat.contains("<ot>");
        String regex = setting.movieDirRenamedTitle == 1 ? setting.movieFilenameFormat : (origTitle ? "<ot>" : "<t>");

        String ftitle = currentMedia.getRenamedTitle(regex, setting);
        ftitle = ftitle.substring(0, ftitle.lastIndexOf("."));

        Renamer renamer = new Renamer(ftitle, currentMedia.getMediaFile().getFile(), renamedField.getText(), setting);

        String url = "";
        URL uri = moviePnl.getSelectedThumb(setting.thumbSize);
        if (uri != null) {
          url = uri.toString();
        }

        /*
         * if (url.equals("") && movie.getThumbs().size() > 0) { url = movie.getThumbs().get(0).getThumbUrl(); }
         */
        renamer.setThumb(url);

        if (!renamer.rename()) {
          setMouseIcon(false);
          JOptionPane.showMessageDialog(MovieRenamer.this, bundle.getString("renameFileFailed"), sError, JOptionPane.ERROR_MESSAGE);
          return;
        }

        if (renamer.cancel) {
          setMouseIcon(false);
          return;
        }

        if (mediaFile.get(index).wasRenamed()) {
          for (int i = 0; i < renamedMediaFile.size(); i++) {
            if (renamedMediaFile.get(i).getMovieFileDest().equals(currentMedia.getMediaFile().getFile().getAbsolutePath())) {
              renamedMediaFile.remove(i);
              break;
            }
          }
        }
        renamedMediaFile.add(renamer.getRenamed());

        mediaFile.get(index).setRenamed(true);

        boolean createXNFO = false;
        boolean createThumbnail = false;
        boolean createFan = false;

        if (setting.movieInfoPanel) {
          createXNFO = nfoChk.isSelected();
          if (setting.fanart) {
            createFan = fanartChk.isSelected();
          }
          if (setting.thumb) {
            createThumbnail = thumbChk.isSelected();
          }
        }

        renamer.createNFO(createXNFO, setting.nfoType == 0 ? movie.getXbmcNFOFromMovie() : movie.getMediaPortalNFOFromMovie());
        renamer.createThumb(createThumbnail, moviePnl.getSelectedThumb(setting.thumbSize));
        renamer.createFanart(createFan, moviePnl.getSelectedFanart(setting.fanartSize));

        mediaFile.get(index).setFile(renamer.getNewFile());
        mediaFileNameModel = new DefaultListModel();
        for (int i = 0; i < mediaFile.size(); i++) {
          mediaFileNameModel.addElement(mediaFile.get(i));
        }


        mediaList.setCellRenderer(new IconListRenderer<MediaFile>(mediaFile));
        mediaList.setModel(mediaFileNameModel);

        try {
          String endl = Utils.ENDLINE;
          BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(setting.renamedFile), "UTF-8"));
          out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + endl);
          out.write("<Movie_Renamer_Renamed>" + endl);
          for (int i = 0; i < renamedMediaFile.size(); i++) {
            out.write("  <renamedMovie title=\"" + Utils.escapeXML(renamedMediaFile.get(i).getTitle()) + "\">" + endl);
            out.write("    <movie src=\"" + Utils.escapeXML(renamedMediaFile.get(i).getMovieFileSrc()) + "\"");
            out.write(" dest=\"" + Utils.escapeXML(renamedMediaFile.get(i).getMovieFileDest()) + "\"/>" + endl);
            out.write("    <date>" + Utils.escapeXML(renamedMediaFile.get(i).getDate()) + "</date>" + endl);
            out.write("  </renamedMovie>" + endl);
          }
          out.write("</Movie_Renamer_Renamed>" + endl);
          out.close();
        } catch (IOException ex) {
          Settings.LOGGER.log(Level.SEVERE, ex.toString());
        }
      }


      setMouseIcon(false);
      currentMedia = null;
      clearInterface(false, true);

      int pos = index + 1;
      while (pos < mediaFile.size()) {
        if (!mediaFile.get(pos).isRenamed() && !mediaFile.get(pos).wasRenamed()) {
          mediaList.setSelectedIndex(pos);
          break;
        } else {
          pos++;
        }
      }

      if (mediaFile.size() <= pos) {
        JOptionPane.showMessageDialog(MovieRenamer.this, bundle.getString("endOfList"), "Information", JOptionPane.INFORMATION_MESSAGE);
      }
    }//GEN-LAST:event_renameBtnActionPerformed

    private void editBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_editBtnActionPerformed

      if (currentMedia.getType() == Media.MOVIE) {
        final Movie movie = (Movie) currentMedia;
        final InfoEditorFrame editorFrame = new InfoEditorFrame(movie.getMovieInfo(), MovieRenamer.this);
        editorFrame.addPropertyChangeListener(new PropertyChangeListener() {

          @Override
          public void propertyChange(PropertyChangeEvent pce) {
            if (pce.getPropertyName().equals("movieInfo")) { // currentMovie.setMovieInfo((MovieInfo) pce.getNewValue());
              moviePnl.addMovieInfo(movie.getMovieInfo());
            }
          }
        });

        SwingUtilities.invokeLater(new Runnable() {

          @Override
          public void run() {
            editorFrame.setVisible(true);
          }
        });
      }
    }//GEN-LAST:event_editBtnActionPerformed

    private void updateBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_updateBtnActionPerformed
      checkUpdate(true);
    }//GEN-LAST:event_updateBtnActionPerformed

  private void movieModeBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_movieModeBtnActionPerformed
    currentMode = modes[MovieRenamerMode.MOVIEMODE];
    movieModeBtn.setEnabled(false);
    tvShowModeBtn.setEnabled(true);
    setTitle(Settings.APPNAME + "-" + setting.getVersion() + "  Movie Mode");
  }//GEN-LAST:event_movieModeBtnActionPerformed

  private void tvShowModeBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_tvShowModeBtnActionPerformed
    currentMode = modes[MovieRenamerMode.MOVIEMODE];
    movieModeBtn.setEnabled(true);
    tvShowModeBtn.setEnabled(false);
    setTitle(Settings.APPNAME + "-" + setting.getVersion() + "  Tv Show Mode");
  }//GEN-LAST:event_tvShowModeBtnActionPerformed

  private void searchFieldKeyReleased(KeyEvent evt) {//GEN-FIRST:event_searchFieldKeyReleased
    if(evt.getKeyCode() == KeyEvent.VK_ENTER){
      currentMedia.setSearch(searchField.getText());
      clearInterface(false, true);
      searchField.setText(currentMedia.getSearch());
      renameBtn.setEnabled(false);
      editBtn.setEnabled(false);
      renamedField.setText(Utils.EMPTY);
      renamedField.setEnabled(false);
      searchMedia();
    }
  }//GEN-LAST:event_searchFieldKeyReleased

  public class SearchWorkerListener implements PropertyChangeListener {

    private SwingWorker<ArrayList<SearchResult>, String> worker;

    public SearchWorkerListener() {
      worker = null;
    }

    public void setWorker(SwingWorker<ArrayList<SearchResult>, String> worker) {
      this.worker = worker;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

      if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {

        ArrayList<SearchResult> results = null;

        try {
          results = worker.get();
        } catch (InterruptedException ex) {
          Settings.LOGGER.log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
          Settings.LOGGER.log(Level.SEVERE, null, ex);
        }

        searchBtn.setEnabled(true);
        searchField.setEnabled(true);

        if (results == null) {
          loading.dispose();
          return;
        }

        if (results.isEmpty()) {
          loading.dispose();
          JOptionPane.showMessageDialog(MovieRenamer.this, bundle.getString("noResult"), sError, JOptionPane.ERROR_MESSAGE);
          return;
        }

        searchResModel = new DefaultListModel();
        resultLbl.setText(bundle.getString("searchResListTitle") + " : " + results.size());

        // Sort result by similarity and year
        if (results.size() > 1 && setting.sortBySimiYear) {
          Levenshtein.sortByLevenshteinDistanceYear(currentMedia.getSearch(), currentMedia.getYear(), results);
        }

        for (SearchResult result : results) {
          searchResModel.addElement(result);
        }

        // Display thumbs in result list          
        if (setting.displayThumbResult) {
          searchResultList.setCellRenderer(new IconListRenderer<SearchResult>(results));
        } else {
          searchResultList.setCellRenderer(new DefaultListCellRenderer());
        }

        searchResultList.setModel(searchResModel);

        loading.setValue(100, SEARCHWORKER);
        if (!searchResModel.isEmpty()) {
          if (MovieRenamer.this.setting.selectFrstRes) {
            searchResultList.setSelectedIndex(0);
          }
        }
      } else {
        loading.setValue(worker.getProgress(), SEARCHWORKER);
      }
    }
  }

  public class FileWorkerListener implements PropertyChangeListener {

    private ListFilesWorker worker;
    private ProgressMonitor progressMonitor;

    public FileWorkerListener() {
      this.worker = null;
    }

    public void setWorker(ListFilesWorker worker) {
      this.worker = worker;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getNewValue().equals(SwingWorker.StateValue.STARTED)) {
        clearInterface(true, true);
        progressMonitor = new ProgressMonitor(MovieRenamer.this, bundle.getString("searchMoviefile"), Utils.EMPTY, 0, 100);
        progressMonitor.setMillisToDecideToPopup(0);
        progressMonitor.setProgress(0);
      } else if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
        try {
          currentMedia = null;
          ArrayList<MediaFile> objects = worker.get();
          mediaFile = objects;
          mediaFileNameModel = new DefaultListModel();

          for (int i = 0; i < objects.size(); i++) {
            mediaFileNameModel.addElement(objects.get(i));
          }

          ((TitledBorder) mediaScroll.getBorder()).setTitle(Utils.EMPTY + mediaFileNameModel.size() + Utils.SPACE + bundle.getString("movies"));

          mediaList.setCellRenderer(new IconListRenderer<MediaFile>(objects));
          mediaScroll.repaint();

          mediaList.setModel(mediaFileNameModel);
          if (mediaFileNameModel.isEmpty()) {
            JOptionPane.showMessageDialog(MovieRenamer.this, bundle.getString("noMovieFound"), sError, JOptionPane.ERROR_MESSAGE);
          } else if (setting.selectFrstMedia) {
            mediaList.setSelectedIndex(0);
          }

        } catch (InterruptedException ex) {
          Settings.LOGGER.log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
          Settings.LOGGER.log(Level.SEVERE, null, ex);
        } catch (CancellationException ex) {
          Settings.LOGGER.log(Level.INFO, "ListFileWorker canceled");
          return;
        }
        if (progressMonitor != null) {
          progressMonitor.close();
        }
        progressMonitor = null;

      } else if (progressMonitor != null) {
        progressMonitor.setProgress(worker.getProgress());
      }

      if (progressMonitor != null && progressMonitor.isCanceled()) {
        if (!worker.isDone()) {
          worker.cancel(true);
        }
      }
    }
  }

  private class MovieInfoListener implements PropertyChangeListener {//A refaire

    private SwingWorker<MovieInfo, String> worker;

    public MovieInfoListener(SwingWorker<MovieInfo, String> worker) {
      this.worker = worker;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {//A refaire

      if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
        try {
          MovieInfo movieInfo = worker.get();
          if (movieInfo == null) {
            loading.setValue(100, INFOWORKER);
            return;
          }

          if (setting.movieInfoPanel) {
            if (setting.thumb) {
              SwingWorker<Void, Void> thumbWorker = WorkerManager.getMediaImageWorker(movieInfo.getThumbs(), Cache.THUMB, moviePnl, setting);
              thumbWorker.addPropertyChangeListener(new MovieImageListener(thumbWorker, THUMBWORKER));
              thumbWorker.execute();
            }
            if (setting.fanart) {
              SwingWorker<Void, Void> fanartWorker = WorkerManager.getMediaImageWorker(movieInfo.getFanarts(), Cache.FANART, moviePnl, setting);
              fanartWorker.addPropertyChangeListener(new MovieImageListener(fanartWorker, FANARTWORKER));
              fanartWorker.execute();
            }
          }

          currentMedia.setInfo(movieInfo);

          String dir = "";
          if (setting.createMovieDirectory) {
            if (setting.movieDirRenamedTitle == 2) {
              dir = setting.movieDir + File.separator;
            } else {
              boolean origTitle = setting.movieFilenameFormat.contains("<ot>");
              String regex = (setting.movieDirRenamedTitle == 1 ? setting.movieFilenameFormat : (origTitle ? "<ot>" : "<t>"));
              dir = currentMedia.getRenamedTitle(regex, setting);
              dir = dir.substring(0, dir.lastIndexOf("."));
              dir += File.separator;
            }
          }

          renamedField.setText(dir + currentMedia.getRenamedTitle(setting.movieFilenameFormat, setting));
          renameBtn.setEnabled(true);
          renamedField.setEnabled(true);
          editBtn.setEnabled(true);
          moviePnl.addMovieInfo(movieInfo);

          SwingWorker<Void, Void> actor = WorkerManager.getMovieActorWorker(movieInfo.getActors(), moviePnl, setting);
          actor.addPropertyChangeListener(new MovieImageListener(actor, ACTORWORKER));
          actor.execute();
          loading.setValue(100, INFOWORKER);

        } catch (InterruptedException ex) {
          Settings.LOGGER.log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
          Settings.LOGGER.log(Level.SEVERE, null, ex);
        }
      } else {
        loading.setValue(worker.getProgress(), INFOWORKER);
      }
    }
  }

  //A faire
  private class TvShowInfoListener implements PropertyChangeListener {

    private SwingWorker worker;

    public TvShowInfoListener(SwingWorker worker) {
      this.worker = worker;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }
  }

  private class MovieImageListener implements PropertyChangeListener {

    private SwingWorker<Void, Void> miw;
    private int id;

    public MovieImageListener(SwingWorker<Void, Void> miw, int id) {
      this.miw = miw;
      this.id = id;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
        loading.setValue(100, id);
      } else {
        loading.setValue(miw.getProgress(), id);
      }
    }
  }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPopupMenu batchProcessMenu;
    private JToolBar btmTb;
    private JPanel centerPnl;
    private JSplitPane centerSp;
    private JButton editBtn;
    private JButton exitBtn;
    private JCheckBox fanartChk;
    private JFileChooser fileChooser;
    private JMenuItem jMenuItem1;
    private Separator jSeparator1;
    private JList mediaList;
    private JScrollPane mediaScroll;
    private JButton movieModeBtn;
    private JCheckBox nfoChk;
    private JButton openBtn;
    private JButton renameBtn;
    private JTextField renamedField;
    private JLabel resultLbl;
    private JButton searchBtn;
    private JTextField searchField;
    private JPanel searchPnl;
    private JList searchResultList;
    private JScrollPane searchScroll;
    private JSplitPane searchSp;
    private Separator separator;
    private JButton settingBtn;
    private JCheckBox thumbChk;
    private JToolBar topTb;
    private JButton tvShowModeBtn;
    private JButton updateBtn;
    // End of variables declaration//GEN-END:variables
}
