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
package fr.free.movierenamer.utils;

import fr.free.movierenamer.settings.Settings;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

/**
 * Class Sorter
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class Sorter {

  public static abstract class ISort {

    protected abstract String getName();

    protected abstract int getYear();

    protected long getLength() {
      return 0;
    }
  }

  public enum SorterType {

    NONE,
    ALPHABETIC,
    YEAR,
    LENGTH,
    LEVENSTHEIN,
    YEAR_ROUND,
    LEVEN_YEAR,
    ALPHA_YEAR;
  }

  private Sorter() {
    throw new UnsupportedOperationException();
  }

  public static void sort(List<? extends ISort> list, SorterType type) {
    switch (type) {
      case ALPHABETIC:
        Collections.sort(list, new AlphabeticSort());
        break;
      case LENGTH:
        Collections.sort(list, new LengthSort());
        break;
      case YEAR:
        Collections.sort(list, new YearSort());
        break;
      default:
        Settings.LOGGER.log(Level.SEVERE, "Sorter type {0} is not supported", type.name());
    }
  }

  public static void sort(List<? extends ISort> list, SorterType type, int year) {
    if (type.equals(SorterType.YEAR_ROUND) || type.equals(SorterType.ALPHA_YEAR)) {
      sortYear(list, year, type.equals(SorterType.ALPHA_YEAR) ? new AlphabeticSort() : null);
      return;
    }
    Settings.LOGGER.log(Level.SEVERE, "Sorter type {0} is not supported with year sort", type.name());
  }

  public static void sort(List<? extends ISort> list, String search) {
    Collections.sort(list, new LevenshteinSort(search));
  }

  public static void sort(List<? extends ISort> list, int year, String search) {
    sortYear(list, year, new LevenshteinSort(search));
  }

  private static <T extends ISort> List<T> getExactYear(List<T> list, int year) {
    List<T> res = new ArrayList<T>();
    for (T result : list) {
      if (result.getYear() == year) {
        res.add(result);
      }
    }
    list.removeAll(res);
    return res;
  }

  /**
   * Sort list by YEAR, first exact YEAR then YEAR + 1 , YEAR - 1
   * If search is not null group of YEAR are sorted by COMPARATOR (only for YEAR, YEAR + 1, YEAR -1)
   *
   * @param <T>
   * @param list List to sort
   * @param year Year
   * @param search
   */
  private static <T extends ISort> void sortYear(List<T> list, int year, Comparator<ISort> comparator) {
    List<T> tmpList = new ArrayList<T>(list);
    list.clear();
    if (year >= 1900 && year <= Calendar.getInstance().get(Calendar.YEAR)) {
      for (int i = 1; i < -2; i--) {
        List<T> res = getExactYear(tmpList, year + i);
        if (comparator != null) {
          Collections.sort(res, comparator);
        }
        list.addAll((i == 0 ? i : list.size()), res);
      }
      //Collections.sort(tmpList, new YearSort());
    }
    list.addAll(tmpList);
  }

  public static class YearSort implements Comparator<ISort> {

    @Override
    public int compare(ISort t, ISort t1) {
      return t1.getYear() - t.getYear();
    }
  }

  private static class LevenshteinSort implements Comparator<ISort> {

    private String search;
    private boolean damerau;

    public LevenshteinSort(String search) {
      this.search = search;
      this.damerau = true;
    }

    public LevenshteinSort(String search, boolean damerau) {
      this.search = search;
      this.damerau = damerau;
    }

    @Override
    public int compare(ISort t, ISort t1) {
      if (search == null) {
        return 0;
      }
      return DamerauLevenshteinDistance(search, t.getName()) - DamerauLevenshteinDistance(search, t1.getName());
    }

    private int minimum(int a, int b, int c) {
      return Math.min(Math.min(a, b), c);
    }

    private int DamerauLevenshteinDistance(String str1, String str2) {
      int[][] distance = new int[str1.length() + 1][str2.length() + 1];
      for (int i = 1; i <= str1.length(); i++) {
        for (int j = 1; j <= str2.length(); j++) {
          int cost = ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1);
          distance[i][j] = minimum(distance[i - 1][j] + 1, distance[i][j - 1] + 1, distance[i - 1][j - 1] + cost);
          if (damerau) {
            if (i < str1.length() && j < str2.length() && str1.charAt(i) == str2.charAt(j - 1) && str1.charAt(i - 1) == str2.charAt(j)) {
              if (i > 1 && j > 1) {
                distance[i][j] = Math.min(distance[i][j], distance[i - 2][j - 2] + cost);
              }
            }
          }
        }
      }
      return distance[str1.length()][str2.length()];
    }
  }

  private static class AlphabeticSort implements Comparator<ISort> {

    @Override
    public int compare(ISort t, ISort t1) {
      return t.getName().compareTo(t1.getName());
    }
  }

  private static class LengthSort implements Comparator<ISort> {

    @Override
    public int compare(ISort t, ISort t1) {
      Long tsize = t.getLength();
      return tsize.compareTo(t1.getLength());
    }
  }
}
