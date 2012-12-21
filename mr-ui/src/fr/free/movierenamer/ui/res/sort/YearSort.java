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

import fr.free.movierenamer.ui.res.UISearchResult;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Class YearSort
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public final class YearSort {

  private YearSort() {
    //
  }

  private static List<UISearchResult> getExactYear(List<UISearchResult> results, int year) {
    List<UISearchResult> res = new ArrayList<UISearchResult>();
    for (UISearchResult result : results) {
      if (result.getSearchResult().getYear() == year) {
        res.add(result);
      }
    }
    results.removeAll(res);
    return res;
  }

  private static List<UISearchResult> getExactYearPlus(List<UISearchResult> results, int year) {
    return getExactYear(results, year + 1);
  }

  private static List<UISearchResult> getExactYearMinus(List<UISearchResult> results, int year) {
    return getExactYear(results, year - 1);
  }

  public static List<UISearchResult> sort(List<UISearchResult> results, int year) {
    List<UISearchResult> sortResults = new ArrayList<UISearchResult>();
    if(year >= 1900 && year <= Calendar.getInstance().get(Calendar.YEAR)) {
      sortResults.addAll(getExactYear(results, year));
      sortResults.addAll(getExactYearPlus(results, year));
      sortResults.addAll(getExactYearMinus(results, year));
      sortResults.addAll(results);
      return sortResults;
    }
    return results;
  }
}
