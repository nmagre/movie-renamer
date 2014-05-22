/*
 * movie-renamer-core
 * Copyright (C) 2012-2014 Nicolas Magré
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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.CastingInfo.PersonProperty;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.ImageInfo.ImageCategoryProperty;
import fr.free.movierenamer.info.ImageInfo.ImageProperty;
import fr.free.movierenamer.info.MediaInfo.MediaProperty;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.MovieInfo.MovieMultipleProperty;
import fr.free.movierenamer.info.MovieInfo.MovieProperty;
import fr.free.movierenamer.scrapper.MovieScrapper;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.ScrapperUtils;
import fr.free.movierenamer.utils.StringUtils;
import fr.free.movierenamer.utils.URIRequest;
import fr.free.movierenamer.utils.XPathUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.w3c.dom.NodeList;

/**
 * Class IMDbScrapper : search movie on IMDB
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class IMDbScrapper extends MovieScrapper {

  private static final String host = "www.imdb.com";
  private static final String name = "IMDb";
  private static final Pattern blacklist = Pattern.compile("\\((TV Series|Video Game|TV Mini-Series|TV Special|TV|TV Episode)\\)");
  private static final Pattern mpaaCodePattern = Pattern.compile("Rated ([RPGN][GC]?(?:-\\d{2})?)");

  public IMDbScrapper() {
    super(AvailableLanguages.en);
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
  protected AvailableLanguages getDefaultLanguage() {
    return AvailableLanguages.en;
  }

  private String createImgPath(String imgPath) {
    if (imgPath == null) {
      return StringUtils.EMPTY;
    }
    return imgPath.replaceAll("@@\\._.*?\\.jpg", "@@.jpg").replaceAll("._V1_S[XY]\\d+.*\\.jpg", ".jpg");
  }

  private URIRequest.RequestProperty getRequestProperties(AvailableLanguages language) {
    return new URIRequest.RequestProperty("Accept-Language", String.format("%s-%s", language.getLocale().getLanguage(), language.getLocale().getCountry()));
  }

  @Override
  protected List<Movie> searchMedia(String query, AvailableLanguages language) throws Exception {
    // http://www.imdb.com/find?s=tt&ref_=fn_tt&q=
    // Only title -> ref_=fn_tt
    // Only movie -> ref_=fn_ft
    URL searchUrl = new URL("http", host, "/find?s=all&q=" + URIRequest.encode(query));
    return searchMedia(searchUrl, language);
  }

  @Override
  protected List<Movie> searchMedia(URL searchUrl, AvailableLanguages language) throws Exception {
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI(), getRequestProperties(language));

    // select movie results
    List<Node> nodes = XPathUtils.selectNodes("//TABLE[@class='findList']//TR", dom);
    List<Movie> results = new ArrayList<Movie>(nodes.size());

    for (Node node : nodes) {
      try {
        Node retNode = XPathUtils.selectNode("TD[@class='result_text']", node);
        if (blacklist.matcher(retNode.getTextContent()).find()) {
          continue;
        }

        String title = XPathUtils.selectNode("A", retNode).getTextContent().trim();
        Matcher m = Pattern.compile("\\((\\d{4}).*\\)").matcher(retNode.getTextContent());
        String year;
        if (m.find()) {
          year = m.group(1);
        } else {
          year = "-1";
        }

        String href = XPathUtils.getAttribute("href", XPathUtils.selectNode("A", retNode));
        int imdbid = findImdbId(href);
        URL thumb;
        try {
          String imgPath = XPathUtils.getAttribute("src", XPathUtils.selectNode("TD[@class='primary_photo']/A/IMG", node));
          if (!imgPath.contains("nopicture")) {
            thumb = new URL(createImgPath(imgPath));
          } else {
            thumb = null;
          }
        } catch (Exception ex) {
          thumb = null;
        }

        results.add(new Movie(new IdInfo(imdbid, ScrapperUtils.AvailableApiIds.IMDB), null, title, null, thumb, Integer.parseInt(year)));
      } catch (Exception e) {
        // ignore
      }
    }

    // we might have been redirected to the movie page
    if (results.isEmpty()) {
      try {
        int imdbid = findImdbId(XPathUtils.selectString("//LINK[@rel='canonical']/@href", dom));
        MovieInfo info = fetchMediaInfo(new Movie(new IdInfo(imdbid, ScrapperUtils.AvailableApiIds.IMDB), null, null, null, null, -1), language);
        URL thumb;
        try {
          String imgPath = info.getPosterPath().toURL().toExternalForm();
          thumb = new URL(createImgPath(imgPath));
        } catch (Exception ex) {
          thumb = null;
        }

        Movie movie = new Movie(new IdInfo(imdbid, ScrapperUtils.AvailableApiIds.IMDB), null, info.getTitle(), null, thumb, info.getYear());
        results.add(movie);

      } catch (Exception e) {
        // ignore, can't find movie
      }
    }

    return results;
  }

  @Override
  protected MovieInfo fetchMediaInfo(Movie movie, AvailableLanguages language) throws Exception {

    final Map<MediaProperty, String> mediaFields = new EnumMap<MediaProperty, String>(MediaProperty.class);
    final Map<MovieProperty, String> fields = new EnumMap<MovieProperty, String>(MovieProperty.class);
    final Map<MovieMultipleProperty, List<String>> multipleFields = new EnumMap<MovieMultipleProperty, List<String>>(MovieMultipleProperty.class);
    final List<String> genres = new ArrayList<String>();
    final List<String> countries = new ArrayList<String>();
    final List<String> studios = new ArrayList<String>();
    final List<String> tags = new ArrayList<String>();
    final List<IdInfo> ids = new ArrayList<IdInfo>();

    URL searchUrl = new URL("http", host, String.format("/title/%s/combined", movie.getImdbId()));
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI(), getRequestProperties(language));

    Node node = XPathUtils.selectNode("//H1", dom);
    ScrapperUtils.setTitle(mediaFields, movie, node);

    // Year
    String syear = XPathUtils.selectString("//SPAN/A[contains(@href,'year')]", node);
    if (syear != null) {
      Pattern pattern = Pattern.compile("\\d{4}");
      Matcher matcher = pattern.matcher(syear);
      if (matcher.find()) {
        ScrapperUtils.addValue(mediaFields, MediaProperty.year, syear);
      }
    }

    // Release date
    String rdate = getH5Content(dom, "Release Date", null);
    if (rdate != null && !rdate.isEmpty()) {
      if (rdate.contains("(")) {
        rdate = rdate.substring(0, rdate.indexOf("(") - 1).trim();
      }

      try {
        SimpleDateFormat sformat = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
        Date date = sformat.parse(rdate);
        sformat.applyPattern("yyyy-MM-dd");
        ScrapperUtils.addValue(fields, MovieProperty.releasedDate, sformat.format(date).toString());
      } catch (ParseException ex) {
      }
    }

    // Original title
    String originalTitle = XPathUtils.selectString("//SPAN[@class='title-extra']/I[contains(., '(original title)')]/preceding-sibling::text()", node);
    ScrapperUtils.addValue(fields, MovieProperty.originalTitle, originalTitle);

    // Rating
    String rate = XPathUtils.selectString("//DIV[@class='starbar-meta']/B", dom);
    if (rate.contains("/")) {
      String[] rateVal = rate.split("\\/");
      if (rateVal.length > 0) {
        ScrapperUtils.addValue(mediaFields, MediaProperty.rating, rateVal[0]);
      }
    }

    // Votes
    String votes = XPathUtils.selectString("//DIV[@class='starbar-meta']/A[@href='ratings']", dom);
    if (votes != null && votes.contains(" votes")) {
      ScrapperUtils.addValue(fields, MovieProperty.votes, votes.replaceAll(" .*", ""));
    }

    // Runtime
    String runtime = getH5Content(dom, "Runtime", null);
    if (runtime != null && !runtime.isEmpty()) {
      Pattern pattern = Pattern.compile("(\\d{2,3}) min");
      Matcher matcher = pattern.matcher(runtime);
      if (matcher.find()) {
        ScrapperUtils.addValue(fields, MovieProperty.runtime, matcher.group(1));
      }
    }

    // Mpaa
    String mpaa = getH5Content(dom, "MPAA", null);
    if (mpaa != null && !mpaa.isEmpty()) {
      fields.put(MovieProperty.certification, mpaa);
      Matcher matcher = mpaaCodePattern.matcher(mpaa);
      if (matcher.find()) {
        ScrapperUtils.addValue(fields, MovieProperty.certificationCode, matcher.group(1));
        ScrapperUtils.addValue(fields, MovieProperty.certification, mpaa);
      }
    } else {// TODO certification (for france, russia, ...)
      List<Node> nodes = XPathUtils.selectNodes("//DIV[@class='info']/H5[contains(., 'Certification')]/parent::node()//DIV/descendant::text()"
              + "[not(ancestor::I) and normalize-space(.) != '\n' and normalize-space(.) != '|' and . != ' ']", dom);
      for (Node pnode : nodes) {
        String cert = pnode.getTextContent();
        if (cert != null && cert.contains("USA:")) {
          ScrapperUtils.addValue(fields, MovieProperty.certificationCode, cert.split(":")[1]);
          break;
        }
      }
    }

    // Studio
    Node nstudios = XPathUtils.selectNode("//DIV[@id='tn15content']//B[@class='blackcatheader' and contains(., 'Production Companies')]", dom);
    if (nstudios != null) {
      NodeList nl = nstudios.getNextSibling().getChildNodes();
      for (int i = 0; i < nl.getLength(); i++) {
        String studio = nl.item(i).getTextContent();
        studio = ScrapperUtils.getValue(studio);
        if (studio != null) {
          studios.add(StringUtils.removeBrackets(studio).trim());
        }
      }
    }

    // Thumb
    try {
      String posterPath = XPathUtils.getAttribute("src", XPathUtils.selectNode("//DIV[@class='photo']/A[@name='poster']/IMG", dom));
      ScrapperUtils.addValue(fields, MovieProperty.posterPath, createImgPath(posterPath));
    } catch (Exception ex) {
      // No thumb
    }

    // Tagline
    ScrapperUtils.addValue(fields, MovieProperty.tagline, getH5Content(dom, "Tagline", "/text()"));

    // Overview
    String overview = getH5Content(dom, "Plot", "/text()");
    if (overview != null && !overview.isEmpty()) {
      if (overview.endsWith("|") && overview.length() > 2) {
        overview = overview.substring(0, overview.length() - 2);
      }
      overview = overview.replaceAll("\\t", "").replaceAll("^\\n", "").trim();
      ScrapperUtils.addValue(fields, MovieProperty.overview, overview);
    }

    // Genre
    List<Node> ngenres = XPathUtils.selectNodes("//DIV[@class='info']//A[contains(@href, 'Genres')]", dom);
    String sgrenre;
    for (Node genre : ngenres) {
      sgrenre = ScrapperUtils.getValue(genre);
      if (sgrenre != null) {
        genres.add(sgrenre);
      }
    }

    // Country
    List<Node> ncountries = XPathUtils.selectNodes("//DIV[@class='info']//A[contains(@href, 'country')]", dom);
    String scountry;
    for (Node country : ncountries) {
      scountry = ScrapperUtils.getValue(country);
      if (scountry != null) {
        countries.add(scountry);
      }
    }

    // Tag
    List<Node> ntags = XPathUtils.selectNodes("//DIV[@class='info']//A[contains(@href, '/keyword/')]", dom);
    String stag;
    for (Node ntag : ntags) {
      stag = ScrapperUtils.getValue(ntag);
      if (stag != null) {
        tags.add(stag);
      }
    }

    // Overview (long version)
    Node plot = XPathUtils.selectNode(String.format("//A[@href='/title/%s/plotsummary']", movie.getImdbId()), dom);
    if (plot != null) {
      searchUrl = new URL("http", host, String.format("/title/%s/plotsummary", movie.getImdbId()));
      dom = URIRequest.getHtmlDocument(searchUrl.toURI(), getRequestProperties(language));
      node = XPathUtils.selectNode("//DIV[@id='main']//LI/P", dom);
      if (node != null) {
        overview = node.getTextContent();
        overview = overview.replaceAll("\\t", "").replaceAll("^\\n", "");
        ScrapperUtils.addValue(fields, MovieProperty.overview, overview);
      }
    }

    ids.add(movie.getImdbId());

    multipleFields.put(MovieInfo.MovieMultipleProperty.studios, studios);
    multipleFields.put(MovieInfo.MovieMultipleProperty.tags, tags);
    multipleFields.put(MovieInfo.MovieMultipleProperty.countries, countries);
    multipleFields.put(MovieInfo.MovieMultipleProperty.genres, genres);

    return new MovieInfo(mediaFields, ids, fields, multipleFields);
  }

  private String getH5Content(Node dom, String text, String path) {
    return XPathUtils.selectString("//DIV[@class='info']/H5[contains(., '" + text + "')]/parent::node()//DIV" + (path != null ? path : ""), dom);
  }

  protected int findImdbId(String source) {
    Matcher matcher = Pattern.compile("tt(\\d+{7})").matcher(source);

    if (matcher.find()) {
      return Integer.parseInt(matcher.group(1));
    }

    // not found
    throw new IllegalArgumentException(String.format("Cannot find imdb id: %s", source));
  }

  protected int findCastImdbId(String source) {
    Matcher matcher = Pattern.compile("nm(\\d+{7})").matcher(source);

    if (matcher.find()) {
      return Integer.parseInt(matcher.group(1));
    }

    // not found
    // throw new IllegalArgumentException(String.format("Cannot find cast imdb id: %s", source));
    return 0;
  }

  @Override
  protected List<ImageInfo> getScrapperImages(Movie movie) throws Exception {// FIXMe we don't know image language
    URL searchUrl = new URL("http", host, String.format("/title/%s/mediaindex", movie.getImdbId()));
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI(), getRequestProperties(getDefaultLanguage()));

    List<ImageInfo> images = new ArrayList<ImageInfo>();

    Node node = XPathUtils.selectNode("//A[@href = '?refine=poster']", dom);
    if (node != null) {
      images.addAll(getImages(new URL("http", host, String.format("/title/%s/mediaindex?refine=poster", movie.getImdbId())),
              ImageCategoryProperty.thumb, getDefaultLanguage()));
    }

    node = XPathUtils.selectNode("//A[@href = '?refine=still_frame']", dom);
    if (node != null) {
      images.addAll(getImages(new URL("http", host, String.format("/title/%s/mediaindex?refine=still_frame", movie.getImdbId())),
              ImageCategoryProperty.fanart, getDefaultLanguage()));
    }

    return images;
  }

  private List<ImageInfo> getImages(URL url, ImageCategoryProperty imgtype, AvailableLanguages language) throws Exception {
    Document dom = URIRequest.getHtmlDocument(url.toURI(), getRequestProperties(language));
    List<ImageInfo> images = new ArrayList<ImageInfo>();
    List<Node> nodes = XPathUtils.selectNodes("//DIV[@class='thumb_list']//IMG", dom);

    int count = 0;
    for (Node inode : nodes) {
      Map<ImageProperty, String> imageFields = new EnumMap<ImageProperty, String>(ImageProperty.class);
      String imgUrl = XPathUtils.getAttribute("src", inode).replaceAll("CR[\\d,]+_SS\\d+", "SY214_SX314");
      imageFields.put(ImageProperty.url, imgUrl.replaceAll("S[XY]\\d+(.)+\\.jpg", "SY_SX.jpg"));
      imageFields.put(ImageProperty.urlMid, imgUrl);
      imageFields.put(ImageProperty.urlTumb, createImgPath(imgUrl));
      images.add(new ImageInfo(count++, imageFields, imgtype));
    }
    return images;
  }

  @Override
  protected List<CastingInfo> fetchCastingInfo(Movie movie, AvailableLanguages language) throws Exception {
    URL searchUrl = new URL("http", host, String.format("/title/%s/fullcredits", movie.getImdbId()));
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI(), getRequestProperties(language));

    List<CastingInfo> casting = new ArrayList<CastingInfo>();

    List<Node> castNodes = XPathUtils.selectNodes("//H4", dom);
    for (Node node : castNodes) {
      String type = node.getTextContent();
      String cinfo = null;

      if (type.contains("Directed")) {
        cinfo = CastingInfo.DIRECTOR;
      } else if (type.contains("Writing")) {
        cinfo = CastingInfo.WRITER;
      } else if (type.contains("Cast")) {
        cinfo = CastingInfo.ACTOR;
      }

      if (cinfo == null) {
        continue;
      }

      List<Node> nodes = XPathUtils.selectNodes("following-sibling::TABLE[1]//TR", node);
      for (Node cnode : nodes) {
        Map<PersonProperty, String> personFields = fetchPersonIdAndName(XPathUtils.selectNode("./TD[@class='name' or @class='itemprop']", cnode));
        if (personFields == null) {
          continue;
        }

        personFields.put(PersonProperty.job, cinfo);

        if (cinfo.equals(CastingInfo.WRITER)) {
          if (!cnode.getTextContent().contains("screenplay")) {
            continue;
          }
        }

        if (cinfo.equals(CastingInfo.ACTOR)) {
          Node pictureNode = XPathUtils.selectNode("./TD[@class='primary_photo']/A/IMG", cnode);
          String picture = (pictureNode != null) ? createImgPath(XPathUtils.getAttribute("loadlate", pictureNode)) : "";
          if (picture.contains("nopicture")) {
            picture = "";
          }

          Node characterNode = XPathUtils.selectNode("TD[@class='character']", cnode);
          String character = "";
          if (characterNode != null) {
            character = characterNode.getTextContent().replace("\n", "").replace("\r", "").replace("(uncredited)", "").trim();
            character = character.replace("\t", " ").replaceAll("\\p{Space}+", " ");
          }

          personFields.put(PersonProperty.character, character);
          personFields.put(PersonProperty.picturePath, picture);
        }

        casting.add(new CastingInfo(personFields));
      }
    }

    return casting;
  }

  private Map<PersonProperty, String> fetchPersonIdAndName(Node node) {
    if (node != null) {
      Node link = XPathUtils.selectNode(".//A", node);
      if (link != null) {
        String pname = link.getTextContent();
        if (pname.length() > 1) {
          int imdbId = findCastImdbId(XPathUtils.getAttribute("href", link));
          if (imdbId != 0) {
            Map<PersonProperty, String> personFields = new EnumMap<PersonProperty, String>(PersonProperty.class);
            personFields.put(PersonProperty.id, Integer.toString(imdbId));
            personFields.put(PersonProperty.name, pname.trim());

            return personFields;
          }
        }
      }
    }
    return null;
  }

  @Override
  public ScrapperUtils.InfoQuality getInfoQuality() {
    return ScrapperUtils.InfoQuality.AWESOME;
  }
}
