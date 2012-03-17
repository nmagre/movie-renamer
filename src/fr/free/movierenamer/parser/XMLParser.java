/******************************************************************************
 *                                                                             *
 *    Movie Renamer                                                            *
 *    Copyright (C) 2012 Magré Nicolas                                         *
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
package fr.free.movierenamer.parser;

import fr.free.movierenamer.utils.ActionNotValidException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import fr.free.movierenamer.movie.MoviePerson;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.movie.MovieImage;
import fr.free.movierenamer.movie.MovieInfo;
import fr.free.movierenamer.parser.XMLParser.Setting;
import fr.free.movierenamer.utils.Renamed;
import fr.free.movierenamer.utils.Utils;
import fr.free.movierenamer.ui.res.tmdbResult;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML parser
 * @param <T> XML object to parse
 * @author Nicolas Magré
 */
public class XMLParser<T> {

  private String XMLFile;
  private IParser<T> itp = null;
  private final int RETRY = 3;

  public XMLParser(String XMLFile, Class c) {
    this.XMLFile = XMLFile;
    if (c == Settings.class) itp = (IParser<T>) new Setting();
    if (c == tmdbResult.class) itp = (IParser<T>) new ImdbAPIImage();
    if (c == MovieInfo.class) itp = (IParser<T>) new ImdbAPIMovInf();
    if (itp == null) throw new NullPointerException("IParser null");
  }

  public T parseXml() throws IOException, InterruptedException {

    SAXParserFactory sparser = SAXParserFactory.newInstance();
    SAXParser parseur = null;
    T obj = null;
    try {
      parseur = sparser.newSAXParser();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
      return null;
    } catch (SAXException e) {
      e.printStackTrace();
      return null;
    }

    for (int i = 0; i < RETRY; i++) {
      try {
        InputSource objFile;
        if (isUrl()) {
          URL url = new URL(XMLFile);
          objFile = new InputSource(url.openStream());
        } else {
          File f = new File(XMLFile);
          objFile = new InputSource(new FileInputStream(f));
        }
        parseur.parse(objFile, (DefaultHandler) itp);
        if (itp == null) throw new NullPointerException("IParser null");
        obj = itp.getObject();
      } catch (SAXException e) {
        break;
      } catch (IOException e) {
        Thread.sleep(500);
        continue;
      }
      return obj;
    }
    throw new IOException("Failed to read after " + RETRY + " attempts");
  }

  /**
   * Check if "XMLFILE" is an url
   * @return
   */
  private boolean isUrl() {
    try {
      new URL(XMLFile);
    } catch (MalformedURLException e) {
      return false;
    }
    return true;
  }

  /**
   * Parse  XML Movie Renamer setting
   */
  public class Setting extends DefaultHandler implements IParser<Settings> {

    private String movieRenamerTag = "Movie_Renamer";
    private String versionAtt = "Version";
    private String settingTag = "setting";
    private String sZero = "0";
    private Settings config;
    private StringBuffer buffer;
    private boolean settingXML;
    private boolean setting;

    public Setting() {
      super();
    }

    @Override
    public void startDocument() throws SAXException {
      super.startDocument();
      settingXML = false;
      setting = false;
      config = new Settings();
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
      buffer = new StringBuffer();
      if (name.equalsIgnoreCase(movieRenamerTag)) {
        settingXML = true;
        config.xmlVersion = attributes.getValue(versionAtt);
      }
      if (name.equalsIgnoreCase(settingTag))
        setting = true;
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
      if (settingXML)
        if (setting)
          try {
            if (name.equals("locale"))
              config.locale = buffer.toString();
            if (name.equalsIgnoreCase("nameFilters")) {
              String res = buffer.toString();
              config.nameFilters = res.split("/_");
            }
            if (name.equalsIgnoreCase("extensions")) {
              String res = buffer.toString();
              config.extensions = res.split("/_");
            }
            try {
              if (name.equalsIgnoreCase("thumbSize"))
                config.thumbSize = Integer.parseInt(buffer.toString());
              if (name.equalsIgnoreCase("fanartSize"))
                config.fanartSize = Integer.parseInt(buffer.toString());
              if (name.equalsIgnoreCase("nbResult")) {
                int nb = Integer.parseInt(buffer.toString());
                config.nbResult = (nb >= config.nbResultList.length ? 0 : nb);
              }
              if (name.equalsIgnoreCase("thumbExt"))
                config.thumbExt = Integer.parseInt(buffer.toString());
              if (name.equalsIgnoreCase("fanartExt"))
                config.fanartExt = Integer.parseInt(buffer.toString());
              if(name.equalsIgnoreCase("renameCase"))
                config.renameCase = Integer.parseInt(buffer.toString());
            } catch (NumberFormatException ex) {
              config.getLogger().log(Level.SEVERE, ex.getMessage());
              config.xmlError = true;
            }

            if (name.equalsIgnoreCase("movieFilenameFormat"))
              config.movieFilenameFormat = buffer.toString().replaceAll("\\$_", "<").replaceAll("_\\$", ">");

            // boolean
            if (name.equalsIgnoreCase("useExtensionFilter"))
              config.useExtensionFilter = buffer.toString().equals(sZero);
            if (name.equalsIgnoreCase("showMovieFilePath"))
              config.showMovieFilePath = buffer.toString().equals(sZero);
            if (name.equalsIgnoreCase("scanSubfolder"))
              config.scanSubfolder = buffer.toString().equals(sZero);
            if (name.equalsIgnoreCase("hideRenamedMovie"))
              config.hideRenamedMovie = buffer.toString().equals(sZero);
            if (name.equalsIgnoreCase("displayApproximateResult"))
              config.displayApproximateResult = buffer.toString().equals(sZero);
            if (name.equalsIgnoreCase("displayThumbResult"))
              config.displayThumbResult = buffer.toString().equals(sZero);
            if (name.equalsIgnoreCase("downThumb"))
              config.downThumb = buffer.toString().equals(sZero);
            if (name.equalsIgnoreCase("downFanart"))
              config.downFanart = buffer.toString().equals(sZero);
            if (name.equalsIgnoreCase("downTrailer"))
              config.downTrailer = buffer.toString().equals(sZero);
            if (name.equalsIgnoreCase("createMovieDirectory"))
              config.createMovieDirectory = buffer.toString().equals(sZero);
            if (name.equalsIgnoreCase("movieDirRenamedTitle"))
              config.movieDirRenamedTitle = buffer.toString().equals(sZero);
            if (name.equalsIgnoreCase("imdbInfo"))
              config.imdbInfo = buffer.toString().equals(sZero);
            if (name.equalsIgnoreCase("imdbFr"))
              config.imdbFr = buffer.toString().equals(sZero);
            if (name.equalsIgnoreCase("selectFrstMovie"))
              config.selectFrstMovie = buffer.toString().equals(sZero);
            if (name.equalsIgnoreCase("selectFrstRes"))
              config.selectFrstRes = buffer.toString().equals(sZero);
            if (name.equalsIgnoreCase("movieInfoPanel"))
              config.movieInfoPanel = buffer.toString().equals(sZero);
            if (name.equalsIgnoreCase("actorImage"))
              config.actorImage = buffer.toString().equals(sZero);
            if (name.equalsIgnoreCase("thumb"))
              config.thumb = buffer.toString().equals(sZero);
            if (name.equalsIgnoreCase("fanart"))
              config.fanart = buffer.toString().equals(sZero);
            if (name.equalsIgnoreCase("checkUpdate"))
              config.checkUpdate = buffer.toString().equals(sZero);
            if (name.equalsIgnoreCase("showNotaMovieWarn"))
              config.showNotaMovieWarn = buffer.toString().equals(sZero);
            if (name.equalsIgnoreCase("autoSearchMovie"))
              config.autoSearchMovie = buffer.toString().equals(sZero);

          } catch (NullPointerException ex) {
            config.getLogger().log(Level.SEVERE, ex.getMessage());
            config.xmlError = true;
          }
      buffer = null;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      String lecture = new String(ch, start, length);
      if (buffer != null)
        buffer.append(lecture);
    }

    @Override
    public Settings getObject() {
      return this.config;
    }
  }

  /**
   * Parse XML from TheMovieDatabase (images only)
   */
  public static class ImdbAPIImage extends DefaultHandler implements IParser<tmdbResult> {

    private ArrayList<MovieImage> thumbs;
    private ArrayList<MovieImage> fanarts;
    private StringBuffer buffer;
    private boolean imdbAPIXML;
    private boolean images;
    private String currentId;
    private MovieImage currentMovieImage;
    private String lastAttribute;
    private String tmdbId;
    private tmdbResult tmdbRes;

    public ImdbAPIImage() {
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
      thumbs = new ArrayList<MovieImage>();
      fanarts = new ArrayList<MovieImage>();
    }

    @Override
    public void endDocument() throws SAXException {
      super.endDocument();
      tmdbRes = new tmdbResult(tmdbId, thumbs, fanarts);
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
                currentMovieImage = new MovieImage(currentId, attributes.getValue(0));
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
    public tmdbResult getObject() {
      return tmdbRes;
    }
  }

  /**
   * Parse XML from TheMovieDatabase (information only)
   */
  public static class ImdbAPIMovInf extends DefaultHandler implements IParser<MovieInfo> {

    private StringBuffer buffer;
    private boolean imdbAPIXML;
    private MovieInfo movieinfo;

    public ImdbAPIMovInf() {
      super();
    }

    @Override
    public void startDocument() throws SAXException {
      super.startDocument();
      imdbAPIXML = false;
      movieinfo = new MovieInfo();
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
      buffer = new StringBuffer();
      if (name.equalsIgnoreCase("OpenSearchDescription"))
        imdbAPIXML = true;
      if (name.equalsIgnoreCase("country"))
        movieinfo.addCountry(attributes.getValue("name"));
      if (name.equalsIgnoreCase("person")) {
        String personnJob = attributes.getValue("job");

        if (personnJob.equals("Director") || personnJob.equals("Actor") || personnJob.equals("Writer"))
          try {//A refaire imbitable
            MoviePerson actor;
            actor = movieinfo.getActorByName(attributes.getValue("name"));
            int job = MoviePerson.ACTOR;
            if (personnJob.equals("Director")) job = MoviePerson.DIRECTOR;
            if (personnJob.equals("Writer")) job = MoviePerson.WRITER;
            if (actor == null) {
              actor = new MoviePerson(attributes.getValue("name"), attributes.getValue("thumb"), job);
              actor.addRole(attributes.getValue("character"));
              movieinfo.addActor(actor);
            } else
              movieinfo.addRole(actor.getName(), attributes.getValue("character"));
          } catch (ActionNotValidException ex) {
            Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
          }
      }
      if (name.equalsIgnoreCase("category"))
        if (attributes.getValue("type").equals("genre"))
          movieinfo.addGenre(attributes.getValue("name"));
      if (name.equalsIgnoreCase("studio"))
        movieinfo.addStudio(attributes.getValue("name"));

    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
      if (name.equalsIgnoreCase("OpenSearchDescription"))
        imdbAPIXML = false;

      if (imdbAPIXML) {
        if (name.equalsIgnoreCase("trailer"))
          movieinfo.setTrailer(buffer.toString());
        if (name.equalsIgnoreCase("overview"))
          movieinfo.setSynopsis(buffer.toString());
        if (name.equalsIgnoreCase("original_name"))
          movieinfo.setOrigTitle(buffer.toString());
        if (name.equalsIgnoreCase("tagline"))
          movieinfo.setTagline(buffer.toString());
        if (name.equalsIgnoreCase("rating"))
          if (Utils.isDigit(buffer.toString()))
            movieinfo.setRating(buffer.toString());
        if (name.equalsIgnoreCase("runtime"))
          if (Utils.isDigit(buffer.toString()))
            movieinfo.setRuntime(Integer.parseInt(buffer.toString()));
        if (name.equalsIgnoreCase("votes"))
          movieinfo.setVotes(buffer.toString());
      }
      buffer = null;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      String lecture = new String(ch, start, length);
      if (buffer != null)
        buffer.append(lecture);
    }

    @Override
    public MovieInfo getObject() {
      return movieinfo;
    }
  }

  /**
   * Parse XML Movie Renamed with Movie Renamer
   */
  public static class RenamedMov extends DefaultHandler implements IParser<ArrayList<Renamed>> {

    private StringBuffer buffer;
    private boolean renamedXML;
    private boolean renamedMovie;
    private ArrayList<Renamed> renameds;
    private Renamed renamed;

    public RenamedMov() {
      super();
    }

    @Override
    public void startDocument() throws SAXException {
      super.startDocument();
      renamedXML = false;
      renamedMovie = false;
      renamed = null;
      renameds = new ArrayList<Renamed>();
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
      buffer = new StringBuffer();
      if (name.equalsIgnoreCase("Movie_Renamer_Renamed"))
        renamedXML = true;
      if (name.equalsIgnoreCase("renamedMovie")) {
        renamed = new Renamed(attributes.getValue("title"));
        renamedMovie = true;
      }

      if (renamedMovie) {
        if (name.equalsIgnoreCase("movie")) {
          renamed.setMovieFileSrc(attributes.getValue("src"));
          renamed.setMovieFileDest(attributes.getValue("dest"));
        }
        if (name.equalsIgnoreCase("date")) renamed.setDate(buffer.toString());
      }
      buffer = null;
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
      if (name.equalsIgnoreCase("Movie_Renamer_Renamed"))
        renamedXML = false;

      if (renamedXML)
        if (renamedMovie)
          if (name.equalsIgnoreCase("renamedMovie")) {
            renameds.add(renamed);
            renamed = null;
            renamedMovie = false;
          }
      buffer = null;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      String lecture = new String(ch, start, length);
      if (buffer != null)
        buffer.append(lecture);
    }

    @Override
    public ArrayList<Renamed> getObject() {
      return renameds;
    }
  }
}
