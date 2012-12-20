/*
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
package fr.free.movierenamer.ui.res.sort;

import fr.free.movierenamer.ui.res.ISort;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class LevenshteinSort
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public final class LevenshteinSort {

  private LevenshteinSort() {
    //
  }

  private static int minimum(int a, int b, int c) {
    return Math.min(Math.min(a, b), c);
  }

  private static int DamerauLevenshteinDistance(String str1, String str2) {
    int[][] distance = new int[str1.length() + 1][str2.length() + 1];
    for (int i = 1; i <= str1.length(); i++) {
      for (int j = 1; j <= str2.length(); j++) {
        int cost = ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1);
        distance[i][j] = minimum(distance[i - 1][j] + 1, distance[i][j - 1] + 1, distance[i - 1][j - 1] + cost);
        if (i < str1.length() && j < str2.length() && str1.charAt(i) == str2.charAt(j - 1) && str1.charAt(i - 1) == str2.charAt(j)) {
          if (i > 1 && j > 1) {
            distance[i][j] = Math.min(distance[i][j], distance[i - 2][j - 2] + cost);
          }
        }
      }
    }
    return distance[str1.length()][str2.length()];
  }

  public static void sort(List<? extends ISort> list, final String search) {
    Collections.sort(list, new Comparator<ISort>() {
      @Override
      public int compare(ISort str1, ISort str2) {

        return DamerauLevenshteinDistance(search, str1.getName()) - DamerauLevenshteinDistance(search, str2.getName());
      }
    });
  }
}
