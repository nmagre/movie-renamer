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
import fr.free.movierenamer.info.CastingInfo.PersonProperty;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.ImageInfo.ImageCategoryProperty;
import fr.free.movierenamer.info.ImageInfo.ImageProperty;
import fr.free.movierenamer.info.MediaInfo.MediaProperty;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.MovieInfo.MotionPictureRating;
import fr.free.movierenamer.info.MovieInfo.MovieMultipleProperty;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.ScrapperUtils;
import fr.free.movierenamer.utils.ScrapperUtils.AvailableApiIds;
import fr.free.movierenamer.utils.StringUtils;
import fr.free.movierenamer.utils.URIRequest;
import fr.free.movierenamer.utils.XPathUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Class AlloGroupScrapper
 *
 * @author Nicolas Magré
 */
public abstract class AlloGroupScrapper extends MovieScrapper {

  private static final Pattern yearPattern = Pattern.compile("\\d{4}");
  private static final Pattern runtimePattern = Pattern.compile("(\\d+)h\\s?(\\d+)min");
  private static final Pattern castIdPattern = Pattern.compile("fichepersonne_gen_cpersonne=(\\d+)\\.html");
  private static final String key = "0A12B34C56D78E9FULONYXTIZKJSHVPWQMGR";
  private static final AvailableApiIds supportedId = AvailableApiIds.ALLOCINE;
  public static final List<AvailableLanguages> avLangs = Arrays.asList(new AvailableLanguages[]{
    AvailableLanguages.fr,
    AvailableLanguages.pt,
    AvailableLanguages.tr,
    AvailableLanguages.de,
    AvailableLanguages.es
  });

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

  protected AlloGroupScrapper(AvailableLanguages lang) {
    super(lang);
  }

  @Override
  public AvailableApiIds getSupportedId() {
    return supportedId;
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
  public IdInfo getIdfromURL(URL url) {
    try {
      return new IdInfo(getId(url), supportedId);
    } catch (Exception ex) {
    }

    return null;
  }

  @Override
  public URL getURL(IdInfo id) {
    try {
      return new URL("http", getHost(), getMoviePageString(id));
    } catch (MalformedURLException ex) {
    }

    return null;
  }

  public static String decodeUrl(String encodedUrl) {

    String res = "";

    for (int i = 0; i < encodedUrl.length(); i += 2) {
      int ch = key.indexOf(encodedUrl.charAt(i));
      int cl = key.indexOf(encodedUrl.charAt(i + 1));

      res += Character.toChars((ch * 16) + cl)[0];
    }
    return res;
  }

  @Override
  protected final List<Movie> searchMedia(String query, AvailableLanguages language) throws Exception {
    URL searchUrl = new URL("http", getHost(), "/" + getSearchString() + "/1/?q=" + URIRequest.encode(query));
    return searchMedia(searchUrl, language);
  }

  @Override
  protected final List<Movie> searchMedia(URL searchUrl, AvailableLanguages language) throws Exception {
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
          thumb = new URL(res);
        }
      } catch (Exception ex) {
        thumb = null;
      }

      Node infoNode = XPathUtils.selectNode("TD[@class='totalwidth']//DIV[@style='margin-top:-5px;']", node);
      String title = XPathUtils.selectNode("A", infoNode).getTextContent().trim();
      String originalTitle = XPathUtils.selectNode("A/following-sibling::text()", infoNode).getTextContent().trim();
      String year = XPathUtils.selectString("SPAN/BR[1]/preceding-sibling::node()", infoNode).trim();

      originalTitle = originalTitle.replace("(", "").replace(")", "");
      originalTitle = originalTitle.equals("") ? title : originalTitle;

      m = yearPattern.matcher(year);
      if (!m.find()) {
        year = "-1";
      }

      results.add(new Movie(null, new IdInfo(id, longid, AvailableApiIds.ALLOCINE), title, originalTitle, thumb, Integer.parseInt(year)));
    }

    // movie page ?
    if (results.isEmpty()) { // TODO a vérifier
      try {
        int alloid = getId(searchUrl);
        String longid = getLongId(searchUrl.toString());
        IdInfo id = new IdInfo(alloid, longid, AvailableApiIds.ALLOCINE);
        MovieInfo info = fetchMediaInfo(new Movie(null, id, null, null, null, -1), id, language);
        URL thumb;
        try {
          thumb = new URL(info.getPosterPath().toURL().toExternalForm());
        } catch (Exception ex) {
          thumb = null;
        }
        Movie movie = new Movie(null, id, info.getTitle(), info.getOriginalTitle(), thumb, info.getYear());

        results.add(movie);

      } catch (Exception e) {
        // ignore, can't find movie
      }
    }

    return results;
  }

  @Override
  protected MovieInfo fetchMediaInfo(Movie movie, IdInfo id, AvailableLanguages language) throws Exception {
    URL searchUrl = new URL("http", getHost(), getMoviePageString(id));
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI());

    Map<MediaProperty, String> mediaFields = new EnumMap<MediaProperty, String>(MediaProperty.class);
    List<String> genres = new ArrayList<String>();
    List<String> countries = new ArrayList<String>();
    List<String> studios = new ArrayList<String>();
    Map<MovieInfo.MovieProperty, String> fields = new EnumMap<MovieInfo.MovieProperty, String>(MovieInfo.MovieProperty.class);
    Map<MovieMultipleProperty, List<String>> multipleFields = new EnumMap<MovieMultipleProperty, List<String>>(
            MovieMultipleProperty.class);

    String title = XPathUtils.selectString("//DIV[@id='title']//SPAN", dom).trim();
    mediaFields.put(MediaProperty.title, title);

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

    List<Node> nodes = XPathUtils.selectNodes("//DIV[@class='content']//TH", dom);
    for (Node node : nodes) {
      InfoTag tag = getInfoTag(XPathUtils.selectString("SPAN", node).replace(" ", "_"));
      switch (tag) {
        case Date_de_sortie:
          Node retNode = XPathUtils.selectNode("//SPAN[@itemprop='datePublished']", node);
          if (retNode != null) {
            String date = XPathUtils.getAttribute("content", retNode);
            Pattern pattern = Pattern.compile("(\\d{4})-\\d{2}-\\d{2}");
            Matcher matcher = pattern.matcher(date);
            if (matcher.find()) {
              mediaFields.put(MediaProperty.year, matcher.group(1));
            }
            fields.put(MovieInfo.MovieProperty.releasedDate, date);
          }

          retNode = XPathUtils.selectNode("../TD//SPAN[@itemprop='duration']", node);
          if (retNode != null) {
            Matcher m = getRuntimePattern().matcher(retNode.getTextContent().trim());
            if (m.find()) {
              fields.put(MovieInfo.MovieProperty.runtime, String.valueOf(Integer.parseInt(m.group(1)) * 60 + Integer.parseInt(m.group(2))));
            }
          }
          break;
        case Genre:
          genres.addAll(Arrays.asList(explodeValue(XPathUtils.selectString("../TD", node))));
          break;
        case Nationalité:
          countries.addAll(parseCountry(XPathUtils.selectNode("../TD", node)));
          break;
      }
    }

    nodes = XPathUtils.selectNodes("//DIV[@class='expendTable']//TABLE//TH", dom);
    for (Node node : nodes) {
      InfoTag tag = getInfoTag(node.getTextContent().replace(" ", "_"));

      switch (tag) {
        case Titre_original:
          mediaFields.put(MediaProperty.originalTitle, XPathUtils.selectString("text()", node.getNextSibling()));
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

    List<Node> retNodes = XPathUtils.selectNodes("//SPAN[@class='note']", dom);
    if (retNodes != null && !retNodes.isEmpty()) {
      Node node = retNodes.size() >= 2 ? retNodes.get(1) : retNodes.get(0);
      Double rating = Double.parseDouble(node.getTextContent().trim().replace(",", "."));
      rating *= 2;
      mediaFields.put(MediaProperty.rating, "" + rating);
    }

    Node retNode = XPathUtils.selectNode("//SPAN[@itemprop='ratingCount']", dom);
    if (retNode != null) {
      fields.put(MovieInfo.MovieProperty.votes, retNode.getTextContent().trim());
    }

    retNode = XPathUtils.selectNode("//DIV[@class='margin_20b']/P[@itemprop='description']", dom);
    if (retNode != null) {
      String overview = retNode.getTextContent().trim();
      if (overview.length() == 0) {
        Node node = retNode.getNextSibling();
        overview = node.getTextContent().trim();
      }
      fields.put(MovieInfo.MovieProperty.overview, overview);
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
    ids.add(movie.getMediaId());
    ids.add(id);

    multipleFields.put(MovieMultipleProperty.studios, studios);
    multipleFields.put(MovieMultipleProperty.tags, tags);
    multipleFields.put(MovieMultipleProperty.countries, countries);
    multipleFields.put(MovieMultipleProperty.genres, genres);

    return new MovieInfo(mediaFields, ids, fields, multipleFields);
  }

  @Override
  protected List<CastingInfo> fetchCastingInfo(Movie movie, IdInfo id, AvailableLanguages language) throws Exception {

    URL searchUrl = new URL("http", getHost(), getCastingPageString(id));
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

      Map<PersonProperty, String> personFields = new EnumMap<PersonProperty, String>(PersonProperty.class
      );
      String img = XPathUtils.getAttribute("src", XPathUtils.selectNode("SPAN/IMG", node));

      ImageInfo imginfo = null;
      if (!img.contains("empty_photo")) {
        String pid = XPathUtils.getAttribute("href", XPathUtils.selectNode("//A", node));
        Matcher matcher = castIdPattern.matcher(pid);
        int cid = pid.hashCode();
        if (matcher.find()) {
          try {
            cid = Integer.parseInt(matcher.group(1));
          } catch (NumberFormatException ex) {
          }
        }

        Map<ImageInfo.ImageProperty, String> fields = new HashMap<ImageInfo.ImageProperty, String>();
        fields.put(ImageProperty.urlTumb, img);
        fields.put(ImageProperty.urlMid, img.replace("r_120_160", "r_640_600"));
        fields.put(ImageProperty.url, img.replace("r_120_160/b_1_d6d6d6/", "").replaceAll("web.img\\d", "web.img6"));

        imginfo = new ImageInfo(cid, fields, ImageCategoryProperty.actor);
      }

      personFields.put(PersonProperty.name, XPathUtils.selectString("P/A", node).trim());

      Node pnode = XPathUtils.selectNode("P/A", node);
      if (pnode != null) {
        String url = XPathUtils.getAttribute("href", pnode);
        Matcher m = getPersonIdPattern().matcher(url);

        if (m.find()) {
          personFields.put(PersonProperty.id, m.group(1));
        }
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
      casting.add(new CastingInfo(personFields, imginfo));
    }

    return casting;
  }

  @Override
  protected List<ImageInfo> fetchImagesInfo(Movie movie) throws Exception {
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    IdInfo imdbId = ScrapperUtils.movieIdLookup(AvailableApiIds.IMDB, movie.getMediaId(), movie);
    if (imdbId != null) {
      movie.setImdbId(imdbId);
      return super.fetchImagesInfo(movie);
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

  protected List<String> parseCountry(Node node) {
    return Arrays.asList(explodeValue(XPathUtils.selectString("DIV", node)));
  }

  private String[] explodeValue(String value) {
    String res = value.replace("\n", "").replaceAll("\\s+", " ");
    res = res.replace(", ", ",");
    return res.split(",");
  }

  @Override
  public ScrapperUtils.InfoQuality getInfoQuality() {
    return ScrapperUtils.InfoQuality.POOR;
  }
}
