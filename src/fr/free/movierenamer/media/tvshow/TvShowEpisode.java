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
package fr.free.movierenamer.media.tvshow;

import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class TvShowEpisode
 * @author Nicolas Magré
 */
public class TvShowEpisode {
  
  private int num;
  private String title;
  private String originalTitle;
  private String rating;
  private String votes;
  private String synopsis;
  private List<MediaID> tvshowIDs;
  private TvShowSeason season;

  public TvShowEpisode() {
    this(null, -1);
  }

  public TvShowEpisode(TvShowSeason season, int num) {
    this.season = season;
    this.num = num;
    title = "?";
    originalTitle = "";
    rating = "";
    votes = "";
    synopsis = "";
    tvshowIDs = new ArrayList<MediaID>();
  }
  
  public int getNum() {
    return num;
  }
  
  public String getTitle() {
    return title;
  }

  public String getOriginalTitle() {
    return originalTitle;
  }
  

  public String getRating() {
    return rating;
  }

  public String getVotes() {
    return votes;
  }

  public List<MediaID> getIDs() {
    return tvshowIDs;
  }

  /**
   * Add tvshow API id
   *
   * @param id Movie APi id
   */
  public void addID(MediaID id) {
    for (MediaID mID : tvshowIDs) {
      if (mID.equals(id)) {
        return;
      }
    }
    tvshowIDs.add(id);
  }  
  
  public void setNum(int num) {
    this.num = num;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setOriginalTitle(String originalTitle) {
    this.originalTitle = originalTitle;
  }

  public void setRating(String rating) {
    this.rating = rating;
  }

  public void setVotes(String votes) {
    this.votes = votes;
  }
  
  public static void sortEpisodes(List<TvShowEpisode> episodes) {
    Collections.sort(episodes, new Comparator<TvShowEpisode>() {

      @Override
      public int compare(TvShowEpisode o1, TvShowEpisode o2) {
        return o1.getNum() - o2.getNum();
      }
    });
  }

  @Override
  public String toString() {
    String res = "Title : " + title + Utils.ENDLINE;
    res += "Original title : " + originalTitle + Utils.ENDLINE;
    res += "Rating :" + rating + Utils.ENDLINE;
    res += "Votes :" + votes + Utils.ENDLINE;
    return res;
  }

  /**
   * @return the synopsis
   */
  public String getSynopsis() {
    return synopsis;
  }

  /**
   * @param synopsis the synopsis to set
   */
  public void setSynopsis(String synopsis) {
    this.synopsis = synopsis;
  }

  public TvShowSeason getSeason() {
    return this.season;
  }
  
  /**
   * @param season the season to set
   */
  public void setSeason(TvShowSeason season) {
    this.season = season;
  }
}
