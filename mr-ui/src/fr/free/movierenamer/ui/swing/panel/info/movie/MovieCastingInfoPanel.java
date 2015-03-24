/*
 * Movie Renamer
 * Copyright (C) 2014 Nicolas Magré
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
package fr.free.movierenamer.ui.swing.panel.info.movie;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.settings.XMLSettings;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.IEventInfo;
import fr.free.movierenamer.ui.bean.UIEvent;
import fr.free.movierenamer.ui.bean.UIMovieInfo;
import fr.free.movierenamer.ui.bean.UIPersonImage;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.swing.ImageListModel;
import fr.free.movierenamer.ui.swing.contextmenu.ContextMenuImage;
import fr.free.movierenamer.ui.swing.panel.info.InfoEditorPanel;
import fr.free.movierenamer.ui.swing.renderer.CastingListRenderer;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import static fr.free.movierenamer.ui.utils.UIUtils.i18n;
import fr.free.movierenamer.ui.worker.IWorker.WorkerId;
import fr.free.movierenamer.ui.worker.WorkerManager;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;

/**
 * Class MovieCastingInfoPanel
 *
 * @author Nicolas Magré
 */
public class MovieCastingInfoPanel extends InfoEditorPanel<UIMovieInfo> {

  private static final long serialVersionUID = 1L;

  private List<UIPersonImage> casts;
  private final List<UIPersonImage> actorsList;
  private final List<UIPersonImage> directorsList;
  private final ImageListModel<UIPersonImage> directorListModel = new ImageListModel<>();
  private final ImageListModel<UIPersonImage> actorListModel = new ImageListModel<>();

  @SuppressWarnings("unchecked")
  public MovieCastingInfoPanel(MovieRenamer mr) {
    super(mr);
    initComponents();
    actorsList = new ArrayList<>();
    directorsList = new ArrayList<>();

    casts = null;
    directorList.setModel(directorListModel);
    actorList.setModel(actorListModel);
    directorList.setCellRenderer(new CastingListRenderer());
    actorList.setCellRenderer(new CastingListRenderer());
    directorList.setVisibleRowCount(0);
    actorList.setVisibleRowCount(0);

    actorList.addMouseListener(new ContextMenuImage());
  }

  @Override
  public Icon getIcon() {
    return ImageUtils.CASTING_16;
  }

  @Override
  public String getPanelName() {
    return UIUtils.i18n.getLanguage("main.castingpnl.actor", false);
  }

  @Override
  public void clear() {
    casts = null;
    directorListModel.clear();
    actorListModel.clear();
    actorsList.clear();
    directorsList.clear();
  }

  @Override
  public void setInfo(UIMovieInfo movieInfo) {
    this.casts = movieInfo.getCasting();

    for (UIPersonImage cast : casts) {
      switch (cast.getJob()) {
        case CastingInfo.ACTOR:
          actorsList.add(cast);
          break;
        case CastingInfo.DIRECTOR:
        case CastingInfo.WRITER:
          directorsList.add(cast);
          break;
      }
    }

    actorListModel.addAll(actorsList);
    directorListModel.addAll(directorsList);

    setImage();
  }

  @Override
  public void UIEventHandler(UIEvent.Event event, IEventInfo info, Object oldObj, Object newObj) {

    super.UIEventHandler(event, info, oldObj, newObj);

    switch (event) {
      case SETTINGS:
        if (newObj instanceof XMLSettings.IProperty) {
          if (newObj instanceof UISettings.UISettingsProperty) {
            UISettings.UISettingsProperty uisproperty = (UISettings.UISettingsProperty) newObj;
            switch (uisproperty) {
              case showActorImage:
                if (casts != null) {
                  // Stop image worker
                  WorkerManager.stop(WorkerId.IMAGE_INFO_ACTOR);
                  WorkerManager.stop(WorkerId.IMAGE_INFO_DIRECTOR);

                  // Re-add loading animation
                  for (int i = 0; i < actorListModel.getSize(); i++) {
                    actorListModel.getElementAt(i).setDefaultIcon();
                  }

                  for (int i = 0; i < directorListModel.getSize(); i++) {
                    directorListModel.getElementAt(i).setDefaultIcon();
                  }

                  setImage();
                }
            }
          }
        }
        break;
    }
  }

  public void setImage() {
    // Get images
    WorkerManager.fetchImages(WorkerId.IMAGE_INFO_ACTOR, actorListModel, UIUtils.listImageSize, ImageUtils.UNKNOWN, UISettings.getInstance().isShowActorImage());
    WorkerManager.fetchImages(WorkerId.IMAGE_INFO_DIRECTOR, directorListModel, UIUtils.listImageSize, ImageUtils.UNKNOWN, UISettings.getInstance().isShowActorImage());
  }

  @Override
  public PanelType getType() {
    return PanelType.CASTING_INFO;
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    directorTb = new com.alee.laf.toolbar.WebToolBar();
    directorLbl = new com.alee.laf.label.WebLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    directorList = new com.alee.laf.list.WebList();
    actorTb = new com.alee.laf.toolbar.WebToolBar();
    actorLbl = new com.alee.laf.label.WebLabel();
    jScrollPane2 = new javax.swing.JScrollPane();
    actorList = new com.alee.laf.list.WebList();

    directorTb.setFloatable(false);
    directorTb.setRollover(true);
    directorTb.setMargin(new java.awt.Insets(0, 4, 0, 4));

    directorLbl.setLanguage(i18n.getLanguageKey("castingpnl.director"));
    directorLbl.setIcon(ImageUtils.USER_16);
    directorTb.add(directorLbl);

    directorList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    directorList.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
    jScrollPane1.setViewportView(directorList);

    actorTb.setFloatable(false);
    actorTb.setRollover(true);
    actorTb.setMargin(new java.awt.Insets(0, 4, 0, 4));

    actorLbl.setLanguage(UIUtils.i18n.getLanguageKey("castingpnl.actor"));
    actorLbl.setIcon(ImageUtils.USER_16);
    actorTb.add(actorLbl);

    actorList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    actorList.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
    jScrollPane2.setViewportView(actorList);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(directorTb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(actorTb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
          .addComponent(jScrollPane2))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(directorTb, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(actorTb, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private com.alee.laf.label.WebLabel actorLbl;
  private com.alee.laf.list.WebList actorList;
  private com.alee.laf.toolbar.WebToolBar actorTb;
  private com.alee.laf.label.WebLabel directorLbl;
  private com.alee.laf.list.WebList directorList;
  private com.alee.laf.toolbar.WebToolBar directorTb;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  // End of variables declaration//GEN-END:variables

}
