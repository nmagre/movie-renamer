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

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.scrapper.MediaScrapper;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.ui.res.UIPersonImage;
import fr.free.movierenamer.ui.res.UISearchResult;
import fr.free.movierenamer.ui.utils.ImageUtils;
import java.awt.Dimension;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;

/**
 * Class SearchMediaImagesWorker
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaCastingWorker extends AbstractWorker<List<UIPersonImage>> {

  private final UISearchResult searchResult;
  private final MediaScrapper<Media, MediaInfo> scrapper;
  private final Dimension actorListDim = new Dimension(30, 53);

  /**
   * Constructor arguments
   *
   * @param errorSupport
   * @param searchResult
   */
  @SuppressWarnings("unchecked")
  public SearchMediaCastingWorker(PropertyChangeSupport errorSupport, UISearchResult searchResult) {
    super(errorSupport);
    this.searchResult = searchResult;
    this.scrapper = (searchResult != null) ? (MediaScrapper<Media, MediaInfo>) searchResult.getScrapper() : null;
  }

  @Override
  public List<UIPersonImage> executeInBackground() throws Exception {
    List<CastingInfo> infos;
    List<UIPersonImage> persons = new ArrayList<UIPersonImage>();

    if (searchResult != null && scrapper != null) {
      Media media = searchResult.getSearchResult();
      infos = scrapper.getCasting(media);
      int count = infos.size();
      for (int i = 0; i < count; i++) {
        if (isCancelled()) {
          return new ArrayList<UIPersonImage>();
        }
        Icon icon = ImageUtils.getIcon(infos.get(i).getPicturePath(), actorListDim, "ui/unknown.png");
        persons.add(new UIPersonImage(infos.get(i), icon));
        double progress = (i + 1) / (double) count;
        setProgress((int) (progress * 100));
      }
    }

    return persons;
  }
}
