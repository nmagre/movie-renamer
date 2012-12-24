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
import fr.free.movierenamer.ui.utils.UIUtils;
import java.awt.FlowLayout;

/**
 * class MediaPanel
 *
 * @author Nicolas Magré
 */
public abstract class MediaPanel extends WebPanel {

  private final WebPanel starPanel;
  private final WebLabel star;
  private final WebLabel star1;
  private final WebLabel star2;
  private final WebLabel star3;
  private final WebLabel star4;

  protected MediaPanel() {
    starPanel = new WebPanel();
    starPanel.setLayout(new FlowLayout());
    star = new WebLabel();
    star1 = new WebLabel();
    star2 = new WebLabel();
    star3 = new WebLabel();
    star4 = new WebLabel();
    starPanel.add(star);
    starPanel.add(star1);
    starPanel.add(star2);
    starPanel.add(star3);
    starPanel.add(star4);
    resetStar();
  }

  /**
   * Clear media panel
   */
  public abstract void clear();

  public abstract void setMediaInfo(MediaInfo mediaInfo);

  public abstract MediaInfo getMediaInfo();

  public abstract WebList getCastingList();

  public abstract WebList getThumbnailsList();

  public abstract WebList getFanartsList();

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

  protected void resetStar() {
    star.setIcon(UIUtils.STAR_EMPTY);
    star1.setIcon(UIUtils.STAR_EMPTY);
    star2.setIcon(UIUtils.STAR_EMPTY);
    star3.setIcon(UIUtils.STAR_EMPTY);
    star4.setIcon(UIUtils.STAR_EMPTY);
  }

  /**
   * Set star compared with rate
   *
   * @param rate
   */
  protected void setRate(Float rate) {
    if (rate < 0.00) {
      return;
    }
    rate /= 2;
    int n = rate.intValue();
    switch (n) {
      case 0:
        break;
      case 1:
        star.setIcon(UIUtils.STAR);
        if ((rate - rate.intValue()) >= 0.50) {
          star1.setIcon(UIUtils.STAR_HALF);
        }
        break;
      case 2:
        star.setIcon(UIUtils.STAR);
        star1.setIcon(UIUtils.STAR);
        if ((rate - rate.intValue()) >= 0.50) {
          star2.setIcon(UIUtils.STAR_HALF);
        }
        break;
      case 3:
        star.setIcon(UIUtils.STAR);
        star1.setIcon(UIUtils.STAR);
        star2.setIcon(UIUtils.STAR);
        if ((rate - rate.intValue()) >= 0.50) {
          star3.setIcon(UIUtils.STAR_HALF);
        }
        break;
      case 4:
        star.setIcon(UIUtils.STAR);
        star1.setIcon(UIUtils.STAR);
        star2.setIcon(UIUtils.STAR);
        star3.setIcon(UIUtils.STAR);
        if ((rate - rate.intValue()) >= 0.50) {
          star4.setIcon(UIUtils.STAR_HALF);
        }
        break;
      case 5:
        star.setIcon(UIUtils.STAR);
        star1.setIcon(UIUtils.STAR);
        star2.setIcon(UIUtils.STAR);
        star3.setIcon(UIUtils.STAR);
        star4.setIcon(UIUtils.STAR);
        break;
      default:
        break;
    }
  }
}
