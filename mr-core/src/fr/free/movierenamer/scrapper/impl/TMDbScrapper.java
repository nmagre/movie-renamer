/*
" * movie-renamer-core
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
package fr.free.movierenamer.scrapper.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.CastingInfo.PersonProperty;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.ImageInfo.ImageCategoryProperty;
import fr.free.movierenamer.info.ImageInfo.ImageProperty;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.MovieInfo.MovieProperty;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.Date;
import fr.free.movierenamer.utils.WebRequest;
import fr.free.movierenamer.utils.XPathUtils;

/**
 * Class TMDbScrapper : search movie on TMDb
 * 
 * @see http://help.themoviedb.org/kb/api/
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class TMDbScrapper extends MovieScrapper {

  private static final String host = "api.themoviedb.org";
  private static final String name = "TheMovieDb";
  private static final String version = "2.1"; // TODO change to v3 !!!!

  private final String apikey;

  public TMDbScrapper() {
    super(Locale.ENGLISH);
    String key = Settings.decodeApkKey(Settings.getApplicationProperty("themoviedb.apkapikey"));
    if (key == null) {
      throw new NullPointerException("apikey must not be null");
    }
    this.apikey = key;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  protected String getHost() {
    return host;
  }

  @Override
  protected List<Movie> searchMedia(String query, Locale locale) throws Exception {
    URL searchUrl = new URL("http", host, "/" + version + "/Movie.search/" + locale.getLanguage() + "/xml/" + apikey + "/" + WebRequest.encode(query));
    // FIXME has to be v3 !!!!
    // URL searchUrl = new URL("http", host, "/" + version + "/search/movie" +
    // "?api_key=" + apikey + "&language=" + locale.getLanguage() + "&query=" +
    // WebRequest.encode(query));
    Document dom = WebRequest.getXmlDocument(searchUrl.toURI());

    List<Node> nodes = XPathUtils.selectNodes("OpenSearchDescription/movies/movie", dom);
    Map<Integer, Movie> resultSet = new LinkedHashMap<Integer, Movie>(nodes.size());

    for (Node node : nodes) {
      int id = XPathUtils.getIntegerContent("id", node);
      int imdbId = -1;
      try {
        String imdb = XPathUtils.getTextContent("imdb_id", node);
        imdbId = Integer.parseInt(imdb.substring(2));
      } catch (Exception e) {
        Logger.getLogger(getClass().getName()).log(Level.WARNING, "Invalid imdb ID", e);
      }
      String movieName = XPathUtils.getTextContent("name", node);
      Node imagesNodes = XPathUtils.selectNode("//images", dom);
      URL thumb = null;
      for (Node image : XPathUtils.selectNodes("image", imagesNodes)) {
        try {
          if ("original".equals(XPathUtils.getAttribute("size", image))) {
            thumb = new URL(XPathUtils.getAttribute("url", image));
            break;
          }
        } catch (Exception e) {
          Logger.getLogger(getClass().getName()).log(Level.WARNING, "Invalid image: " + image, e);
        }
      }
      Date released = Date.parse(XPathUtils.getTextContent("released", node), "yyyy-MM-dd");

      if (!resultSet.containsKey(id)) {
        resultSet.put(id, new Movie(id, movieName, thumb, (released!=null)?released.getYear():-1, imdbId));
      }
    }

    return new ArrayList<Movie>(resultSet.values());
  }

  @Override
  protected MovieInfo fetchMediaInfo(Movie movie, Locale locale) throws Exception {
    URL searchUrl = new URL("http", host, "/" + version + "/Movie.getInfo/" + locale.getLanguage() + "/xml/" + apikey + "/" + movie.getMediaId());
    Document dom = WebRequest.getXmlDocument(searchUrl.toURI());

    List<Node> nodes = XPathUtils.selectNodes("OpenSearchDescription/movies/movie", dom);

    for (Node node : nodes) {
      Map<MovieProperty, String> fields = new EnumMap<MovieProperty, String>(MovieProperty.class);
      fields.put(MovieProperty.title, XPathUtils.getTextContent("name", node));
      fields.put(MovieProperty.rating, XPathUtils.getTextContent("rating", node));
      fields.put(MovieProperty.votes, XPathUtils.getTextContent("votes", node));
      fields.put(MovieProperty.id, XPathUtils.getTextContent("id", node));
      fields.put(MovieProperty.IMDB_ID, XPathUtils.getTextContent("imdb_id", node));
      fields.put(MovieProperty.originalTitle, XPathUtils.getTextContent("original_name", node));
      fields.put(MovieProperty.releasedDate, XPathUtils.getTextContent("released", node));
      fields.put(MovieProperty.overview, XPathUtils.getTextContent("overview", node));
      fields.put(MovieProperty.runtime, XPathUtils.getTextContent("runtime", node));
      fields.put(MovieProperty.budget, XPathUtils.getTextContent("budget", node));

      List<String> genres = new ArrayList<String>();
      for (Node category : XPathUtils.selectNodes("categories/category", node)) {
        if ("genre".equals(XPathUtils.getAttribute("type", category))) {
          genres.add(XPathUtils.getAttribute("name", category));
        }
      }

      List<Locale> countries = new ArrayList<Locale>();
      // TODO set country

      MovieInfo movieInfo = new MovieInfo(fields, genres, countries);
      return movieInfo;
    }

    return null;
  }

  @Override
  protected List<ImageInfo> fetchImagesInfo(Movie movie, Locale locale) throws Exception {
    URL searchUrl = new URL("http", host, "/" + version + "/Movie.getImages/" + locale.getLanguage() + "/xml/" + apikey + "/" + movie.getMediaId());
    Document dom = WebRequest.getXmlDocument(searchUrl.toURI());

    List<ImageInfo> images = new ArrayList<ImageInfo>();
    for (String section : new String[] {
        "backdrop", "posterPath"
    }) {
      List<Node> sectionNodes = XPathUtils.selectNodes("//" + section, dom);
      for (Node curNode : sectionNodes) {
        for (Node image : XPathUtils.selectNodes("image", curNode)) {
          try {
            if ("original".equals(XPathUtils.getAttribute("size", image))) {
              Map<ImageProperty, String> imageFields = new EnumMap<ImageProperty, String>(ImageProperty.class);
              imageFields.put(ImageProperty.height, XPathUtils.getAttribute("height", image));
              imageFields.put(ImageProperty.url, XPathUtils.getAttribute("url", image));
              imageFields.put(ImageProperty.width, XPathUtils.getAttribute("width", image));
              images.add(new ImageInfo(imageFields, section.equals("posterPath") ? ImageCategoryProperty.thumb : ImageCategoryProperty.fanart));
            }
          } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Invalid image: " + image, e);
          }
        }
      }
    }

    return images;
  }

  @Override
  protected List<CastingInfo> fetchCastingInfo(Movie movie, Locale locale) throws Exception {
    URL searchUrl = new URL("http", host, "/" + version + "/Movie.getInfo/" + locale.getLanguage() + "/xml/" + apikey + "/" + movie.getMediaId());
    Document dom = WebRequest.getXmlDocument(searchUrl.toURI());

    List<Node> nodes = XPathUtils.selectNodes("OpenSearchDescription/movies/movie", dom);

    for (Node node : nodes) {
      List<CastingInfo> casting = new ArrayList<CastingInfo>();
      for (Node person : XPathUtils.selectNodes("cast/person", node)) {
        Map<PersonProperty, String> personFields = new EnumMap<PersonProperty, String>(PersonProperty.class);
        personFields.put(PersonProperty.name, XPathUtils.getAttribute("name", person));
        personFields.put(PersonProperty.character, XPathUtils.getAttribute("character", person));
        personFields.put(PersonProperty.job, XPathUtils.getAttribute("job", person));
        casting.add(new CastingInfo(personFields));
      }
      return casting;
    }

    return null;
  }

}
