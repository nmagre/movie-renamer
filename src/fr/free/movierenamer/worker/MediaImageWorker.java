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
import fr.free.movierenamer.ui.MoviePanel;
import fr.free.movierenamer.ui.res.IMediaPanel;
import fr.free.movierenamer.utils.Settings;
import fr.free.movierenamer.utils.Utils;
import java.awt.Image;
import java.awt.color.CMMException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.SwingWorker;

/**
 * Class MovieImageWorker , Download and add thumbnail/fanart to moviePanel
 *
 * @author Magré Nicolas
 */
public class MediaImageWorker extends SwingWorker<Void, Void> {//A refaire , en Media et rajouter les images pour les series

  private ArrayList<MediaImage> arrayImage;
  private int cache;
  private Settings setting;
  private IMediaPanel mediadPanel;

  /**
   * Constructor arguments
   *
   * @param arrayImage List of images to download (or load from cache)
   * @param cache Cache for this type of images
   * @param mediadPanel Movie Renamer media panel
   * @param setting Movie Renamer settings
   */
  public MediaImageWorker(ArrayList<MediaImage> arrayImage, int cache, IMediaPanel mediadPanel, Settings setting) {//A refaire , utiliser un listener + fireporperty au lieu du jpanel
    this.arrayImage = arrayImage;
    this.cache = cache;
    this.setting = setting;
    this.mediadPanel = mediadPanel;
  }

  @Override
  protected Void doInBackground() {
    for (int i = 0; i < arrayImage.size(); i++) {
      Image image;
      try {
        setProgress((i * 100) / arrayImage.size());
        URL url = new URL(arrayImage.get(i).getThumbUrl());
        image = setting.cache.getImage(url, cache);
        if (image == null) {
          setting.cache.add(url.openStream(), url.toString(), cache);
          image = setting.cache.getImage(url, cache);
        }

        if (image == null) {
          continue;
        }

        //Add image to media panel
        mediadPanel.addImageToList(image, arrayImage.get(i), false);

      } catch (IOException ex) {
        Settings.LOGGER.log(Level.INFO, "File not found : {0}", arrayImage.get(i).getThumbUrl());
        continue;
      } catch (CMMException ex) {
        Settings.LOGGER.log(Level.INFO, "LCMS error 12288 : {0}", arrayImage.get(i).getThumbUrl());
        continue;
      } catch (IllegalArgumentException ex) {
        Settings.LOGGER.log(Level.INFO, "BandOffsets.length is wrong! : {0}", arrayImage.get(i).getThumbUrl());
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
