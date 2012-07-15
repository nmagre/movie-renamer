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
package fr.free.movierenamer.media.movie;

import java.util.ArrayList;
import java.util.List;

import fr.free.movierenamer.media.IMediaInfo;
import fr.free.movierenamer.media.MediaID;
import fr.free.movierenamer.media.MediaImage;
import fr.free.movierenamer.media.MediaPerson;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Utils;

/**
 * Class MovieInfo
 * 
 * @author Nicolas Magré
 */
public class MovieInfo implements IMediaInfo<MovieImage> {

  private String title;
  private String sortTitle;
  private String thumb;
  private String trailer;
  private String synopsis;
  private String outline;
  private String tagline;
  private String origTitle;
  private String rating;
  private String mpaa;
  private String runtime;
  private String year;
  private String votes;
  private String top250;
  private boolean watched;
  private MovieImage movieImage;
  private List<MediaID> movieIDs;
  private List<String> set;// Saga
  private List<String> genres;
  private List<String> studios;
  private List<MediaPerson> actors;
  private List<MediaPerson> directors;
  private List<MediaPerson> writers;
  private List<String> countries;

  /**
   * Default constructor
   */
  public MovieInfo() {
    title = "";
    trailer = "";
    thumb = "";
    synopsis = "";
    outline = "";
    tagline = "";
    origTitle = "";
    rating = "-1";
    runtime = "-1";
    year = "";
    votes = "-1";
    top250 = "0";
    watched = false;
    mpaa = "";
    sortTitle = "";
    movieImage = new MovieImage();
    movieIDs = new ArrayList<MediaID>();
    set = new ArrayList<String>();
    genres = new ArrayList<String>();
    studios = new ArrayList<String>();
    actors = new ArrayList<MediaPerson>();
    directors = new ArrayList<MediaPerson>();
    writers = new ArrayList<MediaPerson>();
    countries = new ArrayList<String>();
  }

  /**
   * Get movie title
   * 
   * @return Movie title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Get sort title
   * 
   * @return Sort title
   */
  public String getSortTitle() {
    return (sortTitle.equals("") ? title : sortTitle);
  }

  /**
   * Get (default) movie thumb
   * 
   * @return
   */
  public String getThumb() {
    return thumb;
  }

  /**
   * Get trailer
   * 
   * @return Trailer
   */
  public String getTrailer() {
    return trailer;
  }

  /**
   * Get synopsis
   * 
   * @return Synopsis
   */
  public String getSynopsis() {
    return synopsis;
  }

  /**
   * Get outline
   * 
   * @return Outline
   */
  public String getOutline() {
    return outline;
  }

  /**
   * Get tagline
   * 
   * @return Tagline
   */
  public String getTagline() {
    return tagline;
  }

  /**
   * Get original title
   * 
   * @return Original title
   */
  public String getOrigTitle() {
    return origTitle;
  }

  /**
   * Get rating
   * 
   * @return Rating
   */
  public String getRating() {
    return rating;
  }

  /**
   * Get runtime
   * 
   * @return Runtime
   */
  public String getRuntime() {
    return runtime;
  }

  /**
   * Get year
   * 
   * @return Year
   */
  public String getYear() {
    return year;
  }

  /**
   * Get votes
   * 
   * @return Votes
   */
  public String getVotes() {
    return votes;
  }

  /**
   * Get top 250
   * 
   * @return 0 or top 250 position
   */
  public String getTop250() {
    return top250;
  }

  /**
   * Movie was watched
   * 
   * @return Ture or false
   */
  public boolean getWatched() {
    return watched;
  }

  /**
   * Get movie images
   * 
   * @return MovieImage
   */
  public MovieImage getMovieImage() {
    return movieImage;
  }

  /**
   * Get array of thumbnails
   * 
   * @return List of MovieImage
   */
  public List<MediaImage> getThumbs() {
    return movieImage.getThumbs();
  }

  /**
   * Get array of fanarts
   * 
   * @return List of MovieImage
   */
  public List<MediaImage> getFanarts() {
    return movieImage.getFanarts();
  }

  /**
   * Get movie API IDs
   * 
   * @return List of movie IDs
   */
  public List<MediaID> getIDs() {
    return movieIDs;
  }

  /**
   * Get set
   * 
   * @return Array of set
   */
  public List<String> getSet() {
    return set;
  }

  /**
   * Get mpaa
   * 
   * @return Mpaa
   */
  public String getMpaa() {
    return mpaa;
  }

  /**
   * Get genres
   * 
   * @return Array of genre
   */
  public List<String> getGenres() {
    return genres;
  }

  /**
   * Get studios
   * 
   * @return Array of studios
   */
  public List<String> getStudios() {
    return studios;
  }

  /**
   * Get Actors
   * 
   * @return Array of actors
   */
  public List<MediaPerson> getActors() {
    return actors;
  }

  /**
   * Get directors
   * 
   * @return Array of directors
   */
  public List<MediaPerson> getDirectors() {
    return directors;
  }

  /**
   * Get writers
   * 
   * @return Array of writers
   */
  public List<MediaPerson> getWriters() {
    return writers;
  }

  /**
   * Get countries
   * 
   * @return Array of countries
   */
  public List<String> getCountries() {
    return countries;
  }

  /**
   * Get actors
   * 
   * @param separator Separator
   * @param limit Number of actors to return (0 for all)
   * @return Actors separated by separator
   */
  public String getActorsString(String separator, int limit) {
    return Utils.arrayToString(actors, separator, limit);
  }

  /**
   * Get directors
   * 
   * @param separator Separator
   * @param limit Number of directors to return (0 for all)
   * @return Directors separated by separator
   */
  public String getDirectorsString(String separator, int limit) {
    return Utils.arrayToString(directors, separator, limit);
  }

  /**
   * Get writers to string
   * 
   * @param separator Separator
   * @param limit Number of writers to return (0 for all)
   * @return Writers separated by separator
   */
  public String getWritersString(String separator, int limit) {
    return Utils.arrayToString(writers, separator, limit);
  }

  /**
   * Get genres to string
   * 
   * @param separator Separator
   * @param limit Number of genres to return (0 for all)
   * @return Genre separated by separator
   */
  public String getGenresString(String separator, int limit) {
    return Utils.arrayToString(genres, separator, limit);
  }

  /**
   * Get countries to string
   * 
   * @param separator Separator
   * @param limit Number of countries to return (0 for all)
   * @return Countries separated by separator
   */
  public String getCountriesString(String separator, int limit) {
    return Utils.arrayToString(countries, separator, limit);
  }

  /**
   * Get studios to string
   * 
   * @param separator Separator
   * @param limit Number of studios to return (0 for all)
   * @return Studios separated by separator
   */
  public String getStudiosString(String separator, int limit) {
    return Utils.arrayToString(studios, separator, limit);
  }

  /**
   * Get sets to string
   * 
   * @param separator Separator
   * @param limit Number of sets to return (0 for all)
   * @return Sets separated by separator
   */
  public String getSetString(String separator, int limit) {
    return Utils.arrayToString(set, separator, limit);
  }

  /**
   * Get first director
   * 
   * @return First director
   */
  public String getFirstDirector() {
    String res = "";
    for (int i = 0; i < directors.size(); i++) {
      return directors.get(i).getName();
    }
    return res;
  }

  /**
   * Get the n actor
   * 
   * @param n Position of actor
   * @return Actor name or an empty string
   */
  public String getActorN(int n) {
    if (n >= actors.size()) {
      return "";
    }
    return actors.get(n).getName();
  }

  /**
   * Get the n director
   * 
   * @param n Position of director
   * @return Director name or an empty string
   */
  public String getDirectorN(int n) {
    if (n >= directors.size()) {
      return "";
    }
    return directors.get(n).getName();
  }

  /**
   * Get first genre
   * 
   * @return First genre
   */
  public String getFirstGenreString() {
    String res = "";
    for (int i = 0; i < genres.size(); i++) {
      return genres.get(i);
    }
    return res;
  }

  /**
   * Get the n genre
   * 
   * @param n Position of genre
   * @return Genre or an empty string
   */
  public String getGenreN(int n) {
    if (n >= genres.size()) {
      return "";
    }
    return genres.get(n);
  }

  /**
   * Get the n country
   * 
   * @param n Position of country
   * @return Country or an empty string
   */
  public String getCountryN(int n) {
    if (n >= countries.size()) {
      return "";
    }
    return countries.get(n);
  }

  /**
   * Get actor by name
   * 
   * @param actor Actor name
   * @return MoviePerson if actor found, null otherwise
   */
  public MediaPerson getActorByName(String actor) {
    MediaPerson res = null;
    for (int i = 0; i < actors.size(); i++) {
      if (actors.get(i).getName().equals(actor)) {
        res = actors.get(i);
        break;
      }
    }
    return res;
  }

  /**
   * Set movie images
   * 
   * @param movieImage Movie Images
   */
  public void setImages(MovieImage movieImage) {
    this.movieImage = movieImage;
  }

  /**
   * Set title
   * 
   * @param title Title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Set sort title
   * 
   * @param sortTitle Sort title
   */
  public void setSortTitle(String sortTitle) {
    this.sortTitle = sortTitle;
  }

  /**
   * Set (default) thumb
   * 
   * @param thumb
   */
  public void setThumb(String thumb) {
    this.thumb = thumb;
  }

  /**
   * Set trailer
   * 
   * @param trailer Trailer
   */
  public void setTrailer(String trailer) {
    this.trailer = trailer;
  }

  /**
   * Set studios
   * 
   * @param studios Array of studio
   */
  public void setStudios(List<String> studios) {
    this.studios = studios;
  }

  /**
   * Set directors
   * 
   * @param directors Array of directors
   */
  public void setDirectors(List<MediaPerson> directors) {
    this.directors = directors;
  }

  /**
   * Set genres
   * 
   * @param genres Array of genres
   */
  public void setGenre(List<String> genres) {
    this.genres = genres;
  }

  /**
   * Set sets
   * 
   * @param set Array of sets
   */
  public void setSets(List<String> set) {
    this.set = set;
  }

  /**
   * Set writers
   * 
   * @param writers Array of writers
   */
  public void setWriters(List<MediaPerson> writers) {
    this.writers = writers;
  }

  /**
   * Set countries
   * 
   * @param countries Array of countries
   */
  public void setCountries(List<String> countries) {
    this.countries = countries;
  }

  /**
   * Set synopsis
   * 
   * @param synopsis Synopsis
   */
  public void setSynopsis(String synopsis) {
    this.synopsis = synopsis;
  }

  /**
   * Set outline
   * 
   * @param outline Outline
   */
  public void setOutline(String outline) {
    this.outline = outline;
  }

  /**
   * Set tagline
   * 
   * @param tagline Tagline
   */
  public void setTagline(String tagline) {
    this.tagline = tagline;
  }

  /**
   * Set original title
   * 
   * @param origTitle Original title
   */
  public void setOrigTitle(String origTitle) {
    this.origTitle = origTitle;
  }

  /**
   * Set rating
   * 
   * @param rating Rating
   */
  public void setRating(String rating) {
    this.rating = rating;
  }

  /**
   * Set runtime
   * 
   * @param runtime Runtime
   */
  public void setRuntime(String runtime) {
    this.runtime = runtime;
  }

  /**
   * Set year
   * 
   * @param year Year
   */
  public void setYear(String year) {
    this.year = year;
  }

  /**
   * Set votes
   * 
   * @param votes Votes
   */
  public void setVotes(String votes) {
    this.votes = votes;
  }

  /**
   * Set top 250
   * 
   * @param top250
   */
  public void setTop250(String top250) {
    this.top250 = top250;
  }

  /**
   * Set movie watched
   * 
   * @param watched
   */
  public void setWatched(boolean watched) {
    this.watched = watched;
  }

  /**
   * Set sets
   * 
   * @param set Array of set
   */
  public void setSet(List<String> set) {
    this.set = set;
  }

  /**
   * Set mpaa
   * 
   * @param mpaa Mpaa
   */
  public void setMpaa(String mpaa) {
    this.mpaa = mpaa;
  }

  /**
   * Add a thumb to movie images
   * 
   * @param thumb Thumb to add
   */
  public void addThumb(MediaImage thumb) {
    movieImage.addThumb(thumb);
  }

  /**
   * Add a fanart to movie images
   * 
   * @param fanart Fanart to add
   */
  public void addFanart(MediaImage fanart) {
    movieImage.addFanart(fanart);
  }

  /**
   * Add genre
   * 
   * @param genre Genre
   */
  public void addGenre(String genre) {
    genres.add(genre);
  }

  /**
   * Add studio
   * 
   * @param studio Studio
   */
  public void addStudio(String studio) {
    studios.add(studio);
  }

  /**
   * Add person (Actor, director,...)
   * 
   * @param person Person to add
   */
  public void addPerson(MediaPerson person) {
    switch (person.getJob()) {
    case MediaPerson.ACTOR:
      actors.add(person);
      break;
    case MediaPerson.DIRECTOR:
      directors.add(person);
      break;
    case MediaPerson.WRITER:
      writers.add(person);
      break;
    default:
      break;
    }
  }

  /**
   * Add country
   * 
   * @param country Country
   */
  public void addCountry(String country) {
    countries.add(country);
  }

  /**
   * Add role to actor
   * 
   * @param actor Actor
   * @param role Role
   * @throws ActionNotValidException
   */
  public void addRole(String actor, String role) throws ActionNotValidException {
    for (int i = 0; i < actors.size(); i++) {
      if (actors.get(i).getName().equals(actor)) {
        actors.get(i).addRole(role);
        break;
      }
    }
  }

  /**
   * Add movie API id
   * 
   * @param id Movie APi id
   */
  public void addID(MediaID id) {
    for (MediaID mID : movieIDs) {
      if (mID.equals(id)) {
        return;
      }
    }
    movieIDs.add(id);
  }

  /**
   * Add set
   * 
   * @param strSet Set
   */
  public void addSet(String strSet) {
    set.add(strSet);
  }

  @Override
  public String toString() {
    StringBuilder res = new StringBuilder();
    res.append(title).append(Utils.ENDLINE);
    res.append("  Trailer : ").append(trailer).append(Utils.ENDLINE);
    res.append("  Thumbnail : ").append(thumb).append(Utils.ENDLINE);
    res.append("  Synopsis : ").append(synopsis).append(Utils.ENDLINE);
    res.append("  Short-Synopsis : ").append(outline).append(Utils.ENDLINE);
    res.append("  Set(Saga) : ").append(getSetString(" | ", 0)).append(Utils.ENDLINE);
    res.append("  Mpaa : ").append(mpaa).append(Utils.ENDLINE);
    res.append("  Tagline : ").append(tagline).append(Utils.ENDLINE);
    res.append("  OrigTitle : ").append(origTitle).append(Utils.ENDLINE);
    res.append("  Rating : ").append(rating).append(Utils.ENDLINE);
    res.append("  Runtime : ").append(runtime).append(Utils.ENDLINE);
    res.append("  Year : ").append(year).append(Utils.ENDLINE);
    res.append("  Vote : ").append(votes).append(Utils.ENDLINE);
    res.append("  Top 250 : ").append(top250).append(Utils.ENDLINE);
    res.append("  Watched : ").append(watched ? "True" : "False").append(Utils.ENDLINE);
    res.append("  Genre : ").append(Utils.arrayToString(genres, " | ", 0)).append(Utils.ENDLINE);
    res.append("  Studio : ").append(Utils.arrayToString(studios, " | ", 0)).append(Utils.ENDLINE);
    res.append("  Country : ").append(Utils.arrayToString(countries, " | ", 0)).append(Utils.ENDLINE);
    res.append("  Director : ").append(getDirectorsString(" | ", 0)).append(Utils.ENDLINE);
    res.append("  Writer : ").append(getWritersString(" | ", 0)).append(Utils.ENDLINE);
    res.append("  Actor :\n");

    for (int i = 0; i < actors.size(); i++) {
      res.append("    ").append(actors.get(i).getName()).append(" : ").append(actors.get(i).getRoles()).append(Utils.ENDLINE);
    }
    res.append(movieImage.toString());

    return res.toString();
  }
}
