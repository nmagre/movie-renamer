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

import fr.free.movierenamer.media.MediaImage;
import fr.free.movierenamer.media.tvshow.TvShow;
import fr.free.movierenamer.media.tvshow.TvShowEpisode;
import fr.free.movierenamer.media.tvshow.TvShowInfo;
import fr.free.movierenamer.media.tvshow.TvShowSeason;
import fr.free.movierenamer.ui.res.IMediaPanel;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Class TvShowPanel
 *
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
public class TvShowPanel extends JPanel implements IMediaPanel {
  
  private class ActorImage {

    private String name;
    private String desc;
    private ImageIcon img;

    public ActorImage(String name, String desc, ImageIcon img) {
      this.name = name;
      this.desc = desc;
      if (img == null) {
        this.img = new ImageIcon(actorDefault.getImage().getScaledInstance(actorListDim.width, actorListDim.height, Image.SCALE_DEFAULT));
      } else {
        this.img = img;
      }
    }

    public ImageIcon getImage() {
      return img;
    }

    public String getDesc() {
      return desc;
    }

    public String getName() {
      return name;
    }
  }

  private final DefaultListModel seasonsModel = new DefaultListModel();
  private final DefaultListModel episodesModel = new DefaultListModel();
  private TvShow tvshow;
  private Dimension thumbDim = new Dimension(160, 200);
  public Dimension thumbListDim = new Dimension(60, 90);
  public Dimension fanartListDim = new Dimension(200, 90);
  public Dimension actorListDim = new Dimension(30, 53);
  private final ImageIcon actorDefault = new ImageIcon(getClass().getResource("/image/unknown.png"));
   private final Icon STAR = new ImageIcon(getClass().getResource("/image/star.png"));
  private final Icon STAR_HALF = new ImageIcon(getClass().getResource("/image/star-half.png"));
  private final Icon STAR_EMPTY = new ImageIcon(getClass().getResource("/image/star-empty.png"));
  private List<ActorImage> actors;
  private List<MediaImage> thumbs;
  private List<MediaImage> fanarts;
  private final Settings setting =Settings.getInstance();
  private final Cache cache = Cache.getInstance();
  private static final String SEP = " : ";

  /**
   * Creates new form TvShowPanel
   */
  public TvShowPanel() {
    initComponents();

    origTitleLbl.setText(origTitleLbl.getText() + SEP);
    directorLbl.setText(directorLbl.getText() + SEP);
    runtimeLbl.setText(runtimeLbl.getText() + SEP);
    genreLbl.setText(genreLbl.getText() + SEP);

    origTitleField.setLeadingComponent(origTitleLbl);
    directorField.setLeadingComponent(directorLbl);
    runtimeField.setLeadingComponent(runtimeLbl);
    genreField.setLeadingComponent(genreLbl);

    // Add component to toolbar
    tvshowTb.addToEnd(starPanel);

    actors = new ArrayList<ActorImage>();
    thumbs = new ArrayList<MediaImage>();
    fanarts = new ArrayList<MediaImage>();

    seasonsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    seasonsList.setModel(seasonsModel);
    seasonsList.addListSelectionListener(createSeasonsListListener());

    episodesList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    episodesList.setModel(episodesModel);
    episodesList.addListSelectionListener(createEpisodesListListener());
  }

  /**
   * Create seasons list selection listener
   *
   * @return
   */
  private ListSelectionListener createSeasonsListListener() {
    return new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent e) {
        TvShowInfo tvshowInfo = tvshow.getInfo();
        TvShowSeason season = tvshowInfo.getSeasons().get(seasonsList.getSelectedIndex());
        for (TvShowEpisode episode : season.getEpisodes()) {
          String ep = episode.getNum() + " - " + episode.getTitle();
          if (episode.getOriginalTitle() != null && episode.getOriginalTitle().trim().length()>0) {
            ep += " (" + episode.getOriginalTitle() + ")";
          }
          episodesModel.addElement(ep);
          if(episodesList.getSelectedIndex() < 0)
          {
            if (episode.getNum() == tvshow.getSearchSxe().getEpisode()) {
              episodesList.setSelectedValue(ep, true);
            }
          }
        }
      }

    };
  }

  /**
   * Create episodes list selection listener
   *
   * @return
   */
  private ListSelectionListener createEpisodesListListener() {
    return new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent e) {
        TvShowInfo tvshowInfo = tvshow.getInfo();
        TvShowSeason season = tvshowInfo.getSeasons().get(seasonsList.getSelectedIndex());
        TvShowEpisode episode = season.getEpisodes().get(episodesList.getSelectedIndex());
        episodeSynopsisArea.setText(episode.getSynopsis());
        episodeSynopsisArea.setCaretPosition(0);
        tvshow.setSelectedEpisode(episode);
      }
    };
  }

  public void addTvshowInfo(final TvShow tvshow) {// List<TvShowSeason> seasons, SxE sxe) {
    this.tvshow = tvshow;
    TvShowInfo tvshowInfo = tvshow.getInfo();

    try {
      Image img = cache.getImage(new URL(tvshowInfo.getPoster()), Cache.CacheType.THUMB);
      if (img != null) {
        thumbLbl.setIcon(new ImageIcon(img.getScaledInstance(thumbDim.width, thumbDim.height, Image.SCALE_DEFAULT)));
      }
    } catch (MalformedURLException e) {
    } catch (IOException e) {
    }

    titleLbl.setText(tvshowInfo.getTitle());
    if(tvshowInfo.getYear() != null && tvshowInfo.getYear().trim().length() > 0) {
      yearLbl.setText("(" + tvshowInfo.getYear() + ")");
    }
    genreField.setText(tvshowInfo.getGenresString(" | ", 0));
    synopsisArea.setText(tvshowInfo.getSynopsis());
    synopsisArea.setCaretPosition(0);
    setRate(Float.parseFloat(tvshowInfo.getRating().replace(",", ".")));

    for (final TvShowSeason season : tvshowInfo.getSeasons()) {
      System.out.println(season);
      seasonsModel.addElement(season.getNum());
      if(seasonsList.getSelectedIndex() < 0)
      {
        if (season.getNum() == tvshow.getSearchSxe().getSeason()) {
          seasonsList.setSelectedValue(season.getNum(), true);
        }
      }
    }
  }

  /**
   * Set star compared with rate
   *
   * @param rate
   */
  private void setRate(Float rate) {
    if (rate < 0.00) {
      return;
    }
    rate /= 2;
    int n = rate.intValue();
    switch (n) {
      case 0:
        break;
      case 1:
        star.setIcon(STAR);
        if ((rate - rate.intValue()) >= 0.50) {
          star1.setIcon(STAR_HALF);
        }
        break;
      case 2:
        star.setIcon(STAR);
        star1.setIcon(STAR);
        if ((rate - rate.intValue()) >= 0.50) {
          star2.setIcon(STAR_HALF);
        }
        break;
      case 3:
        star.setIcon(STAR);
        star1.setIcon(STAR);
        star2.setIcon(STAR);
        if ((rate - rate.intValue()) >= 0.50) {
          star3.setIcon(STAR_HALF);
        }
        break;
      case 4:
        star.setIcon(STAR);
        star1.setIcon(STAR);
        star2.setIcon(STAR);
        star3.setIcon(STAR);
        if ((rate - rate.intValue()) >= 0.50) {
          star4.setIcon(STAR_HALF);
        }
        break;
      case 5:
        star.setIcon(STAR);
        star1.setIcon(STAR);
        star2.setIcon(STAR);
        star3.setIcon(STAR);
        star4.setIcon(STAR);
        break;
      default:
        break;
    }
  }

  @Override
  public void addImageToList(Image img, MediaImage mediaImage, boolean selectLast) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void addActorToList(String actor, Image actorImg, String desc) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void clear() {
    // TODO
    thumbLbl.setIcon(null);
    genreField.setText("");
    star.setIcon(STAR_EMPTY);
    star1.setIcon(STAR_EMPTY);
    star2.setIcon(STAR_EMPTY);
    star3.setIcon(STAR_EMPTY);
    star4.setIcon(STAR_EMPTY);
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        origTitleLbl = new com.alee.laf.label.WebLabel();
        directorLbl = new com.alee.laf.label.WebLabel();
        runtimeLbl = new com.alee.laf.label.WebLabel();
        genreLbl = new com.alee.laf.label.WebLabel();
        starPanel = new com.alee.laf.panel.WebPanel();
        star4 = new javax.swing.JLabel();
        star3 = new javax.swing.JLabel();
        star2 = new javax.swing.JLabel();
        star1 = new javax.swing.JLabel();
        star = new javax.swing.JLabel();
        tvshowTb = new com.alee.laf.toolbar.WebToolBar();
        titleLbl = new com.alee.laf.label.WebLabel();
        yearLbl = new com.alee.laf.label.WebLabel();
        genreField = new com.alee.laf.text.WebTextField();
        synopsisPane = new javax.swing.JScrollPane();
        synopsisArea = new javax.swing.JTextArea();
        seasonsPane = new javax.swing.JScrollPane();
        seasonsList = new javax.swing.JList();
        episodesPane = new javax.swing.JScrollPane();
        episodesList = new javax.swing.JList();
        episodeSynopsisPane = new javax.swing.JScrollPane();
        episodeSynopsisArea = new javax.swing.JTextArea();
        thumbLbl = new javax.swing.JLabel();
        webToolBar3 = new com.alee.laf.toolbar.WebToolBar();
        webLabel5 = new com.alee.laf.label.WebLabel();
        webToolBar4 = new com.alee.laf.toolbar.WebToolBar();
        synopsisLbl = new com.alee.laf.label.WebLabel();
        directorField = new com.alee.laf.text.WebTextField();
        origTitleField = new com.alee.laf.text.WebTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        actorList = new com.alee.laf.list.WebList();
        webToolBar5 = new com.alee.laf.toolbar.WebToolBar();
        webLabel6 = new com.alee.laf.label.WebLabel();
        runtimeField = new com.alee.laf.text.WebTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        countryList = new com.alee.laf.list.WebList(){
            public String getToolTipText(MouseEvent evt) {
                // Get item index
                int index = locationToIndex(evt.getPoint());

                // Get item
                ImageIcon item = (ImageIcon) getModel().getElementAt(index);

                // Return the tool tip text
                return item.getDescription();
            }
        };
        webToolBar6 = new com.alee.laf.toolbar.WebToolBar();
        seasonLbl = new com.alee.laf.label.WebLabel();
        webToolBar7 = new com.alee.laf.toolbar.WebToolBar();
        episodeLbl = new com.alee.laf.label.WebLabel();

        origTitleLbl.setText(Utils.i18n("origTitle")); // NOI18N
        origTitleLbl.setFont(new java.awt.Font("Ubuntu", 1, 13)); // NOI18N

        directorLbl.setText(Utils.i18n("director")); // NOI18N
        directorLbl.setFont(new java.awt.Font("Ubuntu", 1, 13)); // NOI18N

        runtimeLbl.setText(Utils.i18n("runtime")); // NOI18N
        runtimeLbl.setFont(new java.awt.Font("Ubuntu", 1, 13)); // NOI18N

        genreLbl.setText("Genre");
        genreLbl.setFont(new java.awt.Font("Ubuntu", 1, 13)); // NOI18N

        starPanel.setAlignmentY(0.0F);

        star4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/star-empty.png"))); // NOI18N

        star3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/star-empty.png"))); // NOI18N

        star2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/star-empty.png"))); // NOI18N

        star1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/star-empty.png"))); // NOI18N

        star.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/star-empty.png"))); // NOI18N

        javax.swing.GroupLayout starPanelLayout = new javax.swing.GroupLayout(starPanel);
        starPanel.setLayout(starPanelLayout);
        starPanelLayout.setHorizontalGroup(
            starPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(starPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(star)
                .addGap(8, 8, 8)
                .addComponent(star1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(star2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(star3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(star4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        starPanelLayout.setVerticalGroup(
            starPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(star1)
            .addComponent(star2)
            .addComponent(star3)
            .addComponent(star4)
            .addComponent(star)
        );

        tvshowTb.setFloatable(false);
        tvshowTb.setRollover(true);

        titleLbl.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        tvshowTb.add(titleLbl);

        yearLbl.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        tvshowTb.add(yearLbl);

        synopsisArea.setColumns(20);
        synopsisArea.setLineWrap(true);
        synopsisArea.setRows(5);
        synopsisArea.setWrapStyleWord(true);
        synopsisPane.setViewportView(synopsisArea);

        seasonsList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        seasonsPane.setViewportView(seasonsList);

        episodesList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        episodesPane.setViewportView(episodesList);

        episodeSynopsisArea.setColumns(20);
        episodeSynopsisArea.setLineWrap(true);
        episodeSynopsisArea.setRows(5);
        episodeSynopsisArea.setWrapStyleWord(true);
        episodeSynopsisPane.setViewportView(episodeSynopsisArea);

        thumbLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        webToolBar3.setFloatable(false);
        webToolBar3.setRollover(true);

        webLabel5.setText("Episode Synopsis");
        webLabel5.setFont(new java.awt.Font("Ubuntu", 1, 13)); // NOI18N
        webToolBar3.add(webLabel5);

        webToolBar4.setFloatable(false);
        webToolBar4.setRollover(true);

        synopsisLbl.setText("Synopsis");
        synopsisLbl.setFont(new java.awt.Font("Ubuntu", 1, 13)); // NOI18N
        webToolBar4.add(synopsisLbl);

        directorField.setEditable(false);

        origTitleField.setEditable(false);

        actorList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(actorList);

        webToolBar5.setFloatable(false);
        webToolBar5.setRollover(true);

        webLabel6.setText(Utils.i18n("actor")); // NOI18N
        webLabel6.setFont(new java.awt.Font("Ubuntu", 1, 13)); // NOI18N
        webToolBar5.add(webLabel6);

        countryList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        countryList.setAutoscrolls(false);
        countryList.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        jScrollPane1.setViewportView(countryList);

        webToolBar6.setFloatable(false);
        webToolBar6.setRollover(true);

        seasonLbl.setText("Seasons");
        seasonLbl.setFont(new java.awt.Font("Ubuntu", 1, 13)); // NOI18N
        webToolBar6.add(seasonLbl);

        webToolBar7.setFloatable(false);
        webToolBar7.setRollover(true);

        episodeLbl.setText("Episodes");
        episodeLbl.setFont(new java.awt.Font("Ubuntu", 1, 13)); // NOI18N
        webToolBar7.add(episodeLbl);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tvshowTb, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(thumbLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(genreField, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                            .addComponent(directorField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                            .addComponent(origTitleField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane3)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(webToolBar5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(9, 9, 9)
                                .addComponent(runtimeField, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(episodeSynopsisPane)
                    .addComponent(webToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(synopsisPane, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
                    .addComponent(webToolBar4, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(webToolBar6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(seasonsPane))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(webToolBar7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(episodesPane))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tvshowTb, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(thumbLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(origTitleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(directorField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(genreField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(webToolBar5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(runtimeField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addGap(5, 5, 5)
                .addComponent(webToolBar4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(synopsisPane, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(webToolBar6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(webToolBar7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(seasonsPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(episodesPane, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(webToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(episodeSynopsisPane, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(655, 655, 655))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.alee.laf.list.WebList actorList;
    private com.alee.laf.list.WebList countryList;
    private com.alee.laf.text.WebTextField directorField;
    private com.alee.laf.label.WebLabel directorLbl;
    private com.alee.laf.label.WebLabel episodeLbl;
    private javax.swing.JTextArea episodeSynopsisArea;
    private javax.swing.JScrollPane episodeSynopsisPane;
    private javax.swing.JList episodesList;
    private javax.swing.JScrollPane episodesPane;
    private com.alee.laf.text.WebTextField genreField;
    private com.alee.laf.label.WebLabel genreLbl;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private com.alee.laf.text.WebTextField origTitleField;
    private com.alee.laf.label.WebLabel origTitleLbl;
    private com.alee.laf.text.WebTextField runtimeField;
    private com.alee.laf.label.WebLabel runtimeLbl;
    private com.alee.laf.label.WebLabel seasonLbl;
    private javax.swing.JList seasonsList;
    private javax.swing.JScrollPane seasonsPane;
    private javax.swing.JLabel star;
    private javax.swing.JLabel star1;
    private javax.swing.JLabel star2;
    private javax.swing.JLabel star3;
    private javax.swing.JLabel star4;
    private com.alee.laf.panel.WebPanel starPanel;
    private javax.swing.JTextArea synopsisArea;
    private com.alee.laf.label.WebLabel synopsisLbl;
    private javax.swing.JScrollPane synopsisPane;
    private javax.swing.JLabel thumbLbl;
    private com.alee.laf.label.WebLabel titleLbl;
    private com.alee.laf.toolbar.WebToolBar tvshowTb;
    private com.alee.laf.label.WebLabel webLabel5;
    private com.alee.laf.label.WebLabel webLabel6;
    private com.alee.laf.toolbar.WebToolBar webToolBar3;
    private com.alee.laf.toolbar.WebToolBar webToolBar4;
    private com.alee.laf.toolbar.WebToolBar webToolBar5;
    private com.alee.laf.toolbar.WebToolBar webToolBar6;
    private com.alee.laf.toolbar.WebToolBar webToolBar7;
    private com.alee.laf.label.WebLabel yearLbl;
    // End of variables declaration//GEN-END:variables

  @Override
  public void setDisplay(Settings setting) {
    // TODO
  }
}
