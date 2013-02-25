/*
 * Movie Renamer
 * Copyright (C) 2012 Nicolas Magré
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

import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import fr.free.movierenamer.info.ImageInfo.ImageCategoryProperty;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.list.UIMediaImage;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.utils.ImageUtils;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

/**
 * class MediaPanel
 *
 * @author Nicolas Magré
 */
public abstract class MediaPanel extends WebPanel {

  private final int nbStar = 5;
  private final WebPanel starPanel;
  private final List<WebLabel> stars;
  private final Map<ImageCategoryProperty, GalleryPanel> galleryPanels;
  private final Map<ImageCategoryProperty, WebLabel> thumbLabel;
  protected MovieRenamer mr;

  protected MediaPanel(MovieRenamer mr, ImageCategoryProperty... supportedImages) {
    this.mr = mr;
    galleryPanels = new EnumMap<ImageCategoryProperty, GalleryPanel>(ImageCategoryProperty.class);
    thumbLabel = new EnumMap<ImageCategoryProperty, WebLabel>(ImageCategoryProperty.class);

    starPanel = new WebPanel();
    starPanel.setMargin(0);
    starPanel.setLayout(new FlowLayout());
    stars = new ArrayList<WebLabel>();
    for (int i = 0; i < nbStar; i++) {
      WebLabel label = new WebLabel();
      label.setMargin(new Insets(-3, 0, 0, 0));
      stars.add(label);
      starPanel.add(stars.get(i));
    }

    for (ImageCategoryProperty property : supportedImages) {
      final GalleryPanel galleryPanel = new GalleryPanel(mr, property);
      PropertyChangeSupport support = galleryPanel.getPropertyChange();
      support.addPropertyChangeListener(new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          if (evt.getPropertyName().equals("updateThumb")) {
            if (galleryPanel.getSelectedImage() != null) {
              thumbLabel.get(galleryPanel.getImageProperty()).setIcon(galleryPanel.getSelectedImage().getIcon());
            }
          }
        }
      });

      WebLabel thumbLbl = new WebLabel();
      thumbLbl.setBorder(new LineBorder(new Color(204, 204, 204), 1, true));
      thumbLbl.setHorizontalAlignment(SwingConstants.CENTER);
      thumbLbl.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent evt) {

          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              galleryPanel.setVisible(true);
            }
          });
        }
      });
      galleryPanels.put(property, galleryPanel);
      thumbLabel.put(property, new WebLabel());
    }

    clearStars();
  }

  protected abstract String getPanelName();

  protected List<WebLabel> getStarsLabel() {
    return Collections.unmodifiableList(stars);
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
    return thumbLabel.get(key);
  }

  public void addImages(List<UIMediaImage> image, ImageCategoryProperty key) {
    if (!isSupportedImage(key)) {
      UISettings.LOGGER.log(Level.SEVERE, String.format("Panel %s does not support image type : %s", getPanelName(), key.name()));
      return;
    }

    thumbLabel.get(key).setIcon(null);
    galleryPanels.get(key).clear();
    galleryPanels.get(key).addImages(image);
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

  public void clearPanel() {
    clear();
    clearStars();

    for (GalleryPanel gpnl : galleryPanels.values()) {
      gpnl.clear();
    }

    for (WebLabel thumbLbl : thumbLabel.values()) {
      thumbLbl.setIcon(null);
      if(thumbLbl.getMouseListeners().length > 0) {
        thumbLbl.removeMouseListener(thumbLbl.getMouseListeners()[0]);
      }
    }
  }

  public boolean isSupportedImage(ImageCategoryProperty property) {
    return galleryPanels.containsKey(property);
  }

  public List<ImageCategoryProperty> getSupportedImages() {
    return new ArrayList<ImageCategoryProperty>(galleryPanels.keySet());
  }

  /**
   * Clear media panel
   */
  protected abstract void clear();

  public void addMediaInfo(MediaInfo mediaInfo) {
    setMediaInfo(mediaInfo);
    for (final ImageCategoryProperty property : thumbLabel.keySet()) {
      thumbLabel.get(property).addMouseListener(new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent evt) {
          showGalleryPanel(property);
        }
      });
    }
  }

  protected abstract void setMediaInfo(MediaInfo mediaInfo);

  public abstract MediaInfo getMediaInfo();

  public abstract WebList getCastingList();

  protected WebPanel getStarPanel() {
    return starPanel;
  }

  protected final void clearStars() {
    for (int i = 0; i < nbStar; i++) {
      stars.get(i).setIcon(ImageUtils.STAREMPTY_16);
    }
  }

  /**
   * Set star compared with rate
   *
   * @param rate
   */
  protected void setRate(Double rate) {
    Double value = rate;
    if (value == null || value < 0.00) {
      return;
    }

    if (value > 5) {
      value /= (10 / nbStar);
    }

    int n = value.intValue();
    for (int i = 0; i < n; i++) {
      stars.get(i).setIcon(ImageUtils.STAR_16);
    }

    if ((rate - rate.intValue()) >= 0.50 && n < nbStar) {
      stars.get(n).setIcon(ImageUtils.STARHALF_16);
    }
  }
}
