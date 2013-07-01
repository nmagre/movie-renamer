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
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.DefaultListModel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.toolbar.WebToolBar;
import com.alee.managers.popup.PopupWay;
import com.alee.utils.SwingUtils;
import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.FileInfo;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.MovieInfo.MovieProperty;
import fr.free.movierenamer.mediainfo.MediaAudio;
import fr.free.movierenamer.mediainfo.MediaSubTitle;
import fr.free.movierenamer.mediainfo.MediaVideo;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.IImage;
import fr.free.movierenamer.ui.bean.UIMediaAudio;
import fr.free.movierenamer.ui.bean.UIMediaSubTitle;
import fr.free.movierenamer.ui.bean.UIPersonImage;
import fr.free.movierenamer.ui.res.Flag;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.swing.ImageListModel;
import fr.free.movierenamer.ui.swing.panel.info.FileInfoPanel;
import fr.free.movierenamer.ui.utils.UIUtils;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * Class MoviePanel
 *
 * @author Magré Nicolas
 */
public class MoviePanel extends AbstractMediaPanel {

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private ComponentTransition componentTransition1;
  private WebBreadcrumbToggleButton fileBct;
  private WebBreadcrumb infoBc;
  private WebBreadcrumbToggleButton movieBct;
  private WebLabel titleLbl;
  private WebPanel webPanel3;
  private WebToolBar webToolBar1;
  // End of variables declaration//GEN-END:variables
  private static final long serialVersionUID = 1L;
  private final UISettings setting;
  private MediaInfo mediaInfo;
  private final DefaultListModel countryListModel = new DefaultListModel();
  private final ImageListModel<UIPersonImage> actorListModel = new ImageListModel<UIPersonImage>();
  private FileInfoPanel fileInfoPnl;

  /**
   * Creates new form MoviePanel
   *
   * @param mr
   */
  public MoviePanel(MovieRenamer mr) {
    super(mr);
    this.setting = UISettings.getInstance();

    initComponents();
    SwingUtils.groupButtons(infoBc);

    movieBct.setSelected(true);

    componentTransition1.setTransitionEffect(new FadeTransitionEffect());

    webToolBar1.addToEnd(getStarPanel());
    webToolBar1.addToEnd(UIUtils.createSettingButton(PopupWay.downLeft, new WebCheckBox()));
  }

  @Override
  public void clear() {// CHECK if SwingUtilities is needed
    SwingUtilities.invokeLater(new Thread() {
      @Override
      public void run() {
        mediaInfo = null;
        clearStars();
        countryListModel.clear();
        titleLbl.setText("");

//        runtimeField.setText("");
//        ratingField.setText("");
//        voteField.setText("");
        actorListModel.clear();
      }
    });
  }

  @Override
  public void setMediaInfo(MediaInfo mediaInfo) {
    this.mediaInfo = mediaInfo;
    MovieInfo movieInfo = (MovieInfo) mediaInfo;

    titleLbl.setText(movieInfo.getTitle() + " (" + movieInfo.getYear() + ")");
    setRate(movieInfo.getRating());
    for (Locale locale : movieInfo.getCountries()) {
      countryListModel.addElement(new UICountry(locale));
    }

    for (CastingInfo info : movieInfo.getCast()) {
      actorListModel.add(new UIPersonImage(info));
    }

    StringBuilder strb = new StringBuilder();
    strb.append("<html><head><style type='text/css'>");
    strb.append("html { background: linear-gradient(to bottom, #373737 1%, #202020 100%) repeat scroll 0 0 transparent;}");
    strb.append("</head>");
    for (MovieProperty property : MovieProperty.values()) {
      strb.append("<b>").append(property.name()).append("</b>").append("      <input type='text' value ='" + movieInfo.get(property) + "'><br>");
    }
    strb.append("</html>");

//    runtimeField.setText("" + movieInfo.getRuntime());
//    ratingField.setText("" + movieInfo.getRating());
//    voteField.setText("" + movieInfo.getVotes());

  }

  public void clearMediaTag() {
    fileInfoPnl.clear();
  }

  @Override
  public MediaInfo getMediaInfo() {
    return mediaInfo;
  }

  @Override
  public WebList getCastingList() {
    return null;
  }

  /**
   * This method is called from within the constructor to initialize the form.
   */
  //WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    webToolBar1 = new WebToolBar();
    titleLbl = new WebLabel();
    webPanel3 = new WebPanel();
    componentTransition1 = new ComponentTransition();
    infoBc = new WebBreadcrumb();
    movieBct = new WebBreadcrumbToggleButton();
    fileBct = new WebBreadcrumbToggleButton();

    setMinimumSize(new Dimension(400, 400));
    setPreferredSize(new Dimension(400, 400));

    webToolBar1.setFloatable(false);
    webToolBar1.setRollover(true);

    titleLbl.setFont(new Font("Ubuntu", 1, 14)); // NOI18N
    titleLbl.setMargin(new Insets(0, 0, 0, 10));
    webToolBar1.add(titleLbl);

    GroupLayout componentTransition1Layout = new GroupLayout(componentTransition1);
    componentTransition1.setLayout(componentTransition1Layout);
    componentTransition1Layout.setHorizontalGroup(
      componentTransition1Layout.createParallelGroup(Alignment.LEADING)
      .addGap(0, 352, Short.MAX_VALUE)
    );
    componentTransition1Layout.setVerticalGroup(
      componentTransition1Layout.createParallelGroup(Alignment.LEADING)
      .addGap(0, 209, Short.MAX_VALUE)
    );

    infoBc.setFocusable(false);

    movieBct.setIcon(new ImageIcon(getClass().getResource("/image/ui/16/movie.png"))); // NOI18N
    movieBct.setText("Movie");
    movieBct.setFocusable(false);
    movieBct.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        movieBctActionPerformed(evt);
      }
    });
    infoBc.add(movieBct);

    fileBct.setIcon(new ImageIcon(getClass().getResource("/image/ui/16/info.png"))); // NOI18N
    fileBct.setText("File");
    fileBct.setFocusable(false);
    fileBct.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        fileBctActionPerformed(evt);
      }
    });
    infoBc.add(fileBct);

    GroupLayout webPanel3Layout = new GroupLayout(webPanel3);
    webPanel3.setLayout(webPanel3Layout);
    webPanel3Layout.setHorizontalGroup(
      webPanel3Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(webPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(webPanel3Layout.createParallelGroup(Alignment.LEADING)
          .addGroup(webPanel3Layout.createSequentialGroup()
            .addComponent(infoBc, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGap(0, 212, Short.MAX_VALUE))
          .addComponent(componentTransition1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    webPanel3Layout.setVerticalGroup(
      webPanel3Layout.createParallelGroup(Alignment.LEADING)
      .addGroup(webPanel3Layout.createSequentialGroup()
        .addComponent(infoBc, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(componentTransition1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addContainerGap())
    );

    GroupLayout layout = new GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(Alignment.LEADING)
      .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(Alignment.TRAILING)
          .addComponent(webToolBar1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(webPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(Alignment.LEADING)
      .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(webToolBar1, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(ComponentPlacement.RELATED)
        .addComponent(webPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGap(101, 101, 101))
    );
  }// </editor-fold>//GEN-END:initComponents

  private void movieBctActionPerformed(ActionEvent evt) {//GEN-FIRST:event_movieBctActionPerformed

  }//GEN-LAST:event_movieBctActionPerformed

  private void fileBctActionPerformed(ActionEvent evt) {//GEN-FIRST:event_fileBctActionPerformed
    componentTransition1.performTransition(fileInfoPnl);
  }//GEN-LAST:event_fileBctActionPerformed

  @Override
  protected String getPanelName() {
    return "Movie Panel";
  }

  @Override
  public ImageListModel<UIPersonImage> getCastingModel() {
    return actorListModel;
  }

  private static class UICastingInfo implements IImage {

    private UIPersonImage info;

    public UICastingInfo(UIPersonImage info) {
      this.info = info;
    }

    @Override
    public Icon getIcon() {
      return info.getIcon();
    }

    @Override
    public void setIcon(Icon icon) {
      info.setIcon(icon);
    }

    @Override
    public String toString() {
      return info.getName();
    }

    @Override
    public URI getUri(ImageInfo.ImageSize size) {
      return info.getUri(size);
    }

    @Override
    public int getId() {
      return info.getId();
    }
  }

  public class UICountry implements IImage {

    private Locale country;
    private Icon icon;

    public UICountry(Locale country) {
      this.country = country;
      icon = Flag.getFlag(country.getCountry()).getIcon();
    }

    @Override
    public Icon getIcon() {
      return icon;
    }

    @Override
    public void setIcon(Icon icon) {
      this.icon = icon;
    }

    @Override
    public String toString() {
      return country.getDisplayCountry(setting.coreInstance.getAppLanguage().getLocale());
    }

    @Override
    public URI getUri(ImageInfo.ImageSize size) {
      return null;
    }

    @Override
    public int getId() {
      throw new UnsupportedOperationException("Not supported yet.");
    }
  }
}
