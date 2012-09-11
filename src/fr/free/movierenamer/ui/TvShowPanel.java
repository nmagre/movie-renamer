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

import fr.free.movierenamer.media.tvshow.TvShowInfo;

import fr.free.movierenamer.media.movie.MovieInfo;

import fr.free.movierenamer.media.tvshow.TvShow;

import fr.free.movierenamer.media.movie.Movie;

import javax.swing.event.ListSelectionEvent;

import javax.swing.event.ListSelectionListener;

import fr.free.movierenamer.media.MediaImage;
import fr.free.movierenamer.media.tvshow.SxE;
import fr.free.movierenamer.media.tvshow.TvShowEpisode;
import fr.free.movierenamer.media.tvshow.TvShowSeason;
import fr.free.movierenamer.ui.res.IMediaPanel;
import fr.free.movierenamer.utils.Settings;
import java.awt.Image;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

/**
 * Class TvShowPanel
 *
 * @author Nicolas Magré
 * @author QUÉMÉNEUR Simon
 */
public class TvShowPanel extends JPanel implements IMediaPanel {

  private final DefaultListModel seasonsModel = new DefaultListModel();
  private final DefaultListModel episodesModel = new DefaultListModel();
  private TvShow tvshow;

  /**
   * Creates new form TvShowPanel
   */
  public TvShowPanel() {
    initComponents();
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
        TvShowInfo tvshowInfo = tvshow.getTvShowInfo();
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
        TvShowInfo tvshowInfo = tvshow.getTvShowInfo();
        TvShowSeason season = tvshowInfo.getSeasons().get(seasonsList.getSelectedIndex());
        TvShowEpisode episode = season.getEpisodes().get(episodesList.getSelectedIndex());
        episodeSynopsisArea.setText(episode.getSynopsis());
      }

    };
  }

  public void addTvshowInfo(final TvShow tvshow) {// List<TvShowSeason> seasons, SxE sxe) {
    this.tvshow = tvshow;
    TvShowInfo tvshowInfo = tvshow.getTvShowInfo();

    titleLbl.setText(tvshowInfo.getTitle());
    if(tvshowInfo.getYear() != null && tvshowInfo.getYear().trim().length() > 0) {
      yearLbl.setText("(" + tvshowInfo.getYear() + ")");
    }
    synopsisArea.setText(tvshowInfo.getSynopsis());
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
//    if (rate < 0.00) {
//      return;
//    }
//    rate /= 2;
//    int n = rate.intValue();
//    switch (n) {
//      case 0:
//        break;
//      case 1:
//        star.setIcon(STAR);
//        if ((rate - rate.intValue()) >= 0.50) {
//          star1.setIcon(STAR_HALF);
//        }
//        break;
//      case 2:
//        star.setIcon(STAR);
//        star1.setIcon(STAR);
//        if ((rate - rate.intValue()) >= 0.50) {
//          star2.setIcon(STAR_HALF);
//        }
//        break;
//      case 3:
//        star.setIcon(STAR);
//        star1.setIcon(STAR);
//        star2.setIcon(STAR);
//        if ((rate - rate.intValue()) >= 0.50) {
//          star3.setIcon(STAR_HALF);
//        }
//        break;
//      case 4:
//        star.setIcon(STAR);
//        star1.setIcon(STAR);
//        star2.setIcon(STAR);
//        star3.setIcon(STAR);
//        if ((rate - rate.intValue()) >= 0.50) {
//          star4.setIcon(STAR_HALF);
//        }
//        break;
//      case 5:
//        star.setIcon(STAR);
//        star1.setIcon(STAR);
//        star2.setIcon(STAR);
//        star3.setIcon(STAR);
//        star4.setIcon(STAR);
//        break;
//      default:
//        break;
//    }
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
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tvshowTb = new com.alee.laf.toolbar.WebToolBar();
        titleLbl = new com.alee.laf.label.WebLabel();
        yearLbl = new com.alee.laf.label.WebLabel();
        synopsisPane = new javax.swing.JScrollPane();
        synopsisArea = new javax.swing.JTextArea();
        seasonsPane = new javax.swing.JScrollPane();
        seasonsList = new javax.swing.JList();
        episodesPane = new javax.swing.JScrollPane();
        episodesList = new javax.swing.JList();
        episodeSynopsisPane = new javax.swing.JScrollPane();
        episodeSynopsisArea = new javax.swing.JTextArea();
        seasonLabel = new javax.swing.JLabel();
        episodeLabel = new javax.swing.JLabel();

        tvshowTb.setFloatable(false);
        tvshowTb.setRollover(true);

        titleLbl.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        tvshowTb.add(titleLbl);

        yearLbl.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        tvshowTb.add(yearLbl);

        synopsisArea.setColumns(20);
        synopsisArea.setRows(5);
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
        episodeSynopsisArea.setRows(5);
        episodeSynopsisPane.setViewportView(episodeSynopsisArea);

        seasonLabel.setText("Season");

        episodeLabel.setText("Episode");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(seasonsPane, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(seasonLabel))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(episodeLabel)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(episodesPane)))
                    .addComponent(episodeSynopsisPane, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)
                    .addComponent(synopsisPane)
                    .addComponent(tvshowTb, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tvshowTb, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(synopsisPane, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(episodeLabel)
                    .addComponent(seasonLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(seasonsPane, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(episodesPane, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addComponent(episodeSynopsisPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel episodeLabel;
    private javax.swing.JTextArea episodeSynopsisArea;
    private javax.swing.JScrollPane episodeSynopsisPane;
    private javax.swing.JList episodesList;
    private javax.swing.JScrollPane episodesPane;
    private javax.swing.JLabel seasonLabel;
    private javax.swing.JList seasonsList;
    private javax.swing.JScrollPane seasonsPane;
    private javax.swing.JTextArea synopsisArea;
    private javax.swing.JScrollPane synopsisPane;
    private com.alee.laf.label.WebLabel titleLbl;
    private com.alee.laf.toolbar.WebToolBar tvshowTb;
    private com.alee.laf.label.WebLabel yearLbl;
    // End of variables declaration//GEN-END:variables

  @Override
  public void setDisplay(Settings setting) {
    // TODO
  }
}
