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
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.ImageInfo.ImageCategoryProperty;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.IEventInfo;
import fr.free.movierenamer.ui.bean.IEventListener;
import fr.free.movierenamer.ui.bean.UIEvent;
import fr.free.movierenamer.ui.bean.UIMediaImage;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.swing.DragAndDrop;
import fr.free.movierenamer.ui.utils.UIUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

/**
 * Class ImagePanel
 *
 * @author Nicolas Magré
 */
public class ImagePanel extends WebPanel implements IEventListener {

  private final Map<ImageCategoryProperty, GalleryPanel> galleryPanels;
  private final Map<ImageCategoryProperty, LabelListener> imageLabels;
  private final MovieRenamer mr;
  public final static int pwidth = 120;

  public enum SupportedImages {

    thumb(ImageCategoryProperty.thumb, "image.thumb", 0.75F),
    fanart(ImageCategoryProperty.fanart, "image.fanart", 1.77F),
    logo(ImageCategoryProperty.logo, "image.logo", 2.58F),
    cdart(ImageCategoryProperty.cdart, "image.cdart", 1.0F),
    clearart(ImageCategoryProperty.clearart, "image.clearart", 1.77F),
    banner(ImageCategoryProperty.banner, "image.banner", 5.4F);
    private ImageCategoryProperty categoryProperty;
    private String i18nKey;
    private WebLabel label = new WebLabel(SwingConstants.LEFT);
    private LabelListener imageLabel;
    private float ratio;
    private DropTarget dt;

    private SupportedImages(ImageCategoryProperty categoryProperty, String i18nKey, float ratio) {
      this.categoryProperty = categoryProperty;
      this.i18nKey = i18nKey;
      this.ratio = ratio;

      int height = (int) (pwidth / ratio);
      label.setMinimumSize(new Dimension(pwidth, height));
      label.setPreferredSize(new Dimension(pwidth, height));
      label.setMaximumSize(new Dimension(pwidth, height));
      label.setFont(label.getFont().deriveFont(Font.BOLD));
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
   */
  public ImagePanel(MovieRenamer mr) {
    this.mr = mr;
    initComponents();

    imageTb.addToEnd(UIUtils.createSettingButton(PopupWay.downLeft));

    galleryPanels = new EnumMap<ImageCategoryProperty, GalleryPanel>(ImageCategoryProperty.class);
    imageLabels = new EnumMap<ImageCategoryProperty, LabelListener>(ImageCategoryProperty.class);

    for (SupportedImages property : SupportedImages.values()) {
      imagePanel.add(Box.createRigidArea(new Dimension(0, 10)));
      WebLabel label = new WebLabel(SwingConstants.LEFT);
      label.setVerticalAlignment(SwingConstants.TOP);
      label.setLanguage(property.getI18nKey());
      imagePanel.add(label);
      imagePanel.add(Box.createRigidArea(new Dimension(0, 5)));

      GalleryPanel galleryPanel = new GalleryPanel(mr, property);
      galleryPanels.put(property.getCategoryProperty(), galleryPanel);
      LabelListener imglbl = new LabelListener(property.getLabel(), property.getCategoryProperty());
      imageLabels.put(property.getCategoryProperty(), imglbl);
      imagePanel.add(property.getLabel());
    }

    imagePanel.revalidate();

    // Add ImagePanel to UIEvent receiver
    UIEvent.addEventListener(ImagePanel.class, this);
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

  public GalleryPanel getGallery(ImageCategoryProperty key) {

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
      thumbLbl.setIcon(null);
      thumbLbl.setListenerEnabled(false);
    }
  }

  private void clearGallery() {
    for (GalleryPanel gpnl : galleryPanels.values()) {
      gpnl.clear();
    }
  }

  public List<ImageCategoryProperty> getSupportedImages() {
    return new ArrayList<ImageCategoryProperty>(galleryPanels.keySet());
  }

  @Override
  public void UIEventHandler(UIEvent.Event event, IEventInfo param, Object newObject) {
    switch (event) {
      case WORKER_STARTED:
        break;
    }
  }

  private class LabelListener {

    private WebLabel label;
    private DropTarget dt;
    private MouseAdapter mouseAdapter;
    private boolean listenerEnabled;
    private ImageCategoryProperty key;

    public LabelListener(WebLabel label, final ImageCategoryProperty key) {
      this.label = label;
      this.key = key;
      this.listenerEnabled = false;
      DragAndDrop dropFile = new DragAndDrop(ImagePanel.this.mr) {
        @Override
        public void getFiles(List<File> files) {
          for (File file : files) {// FIXME ? need to be improved ?
            String mimetype = new MimetypesFileTypeMap().getContentType(file);
            if (mimetype.contains("/")) {
              String type = mimetype.split("/")[0];
              if (type.equals("image")) {
                BufferedImage readImage = null;
                Integer h;
                Integer w;
                try {
                  readImage = ImageIO.read(file);
                  h = readImage.getHeight();
                  w = readImage.getWidth();
                } catch (Exception e) {
                  h = null;
                  w = null;
                }
                Map<ImageInfo.ImageProperty, String> fields = new EnumMap<ImageInfo.ImageProperty, String>(ImageInfo.ImageProperty.class);
                try {
                  fields.put(ImageInfo.ImageProperty.url, file.toURI().toURL().toExternalForm());
                  fields.put(ImageInfo.ImageProperty.height, "" + h);
                  fields.put(ImageInfo.ImageProperty.width, "" + w);

                  galleryPanels.get(key).addImage(new UIMediaImage(new ImageInfo(-1, fields, key)));
                } catch (MalformedURLException ex) {
                  UISettings.LOGGER.log(Level.SEVERE, null, ex);
                }
              }
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
    imagePanel = new com.alee.laf.panel.WebPanel();

    imageTb.setFloatable(false);
    imageTb.setRollover(true);
    imageTb.setMargin(new java.awt.Insets(1, 5, 1, 5));
    imageTb.setRound(5);

    imageLbl.setLanguage(UIUtils.i18n.getLanguageKey("image"));
    imageLbl.setFont(new java.awt.Font("Ubuntu", 1, 13)); // NOI18N
    imageTb.add(imageLbl);

    imagePanel.setLayout(new javax.swing.BoxLayout(imagePanel, javax.swing.BoxLayout.PAGE_AXIS));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(imageTb, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(imagePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(imageTb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(imagePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private com.alee.laf.label.WebLabel imageLbl;
  private com.alee.laf.panel.WebPanel imagePanel;
  private com.alee.laf.toolbar.WebToolBar imageTb;
  // End of variables declaration//GEN-END:variables
}
