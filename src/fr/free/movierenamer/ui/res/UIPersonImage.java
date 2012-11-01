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
package fr.free.movierenamer.ui.res;

import fr.free.movierenamer.info.CastingInfo;
import fr.free.movierenamer.utils.ImageUtils;
import java.awt.Dimension;
import javax.swing.Icon;

/**
 * Class ActorImage
 * 
 * @author Nicolas Magré
 * @author Simon QUÉMÉNEUR
 */
public class UIPersonImage implements IIconList {
  private final Dimension actorListDim = new Dimension(30, 53);
  private final CastingInfo person;
  private final Icon icon;

  public UIPersonImage(CastingInfo person) {
    this.person = person;
    this.icon = ImageUtils.getIcon(this.person.getPicturePath(), actorListDim, "unknown.png");
  }

  @Override
  public Icon getIcon() {
    return icon;
  }

  public String getName() {
    return person.getName();
  }

  public CastingInfo getInfo() {
    return person;
  }

  @Override
  public String toString() {
    return (person != null) ? person.getName() : null;
  }
}