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

import fr.free.movierenamer.parser.xml.AllocineSearch;
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
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Class AllocineSearchWorker
 *
 * @author Nicolas Magré
 */
public class AllocineSearchWorker extends SwingWorker<ArrayList<SearchResult>, Void> {

  private String searchTitle;
  private Settings setting;

  public AllocineSearchWorker(String searchTitle, Settings setting) {
    this.searchTitle = searchTitle;
    this.setting = setting;
  }

  @Override
  protected ArrayList<SearchResult> doInBackground() {
    ArrayList<SearchResult> allocineSearchResult = new ArrayList<SearchResult>();
    try {
      String xmlFile = setting.allocineAPISearch + URLEncoder.encode(searchTitle, "UTF-8");
      XMLParser<ArrayList<SearchResult>> xmp = new XMLParser<ArrayList<SearchResult>>(xmlFile);
      xmp.setParser(new AllocineSearch());
      allocineSearchResult = xmp.parseXml();
    } catch (IOException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (ParserConfigurationException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (SAXException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }
    
    for (SearchResult allores : allocineSearchResult) {
      String thumb = allores.getThumb();
      if (thumb != null) {
        Icon icon = Utils.getSearchThumb(thumb, setting.cache,  new Dimension(45, 70));
        if (icon != null) {
          allores.setIcon(icon);
        }
      }
      if (allores.getIcon() == null) {
        allores.setIcon(new ImageIcon(Utils.getImageFromJAR("/image/icon-48.png", getClass())));
      }
    }
    return allocineSearchResult;
  }
}
