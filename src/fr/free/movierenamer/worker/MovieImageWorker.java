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
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import fr.free.movierenamer.movie.MovieImage;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.ui.MoviePanel;

/**
 *
 * @author duffy
 */
public class MovieImageWorker extends SwingWorker<Void, Void>  {

  private final int THUMB = 0;
  private final int FANART = 1;
  private ArrayList<MovieImage> arrayImage;
  private int type;
  private int cache;
  private Settings setting;
  private MoviePanel moviePnl;

  public MovieImageWorker(ArrayList<MovieImage> arrayImage, int type, int cache, MoviePanel moviePnl, Settings setting) {
    this.arrayImage = arrayImage;
    this.type = type;
    this.cache = cache;
    this.setting = setting;
    this.moviePnl = moviePnl;
  }

  @Override
  protected Void doInBackground() {
    for (int i = 0; i < arrayImage.size(); i++) {
      Image image;
      try {
        
        setProgress((i*100)/arrayImage.size());
        URL url = new URL(arrayImage.get(i).getThumbUrl().replace(".png", ".jpg"));// API bug, png is jpg on server
        image = setting.cache.getImage(url, cache);
        if (image == null) {
          setting.cache.add(url.openStream(), url.toString(), cache);
          image = setting.cache.getImage(url, cache);
        }
        if (image == null) continue;
        switch (type) {
          case THUMB:
            moviePnl.addThumbToList(image, arrayImage.get(i));
            break;
          case FANART:
            moviePnl.addFanartToList(image, arrayImage.get(i));
            break;
          default:
            continue;
        }
      } catch (IOException ex) {
        setting.getLogger().log(Level.INFO, "File not found : {0}", arrayImage.get(i).getThumbUrl());
        continue;
      } catch (CMMException ex) {
        setting.getLogger().log(Level.INFO, "LCMS error 12288 : {0}", arrayImage.get(i).getThumbUrl());
        continue;
      } catch (IllegalArgumentException ex) {
        setting.getLogger().log(Level.INFO, "BandOffsets.length is wrong! : {0}", arrayImage.get(i).getThumbUrl());
        continue;
      } catch (NullPointerException ex) {
        ex.printStackTrace();
        continue;
      }
    }
    setProgress(100);
    return null;
  }
}
