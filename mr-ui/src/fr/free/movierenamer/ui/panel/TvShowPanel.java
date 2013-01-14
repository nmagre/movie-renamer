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
package fr.free.movierenamer.ui.panel;

import com.alee.extended.image.WebImageGallery;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.DefaultListModel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.text.WebTextField;
import com.alee.laf.toolbar.WebToolBar;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.TvShowInfo;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
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

/**
 * Class MoviePanel
 *
 * @author Magré Nicolas
 */
public class TvShowPanel extends MediaPanel {

  private static final long serialVersionUID = 1L;
  private MovieInfo tvShowInfo;
  private final MovieRenamer mr;
  private final Dimension thumbDim = new Dimension(160, 200);
  private final DefaultListModel castingModel = new DefaultListModel();
  private final DefaultListModel thumbnailsModel = new DefaultListModel();
  private final DefaultListModel fanartsModel = new DefaultListModel();
  private final DefaultListModel subTitleModel = new DefaultListModel();
  private final DefaultListModel audioModel = new DefaultListModel();
  private final DefaultListModel countryModel = new DefaultListModel();

  /**
   * Creates new form MoviePanel
   *
   * @param parent
   */
  public TvShowPanel(MovieRenamer parent) {
    initComponents();
    this.mr = parent;
  }

  @Override
  public MediaInfo getMediaInfo() {
    return tvShowInfo;
  }

  @Override
  public void setMediaInfo(MediaInfo mediaInfo) {
    if (mediaInfo instanceof MovieInfo) {
      tvShowInfo = (MovieInfo) mediaInfo;

      titleLbl.setText(tvShowInfo.getTitle());
      origTitleField.setText(tvShowInfo.getOriginalTitle());
      if (tvShowInfo.getReleasedDate() != null) {
        yearLbl.setText("(" + tvShowInfo.getReleasedDate().getYear() + ")");
      }
      runtimeField.setText(tvShowInfo.getRuntime() + " min");
      synopsisArea.setText(tvShowInfo.getOverview());
      genreField.setText(tvShowInfo.getGenres().toString());
      directorField.setText(tvShowInfo.getDirectors().toString());
      setRate(tvShowInfo.getRating());

      for (Locale country : tvShowInfo.getCountries()) {
        ImageIcon icon = new ImageIcon(ImageUtils.getImageFromJAR(String.format("country/%s.png", (country == null) ? "unknown" : country.getCountry().toLowerCase())));
        if (country != null) {
          icon.setDescription(country.getDisplayCountry());
        }
        countryModel.addElement(icon);
      }
      countryList.setModel(countryModel);

      Icon thumbIcon = ImageUtils.getIcon(tvShowInfo.getPosterPath(), thumbDim, "nothumb.png");
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
      // for (String country : countries) {
      // ImageIcon icon = Flag.getFlagByCountry(country);
      // icon.setDescription(country);
      // countryModel.addElement(icon);
      // }
      //
      // countryList.setModel(countryModel);

//      UISearchResult searchResult = mr.getSelectedSearchResult();
//      {
//        // load images
//        SearchMediaImagesWorker worker = new SearchMediaImagesWorker(mr.getErrorSupport(), mr, thumbnailsList, fanartList, searchResult);
//        // SearchMediaImagesWorkerListener listener = new SearchMediaImagesWorkerListener(mr, thumbnailsList, fanartList, worker);
//        // worker.addPropertyChangeListener(listener);
//        worker.execute();
//      }
//
//      {
//        // load casting
//        SearchMediaCastingWorker worker = new SearchMediaCastingWorker(mr.getErrorSupport(), mr, actorList, searchResult);
//        // SearchMediaCastingWorkerListener listener = new SearchMediaCastingWorkerListener(mr, actorList, worker);
//        // worker.addPropertyChangeListener(listener);
//        worker.execute();
//      }

    }
  }

  @Override
  public void clear() {
    // TODO
    setMediaInfo(new TvShowInfo(null));
    // star1.setIcon(STAR_EMPTY);
    // star2.setIcon(STAR_EMPTY);
    // star3.setIcon(STAR_EMPTY);
    // star4.setIcon(STAR_EMPTY);
    // star5.setIcon(STAR_EMPTY);
  }


  @Override
  public WebList getCastingList() {
    return actorList;
  }

  @Override
  public WebImageGallery getFanartsList() {
    return null;
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
    countryLbl = new WebLabel();
    tvShowTb = new WebToolBar();
    titleLbl = new WebLabel();
    yearLbl = new WebLabel();
    thumbLbl = new JLabel();
    origTitleField = new WebTextField();
    directorField = new WebTextField();
    genreField = new WebTextField();
    webToolBar3 = new WebToolBar();
    actorLbl = new WebLabel();
    jScrollPane3 = new JScrollPane();
    actorList = new WebList();
    webToolBar4 = new WebToolBar();
    synopsisLbl = new WebLabel();
    synopsScroll = new JScrollPane();
    synopsisArea = new JTextArea();
    runtimeField = new WebTextField();
    countryPane = new JScrollPane();
    countryList = new WebList(){
      public String getToolTipText(MouseEvent evt) {
        // Get item index
        int index = locationToIndex(evt.getPoint());

        // Get item
        ImageIcon item = (ImageIcon) getModel().getElementAt(index);

        // Return the tool tip text
        return item.getDescription();
      }
    };
    thumbnailTb = new WebToolBar();
    webLabel1 = new WebLabel();
    thumbsScrollPane = new JScrollPane();
    thumbnailsList = new WebList();
    fanartTb = new WebToolBar();
    webLabel2 = new WebLabel();
    fanartsScrollPane = new JScrollPane();
    fanartList = new WebList();
    subtitlesTb = new WebToolBar();
    webLabel3 = new WebLabel();
    subtitlesScrollPane = new JScrollPane();
    subtitlesList = new WebList();

    origTitleLbl.setText(LocaleUtils.i18n("origTitle")); // NOI18N
    origTitleLbl.setFont(new Font("Ubuntu", 1, 13)); // NOI18N

    directorLbl.setText(LocaleUtils.i18n("director")); // NOI18N
    directorLbl.setFont(new Font("Ubuntu", 1, 13)); // NOI18N

    runtimeLbl.setText(LocaleUtils.i18n("runtime")); // NOI18N
    runtimeLbl.setFont(new Font("Ubuntu", 1, 13)); // NOI18N

    genreLbl.setText(LocaleUtils.i18n("genre")); // NOI18N
    genreLbl.setFont(new Font("Ubuntu", 1, 13)); // NOI18N

    countryLbl.setText(LocaleUtils.i18n("country")); // NOI18N
    countryLbl.setFont(new Font("Ubuntu", 1, 13)); // NOI18N

    setMinimumSize(new Dimension(10, 380));
    setPreferredSize(new Dimension(562, 400));

    tvShowTb.setFloatable(false);
    tvShowTb.setRollover(true);

    titleLbl.setFont(new Font("Ubuntu", 1, 14)); // NOI18N
    tvShowTb.add(titleLbl);

    yearLbl.setFont(new Font("Ubuntu", 1, 14)); // NOI18N
    tvShowTb.add(yearLbl);

    thumbLbl.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

    origTitleField.setEditable(false);

    directorField.setEditable(false);

    webToolBar3.setFloatable(false);
    webToolBar3.setRollover(true);

    actorLbl.setText(LocaleUtils.i18n("actors")); // NOI18N
    actorLbl.setFont(new Font("Ubuntu", 1, 13)); // NOI18N
    webToolBar3.add(actorLbl);

    actorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    jScrollPane3.setViewportView(actorList);

    webToolBar4.setFloatable(false);
    webToolBar4.setRollover(true);

    synopsisLbl.setText(LocaleUtils.i18n("synopsis")); // NOI18N
    synopsisLbl.setFont(new Font("Ubuntu", 1, 13)); // NOI18N
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
    countryPane.setViewportView(countryList);

    thumbnailTb.setFloatable(false);
    thumbnailTb.setRollover(true);

    webLabel1.setText(LocaleUtils.i18n("thumbnails")); // NOI18N
    webLabel1.setFont(new Font("Ubuntu", 1, 13)); // NOI18N
    thumbnailTb.add(webLabel1);

    thumbnailsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    thumbsScrollPane.setViewportView(thumbnailsList);

    fanartTb.setFloatable(false);
    fanartTb.setRollover(true);
    fanartTb.setFont(new Font("Ubuntu", 1, 13)); // NOI18N

    webLabel2.setText(LocaleUtils.i18n("fanarts")); // NOI18N
    webLabel2.setFont(new Font("Ubuntu", 1, 13)); // NOI18N
    fanartTb.add(webLabel2);

    fanartList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    fanartsScrollPane.setViewportView(fanartList);

    subtitlesTb.setFloatable(false);
    subtitlesTb.setRollover(true);
    subtitlesTb.setFont(new Font("Ubuntu", 1, 13)); // NOI18N

    webLabel3.setText(LocaleUtils.i18n("subtitles")); // NOI18N
    webLabel3.setFont(new Font("Ubuntu", 1, 13)); // NOI18N
    subtitlesTb.add(webLabel3);

    subtitlesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    subtitlesScrollPane.setViewportView(subtitlesList);

    GroupLayout layout = new GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(Alignment.LEADING)
      .addComponent(tvShowTb, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addGroup(layout.createSequentialGroup()
        .addComponent(thumbLbl, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(Alignment.LEADING)
              .addComponent(directorField, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(genreField, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(origTitleField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addGroup(layout.createSequentialGroup()
                .addComponent(webToolBar3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(runtimeField, GroupLayout.PREFERRED_SIZE, 156, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(countryPane, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE))
              .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane3, GroupLayout.PREFERRED_SIZE, 154, GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33))))
          .addGroup(layout.createSequentialGroup()
            .addGap(41, 41, 41)
            .addComponent(webToolBar4, GroupLayout.PREFERRED_SIZE, 242, GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, Short.MAX_VALUE))))
      .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
        .addGap(9, 9, 9)
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
          .addComponent(thumbsScrollPane, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
          .addComponent(thumbnailTb, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addGap(26, 26, 26)
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
          .addComponent(fanartTb, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(fanartsScrollPane, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        .addGap(18, 18, 18)
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
          .addComponent(subtitlesTb, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(subtitlesScrollPane, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
      .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
        .addGap(0, 0, Short.MAX_VALUE)
        .addComponent(synopsScroll, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(Alignment.LEADING)
      .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
        .addComponent(tvShowTb, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
          .addComponent(thumbLbl, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(origTitleField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(directorField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(genreField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(Alignment.TRAILING)
              .addComponent(webToolBar3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
                .addComponent(runtimeField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(countryPane)))
            .addGap(18, 18, 18)
            .addComponent(jScrollPane3, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(webToolBar4, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)))
        .addGap(37, 37, 37)
        .addComponent(synopsScroll, GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addGroup(layout.createParallelGroup(Alignment.TRAILING)
          .addComponent(thumbnailTb, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
          .addComponent(fanartTb, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
          .addComponent(subtitlesTb, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
          .addComponent(thumbsScrollPane, GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
          .addComponent(fanartsScrollPane, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
          .addComponent(subtitlesScrollPane, GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)))
    );
  }// </editor-fold>//GEN-END:initComponents

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private WebLabel actorLbl;
  private WebList actorList;
  private WebLabel countryLbl;
  private WebList countryList;
  private JScrollPane countryPane;
  private WebTextField directorField;
  private WebLabel directorLbl;
  private WebList fanartList;
  private WebToolBar fanartTb;
  private JScrollPane fanartsScrollPane;
  private WebTextField genreField;
  private WebLabel genreLbl;
  private JScrollPane jScrollPane3;
  private WebTextField origTitleField;
  private WebLabel origTitleLbl;
  private WebTextField runtimeField;
  private WebLabel runtimeLbl;
  private WebList subtitlesList;
  private JScrollPane subtitlesScrollPane;
  private WebToolBar subtitlesTb;
  private JScrollPane synopsScroll;
  private JTextArea synopsisArea;
  private WebLabel synopsisLbl;
  private JLabel thumbLbl;
  private WebToolBar thumbnailTb;
  private WebList thumbnailsList;
  private JScrollPane thumbsScrollPane;
  private WebLabel titleLbl;
  private WebToolBar tvShowTb;
  private WebLabel webLabel1;
  private WebLabel webLabel2;
  private WebLabel webLabel3;
  private WebToolBar webToolBar3;
  private WebToolBar webToolBar4;
  private WebLabel yearLbl;
  // End of variables declaration//GEN-END:variables

  @Override
  public DefaultListModel getCastingModel() {
    return castingModel;
  }

  @Override
  public DefaultListModel getThumbnailsModel() {
    return thumbnailsModel;
  }

  @Override
  public DefaultListModel getFanartsModel() {
    return fanartsModel;
  }

 @Override
  public DefaultListModel getSubtitlesModel() {
    return null;
  }

  @Override
  public WebList getBannersList() {
    return null;
  }

  @Override
  public WebList getCdartsList() {
    return null;
  }

  @Override
  public WebList getLogosList() {
    return null;
  }

  @Override
  public WebList getClearartsList() {
    return null;
  }

  @Override
  public DefaultListModel getBannersModel() {
    return null;
  }

  @Override
  public DefaultListModel getCdartsModel() {
    return null;
  }

  @Override
  public DefaultListModel getLogosModel() {
    return null;
  }

  @Override
  public DefaultListModel getClearartsModel() {
    return null;
  }
}
