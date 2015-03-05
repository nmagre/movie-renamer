/*
 * Movie Renamer
 * Copyright (C) 2015 Nicolas Magré
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
package fr.free.movierenamer.ui.worker.impl;

import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.WebCustomTooltip;
import fr.free.movierenamer.ui.bean.IHtmlListTooltip;
import fr.free.movierenamer.ui.worker.AbstractWorker;

/**
 * Class ListTooltipWorker
 *
 * @author Nicolas Magré
 */
public class ListTooltipWorker extends AbstractWorker<String, Void> {

  private final IHtmlListTooltip htmlToolTip;
  private WebCustomTooltip wct;

  public ListTooltipWorker(IHtmlListTooltip htmlToolTip, WebCustomTooltip wct) {
    this.htmlToolTip = htmlToolTip;
    this.wct = wct;
  }

  @Override
  protected String executeInBackground() throws Exception {
    return htmlToolTip.getHtmlTooltip();
  }

  @Override
  protected void workerDone() throws Exception {
    TooltipManager.hideAllTooltips();
    wct = TooltipManager.showOneTimeTooltip(wct.getComponent(), wct.getDisplayLocation(), get(), wct.getDisplayWay());// Show tootltip with html content
  }

  @Override
  public String getDisplayName() {
    return "tooltip";// FIXME i18n
  }

  @Override
  public WorkerId getWorkerId() {
    return WorkerId.TOOLTIP;
  }

}
