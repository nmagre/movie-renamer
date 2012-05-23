/*
 * Movie Renamer
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
package fr.free.movierenamer.parser.xml;

import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.util.logging.Level;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class MrSettings
 *
 * @author Nicolas Magré
 */
public class MrSettings extends DefaultHandler implements IParser<Settings> {

  private String movieRenamerTag = "Movie_Renamer";
  private String versionAtt = "Version";
  private String settingTag = "setting";
  private String sZero = "0";
  private Settings config;
  private StringBuffer buffer;
  private boolean settingXML;
  private boolean setting;

  public MrSettings() {
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
    if (name.equalsIgnoreCase(settingTag)) {
      setting = true;
    }
  }

  @Override
  public void endElement(String uri, String localName, String name) throws SAXException {
    if (settingXML) {
      if (setting) {
        try {
          if (name.equals("locale")) {
            config.locale = buffer.toString();
          }
          if (name.equalsIgnoreCase("nameFilters")) {
            String res = Utils.unEscapeXML(buffer.toString(), "UTF-8");
            config.nameFilters = res.split("/_");
          }
          if (name.equalsIgnoreCase("extensions")) {
            String res = buffer.toString();
            config.extensions = res.split("/_");
          }
          if (name.equalsIgnoreCase("movieDir")) {
            config.movieDir = buffer.toString();
          }
          if (name.equalsIgnoreCase("separator")) {
            config.separator = buffer.toString();
          }
          if (name.equalsIgnoreCase("laf")) {
            config.laf = buffer.toString();
          }

          try {
            if (name.equalsIgnoreCase("thumbSize")) {
              config.thumbSize = Integer.parseInt(buffer.toString());
            }
            if (name.equalsIgnoreCase("fanartSize")) {
              config.fanartSize = Integer.parseInt(buffer.toString());
            }
            if (name.equalsIgnoreCase("nbResult")) {
              int nb = Integer.parseInt(buffer.toString());
              config.nbResult = (nb >= config.nbResultList.length ? 0 : nb);
            }
            if (name.equalsIgnoreCase("thumbExt")) {
              config.thumbExt = Integer.parseInt(buffer.toString());
            }
            if (name.equalsIgnoreCase("fanartExt")) {
              config.fanartExt = Integer.parseInt(buffer.toString());
            }
            if (name.equalsIgnoreCase("renameCase")) {
              config.renameCase = Integer.parseInt(buffer.toString());
            }
            if (name.equalsIgnoreCase("movieDirRenamedTitle")) {
              int nb = Integer.parseInt(buffer.toString());
              if (nb > 2) {
                nb = 0;
              }
              config.movieDirRenamedTitle = nb;
            }
            if (name.equalsIgnoreCase("nfoType")) {
              int nb = Integer.parseInt(buffer.toString());
              if (nb > 1) {
                nb = 0;
              }
              config.nfoType = nb;
            }
            if (name.equalsIgnoreCase("limit")) {
              config.limit = Integer.parseInt(buffer.toString());
            }
            if (name.equalsIgnoreCase("scrapper")) {
              int nb = Integer.parseInt(buffer.toString());
              if(nb< 0 || nb >2){
                nb = 0;
              }
              config.scrapper = nb;
            }            
          } catch (NumberFormatException ex) {
            Settings.LOGGER.log(Level.SEVERE, ex.getMessage());
            config.xmlError = true;
          }

          if (name.equalsIgnoreCase("movieFilenameFormat")) {
            if (config.xmlVersion.compareToIgnoreCase("1.2.2_Alpha") < 0)// Older setting file
            {
              config.movieFilenameFormat = buffer.toString().replace("$_", "<").replace("_$", ">");
            } else {
              config.movieFilenameFormat = Utils.unEscapeXML(buffer.toString(), "UTF-8");
            }
          }

          // boolean
          if (name.equalsIgnoreCase("useExtensionFilter")) {
            config.useExtensionFilter = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("showMovieFilePath")) {
            config.showMovieFilePath = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("scanSubfolder")) {
            config.scanSubfolder = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("hideRenamedMovie")) {
            config.hideRenamedMovie = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("displayApproximateResult")) {
            config.displayApproximateResult = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("displayThumbResult")) {
            config.displayThumbResult = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("downThumb")) {
            config.downThumb = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("downFanart")) {
            config.downFanart = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("downTrailer")) {
            config.downTrailer = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("createMovieDirectory")) {
            config.createMovieDirectory = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("imdbInfo")) {
            config.imdbInfo = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("imdbFr")) {
            config.imdbFr = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("selectFrstMovie")) {
            config.selectFrstMovie = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("selectFrstRes")) {
            config.selectFrstRes = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("movieInfoPanel")) {
            config.movieInfoPanel = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("actorImage")) {
            config.actorImage = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("thumb")) {
            config.thumb = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("fanart")) {
            config.fanart = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("checkUpdate")) {
            config.checkUpdate = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("showNotaMovieWarn")) {
            config.showNotaMovieWarn = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("autoSearchMovie")) {
            config.autoSearchMovie = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("rmSpcChar")) {
            config.rmSpcChar = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("rmDupSpace")) {
            config.rmDupSpace = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("tvdbFr")) {
            config.tvdbFr = buffer.toString().equals(sZero);
          }
          if (name.equalsIgnoreCase("clearXMLCache")) {
            config.clearXMLCache = buffer.toString().equals(sZero);
          }

        } catch (NullPointerException ex) {
          Settings.LOGGER.log(Level.SEVERE, ex.getMessage());
          config.xmlError = true;
        }
      }
    }
    buffer = null;
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    String lecture = new String(ch, start, length);
    if (buffer != null) {
      buffer.append(lecture);
    }
  }

  @Override
  public Settings getObject() {
    return this.config;
  }
}
