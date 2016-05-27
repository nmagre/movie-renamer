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

import fr.free.movierenamer.ui.swing.IImage;
import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.info.ImageInfo;
import fr.free.movierenamer.ui.utils.ImageUtils;
import java.net.URI;
import javax.swing.Icon;

/**
 * Class ActorImage
 *
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class UIPersonImage implements IImage {

  private final CastingInfo person;
  private Icon icon = ImageUtils.LOAD_24;

  public UIPersonImage(CastingInfo person) {
    this.person = person;
  }

  @Override
  public Icon getIcon() {
    return icon;
  }

  public void setDefaultIcon() {
    icon = ImageUtils.LOAD_24;
  }

  @Override
  public String getName() {
    return person.getName();
  }

  public String getJob() {
    return person.getJob();
  }

  public CastingInfo getInfo() {
    return person;
  }

  @Override
  public URI getUri(ImageInfo.ImageSize size) {
    return person.getImage(size);
  }

  @Override
  public void setIcon(Icon icon) {
    this.icon = icon;
  }

  @Override
  public int getId() {
    String id = getName() + person.getCharacter() + person.getId();
    return id.hashCode();
  }

  @Override
  public String toString() {
    return person.getName();
  }

}
