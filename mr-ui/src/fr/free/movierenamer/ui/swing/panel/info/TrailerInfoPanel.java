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
import fr.free.movierenamer.ui.bean.UITrailer;
import fr.free.movierenamer.ui.swing.ImageListModel;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
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
public class TrailerInfoPanel extends InfoPanel<UITrailer> {

  private final ImageListModel<UITrailer> listModel;
  private final List<UITrailer> trailers;
  private static final Dimension imageSize = new Dimension(160, 120);

  /**
   * Creates new form TrailerPanel
   */
  public TrailerInfoPanel() {
    trailers = new ArrayList<>();
    listModel = new ImageListModel();
    initComponents();
    trailerList.setModel(listModel);
    trailerList.setCellRenderer(UIUtils.trailerListRenderer);
    trailerList.setVisibleRowCount(0);
    trailerList.addListSelectionListener(createTrailerListListener());
  }

  public void addTrailers(List<UITrailer> trailers) {// FIXME nimp a virer
    for (UITrailer trailer : trailers) {
      listModel.add(trailer);
      this.trailers.add(trailer);
    }

    // Avoid reference
    List<UITrailer> uitrailers = new ArrayList<>(this.trailers);
    // Get images
    WorkerManager.fetchImages(uitrailers, listModel, imageSize, ImageUtils.UNKNOWN, true);

    if (!listModel.isEmpty()) {
      trailerList.setSelectedIndex(0);
    }
  }

  private ListSelectionListener createTrailerListListener() {
    return new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent lse) {
        if (!lse.getValueIsAdjusting()) {
          UITrailer ctrailer = (UITrailer) trailerList.getSelectedValue();
          String str = "";
          if (ctrailer.getProviderName().equals("Youtube")) {
            Youtube yt = new Youtube();
            try {
              Map<AbstractStream.Quality, URL> links = yt.getLinks(ctrailer.getUrl().toURL());

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
              Map<AbstractStream.Quality, URL> links = vd.getLinks(ctrailer.getUrl().toURL());

              for (Entry<AbstractStream.Quality, URL> entry : links.entrySet()) {
                str += entry.getKey() + " : " + entry.getValue();
              }
            } catch (MalformedURLException ex) {
              Logger.getLogger(TrailerInfoPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
              Logger.getLogger(TrailerInfoPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
          jTextArea1.setText(str);
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
    jTextArea1.setText("");
  }

  @Override
  public void setInfo(UITrailer info) {// TODO

  }

  @Override
  public UITrailer getInfo() {
    return null;
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
    jScrollPane2 = new javax.swing.JScrollPane();
    jTextArea1 = new javax.swing.JTextArea();

    trailerList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    trailerList.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
    trailerList.setVisibleRowCount(2);
    jScrollPane1.setViewportView(trailerList);

    jTextArea1.setColumns(20);
    jTextArea1.setRows(5);
    jScrollPane2.setViewportView(jTextArea1);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
        .addGap(18, 18, 18)
        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap(87, Short.MAX_VALUE)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
          .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
          .addComponent(jScrollPane1))
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JTextArea jTextArea1;
  private com.alee.laf.list.WebList trailerList;
  // End of variables declaration//GEN-END:variables

}
