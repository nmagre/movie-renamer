/*
 * Movie Renamer
 * Copyright (C) 2012-2014 Nicolas Magré
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
package fr.free.movierenamer.ui.utils;

import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.swing.SpinningDial;
import fr.free.movierenamer.utils.Cache;
import fr.free.movierenamer.utils.URIRequest;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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

  // 24 pixel icon
  public static final Icon APPLICATIONEXIT_24 = getIconFromJar("ui/24/application-exit.png");
  public static final Icon FOLDERVIDEO_24 = getIconFromJar("ui/24/folder-video.png");
  public static final Icon MOVIE_24 = getIconFromJar("ui/24/movie.png");
  public static final Icon SETTING_24 = getIconFromJar("ui/24/setting.png");
  public static final Icon TV_24 = getIconFromJar("ui/24/tv.png");
  public static final Icon UPDATE_24 = getIconFromJar("ui/24/update.png");
  public static final Icon DOWNLOAD_24 = getIconFromJar("ui/24/download.png");
  public static final Icon OK_24 = getIconFromJar("ui/24/check.png");
  public static final Icon HELP_24 = getIconFromJar("ui/24/help.png");
  public static final Icon INFO_24 = getIconFromJar("ui/24/info.png");
  public static final Icon ERROR_24 = getIconFromJar("ui/24/delete.png");
  public static final Icon ERROREXC_24 = getIconFromJar("ui/24/error.png");
  public static final Icon ERESTRICTED_24 = getIconFromJar("ui/24/restricted.png");
  public static final Icon STOP_24 = getIconFromJar("ui/24/stop.png");
  public static final Icon NORESULT_24 = getIconFromJar("ui/24/zoom_out.png");
  public static final Icon LOAD_24 = new SpinningDial(24, 24);
  // 16 pixel icon
  public static final Icon CANCEL_16 = getIconFromJar("ui/16/cancel.png");
  public static final Icon FILEVIEW_16 = getIconFromJar("ui/16/fileview.png");
  public static final Icon GROUPVIEW_16 = getIconFromJar("ui/16/groupview.png");
  public static final Icon IMAGE_ADD_16 = getIconFromJar("ui/16/image_add.png");
  public static final Icon IMAGE_16 = getIconFromJar("ui/16/image.png");
  public static final Icon MEDIA_16 = getIconFromJar("ui/16/media.png");
  public static final Icon MEDIAWARN_16 = getIconFromJar("ui/16/media-warn.png");
  public static final Icon MINUS_16 = getIconFromJar("ui/16/minus.png");
  public static final Icon MOVIE_16 = getIconFromJar("ui/16/movie.png");
  public static final Icon OK_16 = getIconFromJar("ui/16/ok.png");
  public static final Icon PLUS_16 = getIconFromJar("ui/16/plus.png");
  public static final Icon SEARCH_16 = getIconFromJar("ui/16/search.png");
  public static final Icon SETTING_16 = getIconFromJar("ui/16/setting.png");
  public static final Icon TV_16 = getIconFromJar("ui/16/tv.png");
  public static final Icon HELP_16 = getIconFromJar("ui/16/help.png");
  public static final Icon FOLDERVIDEO_16 = getIconFromJar("ui/16/folder-video.png");
  public static final Icon UPDATE_16 = getIconFromJar("ui/16/update.png");
  public static final Icon APPLICATIONEXIT_16 = getIconFromJar("ui/16/application-exit.png");
  public static final Icon INFO_16 = getIconFromJar("ui/16/info.png");
  public static final Icon TEXTFILE_16 = getIconFromJar("ui/16/text.png");
  public static final Icon STAREMPTY_16 = getIcon((URL) null, new Dimension(12, 12), "ui/16/star-empty.png");
  public static final Icon STARHALF_16 = getIcon((URL) null, new Dimension(12, 12), "ui/16/star-half.png");
  public static final Icon STAR_16 = getIcon((URL) null, new Dimension(12, 12), "ui/16/star.png");
  public static final Icon CLEAR_LIST_16 = getIconFromJar("ui/16/media_playlist_clear.png");
  public static final Icon CASTING_16 = getIconFromJar("ui/16/casting.png");
  public static final Icon EDIT_16 = getIconFromJar("ui/16/edit.png");
  public static final Icon COPY_16 = getIconFromJar("ui/16/copy.png");
  public static final Icon PASTE_16 = getIconFromJar("ui/16/paste.png");
  public static final Icon CUT_16 = getIconFromJar("ui/16/cut.png");
  public static final Icon DELETE_16 = getIconFromJar("ui/16/delete.png");
  public static final Icon SELECTALL_16 = getIconFromJar("ui/16/selectall.png");
  public static final Icon ZOOMIN_16 = getIconFromJar("ui/16/zoom_in.png");
  public static final Icon ZOOMOUT_16 = getIconFromJar("ui/16/zoom_out.png");
  public static final Icon LOAD_16 = new SpinningDial(16, 16);
  public static final Icon SUBTITLE_16 = getIconFromJar("ui/16/srt.png");
  public static final Icon LOOK_16 = getIconFromJar("ui/16/look.png");
  public static final Icon SSEARCH_16 = getIconFromJar("ui/16/ssearch.png");
  public static final Icon WINDOW_16 = getIconFromJar("ui/16/window.png");
  public static final Icon OTHER_16 = getIconFromJar("ui/16/other.png");
  public static final Icon NETWORK_16 = getIconFromJar("ui/16/network.png");
  public static final Icon SKIP_16 = getIconFromJar("ui/16/skip.png");
  public static final Icon FORMAT_16 = getIconFromJar("ui/16/format.png");
  public static final Icon TEST_16 = getIconFromJar("ui/16/test.png");

  // 8 pixel icon
  public static final Icon CANCEL_8 = getIconFromJar("ui/8/cancel.png");
  public static final Icon LOAD_8 = new SpinningDial(8, 8);
  // Movie Renamer logo
  public static final Icon LOGO_22 = getIconFromJar("ui/icon-22.png");
  public static final Icon LOGO_32 = getIconFromJar("ui/icon-32.png");
  public static final Icon LOGO_48 = getIconFromJar("ui/icon-48.png");
  public static final Icon LOGO_72 = getIconFromJar("ui/icon-72.png");
  // Misc
  public static final Icon BAN = getIconFromJar("ui/mr-ban.png");
  public static final Icon NO_IMAGE = getIconFromJar("ui/nothumb.png");
  public static final Icon NO_IMAGE_H = flipImageHorizontally(NO_IMAGE);
  public static final Icon WARNING = getIconFromJar("ui/warning.png");
  public static final Icon FILE = getIconFromJar("ui/file.png");
  public static final Icon UNKNOWN = getIconFromJar("ui/unknown.png");

  public static Image iconToImage(Icon icon) {
    if (icon instanceof ImageIcon) {
      return ((ImageIcon) icon).getImage();
    }

    BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
    icon.paintIcon(null, image.getGraphics(), 0, 0);
    return image;
  }

  public static Image getImageFromJAR(String fileName) {
    return getImageFromJAR(fileName, ImageUtils.class);
  }

  /**
   * Get image from jar file (from 'image' folder)
   *
   * @param fileName Image filename
   * @param clazz Class
   * @return Image file or null
   */
  public static Image getImageFromJAR(String fileName, Class<?> clazz) {
    if (fileName == null) {
      return null;
    }

    if (clazz == null) {
      return null;
    }

    Image image = null;
    byte[] thanksToNetscape;
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    InputStream in = clazz.getClass().getResourceAsStream(String.format("/image/%s", fileName));

    try {
      int length = in.available();
      thanksToNetscape = new byte[length];
      in.read(thanksToNetscape);
      image = toolkit.createImage(thanksToNetscape);

    } catch (Exception ex) {
      UISettings.LOGGER.log(Level.SEVERE, null, ex);
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException ex) {
        UISettings.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
    return image;
  }

  public static Icon getIconFromJar(String fileName) {
    return new ImageIcon(getImageFromJAR(fileName));
  }

  public static Icon getIcon(URL imagePth, Dimension dim, String defaultImage) {
    URI uri;
    try {
      uri = (imagePth != null) ? imagePth.toURI() : null;
    } catch (URISyntaxException ex) {
      uri = null;
    }

    return getIcon(uri, dim, new ImageIcon(getImageFromJAR(defaultImage, ImageUtils.class)));
  }

  public static boolean isInCache(URI imagePth) {
    Cache cache = Cache.getCache("long");
    if (cache != null) {
      ImageIcon stored = cache.get(imagePth, ImageIcon.class);
      if (stored != null) {
        return true;
      }
    }

    return false;
  }

  public static Icon getIcon(URI imagePth, Dimension dim, Icon defaultImage) {
    Cache cache = Cache.getCache("long");
    Image img;

    if (imagePth != null) {
      img = isInCache(imagePth) ? cache.get(imagePth, ImageIcon.class).getImage() : null;
      if (img == null) {
        try {
          try (InputStream is = URIRequest.getInputStream(imagePth)) {
            img = ImageIO.read(is);
          }

          if (cache != null) {
            cache.put(imagePth, new ImageIcon(img));
          }
        } catch (IOException ex) {
          img = null;
          Settings.LOGGER.log(Level.SEVERE, String.format("%s %s", ex.getMessage(), imagePth));
        }
      }

    } else {
      img = null;
    }

    if (img == null && defaultImage != null) {
      // load default image id necessary
      img = iconToImage(defaultImage);
    }

    if (dim != null) {
      // let's resize
      img = img.getScaledInstance(dim.width, dim.height, Image.SCALE_DEFAULT);
    }

    Icon icon;
    if (img != null) {
      icon = new ImageIcon(img);
    } else {
      icon = null;
    }
    return icon;
  }

  public Icon resizeIcon(Icon icon, Dimension dim) {
    Image img = iconToImage(icon);
    return new ImageIcon(img.getScaledInstance(dim.width, dim.height, Image.SCALE_DEFAULT));
  }

  public static Icon flipImageHorizontally(Icon icon) {
    return flipIcon(icon, 90);
  }

  public static Icon flipIcon(Icon icon, int angle) {
    int w = icon.getIconWidth();
    int h = icon.getIconHeight();
    int type = BufferedImage.TYPE_INT_RGB;  // other options, see api
    BufferedImage image = new BufferedImage(h, w, type);
    Graphics2D g2 = image.createGraphics();
    double x = (h - w) / 2.0;
    double y = (w - h) / 2.0;
    AffineTransform at = AffineTransform.getTranslateInstance(x, y);
    at.rotate(Math.toRadians(90), w / 2.0, h / 2.0);
    g2.drawImage(iconToImage(icon), at, null);
    g2.dispose();
    icon = new ImageIcon(image);
    return icon;
  }

  private ImageUtils() {
    throw new UnsupportedOperationException();
  }
}
