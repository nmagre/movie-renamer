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
import fr.free.movierenamer.scraper.Scraper;
import fr.free.movierenamer.searchinfo.Hyperlink;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.utils.Sorter;
import java.net.URI;
import javax.swing.Icon;

/**
 *
 * @author duffy
 */
public class AbstractSearchResult<M extends Hyperlink, T extends Scraper> extends Sorter.ISort implements IImage {

  protected final M result;
  protected final T scraper;
  private Icon icon = ImageUtils.LOAD_24;

  protected AbstractSearchResult(M result, T scraper) {
    this.result = result;
    this.scraper = scraper;
  }

  public T getScraper() {
    return scraper;
  }

  public M getSearchResult() {
    return result;
  }

  @Override
  public Icon getIcon() {
    return icon;
  }

  @Override
  public String getName() {
    return result.getName();
  }

  @Override
  public int getYear() {
    return result.getYear();
  }

  @Override
  public String getOriginalName() {
    return result.getOriginalName();
  }

  @Override
  public long getLength() {
    return getName().length();
  }

  @Override
  public URI getUri(ImageInfo.ImageSize size) {// FIXME size ???
    if (result.getURL() != null) {
      return result.getURL();
    }
    return null;
  }

  @Override
  public int getId() {
    return result.getURL().hashCode();
  }

  @Override
  protected boolean hasImage() {
    return getUri(null) != null;
  }

  @Override
  public String toString() {
    String toString = getName();

    return toString;
  }

  @Override
  public void setIcon(Icon icon) {
    this.icon = icon;
  }

}
