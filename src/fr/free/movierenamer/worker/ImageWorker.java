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

import fr.free.movierenamer.media.MediaImage;
import fr.free.movierenamer.ui.res.IMediaPanel;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.awt.Image;
import java.awt.color.CMMException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;

/**
 * Class ImageWorker , Download and add thumbnail/fanart to mediaPanel
 * 
 * @author Magré Nicolas
 */
public class ImageWorker extends Worker<Void> {

  private final List<MediaImage> arrayImage;
  private final Cache.CacheType cache;
  private final IMediaPanel mediadPanel;

  /**
   * Constructor arguments
   *
   * @param arrayImage List of images to download (or load from cache)
   * @param cache Cache for this type of images
   * @param mediadPanel Movie Renamer media panel
   */
  public ImageWorker(List<MediaImage> arrayImage, Cache.CacheType cache, IMediaPanel mediadPanel) {
    this.arrayImage = arrayImage;
    this.cache = cache;
    this.mediadPanel = mediadPanel;
  }

  @Override
  protected Void executeInBackground() {// FIXME change tcp timeout, we don't want to wait
    // FIXME Remove all images which have a problem (not found,...)
    setProgress(0);
    for (int i = 0; i < arrayImage.size(); i++) {
      Image image;
      URL url = null;
      try {
        setProgress((i * 100) / arrayImage.size());
        url = new URL(arrayImage.get(i).getUrl(MediaImage.MediaImageSize.THUMB));
        image = Cache.getInstance().getImage(url, cache);
        if (image == null) {
          Cache.getInstance().add(url, cache);
          image = Cache.getInstance().getImage(url, cache);
        }

        if (image == null) {
          continue;
        }

        //Add image to media panel
        mediadPanel.addImageToList(image, arrayImage.get(i), false);
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          Settings.LOGGER.log(Level.SEVERE, null, e);
        }

      } catch (IOException ex) {
        Settings.LOGGER.log(Level.INFO, "File not found : {0}", url);
        continue;
      } catch (CMMException ex) {
        Settings.LOGGER.log(Level.INFO, "LCMS error 12288 : {0}", url);
        continue;
      } catch (IllegalArgumentException ex) {
        Settings.LOGGER.log(Level.INFO, "BandOffsets.length is wrong! : {0}", url);
        continue;
      } catch (NullPointerException ex) {
        Settings.LOGGER.log(Level.INFO, Utils.getStackTrace("NullPointerException", ex.getStackTrace()));
        continue;
      }
    }
    setProgress(100);
    return null;
  }

}
