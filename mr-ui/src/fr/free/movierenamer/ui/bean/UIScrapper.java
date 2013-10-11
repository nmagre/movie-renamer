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

import fr.free.movierenamer.scrapper.SearchScrapper;
import fr.free.movierenamer.searchinfo.Hyperlink;
import fr.free.movierenamer.ui.utils.ImageUtils;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Class UIScrapper
 *
 * @author Simon QUÉMÉNEUR
 * @author Nicolas Magré
 */
public class UIScrapper implements IIconList {

  private Icon icon;
  private final SearchScrapper<? extends Hyperlink> scrapper;

  public UIScrapper(SearchScrapper<? extends Hyperlink> scrapper) {
    this.scrapper = scrapper;
  }

  @Override
  public Icon getIcon() {
    if (icon == null) {
      icon = new ImageIcon(ImageUtils.getImageFromJAR(String.format("scrapper/%s.png", scrapper.getName().toLowerCase())));
    }
    return icon;
  }

  public SearchScrapper<? extends Hyperlink> getScrapper() {
    return scrapper;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof UIScrapper)) {
      return false;
    }
    UIScrapper other = (UIScrapper) obj;
    return scrapper.getName().equals(other.getScrapper().getName());
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
  public String getName() {
    return scrapper.getClass().toString();
  }
}
