/******************************************************************************
 *                                                                             *
 *    Movie Renamer                                                            *
 *    Copyright (C) 2012 Magré Nicolas                                         *
 *                                                                             *
 *    Movie Renamer is free software: you can redistribute it and/or modify    *
 *    it under the terms of the GNU General Public License as published by     *
 *    the Free Software Foundation, either version 3 of the License, or        *
 *    (at your option) any later version.                                      *
 *                                                                             *
 *    This program is distributed in the hope that it will be useful,          *
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            *
 *    GNU General Public License for more details.                             *
 *                                                                             *
 *    You should have received a copy of the GNU General Public License        *
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                             *
 ******************************************************************************/
package fr.free.movierenamer.ui;

import fr.free.movierenamer.Main;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.awt.dnd.DropTarget;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.ui.res.DropFile;
import fr.free.movierenamer.ui.res.ImdbSearchResult;
import fr.free.movierenamer.utils.Loading;
import fr.free.movierenamer.movie.Movie;
import fr.free.movierenamer.movie.MovieFile;
import fr.free.movierenamer.movie.MovieImage;
import fr.free.movierenamer.movie.MovieInfo;
import fr.free.movierenamer.parser.XMLParser;
import fr.free.movierenamer.ui.res.ContextMenuListMouseListener;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.ui.res.IconListRenderer;
import fr.free.movierenamer.utils.Utils;
import fr.free.movierenamer.ui.res.MovieFileFilter;
import fr.free.movierenamer.utils.HttpGet;
import fr.free.movierenamer.utils.Renamer;
import fr.free.movierenamer.utils.Renamed;
import fr.free.movierenamer.worker.ActorWorker;
import fr.free.movierenamer.worker.ImdbInfoWorker;
import fr.free.movierenamer.worker.ImdbSearchWorker;
import fr.free.movierenamer.worker.ListFilesWorker;
import fr.free.movierenamer.worker.MovieImageWorker;
import fr.free.movierenamer.worker.TheMovieDbImageWorker;
import java.awt.Cursor;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import javax.swing.DefaultListCellRenderer;
import plugins.IPluginInfo;

/**
 * Class MovieRenamer
 * @author Nicolas Magré
 */
public class MovieRenamer extends javax.swing.JFrame {

  private Settings setting;
  private DefaultListModel movieFileNameModel;
  private DefaultListModel searchResModel;
  private Movie currentMovie;
  private ResourceBundle bundle = ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle");
  private String sError = bundle.getString("error");
  private DropFile dropFile;
  private MoviePanel moviePnl;
  private LoadingDialog loading;
  private final int SEARCHWORKER = 0;
  private final int INFOWORKER = 1;
  private final int THUMBWORKER = 2;
  private final int FANARTWORKER = 3;
  private final int ACTORWORKER = 4;
  private ArrayList<MovieFile> mvFile;
  private ContextMenuListMouseListener contex;
  private IPluginInfo[] pluginsInfo;
  private ArrayList<Renamed> renamedMovieFile;

  public MovieRenamer(Settings setting, IPluginInfo[] pluginsInfo) {

    this.setting = setting;
    this.pluginsInfo = pluginsInfo;

    renamedMovieFile = new ArrayList<Renamed>();

    loadRenamedMovie();

    contex = new ContextMenuListMouseListener();
    contex.addPropertyChangeListener(new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getPropertyName().equals("remove")) {
          int index = (Integer) pce.getNewValue();
          if (index == -1) return;
          mvFile.remove(index);
          movieFileNameModel.remove(index);
          clearInterface(false, true);
        } else if (pce.getPropertyName().equals("search")) {
          if (currentMovie == null) return;
          searchMovieImdb(currentMovie.getSearch());
        }
      }
    });

    initComponents();

    for (int i = 0; i < MovieRenamer.this.pluginsInfo.length; i++) {
      if (this.pluginsInfo[i].getRenameStrChk() != null)
        btmTb.add(new JCheckBox(this.pluginsInfo[i].getRenameStrChk()));
    }

    setIconImage(Utils.getImageFromJAR("/image/icon-32.gif", getClass()));

    fileChooser.setFileFilter(new MovieFileFilter(setting));
    fileChooser.setAcceptAllFileFilterUsed(false);//Remove AcceptAll as an available choice in the choosable filter list

    movieList.addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent evt) {
        if (movieList.getSelectedIndex() == -1) return;
        MovieFile movieFile = (MovieFile) movieList.getSelectedValue();
        movieList.ensureIndexIsVisible(movieList.getSelectedIndex());
        if (MovieRenamer.this.setting.showNotaMovieWarn && movieFile.isWarning()) {
          int pos = 0;
          boolean next = (movieList.getSelectedIndex() < mvFile.size() - 1);

          String[] choices = new String[next ? 3 : 2];
          choices[pos++] = bundle.getString("continue");
          if (next) choices[pos++] = bundle.getString("next");
          choices[pos++] = bundle.getString("cancel");

          int res = JOptionPane.showOptionDialog(
            MovieRenamer.this, movieFile.getFile().getName() + Utils.SPACE + bundle.getString("notAMovie") + Utils.ENDLINE
            + Settings.softName + Utils.SPACE + bundle.getString("onlyRename") + Utils.ENDLINE, bundle.getString("warning"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, choices, "");
          if (!next && res == 1) return;
          if (next) {
            if (res == 2) return;
            if (res == 1) {
              pos = movieList.getSelectedIndex() + 1;
              while (pos < mvFile.size())
                if (!mvFile.get(pos).isRenamed() && !mvFile.get(pos).wasRenamed()) {
                  movieList.setSelectedIndex(movieList.getSelectedIndex() + 1);
                  break;
                } else pos++;

              if (mvFile.size() <= pos)
                JOptionPane.showMessageDialog(MovieRenamer.this, bundle.getString("endOfList"), "Information", JOptionPane.INFORMATION_MESSAGE);
              return;
            }
          }
        }

        clearInterface(false, true);
        currentMovie = new Movie(movieFile, MovieRenamer.this.setting.nameFilters);
        searchField.setText(currentMovie.getSearch());
        renameBtn.setEnabled(false);
        editBtn.setEnabled(false);
        renamedField.setText(Utils.EMPTY);
        renamedField.setEnabled(false);
        if (MovieRenamer.this.setting.autoSearchMovie) searchMovieImdb(currentMovie.getSearch());
        else {
          searchBtn.setEnabled(true);
          searchField.setEnabled(true);
        }
      }
    });

    searchResultList.addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent evt) {

        if (searchResultList.getSelectedIndex() == -1) return;
        if (!loading.isShown()) loadDial(false, true);
        currentMovie.setImdbTitle(((ImdbSearchResult) searchResultList.getSelectedValue()).getTitle());
        clearInterface(false, false);
        getMovieInfo(((ImdbSearchResult) searchResultList.getSelectedValue()).getImdbId());
      }
    });

    moviePnl = new MoviePanel(setting, pluginsInfo);

    dropFile = new DropFile(setting, renamedMovieFile, new FileWorkerListener(), this);
    new DropTarget(movieList, dropFile);

    loadInterface();
    setTitle(Settings.softName + "-" + setting.getVersion());
    setLocationRelativeTo(null);

    nfoChk.setText(setting.nfoType == 0 ? bundle.getString("nfoXbmc"):bundle.getString("nfoMediaPortal"));
    if (setting.checkUpdate)
      checkUpdate(false);
  }

  public void setMouseIcon(boolean loading) {
    Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
    Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    setCursor(loading ? hourglassCursor : normalCursor);
  }

  private void loadRenamedMovie() {
    if (new File(setting.renamedFile).exists()) {
      XMLParser<ArrayList<Renamed>> mmp = new XMLParser<ArrayList<Renamed>>(setting.renamedFile, Renamed.class);
      try {
        try {
          renamedMovieFile = mmp.parseXml();
        } catch (InterruptedException ex) {
          Logger.getLogger(MovieRenamer.class.getName()).log(Level.SEVERE, null, ex);
        }
      } catch (IOException e) {
        setting.getLogger().log(Level.SEVERE, e.toString());
      }
    }
  }

  private void loadInterface() {
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

  //Only call in EDT
  private void clearInterface(boolean movieList, boolean searchList) {
    if (!SwingUtilities.isEventDispatchThread()) setting.getLogger().log(Level.SEVERE, "movierenamer : clearInterface is not running in EDT");
    if (currentMovie != null) currentMovie.clear();

    if (movieList) {
      if (movieFileNameModel != null) movieFileNameModel.clear();
      ((TitledBorder) movieScroll.getBorder()).setTitle(bundle.getString("movies"));
      movieScroll.validate();
      movieScroll.repaint();
    }
    if (searchList) {
      if (searchResModel != null) searchResModel.clear();
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

  private void searchMovieImdb(String searchTitle) {
    try {
      loadDial(true, setting.selectFrstRes);
      ImdbSearchWorker imdbsw = new ImdbSearchWorker(MovieRenamer.this, searchTitle, setting);
      imdbsw.addPropertyChangeListener(new SearchWorkerListener(imdbsw));
      imdbsw.execute();
    } catch (MalformedURLException ex) {
      setting.getLogger().log(Level.SEVERE, null, ex);
    } catch (UnsupportedEncodingException ex) {
      setting.getLogger().log(Level.SEVERE, null, ex);
    }
  }

  private void loadDial(boolean search, boolean movieinfo) {
    final ArrayList<Loading> loadingWorker = new ArrayList<Loading>();
    if (search) loadingWorker.add(new Loading(bundle.getString("imSearch"), true, 100, SEARCHWORKER));
    if (movieinfo) {
      loadingWorker.add(new Loading(bundle.getString("movieInf"), true, 100, INFOWORKER));
      if (setting.movieInfoPanel && setting.thumb) loadingWorker.add(new Loading(bundle.getString("thumbnails"), false, 100, THUMBWORKER));
      if (setting.movieInfoPanel && setting.fanart) loadingWorker.add(new Loading("Fanarts", false, 100, FANARTWORKER));
      if (setting.movieInfoPanel && setting.actorImage) loadingWorker.add(new Loading(bundle.getString("actors"), false, 100, ACTORWORKER));
    }

    loading = new LoadingDialog(loadingWorker, this);
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        loading.setVisible(true);
      }
    });
  }

  private void getMovieInfo(String imdbId) {
    try {
      currentMovie.clear();
      currentMovie.setImdbId(imdbId);
      ImdbInfoWorker imdbiw = new ImdbInfoWorker(MovieRenamer.this, imdbId, setting);
      if ((setting.thumb || setting.fanart) && setting.movieInfoPanel) {
        TheMovieDbImageWorker tmdbiw = new TheMovieDbImageWorker(currentMovie.getImdbId(), MovieRenamer.this, setting);
        tmdbiw.addPropertyChangeListener(new MovieInfoListener(tmdbiw));
        tmdbiw.execute();
      }
      imdbiw.addPropertyChangeListener(new MovieInfoListener(imdbiw));
      imdbiw.execute();

    } catch (MalformedURLException ex) {
      setting.getLogger().log(Level.SEVERE, null, ex);
    }
  }

  public final void checkUpdate(boolean showAlready) {
    String ver = setting.getVersion();
    if (!ver.equals(""))
      try {
        URL url = new URL("http://movierenamer.free.fr/update.php?version=" + ver.substring(0, setting.getVersion().lastIndexOf("_")) + "&amp;lang=" + setting.locale);
        HttpGet http = new HttpGet(url);
        String newVerFile = http.sendGetRequest(false, "UTF-8");
        if (newVerFile.equals("")) {
          if (showAlready) JOptionPane.showMessageDialog(this, "Movie Renamer is already up to date", "Update", JOptionPane.INFORMATION_MESSAGE);
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

          boolean updated = Utils.downloadFile(url, jarFile.getAbsolutePath());

          if (updated) {
            n = JOptionPane.showConfirmDialog(this, Settings.softName + Utils.SPACE + bundle.getString("wantRestartAppUpdate"), "Question", JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION)
              if (!Utils.restartApplication(jarFile)) JOptionPane.showMessageDialog(this, bundle.getString("cantRestart"), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
              else {
                dispose();
                System.exit(0);
                return;
              }

          } else {
            JOptionPane.showMessageDialog(this, bundle.getString("cantRestart"), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
            return;
          }
        }
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
      }
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    fileChooser = new javax.swing.JFileChooser();
    topTb = new javax.swing.JToolBar();
    openBtn = new javax.swing.JButton();
    jSeparator1 = new javax.swing.JToolBar.Separator();
    editBtn = new javax.swing.JButton();
    separator = new javax.swing.JToolBar.Separator();
    updateBtn = new javax.swing.JButton();
    settingBtn = new javax.swing.JButton();
    exitBtn = new javax.swing.JButton();
    centerPnl = new javax.swing.JPanel();
    centerSp = new javax.swing.JSplitPane();
    movieScroll = new javax.swing.JScrollPane();
    movieList = new javax.swing.JList();
    searchSp = new javax.swing.JSplitPane();
    searchPnl = new javax.swing.JPanel();
    searchScroll = new javax.swing.JScrollPane();
    searchResultList = new javax.swing.JList();
    searchField = new javax.swing.JTextField();
    searchBtn = new javax.swing.JButton();
    resultLbl = new javax.swing.JLabel();
    btmTb = new javax.swing.JToolBar();
    renameBtn = new javax.swing.JButton();
    renamedField = new javax.swing.JTextField();
    thumbChk = new javax.swing.JCheckBox();
    fanartChk = new javax.swing.JCheckBox();
    nfoChk = new javax.swing.JCheckBox();

    fileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
    fileChooser.setMultiSelectionEnabled(true);

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setMinimumSize(new java.awt.Dimension(770, 570));

    topTb.setFloatable(false);
    topTb.setRollover(true);

    openBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/folder-video.png"))); // NOI18N
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle"); // NOI18N
    openBtn.setToolTipText(bundle.getString("openFolderBtn")); // NOI18N
    openBtn.setFocusable(false);
    openBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    openBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    openBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        openBtnActionPerformed(evt);
      }
    });
    topTb.add(openBtn);
    topTb.add(jSeparator1);

    editBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/accessories-text-editor-6-24.png"))); // NOI18N
    editBtn.setToolTipText(bundle.getString("edit")); // NOI18N
    editBtn.setEnabled(false);
    editBtn.setFocusable(false);
    editBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    editBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    editBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        editBtnActionPerformed(evt);
      }
    });
    topTb.add(editBtn);
    topTb.add(separator);
    topTb.add(Box.createHorizontalGlue());

    updateBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/system-software-update-5.png"))); // NOI18N
    updateBtn.setToolTipText(bundle.getString("updateBtn")); // NOI18N
    updateBtn.setFocusable(false);
    updateBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    updateBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    updateBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        updateBtnActionPerformed(evt);
      }
    });
    topTb.add(updateBtn);

    settingBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/system-settings.png"))); // NOI18N
    settingBtn.setToolTipText(bundle.getString("settingBtn")); // NOI18N
    settingBtn.setFocusable(false);
    settingBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    settingBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    settingBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        settingBtnActionPerformed(evt);
      }
    });
    topTb.add(settingBtn);

    exitBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/application-exit.png"))); // NOI18N
    exitBtn.setToolTipText(bundle.getString("exitBtn")); // NOI18N
    exitBtn.setFocusable(false);
    exitBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    exitBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    exitBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        exitBtnActionPerformed(evt);
      }
    });
    topTb.add(exitBtn);

    getContentPane().add(topTb, java.awt.BorderLayout.PAGE_START);

    centerSp.setDividerLocation(300);

    movieScroll.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("movieListTitle"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 13))); // NOI18N

    movieList.setFont(new java.awt.Font("Dialog", 0, 12));
    movieList.addMouseListener(contex);
    movieScroll.setViewportView(movieList);

    centerSp.setTopComponent(movieScroll);

    searchSp.setDividerLocation(200);
    searchSp.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

    searchPnl.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("searchTitle"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 13))); // NOI18N

    searchResultList.setFont(new java.awt.Font("Dialog", 0, 12));
    searchScroll.setViewportView(searchResultList);

    searchField.setEnabled(false);
    searchField.addMouseListener(new fr.free.movierenamer.ui.res.ContextMenuFieldMouseListener());

    searchBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/system-search-3.png"))); // NOI18N
    searchBtn.setToolTipText(bundle.getString("searchOnImdb")); // NOI18N
    searchBtn.setEnabled(false);
    searchBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        searchBtnActionPerformed(evt);
      }
    });

    resultLbl.setText(bundle.getString("searchResListTitle")); // NOI18N

    javax.swing.GroupLayout searchPnlLayout = new javax.swing.GroupLayout(searchPnl);
    searchPnl.setLayout(searchPnlLayout);
    searchPnlLayout.setHorizontalGroup(
      searchPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, searchPnlLayout.createSequentialGroup()
        .addComponent(searchField, javax.swing.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(searchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
      .addGroup(searchPnlLayout.createSequentialGroup()
        .addComponent(resultLbl)
        .addContainerGap(501, Short.MAX_VALUE))
      .addComponent(searchScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
    );
    searchPnlLayout.setVerticalGroup(
      searchPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(searchPnlLayout.createSequentialGroup()
        .addGroup(searchPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(searchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(resultLbl)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(searchScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
    );

    searchSp.setLeftComponent(searchPnl);

    centerSp.setRightComponent(searchSp);

    javax.swing.GroupLayout centerPnlLayout = new javax.swing.GroupLayout(centerPnl);
    centerPnl.setLayout(centerPnlLayout);
    centerPnlLayout.setHorizontalGroup(
      centerPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(centerSp, javax.swing.GroupLayout.DEFAULT_SIZE, 922, Short.MAX_VALUE)
    );
    centerPnlLayout.setVerticalGroup(
      centerPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(centerSp, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
    );

    getContentPane().add(centerPnl, java.awt.BorderLayout.CENTER);

    btmTb.setFloatable(false);
    btmTb.setRollover(true);

    renameBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dialog-ok-2.png"))); // NOI18N
    renameBtn.setText(bundle.getString("rename")); // NOI18N
    renameBtn.setEnabled(false);
    renameBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    renameBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        renameBtnActionPerformed(evt);
      }
    });
    btmTb.add(renameBtn);

    renamedField.setEnabled(false);
    renamedField.addMouseListener(new fr.free.movierenamer.ui.res.ContextMenuFieldMouseListener());
    btmTb.add(renamedField);

    thumbChk.setText(bundle.getString("thumb")); // NOI18N
    thumbChk.setToolTipText(bundle.getString("downThumb")); // NOI18N
    thumbChk.setFocusable(false);
    thumbChk.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btmTb.add(thumbChk);

    fanartChk.setText("Fanart");
    fanartChk.setToolTipText(bundle.getString("downFanart")); // NOI18N
    fanartChk.setFocusable(false);
    fanartChk.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btmTb.add(fanartChk);

    nfoChk.setText(bundle.getString("nfoXbmc")); // NOI18N
    nfoChk.setToolTipText(bundle.getString("genNFO")); // NOI18N
    nfoChk.setFocusable(false);
    nfoChk.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    btmTb.add(nfoChk);

    getContentPane().add(btmTb, java.awt.BorderLayout.SOUTH);

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
          nfoChk.setText(setting.nfoType == 0 ? bundle.getString("nfoXbmc"):bundle.getString("nfoMediaPortal"));
          if (setting.interfaceChanged) {
            setting.interfaceChanged = false;
            loadInterface();
            if (currentMovie == null) return;
            if (setting.movieInfoPanel) {
              TheMovieDbImageWorker tmdbiw = null;
              ActorWorker actor = null;

              if (setting.actorImage) {
                moviePnl.clearActorList();
                actor = new ActorWorker(currentMovie.getMovieInfo().getActors(), moviePnl, setting);
                actor.addPropertyChangeListener(new MovieImageListener(actor, ACTORWORKER));
              }

              if (setting.thumb || setting.fanart) {
                if (setting.thumb) moviePnl.clearThumbList();
                if (setting.fanart) moviePnl.clearFanartList();
                tmdbiw = new TheMovieDbImageWorker(currentMovie.getImdbId(), MovieRenamer.this, setting);
                tmdbiw.addPropertyChangeListener(new MovieInfoListener(tmdbiw));
              }
              if (setting.thumb || setting.fanart || setting.actorImage) loadDial(false, true);
              if ((actor != null || tmdbiw != null)) loading.setValue(100, INFOWORKER);
              if (actor != null) actor.execute();
              if (tmdbiw != null) tmdbiw.execute();
            }
          }

          if (currentMovie != null) {
            String dir = "";
            if (setting.createMovieDirectory)
              if (setting.movieDirRenamedTitle == 2)
                dir = setting.movieDir + File.separator;
              else {
                String regex = setting.movieDirRenamedTitle == 1 ? setting.movieFilenameFormat : "<t>";
                dir = currentMovie.getRenamedTitle(regex, setting.separator, setting.renameCase);
                dir = dir.substring(0, dir.lastIndexOf("."));
                dir += File.separator;
              }
            renamedField.setText(dir + currentMovie.getRenamedTitle(setting.movieFilenameFormat, setting.separator, setting.renameCase));
          }
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
      searchMovieImdb(searchField.getText());
    }//GEN-LAST:event_searchBtnActionPerformed

    private void renameBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_renameBtnActionPerformed

      setMouseIcon(true);
      ArrayList<MovieImage> images = moviePnl.getAddedThumb();
      for (int i = 0; i < images.size(); i++) {
        currentMovie.addThumb(images.get(i));
      }

      images = moviePnl.getAddedFanart();
      for (int i = 0; i < images.size(); i++) {
        currentMovie.addFanart(images.get(i));
      }

      int index = movieList.getSelectedIndex();
      if (index == -1) {
        setMouseIcon(false);
        JOptionPane.showMessageDialog(MovieRenamer.this, bundle.getString("noMovieSelected"), sError, JOptionPane.ERROR_MESSAGE);
        return;
      }

      String regex = setting.movieDirRenamedTitle == 1 ? setting.movieFilenameFormat : "<t>";
      String ftitle = currentMovie.getRenamedTitle(regex, setting.separator, setting.renameCase);
      ftitle = ftitle.substring(0, ftitle.lastIndexOf("."));

      Renamer renamer = new Renamer(ftitle, currentMovie.getFile(), renamedField.getText(), setting);

      if (!renamer.rename()) {
        setMouseIcon(false);
        JOptionPane.showMessageDialog(MovieRenamer.this, bundle.getString("renameFileFailed"), sError, JOptionPane.ERROR_MESSAGE);
        return;
      }

      if(renamer.cancel){
        setMouseIcon(false);
        return;
      }

      if (mvFile.get(index).wasRenamed())
        for (int i = 0; i < renamedMovieFile.size(); i++) {
          if (renamedMovieFile.get(i).getMovieFileDest().equals(currentMovie.getFile().getAbsolutePath())) {
            renamedMovieFile.remove(i);
            break;
          }
        }
      renamedMovieFile.add(renamer.getRenamed());

      mvFile.get(index).setRenamed(true);

      boolean createXNFO = false;
      boolean createThumbnail = false;
      boolean createFan = false;

      if (setting.movieInfoPanel) {
        createXNFO = nfoChk.isSelected();
        if (setting.fanart) createFan = fanartChk.isSelected();
        if (setting.thumb) createThumbnail = thumbChk.isSelected();
      }

      renamer.createNFO(createXNFO, setting.nfoType == 0 ? currentMovie.getXbmcNFOFromMovie():currentMovie.getMediaPortalNFOFromMovie());
      renamer.createThumb(createThumbnail, moviePnl.getSelectedThumb(setting.thumbSize));
      renamer.createFanart(createFan, moviePnl.getSelectedFanart(setting.fanartSize));

      for (int i = 0; i < pluginsInfo.length; i++) {
        if (pluginsInfo[i].getRenameStrChk() != null)
          pluginsInfo[i].setMovieFileNameNoExt(renamer.getNewFileNoExt());
        pluginsInfo[i].onRename(null);
      }

      mvFile.get(index).setFile(renamer.getNewFile());
      movieFileNameModel = new DefaultListModel();
      for (int i = 0; i < mvFile.size(); i++) {
        movieFileNameModel.addElement(mvFile.get(i));
      }

      movieList.setCellRenderer(new IconListRenderer<MovieFile>(mvFile));
      movieList.setModel(movieFileNameModel);

      try {
        String endl = Utils.ENDLINE;
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(setting.renamedFile), "UTF-8"));
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + endl);
        out.write("<Movie_Renamer_Renamed>" + endl);
        for (int i = 0; i < renamedMovieFile.size(); i++) {
          out.write("  <renamedMovie title=\"" + Utils.escapeXML(renamedMovieFile.get(i).getTitle()) + "\">" + endl);
          out.write("    <movie src=\"" + Utils.escapeXML(renamedMovieFile.get(i).getMovieFileSrc()) + "\"");
          out.write(" dest=\"" + Utils.escapeXML(renamedMovieFile.get(i).getMovieFileDest()) + "\"/>" + endl);
          out.write("    <date>" + Utils.escapeXML(renamedMovieFile.get(i).getDate()) + "</date>" + endl);
          out.write("  </renamedMovie>" + endl);
        }
        out.write("</Movie_Renamer_Renamed>" + endl);
        out.close();
      } catch (IOException ex) {
        setting.getLogger().log(Level.SEVERE, ex.toString());
      }

      setMouseIcon(false);
      currentMovie = null;
      clearInterface(false, true);

      int pos = index + 1;
      while (pos < mvFile.size())
        if (!mvFile.get(pos).isRenamed() && !mvFile.get(pos).wasRenamed()) {
          movieList.setSelectedIndex(pos);
          break;
        } else pos++;

      if (mvFile.size() <= pos) {
        JOptionPane.showMessageDialog(MovieRenamer.this, bundle.getString("endOfList"), "Information", JOptionPane.INFORMATION_MESSAGE);
        return;
      }
    }//GEN-LAST:event_renameBtnActionPerformed

    private void editBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_editBtnActionPerformed
      final InfoEditorFrame editorFrame = new InfoEditorFrame(currentMovie.getMovieInfo(), MovieRenamer.this);
      editorFrame.addPropertyChangeListener(new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent pce) {
          if (pce.getPropertyName().equals("movieInfo")) {
            currentMovie.setMovieInfo((MovieInfo) pce.getNewValue());
            moviePnl.addMovieInfo(currentMovie.getMovieInfo());
          }
        }
      });

      SwingUtilities.invokeLater(new Runnable() {

        @Override
        public void run() {
          editorFrame.setVisible(true);
        }
      });

    }//GEN-LAST:event_editBtnActionPerformed

    private void updateBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_updateBtnActionPerformed
      checkUpdate(true);
    }//GEN-LAST:event_updateBtnActionPerformed

  public class SearchWorkerListener implements PropertyChangeListener {

    private ImdbSearchWorker worker;

    public SearchWorkerListener(ImdbSearchWorker worker) {
      this.worker = worker;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
        try {
          ArrayList<ImdbSearchResult> objects = worker.get();

          searchBtn.setEnabled(true);
          searchField.setEnabled(true);
          
          if (objects == null) {
            loading.dispose();
            return;
          }
          searchResModel = new DefaultListModel();
          resultLbl.setText(bundle.getString("searchResListTitle") + " : " + objects.size());
          for (int i = 0; i < objects.size(); i++) {
            searchResModel.addElement(objects.get(i));
          }

          // Display thumbs in result list
          if (setting.displayThumbResult) searchResultList.setCellRenderer(new IconListRenderer<ImdbSearchResult>(objects));
          else searchResultList.setCellRenderer(new DefaultListCellRenderer());

          searchResultList.setModel(searchResModel);
          if (objects.isEmpty()) {
            JOptionPane.showMessageDialog(MovieRenamer.this, bundle.getString("noResult"), sError, JOptionPane.ERROR_MESSAGE);
            loading.setVisible(false);
          }

        } catch (InterruptedException ex) {
          setting.getLogger().log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
          setting.getLogger().log(Level.SEVERE, null, ex);
        }
        loading.setValue(100, SEARCHWORKER);
        if (!searchResModel.isEmpty())
          if (MovieRenamer.this.setting.selectFrstRes)
            searchResultList.setSelectedIndex(0);
      } else loading.setValue(worker.getProgress(), SEARCHWORKER);
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
          currentMovie = null;
          ArrayList<MovieFile> objects = worker.get();
          mvFile = objects;
          movieFileNameModel = new DefaultListModel();

          for (int i = 0; i < objects.size(); i++) {
            movieFileNameModel.addElement(objects.get(i));
          }

          ((TitledBorder) movieScroll.getBorder()).setTitle(Utils.EMPTY + movieFileNameModel.size() + Utils.SPACE + bundle.getString("movies"));

          movieList.setCellRenderer(new IconListRenderer<MovieFile>(objects));
          movieScroll.repaint();

          movieList.setModel(movieFileNameModel);
          if (movieFileNameModel.isEmpty())
            JOptionPane.showMessageDialog(MovieRenamer.this, bundle.getString("noMovieFound"), sError, JOptionPane.ERROR_MESSAGE);
          else if (setting.selectFrstMovie)
            movieList.setSelectedIndex(0);

        } catch (InterruptedException ex) {
          setting.getLogger().log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
          setting.getLogger().log(Level.SEVERE, null, ex);
        }
        if (progressMonitor != null) progressMonitor.close();
        progressMonitor = null;

      } else if (progressMonitor != null)
        progressMonitor.setProgress(worker.getProgress());
    }
  }

  private class MovieInfoListener implements PropertyChangeListener {

    private SwingWorker imdbiw;

    public MovieInfoListener(SwingWorker imdbiw) {
      this.imdbiw = imdbiw;
    }

    @SuppressWarnings("unchecked") // Remove warning : Cast Object to ArrayList<ArrayList<MovieImage>>
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getNewValue().equals(SwingWorker.StateValue.DONE))
        try {
          Object obj = imdbiw.get();
          if (obj == null) {
            loading.setValue(100, INFOWORKER);
            return;
          }

          if (obj instanceof MovieInfo) {
            currentMovie.setMovieInfo((MovieInfo) obj);

            String dir = "";
            if (setting.createMovieDirectory)
              if (setting.movieDirRenamedTitle == 2)
                dir = setting.movieDir + File.separator;
              else {
                String regex = setting.movieDirRenamedTitle == 1 ? setting.movieFilenameFormat : "<t>";
                dir = currentMovie.getRenamedTitle(regex, setting.separator, setting.renameCase);
                dir = dir.substring(0, dir.lastIndexOf("."));
                dir += File.separator;
              }

            renamedField.setText(dir + currentMovie.getRenamedTitle(setting.movieFilenameFormat, setting.separator, setting.renameCase));
            renameBtn.setEnabled(true);
            renamedField.setEnabled(true);
            editBtn.setEnabled(true);
            moviePnl.addMovieInfo(currentMovie.getMovieInfo());

            System.out.println("Load plugin");
            for (int i = 0; i < MovieRenamer.this.pluginsInfo.length; i++) {
              System.out.println(MovieRenamer.this.pluginsInfo[i].getName());
              String renamedTitle = currentMovie.getRenamedTitle("<t>", setting.separator, setting.renameCase);
              renamedTitle = renamedTitle.substring(0, renamedTitle.lastIndexOf("."));
              MovieRenamer.this.pluginsInfo[i].onSearchFinish(renamedTitle);
            }

            ActorWorker actor = new ActorWorker(currentMovie.getMovieInfo().getActors(), moviePnl, setting);
            actor.addPropertyChangeListener(new MovieImageListener(actor, ACTORWORKER));
            actor.execute();
            loading.setValue(100, INFOWORKER);
          }
          if (obj instanceof ArrayList) {
            ArrayList<ArrayList<MovieImage>> movieimage = (ArrayList<ArrayList<MovieImage>>) obj;
            
            currentMovie.setThumbs(movieimage.get(0));
            currentMovie.setFanarts(movieimage.get(1));
            MovieImageWorker thumb = new MovieImageWorker(currentMovie.getThumbs(), 0, Cache.thumb, moviePnl, setting);
            MovieImageWorker fanart = new MovieImageWorker(currentMovie.getFanarts(), 1, Cache.fanart, moviePnl, setting);

            thumb.addPropertyChangeListener(new MovieImageListener(thumb, THUMBWORKER));
            fanart.addPropertyChangeListener(new MovieImageListener(fanart, FANARTWORKER));

            if (setting.thumb) thumb.execute();
            if (setting.fanart) fanart.execute();
            if (renameBtn.isEnabled()) loading.setValue(100, INFOWORKER);
          }
        } catch (InterruptedException ex) {
          setting.getLogger().log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
          setting.getLogger().log(Level.SEVERE, null, ex);
        }
      else loading.setValue(imdbiw.getProgress(), INFOWORKER);
    }
  }

  private class MovieImageListener implements PropertyChangeListener {

    private SwingWorker miw;
    private int id;

    public MovieImageListener(SwingWorker miw, int id) {
      this.miw = miw;
      this.id = id;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getNewValue().equals(SwingWorker.StateValue.DONE))
        loading.setValue(100, id);
      else loading.setValue(miw.getProgress(), id);
    }
  }
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JToolBar btmTb;
  private javax.swing.JPanel centerPnl;
  private javax.swing.JSplitPane centerSp;
  private javax.swing.JButton editBtn;
  private javax.swing.JButton exitBtn;
  private javax.swing.JCheckBox fanartChk;
  private javax.swing.JFileChooser fileChooser;
  private javax.swing.JToolBar.Separator jSeparator1;
  private javax.swing.JList movieList;
  private javax.swing.JScrollPane movieScroll;
  private javax.swing.JCheckBox nfoChk;
  private javax.swing.JButton openBtn;
  private javax.swing.JButton renameBtn;
  private javax.swing.JTextField renamedField;
  private javax.swing.JLabel resultLbl;
  private javax.swing.JButton searchBtn;
  private javax.swing.JTextField searchField;
  private javax.swing.JPanel searchPnl;
  private javax.swing.JList searchResultList;
  private javax.swing.JScrollPane searchScroll;
  private javax.swing.JSplitPane searchSp;
  private javax.swing.JToolBar.Separator separator;
  private javax.swing.JButton settingBtn;
  private javax.swing.JCheckBox thumbChk;
  private javax.swing.JToolBar topTb;
  private javax.swing.JButton updateBtn;
  // End of variables declaration//GEN-END:variables
}
