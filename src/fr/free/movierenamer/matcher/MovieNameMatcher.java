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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class MovieNameMatcher
 *
 * @author Nicolas Magré
 */
public class MovieNameMatcher {

  private static final String MOVIENAMEBYYEAR = "\\D?(\\d{4})\\D";
  private String filename;
  private String movieYear;
  private List<String> regexs;

  public MovieNameMatcher(MediaFile mfile, List<String> regexs) {
    filename = mfile.getFile().getName();
    this.regexs = regexs;
    movieYear = "";
  }

  /**
   * Get movie name
   *
   * @return Movie name
   */
  public String getMovieName() {
    //Get all matcher values
    ArrayList<NameMatcher> names = new ArrayList<NameMatcher>();
    getMatcherRes(names, getMovieNameByYear());
    getMatcherRes(names, getMovieNameByUpperCase());
    getMatcherRes(names, getMovieNameByRegex());
    if (names.isEmpty()) {
      return CommonWords.normalize(filename.substring(0, filename.lastIndexOf(".")));
    }
    return CommonWords.matchAll(names, false);
  }

  /**
   * Get movie year
   *
   * @return
   */
  public String getYear() {
    return movieYear;
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
    }
  }

  /**
   * Try to get movie name by detecting year
   *
   * @return A matcher with all from beginning to detected year in filename or empty string
   */
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
      name = CommonWords.getFilteredName(CommonWords.normalize(name), regexs);
    }

    movieMatcher.setMatch(name);
    return movieMatcher;
  }

  /**
   * Try to get movie name by detecting first capital word
   *
   * @return A matcher with all from beginning to detected capital word in filename or empty string
   */
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
      movieMatcher.setMatch(CommonWords.getFilteredName(name, regexs));
    }
    return movieMatcher;
  }

  /**
   * Get movie name by regular expression
   *
   * @return A Matcher
   */
  private NameMatcher getMovieNameByRegex() {
    NameMatcher movieMatcher = new NameMatcher("Regex Matcher", NameMatcher.MEDIUM);
    String name = filename.substring(0, filename.lastIndexOf("."));
    name = CommonWords.getFilteredName(CommonWords.normalize(name), regexs);
    movieMatcher.setMatch(name);
    return movieMatcher;
  }
}
