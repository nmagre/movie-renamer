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

import com.alee.extended.breadcrumb.WebBreadcrumb;
import com.alee.extended.breadcrumb.WebBreadcrumbToggleButton;
import com.alee.extended.transition.ComponentTransition;
import com.alee.extended.transition.effects.fade.FadeTransitionEffect;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.DefaultListModel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.text.WebTextArea;
import com.alee.laf.text.WebTextField;
import com.alee.laf.toolbar.WebToolBar;
import com.alee.managers.popup.PopupWay;
import com.alee.utils.SwingUtils;
import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.ImageInfo.ImageCategoryProperty;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.mediainfo.MediaAudio;
import fr.free.movierenamer.mediainfo.MediaSubTitle;
import fr.free.movierenamer.mediainfo.MediaTag;
import fr.free.movierenamer.mediainfo.MediaVideo;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.list.IIconList;
import fr.free.movierenamer.ui.list.IconListRenderer;
import fr.free.movierenamer.ui.list.UIMediaAudio;
import fr.free.movierenamer.ui.list.UIMediaSubTitle;
import fr.free.movierenamer.ui.res.Flag;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.LineBorder;

/**
 * Class MoviePanel
 *
 * @author Magré Nicolas
 */
public class MoviePanel extends MediaPanel {

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private WebLabel actorLbl;
  private WebList actorList;
  private WebTextField aspectRatioField;
  private WebLabel aspectRationLbl;
  private WebLabel audioLbl;
  private WebList audioList;
  private WebTextField codecField;
  private WebLabel codecLbl;
  private ComponentTransition componentTransition1;
  private ComponentTransition componentTransition2;
  private WebTextField containerField;
  private WebLabel containerLbl;
  private WebList countryList;
  private WebTextField directorField;
  private WebLabel directorLbl;
  private WebLabel fanartLbl;
  private WebLabel fanarttLbl;
  private WebBreadcrumbToggleButton fileBct;
  private WebPanel filePanel;
  private WebTextField frameRateField;
  private WebLabel frameRateLbl;
  private WebTextField genreField;
  private WebLabel genreLbl;
  private WebBreadcrumb imageBc;
  private WebBreadcrumbToggleButton imageBct;
  private WebPanel imagePanel;
  private WebPanel imagePanelPlus;
  private WebBreadcrumb infoBc;
  private JScrollPane jScrollPane1;
  private JScrollPane jScrollPane2;
  private JScrollPane jScrollPane3;
  private JScrollPane jScrollPane4;
  private WebBreadcrumbToggleButton moreBct;
  private WebBreadcrumbToggleButton movieBct;
  private WebPanel moviePanel;
  private WebTextField origTitleField;
  private WebLabel originalTitleLbl;
  private WebTextField resolutionField;
  private WebLabel resolutionLbl;
  private WebTextField scantypeField;
  private WebLabel scantypeLbl;
  private WebList subtitleList;
  private WebTextArea synopsisArea;
  private JScrollPane synopsisSp;
  private WebLabel thumbLbl;
  private WebLabel thumbnailLbl;
  private WebLabel titleLbl;
  private WebLabel videoLbl;
  private WebLabel webLabel5;
  private WebLabel webLabel6;
  private WebLabel webLabel8;
  private WebPanel webPanel3;
  private WebToolBar webToolBar1;
  // End of variables declaration//GEN-END:variables
  private static final long serialVersionUID = 1L;
  private final UISettings setting;
  private MediaInfo mediaInfo;
  private final DefaultListModel countryListModel = new DefaultListModel();
  private final DefaultListModel actorListModel = new DefaultListModel();
  private final DefaultListModel audioListModel = new DefaultListModel();
  private final DefaultListModel subtitleListModel = new DefaultListModel();

  /**
   * Creates new form MoviePanel
   *
   * @param mr
   */
  public MoviePanel(MovieRenamer mr) {
    super(mr, ImageCategoryProperty.thumb);
    this.setting = UISettings.getInstance();

    initComponents();
    SwingUtils.groupButtons(infoBc);
    SwingUtils.groupButtons(imageBc);
    movieBct.setSelected(true);
    imageBct.setSelected(true);
    componentTransition1.setTransitionEffect(new FadeTransitionEffect());
    componentTransition2.setTransitionEffect(new FadeTransitionEffect());

    countryList.setModel(countryListModel);
    actorList.setModel(actorListModel);
    audioList.setModel(audioListModel);
    subtitleList.setModel(subtitleListModel);

    countryList.setCellRenderer(new IconListRenderer<IIconList>(false));
    actorList.setCellRenderer(new IconListRenderer<IIconList>(false));
    audioList.setCellRenderer(new IconListRenderer<IIconList>(false));
    subtitleList.setCellRenderer(new IconListRenderer<IIconList>(false));

    webToolBar1.addToEnd(getStarPanel());
    webToolBar1.addToEnd(UIUtils.createSettingbutton(PopupWay.downLeft, "settingHelp", false, new WebCheckBox()));
  }

  @Override
  protected void clear() {
    SwingUtilities.invokeLater(new Thread() {
      @Override
      public void run() {
        mediaInfo = null;
        clearStars();
        countryListModel.clear();
        titleLbl.setText("");
        origTitleField.setText("");
        directorField.setText("");
        genreField.setText("");
//        runtimeField.setText("");
//        ratingField.setText("");
//        voteField.setText("");
        synopsisArea.setText("");
      }
    });
  }

  @Override
  public void setMediaInfo(MediaInfo mediaInfo) {
    this.mediaInfo = mediaInfo;
    MovieInfo movieInfo = (MovieInfo) mediaInfo;

    titleLbl.setText(movieInfo.getTitle() + " (" + movieInfo.getYear() + ")");
    setRate(movieInfo.getRating());
    for (Locale locale : movieInfo.getCountries()) {
      countryListModel.addElement(new UICountry(locale));
    }

    for (CastingInfo info : movieInfo.getCast()) {
      actorListModel.addElement(new UICastingInfo(info));
    }

    origTitleField.setText(movieInfo.getOriginalTitle());
    directorField.setText(movieInfo.getDirectors().toString());
    genreField.setText(movieInfo.getGenres().toString());
//    runtimeField.setText("" + movieInfo.getRuntime());
//    ratingField.setText("" + movieInfo.getRating());
//    voteField.setText("" + movieInfo.getVotes());
    synopsisArea.setText(movieInfo.getOverview());

  }

  public void setMediaTag(MediaTag mtag) {
    if (mtag != null) {
      MediaVideo mvideo = mtag.getMediaVideo();
      List<MediaAudio> maudios = mtag.getMediaAudios();
      List<MediaSubTitle> msubtitles = mtag.getMediaSubTitles();

      containerField.setText(mtag.getContainerFormat());
      // Video
      codecField.setText(mvideo.getCodec());
      frameRateField.setText("" + mvideo.getFrameRate());
      scantypeField.setText(mvideo.getScanType());
      aspectRatioField.setText("" + mvideo.getAspectRatio());
      resolutionField.setText(mvideo.getVideoResolution());

      // Audio
      for (MediaAudio audio : maudios) {
        audioListModel.addElement(new UIMediaAudio(audio));
      }

      // Subtitle
      for (MediaSubTitle subtitle : msubtitles) {
        subtitleListModel.addElement(new UIMediaSubTitle(subtitle));
      }
    }
  }

  public void clearMediaTag() {
    actorListModel.clear();
    audioListModel.clear();
    subtitleListModel.clear();
    containerField.setText("");
    codecField.setText("");
    frameRateField.setText("");
    scantypeField.setText("");
    aspectRatioField.setText("");
    resolutionField.setText("");
  }

  @Override
  public MediaInfo getMediaInfo() {
    return mediaInfo;
  }

  @Override
  public WebList getCastingList() {
    return actorList;
  }

  /**
   * This method is called from within the constructor to initialize the form.
   */
  //WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    filePanel = new WebPanel();
    containerLbl = new WebLabel();
    containerField = new WebTextField();
    videoLbl = new WebLabel();
    codecLbl = new WebLabel();
    codecField = new WebTextField();
    frameRateLbl = new WebLabel();
    frameRateField = new WebTextField();
    scantypeLbl = new WebLabel();
    scantypeField = new WebTextField();
    aspectRationLbl = new WebLabel();
    aspectRatioField = new WebTextField();
    resolutionLbl = new WebLabel();
    resolutionField = new WebTextField();
    audioLbl = new WebLabel();
    jScrollPane3 = new JScrollPane();
    audioList = new WebList();
    webLabel6 = new WebLabel();
    jScrollPane4 = new JScrollPane();
    subtitleList = new WebList();
    imagePanelPlus = new WebPanel();
    webToolBar1 = new WebToolBar();
    titleLbl = new WebLabel();
    webPanel3 = new WebPanel();
    componentTransition1 = new ComponentTransition();
    moviePanel = new WebPanel();
    origTitleField = new WebTextField();
    directorField = new WebTextField();
    synopsisSp = new JScrollPane();
    synopsisArea = new WebTextArea();
    genreField = new WebTextField();
    originalTitleLbl = new WebLabel();
    directorLbl = new WebLabel();
    genreLbl = new WebLabel();
    webLabel5 = new WebLabel();
    jScrollPane1 = new JScrollPane();
    countryList = new WebList();
    jScrollPane2 = new JScrollPane();
    actorList = new WebList();
    actorLbl = new WebLabel();
    webLabel8 = new WebLabel();
    componentTransition2 = new ComponentTransition();
    imagePanel = new WebPanel();
    thumbLbl = getThumbLabel(ImageCategoryProperty.thumb);
    fanartLbl = new WebLabel();
    thumbnailLbl = new WebLabel();
    fanarttLbl = new WebLabel();
    infoBc = new WebBreadcrumb();
    movieBct = new WebBreadcrumbToggleButton();
    fileBct = new WebBreadcrumbToggleButton();
    imageBc = new WebBreadcrumb();
    imageBct = new WebBreadcrumbToggleButton();
    moreBct = new WebBreadcrumbToggleButton();

    filePanel.setPreferredSize(new Dimension(568, 378));
    filePanel.setUndecorated(false);
    filePanel.setWebColored(false);

    containerLbl.setText(LocaleUtils.i18nExt("container")); // NOI18N
    containerLbl.setFont(new Font("Ubuntu", 1, 12)); // NOI18N

    containerField.setEditable(false);

    videoLbl.setText(LocaleUtils.i18nExt("video")); // NOI18N
    videoLbl.setFont(new Font("Ubuntu", 1, 14)); // NOI18N

    codecLbl.setText(LocaleUtils.i18nExt("codec")); // NOI18N
    codecLbl.setFont(new Font("Ubuntu", 1, 12)); // NOI18N

    codecField.setEditable(false);

    frameRateLbl.setText(LocaleUtils.i18nExt("framerate")); // NOI18N
    frameRateLbl.setFont(new Font("Ubuntu", 1, 12)); // NOI18N

    frameRateField.setEditable(false);

    scantypeLbl.setText(LocaleUtils.i18nExt("scanType")); // NOI18N
    scantypeLbl.setFont(new Font("Ubuntu", 1, 12)); // NOI18N

    scantypeField.setEditable(false);

    aspectRationLbl.setText(LocaleUtils.i18nExt("aspectRatio")); // NOI18N
    aspectRationLbl.setFont(new Font("Ubuntu", 1, 12)); // NOI18N

    resolutionLbl.setText(LocaleUtils.i18nExt("resolution")); // NOI18N
    resolutionLbl.setFont(new Font("Ubuntu", 1, 12)); // NOI18N

    resolutionField.setEditable(false);

    audioLbl.setText(LocaleUtils.i18nExt("audio")); // NOI18N
    audioLbl.setFont(new Font("Ubuntu", 1, 14)); // NOI18N

    audioList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    jScrollPane3.setViewportView(audioList);

    webLabel6.setText(LocaleUtils.i18nExt("subtitle")); // NOI18N
    webLabel6.setFont(new Font("Ubuntu", 1, 14)); // NOI18N

    subtitleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    jScrollPane4.setViewportView(subtitleList);

    GroupLayout filePanelLayout = new GroupLayout(filePanel);
    filePanel.setLayout(filePanelLayout);
    filePanelLayout.setHorizontalGroup(
      filePanelLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(filePanelLayout.createSequentialGroup()
        .addGroup(filePanelLayout.createParallelGroup(Alignment.LEADING)
          .addGroup(filePanelLayout.createSequentialGroup()
            .addGroup(filePanelLayout.createParallelGroup(Alignment.LEADING)
              .addGroup(filePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(filePanelLayout.createParallelGroup(Alignment.LEADING)
                  .addComponent(videoLbl, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
                  .addComponent(containerLbl, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)))
              .addGroup(Alignment.TRAILING, filePanelLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(filePanelLayout.createParallelGroup(Alignment.LEADING)
                  .addComponent(scantypeLbl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                  .addGroup(filePanelLayout.createParallelGroup(Alignment.LEADING)
                    .addComponent(frameRateLbl, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
                    .addComponent(codecLbl, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE))
                  .addComponent(aspectRationLbl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                  .addComponent(resolutionLbl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(filePanelLayout.createParallelGroup(Alignment.LEADING)
              .addComponent(containerField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(codecField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(frameRateField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(scantypeField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(aspectRatioField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(resolutionField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
          .addGroup(filePanelLayout.createSequentialGroup()
            .addGroup(filePanelLayout.createParallelGroup(Alignment.LEADING)
              .addGroup(filePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(audioLbl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
              .addGroup(filePanelLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)))
            .addPreferredGap(ComponentPlacement.UNRELATED)
            .addGroup(filePanelLayout.createParallelGroup(Alignment.LEADING)
              .addGroup(filePanelLayout.createSequentialGroup()
                .addComponent(webLabel6, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(181, 229, Short.MAX_VALUE))
              .addGroup(filePanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jScrollPane4, GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)))))
        .addContainerGap())
    );
    filePanelLayout.setVerticalGroup(
      filePanelLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(filePanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(filePanelLayout.createParallelGroup(Alignment.BASELINE)
          .addComponent(containerLbl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(containerField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addGap(18, 18, 18)
        .addGroup(filePanelLayout.createParallelGroup(Alignment.TRAILING)
          .addGroup(filePanelLayout.createSequentialGroup()
            .addComponent(videoLbl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.UNRELATED)
            .addComponent(codecLbl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addComponent(codecField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addGroup(filePanelLayout.createParallelGroup(Alignment.BASELINE)
          .addComponent(frameRateLbl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(frameRateField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addGroup(filePanelLayout.createParallelGroup(Alignment.BASELINE)
          .addComponent(scantypeLbl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(scantypeField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addGroup(filePanelLayout.createParallelGroup(Alignment.BASELINE)
          .addComponent(aspectRationLbl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(aspectRatioField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addGroup(filePanelLayout.createParallelGroup(Alignment.BASELINE)
          .addComponent(resolutionLbl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(resolutionField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addGap(18, 18, 18)
        .addGroup(filePanelLayout.createParallelGroup(Alignment.BASELINE)
          .addComponent(audioLbl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(webLabel6, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addGroup(filePanelLayout.createParallelGroup(Alignment.LEADING, false)
          .addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
          .addComponent(jScrollPane4, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        .addContainerGap(224, Short.MAX_VALUE))
    );

    GroupLayout imagePanelPlusLayout = new GroupLayout(imagePanelPlus);
    imagePanelPlus.setLayout(imagePanelPlusLayout);
    imagePanelPlusLayout.setHorizontalGroup(
      imagePanelPlusLayout.createParallelGroup(Alignment.LEADING)
      .addGap(0, 710, Short.MAX_VALUE)
    );
    imagePanelPlusLayout.setVerticalGroup(
      imagePanelPlusLayout.createParallelGroup(Alignment.LEADING)
      .addGap(0, 615, Short.MAX_VALUE)
    );

    setMinimumSize(new Dimension(10, 380));

    webToolBar1.setFloatable(false);
    webToolBar1.setRollover(true);

    titleLbl.setFont(new Font("Ubuntu", 1, 14)); // NOI18N
    titleLbl.setMargin(new Insets(0, 0, 0, 10));
    webToolBar1.add(titleLbl);

    moviePanel.setPreferredSize(new Dimension(568, 382));
    moviePanel.setUndecorated(false);
    moviePanel.setWebColored(false);

    origTitleField.setEditable(false);
    origTitleField.setFont(new Font("Ubuntu", 0, 12)); // NOI18N

    directorField.setEditable(false);
    directorField.setFont(new Font("Ubuntu", 0, 12)); // NOI18N

    synopsisArea.setColumns(20);
    synopsisArea.setLineWrap(true);
    synopsisArea.setRows(5);
    synopsisArea.setWrapStyleWord(true);
    synopsisSp.setViewportView(synopsisArea);

    genreField.setEditable(false);

    originalTitleLbl.setText(LocaleUtils.i18nExt("originalTitle")); // NOI18N
    originalTitleLbl.setFont(new Font("Ubuntu", 1, 12)); // NOI18N

    directorLbl.setText(LocaleUtils.i18nExt("director")); // NOI18N
    directorLbl.setFont(new Font("Ubuntu", 1, 12)); // NOI18N

    genreLbl.setText(LocaleUtils.i18nExt("genre")); // NOI18N
    genreLbl.setFont(new Font("Ubuntu", 1, 12)); // NOI18N

    webLabel5.setText(LocaleUtils.i18nExt("country")); // NOI18N
    webLabel5.setFont(new Font("Ubuntu", 1, 12)); // NOI18N

    countryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    countryList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    jScrollPane1.setViewportView(countryList);

    actorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    actorList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    jScrollPane2.setViewportView(actorList);

    actorLbl.setText(LocaleUtils.i18nExt("actor")); // NOI18N
    actorLbl.setFont(new Font("Ubuntu", 1, 12)); // NOI18N

    webLabel8.setText(LocaleUtils.i18nExt("synopsis")); // NOI18N
    webLabel8.setFont(new Font("Ubuntu", 1, 12)); // NOI18N

    GroupLayout moviePanelLayout = new GroupLayout(moviePanel);
    moviePanel.setLayout(moviePanelLayout);
    moviePanelLayout.setHorizontalGroup(
      moviePanelLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(moviePanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(moviePanelLayout.createParallelGroup(Alignment.LEADING)
          .addGroup(moviePanelLayout.createSequentialGroup()
            .addComponent(webLabel8, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, Short.MAX_VALUE))
          .addGroup(moviePanelLayout.createSequentialGroup()
            .addGroup(moviePanelLayout.createParallelGroup(Alignment.LEADING)
              .addComponent(synopsisSp, GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE)
              .addGroup(moviePanelLayout.createSequentialGroup()
                .addGroup(moviePanelLayout.createParallelGroup(Alignment.LEADING)
                  .addGroup(moviePanelLayout.createParallelGroup(Alignment.LEADING, false)
                    .addComponent(originalTitleLbl, GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                    .addComponent(directorLbl, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(genreLbl, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(webLabel5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                  .addComponent(actorLbl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(moviePanelLayout.createParallelGroup(Alignment.TRAILING)
                  .addComponent(jScrollPane1, Alignment.LEADING)
                  .addComponent(genreField, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(directorField, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(origTitleField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(jScrollPane2))))
            .addContainerGap())))
    );
    moviePanelLayout.setVerticalGroup(
      moviePanelLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(moviePanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(moviePanelLayout.createParallelGroup(Alignment.BASELINE)
          .addComponent(origTitleField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(originalTitleLbl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addGroup(moviePanelLayout.createParallelGroup(Alignment.BASELINE)
          .addComponent(directorField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(directorLbl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addGroup(moviePanelLayout.createParallelGroup(Alignment.BASELINE)
          .addComponent(genreField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(genreLbl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addGroup(moviePanelLayout.createParallelGroup(Alignment.LEADING)
          .addComponent(webLabel5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
        .addGroup(moviePanelLayout.createParallelGroup(Alignment.LEADING)
          .addGroup(moviePanelLayout.createSequentialGroup()
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE))
          .addGroup(moviePanelLayout.createSequentialGroup()
            .addGap(18, 18, 18)
            .addComponent(actorLbl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(webLabel8, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(synopsisSp, GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
        .addGap(31, 31, 31))
    );

    componentTransition1.add(moviePanel);

    imagePanel.setFont(new Font("Ubuntu", 1, 12)); // NOI18N
    imagePanel.setUndecorated(false);
    imagePanel.setWebColored(false);

    thumbLbl.setBorder(new LineBorder(new Color(204, 204, 204), 1, true));
    thumbLbl.setHorizontalAlignment(SwingConstants.CENTER);
    thumbLbl.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        thumbLblMouseReleased(evt);
      }
    });

    fanartLbl.setBorder(new LineBorder(new Color(204, 204, 204), 1, true));
    fanartLbl.setHorizontalAlignment(SwingConstants.CENTER);
    fanartLbl.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent evt) {
        fanartLblMouseReleased(evt);
      }
    });

    thumbnailLbl.setText("Thumbnail");
    thumbnailLbl.setFont(new Font("Ubuntu", 1, 12)); // NOI18N

    fanarttLbl.setText("Fanart");
    fanarttLbl.setFont(new Font("Ubuntu", 1, 12)); // NOI18N

    GroupLayout imagePanelLayout = new GroupLayout(imagePanel);
    imagePanel.setLayout(imagePanelLayout);
    imagePanelLayout.setHorizontalGroup(
      imagePanelLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(imagePanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(imagePanelLayout.createParallelGroup(Alignment.LEADING)
          .addComponent(thumbnailLbl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addGroup(imagePanelLayout.createSequentialGroup()
            .addGap(12, 12, 12)
            .addComponent(thumbLbl, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE))
          .addComponent(fanartLbl, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
          .addComponent(fanarttLbl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addContainerGap(548, Short.MAX_VALUE))
    );
    imagePanelLayout.setVerticalGroup(
      imagePanelLayout.createParallelGroup(Alignment.LEADING)
      .addGroup(imagePanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(thumbnailLbl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(thumbLbl, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.UNRELATED)
        .addComponent(fanarttLbl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(fanartLbl, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(352, Short.MAX_VALUE))
    );

    componentTransition2.add(imagePanel);

    infoBc.setFocusable(false);

    movieBct.setIcon(new ImageIcon(getClass().getResource("/image/ui/16/movie.png"))); // NOI18N
    movieBct.setText("Movie");
    movieBct.setFocusable(false);
    movieBct.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        movieBctActionPerformed(evt);
      }
    });
    infoBc.add(movieBct);

    fileBct.setIcon(new ImageIcon(getClass().getResource("/image/ui/16/info.png"))); // NOI18N
    fileBct.setText("File");
    fileBct.setFocusable(false);
    fileBct.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        fileBctActionPerformed(evt);
      }
    });
    infoBc.add(fileBct);

    imageBc.setFocusable(false);

    imageBct.setIcon(new ImageIcon(getClass().getResource("/image/ui/16/image.png"))); // NOI18N
    imageBct.setText("Image");
    imageBct.setFocusable(false);
    imageBct.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        imageBctActionPerformed(evt);
      }
    });
    imageBc.add(imageBct);

    moreBct.setIcon(new ImageIcon(getClass().getResource("/image/ui/16/image_add.png"))); // NOI18N
    moreBct.setText("More");
    moreBct.setFocusable(false);
    moreBct.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        moreBctActionPerformed(evt);
      }
    });
    imageBc.add(moreBct);

    GroupLayout webPanel3Layout = new GroupLayout(webPanel3);
    webPanel3.setLayout(webPanel3Layout);
    webPanel3Layout.setHorizontalGroup(
      webPanel3Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(webPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(webPanel3Layout.createParallelGroup(Alignment.LEADING)
          .addComponent(componentTransition2, GroupLayout.PREFERRED_SIZE, 176, GroupLayout.PREFERRED_SIZE)
          .addComponent(imageBc, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(ComponentPlacement.RELATED)
        .addGroup(webPanel3Layout.createParallelGroup(Alignment.LEADING)
          .addGroup(webPanel3Layout.createSequentialGroup()
            .addComponent(infoBc, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, Short.MAX_VALUE))
          .addComponent(componentTransition1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    webPanel3Layout.setVerticalGroup(
      webPanel3Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(webPanel3Layout.createSequentialGroup()
        .addGroup(webPanel3Layout.createParallelGroup(Alignment.LEADING)
          .addComponent(infoBc, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
          .addComponent(imageBc, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
        .addGap(9, 9, 9)
        .addGroup(webPanel3Layout.createParallelGroup(Alignment.LEADING)
          .addComponent(componentTransition1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(componentTransition2, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        .addContainerGap())
    );

    GroupLayout layout = new GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(Alignment.LEADING)
          .addComponent(webPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(webToolBar1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(Alignment.LEADING)
      .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(webToolBar1, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(webPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

  private void thumbLblMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_thumbLblMouseReleased
    showGalleryPanel(ImageInfo.ImageCategoryProperty.thumb);
  }//GEN-LAST:event_thumbLblMouseReleased

  private void movieBctActionPerformed(ActionEvent evt) {//GEN-FIRST:event_movieBctActionPerformed
    componentTransition1.performTransition(moviePanel);
  }//GEN-LAST:event_movieBctActionPerformed

  private void fileBctActionPerformed(ActionEvent evt) {//GEN-FIRST:event_fileBctActionPerformed
    componentTransition1.performTransition(filePanel);
  }//GEN-LAST:event_fileBctActionPerformed

  private void imageBctActionPerformed(ActionEvent evt) {//GEN-FIRST:event_imageBctActionPerformed
    componentTransition2.performTransition(imagePanel);
  }//GEN-LAST:event_imageBctActionPerformed

  private void moreBctActionPerformed(ActionEvent evt) {//GEN-FIRST:event_moreBctActionPerformed
    componentTransition2.performTransition(imagePanelPlus);
  }//GEN-LAST:event_moreBctActionPerformed

  private void fanartLblMouseReleased(MouseEvent evt) {//GEN-FIRST:event_fanartLblMouseReleased
    showGalleryPanel(ImageInfo.ImageCategoryProperty.fanart);
  }//GEN-LAST:event_fanartLblMouseReleased

  @Override
  public DefaultListModel getCastingModel() {
    return actorListModel;
  }

  @Override
  protected String getPanelName() {
    return "Movie Panel";
  }

  public class UICastingInfo implements IIconList {

    private CastingInfo info;

    public UICastingInfo(CastingInfo info) {
      this.info = info;
    }

    @Override
    public Icon getIcon() {
      return null;
    }

    @Override
    public void setIcon(Icon icon) {
      //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
      return info.getName();
    }

    @Override
    public URI getUri(ImageInfo.ImageSize size) {
      return info.getPicturePath();
    }
  }

  public class UICountry implements IIconList {

    private Locale country;
    private Icon icon;

    public UICountry(Locale country) {
      this.country = country;
      icon = Flag.getFlag(country.getCountry()).getIcon();
    }

    @Override
    public Icon getIcon() {
      return icon;
    }

    @Override
    public void setIcon(Icon icon) {
      this.icon = icon;
    }

    @Override
    public String toString() {
      return country.getDisplayCountry(UISettings.getInstance().coreInstance.getAppLanguage());
    }

    @Override
    public URI getUri(ImageInfo.ImageSize size) {
      return null;
    }
  }
}
