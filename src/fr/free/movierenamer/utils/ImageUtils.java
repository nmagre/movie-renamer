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
package fr.free.movierenamer.utils;

import fr.free.movierenamer.settings.Settings;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Class ImageUtils
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public final class ImageUtils {

  /**
   * Get image from jar file
   * 
   * @param <T>
   * @param fileName Image filename
   * @param cls Class
   * @return Image file or null
   */
  public static Image getImageFromJAR(String fileName) {
    if (fileName == null) {
      return null;
    }

    Image image = null;
    byte[] thanksToNetscape;
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    InputStream in = ImageUtils.class.getResourceAsStream(String.format("/fr/free/movierenamer/ui/image/%s", fileName));

    try {
      int length = in.available();
      thanksToNetscape = new byte[length];
      in.read(thanksToNetscape);
      image = toolkit.createImage(thanksToNetscape);

    } catch (Exception ex) {
      Settings.LOGGER.log(Level.SEVERE, null, ex);
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException ex) {
        Settings.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
    return image;
  }

  private ImageUtils() {
    throw new UnsupportedOperationException();
  }

  public static Icon getIcon(URI imagePth, Dimension dim, String defaultImage) {
    Cache cache = Cache.getCache("long");
    java.awt.Image img;
    if (imagePth != null) {
      if(cache !=null){
        ImageIcon stored =cache.get(imagePth, ImageIcon.class);
        if (stored != null) {
          img = stored.getImage();
        } else {
          img = null;
        }
      } else {
        img = null;
      }
      if (img == null) {
        try {
          InputStream is = WebRequest.getInputStream(imagePth.toURL());
          img = ImageIO.read(is);
          if (cache != null) {
            cache.put(imagePth, new ImageIcon(img));
          }
        } catch (IOException ex) {
          img = null;
          Settings.LOGGER.log(Level.SEVERE, "{0} {1}", new Object[] { ex.getMessage(), imagePth });
        }
      }
    } else {
      img = null;
    }
    if (img == null && defaultImage != null) {
      //load default image id necessary
      img = ImageUtils.getImageFromJAR(defaultImage);
    }
    if (dim != null) {
      // let's resize
      img = img.getScaledInstance(dim.width, dim.height, java.awt.Image.SCALE_DEFAULT);
    }
    Icon icon;
    if (img != null) {
      icon = new ImageIcon(img);
    } else {
      icon = null;
    }
    return icon;
  }

}
