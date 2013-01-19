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
import com.alee.laf.list.DefaultListModel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.utils.ImageUtils;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

/**
 * class MediaPanel
 *
 * @author Nicolas Magré
 */
public abstract class MediaPanel extends WebPanel {

  private final int nbStar = 5;
  private final WebPanel starPanel;
  private final List<WebLabel> stars;
  private final GalleryPanel galleryPanel;

  protected MediaPanel(MovieRenamer mr) {
    galleryPanel = new GalleryPanel(mr);
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

    clearStars();
  }

  protected List<WebLabel> getStarsLabel() {
    return stars;
  }

  public GalleryPanel getGalleryPanel() {
    return galleryPanel;
  }

  /**
   * Clear media panel
   */
  public abstract void clear();

  public abstract void setMediaInfo(MediaInfo mediaInfo);

  public abstract MediaInfo getMediaInfo();

  public abstract WebList getCastingList();

  public abstract DefaultListModel getCastingModel();


  protected WebPanel getStarPanel() {
    return starPanel;
  }

  protected void clearStars() {
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
    if (rate == null || rate < 0.00) {
      return;
    }

    if (rate > 5) {
      rate /= (10 / nbStar);
    }

    int n = rate.intValue();
    for (int i = 0; i < n; i++) {
      stars.get(i).setIcon(ImageUtils.STAR_16);
    }

    if ((rate - rate.intValue()) >= 0.50 && n < nbStar) {
      stars.get(n).setIcon(ImageUtils.STARHALF_16);
    }
  }
}
