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
package fr.free.movierenamer.ui.swing.custom;

import com.alee.laf.list.WebList;
import com.alee.managers.language.data.TooltipWay;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.WebCustomTooltip;
import fr.free.movierenamer.ui.swing.IHtmlListTooltip;
import fr.free.movierenamer.ui.swing.IIconList;
import fr.free.movierenamer.ui.bean.UILoader;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.ui.worker.WorkerManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ListModel;
import javax.swing.Timer;

/**
 * Class ListTooltip
 *
 * @author Nicolas Magré
 */
public class ListTooltip extends MouseAdapter {

  private int lastIndex;
  private Timer timer;
  private int time;
  private boolean onlyLoading;
  private TooltipWay tooltipWay;

  public ListTooltip() {
    this(500, false);
  }

  public ListTooltip(int time, boolean onlyLoading) {
    this.time = time;
    this.onlyLoading = onlyLoading;
    lastIndex = -1;
    tooltipWay = TooltipWay.right;
  }

  public void setTooltipWay(TooltipWay tooltipWay) {
    this.tooltipWay = tooltipWay;
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    final WebList list = (WebList) e.getSource();
    ListModel model = list.getModel();
    int index = list.locationToIndex(e.getPoint());

    final Rectangle rect = list.getCellBounds(index, index);

    if (index > -1 && rect.contains(e.getPoint())) {
      if (lastIndex != index) {
        TooltipManager.hideAllTooltips();
        final Object obj = model.getElementAt(index);

        if (obj instanceof IIconList) {
          if ((onlyLoading && obj instanceof UILoader) || !onlyLoading) {

            if (timer != null) {
              timer.stop();
            }

            timer = new Timer(time, new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                int px = tooltipWay == TooltipWay.right ? list.getParent().getWidth() : list.getParent().getWidth() / 2;
                int py = tooltipWay == TooltipWay.right ? rect.getLocation().y + rect.height / 2 : rect.getLocation().y + rect.height;

                if (obj instanceof IHtmlListTooltip) {
                  WebCustomTooltip wct = TooltipManager.showOneTimeTooltip(list, new Point(px, py), ImageUtils.LOAD_WHITE_24, "", tooltipWay);
                  WorkerManager.setHtmlTooltip((IHtmlListTooltip) obj, wct);
                  timer.stop();
                  return;
                }

                String str;
                if (obj instanceof UILoader) {// FIXME i18n 
                  str = UIUtils.i18n.getLanguage("clickToCancel", true);
                } else {
                  str = obj.toString();
                }

                TooltipManager.showOneTimeTooltip(list, new Point(px, py), str, tooltipWay);

                timer.stop();
              }
            });

            timer.start();
            lastIndex = index;
          } else {
            reset();
          }
        }
      }
    } else {
      TooltipManager.hideAllTooltips();
      reset();
    }
  }

  private void reset() {
    if (timer != null) {
      timer.stop();
    }
    lastIndex = -1;
  }

  @Override
  public void mouseExited(MouseEvent e) {
    TooltipManager.hideAllTooltips();
    lastIndex = -1;
    if (timer != null) {
      timer.stop();
    }
  }
}
