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

import com.alee.laf.list.WebList;
import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.ui.list.UIPersonImage;
import fr.free.movierenamer.ui.utils.UIUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Class SearchMediaImagesWorker
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class SearchMediaCastingWorker extends AbstractWorker<List<UIPersonImage>> {

  private final MediaInfo info;
  private final WebList castingList;

  /**
   * Constructor arguments
   *
   * @param info
   * @param castingList
   */
  @SuppressWarnings("unchecked")
  public SearchMediaCastingWorker(MediaInfo info, WebList castingList) {
    super();
    this.info = info;
    this.castingList = castingList;
  }

  @Override
  public List<UIPersonImage> executeInBackground() throws Exception {
    List<UIPersonImage> persons = new ArrayList<UIPersonImage>();
    List<CastingInfo> infos;

    if (info != null) {
      infos = info.getCast();
      int count = infos.size();
      for (int i = 0; i < count; i++) {
        if (isCancelled()) {
          return new ArrayList<UIPersonImage>();
        }

        persons.add(new UIPersonImage(infos.get(i), UIUtils.getAnimatedLoader(castingList, i)));
      }
    }

    return persons;
  }
}
