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

import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.searchinfo.SearchResult;
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

  /**
   *
   * @param <T>
   * @param list
   * @param search
   * @param year
   * @param threshold
   */
  public static <T extends SearchResult> void sortMedia(List<T> list, String search, int year, float threshold) {

    final String toCompare = StringUtils.normaliseClean(search);
    Map<Float, List<T>> values = new TreeMap<>(Collections.reverseOrder());
    for (T media : list) {

      // If year is (almost) the same, we add a "bonus"
      float bonus = 0;
      if (NumberUtils.isYearValid(year)) {
        final int oYear = media.getYear();
        if (year == oYear) {
          bonus = 0.1875F;
        } else if (Math.abs(oYear - year) == 1) {
          bonus = 0.125F;
        }
      }

      // if there is an image we add a "bonus"
      if (media.hasImage()) {
        bonus += 0.025F;
      }

      // Get best similarity between title and orig title
      float sim = getSimilarity(toCompare, media.getName());
      if (media.getOriginalName() != null && media.getName().equals(media.getOriginalName())) {
        sim = Math.max(sim, getSimilarity(toCompare, media.getOriginalName()));
      }
      sim += bonus;

      // We use a list cause 2 (or more) can have the same "sim" number
      List<T> listObj = values.get(sim);
      if (listObj == null) {
        listObj = new ArrayList<>();
        values.put(sim, listObj);
      }

      listObj.add(media);
    }

    // Get the higher "sim number"
    double maxSim = 0;
    if (!values.isEmpty()) {
      maxSim = values.keySet().iterator().next();
    }

    // If "sim number" is greater than threshold we sort the list
    if (maxSim >= threshold) {
      list.clear();
      for (List<T> sortedList : values.values()) {
        list.addAll(sortedList);
      }
    }
  }

  private static float getSimilarity(String search, String str) {
    String toCompare = StringUtils.normaliseClean(str);// Clean the string to get best result (search is already cleaned)
    AbstractStringMetric algorithm;

    Float res = 0.0F;
    algorithm = new Jaro();
    res += algorithm.getSimilarity(search, toCompare);
    algorithm = new JaroWinkler();
    res += algorithm.getSimilarity(search, toCompare);
    algorithm = new Levenshtein();
    res += algorithm.getSimilarity(search, toCompare);
    algorithm = new JaccardSimilarity();
    res += algorithm.getSimilarity(search, toCompare);

    return res / 4;
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
        break;
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
    List<T> res = new ArrayList<>();
    for (T result : list) {
      if (result.getYear() == year) {
        res.add(result);
      }
    }
    list.removeAll(res);
    return res;
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
    List<T> tmpList = new ArrayList<>(list);
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

  private static class YearSort<T extends ISort> implements Comparator<T> {

    @Override
    public int compare(ISort t, ISort t1) {
      return -Integer.compare(t.getYear(), t1.getYear());
    }

  }

  private static class YearDiffSort<T extends ISort> implements Comparator<T> {

    private final int year;

    public YearDiffSort(int year) {
      this.year = year;
    }

    @Override
    public int compare(ISort t, ISort t1) {
      return Integer.compare(Math.abs(t.getYear() - year), Math.abs(t1.getYear() - year));
    }

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
