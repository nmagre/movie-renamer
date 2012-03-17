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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ProgressMonitor;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
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
import fr.free.movierenamer.movie.MoviePerson;
import fr.free.movierenamer.ui.res.ContextMenuFieldMouseListener;
import fr.free.movierenamer.ui.res.ContextMenuListMouseListener;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.ui.res.IconListRenderer;
import fr.free.movierenamer.utils.Utils;
import fr.free.movierenamer.ui.res.MovieFileFilter;
import fr.free.movierenamer.utils.Renamer;
import fr.free.movierenamer.utils.Renamed;
import fr.free.movierenamer.worker.ActorWorker;
import fr.free.movierenamer.worker.ImdbInfoWorker;
import fr.free.movierenamer.worker.ImdbSearchWorker;
import fr.free.movierenamer.worker.ListFilesWorker;
import fr.free.movierenamer.worker.MovieImageWorker;
import fr.free.movierenamer.worker.TheMovieDbInfoWorker;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.DefaultListCellRenderer;

/**
 * Class MovieRenamer
 * @author Nicolas Magré
 */
public class MovieRenamer extends javax.swing.JFrame {

  private Settings setting;
  //List Model
  private DefaultListModel movieFileNameModel;
  private DefaultListModel searchResModel;
  private Movie currentMovie;
  private ResourceBundle bundle = ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle");
  private String sError = bundle.getString("error");
  private DropFile dropFile;
  private MoviePanel movieImagePnl;
  private LoadingDialog loading;
  private final int SEARCHWORKER = 0;
  private final int INFOWORKER = 1;
  private final int THUMBWORKER = 2;
  private final int FANARTWORKER = 3;
  private final int ACTORWORKER = 4;
  private ArrayList<MovieFile> mvFile;
  private ContextMenuListMouseListener contex;

  /** Creates new form MovieRenamerMain
   * @param setting
   */
  public MovieRenamer(Settings setting) {

    this.setting = setting;
    contex = new ContextMenuListMouseListener();
    contex.addPropertyChangeListener(new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getPropertyName().equals("remove")) {
          int index = (Integer) pce.getNewValue();
          if(index == -1) return;
          mvFile.remove(index);
          movieFileNameModel.remove(index);
          clearInterface(false, true);
        }
        else if(pce.getPropertyName().equals("search")){
          if(currentMovie == null) return;
          searchMovieImdb(currentMovie.getSearch());
        }
      }
    });

    initComponents();

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
              movieList.setSelectedIndex(movieList.getSelectedIndex() + 1);
              return;
            }
          }
        }

        clearInterface(false, true);
        currentMovie = new Movie(movieFile, MovieRenamer.this.setting.nameFilters);
        searchField.setText(currentMovie.getSearch());
        renameBtn.setEnabled(false);
        renamedField.setText(Utils.EMPTY);
        renamedField.setEnabled(false);
        if(MovieRenamer.this.setting.autoSearchMovie) searchMovieImdb(currentMovie.getSearch());
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

    movieImagePnl = new MoviePanel(setting);

    dropFile = new DropFile(setting, new FileWorkerListener(), this);
    new DropTarget(movieList, dropFile);

    loadInterface();
    setTitle(Settings.softName + "-" + setting.getVersion());
    setLocationRelativeTo(null);
  }

  private void loadInterface() {
    if (!setting.movieInfoPanel) {
      searchSp.remove(movieImagePnl);
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
      movieImagePnl.setDisplay(setting);
      searchSp.setBottomComponent(movieImagePnl);
      centerSp.setDividerLocation(300);
      searchSp.setDividerLocation(200);
    }
    thumbChk.setVisible(setting.movieInfoPanel && setting.thumb);
    fanartChk.setVisible(setting.movieInfoPanel && setting.fanart);
    nfoChk.setVisible(setting.movieInfoPanel);
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
    movieImagePnl.clearList();
    renameBtn.setEnabled(false);
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

    loading = new LoadingDialog(loadingWorker, MovieRenamer.this);
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
        TheMovieDbInfoWorker tmdbiw = new TheMovieDbInfoWorker(currentMovie, MovieRenamer.this, setting);
        tmdbiw.addPropertyChangeListener(new MovieInfoListener(tmdbiw));
        tmdbiw.execute();
      }
      imdbiw.addPropertyChangeListener(new MovieInfoListener(imdbiw));
      imdbiw.execute();

    } catch (MalformedURLException ex) {
      setting.getLogger().log(Level.SEVERE, null, ex);
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

    fileChooser = new JFileChooser();
    topTb = new JToolBar();
    openBtn = new JButton();
    separator = new Separator();
    updateBtn = new JButton();
    settingBtn = new JButton();
    exitBtn = new JButton();
    centerPnl = new JPanel();
    centerSp = new JSplitPane();
    movieScroll = new JScrollPane();
    movieList = new JList();
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

    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setMinimumSize(new Dimension(770, 570));

    topTb.setFloatable(false);
    topTb.setRollover(true);

    openBtn.setIcon(new ImageIcon(getClass().getResource("/image/folder-video.png"))); // NOI18N
    ResourceBundle bundle = ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle"); // NOI18N
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
    topTb.add(separator);
    topTb.add(Box.createHorizontalGlue());

    updateBtn.setIcon(new ImageIcon(getClass().getResource("/image/system-software-update-5.png"))); // NOI18N
    updateBtn.setToolTipText(bundle.getString("updateBtn")); // NOI18N
    updateBtn.setEnabled(false);
    updateBtn.setFocusable(false);
    updateBtn.setHorizontalTextPosition(SwingConstants.CENTER);
    updateBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
    topTb.add(updateBtn);

    settingBtn.setIcon(new ImageIcon(getClass().getResource("/image/system-settings.png"))); // NOI18N
    settingBtn.setToolTipText(bundle.getString("settingBtn")); // NOI18N
    settingBtn.setFocusable(false);
    settingBtn.setHorizontalTextPosition(SwingConstants.CENTER);
    settingBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
    settingBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        settingBtnActionPerformed(evt);
      }
    });
    topTb.add(settingBtn);

    exitBtn.setIcon(new ImageIcon(getClass().getResource("/image/application-exit.png"))); // NOI18N
    exitBtn.setToolTipText(bundle.getString("exitBtn")); // NOI18N
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


    movieScroll.setBorder(BorderFactory.createTitledBorder(null, bundle.getString("movieListTitle"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 13))); // NOI18N
    movieList.setFont(new Font("Dialog", 0, 12));
    movieList.addMouseListener(contex);
    movieScroll.setViewportView(movieList);

    centerSp.setTopComponent(movieScroll);

    searchSp.setDividerLocation(200);

    searchSp.setOrientation(JSplitPane.VERTICAL_SPLIT);

    searchPnl.setBorder(BorderFactory.createTitledBorder(null, bundle.getString("searchTitle"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 13))); // NOI18N
    searchResultList.setFont(new Font("Dialog", 0, 12));
    searchScroll.setViewportView(searchResultList);

    searchField.setEnabled(false);
    searchField.addMouseListener(new ContextMenuFieldMouseListener());

    searchBtn.setIcon(new ImageIcon(getClass().getResource("/image/system-search-3.png"))); // NOI18N
    searchBtn.setToolTipText(bundle.getString("searchOnImdb")); // NOI18N
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
        .addComponent(searchField, GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(searchBtn, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
      .addGroup(searchPnlLayout.createSequentialGroup()
        .addComponent(resultLbl)
        .addContainerGap(501, Short.MAX_VALUE))
      .addComponent(searchScroll, GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
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
        .addComponent(searchScroll, GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
    );

    searchSp.setLeftComponent(searchPnl);

    centerSp.setRightComponent(searchSp);

    GroupLayout centerPnlLayout = new GroupLayout(centerPnl);
    centerPnl.setLayout(centerPnlLayout);
    centerPnlLayout.setHorizontalGroup(
      centerPnlLayout.createParallelGroup(Alignment.LEADING)
      .addComponent(centerSp, GroupLayout.DEFAULT_SIZE, 922, Short.MAX_VALUE)
    );
    centerPnlLayout.setVerticalGroup(
      centerPnlLayout.createParallelGroup(Alignment.LEADING)
      .addComponent(centerSp, GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
    );

    getContentPane().add(centerPnl, BorderLayout.CENTER);

    btmTb.setFloatable(false);
    btmTb.setRollover(true);

    renameBtn.setIcon(new ImageIcon(getClass().getResource("/image/dialog-ok-2.png"))); // NOI18N
    renameBtn.setText(bundle.getString("rename")); // NOI18N
    renameBtn.setEnabled(false);
    renameBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
    renameBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        renameBtnActionPerformed(evt);
      }
    });
    btmTb.add(renameBtn);

    renamedField.setEnabled(false);
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

    nfoChk.setText("Xbmc NFO");
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
          Settings oldSetting = setting;
          setting = set.getSetting();

          if (setting.interfaceChanged) {
            setting.interfaceChanged = false;
            loadInterface();
            if (currentMovie == null) return;
            if (setting.movieInfoPanel) {
              TheMovieDbInfoWorker tmdbiw = null;
              ActorWorker actor = null;

              if (setting.actorImage) {
                movieImagePnl.clearActorList();
                actor = new ActorWorker(currentMovie.getPersons(MoviePerson.ACTOR), movieImagePnl, setting);
                actor.addPropertyChangeListener(new MovieImageListener(actor, ACTORWORKER));
              }

              if (setting.thumb || setting.fanart) {
                tmdbiw = new TheMovieDbInfoWorker(currentMovie, MovieRenamer.this, setting);
                tmdbiw.addPropertyChangeListener(new MovieInfoListener(tmdbiw));
              }
              if (setting.thumb || setting.fanart || setting.actorImage) loadDial(false, true);
              if ((actor != null ||  tmdbiw != null)) loading.setValue(100, INFOWORKER);
              if (actor != null) actor.execute();
              if (tmdbiw != null) tmdbiw.execute();
            }
          }

          if (currentMovie != null) {
            String dir = "";
            if (setting.createMovieDirectory) {
              String regex = setting.movieDirRenamedTitle ? setting.movieFilenameFormat : "<t>";
              dir = currentMovie.getRenamedTitle(regex, setting.renameCase);
              dir = dir.substring(0, dir.lastIndexOf("."));
              dir += File.separator;
            }
            renamedField.setText(dir + currentMovie.getRenamedTitle(setting.movieFilenameFormat, setting.renameCase));
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


      ArrayList<MovieImage> images = movieImagePnl.getAddedThumb();
      for (int i = 0; i < images.size(); i++) {
        currentMovie.addThumb(images.get(i));
      }

      images = movieImagePnl.getAddedFanart();
      for (int i = 0; i < images.size(); i++) {
        currentMovie.addFanart(images.get(i));
      }

      DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
      String title = Utils.getFilteredName(currentMovie.getImdbTitle(), new String[0]);
      Renamed renamed = new Renamed(title);
      renamed.setDate(dateFormat.format(new Date()));

      int index = movieList.getSelectedIndex();
      if (index == -1) {
        JOptionPane.showMessageDialog(MovieRenamer.this, bundle.getString("noMovieSelected"), sError, JOptionPane.ERROR_MESSAGE);
        return;
      }

      String regex = setting.movieDirRenamedTitle ? setting.movieFilenameFormat : "<t>";
      String ftitle = currentMovie.getRenamedTitle(regex, setting.renameCase);
      ftitle = ftitle.substring(0, ftitle.lastIndexOf("."));

      Renamer renamer = new Renamer(ftitle, currentMovie.getFile(), renamedField.getText(), setting);

      if (!renamer.rename()) {
        JOptionPane.showMessageDialog(MovieRenamer.this, bundle.getString("renameFileFailed"), sError, JOptionPane.ERROR_MESSAGE);
        return;
      }
      mvFile.get(index).setRenamed(true);

      renamer.createNFO(!(nfoChk.isVisible() && nfoChk.isSelected()), currentMovie.getNFOFromMovie());
      renamer.createThumb(!(thumbChk.isVisible() && thumbChk.isSelected()), movieImagePnl.getSelectedThumb(setting.thumbSize));
      renamer.createFanart(!(fanartChk.isVisible() && fanartChk.isSelected()), movieImagePnl.getSelectedFanart(setting.fanartSize));

      mvFile.get(index).setFile(renamer.getNewFile());
      movieFileNameModel = new DefaultListModel();
      for (int i = 0; i < mvFile.size(); i++) {
        movieFileNameModel.addElement(mvFile.get(i));
      }

      movieList.setCellRenderer(new IconListRenderer<MovieFile>(mvFile));
      movieList.setModel(movieFileNameModel);

      currentMovie = null;
      clearInterface(false, true);
      if (mvFile.size() <= (index + 1)) {
        JOptionPane.showMessageDialog(MovieRenamer.this, bundle.getString("endOfList"), "Information", JOptionPane.INFORMATION_MESSAGE);
        return;
      }

      movieList.setSelectedIndex(index + 1);
    }//GEN-LAST:event_renameBtnActionPerformed

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
          if (objects == null) {
            loading.setValue(100, SEARCHWORKER);
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
          searchBtn.setEnabled(true);
          searchField.setEnabled(true);
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
        progressMonitor = new ProgressMonitor(MovieRenamer.this, "List movie files", Utils.EMPTY, 0, 100);
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
            if (setting.createMovieDirectory) {
              String regex = setting.movieDirRenamedTitle ? setting.movieFilenameFormat : "<t>";
              dir = currentMovie.getRenamedTitle(regex, setting.renameCase);
              dir = dir.substring(0, dir.lastIndexOf("."));
              dir += File.separator;
            }

            renamedField.setText(dir + currentMovie.getRenamedTitle(setting.movieFilenameFormat, setting.renameCase));
            renameBtn.setEnabled(true);
            renamedField.setEnabled(true);
            movieImagePnl.addMovie(currentMovie);

            ActorWorker actor = new ActorWorker(currentMovie.getPersons(MoviePerson.ACTOR), movieImagePnl, setting);
            actor.addPropertyChangeListener(new MovieImageListener(actor, ACTORWORKER));
            actor.execute();
            loading.setValue(100, INFOWORKER);
          }
          if (obj instanceof Movie) {
            currentMovie.setThumbs(((Movie) obj).getThumbs());
            currentMovie.setFanarts(((Movie) obj).getFanarts());
            MovieImageWorker thumb = new MovieImageWorker(currentMovie.getThumbs(), 0, Cache.thumb, movieImagePnl, setting);
            MovieImageWorker fanart = new MovieImageWorker(currentMovie.getFanarts(), 1, Cache.fanart, movieImagePnl, setting);

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
  private JToolBar btmTb;
  private JPanel centerPnl;
  private JSplitPane centerSp;
  private JButton exitBtn;
  private JCheckBox fanartChk;
  private JFileChooser fileChooser;
  private JList movieList;
  private JScrollPane movieScroll;
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
  private JButton updateBtn;
  // End of variables declaration//GEN-END:variables
}
