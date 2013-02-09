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
package fr.free.movierenamer.ui.res;

import com.alee.extended.filefilter.DefaultFileFilter;
import fr.free.movierenamer.renamer.NameCleaner;
import fr.free.movierenamer.ui.settings.UISettings;
import fr.free.movierenamer.ui.utils.ImageUtils;
import fr.free.movierenamer.utils.FileUtils;
import fr.free.movierenamer.utils.LocaleUtils;
import java.io.File;
import javax.swing.ImageIcon;

/**
 * Class FileFilter
 *
 * @author Nicolas Magré
 */
public class FileFilter extends DefaultFileFilter {

  public FileFilter() {
    super();
  }

  @Override
  public boolean accept(File file) {
    if (!UISettings.getInstance().isUseExtensionFilter()) {
      return true;
    }

    if (file.isDirectory()) {
      return true;
    }

    return FileUtils.hasExtension(file, NameCleaner.getCleanerProperty("file.extension").split("|"));
  }

  @Override
  public String getDescription() {
    return LocaleUtils.i18n("media");
  }

  @Override
  public ImageIcon getIcon() {
    return (ImageIcon) ImageUtils.LOGO_32;
  }
}
