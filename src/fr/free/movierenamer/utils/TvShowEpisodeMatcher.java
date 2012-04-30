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

import fr.free.movierenamer.media.tvshow.SxE;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Nicolas Magré
 */
public class TvShowEpisodeMatcher {

  private final String SxEPattern = "\\[[Ss]([0-9]+)\\]_\\[[Ee]([0-9]+)([^\\/]*)";
  private final String SxEPattern2 = "([0-9]+)x([0-9]+)([^\\/]*)";
  private final String SxEPattern3 = "[Ss]([0-9]+).?[Ee]([0-9]+)([^\\/]*)";
  private final String SxEPattern4 = "([0-9]+)([0-9][0-9])([\\._ \\-][^\\/]*)";
  private final String SxEPattern5 = "(?:(?i:season)|(?i:saison))?([0-9]+).*[Ee][Pp].?([0-9]{1,2})";
  
  private String episodeName;

  public TvShowEpisodeMatcher(String episodeName) {
    this.episodeName = normalize(episodeName);
  }

  public SxE matchEpisode() {
    return matchAll();
  }

  private SxE matchAll() {
    SxE sxe;
    ArrayList<SxE> SxEs = new ArrayList<SxE>();
    if((sxe = match(SxEPattern)) != null) SxEs.add(sxe);
    if((sxe = match(SxEPattern2)) != null) SxEs.add(sxe);
    if((sxe = match(SxEPattern3)) != null) SxEs.add(sxe);
    if((sxe = match(SxEPattern4)) != null) SxEs.add(sxe);
    if((sxe = match(SxEPattern5)) != null) SxEs.add(sxe);
    if(SxEs.isEmpty()) return new SxE();
    return SxEs.get(0);
  }

  private SxE match(String EPpattern) {
    Pattern pattern = Pattern.compile(EPpattern);
    Matcher matcher = pattern.matcher(episodeName);
    if(matcher.find()){
      String season = matcher.group(1);
      String episode = matcher.group(2);
      int S,E;
      S = Utils.isDigit(season) ? Integer.parseInt(season):-1;
      E = Utils.isDigit(episode) ? Integer.parseInt(episode):-1;
      if(S != -1 || E != -1) return new SxE(S, E);
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
