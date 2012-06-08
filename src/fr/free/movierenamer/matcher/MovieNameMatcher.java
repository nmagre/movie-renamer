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
package fr.free.movierenamer.matcher;

import fr.free.movierenamer.media.MediaFile;
import fr.free.movierenamer.utils.Utils;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Nicolas Magré
 */
public class MovieNameMatcher {

  private static final String MOVIENAMEBYYEAR = "\\D?(\\d{4})\\D";
  private boolean DEBUG = false;
  private String filename;
  private String movieYear;
  private List<String> regexs;

  public MovieNameMatcher(MediaFile mfile, String[] regex) {
    filename = mfile.getFile().getName();
    regexs = Arrays.asList(regex);
    movieYear = "";
  }

  public MovieNameMatcher(MediaFile mfile, String[] regex, boolean debug) {
    filename = mfile.getFile().getName();
    regexs = Arrays.asList(regex);
    movieYear = "";
    DEBUG = debug;
  }

  public String getMovieName() {
    //Get all matcher values
    ArrayList<NameMatcher> names = new ArrayList<NameMatcher>();
    getMatcherRes(names, getMovieNameByYear());
    getMatcherRes(names, getMovieNameByUpperCase());
    getMatcherRes(names, getMovieNameByRegex());
    if (names.isEmpty()) {
      return CommonWords.normalize(filename.substring(0, filename.lastIndexOf(".")) + (movieYear.equals("") ? "" : " " + movieYear));
    }
    return matchAll(names);
  }

  private String matchAll(ArrayList<NameMatcher> names) {//A refaire , c'est nimp

    if (names.size() == 1) {
      return CommonWords.normalize(names.get(0).getMatch() + (movieYear.equals("") ? "" : " " + movieYear));
    }

    ArrayList<String> allMatch = new ArrayList<String>();
    for (int i = 0; i < names.size(); i++) {
      allMatch.add(names.get(i).getMatch());
    }

    //Check if list is already as small as possible
    List<String> movieNames = CommonWords.getCommonWords(allMatch);
    if (movieNames == null || movieNames.isEmpty()) {
      return getFistName(allMatch);
    }

    //Get list as small as possible
    List<String> tmp = CommonWords.getCommonWords(movieNames);
    while (tmp != null) {
      tmp = CommonWords.getCommonWords(tmp);
      if (tmp != null) {
        movieNames = tmp;
      }
    }

    return getFistName(movieNames);
  }

  /**
   * Get the small string in list
   *
   * @param list
   * @return Smaller string or empty
   */
  private String getFistName(List<String> list) {
    if (list.size() < 1) {
      return "";
    }
    Collections.sort(list, new MovieNameMatcher.MyStringLengthComparable());
    return CommonWords.normalize(list.get(0)) + (movieYear.equals("") ? "" : " " + movieYear);
  }

  /**
   * Add tvShow matcher to matcher list if a result is found
   *
   * @param matchResults List of matcher
   * @param tvshowMatcher Matcher to add
   */
  private void getMatcherRes(List<NameMatcher> matchResults, NameMatcher movieMatcher) {
    if (movieMatcher.found()) {
      matchResults.add(movieMatcher);
      if (DEBUG) {
        System.out.println("    " + movieMatcher);
      }
    }
  }

  private NameMatcher getMovieNameByYear() {
    NameMatcher movieMatcher = new NameMatcher("Year Matcher", NameMatcher.MEDIUM);
    String name = "";
    Pattern pattern = Pattern.compile(MOVIENAMEBYYEAR);
    Matcher matcher = pattern.matcher(filename);
    while (matcher.find()) {
      String syear = matcher.group(1);
      int year = Integer.parseInt(syear);
      if (year >= 1900 && year <= Calendar.getInstance().get(Calendar.YEAR)) {
        int index = filename.indexOf(matcher.group(0));
        name = filename.substring(0, index);
        movieYear = syear;
      }
    }

    if (!name.equals("")) {
      name = Utils.getFilteredName(CommonWords.normalize(name), regexs);
    }

    movieMatcher.setMatch(name);
    return movieMatcher;
  }

  private NameMatcher getMovieNameByUpperCase() {
    NameMatcher movieMatcher = new NameMatcher("UpperCase Matcher", NameMatcher.LOW);
    String name = CommonWords.normalize(filename.substring(0, filename.lastIndexOf(".")));
    String[] words = name.split(" ");
    String end = "";
    for (String word : words) {
      String mword = word.replace(":", "");
      if (Utils.isUpperCase(mword) && !Utils.isDigit(mword) && mword.length() > 1) {
        end = word;
        break;
      }
    }

    if (!end.equals("")) {
      name = name.substring(0, name.indexOf(end));
      movieMatcher.setMatch(Utils.getFilteredName(name, regexs));
    }
    return movieMatcher;
  }

  private NameMatcher getMovieNameByRegex() {
    NameMatcher movieMatcher = new NameMatcher("Regex Matcher", NameMatcher.MEDIUM);
    String name = filename.substring(0, filename.lastIndexOf("."));
    name = Utils.getFilteredName(CommonWords.normalize(name), regexs);
    movieMatcher.setMatch(name);
    return movieMatcher;
  }

  /**
   * class MyStringLengthComparable , compare two string length
   */
  private static class MyStringLengthComparable implements Comparator<String> {

    @Override
    public int compare(String s1, String s2) {
      if (s1.length() == 2) {
        return 1;
      }
      if (s2.length() == 2) {
        return -1;
      }
      return s1.length() - s2.length();
    }
  }
}
