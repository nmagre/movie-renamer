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

import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.progressbar.WebProgressBarUI;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.laf.text.WebTextField;
import com.alee.laf.toolbar.WebToolBar;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.ui.res.UIMediaImage;
import fr.free.movierenamer.ui.res.UIPersonImage;
import fr.free.movierenamer.ui.res.UISearchResult;
import fr.free.movierenamer.ui.worker.SearchMediaCastingWorker;
import fr.free.movierenamer.ui.worker.SearchMediaImagesWorker;
import fr.free.movierenamer.ui.worker.SearchPersonWorker;
import fr.free.movierenamer.utils.ImageUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.util.Locale;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Class MoviePanel
 * 
 * @author Magré Nicolas
 */
public class MoviePanel extends WebPanel implements IMediaPanel {

  private static final long serialVersionUID = 1L;
  private MovieInfo movieInfo;
  private final MovieRenamer mr;
  private final Dimension thumbDim = new Dimension(160, 200);
  public Dimension thumbListDim = new Dimension(60, 90);
  public Dimension fanartListDim = new Dimension(200, 90);
  public Dimension actorListDim = new Dimension(30, 53);
  private final DefaultListModel fanartModel = new DefaultListModel();
  private final DefaultListModel thumbnailModel = new DefaultListModel();
  private final DefaultListModel actorModel = new DefaultListModel();
  private final DefaultListModel subTitleModel = new DefaultListModel();
  private final DefaultListModel audioModel = new DefaultListModel();
  private final DefaultListModel countryModel = new DefaultListModel();
  private final Icon STAR = new ImageIcon(ImageUtils.getImageFromJAR("star.png"));
  private final Icon STAR_HALF = new ImageIcon(ImageUtils.getImageFromJAR("star-half.png"));
  private final Icon STAR_EMPTY = new ImageIcon(ImageUtils.getImageFromJAR("star-empty.png"));

  /**
   * Creates new form MoviePanel
   * 
   * @param setting
   */
  public MoviePanel(MovieRenamer parent) {
    initComponents();
    this.mr = parent;

    // titleField.setLeadingComponent(titleLbl);
    origTitleField.setLeadingComponent(origTitleLbl);
    directorField.setLeadingComponent(directorLbl);
    runtimeField.setLeadingComponent(runtimeLbl);
    genreField.setLeadingComponent(genreLbl);

    movieTb.addToEnd(starPanel);

    countryList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    countryList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    countryList.setVisibleRowCount(-1);

    thumbnailsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    thumbnailsList.setLayoutOrientation(JList.VERTICAL_WRAP);
    thumbnailsList.setVisibleRowCount(-1);
    thumbnailsList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (thumbnailsList.getSelectedIndex() == -1) {
          return;
        }
        thumbnailsList.ensureIndexIsVisible(thumbnailsList.getSelectedIndex());
        UIMediaImage mediaImage = (UIMediaImage) thumbnailsList.getSelectedValue();
        if (mediaImage != null) {
          Icon thumbIcon = ImageUtils.getIcon(mediaImage.getInfo().getURI(), thumbDim, "nothumb.png");
          thumbLbl.setIcon(thumbIcon);
        }
      }
    });

    fanartList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    fanartList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    fanartList.setVisibleRowCount(-1);
    fanartList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (fanartList.getSelectedIndex() == -1) {
          return;
        }
        fanartList.ensureIndexIsVisible(fanartList.getSelectedIndex());
        UIMediaImage mediaImage = (UIMediaImage) fanartList.getSelectedValue();
        if (mediaImage != null) {
          Icon thumbIcon = ImageUtils.getIcon(mediaImage.getInfo().getURI(), thumbDim, "nothumb.png");
          thumbLbl.setIcon(thumbIcon);
        }
      }

    });

    castingList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    castingList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    castingList.setVisibleRowCount(-1);
    castingList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (castingList.getSelectedIndex() == -1) {
          return;
        }

        mr.loadDial(false, false, true, false);

        UIPersonImage cast = (UIPersonImage) castingList.getSelectedValue();

        SearchPersonWorker worker = new SearchPersonWorker(mr.getErrorSupport(), mr, actorsPane, cast.getInfo());
        worker.execute();
      }
    });
  }

  @Override
  public MediaInfo getMediaInfo() {
    return movieInfo;
  }

  @Override
  public void setMediaInfo(MediaInfo mediaInfo) {
    if (mediaInfo instanceof MovieInfo) {
      movieInfo = (MovieInfo) mediaInfo;

      titleLbl.setText(movieInfo.getTitle());
      origTitleField.setText(movieInfo.getOriginalTitle());
      if (movieInfo.getReleasedDate() != null) {
        yearLbl.setText("(" + movieInfo.getReleasedDate().getYear() + ")");
      } else {
        yearLbl.setText(null);
      }
      if (movieInfo.getRuntime() != null) {
        runtimeField.setText(movieInfo.getRuntime() + " min");
      } else {
        runtimeField.setText(null);
      }
      synopsisArea.setText(movieInfo.getOverview());
      if (movieInfo.getGenres().size() > 0) {
        genreField.setText(movieInfo.getGenres().toString());
      } else {
        genreField.setText(null);
      }
      if (movieInfo.getDirectors().size() > 0) {
        directorField.setText(movieInfo.getDirectors().get(0));
      } else {
        directorField.setText(null);
      }
      setRate(movieInfo.getRating());

      countryModel.clear();
      for (Locale country : movieInfo.getCountries()) {
        ImageIcon icon = new ImageIcon(ImageUtils.getImageFromJAR(String.format("country/%s.png", (country == null) ? "unknown" : country.getCountry().toLowerCase())));
        if (country != null) {
          icon.setDescription(country.getDisplayCountry());
        }
        countryModel.addElement(icon);
      }
      countryList.setModel(countryModel);

      Icon thumbIcon = ImageUtils.getIcon(movieInfo.getPosterPath(), thumbDim, "nothumb.png");
      thumbLbl.setIcon(thumbIcon);
      // dropFanartTarget.setActive(true);
      // dropThumbTarget.setActive(true);

      origTitleField.setCaretPosition(0);
      synopsisArea.setCaretPosition(0);
      genreField.setCaretPosition(0);
      directorField.setCaretPosition(0);

      // if (!setting.thumb) {
      // if (!movieInfo.getThumb().equals("")) {
      // Image imThumb = getImage(movieInfo.getThumb(), Cache.CacheType.THUMB);
      // if (imThumb != null) {
      // thumbLbl.setIcon(new ImageIcon(imThumb.getScaledInstance(thumbDim.width, thumbDim.height, Image.SCALE_DEFAULT)));
      // }
      // }
      // }

      // List<MediaSubTitle> subtitles = movie.getMediaTag().getMediaSubTitles();
      // List<MediaAudio> audios = movie.getMediaTag().getMediaAudios();
      // List<String> countries = movieInfo.getCountries();
      // for (MediaSubTitle sub : subtitles) {
      // System.out.println(sub.getTitle() + " : " + sub.getLanguage());
      // subTitleModel.addElement(sub);
      // }
      // for (MediaAudio audio : audios) {
      // audioModel.addElement(audio);
      // }

      final UISearchResult searchResult = mr.getSelectedSearchResult();

      thumbnailsList.removeAll();
      fanartList.removeAll();
      {
        // load images
        SearchMediaImagesWorker worker = new SearchMediaImagesWorker(mr.getErrorSupport(), mr, thumbnailsList, fanartList, searchResult);
        worker.execute();
      }

      castingList.removeAll();
      {
        // load casting
        SearchMediaCastingWorker worker = new SearchMediaCastingWorker(mr.getErrorSupport(), mr, castingList, searchResult);
        worker.execute();
      }

    }
  }

  @Override
  public void clear() {
    setMediaInfo(new MovieInfo(null, null, null));
  }

  /**
   * Set star compared with rate
   * 
   * @param rate
   */
  private void setRate(Double rate) {
    star1.setIcon(STAR_EMPTY);
    star2.setIcon(STAR_EMPTY);
    star3.setIcon(STAR_EMPTY);
    star4.setIcon(STAR_EMPTY);
    star5.setIcon(STAR_EMPTY);
    if (rate == null || rate < 0.00) {
      return;
    }
    rate /= 2;
    int n = rate.intValue();
    switch (n) {
    case 0:
      break;
    case 1:
      star1.setIcon(STAR);
      if ((rate - rate.intValue()) >= 0.50) {
        star2.setIcon(STAR_HALF);
      }
      break;
    case 2:
      star1.setIcon(STAR);
      star2.setIcon(STAR);
      if ((rate - rate.intValue()) >= 0.50) {
        star3.setIcon(STAR_HALF);
      }
      break;
    case 3:
      star1.setIcon(STAR);
      star2.setIcon(STAR);
      star3.setIcon(STAR);
      if ((rate - rate.intValue()) >= 0.50) {
        star4.setIcon(STAR_HALF);
      }
      break;
    case 4:
      star1.setIcon(STAR);
      star2.setIcon(STAR);
      star3.setIcon(STAR);
      star4.setIcon(STAR);
      if ((rate - rate.intValue()) >= 0.50) {
        star5.setIcon(STAR_HALF);
      }
      break;
    case 5:
      star1.setIcon(STAR);
      star2.setIcon(STAR);
      star3.setIcon(STAR);
      star4.setIcon(STAR);
      star5.setIcon(STAR);
      break;
    default:
      break;
    }
  }

  @Override
  public WebList getCastingList() {
    return castingList;
  }

  @Override
  public WebList getFanartsList() {
    return fanartList;
  }

  @Override
  public WebList getSubtitlesList() {
    return subtitlesList;
  }

  @Override
  public WebList getThumbnailsList() {
    return thumbnailsList;
  }

  /**
   * This method is called from within the constructor to initialize the form.
   */
  // WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    origTitleLbl = new WebLabel();
    directorLbl = new WebLabel();
    runtimeLbl = new WebLabel();
    genreLbl = new WebLabel();
    starPanel = new WebPanel();
    star5 = new JLabel();
    star4 = new JLabel();
    star3 = new JLabel();
    star2 = new JLabel();
    star1 = new JLabel();
    countryLbl = new WebLabel();
    webProgressBarUI1 = new WebProgressBarUI();
    movieTb = new WebToolBar();
    titleLbl = new WebLabel();
    yearLbl = new WebLabel();
    thumbLbl = new JLabel();
    origTitleField = new WebTextField();
    directorField = new WebTextField();
    genreField = new WebTextField();
    webToolBar4 = new WebToolBar();
    synopsisLbl = new WebLabel();
    synopsScroll = new JScrollPane();
    synopsisArea = new JTextArea();
    runtimeField = new WebTextField();
    jScrollPane1 = new JScrollPane();
    countryList = new WebList() {
      @Override
      public String getToolTipText(MouseEvent evt) {
        // Get item index
        int index = locationToIndex(evt.getPoint());

        // Get item
        ImageIcon item = (ImageIcon) getModel().getElementAt(index);

        // Return the tool tip text
        return item.getDescription();
      }
    };
    infosTabbedPane = new WebTabbedPane();
    thumbnailsPane = new JPanel();
    thumbsScrollPane = new JScrollPane();
    thumbnailsList = new WebList();
    fanartsPane = new JPanel();
    fanartsScrollPane = new JScrollPane();
    fanartList = new WebList();
    actorsPane = new JPanel();
    infosTabPane = new JScrollPane();
    castingList = new WebList();
    actorNameField = new WebTextField();
    ameField = new WebTextField();
    subtitlesPane = new JPanel();
    subtitlesScrollPane = new JScrollPane();
    subtitlesList = new WebList();

    origTitleLbl.setText(LocaleUtils.i18n("origTitle"));
    origTitleLbl.setFont(new Font("Ubuntu", 1, 13));
    directorLbl.setText(LocaleUtils.i18n("director"));
    directorLbl.setFont(new Font("Ubuntu", 1, 13));
    runtimeLbl.setText(LocaleUtils.i18n("runtime"));
    runtimeLbl.setFont(new Font("Ubuntu", 1, 13));
    genreLbl.setText(LocaleUtils.i18n("genre"));
    genreLbl.setFont(new Font("Ubuntu", 1, 13));
    starPanel.setAlignmentY(0.0F);

    star5.setIcon(new ImageIcon(getClass().getResource("/fr/free/movierenamer/ui/image/star-empty.png")));
    star4.setIcon(new ImageIcon(getClass().getResource("/fr/free/movierenamer/ui/image/star-empty.png")));
    star3.setIcon(new ImageIcon(getClass().getResource("/fr/free/movierenamer/ui/image/star-empty.png")));
    star2.setIcon(new ImageIcon(getClass().getResource("/fr/free/movierenamer/ui/image/star-empty.png")));
    star1.setIcon(new ImageIcon(getClass().getResource("/fr/free/movierenamer/ui/image/star-empty.png")));
    GroupLayout starPanelLayout = new GroupLayout(starPanel);
    starPanel.setLayout(starPanelLayout);
    starPanelLayout.setHorizontalGroup(starPanelLayout.createParallelGroup(Alignment.LEADING).addGroup(
        starPanelLayout.createSequentialGroup().addContainerGap().addComponent(star1).addGap(8, 8, 8).addComponent(star2).addPreferredGap(ComponentPlacement.RELATED).addComponent(star3).addPreferredGap(ComponentPlacement.RELATED).addComponent(star4)
            .addPreferredGap(ComponentPlacement.RELATED).addComponent(star5).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
    starPanelLayout.setVerticalGroup(starPanelLayout.createParallelGroup(Alignment.LEADING).addComponent(star2).addComponent(star3).addComponent(star4).addComponent(star5).addComponent(star1));

    countryLbl.setText(LocaleUtils.i18n("country"));
    countryLbl.setFont(new Font("Ubuntu", 1, 13));
    setMargin(new Insets(10, 10, 10, 10));
    setMinimumSize(new Dimension(10, 380));
    setPreferredSize(new Dimension(562, 400));

    movieTb.setFloatable(false);
    movieTb.setRollover(true);

    titleLbl.setFont(new Font("Ubuntu", 1, 14));
    movieTb.add(titleLbl);

    yearLbl.setFont(new Font("Ubuntu", 1, 14));
    movieTb.add(yearLbl);

    thumbLbl.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

    origTitleField.setEditable(false);

    directorField.setEditable(false);

    webToolBar4.setFloatable(false);
    webToolBar4.setRollover(true);

    synopsisLbl.setText(LocaleUtils.i18n("synopsis"));
    synopsisLbl.setFont(new Font("Ubuntu", 1, 13));
    webToolBar4.add(synopsisLbl);

    synopsScroll.setPreferredSize(new Dimension(264, 62));

    synopsisArea.setColumns(20);
    synopsisArea.setEditable(false);
    synopsisArea.setLineWrap(true);
    synopsisArea.setRows(4);
    synopsisArea.setWrapStyleWord(true);
    synopsisArea.setBorder(null);
    synopsisArea.setOpaque(false);
    synopsScroll.setViewportView(synopsisArea);

    countryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    countryList.setAutoscrolls(false);
    countryList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    jScrollPane1.setViewportView(countryList);

    thumbnailsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    thumbsScrollPane.setViewportView(thumbnailsList);

    GroupLayout thumbnailsPaneLayout = new GroupLayout(thumbnailsPane);
    thumbnailsPane.setLayout(thumbnailsPaneLayout);
    thumbnailsPaneLayout.setHorizontalGroup(thumbnailsPaneLayout.createParallelGroup(Alignment.LEADING).addGroup(
        thumbnailsPaneLayout.createSequentialGroup().addContainerGap().addComponent(thumbsScrollPane, GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE).addContainerGap()));
    thumbnailsPaneLayout.setVerticalGroup(thumbnailsPaneLayout.createParallelGroup(Alignment.LEADING).addGroup(
        thumbnailsPaneLayout.createSequentialGroup().addContainerGap().addComponent(thumbsScrollPane, GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE).addContainerGap()));

    infosTabbedPane.addTab(LocaleUtils.i18n("thumbnails"), thumbnailsPane);
    fanartList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    fanartsScrollPane.setViewportView(fanartList);

    GroupLayout fanartsPaneLayout = new GroupLayout(fanartsPane);
    fanartsPane.setLayout(fanartsPaneLayout);
    fanartsPaneLayout.setHorizontalGroup(fanartsPaneLayout.createParallelGroup(Alignment.LEADING).addGroup(
        fanartsPaneLayout.createSequentialGroup().addContainerGap().addComponent(fanartsScrollPane, GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE).addContainerGap()));
    fanartsPaneLayout.setVerticalGroup(fanartsPaneLayout.createParallelGroup(Alignment.LEADING).addGroup(
        fanartsPaneLayout.createSequentialGroup().addContainerGap().addComponent(fanartsScrollPane, GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE).addContainerGap()));

    infosTabbedPane.addTab(LocaleUtils.i18n("fanarts"), fanartsPane);
    castingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    infosTabPane.setViewportView(castingList);

    actorNameField.setEditable(false);

    ameField.setEditable(false);

    GroupLayout actorsPaneLayout = new GroupLayout(actorsPane);
    actorsPane.setLayout(actorsPaneLayout);
    actorsPaneLayout.setHorizontalGroup(actorsPaneLayout.createParallelGroup(Alignment.LEADING).addGroup(
        actorsPaneLayout
            .createSequentialGroup()
            .addContainerGap()
            .addComponent(infosTabPane, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(
                actorsPaneLayout.createParallelGroup(Alignment.LEADING).addComponent(actorNameField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ameField, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)).addContainerGap()));
    actorsPaneLayout.setVerticalGroup(actorsPaneLayout.createParallelGroup(Alignment.LEADING).addGroup(
        actorsPaneLayout
            .createSequentialGroup()
            .addContainerGap()
            .addGroup(
                actorsPaneLayout
                    .createParallelGroup(Alignment.LEADING)
                    .addGroup(
                        actorsPaneLayout.createSequentialGroup().addComponent(actorNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(ameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addGap(0, 0, Short.MAX_VALUE)).addComponent(infosTabPane, GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE))
            .addContainerGap()));

    infosTabbedPane.addTab(LocaleUtils.i18n("actors"), actorsPane);
    subtitlesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    subtitlesScrollPane.setViewportView(subtitlesList);

    GroupLayout subtitlesPaneLayout = new GroupLayout(subtitlesPane);
    subtitlesPane.setLayout(subtitlesPaneLayout);
    subtitlesPaneLayout.setHorizontalGroup(subtitlesPaneLayout.createParallelGroup(Alignment.LEADING).addGroup(
        subtitlesPaneLayout.createSequentialGroup().addContainerGap().addComponent(subtitlesScrollPane, GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE).addContainerGap()));
    subtitlesPaneLayout.setVerticalGroup(subtitlesPaneLayout.createParallelGroup(Alignment.LEADING).addGroup(
        subtitlesPaneLayout.createSequentialGroup().addContainerGap().addComponent(subtitlesScrollPane, GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE).addContainerGap()));

    infosTabbedPane.addTab(LocaleUtils.i18n("subtitles"), subtitlesPane);
    GroupLayout layout = new GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(layout
        .createParallelGroup(Alignment.LEADING)
        .addComponent(movieTb, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(
            layout
                .createSequentialGroup()
                .addComponent(thumbLbl, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(
                    layout
                        .createParallelGroup(Alignment.LEADING)
                        .addComponent(directorField, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(genreField, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(origTitleField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(
                            layout.createSequentialGroup().addComponent(webToolBar4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(runtimeField, GroupLayout.PREFERRED_SIZE, 154, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED).addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 155, GroupLayout.PREFERRED_SIZE).addGap(6, 6, 6))
                        .addComponent(synopsScroll, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGap(4, 4, 4))
        .addGroup(layout.createSequentialGroup().addComponent(infosTabbedPane, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE).addContainerGap()));
    layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(
        Alignment.TRAILING,
        layout
            .createSequentialGroup()
            .addComponent(movieTb, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(
                layout
                    .createParallelGroup(Alignment.LEADING, false)
                    .addGroup(
                        layout
                            .createSequentialGroup()
                            .addComponent(origTitleField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(directorField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(genreField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(
                                layout.createParallelGroup(Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(Alignment.TRAILING, false).addComponent(runtimeField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(jScrollPane1))
                                    .addComponent(webToolBar4, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(synopsScroll, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addComponent(thumbLbl, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(ComponentPlacement.RELATED).addComponent(infosTabbedPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
  }// </editor-fold>//GEN-END:initComponents

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private WebTextField actorNameField;
  private JPanel actorsPane;
  private WebTextField ameField;
  private WebList castingList;
  private WebLabel countryLbl;
  private WebList countryList;
  private WebTextField directorField;
  private WebLabel directorLbl;
  private WebList fanartList;
  private JPanel fanartsPane;
  private JScrollPane fanartsScrollPane;
  private WebTextField genreField;
  private WebLabel genreLbl;
  private JScrollPane infosTabPane;
  private WebTabbedPane infosTabbedPane;
  private JScrollPane jScrollPane1;
  private WebToolBar movieTb;
  private WebTextField origTitleField;
  private WebLabel origTitleLbl;
  private WebTextField runtimeField;
  private WebLabel runtimeLbl;
  private JLabel star1;
  private JLabel star2;
  private JLabel star3;
  private JLabel star4;
  private JLabel star5;
  private WebPanel starPanel;
  private WebList subtitlesList;
  private JPanel subtitlesPane;
  private JScrollPane subtitlesScrollPane;
  private JScrollPane synopsScroll;
  private JTextArea synopsisArea;
  private WebLabel synopsisLbl;
  private JLabel thumbLbl;
  private WebList thumbnailsList;
  private JPanel thumbnailsPane;
  private JScrollPane thumbsScrollPane;
  private WebLabel titleLbl;
  private WebProgressBarUI webProgressBarUI1;
  private WebToolBar webToolBar4;
  private WebLabel yearLbl;
  // End of variables declaration//GEN-END:variables
}
