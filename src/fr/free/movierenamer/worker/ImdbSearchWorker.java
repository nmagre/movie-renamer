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

import fr.free.movierenamer.parser.ImdbParser;
import fr.free.movierenamer.utils.HttpGet;
import fr.free.movierenamer.utils.SearchResult;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.awt.Dimension;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * Class ImdbSearchWorker, Search on imdb
 *
 * @author Nicolas Magré
 */
public class ImdbSearchWorker extends SwingWorker<ArrayList<SearchResult>, String> {

  private String searchTitle;
  private Settings setting;
  private HttpGet http;
  private ImdbParser imdbParser;
  private SwingPropertyChangeSupport errorSupport;
  private ResourceBundle bundle = ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle");

  /**
   * Constructor arguments
   *
   * @param errorSupport Swing change support
   * @param searchTitle Movie title to search
   * @param setting Movie Renamer settings
   */
  public ImdbSearchWorker(SwingPropertyChangeSupport errorSupport, String searchTitle, Settings setting) {
    this.errorSupport = errorSupport;
    this.searchTitle = searchTitle;
    this.setting = setting;
    imdbParser = new ImdbParser(setting);
  }

  @Override
  protected ArrayList<SearchResult> doInBackground() {
    ArrayList<SearchResult> imdbSearchResult = null;

    setProgress(0);
    String searchres = null;
    try {
      http = new HttpGet((setting.imdbFr ? setting.imdbSearchUrl_fr : setting.imdbSearchUrl) + URLEncoder.encode(searchTitle, "ISO-8859-1"));
      searchres = http.sendGetRequest(true, "ISO-8859-15");
    } catch (Exception e) {
      Settings.LOGGER.log(Level.SEVERE, e.toString());
    }

    if (searchres == null) {
      errorSupport.firePropertyChange("closeLoadingDial", false, true);
      publish("httpFailed");
      return null;
    }

    setProgress(30);

    if (searchres != null && !searchres.contains("<b>No Matches.</b>")) {
      boolean searchPage = !http.getURL().toString().matches("http://www.imdb.(com|fr)/title/tt\\d+/");
      try {
        imdbSearchResult = imdbParser.parse(searchres, searchPage);
        int i = 0;
        for (SearchResult imsres : imdbSearchResult) {
          String thumb = imsres.getThumb();
          if (thumb != null) {
            Icon icon = Utils.getSearchThumb(thumb, setting.cache, new Dimension(45, 70));
            if (icon != null) {
              imsres.setIcon(icon);
            }
          }
          if (imsres.getIcon() == null) {
            imsres.setIcon(new ImageIcon(Utils.getImageFromJAR("/image/icon-48.png", getClass())));
          }
          setProgress((30 + (++i * 70)) / imdbSearchResult.size());
        }
      } catch (Exception ex) {
        Settings.LOGGER.log(Level.SEVERE, Utils.getStackTrace("Exception", ex.getStackTrace()));
      }
    }

    if (imdbSearchResult == null) {
      errorSupport.firePropertyChange("closeLoadingDial", false, true);
      publish("scrapperSeachFailed");
      return null;
    }

    setProgress(100);
    Settings.LOGGER.log(Level.INFO, "found : {0} Movies", imdbSearchResult.size());
    return imdbSearchResult;
  }

  @Override
  public void process(List<String> v) {
    JOptionPane.showMessageDialog(null, bundle.getString(v.get(0)), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
  }
}
