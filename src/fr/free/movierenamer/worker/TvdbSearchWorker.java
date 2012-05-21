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

import fr.free.movierenamer.parser.xml.TvdbSearch;
import fr.free.movierenamer.parser.xml.XMLParser;
import fr.free.movierenamer.utils.SearchResult;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.awt.Dimension;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
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
 * Class TvdbSearchWorker ,Search tv Show on tvdb
 * @author Nicolas Magre
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
      xmlp.setParser(new TvdbSearch(setting.tvdbFr));
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
        Icon icon = Utils.getSearchThumb(thumb, setting.cache, new Dimension(200, 70));
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
}
