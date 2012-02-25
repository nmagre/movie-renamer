/******************************************************************************
 *                                                                             *
 *    Movie Renamer                                                            *
 *    Copyright (C) 2012 Magré Nicolas                                         *
 *                                                                             *
 *    Movie Renamer is free software: you can redistribute it and/or modify    *
 *    it under the terms of the GNU General Public License as published by     *
 *    the Free Software Foundation, either version 3 of the License, or        *
 *    (at your option) any later version.                                      *
 *                                                                             *
 *    This program is distributed in the hope that it will be useful,          *
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            *
 *    GNU General Public License for more details.                             *
 *                                                                             *
 *    You should have received a copy of the GNU General Public License        *
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                             *
 ******************************************************************************/
package fr.free.movierenamer.movie;

import fr.free.movierenamer.utils.ActionNotValidException;
import java.util.ArrayList;
import fr.free.movierenamer.utils.Utils;

/**
 *
 * @author duffy
 */
public class MovieInfo {

  private String title;
  private String movieDBId;
  private String trailer;
  private String synopsis;
  private String tagline;
  private String origTitle;
  private String rating;
  private int runtime;
  private String year;
  private String votes;
  private String set;//Saga
  private ArrayList<String> genres;
  private ArrayList<String> studios;
  private ArrayList<MoviePerson> actors;
  private ArrayList<String> countries;

  public MovieInfo() {
    title = "";
    movieDBId = "";
    trailer = "";
    synopsis = "";
    tagline = "";
    origTitle = "";
    rating = "-1";
    runtime = -1;
    year = "";
    votes = "-1";
    set = "";
    genres = new ArrayList<String>();
    studios = new ArrayList<String>();
    actors = new ArrayList<MoviePerson>();
    countries = new ArrayList<String>();
  }

  public String getTitle() {
    return title;
  }

  public String getMovieDBId() {
    return movieDBId;
  }

  public String getTrailer() {
    return trailer;
  }

  public String getSynopsis() {
    return synopsis;
  }

  public String getTagline() {
    return tagline;
  }

  public String getOrigTitle() {
    return origTitle;
  }

  public String getRating() {
    return rating;
  }

  public int getRuntime() {
    return runtime;
  }

  public String getYear() {
    return year;
  }

  public String getVotes() {
    return votes;
  }

  public String getSet() {
    return set;
  }

  public ArrayList<String> getGenres() {
    return genres;
  }

  public ArrayList<String> getStudios() {
    return studios;
  }

  public ArrayList<MoviePerson> getActors() {
    return actors;
  }

  public ArrayList<String> getCountries() {
    return countries;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setMovieDBId(String movieDBId) {
    this.movieDBId = movieDBId;
  }

  public void setTrailer(String trailer) {
    this.trailer = trailer;
  }

  public void setSynopsis(String synopsis) {
    this.synopsis = synopsis;
  }

  public void setTagline(String tagline) {
    this.tagline = tagline;
  }

  public void setOrigTitle(String origTitle) {
    this.origTitle = origTitle;
  }

  public void setRating(String rating) {
    this.rating = rating;
  }

  public void setRuntime(int runtime) {
    this.runtime = runtime;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public void setVotes(String votes) {
    this.votes = votes;
  }
  
  public void setSet(String set) {
    this.set = set;
  }

  public void addGenre(String genre) {
    genres.add(genre);
  }

  public void addStudio(String studio) {
    studios.add(studio);
  }

  public void addActor(MoviePerson actor) {
    actors.add(actor);
  }

  public void addCountry(String country) {
    countries.add(country);
  }

  public MoviePerson getActorByName(String actor) {
    MoviePerson res = null;
    for (int i = 0; i < actors.size(); i++) {
      if (actors.get(i).getName().equals(actor)) {
        res = actors.get(i);
        break;
      }
    }
    return res;
  }

  public void addRole(String actor, String role) throws ActionNotValidException {
    for (int i = 0; i < actors.size(); i++) {
      if (actors.get(i).getName().equals(actor)) {
        actors.get(i).addRole(role);
        break;
      }
    }
  }

  public String getDirectors() {
    StringBuilder res = new StringBuilder();
    for (int i = 0; i < actors.size(); i++) {
      if (actors.get(i).getJob() == MoviePerson.DIRECTOR)
        res.append(" | ").append(actors.get(i).getName());
    }
    if (res.length() == 0) res.delete(0, 2);
    return res.toString();
  }

  public String getWriters() {//A refaire (string concat in loop)
    String res = "";
    for (int i = 0; i < actors.size(); i++) {
      if (actors.get(i).getJob() == MoviePerson.WRITER)
        res += " | " + actors.get(i).getName();
    }
    if (!res.equals(Utils.EMPTY)) res = res.substring(3);
    return res;
  }

  public String getFirstDirector() {
    String res = "";
    for (int i = 0; i < actors.size(); i++) {
      if (actors.get(i).getJob() == MoviePerson.DIRECTOR)
        return actors.get(i).getName();
    }
    return res;
  }

  public String getGenresString() {//A refaire (string concat in loop)
    String res = "";
    for (int i = 0; i < genres.size(); i++) {
      res += " | " + genres.get(i);
    }
    if (!res.equals(Utils.EMPTY)) res = res.substring(3);
    return res;
  }

  public String getCountriesString() {//A refaire (string concat in loop)
    String res = "";
    for (int i = 0; i < countries.size(); i++) {
      res += " | " + countries.get(i);
    }
    if (!res.equals(Utils.EMPTY)) res = res.substring(3);
    return res;
  }

  public String getFirstGenreString() {
    String res = "";
    for (int i = 0; i < genres.size(); i++) {
      return genres.get(i);
    }
    return res;
  }

  @Override
  public String toString(){//A refaire (string concat in loop)
    String res = title + "\n";
    res += "  ImdbId : " + movieDBId +"\n";
    res += "  Trailer : " + trailer +"\n";
    res += "  Synopsis : " + synopsis +"\n";
    res += "  Tagline : " + tagline +"\n";
    res += "  OrigTitle : " + origTitle +"\n";
    res += "  Rating : " + rating +"\n";
    res += "  Runtime : " + runtime +"\n";
    res += "  Year : " + year +"\n";
    res += "  Vote : " + votes +"\n";
    res += "  Genre : " + Utils.arrayToString(genres, " | ") +"\n";
    res += "  Studio : " + Utils.arrayToString(studios, " | ") +"\n";
    res += "  Country : " + Utils.arrayToString(countries, " | ") +"\n";
    res += "  Director : " + getDirectors() + "\n";
    res += "  Writer : " + getWriters() + "\n";
    res += "  Actor :\n";
    for (int i = 0; i < actors.size(); i++) {
      if (actors.get(i).getJob() == MoviePerson.ACTOR)
        res += "    " + actors.get(i).getName() + " : " + actors.get(i).getRoles() + "\n";
    }

    return res;
  }
}
