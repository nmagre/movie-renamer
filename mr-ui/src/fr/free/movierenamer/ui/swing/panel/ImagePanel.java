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
import com.alee.managers.popup.PopupWay;
import fr.free.movierenamer.info.ImageInfo.ImageCategoryProperty;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIMediaImage;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.swing.DragAndDrop;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import java.awt.Color;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.Icon;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

/**
 * Class ImagePanel
 *
 * @author Nicolas Magré
 */
public class ImagePanel extends WebPanel {

  private final Map<ImageCategoryProperty, GalleryPanel> galleryPanels;
  private final Map<ImageCategoryProperty, LabelListener> thumbLabel;
  private final ImageCategoryProperty[] supportedImages = new ImageCategoryProperty[]{ImageCategoryProperty.thumb, ImageCategoryProperty.fanart, ImageCategoryProperty.logo, ImageCategoryProperty.cdart};
  private final MovieRenamer mr;

  /**
   * Creates new form ImagePanel
   */
  public ImagePanel(MovieRenamer mr) {
    this.mr = mr;
    initComponents();

    imageTb.addToEnd(UIUtils.createSettingButton(PopupWay.downLeft));

    galleryPanels = new EnumMap<ImageCategoryProperty, GalleryPanel>(ImageCategoryProperty.class);
    thumbLabel = new EnumMap<ImageCategoryProperty, LabelListener>(ImageCategoryProperty.class);

    for (ImageCategoryProperty property : supportedImages) {
      final GalleryPanel galleryPanel = new GalleryPanel(mr, property);
      PropertyChangeSupport support = galleryPanel.getPropertyChange();
      support.addPropertyChangeListener(new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          if (evt.getPropertyName().equals("updateThumb")) {
            if (evt.getNewValue() != null) {
              thumbLabel.get(galleryPanel.getImageProperty()).setIcon((Icon) evt.getNewValue());
            }
          }
        }
      });

      WebLabel thumbLbl = new WebLabel();
      thumbLbl.setBorder(new LineBorder(new Color(204, 204, 204), 1, true));
      thumbLbl.setHorizontalAlignment(SwingConstants.CENTER);

      galleryPanels.put(property, galleryPanel);
      WebLabel label = new WebLabel();
      switch (property) {
        case thumb:
          label = this.thumbLbl;
          break;
        case fanart:
          label = fanartLbl;
          break;
        case cdart:
          label = cdartLbl;
          break;
        case logo:
          label = logoLbl;
          break;
      }
      thumbLabel.put(property, new LabelListener(label, property));
    }
  }

  public boolean isSupportedImage(ImageCategoryProperty property) {
    return galleryPanels.containsKey(property);
  }

  public GalleryPanel getGallery(ImageCategoryProperty key) {
    if (!isSupportedImage(key)) {
      UISettings.LOGGER.log(Level.SEVERE, String.format("Image %s is not supported by this panel", key.name()));
      return null;
    }
    return galleryPanels.get(key);
  }

  public WebLabel getThumbLabel(ImageCategoryProperty key) {
    if (!isSupportedImage(key)) {
      UISettings.LOGGER.log(Level.SEVERE, String.format("Image %s is not supported by this panel", key.name()));
      return null;
    }
    return thumbLabel.get(key).getLabel();
  }

  private void showGalleryPanel(final ImageCategoryProperty key) {
    if (!isSupportedImage(key)) {
      UISettings.LOGGER.log(Level.SEVERE, String.format("Image %s is not supported by this panel", key.name()));
      return;
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        galleryPanels.get(key).setVisible(true);
      }
    });
  }

  public void addImages(List<UIMediaImage> image, ImageCategoryProperty key) {
    if (!isSupportedImage(key)) {
      UISettings.LOGGER.log(Level.SEVERE, String.format("Panel %s does not support image type : %s", "", key.name()));
      return;
    }

    thumbLabel.get(key).setIcon(null);
    galleryPanels.get(key).clear();
    galleryPanels.get(key).addImages(image);
  }

  public void enabledListener() {
    for (ImageCategoryProperty property : thumbLabel.keySet()) {
      thumbLabel.get(property).setListenerEnabled(true);
    }
  }

  public void clearPanel() {

    for (GalleryPanel gpnl : galleryPanels.values()) {
      gpnl.clear();
    }

    for (LabelListener thumbLbl : thumbLabel.values()) {
      thumbLbl.setIcon(null);
      thumbLbl.setListenerEnabled(false);
    }
  }

  public List<ImageCategoryProperty> getSupportedImages() {
    return new ArrayList<ImageCategoryProperty>(galleryPanels.keySet());
  }

  private class LabelListener {

    private WebLabel label;
    private DropTarget dt;
    private MouseAdapter mouseAdapter;
    private boolean listenerEnabled;
    private ImageCategoryProperty key;

    public LabelListener(WebLabel label, ImageCategoryProperty key) {
      this.label = label;
      this.key = key;
      this.listenerEnabled = false;
      DragAndDrop dropFile = new DragAndDrop(ImagePanel.this.mr) {
        @Override
        public void getFiles(List<File> files) {
          // TODO
          for (File file : files) {
            System.out.println(file.getName());
          }
        }
      };
      this.dt = new DropTarget(label, dropFile);
      dt.setActive(listenerEnabled);

      this.mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent evt) {
          if (listenerEnabled) {
            showGalleryPanel(LabelListener.this.key);
          }
        }
      };
      label.addMouseListener(mouseAdapter);
    }

    public WebLabel getLabel() {
      return label;
    }

    public void setIcon(Icon icon) {
      label.setIcon(icon);
    }

    public void setListenerEnabled(boolean listenerEnabled) {
      this.listenerEnabled = listenerEnabled;
      dt.setActive(listenerEnabled);
    }
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    imageTb = new com.alee.laf.toolbar.WebToolBar();
    imageLbl = new com.alee.laf.label.WebLabel();
    thumbnailLbl = new com.alee.laf.label.WebLabel();
    thumbLbl = new com.alee.laf.label.WebLabel();
    fanartLbl = new com.alee.laf.label.WebLabel();
    fanarttLbl = new com.alee.laf.label.WebLabel();
    webLabel1 = new com.alee.laf.label.WebLabel();
    cdartLbl = new com.alee.laf.label.WebLabel();
    webLabel3 = new com.alee.laf.label.WebLabel();
    logoLbl = new com.alee.laf.label.WebLabel();

    setMinimumSize(new java.awt.Dimension(150, 500));
    setPreferredSize(new java.awt.Dimension(150, 700));

    imageTb.setFloatable(false);
    imageTb.setRollover(true);
    imageTb.setMargin(new java.awt.Insets(1, 5, 1, 5));
    imageTb.setRound(5);

    imageLbl.setLanguage(UIUtils.i18n.getLanguageKey("image"));
    imageLbl.setFont(new java.awt.Font("Ubuntu", 1, 13)); // NOI18N
    imageTb.add(imageLbl);

    thumbnailLbl.setText("Thumbnail");
    thumbnailLbl.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N

    thumbLbl.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
    thumbLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

    fanartLbl.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
    fanartLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

    fanarttLbl.setText("Fanart");
    fanarttLbl.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N

    webLabel1.setText("Cdart");
    webLabel1.setFont(new java.awt.Font("Ubuntu", 1, 12)); // NOI18N

    cdartLbl.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

    webLabel3.setText("Logo");

    logoLbl.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(logoLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(cdartLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(imageTb, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(thumbLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(fanartLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(thumbnailLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(fanarttLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(webLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(webLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(imageTb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(thumbnailLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(thumbLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(fanarttLbl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(fanartLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(webLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(cdartLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(webLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(18, 18, 18)
        .addComponent(logoLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(48, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private com.alee.laf.label.WebLabel cdartLbl;
  private com.alee.laf.label.WebLabel fanartLbl;
  private com.alee.laf.label.WebLabel fanarttLbl;
  private com.alee.laf.label.WebLabel imageLbl;
  private com.alee.laf.toolbar.WebToolBar imageTb;
  private com.alee.laf.label.WebLabel logoLbl;
  private com.alee.laf.label.WebLabel thumbLbl;
  private com.alee.laf.label.WebLabel thumbnailLbl;
  private com.alee.laf.label.WebLabel webLabel1;
  private com.alee.laf.label.WebLabel webLabel3;
  // End of variables declaration//GEN-END:variables
}
