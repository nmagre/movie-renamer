/*
 * Copyright (C) 2014 duffy
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
import fr.free.movierenamer.info.TrailerInfo;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.utils.Sorter;
import java.net.URI;
import javax.swing.Icon;

/**
 * Class UITrailer
 *
 * @author Nicolas Magré
 */
public class UITrailer extends Sorter.ISort implements IImage, UIInfo {

  private final TrailerInfo trailer;
  private Icon icon = ImageUtils.LOAD_24;

  public UITrailer(TrailerInfo trailer) {
    this.trailer = trailer;
  }

  public URI getUrl() {
    return trailer.getUrl();
  }

  @Override
  public Icon getIcon() {
    return icon;
  }

  @Override
  public URI getUri(ImageInfo.ImageSize size) {
    return trailer.getPosterPath();
  }

  @Override
  public String getName() {
    return trailer.getTitle();
  }

  @Override
  public void setIcon(Icon icon) {
    this.icon = icon;
  }

  public String getProviderName() {
    return trailer.getProvider();
  }

  public String getDuration() {
    return trailer.getRuntime();
  }

  @Override
  public int getId() {
    return trailer.get(TrailerInfo.TrailerProperty.url).hashCode();
  }

}
