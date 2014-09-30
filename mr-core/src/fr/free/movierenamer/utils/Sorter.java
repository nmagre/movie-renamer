/*
 * Copyright (C) 2012-2014 Nicolas Magré
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
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Jaro;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

/**
 * Class Sorter
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class Sorter {

  public static abstract class ISort {

    protected abstract String getName();

    protected String getOriginalName() {
      return getName();
    }

    public int getYear() {
      return -1;
    }

    protected long getLength() {
      return 0;
    }

    protected String getLanguage() {
      return "";
    }

    protected boolean hasImage() {
      return false;
    }
  }

  public enum SorterType {

    NONE,
    ALPHABETIC,
    YEAR,
    LENGTH,
    LANGUAGE,
    SIMMETRICS,
    YEAR_ROUND,
    LEVEN_YEAR,
    ALPHA_YEAR;
  }

  private Sorter() {
    throw new UnsupportedOperationException();
  }

  public static <T extends ISort> void sortAccurate(List<T> list, String tocompare, int year, int threshold) {

    tocompare = StringUtils.normaliseClean(tocompare);
    Map<Integer, List<T>> values = new TreeMap<Integer, List<T>>(new IntegerDescending());
    for (T object : list) {

      // if year match we add a "bonus"
      int bonus = 0;
      if (year >= 1900 && year <= Calendar.getInstance().get(Calendar.YEAR)) {
        final int oyear = object.getYear();
        if (year == oyear) {
          bonus = 75;
        } else if (oyear == (year - 1) || oyear == (year + 1)) {
          bonus = 50;
        }
      }

      // if there is an image we add a "bonus"
      if (object.hasImage()) {
        bonus += 10;
      }

      // Get best similarity between title and orig title
      int sim = simCompare(tocompare, object.getName(), bonus);
      if (object.getOriginalName() != null && !ObjectUtils.compare(object.getName(), object.getOriginalName())) {
        sim = Math.max(sim, simCompare(tocompare, object.getName(), bonus));
      }

      // We use a list cause 2 (or more) can have the same "sim" number
      List<T> listObj = values.get(sim);
      if (listObj == null) {
        listObj = new ArrayList<T>();
      }

      listObj.add(object);
      values.put(sim, listObj);
    }

    // Get the best match sim number
    int sim = 0;
    for (Integer msim : values.keySet()) {
      sim = msim;
      break;
    }

    // If sim number is greater than threshold we sort list
    if (sim >= threshold) {
      list.clear();
      for (List<T> olist : values.values()) {
        for (T o : olist) {
          list.add(o);
        }
      }
    }
  }

  private static <T extends ISort> int simCompare(String search, String str, int bonus) {
    str = StringUtils.normaliseClean(str);
    AbstractStringMetric algorithm;
    Float res = 0.0F;
    algorithm = new Jaro();
    res += algorithm.getSimilarity(search, str);
    algorithm = new JaroWinkler();
    res += algorithm.getSimilarity(search, str);
    algorithm = new Levenshtein();
    res += algorithm.getSimilarity(search, str);
    algorithm = new JaccardSimilarity();
    res += algorithm.getSimilarity(search, str);

    return Math.round((res) * 100) + bonus;
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
      case LANGUAGE:
        Collections.sort(list, new LanguageSort());
      default:
        Settings.LOGGER.log(Level.SEVERE, String.format("Sorter type %s is not supported", type.name()));
    }
  }

  public static void sort(List<? extends ISort> list, SorterType type, int year) {
    if (type.equals(SorterType.YEAR_ROUND) || type.equals(SorterType.ALPHA_YEAR)) {
      sortYear(list, year, type.equals(SorterType.ALPHA_YEAR) ? new AlphabeticSort() : null);
      return;
    }
    Settings.LOGGER.log(Level.SEVERE, String.format("Sorter type %s is not supported with year sort", type.name()));
  }

  public static void sort(List<? extends ISort> list, String search) {
    Collections.sort(list, new SimmetricsSort(search));
  }

  public static void sort(List<? extends ISort> list, int year, String search) {
    sortYear(list, year, new SimmetricsSort(search));
  }

  private static <T extends ISort> List<T> getByYear(List<T> list, int year) {
    List<T> res = new ArrayList<T>();
    for (T result : list) {
      if (result.getYear() == year) {
        res.add(result);
      }
    }
    list.removeAll(res);
    return res;
  }

  private static class IntegerDescending implements Comparator<Integer> {

    @Override
    public int compare(Integer t, Integer t1) {
      return t1.compareTo(t);
    }
  }

  /**
   * Sort list by YEAR, first exact YEAR then YEAR + 1 , YEAR - 1 If search is
   * not null group of YEAR are sorted by COMPARATOR (only for YEAR, YEAR + 1,
   * YEAR -1)
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
        List<T> res = getByYear(tmpList, year + i);
        if (comparator != null) {
          Collections.sort(res, comparator);
        }
        list.addAll((i == 0 ? i : list.size()), res);
      }
      //Collections.sort(tmpList, new YearSort());
    }
    list.addAll(tmpList);
  }

  private static class SimmetricsSort implements Comparator<ISort> {

    private final String search;
    private final Float accuracy;

    public SimmetricsSort(String search) {
      this.search = search;
      accuracy = 2.8F;
    }

    public SimmetricsSort(String search, Float accuracy) {
      this.search = search;
      this.accuracy = accuracy;
    }

    @Override
    public int compare(ISort t, ISort t1) {
      if (search == null) {
        return 0;
      }

      return simCompare(search, t1.getName()) - simCompare(search, t.getName());
    }

    private int simCompare(String str1, String str2) {
      str2 = StringUtils.normaliseClean(str2);
      AbstractStringMetric algorithm;
      Float res = 0.0F;
      algorithm = new Jaro();
      res += algorithm.getSimilarity(str1, str2);
      algorithm = new JaroWinkler();
      res += algorithm.getSimilarity(str1, str2);
      algorithm = new Levenshtein();
      res += algorithm.getSimilarity(str1, str2);
      algorithm = new JaccardSimilarity();
      res += algorithm.getSimilarity(str1, str2);

      return Math.round((res) * 100);
    }
  }

  private static class LanguageSort implements Comparator<ISort> {

    @Override
    public int compare(ISort t, ISort t1) {
      return t1.getLanguage().compareTo(t.getLanguage());
    }
  }

  private static class YearSort implements Comparator<ISort> {

    @Override
    public int compare(ISort t, ISort t1) {
      return t1.getYear() - t.getYear();
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
