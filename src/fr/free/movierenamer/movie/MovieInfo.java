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
  private String imdbId;
  private String imdbThumb;
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
  private ArrayList<MoviePerson> directors;
  private ArrayList<MoviePerson> writers;
  private ArrayList<String> countries;

  public MovieInfo() {
    title = "";
    imdbId = "";
    imdbThumb = "";
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
    directors = new ArrayList<MoviePerson>();
    writers = new ArrayList<MoviePerson>();
    countries = new ArrayList<String>();
  }

  public String getTitle() {
    return title;
  }

  public String getImdbId() {
    return imdbId;
  }

  public String getImdbThumb() {
    return imdbThumb;
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

  public ArrayList<MoviePerson> getDirectors() {
    return directors;
  }

  public ArrayList<MoviePerson> getWriters() {
    return writers;
  }

  public ArrayList<String> getCountries() {
    return countries;
  }

  public String getDirectorsString() {
    return Utils.arrayPersonnToString(directors, " | ");
  }

  public String getWritersString() {
    return Utils.arrayPersonnToString(writers, " | ");
  }

  public String getGenresString() {
    return Utils.arrayToString(genres, " | ");
  }

  public String getCountriesString() {
    return Utils.arrayToString(countries, " | ");
  }

  public String getFirstDirector() {
    String res = "";
    for (int i = 0; i < directors.size(); i++) {
      return directors.get(i).getName();
    }
    return res;
  }

  public String getFirstGenreString() {
    String res = "";
    for (int i = 0; i < genres.size(); i++) {
      return genres.get(i);
    }
    return res;
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

  public void setTitle(String title) {
    this.title = title;
  }

  public void setImdbId(String imdbId) {
    this.imdbId = imdbId;
  }

  public void setImdbThumb(String imdbThumb) {
    this.imdbThumb = imdbThumb;
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

  public void addDirector(MoviePerson director) {
    directors.add(director);
  }

  public void addWriter(MoviePerson writer) {
    writers.add(writer);
  }

  public void addCountry(String country) {
    countries.add(country);
  }

  public void addRole(String actor, String role) throws ActionNotValidException {
    for (int i = 0; i < actors.size(); i++) {
      if (actors.get(i).getName().equals(actor)) {
        actors.get(i).addRole(role);
        break;
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder res = new StringBuilder();
    res.append(title).append(Utils.ENDLINE);
    res.append("  ImdbId : ").append(imdbId).append(Utils.ENDLINE);
    res.append("  ImdbThumb : ").append(imdbThumb).append(Utils.ENDLINE);
    res.append("  Trailer : ").append(trailer).append(Utils.ENDLINE);
    res.append("  Synopsis : ").append(synopsis).append(Utils.ENDLINE);
    res.append("  Tagline : ").append(tagline).append(Utils.ENDLINE);
    res.append("  OrigTitle : ").append(origTitle).append(Utils.ENDLINE);
    res.append("  Rating : ").append(rating).append(Utils.ENDLINE);
    res.append("  Runtime : ").append(runtime).append(Utils.ENDLINE);
    res.append("  Year : ").append(year).append(Utils.ENDLINE);
    res.append("  Vote : ").append(votes).append(Utils.ENDLINE);
    res.append("  Genre : ").append(Utils.arrayToString(genres, " | ")).append(Utils.ENDLINE);
    res.append("  Studio : ").append(Utils.arrayToString(studios, " | ")).append(Utils.ENDLINE);
    res.append("  Country : ").append(Utils.arrayToString(countries, " | ")).append(Utils.ENDLINE);
    res.append("  Director : ").append(getDirectorsString()).append(Utils.ENDLINE);
    res.append("  Writer : ").append(getWritersString()).append(Utils.ENDLINE);
    res.append("  Actor :\n");
    for (int i = 0; i < actors.size(); i++) {
      res.append("    ").append(actors.get(i).getName()).append(" : ").append(actors.get(i).getRoles()).append(Utils.ENDLINE);
    }

    return res.toString();
  }
}
