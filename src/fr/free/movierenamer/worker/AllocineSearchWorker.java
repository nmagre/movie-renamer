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

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.free.movierenamer.parser.xml.AllocineSearch;
import fr.free.movierenamer.parser.xml.XMLParser;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.SearchResult;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;

/**
 * Class AllocineSearchWorker Search movie or tvshow
 *
 * @author Nicolas Magré
 */
public class AllocineSearchWorker extends SwingWorker<ArrayList<SearchResult>, String> {

  private static final int RETRY = 3;
  private String searchTitle;
  private Settings setting;
  private SwingPropertyChangeSupport errorSupport;
  private boolean tvshow;

  public AllocineSearchWorker(SwingPropertyChangeSupport errorSupport, boolean tvshow, String searchTitle, Settings setting) {
    this.errorSupport = errorSupport;
    this.tvshow = tvshow;
    this.searchTitle = searchTitle;
    this.setting = setting;
  }

  @Override
  protected ArrayList<SearchResult> doInBackground() {
    ArrayList<SearchResult> allocineSearchResult = null;
    try {
      String uri = setting.allocineAPISearch.replace("FILTER", tvshow ? "tvseries" : "movie") + URLEncoder.encode(searchTitle, "UTF-8");
      URL url = new URL(uri);
      File xmlFile = Cache.getInstance().get(url, Cache.CacheType.XML);
      if (xmlFile == null) {
        for (int i = 0; i < RETRY; i++) {
          InputStream in;
          try {
            in = url.openStream();
            Cache.getInstance().add(in, url.toString(), Cache.CacheType.XML);
            xmlFile = Cache.getInstance().get(url, Cache.CacheType.XML);
            break;
          } catch (Exception e) {//Don't care about exception, "xmlFile" will be null
            Settings.LOGGER.log(Level.SEVERE, null, e);
            try {
              Thread.sleep(300);
            } catch (InterruptedException ex) {
              Settings.LOGGER.log(Level.SEVERE, null, ex);
            }
          }
        }
      }

      if (xmlFile == null) {
        errorSupport.firePropertyChange("closeLoadingDial", false, true);
        publish("httpFailed");
        return null;
      }

      //Parse allocine API XML
      XMLParser<ArrayList<SearchResult>> xmp = new XMLParser<ArrayList<SearchResult>>(xmlFile.getAbsolutePath());
      xmp.setParser(new AllocineSearch(tvshow));
      allocineSearchResult = xmp.parseXml();

    } catch (UnsupportedEncodingException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (ParserConfigurationException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } catch (SAXException ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    }

    if (allocineSearchResult == null) {
      errorSupport.firePropertyChange("closeLoadingDial", false, true);
      publish("scrapperSeachFailed");
      return null;
    }

    for (SearchResult allores : allocineSearchResult) {
      String thumb = allores.getThumb();
      if (thumb != null) {
        Icon icon = Utils.getSearchThumb(thumb, Cache.getInstance(), new Dimension(45, 70));
        if (icon != null) {
          allores.setIcon(icon);
        }
      }
      if (allores.getIcon() == null) {
        allores.setIcon(new ImageIcon(Utils.getImageFromJAR("/image/nothumb.png", getClass())));
      }
    }

    return allocineSearchResult;
  }

  @Override
  public void process(List<String> v) {
    JOptionPane.showMessageDialog(null, Utils.i18n(v.get(0)), Utils.i18n("error"), JOptionPane.ERROR_MESSAGE);
  }
}
