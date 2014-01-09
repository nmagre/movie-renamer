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

import fr.free.movierenamer.ui.swing.dialog.GalleryDialog;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.managers.popup.PopupWay;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.ImageInfo.ImageCategoryProperty;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIMediaImage;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.swing.DragAndDrop;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.MimetypesFileTypeMap;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

/**
 * Class ImagePanel
 *
 * @author Nicolas Magré
 */
public class ImagePanel extends WebPanel {

  private final Map<ImageCategoryProperty, GalleryDialog> galleryPanels;
  private final Map<ImageCategoryProperty, LabelListener> imageLabels;
  private final MovieRenamer mr;
  public final static int pwidth = 120;

  public enum SupportedImages {

    thumb(ImageCategoryProperty.thumb, "mrui.main.image.thumb", 0.75F),
    fanart(ImageCategoryProperty.fanart, "mrui.main.image.fanart", 1.77F),
    logo(ImageCategoryProperty.logo, "mrui.main.image.logo", 2.58F),
    cdart(ImageCategoryProperty.cdart, "mrui.main.image.cdart", 1.0F),
    clearart(ImageCategoryProperty.clearart, "mrui.main.image.clearart", 1.77F),
    banner(ImageCategoryProperty.banner, "mrui.main.image.banner", 5.4F);
    private final ImageCategoryProperty categoryProperty;
    private final String i18nKey;
    private final WebLabel label;
    private LabelListener imageLabel;
    private final float ratio;
    private DropTarget dt;

    private SupportedImages(ImageCategoryProperty categoryProperty, String i18nKey, float ratio) {
      this.categoryProperty = categoryProperty;
      this.i18nKey = i18nKey;
      this.ratio = ratio;

      int height = (int) (pwidth / ratio);
      label = new WebLabel(SwingConstants.CENTER);
      label.setMinimumSize(new Dimension(pwidth, height));
      label.setPreferredSize(new Dimension(pwidth, height));
      label.setMaximumSize(new Dimension(pwidth, height));
      label.setBorder(new LineBorder(new Color(204, 204, 204), 1, true));
    }

    public ImageCategoryProperty getCategoryProperty() {
      return this.categoryProperty;
    }

    public float getRatio() {
      return ratio;
    }

    public String getI18nKey() {
      return i18nKey;
    }

    public WebLabel getLabel() {
      return label;
    }
  }

  /**
   * Creates new form ImagePanel
   *
   * @param mr
   */
  public ImagePanel(MovieRenamer mr) {
    this.mr = mr;
    initComponents();

    imageTb.addToEnd(UIUtils.createSettingButton(PopupWay.downLeft));

    galleryPanels = new EnumMap<>(ImageCategoryProperty.class);
    imageLabels = new EnumMap<>(ImageCategoryProperty.class);

    for (SupportedImages property : SupportedImages.values()) {
      imagePanel.add(Box.createRigidArea(new Dimension(0, 10)));
      WebLabel label = new WebLabel(SwingConstants.CENTER);
      label.setVerticalAlignment(SwingConstants.TOP);
      label.setLanguage(property.getI18nKey());
      label.setDrawShade(true);

      imagePanel.add(label);
      imagePanel.add(Box.createRigidArea(new Dimension(0, 5)));

      GalleryDialog galleryPanel = new GalleryDialog(mr, property);
      galleryPanels.put(property.getCategoryProperty(), galleryPanel);

      LabelListener imglbl = new LabelListener(property.getLabel(), property.getCategoryProperty());
      imageLabels.put(property.getCategoryProperty(), imglbl);
      imagePanel.add(property.getLabel());
    }

    // Font
    imageLbl.setFont(UIUtils.titleFont);
  }

  public boolean isSupportedImage(ImageCategoryProperty key) {
    return galleryPanels.containsKey(key);
  }

  private SupportedImages getSupportedImages(ImageCategoryProperty key) {

    for (SupportedImages simage : SupportedImages.values()) {
      if (simage.getCategoryProperty() == key) {
        return simage;
      }
    }
    return null;
  }

  public GalleryDialog getGallery(ImageCategoryProperty key) {

    if (!isSupportedImage(key)) {
      UISettings.LOGGER.log(Level.SEVERE, String.format("Image %s is not supported by this panel", key.name()));
      return null;
    }
    return galleryPanels.get(key);
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

    galleryPanels.get(key).clear();
    galleryPanels.get(key).addImages(image);
  }

  public void enabledListener() {
    for (ImageCategoryProperty property : imageLabels.keySet()) {
      imageLabels.get(property).setListenerEnabled(true);
    }
  }

  public void clearPanel() {
    clearGallery();

    for (LabelListener thumbLbl : imageLabels.values()) {
      thumbLbl.setIcon(ImageUtils.getIconFromJar("ui/kview.png"));
      thumbLbl.setListenerEnabled(false);
    }
  }

  private void clearGallery() {
    for (GalleryDialog gpnl : galleryPanels.values()) {
      gpnl.clear();
    }
  }

  public List<ImageCategoryProperty> getSupportedImages() {
    return new ArrayList<>(galleryPanels.keySet());
  }

  private class LabelListener {

    private final WebLabel label;
    private final DropTarget dt;
    private final MouseAdapter mouseAdapter;
    private boolean listenerEnabled;
    private ImageCategoryProperty key;

    public LabelListener(WebLabel label, final ImageCategoryProperty key) {
      this.label = label;
      this.key = key;
      this.listenerEnabled = false;
      DragAndDrop dropFile = new DragAndDrop(ImagePanel.this.mr) {
        @Override
        public void getFiles(List<File> files) {
          for (File file : files) {// FIXME need to be improved ?
            MimetypesFileTypeMap mtftp = new MimetypesFileTypeMap();
            mtftp.addMimeTypes("image png tif jpg jpeg bmp");
            String mimetype = mtftp.getContentType(file);
            if (mimetype.contains("image")) {
              Map<ImageInfo.ImageProperty, String> fields = new EnumMap<>(ImageInfo.ImageProperty.class);

              fields.put(ImageInfo.ImageProperty.url, file.toURI().toString());
              UIMediaImage image = new UIMediaImage(new ImageInfo(-1, fields, key));
              try {
                image.setIcon(ImageUtils.getIcon(file.toURI().toURL(), null, null));
              } catch (MalformedURLException ex) {
                Logger.getLogger(ImagePanel.class.getName()).log(Level.SEVERE, null, ex);
              }
              galleryPanels.get(key).addImage(image);
            }
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
    jScrollPane1 = new javax.swing.JScrollPane();
    imagePanel = new com.alee.laf.panel.WebPanel();

    setMargin(new java.awt.Insets(4, 0, 4, 0));

    imageTb.setFloatable(false);
    imageTb.setRollover(true);
    imageTb.setMargin(new java.awt.Insets(0, 4, 0, 4));
    imageTb.setRound(5);

    imageLbl.setLanguage(UIUtils.i18n.getLanguageKey("image"));
    imageLbl.setIcon(ImageUtils.IMAGE_16);
    imageTb.add(imageLbl);

    jScrollPane1.setBorder(null);
    jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    imagePanel.setLayout(new javax.swing.BoxLayout(imagePanel, javax.swing.BoxLayout.PAGE_AXIS));
    jScrollPane1.setViewportView(imagePanel);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(imageTb, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
          .addComponent(jScrollPane1))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(imageTb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))
    );
  }// </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private com.alee.laf.label.WebLabel imageLbl;
  private com.alee.laf.panel.WebPanel imagePanel;
  private com.alee.laf.toolbar.WebToolBar imageTb;
  private javax.swing.JScrollPane jScrollPane1;
  // End of variables declaration//GEN-END:variables
}
