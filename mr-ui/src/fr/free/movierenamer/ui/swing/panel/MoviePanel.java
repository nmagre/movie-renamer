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

import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIMovieInfo;
import fr.free.movierenamer.ui.bean.UIPersonImage;
import fr.free.movierenamer.ui.swing.panel.info.movie.MovieCastingInfoPanel;
import fr.free.movierenamer.ui.swing.panel.info.InfoPanel;
import fr.free.movierenamer.ui.swing.panel.info.movie.MovieInfoPanel;
import java.util.List;

/**
 * Class MoviePanel
 *
 * @author Nicolas Magré
 */
public class MoviePanel extends VideoPanel<UIMovieInfo> {

  @SuppressWarnings("unchecked")
  public MoviePanel(MovieRenamer mr) {
    super(new MovieInfoPanel(mr), new MovieCastingInfoPanel()/*, new MovieIdPanel(mr), new TrailerInfoPanel()*/);
  }

  @Override
  public void clearPanel() {
    // Nothing to clear
  }

  @Override
  protected String getTitle(UIMovieInfo info) {
    String title = info.getTitle();
    if (info.getYear() != null) {
      title += " (" + info.getYear() + ")";
    }
    return title;
  }

  @Override
  protected Double getRate(UIMovieInfo info) {
    return info.getRating();
  }

  @Override
  protected boolean addEditButton() {
    return true;
  }

  @Override
  public UIMovieInfo getInfo() {
    return (UIMovieInfo) panels.get(InfoPanel.PanelType.INFO).getInfo();
  }

  @Override
  public void setInfo(UIMovieInfo info) {
    super.setInfo(info);

    ((InfoPanel<List<UIPersonImage>>) panels.get(InfoPanel.PanelType.CASTING_INFO)).setInfo(info.getCasting());
    //panels.get(InfoPanel.PanelType.ID_INFO).setInfo(info.getIds());
  }

  @Override
  protected boolean addRefreshButton() {
    return true;
  }
}
