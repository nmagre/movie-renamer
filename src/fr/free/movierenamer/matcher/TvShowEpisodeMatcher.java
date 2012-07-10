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

import fr.free.movierenamer.media.tvshow.SxE;
import fr.free.movierenamer.utils.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class TvShowEpisodeMatcher , Retreive episode and season of tv Show
 *
 * @author Nicolas Magré
 */
public class TvShowEpisodeMatcher {

  public static final String seasonPattern = "(?:(?:season)|(?:saison)|(?:s)).?([0-9]{1,2})";
  public static final String episodePattern = "(?:(?:(?:[eé]p)|(?:[eé]pisode)) ([0-9]{1,2}))|(?:[^ ]([0-9]{1,2})[ -_])";
  public static final String SxEPattern = "([0-9]{1,2})x([0-9]{1,2})\\D";
  public static final String SxEPattern2 = "s([0-9]{1,2}).?[eé]([0-9]{1,2})";
  public static final String SxEPattern3 = "([0-9]+)([0-9][0-9])([\\._ \\-][^\\/]*)";
  public static final String SxEPattern4 = "(?:(?:season)|(?:saison)).?([0-9]{1,2}).*[eé]p.?([0-9]{1,2})";
  public static final String SxEPattern5 = "(?:(?:season)|(?:saison)).?([0-9]{1,2}).*(?:[eé]pisode).?([0-9]{1,2})";
  public static final String SxEPattern6 = "s([0-9]{1,2}).*[ée]pisode.?\\D?([0-9]{1,2})";
  public static final String SxEPattern7 = "([0-9]{2}) ([0-9]{2})";
  private String episodeName;
  private String parentFolder;

  public TvShowEpisodeMatcher(String episodeName) {
    if (episodeName.contains(File.separator)) {
      parentFolder = episodeName.substring(0, episodeName.lastIndexOf(File.separator)).toLowerCase();
      this.episodeName = normalize(episodeName.substring(episodeName.lastIndexOf(File.separator) + 1));
    } else {
      this.episodeName = normalize(episodeName);
    }
  }

  /**
   * Retreive season and episode
   *
   * @return SxE
   */
  public SxE matchEpisode() {
    return matchAll();
  }

  /**
   * Try to get the most probable match between all matches result
   *
   * @return SxE
   */
  private SxE matchAll() {
    SxE sxe;
    ArrayList<SxE> SxEs = new ArrayList<SxE>();
    if ((sxe = match(SxEPattern)) != null) {
      SxEs.add(sxe);
    }
    if ((sxe = match(SxEPattern2)) != null) {
      SxEs.add(sxe);
    }
    if ((sxe = match(SxEPattern3)) != null) {
      SxEs.add(sxe);
    }
    if ((sxe = match(SxEPattern4)) != null) {
      SxEs.add(sxe);
    }
    if ((sxe = match(SxEPattern5)) != null) {
      SxEs.add(sxe);
    }
    if ((sxe = match(SxEPattern6)) != null) {
      SxEs.add(sxe);
    }
    if ((sxe = match(SxEPattern7)) != null) {
      SxEs.add(sxe);
    }

    //If no result, try to match season and episode separately
    if (SxEs.isEmpty()) {
      System.out.println("No Match Found, Try To match Separately");
      sxe = new SxE();
      Pattern pattern = Pattern.compile(seasonPattern);
      Matcher matcher = pattern.matcher(parentFolder == null ? episodeName : parentFolder);
      if (matcher.find()) {
        String season = matcher.group(1);
        sxe.setSeason(Utils.isDigit(season) ? Integer.parseInt(season) : -1);
      }

      pattern = Pattern.compile(episodePattern);
      matcher = pattern.matcher(episodeName);
      if (matcher.find()) {
        String episode = matcher.group(1) == null ? matcher.group(2) : matcher.group(1);
        sxe.setEpisode(Utils.isDigit(episode) ? Integer.parseInt(episode) : -1);
      }
      return sxe;
    }

    ArrayList<SxE> completeMatch = new ArrayList<SxE>();
    ArrayList<SxE> partialMatch = new ArrayList<SxE>();

    //Separe complete match and partial match (partial match will be empty in almost all cases)
    for (SxE match : SxEs) {
      System.out.println("Match : " + sxe);
      if (match.isValid()) {
        completeMatch.add(match);
      } else if (match.isPartial()) {
        partialMatch.add(match);
      }
    }

    //If no complete match, try to make a complete match with partial match
    if (completeMatch.isEmpty() && partialMatch.size() > 1) {
      SxE match = new SxE();
      for (SxE mSxE : partialMatch) {
        if (match.getEpisode() == -1 && mSxE.getEpisode() != -1) {
          match.setEpisode(mSxE.getEpisode());
        }
        if (match.getSeason() == -1 && mSxE.getSeason() != -1) {
          match.setSeason(mSxE.getSeason());
        }
        if (match.isValid()) {
          break;
        }
      }
      return match;
    }

    if (completeMatch.size() == 1) {
      return completeMatch.get(0);
    }

    //Try to get the most probable match
    if (completeMatch.size() > 1) {
      SxE fMatch = completeMatch.get(0);
      boolean different = false;
      for (SxE match : completeMatch) {
        if (!fMatch.equals(match)) {
          different = true;
        }
      }
      if (!different) {
        return fMatch;
      }
      return getSxE(completeMatch);
    }

    return partialMatch.isEmpty() ? SxEs.get(0) : partialMatch.get(0);
  }

  /**
   * Try to match season and episode in fileName
   *
   * @param EPpattern Season/Episode patter
   * @return SxE
   */
  private SxE match(String EPpattern) {
    Pattern pattern = Pattern.compile(EPpattern);
    Matcher matcher = pattern.matcher(episodeName);
    if (matcher.find()) {
      String season = matcher.group(1);
      String episode = matcher.group(2);
      String match = matcher.group(1) + matcher.group(2);

      int S, E;
      S = Utils.isDigit(season) ? Integer.parseInt(season) : -1;
      E = Utils.isDigit(episode) ? Integer.parseInt(episode) : -1;

      if (E == 0 && Utils.isDigit(season)) {//Absolute number ?
        S = Integer.parseInt(season + episode);
        E = 0;
      }

      if (S != -1 || E != -1) {
        return new SxE(S, E, match);
      }
    }
    return null;
  }

  /**
   * Get the most probable season and episode by occurrence number
   *
   * @param SxEs List of SxE
   * @return SxE
   */
  private SxE getSxE(ArrayList<SxE> SxEs) {
    SxE sxe = new SxE();
    Map<Integer, Integer> seasonMatch = new LinkedHashMap<Integer, Integer>();
    Map<Integer, Integer> episodeMatch = new LinkedHashMap<Integer, Integer>();
    for (SxE tmp : SxEs) {
      if (tmp.getSeason() != -1) {
        if (seasonMatch.containsKey(tmp.getSeason())) {
          int count = seasonMatch.get(tmp.getSeason()).intValue();
          seasonMatch.remove(tmp.getSeason());
          seasonMatch.put(tmp.getSeason(), count++);
        } else {
          seasonMatch.put(tmp.getSeason(), 1);
        }
      }

      if (tmp.getEpisode() != -1) {
        if (episodeMatch.containsKey(tmp.getEpisode())) {
          int count = episodeMatch.get(tmp.getEpisode()).intValue();
          episodeMatch.remove(tmp.getEpisode());
          episodeMatch.put(tmp.getEpisode(), count++);
        } else {
          episodeMatch.put(tmp.getEpisode(), 1);
        }
      }

      sxe.setSeason(getMostProbableNumber(seasonMatch));
      sxe.setEpisode(getMostProbableNumber(episodeMatch));
    }
    return sxe;
  }

  /**
   * Get the most encountered value in Map
   *
   * @param map Map
   * @return Key or -1
   */
  private int getMostProbableNumber(Map<Integer, Integer> map) {
    if (map.isEmpty()) {
      return -1;
    }
    return getKeyByValue(map, Collections.max(map.values()));
  }

  /**
   * Get key by value
   *
   * @param map Map
   * @param value Value to reteive key
   * @return Key or null
   */
  public static Integer getKeyByValue(Map<Integer, Integer> map, Integer value) {
    for (Entry<Integer, Integer> entry : map.entrySet()) {
      if (value.equals(entry.getValue())) {
        return entry.getKey();
      }
    }
    return null;
  }

  /**
   * Normalize tvShow fileName
   *
   * @param str
   * @return String normalized
   */
  private String normalize(String str) {
    str = str.substring(0, str.lastIndexOf("."));//Remove extension
    str = str.replace(".", " ").replace("_", " ").replace("-", " ").trim();
    str = str.replaceAll("[,;:!]", "");//Remove ponctuation
    str = str.replaceAll("\\s+", " ");//Remove duplicate space character
    return str.toLowerCase();
  }
}
