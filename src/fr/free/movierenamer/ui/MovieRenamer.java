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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.dnd.DropTarget;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
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
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import fr.free.movierenamer.utils.ImdbSearchResult;
import fr.free.movierenamer.utils.Loading;
import fr.free.movierenamer.movie.Movie;
import fr.free.movierenamer.movie.MovieFile;
import fr.free.movierenamer.movie.MovieInfo;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.ui.res.IconListRenderer;
import fr.free.movierenamer.utils.Utils;
import fr.free.movierenamer.movie.MoviePerson;
import fr.free.movierenamer.ui.res.MovieFileFilter;
import fr.free.movierenamer.worker.ActorWorker;
import fr.free.movierenamer.worker.ImdbInfoWorker;
import fr.free.movierenamer.worker.ImdbSearchWorker;
import fr.free.movierenamer.worker.ListFilesWorker;
import fr.free.movierenamer.worker.MovieImageWorker;
import fr.free.movierenamer.worker.TheMovieDbInfoWorker;

/**
 *
 * @author duffy
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

  /** Creates new form MovieRenamerMain
   * @param setting
   */
  public MovieRenamer(Settings setting) {
    this.setting = setting;
    initComponents();

    fileChooser.setFileFilter(new MovieFileFilter(setting));
    fileChooser.setAcceptAllFileFilterUsed(false);//Remove AcceptAll as an available choice in the choosable filter list
    movieList.setDragEnabled(true);
    movieList.addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent evt) {
        if (movieList.getSelectedIndex() == -1) return;
        MovieFile mvFile = (MovieFile) movieList.getSelectedValue();
        if (mvFile.isWarning())
          JOptionPane.showMessageDialog(MovieRenamer.this, mvFile.getFile().getName() + Utils.SPACE + bundle.getString("notAMovie") + Utils.ENDLINE
            + Settings.softName + Utils.SPACE + bundle.getString("onlyRename") + Utils.ENDLINE, bundle.getString("warning"), JOptionPane.WARNING_MESSAGE);
        currentMovie = new Movie(mvFile, MovieRenamer.this.setting.nameFilters);
        searchField.setText(currentMovie.getSearch());
        renameBtn.setEnabled(false);
        renamedField.setText(Utils.EMPTY);
        renamedField.setEnabled(false);
        searchMovieImdb(currentMovie.getSearch());
      }
    });

    searchResultList.addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent evt) {
        if (searchResultList.getSelectedIndex() == -1) return;
        getMovieInfo(((ImdbSearchResult) searchResultList.getSelectedValue()).getImdbId());
      }
    });

    movieImagePnl = new MoviePanel(setting);

    dropFile = new DropFile(setting, new fileWorkerListener(movieList, movieFileNameModel, jScrollPane1), this);
    new DropTarget(movieList, dropFile);

    loadInterface();
  }

  private void loadInterface() {
    switch (setting.interfaceType) {
      case Settings.SIMPLE:
        centerPnl.add(jSplitPane1);
        break;
      case Settings.COMPLETE:
        jSplitPane2.setBottomComponent(movieImagePnl);
        break;
      case Settings.CUSTOM:
        break;
    }
    centerPnl.validate();
    centerPnl.repaint();
  }

  private void searchMovieImdb(String searchTitle) {
    try {
      final ArrayList<Loading> loadingWorker = new ArrayList<Loading>();
      loadingWorker.add(new Loading("Imdb Search", true, 100, SEARCHWORKER));
      loadingWorker.add(new Loading("Movie infomation", true, 100, INFOWORKER));
      loadingWorker.add(new Loading("Thumbnails", false, 100, THUMBWORKER));
      loadingWorker.add(new Loading("Fanarts", false, 100, FANARTWORKER));
      loadingWorker.add(new Loading("Actors", false, 100, ACTORWORKER));
      loading = new LoadingDialog(loadingWorker, MovieRenamer.this);
      SwingUtilities.invokeLater(new Runnable() {

        @Override
        public void run() {
          loading.setVisible(true);
        }
      });
      ImdbSearchWorker imdbsw = new ImdbSearchWorker(searchTitle, imdbFrChk.isSelected(), setting);
      imdbsw.addPropertyChangeListener(new searchWorkerListener(imdbsw, searchResultList, searchResModel));
      imdbsw.execute();
    } catch (MalformedURLException ex) {
      setting.getLogger().log(Level.SEVERE, null, ex);
    } catch (UnsupportedEncodingException ex) {
      setting.getLogger().log(Level.SEVERE, null, ex);
    }
  }

  private void getMovieInfo(String imdbId) {
    try {
      currentMovie.clear();
      currentMovie.setImdbId(imdbId);
      ImdbInfoWorker imdbiw = new ImdbInfoWorker(imdbId, (imdbFrChk.isSelected() ? setting.imdbMovieUrl_fr:setting.imdbMovieUrl), imdbFrChk.isSelected(), setting);
      TheMovieDbInfoWorker tmdbiw = new TheMovieDbInfoWorker(currentMovie, setting);
      imdbiw.addPropertyChangeListener(new MovieInfoListener(imdbiw));
      tmdbiw.addPropertyChangeListener(new MovieInfoListener(tmdbiw));
      imdbiw.execute();
      tmdbiw.execute();
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
        popMenu = new JPopupMenu();
        infoPopMenuItem = new JMenuItem();
        topTb = new JToolBar();
        openBtn = new JButton();
        separator = new Separator();
        imdbFrChk = new JCheckBox();
        updateBtn = new JButton();
        settingBtn = new JButton();
        exitBtn = new JButton();
        centerPnl = new JPanel();
        jSplitPane1 = new JSplitPane();
        jScrollPane1 = new JScrollPane();
        movieList = new JList();
        jSplitPane2 = new JSplitPane();
        jPanel1 = new JPanel();
        jScrollPane2 = new JScrollPane();
        searchResultList = new JList();
        searchField = new JTextField();
        searchBtn = new JButton();
        resultLbl = new JLabel();
        jToolBar1 = new JToolBar();
        renameBtn = new JButton();
        renamedField = new JTextField();
        thumbChk = new JCheckBox();
        fanartChk = new JCheckBox();
        nfoChk = new JCheckBox();

        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);

        infoPopMenuItem.setText("Info");
        infoPopMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                infoPopMenuItemActionPerformed(evt);
            }
        });
        popMenu.add(infoPopMenuItem);

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

        imdbFrChk.setText("Imdb FR");
        imdbFrChk.setFocusable(false);
        imdbFrChk.setHorizontalAlignment(SwingConstants.TRAILING);
        imdbFrChk.setVerticalTextPosition(SwingConstants.BOTTOM);
        topTb.add(imdbFrChk);

        updateBtn.setIcon(new ImageIcon(getClass().getResource("/image/system-software-update-5.png"))); // NOI18N
        updateBtn.setToolTipText(bundle.getString("updateBtn")); // NOI18N
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

        jSplitPane1.setDividerLocation(300);

        jScrollPane1.setBorder(BorderFactory.createTitledBorder(null, bundle.getString("movieListTitle"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 13))); // NOI18N

        movieList.setFont(new Font("Dialog", 0, 12)); // NOI18N
        jScrollPane1.setViewportView(movieList);

        jSplitPane1.setTopComponent(jScrollPane1);

        jSplitPane2.setDividerLocation(200);
        jSplitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);

        jPanel1.setBorder(BorderFactory.createTitledBorder(null, bundle.getString("searchTitle"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", 1, 13))); // NOI18N

        searchResultList.setFont(new Font("Dialog", 0, 12));
        jScrollPane2.setViewportView(searchResultList);

        searchField.setEnabled(false);

        searchBtn.setIcon(new ImageIcon(getClass().getResource("/image/system-search-3.png"))); // NOI18N
        searchBtn.setToolTipText("Search on imdb");
        searchBtn.setEnabled(false);
        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                searchBtnActionPerformed(evt);
            }
        });

        resultLbl.setText(bundle.getString("searchResListTitle")); // NOI18N

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(searchField, GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(searchBtn, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(resultLbl)
                .addContainerGap(469, Short.MAX_VALUE))
            .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(searchBtn, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(resultLbl)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE))
        );

        jSplitPane2.setLeftComponent(jPanel1);

        jSplitPane1.setRightComponent(jSplitPane2);

        GroupLayout centerPnlLayout = new GroupLayout(centerPnl);
        centerPnl.setLayout(centerPnlLayout);
        centerPnlLayout.setHorizontalGroup(
            centerPnlLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(jSplitPane1, GroupLayout.DEFAULT_SIZE, 855, Short.MAX_VALUE)
        );
        centerPnlLayout.setVerticalGroup(
            centerPnlLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(jSplitPane1, GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
        );

        getContentPane().add(centerPnl, BorderLayout.CENTER);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        renameBtn.setText("Rename");
        renameBtn.setEnabled(false);
        renameBtn.setFocusable(false);
        renameBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        renameBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        renameBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                renameBtnActionPerformed(evt);
            }
        });
        jToolBar1.add(renameBtn);

        renamedField.setEnabled(false);
        jToolBar1.add(renamedField);

        thumbChk.setText("Thumb");
        thumbChk.setFocusable(false);
        thumbChk.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar1.add(thumbChk);

        fanartChk.setText("Fanart");
        fanartChk.setFocusable(false);
        fanartChk.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar1.add(fanartChk);

        nfoChk.setText("Xbmc NFO");
        nfoChk.setFocusable(false);
        nfoChk.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar1.add(nfoChk);

        getContentPane().add(jToolBar1, BorderLayout.SOUTH);

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

      final Setting set = new Setting(setting);
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
          if (setting.interfaceChanged) {
            setting.interfaceChanged = false;
            loadInterface();
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

    private void infoPopMenuItemActionPerformed(ActionEvent evt) {//GEN-FIRST:event_infoPopMenuItemActionPerformed
      System.out.println("Inof clicked");
      final JOptionPane optionPane = new JOptionPane(currentMovie.getNFOFromMovie());

      final JDialog dialog = new JDialog((JFrame) null, Utils.EMPTY);
      dialog.getContentPane().add(optionPane);
      dialog.setLocationRelativeTo(this);
      dialog.setModal(true);
      dialog.setSize(200, 350);

      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      dialog.setVisible(true);
    }//GEN-LAST:event_infoPopMenuItemActionPerformed

    private void searchBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
      searchMovieImdb(searchField.getText());
    }//GEN-LAST:event_searchBtnActionPerformed

    private void renameBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_renameBtnActionPerformed

      File file = currentMovie.getFile();//Current File
      String newName = renamedField.getText();
      String newNameNoExt = newName.substring(0, newName.lastIndexOf(Utils.DOT));
      System.out.println(file.getParent() + File.separator + newName);
      if (!file.getName().equals(newName)) {
        boolean success = file.renameTo(new File(file.getParent() + File.separator + newName));
        if (!success) JOptionPane.showMessageDialog(MovieRenamer.this, "Rename file failed", sError, JOptionPane.ERROR_MESSAGE);
      }

      setting.getLogger().log(Level.INFO, "Rename : {0}\nTo : {1}", new Object[]{file.getName(), renamedField.getText()});

      //Creat Xbmc NFO
      if (nfoChk.isSelected())
        try {
          BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(file.getParent() + File.separator + newNameNoExt + ".nfo")), "UTF-8"));
          out.write(currentMovie.getNFOFromMovie(), 0, currentMovie.getNFOFromMovie().length());
          out.close();
        } catch (IOException ex) {
          Logger.getLogger(MovieRenamer.class.getName()).log(Level.SEVERE, null, ex);
        }

      //Download thumb
      if (thumbChk.isSelected())
        try {
          Utils.copyFile(setting.cache.get(movieImagePnl.getSelectedThumb(), Cache.thumb), new File(file.getParent() + File.separator + newNameNoExt + ".tbn"));
        } catch (IOException ex) {
          Logger.getLogger(MovieRenamer.class.getName()).log(Level.SEVERE, null, ex);
        }
      //Download fanart
      if (fanartChk.isSelected())
        try {
          Utils.copyFile(setting.cache.get(movieImagePnl.getSelectedFanart(), Cache.fanart), new File(file.getParent() + File.separator + newNameNoExt + "-fanart.jpg"));
        } catch (IOException ex) {
          Logger.getLogger(MovieRenamer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_renameBtnActionPerformed

  public class searchWorkerListener implements PropertyChangeListener {

    private ImdbSearchWorker worker;
    private DefaultListModel model;
    private JList list;

    public searchWorkerListener(ImdbSearchWorker worker, JList list, DefaultListModel model) {
      this.worker = worker;
      this.model = model;
      this.list = list;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
        try {
          ArrayList<ImdbSearchResult> objects = worker.get();
          model = new DefaultListModel();
          resultLbl.setText(bundle.getString("searchResListTitle") + " : " + objects.size());
          for (int i = 0; i < objects.size(); i++) {
            model.addElement(objects.get(i));
          }
          list.setCellRenderer(new IconListRenderer<ImdbSearchResult>(objects));

          list.setModel(model);
          if (objects.isEmpty()) JOptionPane.showMessageDialog(MovieRenamer.this, bundle.getString("noResult"), sError, JOptionPane.ERROR_MESSAGE);
          searchBtn.setEnabled(!objects.isEmpty());
          searchField.setEnabled(!objects.isEmpty());
        } catch (InterruptedException ex) {
          setting.getLogger().log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
          setting.getLogger().log(Level.SEVERE, null, ex);
        }
        loading.setValue(100, SEARCHWORKER);
        if (!model.isEmpty()) list.setSelectedIndex(0);// Start ImdbInfoWorker
      } else loading.setValue(worker.getProgress(), SEARCHWORKER);
    }
  }

  public class fileWorkerListener implements PropertyChangeListener {

    private ListFilesWorker worker;
    private DefaultListModel model;
    private JComponent component;
    private JList list;
    private ProgressMonitor progressMonitor;

    public fileWorkerListener(JList list, DefaultListModel model, JComponent component) {
      this.worker = null;
      this.model = model;
      this.component = component;
      this.list = list;
    }

    public void setWorker(ListFilesWorker worker) {
      this.worker = worker;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getNewValue().equals(SwingWorker.StateValue.STARTED)) {
        progressMonitor = new ProgressMonitor(MovieRenamer.this, "List movie files", Utils.EMPTY, 0, 100);
        progressMonitor.setMillisToDecideToPopup(0);
        progressMonitor.setProgress(0);
      } else if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
        try {
          ArrayList<MovieFile> objects = worker.get();
          model = new DefaultListModel();
          ((TitledBorder) component.getBorder()).setTitle(Utils.EMPTY + objects.size() + Utils.SPACE + bundle.getString("movies"));
          for (int i = 0; i < objects.size(); i++) {
            model.addElement(objects.get(i));
          }
          list.setCellRenderer(new IconListRenderer<MovieFile>(objects));
          component.repaint();
          list.setModel(model);
          if (objects.isEmpty()) JOptionPane.showMessageDialog(MovieRenamer.this, bundle.getString("noMovieFound"), sError, JOptionPane.ERROR_MESSAGE);
          searchBtn.setEnabled(!objects.isEmpty());
          searchField.setEnabled(!objects.isEmpty());
        } catch (InterruptedException ex) {
          setting.getLogger().log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
          setting.getLogger().log(Level.SEVERE, null, ex);
        }
        if (progressMonitor != null) progressMonitor.close();
        progressMonitor = null;
        //if (!model.isEmpty()) list.setSelectedIndex(0);// Start ImdbInfoWorker
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
      if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
        try {
          Object obj = imdbiw.get();
          if (obj instanceof MovieInfo) {
            currentMovie.setMovieInfo((MovieInfo) obj);
            setting.getLogger().log(Level.INFO, currentMovie.toString());

            renamedField.setText(currentMovie.getRenamedTitle(setting.movieFilenameFormat));
            renameBtn.setEnabled(true);
            renamedField.setEnabled(true);
            movieImagePnl.addMovie(currentMovie);

            ActorWorker actor = new ActorWorker(currentMovie.getPersons(), movieImagePnl, setting);
            actor.addPropertyChangeListener(new MovieImageListener(actor, ACTORWORKER));
            actor.execute();
          }
          if (obj instanceof Movie) {
            movieImagePnl.clearList();
            currentMovie.setThumbs(((Movie) obj).getThumbs());
            currentMovie.setFanarts(((Movie) obj).getFanarts());
            MovieImageWorker thumb = new MovieImageWorker(currentMovie.getThumbs(), 0, Cache.thumb, movieImagePnl, setting);
            MovieImageWorker fanart = new MovieImageWorker(currentMovie.getFanarts(), 1, Cache.fanart, movieImagePnl, setting);
            
            thumb.addPropertyChangeListener(new MovieImageListener(thumb, THUMBWORKER));
            fanart.addPropertyChangeListener(new MovieImageListener(fanart, FANARTWORKER));
            
            thumb.execute();
            fanart.execute();            
          }
        } catch (InterruptedException ex) {
          setting.getLogger().log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
          setting.getLogger().log(Level.SEVERE, null, ex);
        }
        loading.setValue(100, INFOWORKER);
      } else loading.setValue(imdbiw.getProgress(), INFOWORKER);
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
    private JPanel centerPnl;
    private JButton exitBtn;
    private JCheckBox fanartChk;
    private JFileChooser fileChooser;
    private JCheckBox imdbFrChk;
    private JMenuItem infoPopMenuItem;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JSplitPane jSplitPane1;
    private JSplitPane jSplitPane2;
    private JToolBar jToolBar1;
    private JList movieList;
    private JCheckBox nfoChk;
    private JButton openBtn;
    private JPopupMenu popMenu;
    private JButton renameBtn;
    private JTextField renamedField;
    private JLabel resultLbl;
    private JButton searchBtn;
    private JTextField searchField;
    private JList searchResultList;
    private Separator separator;
    private JButton settingBtn;
    private JCheckBox thumbChk;
    private JToolBar topTb;
    private JButton updateBtn;
    // End of variables declaration//GEN-END:variables
}
