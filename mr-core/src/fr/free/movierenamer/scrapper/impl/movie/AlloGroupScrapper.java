/*
 * movie-renamer-core
 * Copyright (C) 2012-2013 Nicolas Magré
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
package fr.free.movierenamer.scrapper.impl.movie;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.MovieInfo.MotionPictureRating;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.ScrapperUtils;
import fr.free.movierenamer.utils.StringUtils;
import fr.free.movierenamer.utils.URIRequest;
import fr.free.movierenamer.utils.XPathUtils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Class AlloGroupScrapper
 *
 * @author Nicolas Magré
 */
public abstract class AlloGroupScrapper extends MovieScrapper {

  private final Pattern yearPattern = Pattern.compile("\\d{4}");
  private final Pattern runtimePattern = Pattern.compile("(\\d+)h (\\d+)min");
  private final Pattern imdbIdPattern = Pattern.compile("tt(\\d+{7})");
  private final String imdbIdLookupHost = "passion-xbmc.org";

  protected interface ITag {

    public InfoTag getInfoTag(String str);
  }

  protected enum InfoTag {

    Date_de_sortie,
    Réalisé_par,
    Genre,
    Nationalité,
    Spectateurs,
    Titre_original,
    Budget,
    Distributeur,
    unknown
  }

  private enum JobTag {

    director,
    actors
  }

  protected AlloGroupScrapper(LocaleUtils.AvailableLanguages lang) {
    super(lang);
  }

  protected abstract String getSearchString();

  protected abstract String getMoviePageString(IdInfo id);

  protected abstract String getCastingPageString(IdInfo id);

  protected abstract Pattern getIdPattern();

  protected abstract Pattern getPersonIdPattern();

  protected abstract String getImageHost();

  protected abstract InfoTag getInfoTag(String str);

  protected abstract MotionPictureRating getRatingScale();

  protected String getLongId(String str) {
    return null;
  }

  protected int getId(URL url) {
    Matcher matcher = getIdPattern().matcher(url.toString());

    if (matcher.find()) {
      return Integer.parseInt(matcher.group(1));
    }

    throw new IllegalArgumentException(String.format("Cannot find allocine id: %s", url));
  }

  protected Pattern getRuntimePattern() {
    return runtimePattern;
  }

  @Override
  protected final List<Movie> searchMedia(String query, Locale language) throws Exception {
    URL searchUrl = new URL("http", getHost(), "/" + getSearchString() + "/1/?q=" + URIRequest.encode(query));
    return searchMedia(searchUrl, language);
  }

  @Override
  protected final List<Movie> searchMedia(URL searchUrl, Locale language) throws Exception {
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI());

    // select movie results
    List<Node> nodes = XPathUtils.selectNodes("//TABLE[@class='totalwidth noborder purehtml']//TR", dom);
    List<Movie> results = new ArrayList<Movie>();

    for (Node node : nodes) {
      Node retNode = XPathUtils.selectNode("TD/A", node);
      if (retNode == null) {// Not a movie
        continue;
      }

      String href = XPathUtils.getAttribute("href", retNode);
      String longid = getLongId(href);
      Matcher m = getIdPattern().matcher(href);
      if (!m.find()) {
        continue;
      }

      int id = Integer.parseInt(m.group(1));

      URL thumb;
      try {
        String res = XPathUtils.getAttribute("src", XPathUtils.selectNode("IMG", retNode));
        if (res.endsWith("gif")) {
          thumb = null;
        } else {
          thumb = new URL("http", getImageHost(), res.replaceAll(".*\\/medias", "/medias"));
        }
      } catch (Exception ex) {
        thumb = null;
      }

      Node infoNode = XPathUtils.selectNode("TD[@class='totalwidth']//DIV[@style='margin-top:-5px;']", node);
      String title = XPathUtils.selectNode("A", infoNode).getTextContent().trim();
      String originalTitle = XPathUtils.selectNode("A/following-sibling::text()", infoNode).getTextContent().trim();
      String year = XPathUtils.selectNode("SPAN/BR[1]/preceding-sibling::text()", infoNode).getTextContent().trim();

      originalTitle = originalTitle.replace("(", "").replace(")", "");
      originalTitle = originalTitle.equals("") ? null : originalTitle;

      m = yearPattern.matcher(year);
      if (!m.find()) {
        year = "-1";
      }

      results.add(new Movie(new IdInfo(id, longid, ScrapperUtils.AvailableApiIds.ALLOCINE), title, originalTitle, thumb, Integer.parseInt(year)));
    }

    // movie page ?
    if (results.isEmpty()) {
      try {
        int alloid = getId(searchUrl);
        String longid = getLongId(searchUrl.toString());
        IdInfo id = new IdInfo(alloid, longid, ScrapperUtils.AvailableApiIds.ALLOCINE);
        MovieInfo info = fetchMediaInfo(new Movie(id, null, null, null, -1), language);
        URL thumb;
        try {
          thumb = new URL(info.getPosterPath().toURL().toExternalForm());
        } catch (Exception ex) {
          thumb = null;
        }
        Movie movie = new Movie(id, info.getTitle(), info.getOriginalTitle(),
                thumb, info.getYear());
        if (movie != null) {
          results.add(movie);
        }
      } catch (Exception e) {
        // ignore, can't find movie
      }
    }

    return results;
  }

  @Override
  protected MovieInfo fetchMediaInfo(Movie movie, Locale language) throws Exception {
    URL searchUrl = new URL("http", getHost(), getMoviePageString(movie.getMediaId()));
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI());

    List<String> genres = new ArrayList<String>();
    List<Locale> countries = new ArrayList<Locale>();
    List<String> studios = new ArrayList<String>();
    Map<MovieInfo.MovieProperty, String> fields = new EnumMap<MovieInfo.MovieProperty, String>(MovieInfo.MovieProperty.class);
    Map<MovieInfo.MovieMultipleProperty, List<?>> multipleFields = new EnumMap<MovieInfo.MovieMultipleProperty, List<?>>(MovieInfo.MovieMultipleProperty.class);

    fields.put(MovieInfo.MovieProperty.title, XPathUtils.selectString("//DIV[@id='title']//SPAN", dom).trim());

    String certification = XPathUtils.selectString("//SPAN[@class='insist']", dom);
    if (certification != null && !certification.equals("")) {
      fields.put(MovieInfo.MovieProperty.certification, certification);
      Matcher matcher = Pattern.compile(".*(\\d{2})").matcher(certification);

      MotionPictureRating mpr = getRatingScale();
      if (mpr != null && matcher.find()) {
        String code = matcher.group(1);
        switch (mpr) {
          case GERMANY:
            code = "FSK " + code;
            break;
          case FRANCE:
            code = "-" + code;
            break;
          case PORTUGAL:
            code = "M/" + code;
            break;
        }

        String mpaacode = MotionPictureRating.getMpaaCode(code, mpr);
        if (mpaacode != null) {
          fields.put(MovieInfo.MovieProperty.certificationCode, mpaacode);
        }
      }
    }

    List<Node> nodes = XPathUtils.selectNodes("//DIV[@class='content']/UL/LI", dom);
    for (Node node : nodes) {
      InfoTag tag = getInfoTag(XPathUtils.selectString("SPAN", node).replace(" ", "_"));

      switch (tag) {
        case Date_de_sortie:
          Node retNode = XPathUtils.selectNode("//SPAN[@itemprop='datePublished']", node);
          if (retNode != null) {
            fields.put(MovieInfo.MovieProperty.releasedDate, XPathUtils.getAttribute("content", retNode));
          }

          retNode = XPathUtils.selectNode("//SPAN[@itemprop='duration']", node);
          if (retNode != null) {
            Matcher m = getRuntimePattern().matcher(retNode.getTextContent().trim());
            if (m.find()) {
              fields.put(MovieInfo.MovieProperty.runtime, String.valueOf(Integer.parseInt(m.group(1)) * 60 + Integer.parseInt(m.group(2))));
            }
          }
          break;
        case Genre:
          genres.addAll(Arrays.asList(explodeValue(XPathUtils.selectString("DIV", node).trim())));
          break;
        case Nationalité:
          for (String country : explodeValue(XPathUtils.selectString("DIV", node).trim())) {
            countries.add(LocaleUtils.findCountry(country, language));
          }
          break;
      }
    }

    nodes = XPathUtils.selectNodes("//DIV[@class='expendTable']//TABLE//TH", dom);
    for (Node node : nodes) {
      InfoTag tag = getInfoTag(node.getTextContent().replace(" ", "_"));

      switch (tag) {
        case Titre_original:
          fields.put(MovieInfo.MovieProperty.originalTitle, XPathUtils.selectString("text()", node.getNextSibling()));
          break;
        case Budget:
          String budget = XPathUtils.selectString("text()", node.getNextSibling());
          if (!budget.equals("-")) {
            fields.put(MovieInfo.MovieProperty.budget, budget);
          }
          break;
        case Distributeur:
          studios.add(XPathUtils.selectString("*", node.getNextSibling()));
          break;
      }
    }

    Node retNode = XPathUtils.selectNode("//DIV[@class='oflow_a']/SPAN[@class='note']", dom);
    if (retNode != null) {
      Float rate = Float.parseFloat(retNode.getTextContent().trim().replace(",", "."));
      fields.put(MovieInfo.MovieProperty.rating, String.valueOf(rate));
    }

    retNode = XPathUtils.selectNode("//SPAN[@itemprop='ratingCount']", dom);
    if (retNode != null) {
      fields.put(MovieInfo.MovieProperty.votes, retNode.getTextContent().trim());
    }

    retNode = XPathUtils.selectNode("//DIV[@class='margin_20b']/P[@itemprop='description']", dom);
    if (retNode != null) {
      fields.put(MovieInfo.MovieProperty.overview, retNode.getTextContent().trim());
    }

    retNode = XPathUtils.selectNode("//DIV[@class='poster']//IMG[@itemprop='image']", dom);
    if (retNode != null) {
      String url = XPathUtils.getAttribute("src", retNode);
      fields.put(MovieInfo.MovieProperty.posterPath, url.replaceAll(".*\\/medias", "http://" + getImageHost() + "/medias"));
    }

    List<String> tags = new ArrayList<String>();
    nodes = XPathUtils.selectNodes("//DIV[@class='box_right_col']/DIV[@class='titlebar_01']/SPAN[contains(., 'Tags')]/parent::node()/parent::node()/UL/LI", dom);
    for (Node node : nodes) {
      tags.add(StringUtils.capitalizedLetter(XPathUtils.selectString("SPAN", node), true));
    }

    List<IdInfo> ids = new ArrayList<IdInfo>();
    ids.add(movie.getId());
    
    multipleFields.put(MovieInfo.MovieMultipleProperty.ids, ids);
    multipleFields.put(MovieInfo.MovieMultipleProperty.studios, studios);
    multipleFields.put(MovieInfo.MovieMultipleProperty.tags, tags);
    multipleFields.put(MovieInfo.MovieMultipleProperty.countries, countries);
    multipleFields.put(MovieInfo.MovieMultipleProperty.genres, genres);

    MovieInfo movieInfo = new MovieInfo(fields, multipleFields);
    return movieInfo;
  }

  @Override
  protected List<CastingInfo> fetchCastingInfo(Movie movie, Locale language) throws Exception {

    URL searchUrl = new URL("http", getHost(), getCastingPageString(movie.getMediaId()));
    List<CastingInfo> casting = new ArrayList<CastingInfo>();

    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI());

    List<Node> nodes = XPathUtils.selectNodes("//DIV[@class='media_list_02 media_list_hl margin_10b']/UL/LI", dom);
    for (Node node : nodes) {
      String job = XPathUtils.getAttribute("itemprop", node);

      JobTag tag;
      try {
        tag = JobTag.valueOf(job);
      } catch (Exception ex) {
        tag = JobTag.actors;
      }

      Map<CastingInfo.PersonProperty, String> personFields = new EnumMap<CastingInfo.PersonProperty, String>(CastingInfo.PersonProperty.class);
      String img = XPathUtils.getAttribute("src", XPathUtils.selectNode("SPAN/IMG", node));
      if (!img.contains("empty_photo")) {
        personFields.put(CastingInfo.PersonProperty.picturePath, img);
      }

      personFields.put(CastingInfo.PersonProperty.name, XPathUtils.selectString("P/A", node).trim());
      String url = XPathUtils.getAttribute("href", XPathUtils.selectNode("P/A", node));
      Matcher m = getPersonIdPattern().matcher(url);
      if (m.find()) {
        personFields.put(CastingInfo.PersonProperty.id, m.group(1));
      }

      switch (tag) {
        case actors:
          String character = XPathUtils.selectString("P[@class='fs11 lighten_hl']", node);
          character = character.replaceAll(".*:", "").trim();
          personFields.put(CastingInfo.PersonProperty.character, character);
          personFields.put(CastingInfo.PersonProperty.job, CastingInfo.ACTOR);
          break;
        case director:
          personFields.put(CastingInfo.PersonProperty.job, CastingInfo.DIRECTOR);
          break;
      }
      casting.add(new CastingInfo(personFields));
    }

    return casting;
  }

  @Override
  protected List<ImageInfo> fetchImagesInfo(Movie movie) throws Exception {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    IdInfo imdbId = imdbIdLookup(movie.getId());
    if (imdbId != null) {
      return super.fetchImagesInfo(new Movie(imdbId, movie.getName(), movie.getOriginalTitle(), movie.getURL(), movie.getYear()));
    }

    // TODO
    /*
     //    URL searchUrl = new URL("http", getHost(), "/rest/v" + version + "/movie?partner=" + apikey + "&profile=large&filter=movie&striptags=synopsis,synopsisshort&format=json&code=" + movie.getMediaId());
     URL searchUrl = createUrl("movie", params);
     JSONObject json = URIRequest.getJsonDocument(searchUrl.toURI());

     JSONObject movieObject = JSONUtils.selectObject("movie", json);
     List<JSONObject> medias = JSONUtils.selectList("media", movieObject);

     List<ImageInfo> images = new ArrayList<ImageInfo>();
     if (medias != null) {
     for (JSONObject media : medias) {
     if ("picture".equals(JSONUtils.selectString("class", media))) {
     Integer code = JSONUtils.selectInteger("code", JSONUtils.selectObject("type", media));
     Map<ImageProperty, String> imageFields = new EnumMap<ImageProperty, String>(ImageProperty.class);
     ImageCategoryProperty category;
     if (code == 31001) {
     // affiche
     category = ImageCategoryProperty.thumb;
     } else if (code == 31006) {
     // photo
     category = ImageCategoryProperty.fanart;
     } else {
     category = ImageCategoryProperty.unknown;
     }
     imageFields.put(ImageProperty.url, JSONUtils.selectString("href", JSONUtils.selectObject("thumbnail", media)));
     imageFields.put(ImageProperty.desc, JSONUtils.selectString("title", media));
     images.add(new ImageInfo(imageFields, category));
     }
     }
     }*/

    return images;
  }

  private IdInfo imdbIdLookup(IdInfo alloId) {
    try {
      Document dom = URIRequest.getHtmlDocument(new URL("http", imdbIdLookupHost, "/scraper/index2.php?Page=ViewMovie&ID=" + alloId.getId()).toURI());
      try {
        String id = XPathUtils.getAttribute("href", XPathUtils.selectNode("//A[contains(@href, 'imdb.com/')]", dom));

        if (id != null && !id.equals("")) {
          Matcher matcher = imdbIdPattern.matcher(id);
          if (matcher.find()) {
            return new IdInfo(Integer.parseInt(matcher.group(1)), ScrapperUtils.AvailableApiIds.IMDB);
          }
        }
      } catch (NullPointerException Ex) {
        // Imdb id not found
      }
    } catch (URISyntaxException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (SAXException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (Exception ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }

    return null;
  }

  private String[] explodeValue(String value) {
    String res = value.replace("\n", "").replaceAll("\\s+", " ");
    res = res.replace(", ", ",");
    return res.split(",");
  }
}
