/******************************************************************************
 *                                                                             *
 *    Movie Renamer                                                            *
 *    Copyright (C) 2011 Magré Nicolas                                         *
 *                                                                             *
 *    Movie Renamer is free software: you can redistribute it and/or modify    *
 *    it under the terms of the GNU General Public License as published by     *
 *    the Free Software Foundation, either version 3 of the License, or        *
 *    (at your option) any later version.                                      *
 *                                                                             *
 *    This program is distributed in the hope that it will be useful,          *
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            *
 *    GNU General Public License for more details.                             *
 *                                                                             *
 *    You should have received a copy of the GNU General Public License        *
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                             *
 ******************************************************************************/
package fr.free.movierenamer.parser.xml;

import fr.free.movierenamer.ui.res.TmdbResult;
import fr.free.movierenamer.utils.Images;
import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class TheMovieDbImage
 * @author Nicolas Magré
 */
public class TheMovieDbImage extends DefaultHandler implements IParser<TmdbResult> {

  private ArrayList<Images> thumbs;
  private ArrayList<Images> fanarts;
  private StringBuffer buffer;
  private boolean imdbAPIXML;
  private boolean images;
  private String currentId;
  private Images currentMovieImage;
  private String lastAttribute;
  private String tmdbId;
  private TmdbResult tmdbRes;

  public TheMovieDbImage() {
    super();
  }

  @Override
  public void startDocument() throws SAXException {
    super.startDocument();
    imdbAPIXML = false;
    images = false;
    currentId = "";
    currentMovieImage = null;
    lastAttribute = "";
    tmdbId = "";
    thumbs = new ArrayList<Images>();
    fanarts = new ArrayList<Images>();
  }

  @Override
  public void endDocument() throws SAXException {
    super.endDocument();
    tmdbRes = new TmdbResult(tmdbId, thumbs, fanarts);
  }

  @Override
  public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
    buffer = new StringBuffer();
    if (name.equalsIgnoreCase("OpenSearchDescription"))
      imdbAPIXML = true;
    if (name.equalsIgnoreCase("images"))
      images = true;

    if (imdbAPIXML)
      if (images)
        if (name.equalsIgnoreCase("image"))
          if (attributes.getQName(0) != null && attributes.getQName(0).equals("type")) {
            if (!currentId.equals(attributes.getValue("id"))) {
              if (currentMovieImage != null) {
                if (lastAttribute.equals("poster"))
                  thumbs.add(currentMovieImage);
                else
                  fanarts.add(currentMovieImage);
                currentMovieImage = null;
              }
              currentId = attributes.getValue("id");
              currentMovieImage = new Images(0);
              lastAttribute = attributes.getValue(0);
            }
            if (attributes.getValue(2).equals("original"))
              currentMovieImage.setOrigUrl(attributes.getValue(1).replace(".png", ".jpg"));// API bug png ar jpg on server
            if (attributes.getValue(2).equals("thumb"))
              currentMovieImage.setThumbUrl(attributes.getValue(1).replace(".png", ".jpg"));
            if (attributes.getValue(2).equals("mid") || attributes.getValue(2).equals("poster"))
              currentMovieImage.setMidUrl(attributes.getValue(1).replace(".png", ".jpg"));
          }
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    if (name.equalsIgnoreCase("OpenSearchDescription"))
      imdbAPIXML = false;

    if (name.equalsIgnoreCase("images")) {
      images = false;
      if (currentMovieImage != null)
        if (lastAttribute.equals("poster"))
          thumbs.add(currentMovieImage);
        else
          fanarts.add(currentMovieImage);
    }

    if (name.equalsIgnoreCase("id"))
      tmdbId = buffer.toString();

    buffer = null;
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    String lecture = new String(ch, start, length);
    if (buffer != null)
      buffer.append(lecture);
  }

  @Override
  public TmdbResult getObject() {
    return tmdbRes;
  }
}