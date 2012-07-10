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

/**
 *
 * @author Nicolas Magré
 */
public class TvShowSeason {

  private int num;
  private MediaID tvshowIDs;
  private ArrayList<TvShowEpisode> episodes;
  private int episodeCount;

  public TvShowSeason(int num) {
    this.num = num;
    episodes = new ArrayList<TvShowEpisode>();
    episodeCount = 0;
  }

  public TvShowSeason(MediaID tvshowIDs) {
    num = 0;
    this.tvshowIDs = tvshowIDs;
    episodes = new ArrayList<TvShowEpisode>();
    episodeCount = 0;
  }

  public int getNum() {
    return num;
  }

  public MediaID getID() {
    return tvshowIDs;
  }

  public ArrayList<TvShowEpisode> getEpisodes() {
    return episodes;
  }

  public int getEpisodeCount() {
    return episodeCount == 0 ? episodes.size() : episodeCount;
  }

  public void setNum(int num) {
    this.num = num;
  }

  public void setId(MediaID id) {
    tvshowIDs = id;
  }

  public void addEpisode(TvShowEpisode episode) {
    episodes.add(episode);
  }

  public void setEpisodeCount(int episodeCount) {
    this.episodeCount = episodeCount;
  }

  public void setEpisodes(ArrayList<TvShowEpisode> episodes) {
    this.episodes = episodes;
  }

  public static void sortSeasons(ArrayList<TvShowSeason> seasons) {
    Collections.sort(seasons, new Comparator<TvShowSeason>() {

      @Override
      public int compare(TvShowSeason o1, TvShowSeason o2) {
        return o1.getNum() - o2.getNum();
      }
    });
  }

  @Override
  public String toString() {
    String res = "Season : " + num + " => " + getEpisodeCount() + " Episodes" + Utils.ENDLINE;
    for (TvShowEpisode episode : episodes) {
      res += "  " + episode.getNum() + " : " + (episode.getTitle().equals("") ? episode.getOriginalTitle() : episode.getTitle()) + Utils.ENDLINE;
      //res += "    " + episode.getPlot() + Utils.ENDLINE;
    }
    return res;
  }
}
