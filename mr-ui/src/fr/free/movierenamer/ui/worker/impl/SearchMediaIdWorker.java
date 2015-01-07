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

import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.searchinfo.Media.MediaType;
import fr.free.movierenamer.ui.bean.UIMovieInfo;
import fr.free.movierenamer.ui.bean.UISearchResult;
import fr.free.movierenamer.ui.swing.panel.info.movie.MovieIdPanel;
import fr.free.movierenamer.ui.worker.AbstractWorker;
import fr.free.movierenamer.utils.ScrapperUtils;
import fr.free.movierenamer.utils.ScrapperUtils.AvailableApiIds;
import java.util.List;

/**
 * Class SearchMediaIdWorker
 *
 * @author Nicolas Magré
 */
public class SearchMediaIdWorker extends AbstractWorker<List<IdInfo>, Void> {

  private final MovieIdPanel panel;
  private final UIMovieInfo mediaInfo;
  private final UISearchResult searchResult;
  private final MediaType mediaType;

  public SearchMediaIdWorker(MovieIdPanel panel, UIMovieInfo mediaInfo, UISearchResult searchResult, MediaType mediaType) {
    this.panel = panel;
    this.mediaInfo = mediaInfo;
    this.searchResult = searchResult;
    this.mediaType = mediaType;
  }

  @Override
  protected List<IdInfo> executeInBackground() throws Exception {
    List<IdInfo> idInfos = mediaInfo.getIds();

    for (AvailableApiIds apiId : AvailableApiIds.getAvailableApiIds(mediaType)) {
      if (!isInList(idInfos, apiId)) {
        IdInfo inf = ScrapperUtils.idLookup(apiId, idInfos.get(0), searchResult.getSearchResult());
        if (inf != null) {
          idInfos.add(inf);
        }
      }
    }

    return idInfos;
  }

  private boolean isInList(List<IdInfo> idInfos, AvailableApiIds apiid) {
    for (IdInfo id : idInfos) {
      if (id.getIdType() == apiid) {
        return true;
      }
    }

    return false;
  }

  @Override
  protected void workerDone() throws Exception {
    List<IdInfo> info = get();
    mediaInfo.setIdsInfo(info);
    panel.setInfo(mediaInfo);
    panel.setSearchButton(true);
  }

  @Override
  public String getDisplayName() {
    return "ID lookup";// FIXME i18n
  }

  @Override
  public WorkerId getWorkerId() {
    return WorkerId.SEARCH_ID;
  }

}
