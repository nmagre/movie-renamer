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

import com.alee.extended.breadcrumb.WebBreadcrumb;
import com.alee.extended.breadcrumb.WebBreadcrumbToggleButton;
import com.alee.extended.transition.ComponentTransition;
import com.alee.extended.transition.effects.fade.FadeTransitionEffect;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;
import com.alee.utils.SwingUtils;
import fr.free.movierenamer.info.FileInfo;
import fr.free.movierenamer.info.Info;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.ui.bean.IEventInfo;
import fr.free.movierenamer.ui.bean.UIEvent;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.swing.panel.generator.PanelGenerator;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.worker.impl.SearchMediaInfoWorker;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/**
 * Class MediaPanel
 *
 * @param <T>
 * @author Nicolas Magré
 */
public abstract class MediaPanel<T extends MediaInfo> extends PanelGenerator implements IInfoPanel<T> {

  private ComponentTransition transitionPanel;
  private WebPanel mediaPanel;
  private final FileInfoPanel fileInfoPanel;
  private WebToggleButton editButton;
  private WebBreadcrumb infoPanelBc;
  private WebLabel loadingLbl;
  protected List<InfoPanel<T>> panels;

  protected MediaPanel() {
    super();
    fileInfoPanel = new FileInfoPanel();
    UIEvent.addEventListener(this.getClass(), this);
  }

  protected void createPanel(List<InfoPanel<T>> panels) {
    createPanel(null, panels);
  }

  protected void createPanel(WebPanel mpanel, final List<InfoPanel<T>> panels) {

    mediaPanel = mpanel != null ? mpanel : this;

    this.panels = panels;
    int pos = 0;
    infoPanelBc = new WebBreadcrumb();
    infoPanelBc.setFocusable(false);
    for (InfoPanel<T> panel : panels) {
      WebBreadcrumbToggleButton bcButton = createbuttonPanel(panel);
      if (pos == 0) {
        bcButton.setSelected(true);
      }
      infoPanelBc.add(bcButton);
    }

    infoPanelBc.add(createbuttonPanel(fileInfoPanel));

    SwingUtils.groupButtons(infoPanelBc);// Group breadcrumb button
    SwingUtils.setEnabledRecursively(infoPanelBc, false);

    boolean addEditButton = addEditButton();

    mediaPanel.add(infoPanelBc, getGroupConstraint(0, false, false));

    loadingLbl = new WebLabel();
    mediaPanel.add(loadingLbl, getGroupConstraint(1, !addEditButton, false));

    if (addEditButton) {
      editButton = new WebToggleButton(ImageUtils.EDIT_16);
      editButton.setRolloverDarkBorderOnly(true);
      editButton.setRolloverDecoratedOnly(true);
      editButton.setRolloverShadeOnly(true);
      editButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          UIEvent.fireUIEvent(UIEvent.Event.EDIT, InfoPanel.class);// Fire event on all info panel (wich are register to UI event)
        }
      });
      editButton.setEnabled(true);// FIXME false;

      TooltipManager.setTooltip(editButton, new WebLabel("mediapanel.edit", editButton.getIcon(), SwingConstants.TRAILING), TooltipWay.down);
      mediaPanel.add(editButton, getGroupConstraint(2, true, false, true, 1));
    }

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
    gbc.gridwidth = addEditButton ? 3 : 1;

    // Fixe random scroll when resize
    scrollpane.setPreferredSize(new Dimension(1, 1));

    mediaPanel.add(scrollpane, gbc);
  }

  private WebBreadcrumbToggleButton createbuttonPanel(final InfoPanel<? extends Info> panel) {
    WebBreadcrumbToggleButton bcButton = new WebBreadcrumbToggleButton();
    bcButton.setIcon(panel.getIcon());
    //bcButton.setText(panel.getPanelName());
    bcButton.setFocusable(false);
    bcButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        transitionPanel.performTransition(panel);
      }
    });
    TooltipManager.setTooltip(bcButton, new WebLabel(panel.getPanelName(), panel.getIcon(), SwingConstants.TRAILING), TooltipWay.down);
    return bcButton;
  }

  protected void setLoading(boolean loading) {
    loadingLbl.setIcon(loading ? ImageUtils.LOAD_16 : null);
  }

  @Override
  public final void UIEventHandler(UIEvent.Event event, IEventInfo info, Object param) {
    UISettings.LOGGER.finer(String.format("%s receive event %s %s", getClass().getSimpleName(), event, (info != null ? info : "")));

    switch (event) {
      case WORKER_STARTED:
        if (info.getClass().equals(SearchMediaInfoWorker.class)) {
          setLoading(true);
        }
        break;
      case WORKER_DONE:
        if (info.getClass().equals(SearchMediaInfoWorker.class)) {
          setLoading(false);
        }
        break;
    }
  }

  public abstract void clearPanel();

  protected abstract boolean addEditButton();

  public void setFileInfo(FileInfo info) {
    fileInfoPanel.setInfo(info);
  }

  public void clearfileInfoPanel() {
    fileInfoPanel.clear();
  }

  public FileInfo getFileInfo() {
    return fileInfoPanel.getInfo();
  }

  @Override
  public void setInfo(T info) {
    addInfo(info);

    SwingUtils.setEnabledRecursively(infoPanelBc, true);
    if (addEditButton()) {
      editButton.setEnabled(true);
    }
  }

  protected abstract void addInfo(T info);
  
  @Override
  public void clear() {
    for (InfoPanel<? extends Info> panel : panels) {
      panel.clear();
    }

    SwingUtils.setEnabledRecursively(infoPanelBc, false);
    if (addEditButton()) {
      editButton.setEnabled(false);
    }
    clearPanel();
  }
}
