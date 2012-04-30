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

import fr.free.movierenamer.parser.xml.TvdbTvShow;
import fr.free.movierenamer.parser.xml.XMLParser;
import fr.free.movierenamer.ui.res.SearchResult;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.awt.Image;
import java.awt.color.CMMException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author duffy
 */
public class TvdbSearchWorker extends SwingWorker<ArrayList<SearchResult>, Void> {

  private String tvShowName;
  private Settings setting;

  public TvdbSearchWorker(String tvShowName, Settings setting) throws MalformedURLException, UnsupportedEncodingException {
    this.tvShowName = tvShowName;
    this.setting = setting;
  }

  @Override
  protected ArrayList<SearchResult> doInBackground() {
    ArrayList<SearchResult> results = new ArrayList<SearchResult>();
    try {
      String search = setting.tvdbAPIUrlTvShow + "GetSeries.php?language=" + (setting.tvdbFr ? "fr" : "en") + "&seriesname=" + URLEncoder.encode(tvShowName, "UTF-8");
      XMLParser<ArrayList<SearchResult>> xmlp = new XMLParser<ArrayList<SearchResult>>(search);
      xmlp.setParser(new TvdbTvShow(setting.tvdbFr));
      results = xmlp.parseXml();
    } catch (IOException ex) {
      Logger.getLogger(TvdbSearchWorker.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Logger.getLogger(TvdbSearchWorker.class.getName()).log(Level.SEVERE, null, ex);
    } catch (ParserConfigurationException ex) {
      Logger.getLogger(TvdbSearchWorker.class.getName()).log(Level.SEVERE, null, ex);
    } catch (SAXException ex) {
      Logger.getLogger(TvdbSearchWorker.class.getName()).log(Level.SEVERE, null, ex);
    }

    for (SearchResult res : results) {
      String thumb = res.getThumb();
      if (thumb != null) {
        Icon icon = getHttpImageIcon(thumb);
        if (icon != null) {
          res.setIcon(icon);
        }
      }
      if (res.getIcon() == null) {
        res.setIcon(new ImageIcon(Utils.getImageFromJAR("/image/icon-48.png", getClass())));
      }
    }
    setProgress(100);
    return results;
  }

  /**
   * Get icon from web server
   *
   * @param url
   * @return Icon or null
   */
  private Icon getHttpImageIcon(String url) {
    Icon icon = null;
    try {
      Image image;
      URL uri = new URL(url);
      image = setting.cache.getImage(uri, Cache.THUMB);
      if (image == null) {
        setting.cache.add(uri.openStream(), uri.toString(), Cache.THUMB);
        image = setting.cache.getImage(uri, Cache.THUMB);
      }
      icon = new ImageIcon(image.getScaledInstance(200, 30, Image.SCALE_DEFAULT));
    } catch (IOException ex) {
      setting.getLogger().log(Level.SEVERE, "{0} {1}", new Object[]{ex.getMessage(), url});
    } catch (CMMException ex) {
      setting.getLogger().log(Level.SEVERE, "{0} {1}", new Object[]{ex.getMessage(), url});
    } catch (IllegalArgumentException ex) {
      setting.getLogger().log(Level.SEVERE, "{0} {1}", new Object[]{ex.getMessage(), url});
    } catch (NullPointerException ex) {
      setting.getLogger().log(Level.SEVERE, "{0} {1}", new Object[]{ex.getMessage(), url});
    }
    return icon;
  }
}
