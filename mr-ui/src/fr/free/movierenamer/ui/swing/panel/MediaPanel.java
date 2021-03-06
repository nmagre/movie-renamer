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

import com.alee.extended.breadcrumb.WebBreadcrumb;
import com.alee.extended.breadcrumb.WebBreadcrumbToggleButton;
import com.alee.extended.transition.ComponentTransition;
import com.alee.extended.transition.effects.fade.FadeTransitionEffect;
import com.alee.laf.button.WebButton;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.managers.language.data.TooltipWay;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.utils.SwingUtils;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.event.IEventInfo;
import fr.free.movierenamer.ui.event.UIEvent;
import fr.free.movierenamer.ui.event.UIEventInfo;
import fr.free.movierenamer.ui.bean.UIMediaInfo;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.swing.panel.generator.PanelGenerator;
import fr.free.movierenamer.ui.swing.panel.info.IMediaInfoPanel;
import fr.free.movierenamer.ui.swing.panel.info.InfoPanel;
import fr.free.movierenamer.ui.swing.panel.info.InfoPanel.PanelType;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.worker.impl.SearchMediaInfoWorker;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/**
 * Class MediaPanel
 *
 * @param <T>
 * @author Nicolas Magré
 */
public abstract class MediaPanel<T extends UIMediaInfo<M>, M extends MediaInfo> extends PanelGenerator implements IMediaInfoPanel<T, M> {

    private static final long serialVersionUID = 1L;

    protected T info;
    private ComponentTransition transitionPanel;
    private WebPanel mediaPanel;
    private WebToggleButton editButton;
    private WebBreadcrumb infoPanelBc;
    private WebButton refreshButton;
    private WebLabel loadingLbl;
    protected Map<PanelType, InfoPanel<T>> panels;

    protected MediaPanel() {
        super();
        UIEvent.addEventListener(this.getClass(), this);
    }

    protected void createPanel(Map<PanelType, InfoPanel<T>> panels) {
        createPanel(null, panels);
    }

    protected void createPanel(WebPanel mpanel, final Map<PanelType, InfoPanel<T>> panels) {

        mediaPanel = mpanel != null ? mpanel : this;

        this.panels = panels;
        int pos = 0;

        // Bread crumb buttons for navigate between panels
        infoPanelBc = new WebBreadcrumb();
        infoPanelBc.setFocusable(false);
        for (Entry<PanelType, InfoPanel<T>> entry : panels.entrySet()) {
            WebBreadcrumbToggleButton bcButton = createbuttonPanel(entry.getValue());
            if (pos == 0) {
                bcButton.setSelected(true);
            }
            infoPanelBc.add(bcButton);
        }

        SwingUtils.groupButtons(infoPanelBc);// Group breadcrumb button
        SwingUtils.setEnabledRecursively(infoPanelBc, false);

        boolean addEditButton = addEditButton();
        boolean addRefreshButton = addRefreshButton();

        int gwith = 2 + (addEditButton ? 1 : 0) + (addRefreshButton ? 1 : 0);

        mediaPanel.add(infoPanelBc, getGroupConstraint(0, false, false));

        loadingLbl = new WebLabel();
        mediaPanel.add(loadingLbl, getGroupConstraint(addRefreshButton ? 2 : 1, !addEditButton && !addRefreshButton, false));

        if (addRefreshButton) {
            refreshButton = new WebButton(ImageUtils.REFRESH_16);
            refreshButton.setRolloverShadeOnly(true);
            refreshButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    UIEvent.fireUIEvent(UIEvent.Event.REFRESH_MEDIAINFO, MovieRenamer.class);// Fire event on mr
                }
            });
            refreshButton.setEnabled(false);

            TooltipManager.setTooltip(refreshButton, new WebLabel("mediapanel.refresh", refreshButton.getIcon(), SwingConstants.TRAILING), TooltipWay.down);
            mediaPanel.add(refreshButton, getGroupConstraint(1, !addEditButton, false));
        }

        if (addEditButton) {
            editButton = new WebToggleButton(ImageUtils.EDIT_16);
            editButton.setRolloverDarkBorderOnly(true);
            editButton.setRolloverDecoratedOnly(true);
            editButton.setRolloverShadeOnly(true);
            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    UIEvent.fireUIEvent(UIEvent.Event.EDIT, InfoPanel.class, new UIEventInfo("", e));// Fire event on all info panel (wich are register to UI event)
                }
            });
            editButton.setEnabled(false);

            TooltipManager.setTooltip(editButton, new WebLabel("mediapanel.edit", editButton.getIcon(), SwingConstants.TRAILING), TooltipWay.down);
            mediaPanel.add(editButton, getGroupConstraint(addRefreshButton ? 3 : 2, true, false, true, 1));
        }

        transitionPanel = new ComponentTransition(panels.values().iterator().next());
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
        gbc.gridwidth = gwith;

        // Fixe random scroll when resize
        scrollpane.setPreferredSize(new Dimension(1, 1));

        mediaPanel.add(scrollpane, gbc);
    }

    public InfoPanel<T> getPanel(PanelType type) {
        return panels.get(type);
    }

    private WebBreadcrumbToggleButton createbuttonPanel(final InfoPanel<?> panel) {
        WebBreadcrumbToggleButton bcButton = new WebBreadcrumbToggleButton();
        bcButton.setIcon(panel.getIcon());
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
    public final void UIEventHandler(UIEvent.Event event, IEventInfo eventInfo, Object object, Object newObject) {
        UISettings.LOGGER.finer(String.format("%s receive event %s %s", getClass().getSimpleName(), event, (eventInfo != null ? eventInfo : "")));

        switch (event) {
            case WORKER_STARTED:
                if (eventInfo != null && eventInfo.getEventObject() instanceof SearchMediaInfoWorker) {
                    setLoading(true);
                }
                break;
            case WORKER_DONE:
                if (eventInfo != null && eventInfo.getEventObject() instanceof SearchMediaInfoWorker) {
                    setLoading(false);
                }
                break;
        }
    }

    public abstract void clearPanel();

    protected abstract boolean addEditButton();

    protected abstract boolean addRefreshButton();

    @Override
    public final void setInfo(T info) {
        this.info = info;

        addInfo(info);

        SwingUtils.setEnabledRecursively(infoPanelBc, true);
        if (addEditButton()) {
            editButton.setEnabled(true);
        }

        if (addRefreshButton()) {
            refreshButton.setEnabled(true);
        }
    }

    @Override
    public T getInfo() {
        return info;
    }

    protected abstract void addInfo(T info);

    @Override
    public void clear() {
        for (Entry<PanelType, InfoPanel<T>> entry : panels.entrySet()) {
            entry.getValue().clear();
        }

        SwingUtils.setEnabledRecursively(infoPanelBc, false);
        if (addEditButton()) {
            editButton.setEnabled(false);
        }
        clearPanel();
    }
}
