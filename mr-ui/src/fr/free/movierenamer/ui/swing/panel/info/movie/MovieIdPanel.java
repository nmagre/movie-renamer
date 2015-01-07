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

import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.text.WebTextField;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIEditor;
import fr.free.movierenamer.ui.bean.UIMovieInfo;
import fr.free.movierenamer.ui.bean.UISearchResult;
import fr.free.movierenamer.ui.swing.panel.info.InfoEditorPanel;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.worker.WorkerManager;
import fr.free.movierenamer.utils.ScrapperUtils.AvailableApiIds;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Nicolas Magré
 */
public class MovieIdPanel extends InfoEditorPanel<UIMovieInfo> {

  private UIMovieInfo movieInfo;
  private WebButton searchBtn;

  public MovieIdPanel(MovieRenamer mr) {
    super(mr);
    initComponents();

    int maxGridWith = 4;// 4 -> Label + link label + field + edit/cancel button
    WebButton button;
    Icon icon;

    for (final AvailableApiIds apiId : AvailableApiIds.getAvailableApiIds(Media.MediaType.MOVIE)) {
      icon = new ImageIcon(ImageUtils.getImageFromJAR(String.format("scrapper/%s.png", apiId.name().toLowerCase())));
      button = new WebButton(icon);

      if (apiId.equals(AvailableApiIds.ROTTENTOMATOES)) {// Rotten tomatoes id cannot reference movie web page
        button.setVisible(false);
      }

      final UIEditor editor = new UIEditor(mr, null, new WebTextField(), button);
      button.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent ae) {

          if (apiId.equals(AvailableApiIds.ROTTENTOMATOES)) {
            return;
          }

          try {
            Desktop.getDesktop().browse(new URL("http", String.format(apiId.getLink(), ((JTextComponent) editor.getEditableComponent()).getText()), "").toURI());
          } catch (Exception ex) {
            // TODO error
          }
        }
      });

      createEditableField(apiId.name().toLowerCase(), editor, maxGridWith);
      map.put(apiId, editor);
    }

    searchBtn = (WebButton) createComponent(Component.BUTTON, "main.searchtb.search");
    searchBtn.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent ae) {
        WorkerManager.searchIds(MovieIdPanel.this, movieInfo, (UISearchResult) MovieIdPanel.this.mr.getSearchResultList().getSelectedValue(), Media.MediaType.MOVIE);
        searchBtn.setEnabled(false);
      }
    });
    add(searchBtn, getGroupConstraint(0, true, false));

    // Add dummy Panel to avoid centering
    add(new WebPanel(), getDummyPanelConstraint());
  }

  @Override
  public Icon getIcon() {
    return ImageUtils.ID_16;
  }

  @Override
  public String getPanelName() {
    return "id";
  }

  @Override
  public PanelType getType() {
    return PanelType.ID_INFO;
  }

  @Override
  public void setInfo(UIMovieInfo movieInfo) {
    this.movieInfo = movieInfo;

    UIEditor editor;
    for (IdInfo idinfo : movieInfo.getIds()) {
      editor = map.get(idinfo.getIdType());
      if (editor != null) {
        editor.setValue(idinfo.toString());
      }
    }
  }
  
  public void setSearchButton(boolean enabled) {
    searchBtn.setEnabled(enabled);
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    setLayout(new java.awt.GridBagLayout());
  }// </editor-fold>//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  // End of variables declaration//GEN-END:variables
}
