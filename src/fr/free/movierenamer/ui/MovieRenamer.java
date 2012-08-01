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
import fr.free.movierenamer.media.tvshow.TvShowInfo;
import fr.free.movierenamer.parser.MrRenamedMovie;
import fr.free.movierenamer.parser.XMLParser;
import fr.free.movierenamer.ui.res.ContextMenuFieldMouseListener;
import fr.free.movierenamer.ui.res.ContextMenuListMouseListener;
import fr.free.movierenamer.ui.res.DropFile;
import fr.free.movierenamer.ui.res.IconListRenderer;
import fr.free.movierenamer.utils.*;
import fr.free.movierenamer.worker.*;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JToolBar.Separator;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
  private final String sError = Utils.i18n("error");
  private DropFile dropFile;
  private LoadingDialog loading;
  private List<MediaFile> mediaFile;
  private ContextMenuListMouseListener contex;
  private List<MediaRenamed> renamedMediaFile;
  private MovieRenamerMode currentMode;
  // Worker
  private MediaSearchWorker searchWorker;
  private MovieInfoWorker movieInfoWorker;
  private TvShowInfoWorker tvShowInfoWorker;
  // Property change
  private PropertyChangeSupport errorSupport;
  private PropertyChangeSupport settingsChange;
  // Media Panel
  private MoviePanel moviePnl;
  private TvShowPanel tvShowPanel;

  private enum CHOICE {

    CONTINUE(Utils.i18n("continue")),
    CHANGE(Utils.i18n("changeMode")),
    CANCEL(Utils.i18n("cancel"));
    private String text;

    private CHOICE(String text) {
      this.text = text;
    }

    @Override
    public String toString() {
      return text;
    }
  }

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
    tvShowPanel = new TvShowPanel();

    loadRenamedMovie();

    //Add drag and drop listener on mediaList
    dropFile = new DropFile(setting, renamedMediaFile, new FileWorkerListener(), MovieRenamer.this);
    DropTarget dt = new DropTarget(mediaList, dropFile);
    dt.setActive(true);

    currentMode = MovieRenamerMode.MOVIEMODE;
    movieModeBtn.setEnabled(false);

    errorSupport = new PropertyChangeSupport(new Object());
    errorSupport.addPropertyChangeListener(new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("closeLoadingDial")) {
          loading.dispose();
        }
      }
    });

    settingsChange = new PropertyChangeSupport(setting);
    settingsChange.addPropertyChangeListener(new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {//TODO A refaire

        Settings.LOGGER.log(Level.INFO, "Settings property change : {0}", evt.getPropertyName());
        if (evt.getPropertyName().equals("settingChange")) {
          Settings oldConf = (Settings) evt.getOldValue();
          Settings newConf = (Settings) evt.getNewValue();

          boolean filterChanged = !oldConf.mediaNameFilters.equals(newConf.mediaNameFilters);
          boolean scrapperChanged = false;
          boolean scrapperLangChanged = false;
          switch (currentMode) {
            case MOVIEMODE:
              scrapperChanged = oldConf.movieScrapper != newConf.movieScrapper;
              scrapperLangChanged = oldConf.movieScrapperFR != newConf.movieScrapperFR;
              break;
            case TVSHOWMODE:
              scrapperChanged = oldConf.tvshowScrapper != newConf.tvshowScrapper;
              scrapperLangChanged = oldConf.tvshowScrapperFR != newConf.tvshowScrapperFR;
              break;
            default:
              break;
          }

          // Update newConf
          MovieRenamer.this.setting = newConf;

          if (Settings.interfaceChanged) {
            boolean getImage = currentMedia != null && newConf.movieInfoPanel && !scrapperChanged && !scrapperLangChanged && !filterChanged && !searchResModel.isEmpty();

            Settings.interfaceChanged = false;

            // Refresh interface
            loadInterface();

            // Get image from cache or web
            if (getImage && currentMode == MovieRenamerMode.MOVIEMODE) {
              ImageWorker thumbWorker = null;
              ImageWorker fanartWorker = null;
              ActorWorker actorWorker = null;

              if (newConf.actorImage) {
                moviePnl.clearActorList();
                actorWorker = WorkerManager.getMovieActorWorker(currentMedia.getActors(), moviePnl);
                actorWorker.addPropertyChangeListener(new workerListener(actorWorker, WorkerManager.WORKERID.ACTORWORKER));
              }

              if (newConf.thumb) {
                moviePnl.clearThumbList();
                try {
                  thumbWorker = WorkerManager.getMediaImageWorker(currentMedia.getImages(MediaImage.MediaImageType.THUMB), Cache.CacheType.THUMB, moviePnl);
                  thumbWorker.addPropertyChangeListener(new workerListener(thumbWorker, WorkerManager.WORKERID.THUMBWORKER));
                } catch (ActionNotValidException ex) {
                  Settings.LOGGER.log(Level.SEVERE, null, ex);
                }
              }

              if (newConf.fanart) {
                moviePnl.clearFanartList();
                try {
                  fanartWorker = WorkerManager.getMediaImageWorker(currentMedia.getImages(MediaImage.MediaImageType.FANART), Cache.CacheType.FANART, moviePnl);
                  fanartWorker.addPropertyChangeListener(new workerListener(fanartWorker, WorkerManager.WORKERID.FANARTWORKER));
                } catch (ActionNotValidException ex) {
                  Settings.LOGGER.log(Level.SEVERE, null, ex);
                }
              }

              if (newConf.thumb || newConf.fanart || newConf.actorImage) {
                loadDial(false);
                loading.setValue(100, WorkerManager.WORKERID.INFOWORKER);
              }

              if (actorWorker != null) {
                actorWorker.execute();
              }

              if (thumbWorker != null) {
                thumbWorker.execute();
              }

              if (fanartWorker != null) {
                fanartWorker.execute();
              }
            }
          }

          nfoChk.setText(newConf.nfoType == 0 ? Utils.i18n("nfoXbmc") : Utils.i18n("nfoMediaPortal"));

          // Re-generate renamed filename
          if (currentMedia != null && !searchResModel.isEmpty()) {
            renamedField.setText(currentMedia.getRenamedTitle());
          }

          if (filterChanged) {
            currentMedia.setDefaultSearch();
          }

          if (scrapperChanged || scrapperLangChanged) {
            searchMedia();
          }
        }
      }
    });

    loadInterface();

    setIconImage(Utils.getImageFromJAR("/image/icon-32.png", getClass()));
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
        currentMedia = null;
        ((TitledBorder) mediaScroll.getBorder()).setTitle(Utils.EMPTY + mediaFileNameModel.size() + Utils.SPACE + Utils.i18n("medias"));
        mediaScroll.repaint();
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
        case MOVIE:
          currentMedia = new Movie(mediaFile);
          break;
        case TVSHOW:
          currentMedia = new TvShow(mediaFile);
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
          case MOVIE:
            if (movieInfoWorker != null && !movieInfoWorker.isDone()) {
              return;
            }
            //Get movie info
            movieInfoWorker = WorkerManager.getMovieInfoWorker(errorSupport, sres.getId());
            if (movieInfoWorker == null) {
              JOptionPane.showMessageDialog(MovieRenamer.this, Utils.i18n("errorBugReport"), sError, JOptionPane.ERROR_MESSAGE);
              return;
            }
            movieInfoWorker.addPropertyChangeListener(new MovieInfoListener(movieInfoWorker));
            movieInfoWorker.execute();
            break;
          case TVSHOW:
            tvShowInfoWorker = WorkerManager.getTvShowInfoWorker(errorSupport, sres.getId(), ((TvShow) currentMedia).getSearchSxe());
            TvShowInfoListener tsil = new TvShowInfoListener(tvShowInfoWorker);
            tvShowInfoWorker.addPropertyChangeListener(tsil);
            tvShowInfoWorker.execute();
            break;
        }
      } catch (ActionNotValidException ex) {// FIXME close loading dialog and show error message
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
  private void setMouseIcon(boolean loading) {
    Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
    Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    setCursor(loading ? hourglassCursor : normalCursor);
  }

  private void loadRenamedMovie() {
    renamedMediaFile = new ArrayList<MediaRenamed>();
    if (new File(Settings.renamedFile).exists()) {
      XMLParser<List<MediaRenamed>> mmp = new XMLParser<List<MediaRenamed>>(Settings.renamedFile);
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

  private void loadInterface() {// TODO , A refaire pour l'impémentation finale des série (après la beta)
    switch (currentMode) {
      case MOVIEMODE:
        if (!setting.movieInfoPanel) {
          MediaSp.remove(moviePnl);
          centerSp.remove(MediaSp);
          centerSp.add(searchPnl);
          centerSp.setOrientation(JSplitPane.VERTICAL_SPLIT);
        } else {
          centerSp.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
          if (centerSp.getBottomComponent().equals(searchPnl)) {
            centerSp.remove(searchPnl);
            MediaSp.removeAll();
            MediaSp.add(searchPnl);
            centerSp.add(MediaSp);
          }
          moviePnl.setDisplay(setting);
          tvShowPanel.setDisplay(setting);
          MediaSp.setBottomComponent(moviePnl);
          centerSp.setDividerLocation(300);
          MediaSp.setDividerLocation(200);
        }

        thumbChk.setVisible(setting.movieInfoPanel && setting.thumb);
        fanartChk.setVisible(setting.movieInfoPanel && setting.fanart);

        nfoChk.setVisible(setting.movieInfoPanel);
        editBtn.setVisible(setting.movieInfoPanel);
        break;
      case TVSHOWMODE:
        if (setting.movieInfoPanel) {
          MediaSp.remove(moviePnl);
        } else {
          if (centerSp.getBottomComponent().equals(searchPnl)) {
            centerSp.remove(searchPnl);
            MediaSp.removeAll();
            MediaSp.add(searchPnl);
            centerSp.add(MediaSp);
          }
        }
        centerSp.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        MediaSp.setBottomComponent(tvShowPanel);

        //Not used for the moment
        thumbChk.setVisible(false);
        fanartChk.setVisible(false);
        nfoChk.setVisible(false);
        //editBtn.setVisible(false);
        break;
      default:
    }

    centerPnl.validate();
    centerPnl.repaint();
    setTitle(Settings.APPNAME + "-" + setting.getVersion() + " " + currentMode.getTitleMode());
  }

  private void clearInterface(boolean mediaList, boolean searchList) {

    if (currentMedia != null) {
      currentMedia.clear();
    }

    if (mediaList) {
      if (mediaFileNameModel != null) {
        mediaFileNameModel.clear();
      }

      ((TitledBorder) mediaScroll.getBorder()).setTitle(Utils.i18n("media"));
      mediaScroll.validate();
      mediaScroll.repaint();
    }

    if (searchList) {
      if (searchResModel != null) {
        searchResModel.clear();
      }
      resultLbl.setText(Utils.i18n("searchResListTitle"));
      searchBtn.setEnabled(false);
      searchField.setEnabled(false);
      renamedField.setText("");
      renamedField.setEnabled(false);
      searchField.setText("");
    }
    renameBtn.setEnabled(false);
    editBtn.setEnabled(false);

    // Clear panel
    moviePnl.clear();
    tvShowPanel.clear();
  }

  /**
   * Check if media type is supported by current mode and ask user on what to do.
   *
   * @return True if current mode support media type, false otherwise
   */
  private boolean checkMediaTypeInCurrentMode(MediaFile mediaFile) {

    if (mediaFile.getType() == currentMode.getMediaType()) {
      return true;
    }

    CHOICE[] choices = {CHOICE.CONTINUE, CHOICE.CHANGE, CHOICE.CANCEL};
    String text = Utils.i18n("whatToDo").replace("FILE", mediaFile.getFile().getName()).replace("MODE", currentMode.getTitle());
    int res = JOptionPane.showOptionDialog(MovieRenamer.this, text, Utils.i18n("mediaModemt"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, choices, "");

    switch (choices[res]) {
      case CONTINUE:
        mediaFile.setType(currentMode.getMediaType());
        break;
      case CHANGE:
        currentMode = currentMode == MovieRenamerMode.MOVIEMODE ? MovieRenamerMode.TVSHOWMODE : MovieRenamerMode.MOVIEMODE;
        movieModeBtn.setEnabled(currentMode == MovieRenamerMode.TVSHOWMODE);
        tvShowModeBtn.setEnabled(currentMode == MovieRenamerMode.MOVIEMODE);
        loadInterface();
        break;
      case CANCEL:
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
    searchWorker = WorkerManager.getSearchWorker(errorSupport, currentMedia);
    if (searchWorker == null) {
      JOptionPane.showMessageDialog(MovieRenamer.this, Utils.i18n("errorBugReport"), sError, JOptionPane.ERROR_MESSAGE);
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
  private void loadDial(boolean search) {
    List<Loading> loadings = getLoading(search);
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
  private List<Loading> getLoading(boolean search) {
    List<Loading> loadings = new ArrayList<Loading>();

    if (search) {
      loadings.add(new Loading(Utils.i18n("mediaSearch"), true, 100, WorkerManager.WORKERID.SEARCHWORKER));
    }

    switch (currentMedia.getType()) {
      case MOVIE:
        if (!search || (search && MovieRenamer.this.setting.selectFrstRes)) {
          loadings.add(new Loading(Utils.i18n("movieInf"), true, 100, WorkerManager.WORKERID.INFOWORKER));
          if (setting.movieInfoPanel && setting.thumb) {
            loadings.add(new Loading(Utils.i18n("thumbnails"), false, 100, WorkerManager.WORKERID.THUMBWORKER));
          }
          if (setting.movieInfoPanel && setting.fanart) {
            loadings.add(new Loading("Fanarts", false, 100, WorkerManager.WORKERID.FANARTWORKER));
          }
          if (setting.movieInfoPanel && setting.actorImage) {
            loadings.add(new Loading(Utils.i18n("actors"), false, 100, WorkerManager.WORKERID.ACTORWORKER));
          }
        }
        break;
      case TVSHOW:
        if (!search || (search && MovieRenamer.this.setting.selectFrstRes)) {
          loadings.add(new Loading(Utils.i18n("movieInf"), true, 100, WorkerManager.WORKERID.INFOWORKER));
        }
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
  public final void checkUpdate(boolean showAlready) {// TODO A refaire, ajouter mise a jour des libs, et à exporter ailleur
    String ver = setting.getVersion();
    if (!ver.equals("")) {
      try {
        URL url = new URL("http://movierenamer.free.fr/update.php?version=" + ver.substring(0, setting.getVersion().lastIndexOf("_")) + "&amp;lang=" + setting.locale);
        HttpGet http = new HttpGet(url);
        String newVerFile = http.sendGetRequest(false, "UTF-8");
        if (newVerFile.equals("")) {
          if (showAlready) {
            JOptionPane.showMessageDialog(this, Utils.i18n("alreadyUpToDate"), Utils.i18n("update"), JOptionPane.INFORMATION_MESSAGE);
          }
          return;
        }
        String newVer = newVerFile.substring(newVerFile.lastIndexOf("-") + 1, newVerFile.lastIndexOf("."));
        URL urlHist = new URL("http://movierenamer.free.fr/update.php?getHistory&lang=" + setting.locale);
        http.setUrl(urlHist);
        String history = http.sendGetRequest(false, "UTF-8");
        File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        int n = JOptionPane.showConfirmDialog(this, Utils.ENDLINE + Utils.i18n("newVersionAvailable")
                + Utils.SPACE + newVer + Utils.ENDLINE + Utils.i18n("updateMr") + Utils.SPACE + ver
                + Utils.SPACE + Utils.i18n("to") + Utils.SPACE
                + newVer + " ?\n\n" + history, "Question", JOptionPane.YES_NO_OPTION);
        if (n == 0) {
          url = new URL("http://movierenamer.free.fr/" + newVerFile.replaceAll(" ", "%20"));

          Utils.downloadFile(url, jarFile.getAbsolutePath());

          n = JOptionPane.showConfirmDialog(this, Settings.APPNAME + Utils.SPACE + Utils.i18n("wantRestartAppUpdate"), "Question", JOptionPane.YES_NO_OPTION);
          if (n == JOptionPane.YES_OPTION) {
            if (!Utils.restartApplication(jarFile)) {
              JOptionPane.showMessageDialog(this, Utils.i18n("cantRestart"), Utils.i18n("error"), JOptionPane.ERROR_MESSAGE);
            } else {
              dispose();
              System.exit(0);
            }
          }
        }
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, Utils.i18n("checkUpdateFailed") + Utils.ENDLINE + e.getMessage(), Utils.i18n("error"), JOptionPane.ERROR_MESSAGE);
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
        jPanel1 = new JPanel();
        mediaScroll = new JScrollPane();
        mediaList = new JList();
        MediaSp = new JSplitPane();
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
        topTb.add(Box.createHorizontalGlue());

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

        mediaScroll.setBorder(BorderFactory.createTitledBorder(null, bundle.getString("media"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 13))); 
        mediaList.setFont(new Font("Dialog", 0, 12));         mediaList.addMouseListener(contex);
        mediaScroll.setViewportView(mediaList);

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addComponent(mediaScroll, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addComponent(mediaScroll, GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
        );

        centerSp.setLeftComponent(jPanel1);

        MediaSp.setDividerLocation(200);
        MediaSp.setOrientation(JSplitPane.VERTICAL_SPLIT);

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
                .addComponent(searchField, GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(searchBtn, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
            .addGroup(searchPnlLayout.createSequentialGroup()
                .addComponent(resultLbl)
                .addContainerGap(516, Short.MAX_VALUE))
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

        MediaSp.setLeftComponent(searchPnl);

        centerSp.setRightComponent(MediaSp);

        GroupLayout centerPnlLayout = new GroupLayout(centerPnl);
        centerPnl.setLayout(centerPnlLayout);
        centerPnlLayout.setHorizontalGroup(
            centerPnlLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(centerSp)
        );
        centerPnlLayout.setVerticalGroup(
            centerPnlLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(centerSp)
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
        List<File> files = new ArrayList<File>(Arrays.asList(selectedFiles));
        dropFile.setMovies(files);
      }
    }//GEN-LAST:event_openBtnActionPerformed

    private void exitBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_exitBtnActionPerformed
      System.exit(0);
    }//GEN-LAST:event_exitBtnActionPerformed

    private void settingBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_settingBtnActionPerformed
      final Setting set = new Setting(setting, settingsChange, this);
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
      // TODO
      setMouseIcon(true);

      int index = mediaList.getSelectedIndex();
      if (index == -1) {
        setMouseIcon(false);
        JOptionPane.showMessageDialog(MovieRenamer.this, Utils.i18n("noMovieSelected"), sError, JOptionPane.ERROR_MESSAGE);
        return;
      }

      if (currentMedia.getType() == Media.MediaType.MOVIE) {
        Movie movie = (Movie) currentMedia;
        List<MediaImage> images = moviePnl.getAddedThumb();
        for (int i = 0; i < images.size(); i++) {
          //  movie.addThumb(images.get(i));
        }

        images = moviePnl.getAddedFanart();
        for (int i = 0; i < images.size(); i++) {
          //movie.addFanart(images.get(i));
        }

        Renamer renamer = new Renamer("", currentMedia.getMediaFile().getFile(), renamedField.getText(), setting);

        String url = "";
        /*
         * URL uri = moviePnl.getSelectedThumb(setting.thumbSize); if (uri != null) { url = uri.toString(); }
         */

        /*
         * if (url.equals("") && movie.getThumbs().size() > 0) { url = movie.getThumbs().get(0).getThumbUrl(); }
         */
        renamer.setThumb(url);

        if (!renamer.rename()) {
          setMouseIcon(false);
          JOptionPane.showMessageDialog(MovieRenamer.this, Utils.i18n("renameFileFailed"), sError, JOptionPane.ERROR_MESSAGE);
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
        /*
         * renamer.createThumb(createThumbnail, moviePnl.getSelectedThumb(setting.thumbSize)); renamer.createFanart(createFan, moviePnl.getSelectedFanart(setting.fanartSize));
         */

        mediaFile.get(index).setFile(renamer.getNewFile());
        mediaFileNameModel = new DefaultListModel();
        for (int i = 0; i < mediaFile.size(); i++) {
          mediaFileNameModel.addElement(mediaFile.get(i));
        }


        mediaList.setCellRenderer(new IconListRenderer<MediaFile>(mediaFile));
        mediaList.setModel(mediaFileNameModel);

        try {
          String endl = Utils.ENDLINE;
          BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Settings.renamedFile), "UTF-8"));
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
        JOptionPane.showMessageDialog(MovieRenamer.this, Utils.i18n("endOfList"), "Information", JOptionPane.INFORMATION_MESSAGE);
      }
    }//GEN-LAST:event_renameBtnActionPerformed

    private void editBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_editBtnActionPerformed

      if (currentMedia.getType() == Media.MediaType.MOVIE) {
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
    currentMode = MovieRenamerMode.MOVIEMODE;
    movieModeBtn.setEnabled(false);
    tvShowModeBtn.setEnabled(true);
    loadInterface();
    clearInterface(false, true);
    if (currentMedia != null) {
      MediaFile mfile = currentMedia.getMediaFile();
      mfile.setType(Media.MediaType.MOVIE);
      currentMedia.setMediaFile(mfile);
      searchMedia();
    }
  }//GEN-LAST:event_movieModeBtnActionPerformed

  private void tvShowModeBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_tvShowModeBtnActionPerformed
    currentMode = MovieRenamerMode.TVSHOWMODE;
    movieModeBtn.setEnabled(true);
    tvShowModeBtn.setEnabled(false);
    loadInterface();
    clearInterface(false, true);
    if (currentMedia != null) {
      MediaFile mfile = currentMedia.getMediaFile();
      mfile.setType(Media.MediaType.TVSHOW);
      currentMedia.setMediaFile(mfile);
      searchMedia();
    }
  }//GEN-LAST:event_tvShowModeBtnActionPerformed

  private void searchFieldKeyReleased(KeyEvent evt) {//GEN-FIRST:event_searchFieldKeyReleased
    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
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

    private SwingWorker<List<SearchResult>, String> worker;

    public SearchWorkerListener() {
      worker = null;
    }

    public void setWorker(SwingWorker<List<SearchResult>, String> worker) {
      this.worker = worker;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

      if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {

        List<SearchResult> results = null;

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
          JOptionPane.showMessageDialog(MovieRenamer.this, Utils.i18n("noResult"), sError, JOptionPane.ERROR_MESSAGE);
          return;
        }

        searchResModel = new DefaultListModel();
        resultLbl.setText(Utils.i18n("searchResListTitle") + " : " + results.size());

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

        loading.setValue(100, WorkerManager.WORKERID.SEARCHWORKER);
        if (!searchResModel.isEmpty()) {
          if (MovieRenamer.this.setting.selectFrstRes) {
            searchResultList.setSelectedIndex(0);
          }
        }
      } else {
        loading.setValue(worker.getProgress(), WorkerManager.WORKERID.SEARCHWORKER);
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
        progressMonitor = new ProgressMonitor(MovieRenamer.this, Utils.i18n("searchMoviefile"), Utils.EMPTY, 0, 100);
        progressMonitor.setMillisToDecideToPopup(0);
        progressMonitor.setProgress(0);
      } else if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
        try {
          currentMedia = null;
          List<MediaFile> objects = worker.get();
          mediaFile = objects;
          mediaFileNameModel = new DefaultListModel();

          for (int i = 0; i < objects.size(); i++) {
            mediaFileNameModel.addElement(objects.get(i));
          }

          ((TitledBorder) mediaScroll.getBorder()).setTitle(Utils.EMPTY + mediaFileNameModel.size() + Utils.SPACE + Utils.i18n("medias"));

          mediaList.setCellRenderer(new IconListRenderer<MediaFile>(objects));
          mediaScroll.repaint();

          mediaList.setModel(mediaFileNameModel);
          if (mediaFileNameModel.isEmpty()) {
            JOptionPane.showMessageDialog(MovieRenamer.this, Utils.i18n("noMovieFound"), sError, JOptionPane.ERROR_MESSAGE);// FIXME change movie by media
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

  private class MovieInfoListener implements PropertyChangeListener {// TODO A refaire

    private MovieInfoWorker worker;

    public MovieInfoListener(MovieInfoWorker worker) {
      this.worker = worker;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

      if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
        try {
          MovieInfo movieInfo = worker.get();
          if (movieInfo == null) {
            loading.setValue(100, WorkerManager.WORKERID.INFOWORKER);
            return;
          }

          if (setting.movieInfoPanel) {
            if (setting.thumb) {
              ImageWorker thumbWorker = WorkerManager.getMediaImageWorker(movieInfo.getThumbs(), Cache.CacheType.THUMB, moviePnl);
              thumbWorker.addPropertyChangeListener(new workerListener(thumbWorker, WorkerManager.WORKERID.THUMBWORKER));
              thumbWorker.execute();
            }
            if (setting.fanart) {
              ImageWorker fanartWorker = WorkerManager.getMediaImageWorker(movieInfo.getFanarts(), Cache.CacheType.FANART, moviePnl);
              fanartWorker.addPropertyChangeListener(new workerListener(fanartWorker, WorkerManager.WORKERID.FANARTWORKER));
              fanartWorker.execute();
            }
          }

          currentMedia.setInfo(movieInfo);
          moviePnl.addMovieInfo(movieInfo);

          renamedField.setText(currentMedia.getRenamedTitle());
          renameBtn.setEnabled(true);
          renamedField.setEnabled(true);
          editBtn.setEnabled(true);

          ActorWorker actor = WorkerManager.getMovieActorWorker(movieInfo.getActors(), moviePnl);
          actor.addPropertyChangeListener(new workerListener(actor, WorkerManager.WORKERID.ACTORWORKER));
          actor.execute();
          loading.setValue(100, WorkerManager.WORKERID.INFOWORKER);

        } catch (InterruptedException ex) {
          Settings.LOGGER.log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
          Settings.LOGGER.log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
          Settings.LOGGER.log(Level.SEVERE, Utils.getStackTrace("NullPointerException", ex.getStackTrace()));
          // TODO display error
        }
      } else {
        loading.setValue(worker.getProgress(), WorkerManager.WORKERID.INFOWORKER);
      }
    }
  }

  private class TvShowInfoListener implements PropertyChangeListener {

    private TvShowInfoWorker worker;

    public TvShowInfoListener(TvShowInfoWorker worker) {
      this.worker = worker;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
        try {
          TvShowInfo seasons = worker.get();
          if (seasons == null) {
            System.out.println("Season is null");
            loading.setValue(100, WorkerManager.WORKERID.INFOWORKER);
            return;
          }

          currentMedia.setInfo(seasons);
          tvShowPanel.addTvshowInfo(seasons.getSeasons(), ((TvShow) currentMedia).getSearchSxe());

        } catch (InterruptedException ex) {
          Settings.LOGGER.log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
          Settings.LOGGER.log(Level.SEVERE, null, ex);
        }

        loading.setValue(100, WorkerManager.WORKERID.INFOWORKER);
      } else {
        loading.setValue(worker.getProgress(), WorkerManager.WORKERID.INFOWORKER);
      }
    }
  }

  private class workerListener implements PropertyChangeListener {

    private Worker<Void> worker;
    private WorkerManager.WORKERID id;

    public workerListener(Worker<Void> worker, WorkerManager.WORKERID id) {
      this.worker = worker;
      this.id = id;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
        loading.setValue(100, id);
      } else {
        loading.setValue(worker.getProgress(), id);
      }
    }
  }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JSplitPane MediaSp;
    private JPopupMenu batchProcessMenu;
    private JToolBar btmTb;
    private JPanel centerPnl;
    private JSplitPane centerSp;
    private JButton editBtn;
    private JButton exitBtn;
    private JCheckBox fanartChk;
    private JFileChooser fileChooser;
    private JMenuItem jMenuItem1;
    private JPanel jPanel1;
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
    private Separator separator;
    private JButton settingBtn;
    private JCheckBox thumbChk;
    private JToolBar topTb;
    private JButton tvShowModeBtn;
    private JButton updateBtn;
    // End of variables declaration//GEN-END:variables
}
