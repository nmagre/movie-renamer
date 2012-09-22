/*
 * movie-renamer
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
package fr.free.movierenamer.media;

import fr.free.movierenamer.media.MediaImage.MediaImageType;
import fr.free.movierenamer.utils.ActionNotValidException;
import fr.free.movierenamer.utils.Utils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class MediaData
 * 
 * @author QUÉMÉNEUR Simon
 */
public abstract class MediaData implements Serializable {

  private String title;
  private String originalTitle;
  private String sortTitle;
  private String thumb;
  private String trailer;
  private String synopsis;
  private String rating;
  private String year;
  private String votes;
  private String runtime;
  private final MediaImages mediaImages;
  private List<MediaID> mediaIDs;
  private List<String> genres;
  private List<MediaPerson> actors;
  private List<MediaPerson> directors;
  private List<MediaPerson> writers;
  private List<String> countries;

  /**
   * 
   */
  public MediaData() {
    this.mediaImages = new MediaImages();
    this.clear();
  }

  /**
   * Get media title
   * 
   * @return Media title
   */
  public final String getTitle() {
    return title;
  }

  /**
   * Get sort title
   * 
   * @return Sort title
   */
  public final String getSortTitle() {
    return (sortTitle.equals("") ? title : sortTitle);
  }

  /**
   * Get (default) movie thumb
   * 
   * @return
   */
  public final String getThumb() {
    return thumb;
  }

  /**
   * Get trailer
   * 
   * @return Trailer
   */
  public final String getTrailer() {
    return trailer;
  }

  /**
   * Get synopsis
   * 
   * @return Synopsis
   */
  public final String getSynopsis() {
    return synopsis;
  }

  /**
   * Get original title
   * 
   * @return Original title
   */
  public final String getOriginalTitle() {
    return originalTitle;
  }

  /**
   * Get rating
   * 
   * @return Rating
   */
  public final String getRating() {
    return rating;
  }

  /**
   * Get year
   * 
   * @return Year
   */
  public final String getYear() {
    return year;
  }

  /**
   * Get votes
   * 
   * @return Votes
   */
  public final String getVotes() {
    return votes;
  }

  /**
   * Get genres
   * 
   * @return Array of genre
   */
  public final List<String> getGenres() {
    return genres;
  }

  /**
   * Get the n genre
   * 
   * @param n Position of genre
   * @return Genre or an empty string
   */
  public final String getGenreN(int n) {
    if (n >= genres.size()) {
      return "";
    }
    return genres.get(n);
  }

  /**
   * Get genres to string
   * 
   * @param separator Separator
   * @param limit Number of genres to return (0 for all)
   * @return Genre separated by separator
   */
  public final String getGenresString(String separator, int limit) {
    return Utils.arrayToString(genres, separator, limit);
  }

  /**
   * Get Actors
   * 
   * @return Array of actors
   */
  public final List<MediaPerson> getActors() {
    return actors;
  }

  /**
   * Get the n actor
   * 
   * @param n Position of actor
   * @return Actor name or an empty string
   */
  public final String getActorN(int n) {
    if (n >= actors.size()) {
      return "";
    }
    return actors.get(n).getName();
  }

  /**
   * Get actors
   * 
   * @param separator Separator
   * @param limit Number of actors to return (0 for all)
   * @return Actors separated by separator
   */
  public final String getActorsString(String separator, int limit) {
    return Utils.arrayToString(actors, separator, limit);
  }

  /**
   * Get actor by name
   * 
   * @param actor Actor name
   * @return MoviePerson if actor found, null otherwise
   */
  public final MediaPerson getActorByName(String actor) {
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
   * Get directors
   * 
   * @return Array of directors
   */
  public final List<MediaPerson> getDirectors() {
    return directors;
  }

  /**
   * Get the n director
   * 
   * @param n Position of director
   * @return Director name or an empty string
   */
  public final String getDirectorN(int n) {
    if (n >= directors.size()) {
      return "";
    }
    return directors.get(n).getName();
  }

  /**
   * Get directors
   * 
   * @param separator Separator
   * @param limit Number of directors to return (0 for all)
   * @return Directors separated by separator
   */
  public final String getDirectorsString(String separator, int limit) {
    return Utils.arrayToString(directors, separator, limit);
  }

  /**
   * Get countries
   * 
   * @return Array of countries
   */
  public final List<String> getCountries() {
    return countries;
  }

  /**
   * Get the n country
   * 
   * @param n Position of country
   * @return Country or an empty string
   */
  public final String getCountryN(int n) {
    if (n >= countries.size()) {
      return "";
    }
    return countries.get(n);
  }

  /**
   * Get countries to string
   * 
   * @param separator Separator
   * @param limit Number of countries to return (0 for all)
   * @return Countries separated by separator
   */
  public final String getCountriesString(String separator, int limit) {
    return Utils.arrayToString(countries, separator, limit);
  }

  /**
   * @return the writers
   */
  public final List<MediaPerson> getWriters() {
    return writers;
  }

  public final String getWritersString(String separator, int limit) {
    return Utils.arrayToString(writers, separator, limit);
  }

  /**
   * Get array of thumbnails
   * 
   * @return List of MovieImages
   */
  public final List<MediaImage> getThumbs() {
    return mediaImages.getThumbs();
  }

  /**
   * Get array of fanarts
   * 
   * @return List of MovieImages
   */
  public final List<MediaImage> getFanarts() {
    return mediaImages.getFanarts();
  }

  /**
   * Get movie API IDs
   * 
   * @return List of movie IDs
   */
  public final List<MediaID> getIDs() {
    return mediaIDs;
  }

  /**
   * @param type
   * @return
   * @throws ActionNotValidException
   */
  public final List<MediaImage> getImages(MediaImageType type) throws ActionNotValidException {
    return mediaImages.getImages(type);
  }

  /**
   * Get runtime
   * 
   * @return Runtime
   */
  public final String getRuntime() {
    return runtime;
  }

  /**
   * Add a thumb to media images
   * 
   * @param thumb Thumb to add
   */
  public final void addThumb(MediaImage thumb) {
    mediaImages.addThumb(thumb);
  }

  /**
   * Add a fanart to media images
   * 
   * @param fanart Fanart to add
   */
  public final void addFanart(MediaImage fanart) {
    mediaImages.addFanart(fanart);
  }

  /**
   * @param extraImages
   */
  public final void addImages(MediaImages extraImages) {
    for (MediaImage thumb : extraImages.getThumbs()) {
      addThumb(thumb);
    }
    for (MediaImage fanart : extraImages.getFanarts()) {
      addFanart(fanart);
    }
  }

  /**
   * Add genre
   * 
   * @param genre Genre
   */
  public final void addGenre(String genre) {
    genres.add(genre);
  }

  /**
   * Add person (Actor, director,...)
   * 
   * @param person Person to add
   */
  public final void addPerson(MediaPerson person) {
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

  public final void addId(MediaID id) {
    for (MediaID mID : mediaIDs) {
      if (mID.equals(id)) {
        return;
      }
    }
    mediaIDs.add(id);
  }

  /**
   * Add country
   * 
   * @param country Country
   */
  public final void addCountry(String country) {
    countries.add(country);
  }

  /**
   * Add role to actor
   * 
   * @param actor Actor
   * @param role Role
   * @throws ActionNotValidException
   */
  public final void addRole(String actor, String role) throws ActionNotValidException {
    for (int i = 0; i < actors.size(); i++) {
      if (actors.get(i).getName().equals(actor)) {
        actors.get(i).addRole(role);
        break;
      }
    }
  }

  /**
   * 
   */
  public final void clear() {
    title = "?";
    originalTitle = "";
    sortTitle = "";
    thumb = "";
    trailer = "";
    synopsis = "";
    rating = "-1";
    year = "";
    votes = "-1";
    runtime = "-1";
    mediaIDs = new ArrayList<MediaID>();
    genres = new ArrayList<String>();
    actors = new ArrayList<MediaPerson>();
    directors = new ArrayList<MediaPerson>();
    countries = new ArrayList<String>();
    writers = new ArrayList<MediaPerson>();
  }

  /**
   * @param title the title to set
   */
  public final void setTitle(String title) {
    this.title = title;
  }

  /**
   * Set runtime
   * 
   * @param runtime Runtime
   */
  public final void setRuntime(String runtime) {
    this.runtime = runtime;
  }

  /**
   * @param originalTitle the originalTitle to set
   */
  public final void setOriginalTitle(String originalTitle) {
    this.originalTitle = originalTitle;
  }

  /**
   * @param sortTitle the sortTitle to set
   */
  public final void setSortTitle(String sortTitle) {
    this.sortTitle = sortTitle;
  }

  /**
   * @param thumb the thumb to set
   */
  public final void setThumb(String thumb) {
    this.thumb = thumb;
  }

  /**
   * @param trailer the trailer to set
   */
  public final void setTrailer(String trailer) {
    this.trailer = trailer;
  }

  /**
   * @param synopsis the synopsis to set
   */
  public final void setSynopsis(String synopsis) {
    this.synopsis = synopsis;
  }

  /**
   * @param rating the rating to set
   */
  public final void setRating(String rating) {
    this.rating = rating;
  }

  /**
   * @param year the year to set
   */
  public final void setYear(String year) {
    this.year = year;
  }

  /**
   * @param votes the votes to set
   */
  public final void setVotes(String votes) {
    this.votes = votes;
  }
  
  /**
   * @param writers the writers to set
   */
  public final void setWriters(List<MediaPerson> writers) {
    this.writers = writers;
  }
  
  /**
   * @param directors the directors to set
   */
  public final void setDirectors(List<MediaPerson> directors) {
    this.directors = directors;
  }
  
  /**
   * @param countries the countries to set
   */
  public final void setCountries(List<String> countries) {
    this.countries = countries;
  }
  
  /**
   * @param genres the genres to set
   */
  public final void setGenres(List<String> genres) {
    this.genres = genres;
  }

}
