/*
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
package fr.free.movierenamer.ui.panel;

import com.alee.utils.LafUtils;
import fr.free.movierenamer.info.ImageInfo.ImageCategoryProperty;
import fr.free.movierenamer.info.ImageInfo.ImageSize;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.list.IIconList;
import fr.free.movierenamer.ui.list.IconListRenderer;
import fr.free.movierenamer.ui.list.UIImageLang;
import fr.free.movierenamer.ui.list.UIMediaImage;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.worker.ImageWorker;
import fr.free.movierenamer.ui.worker.WorkerManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.SwingWorker;

/**
 * Class GalleryPanel
 *
 * @author Nicolas Magré
 */
public class GalleryPanel extends JDialog {

  private static final long serialVersionUID = 1L;
  private int imgsize = 130;
  private final CustomWebImageGallery thumbPreviewGallery;
  private List<UIMediaImage> images;
  private List<UIImageLang> languages;
  private final DefaultComboBoxModel languagesModel;
  private final ImageCategoryProperty property;
  private final MovieRenamer mr;
  private final PropertyChangeSupport propertyChange;

  /**
   * Creates new form GalleryPanel
   *
   * @param mr
   * @param property
   */
  public GalleryPanel(MovieRenamer mr, ImageCategoryProperty property) {
    this.mr = mr;
    this.property = property;

    initComponents();

    propertyChange = new PropertyChangeSupport(previewLbl);
    languages = new ArrayList<UIImageLang>();
    languagesModel = new DefaultComboBoxModel();
    languageCbb.setModel(languagesModel);
    languageCbb.setRenderer(new IconListRenderer<IIconList>(false));

    boolean useLanguage = false;
    switch (property) {
      case thumb:
      case banner:
      case cdart:
      case logo:
        useLanguage = true;
        break;
      case actor:
      case clearart:
      case fanart:
      case unknown:
        previewLbl.setPreferredSize(new Dimension(520, 320));
        useLanguage = false;
        break;
    }

    languageCbb.setVisible(useLanguage);

    thumbPreviewGallery = new CustomWebImageGallery(useLanguage);
    thumbPreviewPnl.setLayout(new BorderLayout());
    thumbPreviewGallery.setOpaque(false);
    images = new ArrayList<UIMediaImage>();

    PropertyChangeSupport changePreview = thumbPreviewGallery.getPropertyChange();
    changePreview.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("selectedImage")) {
          previewLbl.setIcon(ImageUtils.LOADER);
          UIMediaImage image = thumbPreviewGallery.getUIMediaImage();
          if (image == null) {
            UISettings.LOGGER.log(Level.WARNING, "UIMediaImage null");
            return;
          }

          if (ImageUtils.isInCache(image.getUri(ImageSize.medium))) {
            previewLbl.setIcon(ImageUtils.getIcon(image.getUri(ImageSize.medium), null, null));
            return;
          }

          final ImageWorker<UIMediaImage> imgWorker = new ImageWorker<UIMediaImage>(image, null, null, ImageSize.medium);//FIXME
          PropertyChangeListener propertyChange = new PropertyChangeListener() {//FIXME
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
              if (!(evt.getNewValue() instanceof SwingWorker.StateValue)) {
                return;
              }

              if (((SwingWorker.StateValue) evt.getNewValue()).equals(SwingWorker.StateValue.DONE)) {
                try {
                  Icon icon = imgWorker.get();
                  if (icon != null) {
                    previewLbl.setIcon(icon);
                    GalleryPanel.this.propertyChange.firePropertyChange("updateThumb", null, getSelectedImage());
                  } else {
                    // TODO
                  }
                } catch (InterruptedException ex) {
                  UISettings.LOGGER.log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                  UISettings.LOGGER.log(Level.SEVERE, null, ex);
                }
              }
            }
          };
          imgWorker.addPropertyChangeListener(propertyChange);//FIXME
          imgWorker.execute();//FIXME
        }
      }
    });
    thumbPreviewGallery.setImageLength(imgsize);

    thumbPreviewPnl.add(thumbPreviewGallery.getView(false), BorderLayout.CENTER);

    setLocationRelativeTo(mr);
    setModal(true);
  }

  public ImageCategoryProperty getImageProperty() {
    return property;
  }

  public PropertyChangeSupport getPropertyChange() {
    return propertyChange;
  }

  private void getTumbPreview(String country) {
    WorkerManager.stop(this.getClass());//Stop running images worker
    List<UIMediaImage> imageslang = getImageByLanguage(images, country);
    WorkerManager.fetchImages(this.getClass(), imageslang, this, "ui/unknown.png", ImageSize.small);
  }

  public void addImages(List<UIMediaImage> images) {
    this.images.addAll(images);
    for (UIMediaImage image : this.images) {
      UIImageLang imglang = image.getImagelang();

      if (!languages.contains(imglang)) {
        languages.add(imglang);
        languagesModel.addElement(imglang);
      }
    }

    if(languages.size() > 0) {
      languagesModel.setSelectedItem(languages.get(0));
    }
  }

  public synchronized void addThumbPreview(UIMediaImage mimage) {
    if (mimage != null && mimage.getIcon() != null) {
      thumbPreviewGallery.addImage(0, mimage);

      if (thumbPreviewGallery.getImagesSize() == 1) {
        thumbPreviewGallery.setSelectedIndex(0);
      }
    }
  }

  private List<UIMediaImage> getImageByLanguage(List<UIMediaImage> uiimages, String country) {
    List<UIMediaImage> limages = new ArrayList<UIMediaImage>();

    for (UIMediaImage image : uiimages) {
      Locale uiimagelang = image.getImagelang().getLang();

      if (country.replace("_", "").equalsIgnoreCase(uiimagelang.getCountry())) {
        limages.add(0, image);
      }
    }

    return limages;
  }

  public void setSelectedIndex(int index) {
    if (index > 0) {
      thumbPreviewGallery.setSelectedIndex(index);
    }
  }

  public UIMediaImage getSelectedImage() {
    return thumbPreviewGallery.getUIMediaImage();
  }

  public void clear() {
    thumbPreviewGallery.removeAllImages();
    images.clear();
    languagesModel.removeAllElements();
    languages.clear();
  }

  /**
   * This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    galleryPnl = new com.alee.laf.panel.WebPanel();
    thumbPreviewPnl = new com.alee.laf.panel.WebPanel();
    previewPnl = new com.alee.laf.panel.WebPanel();
    previewLbl = new com.alee.laf.label.WebLabel();
    languageCbb = new javax.swing.JComboBox();
    webButton1 = new com.alee.laf.button.WebButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    thumbPreviewPnl.setFocusable(false);

    languageCbb.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        languageCbbActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout previewPnlLayout = new javax.swing.GroupLayout(previewPnl);
    previewPnl.setLayout(previewPnlLayout);
    previewPnlLayout.setHorizontalGroup(
      previewPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, previewPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(languageCbb, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 134, Short.MAX_VALUE)
        .addComponent(previewLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(280, 280, 280))
    );
    previewPnlLayout.setVerticalGroup(
      previewPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(previewPnlLayout.createSequentialGroup()
        .addGroup(previewPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(previewLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(languageCbb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(0, 12, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout galleryPnlLayout = new javax.swing.GroupLayout(galleryPnl);
    galleryPnl.setLayout(galleryPnlLayout);
    galleryPnlLayout.setHorizontalGroup(
      galleryPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(galleryPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(galleryPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(previewPnl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(thumbPreviewPnl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    galleryPnlLayout.setVerticalGroup(
      galleryPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, galleryPnlLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(previewPnl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(thumbPreviewPnl, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
        .addContainerGap())
    );

    webButton1.setText("ok");
    webButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        webButton1ActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(galleryPnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(webButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(galleryPnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(webButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void languageCbbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_languageCbbActionPerformed
    if (languagesModel.getSize() > 0) {
      Locale locale = ((UIImageLang) languagesModel.getSelectedItem()).getLang();
      thumbPreviewGallery.removeAllImages();
      previewLbl.setIcon(null);
      getTumbPreview(locale.getCountry());
    }
  }//GEN-LAST:event_languageCbbActionPerformed

  private void webButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_webButton1ActionPerformed
    propertyChange.firePropertyChange("updateThumb", null, getSelectedImage());
  }//GEN-LAST:event_webButton1ActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private com.alee.laf.panel.WebPanel galleryPnl;
  private javax.swing.JComboBox languageCbb;
  private com.alee.laf.label.WebLabel previewLbl;
  private com.alee.laf.panel.WebPanel previewPnl;
  private com.alee.laf.panel.WebPanel thumbPreviewPnl;
  private com.alee.laf.button.WebButton webButton1;
  // End of variables declaration//GEN-END:variables
}
