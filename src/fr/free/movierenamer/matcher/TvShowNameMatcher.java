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
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class TvShowNameMatcher
 *
 * @author Nicolas Magré
 */
public class TvShowNameMatcher {

  private MediaFile mfile;
  private static final String SEASONFOLDERPATTERN = "(?i:season)|(?i:saison)|(?i:s).*\\d+";
  private static final String TVSHOWFOLDERPATTERN = ".*(?i:tvshwow)|(?i:tv)|(?i:serie)|(?i:série).*";
  private static final String TVSHOWNAMEBYEPISODE = "(([sS]\\d++\\?\\d++)|(\\d++x\\d++.?\\d++x\\d++)|(\\d++[eE]\\d\\d)|([sS]\\d++.[eE]\\d++)|(\\d++x\\d++)|(\\d++x\\d++.?\\d++\\?\\d++))";
  private NameMatcher folderMatch;
  private NameMatcher episodeMatch;
  private NameMatcher commonFileMatch;
  private final boolean DEBUG = true;

  public TvShowNameMatcher(MediaFile mfile) {
    this.mfile = mfile;
    folderMatch = episodeMatch = commonFileMatch = null;
  }

  /**
   * Get tvShowName
   *
   * @return TvShow name or empty string if no name found
   */
  public String getTvShowName() {
    if (DEBUG) {
      System.out.println("File : " + mfile.getFile().getName());
    }
    if (DEBUG) {
      System.out.println("  Match :");
    }

    TvShowEpisodeMatcher tvEpMacther = new TvShowEpisodeMatcher(mfile.getFile().getName());
    tvEpMacther.matchEpisode();
    
    //Get all matcher values
    ArrayList<NameMatcher> names = new ArrayList<NameMatcher>();
    getMatcherRes(names, (folderMatch = matchByFolderName()));
    getMatcherRes(names, (episodeMatch = matchByEpisode()));
    getMatcherRes(names, (commonFileMatch = matchByCommonSeqFileName()));
    if(names.isEmpty()) {
      return CommonWords.normalize(mfile.getFile().getName().substring(0, mfile.getFile().getName().lastIndexOf(".") + 1));
    }
    return matchAll(names);
  }

  /**
   * Add tvShow matcher to matcher list if a result is found
   *
   * @param matchResults List of matcher
   * @param tvshowMatcher Matcher to add
   */
  private void getMatcherRes(List<NameMatcher> matchResults, NameMatcher tvshowMatcher) {
    if (tvshowMatcher.found()) {
      matchResults.add(tvshowMatcher);
      if (DEBUG) {
        System.out.println("    " + tvshowMatcher);
      }
    }
  }

  /**
   * Match all matcher results and try to retreive tvshow name
   *
   * @return Matched tvShow name or empty string if no name found
   */
  private String matchAll(ArrayList<NameMatcher> names) {//A refaire , c'est nimp

    if (names.size() < 2) {
      return names.get(0).getMatch();
    }

    //Look if we got a winner
    if (names.size() == 2) {
      if (folderMatch.getMatch().length() > 0 && commonFileMatch.getMatch().length() > 0) { //Prefer folder match
        return CommonWords.normalize(folderMatch.getMatch());
      }
    } else {
      ArrayList<String> namesArray = new ArrayList<String>();
      ArrayList<Integer> countArray = new ArrayList<Integer>();
      for (NameMatcher name : names) {
        if (namesArray.contains(name.getMatch())) {
          int pos = namesArray.indexOf(name.getMatch());
          int val = countArray.get(pos);
          countArray.set(pos, val++);
        } else {
          namesArray.add(name.getMatch());
          countArray.add(1);
        }
      }

      int pos = -1;
      int val = -1;
      for (int i = 0; i < countArray.size(); i++) {
        int value = countArray.get(i);
        if (val < value && value > 1) {
          val = value;
          pos = i;
        }
      }
      if (pos != -1) {
        return CommonWords.normalize(namesArray.get(pos));
      }
    }

    ArrayList<String> allMatch = new ArrayList<String>();
    for (int i = 0; i < names.size(); i++) {
      allMatch.add(names.get(i).getMatch());
    }

    //Check if list is already as small as possible
    List<String> tvShowNames = CommonWords.getCommonWords(allMatch);
    if (tvShowNames == null || tvShowNames.isEmpty()) {
      return getSmallStringList(allMatch);
    }

    //Get list as small as possible
    List<String> tmp = CommonWords.getCommonWords(tvShowNames);
    while (tmp != null) {
      tmp = CommonWords.getCommonWords(tmp);
      if (tmp != null) {
        tvShowNames = tmp;
      }
    }

    if (DEBUG) {
      System.out.println("    Match All tvshow potential match : " + tvShowNames.toString());
    }

    //Prefer get folder match
    if (folderMatch.getMatch().length() > 0 && tvShowNames.size() >= 2) {
      for (String name : tvShowNames) {
        if (name.equals(folderMatch.getMatch())) {
          return CommonWords.normalize(name);
        }
      }
    }

    if (tvShowNames.size() == 2) {
      if (tvShowNames.get(0).contains(tvShowNames.get(1))) {
        return tvShowNames.get(0);
      }
      if (tvShowNames.get(1).contains(tvShowNames.get(0))) {
        return tvShowNames.get(1);
      }
    }

    return getSmallStringList(tvShowNames);
  }

  /**
   * Match tvShow Name by detecting episode
   *
   * @return A string from beginning to the episode detection in media filename or empty if no episode found
   */
  private NameMatcher matchByEpisode() {

    NameMatcher episodeMatcher = new NameMatcher("Episode Matcher", NameMatcher.MEDIUM);
    String name = mfile.getFile().getName();
    Pattern pattern = Pattern.compile(TVSHOWNAMEBYEPISODE);
    Matcher matcher = pattern.matcher(name);
    if (matcher.find()) {//Match episode in fileName
      name = name.substring(0, name.indexOf(matcher.group(0)));
    } else {
      name = "";
    }
    episodeMatcher.setMatch(CommonWords.normalize(name));
    return episodeMatcher;
  }

  /**
   * Match tvShow Name by common sequence between other files from the same directory
   *
   * @return String with all common words from other files or empty
   */
  private NameMatcher matchByCommonSeqFileName() {

    NameMatcher commonMatcher = new NameMatcher("Common sequence in files matcher", NameMatcher.LOW);
    File file = mfile.getFile().getParentFile();
    File[] files = file.listFiles(new FileFilter() {//Retreive all file that seems to be a tvShow in parent folder

      @Override
      public boolean accept(File file) {
        if (!file.getName().contains(".")) {
          return false;
        }
        if (mfile.getFile().equals(file)) {
          return false;
        }
        String name = file.getName();
        Pattern pattern = Pattern.compile(TVSHOWNAMEBYEPISODE);
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
          return true;
        }
        return false;
      }
    });
 
    if (files == null || files.length < 2) {
      return commonMatcher;
    }

    //Add all words from fileName in list
    ArrayList<String> names = new ArrayList<String>();
    for (File f : files) {
      String name = f.getName().substring(0, f.getName().lastIndexOf(".") + 1);
      names.add(CommonWords.normalize(name));
    }

    //Check if list is as small as possible
    List<String> tvShowNames = CommonWords.getCommonWords(names);
    if (tvShowNames == null) {
      return commonMatcher;
    }

    //Get list as small as possible
    List<String> tmp = CommonWords.getCommonWords(tvShowNames);
    while (tmp != null) {
      tmp = CommonWords.getCommonWords(tmp);
      if (tmp != null) {
        tvShowNames = tmp;
      }
    }

    //If small string result is in folderName, prefer to keep folder match
    String res = getSmallStringList(tvShowNames);
    if (folderMatch != null && folderMatch.getMatch().contains(res)) {
      commonMatcher.setMatch(folderMatch.getMatch());
      return commonMatcher;
    }

    commonMatcher.setMatch(CommonWords.normalize(res));
    return commonMatcher;
  }

  /**
   * Match tvShow by apply regex on fileName
   *
   * @return Parent folder name (or is parent) or empty string if it not seems to be the tvShow name
   */
  private NameMatcher matchByRegEx() {
    return null;
  }

  /**
   * Match tvShow by parent folder name
   *
   * @return Parent folder name (or is parent) or empty string if it not seems to be the tvShow name
   */
  private NameMatcher matchByFolderName() {
    NameMatcher folderNameMatcher = new NameMatcher("Folder Name Macther", NameMatcher.HIGH);
    String res = "";
    final File mediafile = mfile.getFile();
    if (mediafile.getParent() != null) {
      Pattern pattern = Pattern.compile(SEASONFOLDERPATTERN);
      Matcher matcher = pattern.matcher(mediafile.getParent().substring(mediafile.getParent().lastIndexOf(File.separator) + 1));
      if (matcher.find()) {//Parent folder looks like : Season 5,s3,saison 12,...
        if (mediafile.getParentFile().getParent() != null) {//If parent folder looks like a season folder, parent folder of season folder is probably the tvshow name
          res = getTvShowFolderName(mediafile.getParentFile().getParentFile());
        }
      } else {
        File[] files = mediafile.getParentFile().listFiles(new FileFilter() {//Get all files that xontains parent folder name in fileName

          @Override
          public boolean accept(File file) {
            if (file.getName().contains(mediafile.getParentFile().getName())) {
              return true;
            }
            return false;
          }
        });

        if (files != null && files.length > 0) {//If other fileName have parent folderName in their filename, parent folder is probably the tvShow name
          res = getTvShowFolderName(mediafile.getParentFile());
        }
      }
    }

    folderNameMatcher.setMatch(res);
    return folderNameMatcher;
  }

  /**
   * Get tvShow folder name
   *
   * @param file Parent tvshow file
   * @return TvShow folderName or empty if file is a root filesystem or if it not seems to be the tvShow title
   */
  private String getTvShowFolderName(File parentFile) {
    String res = "";
    if (!Utils.isRootDir(parentFile)) {
      String parent = parentFile.getName().substring(parentFile.getName().lastIndexOf(File.separator) + 1);
      Pattern pattern = Pattern.compile(TVSHOWFOLDERPATTERN);
      Matcher matcher = pattern.matcher(parent);
      if (!matcher.find()) {//Check if folderName is not a tvshowName
        res = parent;
      }
    }
    return res.toLowerCase();
  }

  /**
   * Get the small string in list
   *
   * @param list
   * @return Smaller string or empty
   */
  private String getSmallStringList(List<String> list) {
    if (list.size() < 1) {
      return "";
    }
    Collections.sort(list, new MyStringLengthComparable());
    return CommonWords.normalize(list.get(0));
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