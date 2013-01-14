/*
 * mr-ui
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
package fr.free.movierenamer.ui.list;

import fr.free.movierenamer.info.MediaInfo;
import fr.free.movierenamer.scrapper.MediaScrapper;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.ui.utils.ImageUtils;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Class UIScrapper
 * @author Simon QUÉMÉNEUR
 */
public class UIScrapper implements IIconList {

  private final MediaScrapper<? extends Media, ? extends MediaInfo> scrapper;

  public UIScrapper(MediaScrapper<? extends Media, ? extends MediaInfo> scrapper) {
    this.scrapper = scrapper;
  }

  @Override
  public Icon getIcon() {
    return new ImageIcon(ImageUtils.getImageFromJAR(String.format("scrapper/%s.png", scrapper.getName().toLowerCase())));
  }

  public MediaScrapper<? extends Media, ? extends MediaInfo> getScrapper() {
    return scrapper;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj ==null || !(obj instanceof UIScrapper)) {
      return false;
    }
    UIScrapper other = (UIScrapper) obj;
    return scrapper.getName().equals(other.scrapper.getName());
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 29 * hash + (this.scrapper != null ? this.scrapper.hashCode() : 0);
    return hash;
  }

  @Override
  public String toString() {
    return scrapper.getName();
  }

  @Override
  public void setIcon(Icon icon) {
    // DO nothing
  }

}
