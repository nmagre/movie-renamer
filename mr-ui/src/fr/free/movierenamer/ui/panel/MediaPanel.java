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

import com.alee.extended.image.WebImageGallery;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.DefaultListModel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import java.awt.FlowLayout;
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

  protected MediaPanel() {
    starPanel = new WebPanel();
    starPanel.setMargin(0);
    starPanel.setLayout(new FlowLayout());
    stars = new ArrayList<WebLabel>();
    for (int i = 0; i < nbStar; i++) {
      stars.add(new WebLabel());
      starPanel.add(stars.get(i));
    }

    clearStars();
  }

  /**
   * Clear media panel
   */
  public abstract void clear();

  public abstract void setMediaInfo(MediaInfo mediaInfo);

  public abstract MediaInfo getMediaInfo();

  public abstract WebList getCastingList();

  public abstract WebList getThumbnailsList();

  public abstract WebImageGallery getFanartsList();

  public abstract WebList getBannersList();

  public abstract WebList getCdartsList();

  public abstract WebList getLogosList();

  public abstract WebList getClearartsList();

  public abstract WebList getSubtitlesList();

  public abstract DefaultListModel getCastingModel();

  public abstract DefaultListModel getThumbnailsModel();

  public abstract DefaultListModel getFanartsModel();

  public abstract DefaultListModel getBannersModel();

  public abstract DefaultListModel getCdartsModel();

  public abstract DefaultListModel getLogosModel();

  public abstract DefaultListModel getClearartsModel();

  public abstract DefaultListModel getSubtitlesModel();

  protected WebPanel getStarPanel() {
    return starPanel;
  }

  protected void clearStars() {
    for (int i = 0; i < nbStar; i++) {
      stars.get(i).setIcon(ImageUtils.STAREMPTY_24);
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
      stars.get(i).setIcon(ImageUtils.STAR_24);
    }

    if ((rate - rate.intValue()) >= 0.50 && (n + 1) < nbStar) {
      stars.get(n + 1).setIcon(ImageUtils.STARHALF_24);
    }
  }
}
