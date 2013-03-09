/*
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
import fr.free.movierenamer.mediainfo.MediaAudio;
import fr.free.movierenamer.ui.res.Flag;
import java.net.URI;
import javax.swing.Icon;

/**
 * Class UIMediaAudio
 *
 * @author Nicolas Magré
 */
public class UIMediaAudio implements IIconList {

  private MediaAudio maudio;
  private UIImageLang image;

  public UIMediaAudio(MediaAudio maudio) {
    this.maudio = maudio;
  }

  @Override
  public Icon getIcon() {
    if (image == null) {
      image = Flag.getFlag(maudio.getLanguage().getLanguage());
    }
    return image.getIcon();
  }

  @Override
  public void setIcon(Icon icon) {
    //image.setIcon(icon);
  }

  @Override
  public String toString() {
    return (maudio != null && maudio.getTitle().length() > 0) ? maudio.getTitle() : maudio.getLanguage().getLanguage();// FIXME maudio == null ?
  }

  @Override
  public URI getUri(ImageInfo.ImageSize size) {
    return null;
  }

}
