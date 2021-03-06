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
package fr.free.movierenamer.ui.bean;

import fr.free.movierenamer.ui.swing.IIconList;
import fr.free.movierenamer.scraper.ScraperOptions;
import fr.free.movierenamer.scraper.SearchScraper;
import fr.free.movierenamer.searchinfo.Media;
import fr.free.movierenamer.ui.utils.ImageUtils;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Class UIScraper
 *
 * @author Simon QUÉMÉNEUR
 * @author Nicolas Magré
 */
public class UIScraper implements IIconList {

  private Icon icon;
  private final SearchScraper<? extends Media> scraper;

  public UIScraper(SearchScraper<? extends Media> scraper) {
    this.scraper = scraper;
  }

  @Override
  public Icon getIcon() {
    if (icon == null) {
      icon = new ImageIcon(ImageUtils.getImageFromJAR(String.format("scraper/%s.png", scraper.getName().toLowerCase())));
    }
    
    return icon;
  }

  public SearchScraper<? extends Media> getScraper() {
    return scraper;
  }

  public List<ScraperOptions> getOptions() {
    return scraper.getScraperOptions();
  }

  public boolean hasOptions() {
    return !scraper.getScraperOptions().isEmpty();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof UIScraper)) {
      return false;
    }
    UIScraper other = (UIScraper) obj;
    SearchScraper<?> osscraper = other.getScraper();
    if(osscraper == null) {
      return false;
    }
    
    return scraper.getName().equals(osscraper.getName());
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 29 * hash + (this.scraper != null ? this.scraper.hashCode() : 0);
    return hash;
  }

  @Override
  public String toString() {
    return scraper.getName();
  }

  @Override
  public String getName() {
    return scraper.getClass().toString();
  }
}
