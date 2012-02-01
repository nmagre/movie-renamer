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
package fr.free.movierenamer.worker;

import java.awt.Image;
import java.awt.color.CMMException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.HttpGet;
import fr.free.movierenamer.utils.ImdbSearchResult;
import fr.free.movierenamer.parser.ImdbParser;
import fr.free.movierenamer.utils.Settings;

/**
 * Class imdbSearchThread
 * @author Nicolas Magré
 */
public class ImdbSearchWorker extends SwingWorker<ArrayList<ImdbSearchResult>, Void> {

  private String searchTitle;
  private Settings setting;
  private HttpGet http;
  private ImdbParser imdbParser;

  public ImdbSearchWorker(String searchTitle, boolean french, Settings setting) throws MalformedURLException, UnsupportedEncodingException {
    this.searchTitle = searchTitle;
    this.setting = setting;
    http = new HttpGet((french ? setting.imdbSearchUrl_fr:setting.imdbSearchUrl) + URLEncoder.encode(searchTitle, "ISO-8859-1"));
    imdbParser = new ImdbParser(french, setting);
  }

  @Override
  protected ArrayList<ImdbSearchResult> doInBackground() {
    ArrayList<ImdbSearchResult> imdbSearchResult = new ArrayList<ImdbSearchResult>();

    setProgress(0);
    String searchres = http.sendGetRequest(true);
    setProgress(30);
    setting.getLogger().log(Level.INFO, "Search : {0}", searchTitle);

    if (searchres != null && !searchres.contains("<b>No Matches.</b>")) {
      boolean searchPage = !http.getURL().toString().matches("http://www.imdb.(com|fr)/title/tt\\d+/");
      imdbSearchResult = imdbParser.parse(searchres, searchPage);
      
      int i = 0;
      for (ImdbSearchResult imsres : imdbSearchResult) {
        String thumb = imsres.getThumb();
        if (thumb != null) {
          Icon icon = getHttpImageIcon(thumb);
          if (icon != null) imsres.setIcon(icon);
        }
        setProgress(30 + (int) (++i * 70) / imdbSearchResult.size());
      }
    }

    setProgress(100);
    setting.getLogger().log(Level.INFO, "found : {0} Movies", imdbSearchResult.size());
    return imdbSearchResult;
  }

  private Icon getHttpImageIcon(String url) {
    Icon icon = null;
    try {
      Image image;
      URL uri = new URL(url);
      image = setting.cache.getImage(uri, Cache.thumb);
      if (image == null) {
        setting.cache.add(uri.openStream(), uri.toString(), Cache.thumb);
        image = setting.cache.getImage(uri, Cache.thumb);
      }
      icon = new ImageIcon(image.getScaledInstance(45, 70, Image.SCALE_DEFAULT));
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
