/*
 * Movie Renamer
 * Copyright (C) 2012-2013 Nicolas Magré
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
package fr.free.movierenamer.ui.swing.panel;

import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.ui.bean.UIMediaInfo;
import fr.free.movierenamer.ui.swing.panel.info.InfoPanel;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class VideoPanel
 *
 * @author Nicolas Magré
 */
public abstract class VideoPanel<T extends UIMediaInfo<M>, M extends MediaInfo> extends MediaPanel<T, M> {

  private final int nbStar = 5;
  private final WebPanel starPanel;
  private final List<WebLabel> stars;

  /**
   * Creates new form VideoPanel
   *
   * @param panels
   */
  @SafeVarargs
  protected VideoPanel(InfoPanel<T>... panels) {
    super();
    initComponents();

    starPanel = new WebPanel();
    starPanel.setMargin(0);
    starPanel.setLayout(new FlowLayout());
    stars = new ArrayList<>();
    for (int i = 0; i < nbStar; i++) {
      WebLabel label = new WebLabel();
      stars.add(label);
      starPanel.add(stars.get(i));
    }
    mainTb.addToEnd(starPanel);

    Map<InfoPanel.PanelType, InfoPanel<T>> mediaPanels = new LinkedHashMap<>();
    for (InfoPanel<T> panel : panels) {
      mediaPanels.put(panel.getType(), panel);
    }
    createPanel(infoPanels, mediaPanels);

    clearStars();
    mainTb.setMargin(0, 4, 0, 0);
    setMargin(4, 5, 4, 5);

    mediaTitleLbl.setFont(UIUtils.titleFont);
  }

  @Override
  protected void setLoading(boolean loading) {
    super.setLoading(loading);
    mediaTitleLbl.setIcon(loading ? ImageUtils.LOAD_16 : null);
  }

  @Override
  public final void addInfo(T info) {
    for (InfoPanel<T> panel : panels.values()) {
      panel.setInfo(info);
    }

    mediaTitleLbl.setText(getTitle(info));
    setRate(getRate(info));
  }

  protected abstract String getTitle(T info);

  protected abstract Double getRate(T info);

  private void setRate(Double rate) {
    Double value = rate;
    if (value == null || value < 0.00) {
      return;
    }

    value /= (10 / nbStar);

    int n = value.intValue();
    for (int i = 0; i < n; i++) {
      stars.get(i).setIcon(ImageUtils.STAR_16);
    }

    if ((rate - rate.intValue()) >= 0.50 && n < nbStar) {
      stars.get(n).setIcon(ImageUtils.STARHALF_16);
    }
  }

  private void clearStars() {
    for (int i = 0; i < nbStar; i++) {
      stars.get(i).setIcon(ImageUtils.STAREMPTY_16);
    }
  }

  @Override
  public final void clear() {
    super.clear();
    clearStars();
    mediaTitleLbl.setText("");
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    mainTb = new com.alee.laf.toolbar.WebToolBar();
    mediaTitleLbl = new com.alee.laf.label.WebLabel();
    infoPanels = new com.alee.laf.panel.WebPanel();

    setAutoscrolls(true);
    setLayout(new java.awt.BorderLayout());

    mainTb.setFloatable(false);
    mainTb.setRollover(true);

    mediaTitleLbl.setMargin(new java.awt.Insets(0, 0, 0, 10));
    mainTb.add(mediaTitleLbl);

    add(mainTb, java.awt.BorderLayout.PAGE_START);

    infoPanels.setAutoscrolls(true);
    infoPanels.setLayout(new java.awt.GridBagLayout());
    add(infoPanels, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private com.alee.laf.panel.WebPanel infoPanels;
  private com.alee.laf.toolbar.WebToolBar mainTb;
  private com.alee.laf.label.WebLabel mediaTitleLbl;
  // End of variables declaration//GEN-END:variables
}
