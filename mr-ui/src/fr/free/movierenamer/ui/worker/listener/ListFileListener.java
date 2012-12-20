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

import com.alee.laf.list.DefaultListModel;
import com.alee.laf.list.WebList;
import com.alee.laf.optionpane.WebOptionPane;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.res.UIFile;
import fr.free.movierenamer.ui.res.sort.AlphabeticSort;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.ui.worker.ListFilesWorker;
import fr.free.movierenamer.utils.LocaleUtils;
import java.util.Collections;
import java.util.List;

/**
 * Class ListFileListener
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class ListFileListener extends AbstractListener<List<UIFile>> {

  private final WebList mediaList;
  private final DefaultListModel mediaFileNameModel;

  public ListFileListener(ListFilesWorker worker, MovieRenamer mr, WebList mediaList, DefaultListModel mediaFileNameModel) {
    super(mr, worker);
    this.mediaList = mediaList;
    this.mediaFileNameModel = mediaFileNameModel;
  }

  @Override
  protected void done() throws Exception {
    // Remove loader
    mediaFileNameModel.removeAllElements();

    List<UIFile> medias = worker.get();

    Collections.sort(medias, new AlphabeticSort());

    mediaList.setCellRenderer(UIUtils.iconListRenderer);
    mediaFileNameModel.addElements(medias);

    if (mediaFileNameModel.isEmpty()) {
      WebOptionPane.showMessageDialog(mr, LocaleUtils.i18n("noMediaFound"), LocaleUtils.i18n("error"), WebOptionPane.ERROR_MESSAGE);
    } else if (UISettings.getInstance().isSelectFirstMedia()) {
      mediaList.setSelectedIndex(0);
    }
  }
}
