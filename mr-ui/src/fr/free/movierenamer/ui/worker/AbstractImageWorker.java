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
package fr.free.movierenamer.ui.worker;

import fr.free.movierenamer.info.ImageInfo.ImageSize;
import fr.free.movierenamer.settings.Settings;
import fr.free.movierenamer.ui.bean.IImage;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.utils.ClassUtils;
import fr.free.movierenamer.utils.StringUtils;
import fr.free.movierenamer.utils.URIRequest;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

/**
 * Class AbstractImageWorker
 *
 * @param <T>
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public abstract class AbstractImageWorker<T extends IImage> extends AbstractWorker<Icon, AbstractImageWorker<T>.ImageChunk> {

  protected final List<T> images;
  protected final Icon defaultImage;
  protected final ImageSize size;
  protected final WorkerId wid;
  private final boolean downloadImage;
  private final File imageCacheDir = new File(Settings.APPFOLDER, "cache/images");
  private final Dimension resize;
  private static final long delay = 2628000L;

  public AbstractImageWorker(WorkerId wid, List<T> images, ImageSize size, Dimension resize, Icon defaultImage, boolean downloadImage) {
    super();
    this.wid = wid;
    this.images = images;
    this.defaultImage = defaultImage;
    this.size = size;
    this.downloadImage = downloadImage;
    this.resize = resize;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Icon executeInBackground() throws Exception {// TODO add a retry
    Icon res = defaultImage;
    if (!imageCacheDir.exists()) {
      if (!imageCacheDir.mkdirs()) {
        throw new Exception("Unable to create images folder : " + imageCacheDir.toString());
      }
    }

    Calendar cal = Calendar.getInstance();
    long ctime = cal.getTimeInMillis();

    try {

      InputStream input;
      File imageFile;
      ImageIcon img;

      int total = images.size();
      int count = 0;
      for (T image : images) {
        if (isCancelled()) {
          break;
        }

        img = null;

        URI uri = image.getUri(size);

        if (downloadImage && uri != null) {
          // We do not use the cache because there is many issue like high CPU usage, really slow,...
          try {
            String filename = new HexBinaryAdapter().marshal(StringUtils.getSha1(uri.toString()));
            imageFile = new File(imageCacheDir, filename);
            if (!imageFile.exists() || (ctime - imageFile.lastModified()) > delay) {

              input = URIRequest.getInputStream(uri);
              Image bimg = ImageIO.read(input);
              if (resize != null) {
                bimg = bimg.getScaledInstance(resize.width, resize.height, Image.SCALE_FAST);
              }

              BufferedImage buffered = new BufferedImage(bimg.getWidth(null), bimg.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
              buffered.getGraphics().drawImage(bimg, 0, 0, null);

              ImageIO.write(buffered, "PNG", imageFile);
              img = new ImageIcon(bimg);
            } else {
              img = new ImageIcon(ImageIO.read(imageFile));
            }
          } catch (Exception ex) {
            Settings.LOGGER.warning(ex.getMessage());
            // We don't care about
            img = null;
          }
        }

        if (img == null && defaultImage != null) {
          img = (ImageIcon) defaultImage;
        }

        publish(new ImageChunk(img, image.getId()));
        count++;

        setProgress((count * 100) / total);
      }
    } catch (Exception ex) {
      UISettings.LOGGER.log(Level.SEVERE, String.format("%s%n%n%s", getName(), ClassUtils.getStackTrace(ex)));
    }

    return res;
  }

  @Override
  public Object getEventObject() {
      return this;
  }
  
  protected abstract String getName();

  @Override
  protected void workerDone() throws Exception {
    // Do nothing
  }

  public class ImageChunk {

    private final int id;
    private final Icon icon;

    public ImageChunk(Icon icon, int id) {
      this.icon = icon;
      this.id = id;
    }

    public int getId() {
      return id;
    }

    public Icon getIcon() {
      return icon;
    }
  }

  @Override
  public WorkerId getWorkerId() {
    return wid;
  }

}
