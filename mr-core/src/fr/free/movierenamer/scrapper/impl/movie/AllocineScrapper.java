/*
 * movie-renamer-core
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
package fr.free.movierenamer.scrapper.impl.movie;

import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.CastingInfo.PersonProperty;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.MovieInfo.MovieProperty;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.ClassUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.ScrapperUtils.AvailableApiIds;
import fr.free.movierenamer.utils.URIRequest;
import fr.free.movierenamer.utils.XPathUtils;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Class AllocineScrapper : search movie on allocine
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class AllocineScrapper extends MovieScrapper {

  //http://www.allocine.fr/film/fichefilm_gen_cfilm=28546.html
  private static final String host = "www.allocine.fr";
  private static final String imageHost = "images.allocine.fr";
  private static final String name = "Allocine";
  private static final Pattern allocineID = Pattern.compile(".*gen_cfilm=(\\d+).*");
  private static final Pattern allocinePersonID = Pattern.compile(".*cpersonne=(\\d+).*");
  private static final Pattern yearPattern = Pattern.compile("\\d{4}");

  private enum InfoTag {

    Date_de_sortie,
    Réalisé_par,
    Genre,
    Nationalité,
    Spectateurs,
    Titre_original,
    Budget,
    Distributeur
  }

  private enum JobTag {

    director,
    actors
  }

  public AllocineScrapper() {
    super(AvailableLanguages.fr);
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
  protected Locale getDefaultLanguage() {
    return Locale.FRENCH;
  }

  @Override
  protected List<Movie> searchMedia(String query, Locale language) throws Exception {
    URL searchUrl = new URL("http", host, "/recherche/1/?q=" + URIRequest.encode(query));
    return searchMedia(searchUrl, language);
  }

  @Override
  protected List<Movie> searchMedia(URL searchUrl, Locale language) throws Exception {
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI());

    // select movie results
    List<Node> nodes = XPathUtils.selectNodes("//TABLE[@class='totalwidth noborder purehtml']//TR", dom);
    List<Movie> results = new ArrayList<Movie>();

    for (Node node : nodes) {
      Node retNode = XPathUtils.selectNode("TD/A", node);
      if (retNode == null) {// Not a movie
        continue;
      }

      Matcher m = allocineID.matcher(XPathUtils.getAttribute("href", retNode));
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
          thumb = new URL("http", imageHost, res.replaceAll(".*\\/medias", "/medias"));
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

      results.add(new Movie(new IdInfo(id, AvailableApiIds.ALLOCINE), title, originalTitle, thumb, Integer.parseInt(year)));
    }

    return results;
  }

  private String[] explodeValue(String value) {
    String res = value.replace("\n", "").replaceAll("\\s+", " ");
    res = res.replace(", ", ",");
    return res.split(",");
  }

  @Override
  protected MovieInfo fetchMediaInfo(Movie movie, Locale language) throws Exception {
    URL searchUrl = new URL("http", host, "/film/fichefilm_gen_cfilm=" + movie.getMediaId() + ".html");
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI());

    List<String> genres = new ArrayList<String>();
    List<Locale> countries = new ArrayList<Locale>();
    List<String> studios = new ArrayList<String>();
    Map<MovieProperty, String> fields = new EnumMap<MovieProperty, String>(MovieProperty.class);

    fields.put(MovieProperty.title, XPathUtils.selectString("//DIV[@id='title']//SPAN", dom).trim());

    List<Node> nodes = XPathUtils.selectNodes("//DIV[@class='content']/UL/LI", dom);
    for (Node node : nodes) {
      InfoTag tag;
      try {
        tag = InfoTag.valueOf(XPathUtils.selectString("SPAN", node).trim().replace(" ", "_"));
      } catch (Exception ex) {
        continue;
      }

      switch (tag) {
        case Date_de_sortie:
          Node retNode = XPathUtils.selectNode("//SPAN[@itemprop='datePublished']", node);
          if (retNode != null) {
            fields.put(MovieProperty.releasedDate, XPathUtils.getAttribute("content", retNode));
          }

          retNode = XPathUtils.selectNode("//SPAN[@itemprop='duration']", node);
          if (retNode != null) {
            Matcher m = Pattern.compile("(\\d+)h (\\d+)min").matcher(retNode.getTextContent().trim());
            if (m.find()) {
              fields.put(MovieProperty.runtime, String.valueOf(Integer.parseInt(m.group(1)) * 60 + Integer.parseInt(m.group(2))));
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
      InfoTag tag;
      try {
        tag = InfoTag.valueOf(node.getTextContent().trim().replace(" ", "_"));
      } catch (Exception ex) {
        continue;
      }

      switch (tag) {
        case Titre_original:
          fields.put(MovieProperty.originalTitle, XPathUtils.selectString("*", node.getNextSibling()).trim());
          break;
        case Budget:
          String budget = XPathUtils.selectString("text()", node.getNextSibling()).trim();
          if (!budget.equals("-")) {
            fields.put(MovieProperty.budget, budget);
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
      fields.put(MovieProperty.rating, String.valueOf(rate));
    }

    retNode = XPathUtils.selectNode("//SPAN[@itemprop='ratingCount']", dom);
    if (retNode != null) {
      fields.put(MovieProperty.votes, retNode.getTextContent().trim());
    }

    retNode = XPathUtils.selectNode("//DIV[@class='margin_20b']/P[@itemprop='description']", dom);
    if (retNode != null) {
      fields.put(MovieProperty.overview, retNode.getTextContent().trim());
    }

    retNode = XPathUtils.selectNode("//DIV[@class='poster']/IMG[@itemprop='image']", dom);
    if (retNode != null) {
      String url = XPathUtils.getAttribute("src", retNode);
      fields.put(MovieProperty.posterPath, url.replaceAll(".*\\/medias", "http://" + imageHost + "/medias"));
    }

    List<IdInfo> ids = new ArrayList<IdInfo>();
    ids.add(movie.getId());

    MovieInfo movieInfo = new MovieInfo(fields, ids, genres, countries, studios);
    return movieInfo;
  }

  // TODO
 /* @Override
   protected List<ImageInfo> getScrapperImages(Movie movie, Locale language) throws Exception {

   //    URL searchUrl = new URL("http", host, "/rest/v" + version + "/movie?partner=" + apikey + "&profile=large&filter=movie&striptags=synopsis,synopsisshort&format=json&code=" + movie.getMediaId());
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
   }

   return images;
   }
   */
  @Override
  protected List<CastingInfo> fetchCastingInfo(Movie movie, Locale language) throws Exception {

    URL searchUrl = new URL("http", host, "/film/fichefilm-" + movie.getMediaId() + "/casting/");
    List<CastingInfo> casting = new ArrayList<CastingInfo>();
    try {
      Document dom = URIRequest.getHtmlDocument(searchUrl.toURI());

      List<Node> nodes = XPathUtils.selectNodes("//DIV[@class='media_list_02 media_list_hl margin_10b']/UL/LI", dom);
      for (Node node : nodes) {
        String job = XPathUtils.getAttribute("itemprop", node);

        JobTag tag;
        try {
          tag = JobTag.valueOf(job);
        } catch (Exception ex) {
          continue;
        }

        Map<PersonProperty, String> personFields = new EnumMap<PersonProperty, String>(PersonProperty.class);
        String img = XPathUtils.getAttribute("src", XPathUtils.selectNode("SPAN/IMG", node));
        if (!img.contains("empty_photo")) {
          personFields.put(PersonProperty.picturePath, img);
        }

        personFields.put(PersonProperty.name, XPathUtils.selectString("P/A[@itemprop='url']/SPAN", node).trim());
        String url = XPathUtils.getAttribute("href", XPathUtils.selectNode("P/A[@itemprop='url']", node));
        Matcher m = allocinePersonID.matcher(url);
        if (m.find()) {
          personFields.put(PersonProperty.id, m.group(1));
        }

        switch (tag) {
          case actors:
            String character = XPathUtils.selectString("P[@class='fs11 lighten_hl']", node);
            character = character.replaceAll(".*:", "").trim();
            personFields.put(PersonProperty.character, character);
            personFields.put(PersonProperty.job, CastingInfo.ACTOR);
            break;
          case director:
            personFields.put(PersonProperty.job, CastingInfo.DIRECTOR);
            break;
        }
        casting.add(new CastingInfo(personFields));
      }

    } catch (Exception ex) {// No casting
      Settings.LOGGER.log(Level.SEVERE, ClassUtils.getStackTrace(ex));
    }

    return casting;
  }
}
