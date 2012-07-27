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
package fr.free.movierenamer.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class Levenshtein
 *
 * @author Nicolas Magré
 *
 * Some parts are from wikipedia
 */
public abstract class Levenshtein {
  
  private static int minimum(int a, int b, int c) {
    return Math.min(Math.min(a, b), c);
  }
  
  private static int computeLevenshteinDistance(CharSequence str1, CharSequence str2) {
    int[][] distance = new int[str1.length() + 1][str2.length() + 1];
    
    for (int i = 0; i <= str1.length(); i++) {
      distance[i][0] = i;
    }
    
    for (int j = 0; j <= str2.length(); j++) {
      distance[0][j] = j;
    }
    
    for (int i = 1; i <= str1.length(); i++) {
      for (int j = 1; j <= str2.length(); j++) {
        distance[i][j] = minimum(distance[i - 1][j] + 1, distance[i][j - 1] + 1, distance[i - 1][j - 1] + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1));
      }
    }
    
    return distance[str1.length()][str2.length()];
  }

  public static void sortByLevenshteinDistanceYear(final String str, int year, List<SearchResult> arrayStr) {
    List<SearchResult> tmp = new ArrayList<SearchResult>();
    if (year != -1) {
      for (int i = 0; i < arrayStr.size(); i++) {
        SearchResult sres = arrayStr.get(i);
        String tmpStr = sres.getTitle();
        if (!tmpStr.contains("" + year) && !tmpStr.contains("" + (year + 1)) && !tmpStr.contains("" + (year -1))) {
          tmp.add(sres);
        }
      }
    }
    
    if (!tmp.isEmpty()) {
      arrayStr.removeAll(tmp);
    }
    
    Collections.sort(arrayStr, new Comparator<SearchResult>() {
      
      @Override
      public int compare(SearchResult str1, SearchResult str2) {
        
        return computeLevenshteinDistance(str, str1.getTitle()) - computeLevenshteinDistance(str, str2.getTitle());
      }
    });
    if (!tmp.isEmpty()) {
      Collections.sort(tmp, new Comparator<SearchResult>() {
        
        @Override
        public int compare(SearchResult str1, SearchResult str2) {
          
          return computeLevenshteinDistance(str, str1.getTitle()) - computeLevenshteinDistance(str, str2.getTitle());
        }
      });
      arrayStr.addAll(tmp);
    }
  }
}
