/*
 * movie-renamer-core
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
package fr.free.movierenamer.namematcher;

import java.util.*;

/**
 * Class CommonWords
 *
 * @author Nicolas Magré
 */
abstract class CommonWords {

  /**
   * Match all result from all matcher
   *
   * @param names Matchers results
   * @param priority 
   * @return Most probable result
   */
  public static String matchAll(List<NameMatcher> names, boolean priority) {

    if (names.size() == 1) {
      return normalize(names.get(0).getMatch());
    }
    
    if(priority){
      for(NameMatcher name : names) {
        if(name.getPriority() == NameMatcher.HIGH){
          return normalize(name.getMatch());
        }
      }
    }

    List<String> allMatch = new ArrayList<String>();
    for (int i = 0; i < names.size(); i++) {
      allMatch.add(names.get(i).getMatch());
    }

    //Check if list is already as small as possible
    List<String> movieNames = getCommonWords(allMatch);
    if (movieNames == null || movieNames.isEmpty()) {
      return getSmallStringList(allMatch);
    }

    //Get list as small as possible
    List<String> tmp = getCommonWords(movieNames);
    while (tmp != null) {
      tmp = getCommonWords(tmp);
      if (tmp != null) {
        movieNames = tmp;
      }
    }

    return getSmallStringList(movieNames);
  }

  /**
   * Get list of common words in list of string separated by space character
   *
   * @param names List
   * @return List of common words or null if list is as small as possible
   */
  public static List<String> getCommonWords(List<String> names) {
    List<String> common = new ArrayList<String>();
    for (int i = 0; i < names.size(); i++) {
      for (int j = 0; j < names.size(); j++) {
        if (i == j) {
          continue;
        }

        //Retreive common words in names list and add it to common list
        String res = commonList(Arrays.asList(names.get(i).toLowerCase().split(" ")), Arrays.asList(names.get(j).toLowerCase().split(" ")));
        if (res.length() > 0) {
          common.add(normalize(res));
        }
      }
    }

    //Add name to not lose it (if names < 2 , no common words have been added)
    if (names.size() == 1) {
      common.add(normalize(names.get(0)));
    }

    //Remove duplicate string
    Set<String> set = new HashSet<String>(common);
    common = new ArrayList<String>(set);

    //Make sure list are in same order
    Collections.sort(names);
    Collections.sort(common);

    //Names list is already as small as possible
    if (names.equals(common)) {
      return null;
    }

    return common;
  }

  /**
   * Get common words between two string list
   *
   * @param list1 String list
   * @param list2 String list
   * @return String whith all common words separated by space character or empty string
   */
  private static String commonList(List<String> list1, List<String> list2) {
    StringBuilder sb = new StringBuilder();
    for (String str : list1) {
      if (list2.contains(str)) {//Add common words between the two list
        if (sb.length() != 0) {
          sb.append(" ");
        }
        sb.append(str);
      }
    }
    return sb.toString().trim();
  }

  /**
   * Normalize string
   *
   * @param str
   * @return String normalized
   */
  public static String normalize(String str) {
    str = str.replace(".", " ").replace("_", " ").replace("-", " ").trim();
    str = str.replaceAll("[,;!]", "");//Remove ponctuation
    str = str.replaceAll("\\[.*\\]", "").replaceAll("\\(.*\\)", "");//Remoave all [...] and (...)
    str = str.replaceAll("\\s+", " ");//Remove duplicate space character
    return str.trim();
  }

  /**
   * Get the small string in list
   *
   * @param list
   * @return Smaller string or empty
   */
  public static String getSmallStringList(List<String> list) {
    if (list.size() < 1) {
      return "";
    }
    Collections.sort(list, new StringLengthComparable());
    return normalize(list.get(0));
  }

  /**
   * Replace all regular expression from list in media name by void, no case sensitive
   *
   * @param mediaName Media name to filtered
   * @param replaceBy List of regular expression
   * @return Cleaned movie name by regex
   */
  public static String getFilteredName(String mediaName, List<String> replaceBy) {
    String res = mediaName.replaceAll("\\.", " ").replaceAll("_", " ");
    for (String regex : replaceBy) {
      res = res.replaceAll("(?i)" + regex, "");
    }
    return res;
  }
}
