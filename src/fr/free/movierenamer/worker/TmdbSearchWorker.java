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
package fr.free.movierenamer.worker;

import fr.free.movierenamer.parser.xml.TmdbSearch;
import fr.free.movierenamer.parser.xml.XMLParser;
import fr.free.movierenamer.utils.SearchResult;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Class TmdbSearchWorker
 *
 * @author Nicolas Magré
 */
public class TmdbSearchWorker extends SwingWorker<ArrayList<SearchResult>, Void> {

  private String searchTitle;
  private Settings setting;

  public TmdbSearchWorker(String searchTitle, Settings setting) {
    this.searchTitle = searchTitle;
    this.setting = setting;
  }

  @Override
  protected ArrayList<SearchResult> doInBackground() {
    ArrayList<SearchResult> tmdbSearchResult = new ArrayList<SearchResult>();
    try {
      String xmlUrl = new String(DatatypeConverter.parseBase64Binary(setting.xurlMdb)) + "/";
      String xmlFile = setting.tmdbAPISearchUrl + xmlUrl +  URLEncoder.encode(searchTitle, "UTF-8");
      if(setting.imdbFr) {
        xmlFile = xmlFile.replace("/en/", "/fr/");
      }
      XMLParser<ArrayList<SearchResult>> xmp = new XMLParser<ArrayList<SearchResult>>(xmlFile);
      xmp.setParser(new TmdbSearch());
      tmdbSearchResult = xmp.parseXml();
    } catch (IOException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (ParserConfigurationException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (SAXException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }
    
     for (SearchResult tmdbres : tmdbSearchResult) {
      String thumb = tmdbres.getThumb();
      if (thumb != null) {
        Icon icon = Utils.getSearchThumb(thumb, setting.cache,  new Dimension(45, 70));
        if (icon != null) {
          tmdbres.setIcon(icon);
        }
      }
      if (tmdbres.getIcon() == null) {
        tmdbres.setIcon(new ImageIcon(Utils.getImageFromJAR("/image/icon-48.png", getClass())));
      }
    }
    return tmdbSearchResult;
  }
}
