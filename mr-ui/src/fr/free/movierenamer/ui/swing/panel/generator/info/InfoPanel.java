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
package fr.free.movierenamer.ui.swing.panel.generator.info;

import fr.free.movierenamer.info.Info;
import fr.free.movierenamer.ui.bean.IEventInfo;
import fr.free.movierenamer.ui.bean.UIEvent;
import fr.free.movierenamer.ui.swing.panel.generator.PanelGenerator;
import javax.swing.Icon;

/**
 * Class InfoPanel
 *
 * @author Nicolas Magré
 */
public abstract class InfoPanel<T extends Info> extends PanelGenerator implements IInfoPanel<T> {

  public abstract Icon getIcon();

  public abstract String getPanelName();

  protected void regiterUIEvent() {
    UIEvent.addEventListener(this.getClass(), this);
  }

  protected void unregiterUIEvent() {
    UIEvent.removeEventListener(this.getClass());
  }

  @Override
  public void UIEventHandler(UIEvent.Event event, IEventInfo info, Object param) {
    System.out.println("Receive event");
    // Do nothing
  }
}
