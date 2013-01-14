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
package fr.free.movierenamer.ui.worker.listener;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.EventListModel;
import com.alee.laf.list.WebList;
import com.alee.laf.optionpane.WebOptionPane;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.list.IIconList;
import fr.free.movierenamer.ui.list.IconListRenderer;
import fr.free.movierenamer.ui.list.UIFile;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.worker.ListFilesWorker;
import fr.free.movierenamer.utils.LocaleUtils;
import java.util.List;

/**
 * Class ListFileListener
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class ListFileListener extends AbstractListener<List<UIFile>> {

  private final WebList mediaList;
  private final EventList<UIFile> mediaFileEventList;
  private final EventListModel<UIFile> model;

  public ListFileListener(ListFilesWorker worker, MovieRenamer mr, WebList mediaList, EventList<UIFile> mediaFileEventList, EventListModel<UIFile> model) {
    super(mr, worker);
    this.mediaList = mediaList;
    this.mediaFileEventList = mediaFileEventList;
    this.model = model;
  }

  @Override
  protected void done() throws Exception {

    List<UIFile> medias = worker.get();

    mediaList.setCellRenderer(new IconListRenderer<IIconList>(false));

    mediaFileEventList.addAll(medias);
    mediaList.setModel(model);

    if (mediaFileEventList.isEmpty()) {
      WebOptionPane.showMessageDialog(mr, LocaleUtils.i18n("noMediaFound"), LocaleUtils.i18n("warning"), WebOptionPane.WARNING_MESSAGE);
    } else if (UISettings.getInstance().isSelectFirstMedia()) {
      mediaList.setSelectedIndex(0);
      mediaList.revalidate();
      mediaList.repaint();
    }
  }
}
