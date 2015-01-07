/*
 * Movie Renamer
 * Copyright (C) 2014 Nicolas Magré
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

import com.alee.laf.optionpane.WebOptionPane;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.bean.UIFile;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.utils.UIUtils;
import fr.free.movierenamer.ui.worker.ControlWorker;
import fr.free.movierenamer.ui.worker.IWorker.WorkerId;

/**
 * Class SetSearchWorker
 *
 * @author Nicolas Magré
 */
public class SetSearchWorker extends ControlWorker<String, IdInfo> {

  private final UIFile mediaFile;
  private boolean useImdb;

  public SetSearchWorker(MovieRenamer mr, UIFile mediaFile) {
    super(mr);
    this.mediaFile = mediaFile;
    useImdb = false;
  }

  @Override
  protected String executeInBackground() throws Exception {

    String search = mediaFile.getSearch();

    // Search for imdb id in name, nfo, ...
    IdInfo imdbid = mediaFile.getImdbId();
    if (imdbid != null) {
      useImdb = UISettings.getInstance().isUseImdbIdInSearch();
      if (!useImdb) {
        publishPause(imdbid);
      }

      if (useImdb) {
        search = "http://www.imdb.com/title/" + imdbid;
      }
    }

    return search;
  }

  @Override
  protected void processPause(IdInfo imdbid) {
    useImdb = WebOptionPane.showConfirmDialog(mr,
            UIUtils.i18n.getLanguage("dialog.imdbidfound", false, imdbid.toString()),
            UIUtils.i18n.getLanguage("dialog.question", false), WebOptionPane.YES_NO_OPTION,
            WebOptionPane.QUESTION_MESSAGE) == 0;
  }

  @Override
  protected void workerDone() throws Exception {
    String search = get();
    mediaFile.setSearch(search);
    if (useImdb) {
      String cscraperName = mr.getUIScraper().toString();
      if (!"Imdb".equals(cscraperName) && !"Universal".equals(cscraperName)) {// FIXME use constante
        mr.getMode().setUniversalScraper();
      }
    }

    mr.setSearch();
    mr.searchMedia();
  }

  @Override
  public String getDisplayName() {
    return "";
  }

  @Override
  public WorkerId getWorkerId() {
    return WorkerId.SEARCH_SET;
  }

}
