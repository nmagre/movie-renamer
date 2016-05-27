/*
 * movie-renamer-core
 * Copyright (C) 2012-2015 Nicolas Magré
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
package fr.free.movierenamer.scraper.impl.movie;

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
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.info.MediaInfo.MediaProperty;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.MovieInfo.MovieMultipleProperty;
import fr.free.movierenamer.info.MovieInfo.MovieProperty;
import fr.free.movierenamer.info.VideoInfo.VideoProperty;
import fr.free.movierenamer.scraper.MovieScraper;
import fr.free.movierenamer.scraper.SearchParam;
import fr.free.movierenamer.searchinfo.Movie;
import fr.free.movierenamer.utils.ClassUtils;
import fr.free.movierenamer.utils.LocaleUtils.AvailableLanguages;
import fr.free.movierenamer.utils.ScraperUtils;
import fr.free.movierenamer.utils.ScraperUtils.AvailableApiIds;
import fr.free.movierenamer.utils.StringUtils;
import fr.free.movierenamer.utils.URIRequest;
import fr.free.movierenamer.utils.XPathUtils;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import org.w3c.dom.NodeList;

/**
 * Class IMDbScraper : search movie on IMDB
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class IMDbScraper extends MovieScraper {

  private static final String host = "www.imdb.com";
  private static final String name = "IMDb";
  private static final Pattern blacklist = Pattern.compile("\\((TV Series|Video Game|TV Mini-Series|TV Special|TV|TV Episode)\\)");
  private static final Pattern mpaaCodePattern = Pattern.compile("Rated ([RPGN][GC]?(?:-\\d{2})?)");
  private static final AvailableApiIds supportedId = AvailableApiIds.IMDB;
  private static final Pattern imageCropPattern = Pattern.compile("\\._.?V1.?_.?([US])([XY])(\\d+)_CR(\\d+),(\\d+),(\\d+),(\\d+)(.*\\.)");
  private static final Pattern imageXYPattern = Pattern.compile("\\._.?V1.?_.?[US]([XY])(\\d+)_[US]([XY])(\\d+)(.*\\.)");
  private static final Pattern imageXPattern = Pattern.compile("\\._.?V1.?_.?[US]([XY])(\\d+)(_.*\\.)");
  private static final Pattern imageCRPattern = Pattern.compile("\\._.?V1.?_.?CR\\d+,\\d+,\\d+,\\d+_U");

  private enum ImageResize {

    THUMB(45, 70),
    SMALL(92, 138),
    MEDIUM(185, 278),
    ORIG;

    private final int x, y;

    private ImageResize() {
      this(0, 0);
    }

    private ImageResize(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public int getX() {
      return x;
    }

    public int getY() {
      return y;
    }

  }

  public IMDbScraper() {
    super(AvailableLanguages.en);
  }

  @Override
  public AvailableApiIds getSupportedId() {
    return supportedId;
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
  public IdInfo getIdfromURL(URL url) {
    try {
      return new IdInfo(findImdbId(url.toExternalForm()), supportedId);
    } catch (Exception ex) {
    }

    return null;
  }

  @Override
  public URL getURL(IdInfo id) {
    try {
      return new URL("http", host, String.format("/title/%s/combined", id));
    } catch (MalformedURLException ex) {
    }

    return null;
  }

  @Override
  protected AvailableLanguages getDefaultLanguage() {
    return AvailableLanguages.en;
  }

  private static String getImageLnk(String lnk, ImageResize rsize) {
    if (lnk == null || lnk.isEmpty()) {
      return "";
    }

    Matcher cr = imageCRPattern.matcher(lnk);
    if (cr.find()) {
      lnk = cr.replaceAll("._V1_U");
    }

    Matcher mcrop = imageCropPattern.matcher(lnk);
    Matcher mxy = imageXYPattern.matcher(lnk);
    Matcher mx = imageXPattern.matcher(lnk);

    if (mcrop.find()) {

      if (rsize == ImageResize.ORIG) {
        return mcrop.replaceAll("._V1_SX1024_SY1024_.");
      }

      boolean invert = mcrop.group(2).equals("Y");
      int x = Integer.parseInt(invert ? mcrop.group(6) : mcrop.group(3));
      int y = Integer.parseInt(invert ? mcrop.group(3) : mcrop.group(7));
      int crop = Integer.parseInt(mcrop.group(4));
      float ratio = (float) x / y;
      int nx, ny;

      if (invert) {
        ny = rsize.getY();
        nx = (int) (ny * ratio);
        crop *= ny / y;
      } else {
        nx = rsize.getX();
        ny = (int) (nx / ratio);
        crop *= nx / x;
      }

      StringBuffer strb = new StringBuffer();
      mcrop.appendReplacement(strb, String.format("._V1_$1$2%s_CR%s,$5,%s,%s$8", invert ? ny : nx, crop, nx, ny));
      mcrop.appendTail(strb);
      return strb.toString();

    } else if (mxy.find()) {

      if (rsize == ImageResize.ORIG) {
        return mx.replaceAll(".");
      }

      StringBuffer strb = new StringBuffer();
      mxy.appendReplacement(strb, String.format("._V1_S$1%s_S$3%s$5", (mxy.group(1).equals("X") ? rsize.getX() : rsize.getY()), (mxy.group(1).equals("X") ? rsize.getY() : rsize.getX())));
      mxy.appendTail(strb);
      return strb.toString();

    } else if (mx.find()) {

      if (rsize == ImageResize.ORIG) {
        return mx.replaceAll(".");
      }

      String nsize = "" + (mx.group(1).equals("X") ? rsize.getX() : rsize.getY());
      StringBuffer strb = new StringBuffer();
      mx.appendReplacement(strb, String.format("._V1_S$1%s$3", nsize));
      mx.appendTail(strb);
      return strb.toString();

    } else {
      String msg = String.format("No pattern found for image : %s \n %s", lnk, ClassUtils.getStackTrace(new Throwable().getStackTrace()));
      System.err.println(msg);
      System.exit(-1);// FIXME remove + logger
    }

    return lnk;
  }

  private URIRequest.RequestProperty getRequestProperties(AvailableLanguages language) {
    return new URIRequest.RequestProperty("Accept-Language", String.format("%s-%s", language.getLocale().getLanguage(), language.getLocale().getCountry()));
  }

  @Override
  protected List<Movie> searchMedia(String query, SearchParam sep, AvailableLanguages language) throws Exception {
    // http://www.imdb.com/find?s=tt&ref_=fn_tt&q=
    // Only title -> ref_=fn_tt
    // Only movie -> ref_=fn_ft
    URL searchUrl = new URL("http", host, "/find?s=all&q=" + URIRequest.encode(query));
    return searchMedia(searchUrl, sep, language);
  }

  @Override
  protected List<Movie> searchMedia(URL searchUrl, SearchParam sep, AvailableLanguages language) throws Exception {
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI(), getRequestProperties(language));

    // select movie results
    List<Node> nodes = XPathUtils.selectNodes("//TABLE[@class='findList']//TR", dom);
    List<Movie> results = new ArrayList<>(nodes.size());

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
            System.out.println(imgPath);
            System.out.println(getImageLnk(imgPath, ImageResize.THUMB));
            System.out.println();
            thumb = new URL(getImageLnk(imgPath, ImageResize.THUMB));
          } else {
            thumb = null;
          }
        } catch (Exception ex) {
          thumb = null;
        }

        results.add(new Movie(new IdInfo(imdbid, AvailableApiIds.IMDB), null, title, null, thumb, Integer.parseInt(year)));
      } catch (Exception e) {
        // ignore
      }
    }

    // we might have been redirected to the movie page
    if (results.isEmpty()) {
      try {
        int imdbid = findImdbId(XPathUtils.selectString("//LINK[@rel='canonical']/@href", dom));
        IdInfo id = new IdInfo(imdbid, AvailableApiIds.IMDB);
        MovieInfo info = fetchMediaInfo(new Movie(id, null, null, null, null, -1), id, language);
        URL thumb;
        try {
          String imgPath = info.getPosterPath().toURL().toExternalForm();
          System.out.println(imgPath);
          thumb = new URL(getImageLnk(imgPath, ImageResize.THUMB));
        } catch (Exception ex) {
          thumb = null;
        }

        Movie movie = new Movie(new IdInfo(imdbid, AvailableApiIds.IMDB), null, info.getTitle(), null, thumb, info.getYear());
        results.add(movie);

      } catch (Exception e) {
        // ignore, can't find movie
      }
    }

    return results;
  }

  @Override
  protected MovieInfo fetchMediaInfo(Movie movie, IdInfo id, AvailableLanguages language) throws Exception {

    Map<MediaInfo.MediaInfoProperty, String> info = new HashMap<>();
    Map<MovieMultipleProperty, List<String>> multipleFields = new EnumMap<>(MovieMultipleProperty.class);
    List<String> genres = new ArrayList<>();
    List<String> countries = new ArrayList<>();
    List<String> studios = new ArrayList<>();
    List<String> tags = new ArrayList<>();
    List<IdInfo> ids = new ArrayList<>();

    URL searchUrl = new URL("http", host, String.format("/title/%s/combined", id));
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI(), getRequestProperties(language));

    Node node = XPathUtils.selectNode("//H1", dom);
    ScraperUtils.setTitle(info, movie, node);

    // Year
    String syear = XPathUtils.selectString("//SPAN/A[contains(@href,'year')]", node);
    if (syear != null) {
      Pattern pattern = Pattern.compile("\\d{4}");
      Matcher matcher = pattern.matcher(syear);
      if (matcher.find()) {
        ScraperUtils.addValue(info, MediaProperty.year, syear);
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
        ScraperUtils.addValue(info, VideoProperty.releasedDate, sformat.format(date));
      } catch (ParseException ex) {
      }
    }

    // Original title
    String originalTitle = XPathUtils.selectString("//SPAN[@class='title-extra']/I[contains(., '(original title)')]/preceding-sibling::text()", node);
    ScraperUtils.addValue(info, MediaProperty.originalTitle, originalTitle);

    // Rating
    String rate = XPathUtils.selectString("//DIV[@class='starbar-meta']/B", dom);
    if (rate.contains("/")) {
      String[] rateVal = rate.split("\\/");
      if (rateVal.length > 0) {
        ScraperUtils.addValue(info, MediaProperty.rating, rateVal[0]);
      }
    }

    // Votes
    String votes = XPathUtils.selectString("//DIV[@class='starbar-meta']/A[@href='ratings']", dom);
    if (votes != null && votes.contains(" votes")) {
      ScraperUtils.addValue(info, MovieProperty.votes, votes.replaceAll(" .*", ""));
    }

    // Runtime
    String runtime = getH5Content(dom, "Runtime", null);
    if (runtime != null && !runtime.isEmpty()) {
      Pattern pattern = Pattern.compile("(\\d{2,3}) min");
      Matcher matcher = pattern.matcher(runtime);
      if (matcher.find()) {
        ScraperUtils.addValue(info, VideoProperty.runtime, matcher.group(1));
      }
    }

    // Mpaa
    String mpaa = getH5Content(dom, "MPAA", null);
    if (mpaa != null && !mpaa.isEmpty()) {
      info.put(MovieProperty.certification, mpaa);
      Matcher matcher = mpaaCodePattern.matcher(mpaa);
      if (matcher.find()) {
        ScraperUtils.addValue(info, MovieProperty.certificationCode, matcher.group(1));
        ScraperUtils.addValue(info, MovieProperty.certification, mpaa);
      }
    } else {// TODO certification (for france, russia, ...)
      List<Node> nodes = XPathUtils.selectNodes("//DIV[@class='info']/H5[contains(., 'Certification')]/parent::node()//DIV/descendant::text()"
        + "[not(ancestor::I) and normalize-space(.) != '\n' and normalize-space(.) != '|' and . != ' ']", dom);
      for (Node pnode : nodes) {
        String cert = pnode.getTextContent();
        if (cert != null && cert.contains("USA:")) {
          ScraperUtils.addValue(info, MovieProperty.certificationCode, cert.split(":")[1]);
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
        studio = ScraperUtils.getValue(studio);
        if (studio != null) {
          studios.add(StringUtils.removeBrackets(studio).trim());
        }
      }
    }

    // Thumb
    try {
      String posterPath = XPathUtils.getAttribute("src", XPathUtils.selectNode("//DIV[@class='photo']/A[@name='poster']/IMG", dom));
      ScraperUtils.addValue(info, MovieProperty.posterPath, getImageLnk(posterPath, ImageResize.ORIG));
    } catch (Exception ex) {
      // No thumb
    }

    // Tagline
    ScraperUtils.addValue(info, MovieProperty.tagline, getH5Content(dom, "Tagline", "/text()"));

    // Overview
    String overview = getH5Content(dom, "Plot", "/text()");
    if (overview != null && !overview.isEmpty()) {
      if (overview.endsWith("|") && overview.length() > 2) {
        overview = overview.substring(0, overview.length() - 2);
      }
      overview = overview.replaceAll("\\t", "").replaceAll("^\\n", "").trim();
      ScraperUtils.addValue(info, MovieProperty.overview, overview);
    }

    // Genre
    ScraperUtils.getMultipleValues(genres, "//DIV[@class='info']//A[contains(@href, 'Genres')]", dom);

    // Country
    ScraperUtils.getMultipleValues(countries, "//DIV[@class='info']//A[contains(@href, 'country')]", dom);

    // Tag
    ScraperUtils.getMultipleValues(tags, "//DIV[@class='info']//A[contains(@href, '/keyword/')]", dom);

    // Overview (long version)
    Node plot = XPathUtils.selectNode(String.format("//A[@href='/title/%s/plotsummary']", movie.getImdbId()), dom);
    if (plot != null) {
      searchUrl = new URL("http", host, String.format("/title/%s/plotsummary", movie.getImdbId()));
      dom = URIRequest.getHtmlDocument(searchUrl.toURI(), getRequestProperties(language));
      node = XPathUtils.selectNode("//DIV[@id='main']//LI/P", dom);
      if (node != null) {
        overview = node.getTextContent();
        overview = overview.replaceAll("\\t", "").replaceAll("^\\n", "");
        ScraperUtils.addValue(info, MovieProperty.overview, overview);
      }
    }

    ids.add(movie.getImdbId());

    multipleFields.put(MovieInfo.MovieMultipleProperty.studios, studios);
    multipleFields.put(MovieInfo.MovieMultipleProperty.tags, tags);
    multipleFields.put(MovieInfo.MovieMultipleProperty.countries, countries);
    multipleFields.put(MovieInfo.MovieMultipleProperty.genres, genres);

    return new MovieInfo(info, multipleFields, ids);
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
  protected List<ImageInfo> getScraperImages(Movie movie) throws Exception {// FIXME we don't know image language
    URL searchUrl = new URL("http", host, String.format("/title/%s/mediaindex", movie.getImdbId()));
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI(), getRequestProperties(getDefaultLanguage()));

    List<ImageInfo> images = new ArrayList<>();

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
    List<ImageInfo> images = new ArrayList<>();
    List<Node> nodes = XPathUtils.selectNodes("//DIV[@class='thumb_list']//IMG", dom);

    int count = 0;
    for (Node inode : nodes) {
      Map<ImageProperty, String> imageFields = new EnumMap<>(ImageProperty.class);
      System.out.println(XPathUtils.getAttribute("src", inode));
      String imgUrl = XPathUtils.getAttribute("src", inode);
      imageFields.put(ImageProperty.url, getImageLnk(imgUrl, ImageResize.ORIG));
      imageFields.put(ImageProperty.urlMid, getImageLnk(imgUrl, ImageResize.MEDIUM));
      imageFields.put(ImageProperty.urlTumb, getImageLnk(imgUrl, ImageResize.SMALL));
      images.add(new ImageInfo(count++, imageFields, imgtype));
    }
    return images;
  }

  @Override
  protected List<CastingInfo> fetchCastingInfo(Movie movie, IdInfo id, AvailableLanguages language) throws Exception {
    URL searchUrl = new URL("http", host, String.format("/title/%s/fullcredits", id));
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI(), getRequestProperties(language));

    List<CastingInfo> casting = new ArrayList<>();

    List<Node> castNodes = XPathUtils.selectNodes("//H4", dom);
    for (Node node : castNodes) {
      String type = node.getTextContent();
      String cinfo = null;

      if (type.contains("Directed")) {
        cinfo = CastingInfo.DIRECTOR;
      } else if (type.contains("Writing Credits")) {
        cinfo = CastingInfo.WRITER;
      } else if (type.contains("Cast") && !type.contains("Casting")) {
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

        ImageInfo imgInfo = null;
        if (cinfo.equals(CastingInfo.ACTOR)) {

          Node pictureNode = XPathUtils.selectNode("./TD[@class='primary_photo']/A/IMG", cnode);
          if (pictureNode != null) {

            String picture = XPathUtils.getAttribute("loadlate", pictureNode);
            if (picture != null && !picture.contains("nopicture") && !picture.isEmpty()) {
              Map<ImageInfo.ImageProperty, String> fields = new HashMap<>();
              fields.put(ImageProperty.url, getImageLnk(picture, ImageResize.ORIG));
              fields.put(ImageProperty.urlMid, getImageLnk(picture, ImageResize.MEDIUM));
              fields.put(ImageProperty.urlTumb, getImageLnk(picture, ImageResize.SMALL));

              int cid = picture.hashCode();
              if (personFields.get(PersonProperty.id) != null) {
                cid = Integer.parseInt(personFields.get(PersonProperty.id));
              }
              imgInfo = new ImageInfo(cid, fields, ImageCategoryProperty.actor);
            }
          }

          Node characterNode = XPathUtils.selectNode("TD[@class='character']", cnode);
          String character = "";
          if (characterNode != null) {
            character = characterNode.getTextContent().replace("\n", "").replace("\r", "").replace("(uncredited)", "").trim();
            character = character.replace("\t", " ").replaceAll("\\p{Space}+", " ");
          }

          personFields.put(PersonProperty.character, character);
        }

        if (cinfo.equals(CastingInfo.DIRECTOR) || cinfo.equals(CastingInfo.WRITER)) {
          if (personFields.get(PersonProperty.id) != null) {
            try {
              imgInfo = getCastImage(Integer.parseInt(personFields.get(PersonProperty.id)), language);
            } catch (Exception ex) {
            }
          }
        }

        casting.add(new CastingInfo(personFields, imgInfo));
      }
    }

    return casting;
  }

//  private Map<ImageInfo.ImageProperty, String> getImageUrl(String picture) {
//    Map<ImageInfo.ImageProperty, String> fields = new EnumMap<>(ImageInfo.ImageProperty.class);
//    System.out.println(picture);
//    fields.put(ImageProperty.url, picture);
//    fields.put(ImageProperty.urlMid, picture.replaceAll("@@\\._.*?\\.jpg", "@@._V1_SX214.jpg").replaceAll("._V1_S[XY]\\d+.*\\.jpg", "._V1_SX214.jpg"));
//    fields.put(ImageProperty.urlTumb, picture.replaceAll("@@\\._.*?\\.jpg", "@@._V1_SX70.jpg").replaceAll("._V1_S[XY]\\d+.*\\.jpg", "._V1_SX70.jpg"));
//
//    return fields;
//  }
  private Map<PersonProperty, String> fetchPersonIdAndName(Node node) {
    if (node != null) {
      Node link = XPathUtils.selectNode(".//A", node);
      if (link != null) {
        String pname = link.getTextContent();
        if (pname.length() > 1) {
          int imdbId = findCastImdbId(XPathUtils.getAttribute("href", link));
          if (imdbId != 0) {
            Map<PersonProperty, String> personFields = new EnumMap<>(PersonProperty.class);
            personFields.put(PersonProperty.id, Integer.toString(imdbId));
            personFields.put(PersonProperty.name, pname.trim());

            return personFields;
          }
        }
      }
    }

    return null;
  }

  private ImageInfo getCastImage(Integer id, AvailableLanguages language) throws Exception {
    if (id == null || id <= 0) {
      return null;
    }

    URL searchUrl = new URL("http", host, String.format("/name/nm%07d/", id));
    Document dom = URIRequest.getHtmlDocument(searchUrl.toURI(), getRequestProperties(language));

    Node img = XPathUtils.selectNode("//TD[@id = 'img_primary']//IMG", dom);
    if (img != null) {
      String picture = XPathUtils.getAttribute("src", img);
      System.out.println(XPathUtils.getAttribute("src", img));
      if (!picture.contains("nopicture") && !picture.isEmpty()) {
        Map<ImageInfo.ImageProperty, String> fields = new HashMap<>();
        fields.put(ImageProperty.url, getImageLnk(picture, ImageResize.ORIG));
        fields.put(ImageProperty.urlMid, getImageLnk(picture, ImageResize.MEDIUM));
        fields.put(ImageProperty.urlTumb, getImageLnk(picture, ImageResize.SMALL));
        return new ImageInfo(id, fields, ImageCategoryProperty.actor);
      }
    }

    return null;
  }

  @Override
  public InfoQuality getQuality() {
    return InfoQuality.AWESOME;
  }

}
