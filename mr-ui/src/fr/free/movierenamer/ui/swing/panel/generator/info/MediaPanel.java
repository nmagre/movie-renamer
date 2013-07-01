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
package fr.free.movierenamer.ui.swing.panel.generator.info;

import fr.free.movierenamer.ui.swing.panel.generator.InfoPanel;
import fr.free.movierenamer.ui.swing.panel.generator.IInfoPanel;
import com.alee.extended.breadcrumb.WebBreadcrumb;
import com.alee.extended.breadcrumb.WebBreadcrumbToggleButton;
import com.alee.extended.transition.ComponentTransition;
import com.alee.extended.transition.effects.fade.FadeTransitionEffect;
import com.alee.laf.panel.WebPanel;
import com.alee.utils.SwingUtils;
import fr.free.movierenamer.info.FileInfo;
import fr.free.movierenamer.info.Info;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.ui.swing.panel.generator.PanelGenerator;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JScrollPane;

/**
 * Class MediaPanel
 *
 * @param <T>
 * @author Nicolas Magré
 */
public abstract class MediaPanel<T extends MediaInfo> extends PanelGenerator implements IInfoPanel<T> {

  private List<InfoPanel<T>> panels;
  private WebBreadcrumb infoPanelBc;
  private ComponentTransition transitionPanel;
  private WebPanel mediaPanel;
  private final FileInfoPanel fileInfoPanel;
  private T info;

  protected MediaPanel() {
    super();
    fileInfoPanel = new FileInfoPanel();
  }

  protected void createPanel(List<InfoPanel<T>> panels) {
    createPanel(null, panels);
  }

  protected void createPanel(WebPanel mpanel, List<InfoPanel<T>> panels) {

    mediaPanel = mpanel != null ? mpanel : this;

    this.panels = panels;
    int pos = 0;
    infoPanelBc = new WebBreadcrumb();
    infoPanelBc.setFocusable(false);
    for (InfoPanel<? extends Info> panel : panels) {
      WebBreadcrumbToggleButton bcButton = createbuttonPanel(panel);
      if (pos == 0) {
        bcButton.setSelected(true);
      }
      infoPanelBc.add(bcButton);
    }

    infoPanelBc.add(createbuttonPanel(fileInfoPanel));
    SwingUtils.groupButtons(infoPanelBc);// Group breadcrumb button

    mediaPanel.add(infoPanelBc, getGroupConstraint(0, true, false));

    transitionPanel = new ComponentTransition(panels.get(0));
    transitionPanel.setTransitionEffect(new FadeTransitionEffect());

    JScrollPane scrollpane = new JScrollPane(transitionPanel);
    scrollpane.setBorder(null);
    scrollpane.setAutoscrolls(true);
    GridBagConstraints gbc = getGroupConstraint();
    gbc.anchor = GridBagConstraints.PAGE_START;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.gridheight = 1;
    gbc.gridwidth = 1;

    // Fixed random scroll when resize
    scrollpane.setPreferredSize(new Dimension(1, 1));

    mediaPanel.add(scrollpane, gbc);
  }

  private WebBreadcrumbToggleButton createbuttonPanel(final InfoPanel<? extends Info> panel) {
    WebBreadcrumbToggleButton bcButton = new WebBreadcrumbToggleButton();
    bcButton.setIcon(panel.getIcon());
    bcButton.setText(panel.getPanelName());
    bcButton.setFocusable(false);
    bcButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        transitionPanel.performTransition(panel);
      }
    });
    return bcButton;
  }

  @Override
  public T getInfo() {
    return info;
  }

  @Override
  public void setInfo(T info) {
    this.info = info;
    for (InfoPanel<T> panel : panels) {
      panel.setInfo(info);
    }
  }

  public abstract void clearPanel();

  public void setFileInfo(FileInfo info) {
    clearfileInfoPanel();
    fileInfoPanel.setInfo(info);
  }

  public void clearfileInfoPanel() {
    fileInfoPanel.clear();
  }
  
  public FileInfo getFileInfo() {
    return fileInfoPanel.getInfo();
  }

  @Override
  public void clear() {
    info = null;
    for (InfoPanel<T> panel : panels) {
      panel.clear();
    }
    clearPanel();
  }
}
