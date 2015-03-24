/*
 * Copyright (C) 2013-2014 Nicolas Magré
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
package fr.free.movierenamer.ui.swing.panel.info;

import fr.free.movierenamer.stream.AbstractStream;
import fr.free.movierenamer.stream.VideoDetective;
import fr.free.movierenamer.stream.Youtube;
import fr.free.movierenamer.ui.bean.UIMovieInfo;
import fr.free.movierenamer.ui.bean.UISearchTrailerResult;
import fr.free.movierenamer.ui.swing.ImageListModel;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.ui.worker.IWorker.WorkerId;
import fr.free.movierenamer.ui.worker.WorkerManager;
import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Class TrailerInfoPanel
 *
 * @author Nicolas Magré
 */
public class TrailerInfoPanel extends InfoPanel<UIMovieInfo> {

  private static final long serialVersionUID = 1L;

  private final ImageListModel<UISearchTrailerResult> listModel;
  private final List<UISearchTrailerResult> trailers;
  private static final Dimension imageSize = new Dimension(160, 120);

  /**
   * Creates new form TrailerPanel
   */
  @SuppressWarnings("unchecked")
  public TrailerInfoPanel() {
    trailers = new ArrayList<>();
    listModel = new ImageListModel<>();
    initComponents();
    trailerList.setModel(listModel);
    trailerList.setCellRenderer(UIUtils.trailerListRenderer);
    trailerList.setVisibleRowCount(0);
    trailerList.addListSelectionListener(createTrailerListListener());
  }

  public void addTrailers(List<UISearchTrailerResult> trailers) {// FIXME nimp a virer
    for (UISearchTrailerResult trailer : trailers) {
      listModel.add(trailer);
      this.trailers.add(trailer);
    }

    // Get images
    WorkerManager.fetchImages(WorkerId.IMAGE_INFO_TRAILER, listModel, imageSize, ImageUtils.UNKNOWN, true);

    if (!listModel.isEmpty()) {
      //trailerList.setSelectedIndex(0);
    }
  }

  private ListSelectionListener createTrailerListListener() {
    return new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent lse) {
        if (!lse.getValueIsAdjusting()) {
          UISearchTrailerResult ctrailer = (UISearchTrailerResult) trailerList.getSelectedValue();
          String str = "";
          if (ctrailer.getProvider().equals("Youtube")) {
            Youtube yt = new Youtube();
            try {
              Map<AbstractStream.Quality, URL> links = yt.getLinks(ctrailer.getTrailerUrl());

              for (Entry<AbstractStream.Quality, URL> entry : links.entrySet()) {
                str += entry.getKey() + " : " + entry.getValue();
              }
            } catch (MalformedURLException ex) {
              Logger.getLogger(TrailerInfoPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
              Logger.getLogger(TrailerInfoPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
          } else {
            VideoDetective vd = new VideoDetective();
            try {
              Map<AbstractStream.Quality, URL> links = vd.getLinks(ctrailer.getTrailerUrl());

              for (Entry<AbstractStream.Quality, URL> entry : links.entrySet()) {
                str += entry.getKey() + " : " + entry.getValue();
              }
            } catch (MalformedURLException ex) {
              Logger.getLogger(TrailerInfoPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
              Logger.getLogger(TrailerInfoPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        }
      }
    };
  }

  @Override
  public Icon getIcon() {
    return ImageUtils.TRAILER_16;
  }

  @Override
  public String getPanelName() {
    return "Trailer";// FIXME i18n
  }

  @Override
  public void clear() {
    listModel.clear();
    trailers.clear();
  }

  @Override
  public void setInfo(UIMovieInfo info) {// TODO

  }

  @Override
  public PanelType getType() {
    return PanelType.TRAILER_INFO;
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jScrollPane1 = new javax.swing.JScrollPane();
    trailerList = new com.alee.laf.list.WebList();
    searchBtn = new com.alee.laf.button.WebButton();

    trailerList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    trailerList.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
    trailerList.setVisibleRowCount(2);
    jScrollPane1.setViewportView(trailerList);

    searchBtn.setText("webButton1");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(searchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, Short.MAX_VALUE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(searchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JScrollPane jScrollPane1;
  private com.alee.laf.button.WebButton searchBtn;
  private com.alee.laf.list.WebList trailerList;
  // End of variables declaration//GEN-END:variables

}
