/*
 * Movie Renamer
 * Copyright (C) 2012-2013 Nicolas Magré
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
package fr.free.movierenamer.ui.bean;

import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.info.ImageInfo.ImageCategoryProperty;
import fr.free.movierenamer.info.ImageInfo.ImageSize;
import fr.free.movierenamer.ui.utils.FlagUtils;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.utils.Sorter;
import java.awt.Dimension;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import javax.swing.Icon;

/**
 * Class MediaImage
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class UIMediaImage extends Sorter.ISort implements IImage {

  private final ImageInfo info;
  private Icon icon = ImageUtils.LOAD_24;
  private UILang imglang;
  private final ImageCategoryProperty type;

  public UIMediaImage(ImageInfo info) {
    this.info = info;
    this.type = info.getCategory();
    imglang = FlagUtils.getFlag(info.getLanguage());
  }

  @Override
  public Icon getIcon() {
    return icon;
  }

  public ImageInfo getInfo() {
    return info;
  }

  @Override
  public String getLanguage() {
    return info.getLanguage();
  }

  public UILang getImagelang() {
    return imglang;
  }

  private URL getUrl(ImageSize size) {
    return info.getHref(size);
  }

  public ImageCategoryProperty getType() {
    return type;
  }

  @Override
  public void setIcon(Icon icon) {
    this.icon = icon;
  }

  @Override
  public String getName() {
    return getUrl(ImageSize.small).toString();
  }

  @Override
  protected int getYear() {
    return 0;
  }

  @Override
  public String toString() {
    return (info != null) ? info.getDescription() : type.name();
  }

  @Override
  public int getId() {
    return info.getId();
  }

  @Override
  public URI getUri(ImageInfo.ImageSize size) {
    try {
      return getUrl(size).toURI();
    } catch (URISyntaxException ex) {
      UISettings.LOGGER.log(Level.WARNING, null, ex);
    }
    return null;
  }

  @Override
  public Dimension getResize() {
    return null;
  }
}
