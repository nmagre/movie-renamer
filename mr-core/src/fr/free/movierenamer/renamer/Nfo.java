/*
 * mr-core
 * Copyright (C) 2013 Nicolas Magré
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
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.info.MovieInfo;
import fr.free.movierenamer.info.MovieInfo.MovieMultipleProperty;
import fr.free.movierenamer.info.MovieInfo.MovieProperty;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.utils.FileUtils;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
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

  private final MovieInfo mediaInfo;
  private final List<ImageInfo> images;
  private static final Map<MediaInfo.InfoProperty, String> xbmcNFOLayout;
  private static final Map<MediaInfo.InfoProperty, String> boxeeNFOLayout;
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
    xbmcNFOLayout = new LinkedHashMap<MediaInfo.InfoProperty, String>();
    boxeeNFOLayout = new LinkedHashMap<MediaInfo.InfoProperty, String>();

    xbmcNFOLayout.put(MovieProperty.title, "title");
    xbmcNFOLayout.put(MovieProperty.originalTitle, "originaltitle");
    xbmcNFOLayout.put(MovieProperty.sortTitle, "sorttitle");
    xbmcNFOLayout.put(MovieProperty.collection, "set");
    xbmcNFOLayout.put(MovieProperty.rating, "rating");
    xbmcNFOLayout.put(MovieProperty.releasedDate, "year");
    xbmcNFOLayout.put(MovieProperty.votes, "votes");
    xbmcNFOLayout.put(MovieProperty.overview, "plot");
    xbmcNFOLayout.put(MovieProperty.tagline, "tagline");
    xbmcNFOLayout.put(MovieProperty.runtime, "runtime");
    xbmcNFOLayout.put(MovieProperty.certification, "mpaa");
    xbmcNFOLayout.put(MovieProperty.certificationCode, "mpaa");
    xbmcNFOLayout.put(MovieMultipleProperty.genres, "genre");
    xbmcNFOLayout.put(MovieMultipleProperty.countries, "country");
    xbmcNFOLayout.put(MovieMultipleProperty.studios, "studio");
    xbmcNFOLayout.put(MovieMultipleProperty.tags, "tag");

    boxeeNFOLayout.put(MovieProperty.title, "title");
    boxeeNFOLayout.put(MovieProperty.rating, "rating");
    boxeeNFOLayout.put(MovieProperty.releasedDate, "year");
    boxeeNFOLayout.put(MovieProperty.overview, "outline");
    boxeeNFOLayout.put(MovieProperty.runtime, "runtime");

  }

  public Nfo(MovieInfo mediaInfo, List<ImageInfo> images) {
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

  private void addSimpleInfo(Map<MediaInfo.InfoProperty, String> nfoLayout) {
    for (Entry<MediaInfo.InfoProperty, String> entry : nfoLayout.entrySet()) {
      if (entry.getKey() == MovieProperty.certificationCode && mediaInfo.getCertification() != null) {
        continue;
      }

      List<String> values = new ArrayList<String>();
      if (entry.getKey() instanceof MovieProperty) {
        values.add(mediaInfo.get((MovieProperty) entry.getKey()));
      } else {
        values = mediaInfo.get((MovieMultipleProperty) entry.getKey());
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

  public void writeNFO() throws ParserConfigurationException {
    createDocument("movie");
    switch (Settings.getInstance().getMovieNfoType()) {
      case BOXEE:
        addSimpleInfo(boxeeNFOLayout);
        // Add genre
        StringBuilder sgenres = new StringBuilder();
        List<String> genres = mediaInfo.getGenres();
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
        String mpaa = mediaInfo.getCertification(MovieInfo.MotionPictureRating.USA);
        if (mpaa.equals("NC-17")) {
          addNode("mpaa", mpaa.toLowerCase());
        }

        // Add director
        List<CastingInfo> directors = mediaInfo.getDirectors();
        for (CastingInfo director : directors) {
          addNode("director", director.getName());
        }

        // Add actor
        List<CastingInfo> actors = mediaInfo.getActors();
        Node node;
        for (CastingInfo actor : actors) {
          node = createNode(rootElement, "actor");
          addToNode(node, "name", actor.getName());
          addToNode(node, "role", actor.getCharacter());
        }
        break;
      case MEDIAPORTAL:
        addSimpleInfo(xbmcNFOLayout);
        break;
      case XBMC:
        addSimpleInfo(xbmcNFOLayout);
        // Add director
        directors = mediaInfo.getDirectors();
        for (CastingInfo director : directors) {
          addNode("director", director.getName());
        }

        // Add writer
        List<CastingInfo> writers = mediaInfo.getDirectors();
        for (CastingInfo writer : writers) {
          addNode("credits", writer.getName());
        }

        // Add actor
        actors = mediaInfo.getActors();
        for (CastingInfo actor : actors) {
          node = createNode(rootElement, "actor");
          addToNode(node, "name", actor.getName());
          addToNode(node, "role", actor.getCharacter());
          URI img = actor.getPicturePath();
          if (img != null && !img.toString().equals("")) {
            addToNode(node, "thumb", img.toString());
          }
        }

        if (images != null) {
          List<ImageInfo> fanarts = new ArrayList<ImageInfo>();
          for (ImageInfo image : images) {
            if (image.getCategory() == ImageInfo.ImageCategoryProperty.thumb) {
              node = createNode(rootElement, "thumb");
              Attr preview = nfoDocument.createAttribute("preview");
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
            Attr preview = nfoDocument.createAttribute("preview");
            preview.setValue(image.getHref(ImageInfo.ImageSize.medium).toString());
            ((Element) fnode).setAttributeNode(preview);
            ((Element) fnode).setTextContent(image.getHref(ImageInfo.ImageSize.big).toString());
          }
        }

        break;
      case YAMJ:
        addSimpleInfo(xbmcNFOLayout);
        break;
    }

    FileUtils.writeXmlFile(nfoDocument, new File("/tmp/test.nfo"));

    rootElement = null;
    nfoDocument = null;
  }

}
