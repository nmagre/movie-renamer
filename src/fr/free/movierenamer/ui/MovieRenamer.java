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
import com.alee.laf.button.WebButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.text.WebTextField;
import com.alee.laf.toolbar.WebToolBar;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;
import fr.free.movierenamer.Main;
import fr.free.movierenamer.media.*;
import fr.free.movierenamer.media.movie.Movie;
import fr.free.movierenamer.media.movie.MovieInfo;
import fr.free.movierenamer.media.tvshow.TvShow;
import fr.free.movierenamer.media.tvshow.TvShowInfo;
import fr.free.movierenamer.parser.MrRenamedMovie;
import fr.free.movierenamer.parser.XMLParser;
import fr.free.movierenamer.ui.res.ContextMenuListMouseListener;
import fr.free.movierenamer.ui.res.DropFile;
import fr.free.movierenamer.ui.res.IconListRenderer;
import fr.free.movierenamer.utils.*;
import fr.free.movierenamer.worker.*;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JToolBar.Separator;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.*;
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

  private final String SERROR = Utils.i18n("error");
  private Settings setting = Settings.getInstance();
  private DropFile dropFile;
  private LoadingDialog loading;
  private List<MediaFile> mediaFile;
  private ContextMenuListMouseListener contex;
  private List<MediaRenamed> renamedMediaFile;
  // Current variables
  private MovieRenamerMode currentMode;
  private Media currentMedia;
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
  // Media Panel container
  private TransitionPanel containerTransitionMediaPanel;
  // File chooser
  private WebFileChooser fileChooser;
  // List model
  private DefaultListModel mediaFileNameModel;
  private DefaultListModel searchResModel;

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

  public MovieRenamer() {

    contex = new ContextMenuListMouseListener();
    contex.addPropertyChangeListener(contextMenuListener);

    // Create media panel
    moviePnl = new MoviePanel();
    tvShowPanel = new TvShowPanel();

    // Create media panel container and set transition effect
    containerTransitionMediaPanel = new TransitionPanel(moviePnl);
    containerTransitionMediaPanel.setTransitionEffect(TransitionEffect.fade);

    initComponents();
    init();

    // Add listener to lists
    mediaList.addMouseListener(createMediaListListener());
    searchResultList.addListSelectionListener(createSearchResultListListener());

    // Get all renamed media by Movie Renamer
    loadRenamedMovie();

    //Add drag and drop listener on mediaList
    dropFile = new DropFile(setting, renamedMediaFile, new FileWorkerListener(), MovieRenamer.this);
    DropTarget dt = new DropTarget(mediaList, dropFile);
    dt.setActive(true);

    // Set Movie Renamer mode
    currentMode = MovieRenamerMode.MOVIEMODE;
    movieModeBtn.setEnabled(false);

    // Create dummy property change support for close loading dialog on error
    errorSupport = new PropertyChangeSupport(new Object());
    errorSupport.addPropertyChangeListener(new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("closeLoadingDial")) {
          if (loading.isShowing()) {
            loading.dispose();
          }
        }
      }
    });

    // Create Movie Renamer settings property change
    settingsChange = new PropertyChangeSupport(setting);
    settingsChange.addPropertyChangeListener(createSettingsChangeListener());

    loadInterface();

    setIconImage(Utils.getImageFromJAR("/image/icon-32.png", getClass()));
    setLocationRelativeTo(null);

    setVisible(true);

    // Check for Movie Renamer update
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
        mediaLbl.setText(Utils.SPACE + Utils.i18n("media") + " : " + mediaFileNameModel.size());
      } else if (pce.getPropertyName().equals("search")) {
        if (currentMedia == null) {
          return;
        }
        searchMedia();
      }
    }
  };

  private void init() {

    // File chooser
    if (setting.locale.equals("fr")) {// FIXME add to i18n files, move to main
      UIManager.put("WebFileChooser.back", "Précédent");
      UIManager.put("WebFileChooser.forward", "Suivant");
      UIManager.put("WebFileChooser.folderup", "Remonte d'un niveau");
      UIManager.put("WebFileChooser.home", "Répertoire d'accueil");
      UIManager.put("WebFileChooser.refresh", "Rafraichir");
      UIManager.put("WebFileChooser.newfolder", "Crée un nouveau dossier");
      UIManager.put("WebFileChooser.delete", "Supprimer");
      UIManager.put("WebFileChooser.files.selected", "Fichiers sélectionnés");
      UIManager.put("WebFileChooser.cancel", "Annuler");
      UIManager.put("WebFileChooser.view", "Changer de vue");
      UIManager.put("WebFileChooser.view.tiles", "Détails");
      UIManager.put("WebFileChooser.choose", "Ouvrir");
    } else {
      UIManager.put("WebFileChooser.choose", "Open");
    }

    fileChooser = new WebFileChooser(this, "");// FIXME add title
    fileChooser.setFilesToChoose(FilesToChoose.all);
    fileChooser.setSelectionMode(SelectionMode.MULTIPLE_SELECTION);
    MediaFileFilter fp = new MediaFileFilter(setting);
    fileChooser.setPreviewFilter(fp);
    fileChooser.setChooseFilter(fp);
    //fileChooser.setAcceptAllFileFilterUsed(false);//Remove AcceptAll as an available choice in the choosable filter list
    fileChooser.setCurrentDirectory(new File(setting.fileChooserPath));

    // Add button to main toolbar on right
    mainTb.addToEnd(helpBtn);
    mainTb.addToEnd(new JSeparator(JSeparator.VERTICAL));
    mainTb.addToEnd(updateBtn);
    mainTb.addToEnd(settingBtn);
    mainTb.addToEnd(exitBtn);

    // Add tooltip 
    TooltipManager.setTooltip(openBtn, openTooltipLbl, TooltipWay.down);
    TooltipManager.setTooltip(editBtn, new JLabel(Utils.i18n("edit"), new ImageIcon(getClass().getResource("/image/accessories-text-editor-6-24.png")), SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(movieModeBtn, new JLabel(Utils.i18n("movieMode"), new ImageIcon(getClass().getResource("/image/movie.png")),
            SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(tvShowModeBtn, new JLabel(Utils.i18n("tvshowMode"), new ImageIcon(getClass().getResource("/image/tv.png")),
            SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(helpBtn, new JLabel(Utils.i18n("help"), new ImageIcon(getClass().getResource("/image/system-help-3.png")),
            SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(updateBtn, new JLabel(Utils.i18n("updateBtn"), new ImageIcon(getClass().getResource("/image/system-software-update-5.png")),
            SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(settingBtn, new JLabel(Utils.i18n("settingBtn"), new ImageIcon(getClass().getResource("/image/system-settings.png")),
            SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(exitBtn, new JLabel(Utils.i18n("exitBtn"), new ImageIcon(getClass().getResource("/image/application-exit.png")),
            SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(searchBtn, new JLabel(Utils.i18n("search"), new ImageIcon(getClass().getResource("/image/search.png")),
            SwingConstants.TRAILING), TooltipWay.down);
    TooltipManager.setTooltip(renameBtn, new JLabel(Utils.i18n("rename"), new ImageIcon(getClass().getResource("/image/dialog-ok-2.png")),
            SwingConstants.TRAILING), TooltipWay.down);

    // Add media panel container to media split pane
    MediaSp.setBottomComponent(containerTransitionMediaPanel);
    fileFormatField.setText(setting.movieFilenameFormat);
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

        if (index == -1) {
          return;
        }

        // No media selected 
        if (!mediaList.getCellBounds(index, index).contains(e.getPoint())) {
          mediaList.removeSelectionInterval(index, index);
          clearInterface(false, true);
          currentMedia = null;
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
            currentMedia = new Movie(mediaFile, renameField);
            break;
          case TVSHOW:
            currentMedia = new TvShow(mediaFile, renameField);
            break;
          default:
            return;
        }

        searchField.setText(currentMedia.getSearch());
        renameBtn.setEnabled(false);
        editBtn.setEnabled(false);
        renameField.setText(Utils.EMPTY);
        renameField.setEnabled(false);

        searchBtn.setEnabled(!MovieRenamer.this.setting.autoSearchMedia);
        searchField.setEnabled(!MovieRenamer.this.setting.autoSearchMedia);

        if (MovieRenamer.this.setting.autoSearchMedia) {
          searchMedia();
        }
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
          currentMedia.setMediaId(sres.getId());

          switch (currentMedia.getType()) {
            case MOVIE:
              if (movieInfoWorker != null && !movieInfoWorker.isDone()) {
                return;
              }
              //Get movie info
              movieInfoWorker = WorkerManager.getMovieInfoWorker(errorSupport, sres.getId());
              if (movieInfoWorker == null) {
                JOptionPane.showMessageDialog(MovieRenamer.this, Utils.i18n("errorBugReport"), SERROR, JOptionPane.ERROR_MESSAGE);
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
  }

  /**
   * Create settings change listener
   *
   * @return Property Change Listener
   */
  private PropertyChangeListener createSettingsChangeListener() {
    return new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {//TODO A refaire

        Settings.LOGGER.log(Level.INFO, "Settings property change : {0}", evt.getPropertyName());
        if (evt.getPropertyName().equals("settingChange")) {
          Settings oldConf = (Settings) evt.getOldValue();
          Settings newConf = (Settings) evt.getNewValue();

          boolean filterChanged = !oldConf.mediaNameFilters.equals(newConf.mediaNameFilters);
          boolean scrapperChanged = false;
          boolean scrapperLangChanged = false;
          boolean media = currentMedia != null && !searchResModel.isEmpty();
          switch (currentMode) {
            case MOVIEMODE:
              scrapperChanged = oldConf.movieScrapper != newConf.movieScrapper;
              scrapperLangChanged = oldConf.movieScrapperLang != newConf.movieScrapperLang;
              break;
            case TVSHOWMODE:
              scrapperChanged = oldConf.tvshowScrapper != newConf.tvshowScrapper;
              scrapperLangChanged = oldConf.tvshowScrapperLang != newConf.tvshowScrapperLang;
              break;
            default:
              break;
          }

          // Update newConf
          MovieRenamer.this.setting = newConf;

          if (Settings.interfaceChanged) {
            boolean getImage = newConf.movieInfoPanel && !scrapperChanged && !scrapperLangChanged && !filterChanged && media;

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
                  thumbWorker = WorkerManager.getMediaImageWorker(currentMedia.getImages(MediaImage.MediaImageType.THUMB), MediaImage.MediaImageSize.THUMB, Cache.CacheType.THUMB, moviePnl);
                  thumbWorker.addPropertyChangeListener(new workerListener(thumbWorker, WorkerManager.WORKERID.THUMBWORKER));
                } catch (ActionNotValidException ex) {
                  Settings.LOGGER.log(Level.SEVERE, null, ex);
                }
              }

              if (newConf.fanart) {
                moviePnl.clearFanartList();
                try {
                  fanartWorker = WorkerManager.getMediaImageWorker(currentMedia.getImages(MediaImage.MediaImageType.FANART), MediaImage.MediaImageSize.THUMB, Cache.CacheType.FANART, moviePnl);
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

          String text;
          switch (setting.nfoType) {
            case XBMC:
              text = Utils.i18n("nfoXbmc");
              break;
            case MEDIAPORTAL:
              text = Utils.i18n("nfoMediaPortal");
              break;
            case YAMJ:
              text = Utils.i18n("nfoYamj");
              break;
            default:
              text = Utils.i18n("nfoXbmc");
              break;
          }
          nfoChk.setText(text);

          if (filterChanged) {
            currentMedia.resetDefaultSearch();
          }

          if (media && (scrapperChanged || scrapperLangChanged)) {
            searchMedia();
          }

          if (!oldConf.movieFilenameFormat.equals(newConf.movieFilenameFormat)) {
            fileFormatField.setText(newConf.movieFilenameFormat);
            if (media) {// Re-generate renamed filename
              renameField.setText(currentMedia.getRenamedTitle(fileFormatField.getText()));
            }
          }
        }
      }
    };
  }

  //Edit button action listener
  private ActionListener createEditActionListener() {
    return new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent evt) {
        editBtnActionPerformed(evt);
      }
    };
  }

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

    renameTb.remove(nfoChk);
    renameTb.remove(fanartChk);
    renameTb.remove(thumbChk);

    switch (currentMode) {
      case MOVIEMODE:
        if (!setting.movieInfoPanel) {
          MediaSp.remove(containerTransitionMediaPanel);
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
          MediaSp.setBottomComponent(containerTransitionMediaPanel);
          centerSp.setDividerLocation(300);
          MediaSp.setDividerLocation(200);
        }

        if (setting.movieInfoPanel) {
          if (setting.thumb) {
            renameTb.addToEnd(thumbChk);
          }
          if (setting.fanart) {
            renameTb.addToEnd(fanartChk);
          }
          renameTb.addToEnd(nfoChk);
        }

        containerTransitionMediaPanel.switchContent(moviePnl);

        break;
      case TVSHOWMODE:
        if (setting.movieInfoPanel) {
          MediaSp.remove(containerTransitionMediaPanel);
        } else {
          if (centerSp.getBottomComponent().equals(searchPnl)) {
            centerSp.remove(searchPnl);
            MediaSp.removeAll();
            MediaSp.add(searchPnl);
            centerSp.add(MediaSp);
          }
        }
        centerSp.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        MediaSp.setBottomComponent(containerTransitionMediaPanel);

        containerTransitionMediaPanel.switchContent(tvShowPanel);
        break;
      default:
    }

    renameTb.updateUI();
    centerPnl.validate();
    centerPnl.repaint();
    setTitle(Settings.APPNAME + "-" + setting.getVersion() + " " + currentMode.getTitleMode());
  }

  /**
   * Clear Movie Renamer interface
   *
   * @param mediaList Clear media list
   * @param searchList Clear search list
   */
  private void clearInterface(boolean mediaList, boolean searchList) {

    if (currentMedia != null) {
      currentMedia.clear();
    }

    if (mediaList) {
      if (mediaFileNameModel != null) {
        mediaFileNameModel.clear();
      }
      mediaLbl.setText(Utils.i18n("media"));
    }

    if (searchList) {
      if (searchResModel != null) {
        searchResModel.clear();
      }
      searchLbl.setText(Utils.i18n("search"));
      searchBtn.setEnabled(false);
      searchField.setEnabled(false);
      renameField.setText("");
      renameField.setEnabled(false);
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
      JOptionPane.showMessageDialog(this, Utils.i18n("errorBugReport"), SERROR, JOptionPane.ERROR_MESSAGE);
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

        updateBtn = new WebButton();
        settingBtn = new WebButton();
        exitBtn = new WebButton();
        helpBtn = new WebButton();
        openTooltipLbl = new JLabel(new ImageIcon(getClass().getResource("/image/folder-video.png")), SwingConstants.TRAILING);
        renameField = new JTextField();
        thumbChk = new WebCheckBox();
        fanartChk = new WebCheckBox();
        nfoChk = new WebCheckBox();
        mainTb = new WebToolBar();
        openBtn = new WebButton();
        jSeparator1 = new Separator();
        editBtn = new WebButton();
        separator = new Separator();
        movieModeBtn = new WebButton();
        tvShowModeBtn = new WebButton();
        jSeparator2 = new Separator();
        jLabel1 = new JLabel();
        fileFormatField = new WebTextField();
        renameTb = new WebToolBar();
        renameBtn = new WebButton();
        centerPnl = new WebPanel();
        centerSp = new JSplitPane();
        MediaSp = new JSplitPane();
        searchPnl = new WebPanel();
        searchScroll = new JScrollPane();
        searchResultList = new WebList();
        searchTb = new WebToolBar();
        searchLbl = new WebLabel();
        searchBtn = new WebButton();
        searchField = new WebTextField();
        mediaPnl = new WebPanel();
        mediScroll = new JScrollPane();
        mediaList = new WebList();
        mediaTb = new WebToolBar();
        mediaLbl = new WebLabel();

        updateBtn.setIcon(new ImageIcon(getClass().getResource("/image/system-software-update-5.png")));         updateBtn.setFocusable(false);
        updateBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        updateBtn.setRolloverDarkBorderOnly(true);
        updateBtn.setRolloverDecoratedOnly(true);
        updateBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        updateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                updateBtnActionPerformed(evt);
            }
        });

        settingBtn.setIcon(new ImageIcon(getClass().getResource("/image/system-settings.png")));         settingBtn.setFocusable(false);
        settingBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        settingBtn.setRolloverDarkBorderOnly(true);
        settingBtn.setRolloverDecoratedOnly(true);
        settingBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        settingBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                settingBtnActionPerformed(evt);
            }
        });

        exitBtn.setIcon(new ImageIcon(getClass().getResource("/image/application-exit.png")));         exitBtn.setFocusable(false);
        exitBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        exitBtn.setRolloverDarkBorderOnly(true);
        exitBtn.setRolloverDecoratedOnly(true);
        exitBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        exitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                exitBtnActionPerformed(evt);
            }
        });

        helpBtn.setIcon(new ImageIcon(getClass().getResource("/image/system-help-3.png")));         helpBtn.setFocusable(false);
        helpBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        helpBtn.setRolloverDarkBorderOnly(true);
        helpBtn.setRolloverDecoratedOnly(true);
        helpBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        helpBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                helpBtnActionPerformed(evt);
            }
        });

        openTooltipLbl.setText(Utils.i18n("openFolderBtn"));

        renameField.setEnabled(false);

        thumbChk.setText(Utils.i18n("thumb"));         thumbChk.setFocusable(false);

        fanartChk.setText("Fanart");
        fanartChk.setFocusable(false);

        nfoChk.setText(Utils.i18n("nfoXbmc"));         nfoChk.setFocusable(false);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(770, 570));

        mainTb.setFloatable(false);
        mainTb.setRollover(true);
        mainTb.setRound(10);

        openBtn.setIcon(new ImageIcon(getClass().getResource("/image/folder-video.png")));         openBtn.setFocusable(false);
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
        mainTb.add(jSeparator1);

        editBtn.setIcon(new ImageIcon(getClass().getResource("/image/accessories-text-editor-6-24.png")));         editBtn.setEnabled(false);
        editBtn.setFocusable(false);
        editBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        editBtn.setRolloverDarkBorderOnly(true);
        editBtn.setRolloverDecoratedOnly(true);
        editBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        editBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                editBtnActionPerformed(evt);
            }
        });
        mainTb.add(editBtn);
        mainTb.add(separator);

        movieModeBtn.setIcon(new ImageIcon(getClass().getResource("/image/movie.png")));         movieModeBtn.setFocusable(false);
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

        tvShowModeBtn.setIcon(new ImageIcon(getClass().getResource("/image/tv.png")));         tvShowModeBtn.setFocusable(false);
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
        mainTb.add(jSeparator2);

        jLabel1.setFont(new Font("Ubuntu", 1, 13));         jLabel1.setText(Utils.i18n("mediaFileFormat"));         mainTb.add(jLabel1);

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

        renameBtn.setIcon(new ImageIcon(getClass().getResource("/image/dialog-ok-2.png")));         renameBtn.setText(Utils.i18n("rename"));         renameBtn.setEnabled(false);
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

        centerPnl.setMargin(new Insets(1, 1, 1, 1));
        centerPnl.setShadeWidth(2);

        centerSp.setDividerLocation(300);

        MediaSp.setDividerLocation(170);
        MediaSp.setOrientation(JSplitPane.VERTICAL_SPLIT);

        searchPnl.setMargin(new Insets(10, 10, 10, 10));

        searchResultList.setFont(new Font("Dialog", 0, 12));         searchScroll.setViewportView(searchResultList);

        searchTb.setFloatable(false);
        searchTb.setRollover(true);
        searchTb.setRound(0);

        searchLbl.setText(Utils.i18n("search"));         searchLbl.setFont(new Font("Ubuntu", 1, 14));         searchTb.add(searchLbl);

        searchBtn.setIcon(new ImageIcon(getClass().getResource("/image/search.png")));         searchBtn.setEnabled(false);
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

        GroupLayout searchPnlLayout = new GroupLayout(searchPnl);
        searchPnl.setLayout(searchPnlLayout);
        searchPnlLayout.setHorizontalGroup(
            searchPnlLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(searchTb, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(searchPnlLayout.createSequentialGroup()
                .addComponent(searchField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(searchBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addComponent(searchScroll, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE)
        );
        searchPnlLayout.setVerticalGroup(
            searchPnlLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(searchPnlLayout.createSequentialGroup()
                .addComponent(searchTb, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(searchPnlLayout.createParallelGroup(Alignment.TRAILING)
                    .addComponent(searchField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchBtn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(searchScroll, GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))
        );

        searchField.setLeadingComponent(new JLabel(new ImageIcon(Utils.getImageFromJAR("/image/search.png", getClass()))));

        MediaSp.setLeftComponent(searchPnl);

        centerSp.setRightComponent(MediaSp);

        mediaPnl.setMargin(new Insets(10, 10, 10, 10));
        mediaPnl.setMinimumSize(new Dimension(60, 0));

        mediaList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mediScroll.setViewportView(mediaList);

        mediaTb.setFloatable(false);
        mediaTb.setRollover(true);
        mediaTb.setRound(0);

        mediaLbl.setText(Utils.i18n("media"));         mediaLbl.setFont(new Font("Ubuntu", 1, 14));         mediaTb.add(mediaLbl);

        GroupLayout mediaPnlLayout = new GroupLayout(mediaPnl);
        mediaPnl.setLayout(mediaPnlLayout);
        mediaPnlLayout.setHorizontalGroup(
            mediaPnlLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(mediaTb, GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
            .addComponent(mediScroll)
        );
        mediaPnlLayout.setVerticalGroup(
            mediaPnlLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(mediaPnlLayout.createSequentialGroup()
                .addComponent(mediaTb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(mediScroll, GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE))
        );

        centerSp.setLeftComponent(mediaPnl);

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

        pack();
    }// </editor-fold>//GEN-END:initComponents

  private void openBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_openBtnActionPerformed
    fileChooser.setCurrentDirectory(new File(setting.fileChooserPath));
    int n = fileChooser.showDialog();
    if (n == 0) {
      List<File> files = fileChooser.getSelectedFiles();
      if (!files.isEmpty()) {// Remember path
        setting.fileChooserPath = files.get(0).getPath();
        setting.saveSetting();
      }
      dropFile.setMovies(files);
    }
  }//GEN-LAST:event_openBtnActionPerformed

  private void editBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_editBtnActionPerformed

    if (currentMedia.getType() == Media.MediaType.MOVIE) {
      final Movie movie = (Movie) currentMedia;
      final InfoEditorFrame editorFrame = new InfoEditorFrame(movie.getInfo(), MovieRenamer.this);
      editorFrame.addPropertyChangeListener(new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent pce) {
          if (pce.getPropertyName().equals("movieInfo")) { // currentMovie.setMovieInfo((MovieInfo) pce.getNewValue());
            moviePnl.addMovieInfo(movie);
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

  private void movieModeBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_movieModeBtnActionPerformed
    currentMode = MovieRenamerMode.MOVIEMODE;
    movieModeBtn.setEnabled(false);
    tvShowModeBtn.setEnabled(true);
    fileFormatField.setText(setting.movieFilenameFormat);
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
    fileFormatField.setText(setting.tvShowFilenameFormat);
    loadInterface();
    clearInterface(false, true);
    if (currentMedia != null) {
      MediaFile mfile = currentMedia.getMediaFile();
      mfile.setType(Media.MediaType.TVSHOW);
      currentMedia.setMediaFile(mfile);
      searchMedia();
    }
  }//GEN-LAST:event_tvShowModeBtnActionPerformed

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
    });  }//GEN-LAST:event_settingBtnActionPerformed

  private void updateBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_updateBtnActionPerformed
    checkUpdate(true);
  }//GEN-LAST:event_updateBtnActionPerformed

  private void helpBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_helpBtnActionPerformed
    TooltipManager.showOneTimeTooltip(mediaList, new Point(mediaList.getWidth() / 2, mediaList.getHeight() / 2), "Media list help", TooltipWay.up);
    TooltipManager.showOneTimeTooltip(searchResultList, new Point(searchResultList.getWidth() / 2, searchResultList.getHeight() / 2), "searchResultList list help", TooltipWay.up);
    TooltipManager.showOneTimeTooltip(openBtn, new Point(openBtn.getWidth() / 2, openBtn.getHeight()), openTooltipLbl, TooltipWay.down);
    TooltipManager.showOneTimeTooltip(fileFormatField, new Point(fileFormatField.getWidth() / 2, fileFormatField.getHeight()), "Change filename on the fly", TooltipWay.down);
  }//GEN-LAST:event_helpBtnActionPerformed

  private void renameBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_renameBtnActionPerformed
    // TODO
    setMouseIcon(true);

    int index = mediaList.getSelectedIndex();
    if (index == -1) {
      setMouseIcon(false);
      JOptionPane.showMessageDialog(MovieRenamer.this, Utils.i18n("noMovieSelected"), SERROR, JOptionPane.ERROR_MESSAGE);
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

      Renamer renamer = new Renamer("", currentMedia.getMediaFile().getFile(), renameField.getText(), setting);

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
        JOptionPane.showMessageDialog(MovieRenamer.this, Utils.i18n("renameFileFailed"), SERROR, JOptionPane.ERROR_MESSAGE);
        return;
      }

      if (renamer.cancel) {
        setMouseIcon(false);
        return;
      }

      /*
       * if (mediaFile.get(index).wasRenamed()) { for (int i = 0; i < renamedMediaFile.size(); i++) { if
       * (renamedMediaFile.get(i).getMovieFileDest().equals(currentMedia.getMediaFile().getFile().getAbsolutePath())) { renamedMediaFile.remove(i); break; } } }
       * renamedMediaFile.add(renamer.getRenamed());
       *
       * mediaFile.get(index).setRenamed(true);
       */

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

      String nfo;
      switch (setting.nfoType) {
        case XBMC:
          nfo = movie.getXbmcNFOFromMovie();
          break;
        case MEDIAPORTAL:
          nfo = movie.getMediaPortalNFOFromMovie();
          break;
        case YAMJ:
          nfo = movie.getYamjNFOFromMovie();
          break;
        default:
          nfo = movie.getXbmcNFOFromMovie();
          break;
      }

      renamer.createNFO(createXNFO, nfo);
      /*
       * renamer.createThumb(createThumbnail, moviePnl.getSelectedThumb(setting.thumbSize)); renamer.createFanart(createFan, moviePnl.getSelectedFanart(setting.fanartSize));
       */

      // mediaFile.get(index).setFile(renamer.getNewFile());
      mediaFileNameModel = new DefaultListModel();
      for (int i = 0; i < mediaFile.size(); i++) {
        mediaFileNameModel.addElement(mediaFile.get(i));
      }


      //mediaList.setCellRenderer(new IconListRenderer<MediaFile>(mediaFile));
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
    /*
     * while (pos < mediaFile.size()) { if (!mediaFile.get(pos).isRenamed() && !mediaFile.get(pos).wasRenamed()) { mediaList.setSelectedIndex(pos); break; } else { pos++; } }
     */

    if (mediaFile.size() <= pos) {
      JOptionPane.showMessageDialog(MovieRenamer.this, Utils.i18n("endOfList"), "Information", JOptionPane.INFORMATION_MESSAGE);
    }
  }//GEN-LAST:event_renameBtnActionPerformed

  private void searchBtnActionPerformed(ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
    currentMedia.setSearch(searchField.getText());
    clearInterface(false, true);
    searchField.setText(currentMedia.getSearch());
    renameBtn.setEnabled(false);
    editBtn.setEnabled(false);
    renameField.setText(Utils.EMPTY);
    renameField.setEnabled(false);
    searchMedia();
  }//GEN-LAST:event_searchBtnActionPerformed

  private void searchFieldKeyReleased(KeyEvent evt) {//GEN-FIRST:event_searchFieldKeyReleased
    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
      searchBtnActionPerformed(null);
    }
  }//GEN-LAST:event_searchFieldKeyReleased

  private void fileFormatFieldKeyReleased(KeyEvent evt) {//GEN-FIRST:event_fileFormatFieldKeyReleased
    if (currentMedia != null && !searchResModel.isEmpty()) {
      renameField.setText(currentMedia.getRenamedTitle(fileFormatField.getText()));
    }
  }//GEN-LAST:event_fileFormatFieldKeyReleased

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
          JOptionPane.showMessageDialog(MovieRenamer.this, Utils.i18n("noResult"), SERROR, JOptionPane.ERROR_MESSAGE);
          return;
        }

        searchResModel = new DefaultListModel();
        searchLbl.setText(Utils.i18n("search") + " : " + results.size());

        // Sort result by similarity and year
        if (results.size() > 1 && setting.sortBySimiYear) {
          Levenshtein.sortByLevenshteinDistanceYear(currentMedia.getSearch(), currentMedia.getYear(), results);
        }

        for (SearchResult result : results) {
          searchResModel.addElement(result);
        }

        // Display thumbs in result list          
        if (setting.displayThumbResult) {
          searchResultList.setCellRenderer(new IconListRenderer<SearchResult>());
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

          mediaLbl.setText(Utils.i18n("media") + " : " + mediaFileNameModel.size());

          mediaList.setCellRenderer(new IconListRenderer<MediaFile>());
          mediaList.setModel(mediaFileNameModel);

          if (mediaFileNameModel.isEmpty()) {
            JOptionPane.showMessageDialog(MovieRenamer.this, Utils.i18n("noMovieFound"), SERROR, JOptionPane.ERROR_MESSAGE);// FIXME change movie by media
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

  private class MovieInfoListener implements PropertyChangeListener {

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
              ImageWorker thumbWorker = WorkerManager.getMediaImageWorker(movieInfo.getThumbs(), MediaImage.MediaImageSize.THUMB, Cache.CacheType.THUMB, moviePnl);
              thumbWorker.addPropertyChangeListener(new workerListener(thumbWorker, WorkerManager.WORKERID.THUMBWORKER));
              thumbWorker.execute();
            }
            if (setting.fanart) {
              ImageWorker fanartWorker = WorkerManager.getMediaImageWorker(movieInfo.getFanarts(), MediaImage.MediaImageSize.THUMB, Cache.CacheType.FANART, moviePnl);
              fanartWorker.addPropertyChangeListener(new workerListener(fanartWorker, WorkerManager.WORKERID.FANARTWORKER));
              fanartWorker.execute();
            }
          }

          currentMedia.setInfo(movieInfo);
          moviePnl.addMovieInfo((Movie) currentMedia);

          renameField.setText(currentMedia.getRenamedTitle(fileFormatField.getText()));
          renameBtn.setEnabled(true);
          renameField.setEnabled(true);
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
          tvShowPanel.addTvshowInfo((TvShow) currentMedia);//seasons.getSeasons(), ((TvShow) currentMedia).getSearchSxe());
          
          renameField.setText(currentMedia.getRenamedTitle(fileFormatField.getText()));
          renameBtn.setEnabled(true);
          renameField.setEnabled(true);
          editBtn.setEnabled(true);

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
    private WebPanel centerPnl;
    private JSplitPane centerSp;
    private WebButton editBtn;
    private WebButton exitBtn;
    private WebCheckBox fanartChk;
    private WebTextField fileFormatField;
    private WebButton helpBtn;
    private JLabel jLabel1;
    private Separator jSeparator1;
    private Separator jSeparator2;
    private WebToolBar mainTb;
    private JScrollPane mediScroll;
    private WebLabel mediaLbl;
    private WebList mediaList;
    private WebPanel mediaPnl;
    private WebToolBar mediaTb;
    private WebButton movieModeBtn;
    private WebCheckBox nfoChk;
    private WebButton openBtn;
    private JLabel openTooltipLbl;
    private WebButton renameBtn;
    private JTextField renameField;
    private WebToolBar renameTb;
    private WebButton searchBtn;
    private WebTextField searchField;
    private WebLabel searchLbl;
    private WebPanel searchPnl;
    private JList searchResultList;
    private JScrollPane searchScroll;
    private WebToolBar searchTb;
    private Separator separator;
    private WebButton settingBtn;
    private WebCheckBox thumbChk;
    private WebButton tvShowModeBtn;
    private WebButton updateBtn;
    // End of variables declaration//GEN-END:variables
}
