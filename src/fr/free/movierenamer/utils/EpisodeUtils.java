/*
 * movie-renamer
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
package fr.free.movierenamer.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.free.movierenamer.info.EpisodeInfo;

/**
 * Class EpisodeUtils
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public final class EpisodeUtils {

  public static List<EpisodeInfo> filterBySeason(Iterable<EpisodeInfo> episodes, int season) {
    List<EpisodeInfo> results = new ArrayList<EpisodeInfo>(25);

    // filter given season from all seasons
    for (EpisodeInfo episode : episodes) {
      if (episode.getSeason() != null && season == episode.getSeason()) {
        results.add(episode);
      }
    }

    return results;
  }

  public static int getLastSeason(Iterable<EpisodeInfo> episodes) {
    int lastSeason = 0;

    // filter given season from all seasons
    for (EpisodeInfo episode : episodes) {
      if (episode.getSeason() != null && episode.getSeason() > lastSeason) {
        lastSeason = episode.getSeason();
      }
    }

    return lastSeason;
  }

  public static void sortEpisodes(List<EpisodeInfo> episodes) {
    Collections.sort(episodes, episodeComparator());
  }

  public static Comparator<EpisodeInfo> episodeComparator() {
    return new Comparator<EpisodeInfo>() {

      @Override
      public int compare(EpisodeInfo a, EpisodeInfo b) {
        int diff = compareValue(a.getTvShowName(), b.getTvShowName());
        if (diff != 0)
          return diff;

        diff = compareValue(a.getSeason(), b.getSeason());
        if (diff != 0)
          return diff;

        diff = compareValue(a.getEpisode(), b.getEpisode());
        if (diff != 0)
          return diff;

        return compareValue(a.getName(), b.getName());
      }

      private <T> int compareValue(Comparable<T> o1, T o2) {
        if (o1 == null && o2 == null)
          return 0;
        if (o1 == null && o2 != null)
          return Integer.MAX_VALUE;
        if (o1 != null && o2 == null)
          return Integer.MIN_VALUE;

        return o1.compareTo(o2);
      }
    };
  }

  private EpisodeUtils() {
    throw new UnsupportedOperationException();
  }

}
