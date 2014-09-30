/*
 * mr-core
 * Copyright (C) 2013-2014 Nicolas Magré
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
package fr.free.movierenamer.renamer;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.IdInfo;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.info.MediaInfo.InfoProperty;
import fr.free.movierenamer.info.MediaInfo.MediaProperty;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.MovieInfo.MovieMultipleProperty;
import fr.free.movierenamer.info.MovieInfo.MovieProperty;
import fr.free.movierenamer.searchinfo.Media.MediaType;
import static fr.free.movierenamer.searchinfo.Media.MediaType.MOVIE;
import static fr.free.movierenamer.searchinfo.Media.MediaType.TVSHOW;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.ScrapperUtils;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Class Nfo
 *
 * @author Nicolas Magré
 */
public class Nfo {// TODO

  private final MediaInfo mediaInfo;
  private final List<ImageInfo> images;
  private static final Map<MediaInfo.InfoProperty, String> xbmcMovieNFOLayout;
  private static final Map<MediaInfo.InfoProperty, String> boxeeMovieNFOLayout;
  private static final Map<MediaType, Map> xbmcNFOLayout;
  private static final Map<MediaType, Map> BoxeeNFOLayout;
  private static final Settings settings = Settings.getInstance();
  private final String[] boxeeGenre = new String[]{"ACTION", " ADVENTURE", " ANIMATION", " COMEDY", " CRIME", " DOCUMENTARY", " DRAMA", " FAMILY", " FANTASY", " FILM_NOIR", " HISTORY", " MUSIC", " MUSICAL", " MYSTERY", " NEWS", " ROMANCE", " SCI_FI", " SHORT", " SPORT", " THRILLER", " WAR", " WESTERN"};
  private Element rootElement;
  private Document nfoDocument;

  public static enum NFOtype {

    BOXEE,
    MEDIAPORTAL,
    XBMC,
    YAMJ
  }

  static {
    xbmcNFOLayout = new EnumMap<MediaType, Map>(MediaType.class);
    BoxeeNFOLayout = new EnumMap<MediaType, Map>(MediaType.class);
    xbmcMovieNFOLayout = new LinkedHashMap<MediaInfo.InfoProperty, String>();
    boxeeMovieNFOLayout = new LinkedHashMap<MediaInfo.InfoProperty, String>();

    xbmcMovieNFOLayout.put(MediaProperty.title, "title");
    xbmcMovieNFOLayout.put(MediaProperty.rating, "rating");
    xbmcMovieNFOLayout.put(MovieProperty.originalTitle, "originaltitle");
    xbmcMovieNFOLayout.put(MovieProperty.sortTitle, "sorttitle");
    xbmcMovieNFOLayout.put(MovieProperty.collection, "set");
    xbmcMovieNFOLayout.put(MovieProperty.releasedDate, "year");
    xbmcMovieNFOLayout.put(MovieProperty.votes, "votes");
    xbmcMovieNFOLayout.put(MovieProperty.overview, "plot");
    xbmcMovieNFOLayout.put(MovieProperty.tagline, "tagline");
    xbmcMovieNFOLayout.put(MovieProperty.runtime, "runtime");
    xbmcMovieNFOLayout.put(MovieProperty.certification, "mpaa");
    xbmcMovieNFOLayout.put(MovieProperty.certificationCode, "mpaa");
    xbmcMovieNFOLayout.put(MovieMultipleProperty.genres, "genre");
    xbmcMovieNFOLayout.put(MovieMultipleProperty.countries, "country");
    xbmcMovieNFOLayout.put(MovieMultipleProperty.studios, "studio");
    xbmcMovieNFOLayout.put(MovieMultipleProperty.tags, "tag");

    boxeeMovieNFOLayout.put(MediaProperty.title, "title");
    boxeeMovieNFOLayout.put(MediaProperty.rating, "rating");
    boxeeMovieNFOLayout.put(MovieProperty.releasedDate, "year");
    boxeeMovieNFOLayout.put(MovieProperty.overview, "outline");
    boxeeMovieNFOLayout.put(MovieProperty.runtime, "runtime");

    xbmcNFOLayout.put(MediaType.MOVIE, xbmcMovieNFOLayout);
    BoxeeNFOLayout.put(MediaType.MOVIE, boxeeMovieNFOLayout);
  }

  public Nfo(MediaInfo mediaInfo, List<ImageInfo> images) {
    this.mediaInfo = mediaInfo;
    this.images = images;
  }

  private void createDocument(String rootNode) throws ParserConfigurationException {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder;
    docBuilder = docFactory.newDocumentBuilder();

    // root elements
    nfoDocument = docBuilder.newDocument();
    rootElement = nfoDocument.createElement(rootNode);
    nfoDocument.appendChild(rootElement);
  }

  public void getMrXml() throws ParserConfigurationException {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder;
    docBuilder = docFactory.newDocumentBuilder();

    // root elements
    nfoDocument = docBuilder.newDocument();
    rootElement = nfoDocument.createElement("movie-renamer");
    nfoDocument.appendChild(rootElement);

    MovieInfo movieInfo = (MovieInfo) mediaInfo;

    Node node;
    Node anode;
    String nodeName;

    node = createNode(rootElement, "ids");
    for (IdInfo idInfo : movieInfo.getIdsInfo()) {
      anode = createNode(node, idInfo.getIdType().name().toLowerCase());
      anode.setTextContent(idInfo.toString());
    }

    for (MediaProperty property : MediaProperty.values()) {
      node = createNode(rootElement, property.name());
      node.setTextContent(movieInfo.get(property));
    }

    for (MovieProperty property : MovieProperty.values()) {
      node = createNode(rootElement, property.name());
      node.setTextContent(movieInfo.get(property));
    }

    for (MovieMultipleProperty property : MovieMultipleProperty.values()) {
      nodeName = property.name();
      node = createNode(rootElement, nodeName);
      for (String value : movieInfo.get(property)) {
        anode = createNode(node, nodeName.substring(0, nodeName.length() - 1));
        anode.setTextContent(value);
      }
    }

    // Images
    Integer width;
    Integer height;
    for (ImageInfo imginfo : images) {
      node = createNode(rootElement, imginfo.getCategory().name());
      width = imginfo.getWidth();
      height = imginfo.getHeight();

      ((Element) node).setAttribute("lang", imginfo.getLanguage());
      ((Element) node).setAttribute("width", width != null ? width.toString() : "");
      ((Element) node).setAttribute("height", height != null ? height.toString() : "");

      anode = createNode(node, ImageInfo.ImageSize.big.name());
      anode.setTextContent(imginfo.getHref(ImageInfo.ImageSize.big).toExternalForm());
      anode = createNode(node, ImageInfo.ImageSize.medium.name());
      anode.setTextContent(imginfo.getHref(ImageInfo.ImageSize.medium).toExternalForm());
      anode = createNode(node, ImageInfo.ImageSize.small.name());
      anode.setTextContent(imginfo.getHref(ImageInfo.ImageSize.small).toExternalForm());
    }

    // Add director
    List<CastingInfo> directors = movieInfo.getDirectors();
    for (CastingInfo director : directors) {
      node = createNode(rootElement, "director");
      node.setTextContent(director.getName());
    }

    // Add writer
    List<CastingInfo> writers = movieInfo.getDirectors();
    for (CastingInfo writer : writers) {
      node = createNode(rootElement, "writer");
      node.setTextContent(writer.getName());
    }

    // Add actor
    List<CastingInfo> actors = movieInfo.getActors();
    URI img;
    for (CastingInfo actor : actors) {
      node = createNode(rootElement, "actor");
      anode = createNode(node, "name");
      anode.setTextContent(actor.getName());
      anode = createNode(node, "role");
      anode.setTextContent(actor.getCharacter());

      img = actor.getImage(ImageInfo.ImageSize.big);
      anode = createNode(node, "thumb");
      anode.setTextContent(img != null ? img.toString() : "");
    }

  }

  private void addSimpleInfo(final Map<InfoProperty, String> nfoLayout, MediaType infoType) {

    switch (infoType) {
      case MOVIE:
        addSimpleMovieInfo(nfoLayout, (MovieInfo) mediaInfo);
        break;
      case TVSHOW:// TODO tvshow info
        break;
    }
  }

  private void addSimpleMovieInfo(final Map<InfoProperty, String> nfoLayout, MovieInfo movieInfo) {
    InfoProperty property;
    List<String> values;

    for (Entry<InfoProperty, String> entry : nfoLayout.entrySet()) {
      property = entry.getKey();
      if (property == MovieProperty.certificationCode && movieInfo.getCertification() != null) {
        continue;
      }

      if (property == MovieMultipleProperty.tags && !settings.isMovieNfoTag()) {
        continue;
      }

      values = new ArrayList<String>();

      if (entry.getKey() instanceof MovieProperty) {
        values.add(movieInfo.get((MovieProperty) entry.getKey()));
      } else if (entry.getKey() instanceof MediaProperty) {
        values.add(movieInfo.get((MediaProperty) entry.getKey()));
      } else {
        values = movieInfo.get((MovieMultipleProperty) entry.getKey());
      }

      for (String value : values) {
        addNode(entry.getValue(), value);
      }
    }
  }

  private void addNode(String snode, String value) {
    addToNode(rootElement, snode, value);
  }

  private void addToNode(Node node, String snode, String value) {
    if (value != null && !value.trim().equals("")) {
      Node enode = createNode(node, snode);
      enode.setTextContent(value.trim());
    }
  }

  private Node createNode(Node node, String snode) {
    Node enode = nfoDocument.createElement(snode);
    node.appendChild(enode);
    return enode;
  }

  public Document getNFO() throws ParserConfigurationException {
    createDocument("movie");

    MediaType infoType = mediaInfo.getMediaType();

    if (infoType.equals(MediaType.MOVIE)) {
      if (settings.isMovieImdbId()) {
        addNode("id", ((MovieInfo) mediaInfo).getIdString(ScrapperUtils.AvailableApiIds.IMDB));
      }
    }

    switch (Settings.getInstance().getMovieNfoType()) {
      case BOXEE:
        addSimpleInfo(BoxeeNFOLayout.get(infoType), infoType);
        addBoxeeInfo(infoType);
        break;
      case MEDIAPORTAL:
        addSimpleInfo(xbmcNFOLayout.get(infoType), infoType);
        // TODO MEDIAPORTAL NFO
        break;
      case XBMC:
        addSimpleInfo(xbmcNFOLayout.get(infoType), infoType);
        addXbmcInfo(infoType);
        break;
      case YAMJ:
        addSimpleInfo(xbmcNFOLayout.get(infoType), infoType);
        // TODO YAMJ NFO
        break;
    }

    return nfoDocument;
  }

  private void addBoxeeInfo(MediaType infoType) {
    switch (infoType) {
      case MOVIE:
        addBoxeeMovieInfo((MovieInfo) mediaInfo);
        break;
      case TVSHOW:
        break;
    }
  }

  private void addBoxeeMovieInfo(MovieInfo movieInfo) {
    // Add genre
    StringBuilder sgenres = new StringBuilder();
    List<String> genres = movieInfo.getGenres();
    for (String genre : genres) {
      if (Arrays.asList(boxeeGenre).contains(genre.toUpperCase())) {
        if (sgenres.length() > 0) {
          sgenres.append(", ");
        }
        sgenres.append(genre.toUpperCase());
      }
    }
    addNode("genre", sgenres.toString());

    // Add mpaa
    String mpaa = movieInfo.getCertification(MovieInfo.MotionPictureRating.USA);
    if (mpaa.equals("NC-17")) {
      addNode("mpaa", mpaa.toLowerCase());
    }

    // Add director
    List<CastingInfo> directors = movieInfo.getDirectors();
    for (CastingInfo director : directors) {
      addNode("director", director.getName());
    }

    // Add actor
    List<CastingInfo> actors = movieInfo.getActors();
    Node node;
    for (CastingInfo actor : actors) {
      node = createNode(rootElement, "actor");
      addToNode(node, "name", actor.getName());
      addToNode(node, "role", actor.getCharacter());
    }
  }

  private void addXbmcInfo(MediaType infoType) {
    switch (infoType) {
      case MOVIE:
        addXbmcMovieInfo((MovieInfo) mediaInfo);
        break;
      case TVSHOW:
        break;
    }
  }

  private void addXbmcMovieInfo(MovieInfo movieInfo) {
    // Add director
    List<CastingInfo> directors = movieInfo.getDirectors();
    for (CastingInfo director : directors) {
      addNode("director", director.getName());
    }

    // Add writer
    List<CastingInfo> writers = movieInfo.getDirectors();
    for (CastingInfo writer : writers) {
      addNode("credits", writer.getName());
    }

    // Add actor
    List<CastingInfo> actors = movieInfo.getActors();
    Node node;
    for (CastingInfo actor : actors) {
      node = createNode(rootElement, "actor");
      addToNode(node, "name", actor.getName());
      addToNode(node, "role", actor.getCharacter());
      URI img = actor.getImage(ImageInfo.ImageSize.big);
      if (img != null && !img.toString().equals("")) {
        addToNode(node, "thumb", img.toString());
      }
    }

    if (settings.isMovieNfoImage() && images != null) {
      final List<ImageInfo> fanarts = new ArrayList<ImageInfo>();
      Attr preview;

      for (ImageInfo image : images) {
        if (image.getCategory() == ImageInfo.ImageCategoryProperty.thumb) {
          node = createNode(rootElement, "thumb");
          preview = nfoDocument.createAttribute("preview");
          preview.setValue(image.getHref(ImageInfo.ImageSize.medium).toString());
          ((Element) node).setAttributeNode(preview);
          ((Element) node).setTextContent(image.getHref(ImageInfo.ImageSize.big).toString());
        } else if (image.getCategory() == ImageInfo.ImageCategoryProperty.fanart) {
          fanarts.add(image);
        }
      }

      node = createNode(rootElement, "fanart");
      Node fnode;
      for (ImageInfo image : fanarts) {
        fnode = createNode(node, "thumb");
        preview = nfoDocument.createAttribute("preview");
        preview.setValue(image.getHref(ImageInfo.ImageSize.medium).toString());
        ((Element) fnode).setAttributeNode(preview);
        ((Element) fnode).setTextContent(image.getHref(ImageInfo.ImageSize.big).toString());
      }
    }
  }

}
