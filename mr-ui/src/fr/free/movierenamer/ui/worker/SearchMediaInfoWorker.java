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
package fr.free.movierenamer.ui.worker;

import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.scrapper.MediaScrapper;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.ui.IMediaPanel;
import fr.free.movierenamer.ui.LoadingDialog.LoadingDialogPos;
import fr.free.movierenamer.ui.MovieRenamer;
import fr.free.movierenamer.ui.res.UIFile;
import fr.free.movierenamer.ui.res.UISearchResult;
import java.beans.PropertyChangeSupport;

/**
 * Class SearchMediaInfosWorker
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaInfoWorker extends AbstractWorker {

  private final MediaScrapper<Media, MediaInfo> scrapper;
  private final UIFile file;
  private final UISearchResult searchResult;
  private final IMediaPanel mediaPanel;

  /**
   * Constructor arguments
   * 
   * @param parent
   * @param mediaPanel
   * @param file 
   * @param media
   */
  public SearchMediaInfoWorker(PropertyChangeSupport errorSupport, MovieRenamer parent, IMediaPanel mediaPanel, UIFile file, UISearchResult searchResult) {
    super(errorSupport, parent);
    this.mediaPanel = mediaPanel;
    this.file = file;
    this.searchResult = searchResult;
    this.scrapper = (searchResult != null) ? (MediaScrapper<Media, MediaInfo>) searchResult.getScrapper() : null;
  }

  @Override
  protected LoadingDialogPos getLoadingDialogPos() {
    return LoadingDialogPos.inf;
  }

  @Override
  public void executeInBackground() throws Exception {
    MediaInfo info = null;
    if (searchResult != null && scrapper != null) {
      Media media = searchResult.getSearchResult();
      info = scrapper.getInfo(media);
      info.setMediaTag(file.getMediaTag());
    }
    mediaPanel.setMediaInfo(info);
    //parent.updateRenamedTitle();
  }

}
