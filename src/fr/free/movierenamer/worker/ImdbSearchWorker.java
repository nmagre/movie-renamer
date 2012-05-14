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
import fr.free.movierenamer.utils.SearchResult;
import fr.free.movierenamer.utils.HttpGet;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.awt.Dimension;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * Class ImdbSearchWorker, Search on imdb
 *
 * @author Nicolas Magré
 */
public class ImdbSearchWorker extends SwingWorker<ArrayList<SearchResult>, Void> {

  private String searchTitle;
  private Settings setting;
  private HttpGet http;
  private ImdbParser imdbParser;
  private ResourceBundle bundle = ResourceBundle.getBundle("fr/free/movierenamer/i18n/Bundle");

  /**
   * Constructor arguments
   *
   * @param searchTitle Movie title to search
   * @param setting Movie Renamer settings
   * @throws MalformedURLException
   * @throws UnsupportedEncodingException
   */
  public ImdbSearchWorker(String searchTitle, Settings setting) throws MalformedURLException, UnsupportedEncodingException {
    this.searchTitle = searchTitle;
    this.setting = setting;
    http = new HttpGet((setting.imdbFr ? setting.imdbSearchUrl_fr : setting.imdbSearchUrl) + URLEncoder.encode(searchTitle, "ISO-8859-1"));
    imdbParser = new ImdbParser(setting);
  }

  @Override
  protected ArrayList<SearchResult> doInBackground() {
    ArrayList<SearchResult> imdbSearchResult = new ArrayList<SearchResult>();

    setProgress(0);
    String searchres;
    try {
      searchres = http.sendGetRequest(true, "ISO-8859-15");
    } catch (Exception e) {
      Settings.LOGGER.log(Level.SEVERE, e.toString());
      return null;
    }
    
    setProgress(30);
    Settings.LOGGER.log(Level.INFO, "Search : {0}", searchTitle);

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
      } catch (IndexOutOfBoundsException ex) {
        Settings.LOGGER.log(Level.SEVERE, Utils.getStackTrace("IndexOutOfBoundsException", ex.getStackTrace()));
        publish((Void) null);
      }
    }

    setProgress(100);
    Settings.LOGGER.log(Level.INFO, "found : {0} Movies", imdbSearchResult.size());
    return imdbSearchResult;
  }
  
  @Override
  public void process (List<Void> v){
    JOptionPane.showMessageDialog(null, bundle.getString("imdbParserFail"), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
  }
}
